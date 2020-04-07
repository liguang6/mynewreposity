package com.byd.wms.business.modules.returnpda.service;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.List;
import java.util.Map;

/**
 * @author liguang6
 * @version 2019年1月2日12:28:34
 * 立库余料退回（312）
 */
public interface WmsReMaterialWarehouseTwoService {

	public R validatePoReceiptLable(Map<String, Object> map) ;

	public R getPoDetailByBarcode(Map<String, Object> map);

	public PageUtils getGridPoreDataPage(Map<String, Object> params);

	public PageUtils getBarGridPoreDataPage(Map<String, Object> params);

	public R poReDeleteBarInfo(Map<String, Object> map);

	public R getPorecCache(Map<String, Object> map);

	public Map<String,Object> poReceiptPdaIn(Map<String, Object> map);

	List<Map<String,Object>> getAllLabelInfos(Map<String, Object> map);


}
