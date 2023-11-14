package com.pizza.tools.view.api;

import android.view.View;

import com.pizza.tools.view.ClickTool;

/**
 * @author Kyle
 * 重复点击的监听器
 */
public abstract class OnRepeatClickListener implements View.OnClickListener {

    private ClickTool clickTool;

    public OnRepeatClickListener(int fastClickInterval) {
        clickTool = new ClickTool(fastClickInterval);
    }

    /**
     * 重复点击的时候调用
     *
     * @param v
     * @param isCanClick 是否可以点击
     */
    public abstract void onRepeatClick(View v, boolean isCanClick);

    @Override
    public void onClick(View v) {
        onRepeatClick(v, clickTool.isCanClick());
    }

}