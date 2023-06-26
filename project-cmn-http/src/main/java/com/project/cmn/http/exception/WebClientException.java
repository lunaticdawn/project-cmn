package com.project.cmn.http.exception;

import com.project.cmn.http.util.MessageUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;

@Getter
public class WebClientException extends RuntimeException {
    /**
     * {@link HttpStatusCode}
     */
    private final HttpStatusCode httpStatusCode;

    /**
     * 생성자
     *
     * @param httpStatusCode {@link HttpStatusCode}
     */
    public WebClientException(HttpStatusCode httpStatusCode) {
        super(StringUtils.defaultIfBlank(MessageUtils.getMessage(httpStatusCode.value()), httpStatusCode.toString()));

        this.httpStatusCode = httpStatusCode;
    }
}