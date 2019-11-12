package com.mmvtcstudent.Fragment.MeActivitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mmvtcstudent.R;
import com.mmvtcstudent.utils.CustomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Info extends AppCompatActivity {
    private SimpleAdapter adapter;
    private String infoUrl;
    private String cookie;
    private CustomDialog customDialog;

    private ListView listView;

    private List<Map<String, String>> list = new ArrayList<Map<String,String>>();

    private String[] listKey = {  "学号", "姓名", "性别",   "出生日期", "身份证号", "民族","来源地区",
            "政治面貌", "学院",   "专业名称", "专业方向", "培养方向",  "行政班",  "学制",
            "学籍状态",   "学历层次",  "入学日期","当前所在级","考生号",  "准考证号", "毕业中学"};

    private String[] listValue = { "xh",  "xm",  "lbl_xb", "lbl_csrq", "lbl_sfzh", "lbl_mz",  "lbl_lydq",
            "lbl_zzmm", "lbl_xy", "lbl_zymc","lbl_zyfx","lbl_pyfx", "lbl_xzb", "lbl_xz",
            "lbl_xjzt", "lbl_CC", "lbl_rxrq","lbl_dqszj","lbl_ksh", "lbl_zkzh", "lbl_byzx"};

    private String[] listKey1 = {"籍贯","出生地",  "宿舍号", "电子邮箱", "联系电话", "邮政编码","家庭所在地"};
    private String[] listValue1 = {"txtjg","csd", "ssh",   "dzyxdz",  "lxdh",    "yzbm","jtszd"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent =getIntent();
//        将登录数据反序列化
        infoUrl =  intent.getStringExtra("infoUrl");
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        cookie = sp.getString("Cookie", null);

        listView = (ListView) findViewById(R.id.info_listView);
        adapter = new SimpleAdapter(this, list, R.layout.me_item, new String[]{"key","value"}, new int[]{R.id.tv_key,R.id.tv_value});
        listView.setAdapter(adapter);
        customDialog = new CustomDialog(this, "正在加载...");
        customDialog.show();
        new Thread(contentRun).start();
    }


  /*  Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            customDialog.dismiss();
            adapter.notifyDataSetChanged();
        };
    };
*/
    Runnable contentRun = new Runnable() {

        @Override
        public void run() {
         /*   Connection conn = Jsoup.connect(infoUrl)
                    .method(Connection.Method.GET)
                    .header("Cookie", cookie)
                    .referrer(infoUrl);
            Connection.Response response = null;
            try {
                response = conn.execute();
                String content = response.body();
                // HttpUtils.loge("信息html",content);
                getData(content);
                handler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
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
                           /* getcontex().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(getContext(),"错误");
                                }
                            });*/
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            getData(response);
                           Info.this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   customDialog.dismiss();
                                   adapter.notifyDataSetChanged();
                               }
                           });
                        }


                    });

        }

        private void getData(String html) {//解析html获取数据
            Document dom = Jsoup.parse(html);
            for (int i = 0; i < listKey.length; i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", listKey[i]);
                map.put("value", dom.getElementById(listValue[i]).text());
                list.add(map);
            }
            for (int i = 0; i < listKey1.length; i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("key", listKey1[i]);
                map.put("value", dom.getElementById(listValue1[i]).attr("value"));
                list.add(map);
            }
        }
    };

    public void back(View v) {
        finish();
    }
}
