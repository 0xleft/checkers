package uk.wwws.checkers.eventframework.aspects;

import java.lang.reflect.Method;
import javafx.util.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.eventframework.EventManager;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.aspects.errors.EventHandlerWrongDeclarationError;

@Aspect
public class EventHandlerContainerAspect {
    @After("execution((@uk.wwws.checkers.eventframework.annotations.EventHandlerContainer *).new(..))")
    public void onCreate(JoinPoint joinPoint) {

        for (Method declaredMethod : joinPoint.getThis().getClass().getDeclaredMethods()) {
            if (!declaredMethod.isAnnotationPresent(EventHandler.class)) {
                continue;
            }

            if (declaredMethod.getParameterTypes().length != 1) {
                throw new EventHandlerWrongDeclarationError(
                        "The event handler should have 1 parameter");
            }

            Class<?> firstParameter = declaredMethod.getParameterTypes()[0];
            System.out.println(firstParameter);


            // todo also check for all superclasses and if any of them are subclass of Event.class should also add.
            if (!(firstParameter.getSuperclass() == Event.class)) {
                throw new EventHandlerWrongDeclarationError(
                        "The event handler's first argument should extend Event");
            }

            declaredMethod.setAccessible(true);

            EventManager.getInstance().addListener((firstParameter.asSubclass(Event.class)),
                                                   new Pair<>(joinPoint.getThis(), declaredMethod));
        }
    }
}