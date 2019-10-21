package com.enjoy.fire.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnjoyQualifier {
    String value() default "";
}
