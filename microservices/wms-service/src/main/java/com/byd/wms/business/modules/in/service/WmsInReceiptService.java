package com.byd.wms.business.modules.in.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.in.entity.WmsInReceiptEntity;
import java.util.List;
import java.util.Map;

/**
 * WMS收货Service
 *
 * @author (changsha) byd_infomation_center
 * @email
 * @date 2018-08-20 16:06:38
 */
public interface WmsInReceiptService extends IService<WmsInReceiptEntity> {
	public String setMatBatch(Map<String, Object> params ,List<Map<String,Object>> matList);
	PageUtils queryPage(Map<String, Object> params);
	List<String> getMatStock(List<Map<String, Object>> matList);
	List<String> getUrgentMatList(Map<String, Object> params);
	List<Map<String, Object>> getDangerMatList(Map<String, Object> params);
	List<String> getPoItemListByPo(List<String> poitemList);

	public List<Map<String, Object>> getLabelInfo(Map<String, Object> params);

	List<String> getPoTypeListByPo(List<String> poList);
	/**
	 * 根据封装好的查询条件（送货单号#*送货单行项目号）查询每条行项目已收货数量
	 */
	Map<String, Object> getReceiptCount(List<String> asnList);
	/**
	 * 根据供应商代码，工厂代码获取采购和供应商简称（判断WMS_C_PLANT表是否配置了工厂启用供应商管理）
	 */
	Map<String, Object> getVendorInfo(Map<String, Object> params);
	/**
	 * 根据采购订单、订单行项目号从采购订单表中抓取申请人和需求跟踪号
	 */
	Map<String, Object> getBendrAfnam(List<String> poitemList);
	/**
	 * 根据物料行项目从物料质检配置表读取匹配的物料列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getQCMatList(Map<String, Object> params);
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
	List<Map<String, Object>> getLabelInfoByLabelNo(List<Map<String, Object>> lableNoList);

	/**
	 * SCM送货单确认收货
	 * @param params
	 * @param r
	 */
	R boundIn_01(Map<String, Object> params);
	/**
	 * 获取采购订单行项目列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getPoItems(Map<String, Object> params);
	/**
	 * 获取采购订单获取供应商
	 * @param params
	 * @return
	 */
	List<String> getPOLifnr(List<String> params);
	/**
	 * 根据封装好的查询条件（如：SAP采购订单号#*采购订单行项目号）查询每条行项目已收货数量
	 * @param
	 * @return
	 */
	Map<String, Object> getReceiptCount(Map<String,Object> params);
	/**
	 * PO采购订单确认收货
	 * @param params
	 * @param
	 */
	R boundIn_02(Map<String, Object> params);

	void updateSCMStauts(List<Map<String, Object>> skList, List<Map<String,Object>> matList);
	/**
	 * SAP交货单确认收货
	 * @param params
	 * @return
	 */
	R boundIn_03(Map<String, Object> params);
	/**
	 * 根据SAP采购订单号查询行项目的收货权限工厂
	 * @param params
	 * @return
	 */
	List<Map<String, String>> getItemAuthWerksList(Map<String, Object> params);
	/**
	 * 303调拨确认收货
	 * @param params
	 * @return
	 */
	R boundIn_06(Map<String, Object> params);
	/**
	 * 根据物料号获取物料信息
	 * @param MATNR_STR
	 * @return
	 */
	List<Map<String, Object>> getMatListByMATNR(String MATNR_STR,String WERKS);
	/**
	 * 无PO确认收货
	 * @param params
	 * @return
	 */
	R boundIn_05(Map<String, Object> params);
	/**
	 * 根据303凭证和收货工厂、收货仓库号获取需要核销的物料信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getHXMatList(Map<String, Object> params);
	/**
	 * 303调拨收货（A）确认收货
	 * @param params
	 * @return
	 */
	R boundIn_09(Map<String, Object> params);
	/**
	 * 根据SAP采购订单、仓库号查询需要核销的物料信息
	 */
	List<Map<String, Object>> getHXPOMatList(Map<String, Object> params);
	/**
	 * SAP采购订单(A)确认收货
	 * @param params
	 * @return
	 */
	R boundIn_07(Map<String, Object> params);
	/**
	 * SAP交货单(A)确认收货
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getHXDNMatList(Map<String, Object> params);
	/**
	 * SAP交货单(A)确认收货
	 * @param params
	 * @return
	 */
	R boundIn_08(Map<String, Object> params);

	String getWHAddr(String WERKS, String LGORT);
	/**
	 * 云平台送货单确认收货
	 * @param params
	 * @return
	 */
	R boundIn_78(Map<String, Object> params);
}

