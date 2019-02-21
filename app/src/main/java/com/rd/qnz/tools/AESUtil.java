package com.rd.qnz.tools;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;

public class AESUtil {
    private final static String ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";
    private final static String ALGORITHMSHA = "SHA-512";
    private final static StandardPBEStringEncryptor ENCRYPTOR = new StandardPBEStringEncryptor();
    private final static ConfigurablePasswordEncryptor PASSWORD_ENCRYPTOR = new ConfigurablePasswordEncryptor();

    static {
        ENCRYPTOR.setProvider(new BouncyCastleProvider());
        ENCRYPTOR.setAlgorithm(ALGORITHM);
        ENCRYPTOR.setPassword("q!@e*tu");//最多7位
        ENCRYPTOR.setSaltGenerator(new ZeroSaltGenerator());
        PASSWORD_ENCRYPTOR.setAlgorithm(ALGORITHMSHA);
        PASSWORD_ENCRYPTOR.setPlainDigest(true);
    }

    /**
     * 加密
     *
     * @param userId
     * @return
     */
    public static String encrypt(final String encodeStr) {

        return ENCRYPTOR.encrypt(encodeStr);
    }

    /**
     * 解密
     *
     * @param decodeStr
     * @return
     */
    public static String decrypt(final String decodeStr) {
        return ENCRYPTOR.decrypt(decodeStr);
    }


    public static String passwordEncryptor(final String userPassword) {
        return PASSWORD_ENCRYPTOR.encryptPassword(userPassword);
    }

    public static boolean passwordCheck(final String inputPassword, final String encryptedPassword) {
        boolean flag = false;
        if (PASSWORD_ENCRYPTOR.checkPassword(inputPassword, encryptedPassword)) {
            flag = true;
        }
        return flag;
    }

    public static void main(String[] args) {
        String a = "13588036364";
        String b = encrypt(a);
        String c = "GC5mkvi6C+3SyfP/cxItHw==";
        System.out.println(c.length());
        System.out.println("en:" + b);
        System.out.println("de:" + decrypt(b));
        System.out.println("de2:" + decrypt(c));
    }
}
