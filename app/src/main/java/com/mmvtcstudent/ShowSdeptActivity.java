package com.mmvtcstudent;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.mmvtcstudent.Fragment.ListFragments.GzgzFragment;
import com.mmvtcstudent.Fragment.ListFragments.NewsFragment;
import com.mmvtcstudent.Fragment.ListFragments.NoticeFragment;
import com.mmvtcstudent.utils.CustomDialog;
import com.mmvtcstudent.utils.GlideImageLoader;
import com.mmvtcstudent.utils.ToastUtil;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import okhttp3.Call;

public class ShowSdeptActivity extends AppCompatActivity {
    private static String       sdeptUrl="";
   /* private String  sdeptUrl="";*/
   private         CustomDialog customDialog;
    private        List<String> urlList = new ArrayList<>();
    private        TextView     name;
    private        Banner       banner;
    private        String[]     mTabTitles= new String[2];
    private        TabLayout    tabLayout = null;
    private        Fragment[]   mFragmentArrays = new Fragment[2];
    private        ViewPager    viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sdept);

        initView();
        initData();

            new Thread(getData).start();



    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
           // customDialog.dismiss();
            settingBanner(urlList);//设置轮播图URL
           /* xygk.setText(" "+"学院概况");
            rl_show.setBackgroundColor(Color.WHITE);
            solid.setVisibility(View.VISIBLE);
            //rl_show.setVisibility(View.VISIBLE);
            oneAdapter.notifyDataSetChanged();*/

        }
    };
    private void initView() {
        customDialog = new CustomDialog(this, "正在加载...");
        customDialog.show();
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        name= (TextView) findViewById(R.id.name);
        banner= (Banner) findViewById(R.id.banner);
    }

    private void initData() {
        sdeptUrl =getIntent().getStringExtra("sdept");
        String sdeptName=getIntent().getStringExtra("sdeptName");
        name.setText(sdeptName);
        mTabTitles[0] = "系部新闻";
        mTabTitles[1] = "通知公告";

            mFragmentArrays[0] = new SdeptFragment();
            mFragmentArrays[1] = new NoticleFragment();


        PagerAdapter pagerAdapter =new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);//限制重复加载
        viewPager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tabLayout.setupWithViewPager(viewPager);

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
    Runnable getData= new Runnable() {
        @Override
        public void run() {
            getImgList();//得到banner图list
           // getGridData();//得到学院概况数据
           // handler.sendEmptyMessage(0);
        }
    };
    private void getImgList(){
        OkHttpUtils
                .get()
                .url(sdeptUrl)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ShowSdeptActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(ShowSdeptActivity.this,"错误");
                                }
                            });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getUrl(response);

                        ShowSdeptActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                settingBanner(urlList);
                                customDialog.dismiss();
                            }
                        });

                    }
                });
      /*  Connection conn = Jsoup.connect(sdeptUrl)
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
            getImgList();
            e.printStackTrace();
        }*/
    }
    //解析得到banner链接
    private void getUrl(String content) {
        Document body = Jsoup.parse(content);
        String string="http://www.mmvtc.cn";
        if(sdeptUrl.indexOf("jsjgcx")>-1){//计算机工程系
            Elements img=body.select(".orbit img");
            for (int i=0;i<img.size();i++){
                if(img.get(i).attr("src").indexOf(string)>-1){
                    urlList.add(img.get(i).attr("src")) ;
                }else{
                    urlList.add(string+img.get(i).attr("src")) ;
                }
                //urlList.add(string+img.get(i).attr("src")) ;
            }
        }else if(sdeptUrl.indexOf("hxgcx")>-1){//化学工程系
            Elements img=body.select(".orbit img");
            for (int i=0;i<img.size();i++){
                if(img.get(i).attr("src").indexOf(string)>-1){
                    urlList.add(img.get(i).attr("src")) ;
                }else{
                    urlList.add(string+img.get(i).attr("src")) ;
                }
                //utils.loge("测试img",string+img.get(i).attr("src"));

            }
        }else if(sdeptUrl.indexOf("tmgcx")>-1){//化学工程系
            Elements img=body.select(".mainbox_fore img");
            for (int i=0;i<img.size();i++){
                if(img.get(i).attr("src").indexOf(string)>-1){
                    urlList.add(img.get(i).attr("src")) ;
                }else{
                    urlList.add(string+img.get(i).attr("src")) ;
                }
                //utils.loge("测试img",string+img.get(i).attr("src"));

            }
        }


    }

    final class MyViewPagerAdapter extends FragmentPagerAdapter {


        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }

        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    public void back(View v) {
        finish();
    }

    public static String getUrl(){
        return sdeptUrl;
    }
}
