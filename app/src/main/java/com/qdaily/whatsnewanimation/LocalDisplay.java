package com.qdaily.whatsnewanimation;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by dk on 17/1/23.
 */

public class LocalDisplay {

    public static int SCREEN_WIDTH_PIXELS;
    public static int SCREEN_HEIGHT_PIXELS;
    public static int SCREEN_REAL_WIDTH_PIXELS;
    public static int SCREEN_REAL_HEIGHT_PIXELS;
    public static float SCREEN_DENSITY;
    public static float FONTSCALE;
    public static int SCREEN_WIDTH_DP;
    public static int SCREEN_HEIGHT_DP;
    public static int STATUS_BAR_HEIGHT_DP;
    public static int NAVIGATION_BAR_HEIGHT_DP;
    private static boolean sInitialed;

    private static boolean isPad;

    public static void init(Context context) {
        if (sInitialed || context == null) {
            return;
        }
        sInitialed = true;

        //整个屏幕的宽。包含整体。
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        SCREEN_WIDTH_PIXELS = Math.min(metrics.widthPixels, metrics.heightPixels);
        SCREEN_HEIGHT_PIXELS = Math.max(metrics.widthPixels, metrics.heightPixels);
        SCREEN_DENSITY = metrics.density;
        FONTSCALE = metrics.scaledDensity;
        SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / metrics.density);
        SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / metrics.density);

        int heightResId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        STATUS_BAR_HEIGHT_DP = (int) ( heightResId > 0 ? context.getResources().getDimensionPixelSize(heightResId) / metrics.density : 0);
        heightResId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        NAVIGATION_BAR_HEIGHT_DP = (int) ( heightResId > 0 ? context.getResources().getDimensionPixelSize(heightResId) / metrics.density : 0);

        //真是的宽高，去除虚拟键盘。
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                SCREEN_REAL_WIDTH_PIXELS = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                SCREEN_REAL_HEIGHT_PIXELS = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                SCREEN_REAL_WIDTH_PIXELS = realSize.x;
                SCREEN_REAL_HEIGHT_PIXELS = realSize.y;
            } catch (Exception ignored) {

            }


        double x = Math.pow(metrics.widthPixels / metrics.xdpi, 2);
        double y = Math.pow(metrics.heightPixels / metrics.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于7尺寸则为Pad
        isPad = screenInches >= 7.0;

    }

    public static int dp2px(float dp) {
        final float scale = SCREEN_DENSITY;
        return (int) (dp * scale + 0.5f);
    }

    public static int designedDP2px(float designedDp) {
        if (SCREEN_WIDTH_DP != 320) {
            designedDp = designedDp * SCREEN_WIDTH_DP / 320f;
        }
        return dp2px(designedDp);
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / SCREEN_DENSITY + 0.5f);
    }

    public static int px2sp(float pxValue) {
        return (int) (pxValue / FONTSCALE + 0.5f);
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * FONTSCALE + 0.5f);
    }

    public static int sp2dp(float spValue) {
        float pxValue = spValue * FONTSCALE + 0.5f;
        return (int) (pxValue / SCREEN_DENSITY + 0.5f);
    }

    public static void setPadding(final View view, float left, float top, float right, float bottom) {
        view.setPadding(designedDP2px(left), dp2px(top), designedDP2px(right), dp2px(bottom));
    }

    public static RelativeLayout.LayoutParams getLayoutParamsTopMargin(View view, double topRate) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = (int) (SCREEN_HEIGHT_PIXELS * topRate);
        return params;
    }

    public static RelativeLayout.LayoutParams getLayoutParamsTopMargin(View view,
                                                                       double topRate,
                                                                       double widthRate,
                                                                       double heightRate) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = (int) (SCREEN_HEIGHT_PIXELS * topRate);
        params.width = (int) (SCREEN_WIDTH_PIXELS * widthRate);
        params.height = (int) (SCREEN_HEIGHT_PIXELS * heightRate);
        return params;
    }

    public static RelativeLayout.LayoutParams getLayoutParamsWidthHeight(View view,
                                                                         double widthRate,
                                                                         double heightRate) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = (int) (SCREEN_WIDTH_PIXELS * widthRate);
        params.height = (int) (SCREEN_WIDTH_PIXELS * heightRate);
        return params;
    }

    public static RelativeLayout.LayoutParams getLayoutParamsRect(View view, double widthRate,
                                                                  double heightRate,
                                                                  double topRate,
                                                                  double bottomMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = (int) (SCREEN_HEIGHT_PIXELS * topRate);
        params.width = (int) (SCREEN_WIDTH_PIXELS * widthRate);
        params.height = (int) (SCREEN_HEIGHT_PIXELS * heightRate);
        params.bottomMargin = (int) (SCREEN_HEIGHT_PIXELS * bottomMargin);
        return params;
    }

    public static RelativeLayout.LayoutParams getLayoutParamsBottomMargin(View view,
                                                                          double bottomMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.bottomMargin = (int) (SCREEN_HEIGHT_PIXELS * bottomMargin);
        return params;
    }

    public static RelativeLayout.LayoutParams getLayoutParamsBottomMargin(View view,
                                                                          double bottomMargin,
                                                                          double widthRate,
                                                                          double heightRate) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.bottomMargin = (int) (SCREEN_HEIGHT_PIXELS * bottomMargin);
        params.width = (int) (SCREEN_WIDTH_PIXELS * widthRate);
        params.height = (int) (SCREEN_HEIGHT_PIXELS * heightRate);
        return params;
    }
}