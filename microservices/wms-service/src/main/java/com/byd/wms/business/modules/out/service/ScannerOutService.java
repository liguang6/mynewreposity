package com.byd.wms.business.modules.out.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;

/**
 * 扫描枪出库-服务
 * @author yang.lin35
 *
 */
public interface ScannerOutService {
	/**
	 * 下架
	 * @param params
	 * @return
	 */
	public int obtained(List<Map<String,Object>> params);
	/**
	 * 取消下架</br>
	 * 1.更新下架拣配记录，删除标识DEL->X</br>
	 * 2.更新库存数据，STOCK_QTY + QTY , XJ_QTY - QTY, XJ_BIN_CODE 清空</br>
	 * 3.如果bin_code = '05' 调立库接口通知下架</br>
	 * TODO: 立库接口怎么调
	 * @param params
	 * @return
	 */
	public int cancelOntained(List<Map<String,Object>> params);
	
	/**
	 * 交接确认
	 * 
	 * @param params
	 * @return
	 */
	public String handoverComfirm(List<Map<String,Object>> params) ;
	
	/**
	 * 判断是否符合先进先出规则
	 * @param params
	 * @return
	 */
	public String isFirstBatch(List<Map<String, Object>> params);

    List<Map<String, Object>> queryCoreLabelByPda(Map<String, Object> data);

	boolean saveLabelByPda(ArrayList params);

	List<Map<String, Object>> queryBarcodeCache(Map<String, Object> data);

	List<Map<String,Object>> queryBarcodeOnly(Map<String, Object> data);
	
	public int updateBarcodeQty(Map<String, Object> data);
	
	void deteleCacheBarcodeByCreator(@RequestBody List<String> data);
}
