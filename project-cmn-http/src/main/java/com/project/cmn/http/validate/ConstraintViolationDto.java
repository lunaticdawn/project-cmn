package com.project.cmn.http.validate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Validator 의 결과를 담기 위한 Class
 */
@Getter
@Setter
@ToString
@Builder
public class ConstraintViolationDto {
    /**
     * 위반한 값
     */
    private Object invalidValue;

    /**
     * 메시지
     */
    private String message;

    /**
     * 메시지 템플릿. ex) {javax.validation.constraints.NotBlank.message}
     */
    private String messageTemplate;

    /**
     * 위반한 속성이 속한 Root Class
     */
    private String rootClassName;

    /**
     * 위반한 속성이 속한 Class
     */
    private String leafClassName;

    /**
     * 위반한 속성의 경로명
     */
    private String propertyPathName;

    /**
     * 위반한 속성의 이름
     */
    private String propertyName;

    /**
     * 위반한 속성이 속한 Class 의 순서
     */
    private int classOrder;

    /**
     * 위반한 속성의 순서
     */
    private int order;
}
