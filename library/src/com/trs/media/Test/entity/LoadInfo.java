package com.trs.media.Test.entity;

/**
 * Created by wbq on 14-7-29.
 */
public class LoadInfo {
    public int fileSize;//文件大小
    private int complete;//完成度
    private String urlstring;//下载器标识

    public LoadInfo(int fileSize, int complete, String urlstring) {
        this.fileSize = fileSize;
        this.complete = complete;
        this.urlstring = urlstring;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public void setUrlstring(String urlstring) {
        this.urlstring = urlstring;
    }

    public int getFileSize() {

        return fileSize;
    }

    public int getComplete() {
        return complete;
    }

    public String getUrlstring() {
        return urlstring;
    }

    public LoadInfo() {
    }
}
