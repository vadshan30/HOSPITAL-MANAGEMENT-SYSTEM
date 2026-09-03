package com.examly.springapp.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.examly.springapp.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        // Service method logging
    }
}
