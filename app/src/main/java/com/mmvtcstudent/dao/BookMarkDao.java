package com.mmvtcstudent.dao;

import com.mmvtcstudent.bean.ItemBean;

import java.util.List;

/**
 * Created by W on 2019/8/15.
 */

public interface BookMarkDao {

    public void insertBookMark(String time, String readvalue, String item_name, String href);
    public int deleteBookMark(String href);
    public List<ItemBean> getAllBookMark();
    public boolean selectisData(String names);

}
