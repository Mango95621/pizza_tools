package com.pizza.tools.file.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.pizza.tools.log.LogTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.file.FileTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author BoWei
 * 2023/8/24 11:25
 * Android R
 */
public class FileUriUtil {
    /**
     * `MANAGE_EXTERNAL_STORAGE` 权限检查
     *
     * @return `true` Have permission
     */
    public boolean isExternalStorageManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return false;
        }
    }

    public boolean jumpManageAppAllFilesPermissionSetting(Context context) {
        return jumpManageAppAllFilesPermissionSetting(context, false);
    }

    /**
     * 跳转到 `MANAGE_EXTERNAL_STORAGE` 权限设置页面
     *
     * @return `true` Has been set
     */
    public boolean jumpManageAppAllFilesPermissionSetting(Context context, boolean isNewTask) {
        if (isExternalStorageManager()) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                if (isNewTask) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } catch (Exception e) {
                LogTool.e(FileTool.TAG, "jumpManageAppAllFilesPermissionSetting:" + e.getMessage());
            }
        }
        return false;
    }

    /**
     * 从 FilePath 中获取 Uri (Get Uri from FilePath)
     *
     * @param path
     * @return
     */
    public Uri getUriByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        } else {
            return getUriByFile(new File(path));
        }
    }

    public Uri getUriByFile(File file) {
        return getUriByFile(file, false);
    }

    /**
     * Return a content URI for a given file.
     *
     * @param file The file.
     * @param isOriginal true content://  or file:// ; false file://xxx
     * @return a content URI for a given file
     */
    public Uri getUriByFile(File file, boolean isOriginal) {
        if (file == null) {
            return null;
        }
        if (isOriginal) {
            return Uri.fromFile(file);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = ToolInit.getApplication().getPackageName() + FileGlobalUtil.AUTHORITY;
                return FileProvider.getUriForFile(ToolInit.getApplication(), authority, file);
            } else {
                return Uri.fromFile(file);
            }
        }
    }

    public Uri getShareUri(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        } else {
            return getUriByFile(new File(path), false);
        }
    }

    /**
     * @return content://  or  file://
     */
    public Uri getShareUri(File file) {
        return getUriByFile(file, false);
    }

    public Uri getOriginalUri(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        } else {
            return getUriByFile(new File(path), true);
        }
    }

    /**
     * @return file://xxx
     */
    public Uri getOriginalUri(File file) {
        return getUriByFile(file, true);
    }

    /**
     * ### Get the file path through Uri
     *
     * - Need permission: RequiresPermission(permission.READ_EXTERNAL_STORAGE)
     *
     * - Modified from: https://github.com/coltoscosmin/FileUtils/blob/master/FileUtils.java
     *
     * @return file path
     */
    public String getPathByUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        LogTool.i(FileTool.TAG,
                "FileUri getPathByUri -> " +
                        "Uri: " + uri +
                        ", Authority: " + uri.getAuthority() +
                        ", Fragment: " + uri.getFragment() +
                        ", Port: " + uri.getPort() +
                        ", Query: " + uri.getQuery() +
                        ", Scheme: " + uri.getScheme() +
                        ", Host: " + uri.getHost() +
                        ", Segments: " + uri.getPathSegments().toString()
        );

        // 以 file:// 开头的使用第三方应用打开 (open with third-party applications starting with file://)
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            return getDataColumn(uri);
        }

        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // Before 4.4 , API 19 content:// 开头, 比如 content://media/external/images/media/123
        if (!isKitKat && ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
            return getDataColumn(uri);
        }

        Context context = ToolInit.getApplication();
        // After 4.4 , API 19
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equals(type)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + File.separator + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory().toString() + File.separator + split[1];
                    }
                } else if ("home".equals(type)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                                .toString() + File.separator + "documents" + File.separator + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory().toString() + File.separator + "documents" + File.separator + split[1];
                    }
                } else {
                    String sdcardPath =
                            Environment.getExternalStorageDirectory().toString() + File.separator + "documents" + File.separator + split[1];
                    if (sdcardPath.startsWith("file://")) {
                        return sdcardPath.replace("file://", "");
                    } else {
                        return sdcardPath;
                    }
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    String[] contentUriPrefixesToTry = new String[]{
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads",
                            "content://downloads/all_downloads"
                    };
                    for (String contentUriPrefix : contentUriPrefixesToTry) {
                        Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix),
                                Long.parseLong(id)
                        );
                        try {
                            String path = getDataColumn(contentUri);
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        } catch (Exception e) {
                            LogTool.e(FileTool.TAG, e.toString());
                        }
                    }
                } else {
                    return getDataColumn(uri);
                }
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                Uri contentUri = null;
                switch (split[0]) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "download":
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                        }
                        break;
                }
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(contentUri, "_id=?", selectionArgs);
            }

            // GoogleDriveProvider
            else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context);
            }
        }
        // MediaStore (and general)
        else if ("content".equals(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            // Google drive legacy provider
            else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context);
            }
            // Huawei
            else if (isHuaWeiUri(uri)) {
                String uriPath = getDataColumn(uri) != null ? getDataColumn(uri) : uri.toString();
                // content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
                if (uriPath.startsWith("/root")) {
                    return uriPath.replace("/root", "");
                }
            }
            return getDataColumn(uri);
        }
        return getDataColumn(uri);
    }

    private String getDataColumn(Uri uri) {
        return getDataColumn(uri, null, null);
    }

    /**
     * BUG : 部分机型进入"文件管理器" 执行到  cursor.getColumnIndexOrThrow(column);出现
     *       Caused by: java.lang.IllegalArgumentException: column '_data' does not exist. Available columns: []
     *
     * Fixed :
     *      https://stackoverflow.com/questions/42508383/illegalargumentexception-column-data-does-not-exist
     *
     */
    public String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String column = MediaStore.Files.FileColumns.DATA;
        String[] projection = new String[]{column};
        Cursor cursor = null;
        try {
            if (uri == null) {
                return null;
            }
            cursor =
                    ToolInit.getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(column);
                return cursor.getString(columnIndex);
            }
        } catch (Throwable e) {
            LogTool.e(FileTool.TAG, "getDataColumn -> " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    //The Uri to check

    public String getGoogleDriveFilePath(Uri uri, Context context) {
        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data, and display it.
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return "";
        }
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if (!cursor.moveToFirst()) {
            return uri.toString();
        }
        String name = cursor.getString(nameIndex);
        File file = new File(context.getCacheDir(), name);

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }
            outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return uri.toString();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority()) ||
                "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    public boolean isLocalStorageDocument(Uri uri) {
        if (uri == null) {
            return false;
        }
        return FileGlobalUtil.AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        if (uri == null) {
            return false;
        }
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        if (uri == null) {
            return false;
        }
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        if (uri == null) {
            return false;
        }
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
     *
     * @param uri
     * @return
     */
    public boolean isHuaWeiUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        return "com.huawei.hidisk.fileprovider".equals(uri.getAuthority());
    }

    /**
     * 将图片文件转换成uri
     *
     * @param context
     * @param imageFile
     * @return
     */
    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
            if (columnIndex > 0) {
                int id = cursor.getInt(columnIndex);
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            }
            return null;
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 1. 检查 Uri 是否正确
     * 2. Uri 对应的文件是否存在 (可能是已删除, 也肯是系统 db 存有 Uri 相关记录, 但是文件失效或者损坏)
     *
     * EN
     * 1. Check if Uri is correct
     * 2. Whether the file corresponding to Uri exists (may be deleted, or the system db has Uri related records, but the file is invalid or damaged)
     *
     * https://stackoverflow.com/questions/7645951/how-to-check-if-resource-pointed-by-uri-is-available
     */
    public boolean checkUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        ContentResolver resolver = ToolInit.getApplicationContext().getContentResolver();

        // 1. Check Uri
        Cursor cursor = null;
        boolean isUriExist = false;
        try {
            cursor = resolver.query(uri, null, null, null, null);
            // cursor null: content Uri was invalid or some other error occurred
            // cursor.moveToFirst() false: Uri was ok but no entry found.
            isUriExist = cursor != null && cursor.moveToFirst();
        } catch (Throwable t) {
            LogTool.e(FileTool.TAG, "1.Check Uri Error: " + t.getMessage());
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable t) {
            }
        }

        // 2. Check File Exist
        // 如果系统 db 存有 Uri 相关记录, 但是文件失效或者损坏 (If the system db has Uri related records, but the file is invalid or damaged)
        InputStream ins = null;
        boolean isFileExist = false;
        try {
            ins = resolver.openInputStream(uri);
            // file exists
            isFileExist = true;
        } catch (Throwable t) {
            // File was not found eg: open failed: ENOENT (No such file or directory)
            LogTool.e(FileTool.TAG, "2. Check File Exist Error: " + t.getMessage());
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
            } catch (Throwable t) {
            }
        }
        return isUriExist && isFileExist;
    }

    /**
     * ### 通过文件 Uri 获取后缀 eg: txt, png, exe...
     *
     * - 先使用 ContentResolver 去查询, 如果返回""则再尝试使用Uri.toString()去查询
     *
     * - 参考: [storage-samples/ActionOpenDocument](https://github.com/android/storage-samples/blob/main/ActionOpenDocument)
     */
    public String getExtension(Uri uri) {
        if (uri == null) {
            return "";
        }

        boolean b = FileTool.get().getFileGlobalUtil().judgeHasPermission(uri);
        if (!b) {
            return "";
        }
        ContentResolver contentResolver = ToolInit.getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String displayName = cursor.getString(index < 0 ? 0 : index);
                    return FileTool.get().getFileGlobalUtil().getExtension(displayName);
                }
            } finally {
                cursor.close();
            }
        }

        String uriString = uri.toString();
        return FileTool.get().getFileGlobalUtil().getExtension(uriString);
    }

    /**
     * 打印"音频或视频"的详细信息 (Use MediaMetadataRetriever)
     * Print "audio or video" details
     */
    public void dumpMediaInfoByMediaMetadataRetriever(Uri uri) {
        if (uri == null) {
            return;
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(ToolInit.getApplication(), uri);
            //获得媒体专辑的标题
            String albumString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            //获取媒体的艺术家信息
            String artistString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            //获取媒体标题信息
            String titleString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            //获取媒体类型
            String mimetypeString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            //获取媒体持续时间
            String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //获取媒体比特率，位率
            String bitrateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            //获取媒体的日期
            String dateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            //如果媒体包含视频，这个键就会检索它的宽度。
            String video_width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            //如果媒体包含视频，这个键就会检索它的高度。
            String video_height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            //元数据键，用于检索歌曲的数量，如音频、视频、文本，在数据源中，如mp4或3gpp文件。
            String NUM_TRACKS = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
            //检索数字字符串的元数据键，该字符串描述了音频数据源的哪个部分来自于
            String DISC_NUMBER = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
            //表演者或艺术家的信息。
            String ALBUMARTIST = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            //作者
            String AUTHOR = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            //元数据键检索在原始记录中描述音频数据源的顺序的数字字符串。
            String CD_TRACK_NUMBER = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            //帧速率
            String CAPTURE_FRAMERATE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CAPTURE_FRAMERATE = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
            } else {
                CAPTURE_FRAMERATE = "";
            }
            //检索音乐专辑编译状态的元数据键。
            String COMPILATION = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
            //元数据键检索关于数据源的composer的信息
            String COMPOSER = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
            //获取数据源的内容类型或类型的元数据键。
            String GENRE = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            //如果这个键存在，那么媒体就包含了音频内容。
            String HAS_AUDIO = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
            //如果这个密钥存在，那么媒体就包含了视频内容。。
            String HAS_VIDEO = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            //如果可用，此键将检索位置信息。
            String LOCATION = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
            //如果有的话，这个键可以获取视频旋转角度的角度。
            String VIDEO_ROTATION = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            //元数据键，用于检索数据源的写入器(如lyriwriter)的信息。
            String WRITER = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
            //元数据键，用于检索数据源创建或修改时的年份。
            String YEAR = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            //此选项用于getFrameAtTime(long、int)，以检索与最近(在时间)或给定时间最接近的数据源相关联的同步(或键)框架。
            String CLOSEST_SYNC = mmr.extractMetadata(MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            //该选项用于getFrameAtTime(long、int)，用于检索与最近或给定时间最接近的数据源相关的帧(不一定是关键帧)。
            String CLOSEST = mmr.extractMetadata(MediaMetadataRetriever.OPTION_CLOSEST);
            //这个选项用于getFrameAtTime，以检索与在给定时间之前或在给定时间内的数据源相关联的同步(或键)框架。
            String PREVIOUS_SYNC = mmr.extractMetadata(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);

            LogTool.i(FileTool.TAG,
                    " ========= MediaMetadataRetriever Info Begin =========" +
                            "METADATA_KEY_ALBUM: " + albumString +
                            "METADATA_KEY_ARTIST: " + artistString +
                            "METADATA_KEY_TITLE: " + titleString +
                            "METADATA_KEY_MIMETYPE: " + mimetypeString +
                            "METADATA_KEY_DURATION: " + durationString +
                            "METADATA_KEY_BITRATE: " + bitrateString +
                            "METADATA_KEY_DATE: " + dateString +
                            "METADATA_KEY_VIDEO_WIDTH: " + video_width +
                            "METADATA_KEY_VIDEO_HEIGHT: " + video_height +
                            "METADATA_KEY_NUM_TRACKS: " + NUM_TRACKS +
                            "METADATA_KEY_DISC_NUMBER: " + DISC_NUMBER +
                            "METADATA_KEY_ALBUMARTIST: " + ALBUMARTIST +
                            "METADATA_KEY_AUTHOR: " + AUTHOR +
                            "METADATA_KEY_CD_TRACK_NUMBER: " + CD_TRACK_NUMBER +
                            "METADATA_KEY_CAPTURE_FRAMERATE: " + CAPTURE_FRAMERATE +
                            "METADATA_KEY_COMPILATION: " + COMPILATION +
                            "METADATA_KEY_COMPOSER: " + COMPOSER +
                            "METADATA_KEY_GENRE: " + GENRE +
                            "METADATA_KEY_HAS_AUDIO: " + HAS_AUDIO +
                            "METADATA_KEY_HAS_VIDEO: " + HAS_VIDEO +
                            "METADATA_KEY_LOCATION: " + LOCATION +
                            "METADATA_KEY_VIDEO_ROTATION: " + VIDEO_ROTATION +
                            "METADATA_KEY_WRITER: " + WRITER +
                            "METADATA_KEY_YEAR: " + YEAR +
                            "OPTION_CLOSEST_SYNC: " + CLOSEST_SYNC +
                            "OPTION_CLOSEST: " + CLOSEST +
                            "OPTION_PREVIOUS_SYNC: " + PREVIOUS_SYNC +
                            "========= MediaMetadataRetriever Info END ========="
            );
        } catch (Exception e) {
            LogTool.e(FileTool.TAG, "dumpMediaInfoByMediaMetadataRetriever: " + e.getMessage());
        } finally {
            try {
                mmr.release();
            } catch (IOException e) {
                LogTool.e(FileTool.TAG, e.getMessage());
            }
        }
    }

    /**
     * 打印"图片"的详细信息 (Use ExifInterface)
     *
     * Print the detailed information of "Image"
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void dumpMediaInfoByExifInterface(Uri uri) {
        try {
            InputStream inputStream = ToolInit.getApplication().getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return;
            }
            // 传入源文件地址就可以
            ExifInterface exifInterface = new ExifInterface(inputStream);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String length = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            // 光圈
            String aperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
            String iso = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
            // 白平衡
            String balance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            // 曝光时间
            String exposure = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            // 焦距
            String foch_length = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            // 海拔高度
            String gps_altitude_ref = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
            String device_type = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            String dateTimeOriginal = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
            String dateTimeDigitized = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);

            // 图片
            // longitude = 0/1,0/1,0/1
            // latitude=0/1,0/1,0/1
            // device_type=NEX 3 5G
            // dateTime=2021:07:12 14:36:30
            // dateTimeOriginal=2021:07:12 14:36:30
            // dateTimeDigitized=2021:07:12 14:36:30
            LogTool.i(FileTool.TAG,
                    "========= ExifInterface Info END =========" +
                            "TAG_GPS_longITUDE: " + longitude +
                            "TAG_GPS_LATITUDE: " + latitude +
                            "TAG_IMAGE_LENGTH: " + length +
                            "TAG_IMAGE_WIDTH: " + width +
                            "TAG_APERTURE_VALUE: " + aperture +
                            "TAG_ISO_SPEED: " + iso +
                            "TAG_WHITE_BALANCE: " + balance +
                            "TAG_EXPOSURE_TIME: " + exposure +
                            "TAG_FOCAL_LENGTH: " + foch_length +
                            "TAG_GPS_ALTITUDE_REF: " + gps_altitude_ref +
                            "TAG_MODEL: " + device_type +
                            "TAG_DATETIME: " + dateTime +
                            "TAG_DATETIME_ORIGINAL: " + dateTimeOriginal +
                            "TAG_DATETIME_DIGITIZED: " + dateTimeDigitized +
                            "========= ExifInterface Info END ========="
            );
        } catch (Throwable t) {
            LogTool.e(FileTool.TAG, "dumpMediaInfoByExifInterface: " + t.getMessage());
        }
    }

    /**
     * 检查 Uri 对应的文件是否为 图片
     */
    public boolean checkImage(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ParcelFileDescriptor parcelFileDescriptor =
                FileTool.get().getFileGlobalUtil().openFileDescriptor(uri, FileGlobalUtil.MODE_READ_ONLY);
        if (parcelFileDescriptor == null) {
            return false;
        }
        BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, options);
        return options.outWidth != -1;
    }

}