package com.mmvtcstudent.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mmvtcstudent.R;
import com.mmvtcstudent.bean.OneBean;

import java.util.List;

/**
 * 首页 的学院概况grid适配器
 * Created by W on 2019/7/11.
 */

public class OneAdapter extends BaseAdapter {
    private List<OneBean> mData;
    private Context mContext;
    private LinearLayout gv;

    private int bgs[]={
            R.drawable.one_bg,R.drawable.two_bg,
            R.drawable.three_bg,R.drawable.four_bg,
            R.drawable.five_bg,R.drawable.six_bg,
    };
    public OneAdapter(List<OneBean> mData, Context mContext,LinearLayout LinearLayout_gv) {
        this.mData = mData;
        this.mContext = mContext;
        this.gv = LinearLayout_gv;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {



        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.oneitem,viewGroup,false);
            holder = new ViewHolder();
            AbsListView.LayoutParams lp=new AbsListView.LayoutParams(android.view.ViewGroup
                    .LayoutParams.MATCH_PARENT,((gv.getHeight()-12*5)/3));
           /* （这里gridview如果设置了wrapcontent 那么你要给他外面包裹一个父控件高度是matchparent然后传过来这个父控件 计算父控件的高度， 的这样才知道到底高度是多少）-缝隙的宽度*缝隙个数)/行数*/

            holder.mtv= (TextView) convertView.findViewById(R.id.mTv);
            //holder.img= (ImageView) convertView.findViewById(R.id.mIcon);

            holder.bgimg= (ImageView) convertView.findViewById(R.id.bgimg);
           /* holder.bgimg.setMaxHeight(50);*/
           /* holder.readvalue= (TextView) convertView.findViewById(R.id.readvalue);
            holder.item_name= (TextView) convertView.findViewById(R.id.item_name);*/

         /*  AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    gv..getHeight()/1);
            convertView.setLayoutParams(param);*/
            convertView.setLayoutParams(lp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
           // convertView.setLayoutParams(param);
        }

        holder.mtv.setText(mData.get(i).getMtv());
        //holder.mtv.setText(mData.get(i).getMtv());
        //holder.img.setBackgroundResource(imgs[i]);
        holder.bgimg.setImageBitmap(mData.get(i).getBit());

       /* holder.readvalue.setText(mData.get(i).getReadvalue());
        holder.item_name.setText(mData.get(i).getItem_name());*/

        return convertView;
    }
   static   class ViewHolder{
        TextView mtv;
        ImageView img;
       ImageView bgimg;


    }
}
