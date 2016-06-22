package com.ccpress.izijia.entity;

import java.util.Comparator;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/11
 * Time: 13:54
 */
public class CityEntity implements Comparable<CityEntity> {
    private String code;
    private String name;
    private String province;
    private boolean isSelected;
    private String sort_key;
    private String sort_key_full;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getSort_key() {
        return sort_key;
    }

    public void setSort_key(String sort_key) {
        this.sort_key = sort_key;
    }

    public String getSort_key_full() {
        return sort_key_full;
    }

    public void setSort_key_full(String sort_key_full) {
        this.sort_key_full = sort_key_full;
    }

    @Override
    public int compareTo(CityEntity other) {
        if (this.getSort_key().equals("@")
                || other.getSort_key().equals("#")) {
            return -1;
        } else if (this.getSort_key().equals("#")
                || other.getSort_key().equals("@")) {
            return 1;
        } else {
            return this.getSort_key().compareTo(other.getSort_key());
        }
    }
}
