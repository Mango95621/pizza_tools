package com.pizza.tools.file.model;


import android.net.Uri;

import java.util.Date;

/**
 * @author Kyle
 * 2023/8/25 11:17
 * mapping ->
 *  MediaStore.Image.Media._ID,
 *  MediaStore.Image.Media.DISPLAY_NAME,
 */
public class MediaStoreImage {
    private long id;
    private Uri uri;
    private String displayName;
    private Long size;
    private String description;
    private String title;
    private String mimeType;
    private Date dateAdded;

    public MediaStoreImage(long id, Uri uri, String displayName, Long size, String description, String title, String mimeType, Date dateAdded) {
        this.id = id;
        this.uri = uri;
        this.displayName = displayName;
        this.size = size;
        this.description = description;
        this.title = title;
        this.mimeType = mimeType;
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public Uri getUri() {
        return uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Date getDateAdded() {
        return dateAdded;
    }
}