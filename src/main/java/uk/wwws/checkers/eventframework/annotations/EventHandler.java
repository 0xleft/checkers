package uk.wwws.checkers.eventframework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.wwws.checkers.eventframework.Event;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventHandler {
    Priority priority() default Priority.NORMAL;
    boolean ignoreCanceled() default false;
}
