// ============================================================================
//  panxcz_rpc - JSON-line RPC server buat panxcz-tool (app Java = UI-nya)
//
//  Kenapa: core libtool ini dibuat buat jalan sebagai ELF EXTERNAL (root,
//  /proc/<pid>/mem). Waktu dimasukin ke app (uid biasa), akses memorinya
//  EPERM -- makanya explorer-nya "ga fungsi". Solusinya: core TETAP jalan
//  sebagai root lewat `su -c panxcz_rpc --rpc`, app ngobrol lewat
//  stdin/stdout satu JSON per baris.
//
//  Protokol:
//    kirim : {"id":1,"cmd":"attach","pkg":"com.x","pool":true}
//    terima: {"id":1,"ok":true, ...}  /  {"id":1,"ok":false,"err":"..."}
//
//  Fitur (semua port dari Android-LibTool-New — logicnya sama, cuma
//  sumbernya /proc/<pid>/mem, bukan il2cpp API in-process):
//    procs/attach/status  - deteksi + nempel proses game
//    classes/class        - explorer kelas (FilterClasses + ClassViewer)
//    instances            - instance hidup (ImGuiObjectSelector)
//    hex/asm              - HexView / AssemblyView
//    gencode/ggcode       - GenerateCppCode / GenerateGGPatchCode
//    force/unforce        - PatcherView (stub return value)
//    dump                 - Dumper (.cs/.json/.h/.ptrs.tsv)
//    patch/read/write     - patch byte + baca/tulis nilai
// ============================================================================
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cctype>
#include <string>
#include <vector>
#include <utility>
#include <stdexcept>
#include <algorithm>

#include <unistd.h>

#include "core/mem.h"
#include "core/il2cpp.h"
#include "core/target.h"
#include "core/explore.h"
#include "core/metapool.h"
#include "core/dumpout.h"
#include "core/patch.h"
#include "core/gencode.h"
#include "core/memview.h"
#include "core/log.h"

// ---------------------------------------------------------------------------
//  JSON kecil (request datar dari app kita sendiri; nggak perlu parser penuh)
// ---------------------------------------------------------------------------
static std::string jesc(const std::string& s) {
    std::string o;
    o.reserve(s.size() + 8);
    for (char c : s) {
        switch (c) {
            case '"':  o += "\\\""; break;
            case '\\': o += "\\\\"; break;
            case '\n': o += "\\n";  break;
            case '\r': o += "\\r";  break;
            case '\t': o += "\\t";  break;
            default:
                if ((unsigned char)c < 0x20) {
                    char b[8];
                    snprintf(b, sizeof(b), "\\u%04x", c);
                    o += b;
                } else {
                    o += c;
                }
        }
    }
    return o;
}
static std::string jstr(const std::string& v) { return "\"" + jesc(v) + "\""; }

static size_t jSkipWs(const std::string& s, size_t p) {
    while (p < s.size() && isspace((unsigned char)s[p])) ++p;
    return p;
}
// Cari `"key"` lalu ambil nilai string-nya (dengan escape).
static bool jgetStr(const std::string& s, const char* key, std::string& out) {
    const std::string pat = std::string("\"") + key + "\"";
    size_t p = s.find(pat);
    if (p == std::string::npos) return false;
    p = jSkipWs(s, p + pat.size());
    if (p >= s.size() || s[p] != ':') return false;
    p = jSkipWs(s, p + 1);
    if (p >= s.size() || s[p] != '"') return false;
    ++p;
    out.clear();
    while (p < s.size()) {
        char c = s[p++];
        if (c == '\\') {
            if (p >= s.size()) return false;
            char e = s[p++];
            switch (e) {
                case 'n': out += '\n'; break;
                case 't': out += '\t'; break;
                case 'r': out += '\r'; break;
                case '"': out += '"';  break;
                case '\\': out += '\\'; break;
                case '/': out += '/';  break;
                case 'u':
                    if (p + 3 < s.size()) p += 4;
                    out += '?';
                    break;
                default: out += e; break;
            }
        } else if (c == '"') {
            return true;
        } else {
            out += c;
        }
    }
    return false;
}
static bool jgetLL(const std::string& s, const char* key, long long& out) {
    const std::string pat = std::string("\"") + key + "\"";
    size_t p = s.find(pat);
    if (p == std::string::npos) return false;
    p = jSkipWs(s, p + pat.size());
    if (p >= s.size() || s[p] != ':') return false;
    p = jSkipWs(s, p + 1);
    if (p >= s.size()) return false;
    char* end = nullptr;
    out = strtoll(s.c_str() + p, &end, 0);
    return end && end != s.c_str() + p;
}
static bool jgetBool(const std::string& s, const char* key, bool def) {
    const std::string pat = std::string("\"") + key + "\"";
    size_t p = s.find(pat);
    if (p == std::string::npos) return def;
    p = jSkipWs(s, p + pat.size());
    if (p >= s.size() || s[p] != ':') return def;
    p = jSkipWs(s, p + 1);
    if (p < s.size() && s.compare(p, 4, "true") == 0) return true;
    if (p < s.size() && s.compare(p, 5, "false") == 0) return false;
    return def;
}
static std::string jgets(const std::string& s, const char* key) {
    std::string v;
    jgetStr(s, key, v);
    return v;
}

// ---------------------------------------------------------------------------
//  Helper port dari main.cpp (nama polos, resolve Class::Method, dst)
// ---------------------------------------------------------------------------
static std::string bareClassName(const std::string& n) {
    std::string s = n;
    const size_t bt = s.find('`');  // List`1 -> List
    if (bt != std::string::npos) {
        size_t e = bt + 1;
        while (e < s.size() && isdigit((unsigned char)s[e])) ++e;
        s = s.substr(0, bt) + s.substr(e);
    }
    const size_t dot = s.find_last_of('.');
    return dot == std::string::npos ? s : s.substr(dot + 1);
}
static std::string bareMethodName(const std::string& n) {
    std::string s = n;
    const size_t par = s.find('(');
    if (par != std::string::npos) s = s.substr(0, par);
    const size_t sp = s.find_last_of(" \t");
    if (sp != std::string::npos) s = s.substr(sp + 1);
    return s;
}
static std::string lower(const std::string& in) {
    std::string t = in;
    for (char& c : t) c = (char)tolower((unsigned char)c);
    return t;
}
static bool icontains(const std::string& hay, const std::string& needle) {
    if (needle.empty()) return true;
    return lower(hay).find(lower(needle)) != std::string::npos;
}

// Alamat kode dari (module, rva) -- tiga tafsir sama kayak main.cpp.
static uintptr_t codeAddrOf(const std::string& mod, uint64_t rva) {
    uintptr_t addr = mem::addrOfRvaIn(mod.c_str(), rva);
    if (addr) return addr;
    addr = mem::addrOfVaddrIn(mod.c_str(), rva);
    if (addr) return addr;
    addr = mem::addrOfRva(rva);
    if (addr) return addr;
    const uintptr_t b = mem::base(mod.c_str());
    if (!b) return 0;
    return mem::untag(b) + rva;
}

struct MethodSpec {
    std::string cls, method, module;
    uint64_t    rva = 0;
    gencode::Target t;
};

// resolve "Class::Method" -> Target (jalur explore dulu, pool buat MLBB)
static bool genResolve(const std::string& cls, const std::string& method,
                       gencode::Target& out, int& hits) {
    hits = 0;
    bool got = false;
    if (explore::resolve(cls.c_str(), nullptr)) {
        for (const auto& m : explore::info().methods) {
            if (bareMethodName(m.name) != method) continue;
            ++hits;
            if (got) continue;
            out.cls    = cls;
            out.method = bareMethodName(m.name);
            out.module = m.module;
            out.rva    = m.rva;
            gencode::parseSig(m.name, out.sig);
            got = true;
        }
    }
    if (!got && metapool::ready()) {
        for (const auto& e : metapool::all()) {
            if (!e.code || e.name.empty()) continue;
            if (bareMethodName(e.name) != method) continue;
            std::string kn;
            if (!metapool::klassName(e.klass, kn)) continue;
            if (kn != cls && bareClassName(kn) != bareClassName(cls)) continue;
            ++hits;
            if (got) continue;
            out.cls    = cls;
            out.method = bareMethodName(e.name);
            out.module = e.module;
            out.rva    = e.rva;
            gencode::parseSig(e.name, out.sig);
            got = true;
        }
    }
    return got;
}

// "nilai:tipe" / "nilai" -> ekspresi C++ + kind GG (port genRetExpr).
static void genRetExpr(const std::string& value, const std::string& type,
                       std::string& expr, std::string& kind) {
    expr.clear();
    kind.clear();
    std::string v = value;
    while (!v.empty() && (v[0] == ' ' || v[0] == '\t')) v.erase(0, 1);
    while (!v.empty() && (v.back() == ' ' || v.back() == '\t')) v.pop_back();
    const std::string low = lower(v);

    patch::Ret k = patch::Ret::I32;
    double num = 0;
    uint64_t ptr = 0;
    if (!type.empty()) {
        if (!patch::retFromName(type, k)) return;
    } else if (v.empty() || low == "off" || low == "nop" || low == "void") {
        kind = "void";
        expr = "@ret";
        return;
    } else if (low == "true" || low == "false") {
        k = patch::Ret::Bool;
    } else if (v.find('.') != std::string::npos || v.find('e') != std::string::npos ||
               v.find('E') != std::string::npos) {
        k = patch::Ret::Float;
    } else {
        k = patch::Ret::I32;
    }
    if (k == patch::Ret::Ptr) {
        ptr = strtoull(v.c_str(), nullptr, 0);
    } else {
        num = strtod(v.c_str(), nullptr);
    }

    char b[64];
    switch (k) {
        case patch::Ret::Void: expr = "@ret"; kind = "void"; break;
        case patch::Ret::Bool:
            expr = (num != 0.0 || low == "true") ? "true" : "false";
            kind = "bool";
            break;
        case patch::Ret::I16: case patch::Ret::I32:
            snprintf(b, sizeof(b), "%lld", (long long)num);
            expr = b; kind = "i32"; break;
        case patch::Ret::U16: case patch::Ret::U32:
            snprintf(b, sizeof(b), "%llu", (unsigned long long)num);
            expr = b; kind = "u32"; break;
        case patch::Ret::I64: case patch::Ret::U64:
            snprintf(b, sizeof(b), "%lldLL", (long long)num);
            expr = b; kind = "i64"; break;
        case patch::Ret::Float:
            snprintf(b, sizeof(b), "%gf", num);
            expr = b; kind = "f32"; break;
        case patch::Ret::Double:
            snprintf(b, sizeof(b), "%g", num);
            expr = b; kind = "f64"; break;
        case patch::Ret::Ptr: expr = "NULL"; kind = "ptr"; break;
    }
}

// "Class::Method[:len]" / "lib.so:0xRVA[:len]" / "0xADDR[:len]" -> alamat + len
static bool parseViewSpec(const std::string& in, uintptr_t& addr, size_t& len,
                          size_t defLen, size_t maxLen, std::string* labelOut) {
    addr = 0;
    len = defLen;
    std::string s = in;
    const size_t lc = s.rfind(':');
    if (lc != std::string::npos && lc + 1 < s.size()) {
        bool digits = true;
        for (size_t i = lc + 1; i < s.size(); ++i)
            if (!isdigit((unsigned char)s[i])) { digits = false; break; }
        if (digits && lc + 1 < s.size()) {
            len = (size_t)strtoul(s.c_str() + lc + 1, nullptr, 10);
            if (!len) len = defLen;
            if (len > maxLen) len = maxLen;
            s = s.substr(0, lc);
        }
    }
    const size_t dc = s.find("::");
    if (dc != std::string::npos) {
        const std::string cls = s.substr(0, dc);
        const std::string mth = s.substr(dc + 2);
        gencode::Target t;
        int hits = 0;
        if (!genResolve(cls, mth, t, hits) || !hits) return false;
        addr = codeAddrOf(t.module, t.rva);
        if (!addr) return false;
        if (labelOut) *labelOut = t.cls + "::" + t.method;
        return true;
    }
    const size_t c1 = s.find(':');
    if (c1 != std::string::npos) {
        const std::string mod = s.substr(0, c1);
        const uint64_t rva = strtoull(s.c_str() + c1 + 1, nullptr, 0);
        addr = codeAddrOf(mod, rva);
        if (labelOut) *labelOut = mod + ":0x" + [&] {
            char b[32];
            snprintf(b, sizeof(b), "%llX", (unsigned long long)rva);
            return std::string(b);
        }();
        return addr != 0;
    }
    addr = mem::untag(strtoull(s.c_str(), nullptr, 0));
    if (labelOut) *labelOut = s;
    return addr != 0;
}

// ---------------------------------------------------------------------------
//  Daftar kelas (di-cache; dipakai command classes / by=field)
// ---------------------------------------------------------------------------
struct KlassEnt {
    uintptr_t   k;
    std::string name;
};
static std::vector<KlassEnt> g_klasses;
static bool g_klassBuilt = false;

static void buildKlasses() {
    g_klasses.clear();
    if (metapool::ready()) {
        const std::vector<uintptr_t>& all = metapool::allClasses();
        g_klasses.reserve(all.size());
        for (uintptr_t k : all) {
            std::string n;
            if (metapool::klassName(k, n) && !n.empty())
                g_klasses.push_back({k, n});
        }
    }
    g_klassBuilt = true;
}

// ---------------------------------------------------------------------------
//  Dispatch
// ---------------------------------------------------------------------------
static long long g_id = 0;

static std::string cmdPing(const std::string&) {
    return std::string("{\"ver\":\"panxcz_rpc 1.0\",\"pid\":") +
           std::to_string(getpid()) + "}";
}

static std::string cmdProcs(const std::string&) {
    std::vector<target::Proc> ps = target::listProcesses();
    std::sort(ps.begin(), ps.end(),
              [](const target::Proc& a, const target::Proc& b) { return a.score > b.score; });
    std::string o = "{\"count\":" + std::to_string(ps.size()) + ",\"procs\":[";
    size_t n = 0;
    for (const auto& p : ps) {
        if (n >= 64) break;
        if (n) o += ",";
        o += "{\"pid\":" + std::to_string(p.pid) + ",\"pkg\":" + jstr(p.pkg) +
             ",\"score\":" + std::to_string(p.score) + "}";
        ++n;
    }
    o += "]}";
    return o;
}

static bool g_attached = false;

static std::string cmdAttach(const std::string& req) {
    const std::string pkg = jgets(req, "pkg");
    const std::string lib = jgets(req, "lib");
    long long waitSec = 45;
    jgetLL(req, "wait", waitSec);
    if (waitSec < 1) waitSec = 1;
    if (waitSec > 180) waitSec = 180;
    const bool wantPool = jgetBool(req, "pool", true);

    if (pkg.empty() && !mem::attached())
        throw std::runtime_error("pkg kosong");

    target::load();
    if (!pkg.empty()) {
        target::setPackage(pkg);
        target::setLib(lib);  // "" = auto
        mem::detach();
        g_attached = false;
        g_klassBuilt = false;
        g_klasses.clear();
    }
    const std::string usePkg = !pkg.empty() ? pkg : target::package();
    if (!mem::attached()) {
        if (!mem::attach(usePkg.c_str()))
            throw std::runtime_error("proses '" + usePkg + "' nggak ketemu / mati");
    }

    // lib engine sering ke-load belakangan (splash screen) -- coba sampai
    // waitSec, refresh maps tiap beberapa detik (alur yang sama kayak main.cpp)
    bool ilOk = false;
    for (long long i = 0; i < waitSec; ++i) {
        if (il2::init()) { ilOk = true; break; }
        if (i > 0 && i % 5 == 0) mem::refresh();
        sleep(1);
    }
    if (!ilOk)
        throw std::runtime_error("lib il2cpp belum ke-load setelah " +
                                 std::to_string(waitSec) + "s (game masih loading?)");
    g_attached = true;

    if (wantPool && !metapool::ready()) metapool::init();

    char baseHex[24];
    snprintf(baseHex, sizeof(baseHex), "0x%lx", (unsigned long)il2::base());
    const metapool::Stats& st = metapool::stats();
    std::string o = "{\"pkg\":" + jstr(mem::procName().empty() ? usePkg : mem::procName()) +
                    ",\"pid\":" + std::to_string(mem::pid()) +
                    ",\"lib\":" + jstr(il2::libName()) +
                    ",\"base\":" + jstr(baseHex) +
                    ",\"pool\":" + (metapool::ready() ? "true" : "false") +
                    ",\"poolErr\":" + jstr(metapool::lastError()) +
                    ",\"classes\":" + std::to_string(st.classes) +
                    ",\"methods\":" + std::to_string(st.entries) +
                    ",\"named\":" + std::to_string(st.named) + "}";
    return o;
}

static std::string cmdStatus(const std::string&) {
    std::string o = "{\"attached\":" + std::string(mem::attached() ? "true" : "false");
    if (mem::attached()) {
        char baseHex[24];
        snprintf(baseHex, sizeof(baseHex), "0x%lx", (unsigned long)il2::base());
        o += ",\"pkg\":" + jstr(mem::procName()) +
             ",\"pid\":" + std::to_string(mem::pid()) +
             ",\"lib\":" + jstr(il2::libName()) +
             ",\"base\":" + jstr(baseHex) +
             ",\"il2\":" + (il2::ready() ? "true" : "false");
    }
    o += ",\"pool\":" + std::string(metapool::ready() ? "true" : "false") +
         ",\"forced\":" + std::to_string(patch::forcedCount()) + "}";
    return o;
}

static std::string cmdClasses(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach (Attach dulu)");
    if (!g_klassBuilt) buildKlasses();
    const std::string filter = jgets(req, "filter");
    const std::string by = jgets(req, "by");
    long long cap = 10000;
    jgetLL(req, "cap", cap);
    if (cap < 100) cap = 100;
    if (cap > 50000) cap = 50000;

    std::vector<std::string> out;
    size_t total = 0;

    if (by == "method") {
        // kelas yang punya method cocok (dari pool -- murah)
        std::vector<std::string> names;
        for (const auto& e : metapool::all()) {
            if (e.name.empty() || !icontains(e.name, filter)) continue;
            std::string kn;
            if (!metapool::klassName(e.klass, kn)) continue;
            names.push_back(kn);
        }
        std::sort(names.begin(), names.end());
        names.erase(std::unique(names.begin(), names.end()), names.end());
        total = names.size();
        for (auto& n : names)
            if ((long long)out.size() < cap) out.push_back(std::move(n));
    } else if (by == "field") {
        // berat: resolve per kelas -- dibatasi biar UI nggak nunggu bermenit
        long long limit = 1500;
        jgetLL(req, "scanlimit", limit);
        size_t scanned = 0;
        for (const auto& ke : g_klasses) {
            if (scanned >= limit) break;
            ++scanned;
            if (!filter.empty() && !icontains(ke.name, filter)) continue;
            if (!explore::resolve(ke.name.c_str(), nullptr)) continue;
            bool hit = false;
            for (const auto& f : explore::info().fields) {
                if (icontains(f.name, filter)) { hit = true; break; }
            }
            if (hit) out.push_back(ke.name);
        }
        total = out.size();
    } else {
        // default: cari di nama kelas
        for (const auto& ke : g_klasses) {
            if (!filter.empty() && !icontains(ke.name, filter)) continue;
            ++total;
            if ((long long)out.size() < cap) out.push_back(ke.name);
        }
    }

    std::string o = "{\"total\":" + std::to_string(total) +
                    ",\"count\":" + std::to_string(out.size()) +
                    ",\"classes\":[";
    for (size_t i = 0; i < out.size(); ++i) {
        if (i) o += ",";
        o += jstr(out[i]);
    }
    o += "]}";
    return o;
}

static std::string cmdClass(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    const std::string name = jgets(req, "name");
    const std::string ns = jgets(req, "ns");
    if (name.empty()) throw std::runtime_error("name kosong");
    if (!explore::resolve(name.c_str(), ns.empty() ? nullptr : ns.c_str()))
        throw std::runtime_error("kelas '" + name + "' nggak ketemu");
    const explore::KlassInfo& ki = explore::info();

    // method dari pool dilebur (MLBB: jalur pool lebih lengkap)
    std::vector<explore::Method> methods = ki.methods;
    if (metapool::ready() && ki.addr) {
        std::vector<explore::Method> pm;
        if (metapool::forKlass(ki.addr, pm, 4096)) {
            for (auto& m : pm) {
                bool dup = false;
                for (const auto& old : methods) {
                    if (old.name == m.name && (old.rva == m.rva || !m.rva || !old.rva)) {
                        dup = true;
                        break;
                    }
                }
                if (!dup) methods.push_back(std::move(m));
            }
        }
    }

    std::string o = "{\"ok\":true,\"name\":" + jstr(ki.name) +
                    ",\"ns\":" + jstr(ki.ns) +
                    ",\"image\":" + jstr(ki.image) +
                    ",\"source\":" + jstr(ki.source) +
                    ",\"addr\":\"0x" + [&] {
                        char b[24];
                        snprintf(b, sizeof(b), "%lx", (unsigned long)ki.addr);
                        return std::string(b);
                    }() + "\"";
    o += ",\"fields\":[";
    for (size_t i = 0; i < ki.fields.size(); ++i) {
        const auto& f = ki.fields[i];
        if (i) o += ",";
        o += "{\"name\":" + jstr(f.name) +
             ",\"type\":" + jstr(f.typeName) +
             ",\"static\":" + std::string(f.isStatic ? "true" : "false") +
             ",\"off\":" + std::to_string(f.offset) +
             ",\"ft\":" + std::to_string((int)explore::guessType(f.typeEnum)) + "}";
    }
    o += "],\"methods\":[";
    for (size_t i = 0; i < methods.size(); ++i) {
        const auto& m = methods[i];
        if (i) o += ",";
        char rvaHex[24];
        snprintf(rvaHex, sizeof(rvaHex), "0x%llx", (unsigned long long)m.rva);
        char ptrHex[24];
        snprintf(ptrHex, sizeof(ptrHex), "0x%lx", (unsigned long)m.ptr);
        o += "{\"name\":" + jstr(m.name) +
             ",\"ptr\":" + jstr(ptrHex) +
             ",\"rva\":" + jstr(rvaHex) +
             ",\"module\":" + jstr(m.module) + "}";
    }
    o += "]}";
    return o;
}

static std::string cmdInstances(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    const std::string name = jgets(req, "name");
    long long maxN = 32;
    jgetLL(req, "max", maxN);
    if (maxN < 1) maxN = 1;
    if (maxN > 256) maxN = 256;
    if (!explore::resolve(name.c_str(), nullptr))
        throw std::runtime_error("kelas '" + name + "' nggak ketemu");
    const uintptr_t k = explore::info().addr;
    if (!k) throw std::runtime_error("alamat kelas nggak diketahui");
    std::vector<uintptr_t> objs;
    il2::findInstances(k, objs, (size_t)maxN);
    std::string o = "{\"count\":" + std::to_string(objs.size()) + ",\"addrs\":[";
    for (size_t i = 0; i < objs.size(); ++i) {
        if (i) o += ",";
        char b[24];
        snprintf(b, sizeof(b), "0x%lx", (unsigned long)mem::untag(objs[i]));
        o += jstr(b);
    }
    o += "]}";
    return o;
}

static std::string viewToText(const std::string& spec, size_t defLen, size_t maxLen,
                              bool asmMode, size_t maxIns) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    uintptr_t addr = 0;
    size_t len = 0;
    std::string label;
    if (!parseViewSpec(spec, addr, len, defLen, maxLen, &label) || !addr)
        throw std::runtime_error("spek nggak kebaca (Class::Method / lib.so:0xRVA / 0xADDR)");
    std::vector<uint8_t> buf(len, 0);
    if (!mem::read(addr, buf.data(), buf.size()))
        throw std::runtime_error("gagal baca memori 0x" +
                                 [&] { char b[24]; snprintf(b, sizeof(b), "%lx", (unsigned long)addr); return std::string(b); }());
    std::string text;
    if (asmMode) {
        for (const auto& ln : memview::disasm(buf.data(), buf.size(), addr, maxIns))
            text += ln + "\n";
    } else {
        text = memview::hexdump(addr, buf.data(), buf.size());
    }
    std::string forced;
    if (patch::isForced(addr, &forced) && !forced.empty()) text += "\n[forceret] " + forced;
    char hdr[96];
    snprintf(hdr, sizeof(hdr), "%s @0x%lx (%zu byte)\n", label.c_str(),
             (unsigned long)addr, len);
    return std::string(hdr) + text;
}

static std::string cmdHex(const std::string& req) {
    const std::string spec = jgets(req, "spec");
    long long len = 64;
    jgetLL(req, "len", len);
    if (len < 4) len = 4;
    if (len > 2048) len = 2048;
    return jstr(viewToText(spec, (size_t)len, 2048, false, 0));
}

static std::string cmdAsm(const std::string& req) {
    const std::string spec = jgets(req, "spec");
    long long len = 160;
    jgetLL(req, "len", len);
    if (len < 16) len = 16;
    if (len > 512) len = 512;
    return jstr(viewToText(spec, (size_t)len, 512, true, 64));
}

static std::string genCodeCommon(const std::string& req, bool lua) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    const std::string cls = jgets(req, "cls");
    const std::string mth = jgets(req, "method");
    const std::string value = jgets(req, "val");
    const std::string type = jgets(req, "type");
    if (cls.empty() || mth.empty()) throw std::runtime_error("cls/method kosong");
    gencode::Target t;
    int hits = 0;
    if (!genResolve(cls, mth, t, hits) || !hits)
        throw std::runtime_error(cls + "::" + mth + " nggak ketemu (pool perlu?)");
    std::string expr, kind;
    genRetExpr(value, type, expr, kind);
    std::string code;
    bool sigOk = !t.sig.ret.empty();
    if (lua) {
        double num = 0;
        if (!expr.empty() && expr != "@ret" && expr != "NULL" && expr != "true" &&
            expr != "false")
            num = strtod(expr.c_str(), nullptr);
        else if (expr == "true")
            num = 1.0;
        code = gencode::ggLua(t, mem::pid(), kind, num);
    } else {
        code = gencode::dobbyHook(t, expr);
    }
    std::string o = "{\"code\":" + jstr(code) +
                    ",\"sigOk\":" + std::string(sigOk ? "true" : "false") +
                    ",\"hits\":" + std::to_string(hits) +
                    ",\"rva\":\"0x" + [&] {
                        char b[24];
                        snprintf(b, sizeof(b), "%llx", (unsigned long long)t.rva);
                        return std::string(b);
                    }() + "\"" +
                    ",\"module\":" + jstr(t.module) + "}";
    return o;
}

static std::string cmdGencode(const std::string& req) { return genCodeCommon(req, false); }
static std::string cmdGgcode(const std::string& req) { return genCodeCommon(req, true); }

static std::string cmdDump(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    dumpout::Options o;
    o.allLibs = jgetBool(req, "allLibs", true);
    o.json    = jgetBool(req, "json", true);
    o.header  = jgetBool(req, "header", true);
    o.ptr     = jgetBool(req, "ptr", true);
    o.tips    = o.ptr;
    long long maxClasses = 0;
    jgetLL(req, "maxClasses", maxClasses);
    o.maxClasses = maxClasses > 0 ? (size_t)maxClasses : 0;
    o.nsFilter  = jgets(req, "ns");
    o.likeFilter = jgets(req, "like");
    const std::string outdir = jgets(req, "outdir");
    if (!outdir.empty()) o.outDir = outdir;

    dumpout::Result r;
    const bool ok = dumpout::run(o, r);
    std::string so = "{\"ok\":" + std::string(ok ? "true" : "false") +
                     ",\"classes\":" + std::to_string(r.classes) +
                     ",\"fields\":" + std::to_string(r.fields) +
                     ",\"methods\":" + std::to_string(r.methods) +
                     ",\"aborted\":" + std::string(r.aborted ? "true" : "false") +
                     ",\"dir\":" + jstr(r.dir) +
                     ",\"path\":" + jstr(r.path) +
                     ",\"jsonPath\":" + jstr(r.jsonPath) +
                     ",\"headerPath\":" + jstr(r.headerPath) +
                     ",\"ptrsPath\":" + jstr(r.ptrsPath) + "}";
    if (!ok && !r.aborted) throw std::runtime_error("dump gagal (lihat log)");
    return so;
}

static std::string cmdDumpStop(const std::string&) {
    dumpout::requestAbort();
    return "{\"ok\":true}";
}

static std::string cmdForce(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    const std::string cls = jgets(req, "cls");
    const std::string mth = jgets(req, "method");
    const std::string value = jgets(req, "val");
    const std::string type = jgets(req, "type");
    if (cls.empty() || mth.empty()) throw std::runtime_error("cls/method kosong");
    gencode::Target t;
    int hits = 0;
    if (!genResolve(cls, mth, t, hits) || !hits)
        throw std::runtime_error(cls + "::" + mth + " nggak ketemu");
    const uintptr_t addr = codeAddrOf(t.module, t.rva);
    if (!addr) throw std::runtime_error("alamat kode nggak ketemu");

    std::string expr, kind;
    genRetExpr(value, type, expr, kind);
    patch::Ret k = patch::Ret::I32;
    if (type.empty()) {
        // nebak dari bentuk nilai (sama kayak genRetExpr)
        const std::string low = lower(value);
        if (value.empty() || low == "off" || low == "nop" || low == "void")
            k = patch::Ret::Void;
        else if (low == "true" || low == "false")
            k = patch::Ret::Bool;
        else if (value.find('.') != std::string::npos)
            k = patch::Ret::Float;
    } else if (!patch::retFromName(type, k)) {
        throw std::runtime_error("tipe '" + type + "' nggak dikenal");
    }
    double num = 0;
    uint64_t ptr = 0;
    if (k == patch::Ret::Ptr)
        ptr = strtoull(value.c_str(), nullptr, 0);
    else
        num = strtod(value.c_str(), nullptr);

    std::string err;
    if (!patch::forceReturn(addr, k, num, ptr, cls + "::" + mth, t.module, t.rva, &err))
        throw std::runtime_error("force gagal: " + err);
    std::string o = "{\"ok\":true,\"addr\":\"0x" + [&] {
        char b[24];
        snprintf(b, sizeof(b), "%lx", (unsigned long)addr);
        return std::string(b);
    }() + "\",\"forced\":" + std::to_string(patch::forcedCount()) +
        ",\"summary\":" + jstr(patch::forcedSummary()) + "}";
    return o;
}

static std::string cmdUnforce(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    if (jgetBool(req, "all", false)) {
        const size_t n = patch::forcedCount();
        patch::unforceAll();
        return "{\"ok\":true,\"restored\":" + std::to_string(n) + "}";
    }
    const std::string cls = jgets(req, "cls");
    const std::string mth = jgets(req, "method");
    gencode::Target t;
    int hits = 0;
    if (!genResolve(cls, mth, t, hits) || !hits)
        throw std::runtime_error(cls + "::" + mth + " nggak ketemu");
    const uintptr_t addr = codeAddrOf(t.module, t.rva);
    if (!addr) throw std::runtime_error("alamat kode nggak ketemu");
    const bool ok = patch::unforceReturn(addr);
    return std::string("{\"ok\":") + (ok ? "true" : "false") +
           ",\"forced\":" + std::to_string(patch::forcedCount()) + "}";
}

static std::string cmdForced(const std::string&) {
    std::string o = "{\"count\":" + std::to_string(patch::forcedCount()) +
                    ",\"summary\":" + jstr(patch::forcedSummary()) +
                    ",\"fileCount\":" + std::to_string(patch::fileForcedCount()) + "}";
    return o;
}

static std::string cmdPatch(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    if (jgetBool(req, "list", false)) {
        auto& l = patch::list();
        std::string o = "{\"count\":" + std::to_string(l.size()) + ",\"entries\":[";
        for (size_t i = 0; i < l.size(); ++i) {
            const auto& e = l[i];
            if (i) o += ",";
            o += "{\"idx\":" + std::to_string(i) + ",\"label\":" + jstr(e.label) +
                 ",\"module\":" + jstr(e.module) + ",\"rva\":\"0x" + [&] {
                     char b[24];
                     snprintf(b, sizeof(b), "%llx", (unsigned long long)e.rva);
                     return std::string(b);
                 }() + "\",\"on\":" + std::string(e.on ? "true" : "false") +
                 ",\"verified\":" + std::string(e.verified ? "true" : "false") + "}";
        }
        o += "]}";
        return o;
    }
    long long idx = -1;
    jgetLL(req, "idx", idx);
    if (idx >= 0) {
        const bool on = jgetBool(req, "on", true);
        const bool ok = patch::setOn((size_t)idx, on);
        return std::string("{\"ok\":") + (ok ? "true" : "false") + "}";
    }
    const std::string line = jgets(req, "line");
    if (line.empty()) throw std::runtime_error("line kosong");
    std::string err;
    if (!patch::addSpec(line, &err)) throw std::runtime_error("spek nggak kebaca: " + err);
    auto& l = patch::list();
    const size_t idxNew = l.size() - 1;
    const bool applied = patch::applyEntry(idxNew);
    return std::string("{\"ok\":") + (applied ? "true" : "false") +
           ",\"idx\":" + std::to_string(idxNew) +
           ",\"verified\":" + std::string(l[idxNew].verified ? "true" : "false") + "}";
}

static bool parseIoType(const std::string& s, int& sizeOut, bool& fpOut, bool& sgOut) {
    std::string t = lower(s);
    sgOut = true;
    if (!t.empty() && t[0] == 'u') { sgOut = false; t = t.substr(1); }
    fpOut = false;
    if (t == "i8" || t == "byte")  { sizeOut = 1; return true; }
    if (t == "i16" || t == "short"){ sizeOut = 2; return true; }
    if (t == "i32" || t == "int")  { sizeOut = 4; return true; }
    if (t == "i64" || t == "long") { sizeOut = 8; return true; }
    if (t == "f32" || t == "float")  { sizeOut = 4; fpOut = true; return true; }
    if (t == "f64" || t == "double") { sizeOut = 8; fpOut = true; return true; }
    if (t == "ptr") { sizeOut = 8; return true; }
    return false;
}

static std::string cmdRead(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    long long addr = 0;
    jgetLL(req, "addr", addr);
    const std::string type = jgets(req, "type");
    if (!addr) throw std::runtime_error("addr 0");
    int sz = 4;
    bool fp = false, sg = true;
    if (!parseIoType(type, sz, fp, sg))
        throw std::runtime_error("tipe nggak dikenal: " + type);
    uint8_t raw[8] = {0};
    if (!mem::read((uintptr_t)addr, raw, (size_t)sz))
        throw std::runtime_error("baca gagal");
    double val = 0;
    if (fp && sz == 4) { float f; memcpy(&f, raw, 4); val = f; }
    else if (fp && sz == 8) { double d; memcpy(&d, raw, 8); val = d; }
    else if (sz == 1) val = sg ? (double)(int8_t)raw[0] : (double)raw[0];
    else if (sz == 2) { uint16_t u; memcpy(&u, raw, 2); val = sg ? (double)(int16_t)u : (double)u; }
    else if (sz == 4) { uint32_t u; memcpy(&u, raw, 4); val = sg ? (double)(int32_t)u : (double)u; }
    else { uint64_t u; memcpy(&u, raw, 8); val = sg ? (double)(int64_t)u : (double)u; }
    char b[64];
    snprintf(b, sizeof(b), "%.17g", val);
    return std::string("{\"val\":") + b + "}";
}

static std::string cmdWrite(const std::string& req) {
    if (!mem::attached()) throw std::runtime_error("belum attach");
    long long addr = 0;
    jgetLL(req, "addr", addr);
    const std::string type = jgets(req, "type");
    long double valLd = 0;
    {
        std::string vs = jgets(req, "val");
        if (!vs.empty()) valLd = strtold(vs.c_str(), nullptr);
        else {
            long long vi = 0;
            if (jgetLL(req, "val", vi)) valLd = (long double)vi;
        }
    }
    if (!addr) throw std::runtime_error("addr 0");
    int sz = 4;
    bool fp = false, sg = true;
    if (!parseIoType(type, sz, fp, sg))
        throw std::runtime_error("tipe nggak dikenal: " + type);
    uint8_t raw[8] = {0};
    if (fp && sz == 4) { float f = (float)valLd; memcpy(raw, &f, 4); }
    else if (fp && sz == 8) { double d = (double)valLd; memcpy(raw, &d, 8); }
    else {
        long long v = (long long)valLd;
        if (sz == 1) raw[0] = (uint8_t)v;
        else if (sz == 2) { uint16_t u = (uint16_t)v; memcpy(raw, &u, 2); }
        else if (sz == 4) { uint32_t u = (uint32_t)v; memcpy(raw, &u, 4); }
        else { uint64_t u = (uint64_t)v; memcpy(raw, &u, 8); }
    }
    if (!mem::write((uintptr_t)addr, raw, (size_t)sz))
        throw std::runtime_error("tulis gagal (halaman read-only?)");
    uint8_t back[8] = {0};
    const bool verified = mem::read((uintptr_t)addr, back, (size_t)sz) &&
                          memcmp(back, raw, (size_t)sz) == 0;
    return std::string("{\"ok\":true,\"verified\":") + (verified ? "true" : "false") + "}";
}

static std::string cmdLog(const std::string& req) {
    long long n = 30;
    jgetLL(req, "n", n);
    if (n < 1) n = 1;
    if (n > 200) n = 200;
    const int total = logx::count();
    std::string o = "{\"lines\":[";
    long long added = 0;
    for (long long i = 0; i < n && i < total; ++i) {
        const char* l = logx::line((int)i);  // 0 = terbaru
        if (!l) continue;
        if (added++) o += ",";
        o += jstr(l);
    }
    o += "]}";
    return o;
}

static std::string cmdDetach(const std::string&) {
    mem::detach();
    g_attached = false;
    g_klassBuilt = false;
    g_klasses.clear();
    return "{\"ok\":true}";
}

// ---------------------------------------------------------------------------
//  Loop utama: satu request JSON per baris stdin -> satu response per baris
// ---------------------------------------------------------------------------
typedef std::string (*Handler)(const std::string&);

static Handler findCmd(const std::string& c) {
    if (c == "ping")     return cmdPing;
    if (c == "procs")    return cmdProcs;
    if (c == "attach")   return cmdAttach;
    if (c == "status")   return cmdStatus;
    if (c == "classes")  return cmdClasses;
    if (c == "class")    return cmdClass;
    if (c == "instances")return cmdInstances;
    if (c == "hex")      return cmdHex;
    if (c == "asm")      return cmdAsm;
    if (c == "gencode")  return cmdGencode;
    if (c == "ggcode")   return cmdGgcode;
    if (c == "dump")     return cmdDump;
    if (c == "dumpstop") return cmdDumpStop;
    if (c == "force")    return cmdForce;
    if (c == "unforce")  return cmdUnforce;
    if (c == "forced")   return cmdForced;
    if (c == "patch")    return cmdPatch;
    if (c == "read")     return cmdRead;
    if (c == "write")    return cmdWrite;
    if (c == "log")      return cmdLog;
    if (c == "detach")   return cmdDetach;
    return nullptr;
}

int main(int argc, char** argv) {
    bool rpc = false;
    for (int i = 1; i < argc; ++i)
        if (!strcmp(argv[i], "--rpc")) rpc = true;
    if (!rpc) {
        fprintf(stderr,
                "panxcz_rpc: jalankan dengan --rpc (JSON-line server di stdin/stdout)\n"
                "contoh  : su -c '%s --rpc'\n", argv[0]);
        return 2;
    }
    // stderr buat log diagnostik; stdout KHUSUS response JSON.
    setvbuf(stdout, nullptr, _IOLBF, 0);
    setvbuf(stderr, nullptr, _IOLBF, 0);
    logx::add("[rpc] siap (pid %d)", getpid());

    std::string line;
    while (true) {
        line.clear();
        int ch;
        bool any = false;
        while ((ch = getchar()) != EOF && ch != '\n') {
            any = true;
            if (ch != '\r') line += (char)ch;
        }
        if (ch == EOF && !any) break;  // parent mati -> keluar
        if (line.empty()) continue;

        // debug: catat request mentah apa adanya (stderr -> rpc.log)
        fprintf(stderr, "[rpc] req: %.200s\n", line.c_str());

        long long id = 0;
        jgetLL(line, "id", id);
        const std::string cmd = jgets(line, "cmd");
        std::string resp = "{\"id\":" + std::to_string(id);
        Handler h = findCmd(cmd);
        if (!cmd.empty() && h) {
            try {
                const std::string data = h(line);
                resp += ",\"ok\":true,\"d\":" + data;
            } catch (const std::exception& e) {
                resp += ",\"ok\":false,\"err\":" + jstr(e.what());
            } catch (...) {
                resp += ",\"ok\":false,\"err\":\"unknown error\"";
            }
        } else {
            resp += ",\"ok\":false,\"err\":" + jstr("cmd nggak dikenal: " + cmd);
        }
        resp += "}";
        fwrite(resp.data(), 1, resp.size(), stdout);
        fputc('\n', stdout);
        fflush(stdout);
    }
    mem::detach();
    return 0;
}
