# include <limits.h>
# include "bspatch.h"
# include <stdio.h>
# include <string.h>
# include <errno.h>
# include <android/log.h>

#define loge(...) __android_log_print(ANDROID_LOG_DEBUG,"play" ,__VA_ARGS__);

static int64_t offtin(uint8_t *buf) {
    int64_t y;
    y = buf[7] & 0x7F;
    y = y * 256;y += buf[6];
    y = y * 256;y += buf[5];
    y = y * 256;y += buf[4];
    y = y * 256;y += buf[3];
    y = y * 256;y += buf[2];
    y = y * 256;y += buf[1];
    y = y * 256;y += buf[0];
    if (buf[7] & 0x80) y = -y;
    return y;
}

int bspatch(const uint8_t *old, int64_t oldsize, uint8_t *new, int64_t newsize, struct bspatch_stream *stream) {
    uint8_t buf[8];
    int64_t oldpos, newpos;
    int64_t ctrl[3];
    int64_t i;

    oldpos = 0;
    newpos = 0;
    while (newpos < newsize) {
        /* Read control data */
        for (i = 0; i <= 2; i++) {
            if (stream->read(stream, buf, 8)) {
                return -1;
            }
            ctrl[i] = offtin(buf);
        }

        /* Sanity-check */
        if (ctrl[0] < 0 || ctrl[0] > INT_MAX || ctrl[1] < 0 || ctrl[1] > INT_MAX || newpos + ctrl[0] > newsize) {
            return -1;
        }

        /* Read diff string */
        if (stream->read(stream, new + newpos, ctrl[0])) {
            return -1;
        }

        /* Add old data to diff string */
        for (i = 0; i < ctrl[0]; i++) {
            if ((oldpos + i >= 0) && (oldpos + i < oldsize)) {
                new[newpos + i] += old[oldpos + i];
            }
        }

        /* Adjust pointers */
        newpos += ctrl[0];
        oldpos += ctrl[0];

        /* Sanity-check */
        if (newpos + ctrl[1] > newsize) {
            return -1;
        }

        /* Read extra string */
        if (stream->read(stream, new + newpos, ctrl[1])) {
            return -1;
        }

        /* Adjust pointers */
        newpos += ctrl[1];
        oldpos += ctrl[2];
    }

    return 0;
}


#include <bzlib.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <err.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>

static int bz2_read(const struct bspatch_stream* stream, void* buffer, int length)
{
    int n;
    int bz2err;
    BZFILE* bz2;

    bz2 = (BZFILE*)stream->opaque;
    n = BZ2_bzRead(&bz2err, bz2, buffer, length);
    if (n != length) {
        return -1;
    }
    return 0;
}

int bspatch_main(int argc,char * argv[])
{
    FILE * f;
    int fd;
    int bz2err;
    uint8_t header[24];
    uint8_t *old, *new;
    int64_t oldsize, newsize;
    BZFILE* bz2;
    struct bspatch_stream stream;
    struct stat sb;

    if(argc!=4) errx(1,"usage: %s oldfile newfile patchfile\n",argv[0]);

    /* Open patch file */
    if ((f = fopen(argv[3], "r")) == NULL) {
        err(1, "fopen(%s)", argv[3]);
    }

    /* Read header */
    if (fread(header, 1, 24, f) != 24) {
        if (feof(f)) {
            errx(1, "Corrupt patch\n");
        }
        err(1, "fread(%s)", argv[3]);
    }

    /* Check for appropriate magic */
    if (memcmp(header, "ENDSLEY/BSDIFF43", 16) != 0) {
        errx(1, "Corrupt patch\n");
    }

    /* Read lengths from header */
    newsize=offtin(header+16);
    if(newsize<0) {
        errx(1,"Corrupt patch\n");
    }

    /* Close patch file and re-open it via libbzip2 at the right places */
    if(((fd=open(argv[1],O_RDONLY,0))<0) ||
        ((oldsize=lseek(fd,0,SEEK_END))==-1) ||
        ((old=malloc(oldsize+1))==NULL) ||
        (lseek(fd,0,SEEK_SET)!=0) ||
        (read(fd,old,oldsize)!=oldsize) ||
        (fstat(fd, &sb)) ||
        (close(fd)==-1)) err(1,"%s",argv[1]);
    if((new=malloc(newsize+1))==NULL) err(1,NULL);

    if (NULL == (bz2 = BZ2_bzReadOpen(&bz2err, f, 0, 0, NULL, 0))) {
        errx(1, "BZ2_bzReadOpen, bz2err=%d", bz2err);
    }

    stream.read = bz2_read;
    stream.opaque = bz2;

    if (bspatch(old, oldsize, new, newsize, &stream)) {
        errx(1, "bspatch");
    }

    /* Clean up the bzip2 reads */
    BZ2_bzReadClose(&bz2err, bz2);
    fclose(f);

    /* Write the new file */
    if(((fd=open(argv[2],O_CREAT|O_TRUNC|O_WRONLY,sb.st_mode))<0) ||
        (write(fd,new,newsize)!=newsize) || (close(fd)==-1))
        err(1,"%s",argv[2]);

    free(new);
    free(old);

    return 0;
}
