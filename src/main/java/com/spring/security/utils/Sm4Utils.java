package com.spring.security.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * 国密SM4分组密码算法工具类(对称加密)
 * <p>
 * GB/T 32907-2016 信息安全技术 SM4分组密码算法
 * <p>
 * See Also: GB/T32907-2016
 */

public class Sm4Utils {

    private static final String ALGORITHM_NAME = "SM4";

    private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";

    private static final String ALGORITHM_ECB_PKCS7PADDING = "SM4/ECB/PKCS7Padding";
    private static final String ALGORITHM_CBC_PKCS5PADDING = "SM4/CBC/PKCS5Padding";
    private static final String ALGORITHM_CBC_PKCS7PADDING = "SM4/CBC/PKCS7Padding";

    /**
     * SM4算法目前只支持128位(即密钥16字节)
     */
    private static final int DEFAULT_KEY_SIZE = 128;

    static {
        // 防止内存中出现多次BouncyCastleProvider的实例
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private Sm4Utils() {}

    /**
     * 生成密钥
     * <p>
     * 建议使用org.bouncycastle.util.encoders.Hex将二进制转成HEX字符串
     *
     * @throws GeneralSecurityException 生成密钥异常
     * @return 密钥16位
     */

    public static byte[] generateKey() throws GeneralSecurityException {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

//    /**
//     * 生成CBC模式下的向量
//     *
//     * @return 长度为19的字节数组
//     */
//    public static byte[] generateIv() {
//        return IdUtils.get62RadixUuid().substring(3).getBytes(StandardCharsets.UTF_8);
//    }

    /**
     * 加密，SM4-ECB-PKCS5Padding
     *
     * @param data 要加密的明文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 加密异常
     * @return 加密后的密文
     */
    public static byte[] encryptEcbPkcs5Padding(byte[] data, byte[] key) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.ENCRYPT_MODE);
    }
    /**
     * 解密，SM4-ECB-PKCS5Padding
     *
     * @param data 要解密的密文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 解密异常
     * @return 解密后的明文
     */
    public static byte[] decryptEcbPkcs5Padding(byte[] data, byte[] key) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.DECRYPT_MODE);
    }
    /**
     * 加密，SM4-ECB-PKC75Padding
     *
     * @param data 要加密的明文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 加密异常
     * @return 加密后的密文
     */
    public static byte[] encryptEcbPkcs7Padding(byte[] data, byte[] key) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_ECB_PKCS7PADDING, null, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密，SM4-ECB-PKCS7Padding
     *
     * @param data 要解密的密文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 解密异常
     * @return 解密后的明文
     */
    public static byte[] decryptEcbPkcs7Padding(byte[] data, byte[] key) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_ECB_PKCS7PADDING, null, Cipher.DECRYPT_MODE);
    }

    /**
     * 加密，SM4-CBC-PKCS5Padding
     *
     * @param data 要加密的明文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 解密异常
     * @return 解密后的明文
     */
    public static byte[] encryptCbcPkcs5Padding(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_CBC_PKCS5PADDING, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密，SM4-CBC-PKCS5Padding
     *
     * @param data 要解密的密文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 解密异常
     * @return 解密后的明文
     */
    public static byte[] decryptCbcPkcs5Padding(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_CBC_PKCS5PADDING, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * 加密，SM4-CBC-PKCS7Padding
     *
     * @param data 要加密的明文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 解密异常
     * @return 解密后的明文
     */
    public static byte[] encryptCbcPkcs7Padding(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_CBC_PKCS7PADDING, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密，SM4-CBC-PKCS7Padding
     *
     * @param data 要解密的密文
     * @param key  密钥16字节，使用Sm4Util.generateKey生成
     * @throws GeneralSecurityException 解密异常
     * @return 解密后的明文
     */
    public static byte[] decryptCbcPkcs7Padding(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        return sm4(data, key, ALGORITHM_CBC_PKCS7PADDING, iv, Cipher.DECRYPT_MODE);
    }


    /**
     * SM4对称加解密
     *
     * @param input   明文(加密模式)或密文(解密模式)
     * @param key     密钥
     * @param sm4mode sm4加密模式
     * @param iv      初始向量(ECB模式下传NULL)
     * @param mode    Cipher.ENCRYPT_MODE - 加密；Cipher.DECRYPT_MODE - 解密
     * @throws GeneralSecurityException 加解密异常
     * @return 密文(加密模式)或明文(解密模式)
     */

    private static byte[] sm4(byte[] input, byte[] key, String sm4mode, byte[] iv, int mode) throws GeneralSecurityException {
        IvParameterSpec ivParameterSpec = null;
        if (null != iv) {
            ivParameterSpec = new IvParameterSpec(iv);
        }
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(sm4mode, BouncyCastleProvider.PROVIDER_NAME);
        if (null == ivParameterSpec) {
            cipher.init(mode, sm4Key);
        } else {
            cipher.init(mode, sm4Key, ivParameterSpec);
        }
        return cipher.doFinal(input);
    }
}