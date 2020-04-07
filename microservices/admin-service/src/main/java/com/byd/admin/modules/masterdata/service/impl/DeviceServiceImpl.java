package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.DeviceDao;
import com.byd.admin.modules.masterdata.entity.DeviceEntity;
import com.byd.admin.modules.masterdata.service.DeviceService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午10:17:27 
 * 类说明 
 */
@Service("deviceService")
public class DeviceServiceImpl extends ServiceImpl<DeviceDao, DeviceEntity> implements DeviceService {
	@Autowired
	private DeviceDao deviceDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = (String)params.get("WERKS");
		String devicecode = (String)params.get("DEVICE_CODE");
		String machinecode = (String)params.get("MACHINE_CODE");
		String status = (String)params.get("STATUS");
		Page<DeviceEntity> page = this.selectPage(
                new Query<DeviceEntity>(params).getPage(),
                new EntityWrapper<DeviceEntity>().like(StringUtils.isNotBlank(werks),"werks", werks).eq("del", "0")
                
                .addFilterIfNeed(!"".equals(devicecode), "(device_code like '%"+devicecode+"%' or device_name like '%"+devicecode+"%')")
                .like(StringUtils.isNotBlank(machinecode),"machine_code", machinecode)
                .eq(StringUtils.isNotBlank(status),"status", status)
               
        );

        return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getDeviceTypeList(String code) {
		// TODO Auto-generated method stub
		return deviceDao.getDeviceTypeList(code);
	}

	@Override
	public List<Map<String, Object>> getDeviceList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return deviceDao.getDeviceList(map);
	}

	@Override
	public List<Map<String, Object>> getDeviceTypeListByCode(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return deviceDao.getDeviceTypeListByCode(map);
	}

}
