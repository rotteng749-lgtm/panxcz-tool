#ifndef PANXCZ_JNI_BRIDGE_H
#define PANXCZ_JNI_BRIDGE_H
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Initialize libtool core */
JNIEXPORT void JNICALL Java_catch_me_if_you_can_Panxcz_nativeInit(JNIEnv *env, jclass clazz, jstring pkg, jstring lib);

/* Detach */
JNIEXPORT void JNICALL Java_catch_me_if_you_can_Panxcz_nativeDetach(JNIEnv *env, jclass clazz);

/* List classes from il2cpp (filter by namespace/class name, empty = all) */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeListClasses(JNIEnv *env, jclass clazz, jstring filter);

/* Get fields for a class -- returns JSON array of {name, offset, type, static} */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeGetFields(JNIEnv *env, jclass clazz, jstring className);

/* Get methods for a class -- returns JSON array of {name, rva, ptrHex, module} */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeGetMethods(JNIEnv *env, jclass clazz, jstring className);

/* Resolve class info (explore) -- returns JSON with name/ns/fields/methods/statics */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeExplore(JNIEnv *env, jclass clazz, jstring className);

/* Find instances of a class -- returns JSON array of object addresses */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeInstances(JNIEnv *env, jclass clazz, jstring className);

/* Read memory value (int/float/double/pointer) at address */
JNIEXPORT jdouble JNICALL Java_catch_me_if_you_can_Panxcz_nativeReadValue(JNIEnv *env, jclass clazz, jlong address, jint valueType);

/* Write memory value */
JNIEXPORT jboolean JNICALL Java_catch_me_if_you_can_Panxcz_nativeWriteValue(JNIEnv *env, jclass clazz, jlong address, jint valueType, jdouble value);

/* Run il2cpp dump (offline dump) with filter */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeDumpOutput(JNIEnv *env, jclass clazz, jstring filter);

/* Memory scanner first pass (value, type, region) -- returns JSON array of hits */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeScan(JNIEnv *env, jclass clazz, jlong value, jint type, jstring region);

/* Get process region info for target package */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeAttachInfo(JNIEnv *env, jclass clazz);

/* Spoofed identity: return a fake package/version string for external checks */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeSpoofIdentity(JNIEnv *env, jclass clazz);

/* Assembly-CSharp dump: emit .cs file from runtime + dump merge */
JNIEXPORT jstring JNICALL Java_catch_me_if_you_can_Panxcz_nativeDumpAssemblyCSharp(JNIEnv *env, jclass clazz, jstring outFilePath);

#ifdef __cplusplus
}
#endif
#endif
