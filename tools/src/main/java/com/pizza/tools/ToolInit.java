package com.pizza.tools;

import android.app.Application;
import android.content.Context;

/**
 * @author BoWei
 * 工具初始化类，只能在 Application 中初始化
 */
public class ToolInit {

    private static volatile ToolInit singleton;

    private static Application sApplication;

    private static Context sApplicationContext;

    private static boolean sIsDebug = false;

    private ToolInit() {
    }

    public static ToolInit get() {
        if (singleton == null) {
            synchronized (ToolInit.class) {
                if (singleton == null) {
                    singleton = new ToolInit();
                }
            }
        }
        return singleton;
    }

    /**
     * 只能在 Application 里进行初始化，否则 context 存在内存泄漏，或其他问题
     *
     * @param application
     */
    public void init(Application application) {
        sApplication = application;
        sApplicationContext = application.getApplicationContext();
    }

    public static Context getApplicationContext() {
        if (sApplicationContext != null) {
            return sApplicationContext;
        }
        throw new NullPointerException("ToolInit-->当前无法获取Context，请先在Application中调用init()方法");
    }

    public static Application getApplication() {
        if (sApplication != null) {
            return sApplication;
        }
        throw new NullPointerException("ToolInit-->当前无法获取Context，请先在Application中调用init()方法");
    }

    public static void setIsDebug(boolean isDebug) {
        sIsDebug = isDebug;
    }

    public static boolean isDebug() {
        return sIsDebug;
    }

}