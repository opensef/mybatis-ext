package com.opensef.mybatisext.datascope;

import com.opensef.mybatisext.annotation.DataScope;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Order(0)
@Aspect
public class DataScopeAop {

    private final DataScopeHandler dataScopeHandler;

    public DataScopeAop(DataScopeHandler dataScopeHandler) {
        this.dataScopeHandler = dataScopeHandler;
    }

    @Pointcut("@annotation(com.opensef.mybatisext.annotation.DataScope)")
    public void pointCut() {
    }

    /**
     * 在目标方法被调用之前做增强处理
     *
     * @param joinPoint JoinPoint
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        // 功能编码
        String functionCode = getFunctionCode(joinPoint);
        DataScopeInfo dataScopeInfo = dataScopeHandler.create(functionCode);
        if (null != dataScopeInfo) {
            DataScopeUtil.set(dataScopeInfo);
        }
    }

    /**
     * 获取功能编码
     *
     * @param joinPoint JoinPoint
     * @return 功能编码
     */
    private String getFunctionCode(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DataScope dataScope = method.getAnnotation(DataScope.class);
        // 功能编码
        return dataScope.functionCode();
    }

    @After("pointCut()")
    public void after() {
        DataScopeUtil.clear();
    }

}
