package com.byd.wms.business.modules.in.dao;

import com.byd.wms.business.modules.in.entity.WmsInReceiptEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * WMS收货单
 *
 * @author (changsha) byd_infomation_center
 * @email
 * @date 2018-08-20 16:06:38
 */
public interface WmsInReceiptDao extends BaseMapper<WmsInReceiptEntity> {

	List<String> getMatStock(@Param(value="matList") List<Map<String, Object>> matList);
	/**
	 * 根据工厂代码、系统时间查询紧急物料列表
	 */
	List<String> getUrgentMatList(Map<String, Object> params);
	/**
	 * 根据工厂代码查询危化品物料列表
	 */
	List<Map<String, Object>> getDangerMatList(Map<String, Object> params);
	/**
	 * 根据送货单对应的采购订单行项目数据查询WMS采购订单数据表对应的数据
	 * @param condList
	 * @return
	 */
	List<String> getPoItemListByPo(@Param(value="condList") List<Map<String, Object>> condList);

	/**
	 * 根据采购订单号获取采购订单凭证类型信息
	 */
	List<String> getPoTypeListByPo(@Param(value="poList") List<String> poList);
	/**
	 * 根据封装好的查询条件（送货单号##送货单行项目号）查询每条行项目已收货数量
	 * 返回JSON字符串：{"asnno##asnitm":10,....}
	 * @param condMap
	 * @return
	 */
	String getReceiptCount(Map<String, Object> condMap);
	Map<String, Object> getVendorInfo(Map<String, Object> params);
	String getBendrAfnam(@Param(value="condList")List<Map<String, Object>> condList);
	/**
	 * 根据工厂代码、WMS业务类型代码、系统日期查询质检配置
	 * @param params
	 * @return
	 */
	String getTestFlag(Map<String, Object> params);

	/**
	 * 根据SAP物料凭证获取关联的标签号
	 * @param lableNoList
	 * @return
	 */
	List<Map<String, Object>> getLabelNoByMatDocNo(Map<String, Object> params);

	/**
	 * 根据标签号获取标签信息
	 * @param lableNoList
	 * @return
	 */
	List<Map<String, Object>> getLabelInfoByLabelNo(List<Map<String, Object>> labelNoList);
	/**
	 * 根据物料行项目从物料质检配置表读取匹配的物料列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getQCMatList(Map<String, Object> params);
	/**
	 * 保存送检单抬头
	 * @param INSPECTION_MAP
	 */
	void insertInspectionHead(Map<String, Object> INSPECTION_MAP);
	/**
	 * 保存送检单明细
	 * @param matList
	 */
	void insertInspectionItem(List<Map<String, Object>> matList);
	/**
	 * 保存WMS凭证抬头
	 * @param wms_doc_head
	 */
	void insertWMSDocHead(Map<String, Object> wms_doc_head);

	/**
	 * 更新WMS凭证行项目的送检单信息 INSPECTION
	 * @param matList
	 */
	void updateWMSDocItemInspection(List<Map<String, Object>> matList);

	/**
	 * 保存收货单
	 * @param matList
	 */
	void insertReceiptInfo(List<Map<String, Object>> matList);
	/**
	 * 保存质检结果
	 * @param match_list
	 */
	void insertQCResult(List<Map<String, Object>> match_list);
	/**
	 * 保存质检记录
	 * @param match_list
	 */
	void insertQCRecord(List<Map<String, Object>> match_list);
	/**
	 * 根据质检结果行项目更新收货单行项目的可进仓数量
	 * @param match_list
	 */
	void updateReceiptInableQty(List<Map<String, Object>> match_list);
	/**
	 * 根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
	 * 查询收料房库存信息
	 * @param matList
	 * @return
	 */
	List<String> getRhStockList(List<Map<String, Object>> matList);
	/**
	 * 保存收料房库存信息
	 * @param matList
	 */
	void insertRhStock(List<Map<String, Object>> matList);
	/**
	 * 更新收料房库存信息
	 * @param updateList
	 */
	void updateRhStock(List<Map<String, Object>> updateList);
	/**
	 * 保存标签数据
	 * @param skList
	 */
	void insertCoreLabel(List<Map<String, Object>> skList);
	/**
	 *  获取采购订单行项目列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getPOItems(Map<String, Object> params);
	/**
	 *  获取采购订单供应商
	 * @param params
	 * @return
	 */
	List<String> getPOLifnr(@Param(value="lifnrList") List<String> lifnrList);
	/**
	 * 根据SAP采购订单号查询行项目的收货权限工厂
	 * @param params
	 * @return
	 */
	List<Map<String, String>> getItemAuthWerksList(Map<String, Object> params);
	/**
	 * 根据物料号获取物料信息
	 * @param matnr_list
	 * @return
	 */
	List<Map<String, Object>> getMatListByMATNR(@Param(value="list")List<String> matnr_list,@Param(value="WERKS")String WERKS);
	/**
	 * 根据303凭证和收货工厂、收货仓库号获取需要核销的物料信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getHXMatList(Map<String, Object> params);
	/**
	 * 更新SAP303调拨单核销信息表--WMS_HX_TO 实收303DB数量（SS303DB）、303剩余核销数量（HX_QTY_XS）
	 * @param matList
	 */
	void update303HXInfo(List<Map<String, Object>> matList);
	/**
	 * 根据SAP采购订单、仓库号查询需要核销的物料信息
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> getHXPOMatList(Map<String, Object> params);
	/**
	 * 更新SAP采购订单核销信息表--WMS_HX_PO 实收SS103数量（SS103）、剩余核销数量（HX_QTY）
	 * @param matList
	 */
	void updatePOHXInfo(List<Map<String, Object>> matList);
	/**
	 * 根据SAP交货单、仓库号查询需要核销的物料信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getHXDNMatList(Map<String, Object> params);
	/**
	 *  更新SAP交货单核销信息表--WMS_HX_DN 实收SS103T数量（SS103T）、剩余核销数量（HX_QTY_XS）
	 * @param matList
	 */
	void updateDNHXInfo(List<Map<String, Object>> matList);
	/**
	 * 根据送货单号，送货单行项目号获取已收数量
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getHasReceiveQty(Map<String, Object> params);

	public List<Map<String, Object>> getLabelInfo(Map<String, Object> params);

	String getWHAddr(@Param(value="WERKS")String WERKS, @Param(value="LGORT")String LGORT);

}
