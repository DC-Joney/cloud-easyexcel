package com.chamc.archtype.budget.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * 资金计划
 */
@Getter
@Setter
//@Entity
//@Table(name = "CAPITAL_PLAN")
@Validated
@EqualsAndHashCode(callSuper = true)
public class CapitalPlan extends SummaryFunds {

    @NotNull(message = "计划拨款日期不能为空")
    @ExcelProperty("计划拨款日期")
    @DateTimeFormat("yyyy/mm/dd")
    @Past
    private Date appropriationDate;

    @ExcelProperty("请示文号")
    private String  invitationNumber;

    @ExcelProperty("审批进展")
    private String approvalProgress;

    @ExcelProperty("申请日期")
    private String  filingDate;

    @DateTimeFormat("yyyy/mm/dd")
    @ExcelProperty("计划还款日")
    private Date repaymentDate;

    @Override
    public String toString() {
        return super.toString();
    }
}
