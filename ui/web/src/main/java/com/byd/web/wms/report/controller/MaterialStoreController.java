package com.byd.web.wms.report.controller;

import com.byd.utils.R;
import com.byd.web.wms.report.service.MaterialStoreRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/11/25
 */

@RestController
@RequestMapping("report/materialStore")
public class MaterialStoreController {

    @Autowired
    MaterialStoreRemote materialStoreRemote;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return materialStoreRemote.list(params);
    }
}
