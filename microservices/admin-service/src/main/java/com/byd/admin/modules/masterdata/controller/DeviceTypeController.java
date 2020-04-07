package com.byd.admin.modules.masterdata.controller;

import java.util.Arrays;
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

import com.byd.admin.modules.masterdata.entity.BusTypeEntity;
import com.byd.admin.modules.masterdata.entity.DeviceEntity;
import com.byd.admin.modules.masterdata.entity.DeviceTypeEntity;
import com.byd.admin.modules.masterdata.service.DeviceService;
import com.byd.admin.modules.masterdata.service.DeviceTypeService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月9日 上午10:46:21 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/deviceType")
public class DeviceTypeController {
	@Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = deviceTypeService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	DeviceTypeEntity deviceType = deviceTypeService.selectById(id);

        return R.ok().put("deviceType", deviceType);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DeviceTypeEntity deviceType){
    	try{
    	Map<String,Object> condMap=new HashMap<String,Object>();
    	condMap.put("DEVICE_TYPE_CODE", deviceType.getDeviceTypeCode());
    	List<DeviceTypeEntity> retList=deviceTypeService.selectByMap(condMap);
    	if(retList!=null&&retList.size()>0){
    		throw new RuntimeException("设备类型编码 "+deviceType.getDeviceTypeCode()+" 已经存在，不能再次添加！");
    	}
    			
    	ValidatorUtils.validateEntity(deviceType);
    	
    	Map<String,Object> currentUser = userUtils.getUser();
    	deviceType.setDel("0");
    	deviceType.setCreator(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	deviceType.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	deviceType.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	deviceType.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	deviceTypeService.insert(deviceType);
	    } catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}

        return R.ok();
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DeviceTypeEntity deviceType){
        ValidatorUtils.validateEntity(deviceType);
        Map<String,Object> currentUser = userUtils.getUser();
        deviceType.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
        deviceType.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        deviceTypeService.updateAllColumnById(deviceType);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	deviceTypeService.deleteById(id);

        return R.ok();
    }

}
