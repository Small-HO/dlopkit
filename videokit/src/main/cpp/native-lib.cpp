#include "native-lib.h"
#include "libvideo/video_decode.h"

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


extern "C"
JNIEXPORT void JNICALL
Java_com_small_videokit_FFmpegNativeUtils_startVideo(JNIEnv *env, jobject thiz, jstring path,jobject surface) {
    //  转换视频路径
    auto url = env->GetStringUTFChars(path, nullptr);
    //  1.获取上下文
    AVFormatContext *context = avformat_alloc_context();
    //  2.流类型
    StreamType index = stream_code(context,url);
    //  3.解码器
    AVCodecContext *av_context = decoder_context(context, index.video);
    //  4.设置缓冲区
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    SwsContext *sws_context = decoder_sws(av_context, window);
    //  5.逐帧解析
    decoder_start(context, av_context, sws_context, window, index.video);
}