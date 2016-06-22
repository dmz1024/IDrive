package com.ccpress.izijia.entity;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/5
 * Time: 10:37
 */
public class EditGroupListEntity {
    private String name;
    private int num;
    private int gid;
    private int gflag;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getGflag() {
        return gflag;
    }

    public void setGflag(int gflag) {
        this.gflag = gflag;
    }
}
