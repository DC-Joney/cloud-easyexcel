package com.chamc.archtype.budget.excel.event;


import com.chamc.archtype.budget.pojo.excel.ActualCapital;
import com.chamc.archtype.budget.support.excel.rule.ReadExcelEvent;

import java.util.List;

public class ActualCapitalEvent extends ReadExcelEvent<ActualCapital> {

    public ActualCapitalEvent(List<ActualCapital> dataList) {
        super(dataList);
    }
}
