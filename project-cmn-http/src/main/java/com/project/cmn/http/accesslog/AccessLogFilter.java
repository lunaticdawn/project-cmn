package com.project.cmn.http.accesslog;

import com.project.cmn.http.WebCmnConstants;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

/**
 * 접근 정보를 로깅하기 위한 필터.
 * 이 필터를 설정해야 request body 와 response body 의 값을 읽을 수 있음.
 * 소스 내에 있는 getLogKey 메소드를 수정하여 환경에 맞는 로그키를 생성하고 로깅에 사용할 수 있음.
 */
public class AccessLogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest useRequest = request;
        HttpServletResponse useResponse = response;

        if (!(useRequest instanceof ContentCachingRequestWrapper)) {
            useRequest = new ContentCachingRequestWrapper(useRequest);
        }

        if (!(useResponse instanceof ContentCachingResponseWrapper)) {
            useResponse = new ContentCachingResponseWrapper(useResponse);
        }

        try {
            MDC.put(WebCmnConstants.LOG_KEY, this.getLogKey());

            filterChain.doFilter(useRequest, useResponse);

            MDC.remove(WebCmnConstants.LOG_KEY);
        } finally {
            ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) useResponse;

            wrapper.copyBodyToResponse();
        }
    }

    /**
     * 로깅에 사용할 로그키를 반환한다.
     *
     * @return 로깅에 사용할 로그키
     */
    protected String getLogKey() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().withinRange('A', 'Z').build();

        return String.format("LK%s%s", DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmssSSS"), randomStringGenerator.generate(5));
    }
}