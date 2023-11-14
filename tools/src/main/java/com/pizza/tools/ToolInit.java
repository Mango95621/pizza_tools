package com.pizza.tools;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.pizza.tools.log.LogTool;

/**
 * @author Kyle
 * 工具初始化类，只能在 Application 中初始化
 */
public class ToolInit {

    private static volatile ToolInit singleton;

    private Application mApplication;

    private Context mApplicationContext;

    private boolean isDebug;

    private Handler globalHandle;

    private ToolInit() {
    }

    public static ToolInit getInstance() {
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
        this.mApplication = application;
        mApplicationContext = application.getApplicationContext();
        LogTool.init(mApplication, isDebug);
        this.isDebug = isDebug;
        globalHandle = new Handler(Looper.getMainLooper());
    }

    public Context getApplicationContext() {
        if (mApplicationContext != null) {
            return mApplicationContext;
        }
        throw new NullPointerException("ToolInit-->当前无法获取Context，请先在Application中调用init()方法");
    }

    public Application getApplication() {
        if (mApplication != null) {
            return mApplication;
        }
        throw new NullPointerException("ToolInit-->当前无法获取Context，请先在Application中调用init()方法");
    }

    public boolean isDebug() {
        return isDebug;
    }

    public Handler getGlobalHandle() {
        return globalHandle;
    }
}