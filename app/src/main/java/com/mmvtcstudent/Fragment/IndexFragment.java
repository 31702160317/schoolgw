package com.mmvtcstudent.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmvtcstudent.Adapter.OneAdapter;
import com.mmvtcstudent.R;
import com.mmvtcstudent.ShowArticle;
import com.mmvtcstudent.bean.OneBean;
import com.mmvtcstudent.utils.CustomDialog;
import com.mmvtcstudent.utils.GlideImageLoader;
import com.mmvtcstudent.utils.utils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndexFragment extends Fragment implements AdapterView.OnItemClickListener{
    private Banner banner;
    private List<String> urlList = new ArrayList<>();
    private String doMain="http://www.mmvtc.cn/templet/default/index.jsp";
    private CustomDialog customDialog;
    private GridView Grid;
    private OneAdapter oneAdapter;
    private List<OneBean> mData = new ArrayList<>();
    private LinearLayout ll_auto;
    private TextView xygk;
    private RelativeLayout rl_show;
    private View solid;

    public IndexFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_index, container, false);
        customDialog = new CustomDialog(getContext(), "正在加载...");
        customDialog.show();
        xygk= (TextView) v.findViewById(R.id.xygk);
        ll_auto= (LinearLayout) v.findViewById(R.id.ll_auto);
        rl_show= (RelativeLayout) v.findViewById(R.id.show_ll);
        solid=  v.findViewById(R.id.solid);
        banner = (Banner)  v. findViewById(R.id.banner);
        Grid=(GridView)v.findViewById(R.id.Grid);
        new Thread(getGetData).start();
        oneAdapter=new OneAdapter(mData,getContext(),ll_auto);
        Grid.setAdapter(oneAdapter);
        Grid.setOnItemClickListener(this);

        return v;
    }
    // handler更新
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            customDialog.dismiss();
            settingBanner(urlList);//设置轮播图URL
            xygk.setText(" "+"学院概况");
            rl_show.setBackgroundColor(Color.WHITE);
            solid.setVisibility(View.VISIBLE);
            //rl_show.setVisibility(View.VISIBLE);
            oneAdapter.notifyDataSetChanged();

        }
    };

    Runnable getGetData= new Runnable() {
        @Override
        public void run() {
            getImgList();//得到banner图list
            getGridData();//得到学院概况数据
            handler.sendEmptyMessage(0);
        }
    };
    private void getImgList(){
       /* OkHttpUtils
                .get()
                .url(doMain)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                           *//* getcontex().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(getContext(),"错误");
                                }
                            });*//*
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getUrl(response);

                    }
                });*/
        Connection conn = Jsoup.connect(doMain)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                .timeout(6000)
                .followRedirects(true)
                .method(Connection.Method.GET);
        Connection.Response response = null;
        try {
            response = conn.execute();
            String content = response.body();
            getUrl(content);

        } catch (IOException e) {
            //getImgList();
            e.printStackTrace();
        }
    }
    private void getGridData(){
        Connection conn = Jsoup.connect("http://www.mmvtc.cn/templet/default/aboutme.html")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                .timeout(6000)
                .followRedirects(true)
                .method(Connection.Method.GET);
        Connection.Response response = null;
        try {
            response = conn.execute();
            String content = response.body();
            getItemData(content);
            //handler.sendEmptyMessage(0);
        } catch (IOException e) {
           // getGridData();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //轮播图设置
    private void  settingBanner(List list){
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setIndicatorGravity(BannerConfig.RIGHT) //设置指示器位置（指示器居右）
                .setImageLoader(new GlideImageLoader())//设置图片加载器
                .setImages(list)  //设置图片集合
                // .setBannerAnimation(Transformer.DepthPage)//设置动画效过
                //.setBannerTitles(titles)//设置标题集合（当banner样式有显示title时）
                .isAutoPlay(true)//是否自动滚动
                .setDelayTime(2000)//设置轮播时间
                .start();

    }

    private void getItemData(String content) throws Exception {
        Document body = Jsoup.parse(content);
        Elements li = body.select(".subChannelList.list-unstyled li");
        for (int i = 0; i < li.size(); i++) {
            String mtv="";
            String href="";
            Bitmap bitmap = null;
            if(li.get(i).select("figure a img").attr("src")!=""){
                String url="https://www.mmvtc.cn/templet/default/"+li.get(i).select("figure a img").attr("src");
                bitmap= returnBitmap(url);//图片
            }
            if(li.get(i).select("figure h3").text()!="") mtv=li.get(i).select("figure h3").text();//文本
            if(li.get(i).select("a").attr("href")!="") href=li.get(i).select("a").attr("href");//链接

            mData.add(new OneBean(mtv, href,bitmap));
        }

    }

    //解析得到banner链接
    private void getUrl(String content) {
        Document body = Jsoup.parse(content);
        Elements img=body.select("#owl-demo img");
        String string="http://www.mmvtc.cn/templet/default/";
        //得到图片集合 放进urllist
        for (int i=0;i<img.size();i++){
            urlList.add(string+img.get(i).attr("src")) ;
        }

    }
    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    private static Bitmap returnBitmap(String url) {
       /* OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();*/
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

    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public  Bitmap getBitmapFromURL(String src) {
        try {

            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL
                    .openConnection();
            // 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            // 连接设置获得数据流
            conn.setDoInput(true);
            // 不使用缓存
            conn.setUseCaches(false);
            // 这句可有可无，没有影响
            // conn.connect();
            // 得到数据流
            InputStream is = conn.getInputStream(); // 报错？？？？？？？？？？？？？？？
            // 解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            utils.loge("测试",bitmap+"");
            // 关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //  ToastUtil.showToast(getContext(),mData.get(position).getHref());
        Intent intent = new Intent(getActivity(), ShowArticle.class);
        intent.putExtra("name",mData.get(position).getMtv());
        //带有以下字符的都是跳转到为绝对路径  没有的则是跳到主管网www.mmvtc.com/
        boolean hxgcx=(mData.get(position).getHref().indexOf("hxgcx")>-1);//化学工程系
        boolean tmgcx=(mData.get(position).getHref().indexOf("tmgcx")>-1);//土木工程系
        boolean zzb=(mData.get(position).getHref().indexOf("zzb")>-1);//中专部
        boolean jdxxx=(mData.get(position).getHref().indexOf("jdxxx")>-1);//机电
        boolean jjglx=(mData.get(position).getHref().indexOf("jjglx")>-1);//经济管理系
        boolean gdjy=(mData.get(position).getHref().indexOf("gdjy")>-1);//广东教育
        boolean news=(mData.get(position).getHref().indexOf("news")>-1);//新闻网 茂名视听网
        boolean sub=(mData.get(position).getHref().indexOf("a.jsp")>-1);//院校概况
        if(hxgcx||tmgcx||zzb||jdxxx||jjglx||gdjy||news||sub){
            intent.putExtra("url",mData.get(position).getHref());
        }else{
            intent.putExtra("url","http://www.mmvtc.cn"+mData.get(position).getHref());
        }

        startActivity(intent);
    }
}
