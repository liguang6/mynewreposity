package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCPlantBusinessEntity;
import com.byd.web.wms.config.service.WmsCPlantBusinessRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 工厂业务类型配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2018-09-29 14:57:55
 */
@RestController
@RequestMapping("config/plantbusiness")
public class WmsCPlantBusinessController {
    @Autowired
    private WmsCPlantBusinessRemote wmsCPlantBusinessRemote;
    @Autowired
    private UserUtils userUtils;
   
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsCPlantBusinessRemote.list(params);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCPlantBusinessRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCPlantBusinessEntity wmsCPlantBusiness){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCPlantBusiness.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCPlantBusiness.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return wmsCPlantBusinessRemote.save(wmsCPlantBusiness);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCPlantBusinessEntity wmsCPlantBusiness){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCPlantBusiness.setUpdater(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCPlantBusiness.setUpdatDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCPlantBusinessRemote.update(wmsCPlantBusiness);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	WmsCPlantBusinessEntity entity=new WmsCPlantBusinessEntity();
    	entity.setId(id);
    	return wmsCPlantBusinessRemote.delById(entity);
    }
 
	@RequestMapping("/getWmsBusinessCodeList")
	public R getWmsBusinessCodeList(@RequestParam Map<String, Object> params) {
		return wmsCPlantBusinessRemote.getWmsBusinessCodeList(params);
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
		return wmsCPlantBusinessRemote.saveCopyData(list);
    }
}
