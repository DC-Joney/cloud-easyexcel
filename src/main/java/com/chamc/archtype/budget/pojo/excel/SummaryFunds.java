package com.chamc.archtype.budget.pojo.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.chamc.archtype.budget.support.excel.validate.ValidateSql;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
//@MappedSuperclass
@ExcelIgnoreUnannotated
public class SummaryFunds implements Serializable {

    @ExcelProperty("序号")
    private String seq;

    @NotNull(message = "资金不能为空")
    @ExcelProperty("金额")
    @DecimalMin(value = "0.00")
    private BigDecimal money;

    @NotBlank(message = "单位不能为空")
    @ExcelProperty("单位")
    private String unit;

    @NotBlank(message = "资金类型不能为空")
    @ValidateSql(condition = "@budgetService.selectOne(#capitalType)")
    @ExcelProperty("资金类型")
    private String capitalType;

    @ExcelProperty("项目名称")
    private String projectName;

    @ValidateSql(condition = "@budgetService.selectOne(#this)")
    @ExcelProperty("项目类型")
    private String projectType;

    @ValidateSql(condition = "@budgetService.selectOne(#this)")
    @ExcelProperty("专项额度使用")
    private String specialQuota;

    //添加时间
    private LocalDate localDate;

}
