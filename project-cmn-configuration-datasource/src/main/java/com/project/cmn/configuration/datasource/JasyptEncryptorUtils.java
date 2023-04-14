package com.project.cmn.configuration.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * {@link DataSourceItem} 에서 암호화된 필드가 있을 경우 복호화를 지원하기 위한 Utility 클래스
 */
@Slf4j
public class JasyptEncryptorUtils {
    private JasyptEncryptorUtils() {}

    /**
     * JASYPT 로 암호화가 된 필드가 포함된 객체를 받아 복호화한 후 해당 필드에 값을 셋팅한다.
     *
     * @param encryptor JASYPT 암복호화 객체
     * @param source JASYPT 로 암호화가 된 필드가 포함된 객체
     */
    public static void decrypt(PooledPBEStringEncryptor encryptor, Object source) {
        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == String.class) {
                decrypt(encryptor, source, field);
            }
        }
    }

    /**
     * JASYPT 로 암호화가 된 필드의 값을 복호화한 후 해당 필드에 셋팅한다.
     * ENC(암호화가 된 값) 형태만 복호화 한다.
     *
     * @param encryptor JASYPT 암복호화 객체
     * @param source JASYPT 로 암호화가 된 필드가 포함된 객체
     * @param field JASYPT 로 암호화가 된 필드
     */
    public static void decrypt(PooledPBEStringEncryptor encryptor, Object source, Field field) {
        try {
            Object valueObj = getGetMethod(source.getClass(), field).invoke(source);

            if (valueObj != null && field.getType() == String.class) {
                String val = (String) valueObj;

                if (val.startsWith("ENC(")) {
                    val = val.substring("ENC(".length(), val.length() - 1);

                    Method setMethod = getSetMethod(source.getClass(), field);

                    setMethod.invoke(source, encryptor.decrypt(val));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 필드에 해당하는 get 메소드 명을 만든다.
     *
     * @param field 대상 필드
     * @return 필드에 해당하는 get 메소드 명
     */
    public static Method getGetMethod(Class<?> cls, Field field) throws NoSuchMethodException {
        String methodName = null;

        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            if (StringUtils.startsWith(field.getName(), "is")) {
                methodName = field.getName();
            } else {
                methodName = String.format("is%s", StringUtils.capitalize(field.getName()));
            }
        } else {
            methodName = String.format("get%s", StringUtils.capitalize(field.getName()));
        }

        return cls.getDeclaredMethod(methodName);
    }

    /**
     * 필드에 해당하는 set 메소드 명을 만든다.
     *
     * @param field 대상 필드
     * @return 필드에 해당하는 set 메소드 명
     */
    public static Method getSetMethod(Class<?> cls, Field field) throws NoSuchMethodException {
        String methodName = null;

        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            if (StringUtils.startsWith(field.getName(), "is")) {
                methodName = RegExUtils.removeFirst(field.getName(), "is");
            } else {
                methodName = field.getName();
            }

            methodName = String.format("set%s", StringUtils.capitalize(methodName));
        } else {
            methodName = String.format("set%s", StringUtils.capitalize(field.getName()));
        }

        return cls.getDeclaredMethod(methodName, field.getType());
    }
}
