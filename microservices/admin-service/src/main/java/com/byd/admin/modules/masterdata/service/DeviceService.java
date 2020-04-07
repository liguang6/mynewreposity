package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.DeviceEntity;
import com.byd.utils.PageUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午10:17:04 
 * 类说明 
 */
public interface DeviceService extends IService<DeviceEntity>{
	PageUtils queryPage(Map<String, Object> params);
	public List<Map<String, Object>> getDeviceTypeList(String code);
	public List<Map<String,Object>> getDeviceList(Map<String,String> map);
	public List<Map<String,Object>> getDeviceTypeListByCode(Map<String,String> map);
}
