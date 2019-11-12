package com.mmvtcstudent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.mmvtcstudent.Adapter.MyFragmentPagerAdapter;
import com.mmvtcstudent.Fragment.IndexFragment;
import com.mmvtcstudent.Fragment.MeActivitys.BookMark;
import com.mmvtcstudent.utils.CustomDialog;
import com.mmvtcstudent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * 这只是占位置的没有用
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener, android.widget.PopupMenu.OnMenuItemClickListener {
    private ViewPager pager;
    private RadioGroup radioGroup;
    private RadioButton rb_index, rb_list,rb_me;
    private MyFragmentPagerAdapter adapter;
    private CustomDialog customDialog;
    private boolean flag = false;
    private Button button_showPopup;
    private ImageView ivImg;
    private RelativeLayout rl_click;
    //全局定义
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        initView();
    }
    private void initView() {

        ivImg= (ImageView) findViewById(R.id.iv_img);
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_index = (RadioButton) findViewById(R.id.rb_index);
        rb_list = (RadioButton) findViewById(R.id.rb_list);
        rb_me = (RadioButton) findViewById(R.id.rb_me);
        rb_index.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);
        List<Fragment> fragments = new ArrayList<Fragment>();//设置fragment
        fragments.add(new IndexFragment());
        fragments.add(new com.mmvtcstudent.Fragment.ListFragment());
        fragments.add(new com.mmvtcstudent.Fragment.MeFragment());
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);//初始化adapter
        pager = (ViewPager) findViewById(R.id.viewpager);//设置ViewPager
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(this);
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            switch (pager.getCurrentItem()) {
                case 0:
                    rb_index.setChecked(true);
                    break;
                case 1:
                    rb_list.setChecked(true);
                    break;
                case 2:
                    rb_me.setChecked(true);
                    break;

            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int arg1) {
        switch (arg1) {
            case R.id.rb_index:
                pager.setCurrentItem(0);
                break;
            case R.id.rb_list:
                pager.setCurrentItem(1);
                break;
            case R.id.rb_me:
                pager.setCurrentItem(2);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//双击退出软件
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(flag==false){
                flag=true;
                Toast.makeText(getApplicationContext(), "再按一次退出软件", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flag=false;
                    }
                }, 2000);
            }else{
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bookmark:
                Intent intent = new Intent(MainActivity.this, BookMark.class);
                startActivity(intent);
                break;
            case R.id.about:
                    ToastUtil.showToast(getApplicationContext(),"版权所有！请勿侵权！");
                    break;
            case R.id.exit:
                finish();
                break;
        }
        return false;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
          //  case R.id.button_showPopup:
                //限制点击
                //ToastUtil.showToast(this,"暂未开发");
               /* if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME){
                    ToastUtil.showToast(getApplicationContext(),"亲,点击太快不好哦！");
                    break;
                }else{

                   // PopupMenuUtil.getInstance()._show(getApplicationContext(), button_showPopup);
               PopupMenu pop = new PopupMenu(this, view);
                    pop.getMenuInflater().inflate(R.menu.pop, pop.getMenu());
                    pop.show();
                    pop.setOnMenuItemClickListener(this);

                }*/

          /*  case  R.id.rl_click:

                PopupMenuUtil.getInstance()._show(getApplicationContext(), ivImg);
                break;*/


        }
    }
}
