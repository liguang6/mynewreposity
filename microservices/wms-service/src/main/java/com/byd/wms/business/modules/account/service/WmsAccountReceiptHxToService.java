package com.byd.wms.business.modules.account.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.entity.WmsHxToEntity;
import java.util.List;
import java.util.Map;

/**
 * SAP交货单核销业务Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-20 16:06:38
 */
public interface WmsAccountReceiptHxToService extends IService<WmsHxToEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 根据303物料凭证编号+行项目号更新SAP303调拨单核销信息
     * @param wmsHxToEntity 必须包含值：MAT_DOC：303物料凭证编号  MAT_DOC_ITM：行项目号
     * @return boolean
     */
    boolean updateByMatDoc(WmsHxToEntity wmsHxToEntity);
    
    /**
     * 根据303物料凭证行项目，查询关联的核销信息
     * @param toItemList 303物料凭证行项目
     * @return
     */
    List<Map<String, Object>> getHxToListByMatDocItem(List<Map<String,Object>> toItemList);
    
	/**
	 * 303调拨收货确认（V）-核销业务
	 */
	public R boundIn_TOV(Map<String, Object> params);
}

