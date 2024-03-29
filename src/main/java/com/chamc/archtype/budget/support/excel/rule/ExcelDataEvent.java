package com.chamc.archtype.budget.support.excel.rule;

import org.springframework.context.ApplicationEvent;

/**
 * excel数据事件
 * @param <T>
 */
public abstract class ExcelDataEvent<T> extends ApplicationEvent {

    private T source;

    public ExcelDataEvent(T source) {
        super(source);
        this.source = source;
    }

    @Override
    public T getSource() {
        return source;
    }
}
