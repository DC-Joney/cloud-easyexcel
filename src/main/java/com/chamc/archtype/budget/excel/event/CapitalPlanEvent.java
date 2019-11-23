package com.chamc.archtype.budget.excel.event;


import com.chamc.archtype.budget.pojo.excel.CapitalPlan;
import com.chamc.archtype.budget.support.excel.rule.ReadExcelEvent;

import java.util.List;

public class CapitalPlanEvent extends ReadExcelEvent<CapitalPlan> {
    public CapitalPlanEvent(List<CapitalPlan> source) {
        super(source);
    }
}
