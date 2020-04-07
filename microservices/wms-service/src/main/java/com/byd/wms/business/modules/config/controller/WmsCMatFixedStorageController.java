package com.byd.wms.business.modules.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.common.annotation.PermissionDataFilter;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.config.entity.WmsCMatFixedStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatFixedStorageService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * @author ren.wei3
 * 物料固定储位配置
 */
@RestController
@RequestMapping("config/fixedStorage")
public class WmsCMatFixedStorageController {

	@Autowired
    private WmsCMatFixedStorageService wmsCMatFixedStorageService;
	@Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Autowired
    private WmsCoreWhBinService wmsCoreWhBinService;

	/**
     * 列表
     */
    @RequestMapping("/list")
    @PermissionDataFilter(menuKey = "FIXED_STORAGE")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatFixedStorageService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	WmsCMatFixedStorageEntity fixedStorage = wmsCMatFixedStorageService.selectById(id);
        return R.ok().put("fixedStorage", fixedStorage);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PermissionDataFilter(menuKey = "FIXED_OPERATION", menuType="2")
    public R save(@RequestBody Map<String, Object> params){
    	//验证数据权限
    	boolean errorflag = false;
    	//为区分业务数据与权限过滤KEY, 权限对象KEY加前缀 "auth_"
    	List<String> authfilter = (List<String>) params.get("auth_WH_NUMBER");
    	if (authfilter != null) {
    		for (String filter : authfilter) {
    			Pattern pattern = Pattern.compile(filter);
    			Matcher matcher = pattern.matcher(params.get("whNumber").toString());
    			if(matcher.find()){
    				errorflag = true;
    		    }
    		}
    	}
    	if (!errorflag && authfilter != null) {
    		return R.error("您无权操作"+params.get("whNumber").toString()+"的数据!");
//    		return R.error(LocaleLanguageFactory.getValue("NOT_AUTHORIZED_DATA", params.get("whNumber")));
    	}

    	WmsCMatFixedStorageEntity fixed = new WmsCMatFixedStorageEntity();
    	fixed = JSON.parseObject(JSON.toJSONString(params), WmsCMatFixedStorageEntity.class);

    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",fixed.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("WERKS_NOT_FOUND");
	    }
	    List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
				new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER",fixed.getWhNumber()).eq("DEL","0"));
	    if(whList.size()==0) {
	    	return R.error("WH_NOT_FOUND");
	    }

	    List<WmsCoreWhBinEntity> binList=wmsCoreWhBinService.selectList(
	    		 new EntityWrapper<WmsCoreWhBinEntity>()
	    		 .eq("WH_NUMBER", fixed.getWhNumber())
	    		 .eq("STORAGE_AREA_CODE", fixed.getStorageAreaCode())
	             .eq("BIN_CODE", fixed.getBinCode()).eq("DEL","0"));
	    if(binList.size() == 0) {
	    	return R.error("仓库储位不存在！");
	    }

	    List<WmsCMatFixedStorageEntity> list=wmsCMatFixedStorageService.selectList(
				new EntityWrapper<WmsCMatFixedStorageEntity>()
				.eq("WERKS",fixed.getWerks())
				.eq("WH_NUMBER",fixed.getWhNumber())
				.eq("MATNR",fixed.getMatnr())
				.eq("BIN_CODE",fixed.getBinCode())
	    		);
	    if(list.size()>0) {
	    	return R.error("DATA_EXISTS");
	    }

	    wmsCMatFixedStorageService.insert(fixed);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @PermissionDataFilter(menuKey = "FIXED_OPERATION", menuType="2")
    public R update(@RequestBody Map<String, Object> params){
    	//验证数据权限
    	boolean errorflag = false;
    	//为区分业务数据与权限过滤KEY, 权限对象KEY加前缀 "auth_"
    	List<String> authfilter = (List<String>) params.get("auth_WH_NUMBER");
    	if (authfilter != null) {
    		for (String filter : authfilter) {
    			Pattern pattern = Pattern.compile(filter);
    			Matcher matcher = pattern.matcher(params.get("whNumber").toString());
    			if(matcher.find()){
    				errorflag = true;
    		    }
    		}
    	}
    	if (!errorflag && authfilter != null) {
    		return R.error("您无权操作"+params.get("whNumber").toString()+"的数据!");
//    		return R.error(LocaleLanguageFactory.getValue("NOT_AUTHORIZED_DATA", params.get("whNumber")));
    	}
    	WmsCMatFixedStorageEntity fixed = new WmsCMatFixedStorageEntity();
    	fixed = JSON.parseObject(JSON.toJSONString(params), WmsCMatFixedStorageEntity.class);
    	wmsCMatFixedStorageService.updateById(fixed);
    	return R.ok();
    }

    /**
     * 删除BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam String ids){
    	String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			wmsCMatFixedStorageService.deleteById(Long.parseLong(id[i]));
		}
		return R.ok();
    }

    @RequestMapping("/preview")
	public R previewExcel(@RequestBody List<Map<String, Object>> params) throws IOException{
		String werks="";
		String msg="";
		StringBuffer matnrSb=new StringBuffer();
		for(Map<String,Object> map : params){
			if(map.get("whNumber")!=null && !map.get("whNumber").toString().equals("")) {
//				if(!werks.equals(map.get("whNumber").toString())) {
//					List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
//							new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER",map.get("whNumber").toString()).eq("DEL","0"));
//				    if(whList.size()==0) {
//				    	msg="仓库信息未维护！";
//				    }
//				}
			} else {
				msg="仓库不能为空;";
			}
			if(map.get("werks")!=null && !map.get("werks").toString().equals("")) {
				if(!werks.equals(map.get("werks").toString())) {
					List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
							new EntityWrapper<WmsSapPlant>().eq("WERKS", map.get("werks").toString()).eq("DEL","0"));
				    if(plantList.size()==0) {
				    	msg="工厂代码未维护;";
				    }else {
				    	werks=map.get("werks").toString();
				    }
				}
			} else {
				msg="工厂代码不能为空;";
			}
			if(map.get("matnr")!=null && !map.get("matnr").toString().equals("")) {
				/*if(matnrSb.indexOf(map.get("matnr").toString().concat(";"))>=0) {
					msg+="物料号存在重复数据;";
				}else {
					matnrSb.append(map.get("matnr").toString().concat(";"));*/
					List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
							new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",map.get("werks")).
							eq("MATNR",map.get("matnr")));
				    if(materialList.size()==0) {
				    	msg+="SAP物料信息不存在;";
				    }else{
				    	map.put("maktx", materialList.get(0).getMaktx());
					}

			} else {
				msg+="物料号不能为空;";
			}
			map.put("msg", msg);
			msg="";
		}
		return R.ok().put("data", params);
	}

    @RequestMapping("/import")
	public R upload(@RequestBody List<WmsCMatFixedStorageEntity> entityList) throws IOException{
    	List<WmsCMatFixedStorageEntity> addList=new ArrayList<WmsCMatFixedStorageEntity>();
    	List<WmsCMatFixedStorageEntity> updateList=new ArrayList<WmsCMatFixedStorageEntity>();

    	StringBuffer matnr=new StringBuffer();
    	for(WmsCMatFixedStorageEntity entity : entityList) {
    		// 封装成 【工厂代码-物料号,工厂代码-物料号】
    		matnr.append(entity.getWhNumber().concat("-").concat(entity.getWerks()).concat("-").concat(entity.getMatnr()).concat("-").concat(entity.getBinCode()).concat(","));
    	}
    	String [] param=matnr.toString().split(",");
    	List<Map<String,Object>> list=wmsCMatFixedStorageService.validate(Arrays.asList(param));
    	for(WmsCMatFixedStorageEntity entity : entityList) {
    		String concatStr=entity.getWhNumber()+"-"+entity.getWerks()+"-"+entity.getMatnr()+"-"+entity.getBinCode();
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
    	}
    	if(addList.size()>0) {
    		wmsCMatFixedStorageService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    		wmsCMatFixedStorageService.updateBatchById(updateList);
    	}
		return R.ok();
	}
}
