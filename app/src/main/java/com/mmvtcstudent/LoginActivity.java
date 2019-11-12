package com.mmvtcstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mmvtcstudent.bean.Islogin;
import com.mmvtcstudent.bean.LoginInfo;
import com.mmvtcstudent.utils.ToastUtil;
import com.mmvtcstudent.utils.utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.Call;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_user, et_password, et_vertify;
    private CheckBox isSave;
    private ImageView iv_vertify,back;
    private Button btn_login;
    private Button tv_clear;
    private SharedPreferences sharedPreferences;
    private LoginInfo loginInfo = new LoginInfo();
    private String switchVertifyUrl="http://jwc.mmvtc.cn/CheckCode.aspx";
    private String LoginUrl="http://jwc.mmvtc.cn/default2.aspx";
    private Islogin islogin;
    private  String loginCookie="";
    private Bitmap bitmap;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initView();//初始化控件
        last_read();//上次是不是保存密码
        new Thread(vertifyRun).start();// 获取验证码并得到cookie
    }

    // handler更新
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
               /* case 1:
                    byte[] Picture = (byte[]) msg.obj;
                    //使用BitmapFactory工厂，把字节数组转化为bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                    iv_vertify.setImageBitmap(bitmap);
                    break;*/
                case 2:// 验证码错误
                    loginFail("验证码错误");
                    break;
                case 3:// 密码错误
                    loginFail("密码错误");
                    break;
                case 4:// 用户不存
                    loginFail("用户名不存在或未按照要求参加教学活动");
                    break;
                case 5:


                    //
                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                   editor.putString("Cookie", loginInfo.getCookie());
                    editor.putString("sno", loginInfo.getUser());
                    editor.putString("studentName", loginInfo.getStudentName());
                    editor.putBoolean("isLogin", true);
                    editor.commit();
               //十位数时间戳
                    long timeStampSec = System.currentTimeMillis()/1000;
                    String timestamp = String.format("%010d", timeStampSec);

                    utils.loge("测试时间",loginInfo.getCookie());

                    //登录成功
                    islogin=new Islogin(true,timestamp,loginInfo.getUser(),loginInfo.getStudentName(),loginCookie);
                    ToastUtil.showToast(LoginActivity.this,"登录成功");
                    Intent intent =new Intent();
                    //跳转回到第二个页面  传封装的类
                    intent.putExtra("loginData",islogin);
                    //        第一个参数表示结果返回码
                    setResult(2,intent);
                    finish();
                    break;


            }
        }
    };




    private void loginFail(String tip) {
        Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_LONG).show();
        et_vertify.setText("");
        new Thread(vertifyRun).start();
    }




    //初始化控件
    private void initView() {
        back= (ImageView) findViewById(R.id.back);
        et_user = (EditText) findViewById(R.id.et_user);
        et_password = (EditText) findViewById(R.id.et_password);
        et_vertify = (EditText) findViewById(R.id.et_vertify);
        isSave = (CheckBox) findViewById(R.id.cb_isSave);
        iv_vertify = (ImageView) findViewById(R.id.iv_vertify);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_clear= (Button) findViewById(R.id.clear_log);
        tv_clear.setOnClickListener(this);
        iv_vertify.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    /**
     * 上次登录是否有记住密码
     */
    private void last_read() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String stuUser = sp.getString("user", null);
        String stuPassword = sp.getString("password", null);
        if (!(TextUtils.isEmpty(stuUser) && TextUtils.isEmpty(stuPassword))) {
            et_user.setText(stuUser);
            et_password.setText(stuPassword);
            isSave.setChecked(true);
        }
    }

    //保存密码
    private void saveUser(String user, String password) {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", user);
        editor.putString("password", password);
        editor.commit();
    }
    //清除记录
    private void clear_log() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        et_user.setText("");
        et_password.setText("");
        Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
    }
    //按钮点击操作
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_vertify:
                new Thread(vertifyRun).start();
                break;
            case R.id.btn_login:
                this.exLogin();
                break;
            case R.id.clear_log:
                this.clear_log();
                break;
            case R.id.back:
                islogin=new Islogin(false,"","","请先登录！","");

                Intent intent =new Intent();
                //跳转回到第二个页面  传封装的类
                intent.putExtra("loginData",islogin);
                //        第一个参数表示结果返回码
                setResult(2,intent);
                finish();

                break;
        }
    }



    /**
     * 登录
     */
    private void exLogin() {
        loginInfo.setUser(et_user.getText().toString().trim());
        loginInfo.setPassword(et_password.getText().toString().trim());
        loginInfo.setVertify(et_vertify.getText().toString().trim());
        if (TextUtils.isEmpty(loginInfo.getUser())) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(loginInfo.getPassword())) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (loginInfo.getVertify().length() != 4) {
            Toast.makeText(this, "验证码为4位！", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(loginRun).start();
    }

    //线程 转换验证码
    Runnable vertifyRun = new Runnable() {
        @Override
        public void run() {
            getVertify();
        }
    };
    //线程 登录网络请求
    Runnable loginRun = new Runnable() {
        @Override
        public void run() {
            login();
        }
    };

    //登录请求
    private void login() {
        OkHttpUtils
                .get()
                .url(LoginUrl)
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
                                .url(LoginUrl)
                                .addParams("TextBox1", loginInfo.getUser())
                                .addParams("TextBox2", loginInfo.getPassword())
                                .addParams("TextBox3", loginInfo.getVertify())
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
                                        utils.loge("登录的",loginInfo.getVertify());

                                        checkLogin(response);
                                    }
                                });
                    }

                });


    }

    // 登陆结果
    private void checkLogin(String content) {
        Message msg = new Message();
        if (content.indexOf("验证码不正确") != -1) {
            msg.what = 2;
        } else if (content.indexOf("密码错误") != -1) {
            msg.what = 3;
        } else if (content.indexOf("用户名不存在或未按照要求参加教学活动") != -1) {
            msg.what = 4;
        } else if (content.indexOf("欢迎你") == -1) {

            //得到姓名
            Document document = Jsoup.parse(content);

            String studentName = document.getElementById("xhxm").text();
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("studentName", studentName);
            editor.commit();
            loginInfo.setStudentName(studentName);

            msg.what = 5;
            if (isSave.isChecked()) {// 是否保存账号密码
                saveUser(loginInfo.getUser(), loginInfo.getPassword());
            }
        } else {
            Log.i("why:", content);
        }
        handler.sendMessage(msg);// handler更新
    }

    /**
     * 得到验证码和cookie
     */
    private void getVertify() {
        OkHttpUtils
                .get()//
                .url(switchVertifyUrl)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        Log.e("TAG", "onResponse：complete");
                        iv_vertify.setImageBitmap(bitmap);
                    }
                });

    }
}
