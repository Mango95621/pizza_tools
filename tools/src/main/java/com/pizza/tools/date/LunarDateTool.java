package com.pizza.tools.date;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 农历日期工具类
 * @author BoWei
 * @date 2022/12/01
 */
public class LunarDateTool {
    private static final int FALSE = 0;
    private static final List<Integer> LUNAR_INFO = Arrays.asList(
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260,
            0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255,
            0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40,
            0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0,
            0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0,
            0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570,
            0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4,
            0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a,
            0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
            0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552,
            0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60,
            0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0,
            0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577,
            0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
            0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0,
            0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,
            0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0,
            0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,
            0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6,
            0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,
            0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50,
            0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,
            0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0,
            0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,
            0x0d520
    );

    private static final List<Integer> SOLAR_MONTH = Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
    private static final List<String> GAN = Arrays.asList("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸");
    private static final List<String> ZHI = Arrays.asList("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥");
    private static final List<String> ANIMALS = Arrays.asList("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪");
    private static final List<String> SOLAR_TERM = Arrays.asList(
            "小寒",
            "大寒",
            "立春",
            "雨水",
            "惊蛰",
            "春分",
            "清明",
            "谷雨",
            "立夏",
            "小满",
            "芒种",
            "夏至",
            "小暑",
            "大暑",
            "立秋",
            "处暑",
            "白露",
            "秋分",
            "寒露",
            "霜降",
            "立冬",
            "小雪",
            "大雪",
            "冬至"
    );
    private static final List<String> S_TERM_INFO = Arrays.asList(
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c3598082c95f8c965cc920f", "97bd0b06bdb0722c965ce1cfcc920f",
            "b027097bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c359801ec95f8c965cc920f", "97bd0b06bdb0722c965ce1cfcc920f",
            "b027097bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf97c359801ec95f8c965cc920f", "97bd0b06bdb0722c965ce1cfcc920f",
            "b027097bd097c36b0b6fc9274c91aa", "9778397bd19801ec9210c965cc920e",
            "97b6b97bd19801ec95f8c965cc920f", "97bd09801d98082c95f8e1cfcc920f",
            "97bd097bd097c36b0b6fc9210c8dc2", "9778397bd197c36c9210c9274c91aa",
            "97b6b97bd19801ec95f8c965cc920e", "97bd09801d98082c95f8e1cfcc920f",
            "97bd097bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c91aa",
            "97b6b97bd19801ec95f8c965cc920e", "97bcf97c3598082c95f8e1cfcc920f",
            "97bd097bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c3598082c95f8c965cc920f",
            "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c3598082c95f8c965cc920f",
            "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f",
            "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f",
            "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f",
            "97bd097bd07f595b0b6fc920fb0722", "9778397bd097c36b0b6fc9210c8dc2",
            "9778397bd19801ec9210c9274c920e", "97b6b97bd19801ec95f8c965cc920f",
            "97bd07f5307f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36c9210c9274c920e", "97b6b97bd19801ec95f8c965cc920f",
            "97bd07f5307f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36c9210c9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bd07f1487f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf7f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf7f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf7f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e",
            "97bcf7f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b6fc920fb0722",
            "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c9274c920e",
            "97bcf7f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "9778397bd097c36b0b6fc9210c91aa", "97b6b97bd197c36c9210c9274c920e",
            "97bcf7f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "9778397bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c920e",
            "97b6b7f0e47f531b0723b0b6fb0722", "7f0e37f5307f595b0b0bc920fb0722",
            "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36b0b70c9274c91aa",
            "97b6b7f0e47f531b0723b0b6fb0721", "7f0e37f1487f595b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc9210c8dc2", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f595b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9274c91aa",
            "97b6b7f0e47f531b0723b0787b0721", "7f0e27f0e47f531b0b0bb0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c91aa",
            "97b6b7f0e47f149b0723b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722",
            "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c8dc2",
            "977837f0e37f149b0723b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722",
            "7f0e37f5307f595b0b0bc920fb0722", "7f0e397bd097c35b0b6fc9210c8dc2",
            "977837f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e37f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc9210c8dc2",
            "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722",
            "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f149b0723b0787b0721",
            "7f0e27f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "977837f0e37f14998082b0723b06bd", "7f07e7f0e37f149b0723b0787b0721",
            "7f0e27f0e47f531b0723b0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722",
            "977837f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b0721",
            "7f07e7f0e47f531b0723b0b6fb0722", "7f0e37f1487f595b0b0bb0b6fb0722",
            "7f0e37f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b0721",
            "7f07e7f0e47f531b0723b0b6fb0722", "7f0e37f1487f531b0b0bb0b6fb0722",
            "7f0e37f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e37f1487f531b0b0bb0b6fb0722",
            "7f0e37f0e37f14898082b072297c35", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e37f0e37f14898082b072297c35", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14998082b0787b06bd",
            "7f07e7f0e47f149b0723b0787b0721", "7f0e27f1487f531b0b0bb0b6fb0722",
            "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14998082b0723b06bd",
            "7f07e7f0e47f149b0723b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722",
            "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14998082b0723b06bd",
            "7f07e7f0e37f14998083b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722",
            "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14898082b0723b02d5",
            "7f07e7f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722",
            "7f0e36665b66aa89801e9808297c35", "665f67f0e37f14898082b0723b02d5",
            "7ec967f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722",
            "7f0e36665b66a449801e9808297c35", "665f67f0e37f14898082b0723b02d5",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e36665b66a449801e9808297c35", "665f67f0e37f14898082b072297c35",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e26665b66a449801e9808297c35", "665f67f0e37f1489801eb072297c35",
            "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721",
            "7f0e27f1487f531b0b0bb0b6fb0722"
    );
    private static final List<String> N_STR_1 = Arrays.asList("日", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十");
    private static final List<String> N_STR_2 = Arrays.asList("初", "十", "廿", "卅");
    private static final List<String> N_STR_3 = Arrays.asList("正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊");

    private int lYearDays(int y) {
        int sum = 348;
        int i = 0x8000;
        while (i > 0x8) {
            if ((LUNAR_INFO.get(y - 1900) & i) != FALSE) {
                sum += 1;
            } else {
                sum += 0;
            }
            i = i >> 1;
        }
        return (sum + leapDays(y));
    }

    private int leapMonth(int y) {
        return LUNAR_INFO.get(y - 1900) & 0xf;
    }

    private int leapDays(int y) {
        if (leapMonth(y) != FALSE) {
            if ((LUNAR_INFO.get(y - 1900) & 0x10000) != FALSE) {
                return 30;
            } else {
                return 29;
            }
        } else {
            return 0;
        }
    }

    private int monthDays(int y, int m) {
        if (m > 12 || m < 1) {
            return -1;
        }
        if (((LUNAR_INFO.get(y - 1900) & (0x10000 >> m)) != FALSE)) {
            return 30;
        } else {
            return 29;
        }
    }

    private int solarDays(int y, int m) {
        if (m > 12 || m < 1) {
            return -1;
        }
        int ms = m - 1;
        if (ms == 1) {
            if (((y % 4 == 0) && (y % 100 != 0) || (y % 400 == 0))) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return SOLAR_MONTH.get(ms);
        }
    }

    private String toGanZhi(int offset) {
        return GAN.get(offset % 10) + ZHI.get(offset % 12);
    }

    private int getTerm(int y, int n) {
        if (y < 1900 || y > 2100) {
            return -1;
        }
        if (n < 1 || n > 24) {
            return -1;
        }
        String table = S_TERM_INFO.get(y - 1900);
        List<String> info = Arrays.asList(
                String.valueOf(Integer.parseInt(table.substring(0, 5), 16)),
                String.valueOf(Integer.parseInt(table.substring(5, 10), 16)),
                String.valueOf(Integer.parseInt(table.substring(10, 15), 16)),
                String.valueOf(Integer.parseInt(table.substring(15, 20), 16)),
                String.valueOf(Integer.parseInt(table.substring(20, 25), 16)),
                String.valueOf(Integer.parseInt(table.substring(25, 30), 16))
        );
        System.out.println(info);
        List<String> calday = Arrays.asList(
                info.get(0).substring(0, 1),
                info.get(0).substring(1, 2),
                info.get(0).substring(3, 4),
                info.get(0).substring(4, 6),
                info.get(1).substring(0, 1),
                info.get(1).substring(1, 2),
                info.get(1).substring(3, 4),
                info.get(1).substring(4, 6),
                info.get(2).substring(0, 1),
                info.get(2).substring(1, 2),
                info.get(2).substring(3, 4),
                info.get(2).substring(4, 6),
                info.get(3).substring(0, 1),
                info.get(3).substring(1, 2),
                info.get(3).substring(3, 4),
                info.get(3).substring(4, 6),
                info.get(4).substring(0, 1),
                info.get(4).substring(1, 2),
                info.get(4).substring(3, 4),
                info.get(4).substring(4, 6),
                info.get(5).substring(0, 1),
                info.get(5).substring(1, 2),
                info.get(5).substring(3, 4),
                info.get(5).substring(4, 6)
        );
        return Integer.parseInt(calday.get(n - 1));
    }

    /**
     * 公历月转农历月
     * @param m month
     * @return 1->正月，2->二月，...，10->十月，11->冬月，12->腊月
     */
    public String toChinaMonth(int m) {
        if (m > 12 || m < 1) {
            return "";
        }
        String s = N_STR_3.get(m - 1);
        s += "月";
        return s;
    }

    /**
     * 公历日转农历日
     * @param d day
     * @return 1->初一，11->十一，21->廿一，30->卅十
     */
    public String toChinaDay(int d) {
        switch (d) {
            case 10:
                return "初十";
            case 20:
                return "二十";
            case 30:
                return "三十";
            default:
                String s = N_STR_2.get((int) Math.floor((d / 10f)));
                s += N_STR_1.get(d % 10);
                return s;
        }
    }

    /**
     * 获取生肖
     * @param y full year
     * @return 2021->牛
     */
    public String getAnimal(int y) {
        return ANIMALS.get((y - 4) % 12);
    }

    /**
     * 公历转农历
     * @param y full year : 2021
     * @param m month : 2
     * @param d day : 26
     * @return [CalendarInfo]
     */
    public CalendarInfo solar2lunar(int y, int m, int d) {
        if (y < 1900 || y > 2100) {
            return null;
        }
        if (y == 1900 && m == 1 && d < 31) {
            return null;
        }
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(1900, 0, 31);
        Calendar objDate = Calendar.getInstance();
        objDate.set(y, m - 1, d);
        int i = 1900;
        int temp = 0;
        int offset = (int) ((objDate.getTimeInMillis() - minCalendar.getTimeInMillis()) / 86400000);
        while (i < 2101 && offset > 0) {
            temp = lYearDays(i);
            offset -= temp;
            i++;
        }
        if (offset < 0) {
            offset += temp;
            i--;
        }

        boolean isToday = objDate.getTimeInMillis() == Calendar.getInstance().getTimeInMillis();
        int nWeek = objDate.get(Calendar.DAY_OF_WEEK) - 1;
        String cWeek = N_STR_1.get(nWeek);
        if (nWeek == 0) {
            nWeek = 7;
        }
        int year = i;
        int leap = leapMonth(i);
        boolean isLeap = false;
        i = 1;
        while (i < 13 && offset > 0) {
            if (leap > 0 && i == (leap + 1) && !isLeap) {
                --i;
                isLeap = true;
                temp = leapDays(year);
            } else {
                temp = monthDays(year, i);
            }
            if (isLeap && i == (leap + 1)) {
                isLeap = false;
            }
            offset -= temp;
            i++;
        }
        if (offset == 0L && leap > 0 && i == leap + 1)
            if (isLeap) {
                isLeap = false;
            } else {
                isLeap = true;
                --i;
            }
        if (offset < 0) {
            offset += temp;
            --i;
        }
        int month = i;
        int day = offset + 1;
        int sm = m - 1;
        int term3 = getTerm(year, 3);
        String gzY = "";
        if (sm < 2 && d < term3) {
            gzY = toGanZhi(year - 5);
        } else {
            gzY = toGanZhi(year - 4);
        }
        int firstNode = getTerm(y, (m * 2 - 1));
        int secondNode = getTerm(y, (m * 2));
        String gzM = toGanZhi((y - 1900) * 12 + m + 11);
        if (d >= firstNode) {
            gzM = toGanZhi((y - 1900) * 12 + m + 12);
        }
        boolean isTerm = false;
        String term = null;
        if (firstNode == d) {
            isTerm = true;
            term = SOLAR_TERM.get(m * 2 - 2);
        }
        if (secondNode == d) {
            isTerm = true;
            term = SOLAR_TERM.get(m * 2 - 1);
        }
        Calendar ca = Calendar.getInstance();
        ca.set(y, sm, 1);
        int dayCyclical = (int) (ca.getTimeInMillis() / 86400000 + 25567 + 10);
        String gzD = toGanZhi((dayCyclical + d - 1));
        CalendarInfo calendarInfo = new CalendarInfo();
        calendarInfo.setLYear(year);
        calendarInfo.setLMonth(month);
        calendarInfo.setLDay(day);
        calendarInfo.setAnimal(getAnimal(year));
        if (isLeap) {
            calendarInfo.setIMonthCn("闰" + toChinaMonth(month));
        } else {
            calendarInfo.setIMonthCn(toChinaMonth(month));
        }
        calendarInfo.setIDayCn(toChinaDay(day));
        calendarInfo.setCYear(y);
        calendarInfo.setCMonth(m);
        calendarInfo.setCDay(d);
        calendarInfo.setGzYear(gzY);
        calendarInfo.setGzMonth(gzM);
        calendarInfo.setGzDay(gzD);
        calendarInfo.setToday(isToday);
        calendarInfo.setLeap(isLeap);
        calendarInfo.setNWeek(nWeek);
        calendarInfo.setNcWeek("星期" + cWeek);
        calendarInfo.setIsTerm(isTerm);
        calendarInfo.setTerm(term);
        return calendarInfo;
    }

    public CalendarInfo lunar2solar(int y, int m, int d) {
        return lunar2solar(y, m, d, false);
    }

    /**
     * 农历转公历
     * @param y full year : 2021
     * @param m month : 1
     * @param d day : 15
     * @param isLeapMonth is leap month，是否是闰月
     * @return [CalendarInfo]
     */
    public CalendarInfo lunar2solar(int y, int m, int d, boolean isLeapMonth) {
        int leapMonth = leapMonth(y);
        if (isLeapMonth && (leapMonth != m)) {
            return null;
        }
        if (y == 2100 && m == 12 && d > 1 || y == 1900 && m == 1 && d < 31) {
            return null;
        }
        int day = monthDays(y, m);
        if (y < 1900 || y > 2100 || d > day) {
            return null;
        }
        int offset = 0;
        for (int i = 1900; i < y; i++) {
            offset += lYearDays(i);
        }
        int leap;
        boolean isAdd = false;
        for (int i = 1; i < m; i++) {
            leap = leapMonth(y);
            if (!isAdd) {
                if (leap >= 1 && leap <= i) {
                    offset += leapDays(y);
                    isAdd = true;
                }
            }
            offset += monthDays(y, i);
        }
        if (isLeapMonth) {
            offset += day;
        }
        Calendar stmap = Calendar.getInstance();
        stmap.set(1900, 1, 31, 0, 0, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((offset + d - 31) * 86400000L + stmap.getTimeInMillis());
        int cY = calendar.get(Calendar.YEAR);
        int cM = calendar.get(Calendar.MONTH) + 1;
        int cD = calendar.get(Calendar.DATE);
        return solar2lunar(cY, cM, cD);
    }

    static class CalendarInfo {
        /**
         * 农历年
         */
        private int lYear;
        /**
         * 农历月
         */
        private int lMonth;
        /**
         * 农历日
         */
        private int lDay;
        /**
         * 生肖
         */
        private String animal;
        /**
         * 中文农历月
         */
        private String iMonthCn;
        /**
         * 中文农历日
         */
        private String iDayCn;
        /**
         * 公历年
         */
        private int cYear;
        /**
         * 公历月
         */
        private int cMonth;
        /**
         * 公历日
         */
        private int cDay;
        /**
         * 干支年
         */
        private String gzYear;
        /**
         * 干支月
         */
        private String gzMonth;
        /**
         * 干支日
         */
        private String gzDay;
        /**
         * 是否是今天
         */
        private Boolean isToday;
        /**
         * 是否是闰月
         */
        private Boolean isLeap;
        /**
         * 当前日是一周中的第几天
         */
        private int nWeek;
        /**
         * 中文星期
         */
        private String ncWeek;
        /**
         * 是否是节气
         */
        private Boolean isTerm;
        /**
         * 节气
         */
        private String term = null;

        public int getLYear() {
            return lYear;
        }

        public void setLYear(int lYear) {
            this.lYear = lYear;
        }

        public int getLMonth() {
            return lMonth;
        }

        public void setLMonth(int lMonth) {
            this.lMonth = lMonth;
        }

        public int getLDay() {
            return lDay;
        }

        public void setLDay(int lDay) {
            this.lDay = lDay;
        }

        public String getAnimal() {
            return animal == null ? "" : animal;
        }

        public void setAnimal(String animal) {
            this.animal = animal;
        }

        public String getIMonthCn() {
            return iMonthCn == null ? "" : iMonthCn;
        }

        public void setIMonthCn(String iMonthCn) {
            this.iMonthCn = iMonthCn;
        }

        public String getIDayCn() {
            return iDayCn == null ? "" : iDayCn;
        }

        public void setIDayCn(String iDayCn) {
            this.iDayCn = iDayCn;
        }

        public int getCYear() {
            return cYear;
        }

        public void setCYear(int cYear) {
            this.cYear = cYear;
        }

        public int getCMonth() {
            return cMonth;
        }

        public void setCMonth(int cMonth) {
            this.cMonth = cMonth;
        }

        public int getCDay() {
            return cDay;
        }

        public void setCDay(int cDay) {
            this.cDay = cDay;
        }

        public String getGzYear() {
            return gzYear == null ? "" : gzYear;
        }

        public void setGzYear(String gzYear) {
            this.gzYear = gzYear;
        }

        public String getGzMonth() {
            return gzMonth == null ? "" : gzMonth;
        }

        public void setGzMonth(String gzMonth) {
            this.gzMonth = gzMonth;
        }

        public String getGzDay() {
            return gzDay == null ? "" : gzDay;
        }

        public void setGzDay(String gzDay) {
            this.gzDay = gzDay;
        }

        public Boolean getToday() {
            return isToday;
        }

        public void setToday(Boolean today) {
            isToday = today;
        }

        public Boolean getLeap() {
            return isLeap;
        }

        public void setLeap(Boolean leap) {
            isLeap = leap;
        }

        public int getNWeek() {
            return nWeek;
        }

        public void setNWeek(int nWeek) {
            this.nWeek = nWeek;
        }

        public String getNcWeek() {
            return ncWeek == null ? "" : ncWeek;
        }

        public void setNcWeek(String ncWeek) {
            this.ncWeek = ncWeek;
        }

        public Boolean getIsTerm() {
            return isTerm;
        }

        public String getTerm() {
            return term == null ? "" : term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public void setIsTerm(Boolean term) {
            isTerm = term;
        }
    }

}
