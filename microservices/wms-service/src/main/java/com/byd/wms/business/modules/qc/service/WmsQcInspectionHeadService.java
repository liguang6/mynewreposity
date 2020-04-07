package com.byd.wms.business.modules.qc.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionHeadEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;

/**
 * 送检单抬头
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcInspectionHeadService extends IService<WmsQcInspectionHeadEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    
    /**
     * 来料质检 - 未质检  - 保存
     * @param batchInscpectionResults 送检单明细列表
     */
    public void saveBatchInspectionResult(List<WmsQcInspectionItemEntity> batchInscpectionResults,String staffNumber);
    
    /**
     * 来料质检 - 质检中 - 单批质检 -  保存
     * @param batchInscpectionResults
     */
    public void saveOnBatchInspection(List<WmsQcResultEntity> qcResults,String staffNumber);
    /**
	 * 质检中-批量质检
	 * @param qcResults
	 */
    public void saveOnInspection(List<WmsQcResultEntity> qcResults,String staffNumber);
    
    /**
     * 创建库存复检单
     * @param stock
     */
    public String addStockRejudgeInspect(List<Map<String,Object>> stockList,String staffNumber);
    
    /**
     * 已质检改判
     * @param results
     */
    public void reJudgeSave(List<WmsQcResultEntity> results,String staffNumber);
    
}

