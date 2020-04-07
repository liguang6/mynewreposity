package com.byd.wms.business.modules.account.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.entity.WmsHxDnEntity;

import java.util.List;
import java.util.Map;

/**
 * SAP303调拨单核销业务Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-10-10 16:06:38
 */
public interface WmsAccountReceiptHxDnService extends IService<WmsHxDnEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 根据交货单编号+交货单行项目号更新SAP交货单核销信息
     * @param wmsHxDnEntity 必须包含值：VBELN：交货单号  POSNR：交货单行项目号
     * @return boolean
     */
    boolean updateByDnNo(WmsHxDnEntity wmsHxDnEntity);
    
    /**
     * 根据供应商代码获取供应商信息
     * @param LIFNR 供应商编号
     * @return
     */
    Map<String,Object> getSapVendorByNo(String LIFNR);
    
    /**
     * 根据交货单行项目，查询关联的采购订单信息及物料信息
     * @param dnItemList 交货单行项目
     * @return
     */
    List<Map<String, Object>> getPoItemListByDnItem(List<Map<String,Object>> dnItemList,Map<String,Object> params);
    
	/**
	 * SAP交货单收货确认（V）-核销业务
	 */
	public R boundIn_DNV(Map<String, Object> params);
}

