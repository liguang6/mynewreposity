package com.byd.wms.business.modules.out.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ScannerOutDAO {
	/**
	 * <p>查询出库需求头表</p>
	 * @param 
	 * 传入的<tt>Map</tt>需要四个参数:
	 * <ul>
	 * 	<li>werks 工厂</li>
	 *  <li>whNumber 仓库</li>
	 *  <li>requirementNo 需求号</li>
	 *  <li>requirementType 需求类型</li>
	 * </ul>
	 *
	 * @author Yang Lin
	 * @return 符合条件的数据库记录(<tt>Map</tt>)
	 */
	public List<Map<String,Object>> selectOutRequirementHead(@Param("params")Map<String,Object> params);

	/**
	 * Created on 2018/12/28</br>
	 * 查询业务列表
	 * 
	 * @param werks
	 * @param businessClass
	 * @return businessName <tt>key-value</tt>
	 * @author YangLin
	 */
	public List<Map<String,Object>> selectBusinessNames(@Param("werks")String werks,@Param("businessClass")String businessClass);

	
	public List<Map<String,Object>> selectPlantBusiness(@Param("werks")String werks,@Param("businessClass")String businessClass,@Param("businessName")String businessName);

	/**
	 * 更新下架数量
	 * @param  
	 * <ul>
	 * <li>werks:工厂 必填</li>
	 * <li>matnr:料号 必填</li>
	 * <li>batch:批次 必填</li>
	 * <li>xjQty:下架数量</li>
	 * <li>xjBinCode:下架储位</li>
	 * <li>qty:下架数量/取消下架数量</li>
	 * </ul>
	 * @return
	 */
	public int updateStockXJQty(Map<String,Object> obj);
	
	/**
	 * 更新出库拣配表
	 * @param obj
	 * @return
	 */
	public int updateOutPicking(@Param("params")Map<String,Object> obj);
	
	/**
	 * 查询物料的批次信息
	 * 
	 * @param params
	 * <p>
	 * 	<ul><li>werks-工厂</li> <li>wh_number-仓库</li> <li>matnr-物料</li> <li>bin_code-储位</li> <li>lgort-库位</li></ul>
	 * </p>
	 * @return
	 * batch
	 */
	List<Map<String,Object>> selectMatBatch(@Param("params")Map<String,Object> params);

    boolean saveLabelByPda(ArrayList arrayList);

	List<Map<String, Object>> queryCoreLabelByPda(Map<String, Object> data);

	List<Map<String, Object>> queryBarcodeCache(Map<String, Object> data);

	List<Map<String,Object>>  queryBarcodeOnly(Map<String, Object> data);
	
	void deteleCacheBarcodeByCreator(List<String> data);

	int updateBarcodeQty(Map<String,Object> params);
	
	void insertCoreWHTask(List<Map<String,Object>> params);

	void updateStock(List<Map<String, Object>> params);

	List<Map<String, Object>> selectCoreLabel(@Param("params")Map<String,Object> params);

	Map<String, Object> getMoveAndSyn(Map<String, Object> map);
}
