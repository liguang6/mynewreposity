package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.masterdata.entity.DeviceEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午10:32:01 
 * 类说明 
 */
public interface DeviceDao extends BaseMapper<DeviceEntity>{
	// 通过设备类型编码或者名称查找设备类型列表
		public List<Map<String,Object>> getDeviceTypeList(@Param("code")String code);
		
		public List<Map<String,Object>> getDeviceList(Map<String,String> map);
		//设备类型
		public List<Map<String,Object>> getDeviceTypeListByCode(Map<String,String> map);
}
