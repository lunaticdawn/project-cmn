package com.project.cmn.http.exception.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties
public class ExceptionItem {
    /**
     * Exception 의 Simple Name. 필수
     */
    private String name;

    /**
     * response 의 http status 값. 필수
     */
    private int status;

    /**
     * 결과 코드. 옵션
     */
    private String resCode;

    /**
     * 설명. 옵션
     */
    private String desc;

    /**
     * Exception Handler 에서 처리한 결과를 보여줄 View 의 이름. 옵션
     */
    private String viewName;
}
