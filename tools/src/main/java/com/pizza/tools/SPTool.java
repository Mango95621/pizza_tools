package com.pizza.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Kyle
 * SharedPreferences工具类
 */

public class SPTool {

    private static String spName = "pizza_sp";

    /**
     * SP中写入String类型value
     *
     * @param key   键
     * @param value 值
     */
    public static void putString(String key, String value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, value);
    }

    /**
     * SP中写入int类型value
     *
     * @param key   键
     * @param value 值
     */
    public static void putInt(String key, int value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public static int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * SP中读取int
     *
     * @param key 键
     */
    public static int getInt(String key, int value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key, value);
    }

    /**
     * SP中写入long类型value
     *
     * @param key   键
     * @param value 值
     */
    public static void putLong(String key, long value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public static long getLong(String key) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getLong(key, -1L);
    }

    /**
     * SP中写入float类型value
     *
     * @param key   键
     * @param value 值
     */
    public static void putFloat(String key, float value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public static float getFloat(String key) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getFloat(key, -1F);
    }

    /**
     * SP中写入boolean类型value
     *
     * @param key   键
     * @param value 值
     */
    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public static void remove(String key) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }

    /**
     * 清除指定的信息
     *
     * @param name    键名
     * @param key     若为null 则删除name下所有的键值
     */
    public static void clearPreference(String name, String key) {
        SharedPreferences sharedPreferences = ToolInit.getInstance().getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (key != null) {
            editor.remove(key);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    public static boolean isHasKey(String key) {
        SharedPreferences sp = ToolInit.getInstance().getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    public static void setSpName(String spName) {
        SPTool.spName = spName;
    }
}