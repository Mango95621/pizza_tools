package com.pizza.tools;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.pizza.tools.log.LogTool;

/**
 * @author BoWei
 * 工具初始化类，只能在 Application 中初始化
 */
public class ToolInit {

    private static volatile ToolInit singleton;

    private static Application sApplication;

    private static Context sApplicationContext;

    private static boolean sIsDebug;

    private static Handler sGlobalHandle;

    private ToolInit() {
    }

    public static ToolInit get() {
        if (singleton == null) {
            synchronized(ToolInit.class) {
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
    public void init(Application application, boolean isDebug) {
        this.sApplication = application;
        sApplicationContext = application.getApplicationContext();
        LogTool.init(sApplication, isDebug);
        this.sIsDebug = isDebug;
        sGlobalHandle = new Handler(Looper.getMainLooper());
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

    public static boolean isDebug() {
        return sIsDebug;
    }

    public static Handler getGlobalHandle() {
        return sGlobalHandle;
    }
}