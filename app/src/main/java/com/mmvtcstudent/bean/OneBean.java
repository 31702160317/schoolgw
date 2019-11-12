package com.mmvtcstudent.bean;

import android.graphics.Bitmap;

/**
 * Created by W on 2019/7/12.
 */

public  class OneBean {
    private String mtv;
    private String href;
    private Bitmap bit;


    public OneBean(String mtv,String href,Bitmap bit) {
        this.mtv = mtv;
        this.href = href;
        this.bit = bit;

    }
    //学院新闻和通知公告相对路径   学术系部高专绝对路径
    public String getMtv() {
        return mtv;
    }

    public void setMtv(String mtv) {
        this.mtv = mtv;
    }
    public String getHref() {
        return href;
    }

    public void setHref(String mtv) {
        this.href = href;
    }

    public Bitmap getBit() {
        return bit;
    }
}
