package com.pizza.tools.file.model;

/**
 * @author BoWei
 * 2023/8/28 10:49
 *
 */
public class DocumentInfoBean {
    private String displayName;
    private String size;

    public DocumentInfoBean(String displayName, String size) {
        this.displayName = displayName;
        this.size = size;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSize() {
        return size == null ? "" : size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
