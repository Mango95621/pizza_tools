package com.pizza.tools.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.pizza.tools.NetTool;

/**
 * @author Kyle
 * 广播工具类
 */

public class BroadcastTool {

    /**
     * 注册监听网络状态的广播
     *
     * @param context
     * @return
     */
    public static BroadcastReceiverNetWork initRegisterReceiverNetWork(Context context) {
        // 注册监听网络状态的服务
        BroadcastReceiverNetWork mReceiverNetWork = new BroadcastReceiverNetWork();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiverNetWork, mFilter);
        return mReceiverNetWork;
    }

    /**
     * 网络状态改变广播
     */
    public static class BroadcastReceiverNetWork extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetTool.getNetWorkType(context);
        }
    }
}