package com.project.cmn.configuration.datasource;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.NoSuchElementException;

@Getter
@ToString
@AutoConfiguration
@ConfigurationProperties(prefix = "jasypt.encryptor")
public class JasyptEncryptorConfig {
    /**
     * {@link Environment}에서 jasypt.encryptor 설정을 가져와 {@link JasyptEncryptorConfig}로 변환한다.
     *
     * @param environment {@link Environment}
     * @return {@link JasyptEncryptorConfig}
     */
    public static JasyptEncryptorConfig init(Environment environment) {
        try {
            return Binder.get(environment).bind("jasypt.encryptor", JasyptEncryptorConfig.class).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * 암복호화 시 사용할 Secret Key
     */
    @Setter
    private String password;

    /**
     * 암복호화 알고리즘
     */
    private String algorithm = "PBEWithMD5AndDES"; // PBEWITHHMACSHA512ANDAES_256

    /**
     * 반복할 해싱 횟수
     */
    private String keyObtentionIterations = "1000";

    /**
     * 인스턴스 Pool Size
     */
    private String poolSize = "1";

    /**
     * Salt 생성 클래스
     */
    private String saltGeneratorClassname = "org.jasypt.salt.RandomSaltGenerator";

    /**
     * IV 생성 클래스
     */
    private String ivGeneratorClassname = "org.jasypt.iv.RandomIvGenerator";

    /**
     * 최종 인코딩 방식
     */
    private String stringOutputType = "base64";
}
