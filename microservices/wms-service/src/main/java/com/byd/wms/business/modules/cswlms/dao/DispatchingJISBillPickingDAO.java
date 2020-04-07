package com.byd.wms.business.modules.cswlms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface DispatchingJISBillPickingDAO {
	/**
	 * 总装JIS需求拣配单查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectDispatchingJISBillPicking(Map<String,Object> params);
	
	/**
	 * 排序类别查询
	 */
	public List<Map<String,Object>> selectAssemblySortType(Map<String,Object> params);
	
	/**
	 * 总装物流参数配置查询
	 */
	public List<Map<String,Object>> selectAssemblyLogistics(Map<String,Object> params);
	
	public List<Map<String,Object>> selectDispatchingJISBillPickingByDispatcingNo(Map<String,Object> params);
	
	public List<Map<String,Object>> selectCoreWhBin(Map<String,Object> params);
	
	public int updateStockQtyByDispatching(Map<String, Object> params);
	
	public int updateDispatchingComponent(Map<String, Object> params);
	
	public int updateDispatchingPickRecordNoComponent(Map<String, Object> params);
	
	public int insertDispatchingComponent(Map<String, Object> params);
	
	public List<Map<String,Object>> selectDispatchingComponent(Map<String,Object> params);
	
	public List<Map<String,Object>> selectDispatchingItem(Map<String,Object> params);
	
	public List<Map<String,Object>> selectDispatchingHeader(Map<String,Object> params);
	
	public int nextComponentNo(Map<String, Object> params);
	
	public int ifWholeComponentChangedStatus(Map<String, Object> params);
	
	public int ifWholeItemChangedStatus(Map<String, Object> params);
	
	public int updateDispatchingStatusItem(Map<String, Object> params);
	
	public int updateDispatchingStatusItemByList(List<Map<String, Object>> paramlist);
	
	public int updateDispatchingStatusHeader(Map<String, Object> params);
	
	public int updateDispatchingStatusHeaderByList(List<Map<String, Object>> paramlist);
	
	public int updateDispatchingStatusComponent(Map<String, Object> params);
	
	public int updateDispatchingStatusComponentByList(List<Map<String, Object>> paramlist);
	
	public int insertTPicking(List<Map<String, Object>> pickinglist);
	
	/**
	 * 总装非JIS需求拣配单查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectDispatchingFeiJISBillPicking(Map<String,Object> params);
	/**
	 * 拣配确认查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectDispatchingQueRen(Map<String,Object> params);
	
	public int updateDispatchingConformDate(Map<String, Object> params);
	
	public int updateDispatchingConformDateByList(List<Map<String, Object>> paramlist);
	/**
	 * 更新实际交接时间，交接人
	 * @param params
	 * @return
	 */
	public int updateDispatchingHandoverDate(Map<String, Object> params);
	
	public int updateDispatchingHandoverDateByList(List<Map<String, Object>> paramlist);
	
	//
	public List<Map<String,Object>> selectDispatchingBillPickingByPrint(Map<String,Object> params);
	
	/**
	 * 拣配单查询 
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectDispatchingByfabu(Map<String,Object> params);
	
	public int insertHeader(List<Map<String, Object>> pickinglist);
	
	public int insertItem(List<Map<String, Object>> pickinglist);
	
	public int insertComponent(List<Map<String, Object>> pickinglist);
	
	public int deleteComponentByID(Map<String, Object> params);
	
	public int countOfComponentByItem(Map<String, Object> params);
	
	public int deleteItem(Map<String, Object> params);
	
	public int selectCountHeaderByBillNo(Map<String, Object> params);
	
	public int deleteHeader(Map<String, Object> params);
	
	public List<Map<String,Object>> selectComponentByBarcode(Map<String,Object> params);
	
	public int updateDispatchingStatusByBarcode(Map<String, Object> params);
	
	public List<Map<String,Object>> selectTPicking(Map<String,Object> params);//
	
	public int updateStockQtyByBatch(Map<String, Object> params);
	
	public int updateDispatchingPickingInfoByBarcode(Map<String, Object> params);//
	
	public int delTPickingByBarcode(Map<String, Object> params);
	
	public int updateTPickingByBarcodeByList(List<Map<String, Object>> paramlist);
	
	public int insertToWlmsException(Map<String, Object> params);
	
	public List<Map<String,Object>> selectToWlmsException(Map<String,Object> params);
	
	public List<Map<String,Object>> selectPickRecordNoCount(Map<String, Object> params);
	
}
