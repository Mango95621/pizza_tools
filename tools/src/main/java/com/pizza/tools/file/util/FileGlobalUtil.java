package com.pizza.tools.file.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.pizza.tools.DataTool;
import com.pizza.tools.EncryptTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.file.FileTool;
import com.pizza.tools.file.model.DocumentInfoBean;
import com.pizza.tools.log.LogTool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import static com.pizza.tools.ConstantsTool.KB;

/**
 * @author Kyle
 * 2023/8/24 11:45
 *
 */
public class FileGlobalUtil {
    public static final String AUTHORITY = ".txzFileProvider";
    public static final String MODE_READ_ONLY = "r";
    public static final String MODE_WRITE_ONLY_ERASING = "w";
    public static final String MODE_WRITE_ONLY_APPEND = "wa";
    public static final String MODE_READ_WRITE_DATA = "rw";
    public static final String MODE_READ_WRITE_FILE = "rwt";
    public static final String MEDIA_TYPE_IMAGE = "image";
    public static final String MEDIA_TYPE_AUDIO = "audio";
    public static final String MEDIA_TYPE_VIDEO = "video";
    /**
     * 1. 文件超过`数量或大小`限制直接返回失败
     * 2. 回调 onError
     *
     * - The file exceeds the `number or size` limit and returns directly to failure
     * - Callback onError
     */
    public static final int OVER_LIMIT_EXCEPT_ALL = 1;
    /**
     * 1. 文件超过数量限制或大小限制
     * 2. 单一类型: 保留未超限制的文件并返回, 去掉后面溢出的部分; 多种类型: 保留正确的文件, 去掉错误类型的所有文件
     * 3. 回调 onSuccess
     *
     * - The file exceeds the number limit or the size limit
     * - 1. Single type: keep the file that is not over the limit and return, remove the overflow part;
     *      2. Multiple types: keep the correct file, remove all files of the wrong type
     * - Call back onSuccess
     */
    public static final int OVER_LIMIT_EXCEPT_OVERFLOW = 2;

    /**
     * @return 传入的Uri是否已具备访问权限 (Whether the incoming Uri has access permission)
     */
    public boolean giveUriPermission(Uri uri) {
        if (uri != null) {
            Context context = ToolInit.getApplication();

            int permission = context.checkUriPermission(uri, android.os.Process.myPid(), android.os.Process.myUid(), Intent.FLAG_GRANT_READ_URI_PERMISSION);

            switch (permission) {
                case PackageManager.PERMISSION_GRANTED:
                    return true;

                case PackageManager.PERMISSION_DENIED:
                    context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    return false;

                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public void revokeUriPermission(Uri uri) {
        ToolInit.getApplication().revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    /**
     * 判断是否有权限
     * @param uri 文件uri
     * @return
     */
    public boolean judgeHasPermission(Uri uri) {
        boolean isAlreadyHavePermission = false;
        try {
            isAlreadyHavePermission = giveUriPermission(uri);
        } catch (Throwable t) {
            LogTool.e(FileTool.TAG, "giveUriPermission Error " + t.getMessage());
        } finally {
            if (!isAlreadyHavePermission) {
                try {
                    revokeUriPermission(uri);
                } catch (Throwable t) {
                    LogTool.e(FileTool.TAG, "revokeUriPermission Error " + t.getMessage());
                }
            }
        }
        return isAlreadyHavePermission;
    }

    /**
     * ### 加载媒体 单个媒体文件 👉 ContentResolver.openFileDescriptor
     *
     * Load media single media file
     *
     * 根据文件描述符选择对应的打开方式。"r"表示读，"w"表示写
     *
     * Select the corresponding opening method according to the file descriptor. "r" means read, "w" means write
     */

    public ParcelFileDescriptor openFileDescriptor(Uri uri) {
        return openFileDescriptor(uri, MODE_READ_ONLY);
    }

    public ParcelFileDescriptor openFileDescriptor(Uri uri, CancellationSignal cancellationSignal) {
        return openFileDescriptor(uri, MODE_READ_ONLY, cancellationSignal);
    }

    public ParcelFileDescriptor openFileDescriptor(Uri uri, @FileOpenMode String mode) {
        return openFileDescriptor(uri, mode, null);
    }

    public ParcelFileDescriptor openFileDescriptor(Uri uri, @FileOpenMode String mode, CancellationSignal cancellationSignal) {
        if (!FileTool.get().getFileUriUtil().checkUri(uri)) {
            return null;
        }
        try {
            return ToolInit.getApplicationContext().getContentResolver().openFileDescriptor(uri, mode, cancellationSignal);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public void dumpParcelFileDescriptor(ParcelFileDescriptor pfd) {
        if (pfd != null) {
            // 读取成功 : 87  大小:2498324B
            LogTool.d(FileTool.TAG, "Read successfully: getStatSize=" + pfd.getStatSize() + "B");
        } else {
            LogTool.e(FileTool.TAG, "Reading failed!");
        }
    }

    /**
     * 获取文档元数据(Get document metadata)
     */
    public DocumentInfoBean dumpMetaData(Uri uri) {
        boolean hasPermission = judgeHasPermission(uri);
        if (!hasPermission) {
            return null;
        }
        ContentResolver resolver = ToolInit.getApplicationContext().getContentResolver();

        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int cIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String displayName = cursor.getString(Math.max(cIndex, 0));

                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    String size = cursor.isNull(sizeIndex) ? "Unknown" : cursor.getString(sizeIndex);

                    LogTool.i(FileTool.TAG, "Name: " + displayName + "  Size: " + size + " B");
                    return new DocumentInfoBean(displayName, size);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public boolean isSdcardAvailable() {
        String sdcardPath = FileTool.get().getFilePathUtil().getSdcardPath();
        if (TextUtils.isEmpty(sdcardPath)) {
            return false;
        }
        File sd = new File(sdcardPath);
        return sd.canWrite();
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isDir(File file) {
        if (file == null) {
            return false;
        }
        return isFileExists(file) && file.isDirectory();
    }

    public String getFileMD5(File file) {
        return EncryptTool.md5File2String(file);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {MODE_READ_ONLY,
            MODE_WRITE_ONLY_ERASING,
            MODE_WRITE_ONLY_APPEND,
            MODE_READ_WRITE_DATA,
            MODE_READ_WRITE_FILE})
    @interface FileOpenMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {MEDIA_TYPE_IMAGE, MEDIA_TYPE_AUDIO, MEDIA_TYPE_VIDEO})
    @interface FileMediaType {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {OVER_LIMIT_EXCEPT_ALL, OVER_LIMIT_EXCEPT_OVERFLOW})
    @interface FileOverLimitStrategy {
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    public String getFileCharsetSimple(File file) {
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     * <p>
     * url : https://app-xxx-oss/xxx.gif
     *  or
     * fileName : xxx.gif
     *
     * @param fullExtension true ".png" ; false "png"
     * @return fullExtension=false, "gif";
     *         fullExtension=true,  ".gif" substring时不加1
     */
    public String getExtension(String pathOrName, char split, boolean fullExtension) {
        if (TextUtils.isEmpty(pathOrName)) {
            return "";
        }

        int dot = pathOrName.lastIndexOf(split);
        if (dot != -1) {
            if (fullExtension) {
                return pathOrName.substring(dot).toLowerCase(Locale.getDefault());
            } else {
                return pathOrName.substring(dot + 1).toLowerCase(Locale.getDefault());
            }
        } else {
            return "";
        }
    }

    public String getExtension(String pathOrName, char split) {
        return getExtension(pathOrName, split, false);
    }

    /**
     * @return [√] "png" ; [×] ".png"
     */
    public String getExtension(String pathOrName) {
        return getExtension(pathOrName, '.', false);
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public String getFileName(File file) {
        if (file == null) {
            return null;
        }
        return getFileName(file.getPath());
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public String getFileName(String filePath) {
        if (DataTool.isNullString(filePath)) {
            return filePath;
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    public String getFileNameNoExtension(File file) {
        if (file == null) {
            return null;
        }
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    public String getFileNameNoExtension(String filePath) {
        if (DataTool.isNullString(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * 获取文件行数
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    public int getFileLines(String filePath) {
        return getFileLines(FileTool.get().getFileByPath(filePath));
    }

    /**
     * 获取文件行数
     *
     * @param file 文件
     * @return 文件行数
     */
    public int getFileLines(File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[KB];
            int readChars;
            while ((readChars = is.read(buffer, 0, KB)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (buffer[i] == '\n') {
                        ++count;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileTool.get().getFileOperatorUtil().closeIo(is);
        }
        return count;
    }
}