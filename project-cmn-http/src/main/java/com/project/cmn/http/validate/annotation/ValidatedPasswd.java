package com.project.cmn.http.validate.annotation;

import com.project.cmn.http.util.PasswdCheckUtil;

import javax.validation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 비밀번호를 검증하기 위한 annotation
 */
@Documented
@Constraint(validatedBy = {ValidatedPasswd.Validator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface ValidatedPasswd {
    /**
     * 동일문자 반복 횟수. 값 이상이면 부적합
     *
     * @return 동일문자 반복 횟수
     */
    int repeated() default 3;

    /**
     * 문자의 연속 횟수. 값 이상이면 부적합
     *
     * @return 문자의 연속 횟수
     */
    int consecutive() default 3;

    /**
     * 문자 최소 조합 수. 값 이상이면 적합
     *
     * @return 문자 최소 조합 수
     */
    int combination() default 2;

    /**
     * 오류 메시지
     *
     * @return 오류 메시지
     */
    String message() default "{invalid.password}";

    /**
     * validation group
     *
     * @return validation group
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 비밀번호를 검증하기 위한 클래스
     */
    class Validator implements ConstraintValidator<ValidatedPasswd, String> {
        private ValidatedPasswd validatedPasswd;

        @Override
        public void initialize(ValidatedPasswd validatedPasswd) {
            this.validatedPasswd = validatedPasswd;
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return PasswdCheckUtil.checkPasswd(value, validatedPasswd.repeated(), validatedPasswd.consecutive(), validatedPasswd.combination());
        }
    }
}
