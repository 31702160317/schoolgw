package com.mmvtcstudent.bean;

/**
 * Created by W on 2019/7/12.
 */

public  class ItemBean {
    private String time;
    private String readvalue;
    private String item_name;
    private String href;
    private String domain="http://www.mmvtc.cn";


    public ItemBean(String time, String readvalue, String item_name, String href) {
        this.time = time;
        this.readvalue = readvalue;
        this.item_name = item_name;
        this.href = href;

    }
    //学院新闻和通知公告相对路径   学术系部高专绝对路径
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReadvalue() {
        return readvalue;
    }

    public void setReadvalue(String readvalue) {
        this.readvalue = readvalue;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
}
