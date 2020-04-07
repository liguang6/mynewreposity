package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.common.staticFactory.LocaleLanguageFactory;
import com.byd.web.wms.config.service.WmsSBestRuleRemote;

/**
 * @author ren.wei3
 * 最优出入库规则配置
 */
@RestController
@RequestMapping("config/ruleConfig")
public class WmsSBestRuleController {

	@Autowired
    private WmsSBestRuleRemote wmsSBestRuleRemote;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	R rtn =  wmsSBestRuleRemote.list(params);
    	//国际化处理
    	Map<String,Object> list = (Map<String,Object>)rtn.get("page");
    	List<Map<String,Object>> records = (List<Map<String, Object>>) list.get("list");
    	for (Map<String, Object> map : records) {
			if(StringUtils.isNotBlank(map.get("ruleType")==null?null:map.get("ruleType").toString())){
				if (map.get("ruleType").equals("1")) {
					map.put("ruleType",LocaleLanguageFactory.getValue("INBOUND"));
				} else {
					map.put("ruleType",LocaleLanguageFactory.getValue("BUSINESS_OUT"));
				}
			}
//			if(StringUtils.isNotBlank(map.get("ruleFlag")==null?null:map.get("ruleFlag").toString())){
//				if (map.get("ruleFlag").equals("1")) {
//					map.put("ruleFlag",LocaleLanguageFactory.getValue("CONTROL_FLAG"));
//				} else if (map.get("ruleFlag").equals("2")) {
//					map.put("ruleFlag",LocaleLanguageFactory.getValue("WH_BUSINESS_TYPE"));
//				} else if (map.get("ruleFlag").equals("3")) {
//					map.put("ruleFlag",LocaleLanguageFactory.getValue("LGORT"));
//				} else if (map.get("ruleFlag").equals("4")) {
//					map.put("ruleFlag",LocaleLanguageFactory.getValue("STOCK_TYPE"));
//				}
//			}
//			if(StringUtils.isNotBlank(map.get("status")==null?null:map.get("status").toString())){
//				if (map.get("status").equals("X")) {
//					map.put("status",LocaleLanguageFactory.getValue("ENABLED"));
//				} else {
//					map.put("status",LocaleLanguageFactory.getValue("DISABLED"));
//				}
//			}
		}
        return rtn;
    }
    
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsSBestRuleRemote.info(id);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
	    params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
	    params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsSBestRuleRemote.save(params);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
	    params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsSBestRuleRemote.update(params);
    }
    
    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("error");
		}
    	return wmsSBestRuleRemote.delById(id);
    }
}
