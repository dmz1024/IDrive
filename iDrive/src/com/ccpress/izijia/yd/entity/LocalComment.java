package com.ccpress.izijia.yd.entity;

import java.util.List;

/**
 * Created by dengmingzhi on 16/5/23.
 */
public class LocalComment {
    public Tongji tongji;
    public int result;
    public List<Data> data;
    public class Data {
        public String comment_id;
        public String content;
        public String user_name;
        public String thumb;
        public int comment_rank;
        public String huanjing;
        public String shangjia;
        public String add_time;
        public String img1;
        public String img2;
        public String img3;
        public String img4;
        public String img5;
        public String recontent;
        public String replay;
    }

    public class Tongji {
        public int number;
    }
}
