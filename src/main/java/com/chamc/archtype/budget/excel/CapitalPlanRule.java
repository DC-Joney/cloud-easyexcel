package com.chamc.archtype.budget.excel;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.chamc.archtype.budget.excel.event.CapitalPlanEvent;
import com.chamc.archtype.budget.pojo.excel.CapitalPlan;
import com.chamc.archtype.budget.support.excel.rule.ReadExcelEvent;

import java.util.LinkedHashMap;
import java.util.List;

public class CapitalPlanRule extends AbstractCommonFundsRule<CapitalPlan> {

    public CapitalPlanRule() {
        super(CapitalPlan.class,0);
    }

    @Override
    public ReadExcelEvent<CapitalPlan> getExcelEvent(List<CapitalPlan> dataList) {
        return new CapitalPlanEvent(dataList);
    }

    @Override
    protected boolean endCondition(LinkedHashMap<Integer, CellData> floor) {
        if (floor.get(1) != null) {
            CellData cellData = floor.get(1);
            return cellData.toString().equals("合计");
        }
        return false;
    }


    @Override
    protected boolean handleData(CapitalPlan capitalPlan, AnalysisContext context) {
        return !(capitalPlan.getCapitalType() == null && capitalPlan.getMoney() == null && capitalPlan.getUnit() == null &&
                capitalPlan.getAppropriationDate() == null);
    }
}
