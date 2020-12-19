package com.elcom.device.utils;

import com.elcom.device.config.PropertiesConfig;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RsaDecryption {

    public static String decrypt(String encodeData) {
        try {
            // Đọc file chứa private key
//            FileInputStream fis = new FileInputStream("F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignFileService\\config\\rsa-private-key\\privateKey.rsa");
            FileInputStream fis = new FileInputStream(PropertiesConfig.RSA_PRIVATE_KEY);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();

            // Tạo private key
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = factory.generatePrivate(spec);

            // Giải mã dữ liệu
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, priKey);
            byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(encodeData));
            return new String(decryptOut);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
