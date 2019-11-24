package com.chamc.archtype.budget.web;

import com.chamc.archtype.budget.excel.ActualCapitalRule;
import com.chamc.archtype.budget.excel.CapitalPlanRule;
import com.chamc.archtype.budget.pojo.excel.SummaryFunds;
import com.chamc.archtype.budget.support.excel.ExcelUtils;
import com.chamc.archtype.budget.support.excel.rule.ExcelRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;


@Slf4j
@Api(tags = "接口类")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/budget")

// 公司是 spring mvc 模式
public class HelloController {

    private List<SummaryFunds> fundsList = Lists.newCopyOnWriteArrayList();

    @GetMapping("/test")
    public String test() {
        return "test";
    }


    @PostMapping("/upload")
    @ApiOperation(value = "upload", notes = "解析excel文件", httpMethod = "POST")
    public String summaryFunds(MultipartFile multipartFile) throws IOException {

        ImmutableList<ExcelRule<?>> excelRules = ImmutableList.of(new CapitalPlanRule(), new ActualCapitalRule());

        InputStream excelStream = multipartFile.getInputStream();

        ExcelUtils.readExcel(excelStream, excelRules, null,
                false);

        return "success";
    }


    @PostMapping("/asyncUpload")
    @ApiOperation(value = "upload", notes = "解析excel文件", httpMethod = "POST")
    public DeferredResult<String> asyncSummaryFunds(MultipartFile multipartFile) throws IOException {

        ImmutableList<ExcelRule<?>> excelRules = ImmutableList.of(new CapitalPlanRule(), new ActualCapitalRule());

        InputStream excelStream = multipartFile.getInputStream();

        DeferredResult<String> deferredResult = new DeferredResult<>();

        ExcelUtils.readExcelAsync(excelStream, excelRules, null,
                false, deferredResult);

        return deferredResult;
    }


    @GetMapping("/summaryFunds")
    @ApiOperation(value = "summaryFunds", notes = "读取数据", httpMethod = "GET")
    public List<SummaryFunds> getSummaryFunds() {
        return Collections.unmodifiableList(fundsList);
    }


}
