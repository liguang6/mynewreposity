package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.common.service.WmsSapRemote;
import com.byd.web.wms.config.entity.WmsSapPlant;
import com.byd.web.wms.config.service.WmsSapPlantRemote;

@RestController
@RequestMapping("/config/sapPlant")
public class WmsSapPlantController {
	
	@Autowired
	WmsSapPlantRemote wmsSapPlantRemote;
	
	@Autowired
	WmsSapRemote wmsSapRemote;
	
	@Autowired
	UserUtils userUtils;
	
	
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
		return wmsSapPlantRemote.list(params);	
	}
	
	@RequestMapping("/del")
	public R delete(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsSapPlant entity = new WmsSapPlant();
		entity.setId(id);
		entity.setDel("X");
		wmsSapPlantRemote.update(entity);
		return R.ok();
	}
	
	@RequestMapping("/add")
	public R add(@RequestBody WmsSapPlant entity){
		//validate
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		
		wmsSapPlantRemote.add(entity);
		return R.ok();
	}
	
	@RequestMapping("/update")
	public R update(@RequestBody WmsSapPlant entity){
		//validate
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		return wmsSapPlantRemote.update(entity);
	}
	
	@RequestMapping("/get")
	public R getWmsSapPlantById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		return wmsSapPlantRemote.getWmsSapPlantById(id);
	}
	
	@RequestMapping("/import")
	public R uploadWmsSapPlant(@RequestBody List<WmsSapPlant> entityList) throws IOException{
		return wmsSapPlantRemote.uploadWmsSapPlant(entityList);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody MultipartFile excel,@RequestParam Map<String, Object> params) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<WmsSapPlant> entityList = new ArrayList<WmsSapPlant>();
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				WmsSapPlant entity = new WmsSapPlant();
				entity.setWerks(row[0]);
				entity.setWerksName(row[1]);
				entity.setShortName(row[2]);
				entity.setBukrs(row[3]);
				entity.setBukrsName(row[4]);
				entity.setBukrsShortName(row[5]);
				entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
				entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(entity);
			}
		}	
		return wmsSapPlantRemote.previewExcel(entityList);
	}
	
	
	/**
	 * 导出excel文件.
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/exportExcel")
	public ResponseEntity<byte[]> exportExcel(@RequestBody WmsSapPlant entity) throws Exception{
		
		return wmsSapPlantRemote.exportExcel(entity);
	}
	
	
	@RequestMapping("/query")
	public R queryPlants(@RequestParam Map<String,Object> queryParams){
		return wmsSapPlantRemote.queryPlants(queryParams);
	}
}
