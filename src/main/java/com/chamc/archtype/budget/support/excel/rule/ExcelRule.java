package com.chamc.archtype.budget.support.excel.rule;

import com.alibaba.excel.context.AnalysisContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;
import java.util.Map;

public interface ExcelRule<T> extends ApplicationEventPublisherAware, ApplicationContextAware {

    void handleExcelData(T data, AnalysisContext context);

    void handleHeaderExcelData(Map<Integer, String> headerMap);

    Class<T> excelDataClass();

    /**
     * 判断excel表格是否解析到末尾
     * @param context
     * @return
     */
    boolean endCondition(AnalysisContext context);

    List<T> getSyncDataList();

    ReadExcelEvent<T> getReadEvent();


    int headerNum();

    int sheetIndex();

}
