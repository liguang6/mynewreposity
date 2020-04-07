package com.byd.sap.modules.job.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.byd.sap.modules.job.entity.ScheduleJobLogEntity;
import com.byd.sap.modules.job.entity.SapMaterialEntity;
import com.byd.sap.modules.job.entity.SapMaterialUnitEntity;
import com.byd.sap.modules.job.entity.SapPoAccountEntity;
import com.byd.sap.modules.job.entity.SapPoHeadEntity;
import com.byd.sap.modules.job.entity.SapPoItemEntity;
@Mapper
public interface ISapSyncDao {
	public String get_VENDOR_id(Map<String,Object> queryMap);
	public int update_VENDOR(Map<String,Object> queryMap);
	public int insert_VENDOR(Map<String,Object> queryMap);
	public int addOrUpdateCustomerInfo(@Param("list") List<Map<String,Object>> list);
	
	public String get_SapMaterial_id(Map<String,Object> queryMap);
	public int update_SapMaterial(SapMaterialEntity sapMaterialEntity);
	public int insert_SapMaterial(SapMaterialEntity sapMaterialEntity);
	
	public String get_SapMaterialUnit_id(Map<String,Object> queryMap);
	public int update_SapMaterialUnit(SapMaterialUnitEntity sapMaterialUnitEntity);
	public int insert_SapMaterialUnit(SapMaterialUnitEntity sapMaterialUnitEntity);
	
	public int insert_ScheduleJobLog(ScheduleJobLogEntity scheduleJobLog);
	public List<Map<String,String>> getWmsSapCompanyList(Map<String,String> map);
	
	public int insert_SapPoAccount(SapPoAccountEntity sappoaccount);
	public String get_SAPPoAccount_id(Map<String,Object> queryMap);
	public int update_SapPoAccount(SapPoAccountEntity sappoaccount);
	
	public int insert_SapPoHead(SapPoHeadEntity sappohead);
	public String get_SAPPoHead_id(Map<String,Object> queryMap);
	public int update_SAPPoHead(SapPoHeadEntity sappohead);
	
	public int insert_SapPoItem(SapPoItemEntity sappoitem);
	public String get_SAPPoItem_id(Map<String,Object> queryMap);
	public int update_SAPPoItem(SapPoItemEntity sappoitem);
	public int update_SAPPoItem_LMEIN(SapPoItemEntity sappoitem);
	
	public int insert_SapPoComponent(Map<String,Object> queryMap);
	public String get_SapPoComponent_id(Map<String,Object> queryMap);
	public int update_SapPoComponent(Map<String,Object> queryMap);
	
	public int insert_SapMoItem(Map<String,Object> queryMap);
	public int insertOrUpdateSapMoItem(@Param("matList")List<Map<String,Object>> matList);
	/**
	 * 
	 * @param AUFNR 生产订单号
	 * @param CO_POSNR 行项目
	 * @return
	 */
	public String get_SapMoItem_id(@Param("AUFNR")String AUFNR,@Param("CO_POSNR")String CO_POSNR);
	public int update_SapMoItem(Map<String,Object> queryMap);
	
	public int insert_SapMoHead(Map<String,Object> queryMap);
	public String get_SapMoHead_id(String AUFNR);
	public int update_SapMoHead(Map<String,Object> queryMap);
	
	public int insert_SapCustomer(Map<String,Object> queryMap);
	public String get_SapCustomer_id(Map<String,Object> queryMap);
	public int update_SapCustomer(Map<String,Object> queryMap);
	
	public int insert_SapMoComponent(Map<String,Object> queryMap);
	public String get_SapMoComponent_id(Map<String,Object> queryMap);
	public int update_SapMoComponent(Map<String,Object> queryMap);
	public int insertOrUpdateSapMoComponent(@Param("matList")List<Map<String,Object>> matList);
	
	public Map<String,String> getSapUserByWerks(String WERKS);
}
