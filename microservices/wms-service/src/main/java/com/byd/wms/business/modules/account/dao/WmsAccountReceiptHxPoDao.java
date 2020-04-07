package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.account.entity.WmsHxPoEntity;

/**
 * SAP采购订单 核销业务
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-20 16:06:38
 */
public interface WmsAccountReceiptHxPoDao extends BaseMapper<WmsHxPoEntity> {
	/**
	 * 根据工厂代码查询危化品物料列表
	 */
	List<Map<String, Object>> getDangerMatList(Map<String, Object> params);
	Map<String, Object> getVendorInfo(Map<String, Object> params);
	/**
	 *  获取采购订单行项目列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getPOItems(Map<String, Object> params);
	/**
	 * 更新WMS库存信息，包含新增和修改库存
	 * @param updateList
	 */
	void updateWmsStock(List<Map<String, Object>> updateList);
	
	/**
	 * 根据采购订单、行项目号及凭证数量获取剩余核销数量-凭证数量小于0的PO核销记录
	 * @param list
	 * @return
	 */
	List<Map<String, Object>> checkHxPoInfo(List<Map<String, Object>> list);
	
}
