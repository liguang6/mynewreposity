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
import com.byd.web.common.utils.TagUtils;
import com.byd.web.sys.service.SysUserRemote;
import com.byd.web.wms.config.entity.SysDictEntity;
import com.byd.web.wms.config.service.WmsCHandoverRemote;

/**
 * 交接人员置表
 *
 * @author tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
@RestController
@RequestMapping("config/handover")
public class WmsCHandoverController {
    @Autowired
    private WmsCHandoverRemote wmsCHandoverRemote;
    @Autowired
	private SysUserRemote sysUserRemote;
    @Autowired
    private UserUtils userUtils;
    @Autowired
	private TagUtils tagUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCHandoverRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCHandoverRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> handover){
    	handover.put("staffnumber", handover.get("staffNumber"));
    	R r=sysUserRemote.checkCardid(handover);
    	if(r.get("code").toString().equals("0")) {
    		Map<String,Object> user = userUtils.getUser();
    		handover.put("creator",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    		handover.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		handover.put("del","0");
        	return wmsCHandoverRemote.save(handover);	
    	}else {
    		return R.error("一卡通系统未维护该工号！");
    	}
    	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCHandoverRemote.update(params);
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
		return wmsCHandoverRemote.delById(params);
    }
    
    @RequestMapping("/wmsCHandoverlist")
    public R wmsCHandoverlist(@RequestParam Map<String, Object> params){
    	
    	R resRet=wmsCHandoverRemote.wmsCHandoverlist(params);
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
   
}
