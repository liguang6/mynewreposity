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
import com.byd.web.wms.config.entity.WmsCMatDangerEntity;
import com.byd.web.wms.config.service.WmsCMatDangerRemote;

/**
 * 危化品物料配置表
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
@RestController
@RequestMapping("config/matdanger")
public class WmsCMatDangerController {
    @Autowired
    private WmsCMatDangerRemote wmsCMatDangerRemote;
    @Autowired
    private UserUtils userUtils;
 
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCMatDangerRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCMatDangerRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatDangerEntity entity){
    	Map<String,Object> user = userUtils.getUser();
		entity.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
		entity.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setDel("0");
    	return wmsCMatDangerRemote.save(entity);	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatDangerEntity entity){
    	Map<String,Object> user = userUtils.getUser();
		entity.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCMatDangerRemote.update(entity);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCMatDangerEntity params = new WmsCMatDangerEntity();
    	params.setId(id);
    	params.setDel("X");
		return wmsCMatDangerRemote.delById(params);
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<Map<String,Object>> entityList) throws IOException{
    	
		return wmsCMatDangerRemote.upload(entityList);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
    	Map<String,Object> user = userUtils.getUser();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		String werks="";
		String vendor = new String();
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("werks", row[0]);
				map.put("lifnr", row[1]);
				map.put("matnr", row[2]);
				map.put("maktx", row[3]);
				map.put("dangerFlag", row[4]);
				double goodDates=row[5]!=null ? Double.valueOf(row[5].toString()) : 0;
				
				map.put("goodDates", goodDates);
				double minGoodDates=row[6]!=null ? Double.valueOf(row[6].toString()) : 0;
				map.put("minGoodDates", minGoodDates);
				map.put("extendedEffectDate", row[7]);
				map.put("memo", row[8]);
				map.put("del", "0");
				map.put("creator", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("createDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("msg", msg);
				entityList.add(map);
				msg="";
			}
		}
		return wmsCMatDangerRemote.preview(entityList);
	}  
}
