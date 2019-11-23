package com.chamc.archtype.budget.web;

import com.chamc.archtype.budget.support.excel.ExcelException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = ExcelException.class)
    public String excelParseError(ExcelException ex){
        return ex.getMessage();
    }

}
