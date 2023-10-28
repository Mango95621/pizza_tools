package com.pizza.tools.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

/**
 * 提供自定义View和View的常用操作的工具类。
 */
public class ViewTool {
    public static void viewGone(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void viewVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置View的背景，并且保持原先的padding范围。
     *
     * @param view
     * @param drawable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackgroundKeepingPadding(View view, Drawable drawable) {
        int[] padding =
                new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackground(drawable);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    /**
     * 设置View的背景，并且保持原先的padding范围。
     *
     * @param view
     * @param backgroundResId
     */
    public static void setBackgroundKeepingPadding(View view, int backgroundResId) {
        setBackgroundKeepingPadding(view, view.getResources().getDrawable(backgroundResId));
    }

    /**
     * 设置View的背景，并且保持原先的padding范围。
     *
     * @param view
     * @param color
     */
    public static void setBackgroundColorKeepPadding(View view, @ColorInt int color) {
        int[] padding =
                new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackgroundColor(color);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    public static float getAttrFloatValue(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.getFloat();
    }

    public static int getAttrColor(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    public static ColorStateList getAttrColorStateList(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return ContextCompat.getColorStateList(context, typedValue.resourceId);
    }

    public static Drawable getAttrDrawable(Context context, int attrRes) {
        int[] attrs = new int[attrRes];
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawable = ta.getDrawable(0);
        ta.recycle();
        return drawable;
    }

    /**
     * 设置颜色的alpha值
     *
     * @param color 需要被设置的颜色值
     * @param alpha 取值为[0,1]，0表示全透明，1表示不透明
     * @return 返回改变了 alpha 值的颜色值
     */
    public static int setColorAlpha(@ColorInt int color, float alpha) {
        return color & 0x00ffffff | ((int) (alpha * 255)) << 24;
    }

    /**
     * 根据比例，在两个color值之间计算出一个color值
     * **注意该方法是ARGB通道分开计算比例的**
     *
     * @param fromColor 开始的color值
     * @param toColor   最终的color值
     * @param fraction  比例，取值为[0,1]，为0时返回 fromColor， 为1时返回 toColor
     * @return 计算出的color值
     */
    public static int computeColor(@ColorInt int fromColor, @ColorInt int toColor, float fraction) {
        float radio = fraction;
        radio = Math.max(Math.min(radio, 1f), 0f);
        int minColorA = Color.alpha(fromColor);
        int maxColorA = Color.alpha(toColor);
        int resultA = (int) (((maxColorA - minColorA) * radio) + minColorA);
        int minColorR = Color.red(fromColor);
        int maxColorR = Color.red(toColor);
        int resultR = (int) (((maxColorR - minColorR) * radio) + minColorR);
        int minColorG = Color.green(fromColor);
        int maxColorG = Color.green(toColor);
        int resultG = (int) (((maxColorG - minColorG) * radio) + minColorG);
        int minColorB = Color.blue(fromColor);
        int maxColorB = Color.blue(toColor);
        int resultB = (int) (((maxColorB - minColorB) * radio) + minColorB);
        return Color.argb(resultA, resultR, resultG, resultB);
    }

    /**
     * 计算二阶贝塞尔曲线中的某个点
     * B(t) = (1 - t)^2 * P0 + 2t * (1 - t) * P1 + t^2 * P2, t ∈ [0,1]
     *
     * @param t  曲线长度比例
     * @param p0 起始点
     * @param p1 控制点
     * @param p2 终止点
     * @return t对应的点
     */
    public static PointF calculateBezierPointForQuadratic(
            float t,
            PointF p0,
            PointF p1,
            PointF p2
    ) {
        PointF point = new PointF();
        float temp = 1 - t;
        point.x = temp * temp * p0.x + 2 * t * temp * p1.x + t * t * p2.x;
        point.y = temp * temp * p0.y + 2 * t * temp * p1.y + t * t * p2.y;
        return point;
    }

    /**
     * 计算三阶贝塞尔曲线中的某个点
     * B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3, t ∈ [0,1]
     *
     * @param t  曲线长度比例
     * @param p0 起始点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 终止点
     * @return t对应的点
     */
    public static PointF calculateBezierPointForCubic(
            float t,
            PointF p0,
            PointF p1,
            PointF p2,
            PointF p3
    ) {
        PointF point = new PointF();
        float temp = 1 - t;
        point.x =
                p0.x * temp * temp * temp + 3 * p1.x * t * temp * temp + 3 * p2.x * t * t * temp + p3.x * t * t * t;
        point.y =
                p0.y * temp * temp * temp + 3 * p1.y * t * temp * temp + 3 * p2.y * t * t * temp + p3.y * t * t * t;
        return point;
    }
}
