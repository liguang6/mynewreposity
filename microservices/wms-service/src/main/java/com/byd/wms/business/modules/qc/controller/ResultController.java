package com.byd.wms.business.modules.qc.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;
import com.byd.wms.business.modules.qc.service.WmsQcResultService;



/**
 * 检验结果
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcresult")
public class ResultController {
    @Autowired
    private WmsQcResultService wmsQcResultService;
    

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){

        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{editor}")
    public R info(@PathVariable("editor") String editor){
        WmsQcResultEntity wmsQcResult = wmsQcResultService.selectById(editor);

        return R.ok().put("wmsQcResult", wmsQcResult);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsQcResultEntity wmsQcResult){
        wmsQcResultService.insert(wmsQcResult);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsQcResultEntity wmsQcResult){
        ValidatorUtils.validateEntity(wmsQcResult);
        wmsQcResultService.updateAllColumnById(wmsQcResult);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody String[] editors){
        wmsQcResultService.deleteBatchIds(Arrays.asList(editors));

        return R.ok();
    }
    
    /**
     * 查询已质检改判数据
     * 收货单 - 送检单明细 - 质检结果明细
     * @param params
     * @return
     */
    @RequestMapping("/rejudgeitems")
    public R reJudgeInspectItems(@RequestParam Map<String,Object> params){
    	List<WmsQcResultEntity> resultItems = wmsQcResultService.queryRejudgeItems(params);
    	return R.ok().put("list", resultItems);
    }
    
}
