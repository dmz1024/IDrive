package com.ccpress.izijia.yd.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/30.
 */
public class SerializableList<T> implements Serializable {
    private List<T> list;

    public List<T> getMap() {
        return list;
    }

    public void setMap(List<T> list) {
        this.list = list;
    }
}
