package com.project.cmn.http.exception.handler;

import com.project.cmn.http.accesslog.AccessLog;
import com.project.cmn.http.accesslog.AccessLogDto;
import com.project.cmn.http.exception.ErrorDto;
import com.project.cmn.http.exception.Exceptions;
import com.project.cmn.http.exception.InValidValueException;
import com.project.cmn.http.util.MessageUtils;
import com.project.cmn.http.validate.ConstraintViolationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(basePackages = "com.project")
public class CommonExceptionHandler {

    /**
     * {@link javax.validation.Validator} 에서 발생한 {@link ConstraintViolationException} 을 처리한다.
     *
     * @param exception {@link ConstraintViolationException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> constraintViolationExceptionHandler(ConstraintViolationException exception) {
        ErrorDto errorDto = new ErrorDto();
        Set<ConstraintViolation<?>> constraintViolationSet = exception.getConstraintViolations();

        ConstraintViolationDto constraintViolationDto;
        List<ConstraintViolationDto> constraintViolationList = new ArrayList<>();

        for (ConstraintViolation<?> constraintViolation : constraintViolationSet) {
            constraintViolationDto = ConstraintViolationDto.builder()
                    .invalidValue(constraintViolation.getInvalidValue())
                    .message(constraintViolation.getMessage())
                    .messageTemplate(constraintViolation.getMessageTemplate())
                    .rootClassName(constraintViolation.getRootBeanClass().getName())
                    .leafClassName(constraintViolation.getLeafBean().getClass().getName())
                    .propertyPathName(constraintViolation.getPropertyPath().toString())
                    .classOrder(StringUtils.countMatches(constraintViolation.getPropertyPath().toString(), '.'))
                    .build();

            constraintViolationDto.setMessage(this.getConstraintViolationMessage(constraintViolationDto));
            constraintViolationDto.setPropertyName(this.getPropertyName(constraintViolationDto.getPropertyPathName()));

            this.setOrder(constraintViolation.getLeafBean().getClass(), constraintViolationDto);

            constraintViolationList.add(constraintViolationDto);
        }

        errorDto.setConstraintViolationList(this.sortConstraintViolationList(constraintViolationList));
        errorDto.setResMsg(constraintViolationList.get(0).getMessage());

        return getResponseEntity(exception, errorDto);
    }

    /**
     * {@link org.springframework.validation.annotation.Validated} 를 통해 발생한 {@link MethodArgumentNotValidException} 을 처리한다.
     *
     * @param exception {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> methodArgumentNotValidExceptionHhandler(MethodArgumentNotValidException exception) {
        ErrorDto errorDto = new ErrorDto();
        BindingResult bindingResult = exception.getBindingResult();

        if (bindingResult.getFieldErrorCount() > 0) {
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            ConstraintViolationDto constraintViolationDto;
            List<ConstraintViolationDto> constraintViolationList = new ArrayList<>();

            for (FieldError fieldError : fieldErrorList) {
                constraintViolationDto = ConstraintViolationDto.builder()
                        .invalidValue(fieldError.getRejectedValue())
                        .messageTemplate(fieldError.getCode())
                        .propertyPathName(fieldError.getField())
                        .propertyName(this.getPropertyName(fieldError.getField()))
                        .classOrder(0)
                        .order(0)
                        .build();

                constraintViolationDto.setMessage(this.getConstraintViolationMessage(constraintViolationDto));
                constraintViolationDto.setRootClassName(bindingResult.getTarget().getClass().getName());

                this.setLeafClassName(bindingResult.getTarget().getClass(), constraintViolationDto);

                constraintViolationList.add(constraintViolationDto);
            }

            errorDto.setConstraintViolationList(this.sortConstraintViolationList(constraintViolationList));
            errorDto.setResMsg(constraintViolationList.get(0).getMessage());
        }

        return getResponseEntity(exception, errorDto);
    }

    /**
     * 제약조건을 위반한 속성명을 가져온다.
     *
     * @param propertyPathName 제약조건을 위반한 속성의 경로
     * @return 제약조건을 위반한 속성명
     */
    private String getPropertyName(String propertyPathName) {
        return StringUtils.defaultIfBlank(StringUtils.substringAfterLast(propertyPathName, "."), propertyPathName);
    }

    /**
     * 제약조건 위반에 대한 메시지를 가져온다.
     *
     * @param constraintViolationDto {@link ConstraintViolationDto}
     * @return 제약조건 위반에 대한 메시지
     */
    private String getConstraintViolationMessage(ConstraintViolationDto constraintViolationDto) {
        String argName = StringUtils.defaultIfEmpty(MessageUtils.getMessage(constraintViolationDto.getPropertyName()), constraintViolationDto.getPropertyName());
        String message = MessageUtils.getMessage(constraintViolationDto.getMessage(), argName);

        // 지정한 message 는 없는데, message template 이 존재하는 경우
        if (StringUtils.isBlank(message) && StringUtils.isNotBlank(constraintViolationDto.getMessageTemplate())) {
            message = MessageUtils.getMessage(constraintViolationDto.getMessageTemplate(), argName);
        }

        if (StringUtils.isBlank(message)) {
            message = constraintViolationDto.getMessage();
        }

        return message;
    }

    /**
     * 제약조건을 위반한 속성이 있는 class 의 이름을 저장한다.
     *
     * @param rootClass              최상위 class
     * @param constraintViolationDto {@link ConstraintViolationDto}
     */
    private void setLeafClassName(Class<?> rootClass, ConstraintViolationDto constraintViolationDto) {
        if (!constraintViolationDto.getPropertyPathName().contains(".")) {
            constraintViolationDto.setLeafClassName(rootClass.getName());

            this.setOrder(rootClass, constraintViolationDto);

            return;
        }

        Class<?> leafClass = rootClass;
        String propertyPathName = constraintViolationDto.getPropertyPathName();
        String[] propertyPaths;
        String nextPropertyName;
        String leafClassName = null;

        try {
            while (true) {
                propertyPaths = StringUtils.split(propertyPathName, ".");
                nextPropertyName = propertyPaths[0];

                // Collection 인 경우
                if (nextPropertyName.contains("[")) {
                    nextPropertyName = StringUtils.substringBefore(propertyPaths[0], "[");
                }

                leafClassName = leafClass.getDeclaredField(nextPropertyName).getAnnotatedType().getType().getTypeName();

                log.debug("# {} - {}", nextPropertyName, leafClassName);

                // Collection 인 경우
                if (leafClassName.contains("<")) {
                    leafClassName = StringUtils.substringAfterLast(leafClassName, "<");
                    leafClassName = RegExUtils.removeAll(leafClassName, ">");
                }

                // Array 인 경우
                if (leafClassName.contains("[")) {
                    leafClassName = StringUtils.substringBefore(leafClassName, "[");
                }

                leafClass = Class.forName(leafClassName);

                constraintViolationDto.setClassOrder(constraintViolationDto.getClassOrder() + 1);
                constraintViolationDto.setLeafClassName(leafClassName);

                this.setOrder(leafClass, constraintViolationDto);

                propertyPathName = StringUtils.substringAfter(propertyPathName, ".");

                if (!propertyPathName.contains(".")) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 제약조건을 위반한 속성의 순서를 저장한다.
     *
     * @param leafClass              제약조건 위반이 발생한 속성이 속한 class
     * @param constraintViolationDto {@link ConstraintViolationDto}
     */
    private void setOrder(Class<?> leafClass, ConstraintViolationDto constraintViolationDto) {
        Field[] fields = leafClass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            if (constraintViolationDto.getPropertyName().equals(fields[i].getName())) {
                constraintViolationDto.setOrder(i);
            }
        }
    }

    /**
     * 제약조건을 위반한 속성들을 순서에 따라 정렬한다.
     * ClassOrder -> LeafClassName -> Order -> PropertyPathName
     *
     * @param constraintViolationList 제약조건을 위반한 속성들
     * @return 정렬된 속성들
     */
    private List<ConstraintViolationDto> sortConstraintViolationList(List<ConstraintViolationDto> constraintViolationList) {
        Comparator<ConstraintViolationDto> comparator = Comparator.comparing(ConstraintViolationDto::getClassOrder)
                .thenComparing(ConstraintViolationDto::getLeafClassName)
                .thenComparing(ConstraintViolationDto::getOrder)
                .thenComparing(ConstraintViolationDto::getPropertyPathName);

        constraintViolationList = constraintViolationList.stream().sorted(comparator).toList();

        return constraintViolationList;
    }

    /**
     * {@link InValidValueException} 을 처리한다.
     *
     * @param exception {@link InValidValueException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(InValidValueException.class)
    public ResponseEntity<ErrorDto> inValidValueExceptionHandler(InValidValueException exception) {
        String argName = StringUtils.defaultIfEmpty(MessageUtils.getMessage(exception.getFieldName()), exception.getFieldName());
        String message = MessageUtils.getMessage(exception.getResCode(), argName);

        message = StringUtils.defaultIfBlank(message, exception.getMessage());

        ErrorDto errorDto = new ErrorDto();

        errorDto.setResMsg(message);

        return getResponseEntity(exception, errorDto);
    }

    /**
     * {@link Exception} 을 처리한다.
     *
     * @param exception {@link Exception}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDto> exceptionHandler(Exception exception) {
        return getResponseEntity(exception, new ErrorDto());
    }

    /**
     * {@link Exception} 을 처리한다.
     *
     * @param exception {@link Exception}
     * @param errorDto  {@link ErrorDto}
     * @return {@link ResponseEntity}
     */
    public ResponseEntity<ErrorDto> getResponseEntity(Exception exception, ErrorDto errorDto) {
        AccessLogDto accessLogDto = AccessLog.getAccessLogDto();

        // 에러 위치
        if (exception instanceof ConstraintViolationException) {
            log.error(exception.getMessage());
            errorDto.setWhereCause(exception.getStackTrace()[1].toString());
        } else {
            log.error(exception.getMessage(), exception);
            errorDto.setWhereCause(exception.getStackTrace()[0].toString());
        }

        Exceptions exceptions = Exceptions.valueOf(exception);

        errorDto.setHttpStatus(exceptions.getHttpStatus());
        errorDto.setRequestUri(accessLogDto.getRequestUri());
        errorDto.setResCode(exceptions.getResCode());

        if (StringUtils.isBlank(errorDto.getResMsg())) {
            errorDto.setResMsg(exceptions.getResMsg());
        }

        // AccessLogDto 에 결과를 담는다.
        accessLogDto.setHttpStatus(errorDto.getHttpStatus());
        accessLogDto.setResCode(errorDto.getResCode());
        accessLogDto.setResMsg(errorDto.getResMsg());

        return ResponseEntity.status(errorDto.getHttpStatus()).body(errorDto);
    }
}
