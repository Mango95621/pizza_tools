package com.pizza.tools.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.pizza.tools.DataTool;
import com.pizza.tools.EncryptTool;
import com.pizza.tools.ProcessTool;
import com.pizza.tools.ShellTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.activity.IntentTool;
import com.pizza.tools.file.FileTool;
import com.pizza.tools.file.util.FileOperatorUtil;
import com.pizza.tools.file.util.FilePathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle
 */
public class AppTool {

    public static final int RQ_PERMISSION = 2020;

    /**
     * 安装App(支持7.0)
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void installApp(Context context, String filePath) {
        context.startActivity(IntentTool.getInstallAppIntent(context, filePath));
    }

    /**
     * 安装App（支持7.0）
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void installApp(Context context, File file) {
        if (!FileTool.get().getFileGlobalUtil().isFileExists(file)) {
            return;
        }
        installApp(context, file.getAbsolutePath());
    }

    /**
     * 安装App（支持7.0）
     *
     * @param activity    activity
     * @param filePath    文件路径
     * @param requestCode 请求值
     */
    public static void installApp(Activity activity, String filePath, int requestCode) {
        activity.startActivityForResult(IntentTool.getInstallAppIntent(activity, filePath), requestCode);
    }

    /**
     * 安装App(支持7.0)
     *
     * @param activity    activity
     * @param file        文件
     * @param requestCode 请求值
     */
    public static void installApp(Activity activity, File file, int requestCode) {
        if (!FileTool.get().getFileGlobalUtil().isFileExists(file)) {
            return;
        }
        installApp(activity, file.getAbsolutePath(), requestCode);
    }

    /**
     * 静默安装App
     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return {@code true}: 安装成功<br>{@code false}: 安装失败
     */
    public static boolean installAppSilent(Context context, String filePath) {
        File file = FileTool.get().getFileByPath(filePath);
        if (!FileTool.get().getFileGlobalUtil().isFileExists(file)) {
            return false;
        }
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " + filePath;
        ShellTool.CommandResult commandResult = ShellTool.execCmd(command, !isSystemApp(context), true);
        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");
    }

    /**
     * 判断App是否是系统应用
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /**
     * 判断App是否是系统应用
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 卸载App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApp(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return;
        }
        context.startActivity(IntentTool.getUninstallAppIntent(packageName));
    }

    /**
     * 卸载App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    public static void uninstallApp(Activity activity, String packageName, int requestCode) {
        if (DataTool.isNullString(packageName)) {
            return;
        }
        activity.startActivityForResult(IntentTool.getUninstallAppIntent(packageName), requestCode);
    }

    /**
     * 静默卸载App
     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param context     上下文
     * @param packageName 包名
     * @param isKeepData  是否保留数据
     * @return {@code true}: 卸载成功<br>{@code false}: 卸载成功
     */
    public static boolean uninstallAppSilent(Context context, String packageName, boolean isKeepData) {
        if (DataTool.isNullString(packageName)) {
            return false;
        }
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (isKeepData ? "-k " : "") + packageName;
        ShellTool.CommandResult commandResult = ShellTool.execCmd(command, !isSystemApp(context), true);
        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");
    }

    public static void requestPermission(Activity activity, String... permissions) {
        // 申请权限，其中RC_PERMISSION是权限申请码，用来标志权限申请的
        ActivityCompat.requestPermissions(activity, permissions, RQ_PERMISSION);
    }

    /**
     * 用户是否点击了不再提醒
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean isPermissionRequestIgnore(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 检查权限
     *
     * @param context
     * @param permission 例如 Manifest.permission.READ_PHONE_STATE
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int rest = ActivityCompat.checkSelfPermission(context, permission);
            result = rest == PackageManager.PERMISSION_GRANTED;
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 判断App是否有root权限
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppRoot() {
        ShellTool.CommandResult result = ShellTool.execCmd("echo root", true);
        if (result.result == 0) {
            return true;
        }
        if (result.errorMsg != null) {
            Log.d("isAppRoot", result.errorMsg);
        }
        return false;
    }

    /**
     * 打开App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void launchApp(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return;
        }
        context.startActivity(IntentTool.getLaunchAppIntent(context, packageName));
    }

    /**
     * 打开App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    public static void launchApp(Activity activity, String packageName, int requestCode) {
        if (DataTool.isNullString(packageName)) {
            return;
        }
        activity.startActivityForResult(IntentTool.getLaunchAppIntent(activity, packageName), requestCode);
    }

    /**
     * 获取App包名
     *
     * @param context 上下文
     * @return App包名
     */
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取App具体设置
     *
     * @param context 上下文
     */
    public static void getAppDetailsSettings(Context context) {
        getAppDetailsSettings(context, context.getPackageName());
    }

    /**
     * 获取App具体设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void getAppDetailsSettings(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return;
        }
        context.startActivity(IntentTool.getAppDetailsSettingsIntent(packageName));
    }

    /**
     * 获取App名称
     *
     * @param context 上下文
     * @return App名称
     */
    public static String getAppName(Context context) {
        return getAppName(context, context.getPackageName());
    }

    /**
     * 获取App名称
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App名称
     */
    public static String getAppName(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App图标
     *
     * @param context 上下文
     * @return App图标
     */
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    /**
     * 获取App图标
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App图标
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App路径
     *
     * @param context 上下文
     * @return App路径
     */
    public static String getAppPath(Context context) {
        return getAppPath(context, context.getPackageName());
    }

    /**
     * 获取App路径
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App路径
     */
    public static String getAppPath(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pi == null ? null : pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App版本号
     *
     * @return App版本号
     */
    public static String getAppVersionName() {
        return getAppVersionName(ToolInit.getApplicationContext());
    }

    /**
     * 获取App版本号
     *
     * @param context 上下文
     * @return App版本号
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /**
     * 获取App版本号
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本号
     */
    public static String getAppVersionName(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App版本码
     * @return App版本码
     */
    public static int getAppVersionCode() {
        return getAppVersionCode(ToolInit.getApplicationContext());
    }

    /**
     * 获取App版本码
     *
     * @param context 上下文
     * @return App版本码
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * 获取App版本码
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本码
     */
    public static int getAppVersionCode(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return -1;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(Context context) {
        return isAppDebug(context, context.getPackageName());
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return false;
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取App签名
     *
     * @param context 上下文
     * @return App签名
     */
    public static Signature[] getAppSignature(Context context) {
        return getAppSignature(context, context.getPackageName());
    }

    /**
     * 获取App签名
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App签名
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static Signature[] getAppSignature(Context context, String packageName) {
        if (DataTool.isNullString(packageName)) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取应用签名的的SHA1值
     * <p>可据此判断高德，百度地图key是否正确</p>
     *
     * @param context 上下文
     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
    public static String getAppSignatureSHA1(Context context) {
        return getAppSignatureSHA1(context, context.getPackageName());
    }

    /**
     * 获取应用签名的的SHA1值
     * <p>可据此判断高德，百度地图key是否正确</p>
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
    public static String getAppSignatureSHA1(Context context, String packageName) {
        Signature[] signature = getAppSignature(context, packageName);
        if (signature == null) {
            return null;
        }
        return EncryptTool.sha1ToString(signature[0].toByteArray()).
                replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
    }

    /**
     * 判断App是否处于前台
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return info.processName.equals(context.getPackageName());
            }
        }
        return false;
    }

    /**
     * 判断App是否处于前台
     * <p>当不是查看当前App，且SDK大于21时，
     * 需添加权限 {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>}</p>
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppForeground(Context context, String packageName) {
        return !DataTool.isNullString(packageName) && packageName.equals(ProcessTool.getForegroundProcessName(context));
    }

    /**
     * 判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        return !DataTool.isNullString(packageName) && IntentTool.getLaunchAppIntent(context, packageName) != null;
    }

    /**
     * 安装APK
     *
     * @param context
     * @param APK_PATH
     */
    public static void installAPK(Context context, String APK_PATH) {
        // 提示安装APK
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + APK_PATH), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    /**
     * 获取当前App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）</p>
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    public static AppInfo getAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi != null ? getBean(pm, pi) : null;
    }

    /**
     * 得到AppInfo的Bean
     *
     * @param packageManager 包的管理
     * @param packageInfo    包的信息
     * @return AppInfo类
     */
    private static AppInfo getBean(PackageManager packageManager, PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        AppInfo appInfo = new AppInfo();
        File file = new File(applicationInfo.sourceDir);
        // 是否为系统App
        boolean isSystemApp =
                ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                        (applicationInfo.flags
                                & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);

        appInfo.setName(applicationInfo.loadLabel(packageManager).toString())
                .setPackageName(packageInfo.packageName)
                .setSize(file.length())
                .setVersionCode(packageInfo.versionCode)
                .setSystem(isSystemApp);
        if (!TextUtils.isEmpty(packageInfo.versionName)) {
            appInfo.setVersionName(packageInfo.versionName);
        }
        return appInfo;
    }

    /**
     * 获取所有已安装App信息
     * <p>{@link #getBean(PackageManager, PackageInfo)}（名称，图标，包名，包路径，版本号，版本Code，是否安装在SD卡，是否是用户程序）</p>
     * <p>依赖上面的getBean方法</p>
     *
     * @param context 上下文
     * @return 所有已安装的AppInfo列表
     */
    public static List<AppInfo> getAllAppsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有软件信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            if (pi != null) {
                list.add(getBean(pm, pi));
            }
        }
        return list;
    }

    /**
     * 判断当前App处于前台还是后台
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.GET_TASKS"/>}</p>
     * <p>并且必须是系统应用该方法才有效</p>
     *
     * @param context 上下文
     * @return {@code true}: 后台<br>{@code false}: 前台
     */
    public static boolean isAppBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return !topActivity.getPackageName().equals(context.getPackageName());
        }
        return false;
    }

    /**
     * 清除App所有数据
     *
     * @param context  上下文
     * @param dirPaths 目录路径
     * @return {@code true}: 成功<br>{@code false}: 失败
     */
    public static boolean cleanAppData(Context context, String... dirPaths) {
        File[] dirs = new File[dirPaths.length];
        int i = 0;
        for (String dirPath : dirPaths) {
            dirs[i++] = new File(dirPath);
        }
        return cleanAppData(context, dirs);
    }

    /**
     * 清除App所有数据
     *
     * @param dirs 目录
     * @return {@code true}: 成功<br>{@code false}: 失败
     */
    public static boolean cleanAppData(Context context, File... dirs) {
        FileOperatorUtil fileOperatorUtil = FileTool.get().getFileOperatorUtil();
        FilePathUtil filePathUtil = FileTool.get().getFilePathUtil();
        boolean isSuccess = fileOperatorUtil.deleteFilesNotDir(filePathUtil.getCacheDir());
        isSuccess &= fileOperatorUtil.deleteFilesNotDir(filePathUtil.getDatabasePath(null));
        isSuccess &= fileOperatorUtil.deleteFilesNotDir(filePathUtil.getSharedPreferencesPath());
        isSuccess &= fileOperatorUtil.deleteFilesNotDir(filePathUtil.getFilesDir());
        isSuccess &= fileOperatorUtil.deleteFilesNotDir(filePathUtil.getExternalCacheDir());
        for (File dir : dirs) {
            isSuccess &= fileOperatorUtil.deleteFilesNotDir(dir);
        }
        return isSuccess;
    }

    /**
     * 获取对应 apk 的 Context 对象
     *
     * @param pkgName
     * @return
     */
    public static Context getAppContextByPkg(String pkgName) {
        try {
            return ToolInit.getApplication()
                    .createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}