package com.byd.wms.business.modules.cswlms.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年12月29日 下午5:35:29 
 * 类说明 
 */
public interface DisPatchingJISBillPickingService {
	public List<Map<String, Object>> selectDispatchingJISBillPicking(
			Map<String, Object> params) ;
	public List<Map<String, Object>> selectAssemblySortType(
			Map<String, Object> params);
	public List<Map<String,Object>> selectAssemblyLogistics(Map<String,Object> params);
	
	public List<Map<String,Object>> selectDispatchingJISBillPickingByDispatcingNo(Map<String,Object> params);
	
	public void JISPicking(List<Map<String,Object>> paramlist);
	
	public void FeiJISPicking(List<Map<String,Object>> paramlist);
	
	public List<Map<String, Object>> selectDispatchingFeiJISBillPicking(
			Map<String, Object> params) ;
	
	public void printJIS(List<Map<String,Object>> paramlist);
	
	public void printFeiJIS(List<Map<String,Object>> paramlist);
	/**
	 * 拣配确认查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectDispatchingQueRen(Map<String,Object> params);
	
	/**
	 * 拣配确认验证  库存
	 * @param params
	 * @return
	 */
	public void checkDispatchingQueRen(List<Map<String,Object>> params);
	/**
	 * 拣配确认 更新对应表数据
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	public void updateQueRen(List<Map<String,Object>> params) throws ParseException;
	
	/**
	 * 拣配确认 更新对应component表的PICK_RECORD_NO,PICK_RECORD_USER_ID
	 * @param params
	 * @return
	 */
	public void updatePickRecordNo(List<Map<String,Object>> params);
	/**
	 * 物流需求交接 更新对应表数据
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	public void updateJiaojie(List<Map<String,Object>> params) throws ParseException;
	
	public List<Map<String, Object>> selectDispatchingBillPickingByPrint(
			Map<String, Object> params) ;
	
	/**
	 * 拣配单查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectDispatchingByfabu(Map<String,Object> params);
	/**
	 * 物流需求交接 过账
	 * @param params
	 * @return
	 */
	public Map<String, Object> sapJiaojie(List<Map<String,Object>> params);
	
	public List<Map<String,Object>> selectDispatchingItem(Map<String, Object> params);
	
	public List<Map<String,Object>> selectDispatchingHeader(Map<String, Object> params);
	
	/**
	 * 重新发布 更新对应表数据
	 * @param params
	 * @return
	 */
	public void updateFabu(List<Map<String,Object>> params);
	
	public String dispatchingFabu(List<Map<String,Object>> params) throws ParseException;
	/**
	 * 需求拆分
	 * @param param
	 * @return
	 */
	public String dispatchingChaifen(Map<String, Object> param) throws ParseException;
	
	public List<Map<String,Object>> selectToWlmsException(Map<String, Object> params);
	
	public void insertToWlmsException(Map<String, Object> params);
	
	/**
	 * 立库接口
	 */
	public String outStockLiKu(List<Map<String,Object>> params,String st);
	
	public Map<String, Object> pickRecordNoCount(String barcode);
	
	public Map<String,Object> dispatchingHandoverService(Map<String, Object> params) throws Exception;
	
	public Map<String,Object> updateQueRenService(Map<String, Object> params) throws ParseException;
}
