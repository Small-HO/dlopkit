# ifndef BSDIFF_H
# define BSDIFF_H

# include <stddef.h>
# include <stdint.h>

struct bsdiff_stream {
    void *opaque;
    void *(*malloc)(size_t size);
    void (*free)(void *ptr);
    int (*write)(struct bsdiff_stream *stream, const void *buffer, int size);
};

int bsdiff(const uint8_t *old, int64_t oldsize, const uint8_t *new, int64_t newsize,struct bsdiff_stream *stream);
#endif
