package com.pizza.tools.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.annotation.Size;
import androidx.fragment.app.Fragment;

import com.pizza.tools.ThreadPoolTool;
import com.pizza.tools.ToastTool;
import com.pizza.tools.file.FileTool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Description:
 * 蓝牙工具类
 *
 * @author Kyle
 * 2020/5/7 11:47 AM
 */
public class BluetoothTool {

    public static final String TAG = "BluetoothTool";
    public static final int BLUETOOTH_OPEN_CODE = 1;
    public static final int BLUETOOTH_VISIBLE_CODE = 2;
    public static final int DISCONNECTED = 10;
    public static final int CONNECTED = 11;
    public static final int MSG = 12;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int FLAG_MSG = 0;
    private static final int FLAG_FILE = 1;
    private static final UUID A2DP_SRC_UUID = UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB");
    private static final UUID A2DP_SINK_UUID = UUID.fromString("0000110B-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mBluetoothAdapter;
//    private BluetoothLeScanner
    /**
     * 用来调度蓝牙通信的线程池
     */
    private ThreadPoolTool threadPool;
    /**
     * 通讯的 socket
     * 客户端和服务端由此进行通信
     * 客户端通过 socket 发送消息或文件
     * 服务端获取 socket 收取消息或文件
     */
    private BluetoothSocket bluetoothSocket;
    /**
     * 服务端的 socket，通过获取客户端 socket 进行通信
     */
    private BluetoothServerSocket bluetoothServerSocket;
    /**
     * 蓝牙输出的流
     */
    private DataOutputStream bluetoothOutStream;
    /**
     * 当前是否正在进行读操作
     */
    private boolean isReading;
    /**
     * 当前是否正在进行发送操作
     */
    private boolean isSending;

    private BluetoothHeadset bluetoothHeadset;

    private BluetoothSocketListener listener;

    public BluetoothTool() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        threadPool = new ThreadPoolTool().cacheThreadPool(3);
    }

    /**
     * 是否支持蓝牙 Ble
     * 低功耗蓝牙
     *
     * @param context
     * @return
     */
    public static boolean isSupportBle(Context context) {
        return !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public void openBluetooth(Activity activity) {
        if (!isBluetoothOpen()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, BLUETOOTH_OPEN_CODE);
        }
    }

    public void openBluetooth(Fragment fragment) {
        if (!isBluetoothOpen()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragment.startActivityForResult(intent, BLUETOOTH_OPEN_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    public void closeBluetooth() {
        stopScan();
        mBluetoothAdapter.disable();
    }

    /**
     * 绑定配置文件
     *
     * @param context
     */
    public void bindProfile(Context context) {
        // 设置监听（监听连接状态）
        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = (BluetoothHeadset) proxy;
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = null;
                }
            }
        };

        // 建立与配置文件代理的连接
        mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.HEADSET);
        // 使用 mBluetoothHeadset 代理内部的方法

        // 使用完毕后关闭
        mBluetoothAdapter.closeProfileProxy(0, bluetoothHeadset);
    }

    /**
     * @param activity
     * @param duration 0-3600 0为 一直可以搜索到，系统默认为 120 秒，
     *                 任何小于 0 或者大于 3600 的值都会自动设置为 120 秒钟。
     */
    public void requestBluetoothVisible(Activity activity, @Size(min = 0, max = 3600) Integer duration) {
        if (isBluetoothHas()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            if (duration != null) {
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
            }
            activity.startActivityForResult(intent, BLUETOOTH_VISIBLE_CODE);
        }
    }

    /**
     * @param fragment
     * @param duration 0-3600 0为 一直可以搜索到，系统默认为 120 秒，
     *                 任何小于 0 或者大于 3600 的值都会自动设置为 120 秒钟。
     */
    public void requestBluetoothVisible(Fragment fragment, @Size(min = 0, max = 3600) Integer duration) {
        if (isBluetoothHas()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            if (duration != null) {
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
            }
            fragment.startActivityForResult(intent, BLUETOOTH_VISIBLE_CODE);
        }
    }

    /**
     * 获取已连接的蓝牙设备
     * @return
     */
    @SuppressLint("MissingPermission")
    public Set<BluetoothDevice> getBondedDeviceList() {
        if (isBluetoothOpen()) {
            return mBluetoothAdapter.getBondedDevices();
        }
        return null;
    }

    /**
     * 反射获取已连接的蓝牙设备
     *
     * @return
     */
    public Set<BluetoothDevice> reflexGetBondDeviceList() {
        try {
            Set<BluetoothDevice> deviceList = new HashSet<>();
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            // 得到BluetoothAdapter的Class对象
            Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;
            // 得到连接状态的方法
            Method method =
                    bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            // 打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Set<BluetoothDevice> devices = getBondedDeviceList();
                if (devices == null) {
                    return null;
                }
                // 集合里面包括已绑定的设备和已连接的设备
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod =
                            BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected =
                            (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        // 根据状态来区分是已连接的还是已绑定的，isConnected为true表示是已连接状态。
                        deviceList.add(device);
                    }
                }
            }
            return deviceList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public boolean isDiscovering() {
        return mBluetoothAdapter.isDiscovering();
    }

    /**
     * 进行配对
     *
     * @param device
     */
    @SuppressLint("MissingPermission")
    public void createBond(BluetoothDevice device) {
        device.createBond();
    }

    /**
     * 移除配对
     *
     * @param device
     */
    public void removeBond(BluetoothDevice device) {
        try {
            Method method = BluetoothDevice.class.getMethod("removeBond");
            method.invoke(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void registerMonitorReceiver(Activity activity, BroadcastReceiver bluetoothMonitor) {
        IntentFilter intentFilter = new IntentFilter();
        /*
         蓝牙设备的扫描状态
         包含额外字段 EXTRA_SCAN_MODE 和 EXTRA_PREVIOUS_SCAN_MODE
         两者分别告诉我们新的和旧的扫描模式。每个字段可能包括：
         SCAN_MODE_CONNECTABLE_DISCOVERABLE(可检测到模式)、
         SCAN_MODE_CONNECTABLE(未处于可检测模式但可以接受连接)、
         SCAN_MODE_NOE(未处于可检测到模式并且无法连接)
         */
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        // 蓝牙发现新设备(未配对的设备)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);
        // 蓝牙开始搜索
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // 蓝牙搜索结束
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 在系统弹出配对框之前(确认/输入配对码)
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        // 设备配对状态改变
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // BluetoothAdapter连接状态
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // BluetoothHeadset连接状态
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        // BluetoothA2dp连接状态
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        // 注册广播
        activity.registerReceiver(bluetoothMonitor, intentFilter);
    }

    private void unregisterMonitorReceiver(Activity activity, BroadcastReceiver bluetoothMonitor) {
        activity.unregisterReceiver(bluetoothMonitor);
    }

    /**
     * 开始扫描蓝牙设备
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public boolean startScan() {
        return mBluetoothAdapter.startDiscovery();
    }

    /**
     * 开始扫描蓝牙设备
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public boolean stopScan() {
        if (isDiscovering()) {
            return mBluetoothAdapter.cancelDiscovery();
        }
        return true;
    }

    public void destory(Activity activity, BroadcastReceiver bluetoothMonitor) {
        stopScan();
        closeServerSocket();
        listener = null;
        unregisterMonitorReceiver(activity, bluetoothMonitor);
    }

    public boolean isBluetoothHas() {
        return mBluetoothAdapter != null;
    }

    @SuppressLint("MissingPermission")
    public boolean isBluetoothOpen() {
        return isBluetoothHas() && mBluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public void connect(BluetoothDevice device) {
        stopScan();
        closeSocket();
        try {
            // 加密传输，Android系统强制配对，弹窗显示配对码
//            final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            // 明文传输(不安全)，无需配对
            final BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            threadPool.execute(() -> {
                loopRead(socket);
            });
        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
        }
    }

    /**
     * 监听客户端发起的连接
     */
    @SuppressLint("MissingPermission")
    public void serverListen() {
        try {
            // 加密传输，Android强制执行配对，弹窗显示配对码
            // mSSocket = adapter.listenUsingRfcommWithServiceRecord(TAG, SPP_UUID);

            // 明文传输(不安全)，无需配对
            bluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(TAG, SPP_UUID);
            // 开启子线程
            threadPool.execute(() -> {
                try {
                    // 监听连接
                    BluetoothSocket socket = bluetoothServerSocket.accept();
                    // 关闭监听，只连接一个设备
                    bluetoothServerSocket.close();
                    // 循环读取
                    loopRead(socket);
                } catch (Throwable e) {
                    closeSocket();
                }
            });
        } catch (Throwable e) {
            closeSocket();
        }
    }

    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    private void loopRead(BluetoothSocket socket) {
        bluetoothSocket = socket;
        try {
            if (!bluetoothSocket.isConnected()) {
                bluetoothSocket.connect();
            }
            notify(CONNECTED, bluetoothSocket.getRemoteDevice());
            bluetoothOutStream = new DataOutputStream(bluetoothSocket.getOutputStream());
            DataInputStream in = new DataInputStream(bluetoothSocket.getInputStream());
            isReading = true;
            while (isReading) {
                // 死循环读取
                switch (in.readInt()) {
                    // 读取短消息
                    case FLAG_MSG:
                        String msg = in.readUTF();
                        notify(MSG, "接收短消息：" + msg);
                        break;
                    // 读取文件
                    case FLAG_FILE:
                        FileTool.get().getFileOperatorUtil().createDirectory(
                                FileTool.get().getCanUseRootPath() + "/bluetooth/");
                        String fileName = in.readUTF();
                        long fileLen = in.readLong();
                        // 读取文件内容
                        long len = 0;
                        int r;
                        byte[] b = new byte[4 * 1024];
                        FileOutputStream out =
                                new FileOutputStream(FileTool.get().getCanUseRootPath() + "/bluetooth/" + fileName);
                        notify(MSG, "正在接收文件(" + fileName + "),请稍后...");
                        while ((r = in.read(b)) != -1) {
                            out.write(b, 0, r);
                            len += r;
                            if (len >= fileLen) {
                                break;
                            }
                        }
                        notify(MSG, "文件接收完成(存放在: SD卡/bluetooth 目录)");
                        break;
                    default:
                        break;
                }
            }
        } catch (Throwable e) {
            closeSocket();
        }
    }

    /**
     * 关闭Socket连接
     */
    private void closeSocket() {
        try {
            if (bluetoothSocket != null) {
                isReading = false;
                bluetoothSocket.close();
            }
            notify(DISCONNECTED, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        closeSocket();
        try {
            if (bluetoothServerSocket != null) {
                bluetoothServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setListener(BluetoothSocketListener listener) {
        this.listener = listener;
    }

    /**
     * 当前设备与指定设备是否连接
     */
    public boolean isConnected(BluetoothDevice dev) {
        boolean connected = (bluetoothSocket != null && bluetoothSocket.isConnected());
        if (dev == null) {
            return connected;
        }
        return connected && bluetoothSocket.getRemoteDevice().equals(dev);
    }

    private void notify(int state, Object obj) {
        threadPool.runOnUi(() -> {
            if (listener != null) {
                listener.onHandler(state, obj);
            }
        });
    }

    /**
     * 发送短消息
     */
    public void sendMsg(String msg) {
        if (checkSend()) {
            return;
        }
        isSending = true;
        try {
            //消息标记
            bluetoothOutStream.writeInt(FLAG_MSG);
            bluetoothOutStream.writeUTF(msg);
            bluetoothOutStream.flush();
            notify(MSG, "发送短消息：" + msg);
        } catch (Throwable e) {
            closeSocket();
        }
        isSending = false;
    }

    /**
     * 发送文件
     */
    public void sendFile(final String filePath) {
        if (checkSend()) {
            return;
        }
        isSending = true;
        threadPool.execute(() -> {
            try {
                FileInputStream in = new FileInputStream(filePath);
                File file = new File(filePath);
                bluetoothOutStream.writeInt(FLAG_FILE); //文件标记
                bluetoothOutStream.writeUTF(file.getName()); //文件名
                bluetoothOutStream.writeLong(file.length()); //文件长度
                int r;
                byte[] b = new byte[4 * 1024];
                notify(MSG, "正在发送文件(" + filePath + "),请稍后...");
                while ((r = in.read(b)) != -1) {
                    bluetoothOutStream.write(b, 0, r);
                }
                bluetoothOutStream.flush();
                notify(MSG, "文件发送完成.");
            } catch (Throwable e) {
                closeSocket();
            }
            isSending = false;
        });
    }

    private boolean checkSend() {
        if (isSending) {
            ToastTool.showToast("正在发送其它数据,请稍后再试");
            return true;
        }
        return false;
    }

    public interface BluetoothSocketListener {
        void onHandler(int state, Object obj);
    }

}
