package com.pizza.tools.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.pizza.tools.view.api.OnAnimUpdateListener;

/**
 * @author BoWei
 * 动画工具类
 */
public class AnimatorTool {

    public static ObjectAnimator showAnim;
    public static ObjectAnimator hideAnim;
    private static final Interpolator accelerator = new AccelerateInterpolator();
    private static final Interpolator decelerator = new DecelerateInterpolator();

    /**
     * 颜色渐变动画
     *
     * @param beforeColor 变化之前的颜色
     * @param afterColor  变化之后的颜色
     * @param listener    变化事件
     */
    public static void animationColorGradient(int beforeColor, int afterColor, final OnAnimUpdateListener listener) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), beforeColor, afterColor).setDuration(3000);
        valueAnimator.addUpdateListener(animation -> listener.onUpdate((Integer) animation.getAnimatedValue()));
        valueAnimator.start();
    }

    /**
     * 卡片翻转动画
     *
     * @param frontView
     * @param backView
     */
    public static void cardFlipAnimation(Context context,
                                         final View frontView,
                                         final View backView,
                                         long duration) {
        if (frontView == null || backView == null) {
            return;
        }
        if (duration > 0) {
            duration = 200;
        }
        if (showAnim != null) {
            showAnim.end();
            showAnim.cancel();
            showAnim = null;
        }
        if (hideAnim != null && hideAnim.getListeners() != null) {
            hideAnim.end();
            hideAnim.cancel();
            hideAnim.getListeners().clear();
            hideAnim = null;
        }
        // 这里要将两个 view 的视距调大,否则会有变形
        int distance = 16000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        frontView.setCameraDistance(scale);
        backView.setCameraDistance(scale);

        if (frontView.getVisibility() == View.GONE) {
            // 局部layout可达到字体翻转 背景不翻转
            showAnim = ObjectAnimator.ofFloat(frontView,
                    "rotationY", -90f, 0f);
            hideAnim = ObjectAnimator.ofFloat(backView,
                    "rotationY", 0f, 90f);
        } else if (backView.getVisibility() == View.GONE) {
            showAnim = ObjectAnimator.ofFloat(backView,
                    "rotationY", -90f, 0f);
            hideAnim = ObjectAnimator.ofFloat(frontView,
                    "rotationY", 0f, 90f);
        }
        // 翻转速度
        hideAnim.setDuration(duration);
        // 在动画开始的地方速率改变比较慢，然后开始加速
        hideAnim.setInterpolator(accelerator);
        showAnim.setDuration(duration);
        showAnim.setInterpolator(decelerator);
        hideAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator arg0) {
                if (frontView.getVisibility() == View.GONE) {
                    frontView.setVisibility(View.VISIBLE);
                    showAnim.start();
                    backView.setVisibility(View.GONE);
                } else {
                    backView.setVisibility(View.VISIBLE);
                    showAnim.start();
                    frontView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationStart(Animator arg0) {

            }
        });
        hideAnim.start();
    }

    /**
     * 缩小动画
     *
     * @param view
     */
    public static void zoomIn(final View view, float scale, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }

    /**
     * 放大动画
     *
     * @param view
     */
    public static void zoomOut(final View view, float scale) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }

    public static void scaleUpDown(View view) {
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1200);
        view.startAnimation(animation);
    }

    public static void animateHeight(int start, int end, final View view) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();//根据时间因子的变化系数进行设置高度
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = value;
            view.setLayoutParams(layoutParams);//设置高度
        });
        valueAnimator.start();
    }

    public static ObjectAnimator popIn(final View view, final long duration) {
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);

        ObjectAnimator popup = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
                PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0f, 1f));
        popup.setDuration(duration);
        popup.setInterpolator(new OvershootInterpolator());

        return popup;
    }

    public static ObjectAnimator popOut(final View view, final long duration, final AnimatorListenerAdapter animatorListenerAdapter) {
        ObjectAnimator popOut = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0f));
        popOut.setDuration(duration);
        popOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                if (animatorListenerAdapter != null) {
                    animatorListenerAdapter.onAnimationEnd(animation);
                }
            }
        });
        popOut.setInterpolator(new AnticipateOvershootInterpolator());

        return popOut;
    }

    public static long getAnimationDuration(Animation animation, long defaultDuration) {
        if (animation == null) {
            return defaultDuration;
        }
        long result = animation.getDuration();
        return result < 0 ? defaultDuration : result;
    }

    public static long getAnimatorDuration(Animator animator, long defaultDuration) {
        if (animator == null) {
            return defaultDuration;
        }
        long duration = 0;
        if (animator instanceof AnimatorSet) {
            AnimatorSet set = ((AnimatorSet) animator);
            duration = set.getDuration();
            if (duration < 0) {
                for (Animator childAnimation : set.getChildAnimations()) {
                    duration = Math.max(duration, childAnimation.getDuration());
                }
            }
        } else {
            duration = animator.getDuration();
        }
        return duration < 0 ? defaultDuration : duration;
    }
}
