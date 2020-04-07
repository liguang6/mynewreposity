package com.byd.wms.business.modules.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.common.entity.WmsCoreMatBatchEntity;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 上午10:09:56 
 * 类说明 
 */
public interface WmsCoreMatBatchDao extends BaseMapper<WmsCoreMatBatchEntity> {
	List<Map<String,Object>> selectCoreMatBatchList(Map<String, Object> map);
	void insertCoreMatBatch(Map<String, Object> map);
	
	List<Map<String,Object>> selectBatchCodeList(Map<String, Object> map);
	
	/**
	 * 获取某一天最大流水批次
	 * @param map
	 * @return
	 */
	String getMaxBatch(Map<String, Object> map);
	void deleteBatch(@Param("batchList")List<Map<String, Object>> batchList);
}
