package com.pizza.tools.file;

import android.os.Build;
import android.os.Environment;

import com.pizza.tools.file.util.FileGlobalUtil;
import com.pizza.tools.file.util.FileMimeType;
import com.pizza.tools.file.util.FileOperatorUtil;
import com.pizza.tools.file.util.FilePathUtil;
import com.pizza.tools.file.util.FileSizeUtil;
import com.pizza.tools.file.util.FileUriUtil;
import com.pizza.tools.file.util.MediaStoreUtil;
import com.pizza.tools.DataTool;

import java.io.File;

/**
 * @author BoWei
 * 2023/8/22 14:44
 * 文件工具类
 */
public class FileTool {

    public static final String TAG = "FileTool";
    private static volatile FileTool singleton;
    private final FilePathUtil mFilePathUtil;
    private final FileUriUtil mFileUriUtil;
    private final FileSizeUtil mFileSizeUtil;
    private final FileGlobalUtil mFileGlobalUtil;
    private final FileMimeType mFileMimeType;
    private final FileOperatorUtil mFileOperatorUtil;
    private final MediaStoreUtil mMediaStoreUtil;

    private FileTool() {
        mMediaStoreUtil = new MediaStoreUtil();
        mFilePathUtil = new FilePathUtil();
        mFileUriUtil = new FileUriUtil();
        mFileGlobalUtil = new FileGlobalUtil();
        mFileMimeType = new FileMimeType();
        mFileOperatorUtil = new FileOperatorUtil();
        mFileSizeUtil = new FileSizeUtil();
    }

    public static FileTool get() {
        if (singleton == null) {
            synchronized (FileTool.class) {
                if (singleton == null) {
                    singleton = new FileTool();
                }
            }
        }
        return singleton;
    }

    public FilePathUtil getFilePathUtil() {
        return mFilePathUtil;
    }

    public FileUriUtil getFileUriUtil() {
        return mFileUriUtil;
    }

    public FileGlobalUtil getFileGlobalUtil() {
        return mFileGlobalUtil;
    }

    public FileMimeType getFileMimeType() {
        return mFileMimeType;
    }

    public FileOperatorUtil getFileOperatorUtil() {
        return mFileOperatorUtil;
    }

    public FileSizeUtil getFileSizeUtil() {
        return mFileSizeUtil;
    }

    public MediaStoreUtil getMediaStoreUtil() {
        return mMediaStoreUtil;
    }

    /**
     * 获取可用的根目录
     */
    public File getCanUseRootPath() {
        File path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 高版本，不直接往sdcard存了
            if (mFileGlobalUtil.isSdcardAvailable()) {
                return mFilePathUtil.getExternalFilesDir();
            } else {
                // 取得data的应用目录
                return mFilePathUtil.getFilesDir();
            }
        } else {
            // 低版本获取sdcard根路径，直接存到sdcard里面
            if (mFileGlobalUtil.isSdcardAvailable()) {
                // 取得sdcard文件路径
                path = Environment.getExternalStorageDirectory();
            } else {
                // 取得data的应用目录
                path = mFilePathUtil.getFilesDir();
            }
        }
        return path;
    }

    /**
     * 获取SD卡剩余空间
     * @return SD卡剩余空间
     */
    public String getFreeSpace() {
        return DataTool.byte2FitSize(mFileSizeUtil.getFreeSpace(mFilePathUtil.getSdcardPath()));
    }

    public File createOrSaveFile(File file) {
        return mFileOperatorUtil.createFile(file);
    }

    public int deleteFile(File file) {
        return mFileOperatorUtil.deleteFile(file);
    }

    public boolean createDir(String dirPath) {
        return mFileOperatorUtil.createDirectory(dirPath);
    }

    public int deleteDir(String dirPath) {
        return mFileOperatorUtil.deleteFile(dirPath);
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public File getFileByPath(String filePath) {
        return DataTool.isNullString(filePath) ? null : new File(filePath);
    }

    public File copyFile(File src, String destFileDir, String destFileName) {
        return mFileOperatorUtil.copyFile(src, destFileDir, destFileName);
    }

    public boolean copyDir(File srcDir, File destDir, boolean isIgnore) {
        return mFileOperatorUtil.copyDirOfResult(srcDir, destDir, isIgnore);
    }

    public boolean moveFile(File src, String destFileDir, String destFileName) {
        return mFileOperatorUtil.moveFileOfResult(src, destFileDir, destFileName);
    }

    public boolean moveDir(File srcDir, File destDir, boolean isIgnore) {
        return mFileOperatorUtil.moveDirOfResult(srcDir, destDir, isIgnore);
    }

}