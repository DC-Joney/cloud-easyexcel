package com.chamc.archtype.budget.support.excel.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * el 校验 返回类型是boolean
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SQLCheckValidator.class)
public @interface ValidateSql {

    String condition();

    Class<?> targetClass() default Void.class;

    boolean isNull() default true;

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
