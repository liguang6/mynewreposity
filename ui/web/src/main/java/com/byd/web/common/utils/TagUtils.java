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
import com.byd.web.qms.config.service.QmsDictRemote;
import com.byd.web.sys.masterdata.entity.DictEntity;
import com.byd.web.sys.masterdata.entity.SysDeptEntity;
import com.byd.web.sys.masterdata.service.DeptRemote;
import com.byd.web.sys.masterdata.service.DictRemote;
import com.byd.web.sys.masterdata.service.MasterDataRemote;
import com.byd.web.wms.config.entity.SysDictEntity;
import com.byd.web.wms.config.service.WmsDictRemote;
import com.byd.web.wms.report.service.EngineMaterialRemote;

/* * 
 * 前台页面调用格式：${tag.dictSelect(id,type,false,defaultval)}
 * */
@Component
public class TagUtils {
	@Autowired
    private DeptRemote sysDeptRemote;//平台部门
	@Autowired
	private WmsDictRemote wmsDictRemote;//wms服务字典
	@Autowired
	private QmsDictRemote qmsDictRemote;//qms服务字典
	@Autowired
	private DictRemote masterdataDictRemote;//主数据服务字典
    @Autowired
    private UserUtils userUtils;
    @Autowired 
    private EngineMaterialRemote engineMaterialRemote;
	@Autowired
    private MasterDataRemote masterDataRemote;//主数据服务
	
	/**
	 * @param 
	 * id:sellect标签的ID  不能为空值;
	 * type:字典类型 不能为空值;
	 * blank：是否需要空值项 不能为空值;
	 * params[0]:默认选项值(code) 可为空值;
	 */
	public List<SysDictEntity> wmsDictList(String type) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("TYPE", type);
		List<SysDictEntity> list = wmsDictRemote.selectByMap(params);
		for (SysDictEntity sysDictEntity : list) {
			if(StringUtils.isNotBlank(sysDictEntity.getLangKey())){
				if(StringUtils.isNotBlank(LocaleLanguageFactory.getValue(sysDictEntity.getLangKey()))) {
					sysDictEntity.setValue(LocaleLanguageFactory.getValue(sysDictEntity.getLangKey()));
				}
				
			}
		}
		return list;
	}
	/**
	 * @param 
	 * type:字典类型 不能为空值;
	 */
	public List<Map<String,Object>> qmsDictList(String type) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("type", type);
		List<Map<String,Object>> list = qmsDictRemote.getDictList(params);
		return list;
	}
	
	/**
	 * @param 
	 * type:字典类型 不能为空值;
	 */
	public List<DictEntity> masterdataDictList(String type) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("type", type);
		List<DictEntity> list = masterdataDictRemote.selectByMap(params);
		return list;
	}
	
	/**
	 * @param 
	 * type:字典类型 不能为空值;
	 */
	public List<Map<String,Object>> busTypeList(String type) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("VEHICLE_TYPE", type);
		R r = masterDataRemote.getBusTypeList(params);
		if(r.get("data") !=null) {
			return (List<Map<String,Object>>)r.get("data");
		}
		return new ArrayList<>();
	}
	
	/**
	 * 部门
	 * @param type
	 * @return
	 */
	public List<SysDeptEntity> deptListByType(String type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deptType", type);
		List<SysDeptEntity> list = sysDeptRemote.list(params);
		return list;
	}
	
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
	 * 根据菜单KEY,工厂代码获取当前登录用户拥有的车间权限
	 * @param MENU_KEY
	 * @return
	 */
	public List<Map<String,Object>> getUserAuthWorkshopByWerks(String MENU_KEY,String WERKS){
		Map<String,Object> params = new HashMap<>();
		params.put("MENU_KEY", MENU_KEY);
		params.put("WERKS", WERKS);
		R r = masterDataRemote.getUserWorkshopByWerks(params);
		
		List<Map<String,Object>> data = new ArrayList<>();
		if(r.get("data") !=null) {
			data = (List<Map<String,Object>>)r.get("data");
		}
		return data;
	}
	
	/**
	 * 根据菜单KEY,工厂代码、车间获取当前登录用户拥有的线别权限
	 * @param MENU_KEY
	 * @return
	 */
	public List<Map<String,Object>> getUserAuthLine(String MENU_KEY,String WERKS,String WORKSHOP){
		Map<String,Object> params = new HashMap<>();
		params.put("MENU_KEY", MENU_KEY);
		params.put("WERKS", WERKS);
		params.put("WORKSHOP", WORKSHOP);
		R r = masterDataRemote.getUserLine(params);
		
		List<Map<String,Object>> data = new ArrayList<>();
		if(r.get("data") !=null) {
			data = (List<Map<String,Object>>)r.get("data");
		}
		
		return data;
	}
	
	/**
	 * 根据工厂代码获取所有车间
	 * @param 
	 * @return
	 */
	public List<Map<String,Object>> getWorkshopByWerks(String WERKS){
		Map<String,Object> params = new HashMap<>();
		params.put("WERKS", WERKS);
		R r = masterDataRemote.getWerksWorkshopList(params);
		
		List<Map<String,Object>> data = new ArrayList<>();
		if(r.get("data") !=null) {
			data = (List<Map<String,Object>>)r.get("data");
		}
		return data;
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
	
	public List<Map<String, Object>> getProject() {
		Map<String, Object> map = new HashMap<>();
		return engineMaterialRemote.queryProject(map);
	}
}
