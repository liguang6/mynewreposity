package com.byd.admin.modules.masterdata.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.DeviceTypeEntity;
import com.byd.utils.PageUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午10:17:04 
 * 类说明 
 */
public interface DeviceTypeService extends IService<DeviceTypeEntity>{
	PageUtils queryPage(Map<String, Object> params);
}
