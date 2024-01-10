package com.pizza.tools.media;

import android.content.Context;
import android.util.Log;

import com.pizza.tools.file.FileTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author BoWei
 * 2023/8/29 12:17
 * 多媒体工具类
 */
public class MediaTool {
    /**
     * 将在线的m3u8替换成本地的m3u8
     *
     * @param context  实体
     * @param file     在线的m3u8
     * @param pathList 本地的ts文件
     * @return
     */
    public static String getNativeM3u(final Context context, File file, List<File> pathList) {
        InputStream in = null;
        int num = 0;
        //需要生成的目标buff
        StringBuffer buffer = new StringBuffer();
        try {
            if (file != null) {
                in = new FileInputStream(file);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 && line.startsWith("http://")) {
                    buffer.append("file:").append(pathList.get(num).getAbsolutePath()).append("\r\n");
                    num++;
                } else {
                    buffer.append(line).append("\r\n");
                }
            }
            if (file != null) {
                FileTool.get().getFileOperatorUtil().writeString2File(file.getAbsolutePath(), buffer.toString());
                Log.d("ts替换", "ts替换完成");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            FileTool.get().getFileOperatorUtil().closeIo(in);
        }
        return buffer.toString();
    }
}
