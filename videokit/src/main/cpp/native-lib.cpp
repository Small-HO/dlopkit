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

/** 提取视频 */
extern "C"
JNIEXPORT void JNICALL
Java_com_small_videokit_FFmpegNativeUtils_extractVideo(JNIEnv *env, jobject thiz, jstring video_path, jstring video_output_path) {
    //  转换视频路径
    auto video_path_str = env->GetStringUTFChars(video_path, nullptr);
    auto video_output_path_str = env->GetStringUTFChars(video_output_path, nullptr);
    // 1. 获取上下文
    AVFormatContext *context = avformat_alloc_context();
    //  2.流类型
    StreamType index = stream_code(context,video_path_str);
    //  3.解码器
    AVCodecContext *av_context = decoder_context(context, index.video);

    // 7. 初始化输出文件
    FILE *output_file = fopen(video_output_path_str, "wb");
    if (!output_file) {
        return;
    }
    //  逐帧解析
    uint8_t *dst_data[4];
    int dst_line_size[4];
    av_image_alloc(dst_data, dst_line_size, av_context->width, av_context->height,AV_PIX_FMT_RGBA, 1);
    AVPacket *av_packet = av_packet_alloc();
    while (av_read_frame(context, av_packet) >= 0) {
        //  发送数据包到解析队列
        avcodec_send_packet(av_context, av_packet);
        AVFrame *av_frame = av_frame_alloc();
        //  接收
        int av_receive_result = avcodec_receive_frame(av_context, av_frame);
        if (av_receive_result == AVERROR(EAGAIN)) {
            continue;
        } else if (av_receive_result < 0) {
            break;
        }
        //  数据放到RGB目标容器
//        av_image_alloc(dst_data, dst_line_size, av_context->width, av_context->height,AV_PIX_FMT_RGBA, 1);
//        sws_scale(sws_context, av_frame->data, av_frame->linesize, 0, av_frame->height, dst_data,dst_line_size);

        fwrite(dst_data[0], 1, av_context->width * av_context->height * 4, output_file);
        av_freep(&dst_data[0]);
        av_packet_unref(av_packet);
        //  释放资源
        av_frame_free(&av_frame);
        av_packet_clone(av_packet);
    }
    env->ReleaseStringUTFChars(video_path, video_path_str);
    env->ReleaseStringUTFChars(video_output_path, video_output_path_str);
}



/** 提取音频文件 0: 音频 1: 视频 */
extern "C"
JNIEXPORT void JNICALL
Java_com_small_videokit_FFmpegNativeUtils_extractMedia(JNIEnv *env, jobject thiz, jstring input_path, jstring output_path, jint media_type) {
    const char *input_path_str = env->GetStringUTFChars(input_path, nullptr);
    const char *output_path_str = env->GetStringUTFChars(output_path, nullptr);

    AVFormatContext *format_context = avformat_alloc_context();
    if (avformat_open_input(&format_context, input_path_str, nullptr, nullptr) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "FFmpegNativeUtils", "Error finding decoder");
        return;
    }

    if (avformat_find_stream_info(format_context, nullptr) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "FFmpegNativeUtils", "Error finding decoder");
        avformat_close_input(&format_context);
        return;
    }

    int stream_index = -1;
    for (int i = 0; i < format_context->nb_streams; ++i) {
        if ((media_type == 0 && format_context->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) ||
            (media_type == 1 && format_context->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO)) {
            stream_index = i;
            break;
        }
    }

    if (stream_index == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "FFmpegNativeUtils", "Error finding decoder");
        avformat_close_input(&format_context);
        return;
    }

    AVCodecParameters *codec_parameters = format_context->streams[stream_index]->codecpar;
    const AVCodec *codec = avcodec_find_decoder(codec_parameters->codec_id);
    AVCodecContext *codec_context = avcodec_alloc_context3(codec);
    avcodec_parameters_to_context(codec_context, codec_parameters);

    if (avcodec_open2(codec_context, codec, nullptr) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "FFmpegNativeUtils", "Error finding decoder");
        avcodec_free_context(&codec_context);
        avformat_close_input(&format_context);
        return;
    }

    FILE *output_file = fopen(output_path_str, "wb");
    if (!output_file) {
        __android_log_print(ANDROID_LOG_ERROR, "FFmpegNativeUtils", "Error finding decoder");
        avcodec_free_context(&codec_context);
        avformat_close_input(&format_context);
        return;
    }

    AVPacket *packet = av_packet_alloc();
    AVFrame *frame = av_frame_alloc();
    while (av_read_frame(format_context, packet) >= 0) {
        if (packet->stream_index == stream_index) {
            avcodec_send_packet(codec_context, packet);
            int receive_result = avcodec_receive_frame(codec_context, frame);
            if (receive_result == 0) {
                fwrite(frame->data[0], 1, frame->linesize[0], output_file);
            }
        }
        av_packet_unref(packet);
    }

    __android_log_print(ANDROID_LOG_ERROR, "FFmpegNativeUtils", "---------------------------- ok ----------------------------");

    fclose(output_file);
    av_frame_free(&frame);
    av_packet_free(&packet);
    avcodec_free_context(&codec_context);
    avformat_close_input(&format_context);

    env->ReleaseStringUTFChars(input_path, input_path_str);
    env->ReleaseStringUTFChars(output_path, output_path_str);
}