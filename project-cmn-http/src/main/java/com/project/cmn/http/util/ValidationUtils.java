package com.project.cmn.http.util;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * Controller 가 아닌 다른 Layer 에서도 {@link Validator} 를 사용하기 위한 Utility Class
 */
@Slf4j
public class ValidationUtils {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 생성자
     */
    private ValidationUtils() {}

    /**
     * Validation 을 체크한다.
     *
     * @param target Validation 을 체크할 대상 객체
     * @param groups Validation 을 체크할 그룹
     */
    public static void validate(Object target, Class<?>... groups) {
        Set<ConstraintViolation<Object>> result;

        // 대상 객체가 List 인 경우와 아닌 경우를 나누어 처리
        if (target instanceof List<?> lists) {
            for (Object obj : lists) {
                result = validator.validate(obj, groups);

                if (!result.isEmpty()) {
                    throw new ConstraintViolationException(result);
                }
            }
        } else {
            result = validator.validate(target, groups);

            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result);
            }
        }
    }
}
