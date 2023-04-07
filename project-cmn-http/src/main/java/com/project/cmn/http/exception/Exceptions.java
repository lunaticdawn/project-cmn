package com.project.cmn.http.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

@Getter
public enum Exceptions {
    ConstraintViolationException(HttpStatus.BAD_REQUEST.value(), null, "Bad Request"),
    MethodArgumentNotValidException(HttpStatus.BAD_REQUEST.value(), null, "Bad Request"),
    InValidValueException(HttpStatus.BAD_REQUEST.value(), null, "Bad Request"),
    Exception(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "Internal Server Error");

    private final int httpStatus;
    private final String resCode;
    private final String resMsg;

    Exceptions(int httpStatus, String resCode, String resMsg) {
        this.httpStatus = httpStatus;
        this.resCode = StringUtils.defaultIfBlank(resCode, String.valueOf(httpStatus));
        this.resMsg = resMsg;
    }

    /**
     * {@link Exception} 에 해당하는 {@link Exceptions} 을 가져온다.
     *
     * @param exception {@link Exception}
     * @return {@link Exceptions}
     */
    public static Exceptions valueOf(Exception exception) {
        Exceptions find;

        try {
            find = valueOf(exception.getClass().getSimpleName());
        } catch (Exception ex) {
            find = Exception;
        }

        return find;
    }
}
