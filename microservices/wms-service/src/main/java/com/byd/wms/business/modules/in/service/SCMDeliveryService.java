package com.byd.wms.business.modules.in.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.wms.business.modules.in.entity.SCMDeliveryEntity;
import com.byd.wms.business.modules.in.entity.Wmin00an00Entity;
import com.byd.wms.business.modules.in.entity.Wmin00anEntity;
import com.byd.wms.business.modules.in.entity.Wmin00skEntity;

public interface SCMDeliveryService extends IService<SCMDeliveryEntity>{
	
	/**
	 * 查询SCM送货单信息
	 * @param params
	 * @return
	 */
	//List<Map<String, Object>> querySCMDelivery(Map<String, Object> params);
	Map<String, Object> querySCMDelivery(Map<String, Object> params);
	
	Map<String, Object> updateTPO(List<Map<String,Object>> params);
	
	 List<Map<String, Object>> querytpo(Map<String, Object> params);
	
	 Map<String, Object> updateTDELIVERYDETAIL(List<Map<String,Object>> params);//对应包装箱信息表
	 Map<String, Object> updateTDELIVERYROWITEM(List<Map<String,Object>> params);//对应wmin00an
	 Map<String, Object> updateTDELIVERYNOTE(List<Map<String,Object>> params);//对应wmin00an00
	 
	 public List<Map<String, Object>> getMatBarcodeList(Map<String, Object> params);
	 public List<Map<String, Object>> getAllMatBarcodeList(Map<String, Object> params);
	 public List<Map<String, Object>> getScmBarCodeInfo(Map<String, Object> params);
	 public List<Map<String, Object>> getAllMatBarcodeListBySapMatDocNo(Map<String, Object> params);
}
