package com.byd.wms.business.modules.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.dao.WmsAccountKPODao;
import com.byd.wms.business.modules.account.service.WmsAccountKPOService;
import com.byd.wms.business.modules.common.service.CommonService;

/**
 * 跨工厂收货账务处理 service
 * @author develop01
 */
@Service("wmsAccountKPOService")
public class WmsAccountKPOServiceImpl implements WmsAccountKPOService {
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsAccountKPODao wmsAccountKPODao;
    
    
	/**
	 * 根据工厂、仓库库、采购订单、料号、供应商代码、收货进仓日期查询跨工厂收货进仓产生的105DS WMS凭证信息
	 * @param params PO_WERKS： 采购订单工厂   WERKS：收货工厂  WH_NUMBER：收货仓库号  LIFNR：供应商代码  收货进仓日期：CREATE_DATE_S-CREATE_DATE_E
	 * BEDNR：需求跟踪号  PO_NO：采购订单： 料号：MATNR 
	 * @return 跨工厂收货进仓产生的105DS WMS凭证信息
	 */
    @Override
    public List<Map<String, Object>> getKPOWmsDocInfo(Map<String,Object> params){
    	List<Map<String, Object>> list = wmsAccountKPODao.getKPOWmsDocInfo(params);
    	return list;
    }
	
    /**
	 * 跨工厂收货账务处理-101收货
	 * 1、根据105DS凭证数据，生成101DS WMS凭证，并且过账SAP
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R postGR_101(Map<String, Object> params) {
		StringBuilder msg=new StringBuilder("跨工厂收货账务（101）处理成功：");
		List<Map<String,Object>> matList = (List<Map<String,Object>>) params.get("matList");
		//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		Map<String,Object> cdmap=new HashMap<String,Object>();
		String BUSINESS_NAME = "58";//跨工厂收货过账
		String BUSINESS_TYPE = "17";//17 WMS凭证
		String BUSINESS_CLASS = "10";//10 账务处理
		cdmap.put("BUSINESS_NAME", BUSINESS_NAME);
		cdmap.put("BUSINESS_TYPE", BUSINESS_TYPE);
		cdmap.put("BUSINESS_CLASS", BUSINESS_CLASS);
		cdmap.put("WERKS", params.get("WERKS"));
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn = commonService.getMoveAndSyn(cdmap);
		if(moveSyn == null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("工厂"+params.get("WERKS")+"未配置跨工厂收货过账业务类型！");
		}
		String SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("收货工厂"+params.get("WERKS")+"未配置跨工厂收货过账业务类型的SAP过账移动类型！");
		}
		params.put("BUSINESS_NAME", BUSINESS_NAME);
		params.put("BUSINESS_TYPE", BUSINESS_TYPE);
		params.put("BUSINESS_CLASS", BUSINESS_CLASS);
		
		params.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		/**
		 * 产生101DS WMS凭证记录 
		 * 
		 */
		String WMS_NO = commonService.saveWMSDoc(params,matList);
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		
		//将105DSWMS凭证的可冲销、可取消标识修改为X，即已经做了跨工厂收货账务处理的105DS凭证不允许再冲销、取消
		wmsAccountKPODao.updateWmsDocCancelFlag(matList);
		
		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		String SAP_NO = commonService.doSapPost(params);
		
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}

		return R.ok(msg.toString());
	}
	
	
}