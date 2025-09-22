package com.carbon.aop;

import com.carbon.annotation.Log;
import com.carbon.common.BusinessType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 请求响应日志 AOP
 **/
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    /**
     * 执行拦截
     */
    @Around("@annotation(logAnnotation)")
    public Object doInterceptor(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        // 获取方法注解
        //MethodSignature signature = (MethodSignature) point.getSignature();
        //Method method = signature.getMethod();
        // 获取注解信息
        String title = logAnnotation.title();
        BusinessType businessType = logAnnotation.businessType();
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String requestId = UUID.randomUUID().toString();
        String url = request.getRequestURI();
        // 请求参数
        Object[] args = point.getArgs();
        log.info("request start, id: {}, path: {}, ip: {}, module: {}, type: {}, params: {}",
                requestId, url, request.getRemoteHost(), title, businessType, args);
        // 执行方法
        Object result = point.proceed();
        // 响应日志
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms, response: {}", requestId, cost, result);
        return result;
    }
}

