package com.pizza.tools.file.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.pizza.tools.file.model.SplitResults;
import com.pizza.tools.log.LogTool;
import com.pizza.tools.DataTool;
import com.pizza.tools.EncodeTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.file.FileTool;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Kyle
 * 2023/8/24 09:58
 *
 */
public class FileOperatorUtil {

    /**
     * 获取目录下所有符合filter的文件
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public List<File> listFilesFilterInDir(File dir, FilenameFilter filter, boolean isRecursive) {
        if (isRecursive) {
            return listFilesFilter(dir, filter);
        }
        if (!FileTool.get().getFileGlobalUtil().isDir(dir)) {
            return null;
        }
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (filter.accept(file.getParentFile(), file.getName())) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 获取目录下所有符合filter的文件包括子目录
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    public List<File> listFilesFilter(File dir, FilenameFilter filter) {
        if (FileTool.get().getFileGlobalUtil().isDir(dir)) {
            return null;
        }
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (filter.accept(file.getParentFile(), file.getName())) {
                list.add(file);
            }
            if (file.isDirectory()) {
                list.addAll(listFilesFilter(file, filter));
            }
        }
        return list;
    }

    public String file2Base64(String filePath) {
        FileInputStream fis = null;
        String base64String = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024 * 100];
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        base64String = EncodeTool.base64Encode2String(bos.toByteArray(), Base64.NO_WRAP);
        return base64String;
    }

    /**
     * 修改文件的后缀
     *
     * eg:  changeFileExtension(originName = "/xxx/xxx/test.txt", '.', "jpeg")
     */
    public String changeFileExtension(String pathOrName, char split, String newExtension) {
        if (pathOrName == null || pathOrName.isEmpty()) {
            return "";
        }

        int dot = pathOrName.lastIndexOf(split);
        if (dot != -1) {
            int endIndex = dot + 1;
            return pathOrName.substring(0, endIndex).toLowerCase(Locale.getDefault()) + newExtension;
        }

        return "";
    }

    /**
     * ### 路径分割
     *
     * ```
     * eg:
     * srcPath=/storage/emulated/0/Movies/myVideo.mp4  path=/storage/emulated/0/Movies name=myVideo suffix=mp4 nameSuffix=myVideo.mp4
     *
     * /xxx/xxx/note.txt ->  path: /xxx/xxx   name: note   suffix: txt
     * ///note.txt       ->  path: ///        name: note   suffix: txt
     * /note.txt         ->  path: ""         name: note   suffix: txt
     * note.txt          ->  path: ""         name: note   suffix: txt
     * ```
     */
    public SplitResults splitFilePath(String srcPath, char nameSplit, char suffixSplit) {
        if (TextUtils.isEmpty(srcPath)) {
            return null;
        }

        int cut = srcPath.lastIndexOf(nameSplit);
        String path = cut == -1 ? "" : srcPath.substring(0, cut);
        String nameSuffix = cut == -1 ? srcPath : srcPath.substring(cut + 1);

        int dot = nameSuffix.lastIndexOf(suffixSplit);
        if (dot != -1) {
            String suffix = nameSuffix.substring(dot + 1).toLowerCase(Locale.getDefault());
            String name = nameSuffix.substring(0, dot);
            return new SplitResults(path, name, suffix, nameSuffix);
        }
        return null;
    }

    public SplitResults splitFilePath(String srcPath) {
        return splitFilePath(srcPath, '/', '.');
    }

    /**
     * abc.jpg -> abc
     */
    public String getFileNameNoSuffix(String path) {
        String nameNoSuffix = path;
        SplitResults splitResults = splitFilePath(path);
        if (splitResults != null) {
            nameNoSuffix = splitResults.getName();
        }
        return nameNoSuffix;
    }

    /**
     * abc.jpg -> jpg
     */
    public String getFileNameSuffix(String path) {
        String nameSuffix = path;
        SplitResults splitResults = splitFilePath(path);
        if (splitResults != null) {
            nameSuffix = splitResults.getSuffix();
        }
        return nameSuffix;
    }

    /**
     * /storage/emulated/0/Movies/myVideo.mp4  ->  /storage/emulated/0/Movies
     */
    public String getFilePathFromFullPath(String path, char split) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        int cut = path.lastIndexOf(split);
        if (cut != -1) {
            return path.substring(0, cut);
        }

        return path;
    }

    public String getFilePathFromFullPath(String path) {
        return getFilePathFromFullPath(path, '/');
    }

    /**
     * /storage/emulated/0/Movies/myVideo.mp4  ->  myVideo.mp4
     */
    public String getFileNameFromPath(String path, char split) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        int cut = path.lastIndexOf(split);
        if (cut != -1) {
            return path.substring(cut + 1);
        }

        return path;
    }

    public String getFileNameFromPath(String path) {
        return getFileNameFromPath(path, '/');
    }

    /**
     * /storage/emulated/0/Movies/myVideo.mp4  ->  myVideo.mp4
     */
    public String getFileNameFromUri(Uri uri) {
        boolean b = FileTool.get().getFileGlobalUtil().judgeHasPermission(uri);
        if (!b) {
            return "";
        }
        String filename = null;
        ContentResolver resolver = ToolInit.getApplicationContext().getContentResolver();
        String mimeType = resolver.getType(uri);

        if (mimeType == null) {
            filename = getFileNameFromPath(FileTool.get().getFileUriUtil().getPathByUri(uri));
        } else {
            Cursor query = resolver.query(uri, null, null, null, null);
            if (query != null) {
                int nameIndex = query.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (query.moveToFirst()) {
                    filename = query.getString(nameIndex);
                }
            }
        }
        if (ToolInit.isDebug()) {
            LogTool.i(FileTool.TAG, "getFileNameFromUri: " + mimeType + " " + filename);
        }
        return filename;
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 文件行链表
     */
    public List<String> readFile2List(File file, String charsetName) {
        return readFile2List(file, 0, 0x7FFFFFFF, charsetName);
    }

    /**
     * 指定编码按行读取文件到List
     *
     * @param file        文件
     * @param st          需要读取的开始行数
     * @param end         需要读取的结束行数
     * @param charsetName 编码格式
     * @return 包含从start行到end行的list
     */
    public List<String> readFile2List(File file, int st, int end, String charsetName) {
        if (file == null) {
            return null;
        }
        if (st > end) {
            return null;
        }
        BufferedReader reader = null;
        try {
            String line;
            int curLine = 1;
            List<String> list = new ArrayList<>();
            if (DataTool.isNullString(charsetName)) {
                reader = new BufferedReader(new FileReader(file));
            } else {
                InputStreamReader inputStreamReader;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    inputStreamReader = new InputStreamReader(Files.newInputStream(file.toPath()), charsetName);
                } else {
                    inputStreamReader = new InputStreamReader(new FileInputStream(file), charsetName);
                }
                reader = new BufferedReader(inputStreamReader);
            }
            while ((line = reader.readLine()) != null) {
                if (curLine > end) {
                    break;
                }
                if (st <= curLine && curLine <= end) {
                    list.add(line);
                }
                ++curLine;
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串
     */
    public String readFileString(String filePath, String charsetName) {
        return readFileString(FileTool.get().getFileByPath(filePath), charsetName);
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串
     */
    public String readFileString(File file, String charsetName) {
        if (file == null) {
            return "";
        }
        if (!FileTool.get().getFileGlobalUtil().isFileExists(file)) {
            return "";
        }

        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (DataTool.isNullString(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\r\n");// windows系统换行为\r\n，Linux为\n
            }
            if (sb.length() > 2) {
                // 要去除最后的换行符
                return sb.replace(sb.length() - 2, sb.length(), "").toString();
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            closeIo(reader);
        }
    }

    /**
     * 读取文本文件中的内容
     *
     * Read the contents of the text file
     */
    public String readFileText(InputStream stream) {
        if (stream == null) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
            reader.close();
            stream.close();
        } catch (Exception e) {
            LogTool.e(FileTool.TAG, e.getMessage());
        }
        return content.toString();
    }

    public String readFileText(Uri uri) {
        if (uri != null) {
            try {
                InputStream stream = ToolInit.getApplicationContext().getContentResolver().openInputStream(uri);
                return readFileText(stream);
            } catch (Exception e) {
                LogTool.e(FileTool.TAG, e.getMessage());
            }
        }
        return null;
    }

    public byte[] readFileBytes(InputStream stream) {
        if (stream != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                byte[] byteArray = buffer.toByteArray();
                buffer.close();
                return byteArray;
            } catch (IOException e) {
                LogTool.e(FileTool.TAG, "readFileBytes: " + e.getMessage());
            }
        }
        return null;
    }

    public byte[] readFileBytes(Uri uri) {
        if (uri != null) {
            try {
                InputStream stream = ToolInit.getApplicationContext().getContentResolver().openInputStream(uri);
                return readFileBytes(stream);
            } catch (Exception e) {
                LogTool.e(FileTool.TAG, "readFileBytes: " + e.getMessage());
            }
        }
        return null;
    }

    public File createFile(File file, boolean overwrite) {
        if (file == null) {
            return null;
        }
        return createFile(file.getParent(), file.getName(), overwrite);
    }

    /**
     * 创建文件 (Create a file)
     *
     * eg: filePath is getExternalCacheDir() , fileName is xxx.json
     *
     * System path: /mnt/sdcard/Android/data/ando.guard/cache/xxx.json
     */
    public File createFile(String filePath, String fileName, boolean overwrite) {
        if (filePath == null || fileName == null || filePath.isEmpty() || fileName.isEmpty()) {
            return null;
        }
        if (!createDirectory(filePath)) {
            return null;
        }

        File file = new File(filePath, fileName);
        if (file.exists()) {
            if (file.isDirectory()) {
                file.delete();
            }
            if (!overwrite) {
                SplitResults splitResults = splitFilePath(fileName);

                int index = 0;
                while (file.exists()) {
                    index++;
                    file = new File(filePath, splitResults.getName() + "(" + index + ")" + "." + splitResults.getSuffix());
                }
            } else {
                file.delete();
            }
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            LogTool.e(FileTool.TAG, e.toString());
        }
        return file;
    }

    public File createFile(String filePath, String fileName) {
        return createFile(filePath, fileName, false);
    }

    public File createFile(File file) {
        return createFile(file, false);
    }

    public boolean createDirectory(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            return false;
        }
        return createDirectory(new File(dirPath));
    }

    /**
     * 创建目录 (Create a directory)
     */
    public boolean createDirectory(File dirFile) {
        if (dirFile == null) {
            return false;
        }
        if (dirFile.exists()) {
            if (!dirFile.isDirectory()) {
                dirFile.delete();
            } else {
                return true;
            }
        }
        return dirFile.mkdirs();
    }

    /**
     * 将字符串 保存成 文件
     *
     * @param filePath
     * @param content
     */
    public void writeString2File(String filePath, String content) {
        BufferedWriter bw = null;
        try {
            //根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new FileWriter(filePath));
            // 将内容写入文件中
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }

    /**
     * 把 ByteArray 写到 target(File) 中 (Write ByteArray to target(File))
     *
     * eg: /storage/.../xxx.txt
     */
    public File writeBytes2File(byte[] bytes, File target) {
        try {
            FileChannel channel = new FileOutputStream(target).getChannel();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            channel.write(buffer);
            channel.force(true); // 强制刷新
            channel.close();
            LogTool.i(FileTool.TAG, "writeBytesToFile target= " + target.length());
            return target;
        } catch (IOException e) {
            LogTool.e(FileTool.TAG, e.toString());
        }
        return null;
    }

    public void write2File(Bitmap bitmap, String pathAndName, boolean overwrite) {
        if (TextUtils.isEmpty(pathAndName)) {
            return;
        }
        write2File(bitmap, new File(pathAndName), overwrite);
    }

    public void write2File(Bitmap bitmap, String filePath, String fileName, boolean overwrite) {
        if (TextUtils.isEmpty(filePath) && TextUtils.isEmpty(fileName)) {
            return;
        }
        write2File(bitmap, new File(filePath, fileName), overwrite);
    }

    /**
     * Bitmap保存为本地文件 (Save Bitmap as a local file)
     */
    public void write2File(Bitmap bitmap, File file, boolean overwrite) {
        if (file == null) {
            return;
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                file.delete();
            }
            if (overwrite) {
                file.delete();
            } else {
                return;
            }
        }
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            LogTool.e(FileTool.TAG, e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LogTool.e(FileTool.TAG, e.getMessage());
                }
            }
        }
    }

    public File write2File(InputStream input, String pathAndName, boolean overwrite) {
        if (pathAndName == null || pathAndName.isEmpty()) {
            return null;
        }
        return write2File(input, new File(pathAndName), overwrite);
    }

    public File write2File(InputStream input, String filePath, String fileName, boolean overwrite) {
        if (filePath == null || filePath.isEmpty() || fileName == null || fileName.isEmpty()) {
            return null;
        }
        return write2File(input, new File(filePath, fileName), overwrite);
    }

    public File write2File(InputStream input, File file, boolean overwrite) {
        if (file == null) {
            return null;
        }
        File target = null;
        FileOutputStream output = null;
        try {
            File dir = file.getParentFile();
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            if (file.exists() && file.isDirectory()) {
                file.delete();
            }

            if (!file.exists()) {
                file.createNewFile();
            } else {
                // Exist
                if (overwrite) {
                    file.delete();
                } else {
                    if (file.exists()) {
                        target = createFile(file, false);
                    }
                }
            }
            output = !overwrite && target != null ? new FileOutputStream(target) : new FileOutputStream(file);

            byte[] b = new byte[8 * 1024];
            int length;
            while ((length = input.read(b)) != -1) {
                output.write(b, 0, length);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return target != null ? target : file;
    }

    /**
     * 重命名, 会覆盖原文件
     *
     * @param newFileDirectory 新文件路径, 默认不变
     * @param newFileName 新文件名称
     * @param newFileNameSuffix 新文件后缀, 默认不变
     *
     * @return 重命名成功后, 返回新的文件
     */
    public File renameFile(File oldFile, String newFileDirectory, String newFileName, String newFileNameSuffix) {
        String destDirectory = newFileDirectory;
        if (newFileDirectory == null || newFileDirectory.isEmpty()) {
            destDirectory = oldFile.getParent();
        }
        String destFileSuffix = newFileNameSuffix;
        if (newFileNameSuffix == null || newFileNameSuffix.isEmpty()) {
            destFileSuffix = getFileNameSuffix(oldFile.getName());
        }
        File dest = new File(destDirectory, newFileName + "." + destFileSuffix);
        if (dest.exists()) {
            dest.delete();
        }
        return oldFile.renameTo(dest) ? dest : null;
    }

    public File renameFile(File oldFile, String newFileName) {
        return renameFile(oldFile, null, newFileName, null);
    }

    public File renameFile(File oldFile, String newFileDirectory, String newFileName) {
        return renameFile(oldFile, newFileDirectory, newFileName, null);
    }

    /**
     * ### 拷贝文件到指定路径和名称 (Copy the file to the specified path and name)
     *
     * 效率和`kotlin-stdlib-1.4.21.jar`中的`kotlin.io.FilesKt__UtilsKt.copyTo`基本相当
     * ```kotlin
     * fun File.copyTo(target: File, overwrite: Boolean = false, bufferSize: Int = DEFAULT_BUFFER_SIZE): File
     * ```
     * Usage:
     * ```kotlin
     * boolean copyResult = FileUtils.copyFile(fileOld, getExternalFilesDir(null).getPath(), "test.txt");
     * File targetFile = new File(getExternalFilesDir(null).getPath() + "/" + "test.txt");
     * ```
     *
     * @param src 源文件 Source File
     * @param destDirPath 目标文件夹路径(Target file path)
     * @param destFileName 目标文件名称(Target file name)
     * @param overwrite 覆盖目标文件
     */
    public File copyFile(File src, String destDirPath, String destFileName, boolean overwrite) {
        // 如果拷贝的源文件不存在，或者文件夹路径为空，那么直接返回
        if (!src.exists() || TextUtils.isEmpty(destDirPath)) {
            return null;
        }

        if (!createDirectory(destDirPath)) {
            // 如果文件夹创建失败，那么直接返回null
            return null;
        }

        File dest;
        if (overwrite) {
            dest = new File(destDirPath + File.separator + destFileName);
            if (dest.exists()) {
                dest.delete(); // delete file
            }
        } else {
            dest = createFile(destDirPath, destFileName, false);
        }



        try {
            dest.createNewFile();
        } catch (IOException e) {
            LogTool.e(FileTool.TAG, e.toString());
        }
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (srcChannel != null) {
                    srcChannel.close();
                }
                if (dstChannel != null) {
                    dstChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dest;
    }

    public File copyFile(File src, String destFilePath, String destFileName) {
        return copyFile(src, destFilePath, destFileName, true);
    }

    public boolean moveFileOfResult(File src, String destFilePath, String destFileName) {
        return copyFile(src, destFilePath, destFileName) != null;
    }

    public File moveFile(File src, String destFilePath, String destFileName) {
        File file = copyFile(src, destFilePath, destFileName);
        if (file != null) {
            deleteFile(src);
        }
        return file;
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @param isIgnore 单个文件拷贝失败的时候，是否忽略
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    public boolean copyDirOfResult(File srcDir, File destDir, boolean isIgnore) {
        List<String> list = copyDir(srcDir, destDir, isIgnore);
        return list != null && list.isEmpty();
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @param isIgnore 单个文件拷贝失败的时候，是否忽略
     * @return 拷贝失败的文件路径
     */
    public List<String> copyDir(File srcDir, File destDir, boolean isIgnore) {
        List<String> failFilePath = new ArrayList<>();
        if (srcDir == null || destDir == null) {
            return null;
        }
        // 如果目标目录在源目录中则返回false
        // srcPath : sdcard/txz/test
        // destPath: sdcard/txz/test1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) {
            return null;
        }
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return null;
        }
        // 目标目录不存在返回false
        if (!createDirectory(destDir.getPath())) {
            return null;
        }
        File[] files = srcDir.listFiles();
        for (File file : files) {
            String destName = file.getName();
            File oneDestFile = new File(destPath + destName);
            if (file.isFile()) {
                // 如果操作失败返回false
                if (copyFile(file, destPath, destName) == null) {
                    failFilePath.add(file.getAbsolutePath());
                    if (!isIgnore) {
                        return failFilePath;
                    }
                }
            } else if (file.isDirectory()) {
                // 如果操作失败返回false
                if (!copyDirOfResult(file, oneDestFile, isIgnore)) {
                    return failFilePath;
                }
            }
        }
        return failFilePath;
    }

    public boolean moveDirOfResult(File srcDir, File destDir, boolean isIgnore) {
        boolean b = copyDirOfResult(srcDir, destDir, isIgnore);
        if (b) {
            // 如果文件拷贝成功，就把源目录删掉
            deleteFile(srcDir);
        }
        return b;
    }

    /**
     * 移动目录
     * @param srcDir
     * @param destDir
     * @param isIgnore
     * @return
     */
    public List<String> moveDir(File srcDir, File destDir, boolean isIgnore) {
        List<String> list = copyDir(srcDir, destDir, isIgnore);
        if (list != null && list.isEmpty()) {
            // 如果文件拷贝成功，就把源目录删掉
            deleteFile(srcDir);
        }
        return list;
    }

    /**
     * @return 返回0表示删除失败 (EN: Returns 0 to indicate that the deletion failed)
     */
    public int deleteFile(Uri uri) {
        String path = FileTool.get().getFileUriUtil().getPathByUri(uri);
        if (path != null) {
            return deleteFileWithoutExcludeNames(new File(path));
        } else {
            return 0;
        }
    }

    public int deleteFile(String pathAndName) {
        if (pathAndName == null || pathAndName.trim().isEmpty()) {
            return 0;
        } else {
            return deleteFileWithoutExcludeNames(new File(pathAndName));
        }
    }

    /**
     * 删除文件或文件夹
     *
     * Delete files or directories
     *
     * @param file
     * @return 删除`文件/文件夹`数量 (Delete the number of `file folders`)
     */
    public int deleteFile(File file) {
        return deleteFileWithoutExcludeNames(file);
    }

    /**
     * 删除文件或文件夹
     *
     * Delete files or directories
     * <p>
     *     建议异步处理
     *
     * @param file  `文件/文件夹`
     * @param excludeFiles 指定名称的一些`文件/文件夹`不做删除 (Some `files/directory` with specified names are not deleted)
     * @return Int 删除`文件/文件夹`数量 (Delete the number of `file folders`)
     */
    public int deleteFileWithoutExcludeNames(File file, String... excludeFiles) {
        int count = 0;
        if (file == null || !file.exists()) {
            return count;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if ((children == null || children.length == 0) && shouldFileDelete(file, excludeFiles)) {
                if (file.delete()) {
                    count++; // delete directory
                }
            } else {
                int i = 0;
                while (children != null && i < children.length) {
                    count += deleteFileWithoutExcludeNames(children[i]);
                    i++;
                }
            }
        }
        if (excludeFiles == null || excludeFiles.length == 0) {
            if (file.delete()) {
                count++;
            }
        } else {
            if (shouldFileDelete(file, excludeFiles) && file.delete()) {
                count++;
            }
        }
        return count;
    }

    public boolean shouldFileDelete(File file, String... excludeFiles) {
        if (excludeFiles != null && excludeFiles.length > 0) {
            for (String excludeFile : excludeFiles) {
                if (excludeFile != null && excludeFile.equals(file.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean deleteFilesNotDir(Uri uri) {
        String path = FileTool.get().getFileUriUtil().getPathByUri(uri);
        return path != null && deleteFilesNotDir(new File(path));
    }

    public boolean deleteFilesNotDir(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            return false;
        }
        return deleteFilesNotDir(new File(dirPath));
    }

    /**
     * 只删除文件，不删除文件夹 (Only delete files, not folders)
     * 如果 `File(dirPath).isDirectory==false`, 那么将不做后续处理
     * If `File(dirPath).isDirectory==false`, then no subsequent processing will be done
     *
     * @param dir directory path
     */
    public boolean deleteFilesNotDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }

        String[] children = dir.list();
        if (children == null || children.length == 0) {
            return true;
        }

        int len = children.length;
        File child;
        for (int i = 0; i < len; i++) {
            child = new File(dir, children[i]);
            boolean success;
            if (child.isDirectory()) {
                if (child.list() == null || child.list().length == 0) {
                    continue;
                }
                success = deleteFilesNotDir(child);
            } else {
                success = child.delete();
            }
            if (!success) {
                return false;
            }
            if (i == len - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 移除超过指定期限的文件(Remove files older than specified age)
     * eg: 移除超过一个月的文件(Remove files older than a month) maxFileAge=2678400000L
     *
     * @param directoryPath 期限
     */
    public void deleteFilesOutDate(String directoryPath, long maxFileAge) {
        // Used to exemplify deletion of files more than 1 month old
        // Note the L that tells the compiler to interpret the number as a long
        // Get file handle to the directory. In this case the application files dir
        File dir = new File(directoryPath);
        // Obtain list of files in the directory.
        // listFiles() returns a list of File objects to each file found.
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        // Loop through all files
        for (File f : files) {
            // Get the last modified date. Milliseconds since 1970
            long lastModified = f.lastModified();
            // Do stuff here to deal with the file..
            // For instance delete files older than 1 month
            if (lastModified + maxFileAge < System.currentTimeMillis()) {
                f.delete();
            }
        }
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public void closeIo(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 多个文件合并
     *
     * @param outFile
     * @param files
     */
    public void mergeFiles(Context context, File outFile, List<File> files) {
        FileChannel outChannel = null;
        try {
            outChannel = new FileOutputStream(outFile).getChannel();
            for (File f : files) {
                FileChannel fc = new FileInputStream(f).getChannel();
                int bufferSize = 1024 * 8;
                ByteBuffer bb = ByteBuffer.allocate(bufferSize);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            Log.d(FileTool.TAG, "拼接完成");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException ignore) {
            }
        }
    }


}