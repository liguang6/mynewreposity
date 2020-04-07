package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.ExcelReader;
import com.byd.utils.ExcelWriter;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.HelloRemote;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

@RestController
@RequestMapping("/config/sapPlant")
public class WmsSapPlantController {
	
	@Autowired
	WmsSapPlantService wmsSapPlantService;
	
	@Autowired
	WmsSapRemote wmsSapRemote;
	@Autowired
	HelloRemote helloRemote;
	
	@RequestMapping("/hello")
	public Map<String, Object> hello(@RequestParam Map<String, Object> params){
		System.out.println("---->hello [getSapBapiGoodsmvtDetail] start::materialdocument = " + params.get("materialdocument") + ";matdocumentyear = " + params.get("matdocumentyear"));
		Map<String, Object> re = wmsSapRemote.getSapBapiGoodsmvtDetail(params.get("materialdocument").toString(),params.get("matdocumentyear").toString());
		//String re = helloRemote.hello(params);getSapBapiGoodsmvtCreateMB01
		
		/*Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("WERKS", "C161");		
		
		List<Map<String,String>> itemList =  new ArrayList<Map<String,String>>();
		Map<String,String> item = new HashMap<String,String>();		
		item.put("MATERIAL", "10028653-00");				//GOODSMVT_ITEM
		item.put("PLANT", "C161");
		item.put("STGE_LOC", "0030");
		item.put("GR_RCPT", "TEST");
		Map<String,String> item2 = new HashMap<String,String>();		
		item2.put("MATERIAL", "10028653-01");				//GOODSMVT_ITEM
		item2.put("PLANT", "C191");
		item2.put("STGE_LOC", "0031");
		item2.put("GR_RCPT", "TEST2");
		itemList.add(item);
		itemList.add(item2);
		paramMap.put("ITEMLIST", itemList);
		
		Map<String, Object> re = wmsSapRemote.getSapBapiGoodsmvtCreateMB01(paramMap);*/
		
		System.out.println("---->hello end");
		return re;
	}
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmsSapPlantService.queryPage(params);	
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/del")
	public R delete(@RequestBody WmsSapPlant entity){
		
		wmsSapPlantService.updateById(entity);
		return R.ok();
	}
	
	@RequestMapping("/add")
	public R add(@RequestBody WmsSapPlant entity){
		List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",entity.getWerks()).eq("DEL","0"));
	    if(plantList.size()>0) {
	    	return R.error(entity.getWerks()+"工厂信息已维护！");
	    }
		wmsSapPlantService.insert(entity);
		return R.ok();
	}
	
	@RequestMapping("/update")
	public R update(@RequestBody WmsSapPlant entity){
		
		wmsSapPlantService.updateById(entity);
		return R.ok();
	}
	
	@RequestMapping("/get")
	public R getWmsSapPlantById(@RequestParam Long id){
		
		WmsSapPlant data = wmsSapPlantService.selectById(id);
		return R.ok().put("data", data);
	}
	
	@RequestMapping("/import")
	public R uploadWmsSapPlant(@RequestBody List<WmsSapPlant> entityList) throws IOException{
		wmsSapPlantService.insertBatch(entityList);
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<WmsSapPlant> entityList)throws IOException{
		
		return R.ok().put("data", entityList);
	}
	
	/**
	 * 导出excel文件.
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/exportExcel")
	public ResponseEntity<byte[]> exportExcel(@RequestBody WmsSapPlant entity) throws Exception{
		List<WmsSapPlant> entitylist = wmsSapPlantService.selectList(new EntityWrapper<WmsSapPlant>().like("werks_name",entity.getWerksName()));
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("werks", "工厂代码");
		map.put("werksName", "工厂名称");
		map.put("shortName", "工厂简称");
		map.put("bukrs", "公司代码");
		map.put("bukrsName", "公司名称");
		map.put("bukrsShortName", "公司简称");
		map.put("editor", "编辑人");
		map.put("editDate", "编辑日期");
		return ExcelWriter.generateBytesResponse(entitylist,map);
	}
	
	
	@RequestMapping("/query")
	public R queryPlants(@RequestParam Map<String,Object> queryParams){
		List<WmsSapPlant> list = wmsSapPlantService.queryPlantListByMap(queryParams);
		return R.ok().put("list", list);
	}
}
