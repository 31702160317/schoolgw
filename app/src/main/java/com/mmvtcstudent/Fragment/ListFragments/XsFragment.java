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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class XsFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView;
    private IndexBaseAdapter indexBaseAdapter;
    private List<ItemBean> mData = new ArrayList<>();
    private PullToRefreshLayout pullToRefreshLayout;

    private List<String> popupMenuItemList = new ArrayList<>();
    private float mRawX;
    private float mRawY;
    private BookMarkImpl bookMark;
    /*public int page=1;*/
    public XsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xs, container, false);
        listView = (ListView) view.findViewById(R.id.list_xs);
        indexBaseAdapter=new IndexBaseAdapter(mData, getContext());

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
                        bookMark=new BookMarkImpl(getContext());
                        if(!bookMark.selectisData(mData.get(contextPosition).getHref())){//查询是否有一样数据
                            BookMarkImpl   bookMarks=new BookMarkImpl(getContext());
                            bookMarks.insertBookMark(mData.get(contextPosition).getTime(),mData.get(contextPosition).getReadvalue(),mData.get(contextPosition).getItem_name(),mData.get(contextPosition).getHref());
                            ToastUtil.showToast(getContext(), "收藏成功");
                        }else{
                            ToastUtil.showToast(getContext(), "已收藏过！");
                        }
                    }
                });
                return true;
            }
        });

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.activity_list);
        //  pullToRefreshLayout.autoRefresh();
        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mData.clear();
                        new Thread(getNewItem).start();
                        ToastUtil.showToast(getContext(),"刷新成功");
                        pullToRefreshLayout.finishRefresh();
                    }
                },1000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //new Thread(getNewItem).start();

                        pullToRefreshLayout.finishLoadMore();
                    }
                },500);
            }
        });

        listView.setAdapter(indexBaseAdapter);
        listView.setOnItemClickListener(this);
        new Thread(getNewItem).start();
        return view;
    }


    Runnable getNewItem = new Runnable() {
        @Override
        public void run() {
            Map<String,String> map=new HashMap<>();
            map.put("userAgent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            OkHttpUtils
                    .get()
                    .url("http://www.mmvtc.cn/templet/default/index.jsp")
                    .headers(map)
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
                            getUrl(response);
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

    private void getUrl(String content) {
        Document body = Jsoup.parse(content);
        Elements list = body.select(".tabs-container .tab-content ul").get(0).select("li");
        for (int i = 0; i < list.size(); i++) {
            String time = list.get(i).select("time").text();
            String readValue = list.get(i).select("span").text();
            String item_name = list.get(i).select("a").text();
            String href=list.get(i).select("a").attr("href");
            mData.add(new ItemBean(time,readValue,item_name,href));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Intent intent = new Intent(getActivity(), ShowArticle.class);
        intent.putExtra("name","学术信息");
        //带有以下字符的都是跳转到为绝对路径  没有的则是跳到主管网www.mmvtc.com/
        boolean hxgcx=(mData.get(position).getHref().indexOf("hxgcx")>-1);//化学工程系
        boolean tmgcx=(mData.get(position).getHref().indexOf("tmgcx")>-1);//土木工程系
        boolean zzb=(mData.get(position).getHref().indexOf("zzb")>-1);//中专部
        boolean jdxxx=(mData.get(position).getHref().indexOf("jdxxx")>-1);//机电
        boolean jjglx=(mData.get(position).getHref().indexOf("jjglx")>-1);//经济管理系
        boolean gdjy=(mData.get(position).getHref().indexOf("gdjy")>-1);//广东教育
        boolean news=(mData.get(position).getHref().indexOf("news")>-1);//新闻网 茂名视听网
        if(hxgcx||tmgcx||zzb||jdxxx||jjglx||gdjy||news){
            intent.putExtra("url",mData.get(position).getHref());
        }else{
            intent.putExtra("url","http://www.mmvtc.cn"+mData.get(position).getHref());
        }
        startActivity(intent);
    }
}
