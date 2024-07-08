package ru.gb.cafeteria.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
public class LoggingAspect {

    @Around("@annotation(TrackUserAction)")
    public Object logUser(ProceedingJoinPoint targetMethod) throws Throwable {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");

        Annotation[] targetMethodAnno = ((MethodSignature) targetMethod.getSignature()).getMethod().getAnnotations();
        String targetMethodAnnoStr = targetMethodAnno.length > 0 ? " (" + getSimpleNames(targetMethodAnno) + ") " : "";

        Object[] targetMethodArgs = targetMethod.getArgs();
        String targetMethodArgsStr = targetMethodArgs.length > 0 ? " с аргументами ["
                + Arrays.toString(targetMethodArgs) + "]" : " без аргументов";

        Object resultValue = targetMethod.proceed();

        String log = ">>> LOG " +
                dateFormat.format(new Date()) +
                ": вызван метод " +
                targetMethod.getSignature().getName() +
                targetMethodAnnoStr +
                targetMethodArgsStr +
                " с результатом: " +
                resultValue +
                ".\n";

        System.out.println(log);

        return resultValue;
    }

    private String getSimpleNames(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .map(annotation -> "@" + annotation.annotationType().getSimpleName() + formatAnnotationAttributes(annotation))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    private String formatAnnotationAttributes(Annotation annotation) {
        Method[] methods = annotation.annotationType().getDeclaredMethods();
        String attributes = Arrays.stream(methods)
                .map(method -> {
                    try {
                        Object value = method.invoke(annotation);
                        if (value.getClass().isArray()) {
                            return method.getName() + "={" + Arrays.toString((Object[]) value) + "}";
                        } else {
                            return method.getName() + "=" + value.toString();
                        }
                    } catch (Exception e) {
                        return method.getName() + "=N/A";
                    }
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return attributes.isEmpty() ? "" : "(" + attributes + ")";
    }
}
