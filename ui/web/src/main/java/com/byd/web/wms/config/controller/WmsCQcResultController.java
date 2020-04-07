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
import com.byd.utils.validator.ValidatorUtils;
import com.byd.web.wms.config.entity.WmsCQcResultEntity;
import com.byd.web.wms.config.entity.WmsCoreWhBinEntity;
import com.byd.web.wms.config.service.WmsCQcResultRemote;

/**
 * 质检结果配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@RestController
@RequestMapping("config/wmscqcresult")
public class WmsCQcResultController {
    @Autowired
    private WmsCQcResultRemote wmsCQcResultRemote;
    @Autowired
    private UserUtils userUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsCQcResultRemote.list(params);
    }
    
    
    /**
     * 查询检验结果信息
     * @param params
     * @return
     */
    @RequestMapping("/list2")
    public R list2(@RequestParam Map<String, Object> params){
    	
    	return wmsCQcResultRemote.list2(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCQcResultRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcResultEntity wmsCQcResult){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCQcResult.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcResult.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	wmsCQcResult.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcResult.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCQcResultRemote.save(wmsCQcResult);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcResultEntity wmsCQcResult){
        ValidatorUtils.validateEntity(wmsCQcResult);
        Map<String,Object> user = userUtils.getUser();
        wmsCQcResult.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCQcResult.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));       
        return wmsCQcResultRemote.update(wmsCQcResult);//全部更新
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
    	return wmsCQcResultRemote.delete(ids);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	Map<String,Object> user = userUtils.getUser();
    	WmsCQcResultEntity entity = new WmsCQcResultEntity();
		entity.setId(id);
		entity.setDel("X");
		entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        return wmsCQcResultRemote.delById(entity);
    }
    /**
     * 导入预览
     * @param excel
     * @return
     * @throws IOException 
     */
    @RequestMapping("/preview")
    public R preview(MultipartFile excel) throws IOException{
    	List<String[]> sheet =  ExcelReader.readExcel(excel);
    	List<WmsCQcResultEntity> entitys = new ArrayList<WmsCQcResultEntity>();
    	for(String[] row:sheet){
    		//load
    		WmsCQcResultEntity entity = new WmsCQcResultEntity();
    		entity.setWerks(row[0]);
    		entity.setQcResultCode(row[1]);
    		entity.setQcStatus(row[2]);
    		entity.setWhFlag(row[3]);
    		entity.setReturnFlag(row[4]);
    		entity.setReviewFlag(row[5]);
    		entity.setGoodFlag(row[6]);
    		entitys.add(entity);
    	}
    	return wmsCQcResultRemote.previewExcel(entitys);
    }
    
    /**
     * 导入
     * @param entitys
     * @return
     */
    @RequestMapping("/import")
    public R importExcel(List<WmsCQcResultEntity> entitys){
    	//
    	return wmsCQcResultRemote.importExcel(entitys);
    }
    /**
     * 复制保存
     * @param entitys
     * @return
     */
    @RequestMapping("/batchSave")
    public R batchSave(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("USERNAME",user.get("USERNAME")+"："+user.get("FULL_NAME"));
		return wmsCQcResultRemote.batchSave(params);
    }
}
