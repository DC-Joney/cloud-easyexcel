package com.chamc.archtype.budget.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface ExcelTaskExecutor {

    @AliasFor(annotation = Qualifier.class,value = "value")
    String value() default "";

}
