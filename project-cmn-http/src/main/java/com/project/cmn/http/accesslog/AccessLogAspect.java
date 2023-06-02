package com.project.cmn.http.accesslog;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * URL 호출 시 거치는 메소드에 대한 정보를 남기기 위한 AOP
 */
@Aspect
@Configuration
@ConditionalOnProperty(prefix = "project.access.log", name = "aspect", havingValue = "true")
public class AccessLogAspect {
    @Pointcut("execution(* com.project..*Controller.*(..))")
    public void pointcutController() {}

    @Pointcut("execution(* com.project..*Service.*(..)) || execution(* com.project..*ServiceImpl.*(..))")
    public void pointcutService() {}

    @Pointcut("execution(* com.project..*Mapper.*(..))")
    public void pointcutMapper() {}

    /**
     * 메소드 시작 전에 {@link org.springframework.util.StopWatch} 를 실행
     *
     * @param jp {@link JoinPoint}
     */
    @Before("pointcutController() || pointcutService() || pointcutMapper()")
    public void beforeMethod(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
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
     * 메소드 종료 후에 {@link org.springframework.util.StopWatch} 를 중지
     *
     * @param jp {@link JoinPoint}
     */
    @Before("pointcutController() || pointcutService() || pointcutMapper()")
    public void afterMethod(JoinPoint jp) {
        CmnStopWatch stopWatch = AccessLog.getAccessLogDto().getStopWatch();

        if (stopWatch != null && stopWatch.isRunning()) {
            stopWatch.stop();
        }
    }
}