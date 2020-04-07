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
import com.byd.utils.validator.ValidatorUtils;
import com.byd.web.wms.config.entity.WmsCQcMatEntity;
import com.byd.web.wms.config.service.WmsCQcMatRemote;

/**
 * 物料质检配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@RestController
@RequestMapping("config/wmscqcmat")
public class WmsCQcMatController {
    @Autowired
    private WmsCQcMatRemote wmsCQcMatRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsCQcMatRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCQcMatRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcMatEntity wmsCQcMat){
        wmsCQcMatRemote.save(wmsCQcMat);

        return R.ok();
    }
    
    /**
     * 保存或新增
     * @param wmsCQcMat
     * @return
     */
    @RequestMapping("/save_update")
    public R saveOrUpdate(@RequestBody WmsCQcMatEntity wmsCQcMat){
    	Map<String,Object> user = userUtils.getUser();
    	if(wmsCQcMat.getId()==null){//Id为空，属于新增操作
    		wmsCQcMat.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    		wmsCQcMat.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
    	}
    	wmsCQcMat.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcMat.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
    	wmsCQcMatRemote.saveOrUpdate(wmsCQcMat);
    	return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcMatEntity wmsCQcMat){
        ValidatorUtils.validateEntity(wmsCQcMat);
        Map<String,Object> user = userUtils.getUser();
        wmsCQcMat.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCQcMat.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCQcMatRemote.update(wmsCQcMat);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    public R delete(@RequestBody Long[] ids){
        wmsCQcMatRemote.delete(ids);

        return R.ok();
    }
    
    @RequestMapping("/delete")
    public R deleteOne(Long id){
    	wmsCQcMatRemote.delById(id);//逻辑删除
    	return R.ok();
    }
    
    /**
     * 导入预览
     * @param excel
     * @return
     * @throws IOException
     */
    @RequestMapping("/preview")
    public R preview(MultipartFile excel) throws IOException{
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
				map.put("lifnr", row[3]);
				map.put("liktx", row[4]);
				map.put("testFlag", row[5]);
				map.put("startDate", row[6]);
				map.put("endDate", row[7]);
				map.put("del", "0");
				map.put("creator", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("createDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
			}
		}
		
		return wmsCQcMatRemote.previewExcel(entityList);
    }
    
    //批量导入
  	@RequestMapping("/import")
  	public R importBatch(@RequestBody List<Map<String,Object>> entitys) {
        return wmsCQcMatRemote.upload(entitys);
  	}

}
