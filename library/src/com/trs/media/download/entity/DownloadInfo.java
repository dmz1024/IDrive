package com.trs.media.download.entity;

/**
 * Created by wbq on 14-7-29.
 */
public class DownloadInfo {
    private int threadId;   //下载器id
    private int startPos;   //开始点
    private int endPos;     //结束点
    private int compeleteSize;//完成度
    private String url;//下载器网络标识

    public DownloadInfo(int threadId, int startPos, int endPos,
                        int compeleteSize, String url) {
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.compeleteSize = compeleteSize;
        this.url=url;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public void setCompeleteSize(int compeleteSize) {
        this.compeleteSize = compeleteSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getThreadId() {

        return threadId;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public int getCompeleteSize() {
        return compeleteSize;
    }

    public String getUrl() {
        return url;
    }
}
