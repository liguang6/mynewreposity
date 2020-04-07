package com.byd.wms.business.modules.config.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerTypeEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCMatManagerService;
import com.byd.wms.business.modules.config.service.WmsCMatManagerTypeService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;

/**
 * 仓库人料关系配置
 *
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@RestController
@RequestMapping("config/matmanager")
public class WmsCMatManagerController {
    @Autowired
    private WmsCMatManagerService wmsCMatManagerService;
    @Autowired
    private WmsCMatManagerTypeService wmsCMatManagerTypeService;
    @Autowired
	private WmsSapVendorService wmsSapVendorService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatManagerService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCMatManagerEntity wmsCMatManager = wmsCMatManagerService.selectById(id);

        return R.ok().put("wmsCMatManager", wmsCMatManager);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatManagerEntity wmsCMatManager){
    	
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS", wmsCMatManager.getWerks()).
                eq("MATNR", wmsCMatManager.getMatnr()));
    	if(materialList.size()==0) {
    		return R.error("SAP物料未维护!");
    	}
//    	if(wmsCMatManager.getLifnr()!=null && !wmsCMatManager.getLifnr().equals("")) {
//    		List<WmsSapVendor> vendorList = wmsSapVendorService.selectList(
//    				new EntityWrapper<WmsSapVendor>().eq("LIFNR",wmsCMatManager.getLifnr()));
//    	    if(vendorList.size()==0) {
//    	    	return R.error("供应商未维护！");
//    	    }
//    	}
	    List<WmsCMatManagerTypeEntity> authorizeCodeList = wmsCMatManagerTypeService.selectList(
				new EntityWrapper<WmsCMatManagerTypeEntity>().eq("DEL", "0").eq("WERKS",wmsCMatManager.getWerks())
						.eq("WH_NUMBER",wmsCMatManager.getWhNumber()).eq("AUTHORIZE_CODE",wmsCMatManager.getAuthorizeCode()));
	    if(authorizeCodeList.size()==0) {
	    	return R.error("授权码未维护！");
	    }
	    List<WmsCMatManagerEntity> list=wmsCMatManagerService.selectList(
				new EntityWrapper<WmsCMatManagerEntity>().eq("DEL","0")
				.eq("WERKS",wmsCMatManager.getWerks())
				.eq("WH_NUMBER",wmsCMatManager.getWhNumber())
				.eq("MATNR",wmsCMatManager.getMatnr())
				.eq(StringUtils.isNotBlank(wmsCMatManager.getLifnr()),"LIFNR",wmsCMatManager.getLifnr())
				.eq("AUTHORIZE_CODE",wmsCMatManager.getAuthorizeCode())
	    		);
	    if(list.size()>0) {
	    	return R.error("仓库人料关系已维护！");
	    }
	   
        wmsCMatManagerService.insert(wmsCMatManager);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatManagerEntity wmsCMatManager){
    	List<WmsCMatManagerTypeEntity> authorizeCodeList = wmsCMatManagerTypeService.selectList(
				new EntityWrapper<WmsCMatManagerTypeEntity>().eq("DEL", "0").eq("WERKS",wmsCMatManager.getWerks())
						.eq("WH_NUMBER",wmsCMatManager.getWhNumber()).eq("AUTHORIZE_CODE",wmsCMatManager.getAuthorizeCode()));
	    if(authorizeCodeList.size()==0) {
	    	return R.error("授权码未维护！");
	    }
        wmsCMatManagerService.updateAllColumnById(wmsCMatManager);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody String[] ids){
        wmsCMatManagerService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatManagerEntity wmsCMatManager){
    	
		wmsCMatManagerService.updateById(wmsCMatManager);
		return R.ok();
    }
    /**
	 * 粘贴数据校验
	 * @param excel
	 * @return
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestBody Map<String,Object> param){
		Gson gson=new Gson();
		List<WmsCMatManagerEntity> list =gson.fromJson((String) param.get("saveData"),new TypeToken<List<WmsCMatManagerEntity>>() {}.getType());
		List<String> matnrList=new ArrayList<String>();
		List<String> lifnrList=new ArrayList<String>();
		List<String> authorizeCodeList=new ArrayList<String>();
		for(WmsCMatManagerEntity entity:list){
			String msg="";
			if(isEmpty(entity.getMatnr())){
				msg+="SAP料号不能为空;";
			}else {
				if(!matnrList.contains(entity.getMatnr())) {
					matnrList.add(entity.getMatnr());
				}
			}
			if(!isEmpty(entity.getLifnr())){
				if(!lifnrList.contains(entity.getLifnr())) {
					lifnrList.add(entity.getLifnr());
				}
			}
			if(isEmpty(entity.getAuthorizeCode())){
				msg+="授权码不能为空;";
			}else {
				if(!authorizeCodeList.contains(entity.getAuthorizeCode())) {
					authorizeCodeList.add(entity.getAuthorizeCode());
				}
			}
			entity.setMsg(msg);
		}
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("WERKS",param.get("werks"));
		paramMap.put("WH_NUMBER",param.get("whNumber"));
		paramMap.put("matnrList", matnrList);
		paramMap.put("lifnrList", lifnrList);
		paramMap.put("authorizeCodeList", authorizeCodeList);
		List<Map<String,Object>> matList=wmsCMatManagerService.validateMat(paramMap);
		Map<String, Map<String,Object>> lifnrMap =null;
		if(lifnrList.size()>0) {
			List<Map<String,Object>> lifList=wmsCMatManagerService.validateLifnr(paramMap);
			lifnrMap = lifList.stream().collect(
	                Collectors.toMap(w -> w.get("LIFNR").toString(),w -> w));
		}
		List<String> authorizeList=wmsCMatManagerService.validateAuthorizeCode(paramMap);
		// 将list转为Map
        Map<String, Map<String,Object>> matMap = matList.stream().collect(
                Collectors.toMap(w -> w.get("MATNR").toString(),w -> w));
        
		for(WmsCMatManagerEntity entity:list) {
			String matnr=entity.getMatnr();
			if(matMap.containsKey(matnr)) {
				entity.setMaktx(matMap.get(matnr).get("MAKTX").toString());
			}else {
				entity.setMsg(entity.getMsg()+"物料信息未维护;");
			}
			String lifnr=entity.getLifnr();
			if(lifnr!=null && !lifnr.equals("")) {
				if(lifnrMap.containsKey(lifnr)) {
					entity.setLiktx(lifnrMap.get(lifnr).get("NAME1").toString());
				}
			}
			
			if(!authorizeList.contains(entity.getAuthorizeCode())) {
				entity.setMsg(entity.getMsg()+"授权码信息未维护;");
			}
		}
		return new R().put("list", list);
	}
	/**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestBody()Map<String,Object> params) throws IllegalAccessException{
		Gson gson=new Gson();
		List<WmsCMatManagerEntity> detailList =gson.fromJson((String) params.get("detailList"),new TypeToken<List<WmsCMatManagerEntity>>() {}.getType());
	    
		for(WmsCMatManagerEntity detail:detailList){
			detail.setCreator(params.get("user").toString());
			detail.setEditor(params.get("user").toString());
			detail.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			detail.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		}
		wmsCMatManagerService.batchSave(detailList);
		return new R();
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
