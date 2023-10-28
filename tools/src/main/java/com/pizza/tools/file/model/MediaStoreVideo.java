package com.pizza.tools.file.model;

import android.net.Uri;

/**
 * @author Kyle
 * 2023/8/25 11:24
 * mapping ->
 *  MediaStore.Video.Media._ID,
 *  MediaStore.Video.Media.DISPLAY_NAME,
 *  MediaStore.Video.Media.DURATION,
 *  MediaStore.Video.Media.SIZE
 */
public class MediaStoreVideo {
    private long id;
    private Uri uri;
    private String displayName;
    private Long duration;
    private Long size;

    public MediaStoreVideo(long id, Uri uri, String displayName, Long duration, Long size) {
        this.id = id;
        this.uri = uri;
        this.displayName = displayName;
        this.duration = duration;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}