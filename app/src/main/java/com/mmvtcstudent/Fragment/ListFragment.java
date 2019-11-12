package com.mmvtcstudent.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mmvtcstudent.Fragment.ListFragments.GzgzFragment;
import com.mmvtcstudent.Fragment.ListFragments.NewsFragment;
import com.mmvtcstudent.Fragment.ListFragments.NoticeFragment;
import com.mmvtcstudent.Fragment.ListFragments.SdeptFragment;
import com.mmvtcstudent.Fragment.ListFragments.XsFragment;
import com.mmvtcstudent.R;
import com.mmvtcstudent.ShowSdeptActivity;
import com.mmvtcstudent.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private TabLayout tabLayout = null;

    private ViewPager viewPager;

    private Fragment[] mFragmentArrays = new Fragment[5];
    private TextView add_res;
    private String[] mTabTitles = new String[5];
    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_list, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.tablayout);
        viewPager = (ViewPager) v.findViewById(R.id.tab_viewpager);

        mTabTitles[0] = "学院新闻";
        mTabTitles[1] = "通知公告";
        mTabTitles[2] = "学术信息";
        mTabTitles[3] = "系部动态";
        mTabTitles[4] = "高职高专动态";
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //设置tablayout距离上下左右的距离
        //tab_title.setPadding(20,20,20,20);
        mFragmentArrays[0] = new NewsFragment();
        mFragmentArrays[1] = new NoticeFragment();
        mFragmentArrays[2] = new XsFragment();
        mFragmentArrays[3] = new SdeptFragment();
        mFragmentArrays[4] = new GzgzFragment();
        PagerAdapter pagerAdapter =new MyViewPagerAdapter(getFragmentManager());
        viewPager.setOffscreenPageLimit(6);//限制重复加载
        viewPager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tabLayout.setupWithViewPager(viewPager);
        //new Thread(getData).start();
        add_res= (TextView) v.findViewById(R.id.add_res);
        add_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu pop = new PopupMenu(getContext(), view);
                pop.getMenuInflater().inflate(R.menu.pop, pop.getMenu());
                pop.show();
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        Intent intent = new Intent(getContext(), ShowSdeptActivity.class);
                        switch (menuItem.getItemId()) {
                            case R.id.jsjgcx:
                                intent.putExtra("sdept","http://www.mmvtc.cn/templet/jsjgcx/indexCope.jsp");
                                intent.putExtra("sdeptName","计算机工程系");
                                startActivity(intent);
                                break;
                            case R.id.hxgcx:
                                intent.putExtra("sdept","http://www.mmvtc.cn/templet/hxgcx/");
                                intent.putExtra("sdeptName","化学工程系");
                                startActivity(intent);
                                break;
                                case R.id.tmgcx:
                                intent.putExtra("sdept","http://www.mmvtc.cn/templet/tmgcx/");
                                intent.putExtra("sdeptName","土木工程系");
                                startActivity(intent);
                                break;
                        }



                        return false;
                    }
                });
            }
        });
        return v;
    }
    final class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm, Fragment[] mFragmentArrays) {
            super(fm);

        }

        public MyViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }

        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

}
