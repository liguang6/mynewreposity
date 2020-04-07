package com.byd.wms.business.modules.inpda.service;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.List;
import java.util.Map;

/**
 * @author rain
 * @version 创建时间：2019年12月26日10:14:57
 * 类说明  PDA-UB订单入库
 */
public interface WmsInPdaUBPoReceiptService {

	public R getMorecCache(Map<String, Object> map);

	public R validlableinfo(Map<String, Object> map) ;

	public Map<String, Object> labelMoHandoverValid(Map<String, Object> params);

	public R getMoDetailByBarcode(Map<String, Object> map);

	public PageUtils getGridPoreDataPage(Map<String, Object> params);

	public PageUtils getBarGridPoreDataPage(Map<String, Object> params);

	public PageUtils getGridReqItemData(Map<String, Object> params);


	public R poReDeleteBarInfo(Map<String, Object> map);

	public R handover_new(Map<String, Object> map) ;
	public R ubPOReceiptPdaIn(Map<String, Object> map);

	List<Map<String,Object>> getAllLabelInfos(Map<String, Object> map);








}
