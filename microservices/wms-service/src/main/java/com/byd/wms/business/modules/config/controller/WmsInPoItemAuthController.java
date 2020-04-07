package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.config.entity.WmsInPoItemAuthEntity;
import com.byd.wms.business.modules.config.service.WmsInPoItemAuthService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月31日 下午1:17:47 
 * 类说明 
 */
@RestController
@RequestMapping("/config/poAuth")
public class WmsInPoItemAuthController {
//	@Autowired
//    private SysDeptService sysDeptService;
	@Autowired
	WmsInPoItemAuthService wmsinpoitemauthService;
	@Autowired
	private UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmsinpoitemauthService.queryPage(params);	
		return R.ok().put("page", page);
	}
	/*
	 * 根据采购单号获取采购列表
	 */
	@RequestMapping("/getPolist")
	public R getPolist(@RequestParam Map<String, Object> params){
		
		//查询登陆用户对应的工厂
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("IN_KPO_AUTH");
    	
    	List<WmsInPoItemAuthEntity> werksList=new ArrayList<WmsInPoItemAuthEntity>();
    	for(Map dept:deptList){
    		WmsInPoItemAuthEntity bean=new WmsInPoItemAuthEntity();
    		bean.setWerks(dept.get("CODE").toString());
    		werksList.add(bean);
    	}
    	
    	params.put("werksList", werksList);
		List<Map<String,Object>> retPolist = wmsinpoitemauthService.getPolist(params);	
		return R.ok().put("retPolist", retPolist);
	}
	
	@RequestMapping("/PoAuthSave")
	public R savePoData(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		
		wmsinpoitemauthService.savePoAuthData(params);
		return R.ok();
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	WmsInPoItemAuthEntity wmsInPoItemAuth = wmsinpoitemauthService.selectById(id);
        return R.ok().put("wmsInPoItemAuth", wmsInPoItemAuth);
    }
    
    @RequestMapping("/update")
	public R update(@RequestBody WmsInPoItemAuthEntity entity){
		
		wmsinpoitemauthService.updateById(entity);
		return R.ok();
	}
    
    @RequestMapping("/del")
	public R delete(Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsInPoItemAuthEntity entity = new WmsInPoItemAuthEntity();
		entity.setId(id);
		entity.setDel("1");
		wmsinpoitemauthService.updateById(entity);
		return R.ok();
	}
    
    @RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsInPoItemAuthEntity entity = new WmsInPoItemAuthEntity();
			entity.setId(Long.parseLong(id[i]));
			entity.setDel("1");
			wmsinpoitemauthService.updateById(entity);
		}
		return R.ok();
	}
    
    @RequestMapping("/queryByEbeln")
	public R queryByEbeln(@RequestParam Map<String, Object> params){
    	List<WmsInPoItemAuthEntity> retQuery=new ArrayList<WmsInPoItemAuthEntity>();
    	
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){//EBELN
			String ebeln=jarr.getJSONObject(i).getString("EBELN");
			String ebelp=jarr.getJSONObject(i).getString("EBELP");
			
			Map<String, Object> cond=new HashMap<String, Object>();
			cond.put("EBELN", ebeln);
			cond.put("EBELP", ebelp);
			cond.put("DEL", "0");
			List<WmsInPoItemAuthEntity> rettemp=wmsinpoitemauthService.selectByMap(cond);
			if(rettemp.size()>0){
				retQuery.add(rettemp.get(0));
			}
		}
    	
    	
    	return R.ok().put("retQuery", retQuery);
    }
    /**
     * 小写的参数
     * @param params
     * @return
     */
    @RequestMapping("/queryByEbelnMin")
	public R queryByEbelnMin(@RequestParam Map<String, Object> params){
    	List<WmsInPoItemAuthEntity> retQuery=new ArrayList<WmsInPoItemAuthEntity>();
    	
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){//EBELN
			String ebeln=jarr.getJSONObject(i).getString("ebeln");
			String ebelp=jarr.getJSONObject(i).getString("ebelp");
			
			Map<String, Object> cond=new HashMap<String, Object>();
			cond.put("EBELN", ebeln);
			cond.put("EBELP", ebelp);
			cond.put("DEL", "0");
			List<WmsInPoItemAuthEntity> rettemp=wmsinpoitemauthService.selectByMap(cond);
			if(rettemp.size()>0){
				retQuery.add(rettemp.get(0));
			}
		}
    	
    	
    	return R.ok().put("retQuery", retQuery);
    }
    
    @RequestMapping("/preview")
	public R previewExcel(List<WmsInPoItemAuthEntity> entityList) throws IOException{
		
		if(!CollectionUtils.isEmpty(entityList)){
			
			for(WmsInPoItemAuthEntity entity:entityList){
				String msg="";
				
				if(StringUtils.isBlank(entity.getWerks()==null ? "" : entity.getWerks())){
					msg+="采购工厂不能为空";
				}
				if(StringUtils.isBlank(entity.getEbeln()==null ? "" : entity.getEbeln())){
					msg+="采购订单不能为空";
				}
				if(StringUtils.isBlank(entity.getEbelp()==null ? "" : entity.getEbelp())){
					msg+="行号不能为空";
				}
				entity.setMsg(msg);	
				entityList.add(entity);
			}
		}
		return R.ok().put("data", entityList);
	}
    @RequestMapping("/import")
	public R upload(@RequestParam Map<String, Object> params) throws IOException{
    	wmsinpoitemauthService.savePoAuthDataImport(params);
		return R.ok();
    }
}
