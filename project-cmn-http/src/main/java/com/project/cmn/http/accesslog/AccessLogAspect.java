package com.project.cmn.http.accesslog;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * URL 호출 시 거치는 메소드에 대한 정보를 남기기 위한 AOP
 */
@Aspect
@Configuration
@ConditionalOnProperty(prefix = "project.access.log", value = "enabled", havingValue = "true")
public class AccessLogAspect {
    /**
     * StopWatch 로깅을 위한 AOP 설정
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return 메소드의 리턴 값
     * @throws Throwable {@link Throwable}
     */
    @Around("execution(* com.project..*Controller.*(..)) || execution(* com.project..*Service.*(..)) || execution(* com.project..*ServiceImpl.*(..)) || execution(* com.project..*Mapper.*(..))")
    public Object arroundLogging(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String executeMethodName = signature.getMethod().getName();

        CmnStopWatch stopWatch = AccessLog.getAccessLogDto().getStopWatch();

        if (stopWatch != null) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }

            stopWatch.start(String.format("%s.%s", signature.getDeclaringType().getSimpleName(), executeMethodName));
        }

        Object result = pjp.proceed();

        if (stopWatch != null && stopWatch.isRunning()) {
            stopWatch.stop();
        }

        return result;
    }
}
