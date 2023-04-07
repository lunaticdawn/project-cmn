package com.project.cmn.http.exception;

import com.project.cmn.http.util.MessageUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;

@Getter
public class InValidValueException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String resCode;
    private final String resMsg;
    private final String fieldName;

    /**
     * 생성자
     *
     * @param fieldName 유효하지 않은 값을 받은 필드명
     */
    public InValidValueException(String fieldName) {
        this.fieldName = fieldName;
        this.resCode = Exceptions.InValidValueException.getResCode();

        String convertFieldName = StringUtils.defaultIfBlank(MessageUtils.getMessage(fieldName), fieldName);

        this.resMsg = MessageUtils.getMessage(this.resCode, convertFieldName);
    }
}
