package com.chamc.archtype.budget.support.excel.rule;

import com.alibaba.excel.context.AnalysisContext;
import com.chamc.archtype.budget.support.excel.ExcelParseException;
import com.chamc.archtype.budget.support.excel.ExcelReadCompletedEvent;
import com.chamc.archtype.budget.support.excel.ExcelTitleParseException;
import com.chamc.archtype.budget.support.excel.validate.ExcelValidatorFactory;
import com.chamc.archtype.budget.utils.LocalContext;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.function.Function;


/**
 * excel 处理规则
 *
 * @param <T>
 */

@Slf4j
public abstract class AbstractExcelRule<T> implements ExcelRule<T>, ApplicationEventPublisherAware,
        InitializingBean {

    private static final Function<Map<String, String>, Set<String>> functionConverter =
            headerMap -> Sets.newHashSet(Maps.filterValues(headerMap, StringUtils::hasText).values());

    private Collection<HeaderKey> headerNames;

//    private static final int DATA_LIMIT = 1000;

    @Getter
    private List<T> dataList = new LinkedList<>();

    @Getter
    private Map<String, String> headerMap = new HashMap<>(8);

    private ApplicationEventPublisher eventPublisher;

    private Class<T> dataClass;

    private ApplicationContext applicationContext;

    @Getter
    private ExcelValidatorFactory validatorFactory;

    @Getter
    private LoadingCache<Class<?>, Set<String>> excelClassCache;

    @Override
    public void afterPropertiesSet() {

    }

    public AbstractExcelRule(Collection<HeaderKey> headerNames, Class<T> dataClass) {
        this.dataClass = dataClass;
        this.headerNames = headerNames;
        this.validatorFactory = LocalContext.getValidatorFactory();
        this.excelClassCache = LocalContext.getExcelClassCache();
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void handleExcelData(T data, AnalysisContext context) {

        boolean checkState = handleData(data, context);


        if (checkState && getValidatorFactory().supports(dataClass)) {
            DataBinder dataBinder = new DataBinder(data);
            dataBinder.setValidator(getValidatorFactory());
            dataBinder.validate();
            BindingResult bindingResult = dataBinder.getBindingResult();
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldError();
                throw new ExcelParseException(fieldError.getDefaultMessage());
            }
        }
        dataList.add(data);

//        //控制大小
//        if (dataList.size() >= DATA_LIMIT) {
//            eventPublisher.publishEvent(getExcelEvent(dataList));
//            dataList.clear();
//        }
    }

    /**
     * 需要发布的事件
     *
     * @param dataList
     * @return
     */
    public abstract ReadExcelEvent<T> getExcelEvent(List<T> dataList);

    public void handleHeaderExcelData(Map<Integer, String> headers) {

        log.info("AbstractExcelRule init count");

        if (headerNames == null || headerNames.size() <= 0) {
            return;
        }

        headerNames.forEach(headerKey -> headerMap.put(headerKey.dataName, headers.remove(headerKey.key)));

        String title = getHeaderMap().get("title");

        if (!StringUtils.hasText(title)) {
            throw new ExcelTitleParseException("titles数据不能为空!!!");
        }

        Set<String> titleSet = functionConverter.apply(Splitter.on(",").withKeyValueSeparator("=").split(title));

        Set<String> excelFieldSet = getExcelClassCache().getUnchecked(dataClass);

        Sets.SetView<String> intersection = Sets.difference(titleSet, excelFieldSet);

        if (intersection.size() > 0) {
            throw new ExcelTitleParseException(Joiner.on(",").skipNulls().join(intersection) + " 列不匹配,请按照模板修改");
        }

        headerNames = null;

    }

    @Override
    public Class<T> excelDataClass() {
        return dataClass;
    }


    @Subscribe
    public void excelReadEnd(ExcelReadCompletedEvent completedEvent) {

        clearCache();

        //异步发布事件
        if (completedEvent.isAsyncState()) {
            if (dataList.size() > 0) {
                log.info("Header Map is" + headerMap);
                eventPublisher.publishEvent(getExcelEvent(dataList));
            }
        }
    }


    @Override
    public List<T> getSyncDataList() {
        return dataList;
    }


    @Override
    public ReadExcelEvent<T> getReadEvent() {
        return getExcelEvent(dataList);
    }

    private void clearCache() {
        headerMap.clear();
    }

    /**
     * 判断数据是否需要进行校验、忽略、或者存储
     *
     * @param data
     * @param context
     * @return
     */
    protected abstract boolean handleData(T data, AnalysisContext context);


    @RequiredArgsConstructor(staticName = "of")
    protected static class HeaderKey {

        @NonNull
        private Integer key;

        @NonNull
        private String dataName;
    }

}
