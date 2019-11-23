package com.chamc.archtype.budget.support.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.chamc.archtype.budget.support.excel.rule.ExcelRule;
import com.chamc.archtype.budget.support.excel.rule.ReadExcelEvent;
import com.chamc.archtype.budget.support.excel.sheet.ExcelReadListener;
import com.chamc.archtype.budget.utils.EventBusPool;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * 同步Excel管理器
 */
@Slf4j
public class SyncExcelManagement implements ExcelManagement {

    private int successState = 0;

    private int sheetSize;

    private ApplicationEventPublisher publisher;

    private Set<ExcelReadListener<?>> listeners = Sets.newHashSet();

    private List<ReadExcelEvent<?>> readExcelEvents = Lists.newLinkedList();

    private Function<List<ReadExcelEvent<?>>, ApplicationEvent> convertFunction;

    private EventBusPool eventBusPool;

    SyncExcelManagement(EventBusPool eventBusPool) {
        this.eventBusPool = eventBusPool;
    }

    SyncExcelManagement(EventBusPool eventBusPool, Function<List<ReadExcelEvent<?>>, ApplicationEvent> convertFunction) {
        this.eventBusPool = eventBusPool;
        this.convertFunction = convertFunction;
    }

    @Subscribe
    public void excelReadSuccess(SyncSheetSuccessEvent event) {

        successState += event.getNum();

        if (event.getEvent().getSource().size() > 0) {
            readExcelEvents.add(event.getEvent());
        }

        if (successState == sheetSize) {

            //发布数据
            if (Objects.nonNull(convertFunction)) {

                ApplicationEvent applicationEvent = convertFunction.apply(readExcelEvents);

                Objects.requireNonNull(applicationEvent, "Custom event must not be null");

                publisher.publishEvent(applicationEvent);

                //清除缓存
                listeners.clear();
                readExcelEvents.clear();

            } else {
                readExcelEvents.forEach(publisher::publishEvent);
            }
        }
    }


    @Subscribe
    public void clearCache(ClearCacheEvent cacheEvent) {
        try {
            cacheEvent.eventBus.unregister(this);
            eventBusPool.getObjectPool().returnObject(cacheEvent.eventBus);
            if(cacheEvent.errorState){
                listeners.clear();
                readExcelEvents.clear();
            }
        } catch (Exception e) {
            log.error("卸载eventBus出错，错误信息为 : ", e);
            throw new ExcelException("解析excel数据出错,请联系管理员解决");
        }

    }

    @Override
    public void readExcel(ExcelReader excelReader) {
        this.sheetSize = listeners.size();
        listeners.forEach(listener -> {
            ExcelRule<?> excelRule = listener.getExcelRule();
            ReadSheet readSheet =
                    EasyExcel.readSheet(excelRule.sheetIndex()).head(excelRule.excelDataClass())
                            .headRowNumber(excelRule.headerNum())
                            .registerReadListener(listener).build();
            excelReader.read(readSheet);
        });
    }

    void addListener(ExcelReadListener excelReadListener) {
        try {
            excelReadListener.register(this, eventBusPool.getObjectPool().borrowObject());
        } catch (Exception e) {
            log.error("注册sheet单元出错，错误信息为 : {} ", e);
            throw new ExcelException("解析EXCEL表格出错，请及时联系管理员");
        }
        listeners.add(excelReadListener);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    @Getter
    @RequiredArgsConstructor
    public static class SyncSheetSuccessEvent {
        private int num = 1;
        @NonNull
        private ReadExcelEvent<?> event;
    }

    @RequiredArgsConstructor(staticName = "of")
    public static class ClearCacheEvent {

        @NonNull
        private EventBus eventBus;

        @NonNull
        private boolean errorState;

    }

}
