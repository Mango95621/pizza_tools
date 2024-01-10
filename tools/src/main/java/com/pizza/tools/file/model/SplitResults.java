package com.pizza.tools.file.model;

/**
 * @author BoWei
 * 2023/8/28 10:45
 *
 */
public class SplitResults {
    private String path;
    private String name;
    private String suffix;
    private String nameSuffix;

    public SplitResults(String path, String name, String suffix, String nameSuffix) {
        this.path = path;
        this.name = name;
        this.suffix = suffix;
        this.nameSuffix = nameSuffix;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }
}