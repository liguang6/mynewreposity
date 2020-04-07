package com.byd.web.wms.in.controller;

import com.byd.utils.R;
import com.byd.web.wms.in.service.StoPrintConfigRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 需求查询，删除，关闭
 * @author yang lin
 * @date 2019-06-03
 *
 */
@Controller
@RequestMapping("in/sto")
public class StoPrintConfigController {
    @Autowired
    StoPrintConfigRemote stoPrintConfigRemote;
    @RequestMapping("/query")
    @ResponseBody
    public R query(@RequestParam Map<String, Object> params){
        return  stoPrintConfigRemote.query(params);
    }
    @RequestMapping("/edit")
    @ResponseBody
    public R edit(@RequestParam Map<String,Object> params){
        return stoPrintConfigRemote.edit(params);
    }
    @RequestMapping("/add")
    @ResponseBody
    public R add(@RequestParam Map<String,Object> param){
        return stoPrintConfigRemote.add(param);
    }
    @RequestMapping("/del")
    @ResponseBody
    public R del(@RequestParam Map<String,Object> params){
        return stoPrintConfigRemote.del(params);
    }



}
