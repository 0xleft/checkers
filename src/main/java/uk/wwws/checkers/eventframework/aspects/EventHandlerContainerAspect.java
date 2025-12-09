package uk.wwws.checkers.eventframework.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class EventHandlerContainerAspect {
    @After("execution((@uk.wwws.checkers.eventframework.annotations.EventHandlerContainer *).new(..))")
    public void onCreate(JoinPoint joinPoint) {
        System.out.println(joinPoint.getThis().getClass());
    }
}