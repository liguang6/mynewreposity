package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.HashMap;
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
import com.byd.web.wms.config.service.WmsCMatPackageRemote;

/**
 * 物料包装规格置表
 *
 * @author tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
@RestController
@RequestMapping("config/matPackage")
public class WmsCMatPackageController {
    @Autowired
    private WmsCMatPackageRemote wmsCMatPackageRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCMatPackageRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Map<String, Object> params){
    	Long id=Long.parseLong(params.get("headId").toString());
        return wmsCMatPackageRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> wmsCMatUsing){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCMatUsing.put("creator",user.get("USERNAME")+"："+user.get("FULL_NAME"));
		wmsCMatUsing.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmsCMatUsing.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
		wmsCMatUsing.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return wmsCMatPackageRemote.merge(wmsCMatUsing);	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCMatPackageRemote.update(params);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return wmsCMatPackageRemote.delById(params);
    }
   
}
