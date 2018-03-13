package com.gorio.acs.kdcserver.tools;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;

/**
 * Class Name Logging2
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/24
 */
@NoArgsConstructor
@Slf4j
@Aspect
public class Logging {
    /** Following is the definition for a pointcut to select
     *  all the methods available. So advice will be called
     *  for all the methods.
     */
    @Pointcut("execution(* com.gorio.acs.kdcserver.entity.*.*(..))")
    private void selectAll(){}
    /**
     * This is the method which I would like to execute
     * before a selected method execution.
     */
    @Before("selectAll()")
    public void beforeAdvice(){
        log.info("========================================================================");
        log.info("================================begin===================================");
    }
    /**
     * This is the method which I would like to execute
     * after a selected method execution.
     */
    @After("selectAll()")
    public void afterAdvice(){
        log.info("================================done====================================");
    }
    /**
     * This is the method which I would like to execute
     * when any method returns.
     */
    @AfterReturning(pointcut = "selectAll()", returning="retVal")
    public void afterReturningAdvice(Object retVal){
        log.info("===============return result == {}====================================",retVal);
    }
    /**
     * This is the method which I would like to execute
     * if there is an exception raised by any method.
     */
    @AfterThrowing(pointcut = "selectAll()", throwing = "ex")
    public void afterThrowingAdvice(IllegalArgumentException ex){
        log.error("===============Not expected thing happen == {}====================================\n",ex.getMessage());
    }
}
