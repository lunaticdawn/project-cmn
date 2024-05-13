package com.project.cmn.http.accesslog;


import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

/**
 * URL 호출 시 거치는 메소드에 대한 정보를 남기기 위한 AOP
 */
@Aspect
@AutoConfiguration
@ConditionalOnClass(AccessLogConfig.class)
@ConditionalOnProperty(prefix = "project.access.log", name = "aspect", havingValue = "true")
public class AccessLogAspect {
    @Pointcut("execution(* com.project..*Controller.*(..))")
    public void pointcutController() {}

    @Pointcut("execution(* com.project..*Service.*(..)) || execution(* com.project..*ServiceImpl.*(..))")
    public void pointcutService() {}

    @Pointcut("execution(* com.project..*Mapper.*(..))")
    public void pointcutMapper() {}

    /**
     * 각 메소드에 AOP 를 적용한다.
     *
     * @param jp {@link ProceedingJoinPoint}
     * @return 메소드 실행 결과
     * @throws Throwable 메소드를 실행할 때 발생하는 오류
     */
    @Around("pointcutController() || pointcutService() || pointcutMapper()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        this.startStopWatch(signature);

        Object result = jp.proceed();

        this.setResCnt(signature, result);
        this.stopStopWatch();

        return result;
    }

    /**
     * StopWatch 를 시작한다.
     *
     * @param signature {@link MethodSignature}
     */
    private void startStopWatch(MethodSignature signature) {
        String executeMethodName = signature.getMethod().getName();

        CmnStopWatch stopWatch = AccessLog.getAccessLogDto().getStopWatch();

        if (stopWatch != null) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }

            stopWatch.start(String.format("%s.%s", signature.getDeclaringType().getSimpleName(), executeMethodName));
        }
    }

    /**
     * StopWatch 를 멈춘다.
     */
    private void stopStopWatch() {
        CmnStopWatch stopWatch = AccessLog.getAccessLogDto().getStopWatch();

        if (stopWatch != null && stopWatch.isRunning()) {
            stopWatch.stop();
        }
    }

    /**
     * Mapper 의 메소드 실행 결과가 {@link List} 형인 경우, 그 결과의 갯수를 {@link AccessLogDto} 에 Set 한다.
     *
     * @param signature {@link MethodSignature}
     * @param result 메소드 실행 결과
     */
    private void setResCnt(MethodSignature signature, Object result) {
        if (StringUtils.endsWith(signature.getDeclaringType().getSimpleName(), "Mapper") && result instanceof List<?> resultList) {
            if (resultList.isEmpty()) {
                AccessLog.getAccessLogDto().setResCnt(0);
            } else {
                AccessLog.getAccessLogDto().setResCnt(resultList.size());
            }
        }
    }
}
