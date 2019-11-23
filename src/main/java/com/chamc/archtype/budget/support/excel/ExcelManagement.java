package com.chamc.archtype.budget.support.excel;

import com.alibaba.excel.ExcelReader;
import org.springframework.context.ApplicationEventPublisherAware;

public interface ExcelManagement extends ApplicationEventPublisherAware {

    void readExcel(ExcelReader excelReader);

}
