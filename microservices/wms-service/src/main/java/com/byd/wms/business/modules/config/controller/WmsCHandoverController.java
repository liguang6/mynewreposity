package com.byd.wms.business.modules.config.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCHandoverEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCHandoverService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 交接人员配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
@RestController
@RequestMapping("config/handover")
public class WmsCHandoverController {
    @Autowired
    private WmsCHandoverService wmsCHandoverService;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCHandoverService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCHandoverEntity handover = wmsCHandoverService.selectById(id);
        return R.ok().put("handover", handover);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> param){
    	
    	List<WmsCHandoverEntity> urgentList=wmsCHandoverService.selectList(new EntityWrapper<WmsCHandoverEntity>()
	    		 .eq("WH_NUMBER", param.get("whNumber")).eq("WERKS", param.get("werks"))
	             .eq("BUSINESS_NAME", param.get("businessName"))
	             .eq("STAFF_NUMBER", param.get("staffNumber")).eq("DEL","0"));
    	if(urgentList.size()>0) {
    		return R.error("交接人员已维护!");
    	}
    	WmsCHandoverEntity entity=JSON.parseObject(JSON.toJSONString(param), WmsCHandoverEntity.class);
    	wmsCHandoverService.insert(entity);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> param){
    	WmsCHandoverEntity entity=JSON.parseObject(JSON.toJSONString(param), WmsCHandoverEntity.class);
    	wmsCHandoverService.updateById(entity);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody Map<String, Object> param){
    	WmsCHandoverEntity entity=JSON.parseObject(JSON.toJSONString(param), WmsCHandoverEntity.class);
    	entity.setDel("X");
    	wmsCHandoverService.updateById(entity);
		return R.ok();
    }
    
    @RequestMapping("/wmsCHandoverlist")
    public R wmsCHandoverlist(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	result=wmsCHandoverService.getCHandoverList(params);
    	
    	String business_code_str="";
    	JSONArray jarr_business_code_list = JSON.parseArray(params.get("BUSINESS_CODE_LIST").toString());
    	for(int i=0;i<jarr_business_code_list.size();i++){
    		String BUSINESS_CODE=jarr_business_code_list.get(i)==null?"":jarr_business_code_list.get(i).toString();
    		
    		for(int j=0;j<result.size();j++){
    			if(result.get(j).get("BUSINESS_NAME") != null && result.get(j).get("BUSINESS_NAME").equals(BUSINESS_CODE)){
    				business_code_str=business_code_str+","+BUSINESS_CODE;
    			} else {
    				result.remove(j);
    				j--;
    			}
    		}
    	}
    	
        return R.ok().put("result", result).put("business", business_code_str);
    }

}
