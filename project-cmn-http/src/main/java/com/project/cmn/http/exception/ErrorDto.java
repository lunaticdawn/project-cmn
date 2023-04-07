package com.project.cmn.http.exception;

import com.project.cmn.http.validate.ConstraintViolationDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 에러에 대한 정보를 담는 Class
 */
@Getter
@Setter
@ToString
public class ErrorDto {
    /**
     * {@link org.springframework.http.HttpStatus} 의 값
     */
    private int httpStatus;

    /**
     * 에러가 발생한 URI
     */
    private String requestUri;

    /**
     * 결과 코드
     */
    private String resCode;

    /**
     * 결과메시지
     */
    private String resMsg;

    /**
     * 발생 위치
     */
    private String whereCause;

    /**
     * 위반한 제약조건에 대한 정보들
     */
    private List<ConstraintViolationDto> constraintViolationList;
}
