package org.suai.universityapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * Класс для генерации уникальных id
 */
public class IdGenerator {

    /**
     * Метод генерации уникальных id
     * @param message сообщение
     * @param uid id сообщения
     * @return уникальный id
     */
    static String generate(String message, String uid) {

        String firstPart  = hash(uid);
        String secondPart = hash(message);

        String firstHash = hash(firstPart.concat(secondPart));

        SecureRandom random = new SecureRandom();
        long randLong = random.nextLong();
        String thirdPart = hash(Long.toString(randLong));

        return hash(firstHash.concat(thirdPart));
    }

    /**
     * Метод генерации md5-хэша
     * @param s строка
     * @return хэш(строка)
     */
    private static String hash(String s) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(digest).update(s.getBytes());
        byte[] messageDigest = digest.digest();

        // Create Hex String
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));

        return hexString.toString();
    }
}
