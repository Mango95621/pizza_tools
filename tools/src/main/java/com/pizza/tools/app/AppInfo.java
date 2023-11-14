package com.pizza.tools.app;

import android.graphics.drawable.Drawable;

/**
 * Description:
 *
 * @author Kyle
 * 2020/8/27 6:38 PM
 */
public class AppInfo {
    private String name;
    private Drawable icon;
    private String packageName;
    private String packagePath;
    private String versionName;
    private int versionCode;
    /**
     * 本地apk大小
     */
    private long size = 0L;
    private boolean isSystem;

    public String getName() {
        return name == null ? "" : name;
    }

    public AppInfo setName(String name) {
        this.name = name;
        return this;
    }

    public Drawable getIcon() {
        return icon;
    }

    public AppInfo setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public String getPackageName() {
        return packageName == null ? "" : packageName;
    }

    public AppInfo setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getPackagePath() {
        return packagePath == null ? "" : packagePath;
    }

    public AppInfo setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public String getVersionName() {
        return versionName == null ? "" : versionName;
    }

    public AppInfo setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public AppInfo setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public long getSize() {
        return size;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public AppInfo setSystem(boolean system) {
        isSystem = system;
        return this;
    }

    public AppInfo setSize(long size) {
        this.size = size;
        return this;
    }
}
