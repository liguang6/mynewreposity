package com.byd.wms.business.modules.account.service;

import com.byd.utils.R;
import java.util.List;
import java.util.Map;

/**
 * SAP生产订单核销业务（成品、联产品、副产品虚收）Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-10-24 16:06:38
 */
public interface WmsAccountReceiptHxMoService {

    /**
     * 根据生产订单号更新生产订单抬头（成品虚收）核销信息
     * @param map 必须包含值：AUFNR：生产订单号
     * @return boolean
     */
    boolean updateMoHeadByMoNo(Map<String,Object> map);
    
    /**
     * 根据生产订单号更新生产订单组件（联产品、副产品、投料）核销信息
     * @param map 必须包含值：AUFNR：生产订单号、RSNUM：预留/相关需求的编号、RSPOS：预留/相关需求的项目编号
     * @return boolean
     */
    boolean updateMoComponentByMoNo(Map<String,Object> map);
    
    /**
     * 根据生产订单号，查询生产订单及关联的核销信息（包含抬头、行项目和副产品及核销信息）
     * @param  moList 必须包含值：AUFNR 生产订单号数组 WERKS 生产工厂
     * @return map 包含： moHeadInfoList-List<Map<String,Object>>：生产订单抬头及核销信息（成品收货（V）） 
     * 			   moComponentInfoList-List<Map<String,Object>>：生产订单副产品及核销信息（副产品收货（V）核销信息）
     */
    Map<String, Object> getMoInfoByMoNo(List<String> moList,String WERKS);
    
    /**
     * 根据生产订单号，查询生产订单抬头及组件的核销信息（包含抬头、组件、组件核销信息）
     * @param  MO_NO ：生产订单号  WERKS 生产工厂,
     * @return map 包含： moHeadInfoList-List<Map<String,Object>>：生产订单抬头及核销信息
     * 			   moComponentInfoList-List<Map<String,Object>>：生产订单组件及核销信息
     */
    Map<String, Object> getMoInfoByMoNo(String MO_NO,String WERKS);
    
    /**
     * 根据生产订单组件查询组件物料在WMS系统的虚拟库存、非限制库存、是否可超虚拟库存发料
     * @param moComponentList 生产订单组装机
     * @return moComponentList 包含了库存信息
     */
    List<Map<String, Object>> getMoComponentVirtualStock(List<Map<String, Object>> moComponentList);
    /**
     * 根据工厂、业务类型名称、WMS业务类型、仓库业务分类 查询 是否可超虚拟库存发料
     * @param map WERKS：工厂 BUSINESS_NAME：业务类型名称 BUSINESS_TYPE：WMS业务类型  BUSINESS_CLASS：业务分类
     * @return
     */
    List<Map<String, Object>> getPlantBusinessInfo(Map<String,Object> map);
    
	/**mo
	 * SAP生产订单核销业务（成品、联产品、副产品虚收）收货（V）-核销业务
	 */
	public R boundIn_MOV(Map<String, Object> params);
	
	/**
	 * SAP生产订单核销业务（组件）发料（V）
	 * @param params
	 * @return
	 */
	R postGI_MOV(Map<String, Object> params);
	/**
	 * 获取物料信息，物料库存信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getMatListByMATNR(Map<String, Object> params);
	/**
	 * 303调拨虚发确认
	 * @param params
	 * @return
	 */
	R postGI_303V(Map<String, Object> params);
	/**
	 * 查询交货单是否存在未删除的需求号【WMS_OUT_REQUIREMENT_ITEM】
	 * @param oUT_NO
	 * @return
	 */
	int getRequirementBySapOutNo(String OUT_NO);
}

