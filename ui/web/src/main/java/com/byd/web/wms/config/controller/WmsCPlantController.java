package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.byd.web.wms.config.entity.WmsCWhEntity;
import com.byd.web.wms.config.service.WmsCPlantRemote;

@RestController
@RequestMapping("/config/CPlant")
public class WmsCPlantController {
	@Autowired
	WmsCPlantRemote wmscPlantRemote;
	@Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmscPlantRemote.list(params);	
	}
	@RequestMapping("/get")
	public R getWmsCPlantById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		return wmscPlantRemote.info(id);
	}
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmscPlantRemote.info(id);
    }
	@RequestMapping("/update")
	public R update(@RequestBody WmsCWhEntity entity){
		//validate
		entity.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		entity.setEditorDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		wmscPlantRemote.update(entity);
		return R.ok();
	}
	@RequestMapping("/del")
	public R delete(Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCWhEntity entity = new WmsCWhEntity();
		entity.setId(id);
		entity.setDelFlag("X");
		wmscPlantRemote.update(entity);
		return R.ok();
	}
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		
		return wmscPlantRemote.deletes(ids);
	}
	@RequestMapping("/save")
	public R add(@RequestBody WmsCWhEntity entity){
		//validate
		entity.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		entity.setEditorDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setDelFlag("0");
		wmscPlantRemote.save(entity);
		return R.ok();
	}
	@RequestMapping("/listEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		return wmscPlantRemote.queryEntity(params);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<WmsCWhEntity> entityList = new ArrayList<WmsCWhEntity>();
		int index=0;
		if(sheet != null && sheet.size() > 0){
			
			for(String[] row:sheet){
				String msg="";
				WmsCWhEntity entity = new WmsCWhEntity();
				if(0<row.length){
				   entity.setWerks(row[0]);
				}
				if(1<row.length){
				   entity.setWerksName(row[1]);
				}
				if(2<row.length){
					entity.setWhNumber(row[2]);
				}
				
				if(3<row.length){
				   entity.setVendorFlag(row[3]);
				}
				if(4<row.length){
				   entity.setIgFlag(row[4]);
				}
				if(5<row.length){
				   entity.setWmsFlag(row[5]);
				}
				if(6<row.length){
				   entity.setHxFlag(row[6]);
				}
				if(7<row.length){
				   entity.setPackageFlag(row[7]);
				}
				
				if(8<row.length){
				   entity.setBarcodeFlag(row[8]);
				}
				if(9<row.length){
				   entity.setResbdFlag(row[9]);
				}
				if(10<row.length){
				   entity.setCmmsFlag(row[10]);
				}
				
				entity.setMsg(msg);
				entity.setRowNo(String.valueOf(++index));
				entityList.add(entity);
				
			}
		}
		return wmscPlantRemote.previewExcel(entityList);
	}
	@RequestMapping("/import")
	public R upload(@RequestBody List<WmsCWhEntity> entityList) throws IOException{
		 for(int i=0;i<entityList.size();i++){
			 entityList.get(i).setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
			 entityList.get(i).setEditorDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		 }
		 
		 return wmscPlantRemote.upload(entityList);
	 }
	 
	@RequestMapping("/listPlantName")
	public R queryPlantName(@RequestParam Map<String, Object> params){
		return wmscPlantRemote.queryPlantName(params);
	}
}
