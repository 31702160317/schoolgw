package com.mmvtcstudent.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by W on 2019/8/11.
 */

public class utils {
    /**
     *打印所有
     * @param tag
     * @param msg
     */
    public static   void loge  (String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize ) {// 长度小于等于限制直接打印
            Log.e(tag, msg);
        }else {
            while (msg.length() > segmentSize ) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize );
                msg = msg.replace(logContent, "");
                Log.e(tag,"-------------------"+ logContent);
            }
            Log.e(tag,"-------------------"+ msg);// 打印剩余日志
        }
    }


    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    private static Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }


    public static void setGridViewMatchParent(GridView gridView ) {
        // 获取gridView的adapter
            ListAdapter adapter = gridView.getAdapter();
        if (adapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 2;// gridView.getNumColumns();
        int totalHeight = 0;
        // i每次加2，相当于adapter.getCount()小于等于2时 循环一次，计算一次item的高度， adapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < adapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = adapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取gridView的布局参数
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        gridView.setLayoutParams(params);
    }

}
