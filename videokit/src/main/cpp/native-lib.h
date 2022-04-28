#include <android/log.h>
#include <zconf.h>
#include <android/native_window_jni.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

#define loge(...) __android_log_print(ANDROID_LOG_DEBUG,"play" ,__VA_ARGS__);

void loopDecoding(ANativeWindow *pWindow, AVFormatContext *pContext, AVCodecContext *pContext1);


/** 1. 解封装 */
void delidding(const char *audio_path, AVFormatContext *context) {
    //  打开视频文件
    int ceshi = avformat_open_input(&context, audio_path, nullptr, nullptr);
    if (avformat_open_input(&context, audio_path, nullptr, nullptr) != 0) {
        return;
    }
    //  获取视频信息
    if (avformat_find_stream_info(context, nullptr) != 0) {
        return;
    }
}
/** 2. 音视频流索引 */
int indexes(AVFormatContext *context) {
    int video_stream_index = -1;
    for (int i = 0; i < context->nb_streams; ++i) {
        if (context->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream_index = i;
            break;
        }
    }
    return video_stream_index;
}
/** 3. 初始化解码器 */
void decoder(ANativeWindow *window, int index, AVFormatContext *context) {
    AVCodecParameters *codec_context = context->streams[index]->codecpar;
    //  获取解码器
    const AVCodec *avCodec = avcodec_find_decoder(codec_context->codec_id);
    //  创建解码器上下文
    AVCodecContext *av_code_context = avcodec_alloc_context3(avCodec);
    //  赋值参数至上下文
    avcodec_parameters_to_context(av_code_context, codec_context);
    //  打开解码器
    if (avcodec_open2(av_code_context, avCodec, nullptr) != 0) {
        return;
    }
    //  解码
    loopDecoding(window, context, av_code_context);
}

/** 4. 解码 */
void loopDecoding(ANativeWindow *window, AVFormatContext *context, AVCodecContext *codecContext) {
    //  数据转换为RGB
    SwsContext *sws_context = sws_getContext(codecContext->width, codecContext->height,codecContext->pix_fmt, codecContext->width,codecContext->height, AV_PIX_FMT_RGBA, SWS_BILINEAR,nullptr, nullptr, nullptr);
    //  设置绘制缓冲区
    ANativeWindow_setBuffersGeometry(window, codecContext->width, codecContext->height,WINDOW_FORMAT_RGBA_8888);

    //  缓冲区
    ANativeWindow_Buffer a_native_window_buffer;
    // 计算出转换为RGB所需要的容器的大小
    uint8_t *dst_data[4];
    int dst_line_size[4];
    av_image_alloc(dst_data, dst_line_size, codecContext->width, codecContext->height,AV_PIX_FMT_RGBA, 1);

    AVPacket *av_packet = av_packet_alloc();
    while (av_read_frame(context, av_packet) >= 0) {
        //  发送数据包到解析队列
        avcodec_send_packet(codecContext, av_packet);
        AVFrame *av_frame = av_frame_alloc();
        //  接收
        int av_receive_result = avcodec_receive_frame(codecContext, av_frame);
        if (av_receive_result == AVERROR(EAGAIN)) {
            continue;
        } else if (av_receive_result < 0) {
            break;
        }
        //  数据放到RGB目标容器
        sws_scale(sws_context, av_frame->data, av_frame->linesize, 0, av_frame->height, dst_data,dst_line_size);
        //  加锁然后进行渲染
        ANativeWindow_lock(window, &a_native_window_buffer, nullptr);

        auto *first_window = static_cast<uint8_t *>(a_native_window_buffer.bits);
        uint8_t *src_data = dst_data[0];

        int dst_stride = a_native_window_buffer.stride * 4;
        int src_line_size = dst_line_size[0];
        //  遍历缓冲区数据
        for (int i = 0; i < a_native_window_buffer.height; i++) {
            //  内存拷贝进行渲染
            memcpy(first_window + i * dst_stride, src_data + i * src_line_size, dst_stride);
        }

        //  绘制完解锁
        ANativeWindow_unlockAndPost(window);
        //  40000微秒之后解析下一帧(这个是根据视频的帧率来设置的，我这播放的视频帧率是25帧/秒)
        usleep(1000 * 40);
        //  释放资源
        av_frame_free(&av_frame);
        av_packet_clone(av_packet);
    }
}



































///** 初始化 */
//void initFFmpeg(const char *audio_path, ANativeWindow *window) {
//    //  1.封装格式上下文
//    AVFormatContext *av_context = avformat_alloc_context();
//    //  2.打开视频文件
//    if (avformat_open_input(&av_context, audio_path, nullptr, nullptr) != 0) {
//        return;
//    }
//    //  3.获取视频信息
//    if (avformat_find_stream_info(av_context, nullptr) != 0) {
//        return;
//    }
//    //  找到音频/视频流
//    int video_stream_index = -1;
//    for (int i = 0; i < av_context->nb_streams; ++i) {
//        if (av_context->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
//            video_stream_index = i;
//            break;
//        }
//    }
//
//    //  4.获取视频解码器
//    AVCodecParameters *codec_context = av_context->streams[video_stream_index]->codecpar;
//    const AVCodec *avCodec = avcodec_find_decoder(codec_context->codec_id);
//    AVCodecContext *av_code_context = avcodec_alloc_context3(avCodec);
//    avcodec_parameters_to_context(av_code_context, codec_context);
//
//    //  5.打开解码器
//    if (avcodec_open2(av_code_context, avCodec, nullptr) != 0) {
//        return;
//    }
//    /** ------------------------- 解码流程 ------------------------- */
//    AVPacket *av_packet = av_packet_alloc();
//    //  数据转换为RGB
//    SwsContext *sws_context = sws_getContext(av_code_context->width, av_code_context->height,av_code_context->pix_fmt, av_code_context->width,av_code_context->height, AV_PIX_FMT_RGBA, SWS_BILINEAR,nullptr, nullptr, nullptr);
//    //  设置绘制缓冲区
//    ANativeWindow_setBuffersGeometry(window, av_code_context->width, av_code_context->height,WINDOW_FORMAT_RGBA_8888);
//    //  缓冲区
//    ANativeWindow_Buffer a_native_window_buffer;
//
//    // 计算出转换为RGB所需要的容器的大小
//    uint8_t *dst_data[4];
//    int dst_line_size[4];
//    av_image_alloc(dst_data, dst_line_size, av_code_context->width, av_code_context->height,AV_PIX_FMT_RGBA, 1);
//
//    //  6.一帧一帧读取
//    while (av_read_frame(av_context, av_packet) >= 0) {
//        avcodec_send_packet(av_code_context, av_packet);
//        AVFrame *av_frame = av_frame_alloc();
//        int av_receive_result = avcodec_receive_frame(av_code_context, av_frame);
//        if (av_receive_result == AVERROR(EAGAIN)) {
//            continue;
//        } else if (av_receive_result < 0) {
//            break;
//        }
//        //  数据放到RGB目标容器
//        sws_scale(sws_context, av_frame->data, av_frame->linesize, 0, av_frame->height, dst_data,dst_line_size);
//        //  加锁然后进行渲染
//        ANativeWindow_lock(window, &a_native_window_buffer, nullptr);
//
//        auto *first_window = static_cast<uint8_t *>(a_native_window_buffer.bits);
//        uint8_t *src_data = dst_data[0];
//
//        int dst_stride = a_native_window_buffer.stride * 4;
//        int src_line_size = dst_line_size[0];
//        //  遍历缓冲区数据
//        for (int i = 0; i < a_native_window_buffer.height; i++) {
//            //  内存拷贝进行渲染
//            memcpy(first_window + i * dst_stride, src_data + i * src_line_size, dst_stride);
//        }
//
//        //  绘制完解锁
//        ANativeWindow_unlockAndPost(window);
//
//        //  40000微秒之后解析下一帧(这个是根据视频的帧率来设置的，我这播放的视频帧率是25帧/秒)
//        usleep(1000 * 40);
//
//        //  释放资源
//        av_frame_free(&av_frame);
//        av_packet_clone(av_packet);
//    }
//}