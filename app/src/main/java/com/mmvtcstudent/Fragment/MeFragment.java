package com.mmvtcstudent.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmvtcstudent.Fragment.MeActivitys.BookMark;
import com.mmvtcstudent.Fragment.MeActivitys.Info;
import com.mmvtcstudent.Fragment.MeActivitys.score;
import com.mmvtcstudent.LoginActivity;
import com.mmvtcstudent.R;
import com.mmvtcstudent.bean.Islogin;
import com.mmvtcstudent.utils.ToastUtil;
import com.mmvtcstudent.utils.utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout re_shoucang,re_about,re_exit,re_myinfo,re_info,re_score,re_course;
    private TextView tv_sno,tv_name,tv_login;
    private ImageView iv_img;
    String Cookie="";
    String infoUrl,scoreUrl,courseUrl;
    String stuUser,studentName;
    private String imgUrl="";
    private boolean isLogin=false;
    private  byte[] byte_image;
    private Bitmap bitmap;
    Islogin item;
    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_me, container, false);
        tv_login= (TextView) view.findViewById(R.id.tv_login);
        tv_sno= (TextView) view.findViewById(R.id.tv_sno);
        tv_name= (TextView) view.findViewById(R.id.tv_name);
        iv_img= (ImageView) view.findViewById(R.id.iv_img);
        re_shoucang= (RelativeLayout) view.findViewById(R.id.re_shoucang);
        re_info= (RelativeLayout) view.findViewById(R.id.re_info);
        re_score= (RelativeLayout) view.findViewById(R.id.re_score);
        re_course= (RelativeLayout) view.findViewById(R.id.re_course);

        re_about= (RelativeLayout) view.findViewById(R.id.re_about);
        re_exit= (RelativeLayout) view.findViewById(R.id.re_exit);
        re_myinfo= (RelativeLayout) view.findViewById(R.id.re_myinfo);

        re_shoucang.setOnClickListener(this);
        re_about.setOnClickListener(this);
        re_exit.setOnClickListener(this);
        re_myinfo.setOnClickListener(this);
        re_info.setOnClickListener(this);
        re_score.setOnClickListener(this);
        re_course.setOnClickListener(this);

        initData();
        // Inflate the layout for this fragment
        return view;
    }

    private void initData() {


        SharedPreferences sp = getContext().getSharedPreferences("user", MODE_PRIVATE);
        stuUser = sp.getString("user", "");
        studentName = sp.getString("studentName", "");
        Cookie = sp.getString("Cookie", null);
        boolean islogin=sp.getBoolean("isLogin",false);

        if(islogin){
            String sno=sp.getString("user", "");
            String studentName=sp.getString("studentName", "");
            String name=  studentName.replace("同学","");
           /* utils.loge("姓名",sno);
            utils.loge("姓名",name);*/
            new Thread(loginRun).start();
            infoUrl ="http://jwc.mmvtc.cn/xsgrxx.aspx?xh="+sno+"&xm="+ URLEncoder.encode(name)+"&gnmkdm=N121501";
            scoreUrl ="http://jwc.mmvtc.cn/xscjcx.aspx?xh="+sno+"&xm="+ URLEncoder.encode(name)+"&gnmkdm=N121065";
            courseUrl="http://jwc.mmvtc.cn/tjkbcx.aspx?xh="+sno+"&xm="+ URLEncoder.encode(name)+"&gnmkdm=N121063";
            utils.loge("姓名",infoUrl);
            //new Thread(getImg).start();//得到头像
            isLogin=true;
        }
        if(isLogin){
            tv_login.setText("退出登录");
        }else {
            tv_login.setText("登录");
        }


    }
    //线程 登录网络请求
    Runnable loginRun = new Runnable() {
        @Override
        public void run() {
            login();
        }
    };
    //登录请求
    private void login() {
        final String loginHttp="http://jwc.mmvtc.cn/default4.aspx";
        SharedPreferences sp = getContext().getSharedPreferences("user", MODE_PRIVATE);
        final String xh = sp.getString("user", "");
        final String stuPassword = sp.getString("password", null);
        final String studentName=sp.getString("studentName", "");
        OkHttpUtils
                .get()
                .url(loginHttp)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document html = Jsoup.parse(response);
                        Elements e = html.select("input[name=__VIEWSTATE]");
                        final String   viewstate = e.get(0).attr("value");

                        OkHttpUtils
                                .post()
                                .url(loginHttp)
                                .addParams("TextBox1", xh)
                                .addParams("TextBox2", stuPassword)

                                .addParams("RadioButtonList1", "学生")
                                .addParams("__VIEWSTATE", viewstate)
                                .addParams("Button1", "")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        new Thread(getImg).start();//得到头像
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_sno.setText(xh);
                                                tv_name.setText(studentName);
                                            }
                                        });
                                    }
                                });
                    }

                });



    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.re_myinfo:
                if(!isLogin){
                    Intent intent0 = new Intent(getContext(), LoginActivity.class);
                    // intent1.putExtra("isLogin",isLogin);
                    startActivityForResult(intent0,1);
                }

                break;
            case R.id.re_shoucang:
                Intent intent2 = new Intent(getContext(), BookMark.class);
                startActivity(intent2);
                break;
            case R.id.re_info:
                if(isLogin){
                    Intent intent3 = new Intent(getContext(),
                            Info.class);
                    intent3.putExtra("infoUrl", infoUrl);
                    startActivity(intent3);
                }else {
                    ToastUtil.showToast(getContext(),"请先登录！");
                    Intent intent1 = new Intent(getContext(), LoginActivity.class);
                    // intent1.putExtra("isLogin",isLogin);
                    startActivityForResult(intent1,1);
                }


                break;
            case R.id.re_score:
                if(isLogin){
                    Intent intent4 = new Intent(getContext(),
                            score.class);
                    intent4.putExtra("scoreUrl", scoreUrl);
                    startActivity(intent4);
                }else {
                    ToastUtil.showToast(getContext(),"请先登录！");
                    Intent intent1 = new Intent(getContext(), LoginActivity.class);
                    // intent1.putExtra("isLogin",isLogin);
                    startActivityForResult(intent1,1);
                }

                break;

            case R.id.re_about:
                ToastUtil.showToast(getContext(),"茂名职业技术学院教务处官网,开发单位: 正方软件股份有限公司！");
                break;
            case R.id.re_exit:

                if(!isLogin){
                    Intent intent1 = new Intent(getContext(), LoginActivity.class);
                   // intent1.putExtra("isLogin",isLogin);
                    startActivityForResult(intent1,1);

                    break;
                }else{
                    showNormalDialog();


                }

                break;
        }
    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getContext());
       /* normalDialog.setIcon(R.drawable.icon_dialog);*/
        normalDialog.setTitle("退出登录");
        
        normalDialog.setMessage("确定要退出吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        SharedPreferences sp = getContext().getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("password","");
                        editor.putBoolean("isLogin",false);
                        editor.commit();
                        isLogin=false;
                        iv_img.setImageResource(R.drawable.default_useravatar);
                        tv_sno.setText("学号:");
                        tv_login.setText("登录");
                        tv_name.setText("请先登录！");
                        ToastUtil.showToast(getContext(),"退出成功");

                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        normalDialog.show();
    }
    /**
     * 从登录界面返回是执行的首先操作
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(resultCode==2){
                if(requestCode==1){
                     item= (Islogin) data.getSerializableExtra("loginData");
                    isLogin=item.islogin();
                    Cookie=item.getCookie();
                    tv_sno.setText(item.getSno());
                    tv_name.setText(item.getName());
                    if(isLogin){
                        studentName=item.getName().replace("同学","");
                        infoUrl ="http://jwc.mmvtc.cn/xsgrxx.aspx?xh="+item.getSno()+"&xm="+ URLEncoder.encode(studentName)+"&gnmkdm=N121501";
                        scoreUrl ="http://jwc.mmvtc.cn/xscjcx.aspx?xh="+item.getSno()+"&xm="+ URLEncoder.encode(studentName)+"&gnmkdm=N121065";
                        courseUrl="http://jwc.mmvtc.cn/tjkbcx.aspx?xh="+item.getSno()+"&xm="+ URLEncoder.encode(studentName)+"&gnmkdm=N121063";
                        new Thread(getImg).start();//得到头像
                        tv_login.setText("退出登录");
                    }else{
                        tv_sno.setText("学号:");
                        tv_login.setText("登录");
                    }

                }

            }


        }
    }

    //得到登录者的头像
    Runnable getImg= new Runnable() {
        @Override
        public void run() {
            getImgMes();
            //getImgByte();
        }
    };
    //得到头像图片
    private void getImgMes(){
        utils.loge("进来了面","11111");
        //String img="";
        Map<String,String> headers=new HashMap<>();
        headers.put("Referer",infoUrl);
        OkHttpUtils
                .get()
                .url(infoUrl)
                .headers(headers)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getContext(),"url错误");
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document dom = Jsoup.parse(response);
                        String img = dom.select("#xszp").attr("src");//得到
                        //http://jwc.mmvtc.cn/readimagexs.aspx?xh=31702160317&timestamp=1246100313&secret=66c255b35516449eb7ad09d7ecfd4c45
                        String URL="http://jwc.mmvtc.cn/"+img;

                        OkHttpUtils
                                .get()//
                                .url(URL)//
                                .tag(this)//
                                .build()//
                                .connTimeOut(20000)
                                .execute(new BitmapCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("TAG", "onResponse：图片错误");
                                    }

                                    @Override
                                    public void onResponse(Bitmap bitmap, int id) {
                                        Log.e("TAG", "onResponse：complete");
                                        iv_img.setImageBitmap(bitmap);
                                    }
                                });

                    }


                });


      /*  Connection conn = Jsoup.connect(infoUrl)
                .method(Connection.Method.GET)
                .header("Cookie", Cookie)
                .referrer(infoUrl);
        Connection.Response response = null;
        try {
            response = conn.execute();
            String content = response.body();

            Document dom = Jsoup.parse(content);
            String img = dom.select("#xszp").attr("src");//得到
            //http://jwc.mmvtc.cn/readimagexs.aspx?xh=31702160317&timestamp=1246100313&secret=66c255b35516449eb7ad09d7ecfd4c45
            String URL="http://jwc.mmvtc.cn/"+img;
            utils.loge("有毒吧",URL);
            HttpGet httpGet = new HttpGet(URL);
            httpGet.setHeader("Cookie", Cookie);//设置cookie
            httpGet.setHeader("Referer", URL);//设置上一个网页的网址
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse httpResponse = client.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                   // String  imgContent= EntityUtils.toString(httpResponse.getEntity());
                   // Document html = Jsoup.parse(imgContent);
                    byte[] byte_image = EntityUtils.toByteArray(httpResponse.getEntity());
                    // Log.i("byte_image", byte_image.toString());
                    Message message = handler.obtainMessage();

                    message.obj = byte_image;
                    message.what = 1;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          *//*  imgUrl=URL;*//*


        } catch (IOException e) {
            getImgMes();
            e.printStackTrace();
        }*/
    }
    //获得imgurl后下载
    /*private void getImgByte(){
        Connection conn = Jsoup.connect(imgUrl)
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .header("Cookie", Cookie)
                .referrer(imgUrl);
        Connection.Response response = null;
        try {
            response = conn.execute();
           // String content = response.body();

                utils.loge("测试这个链接",imgUrl);
            byte[] byte_image = response.bodyAsBytes();
           // Log.i("byte_image", byte_image.toString());
            Message message = handler.obtainMessage();
            utils.loge("看看",byte_image+"");
            message.obj = byte_image;
            message.what = 1;
            handler.sendMessage(message);


        } catch (IOException e) {
            getImgByte();
            e.printStackTrace();
        }

    }*/

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
}
