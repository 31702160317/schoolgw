package com.mmvtcstudent.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mmvtcstudent.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by W on 2019/8/15.
 */

public class BookMarkImpl implements BookMarkDao {
    private DBHelper mHelpter = null;
    public BookMarkImpl(Context context) {
        mHelpter = new DBHelper(context);
    }
    //String time, String readvalue, String item_name, String href
    @Override
    public void insertBookMark(String time, String readvalue, String item_name, String href) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        db.execSQL("insert into bookmark(time,readvalue,item_name,href) values(?,?,?,?)",
                new String[]{time, readvalue, item_name,href});
        db.close();
    }

    @Override
    public int deleteBookMark(String href) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        int inde = db.delete("bookmark","href = ?",new String[]{String.valueOf(href)});
        return inde;
    }

    @Override
    public List<ItemBean> getAllBookMark() {
       SQLiteDatabase db = mHelpter.getReadableDatabase();
        List<ItemBean> list = new ArrayList<ItemBean>();
         Cursor cursor = db.query("bookmark",null,null,null,null,null,null);
       // utils.loge("测试如何",cursor.getColumnCount()+"");
        while (cursor.moveToNext()){
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String readvalue=cursor.getString(cursor.getColumnIndex("readvalue"));
            String item_name=cursor.getString(cursor.getColumnIndex("item_name"));
            String href=cursor.getString(cursor.getColumnIndex("href"));
            list.add(new ItemBean(time,readvalue,item_name,href));
            Log.e("--Main--", "==============getAllBookMark======"+time+"================"+readvalue+"================"+item_name);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     *
     * 查询db中是否有数据
     * @param href
     * @return
     */
    @Override
    public boolean selectisData(String href) {
        SQLiteDatabase db = mHelpter.getReadableDatabase();
        Cursor cursor = db.query("bookmark",null,"href = ?",new String[]{href},null,null,null);
        while (cursor.moveToNext()){
            return true;
        }
        return false;
    }


}
