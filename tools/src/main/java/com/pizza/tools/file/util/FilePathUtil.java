package com.pizza.tools.file.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.pizza.tools.log.LogTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.file.FileTool;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Kyle
 * 2023/8/24 09:55
 *
 */
public class FilePathUtil {
    private static final String HIDDEN_PREFIX = ".";

    /**
     * File (not directories) filter.
     */
    private final FileFilter mFileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && !pathname.getName().startsWith(HIDDEN_PREFIX);
        }
    };

    public FileFilter getFileFilter() {
        return mFileFilter;
    }

    /**
     * Folder (directories) filter.
     */
    public FileFilter mDirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() && !pathname.getName().startsWith(HIDDEN_PREFIX);
        }
    };

    public FileFilter getDirFilter() {
        return mDirFilter;
    }

    public String getSdcardPath() {
        File[] externalFilesDirs = ToolInit.getApplicationContext().getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
        if (externalFilesDirs == null || externalFilesDirs.length == 0) {
            return "";
        }
        // 第0个元素一定是内置sdcard卡
        String path = externalFilesDirs[0].getAbsolutePath();
        // 上方获取的路径类似/storage/emulated/0/Android/data/data/xxx.xxx.xxx/files/music
        // 因此截取路径Android，获取到/storage/emulated/0这个sdcard的根路径
        String sdcardPath = path.substring(0, path.indexOf("/Android"));
        return sdcardPath + File.separator;
    }

    // Checks if a volume containing external storage is available for read and write.
    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    public boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    /**
     * 获取外部存储空间视图模式
     * AndroidManifest.xml 中设置 requestLegacyExternalStorage 可修改外部存储空间视图模式，true为 Legacy View，false为 Filtered View。
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    public boolean isExternalStorageLegacy() {
        return Environment.isExternalStorageLegacy();
    }

    /**
     * 获取 Android 系统根目录
     * <pre>path: /system</pre>
     *
     * @return 系统根目录
     */
    public File getRootDirectory() {
        return Environment.getRootDirectory();
    }

    /**
     * 获取 data 目录
     * <pre>path: /data</pre>
     *
     * @return data 目录
     */
    public File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    /**
     * 获取缓存目录
     * <pre>path: data/cache</pre>
     *
     * @return 缓存目录
     */
    public File getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * Media File[]
     */
    public File[] getExternalMediaDirs() {
        return ToolInit.getApplication().getExternalMediaDirs();
    }

    /**
     * Obb File[]
     */
    public File[] getObbDirs() {
        return ToolInit.getApplication().getObbDirs();
    }

    /**
     * Cache File[]
     */
    public File[] getExternalCacheDirs() {
        return ToolInit.getApplication().getExternalCacheDirs();
    }

    /**
     * Data File[]
     * <pre>
     *     getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)[0]
     *     等效于
     *     getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
     * </pre>
     */
    public  File[] getExternalFilesDirs(String type) {
        return ToolInit.getApplication().getExternalFilesDirs(type);
    }

    /**
     * 获取此应用的文件目录
     * <pre>path: /data/data/package/files</pre>
     *
     * @return 此应用的文件目录
     */
    public File getFilesDir() {
        return ToolInit.getApplication().getFilesDir();
    }

    /**
     * 获取此应用的数据库文件目录
     * <pre>path: /data/data/package/databases/name</pre>
     *
     * @param name 数据库文件名
     * @return 数据库文件目录
     */
    public File getDatabasePath(String name) {
        return ToolInit.getApplication().getDatabasePath(name);
    }

    public File getSharedPreferencesPath() {
        return new File(getFilesDir().getParent() + File.separator + "shared_prefs");
    }

    /**
     * 获取此应用的 Obb 目录
     * <pre>path: /storage/emulated/0/Android/obb/package</pre>
     * <pre>一般用来存放游戏数据包</pre>
     *
     * @return 此应用的 Obb 目录
     */
    public File getObbDir() {
        return ToolInit.getApplication().getObbDir();
    }


    /**
     * 获取此应用在外置储存中的缓存目录
     * <pre>path: /storage/emulated/0/Android/data/package/cache</pre>
     *
     * @return 此应用在外置储存中的缓存目录
     */
    public File getExternalCacheDir() {
        return ToolInit.getApplication().getExternalCacheDir();
    }

    /**
     * 获取此应用在外置储存中的文件目录
     * <pre>path: /storage/emulated/0/Android/data/package/files</pre>
     *
     *  <pre>
     *      /storage/emulated/0/Android/data/package/files/Documents/
     *
     *      getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)[0]
     *      等效于
     *      getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
     *  </pre>
     * @return 此应用在外置储存中的文件目录
     */
    public File getExternalFilesDir() {
        return ToolInit.getApplication().getExternalFilesDir(null);
    }

    /**
     * 获取此应用在外置储存中的闹钟铃声目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Alarms</pre>
     *
     * @return 此应用在外置储存中的闹钟铃声目录
     */
    public File getExternalFilesDirAlarms() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_ALARMS);
    }

    /**
     * 获取此应用在外置储存中的相机目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/DCIM</pre>
     *
     * @return 此应用在外置储存中的相机目录
     */
    public File getExternalFilesDirDcim() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_DCIM);
    }

    /**
     * 获取此应用在外置储存中的文档目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Documents</pre>
     *
     * @return 此应用在外置储存中的文档目录
     */
    public File getExternalFilesDirDocuments() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    }

    /**
     * 获取此应用在外置储存中的下载目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Download</pre>
     *
     * @return 此应用在外置储存中的闹钟目录
     */
    public File getExternalFilesDirDownloads() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * 获取此应用在外置储存中的视频目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Movies</pre>
     *
     * @return 此应用在外置储存中的视频目录
     */
    public File getExternalFilesDirMovies() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
    }

    /**
     * 获取此应用在外置储存中的音乐目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Music</pre>
     *
     * @return 此应用在外置储存中的音乐目录
     */
    public File getExternalFilesDirMusic() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
    }

    /**
     * 获取此应用在外置储存中的提示音目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Notifications</pre>
     *
     * @return 此应用在外置储存中的提示音目录
     */
    public File getExternalFilesDirNotifications() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);
    }

    /**
     * 获取此应用在外置储存中的图片目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Pictures</pre>
     *
     * @return 此应用在外置储存中的图片目录
     */
    public File getExternalFilesDirPictures() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    /**
     * 获取此应用在外置储存中的 Podcasts 目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Podcasts</pre>
     *
     * @return 此应用在外置储存中的 Podcasts 目录
     */
    public File getExternalFilesDirPodcasts() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
    }

    /**
     * 获取此应用在外置储存中的铃声目录
     * <pre>path: /storage/emulated/0/Android/data/package/files/Ringtones</pre>
     *
     * @return 此应用在外置储存中的铃声目录
     */
    public File getExternalFilesDirRingtones() {
        return ToolInit.getApplication().getExternalFilesDir(Environment.DIRECTORY_RINGTONES);
    }

    /**
     * 获取数据库存储路径(Get the database storage path)
     *
     * /data/data/包名/databases/
     */
    public String getDatabasePath(Context context, String dirName) {
        File root = context.getDatabasePath(null);
        if (root != null) {
            String path = root.getAbsolutePath() + File.separator + dirName + File.separator;
            File file = new File(path);
            if (!file.exists() && !file.mkdirs()) {
                LogTool.e(FileTool.TAG, "can't make dirs in " + file.getAbsolutePath());
            }
            return path;
        }
        return null;
    }


    /**
     * 获取外部缓存存储路径(Get external cache storage path)
     *
     * 获取此应用的缓存目录
     * release时目录为/data/data/package/cache
     * debug时目录为/SDCard/Android/data/包名/cache/
     *
     * @return 此应用的缓存目录
     */
    public File getCacheDir() {
        if (ToolInit.isDebug()) {
            return ToolInit.getApplication().getExternalCacheDir();
        } else {
            return ToolInit.getApplication().getCacheDir();
        }
    }

    public String getCachePath(String dirName) {
        return getCachePath(getCacheDir(), dirName);
    }

    /**
     * 获取缓存存储路径(Get cache storage path)
     *
     * /SDCard/Android/data/包名/cache/
     *
     * 设置：对应清除缓存(Setting: corresponding to clear cache)
     */
    public String getCachePath(File cacheDir, String dirName) {
        String rootPath = cacheDir.getAbsolutePath();
        if (!TextUtils.isEmpty(rootPath) && !TextUtils.isEmpty(dirName)) {
            String path = rootPath + File.separator + dirName + File.separator;
            File file = new File(path);
            if (!file.exists() && !file.mkdirs()) {
                LogTool.e(FileTool.TAG, "can't make dirs in " + file.getAbsolutePath());
            }
            return path;
        }
        return null;
    }

    public File getFileDir() {
        if (ToolInit.isDebug()) {
            return ToolInit.getApplication().getExternalCacheDir();
        } else {
            return ToolInit.getApplication().getFilesDir();
        }
    }

    public String getFilesPath(String dirName) {
        return getFilesPath(getFileDir(), dirName);
    }

    /**
     * 获取文件存储路径(Get file storage path)
     *
     * /SDCard/Android/data/包名/files/
     *
     * 设置：对应清除数据(Settings: corresponding to clear data)
     */
    public String getFilesPath(File filesDir, String dirName) {
        String rootPath = filesDir.getAbsolutePath();
        if (!TextUtils.isEmpty(rootPath) && !TextUtils.isEmpty(dirName)) {
            String path = rootPath + File.separator + dirName + File.separator;
            File file = new File(path);
            if (!file.exists() && !file.mkdirs()) {
                LogTool.e(FileTool.TAG, "can't make dirs in " + file.getAbsolutePath());
            }
            return path;
        }
        return rootPath;
    }

    public boolean isLocal(String url) {
        return url != null && !url.trim().isEmpty() && !url.startsWith("http") && !url.startsWith("https");
    }

    public boolean isGif(String mimeType) {
        return mimeType != null && !mimeType.trim().isEmpty() && mimeType.equalsIgnoreCase("image/gif");
    }

    public boolean isGif(Uri uri) {
        if (uri == null) {
            return false;
        }
        String mimeType = FileTool.get().getFileMimeType().getMimeType(uri);
        return isGif(mimeType);
    }
}