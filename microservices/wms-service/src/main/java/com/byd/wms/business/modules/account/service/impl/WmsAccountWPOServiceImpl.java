package com.byd.wms.business.modules.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.dao.WmsAccountWPODao;
import com.byd.wms.business.modules.account.service.WmsAccountWPOService;
import com.byd.wms.business.modules.common.service.CommonService;

/**
 * 无PO收货账务处理 service
 * @author develop01
 */
@Service("wmsAccountWPOService")
public class WmsAccountWPOServiceImpl implements WmsAccountWPOService {
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsAccountWPODao wmsAccountWPODao;
    
    
	/**
	 * 根据工厂、仓库库、供应商代码、收货日期查询无PO收货产生的收货单信息
	 * @param params WERKS：工厂  WH_NUMBER：仓库号  LIFNR：供应商代码  收货日期：CREATE_DATE_S-CREATE_DATE_E
	 * BEDNR：需求跟踪号
	 * @return 无PO收货单信息
	 */
    @Override
    public List<Map<String, Object>> getWPOReceiptInfo(Map<String,Object> params){
    	List<Map<String, Object>> list = wmsAccountWPODao.getWPOReceiptInfo(params);
    	return list;
    }
    
    @Override
    public List<Map<String, Object>> getPoItemInfo(Map<String,Object> params){
    	List<Map<String, Object>> list = wmsAccountWPODao.getPoItemInfo(params);
    	return list;
    }
    
	
    /**
	 * 无PO收货账务处理
	 * 1、更新收货单的采购订单、行项目号，将收货单行项目的WMS业务类型由05 无PO->0502 无PO-补PO（解决部分物料还在质检尚未进仓，后续进仓过账问题）
	 * 2 、更新收货单关联的103W、105W WMS凭证对应的PO及行项目号，行项目的WMS业务类型由05 无PO->0502 无PO-补PO（解决部分物料还在质检尚未进仓，后续进仓过账问题）
	 * 3、更新进仓单行项目，将行项目状态为(00 已创建 01 部分进仓)的进仓单行项目的WMS业务类型为 由 05 无PO->0502 无PO-补PO（解决部分已创建进仓单，未进仓交接，后续进仓交接过账问题）
	 * 4 、已产生的105W WMS凭证执行SAP过账
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R postGI_WPO(Map<String, Object> params) {
		StringBuilder msg=new StringBuilder("无PO收货账务处理成功，相关收货单已关联PO信息：");
		List<Map<String,Object>> matList = (List<Map<String,Object>>) params.get("matList");
		//1、更新收货单的采购订单、行项目号，将收货单行项目的WMS业务类型由05 无PO->0502 无PO-补PO
		wmsAccountWPODao.updateReceiptPoInfo(matList);
		
		//2 、更新收货单关联的103W、105W WMS凭证对应的PO及行项目号，行项目的WMS业务类型由05 无PO->0502 无PO-补PO（解决部分物料还在质检尚未进仓，后续进仓过账问题）
		wmsAccountWPODao.updateWmsDocPoInfo(matList);
		wmsAccountWPODao.updateWmsDocBusinessType(matList);
		
		//3、更新进仓单行项目，将行项目状态为(00 已创建 01 部分进仓)的进仓单行项目的WMS业务类型为 由 05 无PO->0502 无PO-补PO（解决部分已创建进仓单，未进仓交接，后续进仓交接过账问题）
		
		wmsAccountWPODao.updateInboundItemBusinessType(matList);
		
		//4 、已产生的105W WMS凭证执行SAP过账
		//获取105W WMS凭证信息
		List<Map<String,Object>> wmsDocList = wmsAccountWPODao.getWpoWmsDocInfo(matList);
		if(wmsDocList.size()>0) {
			//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
			Map<String,Object> cdmap=new HashMap<String,Object>();
			cdmap.put("BUSINESS_NAME", wmsDocList.get(0).get("BUSINESS_NAME"));
			cdmap.put("BUSINESS_TYPE", wmsDocList.get(0).get("BUSINESS_TYPE"));
			cdmap.put("BUSINESS_CLASS", wmsDocList.get(0).get("BUSINESS_CLASS"));
			cdmap.put("WERKS", params.get("WERKS"));
			cdmap.put("SOBKZ", wmsDocList.get(0).get("SOBKZ"));
			Map<String,Object> moveSyn = commonService.getMoveAndSyn(cdmap);
			for (Map<String, Object> map : wmsDocList) {
				String QTY_WMS = map.get("QTY_WMS").toString();
				String QTY_CANCEL = map.get("QTY_CANCEL")==null?"0":map.get("QTY_CANCEL").toString();
				Double QTY_SAP = Double.valueOf(QTY_WMS) - Double.valueOf(QTY_CANCEL);
				map.put("QTY_SAP", QTY_SAP);
				map.put("SAP_MOVE_TYPE", moveSyn.get("SAP_MOVE_TYPE"));
			}
			//4、更新105W WMS凭证的 QTY_SAP数量、SAP_MOVE_TYPE
			wmsAccountWPODao.updateWmsDocSapMoveType(wmsDocList);
			
			String BUSINESS_NAME = wmsDocList.get(0).get("BUSINESS_NAME").toString();//业务类型描述
			String BUSINESS_TYPE = wmsDocList.get(0).get("BUSINESS_TYPE").toString();//WMS业务类型
			String BUSINESS_CLASS = "02";
			params.put("BUSINESS_NAME", BUSINESS_NAME);
			params.put("BUSINESS_TYPE", BUSINESS_TYPE);
			params.put("BUSINESS_CLASS", BUSINESS_CLASS);
			
			params.put("matList", wmsDocList);
			/**
			 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
			 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
			 * SAP过账失败回滚
			 */
			
			String SAP_NO = commonService.doSapPost(params);
			
			
			if(StringUtils.isNotBlank(SAP_NO)) {
				msg.append(" 已进仓物料过账产生的SAP凭证:");
				msg.append(SAP_NO);
			}
		}else {
			msg.append(" 无进仓数据，未过账！");
		}

		return R.ok(msg.toString());
	}
	
	
}