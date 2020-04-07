package com.byd.web.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.common.staticFactory.LocaleLanguageFactory;

/* * 
 * 前台页面调用格式：${tag.dictSelect(id,type,false,defaultval)}
 * */
@Component
public class TagUtils {
	
	
    @Autowired
    private UserUtils userUtils;
	
	
	
	
	
	
	/**
	 * 根据菜单KEY获取当前登录用户拥有的工厂权限
	 * @param MENU_KEY
	 * @return
	 */
	public Set<Map<String,Object>> getUserAuthWerks(String MENU_KEY){
		return userUtils.getUserWerks(MENU_KEY);
	}
	public Set<Map<String,Object>> getAllWerks(){
		return userUtils.getAllWerks();
	}
	
	/**
	 * 根据菜单KEY获取当前登录用户拥有的仓库号权限
	 * @param MENU_KEY
	 * @return
	 */
	public Set<Map<String,Object>> getUserAuthWh(String MENU_KEY){
		return userUtils.getUserWh(MENU_KEY);
	}
	
	
	/**
	 * 根据菜单KEY从 redis获取当前登录用户拥有特定数据对象的权限值
	 * @param MENU_KEY
	 * @param DATA_KEY
	 * @return
	 */
	public Set<Map<String,Object>> getUserDataAuth(String MENU_KEY,String DATA_FIELD){
		if(DATA_FIELD == null || StringUtils.isEmpty(DATA_FIELD)) {
			return null;
		}
		return userUtils.getUserDataAuth(MENU_KEY, DATA_FIELD);
	}
	
	/**
	 * 获取国际化翻译标签
	 * @param pkey 必填
	 * @param showType 语言长度：S、M、L，必填
	 * @param params 国际化中参数，如：欢迎{0}使用WMS  选填
	 * @return
	 */
	public String getLocale(String pkey,String showType,Object... params) {
//    	String defValue ="";
    	String outStr = LocaleLanguageFactory.getValue(pkey,showType);
    	
    	if (params != null && params.length > 0) {
    		outStr = LocaleLanguageFactory.getValue(pkey,showType,params);
    	}
    	
    	if(outStr==null){
    		outStr = pkey;
    	}
    	
		return outStr;
	}
	
}
