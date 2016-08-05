package com.app.phonesafe.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码MD5处理
 */
public class MD5Util {
    public static String md5(String psd){
        psd=psd+"phonesafe";//加盐处理
        byte[] hash;
        try {
            //1,指定加密算法类型(MD5),并将需要加密的字符串中转换成byte类型的数组,然后进行随机哈希过程
            hash= MessageDigest.getInstance("MD5").digest(psd.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 should be supported?",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should be supported?",e);
        }
        //2,循环遍历bs,然后让其生成32位字符串,固定写法
        //3,拼接字符串过程
        StringBuilder hex=new StringBuilder(hash.length*2);
        for(byte b:hash){
            if((b & 0xFF)<0x10)hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
