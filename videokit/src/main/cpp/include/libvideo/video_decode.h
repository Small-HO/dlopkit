#include "video_utils.h"
#include "../libavformat/avformat.h"
#include "../libavcodec/avcodec.h"
#include "../libavutil/avutil.h"


/**
 * 获取流指针
 * @return 流指针
 */
StreamType stream_code(AVFormatContext *context, const char *url) {
    //  打开文件 (成功：0 失败：负 )
    if (avformat_open_input(&context, url, nullptr, nullptr) != 0) {
        exit(1);
    }
    //  获取流信息 (成功:>=0 失败：负)
    if (avformat_find_stream_info(context, nullptr) < 0) {
        exit(1);
    }
    return stream_index(context);
 }


/**
 * 解码器
 * @ruten 解码器上下文
 * */
AVCodecContext * decoder_context(AVFormatContext *context, int index) {
    //  1.获取解码器
    const AVCodec *avCodec = avcodec_find_decoder(context->streams[index]->codecpar->codec_id);
    //  2.创建解码器上下文
    AVCodecContext *av_code_context = avcodec_alloc_context3(avCodec);
    //  3.赋值参数至上下文
    avcodec_parameters_to_context(av_code_context, context->streams[index]->codecpar);
    //  4.打开解码器
    if (avcodec_open2(av_code_context, avCodec, nullptr) != 0) {
        exit(1);
    }
    return av_code_context;
}


/**
 * 设置缓冲区
 * @ return 上下文对象
 */
SwsContext * decoder_sws(AVCodecContext *context, ANativeWindow *window) {
    //  数据转换为RGB
    SwsContext *sws_context = sws_getContext(context->width, context->height,context->pix_fmt, context->width, context->height, AV_PIX_FMT_RGBA, SWS_BILINEAR,nullptr, nullptr, nullptr);
    //  设置绘制缓冲区
    ANativeWindow_setBuffersGeometry(window, context->width, context->height,WINDOW_FORMAT_RGBA_8888);
    return sws_context;
}


/**
 * 循环解析
 *
 */
void decoder_start(AVFormatContext *context, AVCodecContext *av_context, SwsContext *sws_context, ANativeWindow *window, int index) {
    //  设置缓冲区
    ANativeWindow_Buffer a_native_window_buffer;
    uint8_t *dst_data[4];
    int dst_line_size[4];
    av_image_alloc(dst_data, dst_line_size, av_context->width, av_context->height,AV_PIX_FMT_RGBA, 1);
    //  逐帧解析
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
        sws_scale(sws_context, av_frame->data, av_frame->linesize, 0, av_frame->height, dst_data, dst_line_size);
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
        //  延迟解析
        usleep(unsigned (stream_rate(context, index)));
        //  释放资源
        av_frame_free(&av_frame);
        av_packet_clone(av_packet);
    }
}