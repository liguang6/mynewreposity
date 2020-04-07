package com.byd.web.qms.processQuality.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.qms.processQuality.service.QmsCheckTemplateRemote;
import com.byd.web.wms.kn.service.WmsKnStockModifyRemote;
/**
 * 品质检验模板 以EXCEL导入方式操作
 * @author cscc tangj
 * @email 
 * @date 2019-07-24 09:12:08
 */
@RestController
@RequestMapping("qms/checkTemplate")
public class QmsCheckTemplateController {
    @Autowired
    private QmsCheckTemplateRemote qmsCheckTemplateRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("user", user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsCheckTemplateRemote.queryPage(params);
    }
    /**
     * 明细列表
     */
    @RequestMapping("/queryDetail")
    public R queryDetail(@RequestParam Map<String, Object> params){
    	return qmsCheckTemplateRemote.queryDetail(params);
    }
	// Excel导入预览
	@RequestMapping("/previewExcel")
	public R previewExcel(MultipartFile excel,@RequestParam Map<String,Object> param){
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		List<String[]> sheet =  null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			//返回空List （初始化时，没选择导入文件，也显示表头标题）
			return R.ok().put("data", entityList);	
		}	
		int index=0;
		int rowNo=1;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("tempItemNo", rowNo+"");
				map.put("processName", row[index]);
				map.put("testItem", row[++index]);
				map.put("testStandard", row[++index]);
				map.put("testGroup", row[++index]);
				map.put("numberFlag", row[++index]);
				map.put("onePassedFlag", row[++index]);
				map.put("photoFlag", row[++index]);
				map.put("patrolFlag", row[++index]);
				entityList.add(map);
				rowNo++;
				index=0;
			}
		}
		param.put("entityList", entityList);
		return qmsCheckTemplateRemote.previewExcel(param);
	}
	
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	try {
    		Map<String,Object> user = userUtils.getUser();
    		params.put("user", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			R r=qmsCheckTemplateRemote.save(params);
	    	return r;
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 保存or更新
     */
    @RequestMapping("/saveOrUpdate")
    public R saveOrUpdate(@RequestParam Map<String, Object> params){
    	try {
    		Map<String,Object> user = userUtils.getUser();
    		params.put("user", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			R r=qmsCheckTemplateRemote.saveOrUpdate(params);
	    	return r;
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 
     */
    @RequestMapping("/delete/{tempNo}")
    public R delete(@PathVariable("tempNo") String tempNo){
    	return qmsCheckTemplateRemote.delete(tempNo);
    }
    
    /**
     * 
     */
    @RequestMapping("/deleteItem/{id}")
    public R deleteItem(@PathVariable("id") Long id){
    	return qmsCheckTemplateRemote.deleteItem(id);
    }
    // 处理空值
    public boolean isNotNull(Object str) {
    	boolean result=false;
    	if(str==null) {
    		return result;
    	}
    	if(str.toString().trim().equals("")) {
    		return result;
    	}
    	return true;
    }
}