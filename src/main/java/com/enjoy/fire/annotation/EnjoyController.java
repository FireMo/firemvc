package com.enjoy.fire.annotation;

import java.lang.annotation.*;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/8 21:08
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnjoyController {
    String value() default "";
}
