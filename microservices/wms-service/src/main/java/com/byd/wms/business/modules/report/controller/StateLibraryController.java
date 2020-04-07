package com.byd.wms.business.modules.report.controller;

import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.StateLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/5
 * 立库库存查询报表
 */
@RestController
@RequestMapping("report/stateLibrary")
public class StateLibraryController {

    @Autowired
    StateLibraryService stateLibraryService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return  R.ok().put("page", stateLibraryService.getInventoryList(params));
    }
}
