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
import com.byd.web.wms.config.entity.WmsCMatLtSampleEntity;
import com.byd.web.wms.config.service.WmsCMatLtSampleRemote;

/**
 * 物料物流参数配置表 自制产品入库参数
 *
 * @author cscc tangj
 * @email 
 * @date 2018-09-28 10:30:07
 */
@RestController
@RequestMapping("config/sample")
public class WmsCMatLtSampleController {
    @Autowired
    private WmsCMatLtSampleRemote wmsCMatLtSampleRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCMatLtSampleRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCMatLtSampleRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatLtSampleEntity wmsCMatLtSample){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatLtSample.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCMatLtSample.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	wmsCMatLtSample.setDel("0");
    	return wmsCMatLtSampleRemote.save(wmsCMatLtSample);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatLtSampleEntity wmsCMatLtSample){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatLtSample.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCMatLtSample.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));        
        return wmsCMatLtSampleRemote.update(wmsCMatLtSample);
    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//        wmsCMatLtSampleService.deleteBatchIds(Arrays.asList(ids));
//
//        return R.ok();
//    }
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	WmsCMatLtSampleEntity wmsCMatLtSample=new WmsCMatLtSampleEntity();
    	wmsCMatLtSample.setId(id);
    	wmsCMatLtSample.setDel("X");
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatLtSample.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCMatLtSample.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return wmsCMatLtSampleRemote.delById(wmsCMatLtSample);
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<Map<String,Object>> entityList) throws IOException{
		return wmsCMatLtSampleRemote.upload(entityList);
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
				map.put("fullBoxQty", row[3]);
				map.put("ltWare", row[4]);
				map.put("carType", row[5]);
				map.put("proStation", row[6]);
				map.put("disStation", row[7]);
				map.put("disAddrss", row[8]);
				map.put("mouldNo", row[9]);
				map.put("creator", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("createDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
			}
		}
		return wmsCMatLtSampleRemote.previewExcel(entityList); 
	}  
}
