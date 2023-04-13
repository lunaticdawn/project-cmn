package com.project.cmn.http.exception.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "project.exceptions")
public class ExceptionsConfig {
    /**
     * Exception Handler 에서 처리한 후 response 에 셋팅할 http status 값
     * 옵션. 없으면 {@link org.springframework.http.HttpStatus} 의 INTERNAL_SERVER_ERROR
     */
    private int defaultStatus;

    /**
     * Exception Handler 에서 처리한 결과를 보여줄 기본 View 의 이름.
     * 옵션. 없으면 {@link MappingJackson2JsonView} 가 기본
     */
    private String defaultViewName;

    /**
     * Exception 에 대한 처리 정보들
     */
    List<ExceptionItem> itemList;
}
