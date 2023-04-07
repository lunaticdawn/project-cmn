package com.project.cmn.http.accesslog;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 접근로그 관련 설정
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "project.access.log")
public class AccessLogConfig {
    /**
     * {@link Environment}에서 project.access.log 설정을 가져와 {@link AccessLogConfig}로 변환한다.
     *
     * @param environment {@link Environment}
     * @return {@link AccessLogConfig}
     */
    public static AccessLogConfig init(Environment environment) {
        return Binder.get(environment).bindOrCreate("project.access.log", AccessLogConfig.class);
    }

    /**
     * Access Log 사용 여부. default: false
     */
    private boolean enabled = false;

    /**
     * {@link AccessLogFilter} 의 사용 여부. default: false
     */
    private boolean filter = false;

    /**
     * 필터 순서. default: 0
     */
    private int filterOrder = 0;

    /**
     * {@link AccessLogFilter} 를 적용할 URL 패턴
     */
    private List<String> urlPatterns;

    /**
     * Request Uri 의 로깅 여부. default: true
     */
    private boolean requestUri = true;

    /**
     * Request Header 의 로깅 여부. default: true
     */
    private boolean requestHeader = true;

    /**
     * Request Parameter 의 로깅 여부. default: true
     */
    private boolean requestParam = true;

    /**
     * Request Body 의 로깅 여부. default: true
     */
    private boolean requestBody = true;

    /**
     * Response Header 의 로깅 여부. default: true
     */
    private boolean responseHeader = true;

    /**
     * Response Body 의 로깅 여부. default: true
     */
    private boolean responseBody = true;

    /**
     * {@link org.springframework.util.StopWatch} 정보의 로깅 여부. default: true
     */
    private boolean stopWatch = true;

    /**
     * 로깅할 Request Body 의 길이. default: 0 - 전체를 로깅
     */
    private int requestBodyLength = 0;

    /**
     * 로깅할 Response Body 의 길이. default: 0 - 전체를 로깅
     */
    private int responseBodyLength = 0;

    /**
     * 로깅할 Request Header 들. default: 전체 로깅
     */
    private List<String> requestHeaderNames;

    /**
     * 로깅할 Response Header 들. default: 전체 로깅
     */
    private List<String> responseHeaderNames;

    /**
     * 로깅할 Path Pattern 들
     */
    private List<String> pathPatterns;

    /**
     * 로깅하지 않을 Path Pattern 들
     */
    private List<String> excludePathPatterns;
}
