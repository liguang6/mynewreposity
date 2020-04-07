package com.byd.wms.business.modules.common.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.entity.WmsCBatchPlanRulesEntity;
import com.byd.wms.business.modules.common.entity.WmsCDocNo;
import com.byd.wms.business.modules.common.service.WmsCBatchPlanRulesService;
import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/** 
 * @author 作者 E-mail: peng.tao1@byd
 * @version 创建时间：2018年8月28日 下午2:45:43 
 * 类说明 
 */
@RestController
@RequestMapping("/common/batchRules")
public class WmsCBatchPlanRulesController {
	@Autowired
	WmsCBatchPlanRulesService wmscBatchPlanRulesService;
	@Autowired
	WmsSapPlantLgortService lgortService;
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page=wmscBatchPlanRulesService.queryPage(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/querybatchcode")
    public R queryBatchCode(@RequestParam Map<String, Object> params){
		List<Map<String, Object>>  retlist=wmscBatchPlanRulesService.selectBatchCodeList(params);
		return R.ok().put("retlist", retlist);
	}
	
	@RequestMapping("/listBatchOnly")
	public R queryBatchOnly(@RequestParam Map<String, Object> params){
		params.put("del", "0");
		String onlyOne="";
		String onlyid="";
		List<WmsCBatchPlanRulesEntity> entityList=wmscBatchPlanRulesService.selectByMap(params);
		if(entityList!=null&&entityList.size()>0){//存在
			onlyOne="true";
			onlyid=entityList.get(0).getId().toString();
		}else{
			onlyOne="false";
		}
		return R.ok().put("onlyOne", onlyOne).put("onlyId", onlyid);
	}
	
	@RequestMapping("/save")
	public R add(@RequestParam Map<String, Object> params){
		WmsCBatchPlanRulesEntity entity = new WmsCBatchPlanRulesEntity();
		entity.setWerks(params.get("werks")==null?"":params.get("werks").toString());
		entity.setBusinessName(params.get("businessName")==null?"":params.get("businessName").toString());
		entity.setBusinessNameText(params.get("businessNameText")==null?"":params.get("businessNameText").toString());
		entity.setBatchRuleCode(params.get("batchRuleCode")==null?"":params.get("batchRuleCode").toString());
		entity.setBatchRuleText(params.get("batchRuleText")==null?"":params.get("batchRuleText").toString());
		entity.setLgort(params.get("lgort")==null?"":params.get("lgort").toString());
		entity.setDangerFlag(params.get("dangerFlag")==null?"0":params.get("dangerFlag").toString());
		entity.setfBatchFlag(params.get("fBatchFlag")==null?"0":params.get("fBatchFlag").toString());
		entity.setDel("0");
		entity.setEditor(params.get("USERNAME").toString());
		entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
		wmscBatchPlanRulesService.insert(entity);
		return R.ok();
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	WmsCBatchPlanRulesEntity wmscbatchplanrules = wmscBatchPlanRulesService.selectById(id);
        return R.ok().put("wmscbatchplanrules", wmscbatchplanrules);
    }
    
    @RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params){
		WmsCBatchPlanRulesEntity entity = new WmsCBatchPlanRulesEntity();
		entity.setId(params.get("id")==null?null:Long.valueOf(params.get("id").toString()));
		entity.setWerks(params.get("werks")==null?"":params.get("werks").toString());
		entity.setBusinessName(params.get("businessName")==null?"":params.get("businessName").toString());
		entity.setBusinessNameText(params.get("businessNameText")==null?"":params.get("businessNameText").toString());
		entity.setBatchRuleCode(params.get("batchRuleCode")==null?"":params.get("batchRuleCode").toString());
		entity.setBatchRuleText(params.get("batchRuleText")==null?"":params.get("batchRuleText").toString());
		entity.setLgort(params.get("lgort")==null?"":params.get("lgort").toString());
		entity.setDangerFlag(params.get("dangerFlag")==null?"":params.get("dangerFlag").toString());
		entity.setfBatchFlag(params.get("fBatchFlag")==null?"0":params.get("fBatchFlag").toString());
		entity.setDel("0");
		entity.setEditor(params.get("USERNAME").toString());
		entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
		wmscBatchPlanRulesService.updateById(entity);
		return R.ok();
	}
    
    @RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsCBatchPlanRulesEntity entity = new WmsCBatchPlanRulesEntity();
			entity.setId(Long.parseLong(id[i]));
			entity.setDel("X");
			wmscBatchPlanRulesService.updateById(entity);
		}
		return R.ok();
	}
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/saveCopyData")
	@ResponseBody
	public R saveCopyData(@RequestBody()List<Map<String,Object>> detailList) throws IllegalAccessException{
		List<WmsCBatchPlanRulesEntity> savelist=new ArrayList<WmsCBatchPlanRulesEntity>();
		for(Map<String,Object> map : detailList) {
			WmsCBatchPlanRulesEntity entity= JSON.parseObject(JSON.toJSONString(map), WmsCBatchPlanRulesEntity.class);
			if(entity.getLgort()!=null && !entity.getLgort().equals("")) {
				List<WmsSapPlantLgortEntity> list=lgortService.selectList(new EntityWrapper<WmsSapPlantLgortEntity>()
			    		.eq("WERKS",entity.getWerks()).eq("LGORT", entity.getLgort())
			    		.eq("DEL","0"));
			    if(list.size()==0) {
			    	return R.error(entity.getWerks()+"工厂未维护库位【"+entity.getLgort()+"】！");
			    }
			}
			savelist.add(entity);
		}
		wmscBatchPlanRulesService.merge(savelist);
		return new R();
	}
}
