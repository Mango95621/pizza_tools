package com.pizza.tools.activity;

import android.app.Activity;

/**
 * @author BoWei
 * 2023/8/1 11:40
 * Activity的实体类
 */
public class ActivityBean {
    private Activity mActivity;
    private long addTime;

    public ActivityBean(Activity activity, long addTime) {
        mActivity = activity;
        this.addTime = addTime;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
}
