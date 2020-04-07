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
import org.springframework.web.bind.annotation.ResponseBody;
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
 * @version 创建时间：2019年9月7日 上午10:46:21 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/device")
public class DeviceController {
	@Autowired
    private DeviceService deviceService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private DeviceTypeService deviceTypeService;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = deviceService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	DeviceEntity device = deviceService.selectById(id);

        return R.ok().put("device", device);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DeviceEntity device){
    	try{
    	//验证设备类型
    	Map<String,Object> condMap=new HashMap<String,Object>();
    	condMap.put("DEVICE_TYPE_CODE", device.getDeviceCode());
    	List<DeviceTypeEntity> retList=deviceTypeService.selectByMap(condMap);
    	if(retList.size()==0){
    		throw new RuntimeException("设备类型编码 "+device.getDeviceCode()+" 不存在，不能添加！");
    	}
    	
    	
    	ValidatorUtils.validateEntity(device);
    	
    	Map<String,Object> currentUser = userUtils.getUser();
    	
    	device.setCreator(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	device.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	device.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	device.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	deviceService.insert(device);
    	
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
    public R update(@RequestBody DeviceEntity device){
    	try{
        	//验证设备类型
        	Map<String,Object> condMap=new HashMap<String,Object>();
        	condMap.put("DEVICE_TYPE_CODE", device.getDeviceCode());
        	List<DeviceTypeEntity> retList=deviceTypeService.selectByMap(condMap);
        	if(retList.size()==0){
        		throw new RuntimeException("设备类型编码 "+device.getDeviceCode()+" 不存在，不能保存！");
        	}
        
        ValidatorUtils.validateEntity(device);
        Map<String,Object> currentUser = userUtils.getUser();
        device.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
        device.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        deviceService.updateAllColumnById(device);//全部更新
        
    	} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	DeviceEntity deviceEntity=new DeviceEntity();
    	deviceEntity.setId(id);
    	deviceEntity.setDel("1");
    	deviceService.updateById(deviceEntity);

        return R.ok();
    }
    
    @RequestMapping("/getDeviceTypeList")
    public R getDeviceTypeList(@RequestParam(value = "code") String code){
        List<Map<String,Object>> list = deviceService.getDeviceTypeList(code);
        return R.ok().put("list", list);
    }
    
    /**
     * 根据传入的 工厂_机台编号查询，返回出新的工厂_机台编号_流水
     * @param map
     * @return
     */
    @RequestMapping("/getMaxMachineCode")
    @ResponseBody
    public R getMaxMachineCode(@RequestParam Map<String,String> map){
    	String machine_code="";
    	List<Map<String, Object>> deviceList = deviceService.getDeviceList(map);
    	//生成机台编码 工厂_设备类型编码_三位流水号
		//1 判断 工厂_设备类型编码在表中的 machine_code是否存在，
		//2 不存在，则返回工厂_设备类型编码_000
		//3 存在，则取出三位流水码，取最大的值加1，返回
    	if(deviceList!=null&&deviceList.size()>0){//存在
    		int tempRadom=0;//保存最大的流水
    		for(Map<String, Object> tempMap:deviceList){
    			String[] machine_code_arry=tempMap.get("MACHINE_CODE").toString().split("_");
    			String radomStr=machine_code_arry[2];//流水号
    			int radomInt=Integer.parseInt(radomStr);//流水号 整数
    			if(radomInt>tempRadom){
    				tempRadom=radomInt;
    			}
    		}
    		tempRadom=tempRadom+1;//在最大的流水上加 1
    		String maxRadom=String.format("%03d",tempRadom);
    		machine_code=map.get("WERKS_MACHINE").toString().concat("_").concat(maxRadom);
    	}else{//不存在
    		machine_code=map.get("WERKS_MACHINE").toString().concat("_").concat("001");
    	}
        return R.ok().put("result", machine_code);
    }

    
    @RequestMapping("/getDeviceTypeByCode")
    @ResponseBody
    public R getDeviceTypeByCode(@RequestParam Map<String,String> map){
    	List<Map<String, Object>> deviceTypeList=deviceService.getDeviceTypeListByCode(map);
    	return R.ok().put("data", deviceTypeList);
    }
    
    
    @RequestMapping("/getDeviceList")
    @ResponseBody
    public R getDeviceList(@RequestParam Map<String,String> map){
    	List<Map<String, Object>> deviceList=deviceService.getDeviceList(map);
    	return R.ok().put("data", deviceList);
    }
}
