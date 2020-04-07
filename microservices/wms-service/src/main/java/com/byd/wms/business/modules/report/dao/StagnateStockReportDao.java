package com.byd.wms.business.modules.report.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 库存冻结记录
 * 
 * @author chen.yafei1
 * @email 
 * @date 2019-11-27
 */
public interface StagnateStockReportDao extends BaseMapper {
	//查询呆滞库存
	List<Map<String,Object>> getStagnateStockInfoList(Map<String,Object> param);
	//查询呆滞库存   查询呆滞库存信息记录条数
	int getStagnateStockInfoCount(Map<String,Object> param);
	
}
