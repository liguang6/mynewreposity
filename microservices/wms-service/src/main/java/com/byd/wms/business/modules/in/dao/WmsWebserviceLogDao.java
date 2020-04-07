package com.byd.wms.business.modules.in.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.in.entity.WmsWebServiceLogEntity;

import java.util.List;
import java.util.Map;

/**
 * WMSwebserviceLog日志
 * 
 * @author rain
 * @email 
 * @date 2019年11月27日13:33:27
 */
public interface WmsWebserviceLogDao extends BaseMapper<WmsWebServiceLogEntity> {


	/**
	 * 列表查询
	 * @param param
	 * @return
	 */
	int queryLogCount(Map<String,Object> param);
	public List<Map<String, Object>> queryLogInfos(Map<String, Object> params);

	/**
	 * 通过pk查询日志
	 * @param pkLog
	 * @return
	 */
	public WmsWebServiceLogEntity queryByPkLog(Long pkLog);


	/**
	 * 保存webserviceLog日志信息
	 * @param logList
	 */
	void insertLogInfos(List<Map<String, Object>> logList);
	public void saveLogInfos(Map<String, Object> map);//map里面有add_list=List<Map<String, Object>>
	
}
