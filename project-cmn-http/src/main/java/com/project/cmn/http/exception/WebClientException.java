package com.project.cmn.http.exception;

import com.project.cmn.http.util.MessageUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

@Getter
public class WebClientException extends RuntimeException {
    /**
     * {@link HttpStatus}
     */
    private final HttpStatus httpStatus;

    /**
     * 생성자
     *
     * @param httpStatus {@link HttpStatus}
     */
    public WebClientException(HttpStatus httpStatus) {
        super(StringUtils.defaultIfBlank(MessageUtils.getMessage(httpStatus.value()), httpStatus.toString()));

        this.httpStatus = httpStatus;
    }
}
