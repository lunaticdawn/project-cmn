package com.project.cmn.http.exception;

import com.project.cmn.http.util.MessageUtils;
import lombok.Getter;

import java.io.Serial;

@Getter
public class InvalidValueException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String fieldName;
    private static final String MSG_CODE = "invalid.value";

    /**
     * 생성자
     *
     * @param fieldName 유효하지 않은 값을 받은 필드명
     */
    public InvalidValueException(String fieldName) {
        super(MessageUtils.getMessage(InvalidValueException.MSG_CODE, fieldName));

        this.fieldName = fieldName;
    }
}
