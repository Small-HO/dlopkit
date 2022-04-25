#include "native-lib.h"

/*
 *  1. 解封装
 *  2. 获取视频流索引
 *  3. 解码
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_small_videokit_FFmpegNativeUtils_videoPlay(JNIEnv *env, jobject thiz, jstring path,jobject surface) {
    //  转换视频路径
    auto audio_path = env->GetStringUTFChars(path, nullptr);
    //  封装格式上下文
    AVFormatContext *av_context = avformat_alloc_context();
    //  绘制窗体
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);

    //  1. 解封装
    delidding(audio_path, av_context);
    //  2. 视频索引
    int position = indexes(av_context);
    //  3. 解码
    decoder(window,position,av_context);

    env->ReleaseStringUTFChars(path, audio_path);
}