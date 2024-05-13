package com.project.cmn.http.accesslog;


import com.project.cmn.util.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * {@link HttpServletRequest}와 {@link HttpServletResponse}를 분석하여 접근로그에 대한 정보를 {@link AccessLogDto}에 담고, 로깅한다.
 */
@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnClass(AccessLogConfig.class)
public class AccessLogInterceptor implements HandlerInterceptor {
    /**
     * Access Log 관련 설정
     */
    private final AccessLogConfig accessLogConfig;

    /**
     * {@link HttpServletRequest} 와 {@link HttpServletResponse} 의 파싱을 담당하는 클래스
     */
    private final AccessLog accessLog;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        accessLog.start(request);
        accessLog.setRequestHeader(request);
        accessLog.setRequestParam(request);

        this.logRequestHeader(AccessLog.getAccessLogDto());

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        accessLog.setRequestBody(request);
        accessLog.setResponseHeader(response);
        accessLog.setResponseBody(response);

        AccessLogDto accessLogDto = accessLog.end();

        this.logRequestBody(accessLogDto);
        this.getResponseLogStr(accessLogDto);

        CmnStopWatch stopWatch = accessLogDto.getStopWatch();

        if (stopWatch != null) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
                accessLogDto.setDurationTime(stopWatch.getTotalTimeMillis());
            }

            if (accessLogConfig.isStopWatch()) {
                log.info("\n{}", stopWatch.prettyPrintMillis());
            }
        }
    }

    /**
     * {@link HttpServletRequest}의 Header 정보를 바탕으로 한 요청 로그를 출력한다.
     *
     * @param accessLogDto {@link AccessLogDto}
     */
    private void logRequestHeader(AccessLogDto accessLogDto) {
        if (!accessLogConfig.isRequestInfo()) {
            return;
        }

        StringBuilder buff = new StringBuilder();

        buff.append("\n");
        buff.append("-------------------- REQUEST INFO START --------------------").append("\n");
        buff.append(String.format("-- URL: %s %s", accessLogDto.getHttpMethod(), accessLogDto.getRequestUri())).append("\n");

        if (accessLogConfig.isRequestHeader()) {
            buff.append(String.format("-- HEADER: %s", accessLogDto.getRequestHeader())).append("\n");
        }

        if (accessLogConfig.isRequestParam()) {
            buff.append(String.format("-- PARAM: %s", accessLogDto.getRequestParam())).append("\n");
        }

        buff.append("-------------------- REQUEST INFO END   --------------------");

        log.info(buff.toString());
    }

    /**
     * {@link HttpServletRequest}의 Body 정보를 출력한다.
     * Request의 Content-Type이 application/json인 경우에만 Body를 저장
     *
     * @param accessLogDto {@link AccessLogDto}
     */
    private void logRequestBody(AccessLogDto accessLogDto) {
        if (accessLogConfig.isRequestBody()) {
            StringBuilder buff = new StringBuilder();

            buff.append("\n");
            buff.append("-------------------- REQUEST BODY START --------------------").append("\n");

            if (StringUtils.isNotBlank(accessLogDto.getRequestBody())) {
                try {
                    buff.append(JsonUtils.toJsonStrPretty(accessLogDto.getRequestBody()));
                } catch (IOException e) {
                    buff.append(accessLogDto.getRequestBody());
                }
            } else {
                buff.append("Request content is null or missing.");
            }

            buff.append("\n");
            buff.append("-------------------- REQUEST BODY END   --------------------");

            log.info(buff.toString());
        }
    }

    private void getResponseLogStr(AccessLogDto accessLogDto) {
        if (!accessLogConfig.isResponseInfo()) {
            return;
        }

        StringBuilder buff = new StringBuilder();

        buff.append("\n");
        buff.append("-------------------- RESPONSE INFO START --------------------").append("\n");

        if (accessLogConfig.isResponseHeader()) {
            buff.append(String.format("-- URL: %s %s", accessLogDto.getHttpMethod(), accessLogDto.getRequestUri())).append("\n");
            buff.append(String.format("-- HEADER: %s", accessLogDto.getResponseHeader())).append("\n");
            buff.append(String.format("-- STATUS: %s", HttpStatus.resolve(accessLogDto.getResStatus()))).append("\n");
            buff.append("\n");
        }

        if (accessLogConfig.isResponseBody()) {
            if (StringUtils.isNotBlank(accessLogDto.getResponseBody())) {
                try {
                    buff.append(JsonUtils.toJsonStrPretty(accessLogDto.getResponseBody()));
                } catch (IOException e) {
                    buff.append(accessLogDto.getResponseBody());
                }
            } else {
                buff.append("-- BODY: Unhandled response.");
            }
        }

        buff.append("\n");
        buff.append("-------------------- RESPONSE INFO END   --------------------");

        log.info(buff.toString());
    }
}
