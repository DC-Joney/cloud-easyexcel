package com.chamc.archtype.budget.pojo.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * 实际投放
 */

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Validated
public class ActualCapital extends SummaryFunds {

    @ExcelProperty("说明")
    private String explain;


    @Override
    public String toString() {
        return super.toString();
    }
}
