package com.chamc.archtype.budget.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.chamc.archtype.budget.support.excel.ExcelUtils;
import com.chamc.archtype.budget.support.excel.validate.ExcelValidatorFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * 缓存、校验、EventBus管理
 */
public class LocalContext implements InitializingBean, ApplicationContextAware {

    @Getter
    private static volatile ExcelValidatorFactory validatorFactory;

    @Getter
    private static volatile LoadingCache<Class<?>, Set<String>> excelClassCache;

    private ApplicationContext applicationContext;

    @Autowired
    private EventBusPool eventBusPool;

    private LocalContext() {
    }

    @Override
    public void afterPropertiesSet() {
        excelClassCache = CacheBuilder.newBuilder()
                .maximumSize(100).initialCapacity(10)
                .softValues().weakKeys()
                .build(new ExcelCacheLoader());
        LocalContext.validatorFactory = new ExcelValidatorFactory(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public static EventBusPool getEventBusPool() {
        return shardInstance().eventBusPool;
    }


    public static LocalContext shardInstance() {
        return LocalContextHolder.INSTANCE;
    }

    private static class LocalContextHolder {
        private static final LocalContext INSTANCE = new LocalContext();
    }


    //缓存excel表头对应的处理
    private static class ExcelCacheLoader extends CacheLoader<Class<?>, Set<String>> {

        @Override
        public Set<String> load(@ParametersAreNonnullByDefault Class<?> classKey) {

            HashSet<String> excelFileNames = Sets.newHashSet();

            ReflectionUtils.doWithFields(classKey, field -> {

                AnnotationAttributes attributes =
                        AnnotatedElementUtils.findMergedAnnotationAttributes(field, ExcelProperty.class,
                                false, true);

                String[] values;

                if (attributes != null && (values = attributes.getStringArray("value")) != null && values.length > 0) {

                    //合并字符串
                    // do something

                    Optional<String> first = Arrays.stream(values).filter(StringUtils::hasText).findFirst();
                    first.ifPresent(excelFileNames::add);

                }

            });
            return Collections.unmodifiableSet(excelFileNames);
        }
    }
}
