package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.account.entity.WmsHxToEntity;

/**
 * SAP生产订单核销业务（成品、联产品、副产品虚收） 核销业务
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-10-20 16:06:38
 */
public interface WmsAccountReceiptHxMoDao{
	
    /**
     * 根据生产订单编号查询生产订单抬头及抬头核销信息（成品收货（V））
     * @param List<String> moList 必须包含值：AUFNR：生产订单号数组  WERKS：生产工厂
     * @return map 生产订单抬头及核销信息（成品收货（V））
     */
    List<Map<String, Object>> getMoHeadInfoByMoNo(@Param("moList")List<String> moList,@Param("WERKS")String WERKS);
    /**
     * 根据生产订单编号查询生产订单行项目及核销信息（成品收货（V））
     * @param List<String> moList 必须包含值：AUFNR：生产订单号数组  WERKS：生产工厂
     * @return map 生产订单抬头及核销信息（成品收货（V））
     */
    List<Map<String, Object>> getMoItemInfoByMoNo(@Param("moList")List<String> moList,@Param("WERKS")String WERKS);
    
    /**
     * 根据生产订单编号查询生产订单联产品及核销信息（成品收货（V））
     * @param List<String> moList 必须包含值：AUFNR：生产订单号数组 WERKS：生产工厂
     * @return map 生产订单组件及核销信息（副产品收货（V）核销信息）
     */
    List<Map<String, Object>> getMoByComponentInfoByMoNo(@Param("moList")List<String> moList,@Param("WERKS")String WERKS);
    
    /**
     * 根据生产订单编号查询生产订单组件及虚发信息（生产订单发料（V））
     * @param List<String> moList 必须包含值：AUFNR：生产订单号数组 WERKS：生产工厂
     * @return map 生产订单组件及核销信息（组件发料核销信息）
     */
    List<Map<String, Object>> getMoComponentInfoByMoNo(@Param("moList")List<String> moList,@Param("WERKS")String WERKS,@Param("BWART") String BWART);
    
    /**
     * 根据生产订单组件查询组件物料在WMS系统的虚拟库存、非限制库存、是否可超虚拟库存发料
     * @param moComponentList 生产订单组装机
     * @return moComponentList 包含了库存信息
     */
    List<Map<String, Object>> getMoComponentVirtualStock(@Param("moComponentList")List<Map<String, Object>> moComponentList);
    
    /**
     * 根据工厂、业务类型名称、WMS业务类型、仓库业务分类 查询 是否可超虚拟库存发料
     * @param map WERKS：工厂 BUSINESS_NAME：业务类型名称 BUSINESS_TYPE：WMS业务类型  BUSINESS_CLASS：业务分类
     * @return
     */
    List<Map<String, Object>> getPlantBusinessInfo(Map<String,Object> map);
    
    /**
     * 新增或者修改生产订单行项目核销信息
     * @param matList 物料信息
     */
    void insertOrUpdateMoItemHxInfo(@Param("matList")List<Map<String,Object>> matList);
    /**
     * 新增或者修改生产订单组件核销信息
     * @param matList 物料信息
     */
    void insertOrUpdateMoComponentHxInfo(@Param("matList")List<Map<String,Object>> matList);
    
    /**
     * 根据ID查询生产订单行项目核销信息
     * @param hxMoItemId
     * @return 
     */
    Map<String,Object> getHxMoItemById(@Param("hxMoItemId")String hxMoItemId);
    
    /**
     * 根据ID查询生产订单组件核销信息
     * @param hxMoComponentId
     * @return 
     */
    Map<String,Object> getHxMoComponentById(@Param("hxMoComponentId")String hxMoComponentId);
    /**
     * 更新SAP采购订单组件表投料数量
     * @param matList
     */
	void updateSapMoComponentTLInfo(List<Map<String, Object>> matList);
	/**
	 * 获取物料信息，物料库存信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getMatListByMATNR(Map<String, Object> params);
	/**
	 * 更新调拨核销表- WMS_HX_TO，移动类型对应的数量XF303及剩余核销数量HX_QTY_XF
	 * @param postMatList
	 */
	void saveHXTO(List<Map<String, Object>> postMatList);
	/**
	 * 查询交货单是否存在未删除的需求号
	 * @param OUT_NO
	 * @return
	 */
	int getRequirementBySapOutNo(@Param("SAP_OUT_NO")String OUT_NO);
}
