package com.pizza.tools.file.model;

/**
 * @author Kyle
 * 2023/8/28 10:46
 *
 */
public class MediaInfoBean {
    private long dateAdded;
    private long dateModified;
    private long dateExpires;

    private long dateTaken;

    public MediaInfoBean(long dateAdded, long dateModified, long dateExpires) {
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.dateExpires = dateExpires;
    }

    public MediaInfoBean(long dateAdded, long dateModified, long dateExpires, long dateTaken) {
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.dateExpires = dateExpires;
        this.dateTaken = dateTaken;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(long dateExpires) {
        this.dateExpires = dateExpires;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }
}
