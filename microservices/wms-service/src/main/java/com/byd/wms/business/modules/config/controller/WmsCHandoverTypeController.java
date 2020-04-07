package com.byd.wms.business.modules.config.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCHandoverTypeEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsCHandoverTypeService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 交接模式配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
@RestController
@RequestMapping("config/handoverType")
public class WmsCHandoverTypeController {
    @Autowired
    private WmsCHandoverTypeService wmsCHandoverTypeService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCHandoverTypeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCHandoverTypeEntity handoverType = wmsCHandoverTypeService.selectById(id);

        return R.ok().put("handoverType", handoverType);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> param){
    
    	List<WmsCHandoverTypeEntity> handoverTypeList=wmsCHandoverTypeService.selectList(new EntityWrapper<WmsCHandoverTypeEntity>()
	    		 .eq("WH_NUMBER", param.get("whNumber")).eq("WERKS", param.get("werks"))
	             .eq("BUSINESS_NAME", param.get("businessName"))
	            // .eq("HANDOVER_TYPE", param.get("handoverType"))
	             .eq("DEL","0"));
    	if(handoverTypeList.size()>0) {
    		return R.error("该业务类型交接模式配置已维护!");
    	}
    	WmsCHandoverTypeEntity entity=JSON.parseObject(JSON.toJSONString(param), WmsCHandoverTypeEntity.class);
    	boolean isOk=wmsCHandoverTypeService.insert(entity);
    	if(isOk) {
        	return R.ok();
        }else {
        	return R.error("保存失败");
        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> param){
    	WmsCHandoverTypeEntity entity=JSON.parseObject(JSON.toJSONString(param), WmsCHandoverTypeEntity.class);
    	boolean isOk=wmsCHandoverTypeService.updateById(entity);
        if(isOk) {
        	return R.ok();
        }else {
        	return R.error("更新失败");
        }
        
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody Map<String, Object> param){
    	WmsCHandoverTypeEntity entity=JSON.parseObject(JSON.toJSONString(param), WmsCHandoverTypeEntity.class);
    	wmsCHandoverTypeService.updateById(entity);
		return R.ok();
    }
    
    @RequestMapping("/wmsCHandoverTypelist")
    public R wmsCHandoverTypelist(@RequestParam Map<String, Object> params){
    	List<String> codeList_f=new ArrayList<String>();//前台 业务类型 编码
    	JSONArray jarr_business_code_list = JSON.parseArray(params.get("BUSINESS_CODE_LIST").toString());
    	for(int i=0;i<jarr_business_code_list.size();i++){
    		String BUSINESS_CODE=jarr_business_code_list.get(i)==null?"":jarr_business_code_list.get(i).toString();
    		codeList_f.add(BUSINESS_CODE);
    	}
    	params.put("list", codeList_f);
    	//由于总装物流的工厂可能是多个，所以用集合到sql中查询
    	if(params.get("WERKS")!=null&&!"".equals(params.get("WERKS").toString())){
    		String[] werksArr=params.get("WERKS").toString().split(",");
    		params.put("werkslist", werksArr);
    	}
    	
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	result=wmsCHandoverTypeService.getList(params);
    	
    	List<String> codeList=new ArrayList<String>();//查询出来的 业务类型 编码
    	String handover_type="";
    	String handover_type_flag="";//查询的记录中交接类型是否一致,比如无交接，非无交接
    	for(int j=0;j<result.size();j++){
    		if(!"".equals(handover_type)){
    			if(!handover_type.equals(result.get(j).get("HANDOVER_TYPE"))){
    				handover_type_flag="false";
    			}
    		}else{
    			handover_type=result.get(j).get("HANDOVER_TYPE").toString();
    		}
    		codeList.add(result.get(j).get("BUSINESS_CODE")==null?"":result.get(j).get("BUSINESS_CODE").toString());
		}
    	String business_code_str="";
    	for(int i=0;i<codeList_f.size();i++){
    		String BUSINESS_CODE=codeList_f.get(i);
    		if(!codeList.contains(BUSINESS_CODE)){
    			business_code_str=business_code_str+","+BUSINESS_CODE;
    		}
    	}
        return R.ok().put("result", result).put("business", business_code_str).put("handover_type_flag", handover_type_flag);
    }
    /**
     * 复制保存
     * @param entitys
     * @return
     */
    @RequestMapping("/saveCopyData")
    public R saveCopyData(@RequestBody List<Map<String, Object>> list){
    	wmsCHandoverTypeService.merge(list);
    	return R.ok();
    }
}
