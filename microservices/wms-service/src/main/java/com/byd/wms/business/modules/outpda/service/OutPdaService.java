package com.byd.wms.business.modules.outpda.service;

import java.util.Map;

import com.byd.utils.PageUtils;

public interface OutPdaService {
	public PageUtils queryTaskPage(Map<String, Object> params);
	/**
	 * 根据需求号获取超发标识
	 * @param params
	 * @return
	 */
	public String getChaFaBiaoShi(Map<String, Object> params);
	/**
	 * 根据条码号、料号、批次、储位获取状态为07、08的条码信息
	 * 
	 * @param params(matnr:料号、batch:批次、bin_code:储位、label_no;)
	 * @return
	 */
	public Map<String, Object> getLabelInfo(Map<String, Object> params);
	
	/**
	 * 保存下架信息
	 * @param params
	 * @return
	 */
	public void saveXiaJiaXinXi(Map<String, Object> params);
}
