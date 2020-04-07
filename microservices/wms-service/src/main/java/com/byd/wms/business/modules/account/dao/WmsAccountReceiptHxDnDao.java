package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.account.entity.WmsHxDnEntity;

/**
 * SAP交货单 核销业务
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-09-17 16:06:38
 */
public interface WmsAccountReceiptHxDnDao extends BaseMapper<WmsHxDnEntity> {
	
    /**
     * 根据供应商代码获取供应商信息
     * @param LIFNR 供应商编号
     * @return
     */
    public Map<String,Object> getSapVendorByNo(String LIFNR);
	
    /**
     * 根据交货单行项目，查询关联的采购订单信息及物料信息
     * @param dnItemList 交货单行项目
     * @return
     */
    List<Map<String, Object>> getPoItemListByDnItem(@Param("dnItemList")List<Map<String,Object>> dnItemList,@Param("params") Map<String,Object> params);
    
	
}
