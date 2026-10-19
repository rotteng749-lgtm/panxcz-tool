# panxcz-tool

Reconstructed **GameGuardian (96.0)** source, renamed to `panxcz-tool`.

- **Spoofed version:** `14.0-spoofed`
- **Target:** Android 14–17 (`compileSdk 36`, `minSdk 21`)
- **Native:** `panxcz-libtool` embedded + `libpanxcz.so` (JNI bridge, 14 exports)
- **Explorer:** IL2CPP per-function explorer + `assembly-csharp.dll` dump (`Panxcz.java`, `service_explorer.xml`)
- **License:** Apache 2.0

## Build

```bash
./build_native.sh        # build libpanxcz.so with NDK r28b
./gradlew :app:assembleRelease
```

Keystore voor signing ada di repo private [`panxcz-keystore`](https://github.com/rotteng749-lgtm/panxcz-keystore).

## Repos

- **Public:** https://github.com/rotteng749-lgtm/panxcz-tool
- **Private keystore:** https://github.com/rotteng749-lgtm/panxcz-keystore
