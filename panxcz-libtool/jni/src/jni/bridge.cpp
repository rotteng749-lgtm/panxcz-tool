#include "jni/bridge.h"
#include <core/mem.h>
#include <core/il2cpp.h>
#include <core/target.h>
#include <core/explore.h>
#include <core/dumpout.h>
#include <core/patch.h>
#include <core/scanner.h>
#include <core/log.h>
#include <cstring>
#include <string>

using namespace mem;
using namespace explore;
using namespace dumpout;

static std::string fromJString(JNIEnv *env, jstring s) {
    if (!s) return "";
    const char *c = env->GetStringUTFChars(s, nullptr);
    std::string r(c ? c : "");
    if (c) env->ReleaseStringUTFChars(s, c);
    return r;
}
static jstring toJString(JNIEnv *env, const char *utf) {
    return env->NewStringUTF(utf ? utf : "");
}
static jstring JStr(JNIEnv *env, const std::string &s) { return toJString(env, s.c_str()); }

JNIEXPORT void JNICALL Java_catch_me_if_you_can_Panxcz_nativeInit(JNIEnv *env, jclass, jstring pkg, jstring lib) {
    mem::attach(fromJString(env, pkg).c_str());
    il2::init();
    target::load();
    std::string l = fromJString(env, lib);
    if (!l.empty()) target::setLib(l.c_str());
    target::attach();
}

JNIEXPORT void JNICALL Java_catch_me_if_you_can_Panxcz_nativeDetach(JNIEnv *, jclass) { mem::detach(); }

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeListClasses(JNIEnv *env, jclass, jstring filter) {
    std::string f = fromJString(env, filter);
    (void)f; // filter applied via findKlassBatch if needed; demo uses common names
    std::string out = "{\"classes\":[\"RunSessionData\",\"PlayerData\",\"Hero\",\"BattleManager\",\"ShowEntity\",\"GameSession\",\"MonoBehaviour\"]}";
    return toJString(env, out.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeGetFields(JNIEnv *env, jclass, jstring className) {
    std::string cn = fromJString(env, className);
    explore::resolve(cn.c_str());
    KlassInfo &ki = explore::info();
    std::string out = "{\"fields\":[";
    int c = 0;
    for (auto &f : ki.fields) {
        if (c++) out += ",";
        out += "{\"name\":\"" + f.name + "\",\"offset\":" + std::to_string(f.offset) +
               ",\"type\":\"" + std::string(f.typeName) + "\",\"token\":" + std::to_string(f.token) +
               ",\"static\":" + std::string(f.isStatic ? "true" : "false") + "}";
    }
    out += "],\"ok\":" + std::string(ki.ok ? "true" : "false") + "}";
    return toJString(env, out.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeGetMethods(JNIEnv *env, jclass, jstring className) {
    std::string cn = fromJString(env, className);
    explore::resolve(cn.c_str());
    KlassInfo &ki = explore::info();
    std::string out = "{\"methods\":[";
    int c = 0;
    for (auto &m : ki.methods) {
        if (c++) out += ",";
        char ptrHex[32]; snprintf(ptrHex, sizeof(ptrHex), "0x%lx", (unsigned long)m.ptr);
        out += "{\"name\":\"" + m.name + "\",\"rva\":" + std::to_string(m.rva) +
               ",\"ptr\":\"" + std::string(ptrHex) + "\",\"module\":\"" + m.module + "\"}";
    }
    out += "],\"ok\":" + std::string(ki.ok ? "true" : "false") + "}";
    return toJString(env, out.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeExplore(JNIEnv *env, jclass, jstring className) {
    std::string cn = fromJString(env, className);
    bool ok = explore::resolve(cn.c_str());
    KlassInfo &ki = explore::info();
    std::string out = "{\"ok\":" + std::string(ok ? "true" : "false") +
                      ",\"name\":\"" + ki.name + "\",\"namespace\":\"" + ki.ns +
                      "\",\"image\":\"" + ki.image + "\",\"statics_addr\":" + std::to_string(ki.statics);
    out += "}"; return toJString(env, out.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeInstances(JNIEnv *env, jclass, jstring className) {
    std::string cn = fromJString(env, className);
    explore::resolve(cn.c_str());
    std::vector<uintptr_t> v = explore::instances(32);
    std::string out = "{\"count\":" + std::to_string(v.size()) + ",\"instances\":[";
    for (size_t i = 0; i < v.size(); ++i) {
        if (i) out += ",";
        char buf[32]; snprintf(buf, sizeof(buf), "%p", (void*)v[i]); out += "\"" + std::string(buf) + "\"";
    }
    out += "]}"; return toJString(env, out.c_str());
}

JNIEXPORT jdouble JNICALL Java_catch_me_if_you_can_Panxcz_nativeReadValue(JNIEnv *, jclass, jlong addr, jint type) {
    int v = 0; float f = 0; double d = 0;
    if (type == 0 || type == 1) { mem::rd<int>((uintptr_t)addr, v); return (double)v; }
    else if (type == 3) { mem::rd<float>((uintptr_t)addr, f); return (double)f; }
    else { mem::rd<double>((uintptr_t)addr, d); return (double)d; }
    return 0.0;
}

JNIEXPORT jboolean JNICALL Java_catch_me_if_you_can_Panxcz_nativeWriteValue(JNIEnv *, jclass, jlong addr, jint type, jdouble value) {
    int iv = (int)value; float fv = (float)value; double dv = (double)value;
    if (type == 0 || type == 1) return (bool)mem::wr<int>((uintptr_t)addr, iv);
    else if (type == 3) return (bool)mem::wr<float>((uintptr_t)addr, fv);
    else return (bool)mem::wr<double>((uintptr_t)addr, dv);
    return false;
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeDumpOutput(JNIEnv *env, jclass, jstring filter) {
    std::string f = fromJString(env, filter);
    Options o; o.allLibs = true; o.likeFilter = f; o.outDir = "/sdcard/panxcz-tools/dumper/";
    if (!f.empty()) { o.nsFilter = ""; }
    else { o.nsFilter = ""; }
    Result r; bool ok = run(o, r);
    std::string out = "{\"ok\":" + std::string(ok ? "true" : "false") + ",\"classes\":" + std::to_string(r.classes) +
                      ",\"dir\":\"" + r.dir + "\"}";
    return toJString(env, out.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeScan(JNIEnv *env, jclass, jlong value, jint type, jstring region) {
    std::string reg = fromJString(env, region);
    (void)reg; (void)type;
    std::vector<uintptr_t> hits;
    mem::scanQwordMasked((uint64_t)value, 0xFFFFFFFFFFFFFFFFULL, hits, 128, true);
    std::string out = "{\"count\":" + std::to_string(hits.size()) + ",\"hits\":[";
    for (size_t i = 0; i < hits.size() && i < 8; ++i) {
        if (i) out += ",";
        char buf[32]; snprintf(buf, sizeof(buf), "%p", (void*)hits[i]); out += "\"" + std::string(buf) + "\"";
    }
    out += "]}"; return toJString(env, out.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeAttachInfo(JNIEnv *env, jclass) {
    std::string info = "{\"pid\":" + std::to_string(mem::pid()) +
                      ",\"proc\":\"" + mem::procName() + "\",\"regions\":" + std::to_string(mem::regions().size()) + "}";
    return toJString(env, info.c_str());
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeSpoofIdentity(JNIEnv *env, jclass) {
    // Spoofed version identity: different package/version/label
    return toJString(env, "{\"package_name\":\"com.android.systemui\",\"version_name\":\"14.0-spoofed\",\"label\":\"SystemUI\",\"build_type\":\"release-spoof\"}");
}

JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeDumpAssemblyCSharp(JNIEnv *env, jclass, jstring outFilePath) {
    std::string fp = fromJString(env, outFilePath);
    bool ok = false;
    if (fp.empty()) {
        std::string outPath;
        ok = explore::dumpTo("/sdcard/panxcz-tools/", &outPath);
        fp = outPath;
    }
    std::string result = "{\"ok\":" + std::string(ok ? "true" : "false") +
                        ",\"path\":\"" + fp + "\"}";
    return toJString(env, result.c_str());
}
