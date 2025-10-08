//package com.Nikhil.CreditCardSystem.util;
//
//
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
//
//public class AESUtil {
//    private static final String SECRET_KEY = "MySecretKey12345"; // 16 chars for AES-128
//
//    public static String encrypt(String data) {
//        if (data == null) return null;
//        try {
//            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
//            return Base64.getEncoder().encodeToString(encryptedBytes);
//        } catch (Exception e) {
//            throw new RuntimeException("Error while encrypting: " + e.getMessage());
//        }
//    }
//
//    public static String decrypt(String encryptedData) {
//        if (encryptedData == null) return null;
//        try {
//            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
//            return new String(cipher.doFinal(decodedBytes));
//        } catch (Exception e) {
//            throw new RuntimeException("Error while decrypting: " + e.getMessage());
//        }
//    }
//}
//
