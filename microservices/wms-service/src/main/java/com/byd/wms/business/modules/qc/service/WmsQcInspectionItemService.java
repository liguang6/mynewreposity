package com.byd.wms.business.modules.qc.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import java.util.List;
import java.util.Map;

/**
 * 送检单明细
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcInspectionItemService extends IService<WmsQcInspectionItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
        
    PageUtils queryHasInspectedListWithPage(Map<String, Object> params);
    
    /**
     * 创建复检单 - 查询库存数据
     * @param params
     * @return
     */
    PageUtils queryStockReJudgeItemsWithPage(Map<String, Object> params);
    
    /**
     * 库存复检-未质检查询
     * @param params
     * @return
     */
    PageUtils selectStockReJudgeNotInspected(Map<String, Object> params);
    
    /**
     * 库存复检-质检中
     * @param params
     * @return
     */
    List<Map<String, Object>>  selectStockRejudgeOnInspect(Map<String, Object> params);
    
    /**
     * 查询检验单信息
     * @param params
     * @return
     */
    List<WmsQcInspectionItemEntity> selectInspectionItems(Map<String, Object> params);
    
    List<Map<String, Object>>  getInspectionItemTask(Map<String, Object> params);
}

