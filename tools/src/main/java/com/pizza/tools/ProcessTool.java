package com.pizza.tools;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.pizza.tools.app.AppTool;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author BoWei
 * 进程相关工具类
 */

public class ProcessTool {

    /**
     * 当前进程名
     */
    private static String currentProcessName = "";

    /**
     * 获取前台线程包名
     * <p>当不是查看当前App，且SDK大于21时，
     * 需添加权限 {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>}</p>
     *
     * @return 前台应用包名
     */
    public static String getForegroundProcessName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos != null && infos.size() != 0) {
            for (ActivityManager.RunningAppProcessInfo info : infos) {
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return info.processName;
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            System.out.println("RxProcessTool->" + list);
            if (list.size() > 0) {
                // 有"有权查看使用权限的应用"选项
                try {
                    ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
                    AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                    if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) != AppOpsManager.MODE_ALLOWED) {
                        context.startActivity(intent);
                    }
                    if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) != AppOpsManager.MODE_ALLOWED) {
                        Log.d("getForegroundApp", "没有打开\"有权查看使用权限的应用\"选项");
                        return null;
                    }
                    UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    long endTime = System.currentTimeMillis();
                    long beginTime = endTime - 86400000 * 7;
                    List<UsageStats> usageStatses = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
                    if (usageStatses == null || usageStatses.isEmpty()) {
                        return null;
                    }
                    UsageStats recentStats = null;
                    for (UsageStats usageStats : usageStatses) {
                        if (recentStats == null || usageStats.getLastTimeUsed() > recentStats.getLastTimeUsed()) {
                            recentStats = usageStats;
                        }
                    }
                    return recentStats == null ? null : recentStats.getPackageName();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("getForegroundApp", "无\"有权查看使用权限的应用\"选项");
            }
        }
        return null;
    }

    /**
     * 获取后台服务进程
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>}</p>
     *
     * @return 后台服务进程
     */
    public static Set<String> getAllBackgroundProcesses(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        Set<String> set = new HashSet<>();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            Collections.addAll(set, info.pkgList);
        }
        return set;
    }

    /**
     * 杀死后台服务进程
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>}</p>
     *
     * @return 被暂时杀死的服务集合
     */
    public static Set<String> killAllBackgroundProcesses(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        Set<String> set = new HashSet<>();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            for (String pkg : info.pkgList) {
                am.killBackgroundProcesses(pkg);
                set.add(pkg);
            }
        }
        infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            for (String pkg : info.pkgList) {
                set.remove(pkg);
            }
        }
        return set;
    }

    /**
     * 杀死后台服务进程
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>}</p>
     *
     * @param packageName 包名
     * @return {@code true}: 杀死成功<br>{@code false}: 杀死失败
     */
    public static boolean killBackgroundProcesses(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        if (infos == null || infos.size() == 0) {
            return true;
        }
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (Arrays.asList(info.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName);
            }
        }
        infos = am.getRunningAppProcesses();
        if (infos == null || infos.size() == 0) {
            return true;
        }
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (Arrays.asList(info.pkgList).contains(packageName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 杀死前台进程
     * 需添加权限 android.permission.FORCE_STOP_PACKAGES
     * @param packageName 应用包名
     */
    public static void stopAppByForce(String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                ToolInit.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        Method method;
        try {
            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentProcessName() {
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) ToolInit.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                currentProcessName = processInfo.processName;
                break;
            }
        }
        return currentProcessName;
    }

    public static boolean isMainProcess() {
        return getCurrentProcessName().equals(AppTool.getAppPackageName(ToolInit.getApplicationContext()));
    }
}
