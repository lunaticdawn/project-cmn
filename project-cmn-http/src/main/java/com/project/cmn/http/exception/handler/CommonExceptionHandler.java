package com.project.cmn.http.exception.handler;

import com.project.cmn.http.WebCmnConstants;
import com.project.cmn.http.accesslog.AccessLog;
import com.project.cmn.http.accesslog.AccessLogDto;
import com.project.cmn.http.exception.InvalidValueException;
import com.project.cmn.http.exception.WebClientException;
import com.project.cmn.http.exception.config.ExceptionItem;
import com.project.cmn.http.exception.config.ExceptionsConfig;
import com.project.cmn.http.util.MessageUtils;
import com.project.cmn.http.validate.ConstraintViolationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 기본 Exception Handler. 추가로 정의한 Exception 에 대해 처리해야 하는 경우에는 이 클래스를 상속받아 정의한다.
 */
@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {
    private final Map<String, ExceptionItem> exceptionsMap = new HashMap<>();
    private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private String resCode = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
    private String viewName;

    /**
     * 생성자
     *
     * @param exceptionsConfig {@link ExceptionsConfig} Exception 처리에 대한 설정
     */
    public CommonExceptionHandler(ExceptionsConfig exceptionsConfig) {
        this.status = exceptionsConfig.getDefaultStatus() == 0 ? HttpStatus.INTERNAL_SERVER_ERROR.value() : exceptionsConfig.getDefaultStatus();
        this.viewName = StringUtils.isBlank(exceptionsConfig.getDefaultViewName()) ? null : exceptionsConfig.getDefaultViewName();

        if (exceptionsConfig.getItemList() != null && !exceptionsConfig.getItemList().isEmpty()) {
            for (ExceptionItem item : exceptionsConfig.getItemList()) {
                exceptionsMap.put(item.getName(), item);
            }
        }
    }

    /**
     * {@link javax.validation.Validator} 에서 발생한 {@link ConstraintViolationException} 을 처리한다.
     *
     * @param exception {@link ConstraintViolationException}
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ModelAndView constraintViolationExceptionHandler(ConstraintViolationException exception, HttpServletResponse response) {
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

        return getResponse(exception, this.sortConstraintViolationList(constraintViolationList), response);
    }

    /**
     * {@link org.springframework.validation.annotation.Validated} 를 통해 발생한 {@link BindException} 을 처리한다.
     *
     * @param exception {@link BindException}
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(BindException.class)
    protected ModelAndView bindExceptionHandler(BindException exception, HttpServletResponse response) {
        BindingResult bindingResult = exception.getBindingResult();
        List<ConstraintViolationDto> constraintViolationList = new ArrayList<>();

        if (bindingResult.getFieldErrorCount() > 0) {
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            ConstraintViolationDto constraintViolationDto;

            for (FieldError fieldError : fieldErrorList) {
                constraintViolationDto = ConstraintViolationDto.builder()
                        .invalidValue(fieldError.getRejectedValue())
                        .message(fieldError.getDefaultMessage())
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

        }

        return getResponse(exception, this.sortConstraintViolationList(constraintViolationList), response);
    }

    /**
     * 기타 Exception 을 처리한다.
     *
     * @param exception {@link Exception}
     * @return {@link ModelAndView}
     */
    @ExceptionHandler({InvalidValueException.class, WebClientException.class, Exception.class})
    protected ModelAndView exceptionHandler(Exception exception, HttpServletResponse response) {
        return getResponse(exception, exception.getMessage(), response);
    }

    /**
     * Exception 에 대한 Response 를 가져온다.
     *
     * @param exception 발생한 Exception
     * @param message 결과 메시지
     * @return Exception 에 대한 Response
     */
    protected ModelAndView getResponse(Exception exception, String message, HttpServletResponse response) {
        if (exception instanceof ConstraintViolationException
                || exception instanceof BindException) {
            log.error(exception.getMessage());
        } else {
            log.error(exception.getMessage(), exception);
        }

        message = this.resolveMessage(exception, message);

        AccessLogDto accessLogDto = AccessLog.getAccessLogDto();

        // DB 에 이력을 넣을 수 있도록 AccessLogDto 에 결과를 담는다.
        accessLogDto.setHttpStatus(status);
        accessLogDto.setResCode(resCode);
        accessLogDto.setResMsg(message);

        // 응답을 만든다.
        ModelAndView mav = new ModelAndView();

        mav.addObject(WebCmnConstants.ResponseKeys.TIMESTAMP.code(), LocalDateTime.now());
        mav.addObject(WebCmnConstants.ResponseKeys.RES_STATUS.code(), status);
        mav.addObject(WebCmnConstants.ResponseKeys.RES_CODE.code(), resCode);
        mav.addObject(WebCmnConstants.ResponseKeys.RES_MSG.code(), message);

        this.resolveView(mav, viewName);

        response.setStatus(status);

        return mav;
    }

    /**
     * 제약조건 위반에 대한 Response 를 가져온다.
     *
     * @param exception 제약조건 위반 관련 Exception
     * @param constraintViolationList 제약조건 위반 리스트
     * @return 제약조건 위반에 대한 Response
     */
    protected ModelAndView getResponse(Exception exception, List<ConstraintViolationDto> constraintViolationList, HttpServletResponse response) {
        ModelAndView mav = this.getResponse(exception, constraintViolationList.get(0).getMessage(), response);

        return mav.addObject(WebCmnConstants.ResponseKeys.CONSTRAINT_VIOLATION_LIST.code(), constraintViolationList);
    }

    /**
     * 각 Exception 에 대한 개별 설정 적용
     *
     * @param exception 처리되어야 하는 Exception 들
     * @param message Exception 의 기본 메시지
     * @return 각 Exception 에 대한 개별 설정 적용이 된 후 메시지
     */
    private String resolveMessage(Exception exception, String message) {
        // 각 Exception 에 대한 개별 설정 적용
        ExceptionItem exceptionItem = this.getExceptionItem(exception);

        if (exceptionItem != null) {
            status = exceptionItem.getStatus();

            // 응답코드 셋팅
            if (StringUtils.isNotBlank(exceptionItem.getResCode())) {
                resCode = exceptionItem.getResCode();

                // 응답코드에 대한 메시지가 설정되어 있다면, 응답코드에 대한 메시지를 우선으로 설정한다.
                if (StringUtils.isNotBlank(MessageUtils.getMessage(resCode))) {
                    message = MessageUtils.getMessage(resCode);
                }
            }

            // 뷰 이름 셋팅
            if (StringUtils.isNotBlank(exceptionItem.getViewName())) {
                viewName = exceptionItem.getViewName();
            }
        }

        return message;
    }

    /**
     * View 를 set 한다.
     * 요청의 content-type 이 application/json 인 경우, {@link MappingJackson2JsonView} 를 set 하고, 그 외의 경우에는 viewName 을 기준으로 한다.
     *
     * @param mav {@link ModelAndView}
     * @param viewName set 할 view name
     */
    private void resolveView(ModelAndView mav, String viewName) {
        AccessLogDto accessLogDto = AccessLog.getAccessLogDto();

        if (accessLogDto.getRequestHeader() != null && !accessLogDto.getRequestHeader().isEmpty()) {
            if (StringUtils.startsWith(accessLogDto.getRequestHeader().get("content-type"), MediaType.APPLICATION_JSON_VALUE)) {
                mav.setView(new MappingJackson2JsonView());
            } else {
                if (StringUtils.isNotBlank(viewName)) {
                    mav.setViewName(viewName);
                } else {
                    mav.setView(new MappingJackson2JsonView());
                }
            }
        } else {
            if (StringUtils.isNotBlank(viewName)) {
                mav.setViewName(viewName);
            } else {
                mav.setView(new MappingJackson2JsonView());
            }
        }
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
        String message = MessageUtils.getMessage(constraintViolationDto.getMessage(), argName); // 제약조건 annotation 의 message 값으로 message code 값을 준 경우, 메시지가 만들어짐

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
        String leafClassName;

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
     * Exception 에 대한 설정을 가져온다.
     *
     * @param exception 설정을 가져올 Exception
     * @return Exception 에 대한 설정
     */
    protected ExceptionItem getExceptionItem(Exception exception) {
        return exceptionsMap.get(exception.getClass().getSimpleName());
    }
}