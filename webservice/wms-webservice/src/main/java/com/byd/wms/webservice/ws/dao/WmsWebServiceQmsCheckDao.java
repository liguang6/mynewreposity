package com.byd.wms.webservice.ws.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * qms质检接口dao
 * 
 * @author rain
 * @email 
 * @date 2019年11月28日16:35:16
 */
public interface WmsWebServiceQmsCheckDao  {


	Map<String,Object> getInReceiptByNo(Map<String, Object> map);

	List<Map<String,Object>> getInspectionItemByNo(Map<String, Object> map);

	Map<String,Object> getInspectionItemByNoINo(Map<String, Object> map);


	List<Map<String,Object>> getCoreLableByNo(Map<String, Object> map);


	/**
	 * 批量更新收货单信息
	 * @param list
	 * @return
	 */
	int batchUpdateInReceipt(List<Map<String,Object>> list);
	/**
	 * 批量更新质检单明细状态信息
	 * @param list
	 * @return
	 */
	int batchUpInspeItemStatus(List<Map<String,Object>> list);
	/**
	 * 批量更新质检单状态信息（部分质检）
	 * @param list
	 * @return
	 */
	int batchUpInspeHeadStatus(List<Map<String,Object>> list);
	/**
	 * 批量更新质检单状态信息（全部质检）
	 * @param list
	 * @return
	 */
	int batchUpInspeHeadAllStatus(List<Map<String,Object>> list);
	/**
	 * 通过label_no，批量更新标签表信息
	 * @param list
	 * @return
	 */
	int batchUpdateCoreLabel(List<Map<String,Object>> list);

	/**
	 * 通过质检单号，批量更新标签表信息（合格）
	 * @param set
	 * @return
	 */
	int upQCoreLabelByInspectionNo(@Param("inspectNO")String inspectNO);

	int bthUpQCoreLabelByInspectionNo(List<Map<String,Object>> list);

//	int bthUpQCoreLabelByInspectionNo(@Param("list") List<String> list);

	/**
	 * 通过质检单号，批量更新标签表信息（不合格）
	 * @param set
	 * @return
	 */
	int bthUpNqCoreLabelByInspectionNo(List<Map<String,Object>> list);

//	int bthUpNqCoreLabelByInspectionNo(@Param("list") List<String> list);

	/**
	 * 批量保存质检结果
	 * @param list
	 * @return
	 */
	int saveQcResult(List<Map<String,Object>> list);

	int batSaveTQcResult(Map<String, Object> map);

	int batSaveYNQcResult(Map<String, Object> map);




	/**
	 * 保存webserviceLog日志信息
	 * @param
	 */
	void insertLogInfo(Map<String, Object> map);
	void insertLogInfos(List<Map<String, Object>> logList);
	void saveLogInfos(Map<String, Object> map);

}
