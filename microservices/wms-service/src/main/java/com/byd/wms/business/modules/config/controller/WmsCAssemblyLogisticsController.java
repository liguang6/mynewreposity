package com.byd.wms.business.modules.config.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCAssemblyLogisticsEntity;
import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.service.WmsCAssemblyLogisticsService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年7月26日 下午3:16:47 
 * 类说明 
 */
@RestController
@RequestMapping("config/assemblylogistics")
public class WmsCAssemblyLogisticsController {
	@Autowired
	private WmsCAssemblyLogisticsService wmsCAssemblyLogisticsService;
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCAssemblyLogisticsService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics){
    	
    	wmsCAssemblyLogisticsService.updateById(wmsCAssemblyLogistics);
		return R.ok();
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics = wmsCAssemblyLogisticsService.selectById(id);

        return R.ok().put("wmsCAssemblyLogistics", wmsCAssemblyLogistics);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics){
	    
        List<WmsCAssemblyLogisticsEntity> list=wmsCAssemblyLogisticsService.selectList(
    			new EntityWrapper<WmsCAssemblyLogisticsEntity>().eq("ASSEMBLY_WERKS", wmsCAssemblyLogistics.getAssemblyWerks())
    			.eq("WERKS_F", wmsCAssemblyLogistics.getWerksF())
    			.eq("LGORT_F", wmsCAssemblyLogistics.getLgortF())
    			.eq("SOBKZ", wmsCAssemblyLogistics.getSobkz())
    			.eq("WMS_MOVE_TYPE", wmsCAssemblyLogistics.getWmsMoveType())
    			.eq("SAP_MOVE_TYPE", wmsCAssemblyLogistics.getSapMoveType())
    			.eq("SAP_FLAG_F", wmsCAssemblyLogistics.getSapFlagF()).eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("该总装物流参数已维护！");
    	}else {
    		wmsCAssemblyLogisticsService.insert(wmsCAssemblyLogistics);
        	return R.ok();
    	}
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics){
        
    	wmsCAssemblyLogisticsService.updateAllColumnById(wmsCAssemblyLogistics);//全部更新
        
        return R.ok();
    }
    
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestBody()Map<String,Object> param) throws IllegalAccessException{
		Gson gson=new Gson();
		List<WmsCAssemblyLogisticsEntity> detailList =gson.fromJson((String) param.get("detailList"),new TypeToken<List<WmsCAssemblyLogisticsEntity>>() {}.getType()); 
		
		for(WmsCAssemblyLogisticsEntity detail:detailList){
			detail.setCreator(param.get("USERNAME").toString());
			detail.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			detail.setEditor(param.get("USERNAME").toString());
			detail.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		}
		wmsCAssemblyLogisticsService.batchSave(detailList);
		return new R();
	}
    
    /**
	 * 粘贴数据校验
	 * @return 
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestBody Map<String,Object> param){
		Gson gson=new Gson();
		List<WmsCAssemblyLogisticsEntity> list =gson.fromJson((String) param.get("saveData"),new TypeToken<List<WmsCAssemblyLogisticsEntity>>() {}.getType());
		
		for(WmsCAssemblyLogisticsEntity entity:list){
			String msg="";
			if(!"是".equals(entity.getWmsFlagF())&&!"否".equals(entity.getWmsFlagF())){
				msg+="是否上wms请填写是或者否;";
			}else if(!"无需过账".equals(entity.getSapFlagF())&&!"实时过账".equals(entity.getSapFlagF())&&!"异步过账".equals(entity.getSapFlagF())){
				msg+="过账标志请填写无需过账、实时过账、异步过账;";
			}
			entity.setMsg(msg);
		}
		return new R().put("list", list);
	}
	
	public boolean isEmpty(Object obj) {
		boolean result=false;
		if(obj==null) {
			return true;
		}
		if(StringUtils.isBlank(obj.toString())) {
			return true;
		}
		return result;
	}

}



