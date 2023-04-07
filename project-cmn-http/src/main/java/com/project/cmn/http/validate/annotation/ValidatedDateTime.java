package com.project.cmn.http.validate.annotation;

import javax.validation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.format.DateTimeFormatter;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 날짜시간의 포맷을 검증하기 위한 annotation
 */
@Documented
@Constraint(validatedBy = {ValidatedDateTime.Validator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface ValidatedDateTime {
    /**
     * 날짜-시간 포맷
     *
     * @return 날짜-시간 포맷
     */
    String format() default "yyyyMMddHHmmss";

    /**
     * 오류 메시지
     *
     * @return 오류 메시지
     */
    String message() default "{invalid.date.format}";

    /**
     * validation group
     *
     * @return validation group
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 날짜시간의 포맷을 검증하기 위한 클래스
     */
    class Validator implements ConstraintValidator<ValidatedDateTime, String> {
        private ValidatedDateTime validatedDateTime;

        @Override
        public void initialize(ValidatedDateTime validatedDateTime) {
            this.validatedDateTime = validatedDateTime;
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern(validatedDateTime.format());

                dtf.parse(value);

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
