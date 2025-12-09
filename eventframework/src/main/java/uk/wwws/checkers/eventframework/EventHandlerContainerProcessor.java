package uk.wwws.checkers.eventframework;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("uk.wwws.checkers.events.EventHandlerContainer")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class EventHandlerContainerProcessor extends AbstractProcessor {
    public EventHandlerContainerProcessor() {
        processingEnv.getMessager().printNote("quicktime");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        throw new RuntimeException("error");
    }
}
