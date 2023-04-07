package com.project.cmn.util.cipher;

import lombok.NonNull;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * AES 256 암호화 Utility Class
 */
public class Aes256 {
    /**
     * AES algorithm
     */
    private static String aesAlgorithm;

    /**
     * Hex 문자열로 된 secret key
     */
    private static String secretKey;

    /**
     * Hex 문자열로 된 initialization vector
     */
    private static String ivParam;

    /**
     * 생성자
     */
    private Aes256() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 프로젝트 내에서 한번만 셋팅하여 사용할 경우
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param ivParam      Hex 문자열로 된 initialization vector
     */
    public static void setConfig(@NonNull String aesAlgorithm, @NonNull String secretKey, @NonNull String ivParam) {
        // 한번도 셋팅되지 않았을 때만 셋팅 가능
        if (StringUtils.isNotBlank(Aes256.aesAlgorithm)) {
            return;
        }

        Aes256.aesAlgorithm = aesAlgorithm;
        Aes256.secretKey = secretKey;
        Aes256.ivParam = ivParam;
    }

    /**
     * ecret Key 를 생성한다.
     *
     * @return Hex 문자열로 된 Secret Key
     * @throws NoSuchAlgorithmException {@link NoSuchAlgorithmException} AES Algorithm 을 못찾은 경우
     */
    public static String getSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");

        generator.init(256);

        Key key = generator.generateKey();
        byte[] keyBytes = key.getEncoded();

        return Hex.encodeHexString(keyBytes);
    }

    /**
     * Itialization Vector 를 생성한다.
     *
     * @return Hex 문자열로 된 initialization vector
     * @throws NoSuchAlgorithmException {@link NoSuchAlgorithmException} AES Algorithm 을 못찾은 경우
     */
    public static String getInitializationVector() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");

        generator.init(128);

        Key key = generator.generateKey();
        byte[] keyBytes = key.getEncoded();

        return Hex.encodeHexString(keyBytes);
    }

    /**
     * 암호화 한다.
     *
     * @param cipher    {@link Cipher}
     * @param plainText 암호화할 평문
     * @param charset   {@link Charset} 암호활 평문의 문자셋
     * @param isBase64  true: 최종 결과 값을 Base64로 인코딩. false: 최종 결과 값을 Hex 문자열로 변환
     * @return 암호화된 문자열
     * @throws IllegalBlockSizeException {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws BadPaddingException       {@link BadPaddingException} 패딩 오류
     */
    public static String encrypt(Cipher cipher, String plainText, Charset charset, boolean isBase64) throws IllegalBlockSizeException, BadPaddingException {
        byte[] plainTextBytes = plainText.getBytes(charset);
        byte[] encryptBytes = cipher.doFinal(plainTextBytes);

        if (isBase64) {
            return Base64.encodeBase64String(encryptBytes);
        } else {
            return Hex.encodeHexString(encryptBytes);
        }
    }

    /**
     * 암호화한 후 Base64로 인코딩하여 반환한다.
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param ivParam      Hex 문자열로 된 initialization vector
     * @param plainText    암호화할 평문
     * @return 암호화한 후 Base64로 인코딩된 문자열
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws IllegalBlockSizeException          {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws BadPaddingException                {@link BadPaddingException} 패딩 오류
     */
    public static String encrypt(String aesAlgorithm, String secretKey, String ivParam, String plainText) throws DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(aesAlgorithm, secretKey, ivParam, Cipher.ENCRYPT_MODE);

        return encrypt(cipher, plainText, StandardCharsets.UTF_8, true);
    }

    /**
     * 암호화한 후 Base64로 인코딩하여 반환한다.
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param plainText    암호화할 평문
     * @return 암호화한 후 Base64로 인코딩된 문자열
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws IllegalBlockSizeException          {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws BadPaddingException                {@link BadPaddingException} 패딩 오류
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     */
    public static String encrypt(String aesAlgorithm, String secretKey, String plainText) throws DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return encrypt(aesAlgorithm, secretKey, secretKey, plainText);
    }

    /**
     * 복호화 한다.
     *
     * @param cipher      {@link Cipher}
     * @param encryptText 암호화된 문자열
     * @param charset     {@link Charset} 복호화된 문자열의 문자셋
     * @param isBase64    true: 암호화된 문자열이 Base64로 인코딩되어 있음, false: 암호화된 문자열이 Hex 문자열임
     * @return 복호화된 문자열
     * @throws DecoderException          {@link DecoderException} {@link Hex} 로 디코드할 때 발생
     * @throws IllegalBlockSizeException {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws BadPaddingException       {@link BadPaddingException} 패딩 오류
     */
    public static String decrypt(Cipher cipher, String encryptText, Charset charset, boolean isBase64) throws DecoderException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptTextBytes;

        if (isBase64) {
            encryptTextBytes = Base64.decodeBase64(encryptText);
        } else {
            encryptTextBytes = Hex.decodeHex(encryptText);
        }

        byte[] decryptBytes = cipher.doFinal(encryptTextBytes);

        return new String(decryptBytes, charset);
    }

    /**
     * Base64로 디코딩한 후 복호화한다.
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param ivParam      Hex 문자열로 된 initialization vector
     * @param encryptText  암호화된 문자열
     * @return 복호화된 문자열
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws IllegalBlockSizeException          {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws BadPaddingException                {@link BadPaddingException} 패딩 오류
     */
    public static String decrypt(String aesAlgorithm, String secretKey, String ivParam, String encryptText) throws DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(aesAlgorithm, secretKey, ivParam, Cipher.DECRYPT_MODE);

        return decrypt(cipher, encryptText, StandardCharsets.UTF_8, true);
    }

    /**
     * Base64로 디코딩한 후 복호화한다.
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param encryptText  암호화된 문자열
     * @return 복호화된 문자열
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws IllegalBlockSizeException          {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws BadPaddingException                {@link BadPaddingException} 패딩 오류
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     */
    public static String decrypt(String aesAlgorithm, String secretKey, String encryptText) throws DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return decrypt(aesAlgorithm, secretKey, secretKey, encryptText);
    }

    /**
     * {@link Cipher}를 가져온다.
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param cryptMode    암복화화 구분
     * @return 생성된 {@link Cipher}
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     */
    public static Cipher getCipher(String aesAlgorithm, String secretKey, int cryptMode) throws DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        return getCipher(aesAlgorithm, secretKey, secretKey, cryptMode);
    }

    /**
     * {@link Cipher}를 가져온다.
     *
     * @param aesAlgorithm AES algorithm
     * @param secretKey    Hex 문자열로 된 secret key
     * @param ivParam      Hex 문자열로 된 initialization vector
     * @param cryptMode    암복화화 구분
     * @return 생성된 {@link Cipher}
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     */
    public static Cipher getCipher(String aesAlgorithm, String secretKey, String ivParam, int cryptMode) throws DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] secretKeyBytes = new byte[32];
        byte[] secretKeyData = Hex.decodeHex(secretKey);

        System.arraycopy(secretKeyData, 0, secretKeyBytes, 0, secretKeyBytes.length);

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
        Cipher cipher = Cipher.getInstance(aesAlgorithm);

        if (aesAlgorithm.contains("ECB")) {
            cipher.init(cryptMode, secretKeySpec);
        } else {
            byte[] ivParamBytes = new byte[16];
            byte[] ivParamData = Hex.decodeHex(ivParam);

            System.arraycopy(ivParamData, 0, ivParamBytes, 0, ivParamBytes.length);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParamBytes);
            cipher.init(cryptMode, secretKeySpec, ivParameterSpec);
        }

        return cipher;
    }

    /**
     * 암호화한 후 Base64로 인코딩하여 반환한다.
     * Aes256.setConfig() 를 실행한 후에 사용 가능하다.
     *
     * @param plainText 암호화한 후 Base64로 인코딩된 문자열
     * @return 암호화한 후 Base64로 인코딩된 문자열
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws IllegalBlockSizeException          {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws BadPaddingException                {@link BadPaddingException} 패딩 오류
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     */
    public static String encrypt(String plainText) throws DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return encrypt(Aes256.aesAlgorithm, Aes256.secretKey, Aes256.ivParam, plainText);
    }

    /**
     * Base64로 디코딩한 후 복호화한다.
     * Aes256.setConfig() 를 실행한 후에 사용 가능하다.
     *
     * @param encryptText 암호화된 문자열
     * @return 복호화된 문자열
     * @throws DecoderException                   {@link DecoderException} Hex 문자열로된 secretKey 를 decode 할 때 발생
     * @throws InvalidAlgorithmParameterException {@link InvalidAlgorithmParameterException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws NoSuchPaddingException             {@link NoSuchPaddingException} {@link Cipher}를 생성할 때 발생
     * @throws NoSuchAlgorithmException           {@link NoSuchAlgorithmException} {@link Cipher}를 생성할 때 발생
     * @throws InvalidKeyException                {@link InvalidKeyException} 암호화키 값을 이용하여 {@link Cipher}를 초기화할 때 발생
     * @throws IllegalBlockSizeException          {@link IllegalBlockSizeException} secret key, initialization vector 사이즈 오류
     * @throws BadPaddingException                {@link BadPaddingException} 패딩 오류
     */
    public static String decrypt(String encryptText) throws DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return decrypt(Aes256.aesAlgorithm, Aes256.secretKey, Aes256.ivParam, encryptText);
    }
}
