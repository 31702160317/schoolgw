package com.mmvtcstudent.Fragment.MeActivitys;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.mmvtcstudent.Adapter.IndexBaseAdapter;
import com.mmvtcstudent.R;
import com.mmvtcstudent.ShowArticle;
import com.mmvtcstudent.bean.ItemBean;
import com.mmvtcstudent.dao.BookMarkImpl;
import com.mmvtcstudent.utils.PopupList;
import com.mmvtcstudent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class BookMark extends AppCompatActivity {
    private ListView listView;
    private IndexBaseAdapter indexBaseAdapter;
    private List<ItemBean> mData = new ArrayList<>();
    private ImageView back;
    private PullToRefreshLayout pullToRefreshLayout;
    private TextView count,topCount;
    private List<String> popupMenuItemList = new ArrayList<>();
    private float mRawX;
    private float mRawY;
    private BookMarkImpl bookMark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        back= (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bookMark=new BookMarkImpl(BookMark.this);
        mData=bookMark.getAllBookMark();
        count= (TextView) findViewById(R.id.count);
        topCount= (TextView) findViewById(R.id.topCount);
        if(mData.size()==0){
            topCount.setText("收藏夹");
            count.setVisibility(View.VISIBLE);
        }else{
            topCount.setText("收藏夹("+mData.size()+")");
        }
        PopupList popupList = new PopupList(BookMark.this);

        //长按选择删除
        listView = (ListView) findViewById(R.id.list_bookmark);
        popupMenuItemList.add("删除");
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
                Intent intent = new Intent(getApplicationContext(), ShowArticle.class);
                intent.putExtra("name","收藏");

                //带有以下字符的都是跳转到为绝对路径  没有的则是跳到主管网www.mmvtc.com/
                boolean hxgcx=(mData.get(position).getHref().indexOf("hxgcx")>-1);//化学工程系
                boolean tmgcx=(mData.get(position).getHref().indexOf("tmgcx")>-1);//土木工程系
                boolean zzb=(mData.get(position).getHref().indexOf("zzb")>-1);//中专部
                boolean jdxxx=(mData.get(position).getHref().indexOf("jdxxx")>-1);//机电
                boolean jjglx=(mData.get(position).getHref().indexOf("jjglx")>-1);//经济管理系
                boolean gdjy=(mData.get(position).getHref().indexOf("gdjy")>-1);//广东教育
                boolean news=(mData.get(position).getHref().indexOf("news")>-1);//新闻网 茂名视听网
                boolean jsjgcx=(mData.get(position).getHref().indexOf("jsjgcx")>-1);//计算机工程系
                //  utils.loge("计算机工程系",mData.get(position).getHref());
                if(hxgcx||tmgcx||zzb||jdxxx||jjglx||gdjy||news){
                    intent.putExtra("url",mData.get(position).getHref());
                }else{
                    intent.putExtra("url","http://www.mmvtc.cn"+mData.get(position).getHref());
                }

                startActivity(intent);
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
                        ToastUtil.showToast(BookMark.this,mData.get(position).getHref());
                        bookMark=new BookMarkImpl(BookMark.this);
                        int i= bookMark.deleteBookMark(mData.get(contextPosition).getHref());
                        if(i>0){
                            mData.remove(contextPosition);
                            handler.sendEmptyMessage(0);
                            ToastUtil.showToast(BookMark.this, "删除成功");
                        }else{
                            ToastUtil.showToast(BookMark.this, "删除失败");
                        }

                    }
                });
                return true;
            }
        });
        indexBaseAdapter=new IndexBaseAdapter(mData, this);
        listView.setAdapter(indexBaseAdapter);
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

         /*   BookMarkImpl bookMark=new BookMarkImpl(BookMark.this);
            mData=bookMark.getAllBookMark();*/
            if(mData.size()==0){
                topCount.setText("收藏夹");
                count.setVisibility(View.VISIBLE);
            }else{
                topCount.setText("收藏夹("+mData.size()+")");
            }
            indexBaseAdapter.notifyDataSetChanged();
        }
    };
}
