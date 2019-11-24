package com.chamc.archtype.budget.support.excel;

import com.alibaba.excel.ExcelReader;
import com.chamc.archtype.budget.support.excel.sheet.ExcelReadListener;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public interface ExcelManagement extends ApplicationEventPublisherAware {


    void addListener(ExcelReadListener<?> excelReadListener);

    void readExcel(ExcelReader excelReader);

    void readAsyncExcel(ExcelReader excelReader, DeferredResult<String> deferredResult);

}
