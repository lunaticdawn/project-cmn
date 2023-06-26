package com.project.cmn.http.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.cmn.http.exception.WebClientException;
import com.project.cmn.util.JsonUtils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.Map;
import java.util.function.Consumer;

public class WebClientUtils {
    private WebClientUtils() {}

    public static Consumer<HttpHeaders> getHttpHeadersConsumer(Map<String, String> httpHeaderMap) {
        return httpHeaders -> {
            if (httpHeaderMap != null && !httpHeaderMap.isEmpty()) {
                httpHeaders.setAll(httpHeaderMap);
            }
        };
    }

    public static WebClient getWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public static Object get(Map<String, String> httpHeaderMap, String url, Class<?> clazz) throws SSLException {
        Consumer<HttpHeaders> httpHeadersConsumer = getHttpHeadersConsumer(httpHeaderMap);

        WebClient webClient = getWebClient();

        return webClient.get()
                .uri(url)
                .headers(httpHeadersConsumer)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new WebClientException(clientResponse.statusCode())))
                .bodyToMono(clazz).block();
    }

    public static Object post(Map<String, String> httpHeaderMap, String url, Object param, Class<?> clazz) throws SSLException, JsonProcessingException {
        Consumer<HttpHeaders> httpHeadersConsumer = getHttpHeadersConsumer(httpHeaderMap);

        WebClient webClient = getWebClient();

        return webClient.post()
                .uri(url)
                .headers(httpHeadersConsumer)
                .bodyValue(JsonUtils.toJsonStr(param))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new WebClientException(clientResponse.statusCode())))
                .bodyToMono(clazz).block();
    }
}