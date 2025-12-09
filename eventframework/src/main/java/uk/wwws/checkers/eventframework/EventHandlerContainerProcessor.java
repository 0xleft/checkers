package uk.wwws.checkers.eventframework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("uk.wwws.checkers.eventframework.EventHandlerContainer")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class EventHandlerContainerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printError("WHAT");
        JavaFileObject file = null;
        try {
            file = processingEnv.getFiler().createSourceFile("GeneratedClass");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter out = new PrintWriter(file.openWriter())) {
            out.println("package com.example;");
            out.println("public class GeneratedClass { /* content */ }");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
