package com.ccpress.izijia.yd.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dengmingzhi on 16/5/29.
 *
 */
public class SerializableMap implements Serializable {
    private Map<String,Object[]> map;

    public Map<String, Object[]> getMap() {
        return map;
    }

    public void setMap(Map<String, Object[]> map) {
        this.map = map;
    }
}
