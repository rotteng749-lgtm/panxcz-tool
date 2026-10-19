#!/usr/bin/env bash
# ============================================================================
#  build_native.sh - build libpanxcz.so (JNI) + ELF panxcz_libtool
#
#  NDK diprioritaskan ke folder download lu:
#    /home/panxcz/Documents/workspace/16251728/ndk/android-ndk-r28b
#  Kalau nggak ada, fall back ke NDK di ~/. atau /opt.
#
#  Output:
#    app/src/main/jniLibs/arm64-v8a/libpanxcz.so   -> dipaket ke APK
#    panxcz-libtool/out/panxcz_libtool{,.sh,.elf}  -> ELF standalone
# ============================================================================
set -uo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIBTOOL="$HERE/panxcz-libtool"

NDK_CANDIDATES=(
    "/home/panxcz/Documents/workspace/16251728/ndk/android-ndk-r28b"
    "/home/panxcz/android-ndk-r28b/android-ndk-r28b"
    "${NDK_HOME:-}"
    "${ANDROID_NDK_HOME:-}"
    "${ANDROID_NDK_ROOT:-}"
    "/opt/android-ndk-r28b"
    "/usr/local/android-ndk-r28b"
)

NDK=""
for c in "${NDK_CANDIDATES[@]}"; do
    [ -n "$c" ] || continue
    if [ -x "$c/ndk-build" ]; then NDK="$c"; break; fi
done

if [ -z "$NDK" ]; then
    echo "[x] NDK r28b nggak ketemu." >&2
    echo "    Taruh di: /home/panxcz/Documents/workspace/16251728/ndk/android-ndk-r28b" >&2
    echo "    atau set NDK_HOME=/path/ke/ndk" >&2
    exit 1
fi

export NDK_HOME="$NDK"
ABI="arm64-v8a"
JOBS="$(nproc 2>/dev/null || echo 4)"

echo "[*] NDK   : $NDK"
echo "[*] ABI   : $ABI"
echo "[*] jobs  : $JOBS"

# --- build ---------------------------------------------------------------
"$NDK/ndk-build" \
    NDK_PROJECT_PATH="$LIBTOOL" \
    APP_BUILD_SCRIPT="$LIBTOOL/jni/Android.mk" \
    NDK_APPLICATION_MK="$LIBTOOL/jni/Application.mk" \
    NDK_OUT="$LIBTOOL/obj" \
    NDK_LIBS_OUT="$LIBTOOL/libs" \
    APP_ABI="$ABI" \
    -j"$JOBS" || exit 1

SO="$LIBTOOL/libs/$ABI/libpanxcz.so"
[ -f "$SO" ] || { echo "[x] libpanxcz.so nggak keproduksi" >&2; exit 1; }

# --- copy ke jniLibs APK --------------------------------------------------
DEST="$HERE/app/src/main/jniLibs/$ABI"
mkdir -p "$DEST"
cp -f "$SO" "$DEST/libpanxcz.so"
chmod 644 "$DEST/libpanxcz.so"

echo
echo "[+] native siap:"
ls -lh "$DEST/libpanxcz.so" | awk '{printf "    %10s  %s\n", $5, $9}'
echo "    ELF standalone: panxcz-libtool/out/panxcz_libtool"
