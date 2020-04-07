package com.byd.wms.business.modules.in.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.in.entity.SCMDeliveryEntity;

public interface SCMDeliveryDao extends BaseMapper<SCMDeliveryEntity>{
	
	public List<Map<String, Object>> queryWin00anBySCM(Map<String, Object> params);
	public List<Map<String, Object>> queryWin00an00BySCM(Map<String, Object> params);
	public List<Map<String, Object>> queryWin00skBySCM(Map<String, Object> params);
	public int updateTPO(List<Map<String, Object>> params);
	public List<Map<String, Object>> querytpo(Map<String, Object> params);
	
	public int updateTDELIVERYDETAIL(List<Map<String,Object>> params);//对应包装箱信息表
	public int updateTDELIVERYROWITEM(List<Map<String,Object>> params);//对应wmin00an
	public int updateTDELIVERYNOTE(List<Map<String,Object>> params);//对应wmin00an00
	
	public List<Map<String, Object>> queryHeadBySCM(Map<String, Object> params);
	public List<Map<String, Object>> queryItemBySCM(Map<String, Object> params);
	
	public List<Map<String, Object>> getMatBarcodeList(Map<String, Object> params);
	public List<Map<String, Object>> getAllMatBarcodeList(Map<String, Object> params);
	public List<Map<String, Object>> getScmBarCodeInfo(Map<String, Object> params);
	public List<Map<String, Object>> getLabelsBySapMetDocNo(Map<String, Object> params);
	public List<Map<String, Object>> getLabelsInfoBySapMetDocNo(Map<String, Object> params);
	
	int queryHeadBySCMCount(Map<String,Object> param);
	int queryItemBySCMCount(Map<String,Object> param);
	
	int updateHEAD(List<Map<String, Object>> params);
	int updateITEM(List<Map<String, Object>> params);
	int updateDETAIL(List<Map<String, Object>> params);
	
	public int updateTPO_onWayAmount(List<Map<String, Object>> params);
	public List<Map<String, Object>> queryAllItemBySCM(List<Map<String, Object>> params);
	
}
