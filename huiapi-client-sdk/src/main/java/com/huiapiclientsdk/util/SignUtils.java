package com.huiapiclientsdk.util;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具
 * @author 顾琴
 */
public class SignUtils {
    /**
     * 生成签名
     * @param body
     * @param secretKey
     * @return
     */
    public static String genSecret(String body, String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        return md5.digestHex(body+"."+secretKey);
    }
}
