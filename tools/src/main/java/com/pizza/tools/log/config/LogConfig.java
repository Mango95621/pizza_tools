package com.pizza.tools.log.config;


import com.pizza.tools.log.parser.Parser;

/**
 * @author BoWei
 * @Description: 日志配置接口
 */
public interface LogConfig {
    /**
     * 设置是否输出日志
     */
    LogConfig configAllowLog(boolean allowLog);

    /**
     * 设置标签前缀
     */
    LogConfig configTagPrefix(String prefix);

    /**
     * 设置需要格式化的标签
     */
    LogConfig configFormatTag(String formatTag);

    /**
     * 设置是否显示排版线条
     */
    LogConfig configShowBorders(boolean showBorder);

    /**
     * 设置日志最小显示级别
     */
    LogConfig configLevel(int logLevel);

    /**
     * 添加自定义解析器
     *
     * @param classes
     * @return
     */
    LogConfig addParserClass(Class<? extends Parser>... classes);
}
