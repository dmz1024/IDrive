package com.ccpress.izijia.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Wu Jingyu
 * Date: 2015/4/29
 * Time: 17:17
 */
public class MediaEntity implements Parcelable, Comparable<MediaEntity> {
    private int id;
    private String name;
    private String title;
    private String path;
    private long createtime;
    private String contenttype;
    private boolean isChecked;

    @Override
    public int compareTo(MediaEntity other) {
        if(this.createtime < other.createtime) {
            return 1;
        }
        if(this.createtime > other.createtime) {
            return -1;
        }
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeLong(createtime);
        parcel.writeString(contenttype);
        parcel.writeString(isChecked ? "1" : "0");
    }

    public static final Parcelable.Creator<MediaEntity> CREATOR = new Creator<MediaEntity>() {
        @Override
        public MediaEntity createFromParcel(Parcel parcel) {
            MediaEntity result = new MediaEntity();
            result.id = parcel.readInt();
            result.name = parcel.readString();
            result.title = parcel.readString();
            result.path = parcel.readString();
            result.createtime  = parcel.readLong();
            result.contenttype = parcel.readString();
            if(parcel.readString().equals("1")){
                result.isChecked = true;
            } else {
                result.isChecked = false;
            }
            return result;
        }

        @Override
        public MediaEntity[] newArray(int size) {
            return new MediaEntity[0];
//            return new MediaEntity[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(o.getClass() == this.getClass()){
            MediaEntity other = (MediaEntity) o;
            return this.getId() == other.getId();
        } else {
            return false;
        }
    }
}
