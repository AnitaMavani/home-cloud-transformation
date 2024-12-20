package com.homecloud.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class FileEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "mykey12345678900";

    public static byte[] encrypt(byte[] fileData) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return Base64.encodeBase64(cipher.doFinal(fileData));
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(Base64.decodeBase64(encryptedData));
    }
}