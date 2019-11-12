package com.mmvtcstudent;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mmvtcstudent.utils.CustomDialog;
import com.mmvtcstudent.utils.ToastUtil;
import com.mmvtcstudent.utils.utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class ShowArticle extends AppCompatActivity {
    private TextView content;
    private TextView headTitle,title,test;
    private TextView introduce;
    private String url,titleStr,introduceStr,contentStr,base64Url="";
    private List<String> imgList = new ArrayList<>();
    String videoUrl="";
    private ScrollView scrollView;
    private LinearLayout ll_push_child;
    private ImageView img_add;
    private CustomDialog customDialog;
    private WebView videos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);
        initView();

        //加载视频
        videos= (WebView) findViewById(R.id.videos);
        WebSettings webSettings= videos.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8") ;//这句话去掉也没事。。只是设置了编码格式
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);//必须保留。。否则无法播放优酷视频网页。。其他的可以
        videos.setWebChromeClient(new WebChromeClient());//重写一下。有的时候可能会出现问题
        videos.setWebViewClient(new WebViewClient(){//不写的话自动跳到默认浏览器了。。跳出APP了。。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//这个方法必须重写。否则会出现优酷视频周末无法播放。周一-周五可以播放的问题
                if(url.startsWith("intent")||url.startsWith("youku")){
                    return true;
                }else{
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });


        Intent intent = getIntent();
        headTitle.setText(intent.getStringExtra("name"));
        url = intent.getStringExtra("url");
       // utils.loge("这是什么",getac);

        customDialog = new CustomDialog(this, "正在加载...");
        customDialog.show();
        //customDialog.show();
        new Thread(getData).start();

    }

    private void initView() {

        ///scrollView.findViewById(R.id.sv_layout);
       // test= (TextView) findViewById(R.id.test);
        content = (TextView) findViewById(R.id.cotent);
        headTitle = (TextView) findViewById(R.id.name);
        introduce= (TextView) findViewById(R.id.introduce);
        title= (TextView) findViewById(R.id.titles);
        ll_push_child= (LinearLayout) findViewById(R.id.ll_push);
        img_add= (ImageView) findViewById(R.id.img_add);
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {



                    title.setText(titleStr);
                    introduce.setText(introduceStr);

                    //内容
                    if(!TextUtils.isEmpty(contentStr)){
                        content.setText(contentStr.replace("null",""));
                        content.setVisibility(View.VISIBLE);
                    }

                    //如果有图片则动态加载imageview
                    if(!imgList.isEmpty()){
                        for (int i = 0; i < imgList.size(); i++) {
                            String urlImg =  imgList.get(i);
                            ImageView newImg = new ImageView(ShowArticle.this);
                            //设置想要的图片，相当于android:src="@drawable/image"
                            newImg.setLeft(20);
                            ll_push_child.addView(newImg);
                            Glide.with(ShowArticle.this)
                                    .load(urlImg)
                                    .into(newImg);
                        }

                        // img_add.setVisibility(View.VISIBLE);
                        //  ImageView img=new ImageView(getApplicationContext());

                    }

                    if(!TextUtils.isEmpty(videoUrl)){
                        videos.setVisibility(View.VISIBLE);
                        videos.loadUrl(videoUrl);
                    }
                    // content.setVisibility(View.GONE);
                    //图片是base64src动态加载
                    //加载取消

            customDialog.dismiss();

           /*     }
            },500);*/

        }
    };
    Runnable getData = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new StringCallback()
                    {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                           ShowArticle.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    title.setText("无法显示");
                                    introduce.setText("无法显示");
                                    content.setText("无法显示");
                                    customDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            getContent(response);
                           /* ShowArticle.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indexBaseAdapter.notifyDataSetChanged();
                                }
                            });*/
                        }


                    });
          /*  Connection conn = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                    .timeout(6000)
                    .followRedirects(true)
                    .method(Connection.Method.GET);
            Connection.Response response = null;
            try {
                response = conn.execute();
                String content = response.body();


                getContent(content);

            } catch (IOException e) {
                new Thread(getData).start();
                e.printStackTrace();
            }*/
        }
    };

    private void getContent(String content) {
        Document body = Jsoup.parse(content);

        contentStr="";
        //学术信息
            if(headTitle.getText().equals("学术信息")){//学术信息所有
                Elements container = body.select(".new .content");
                titleStr = container.select("h3").text();//标题
                introduceStr=container.select("h4").text();
                //内容区
                contentStr="\t\t\t\t"+container.select(".news_content font").text();
                Elements p=container.select(".news_content p");//内容
                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";
                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }
                contentStr+="\t\t\t\t"+container.select(".news_content a").text();

            }else if(url.indexOf("a.jsp")>-1){//院校概况
                Elements container = body.select(".col-md-10.bg-f-01");
                titleStr = container.select(".ali-ol-experiment-title.mt-20").text();//标题
                introduceStr=container.select(".ali-ol-experiment-data.mb-15").text();

                Elements p=container.select(".ali-ol-experiment-content p");//内容
                // utils.loge("这是p",p.toString());

                //imgtils.loge("Cesh",getIntent().getStringExtra("name").equals("校园风光")+"jfd");
                    if(getIntent().getStringExtra("name").equals("校园风光")){//判断学院风光
                        Elements  img=p.get(0).select("img");
                        for (int i=0;i<img.size();i++){
                            imgList.add(img.get(i).attr("src"));
                        }
                    }else{//(p.get(i).select("img")!=null)

                       // contentStr="\t\t\t\t"+container.select(".ali-ol-experiment-content").text();
                        for(int i=0;i<p.size();i++){
                            contentStr +="\t\t\t\t"+p.get(i).text()+"\n";
                            imgList.add(p.get(i).select("img").attr("src"));
                        }
                        //历史沿革
                        utils.loge("测试啊",p.size()+"");
                        if(p.size()==0){
                            contentStr="\t\t\t\t"+container.select(".ali-ol-experiment-content").text();
                        }
                    }

            }else if(url.indexOf("hxgcx")>-1){   //化学工程系
                Elements container = body.select(".frame");
                titleStr = container.select("h1").text();//标题
                introduceStr=container.select("h4").text();
                contentStr="\t\t\t\t"+container.select(".news_content font").text();
                Elements p=container.select(".content p");//内容
                // utils.loge("这是p",p.toString());
                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入图片
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }

            }else if(url.indexOf("zzb")>-1){//中专部
                Elements container = body.select("#article_container");
                titleStr = container.select("#tt").text();//标题
                introduceStr=container.select("h4").text();
                Elements p=container.select(".list_nr p");//内容
                // utils.loge("这是p",p.toString());
                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }

            } else if(url.indexOf("tmgcx")>-1){//土木工程系
                Elements container = body.select(".list-text");
                titleStr = container.select("#title").text();//标题
                introduceStr=container.select("#laiyuan").text();
                Elements p=container.select("#xiangxi p");//内容

                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }

            } else if(url.indexOf("jdxxx")>-1){//机电系
                Elements container = body.select(".larticleMain");

                titleStr = container.select(".articleTitle").text();//标题
                introduceStr=container.select(".articleInfo").text();
                Elements p=container.select(".articleContent p");//内容

                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }
            }else if(url.indexOf("jjglx")>-1){//经济管理系
                Elements container = body.select("#content_wenzhang");
                titleStr = container.select("h3").text();//标题
                introduceStr=container.select("#date").text();
                Elements p=container.select(".news_content p");//内容

                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }
            }else if(url.indexOf("gdjy")>-1){//广东教育 在学院新闻
                Elements container = body.select(".center");
               /* utils.loge("测试广东教育",container.toString());*/
                titleStr = container.select("#zoomtitl").text();//标题
                introduceStr=container.select("#zoomtime").text();
                Elements p=container.select("#zoomcon p");//内容

                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));
                    }
                }

            }else if(url.indexOf("news")>-1){//茂名视听网
                Elements container = body.select("body");
               /* utils.loge("测试广东教育",container.toString());*/
                titleStr = container.select(".newtitle").text();//标题
                introduceStr=container.select(".newname").text();

                    contentStr +="\t\t\t\t"+"无法显示";



            } else if(url.indexOf("jsjgcx")>-1){//计算机工程系
                Elements container = body.select(".container");

               /* utils.loge("测试广东教育",container.toString());*/
                titleStr = container.select(".newsTitle").text();//标题
                Elements p=container.select(".newsContent p");//内容
                introduceStr=p.get(0).text();
                for(int i=0;i<p.size();i++){
                    if(i==0){
                        continue;
                    }
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";
                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        String imgURLS="";
                        imgURLS=p.get(i).select("img").attr("src");
                        if(imgURLS.indexOf("http://www.mmvtc.cn")>-1){
                            imgList.add(p.get(i).select("img").attr("src"));
                        }else{
                            imgList.add("http://www.mmvtc.cn"+p.get(i).select("img").attr("src"));
                        }
                       // utils.loge("这是哪",p.get(i).select("img").attr("src"));

                    }
                }

            }else{

                Elements container = body.select(".comm-docs-contern.bg-g-04 .container");
                //testStr=container.toString();
                titleStr = container.select(".ali-ol-experiment-title").text();//标题
                introduceStr=container.select(".ali-ol-experiment-data").text();//介绍
                Elements p=container.select(".ali-ol-experiment-content p");//介绍

                //不是base64的图片
                for(int i=0;i<p.size();i++){
                    contentStr +="\t\t\t\t"+p.get(i).text()+"\n";

                    if((p.get(i).select("img")!=null)){//如果p标签有连接则放入
                        imgList.add(p.get(i).select("img").attr("src"));

                    }

                }
                //base64图片
                if(container.select(".ali-ol-experiment-content img").attr("src").indexOf("data:image")!=-1){
                    base64Url=container.select(".ali-ol-experiment-content img").attr("src")  ;
                }

                //utils.loge("dianjie",base64Url.length()+"");
                //找到iframe中的视频链接
                videoUrl= container.select(".ali-ol-experiment-content iframe").attr("src");
            }


        handler.sendEmptyMessage(0);

    }

    public void back(View v) {

        finish();
    }
    @Override
    protected void onPause () {
        super.onPause ();
        videos.pauseTimers ();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //一定要销毁，否则无法停止播放
        videos.destroy();
    }

}
