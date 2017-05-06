package com.jack.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.jack.mobilesafe.utils.CacheUtils;
import com.jack.mobilesafe.utils.LogUtils;

import static com.jack.mobilesafe.utils.CacheUtils.IS_FIRST_USE;

public class WelcomeActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        context = this;

        //获取ImageView控件
        ImageView iv_welcome = (ImageView) findViewById(R.id.iv_welcome);
        //动画合集
        AnimationSet set = new AnimationSet(false);

        /**
         * 动画1：透明度动画
         * 使用xml动画实现
         */
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
        set.addAnimation(alphaAnimation);

        /**
         * 动画2：缩放动画
         * 参数1：fromX    初始状态X的比例
         * 参数2：toX      结束状态X的比例
         * 参数3：fromY    初始状态Y的比例
         * 参数4：toY      结束状态Y的比例
         * 参数5：pivotX   中心点X坐标的值
         * 参数6：pivotY   中心点Y坐标的值
         */
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                iv_welcome.getWidth() / 2, iv_welcome.getHeight() /2);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(2000);
        set.addAnimation(scaleAnimation);

        /**
         * 动画3：旋转动画
         * 参数1：初始状态角度
         * 参数2：结束状态角度
         * 参数3：中心点X值的类型
         * 参数4：中心点X的值
         * 参数5：中心点Y值的类型
         * 参数6：中心点Y的值
         */
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(2000);
        set.addAnimation(rotateAnimation);

        /**
         * 为动画添加监听
         */
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //动画开始时执行
                LogUtils.LogI("WelcomActivity:Animation is begin.");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束时执行
                LogUtils.LogI("WelcomActivity:Animation is end.");

                Intent intent = new Intent();
                if (CacheUtils.getBoolean(context, IS_FIRST_USE, true)) {
                    intent.setClass(context, GuideActivity.class);
                } else {
                    intent.setClass(context, SplashActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //动画重复播放时执行
                LogUtils.LogI("WelcomActivity:Animation is repeat.");
            }
        });

        set.setStartOffset(500);

        //为控件设置动画
        iv_welcome.setAnimation(set);
    }
}