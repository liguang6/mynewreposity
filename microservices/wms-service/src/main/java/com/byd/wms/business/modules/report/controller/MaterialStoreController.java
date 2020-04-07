package com.byd.wms.business.modules.report.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.MaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/11/25
 * 收料房报表
 */

@RestController
@RequestMapping("report/materialStore")
public class MaterialStoreController {

    @Autowired
    MaterialStoreService materialStoreService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return  R.ok().put("page", materialStoreService.getMaterialList(params));
    }
}
