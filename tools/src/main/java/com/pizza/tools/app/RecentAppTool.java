package com.pizza.tools.app;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 清除任务管理器所有应用（切换到儿童模式时调用）
 */
public class RecentAppTool {

    /**
     * If set, the process of the root activity of the task will be killed
     * as part of removing the task.
     */
    public static final int REMOVE_TASK_KILL_PROCESS = 0x0001;
    private static final String[] WHITE_PACKAGES = {
            "cc.popin.aladdin.home",
            "cc.popin.aladdin.settings"
    };
    private ActivityManager mActivityManager = null;
    private Method mRemoveTask;

    public RecentAppTool(Context context) {
        try {
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            mRemoveTask = activityManagerClass.getMethod("removeTask", new Class[]{int.class});
            mRemoveTask.setAccessible(true);

        } catch (ClassNotFoundException e) {
            Log.i("RecentActivityManager", "No Such Class Found Exception", e);
        } catch (Exception e) {
            Log.i("RecentActivityManager", "General Exception occurred", e);
        }
    }

    /**
     * Completely remove the given task.
     *
     * @param taskId Identifier of the task to be removed.
     * @param flags  Additional operational flags.  May be 0 or
     *               {@link #REMOVE_TASK_KILL_PROCESS}.
     * @return Returns true if the given task was found and removed.
     */
    public boolean removeTask(int taskId, int flags) {
        try {
            return (Boolean) mRemoveTask.invoke(mActivityManager, Integer.valueOf(taskId)); // , Integer.valueOf(flags)
        } catch (Exception ex) {
            Log.i("RecentActivityManager", "Task removal failed", ex);
        }
        return false;
    }

    public void clearRecentTasks() {
        try {
            List<ActivityManager.RecentTaskInfo> recents =
                    mActivityManager.getRecentTasks(1000, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
            // Start from 1, since we don't want to kill ourselves!
            for (int i = 0; i < recents.size(); i++) {
                String packageName = (recents.get(i) != null && recents.get(i).baseActivity != null)
                        ? recents.get(i).baseActivity.getPackageName() : "";

                boolean isNeedContinue = false;
                for (String p : WHITE_PACKAGES) {
                    if (p.equals(packageName)) {
                        isNeedContinue = true;
                        break;
                    }
                }
                if (isNeedContinue) {
                    continue;
                }
                removeTask(recents.get(i).persistentId, 0);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}