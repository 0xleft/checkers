package uk.wwws.checkers.eventframework.aspects;

import java.lang.annotation.Annotation;
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
        int annotatedMethodCount = 0;

        for (Method declaredMethod : joinPoint.getThis().getClass().getDeclaredMethods()) {
            if (!declaredMethod.isAnnotationPresent(EventHandler.class)) {
                continue;
            }

            if (declaredMethod.getParameterTypes().length != 1) {
                throw new EventHandlerWrongDeclarationError(
                        "The event handler should have 1 parameter");
            }

            Class<?> firstParameter = declaredMethod.getParameterTypes()[0];
            if (!(hasSuperclass(firstParameter, Event.class))) {
                throw new EventHandlerWrongDeclarationError(
                        "The event handler's first argument should extend Event");
            }

            declaredMethod.setAccessible(true);
            EventHandler annotation = declaredMethod.getAnnotation(EventHandler.class);

            EventManager.getInstance().addListener((firstParameter.asSubclass(Event.class)),
                                                   new Pair<>(joinPoint.getThis(), declaredMethod),
                                                   annotation.priority(),
                                                   annotation.ignoreCanceled());

            annotatedMethodCount++;
        }

        if (annotatedMethodCount == 0) {
            logger.warn("No handlers were found for EventHandlerContainer annotated class in: {}",
                        joinPoint.getThis().getClass());
            return;
        }

        logger.debug("Added {} handlers in {}", annotatedMethodCount,
                     joinPoint.getThis().getClass());
    }

    private boolean hasSuperclass(Class<?> clazz, Class<?> superClass) {
        if (clazz.getSuperclass() == Object.class) {
            return false;
        }

        if (clazz.getSuperclass() == superClass) {
            return true;
        }

        return hasSuperclass(clazz.getSuperclass(), superClass);
    }
}