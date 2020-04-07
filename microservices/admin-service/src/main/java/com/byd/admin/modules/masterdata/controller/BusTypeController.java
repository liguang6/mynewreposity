package com.byd.admin.modules.masterdata.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.masterdata.entity.BusTypeEntity;
import com.byd.admin.modules.masterdata.service.BusTypeService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;

/**
 * 车型表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-05 15:56:12
 */
@RestController
@RequestMapping("/masterdata/bustype")
public class BusTypeController {
    @Autowired
    private BusTypeService settingBusTypeService;
    @Autowired
    private UserUtils userUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = settingBusTypeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        BusTypeEntity settingBusType = settingBusTypeService.selectById(id);

        return R.ok().put("settingBusType", settingBusType);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody BusTypeEntity settingBusType){
    	ValidatorUtils.validateEntity(settingBusType);
    	
    	Map<String,Object> currentUser = userUtils.getUser();
    	
        settingBusType.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
        settingBusType.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        settingBusTypeService.insert(settingBusType);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BusTypeEntity settingBusType){
        ValidatorUtils.validateEntity(settingBusType);
        Map<String,Object> currentUser = userUtils.getUser();
        settingBusType.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
        settingBusType.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        settingBusTypeService.updateAllColumnById(settingBusType);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        settingBusTypeService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
