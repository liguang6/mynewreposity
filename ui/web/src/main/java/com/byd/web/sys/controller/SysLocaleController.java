package com.byd.web.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.web.common.staticFactory.LocaleLanguageFactory;
import com.byd.web.sys.service.SysLocaleService;

/**
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("sys/language")
public class SysLocaleController {

	@Autowired
    private SysLocaleService sysLocaleService;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysLocaleService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R deleteById(@RequestParam Map<String, Object> params){
    	
    	String languageListStr=params.get("languageList").toString();
    	List<Map> languageList = JSONObject.parseArray(languageListStr, Map.class);
    	for (Map<String,Object> languagemap: languageList) {
    		if (null != languagemap.get("id") && !languagemap.get("id").equals("")) {
    			Long id = Long.valueOf(languagemap.get("id").toString());
        		sysLocaleService.deleteById(id);
    		}
    	}
//        return R.ok("删除成功！");
        return R.ok(LocaleLanguageFactory.getValue("DELETE_SUCCESS"));
    }
    
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params) {
    	String languageListStr=params.get("languageList").toString();
    	
    	List<Map> languageList = JSONObject.parseArray(languageListStr, Map.class);
    	sysLocaleService.saveLanguage(languageList);
    	
    	return R.ok(LocaleLanguageFactory.getValue("SAVE_SUCCESS"));
//    	return R.ok("保存成功！");
    }
    
    /**
     * 根据KEY获取国际化
     */
    @ResponseBody
    @RequestMapping("/getLocale")
    public R getLocale(@RequestParam Map<String, Object> params){
		System.out.println(params.toString());
    	String keyArrayStr=params.get("keyArray").toString();
    	String showType=params.get("showType").toString();
    	JSONArray keyArray=JSONArray.parseArray(keyArrayStr);
    	Map<String,String> map = new HashMap<String,String>();
    	for (int i = 0; i < keyArray.size(); i++) {
    		String outStr = LocaleLanguageFactory.getValue(keyArray.getString(i), showType);
    		map.put(keyArray.getString(i), outStr);
		}
    	
        return R.ok().put("locales", map);
    }
    
    /**
     * 根据KEY获取国际化
     */
    @ResponseBody
    @RequestMapping("/getPKey")
    public R getPKey(@RequestParam Map<String, Object> params){
    	String keyStr=params.get("keyStr").toString();
    	String showType=params.get("showType").toString();
    	Object localeparams=params.get("localeparams");
    	
    	Map<String,String> map = new HashMap<String,String>();
    	String outStr = LocaleLanguageFactory.getValue(keyStr,showType);
        if (localeparams != null) {
        	outStr = LocaleLanguageFactory.parsePropertyTokens(outStr, localeparams);
        }
    	map.put(keyStr, outStr);
    	
        return R.ok().put("localebykey", outStr);
    }
}
