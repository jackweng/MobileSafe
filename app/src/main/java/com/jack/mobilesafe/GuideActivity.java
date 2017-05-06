package com.jack.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.jack.mobilesafe.utils.CacheUtils;
import com.jack.mobilesafe.viewpageranimation.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

import static com.jack.mobilesafe.utils.CacheUtils.IS_FIRST_USE;

public class GuideActivity extends Activity implements View.OnClickListener {

    private ViewPager mViewPager;
    private Button btn_enter;
    private List<ImageView> imageViews;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        context = this;

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        btn_enter = (Button) findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(this);
        initPager();

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageViews.get(position));
                return imageViews.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViews.get(position));
            }

            @Override
            public int getCount() {
                return imageViews == null? 0:imageViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object == view;
            }
        });

        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == imageViews.size() - 1) {
                    btn_enter.setVisibility(View.VISIBLE);
                } else {
                    btn_enter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPager() {
        imageViews = new ArrayList<>();
        ImageView imageView1 = new ImageView(context);
        imageView1.setBackgroundResource(R.drawable.guide_1);
        imageViews.add(imageView1);
        ImageView imageView2 = new ImageView(context);
        imageView2.setBackgroundResource(R.drawable.guide_2);
        imageViews.add(imageView2);
        ImageView imageView3 = new ImageView(context);
        imageView3.setBackgroundResource(R.drawable.guide_3);
        imageViews.add(imageView3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter:
                CacheUtils.putBoolean(context, IS_FIRST_USE, false);
                Intent intent = new Intent();
                intent.setClass(GuideActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}