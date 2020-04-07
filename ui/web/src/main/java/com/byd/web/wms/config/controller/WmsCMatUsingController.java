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
import com.byd.web.wms.config.entity.WmsCMatUsingEntity;
import com.byd.web.wms.config.service.WmsCMatUsingRemote;

/**
 * 物料上线配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2018-08-15 13:57:57
 */
@RestController
@RequestMapping("config/matusing")
public class WmsCMatUsingController {
    @Autowired
    private WmsCMatUsingRemote wmsCMatUsingRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCMatUsingRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCMatUsingRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatUsingEntity wmsCMatUsing){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatUsing.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
		wmsCMatUsing.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmsCMatUsing.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCMatUsing.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmsCMatUsing.setDel("0");
    	return wmsCMatUsingRemote.save(wmsCMatUsing);	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatUsingEntity wmsCMatUsing){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatUsing.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCMatUsing.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCMatUsingRemote.update(wmsCMatUsing);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	Map<String,Object> user = userUtils.getUser();
    	WmsCMatUsingEntity params = new WmsCMatUsingEntity();
    	params.setId(id);
    	params.setDel("X");
    	params.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return wmsCMatUsingRemote.delById(params);
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<Map<String,Object>> entityList) throws IOException{
    	
		return wmsCMatUsingRemote.upload(entityList);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		Map<String,Object> user = userUtils.getUser();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("werks", row[0]);
				map.put("matnr", row[1]);
				map.put("maktx", row[2]);			
				String useFlag=row[3]!=null ? (row[3].toString().equals("是") ? "是" : "否") : "否";
				map.put("useFlag", useFlag);
				map.put("startDate", row[4]);
				map.put("endDate", row[5]);
				map.put("memo", row[6]);
				map.put("creator", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("createDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
			}
		}
		return wmsCMatUsingRemote.previewExcel(entityList);
	} 
			
}
