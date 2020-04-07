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
import com.byd.web.common.utils.TagUtils;
import com.byd.web.wms.config.entity.SysDictEntity;
import com.byd.web.wms.config.service.WmsCHandoverTypeRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 交接模式置表
 *
 * @author tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
@RestController
@RequestMapping("config/handoverType")
public class WmsCHandoverTypeController {
    @Autowired
    private WmsCHandoverTypeRemote wmsCHandoverTypeRemote;
    @Autowired
    private UserUtils userUtils;
    @Autowired
	private TagUtils tagUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCHandoverTypeRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCHandoverTypeRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("creator",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		params.put("del","0");
    	return wmsCHandoverTypeRemote.save(params);	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCHandoverTypeRemote.update(params);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	Map<String,Object> params=new HashMap<String,Object>();
    	Map<String,Object> user = userUtils.getUser();
    	params.put("id", id);
    	params.put("del", "X");
    	params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return wmsCHandoverTypeRemote.delById(params);
    }
    @RequestMapping("/import")
	public R upload(@RequestParam List<Map<String,Object>> entityList) throws IOException{
    	
		return wmsCHandoverTypeRemote.upload(entityList);
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
		return wmsCHandoverTypeRemote.previewExcel(entityList);
	} 
	
	@RequestMapping("/wmsCHandoverTypelist")
    public R wmsCHandoverTypelist(@RequestParam Map<String, Object> params){
		
		R resRet=wmsCHandoverTypeRemote.wmsCHandoverTypelist(params);
		
		String business_strName="";
		String businessStr=(String) resRet.get("business");
		if(!"".equals(businessStr)){//查询  没有配置的 业务类型的名称 并返回到前台
			String businessArray[]=businessStr.split(",");
			for(SysDictEntity dict : tagUtils.wmsDictList("BUSINESS_NAME")) {
				for(int m=0;m<businessArray.length;m++){
					if(!"".equals(businessArray[m])){
						if(dict.getCode().equals(businessArray[m])){
							business_strName=business_strName+"   "+dict.getValue();
						}
					}
				}
			}
		}
		resRet.put("business_strName", business_strName);
        return resRet;
    }
	/**
     * 复制保存
     * @param entitys
     * @return
     */
    @RequestMapping("/copy")
    public R copy(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	Gson gson=new Gson();
    	List<Map<String,Object>> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		list.forEach(k->{
			k=(Map<String,Object>)k;	
			k.put("CREATOR",user.get("USERNAME")+"："+user.get("FULL_NAME"));
	    	k.put("CREATEDATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		});
		return wmsCHandoverTypeRemote.saveCopyData(list);
    }		
}
