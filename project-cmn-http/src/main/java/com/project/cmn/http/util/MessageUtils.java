package com.project.cmn.http.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * {@link MessageSourceAccessor} 를 편하게 사용하기 위한 Utility Class
 */
@Slf4j
public class MessageUtils {
    private static MessageSourceAccessor messageSourceAccessor;
    private static LocaleResolver localeResolver;

    private MessageUtils() {}

    /**
     * {@link MessageSourceAccessor} 를 set
     *
     * @param messageSourceAccessor {@link MessageSourceAccessor}
     */
    public static void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        MessageUtils.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * {@link LocaleResolver} 를 set
     *
     * @param localeResolver {@link LocaleResolver}
     */
    public static void setLocaleResolver(LocaleResolver localeResolver) {
        MessageUtils.localeResolver = localeResolver;
    }

    /**
     * 메시지를 조회한다.
     *
     * @param key  메시지 키
     * @param args 추가적인 arguments
     * @return 메시지. 메시지를 못찾은 경우에는 빈문자열
     */
    public static String getMessage(String key, Object... args) {
        String msg;
        String keyStr = key;

        // key를 감싸고 있는 양쪽 중괄호는 제거. - Hibernate validator message template
        keyStr = RegExUtils.removePattern(keyStr, "^\\{");
        keyStr = RegExUtils.removePattern(keyStr, "\\}$");

        if (StringUtils.isNumeric(keyStr)) {
            msg = getMessage(Integer.parseInt(keyStr), args);
        } else {
            msg = messageSourceAccessor.getMessage(keyStr, args, "", getLocale());
        }

        return msg;
    }

    /**
     * 메시지를 조회한다.
     * <pre>
     * {@code
     *     < 100: common.%d - 공통 영역
     *     1xx: info.%d - Http Status
     *     2xx: success.%d - Http Status
     *     3xx: redirect.%d - Http Status
     *     4xx: client.error.%d - Http Status
     *     5xx: service.error.%d - Http Status
     *     그외: service.%d - 서비스 영역
     * }
     * </pre>
     *
     * @param resultCode 코드
     * @param args       추가적인 arguments
     * @return 메시지
     */
    public static String getMessage(int resultCode, Object... args) {
        String key;

        if (resultCode < 100) {
            key = String.format("common.%d", resultCode);
        } else if (resultCode < 200) {
            key = String.format("info.%d", resultCode);
        } else if (resultCode < 300) {
            key = String.format("success.%d", resultCode);
        } else if (resultCode < 400) {
            key = String.format("redirect.%d", resultCode);
        } else if (resultCode < 500) {
            key = String.format("client.error.%d", resultCode);
        } else if (resultCode < 600) {
            key = String.format("server.error.%d", resultCode);
        } else {
            key = String.format("service.%d", resultCode);
        }

        return getMessage(key, args);
    }

    /**
     * 현재 사용자의 {@link Locale} 을 가져온다.
     * {@link org.springframework.web.servlet.i18n.SessionLocaleResolver} 를 사용. (Session 에 있는 Locale 정보 -> Accept-Language 에 있는 Locale 정보)
     *
     * @return 현재 사용자의 {@link Locale}.
     */
    public static Locale getLocale() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Locale locale = Locale.getDefault();

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            locale = localeResolver.resolveLocale(request);
        }

        return locale;
    }
}
