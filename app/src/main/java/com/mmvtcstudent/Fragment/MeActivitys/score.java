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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class score extends AppCompatActivity {
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
    private String scoreUrl = "";
    private String cookie = "";
    private String viewstate = "";
    private CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Intent intent =getIntent();
        scoreUrl =  intent.getStringExtra("scoreUrl");
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        cookie = sp.getString("Cookie", null);

        listView = (ListView)findViewById(R.id.listView);


        adapter = new SimpleAdapter(this, list, R.layout.score_item, new String[]{"nianfen","xueqi","className","score"}, new int[]{R.id.nianfen,R.id.xueqi,R.id.className,R.id.score});
        listView.setAdapter(adapter);
        customDialog = new CustomDialog(this, "正在加载...");
        customDialog.show();
        // HttpUtils.loge("scoreUrl",scoreUrl);
        if (list.isEmpty()){
            new Thread(runnable).start();
        }
    }
    /*Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            customDialog.dismiss();
            //
            adapter.notifyDataSetChanged();
        }
    };
*/
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getViewstate();//得到viewstaate并且请求历年成绩
           // getScore();
          //  handler.sendEmptyMessage(0);
        }
    };
    private void getViewstate() {//

        final Map<String,String> headers=new HashMap<>();
        headers.put("Referer",scoreUrl);
        OkHttpUtils
                .get()
                .url(scoreUrl)
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

                        Document html = Jsoup.parse(response);
                        Elements e = html.select("input[name=__VIEWSTATE]");//这里的到密钥
                        viewstate = e.get(0).attr("value");
                        OkHttpUtils
                                .post()
                                .url(scoreUrl)
                                .headers(headers)
                                .addParams("__EVENTTARGET","")
                                .addParams("__VIEWSTATE",viewstate)
                                .addParams("ddlXN", "")
                                .addParams("ddlXQ","")
                                .addParams("ddl_kcxz", "")
                                .addParams("btn_zcj", "历年成绩")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        getScoreItem(response);
                                        score.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                customDialog.dismiss();
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });

                    }


                });

     /*   HttpGet httpGet = new HttpGet(scoreUrl);
        httpGet.setHeader("Cookie", cookie);//设置cookie
        httpGet.setHeader("Referer", scoreUrl);//设置上一个网页的网址
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                Document html = Jsoup.parse(content);
                Elements e = html.select("input[name=__VIEWSTATE]");//这里的到密钥
                viewstate = e.get(0).attr("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void getScore() {
        Map<String,String> headers=new HashMap<>();
        headers.put("Referer",scoreUrl);
        OkHttpUtils
                .post()
                .url(scoreUrl)
                .headers(headers)
                .addParams("__EVENTTARGET","")
                .addParams("__VIEWSTATE",viewstate)
                .addParams("ddlXN", "")
                .addParams("ddlXQ","")
                .addParams("ddl_kcxz", "")
                .addParams("btn_zcj", "历年成绩")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getScoreItem(response);
                        score.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                customDialog.dismiss();
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
      /*  Connection conn = Jsoup.connect(scoreUrl)
                .method(Connection.Method.POST)
                .timeout(6000)
                .header("Cookie", cookie)
                .data("__EVENTTARGET","")
                .data("__VIEWSTATE",viewstate)
                .data("ddlXN", "")
                .data("ddlXQ","")
                .data("ddl_kcxz", "")
                .data("btn_zcj", "历年成绩").referrer(scoreUrl);
        Connection.Response response = null;
        try {
            response = conn.execute();
            String body = response.body();
            getScoreItem(body);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void getScoreItem(String html) {//解析html获取数据
        Document content = Jsoup.parse(html);
        Element ScoreList = content.getElementById("Datagrid1");
        Elements tr = ScoreList.getElementsByTag("tr");
        for (int i = 1; i < tr.size(); i++) {
            Elements td = tr.get(i).getElementsByTag("td");
            String nianfen=td.get(0).text();
            String xueqi = td.get(1).text();
            String className = td.get(3).text();
            String score = td.get(8).text();
            Map<String, String> map = new HashMap<String, String>();
            map.put("nianfen",nianfen);
            map.put("xueqi", xueqi);
            map.put("className", className);
            map.put("score", score);
            list.add(map);
        }

    }

    public void back(View v) {
        finish();
    }
}
