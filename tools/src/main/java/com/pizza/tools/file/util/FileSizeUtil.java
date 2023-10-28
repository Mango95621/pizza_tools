package com.pizza.tools.file.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.pizza.tools.log.LogTool;
import com.pizza.tools.ConstantsTool;
import com.pizza.tools.ToolInit;
import com.pizza.tools.file.FileTool;

import java.io.File;
import java.math.BigDecimal;


/**
 * @author Kyle
 * 2023/8/24 15:25
 * - 1.计算文件大小: BigDecimal
 *
 * - 2.获取文件大小的方法只有两种(There are only two ways to get the file size):
 *   - File.length
 *   - ContentResolver.query()
 */
public class FileSizeUtil {
    /**
     * 获取指定 `文件/文件夹` 大小
     *
     * Get the size of the specified `file folder`
     */
    public long getFolderSize(File file) {
        long size = 0L;
        if (file == null || !file.exists()) {
            return size;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return size;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size += getFolderSize(files[i]);
            } else {
                size += getFileSize(files[i]);
            }
        }
        return size;
    }

    public double calculateFileOrDirSize(String path, FileSizeType sizeType) {
        return calculateFileOrDirSize(path, 2, sizeType);
    }

    /**
     * 计算`文件/文件夹`的大小 (Calculate the size of `file folder`)
     *
     * @param path 文件/文件夹的路径
     * @param sizeType 指定要转换的单位类型
     * @return 大小 double
     */
    public double calculateFileOrDirSize(String path, int scale, FileSizeType sizeType) {
        if (TextUtils.isEmpty(path)) {
            return 0.00;
        }
        return formatSizeByTypeWithoutUnit(BigDecimal.valueOf(calculateFileOrDirSize(path)), scale, sizeType).doubleValue();
    }

    /**
     * 计算`文件/文件夹`的大小 (Calculate the size of `file folder`)
     *
     * @param path 文件/文件夹的路径
     * @return Size
     */
    public long calculateFileOrDirSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0L;
        }

        File file = new File(path);
        long blockSize = 0L;
        try {
            if (file.isDirectory()) {
                blockSize = getFolderSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            LogTool.e(FileTool.TAG, "Failed to get file size = " + blockSize);
        }
        LogTool.i(FileTool.TAG, "Get file size = " + blockSize);
        return blockSize;
    }

    /**
     * 自动计算指定文件或指定文件夹的大小 (Automatically calculate the size of the specified file or specified folder)
     *
     * @param path 文件路径
     * @return 带 B、KB、M、GB、TB 单位的字符串
     */
    public String getFileOrDirSizeFormatted(String path) {
        return formatFileSize(calculateFileOrDirSize(path));
    }

    /**
     * 获取文件大小
     */
    public long getFileSize(File file) {
        if (file.exists()) {
            return file.length();
        } else {
            return 0L;
        }
    }

    public long getFileSize(Uri uri) {
        return getFileSize(ToolInit.getApplication(), uri);
    }

    /**
     * ContentResolver.query 获取 `文件/文件夹` 大小 (Get the size of `file folder`)
     *
     * @return File Size, Unit Byte
     */
    public long getFileSize(Context context, Uri uri) {
        long zero = 0L;
        String uriScheme = uri.getScheme();
        Cursor cursor =
                context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return zero;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || "content".equals(uriScheme)) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            // 1.Technically the column stores an int, but cursor.getString() will do the conversion automatically.
            // it.getString(sizeIndex)
            // 2.it.moveToFirst() -> Caused by: android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
            if (cursor.moveToFirst() && !cursor.isNull(sizeIndex)) {
                return cursor.getLong(sizeIndex);
            } else {
                return zero;
            }
        } else if ("file".equals(uriScheme)) {
            String pathByUri = FileTool.get().getFileUriUtil().getPathByUri(uri);
            if (TextUtils.isEmpty(pathByUri)) {
                return zero;
            }
            return new File(pathByUri).length();
        } else {
            return zero;
        }
    }

    /**
     * 保留两位小数, 不带单位 (Keep two decimal places, no unit)
     */
    public String formatFileSize(long size) {
        return formatFileSize(size, 2, true);
    }

    public String formatFileSize(long size, int scale) {
        return formatFileSize(size, scale, false);
    }

    /**
     * @param scale 精确到小数点以后几位 (Accurate to a few decimal places)
     */
    public String formatFileSize(long size, int scale, boolean withUnit) {
        long divisor = 1024L;
        // ROUND_DOWN 1023 -> 1023B ; ROUND_HALF_UP  1023 -> 1KB
        BigDecimal kiloByte = formatSizeByTypeWithDivisor(BigDecimal.valueOf(size), scale, FileSizeType.SIZE_TYPE_B, divisor);
        if (kiloByte.doubleValue() < 1) {
            return kiloByte.toPlainString() + (withUnit ? FileSizeType.SIZE_TYPE_B.unit : "");
        }
        // KB
        BigDecimal megaByte = formatSizeByTypeWithDivisor(kiloByte, scale, FileSizeType.SIZE_TYPE_KB, divisor);
        if (megaByte.doubleValue() < 1) {
            return kiloByte.toPlainString() + (withUnit ? FileSizeType.SIZE_TYPE_KB.unit : "");
        }
        // M
        BigDecimal gigaByte = formatSizeByTypeWithDivisor(megaByte, scale, FileSizeType.SIZE_TYPE_MB, divisor);
        if (gigaByte.doubleValue() < 1) {
            return kiloByte.toPlainString() + (withUnit ? FileSizeType.SIZE_TYPE_MB.unit : "");
        }
        // GB
        BigDecimal teraBytes = formatSizeByTypeWithDivisor(gigaByte, scale, FileSizeType.SIZE_TYPE_GB, divisor);
        if (teraBytes.doubleValue() < 1) {
            return kiloByte.toPlainString() + (withUnit ? FileSizeType.SIZE_TYPE_GB.unit : "");
        }
        // TB
        return kiloByte.toPlainString() + (withUnit ? FileSizeType.SIZE_TYPE_TB.unit : "");
    }

    /**
     * ### 转换文件大小不带单位, 注:没有单位,可自定义. 如: sizeType为`FileSizeType.SIZE_TYPE_MB`则返回`2.383`, 即`2.383M`
     *
     *  The converted file size does not have a unit. Note: There is no unit and can be customized.
     *  For example: sizeType is `FileSizeType.SIZE_TYPE_MB` then returns `2.383`, that is, `2.383M`
     *
     * - BigDecimal 实现提供（相对）精确的除法运算。当发生除不尽的情况时(ArithmeticException)，由scale参数指定精度，以后的数字四舍五入
     *
     * - https://www.liaoxuefeng.com/wiki/1252599548343744/1279768011997217
     * https://zhuanlan.zhihu.com/p/75780642
     * <pre>
     *      注: 禁止使用构造方法BigDecimal(double)的方式把double值转化为BigDecimal对象
     *      说明：反编译出的字节码文件显示每次循环都会new出一个StringBuilder对象，然后进行append操作，最后通过toString方法返回String对象，造成内存资源浪费。
     *      BigDecimal result = new BigDecimal(Double.toString(megaByte));
     * </pre>
     *
     * @param size 大小 Byte
     * @param scale 精确到小数点以后几位
     */
    public BigDecimal formatSizeByTypeWithoutUnit(BigDecimal size, int scale, FileSizeType sizeType) {
        BigDecimal divisor;
        switch (sizeType) {
            case SIZE_TYPE_B:
                divisor = BigDecimal.valueOf(ConstantsTool.BYTE);
                break;
            case SIZE_TYPE_MB:
                divisor = BigDecimal.valueOf(ConstantsTool.MB);
                break;
            case SIZE_TYPE_GB:
                divisor = BigDecimal.valueOf(ConstantsTool.GB);
                break;
            case SIZE_TYPE_TB:
                divisor = BigDecimal.valueOf(ConstantsTool.TB);
                break;
            case SIZE_TYPE_KB:
            default:
                divisor = BigDecimal.valueOf(ConstantsTool.KB);
                break;
        }
        return size.divide(
                divisor,
                scale,
                sizeType == FileSizeType.SIZE_TYPE_B ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_HALF_UP
        );
    }

    public BigDecimal formatSizeByTypeWithDivisor(BigDecimal size, int scale, FileSizeType sizeType, long divisor) {
        return size.divide(
                BigDecimal.valueOf(divisor),
                scale,
                sizeType == FileSizeType.SIZE_TYPE_B ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_HALF_UP
        );
    }

    /**
     * 转换文件大小带单位, 注:带单位 2.383M
     *
     * Convert file size with unit, note: with unit 2.383M
     */
    public String formatSizeByTypeWithUnit(long size, int scale, FileSizeType sizeType) {
        return formatSizeByTypeWithoutUnit(BigDecimal.valueOf(size), scale, sizeType).toPlainString() + sizeType.unit;
    }

    /**
     * 获取路径下的剩余空间
     * @param dirPath 要查询的路径，比如根目录就是/
     * @return 剩余空间
     */
    public long getFreeSpace(String dirPath) {
        if (!FileTool.get().getFileGlobalUtil().isSdcardAvailable()) {
            return 0;
        }
        StatFs stat = new StatFs(dirPath);
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        return availableBlocks * blockSize;
    }

    public static enum FileSizeType {

        SIZE_TYPE_B(1, "B"),
        SIZE_TYPE_KB(2, "KB"),
        SIZE_TYPE_MB(3, "M"),
        SIZE_TYPE_GB(4, "GB"),
        SIZE_TYPE_TB(5, "TB");
        private int id;
        private String unit;

        FileSizeType(int id, String unit) {
            this.id = id;
            this.unit = unit;
        }

    }
}
