package com.chamc.archtype.budget.support.excel.validate;

import com.chamc.archtype.budget.support.excel.ExcelUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class SQLCheckValidator implements ConstraintValidator<ValidateSql, String> {

    private ValidateSql validateSql;

    @Override
    public void initialize(ValidateSql validateSql) {
        this.validateSql = validateSql;
    }


    @Override
    public boolean isValid(String targetValue, ConstraintValidatorContext context) {

        ValidateSqlExpressContext elContext = ExcelUtils.getContext();

        if (!StringUtils.hasText(validateSql.condition())) {
            throw new RuntimeException("The condition must not null");
        }

        if (validateSql.isNull() && !StringUtils.hasText(targetValue)) {
            return true;
        }

        AnnotatedElementKey fieldKey = new AnnotatedElementKey(targetValue.getClass(), validateSql.targetClass());

        return elContext.condition(validateSql.condition(), targetValue, fieldKey);
    }

}
