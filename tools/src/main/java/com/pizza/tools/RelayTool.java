package com.pizza.tools;

import java.util.Arrays;

/**
 * 继电器工具类
 */
public class RelayTool {

    // 帧首
    private static long HEAD = 0x005A;
    // 设备类
    private static long DEVICE = 0x60;
    // 地址
    private static long ADDRESS = 0xFF;

    /**
     * 全开
     */
    public static String openAll() {
//        return "005A600003000000BD";
        return "005A60FF03000000BC";
    }

    /**
     * 全关
     */
    public static String closeAll() {
        return "005A60FF04000000BD";
    }


    public static String readAddress() {
        return "005A60FF10000000C9";
    }

    public static String readAddress(long address) {
        // 命令类型
        long type = 0x10;
        // 参数0
        long arg0 = 0x00;
        // 参数1
        long arg1 = 0x00;
        // 参数2
        long arg2 = 0x00;
        // 校验和
        // 取低8位
        long checkSum = (HEAD + DEVICE + address + type + arg0 + arg1 + arg2) & 0xFF;

        long cmd = (HEAD << 56) + (DEVICE << 48) + (address << 40) + (type << 32) + (arg0 << 24) + (arg1 << 16) + (arg2 << 8) + checkSum;
        // 不满18位高位补0
        return padLeft(Long.toHexString(cmd), 18);
    }

    /**
     * 设置主板地址
     * <p>
     * 例如把0号卡地址设置成2号地址: 00 5A 60 00 11 02 00 00 CD
     *
     * @param boardNo 主板号
     * @param address 设置主板的地址
     *
     * @return
     */
    public static String setAddress(int boardNo, int address) {
        // 命令类型
        long type = 0x11;
        // 参数0
        long arg0 = address;
        // 参数1
        long arg1 = 0x00;
        // 参数2
        long arg2 = 0x00;
        // 校验和
        // 取低8位
        long checkSum = (HEAD + DEVICE + boardNo + type + arg0 + arg1 + arg2) & 0xFF;

        long cmd = (HEAD << 56) + (DEVICE << 48) + (ADDRESS << 40) + (type << 32) + (arg0 << 24) + (arg1 << 16) + (arg2 << 8) + checkSum;
        // 不满18位高位补0
        return padLeft(Long.toHexString(cmd), 18);
    }

    /**
     * 读继电器状态
     */
    public static String readState() {
        return "005A60FF07000000C0";
    }

    /**
     * 读科大讯飞语音播报状态
     */
    public static String readIflytekState() {
        return "FD000121";
    }

    /**
     * 操作单个继电器
     *
     * @param position 继电器位置
     */
    public static String openSingleCmd(int position) {
        // 命令类型
        long type = 0x01;
        // 参数0
        long arg0 = position;
        // 参数1
        long arg1 = 0x00;
        // 参数2
        long arg2 = 0x00;
        // 校验和
        // 取低8位
        long checkSum = (HEAD + DEVICE + ADDRESS + type + arg0 + arg1 + arg2) & 0xFF;

        long cmd = (HEAD << 56) + (DEVICE << 48) + (ADDRESS << 40) + (type << 32) + (arg0 << 24) + (arg1 << 16) + (arg2 << 8) + checkSum;
        // 不满18位高位补0
        return padLeft(Long.toHexString(cmd), 18);
    }

    private static String padLeft(String s, int length) {
        byte[] bs = new byte[length];
        byte[] ss = s.getBytes();
        Arrays.fill(bs, (byte) (48 & 0xFF));
        System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
        return new String(bs);
    }
}
