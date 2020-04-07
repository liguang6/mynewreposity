package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.byd.web.wms.config.entity.WmsCMatStorageEntity;
import com.byd.web.wms.config.service.WmsCMatStorageRemote;



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
    private WmsCMatStorageRemote wmsCMatStorageRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCMatStorageRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCMatStorageRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatStorageEntity wmsCMatStorage){
    	Map<String,Object> user = userUtils.getUser();
		wmsCMatStorage.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
		wmsCMatStorage.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmsCMatStorage.setDel("0");
    	return wmsCMatStorageRemote.save(wmsCMatStorage);	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatStorageEntity wmsCMatStorage){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatStorage.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCMatStorage.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCMatStorageRemote.update(wmsCMatStorage);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCMatStorageEntity params = new WmsCMatStorageEntity();
    	params.setId(id);
    	params.setDel("X");
		return wmsCMatStorageRemote.delById(params);
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<Map<String,Object>> entityList) throws IOException{
    	
		return wmsCMatStorageRemote.upload(entityList);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		Map<String,Object> user = userUtils.getUser();
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		int index=0;
		for(String[] row:sheet){
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("rowNo", ++index);
			map.put("werks", row[0]);
			
			map.put("whNumber", row[1]);
			
			map.put("matnr", row[2]);
			map.put("maktx", row[3]);
			
			map.put("inControlFlag", row[4]);
			
			map.put("outControlFlag", row[5]);
			
			map.put("length", row[6]);
			
			map.put("width", row[7]);
			map.put("height", row[8]);
			map.put("sizeUnit", row[9]);
			map.put("volum", row[10]);
			map.put("volumUnit", row[11]);
			map.put("weight", row[12]);
			map.put("weightUnit", row[13]);
			map.put("storageUnit", row[14]);
			map.put("qty", row[15]);
			map.put("stockL", row[16]);
			map.put("stockM", row[17]);
			map.put("mpqFlag", row[18]);
			map.put("externalBatchFlag", row[19]);
			map.put("correlationMaterial", row[20]);
			map.put("repulsiveMaterial", row[21]);
			
			map.put("del", "0");
			map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			entityList.add(map);
		}
		
		return wmsCMatStorageRemote.previewExcel(entityList);
	} 
    /**
     * 根据binCode带出其他信息
     */
    @RequestMapping("/queryBinCode")
    public R queryBinCode(@RequestParam Map<String, Object> params){
        return wmsCMatStorageRemote.queryBinCode(params);
    }
    /**
     * 根据areaCode带出其他信息
     */
    @RequestMapping("/queryAreaCode")
    public R queryAreaCode(@RequestParam Map<String, Object> params){
    	return wmsCMatStorageRemote.queryAreaCode(params);
    }
    
    @RequestMapping("/queryCtrFlag")
    public R queryCtrFlag(@RequestParam Map<String, Object> params){
        return wmsCMatStorageRemote.queryCtrFlag(params);
    }

}
