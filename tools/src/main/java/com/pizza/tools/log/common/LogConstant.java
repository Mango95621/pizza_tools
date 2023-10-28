package com.pizza.tools.log.common;

import java.util.List;

import com.pizza.tools.log.config.LogDefaultConfig;
import com.pizza.tools.log.parser.BundleParse;
import com.pizza.tools.log.parser.CollectionParse;
import com.pizza.tools.log.parser.IntentParse;
import com.pizza.tools.log.parser.MapParse;
import com.pizza.tools.log.parser.Parser;
import com.pizza.tools.log.parser.ReferenceParse;
import com.pizza.tools.log.parser.ThrowableParse;

/**
 * @Description: 日志常量
 */
public class LogConstant {

    public static final String STRING_OBJECT_NULL = "Object[object is null]";

    /**
     * 每行最大日志长度
     */
    public static final int LINE_MAX = 1024 * 3;

    /**
     * 解析属性最大层级
     */
    public static final int MAX_CHILD_LEVEL = 2;

    public static final int MIN_STACK_OFFSET = 5;

    /**
     * 换行符
     */
    public static final String BR = System.getProperty("line.separator");

    /**
     * 分割线方位
     */
    public static final int DIVIDER_TOP = 1;
    public static final int DIVIDER_BOTTOM = 2;
    public static final int DIVIDER_CENTER = 4;
    public static final int DIVIDER_NORMAL = 3;

    // 默认支持解析库
    public static final Class<? extends Parser>[] DEFAULT_PARSE_CLASS = new Class[]{
            BundleParse.class, IntentParse.class, CollectionParse.class,
            MapParse.class, ThrowableParse.class, ReferenceParse.class
    };

    /**
     * 获取默认解析类
     *
     * @return
     */
    public static List<Parser> getParsers() {
        return LogDefaultConfig.getInstance().getParseList();
    }
}
