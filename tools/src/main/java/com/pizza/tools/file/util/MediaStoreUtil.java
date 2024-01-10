package com.pizza.tools.file.util;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.text.TextUtils;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.pizza.tools.file.model.MediaInfoBean;
import com.pizza.tools.file.model.RenameResult;
import com.pizza.tools.log.LogTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.file.FileTool;
import com.pizza.tools.file.model.MediaStoreImage;
import com.pizza.tools.file.model.MediaStoreVideo;
import com.pizza.tools.file.model.QuerySelectionStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author BoWei
 * 2023/8/24 15:25
 */
public class MediaStoreUtil {

    public ContentValues createContentValues(
            String displayName, String description, String mimeType, String title,
            String relativePath) {
        return createContentValues(displayName, description, mimeType, title, relativePath, 1);
    }

    /**
     * ### Create ContentValues
     * ```
     * values.put(MediaStore.Images.Media.IS_PENDING, isPending)
     * Android Q , MediaStore中添加 MediaStore.Images.Media.IS_PENDING flag，用来表示文件的 isPending 状态，0是可见，其他不可见
     * ```
     * @param displayName 文件名
     * @param description 描述
     * @param mimeType 媒体类型
     * @param title 标题
     * @param relativePath 相对路径 eg: ${Environment.DIRECTORY_PICTURES}/xxx
     * @param isPending 默认0 , 0是可见，其他不可见
     */
    public ContentValues createContentValues(
            String displayName, String description, String mimeType, String title,
            String relativePath, Integer isPending) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, description);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Images.Media.TITLE, title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);
            contentValues.put(MediaStore.Images.Media.IS_PENDING, isPending);
        }
        return contentValues;
    }

    /**
     * ContentResolver的insert方法, 将多媒体文件保存到多媒体的公共集合目录
     *
     * https://developer.huawei.com/consumer/cn/doc/50127
     * ```
     * 可以通过PRIMARY_DIRECTORY和SECONDARY_DIRECTORY字段来设置一级目录和二级目录：
     *（a）一级目录必须是和MIME type的匹配的根目录下的Public目录，一级目录可以不设置，不设置时会放到默认的路径；
     *（b）二级目录可以不设置，不设置时直接保存在一级目录下；
     *（c）应用生成的文档类文件，代码里面默认不设置时，一级是Downloads目录，也可以设置为Documents目录，建议推荐三方应用把文档类的文件一级目录设置为Documents目录；
     *（d）一级目录MIME type，默认目录、允许的目录映射以及对应的读取权限如下表所示： https://user-gold-cdn.xitu.io/2020/6/1/1726dd80a91347cf?w=1372&h=470&f=png&s=308857
     * ```
     * @param uri 多媒体数据库的Uri MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
     * @param context
     * @param mimeType 需要保存文件的mimeType
     * @param displayName 显示的文件名字
     * @param description 文件描述信息
     * @param saveFileName 需要保存的文件名字
     * @param saveSecondaryDir 保存的二级目录
     * @param savePrimaryDir 保存的一级目录  eg : Environment.DIRECTORY_DCIM
     * @return 返回插入数据对应的uri
     */

    public String insertMediaFile(
            Uri uri, Context context, String mimeType, String displayName, String description,
            String saveFileName, String saveSecondaryDir, String savePrimaryDir) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, savePrimaryDir + File.separator + saveSecondaryDir);
        }

        Uri url = null;
        String stringUrl = null;
        ContentResolver cr = context.getContentResolver();

        try {
            if (uri == null || saveFileName == null || saveFileName.isEmpty()) {
                return null;
            }

            url = cr.insert(uri, values);
            if (url == null) {
                return null;
            }

            byte[] buffer = new byte[1024];
            FileOutputStream fos = null;
            ParcelFileDescriptor pfd =
                    FileTool.get().getFileGlobalUtil().openFileDescriptor(uri, FileGlobalUtil.MODE_WRITE_ONLY_ERASING);

            try {
                pfd = cr.openFileDescriptor(uri, "w");
                if (pfd == null) {
                    return null;
                }

                fos = new FileOutputStream(pfd.getFileDescriptor());
                InputStream ins = context.getResources().getAssets().open(saveFileName);

                int numRead;
                while ((numRead = ins.read(buffer)) != -1) {
                    fos.write(buffer, 0, numRead);
                }

                fos.flush();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
                if (pfd != null) {
                    try {
                        pfd.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (Exception e) {
            LogTool.e(FileTool.TAG, "Failed to insert media file " + e.getMessage());

            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    /**
     * 1.会出现创建多个图片问题
     *
     * 2.MediaStore.Images.Media.INTERNAL_CONTENT_URI
     * java.lang.UnsupportedOperationException: Writing to internal storage is not supported.
     *    at android.database.DatabaseUtils.readExceptionFromParcel(DatabaseUtils.java:172)
     *    at android.database.DatabaseUtils.readExceptionFromParcel(DatabaseUtils.java:140)
     *    at android.content.ContentProviderProxy.insert(ContentProviderNative.java:481)
     *    at android.content.ContentResolver.insert(ContentResolver.java:1844)
     */
    public Uri insertBitmap(Bitmap bitmap, ContentValues values) {
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = ToolInit.getApplicationContext().getContentResolver();
        Uri insertUri = resolver.insert(externalUri, values);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        OutputStream os = null;
        try {
            if (insertUri != null && bitmap != null) {
                os = resolver.openOutputStream(insertUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                if (os != null) {
                    os.flush();
                }

                LogTool.d(FileTool.TAG, "创建Bitmap成功 insertBitmap " + insertUri);

                values.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(insertUri, values, null, null);
                }
            }
        } catch (Exception e) {
            LogTool.d(FileTool.TAG, "创建失败：" + e.getMessage());
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Throwable t) {
            }
        }
        return insertUri;
    }

    public void insertAudio(String displayName) {
        ContentResolver resolver = ToolInit.getApplication().getContentResolver();
        Uri audioCollection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }

        ContentValues songDetails = createContentValues(displayName, null, null, null, Environment.DIRECTORY_MUSIC + "/audio", 1);
        Uri songContentUri = resolver.insert(audioCollection, songDetails);

        if (songContentUri != null) {
            try {
                resolver.openFileDescriptor(songContentUri, "w", null).close();
            } catch (IOException e) {
            }

            songDetails.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                songDetails.put(MediaStore.Audio.Media.IS_PENDING, 0);
                resolver.update(songContentUri, songDetails, null, null);
            }
        }
    }

    /**
     * 创建 contentResolver.query 中的两个参数 String selection 和 String[] selectionArgs
     */
    public QuerySelectionStatement buildQuerySelectionStatement(
            @FileGlobalUtil.FileMediaType String mediaType,
            String displayName,
            String description,
            String mimeType,
            String title,
            String relativePath,
            boolean isFuzzy
    ) {
        String symbol = isFuzzy ? " like " : " = ";
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        boolean needAddPre = false;
        if (!TextUtils.isEmpty(displayName)) {
            String columnDisplayName;
            switch (mediaType) {
                case FileGlobalUtil.MEDIA_TYPE_VIDEO:
                    columnDisplayName = MediaStore.Video.Media.DISPLAY_NAME;
                    break;
                case FileGlobalUtil.MEDIA_TYPE_AUDIO:
                    columnDisplayName = MediaStore.Audio.Media.DISPLAY_NAME;
                    break;
                default:
                    columnDisplayName = MediaStore.Images.Media.DISPLAY_NAME;
                    break;
            }
            selection.append(" ").append(columnDisplayName).append(symbol).append(" ? ");
            selectionArgs.add(displayName);
            needAddPre = true;
        }
        if (!TextUtils.isEmpty(description) && !FileGlobalUtil.MEDIA_TYPE_AUDIO.equals(mediaType)) {
            String columnDescription;
            if (mediaType.equals(FileGlobalUtil.MEDIA_TYPE_VIDEO)) {
                columnDescription = MediaStore.Video.Media.DESCRIPTION;
            } else {
                columnDescription = MediaStore.Images.Media.DESCRIPTION;
            }
            selection.append(needAddPre ? " and " : " ").append(columnDescription).append(symbol).append(" ? ");
            selectionArgs.add(description);
            needAddPre = true;
        }
        if (title != null && !title.isEmpty()) {
            String columnTitle;
            switch (mediaType) {
                case FileGlobalUtil.MEDIA_TYPE_VIDEO:
                    columnTitle = MediaStore.Video.Media.TITLE;
                    break;
                case FileGlobalUtil.MEDIA_TYPE_AUDIO:
                    columnTitle = MediaStore.Audio.Media.TITLE;
                    break;
                default:
                    columnTitle = MediaStore.Images.Media.TITLE;
                    break;
            }
            selection.append(needAddPre ? " and " : " ").append(columnTitle).append(symbol).append(" ? ");
            selectionArgs.add(title);
            needAddPre = true;
        }
        if (mimeType != null && !mimeType.isEmpty()) {
            String columnMimeType;
            switch (mediaType) {
                case FileGlobalUtil.MEDIA_TYPE_VIDEO:
                    columnMimeType = MediaStore.Video.Media.MIME_TYPE;
                    break;
                case FileGlobalUtil.MEDIA_TYPE_AUDIO:
                    columnMimeType = MediaStore.Audio.Media.MIME_TYPE;
                    break;
                default:
                    columnMimeType = MediaStore.Images.Media.MIME_TYPE;
                    break;
            }
            selection.append(needAddPre ? " and " : " ").append(columnMimeType).append(symbol).append(" ? ");
            selectionArgs.add(mimeType);
            needAddPre = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (relativePath != null && !relativePath.isEmpty()) {
                String columnRelativePath;
                switch (mediaType) {
                    case FileGlobalUtil.MEDIA_TYPE_VIDEO:
                        columnRelativePath = MediaStore.Video.Media.RELATIVE_PATH;
                        break;
                    case FileGlobalUtil.MEDIA_TYPE_AUDIO:
                        columnRelativePath = MediaStore.Audio.Media.RELATIVE_PATH;
                        break;
                    default:
                        columnRelativePath = MediaStore.Images.Media.RELATIVE_PATH;
                        break;
                }
                selection.append(needAddPre ? " and " : " ").append(columnRelativePath).append(symbol).append(" ? ");
                selectionArgs.add(relativePath);
            }
        }

        LogTool.i(FileTool.TAG, "查询语句= " + selection);
        return new QuerySelectionStatement(selection, selectionArgs, needAddPre);
    }

    public Cursor createMediaCursor(Uri uri, String[] projectionArgs, String sortOrder, QuerySelectionStatement querySelectionStatement) {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your app didn't create.
        switch (ToolInit.getApplicationContext().checkUriPermission(uri, android.os.Process.myPid(), android.os.Process.myUid(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION)) {
            case PackageManager.PERMISSION_GRANTED:
                break;
            case PackageManager.PERMISSION_DENIED:
                ToolInit.getApplicationContext().grantUriPermission(ToolInit.getApplication().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                break;
        }

        return ToolInit.getApplicationContext().getContentResolver().query(
                uri, projectionArgs,
                querySelectionStatement != null ? querySelectionStatement.getSelection().toString() : null,
                querySelectionStatement != null ? querySelectionStatement.getSelectionArgs().toArray(new String[0]) : null,
                sortOrder
        );
    }

    public List<MediaStoreVideo> queryMediaStoreVideo(String[] projectionArgs, String sortOrder, long sourceDuration, TimeUnit sourceUnit) {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your app didn't create.

        // Container for information about each video.
        List<MediaStoreVideo> videoList = new ArrayList<>();
        Uri external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        QuerySelectionStatement queryStatement = buildQuerySelectionStatement(
                FileGlobalUtil.MEDIA_TYPE_VIDEO, null, null, null, null, null, false
        );

        // Show only videos that are at least 5 minutes in duration.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            queryStatement.append(MediaStore.Video.Media.DURATION + " >= ? ",
                    String.valueOf(TimeUnit.MILLISECONDS.convert(sourceDuration, sourceUnit)));
        }

        try (Cursor cursor = createMediaCursor(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projectionArgs, sortOrder, queryStatement)) {
            if (cursor != null) {
                // Cache column indices.
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int durationColumn = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ?
                        cursor.getColumnIndex(MediaStore.Video.Media.DURATION) : 0;
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    int size = cursor.getInt(sizeColumn);

                    Uri contentUri = ContentUris.withAppendedId(external, id);
                    // Stores column values and the contentUri in a local object that represents the media file.
                    videoList.add(new MediaStoreVideo(id, contentUri, name, (long) duration, (long) size));
                }
            }
        }
        return videoList;
    }

    public Uri queryMediaStoreImages(String displayName) {
        return queryMediaStoreImages(displayName, false);
    }

    public Uri queryMediaStoreImages(String displayName, boolean isFuzzy) {
        List<MediaStoreImage> images = queryMediaStoreImages(
                displayName,
                null, null, null, null, isFuzzy
        );
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(0).getUri();
    }

    public List<MediaStoreImage> queryMediaStoreImages(String sortOrder, String displayName,
                                                       String description, String mimeType,
                                                       String title, String relativePath,
                                                       boolean isFuzzy) {
        return queryMediaStoreImages(new String[]{MediaStore.Images.Media._ID}, sortOrder, displayName,
                description, mimeType, title, relativePath,
                isFuzzy);
    }

    public List<MediaStoreImage> queryMediaStoreImages(String displayName,
                                                       String description, String mimeType, String title,
                                                       String relativePath, boolean isFuzzy) {
        return queryMediaStoreImages(null, displayName,
                description, mimeType, title, relativePath,
                isFuzzy);
    }

    public List<MediaStoreImage> queryMediaStoreImages(String[] projectionArgs, String sortOrder, String displayName,
                                                       String description, String mimeType, String title, String relativePath, boolean isFuzzy) {
        // Need the READ_EXTERNAL_STORAGE permission if accessing image files that your app didn't create.

        // Container for information about each image.
        List<MediaStoreImage> imageList = new ArrayList<>();
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        QuerySelectionStatement queryStatement = buildQuerySelectionStatement(
                FileGlobalUtil.MEDIA_TYPE_IMAGE, displayName, description, mimeType, title, relativePath, isFuzzy
        );

        try (Cursor cursor = createMediaCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionArgs, sortOrder, queryStatement)) {
            if (cursor != null) {
                // Cache column indices.
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int descColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                while (cursor.moveToNext()) {
                    // Get values of columns for a given image.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(displayNameColumn);
                    int size = cursor.getInt(sizeColumn);
                    String desc = cursor.getString(descColumn);
                    String titleRs = cursor.getString(titleColumn);
                    String mimeTypeRs = cursor.getString(mimeColumn);
                    Date dateModified = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)));

                    Uri contentUri = ContentUris.withAppendedId(external, id);
                    // Stores column values and the contentUri in a local object that represents the media file.
                    imageList.add(new MediaStoreImage(id, contentUri, name, (long) size, desc, titleRs, mimeTypeRs, dateModified));
                }
            }
        }
        return imageList;
    }

    /**
     * 查询全部图片
     */
    public List<MediaStoreImage> queryMediaStoreImages() {
        QuerySelectionStatement queryStatement = buildQuerySelectionStatement(
                FileGlobalUtil.MEDIA_TYPE_IMAGE, null, null, null, null, null, true
        );
        return queryMediaStoreImages(null, null, queryStatement);
    }

    /**
     * 加载媒体文件的集合 👉 ContentResolver.query
     * <pre>
     * 官方指南 👉 内容提供程序基础知识
     * https://developer.android.com/guide/topics/providers/content-provider-basics?hl=zh-cn
     * </pre>
     * 注意事项:
     * 1.多次测试表明 displayName/description/mimeType 可以 作为 and 多条件查询,而其他的字段则会干扰查询结果
     * 2.like 模糊查询,忽略文件名的大小写 ;  =  字段值必须完全一致
     */
    public ArrayList<MediaStoreImage> queryMediaStoreImages(String[] projectionArgs, String sortOrder, QuerySelectionStatement querySelectionStatement) {
        ArrayList<MediaStoreImage> imageList = new ArrayList<>();
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = null;
        try {
            cursor = createMediaCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionArgs, sortOrder, querySelectionStatement);
            if (cursor != null) {
                LogTool.i(FileTool.TAG, "Found " + cursor.getCount() + " images");
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int descColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int size = cursor.getInt(sizeColumn);
                    String desc = cursor.getString(descColumn);
                    String titleRs = cursor.getString(titleColumn);
                    String mimeTypeRs = cursor.getString(mimeColumn);
                    Date dateModified = new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)));

                    Uri contentUri = ContentUris.withAppendedId(external, id);
                    imageList.add(new MediaStoreImage(id, contentUri, name, (long) size, desc, titleRs, mimeTypeRs, dateModified));
                }

                if (imageList.isEmpty()) {
                    LogTool.e(FileTool.TAG, "查询失败!");
                }

                for (MediaStoreImage img : imageList) {
                    LogTool.d(FileTool.TAG, "查询成功，Uri路径  " + img.getUri());
                }
            }
        } catch (Exception e) {
            LogTool.e(FileTool.TAG, "查询失败! " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return imageList;
    }

    // Storage Access Framework (SAF) 👉 https://developer.android.google.cn/training/data-storage/shared/documents-files
    public boolean checkUriColumnFlag(Uri uri, int flag) {
        ContentResolver contentResolver = ToolInit.getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnFlags = cursor.getInt(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_FLAGS));
            LogTool.i("Column Flags: " + columnFlags + "  Flag: " + flag);
            if (columnFlags >= flag) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * 选择一个图片文件
     */
    public void selectImage(Activity activity, int requestCode) {
        selectFile(activity, "image/*", requestCode);
    }

    /**
     * 选择一个文件
     */
    public void selectFile(Activity activity, String mimeType, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * ### 新建文件 SAF
     *
     * `mimeType 和 fileName 传反了 👉 android.content.ActivityNotFoundException: No Activity found to handle Intent`
     */
    public void createFile(Activity activity, Uri pickerInitialUri, String fileName, String mimeType, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * ### 打开文件 SAF
     * ```
     * 请注意以下事项：
     *      1.当应用触发 ACTION_OPEN_DOCUMENT Intent 时，该 Intent 会启动选择器，以显示所有匹配的文档提供程序。
     *      2.在 Intent 中添加 CATEGORY_OPENABLE 类别可对结果进行过滤，从而只显示可打开的文档（如图片文件）。
     *      3.intent.setType("image/ *") 语句可做进一步过滤，从而只显示 MIME 数据类型为图像的文档。
     * ```
     */
    public void openFile(Activity activity, Uri pickerInitialUri, String mimeType, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * ### 打开目录 SAF
     *
     * 接收数据 :
     *
     * ```kotlin
     * override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
     *      if (requestCode == your-request-code && resultCode == Activity.RESULT_OK) {
     *          // The result data contains a URI for the document or directory that the user selected.
     *          resultData?.data?.also { uri ->
     *              // Perform operations on the document using its URI.
     *          }
     *      }
     * }
     * ```
     */
    public void openDirectory(Activity activity, Uri pickerInitialUri, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 移动文件 SAF
     */
    public void moveFile(Uri sourceDocumentUri, Uri sourceParentDocumentUri, Uri targetParentDocumentUri) {
        if (checkUriColumnFlag(sourceDocumentUri, DocumentsContract.Document.FLAG_SUPPORTS_MOVE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    DocumentsContract.moveDocument(ToolInit.getApplicationContext().getContentResolver(),
                            sourceDocumentUri,
                            sourceParentDocumentUri,
                            targetParentDocumentUri);
                } catch (FileNotFoundException e) {
                    LogTool.e(e.getMessage());
                }
            }
        }
    }

    /**
     * 删除文件 SAF
     */
    public boolean deleteFile(Uri uri) {
        if (checkUriColumnFlag(uri, DocumentsContract.Document.FLAG_SUPPORTS_DELETE)) {
            try {
                return DocumentsContract.deleteDocument(ToolInit.getApplicationContext().getContentResolver(), uri);
            } catch (FileNotFoundException e) {
                LogTool.e(e.getMessage());
            }
        }
        return false;
    }

    /**
     * ### 重命名文件 SAF
     *
     * ```
     * 注意重名文件
     *
     * 对同一Uri对应的文件重命名不能重复，新旧名相同会报错 java.lang.IllegalStateException: File already exists
     * 因此先判断比对旧Uri对应的文件名是否和 newDisplayName 是否相同
     * ```
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RenameResult renameFile(Uri uri, String newDisplayName) {
        if (!checkUriColumnFlag(uri, DocumentsContract.Document.FLAG_SUPPORTS_RENAME)) {
            return new RenameResult(false, "重命名失败");
        }

        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex > 0) {
                    String displayName = cursor.getString(columnIndex);
                    if (!displayName.equalsIgnoreCase(newDisplayName)) {
                        DocumentsContract.renameDocument(contentResolver, uri, newDisplayName != null ? newDisplayName : "");
                    }
                    return new RenameResult(true, "重命名成功");
                }
            }
        } catch (Exception e) {
            LogTool.e(e.getMessage());
            return new RenameResult(false, "已存在该名称的文件");
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
        }
        return new RenameResult(false, "重命名失败");
    }

    /**
     * 照片的位置信息
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public float[] getMediaLocation(Uri uri) {
        Uri photoUri = MediaStore.setRequireOriginal(uri);
        boolean hasPermission = FileTool.get().getFileGlobalUtil().judgeHasPermission(uri);
        if (hasPermission) {
            try {
                InputStream inputStream = ToolInit.getApplicationContext().getContentResolver().openInputStream(photoUri);
                if (inputStream != null) {
                    ExifInterface exifInterface = new ExifInterface(inputStream);
                    float[] lanLonArr = new float[2];
                    boolean isGet = exifInterface.getLatLong(lanLonArr);
                    if (isGet) {
                        return lanLonArr;
                    } else {
                        return new float[]{0f, 0f};
                    }
                }
            } catch (Exception e) {
                LogTool.e(FileTool.TAG, e.getMessage());
            }
        }
        return new float[]{0f, 0f};
    }

    /**
     * 通过Uri获取Bitmap,耗时操作不应该在主线程
     *
     * https://developer.android.google.cn/training/data-storage/shared/documents-files#bitmap
     *
     * Note: You should complete this operation on a background thread, not the UI thread.
     */
    public Bitmap getBitmapFromUri(Uri uri) {
        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, FileGlobalUtil.MODE_READ_ONLY);
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                return BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (FileNotFoundException e) {
            LogTool.e(FileTool.TAG, e.getMessage());
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * ### 读取文档信息
     *
     * https://developer.android.google.cn/training/data-storage/shared/documents-files#input_stream
     */
    public String readTextFromUri(Uri uri) {
        boolean hasPermission = FileTool.get().getFileGlobalUtil().judgeHasPermission(uri);
        if (!hasPermission) {
            return "";
        }
        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();
        StringBuilder sb = new StringBuilder();

        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream != null) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = reader.readLine();
                    }
                } finally {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 编辑文档
     */
    public void writeTextToUri(Uri uri, String text) {
        if (text == null || text.trim().isEmpty() || !checkUriColumnFlag(uri, DocumentsContract.Document.FLAG_SUPPORTS_WRITE)) {
            return;
        }

        boolean hasPermission = FileTool.get().getFileGlobalUtil().judgeHasPermission(uri);
        if (!hasPermission) {
            return;
        }

        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        ParcelFileDescriptor parcelFileDescriptor = null;
        FileOutputStream fos = null;
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, FileGlobalUtil.MODE_WRITE_ONLY_ERASING);
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                fos = new FileOutputStream(fileDescriptor);
                fos.write(text.getBytes());
            }
        } catch (IOException e) {
            LogTool.e(FileTool.TAG, "writeTextToUri Failed : " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                LogTool.e(FileTool.TAG, "writeTextToUri Failed : " + e.getMessage());
            }
        }
    }

    /**
     * 加载媒体 单个媒体文件的缩略图 👉 ContentResolver.loadThumbnail
     * <p>
     * ContentResolver.loadThumbnail,传入size，返回指定大小的缩略图
     */
    public Bitmap loadThumbnail(Uri uri, int width, int height) {
        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (uri != null) {
                    return contentResolver.loadThumbnail(uri, new Size(width, height), null);
                }
            }
        } catch (IOException e) {
            LogTool.e(FileTool.TAG, "loadThumbnail Failed : " + e.getMessage());
        }

        return null;
    }

    /**
     * 权限 Manifest.permission.READ_EXTERNAL_STORAGE
     * <pre>
     *     1.只有在删除非当前APP的应用 图片时候才会触发 RecoverableSecurityException
     *     2.重复删除同一uri对应的文件,会出现  java.lang.SecurityException: com.xxx.sample has no access to content://media/external/images/media/353235
     *     3.如果删除的是整个目录中的文件(eg:MediaStore.Images.Media.EXTERNAL_CONTENT_URI),系统会在数据库Table中记录当前应用创建文件时的信息,
     *       此时用户执行操作"系统设置->应用信息->存储->删除数据"会把应用的数据全部删除,Table信息也会被删除. 这样会导致使用 ContentResolver.delete(uri) 做删除时
     *       不能删除之前创建的文件,因此建议采用 SAF 方式做清空目录操作
     */
    public boolean deleteUri(Activity activity, Uri uri, String where, String[] selectionArgs, int requestCode) {
        int delete = 0;
        try {
            // 删除失败 -> 重复删除同一 Uri 对应的文件!
            if (!FileTool.get().getFileUriUtil().checkUri(uri)) {
                return false;
            }

            delete = ToolInit.getApplicationContext().getContentResolver().delete(uri, where, selectionArgs);
            LogTool.d(FileTool.TAG, "删除结果 " + uri + " " + delete);
        } catch (SecurityException e1) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // 更新其他应用的媒体文件，如果应用使用分区存储，它通常无法更新其他应用存放到媒体存储中的媒体文件。
                    // 不过，仍然可以通过捕获平台抛出的 RecoverableSecurityException 来征得用户同意以修改文件。
                    if (e1 instanceof RecoverableSecurityException) {
                        RecoverableSecurityException recoverableSecurityException = (RecoverableSecurityException) e1;
                        IntentSender requestAccessIntentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                        activity.startIntentSenderForResult(requestAccessIntentSender, requestCode, null, 0, 0, 0, null);
                    } else {
                        throw e1;
                    }
                } else {
                    LogTool.e(FileTool.TAG, "低于Q版本 " + e1.getMessage());
                }
            } catch (IntentSender.SendIntentException e2) {
                LogTool.e(FileTool.TAG, "delete Fail e2 " + uri + "  " + e2.getMessage());
            }
        }
        return delete != -1;
    }

    public boolean deleteUri(Activity activity, Uri uri, int requestCode) {
        return deleteUri(activity, uri, null, null, requestCode);
    }

    public boolean deleteUriDirectory(Activity activity, int requestCode, String mediaType) {
        Uri uri;
        switch (mediaType) {
            case FileGlobalUtil.MEDIA_TYPE_AUDIO:
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            case FileGlobalUtil.MEDIA_TYPE_VIDEO:
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
        }
        return deleteUri(activity, uri, null, null, requestCode);
    }

    public boolean deleteUriMediaStoreImage(Activity activity, MediaStoreImage mediaImage, int requestCode) {
        String[] selectionArgs = {String.valueOf(mediaImage.getId())};
        return deleteUri(activity, mediaImage.getUri(), MediaStore.Images.Media._ID + " = ?", selectionArgs, requestCode);
    }


    /**
     * 获取虚拟文件的输入流,需要传入想要的 mimeType
     * <p>
     * https://developer.android.google.cn/training/data-storage/shared/documents-files#open-virtual-file
     */
    public InputStream getInputStreamForVirtualFile(Uri uri, String mimeTypeFilter) {
        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        String[] openableMimeTypes = contentResolver.getStreamTypes(uri, mimeTypeFilter);
        if (openableMimeTypes != null && openableMimeTypes.length > 0) {
            try {
                AssetFileDescriptor assetFileDescriptor = contentResolver.openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null);
                if (assetFileDescriptor != null) {
                    return assetFileDescriptor.createInputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LogTool.e(FileTool.TAG, "文件未找到!");
        }

        return null;
    }

    /**
     * 判断是否为虚拟文件
     * <p>
     *     https://developer.android.google.cn/training/data-storage/shared/documents-files#open-virtual-file
     */
    @RequiresApi(Build.VERSION_CODES.N)
    public boolean isVirtualFile(Uri uri) {
        Context context = ToolInit.getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();

        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false;
        }

        String[] projection = {DocumentsContract.Document.COLUMN_FLAGS};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int flags = cursor.getInt(0);
                    return (flags & DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT) != 0;
                }
            } finally {
                cursor.close();
            }
        }

        return false;
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    public void testQueryMediaVideoByUri() {
        String[] projectionArgs = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };

        // Display videos in alphabetical order based on their display name.
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
        List<MediaStoreVideo> videoList = queryMediaStoreVideo(projectionArgs, sortOrder, 5L, TimeUnit.MINUTES);
        if (videoList != null) {
            for (MediaStoreVideo video : videoList) {
                LogTool.i(FileTool.TAG, "视频列表: " + video.toString());
            }
        }
    }

    /**
     * 获取媒体文件的"拍摄时间" (Get the "shooting time" of the media file)
     *
     * 【注】获取拍摄时间优先级: 图片(ExifInterface) ; 视频,音频(MediaMetadataRetriever) ; 最后如果前两者都没获取到时间, 则使用文件最后修改时间(lastModified)
     *
     * 【Note】Get the shooting time priority: Picture (ExifInterface); Video, Audio (MediaMetadataRetriever); Finally, if the first two do not get the time, use the last modified time of the file (lastModified)
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getMediaShotTime(Uri uri) {
        if (uri == null) {
            return -1;
        }

        // 直接使用 File(mediaFile.path) 获取不到信息 (No information can be obtained directly using File(mediaFile.path))
        // eg: /storage/emulated/0/Movies/VID_20210621_17180117.mp4 true false 1624267109000 ;
        // isFile=false isDirectory=false  lastModified=0
        String pathByUri = FileTool.get().getFileUriUtil().getPathByUri(uri);
        if (TextUtils.isEmpty(pathByUri)) {
            return -1;
        }
        File fileReal = new File(pathByUri);
        if (!fileReal.exists() || fileReal.isDirectory()) {
            return -1;
        }

        // 如果 ExifInterface 或 MediaMetadataRetriever 没有获取到时间,使用 lastModified 时间
        // If ExifInterface or MediaMetadataRetriever does not get the time, use the lastModified time
        long fileLastModifiedTime = fileReal.lastModified();
        if (fileLastModifiedTime <= 0) {
            fileLastModifiedTime = System.currentTimeMillis();
        }

        LogTool.d(FileTool.TAG, "isFile=" + fileReal.isFile() +
                " isDirectory=" + fileReal.isDirectory() +
                " lastModified=" + fileLastModifiedTime);

        // 注意:先用 ExifInterface , 后用 MediaMetadataRetriever (Note: Use ExifInterface first, then MediaMetadataRetriever)
        // 如果给把图片的 Uri 交给 MediaMetadataRetriever 处理会报错: setDataSource failed: status = 0x80000000
        // If the Uri of the picture is handed over to MediaMetadataRetriever for processing,
        // an error will be reported: setDataSource failed: status = 0x80000000
        try {
            InputStream inputStream = ToolInit.getApplication().getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return fileLastModifiedTime;
            }
            ExifInterface exifInterface = new ExifInterface(inputStream);
            String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            long modifiedTime;

            // 图片(Image)
            // longitude = 0/1,0/1,0/1
            // latitude=0/1,0/1,0/1
            // device_type=NEX 3 5G
            // dateTime=2021:07:12 14:36:30
            // dateTimeOriginal=2021:07:12 14:36:30
            // dateTimeDigitized=2021:07:12 14:36:30
            if (!TextUtils.isEmpty(dateTime)) {
                // 1.视频,音频 (Video, audio)
                // ExifInterface 获取到的 ExifInterface.TAG_DATETIME 返回 null, 使用 MediaMetadataRetriever 重新获取
                // ExifInterface.TAG_DATETIME obtained by ExifInterface returns null, use MediaMetadataRetriever to
                // get it again
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                try {
                    mmr.setDataSource(ToolInit.getApplication(), uri);
                    // 获取媒体的日期(Date the media was acquired): "20210708T070344.000Z"
                    String dateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
                    modifiedTime = formatMediaMetadataKeyDate(dateString).getTime();
                } catch (Exception e) {
                    LogTool.e(FileTool.TAG, "getMediaShotTime: " + e.getMessage());
                    modifiedTime = fileLastModifiedTime;
                } finally {
                    mmr.release();
                }
            } else {
                // 2.图片(Image)  ExifInterface.TAG_DATETIME  dateTime=2021:07:12 14:36:30
                modifiedTime =
                        new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).parse(dateTime).getTime();
            }
            return modifiedTime;
        } catch (Throwable t) {
            LogTool.e(FileTool.TAG, "getMediaShotTime by uri: " + t.getMessage());
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getMediaShotTime(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }
        try {
            File fileReal = new File(path);
            if (!fileReal.exists() || fileReal.isDirectory()) {
                return -1;
            }
            Uri uri = FileTool.get().getFileUriUtil().getUriByPath(path);
            if (uri == null) {
                return -1;
            }
            return getMediaShotTime(uri);
        } catch (Throwable t) {
            LogTool.e(FileTool.TAG, "getMediaShotTime by path: " + t.getMessage());
        }
        return -1;
    }

    /**
     * "20210708T070344.000Z" 👉 Date()
     *
     * 转换 MediaMetadataRetriever.METADATA_KEY_DATE 特殊的时间格式
     * (Convert MediaMetadataRetriever.METADATA_KEY_DATE to special time format)
     *
     * > Thanks
     *
     * https://stackoverflow.com/questions/38648437/android-mediametadataretriever-metadata-key-date-gives-only-date-of-video-on-gal/39828238
     *
     * https://blog.csdn.net/qq_31332467/article/details/79166945
     *
     * @param date "20210708T070344.000Z"
     * @return Date Object
     */
    public Date formatMediaMetadataKeyDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return null;
        }

        Date inputDate = null;
        try {
            inputDate =
                    new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS Z", Locale.getDefault())
                            .parse(date.replace("Z", " " + "UTC"));
        } catch (Exception e) {
            LogTool.w(FileTool.TAG, "error parsing date: " + e);
            try {
                inputDate = new SimpleDateFormat("yyyy MM dd.SSS Z", Locale.getDefault())
                        .parse(date.replace("Z", " UTC"));
                return null;
            } catch (Exception ex) {
                LogTool.e(FileTool.TAG, "error parsing date: " + ex);
            }
        }
        if (inputDate != null) {
            LogTool.i(FileTool.TAG, "formatMediaMetadataKeyDate: " + inputDate.getTime());
        }
        return inputDate;
    }

    /**
     * 查找 bucketId 对应的媒体文件的时间信息(Find the time information of the media file corresponding to bucketId)
     *
     * @param targetBucketId Media File bucketId
     * @return dateAdded, dateModified, dateExpires
     */
    public MediaInfoBean getMediaShotTime(long targetBucketId) {
        // https://blog.csdn.net/ifmylove2011/article/details/51425921
        // https://stackoverflow.com/questions/64933336/android-mediastore-files-getcontenturi-how-to-get-a-folder-internal-to-the-app
        String[] projection = new String[]{
                MediaStore.Files.FileColumns._ID,
                "bucket_id",
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                "date_expires",
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

        Cursor cursor = ToolInit.getApplication().getContentResolver().query(
                MediaStore.Files.getContentUri("external"), projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor == null) {
            return null;
        }

        int bucketIdColumn = cursor.getColumnIndex("bucket_id");
        int dateAddedColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);
        int dateModifiedColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
        int dateExpiresColumn = cursor.getColumnIndex("date_expires");
        int dateTakenColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);

        while (cursor.moveToNext()) {
            // moveToFirst
            long bucketId = cursor.getLong(bucketIdColumn);
            if (bucketId == targetBucketId) {
                long dateAdded = cursor.getLong(dateAddedColumn);
                long dateModified = cursor.getLong(dateModifiedColumn);
                long dateExpires = cursor.getLong(dateExpiresColumn);
                long dateTaken = cursor.getLong(dateTakenColumn);

                /*
                   注: dateModified 才是照片的真正拍摄时间, 而 dateAdded 是把文件重命名后的或者其他操作修改后的时间... 感觉Android把这俩时间搞反了
                   dateModified DATE_MODIFIED 1657785037 2022-07-14 15:50:37
                   dateAdded    DATE_ADDED    1657786785 2022-07-14 16:19:45
                   bucketId=-1739773001 ; dateAdded=1657786785 ;
                   dateModified=1657785037 ; dateExpires=0 ; dateTaken=1657785037173
                 */
                LogTool.i(FileTool.TAG,
                        "bucketId=" + bucketId +
                                " dateAdded=" + dateAdded +
                                " dateModified=" + dateModified +
                                " dateExpires=" + dateExpires +
                                " dateTaken=" + dateTaken
                );
                return new MediaInfoBean(dateAdded, dateModified, dateExpires, dateTaken);
            }
        }
        return null;
    }
}