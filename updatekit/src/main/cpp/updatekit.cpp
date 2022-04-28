#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
extern int bsdiff_main(int argc, char *argv[]);
extern int bspatch_main(int argc, char *argv[]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_small_updatekit_UpdateNativeUtils_diff(JNIEnv *env, jobject, jstring new_path,jstring old_path, jstring patch_path) {
    const char *oldFile = env->GetStringUTFChars(old_path, nullptr);
    const char *newFile = env->GetStringUTFChars(new_path, nullptr);
    const char *patchFile = env->GetStringUTFChars(patch_path, nullptr);
    const char *argv[] = {"updatekit", oldFile, newFile, patchFile};
    int res = bsdiff_main(4, const_cast<char **>(argv));
    env->ReleaseStringUTFChars(old_path, oldFile);
    env->ReleaseStringUTFChars(new_path, newFile);
    env->ReleaseStringUTFChars(patch_path, patchFile);
    return res;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_small_updatekit_UpdateNativeUtils_patch(JNIEnv *env, jobject, jstring old_path,jstring patch_path, jstring combine_path) {
    const char *oldFile = env->GetStringUTFChars(old_path, nullptr);
    const char *patchFile = env->GetStringUTFChars(patch_path, nullptr);
    const char *combineFile = env->GetStringUTFChars(combine_path, nullptr);
    const char *argv[] = {"updatekit", oldFile, combineFile, patchFile};
    int res = bspatch_main(4, const_cast<char **>(argv));
    env->ReleaseStringUTFChars(old_path, oldFile);
    env->ReleaseStringUTFChars(patch_path, patchFile);
    env->ReleaseStringUTFChars(combine_path, combineFile);
    return res;
}