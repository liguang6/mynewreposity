package com.byd.web.wms.qc.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.web.wms.qc.entity.WmsQcResultEntity;
import com.byd.web.wms.qc.service.WmsQcServiceRemote;



/**
 * 检验结果
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcresult")
public class WmsQcResultController {
   
	@Autowired
	WmsQcServiceRemote qcServiceRemote; 
	
	@Autowired
	UserUtils userUtils;
    
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return qcServiceRemote.listQcResult(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{editor}")
    public R info(@PathVariable("editor") String editor){

        return qcServiceRemote.infoQcResult(editor);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsQcResultEntity wmsQcResult){
       // wmsQcResultService.insert(wmsQcResult);

        return qcServiceRemote.saveQcResult(wmsQcResult);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsQcResultEntity wmsQcResult){
        ValidatorUtils.validateEntity(wmsQcResult);        
        return qcServiceRemote.updateQcResult(wmsQcResult);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody String[] editors){
        return qcServiceRemote.deleteQcResult(editors);
    }
    
    /**
     * 查询已质检改判数据
     * 收货单 - 送检单明细 - 质检结果明细
     * @param params
     * @return
     */
    @RequestMapping("/rejudgeitems")
    public R reJudgeInspectItems(@RequestParam Map<String,Object> params){
    	Map<String,Object> user =  userUtils.getUser();
    	String FULL_NAME = (String) user.get("FULL_NAME");
    	params.put("FULL_NAME", FULL_NAME);
    	return qcServiceRemote.reJudgeInspectItems(params);
    }
    
}
