package com.byd.wms.business.modules.qc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionHeadEntity;

/**
 * 送检单抬头
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcInspectionHeadDao extends BaseMapper<WmsQcInspectionHeadEntity> {
	/**
	 * 更新WMS库存表】数量（QTY）和冻结数量（FREEZE_QTY）字段值。
	 * @param params
	 */
	void updateStockQty(@Param("params")Map<String,Object> params);
	
	/**
	 * 库存复检-质检中-更新
	 * @param params
	 */
	void updateOnInspectStockQty(@Param("params")Map<String,Object> params);
	
	/**
	 * 查询检验单
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryInspectionList(@Param("page")Pagination page,@Param("params")Map<String,Object> params);

	/**
     * 更新标签状态
     * @param status
     * @param labelNo
     */
    public void updateWmsCoreLabelStatus(@Param("status")String status,@Param("label_no")String labelNo);
    
    /**
     * 插入库存冻结记录
     * @param columns
     */
    void insertKnFreezeRecord(Map<String,Object> columns);
}
