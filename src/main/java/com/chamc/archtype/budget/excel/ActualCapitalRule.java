package com.chamc.archtype.budget.excel;


import com.alibaba.excel.context.AnalysisContext;
import com.chamc.archtype.budget.excel.event.ActualCapitalEvent;
import com.chamc.archtype.budget.pojo.excel.ActualCapital;
import com.chamc.archtype.budget.pojo.excel.SummaryFunds;
import com.chamc.archtype.budget.support.excel.rule.ReadExcelEvent;

import java.util.List;

public class ActualCapitalRule extends AbstractCommonFundsRule<ActualCapital> {

    public ActualCapitalRule() {
        super(ActualCapital.class,2);
    }

    @Override
    public ReadExcelEvent<ActualCapital> getExcelEvent(List<ActualCapital> dataList) {
        return new ActualCapitalEvent(dataList);
    }


    @Override
    public boolean endCondition(AnalysisContext context) {
        Object result = context.readRowHolder().getCurrentRowAnalysisResult();
        if(result instanceof SummaryFunds){
            SummaryFunds funds = (SummaryFunds) result;
            return funds.getSeq().equals("未投放");
        }
        return false;
    }


    @Override
    protected boolean handleData(ActualCapital capital, AnalysisContext context) {

        if(endCondition(context)){
            return false;
        }

        return !(capital.getCapitalType() == null && capital.getMoney() == null && capital.getUnit() == null);

    }
}
