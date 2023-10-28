package com.pizza.tools.date;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static  com.pizza.tools.ConstantsTool.DAY;
import static  com.pizza.tools.ConstantsTool.HOUR;
import static  com.pizza.tools.ConstantsTool.MIN;
import static  com.pizza.tools.ConstantsTool.MSEC;
import static  com.pizza.tools.ConstantsTool.SEC;

import com.pizza.tools.ConstantsTool;
import com.pizza.tools.DataTool;

/**
 * @author Kyle
 * 时间相关工具类
 */
public class TimeTool {

    /**
     * 在工具类中经常使用到工具类的格式化描述，这个主要是一个日期的操作类，所以日志格式主要使用 SimpleDateFormat的定义格式.
     * 格式的意义如下： 日期和时间模式
     * 日期和时间格式由日期和时间模式字符串指定。在日期和时间模式字符串中，未加引号的字母 'A' 到 'Z' 和 'a' 到 'z'
     * 被解释为模式字母，用来表示日期或时间字符串元素。文本可以使用单引号 (') 引起来，以免进行解释。"''"
     * 表示单引号。所有其他字符均不解释；只是在格式化时将它们简单复制到输出字符串，或者在分析时与输入字符串进行匹配。
     * <p>
     * 定义了以下模式字母（所有其他字符 'A' 到 'Z' 和 'a' 到 'z' 都被保留）
     * <p>
     * HH:mm    15:44
     * h:mm     3:44 下午
     * HH:mm    15:44 CST
     * HH:mm     15:44 +0800
     * HH:mm     15:44 中国标准时间
     * HH:mm:ss    15:44:40
     * yyyy-MM-dd    2016-08-12
     * yyyy-MM-dd HH:mm    2016-08-12 15:44
     * yyyy-MM-dd HH:mm:ss    2016-08-12 15:44:40
     * yyyy-MM-dd HH:mm:ss    2016-08-12 15:44:40 中国标准时间
     * EEEE yyyy-MM-dd HH:mm:ss    星期五 2016-08-12 15:44:40 中国标准时间
     * yyyy-MM-dd HH:mm:ss.SSSZ    2016-08-12 15:44:40.461+0800
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
     * yyyy.MM.dd G 'at' HH:mm:ss z    2016.08.12 公元 at 15:44:40 CST
     * K:mm a    3:44 下午
     * EEE, MMM d, ''yy    星期五, 八月 12, '16
     * hh 'o''clock' a, zzzz    03 o'clock 下午, 中国标准时间
     * yyyyy.MMMMM.dd GGG hh:mm aaa    02016.八月.12 公元 03:44 下午
     * EEE, d MMM yyyy HH:mm:ss Z    星期五, 12 八月 2016 15:44:40 +0800
     * yyMMddHHmmssZ    160812154440+0800
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
     * EEEE 'DATE('yyyy-MM-dd')' 'TIME('HH:mm:ss')' zzzz    星期五 DATE(2016-08-12) TIME(15:44:40) 中国标准时间
     */
    public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);


    /**
     * 将时间戳转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param milliseconds 毫秒时间戳
     * @return 时间字符串
     */
    public static String milliseconds2String(long milliseconds) {
        return milliseconds2String(milliseconds, DEFAULT_SDF);
    }

    /**
     * 将时间戳转为时间字符串
     * <p>格式为用户自定义</p>
     *
     * @param milliseconds 毫秒时间戳
     * @param format       时间格式
     * @return 时间字符串
     */
    public static String milliseconds2String(long milliseconds, SimpleDateFormat format) {
        return format.format(new Date(milliseconds));
    }

    /**
     * 将时间戳转为时间字符串
     *
     * @param milliseconds 毫秒时间戳
     * @param format       时间格式
     * @return 时间字符串
     */
    public static String milliseconds2String(long milliseconds, String format) {
        if (String.valueOf(milliseconds).length() == 10) {
            milliseconds = milliseconds * 1000;
        }
        return new SimpleDateFormat(format, Locale.CHINA).format(new Date(milliseconds));
    }

    /**
     * 将时间字符串转为时间戳
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return 毫秒时间戳
     */
    public static long string2Milliseconds(String time) {
        return string2Milliseconds(time, DEFAULT_SDF);
    }

    /**
     * 将时间字符串转为时间戳
     * <p>格式为用户自定义</p>
     *
     * @param time   时间字符串
     * @param format 时间格式
     * @return 毫秒时间戳
     */
    public static long string2Milliseconds(String time, SimpleDateFormat format) {
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将时间字符串转为Date类型
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return Date类型
     */
    public static Date string2Date(String time) {
        return string2Date(time, DEFAULT_SDF);
    }

    /**
     * 将时间字符串转为Date类型
     * <p>格式为用户自定义</p>
     *
     * @param time   时间字符串
     * @param format 时间格式
     * @return Date类型
     */
    public static Date string2Date(String time, SimpleDateFormat format) {
        return new Date(string2Milliseconds(time, format));
    }

    /**
     * 将Date类型转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time Date类型时间
     * @return 时间字符串
     */
    public static String date2String(Date time) {
        return date2String(time, DEFAULT_SDF);
    }

    /**
     * 将Date类型转为时间字符串
     * <p>格式为用户自定义</p>
     *
     * @param time   Date类型时间
     * @param format 时间格式
     * @return 时间字符串
     */
    public static String date2String(Date time, SimpleDateFormat format) {
        return format.format(time);
    }

    /**
     * 将Date类型转为时间戳
     *
     * @param time Date类型时间
     * @return 毫秒时间戳
     */
    public static long date2Milliseconds(Date time) {
        return time.getTime();
    }

    /**
     * 将时间戳转为Date类型
     *
     * @param milliseconds 毫秒时间戳
     * @return Date类型时间
     */
    public static Date milliseconds2Date(long milliseconds) {
        return new Date(milliseconds);
    }

    /**
     * 毫秒时间戳单位转换（单位：unit）
     *
     * @param milliseconds 毫秒时间戳
     * @param unit         <ul>
     *                     <li>{@link ConstantsTool.TimeUnit#MSEC}: 毫秒</li>
     *                     <li>{@link ConstantsTool.TimeUnit#SEC }: 秒</li>
     *                     <li>{@link ConstantsTool.TimeUnit#MIN }: 分</li>
     *                     <li>{@link ConstantsTool.TimeUnit#HOUR}: 小时</li>
     *                     <li>{@link ConstantsTool.TimeUnit#DAY }: 天</li>
     *                     </ul>
     * @return unit时间戳
     */
    private static long milliseconds2Unit(long milliseconds, ConstantsTool.TimeUnit unit) {
        switch (unit) {
            case MSEC:
                return milliseconds / MSEC;
            case SEC:
                return milliseconds / SEC;
            case MIN:
                return milliseconds / MIN;
            case HOUR:
                return milliseconds / HOUR;
            case DAY:
                return milliseconds / DAY;
        }
        return -1;
    }

    /**
     * 获取两个时间差（单位：unit）
     * <p>time1和time2格式都为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time0 时间字符串1
     * @param time1 时间字符串2
     * @param unit  <ul>
     *              <li>{@link ConstantsTool.TimeUnit#MSEC}: 毫秒</li>
     *              <li>{@link ConstantsTool.TimeUnit#SEC }: 秒</li>
     *              <li>{@link ConstantsTool.TimeUnit#MIN }: 分</li>
     *              <li>{@link ConstantsTool.TimeUnit#HOUR}: 小时</li>
     *              <li>{@link ConstantsTool.TimeUnit#DAY }: 天</li>
     *              </ul>
     * @return unit时间戳
     */
    public static long getIntervalTime(String time0, String time1, ConstantsTool.TimeUnit unit) {
        return getIntervalTime(time0, time1, unit, DEFAULT_SDF);
    }

    /**
     * 获取两个时间差（单位：unit）
     * <p>time1和time2格式都为format</p>
     *
     * @param time0  时间字符串1
     * @param time1  时间字符串2
     * @param unit   <ul>
     *               <li>{@link ConstantsTool.TimeUnit#MSEC}: 毫秒</li>
     *               <li>{@link ConstantsTool.TimeUnit#SEC }: 秒</li>
     *               <li>{@link ConstantsTool.TimeUnit#MIN }: 分</li>
     *               <li>{@link ConstantsTool.TimeUnit#HOUR}: 小时</li>
     *               <li>{@link ConstantsTool.TimeUnit#DAY }: 天</li>
     *               </ul>
     * @param format 时间格式
     * @return unit时间戳
     */
    public static long getIntervalTime(String time0, String time1, ConstantsTool.TimeUnit unit, SimpleDateFormat format) {
        return Math.abs(milliseconds2Unit(string2Milliseconds(time0, format)
                - string2Milliseconds(time1, format), unit));
    }

    /**
     * 获取两个时间差（单位：unit）
     * <p>time1和time2都为Date类型</p>
     *
     * @param time0 Date类型时间1
     * @param time1 Date类型时间2
     * @param unit  <ul>
     *              <li>{@link ConstantsTool.TimeUnit#MSEC}: 毫秒</li>
     *              <li>{@link ConstantsTool.TimeUnit#SEC }: 秒</li>
     *              <li>{@link ConstantsTool.TimeUnit#MIN }: 分</li>
     *              <li>{@link ConstantsTool.TimeUnit#HOUR}: 小时</li>
     *              <li>{@link ConstantsTool.TimeUnit#DAY }: 天</li>
     *              </ul>
     * @return unit时间戳
     */
    public static long getIntervalTime(Date time0, Date time1, ConstantsTool.TimeUnit unit) {
        return Math.abs(milliseconds2Unit(date2Milliseconds(time1)
                - date2Milliseconds(time0), unit));
    }

    /**
     * 获取当前时间
     *
     * @return 毫秒时间戳
     */
    public static long getCurTimeMills() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @return 时间字符串
     */
    public static String getCurTimeString() {
        return date2String(new Date());
    }

    /**
     * 获取当前时间
     * <p>格式为用户自定义</p>
     *
     * @param format 时间格式
     * @return 时间字符串
     */
    public static String getCurTimeString(SimpleDateFormat format) {
        return date2String(new Date(), format);
    }

    /**
     * 获取当前时间
     * <p>Date类型</p>
     *
     * @return Date类型时间
     */
    public static Date getCurTimeDate() {
        return new Date();
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @param unit <ul>
     *             <li>{@link ConstantsTool.TimeUnit#MSEC}:毫秒</li>
     *             <li>{@link ConstantsTool.TimeUnit#SEC }:秒</li>
     *             <li>{@link ConstantsTool.TimeUnit#MIN }:分</li>
     *             <li>{@link ConstantsTool.TimeUnit#HOUR}:小时</li>
     *             <li>{@link ConstantsTool.TimeUnit#DAY }:天</li>
     *             </ul>
     * @return unit时间戳
     */
    public static long getIntervalByNow(String time, ConstantsTool.TimeUnit unit) {
        return getIntervalByNow(time, unit, DEFAULT_SDF);
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <p>time格式为format</p>
     *
     * @param time   时间字符串
     * @param unit   <ul>
     *               <li>{@link ConstantsTool.TimeUnit#MSEC}: 毫秒</li>
     *               <li>{@link ConstantsTool.TimeUnit#SEC }: 秒</li>
     *               <li>{@link ConstantsTool.TimeUnit#MIN }: 分</li>
     *               <li>{@link ConstantsTool.TimeUnit#HOUR}: 小时</li>
     *               <li>{@link ConstantsTool.TimeUnit#DAY }: 天</li>
     *               </ul>
     * @param format 时间格式
     * @return unit时间戳
     */
    public static long getIntervalByNow(String time, ConstantsTool.TimeUnit unit, SimpleDateFormat format) {
        return getIntervalTime(getCurTimeString(), time, unit, format);
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <p>time为Date类型</p>
     *
     * @param time Date类型时间
     * @param unit <ul>
     *             <li>{@link ConstantsTool.TimeUnit#MSEC}: 毫秒</li>
     *             <li>{@link ConstantsTool.TimeUnit#SEC }: 秒</li>
     *             <li>{@link ConstantsTool.TimeUnit#MIN }: 分</li>
     *             <li>{@link ConstantsTool.TimeUnit#HOUR}: 小时</li>
     *             <li>{@link ConstantsTool.TimeUnit#DAY }: 天</li>
     *             </ul>
     * @return unit时间戳
     */
    public static long getIntervalByNow(Date time, ConstantsTool.TimeUnit unit) {
        return getIntervalTime(getCurTimeDate(), time, unit);
    }

    /**
     * 判断闰年
     *
     * @param year 年份
     * @return {@code true}: 闰年<br>{@code false}: 平年
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 将date转换成format格式的日期
     *
     * @param format 格式
     * @param date   日期
     * @return
     */
    public static String simpleDateFormat(String format, Date date) {
        if (DataTool.isNullString(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format, Locale.CHINA).format(date);
    }

    //--------------------------------------------字符串转换成时间戳-----------------------------------

    /**
     * 将指定格式的日期转换成时间戳
     *
     * @param date
     * @return
     */
    public static String date2Timestamp(Date date) {
        return String.valueOf(date.getTime()).substring(0, 10);
    }

    /**
     * 将日期字符串 按照 指定的格式 转换成 DATE
     * 转换失败时 return null;
     *
     * @param format
     * @param dates
     * @return
     */
    public static Date string2Date(String format, String dates) {
        SimpleDateFormat sdr = new SimpleDateFormat(format, Locale.CHINA);
        Date date = null;
        try {
            date = sdr.parse(dates);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将 yyyy年MM月dd日 转换成 时间戳
     *
     * @param format
     * @param datess
     * @return
     */
    public static String string2Timestamp(String format, String datess) {
        Date date = string2Date(format, datess);
        return date2Timestamp(date);
    }


    //===========================================字符串转换成时间戳====================================

    /**
     * 获取当前日期时间 / 得到今天的日期
     * str yyyyMMddhhMMss 之类的
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDateTime(String format) {
        return simpleDateFormat(format, new Date());
    }

    /**
     * 时间戳  转换成 指定格式的日期
     * 如果format为空，则默认格式为
     *
     * @param times  时间戳
     * @param format 日期格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String times, String format) {
        return simpleDateFormat(format, new Date(DataTool.stringToInt(times) * 1000L));
    }

    /**
     * 得到昨天的日期
     *
     * @param format 日期格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getYesterdayDate(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return simpleDateFormat(format, calendar.getTime());
    }

    /**
     * 视频时间 转换成 "mm:ss"
     *
     * @param milliseconds
     * @return
     */
    public static String formatTime(long milliseconds) {
        String format = "mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String video_time = sdf.format(milliseconds);
        return video_time;
    }

    /**
     * "mm:ss" 转换成 视频时间
     *
     * @param time
     * @return
     */
    public static long formatSeconds(String time) {
        String format = "mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date;
        long times = 0;
        try {
            date = sdf.parse(time);
            times = date.getTime();
            Log.d("时间戳", times + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param strDate 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常<br>
     */
    public static int dateForWeek(String strDate) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(DEFAULT_SDF.parse(strDate));
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return 7;
        } else {
            return c.get(Calendar.DAY_OF_WEEK) - 1;
        }
    }

    /**
     * 判断当前日期是星期几
     *
     * @param strDate 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常<br>
     */
    public static int dateForWeek(String strDate, SimpleDateFormat simpleDateFormat) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(simpleDateFormat.parse(strDate));
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return 7;
        } else {
            return c.get(Calendar.DAY_OF_WEEK) - 1;
        }
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     *
     * @param timeStamp
     * @return
     */
    public static String convertTime2Hint(long timeStamp) {
        return convertTime2Hint(timeStamp, "yy/MM/dd");
    }

    public static String convertTime2Hint(long timeStamp, String format) {
        long curTime = getCurTimeMills() / (long) 1000;
        long time = curTime - timeStamp;
        if (time < 60 && time >= 0) {
            return time + "秒钟前";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 86400 && time < 86400 * 30) {
            return time / 86400 + "天前";
        } else {
            return milliseconds2String(timeStamp, format);
        }
    }
}