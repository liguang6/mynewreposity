package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.byd.web.wms.common.service.WmsSapRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCVendor;
import com.byd.web.wms.config.service.WmsCVendorRemote;

@RestController
@RequestMapping("/config/CVendor")
public class WmsCVendorController {
	@Autowired
	WmsCVendorRemote wmsCVendorRemote;
	@Autowired
	UserUtils userUtils;
	@Autowired
	WmsSapRemote wmsSapRemote;

	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmsCVendorRemote.list(params);	
	}
	
	@RequestMapping("/save")
	public R add(@RequestBody WmsCVendor entity){
		//validate
		entity.setDelFlag("0");
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return wmsCVendorRemote.add(entity);
	}
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCVendorRemote.info(id);
    }
	@RequestMapping("/update")
	public R update(@RequestBody WmsCVendor entity){
		//validate
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		return wmsCVendorRemote.update(entity);
	}
	@RequestMapping("/del")
	public R delete(Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCVendor entity = new WmsCVendor();
		entity.setId(id);
		entity.setDelFlag("X");
		return wmsCVendorRemote.update(entity);
	}
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		return wmsCVendorRemote.deletes(ids);
	}
	@RequestMapping("/listEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		
		return wmsCVendorRemote.queryEntity(params);
		
	}
	
	@RequestMapping("/listPlantName")
	public R queryPlantName(@RequestParam Map<String, Object> params){
		
		return wmsCVendorRemote.queryPlantName(params);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<WmsCVendor> entityList = new ArrayList<WmsCVendor>();
		
		int index=0;
		if(sheet != null && sheet.size() > 0){
			
			for(String[] row:sheet){
				WmsCVendor entity = new WmsCVendor();
				if(0<row.length){
				   entity.setWerks(row[0]);
				}
				/*if(1<row.length){
				entity.setWerksName(row[1]);
				if("".equals(row[1])||row[1]==null){
					msg+="工厂名称不能为空";
				}
				}*/
				if(2<row.length){
				   entity.setLifnr(row[2]);
				}
				if(3<row.length){
				   entity.setName1(row[3]);
				}
				if(4<row.length){
				   entity.setShortName(row[4]);
				}
				if(5<row.length){
				   entity.setIsScm(row[5]);
				}
				if(6<row.length){
				   entity.setVendorManager(row[6]);
				}
				
				entity.setRowNo(String.valueOf(++index));
				
				entityList.add(entity);
				
			}
		}
		return wmsCVendorRemote.previewExcel(entityList);
	}
	
	@RequestMapping("/import")
	public R upload(@RequestBody List<WmsCVendor> entityList) throws IOException{
		 for(int i=0;i<entityList.size();i++){
			 WmsCVendor wmscVendor=entityList.get(i);
			 wmscVendor.setDelFlag("0");
			 wmscVendor.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
			 wmscVendor.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		 }
		 
		 return wmsCVendorRemote.upload(entityList);
	 }
	
	@RequestMapping("/listVendortName")
	public R queryVendorName(@RequestParam Map<String, Object> params){
		
		return wmsCVendorRemote.queryVendorName(params);
	}

	/**
	 * 手动同步SAP供应商数据
	 */
	@RequestMapping("/syncSAPVendor")
	public R syncSAPVendor(@RequestParam Map<String, Object> params){

		Map<String, Object> result = wmsSapRemote.getSapBapiVendorInfoSync(params);
		return R.ok(result.get("MESSAGE").toString());
	}
	/**
	 * 根据供应商代码或者供应商名称获取已经从SAP同步过来的供应商信息
	 */
	@RequestMapping("/listSAPSAPVendor")
	public R listSAPSAPVendor(@RequestParam Map<String, Object> params){

		return wmsCVendorRemote.querySAPVendorName(params);

	}
}
