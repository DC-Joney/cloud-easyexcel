package com.chamc.archtype.budget.support.excel.sheet;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.chamc.archtype.budget.support.excel.*;
import com.chamc.archtype.budget.support.excel.rule.ExcelRule;
import com.chamc.archtype.budget.utils.EventBusPool;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ExcelReadListener<T> extends AnalysisEventListener<T> {

    @NonNull
    @Getter
    private final ExcelRule<T> excelRule;

    private boolean readCompleted;

    private EventBus eventBus;

    @Getter
    private final Map<Integer, String> headerMap;

    private boolean initHeader = false;

    private int headerNum = 0;

    private boolean asyncState;
//    private ExcelManagement management;

    public ExcelReadListener(ExcelRule<T> excelRule) {
        this(excelRule,false);
    }

    public ExcelReadListener(ExcelRule<T> excelRule,boolean asyncState) {
        this.asyncState = asyncState;
        this.excelRule = excelRule;
        this.headerMap = new HashMap<>(8);
    }


    @Override
    public void invoke(T data, AnalysisContext context) {
        if (!initHeader) {
            excelRule.handleHeaderExcelData(headerMap);
            initHeader = true;
        }

        if (!readCompleted) {
            excelRule.handleExcelData(data, context);
        }
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        eventBus.post(new ExcelReadCompletedEvent(asyncState));
        if(!asyncState){
            eventBus.post(new SyncExcelManagement.SyncSheetSuccessEvent(excelRule.getReadEvent()));
        }
    }

    //做头部解析
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {

        Map<Integer, String> excelHeadMap = Maps.filterValues(headMap, StringUtils::hasText);

        String headerMap = Joiner.on(",").withKeyValueSeparator("=").join(excelHeadMap);

        this.headerMap.put(headerNum++, headerMap);
    }


    @Override
    public boolean hasNext(AnalysisContext context) {

        if (readCompleted) {
            return false;
        }

        if (excelRule.endCondition(context)) {
            postSuccess(context);
        }

        return true;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        try {

            //挪动位置
            log.error("exceptions is ## : {}", exception.getMessage());

            ReadSheetHolder sheetHolder = context.readSheetHolder();

            ReadRowHolder rowHolder = context.readRowHolder();

            String message = "第" + (sheetHolder.getSheetNo() + 1) + "个sheet页面名称为 : " + sheetHolder.getSheetName() + "，==> ";

            if (exception instanceof ExcelParseException || exception instanceof ExcelTitleParseException) {

                if (exception instanceof ExcelParseException) {
                    message = message + "第" + (rowHolder.getRowIndex() + 1) + "行数据格式出错: " + exception.getMessage();
                }
                if (exception instanceof ExcelTitleParseException) {
                    message = "excel标头错误，差异信息为: " + exception.getMessage();
                }

                throw new ExcelException(message);
            }

            boolean executeEnd = excelRule.endCondition(context);
            if (!executeEnd) {
                message = message + "第" + (rowHolder.getRowIndex() + 1) + "行信息格式出错，请比对校验";
                throw new ExcelException(message);
            }

        } catch (Exception e) {
            unRegister(true);
            throw e;
        }

        postSuccess(context);
    }


    //通知 excelRule发布数据存储
    private void postSuccess(AnalysisContext context) {
        readCompleted = true;
        doAfterAllAnalysed(context);
        unRegister(false);
    }

    public void register(ExcelManagement excelManagement, EventBus eventBus) throws Exception {
        this.eventBus = eventBus;
        eventBus.register(excelRule);
        eventBus.register(excelManagement);
//        this.management = excelManagement;
    }

    private void unRegister(boolean errorState) {
        eventBus.unregister(excelRule);
        eventBus.post(SyncExcelManagement.ClearCacheEvent.of(eventBus,errorState));
        headerMap.clear();
        eventBus = null;
    }

}
