package com.project.cmn.util.cipher;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA 암호화 Utility Class
 */
public class Sha {
    private Sha() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * SHA-256 암호화
     *
     * @param plainText 암호화할 평문
     * @return 암호화 된 문자열
     * @throws NoSuchAlgorithmException {@link NoSuchAlgorithmException} SHA-256 algorithm 이 없을 때
     */
    public static String encrypt256(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64URLSafeString(md.digest());
    }

    /**
     * SHA-512 암호화
     *
     * @param plainText 암호화할 평문
     * @return 암호화 된 문자열
     * @throws NoSuchAlgorithmException {@link NoSuchAlgorithmException} SHA-512 algorithm 이 없을 때
     */
    public static String encrypt512(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");

        md.update(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64URLSafeString(md.digest());
    }
}
