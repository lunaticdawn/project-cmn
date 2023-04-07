package com.project.cmn.http.configuration;

import com.project.cmn.http.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * {@link MessageUtils} 를 사용하기 위한 설정
 */
@Slf4j
@Configuration
public class MessageSourceConfiguration implements WebMvcConfigurer {
    /**
     * {@link MessageSourceAccessor} 를 Bean 으로 등록하면서 {@link MessageUtils} 를 사용할 수 있게 만든다.
     *
     * @param messageSource {@link org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration} 을 통해 등록된 {@link MessageSource}
     * @return {@link MessageSourceAccessor}
     */
    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        log.info("# Register Bean: MessageSourceAccessor");
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);

        MessageUtils.setMessageSourceAccessor(messageSourceAccessor);

        return messageSourceAccessor;
    }

    /**
     * {@link LocaleChangeInterceptor} 사용을 위해 {@link LocaleResolver} 를 Bean 으로 등록한다.
     *
     * @return {@link LocaleResolver}
     */
    @Bean
    public LocaleResolver localeResolver() {
        log.info("# Register Bean: LocaleResolver");
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();

        MessageUtils.setLocaleResolver(sessionLocaleResolver);

        return sessionLocaleResolver;
    }

    /**
     * 파라미터를 통해 {@link java.util.Locale} 을 변경할 수 있게 {@link LocaleChangeInterceptor} 를 추가한다.
     *
     * @param registry {@link InterceptorRegistry} Interceptor 정보를 담고 있는 Registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("# Add Interceptor: LocaleChangeInterceptor");
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();

        localeChangeInterceptor.setParamName("lang");

        registry.addInterceptor(localeChangeInterceptor);
    }
}
