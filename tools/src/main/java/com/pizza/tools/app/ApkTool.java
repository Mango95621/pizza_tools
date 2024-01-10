package com.pizza.tools.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

/**
 * @author BoWei
 * 11/25/20 10:40 AM
 */
public class ApkTool {
    /**
     * 从 apk 中获取 MD5 签名信息
     * @param apkPath
     * @return
     * @throws Exception
     */
    public static String getApkSignatureMD5(String apkPath) {
        byte[] bytes = getSignaturesFromApk(apkPath);
        if (bytes == null) {
            return "";
        }
        return hexDigest(bytes, "MD5");
    }

    /**
     * 从APK中读取签名
     *
     * @param apkPath
     * @return
     * @throws IOException
     */
    public static byte[] getSignaturesFromApk(String apkPath) {
        try {
            File file = new File(apkPath);
            JarFile jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null && certs.length > 0) {
                return certs[0].getEncoded();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String hexDigest(byte[] bytes, String algorithm) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(bytes);
        StringBuffer hexValue = new StringBuffer();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 加载签名
     *
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    public static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getApkSignatureSHA1(String apkPath) {
        byte[] bytes = getSignaturesFromApk(apkPath);
        if (bytes == null) {
            return "";
        }
        return hexDigest(bytes, "SHA1");
    }

    public static String getApkSignatureSHA256(String apkPath) {
        byte[] bytes = getSignaturesFromApk(apkPath);
        if (bytes == null) {
            return "";
        }
        return hexDigest(bytes, "SHA256");
    }

    /**
     * 获取已经安装的 app 的 MD5 签名信息
     *
     * @param context
     * @param pkgName
     *
     * @return
     */
    public static String getAppSignatureMD5(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "MD5");
    }

    public static String getAppSignature(Context context, String pkgName, String algorithm) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    pkgName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return hexDigest(sign.toByteArray(), algorithm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAppSignatureSHA1(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "SHA1");
    }

    public static String getAppSignatureSHA256(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "SHA256");
    }

}