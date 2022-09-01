#define loge(...) __android_log_print(ANDROID_LOG_DEBUG,"play" ,__VA_ARGS__);

typedef struct StreamType {
    int video;  //  视频流
    int audio;  //  音频流
} StreamType;
/** 数据流指针 */
StreamType stream_index(AVFormatContext *context) {
    int video_stream_index = -1;
    int audio_stream_index = -1;
    for (int i = 0; i < context->nb_streams; ++i) {
        if (context->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream_index = i;
            break;
        }
        if (context->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audio_stream_index = i;
            break;
        }
    }
    StreamType type = { video_stream_index, audio_stream_index };
    return type;
}

/** 帧率 */
double stream_rate(AVFormatContext *context, int index) {
    //  40000微秒之后解析下一帧(这个是根据视频的帧率来设置的，我这播放的视频帧率是25帧/秒)
    return 1.0 / av_q2d(context->streams[index]->avg_frame_rate) * 1000 * 1000;
}