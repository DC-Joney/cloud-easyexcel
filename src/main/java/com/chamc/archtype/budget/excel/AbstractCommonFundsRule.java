package com.chamc.archtype.budget.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.chamc.archtype.budget.pojo.excel.SummaryFunds;
import com.chamc.archtype.budget.support.excel.rule.AbstractExcelRule;

import java.util.Collections;
import java.util.LinkedHashMap;


// 处理 SummaryFunds 数据
public abstract class AbstractCommonFundsRule<T extends SummaryFunds> extends AbstractExcelRule<T> {

    private Class<T> dataClass;

    private int sheetIndex;

    public AbstractCommonFundsRule(Class<T> dataClass, int sheetIndex) {
        super(Collections.singletonList(HeaderKey.of(3, "title")), dataClass);
        this.dataClass = dataClass;
        this.sheetIndex = sheetIndex;
    }

    /**
     * 读取excel表格什么时候停止
     * @param context
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean endCondition(AnalysisContext context) {
        Object result = context.readRowHolder().getCurrentRowAnalysisResult();
        if (result instanceof LinkedHashMap) {
            LinkedHashMap<Integer, CellData> linkedHashMap = (LinkedHashMap<Integer, CellData>) result;
            return linkedHashMap.size() > 0 && endCondition(linkedHashMap);
        }
        return false;
    }


    @Override
    public int sheetIndex() {
        return sheetIndex;
    }

    @Override
    public int headerNum() {
        return 4;
    }

    protected boolean endCondition(LinkedHashMap<Integer, CellData> floor) {
        return true;
    }



}
