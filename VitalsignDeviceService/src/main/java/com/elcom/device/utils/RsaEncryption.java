package com.elcom.device.utils;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RsaEncryption {

    public static void main(String[] args) {
        try {
            // Đọc file chứa public key
            FileInputStream fis = new FileInputStream("F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignFileService\\config\\rsa-private-key\\publicKey.rsa");
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();

            // Tạo public key
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = factory.generatePublic(spec);

            // Mã hoá dữ liệu
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, pubKey);
            String plainData = "ELC-G-001";
            System.out.println("Chuỗi ban đầu: " + plainData);
            byte encryptOut[] = c.doFinal(plainData.getBytes());
            String encodedData = Base64.getEncoder().encodeToString(encryptOut);
            System.out.println("Chuỗi sau khi mã hoá: " + encodedData);
            System.out.println("Chuỗi sau khi giải mã: " + RsaDecryption.decrypt(encodedData));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
