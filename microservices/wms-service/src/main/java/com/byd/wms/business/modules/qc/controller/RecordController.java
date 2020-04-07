package com.byd.wms.business.modules.qc.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;
import com.byd.wms.business.modules.qc.service.WmsQcRecordService;



/**
 * 检验记录
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcrecord")
public class RecordController {
    @Autowired
    private WmsQcRecordService wmsQcRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsQcRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsQcRecordEntity wmsQcRecord = wmsQcRecordService.selectById(id);

        return R.ok().put("wmsQcRecord", wmsQcRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsQcRecordEntity wmsQcRecord){
        wmsQcRecordService.insert(wmsQcRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsQcRecordEntity wmsQcRecord){
        ValidatorUtils.validateEntity(wmsQcRecord);
        wmsQcRecordService.updateAllColumnById(wmsQcRecord);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsQcRecordService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
