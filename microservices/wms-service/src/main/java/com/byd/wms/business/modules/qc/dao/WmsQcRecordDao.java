package com.byd.wms.business.modules.qc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;

/**
 * 检验记录
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcRecordDao extends BaseMapper<WmsQcRecordEntity> {
	
	List<Map<String,Object>> selectRecordList(@Param("page")Pagination page,@Param("params")Map<String,Object> params);
	
}
