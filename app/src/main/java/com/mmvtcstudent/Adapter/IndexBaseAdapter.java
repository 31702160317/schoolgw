package com.mmvtcstudent.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mmvtcstudent.bean.ItemBean;
import com.mmvtcstudent.R;

import java.util.List;

/**
 * Created by W on 2019/7/11.
 * 所有条目的listview适配器
 */

public class IndexBaseAdapter extends BaseAdapter {
    private List<ItemBean> mData;
    private Context mContext;


    public IndexBaseAdapter(List<ItemBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item,viewGroup,false);
            holder = new ViewHolder();
            holder.time= (TextView) convertView.findViewById(R.id.time);
            holder.readvalue= (TextView) convertView.findViewById(R.id.readvalue);
            holder.item_name= (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.time.setText(mData.get(i).getTime());
        holder.readvalue.setText(mData.get(i).getReadvalue());
        holder.item_name.setText(mData.get(i).getItem_name());

        return convertView;
    }
   static   class ViewHolder{
        TextView time;
        TextView readvalue;
        TextView item_name;


    }
}
