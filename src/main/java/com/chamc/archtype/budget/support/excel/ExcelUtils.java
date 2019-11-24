package com.chamc.archtype.budget.support.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.chamc.archtype.budget.support.excel.rule.ExcelRule;
import com.chamc.archtype.budget.support.excel.rule.ReadExcelEvent;
import com.chamc.archtype.budget.support.excel.sheet.ExcelReadListener;
import com.chamc.archtype.budget.support.excel.validate.ValidateSqlExpressContext;
import com.chamc.archtype.budget.utils.LocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Slf4j
public class ExcelUtils implements ApplicationContextAware, ApplicationEventPublisherAware, InitializingBean {

    private ApplicationContext applicationContext;

    private ExcelUtils() {
    }

    @Autowired
    private LocalContext localContext;

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void afterPropertiesSet() {


    }


    public static void readExcel(InputStream inputStream, Map<Integer, ExcelRule<?>> ruleMap) {

        Assert.notNull(ruleMap, "ruleMap must not be null");

        ExcelReader excelReader = EasyExcel.read(inputStream).build();

        SyncExcelManagement excelManagement = new SyncExcelManagement(LocalContext.getEventBusPool());

        excelManagement.setApplicationEventPublisher(getSharedInstance().eventPublisher);

        ruleMap.forEach((sheetNo, excelRule) -> {

            ExcelReadListener<?> sheetListener = new ExcelReadListener<>(excelRule);

            excelManagement.addListener(sheetListener);

            if (Objects.nonNull(sheetNo) && Objects.nonNull(excelRule)) {
                excelRule.setApplicationEventPublisher(getSharedInstance().eventPublisher);
                excelRule.setApplicationContext(getSharedInstance().applicationContext);
                ReadSheet readSheet =
                        EasyExcel.readSheet(sheetNo).head(excelRule.excelDataClass())
                                .headRowNumber(excelRule.headerNum())
                                .registerReadListener(sheetListener).build();

                excelReader.read(readSheet);
            }
        });

        excelReader.finish();
    }


    public static void readExcelAsync(InputStream inputStream,
                                      List<ExcelRule<?>> excelRules,
                                      Function<List<ReadExcelEvent<?>>, ApplicationEvent> convertFunction,
                                      boolean asyncState, DeferredResult<String> deferredResult) {

        Assert.notNull(deferredResult,"The deferred result must not be null");

        LocalContext.getExcelExecutor().submit(() -> {

            Instant before = Instant.now();

            ExcelReader excelReader = EasyExcel.read(inputStream).build();

            SyncExcelManagement excelManagement = new SyncExcelManagement(LocalContext.getEventBusPool(), convertFunction);

            readExcel(excelRules, asyncState, excelManagement);

            excelManagement.readAsyncExcel(excelReader, deferredResult);

            excelReader.finish();

            log.info("Async Execute parse excel time is : {}", Duration.between(before, Instant.now()));

        });
    }

    /**
     * 代码不规范，有问题，需要改，reactor是流式处理，这么写没好处
     *
     * @param inputStream     excel IO流
     * @param excelRules      excel表格对应的sheet页面
     * @param convertFunction //excel多Sheet页面事件处理
     * @param asyncState      //是否异步
     * @param deferredResult  返回上传数据信息结果
     */
    public static void readExcel(InputStream inputStream,
                                 List<ExcelRule<?>> excelRules,
                                 Function<List<ReadExcelEvent<?>>, ApplicationEvent> convertFunction,
                                 boolean asyncState) {

        Instant before = Instant.now();

        ExcelReader excelReader = EasyExcel.read(inputStream).build();

        SyncExcelManagement excelManagement = new SyncExcelManagement(LocalContext.getEventBusPool(), convertFunction);

        readExcel(excelRules, asyncState, excelManagement);

        excelManagement.readExcel(excelReader);

        excelReader.finish();

        log.info("Sync Execute parse excel time is : {}", Duration.between(before, Instant.now()));

    }


    private static void readExcel(List<ExcelRule<?>> excelRules,
                                  boolean asyncState, ExcelManagement excelManagement) {
        Assert.notEmpty(excelRules, "ruleMap must not be null");

        excelManagement.setApplicationEventPublisher(getSharedInstance().eventPublisher);

        excelRules.forEach(excelRule -> {

            ExcelReadListener<?> sheetListener = new ExcelReadListener<>(excelRule, asyncState);

            excelManagement.addListener(sheetListener);

            if (Objects.nonNull(excelRule)) {
                excelRule.setApplicationEventPublisher(getSharedInstance().eventPublisher);
                excelRule.setApplicationContext(getSharedInstance().applicationContext);
            }
        });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static ExcelUtils getSharedInstance() {
        return ExcelUtilsHolder.INSTANCE;
    }

    private static class ExcelUtilsHolder {
        private static final ExcelUtils INSTANCE = new ExcelUtils();
    }


}
