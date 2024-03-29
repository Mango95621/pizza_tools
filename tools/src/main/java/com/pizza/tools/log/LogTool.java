package com.pizza.tools.log;

import android.util.Log;

import com.pizza.tools.ToolInit;
import com.pizza.tools.date.TimeTool;
import com.pizza.tools.file.FileTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author BoWei
 */
public class LogTool {
    /**
     * 日志的输出格式
     */
    private static final SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE);
    /**
     * 日志文件格式
     */
    private static final SimpleDateFormat FILE_SUFFIX = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);
    /**
     * sd卡中日志文件的最多保存天数
     */
    private static final int LOG_SAVE_DAYS = 7;
    /**
     * 日志文件总开关
     */
    private static boolean logSwitch = true;
    /**
     * 日志写入文件开关
     */
    private static final Boolean LOG_TO_FILE = false;
    /**
     * 默认的tag
     */
    private static final String LOG_TAG = "Mango";
    /**
     * 日志文件保存路径
     */
    private static String logFilePath;
    /**
     * 日志文件保存名称
     */
    private static String logFileName;

    /**
     * 是否使用树形 log
     */
    private static boolean isTreeShow = false;

    public static void init(boolean isLog) {
        init(isLog, "Log");
    }

    public static void init(boolean isLog, String fileName) {
        logFilePath = FileTool.get().getCanUseRootPath() + ToolInit.getApplicationContext().getPackageName();
        logFileName = fileName;
        logSwitch = isLog;
    }

    public static void setIsTreeShow(boolean isTreeShow) {
        LogTool.isTreeShow = isTreeShow;
    }

    /****************************
     * Warn
     *********************************/
    public static void w(Object msg) {
        w(LOG_TAG, msg);
    }

    public static void w(String tag, Object msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'w');
    }

    /***************************
     * Error
     ********************************/
    public static void e(Object msg) {
        e(LOG_TAG, msg);
    }

    public static void e(String tag, Object msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'e');
    }

    /***************************
     * Debug
     ********************************/
    public static void d(Object msg) {
        d(LOG_TAG, msg);
    }

    public static void d(String tag, Object msg) {// 调试信息
        d(tag, msg, null);
    }

    public static void d(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'd');
    }

    /****************************
     * Info
     *********************************/
    public static void i(Object msg) {
        i(LOG_TAG, msg);
    }

    public static void i(String tag, Object msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'i');
    }

    /**************************
     * Verbose
     ********************************/
    public static void v(Object msg) {
        v(LOG_TAG, msg);
    }

    public static void v(String tag, Object msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'v');
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     */
    private static void log(String tag, String msg, Throwable tr, char level) {
        if (logSwitch) {
            if (!isTreeShow && msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    // 当前截取的长度<总长度则继续截取最大的长度来打印
                    if (i + 4000 < msg.length()) {
                        if ('e' == level) {
                            Log.e(tag, msg.substring(i, i + 4000), tr);
                        } else if ('w' == level) {
                            Log.w(tag, msg.substring(i, i + 4000), tr);
                        } else if ('d' == level) {
                            Log.d(tag, msg.substring(i, i + 4000), tr);
                        } else if ('i' == level) {
                            Log.i(tag, msg.substring(i, i + 4000), tr);
                        } else {
                            Log.v(tag, msg.substring(i, i + 4000), tr);
                        }
                    } else {
                        // 当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                        if ('e' == level) {
                            Log.e(tag, msg.substring(i), tr);
                        } else if ('w' == level) {
                            Log.w(tag, msg.substring(i), tr);
                        } else if ('d' == level) {
                            Log.d(tag, msg.substring(i), tr);
                        } else if ('i' == level) {
                            Log.i(tag, msg.substring(i), tr);
                        } else {
                            Log.v(tag, msg.substring(i), tr);
                        }
                    }
                }
            } else if (isTreeShow) {
                if ('e' == level) {
                    TreeLogTool.e(tag, msg);
                } else if ('w' == level) {
                    TreeLogTool.w(tag, msg);
                } else if ('d' == level) {
                    TreeLogTool.d(tag, msg);
                } else if ('i' == level) {
                    TreeLogTool.i(tag, msg);
                } else {
                    TreeLogTool.v(tag, msg);
                }
            } else {
                if ('e' == level) {
                    Log.e(tag, msg, tr);
                } else if ('w' == level) {
                    Log.w(tag, msg, tr);
                } else if ('d' == level) {
                    Log.d(tag, msg, tr);
                } else if ('i' == level) {
                    Log.i(tag, msg, tr);
                } else {
                    Log.v(tag, msg, tr);
                }
            }

            if (LOG_TO_FILE) {
                log2File(String.valueOf(level), tag, msg + (tr == null ? "" : "\n") + Log.getStackTraceString(tr));
            }
        }
    }

    private static void subsectionPrintLog(String logBody) {
        if (logBody.length() > 4000) {
            for (int i = 0; i < logBody.length(); i += 4000) {
                // 当前截取的长度<总长度则继续截取最大的长度来打印
                if (i + 4000 < logBody.length()) {
                    Log.i("msg" + i, logBody.substring(i, i + 4000));
                } else {
                    // 当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                    Log.i("msg" + i, logBody.substring(i));
                }
            }
        } else {
            // 直接打印
            Log.i("msg", logBody);
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private synchronized static void log2File(String mylogtype, String tag, String text) {
        Date nowtime = new Date();
        String date = FILE_SUFFIX.format(nowtime);
        String dateLogContent = LOG_FORMAT.format(nowtime) + ":" + mylogtype + ":" + tag + ":" + text;
        File destDir = new File(logFilePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file = new File(logFilePath, logFileName + date);
        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(dateLogContent);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定的日志文件
     */
    public static void delFile() {
        String needDelFile = FILE_SUFFIX.format(getDateBefore());
        File file = new File(logFilePath, needDelFile + logFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到LOG_SAVE_DAYS天前的日期
     *
     * @return
     */
    private static Date getDateBefore() {
        Date nowTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - LOG_SAVE_DAYS);
        return now.getTime();
    }

    public static void saveLogFile(String message) {
        File fileDir =
                new File(FileTool.get().getCanUseRootPath() + File.separator + ToolInit.getApplicationContext().getPackageName());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(fileDir, TimeTool.getCurrentDateTime("yyyyMMdd") + ".txt");
        try {
            if (file.exists()) {
                PrintStream ps = new PrintStream(new FileOutputStream(file, true));
                // 往文件里写入字符串
                ps.append(TimeTool.getCurrentDateTime("\n\n\nyyyy-MM-dd HH:mm:ss"))
                        .append("\n")
                        .append(message);
            } else {
                PrintStream ps = new PrintStream(new FileOutputStream(file));
                file.createNewFile();
                // 往文件里写入字符串
                ps.println(TimeTool.getCurrentDateTime("yyyy-MM-dd HH:mm:ss") + "\n" + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
