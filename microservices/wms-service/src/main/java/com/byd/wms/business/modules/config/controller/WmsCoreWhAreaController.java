package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhAreaService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;

/**
 * 仓库存储类型配置 各工厂仓库存储类型设置
 * @author tangj
 * @email 
 * @date 2018年08月06日 
 */
@RestController
@RequestMapping("config/wharea")
public class WmsCoreWhAreaController {
    @Autowired
    private WmsCoreWhAreaService wmsCoreWhAreaService;
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCoreWhAreaService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	WmsCoreWhAreaEntity wharea = wmsCoreWhAreaService.selectById(id);
        return R.ok().put("wharea", wharea);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreWhAreaEntity whArea){
//    	List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
//				new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER",whArea.getWhNumber()).eq("DEL","0"));
//	    if(whList.size()==0) {
//	    	return R.error("仓库信息未维护！");
//	    }
	    /*List<WmsCoreStorageTypeEntity> storageTypeList=wmsCoreStorageTypeService.selectList(
				new EntityWrapper<WmsCoreStorageTypeEntity>().eq("STORAGE_TYPE_CODE",whArea.getStorageAreaCode()).eq("DEL","0"));
	    if(storageTypeList.size()==0) {
	    	return R.error("储存类型未维护！");
	    }*/
	    
        List<WmsCoreWhAreaEntity> list=wmsCoreWhAreaService.selectList(
    			new EntityWrapper<WmsCoreWhAreaEntity>().eq("WH_NUMBER", whArea.getWhNumber())
    			.eq("STORAGE_AREA_CODE",whArea.getStorageAreaCode())
    			.eq("AREA_NAME",whArea.getAreaName())
    			.eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("仓库存储类型配置已维护！");
    	}else {
    		wmsCoreWhAreaService.insert(whArea);
        	return R.ok();
    	}
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreWhAreaEntity wh){
        
    	wmsCoreWhAreaService.updateById(wh);
        return R.ok();
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCoreWhAreaEntity wh){
    	
		wmsCoreWhAreaService.updateById(wh);
		return R.ok();
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<Map<String,Object>> mapList) throws IOException{
    	List<WmsCoreWhAreaEntity> addList=new ArrayList<WmsCoreWhAreaEntity>();
    	List<WmsCoreWhAreaEntity> updateList=new ArrayList<WmsCoreWhAreaEntity>();

    	StringBuffer matnr=new StringBuffer();
    	/*for(Map<String,Object> entity : entityList) {
    		// 封装成 【仓库代码-储存代码-存储区代码,仓库代码-储存代码-存储区代码】
    		matnr.append(entity.getWhNumber().concat("-").concat(entity.getStorageAreaCode())
    				//.concat("-").concat(entity.getAreaCode())
    				.concat(","));
    	}*/
    	String [] param=matnr.toString().split(",");
    	//List<Map<String,Object>> list=wmsCoreWhAreaService.validate(Arrays.asList(param));
    	/*for(WmsCoreWhAreaEntity entity : entityList) {
    		
    		String concatStr=entity.getWhNumber()+"-"+entity.getStorageAreaCode()
    				+"-"+entity.getAreaCode();
    		boolean updateFlag=false;
    		for(Map<String,Object> map : list) {
    			String uniqueStr=map.get("UNIQUE_STR").toString();
    			if(concatStr.equals(uniqueStr)) {
    				entity.setId(Long.valueOf(map.get("ID").toString()));
    				updateList.add(entity);
    				updateFlag=true;
    				break;
    			}
    		}
    		if(!updateFlag) {
    			addList.add(entity);
    		}
    	}*/
    	
    	for(int m=0;m<mapList.size();m++){
    		WmsCoreWhAreaEntity entityTemp=new WmsCoreWhAreaEntity();
    		entityTemp.setWhNumber(mapList.get(m).get("whNumber")==null?"":mapList.get(m).get("whNumber").toString());
    		entityTemp.setAreaName(mapList.get(m).get("areaName")==null?"":mapList.get(m).get("areaName").toString());
    		entityTemp.setStorageAreaCode(mapList.get(m).get("storageAreaCode")==null?"":mapList.get(m).get("storageAreaCode").toString());
    		
    		if(mapList.get(m).get("storageModel")!=null && !mapList.get(m).get("storageModel").toString().equals("")) {
				if("固定存储".equals(mapList.get(m).get("storageModel").toString())){
					entityTemp.setStorageModel("00");
				}else if("随机存储".equals(mapList.get(m).get("storageModel").toString())){
					entityTemp.setStorageModel("01");
				}
			}
    		if(mapList.get(m).get("putRule")!=null && !mapList.get(m).get("putRule").toString().equals("")) {
				if("空仓位".equals(mapList.get(m).get("putRule").toString())){
					entityTemp.setPutRule("00");
				}else if("添加至现有仓位".equals(mapList.get(m).get("putRule").toString())){
					entityTemp.setPutRule("01");
				}
			}
    		
    		if(mapList.get(m).get("mixFlag")!=null && !mapList.get(m).get("mixFlag").toString().equals("")) {
				if("否".equals(mapList.get(m).get("mixFlag").toString())){
					entityTemp.setMixFlag("0");
				}else if("是".equals(mapList.get(m).get("mixFlag").toString())){
					entityTemp.setMixFlag("X");
				}
			}
    		
    		if(mapList.get(m).get("storageCapacityFlag")!=null && !mapList.get(m).get("storageCapacityFlag").toString().equals("")) {
				if("否".equals(mapList.get(m).get("storageCapacityFlag").toString())){
					entityTemp.setStorageCapacityFlag("0");
				}else if("是".equals(mapList.get(m).get("storageCapacityFlag").toString())){
					entityTemp.setStorageCapacityFlag("X");
				}
			}
    		
    		if(mapList.get(m).get("autoPutawayFlag")!=null && !mapList.get(m).get("autoPutawayFlag").toString().equals("")) {
				if("否".equals(mapList.get(m).get("autoPutawayFlag").toString())){
					entityTemp.setAutoPutawayFlag("0");
				}else if("是".equals(mapList.get(m).get("autoPutawayFlag").toString())){
					entityTemp.setAutoPutawayFlag("X");
				}
			}
    		
    		if(mapList.get(m).get("autoReplFlag")!=null && !mapList.get(m).get("autoReplFlag").toString().equals("")) {
				if("否".equals(mapList.get(m).get("autoReplFlag").toString())){
					entityTemp.setAutoReplFlag("0");
				}else if("是".equals(mapList.get(m).get("autoReplFlag").toString())){
					entityTemp.setAutoReplFlag("X");
				}
			}
    		
    		if(mapList.get(m).get("checkWeightFlag")!=null && !mapList.get(m).get("checkWeightFlag").toString().equals("")) {
				if("否".equals(mapList.get(m).get("checkWeightFlag").toString())){
					entityTemp.setCheckWeightFlag("0");
				}else if("是".equals(mapList.get(m).get("checkWeightFlag").toString())){
					entityTemp.setCheckWeightFlag("X");
				}
			}
    		
    		entityTemp.setFloor(mapList.get(m).get("floor")==null?"":mapList.get(m).get("floor").toString());
    		entityTemp.setCoordinate(mapList.get(m).get("coordinate")==null?"":mapList.get(m).get("coordinate").toString());
    		if(mapList.get(m).get("status")!=null && !mapList.get(m).get("status").toString().equals("")) {
				if("启用".equals(mapList.get(m).get("status").toString())){
					entityTemp.setStatus("00");
				}else if("停用".equals(mapList.get(m).get("status").toString())){
					entityTemp.setStatus("01");
				}
			}
    		if(mapList.get(m).get("binSearchSequence")!=null && !mapList.get(m).get("binSearchSequence").toString().equals("")) {
				if("按储位编码排序".equals(mapList.get(m).get("binSearchSequence").toString())){
					entityTemp.setBinSearchSequence("00");
				}else if("按储位坐标排序".equals(mapList.get(m).get("binSearchSequence").toString())){
					entityTemp.setBinSearchSequence("01");
				}
			}
    		
    		entityTemp.setEditor(mapList.get(m).get("editor")==null?"":mapList.get(m).get("editor").toString());
    		entityTemp.setEditDate(mapList.get(m).get("editDate")==null?"":mapList.get(m).get("editDate").toString());
    		addList.add(entityTemp);
    	}
    	if(addList.size()>0) {
    		wmsCoreWhAreaService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    		wmsCoreWhAreaService.updateBatchById(updateList);
    	}
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<Map<String,Object>> entityList) throws IOException{
		String whNumber="";
		String storageAreaCode="";
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		if(!CollectionUtils.isEmpty(entityList)){
			for(Map<String,Object> map:entityList){
				
				if(map.get("whNumber")!=null && !map.get("whNumber").toString().equals("")) {
					if(!whNumber.equals(map.get("whNumber").toString())) {
						List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
								new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER", map.get("whNumber").toString()).eq("DEL","0"));
					    if(whList.size()==0) {
					    	msg="仓库代码未维护;";
					    }else {
					    	whNumber=map.get("whNumber").toString();
					    }
					}
				} else {
					msg="仓库代码不能为空;";
				}
				/*map.put("storageAreaCode", map.get("storageAreaCode"));
				if(map.get("storageAreaCode")!=null && !map.get("storageAreaCode").toString().equals("")) {
					if(!storageAreaCode.equals(map.get("storageAreaCode").toString())) {
						List<WmsCoreStorageTypeEntity> storageList=wmsCoreStorageTypeService.selectList(
								new EntityWrapper<WmsCoreStorageTypeEntity>().eq("STORAGE_TYPE_CODE", map.get("storageAreaCode").toString()).eq("DEL","0"));
					    if(storageList.size()==0) {
					    	msg="储存类型代码未维护;";
					    }else {
					    	storageAreaCode=map.get("storageAreaCode").toString();
					    }
					}
				} else {
					msg="储存类型代码不能为空;";
				}*/
				/*map.put("areaCode", map.get("areaCode"));
				if(map.get("areaCode")!=null && !map.get("areaCode").toString().equals("")) {
					if(matnrSb.indexOf(map.get("areaCode").toString().concat(";"))>=0) {
						msg+="存储区代码存在重复数据;";
					}else {
						matnrSb.append(map.get("areaCode").toString().concat(";"));
					}
				} else {
					msg+="存储区代码不能为空;";
				}*/
				map.put("areaName", map.get("areaName"));
				if(map.get("areaName")==null || (map.get("areaName")!=null && map.get("areaName").toString().equals(""))) {
					msg+="存储区名称不能为空;";
				}
				// 默认按储位编码排序
				map.put("binSearchSequence", "按储位编码排序");
				if(map.get("binSearchSequence")!=null || (map.get("binSearchSequence")!=null && map.get("binSearchSequence").toString().equals(""))) {
					if(map.get("binSearchSequence").toString().equals("01")) {
						map.put("binSearchSequence", "按储位坐标排序");
					}
				}
				map.put("msg", msg);
				msg="";
				
				
			}
			
		}
		return R.ok().put("data", entityList);
	}  
	// 查询All
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params){
        List<WmsCoreWhAreaEntity> list = 
        		wmsCoreWhAreaService.selectList(new EntityWrapper<WmsCoreWhAreaEntity>().
        		eq("WH_NUMBER",params.get("whNumber")).
    			eq("DEL","0"));
        return R.ok().put("list", list);
    }

	/**
	 *
	 */
	@RequestMapping("/queryAreaName")
	public R queryAreaName(@RequestParam Map<String, Object> params){
		List<WmsCoreWhAreaEntity> list =
				wmsCoreWhAreaService.selectList(new EntityWrapper<WmsCoreWhAreaEntity>().
						eq("WH_NUMBER",params.get("whNumber"))
						.eq("STORAGE_AREA_CODE",params.get("storageAreaCode")).
						eq("DEL","0"));
		return R.ok().put("list", list);
	}
}
