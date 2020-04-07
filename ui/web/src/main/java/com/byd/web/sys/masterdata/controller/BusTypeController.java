package com.byd.web.sys.masterdata.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.entity.BusTypeEntity;
import com.byd.web.sys.masterdata.service.BusTypeRemote;

/**
 * 车型表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-05 15:56:12
 */
@RestController
@RequestMapping("masterdata/bustype")
public class BusTypeController {
    @Autowired
    private BusTypeRemote busTypeRemote;
    @Autowired
    private UserUtils userUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return busTypeRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return busTypeRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody BusTypeEntity settingBusType){
    	return busTypeRemote.save(settingBusType);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BusTypeEntity settingBusType){
        return busTypeRemote.update(settingBusType);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        return busTypeRemote.delete(ids);
    }

}
