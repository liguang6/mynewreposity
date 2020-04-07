package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.formbean.WmsCMatStorageFormbean;
import com.byd.wms.business.modules.config.service.WmsCControlFlagService;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCoreWhAreaService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;



/**
 * 物料存储配置表 仓库系统上线前配置
 *
 * @author tangj
 * @email 
 * @date 2018-08-10 16:09:55
 */
@RestController
@RequestMapping("config/matstorage")
public class WmsCMatStorageController {
    @Autowired
    private WmsCMatStorageService wmsCMatStorageService;
    @Autowired
    private WmsCoreWhBinService wmsCoreWhBinService;
    @Autowired
    private WmsCoreWhAreaService wmsCoreWhAreaService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
    @Autowired
    private WmsCControlFlagService wmsCControlFlagService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = wmsCMatStorageService.queryPage(params);
        PageUtils page = wmsCMatStorageService.queryPagenew(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCMatStorageEntity wmsCMatStorage = wmsCMatStorageService.selectById(id);
        
        WmsCMatStorageFormbean formbeanTemp=new WmsCMatStorageFormbean();
        formbeanTemp.setCorrelationMaterial(wmsCMatStorage.getCorrelationMaterial());
        formbeanTemp.setDel(wmsCMatStorage.getDel());
        formbeanTemp.setEditDate(wmsCMatStorage.getEditDate());
        formbeanTemp.setEditor(wmsCMatStorage.getEditor());
        formbeanTemp.setExternalBatchFlag(wmsCMatStorage.getExternalBatchFlag());
        formbeanTemp.setHeight(wmsCMatStorage.getHeight());
        formbeanTemp.setId(wmsCMatStorage.getId());
        formbeanTemp.setInControlFlag(wmsCMatStorage.getInControlFlag());
        formbeanTemp.setLength(wmsCMatStorage.getLength());
        formbeanTemp.setMatnr(wmsCMatStorage.getMatnr());
        formbeanTemp.setMpqFlag(wmsCMatStorage.getMpqFlag());
        formbeanTemp.setOutControlFlag(wmsCMatStorage.getOutControlFlag());
        formbeanTemp.setQty(wmsCMatStorage.getQty());
        formbeanTemp.setRepulsiveMaterial(wmsCMatStorage.getRepulsiveMaterial());
        formbeanTemp.setSizeUnit(wmsCMatStorage.getSizeUnit());
        formbeanTemp.setStockL(wmsCMatStorage.getStockL());
        formbeanTemp.setStockM(wmsCMatStorage.getStockM());
//        formbeanTemp.setStorageAreaCode(wmsCMatStorage.getStorageAreaCode());
//        formbeanTemp.setStorageModel(wmsCMatStorage.getStorageModel());
        formbeanTemp.setStorageUnit(wmsCMatStorage.getStorageUnit());
        formbeanTemp.setVolum(wmsCMatStorage.getVolum());
        formbeanTemp.setVolumUnit(wmsCMatStorage.getVolumUnit());
        formbeanTemp.setWeight(wmsCMatStorage.getWeight());
        formbeanTemp.setWeightUnit(wmsCMatStorage.getWeightUnit());
        formbeanTemp.setWerks(wmsCMatStorage.getWerks());
        formbeanTemp.setWhNumber(wmsCMatStorage.getWhNumber());
        formbeanTemp.setWidth(wmsCMatStorage.getWidth());
        
        Map<String,Object> sapMaterialTemp=new HashMap<String,Object>();
        sapMaterialTemp.put("WERKS", wmsCMatStorage.getWerks());
        sapMaterialTemp.put("MATNR", wmsCMatStorage.getMatnr());
        List<WmsSapMaterialEntity> retsapmateriallist=wmsSapMaterialService.selectByMap(sapMaterialTemp);
        if(retsapmateriallist!=null&&retsapmateriallist.size()>0){
        	formbeanTemp.setMaktx(retsapmateriallist.get(0).getMaktx());
        }

        return R.ok().put("wmsCMatStorage", formbeanTemp);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatStorageEntity wmsCMatStorage){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",wmsCMatStorage.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
	    List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
				new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER",wmsCMatStorage.getWhNumber()).eq("DEL","0"));
	    if(whList.size()==0) {
	    	return R.error("仓库信息未维护！");
	    }
    	List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS", wmsCMatStorage.getWerks()).
                eq("MATNR", wmsCMatStorage.getMatnr()));
    	if(materialList.size()==0) {
    		return R.error("SAP物料未维护!");
    	}
    	List<WmsCoreWhBinEntity> binList=wmsCoreWhBinService.selectList(new EntityWrapper<WmsCoreWhBinEntity>().eq("WH_NUMBER", wmsCMatStorage.getWhNumber())
                .eq("DEL", "0"));
    	if(binList.size()==0) {
    		return R.error("仓库储位未维护!");
    	}
    	List<WmsCMatStorageEntity> list= wmsCMatStorageService.selectList(
                new EntityWrapper<WmsCMatStorageEntity>().eq("WERKS", wmsCMatStorage.getWerks()).
//                eq("BIN_CODE", wmsCMatStorage.getBinCode()).
//                eq("STORAGE_TYPE", wmsCMatStorage.getStorageType()).
//                eq("AREA_CODE", wmsCMatStorage.getAreaCode()).
//                eq("BIN_ROW", wmsCMatStorage.getBinRow()).
//                eq("BIN_COlUMN", wmsCMatStorage.getBinColumn()).
//                eq("BIN_FLOOR", wmsCMatStorage.getBinFloor()).
                eq("WH_NUMBER", wmsCMatStorage.getWhNumber()).
                eq("MATNR", wmsCMatStorage.getMatnr()).eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("物料存储配置已维护!");
    	}
    	wmsCMatStorageService.insert(wmsCMatStorage);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatStorageEntity wmsCMatStorage){
    	List<WmsCoreWhBinEntity> binList=wmsCoreWhBinService.selectList(new EntityWrapper<WmsCoreWhBinEntity>().eq("WH_NUMBER", wmsCMatStorage.getWhNumber()).
                eq("DEL", "0"));
    	if(binList.size()==0) {
    		return R.error("仓库储位未维护!");
    	}
        wmsCMatStorageService.updateById(wmsCMatStorage);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatStorageEntity wmsCMatStorage){
       
		wmsCMatStorageService.updateById(wmsCMatStorage);
		return R.ok();
    }
    @RequestMapping("/upload")
	public R upload(@RequestBody List<WmsCMatStorageEntity> entityList) throws IOException{
    	List<WmsCMatStorageEntity> addList=new ArrayList<WmsCMatStorageEntity>();
    	List<WmsCMatStorageEntity> updateList=new ArrayList<WmsCMatStorageEntity>();

    	StringBuffer matnr=new StringBuffer();
    	for(WmsCMatStorageEntity entity : entityList) {
    		// 封装成 【工厂代码-仓库号-物料号,工厂代码-仓库号-物料号】
    		matnr.append(entity.getWerks().concat("-").concat(entity.getWhNumber()).concat("-").concat(entity.getMatnr()).concat(","));
    	}
    	String [] param=matnr.toString().split(",");
    	List<Map<String,Object>> list=wmsCMatStorageService.validate(Arrays.asList(param));
    	for(WmsCMatStorageEntity entity : entityList) {
    		String concatStr=entity.getWerks()+"-"+entity.getWhNumber()+"-"+entity.getMatnr();
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
    		wmsCMatStorageService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    		wmsCMatStorageService.updateBatchById(updateList);
    	}
		return R.ok();
	}
	
	@RequestMapping("/previewExcel")
	public R previewExcel(@RequestBody List<Map<String,Object>> params) throws IOException{
		String werks="";
		String whNumber="";
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		int index=0;
		if(!CollectionUtils.isEmpty(params)){
//			List<WmsCoreWhAreaEntity> areaList=
//					wmsCoreWhAreaService.selectList(new EntityWrapper<WmsCoreWhAreaEntity>().eq("DEL","0"));
			for(Map<String,Object> map:params){
				map.put("rowNo", ++index);
				map.put("werks", map.get("werks"));
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
				map.put("whNumber", map.get("whNumber"));
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
				map.put("matnr", map.get("matnr"));
				map.put("maktx", map.get("maktx"));
				if(map.get("matnr")!=null && !map.get("matnr").toString().equals("")) {
					if(matnrSb.indexOf(map.get("matnr").toString().concat(";"))>=0) {
						msg+="物料号存在重复数据;";
					}else {
						matnrSb.append(map.get("matnr").toString().concat(";"));
						List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
								new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",map.get("werks")).
								eq("MATNR",map.get("matnr")));
					    if(materialList.size()==0) {
					    	msg+="SAP物料信息不存在;";
					    }else{
					    	map.put("maktx", materialList.get(0).getMaktx());
						}
					}
				} else {
					msg+="物料号不能为空;";
				}
				/*map.put("binCode", map.get("binCode"));
				if(map.get("binCode")!=null && !map.get("binCode").toString().equals("")) {
					
						List<WmsCoreWhBinEntity> binList=wmsCoreWhBinService.selectList(
								new EntityWrapper<WmsCoreWhBinEntity>().eq("WH_NUMBER",map.get("whNumber")).
								eq("BIN_CODE",map.get("binCode")).eq("DEL", "0"));
					    if(binList.size()==0) {
					    	msg+="储位代码未维护;";
					    }else{
					    	map.put("binName", binList.get(0).getBinName());*/
					    	/*for(WmsCoreWhAreaEntity area : areaList) {
					    		if(map.get("whNumber")!=null && !areaCode.equals("")) {
					    			if(area.getWhNumber().equals(map.get("whNumber").toString()) &&
					    					areaCode.equals(area.getAreaCode())) {
					    				map.put("areaName", area.getAreaName());
					    			}
					    		}
					    	}*/
					    	/*map.put("binRow", binList.get(0).getBinRow());
					    	map.put("binColumn", binList.get(0).getBinColumn());
					    	map.put("binFloor", binList.get(0).getBinFloor());
						}		
				} else {
					msg+="储位代码不能为空;";
				}*/
				map.put("storageUnit", map.get("storageUnit"));
				if(map.get("storageUnit")==null || (map.get("storageUnit")!=null && map.get("storageUnit").toString().equals(""))) {
					msg+="存储最小包装单位不能为空;";
				}
				map.put("qty", map.get("qty"));
				if(map.get("qty")==null || (map.get("qty")!=null && map.get("qty").toString().equals(""))) {
					msg+="数量不能为空;";
				}
				map.put("stockL", map.get("stockL"));
				if(map.get("stockL")==null || (map.get("stockL")!=null && map.get("stockL").toString().equals(""))) {
					msg+="最大库存不能为空;";
				}
				map.put("stockM", map.get("stockM"));
				if(map.get("stockM")==null || (map.get("stockM")!=null && map.get("stockM").toString().equals(""))) {
					msg+="最小库存不能为空;";
				}
				map.put("del", "0");
				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", params);
	}  
    /**
     * 根据binCode带出其他信息
     */
    @RequestMapping("/queryBinCode")
    public R queryBinCode(@RequestParam Map<String, Object> params){
        List<WmsCoreWhBinEntity> list = wmsCoreWhBinService.selectList(new EntityWrapper<WmsCoreWhBinEntity>().
        		eq("WH_NUMBER",params.get("whNumber")).eq("BIN_CODE",params.get("binCode")).eq("DEL","0"));
        if(list.size()==0) {
        	return R.error("仓库储位未维护");
        }else {
        	return R.ok().put("bin", list.get(0));
        }
    }
    /**
     * 根据areaCode带出其他信息
     */
    @RequestMapping("/queryAreaCode")
    public R queryAreaCode(@RequestParam Map<String, Object> params){
        List<WmsCoreWhAreaEntity> list = wmsCoreWhAreaService.selectList(new EntityWrapper<WmsCoreWhAreaEntity>().
        		eq("WH_NUMBER",params.get("whNumber")).eq("AREA_CODE",params.get("areaCode")).eq("DEL","0"));
        if(list.size()==0) {
        	return R.error("存储区配置未维护");
        }else {
        	
        	return R.ok().put("area", list.get(0));
        }
    }
    
    @RequestMapping("/queryCtrFlag")
    public R queryCtrFlag(@RequestParam Map<String, Object> params){
    	//入库
        List<WmsCControlFlagEntity> inList = wmsCControlFlagService.selectList(new EntityWrapper<WmsCControlFlagEntity>().
        		eq("WH_NUMBER",params.get("whNumber")).eq("CONTROL_FLAG_TYPE","00").eq("DEL","0"));
        
        //出库
        List<WmsCControlFlagEntity> outList = wmsCControlFlagService.selectList(new EntityWrapper<WmsCControlFlagEntity>().
        		eq("WH_NUMBER",params.get("whNumber")).eq("CONTROL_FLAG_TYPE","01").eq("DEL","0"));

        	return R.ok().put("inList", inList).put("outList", outList);
        }
}
