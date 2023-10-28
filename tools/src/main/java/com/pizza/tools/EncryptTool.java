package com.pizza.tools;

import android.util.Base64;

import com.pizza.tools.file.FileTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Kyle
 * 加密解密相关的工具类
 */
public class EncryptTool {

    public static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /*********************** 哈希加密相关 ***********************/
    private static final String DES_Algorithm = "DES";
    private static final String TripleDES_Algorithm = "DESede";
    private static final String AES_Algorithm = "AES";
    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static final String RSA_Algorithm = "RSA/NONE/PKCS1Padding";
    /**
     * DES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static String DES_Transformation = "DES/ECB/NoPadding";
    /**
     * 3DES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static String TripleDES_Transformation = "DESede/ECB/NoPadding";
    /**
     * AES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static String AES_Transformation = "AES/ECB/NoPadding";

    /**
     * MD2加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String md2ToString(String data) {
        return md2ToString(data.getBytes());
    }

    /**
     * MD2加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String md2ToString(byte[] data) {
        return DataTool.bytes2HexString(md2(data));
    }

    /**
     * MD2加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] md2(byte[] data) {
        return algorithm(data, "MD2");
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的16进制密文
     */
    public static String md5File2String(String filePath) {
        return md5File2String(new File(filePath));
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    public static byte[] md5File(String filePath) {
        return md5File(new File(filePath));
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的16进制密文
     */
    public static String md5File2String(File file) {
        return md5File(file) != null ? DataTool.bytes2HexString(md5File(file)) : "";
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    public static byte[] md5File(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileChannel channel = fis.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(buffer);
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            FileTool.get().getFileOperatorUtil().closeIo(fis);
        }
        return null;
    }

    /**
     * 十六进制
     *
     * @param buffer
     * @return
     */
    public static String md5(byte[] buffer) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.reset();
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(String str) {
        return md5(str.getBytes());
    }

    /**
     * SHA1加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String sha1ToString(String data) {
        return sha1ToString(data.getBytes());
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String sha1ToString(byte[] data) {
        return DataTool.bytes2HexString(sha1(data));
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] sha1(byte[] data) {
        return algorithm(data, "SHA-1");
    }

    /**
     * SHA224加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String sha224ToString(String data) {
        return sha224ToString(data.getBytes());
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String sha224ToString(byte[] data) {
        return DataTool.bytes2HexString(sha224(data));
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] sha224(byte[] data) {
        return algorithm(data, "SHA-224");
    }

    /**
     * SHA256加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String sha256ToString(String data) {
        return sha256ToString(data.getBytes());
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String sha256ToString(byte[] data) {
        return DataTool.bytes2HexString(sha256(data));
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] sha256(byte[] data) {
        return algorithm(data, "SHA-256");
    }

    /**
     * SHA384加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String sha384ToString(String data) {
        return sha384ToString(data.getBytes());
    }

    /************************ DES加密相关 ***********************/

    /**
     * SHA384加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String sha384ToString(byte[] data) {
        return DataTool.bytes2HexString(sha384(data));
    }

    /**
     * SHA384加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] sha384(byte[] data) {
        return algorithm(data, "SHA-384");
    }

    /**
     * SHA512加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String sha512ToString(String data) {
        return sha512ToString(data.getBytes());
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String sha512ToString(byte[] data) {
        return DataTool.bytes2HexString(sha512(data));
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] sha512(byte[] data) {
        return algorithm(data, "SHA-512");
    }

    /**
     * 对data进行algorithm算法加密
     *
     * @param data      明文字节数组
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private static byte[] algorithm(byte[] data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * @param data           数据
     * @param key            秘钥
     * @param algorithm      采用何种DES算法
     * @param transformation 转变
     * @param isEncrypt      是否加密
     * @return 密文或者明文，适用于DES，3DES，AES
     */
    public static byte[] desTemplet(byte[] data, byte[] key, String algorithm, String transformation, boolean isEncrypt) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            Cipher cipher = Cipher.getInstance(transformation);
            SecureRandom random = new SecureRandom();
            cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, random);
            return cipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return Base64密文
     */
    public static byte[] des2Base64(byte[] data, byte[] key) {
        return EncodeTool.base64Encode(des(data, key), Base64.NO_WRAP);
    }

    /**
     * DES加密后转为16进制
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 16进制密文
     */
    public static String des2HexString(byte[] data, byte[] key) {
        return DataTool.bytes2HexString(des(data, key));
    }

    /************************ 3DES加密相关 ***********************/

    /**
     * DES加密
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 密文
     */
    public static byte[] des(byte[] data, byte[] key) {
        return desTemplet(data, key, DES_Algorithm, DES_Transformation, true);
    }

    /**
     * DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  8字节秘钥
     * @return 明文
     */
    public static byte[] decryptBase64DES(byte[] data, byte[] key) {
        return decryptDES(EncodeTool.base64Decode(data), key);
    }

    /**
     * DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  8字节秘钥
     * @return 明文
     */
    public static byte[] decryptHexStringDES(String data, byte[] key) {
        return decryptDES(DataTool.hexString2Bytes(data), key);
    }

    /**
     * DES解密
     *
     * @param data 密文
     * @param key  8字节秘钥
     * @return 明文
     */
    public static byte[] decryptDES(byte[] data, byte[] key) {
        return desTemplet(data, key, DES_Algorithm, DES_Transformation, false);
    }

    /**
     * 3DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return Base64密文
     */
    public static byte[] encrypt3DES2Base64(byte[] data, byte[] key) {
        return EncodeTool.base64Encode(encrypt3DES(data, key), Base64.NO_WRAP);
    }

    /**
     * 3DES加密后转为16进制
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return 16进制密文
     */
    public static String encrypt3DES2HexString(byte[] data, byte[] key) {
        return DataTool.bytes2HexString(encrypt3DES(data, key));
    }

    /**
     * 3DES加密
     *
     * @param data 明文
     * @param key  24字节密钥
     * @return 密文
     */
    public static byte[] encrypt3DES(byte[] data, byte[] key) {
        return desTemplet(data, key, TripleDES_Algorithm, TripleDES_Transformation, true);
    }

    /**
     * 3DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  24字节秘钥
     * @return 明文
     */
    public static byte[] decryptBase64_3DES(byte[] data, byte[] key) {
        return decrypt3DES(EncodeTool.base64Decode(data), key);
    }

    /************************ AES加密相关 ***********************/

    /**
     * 3DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  24字节秘钥
     * @return 明文
     */
    public static byte[] decryptHexString3DES(String data, byte[] key) {
        return decrypt3DES(DataTool.hexString2Bytes(data), key);
    }

    /**
     * 3DES解密
     *
     * @param data 密文
     * @param key  24字节密钥
     * @return 明文
     */
    public static byte[] decrypt3DES(byte[] data, byte[] key) {
        return desTemplet(data, key, TripleDES_Algorithm, TripleDES_Transformation, false);
    }

    /**
     * AES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return Base64密文
     */
    public static byte[] encryptAES2Base64(byte[] data, byte[] key) {
        return EncodeTool.base64Encode(encryptAES(data, key), Base64.NO_WRAP);
    }

    /**
     * AES加密后转为16进制
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 16进制密文
     */
    public static String encryptAES2HexString(byte[] data, byte[] key) {
        return DataTool.bytes2HexString(encryptAES(data, key));
    }

    /**
     * AES加密
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 密文
     */
    public static byte[] encryptAES(byte[] data, byte[] key) {
        return desTemplet(data, key, AES_Algorithm, AES_Transformation, true);
    }

    /**
     * AES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    public static byte[] decryptBase64AES(byte[] data, byte[] key) {
        return decryptAES(EncodeTool.base64Decode(data), key);
    }

    /**
     * AES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    public static byte[] decryptHexStringAES(String data, byte[] key) {
        return decryptAES(DataTool.hexString2Bytes(data), key);
    }

    /**
     * AES解密
     *
     * @param data 密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    public static byte[] decryptAES(byte[] data, byte[] key) {
        return desTemplet(data, key, AES_Algorithm, AES_Transformation, false);
    }

    /************************ RSA加密相关 ***********************/

    /**
     * RSA产生密钥对
     *
     * @param keyLength 密钥长度，小于1024长度的密钥已经被证实是不安全的，通常设置为1024或者2048，建议2048
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            //设置密钥长度
            keyPairGenerator.initialize(keyLength);
            //产生密钥对
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return keyPair;
    }

    /**
     * 加密或解密数据的通用方法
     *
     * @param srcData 待处理的数据
     * @param key     公钥或者私钥
     * @param mode    指定是加密还是解密，值为Cipher.ENCRYPT_MODE或者Cipher.DECRYPT_MODE
     */
    private static byte[] processData(byte[] srcData, Key key, int mode) {
        //用来保存处理结果
        byte[] resultBytes = null;
        try {
            //获取Cipher实例
            Cipher cipher = Cipher.getInstance(RSA_Algorithm);
            //初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(mode, key);
            //处理数据
            resultBytes = cipher.doFinal(srcData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultBytes;
    }

    /**
     * 使用公钥加密数据，结果用Base64转码
     */
    public static String encryptDataByPublicKey(byte[] srcData, String publicKeyStr) {
        byte[] resultBytes = processData(srcData,
                keyStrToPublicKey(publicKeyStr),
                Cipher.ENCRYPT_MODE);
        return EncodeTool.base64Encode2String(resultBytes, Base64.NO_WRAP);
    }

    /**
     * 使用私钥解密，返回解码数据
     */
    public static byte[] decryptDataByPrivate(String encryptedData, String privateKeyStr) {

        byte[] bytes = EncodeTool.base64Decode(encryptedData);

        return processData(bytes,
                keyStrToPrivate(privateKeyStr),
                Cipher.DECRYPT_MODE);
    }

    /**
     * 使用私钥进行解密，解密数据转换为字符串，使用utf-8编码格式
     */
    public static String decryptedToStrByPrivate(String encryptedData, String privateKeyStr) {
        return new String(decryptDataByPrivate(encryptedData, privateKeyStr));
    }

    /**
     * 使用私钥解密，解密数据转换为字符串，并指定字符集
     */
    public static String decryptedToStrByPrivate(String encryptedData,
                                                 String privateKeyStr, String charset) {
        try {
            return new String(decryptDataByPrivate(encryptedData, privateKeyStr), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 使用私钥加密，结果用Base64转码
     */
    public static String encryptDataByPrivateKey(byte[] srcData, String privateKeyStr) {
        byte[] resultBytes = processData(srcData,
                keyStrToPrivate(privateKeyStr),
                Cipher.ENCRYPT_MODE);

        return EncodeTool.base64Encode2String(resultBytes, Base64.NO_WRAP);
    }

    /**
     * 使用公钥解密，返回解密数据
     */
    public static byte[] decryptDataByPublicKey(String encryptedData, String publicKeyStr) {
        if (encryptedData.isEmpty()) {
            return new byte[]{};
        }
        byte[] bytes = EncodeTool.base64Decode(encryptedData);

        return processData(bytes, keyStrToPublicKey(publicKeyStr), Cipher.DECRYPT_MODE);

    }

    /**
     * 使用公钥解密，结果转换为字符串，使用默认字符集utf-8
     */
    public static String decryptedToStrByPublicKey(String encryptedData, String publicKeyStr) {
        return new String(decryptDataByPublicKey(encryptedData, publicKeyStr));
    }

    /**
     * 使用公钥解密，结果转换为字符串，使用指定字符集
     */
    public static String decryptedToStrByPublicKey(String encryptedData,
                                                   String publicKeyStr, String charset) {
        try {
            return new String(decryptDataByPublicKey(encryptedData, publicKeyStr), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将字符串形式的公钥转换为公钥对象
     */
    private static PublicKey keyStrToPublicKey(String publicKeyStr) {
        PublicKey publicKey = null;
        byte[] keyBytes = EncodeTool.base64Decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * 将字符串形式的私钥，转换为私钥对象
     */
    private static PrivateKey keyStrToPrivate(String privateKeyStr) {
        PrivateKey privateKey = null;

        byte[] keyBytes = EncodeTool.base64Decode(privateKeyStr);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
}