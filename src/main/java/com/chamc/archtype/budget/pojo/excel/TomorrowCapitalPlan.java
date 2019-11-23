package com.chamc.archtype.budget.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.CellData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 资金计划(T+1)
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class TomorrowCapitalPlan extends SummaryFunds {

    @NotNull
    @ExcelProperty("计划拨款日期")
    private CellData<String> appropriationDate;

    @ExcelProperty("审批进展")
    private String approvalProgress;

    @ExcelProperty("申请日期")
    private String  filingDate;

    @ExcelProperty("附件")
    private Object object;

    @Override
    public String toString() {
        return super.toString();
    }
}
