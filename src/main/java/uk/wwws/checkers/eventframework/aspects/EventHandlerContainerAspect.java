package uk.wwws.checkers.eventframework.aspects;

import java.lang.reflect.Method;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.eventframework.EventManager;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.aspects.errors.EventHandlerWrongDeclarationError;

@Aspect
public class EventHandlerContainerAspect {
    private static final Logger logger = LogManager.getRootLogger();

    @After("execution((@uk.wwws.checkers.eventframework.annotations.EventHandlerContainer *).new(..))")
    public void onCreate(JoinPoint joinPoint) {
        int annotatedMethodCount =
                addSuperclassMethods(joinPoint.getThis(), joinPoint.getThis().getClass());

        logger.debug("Added {} handlers in {}", annotatedMethodCount, joinPoint.getThis());
    }

    private int addSuperclassMethods(Object mainThis, Class<?> superClass) {
        int annotatedMethodCount = 0;

        for (Method declaredMethod : superClass.getDeclaredMethods()) {
            EventHandler annotation = declaredMethod.getAnnotation(EventHandler.class);

            logger.trace("Checking method for adding to event handlers: {} {} {}", superClass,
                         declaredMethod, declaredMethod.getDeclaredAnnotations());

            if (annotation == null) {
                continue;
            }

            if (declaredMethod.getParameterTypes().length != 1) {
                throw new EventHandlerWrongDeclarationError(
                        "The event handler should have 1 parameter");
            }

            Class<?> firstParameter = declaredMethod.getParameterTypes()[0];
            if (!isSubclass(firstParameter, Event.class)) {
                throw new EventHandlerWrongDeclarationError(
                        "The event handler's first argument should extend Event");
            }

            declaredMethod.setAccessible(true);


            EventManager.getInstance().addListener(firstParameter.asSubclass(Event.class),
                                                   new Pair<>(mainThis, declaredMethod),
                                                   annotation.priority(),
                                                   annotation.ignoreCanceled(),
                                                   annotation.isPlatform());

            annotatedMethodCount++;

            logger.debug("Added new handler: {}", declaredMethod);
        }

        if (superClass.getSuperclass() == Object.class) {
            return annotatedMethodCount;
        }

        return annotatedMethodCount + addSuperclassMethods(mainThis, superClass.getSuperclass());
    }

    private boolean isSubclass(Class<?> clazz, Class<?> superClass) {
        if (clazz.getSuperclass() == Object.class) {
            return false;
        }

        if (clazz.getSuperclass() == superClass) {
            return true;
        }

        return isSubclass(clazz.getSuperclass(), superClass);
    }
}