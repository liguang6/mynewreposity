package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.account.entity.WmsHxToEntity;

/**
 * SAP303调拨单 核销业务
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-10-10 16:06:38
 */
public interface WmsAccountReceiptHxToDao extends BaseMapper<WmsHxToEntity> {
	
    /**
     * 根据303物料凭证行项目，查询关联的核销信息
     * @param toItemList 303物料凭证行项目
     * @return
     */
    List<Map<String, Object>> getHxToListByMatDocItem(List<Map<String,Object>> toItemList);
    
	/**
	 * 根据303调拨凭证号、行项目号及凭证数量获取剩余核销数量-凭证数量小于0的调拨核销记录
	 * @param list
	 * @return
	 */
	List<Map<String, Object>> checkHxToInfo(List<Map<String, Object>> list);
}
