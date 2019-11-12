package com.mmvtcstudent.bean;

import java.io.Serializable;

/**
 * Created by W on 2019/7/12.
 */

public  class Islogin implements Serializable {
   private boolean islogin;
    private String current;
    private String sno;
    private String name;
    private String cookie;


    public Islogin(boolean islogin, String current, String sno, String name,String cookie) {
        this.islogin = islogin;
        this.current = current;
        this.sno = sno;
        this.name = name;
        this.cookie = cookie;
    }

    public boolean islogin() {
        return islogin;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
    public String getCookie() {
        return cookie;
    }
}
