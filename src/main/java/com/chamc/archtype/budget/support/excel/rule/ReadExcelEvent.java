package com.chamc.archtype.budget.support.excel.rule;


import java.util.List;

/**
 * 读取excel数据事件
 * @param <T>
 */
public abstract class ReadExcelEvent<T> extends ExcelDataEvent<List<T>> {
    public ReadExcelEvent(List<T> dataList) {
        super(dataList);
    }
}
