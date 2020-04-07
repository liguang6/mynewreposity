package com.byd.wms.business.modules.inpda.service;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.List;
import java.util.Map;

/**
 * @author rain
 * @version 2019年12月4日20:28:52
 * PDA采购订单收货
 */
public interface WmsPoReceiptPdaService {

	public R validatePoReceiptLable(Map<String, Object> map) ;

	public R getPoDetailByBarcode(Map<String, Object> map);

	public PageUtils getGridPoreDataPage(Map<String, Object> params);

	public PageUtils getBarGridPoreDataPage(Map<String, Object> params);

	public R poReDeleteBarInfo(Map<String, Object> map);

	public R getPorecCache(Map<String, Object> map);

	public R poReceiptPdaIn(Map<String, Object> map);

	List<Map<String,Object>> getAllLabelInfos(Map<String,Object> map);


}
