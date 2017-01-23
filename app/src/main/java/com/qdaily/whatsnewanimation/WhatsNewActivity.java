package com.qdaily.whatsnewanimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by dk on 17/1/23.
 */

public class WhatsNewActivity extends Activity {

    private RelativeLayout rl1, rl2, rl3;
    private ImageView pageone_text, pagetwo_image, pagetwo_text, pagethree_phone, pagethree_text1, pagethree_text2,
            pagethree_startHome, pagethree_floatText1, pagethree_floatText2, pageone_indicator, pagetwo_indicator, pagethree_indicator;
    private Animation animationPageOneText, animationPageTwoImage, animationPageTwoText,
            animationPageThreePhone, animationPageThreeText1, animationPageThreeText2, animationPageThreeStartHome,
            animationPageThreeFloatText2;
    boolean pageTwoAnimationRun = false, pageThreeAnimationRun = false;
    private PageOneAnimationListener pageOneAnimationListener;
    private PageTwoAnimationListener pageTwoAnimationListener;
    private PageThreeAnimationListener pageThreeAnimationListener;
    private int cur = 0, startHomeleft = 0, startHomeright = 0, startHometop = 0, startHomebottom = 0;
    private float oldX = 0, newX = 0, alpha = 0, clickX = 0, clickY = 0;
    private static final float CHANGE_DISTANCE = 10;//透明度发生变化的最小滑动距离
    private static float GONG_DISTANCE;//翻页时完全消失的最小距离
    private static float FLIP_DISTANCE;//翻页的最小滑动距离为
    private AlphaAnimation showAnimation, hideAnmation;
    private RelativeLayout touch_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_whatsnew);
        LocalDisplay.init(this);
        GONG_DISTANCE = LocalDisplay.SCREEN_WIDTH_PIXELS / 2;
        FLIP_DISTANCE = LocalDisplay.SCREEN_WIDTH_PIXELS / 8;
        Log.w("Whatsnew","   GONG_DISTANCE   "+GONG_DISTANCE+"\n"+" FLIP_DISTANCE  "+FLIP_DISTANCE);
        pageone_indicator = (ImageView) findViewById(R.id.pageone_indicator);
        pagetwo_indicator = (ImageView) findViewById(R.id.pagetwo_indicator);
        pagethree_indicator = (ImageView) findViewById(R.id.pagethree_indicator);
        touch_image = (RelativeLayout) findViewById(R.id.touch_image);
        touch_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (oldX - event.getX() > CHANGE_DISTANCE && cur < 2) {
//                    右滑
                            alpha = 1 - (oldX - event.getX()) / GONG_DISTANCE;
                            if (alpha < 0) {
                                alpha = 0;
                            }
                            setRlAlpha(true, true, alpha);
                        } else if (event.getX() - oldX > CHANGE_DISTANCE && cur > 0) {
//                    左滑
                            alpha = (event.getX() - oldX) / GONG_DISTANCE;
                            if (alpha > 1) {
                                alpha = 1;
                            }
                            setRlAlpha(true, false, alpha);
                        } else {
//                    不满足滑动最大距离,不透明
                            alpha = 0;
                            setRlAlpha(false, true, alpha);
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        clickX = event.getX();
                        clickY = event.getY();
                        if (clickX >= startHomeleft && clickX <= startHomeright && clickY >= startHometop && clickY <= startHomebottom && cur == 2 && !pageThreeAnimationRun) {
                            pagethree_startHome.performClick();
                        }
                        newX = event.getX();
                        if (oldX - newX > FLIP_DISTANCE && cur < 2) {
                            cur++;
                            setPageAnimation(cur, true);
                        } else if (newX - oldX > FLIP_DISTANCE && cur > 0) {
                            cur--;

                            setPageAnimation(cur, false);
                        } else {
                            setRlAlphaWithANimation(oldX - newX > 0, oldX - newX > 0 ? 1f : 0f);
                        }
                        oldX = 0;
                        newX = 0;
                        alpha = 0;
                        clickX = 0;
                        clickY = 0;
                        return true;
                }
                return false;
            }
        });

        rl1 = (RelativeLayout) findViewById(R.id.rl_whatsnew_page1);
        rl2 = (RelativeLayout) findViewById(R.id.rl_whatsnew_page2);
        rl3 = (RelativeLayout) findViewById(R.id.rl_whatsnew_page3);

        initPageOne();
        initPageTwo();
        initPageThree();

        pageOneStart();
    }

    @Override
    public void setTheme(@StyleRes int resid) {
        super.setTheme(resid);
    }

    private void setRlAlphaWithANimation(boolean slideToRight, float toalpha) {
        if (alpha == 0 || alpha == 1) {
            return;
        }
        if (rl1 == null || rl2 == null) {
            return;
        }
        if (slideToRight) {
            showAnimation = new AlphaAnimation(alpha, toalpha);
            showAnimation.setDuration(500);
            showAnimation.setFillAfter(true);
        } else {
            hideAnmation = new AlphaAnimation(alpha, toalpha);
            hideAnmation.setDuration(500);
            hideAnmation.setFillAfter(true);
        }
        switch (cur) {
            case 0:
                if (slideToRight)
                    rl1.startAnimation(showAnimation);
                break;
            case 1:
                if (slideToRight) {
                    rl2.startAnimation(showAnimation);
                } else {
                    rl1.startAnimation(hideAnmation);
                }
                break;
            case 3:
                if(!slideToRight)
                    rl2.startAnimation(hideAnmation);
                break;
        }
        setItemAlpha();
    }

    //semi 是否半透明, slideToRight 是否向右滑动, alpha 透明度的值
    private void setRlAlpha(boolean semi, boolean slideToRight, float alpha) {
        switch (cur) {
            case 0:
                rl1.setAlpha(semi ? alpha : 1.0f);
                break;
            case 1:
                if (slideToRight) {
                    rl2.setAlpha(semi ? alpha : 1.0f);
                } else {
                    rl1.setAlpha(semi ? alpha : 0.0f);
                }
                break;
            case 2:
                rl2.setAlpha(semi ? alpha : 0.0f);
                break;
        }
    }

    private void setPageAnimation(int curPosition, boolean slideToRight) {
        switch (curPosition) {
            case 0:
                pageTwoStop();
                if (alpha != 1) {
                    showAnimation = new AlphaAnimation(alpha, 1f);
                    showAnimation.setDuration(500);
                    showAnimation.setFillAfter(true);
                    rl2.clearAnimation();
                    rl3.clearAnimation();
                    if (rl1.getAlpha() != 1f) {
                        rl1.startAnimation(showAnimation);
                    }
                }
                setIndicator();
                setItemAlpha();
                pageOneStart();
                break;
            case 1:
                if (slideToRight) {
                    pageOneStop();
                    if (alpha != 0) {
                        hideAnmation = new AlphaAnimation(alpha, 0f);
                        hideAnmation.setDuration(500);
                        hideAnmation.setFillAfter(true);
                        rl2.clearAnimation();
                        rl3.clearAnimation();
                        if (rl1.getAlpha() != 0f) {
                            rl1.startAnimation(hideAnmation);
                        }
                    }
                } else {
                    pageThreeStop();
                    if (alpha != 1) {
                        showAnimation = new AlphaAnimation(alpha, 1f);
                        showAnimation.setDuration(500);
                        showAnimation.setFillAfter(true);
                        rl1.clearAnimation();
                        rl3.clearAnimation();
                        if (rl2.getAlpha() != 1f) {
                            rl2.startAnimation(showAnimation);
                        }
                    }
                }
                setIndicator();
                setItemAlpha();
                pageTwoStart();
                break;
            case 2:
                pageTwoStop();
                if (alpha != 0) {
                    hideAnmation = new AlphaAnimation(alpha, 0f);
                    hideAnmation.setDuration(500);
                    hideAnmation.setFillAfter(true);
                    rl1.clearAnimation();
                    rl3.clearAnimation();
                    if (rl2.getAlpha() != 0f)
                        rl2.startAnimation(hideAnmation);
                }
                setIndicator();
                setItemAlpha();
                pageThreeStart();
                break;
        }
    }

    private void setIndicator() {
        switch (cur) {
            case 0:
                pageone_indicator.setVisibility(View.VISIBLE);
                pagetwo_indicator.setVisibility(View.INVISIBLE);
                pagethree_indicator.setVisibility(View.INVISIBLE);
                break;
            case 1:
                pageone_indicator.setVisibility(View.INVISIBLE);
                pagetwo_indicator.setVisibility(View.VISIBLE);
                pagethree_indicator.setVisibility(View.INVISIBLE);
                break;
            case 2:
                pageone_indicator.setVisibility(View.INVISIBLE);
                pagetwo_indicator.setVisibility(View.INVISIBLE);
                pagethree_indicator.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setItemAlpha() {
        switch (cur) {
            case 0:
                rl1.setAlpha(1);
                rl2.setAlpha(1);
                break;
            case 1:
                rl1.setAlpha(0);
                rl2.setAlpha(1);
                break;
            case 2:
                rl1.setAlpha(0);
                rl2.setAlpha(0);
                break;
        }
    }

    private void initPageOne() {
        pageOneAnimationListener = new PageOneAnimationListener();
        pageone_text = (ImageView) findViewById(R.id.pageone_text);
        animationPageOneText = AnimationUtils.loadAnimation(this, R.anim.whatsnew_alpha);
        animationPageOneText.setDuration(1200);
        animationPageOneText.setAnimationListener(pageOneAnimationListener);
    }

    private void initPageTwo() {
        pageTwoAnimationListener = new PageTwoAnimationListener();
        pagetwo_image = (ImageView) findViewById(R.id.pagetwo_image);
        pagetwo_text = (ImageView) findViewById(R.id.pagetwo_text);
        animationPageTwoImage = AnimationUtils.loadAnimation(this, R.anim.whatsnew_scale);
        animationPageTwoImage.setDuration(800);
        animationPageTwoText = AnimationUtils.loadAnimation(this, R.anim.whatsnew_alpha);
        animationPageTwoImage.setAnimationListener(pageTwoAnimationListener);
    }

    private void initPageThree() {
        pageThreeAnimationListener = new PageThreeAnimationListener();
        pagethree_phone = (ImageView) findViewById(R.id.pagethree_phone);
        pagethree_text1 = (ImageView) findViewById(R.id.pagethree_text1);
        pagethree_text2 = (ImageView) findViewById(R.id.pagethree_text2);
        pagethree_startHome = (ImageView) findViewById(R.id.start_home);
        pagethree_startHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iWantGoHome();
            }
        });
        pagethree_startHome.post(new Runnable() {
            @Override
            public void run() {
                startHomeleft = pagethree_startHome.getLeft();
                startHomeright = pagethree_startHome.getRight();
                startHometop = pagethree_startHome.getTop();
                startHomebottom = pagethree_startHome.getBottom();
            }
        });
        pagethree_floatText1 = (ImageView) findViewById(R.id.pagethree_floattext1);
        pagethree_floatText2 = (ImageView) findViewById(R.id.pagethree_floattext2);
        animationPageThreePhone = AnimationUtils.loadAnimation(this, R.anim.whatsnew_alpha);
        animationPageThreeText1 = AnimationUtils.loadAnimation(this, R.anim.whatsnew_scale);
        animationPageThreeText2 = AnimationUtils.loadAnimation(this, R.anim.whatsnew_scale);
        animationPageThreeFloatText2 = AnimationUtils.loadAnimation(this, R.anim.whatsnew_alpha);
        animationPageThreeStartHome = AnimationUtils.loadAnimation(this, R.anim.whatsnew_scale_home);
        animationPageThreePhone.setAnimationListener(pageThreeAnimationListener);
        animationPageThreeText1.setAnimationListener(pageThreeAnimationListener);
        animationPageThreeText2.setAnimationListener(pageThreeAnimationListener);
        animationPageThreeFloatText2.setAnimationListener(pageThreeAnimationListener);
        animationPageThreeStartHome.setAnimationListener(pageThreeAnimationListener);
    }

    private void pageOneStart() {
        pageone_text.startAnimation(animationPageOneText);

    }

    private void pageTwoStart() {
        pageTwoAnimationRun = true;
        pagetwo_image.startAnimation(animationPageTwoImage);
    }

    private void pageThreeStart() {
        pageThreeAnimationRun = true;
        pagethree_phone.startAnimation(animationPageThreePhone);
    }

    private void pageOneStop() {
        pageone_text.clearAnimation();
    }

    private void pageTwoStop() {
        pageTwoAnimationRun = false;
        pagetwo_image.clearAnimation();
        pagetwo_text.clearAnimation();
    }

    private void pageThreeStop() {
        pageThreeAnimationRun = false;
        pagethree_phone.clearAnimation();
        pagethree_text1.clearAnimation();
        pagethree_text2.clearAnimation();
        pagethree_startHome.clearAnimation();
        pagethree_floatText1.clearAnimation();
        pagethree_floatText2.clearAnimation();
    }

    private class PageOneAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private class PageTwoAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (pageTwoAnimationRun) {
                pagetwo_text.startAnimation(animationPageTwoText);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private class PageThreeAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (pageThreeAnimationRun) {
                if (animation == animationPageThreePhone) {
                    pagethree_text1.startAnimation(animationPageThreeText1);
                } else if (animation == animationPageThreeText1) {
                    pagethree_text2.startAnimation(animationPageThreeText2);
                } else if (animation == animationPageThreeText2) {
                    pagethree_floatText1.startAnimation(animationPageThreeFloatText2);
                    pagethree_floatText2.startAnimation(animationPageThreeFloatText2);
                } else if (animation == animationPageThreeFloatText2) {
                    pagethree_startHome.startAnimation(animationPageThreeStartHome);
                } else if (animation == animationPageThreeStartHome) {
                    pageThreeAnimationRun = false;
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private void iWantGoHome() {
        startActivity(new Intent(WhatsNewActivity.this, MainActivity.class));
        WhatsNewActivity.this.finish();
    }
}
