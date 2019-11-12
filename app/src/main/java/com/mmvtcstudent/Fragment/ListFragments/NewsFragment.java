package com.mmvtcstudent.Fragment.ListFragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.mmvtcstudent.Adapter.IndexBaseAdapter;
import com.mmvtcstudent.bean.ItemBean;
import com.mmvtcstudent.R;
import com.mmvtcstudent.ShowArticle;
import com.mmvtcstudent.dao.BookMarkImpl;
import com.mmvtcstudent.utils.PopupList;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private IndexBaseAdapter indexBaseAdapter;
    private List<ItemBean> mData = new ArrayList<>();
    private PullToRefreshLayout pullToRefreshLayout;
    public int page = 1;
    String more = "";
    private List<String> popupMenuItemList = new ArrayList<>();
    private float mRawX;
    private float mRawY;
    private BookMarkImpl bookMark;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listView = (ListView) view.findViewById(R.id.list_news);
        indexBaseAdapter = new IndexBaseAdapter(mData, getContext());


        //收藏功能控件
        PopupList popupList = new PopupList(getContext());
        popupMenuItemList.add("收藏");
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX = event.getRawX();
                mRawY = event.getRawY();
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.showToast(getContext(), "cccc");
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupList popupList = new PopupList(view.getContext());
                popupList.showPopupListWindow(view, position, mRawX, mRawY, popupMenuItemList, new PopupList.PopupListListener() {
                    @Override
                    public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                        return true;
                    }

                    @Override
                    public void onPopupListClick(View contextView, int contextPosition, int position) {
                        bookMark = new BookMarkImpl(getContext());
                        if (!bookMark.selectisData(mData.get(contextPosition).getHref())) {//查询是否有一样数据
                            BookMarkImpl bookMarks = new BookMarkImpl(getContext());
                            bookMarks.insertBookMark(mData.get(contextPosition).getTime(), mData.get(contextPosition).getReadvalue(), mData.get(contextPosition).getItem_name(), mData.get(contextPosition).getHref());
                            ToastUtil.showToast(getContext(), "收藏成功");
                        } else {
                            ToastUtil.showToast(getContext(), "已收藏过！");
                        }


                    }
                });
                return true;
            }
        });

        //刷新控件
        //  pullToRefreshLayout.autoRefresh();
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.activity_list);
        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mData.clear();
                        new Thread(getData).start();
                        pullToRefreshLayout.finishRefresh();
                        ToastUtil.showToast(getContext(), "刷新成功");
                    }
                }, 1000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        utils.loge("测试啊", mData.size() + "");
                        //  ToastUtil.showToast(getContext(),"加载成功");
                        new Thread(getMoreItem).start();

                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 500);
            }
        });

        listView.setAdapter(indexBaseAdapter);
        listView.setOnItemClickListener(this);

        new Thread(getData).start();
        return view;
    }


  /*  Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            indexBaseAdapter.notifyDataSetChanged();
        }
    };*/
    Runnable getData = new Runnable() {
        @Override
        public void run() {
            getMoreURL();//得到当前的李白
        }
    };


    //加载更多
    Runnable getMoreItem = new Runnable() {
        @Override
        public void run() {
            String next = more + "&pn=" + (++page);
            OkHttpUtils
                    .get()
                    .url(next)

                    .build()
                    .execute(new StringCallback()
                    {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(getContext(),"网络可能在施工，下拉即可刷新！");
                                }
                            });
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            getItemData(response);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    indexBaseAdapter.notifyDataSetChanged();
                                }
                            });
                        }


                    });
        }
    };

    private void getMoreURL() {
        OkHttpUtils
                .get()
                .url("http://www.mmvtc.cn/templet/default/index.jsp")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getContext(), "错误");
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getUrl(response);//得到更多的链接
                        OkHttpUtils
                                .get()
                                .url(more)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showToast(getContext(), "错误");
                                                indexBaseAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        getItemData(response);

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                indexBaseAdapter.notifyDataSetChanged();
                                            }
                                        });

                                    }


                                });
                    }


                });
        /*Connection conn = Jsoup.connect("http://www.mmvtc.cn/templet/default/index.jsp")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                .timeout(6000)
                .followRedirects(true)
                .method(Connection.Method.GET);
        Connection.Response response = null;
        try {
            response = conn.execute();
            String content = response.body();
            getUrl(content);
            //handler.sendEmptyMessage(0);
        } catch (IOException e) {
            getMoreURL();
            e.printStackTrace();
        }*/
    }


    private void getItemData(String content) {
        Document body = Jsoup.parse(content);

        Elements li = body.select(".ali-ol-experiment-content ul li");
        for (int i = 0; i < li.size(); i++) {
            String time = li.get(i).select("time").text();
            String readValue = li.get(i).select("span.pv").text();
            String item_name = li.get(i).select("a").text();
            String href = li.get(i).select("a").attr("href");
            mData.add(new ItemBean(time, readValue, item_name, href));
        }

        //handler.sendEmptyMessage(0);
    }
    //得到更多链接
    private void getUrl(String content) {
        Document body = Jsoup.parse(content);
        more = body.select(".col-md-6 .news .pull-right a").attr("href");
    }

   /* private void getlist() {
        Connection conn = Jsoup.connect(more)
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
            getlist();
            e.printStackTrace();
        }
    }*/



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(getActivity(), ShowArticle.class);
        intent.putExtra("name", "学院新闻");
        //带有以下字符的都是跳转到为绝对路径  没有的则是跳到主管网www.mmvtc.com/
        boolean hxgcx = (mData.get(position).getHref().indexOf("hxgcx") > -1);//化学工程系
        boolean tmgcx = (mData.get(position).getHref().indexOf("tmgcx") > -1);//土木工程系
        boolean zzb = (mData.get(position).getHref().indexOf("zzb") > -1);//中专部
        boolean jdxxx = (mData.get(position).getHref().indexOf("jdxxx") > -1);//机电
        boolean jjglx = (mData.get(position).getHref().indexOf("jjglx") > -1);//经济管理系
        boolean gdjy = (mData.get(position).getHref().indexOf("gdjy") > -1);//广东教育
        boolean news = (mData.get(position).getHref().indexOf("news") > -1);//新闻网 茂名视听网
        if (hxgcx || tmgcx || zzb || jdxxx || jjglx || gdjy || news) {
            intent.putExtra("url", mData.get(position).getHref());
        } else {
            intent.putExtra("url", "http://www.mmvtc.cn" + mData.get(position).getHref());
        }
        startActivity(intent);
    }


}
