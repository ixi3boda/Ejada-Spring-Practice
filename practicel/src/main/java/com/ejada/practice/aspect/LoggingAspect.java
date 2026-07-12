package com.ejada.practice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Cross-cutting logging for the service layer, implemented with Spring AOP.
 *
 * Instead of adding
 * try/catch + log statements inside every service method, a single aspect
 * intercepts calls to every method under {@code com.ejada.practice.service}
 * and logs the call, its arguments, execution time, and any exception -
 * without the service classes knowing this is happening.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Matches every method of every class in the service package.
     */
    @Pointcut("execution(* com.ejada.practice.service..*(..))")
    public void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.debug("Entering {} with arguments={}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedMs = System.currentTimeMillis() - start;
            log.info("{} completed in {} ms", signature, elapsedMs);
            return result;
        } catch (Throwable ex) {
            long elapsedMs = System.currentTimeMillis() - start;
            log.warn("{} failed after {} ms with {}: {}",
                    signature, elapsedMs, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}
