package com.byd.web.wms.config.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.web.wms.config.entity.WmsCQcReturnReasonsEntity;
import com.byd.web.wms.config.service.WmsCQcReturnReasonsRemote;

/**
 * 退货原因配置表
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-15 17:10:53
 */
@RestController
@RequestMapping("config/wmscqcreturnreasons")
public class WmsCQcReturnReasonsController {
    @Autowired
    private WmsCQcReturnReasonsRemote wmsCQcReturnReasonsRemote;
    @Autowired
    private UserUtils userUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsCQcReturnReasonsRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCQcReturnReasonsRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcReturnReasonsEntity wmsCQcReturnReasons){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCQcReturnReasons.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcReturnReasons.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCQcReturnReasonsRemote.add(wmsCQcReturnReasons);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcReturnReasonsEntity wmsCQcReturnReasons){
        ValidatorUtils.validateEntity(wmsCQcReturnReasons);
    	Map<String,Object> user = userUtils.getUser();
    	wmsCQcReturnReasons.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcReturnReasons.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCQcReturnReasonsRemote.update(wmsCQcReturnReasons);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCQcReturnReasonsRemote.delete(ids);

        return R.ok();
    }

}
