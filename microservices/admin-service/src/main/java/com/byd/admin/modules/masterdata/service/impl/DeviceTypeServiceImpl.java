package com.byd.admin.modules.masterdata.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.DeviceDao;
import com.byd.admin.modules.masterdata.dao.DeviceTypeDao;
import com.byd.admin.modules.masterdata.entity.DeviceEntity;
import com.byd.admin.modules.masterdata.entity.DeviceTypeEntity;
import com.byd.admin.modules.masterdata.service.DeviceService;
import com.byd.admin.modules.masterdata.service.DeviceTypeService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月9日 上午10:17:27 
 * 类说明 
 */
@Service("deviceTypeService")
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeDao, DeviceTypeEntity> implements DeviceTypeService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String deviceTypeCode = (String)params.get("deviceTypeCode");
		String deviceTypeName = (String)params.get("deviceTypeName");
		Page<DeviceTypeEntity> page = this.selectPage(
                new Query<DeviceTypeEntity>(params).getPage(),
                new EntityWrapper<DeviceTypeEntity>().like(StringUtils.isNotBlank(deviceTypeCode),"device_type_code", deviceTypeCode)
               .like(StringUtils.isNotBlank(deviceTypeName),"device_type_name", deviceTypeName)
        );

        return new PageUtils(page);
	}

}
