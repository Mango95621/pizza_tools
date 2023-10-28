package com.pizza.tools;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.IntDef;

import com.pizza.tools.log.LogTool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Kyle
 * 2023/8/23 15:44
 * Toast的调用，需要搭配UI库0.0.5以上版本使用
 */
public class ToastTool {
    /**
     * action由当前应用包名+toast来组成，如com.txznet.txz.toast
     */
    private static String sToastAction;

    public static final int NORMAL = 0;
    public static final int INFO = 1;
    public static final int ERROR = 2;
    public static final int SUCCESS = 3;
    public static final int WARNING = 4;

    public static void showToast(String content) {
        showToast(content, 500);
    }

    public static void showToast(String content, int duration) {
        showToast(content, duration, NORMAL);
    }

    public static void showToast(String content, int duration, @ToastType int type) {
        if (TextUtils.isEmpty(sToastAction)) {
            sToastAction = ToolInit.getApplicationContext().getPackageName() + ".toast";
            try {
                Class<?> aClass = Class.forName("com.txznet.pizza.ui.ToastBroadcast");
                aClass.newInstance();
            } catch (Exception e) {
                LogTool.e("广播未注册成功->" + e.getMessage());
            }
        }
        Intent intent = new Intent(sToastAction);
        // 仅当前的应用能收到
        intent.setPackage(ToolInit.getApplicationContext().getPackageName());
        intent.putExtra("content", content);
        intent.putExtra("duration", duration);
        intent.putExtra("type", type);
        ToolInit.getApplication().sendBroadcast(intent);
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef(value = {NORMAL, INFO, SUCCESS, WARNING, ERROR})
    public static @interface ToastType {
    }
}