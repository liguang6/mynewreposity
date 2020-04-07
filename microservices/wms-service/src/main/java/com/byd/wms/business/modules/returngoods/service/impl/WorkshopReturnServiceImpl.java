package com.byd.wms.business.modules.returngoods.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.returngoods.dao.WorkshopReturnDao;
import com.byd.wms.business.modules.returngoods.service.WorkshopReturnService;

@Service("workshopReturnService")
public class WorkshopReturnServiceImpl implements WorkshopReturnService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WorkshopReturnDao WorkshopReturnDao;
	@Autowired
	WmsCDocNoService wmscDocNoService;
    @Autowired
    WmsCTxtService wmsCTxtService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WarehouseTasksService warehouseTasksService;
    @Autowired
    private UserUtils userUtils;

	@Override
	public List<Map<String, Object>> getBusinessNameListByWerks(Map<String, Object> params) {
		return WorkshopReturnDao.getBusinessNameListByWerks(params);
	}

	@Override
	public List<Map<String, Object>> getSapVendorInfo(Map<String, Object> params){
		return WorkshopReturnDao.getSapVendorInfo(params);
	}

	@Override
	public List<Map<String, Object>> getWorkshopReturnData(Map<String, Object> params){
		logger.info("-->getWorkshopReturnData BUSINESS_NAME=" + params.get("BUSINESS_NAME").toString());
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		String ORDERS = params.get("ORDERS").toString();
		String POORDERS = params.get("PO_ORDER").toString();
		String MATNRS = params.get("MATNRS").toString();
		if("".equals(MATNRS)) {
			params.put("MATNRS", "");
		}else {
			MATNRS = MATNRS.replaceAll(",", "','");
			MATNRS = "('" + MATNRS + "')";
			params.put("MATNRS", MATNRS);
		}

		if("35".equals(BUSINESS_NAME)) {
			ORDERS = ORDERS.replaceAll(",", "','");
			ORDERS = "('" + ORDERS + "')";
			logger.info("-->" + ORDERS);
			params.put("AUFNRS", ORDERS);

			result = WorkshopReturnDao.getWorkshopReturnData(params);
			//分情况计算可退数量KT_QTY
			for(int i=0;i<result.size();i++) {
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString())
						- Float.valueOf(result.get(i).get("RETURN_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString())
						- Float.valueOf(result.get(i).get("RETURN_QTY").toString()));
			}
		}else if("36".equals(BUSINESS_NAME)) {
			ORDERS = ORDERS.replaceAll(",", "','");
			ORDERS = "('" + ORDERS + "')";
			logger.info("-->" + ORDERS);
			params.put("AUFNRS", ORDERS);

			result = WorkshopReturnDao.getWorkshopReturnData36(params);
			//分情况计算可退数量KT_QTY
			for(int i=0;i<result.size();i++) {
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString())
						- Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString())
						- Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("37".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getWorkshopReturnData37(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("38".equals(BUSINESS_NAME)) {
			POORDERS = POORDERS.replaceAll(",", "','");
			POORDERS = "('" + POORDERS + "')";
			logger.info("-->" + POORDERS);
			params.put("POORDERS", POORDERS);
			result = WorkshopReturnDao.getWorkshopReturnData38(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("39".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getWorkshopReturnData39(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("40".equals(BUSINESS_NAME)) {
			if("".equals(ORDERS)) {
				params.put("AUFNRS", "");
			}else {
				ORDERS = ORDERS.replaceAll(",", "','");
				ORDERS = "('" + ORDERS + "')";
				params.put("AUFNRS", ORDERS);
			}
			result = WorkshopReturnDao.getWorkshopReturnData40(params);

			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",(float)Math.round((Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()))*100)/100);
				result.get(i).put("QTY1",(float)Math.round((Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()))*100)/100);
			}
		}else if("68".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getWorkshopReturnData68(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("69".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getWorkshopReturnData69(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("70".equals(BUSINESS_NAME)) {
			String SAP_ORDERS = params.get("SAP_ORDERS").toString();

			SAP_ORDERS = SAP_ORDERS.replaceAll(",", "','");
			SAP_ORDERS = "('" + SAP_ORDERS + "')";
			params.put("SAP_ORDERS", SAP_ORDERS);
			result = WorkshopReturnDao.getWorkshopReturnData70(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("71".equals(BUSINESS_NAME)) {
			String SAP_ORDERS = params.get("SAP_ORDERS").toString();
			SAP_ORDERS = SAP_ORDERS.replaceAll(",", "','");
			SAP_ORDERS = "('" + SAP_ORDERS + "')";
			params.put("SAP_ORDERS", SAP_ORDERS);
			result = WorkshopReturnDao.getWorkshopReturnData71(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("72".equals(BUSINESS_NAME)) {
			String PO_ORDER = params.get("PO_ORDER").toString();
			PO_ORDER = PO_ORDER.replaceAll(",", "','");
			PO_ORDER = "('" + PO_ORDER + "')";
			params.put("PO_ORDER", PO_ORDER);
			result = WorkshopReturnDao.getWorkshopReturnData72(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("46".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getWorkshopReturnData46(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getWorkshopReturnConfirmData(Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if("1".equals(params.get("ORIGINALBATCH").toString())) {
			/*
			 * 14.3.3.2	当查询条件的“是否参考领料退料”选择“是”时，系统首先根据“工厂+仓库号+料号+生产订单+生产订单行项目号”关联查询【需求抬头表】、
			 * 【需求行项目表】和【拣配下架表】获取符合条件的领料数据，
			 * 再根据符合条件的领料数据关联查询【退货单抬头表】和【退货单行项目表】确认符合条件的领料批次是否已被退料了
			 * （符合条件的领料数据-已被退料占用的数据即为本次允许匹配的退料数据，查询退料占用数据时含创建状态和完成状态）。
			取值【需求抬头表】、【需求行项目表时】，取相对应的“BUSINESS_TYPE，BUSINESS_NAME，行项目状态=04、05、06，删除标识≠X“范围内的数据，数量取“实领/发数量(交接) ”字段值。
			取值【拣配下架表】时，取相对应的“BUSINESS_TYPE，BUSINESS_NAME，行项目状态=02、03，删除标识≠X”范围内的数据，数量取“ 非限制交接过账数量”字段值。
			匹配领料数据时优先匹配“特殊库存类型=K”的数据，如K类数据不满足再匹配“特殊库存类型=Z”的数据。
			如符合条件的数据有多条，则按照批次“后进的先退”的原则推荐符合条件的数据。
			最后确定退料批次后，如针对同一“料号+行项目”的退料数据有多条，则需要在退料单行项目表中新插入新的行项目数据，记录对应的退料批次。
			 * */
			List<Map<String, Object>> mat_infos = new ArrayList<Map<String, Object>>();
			mat_infos = WorkshopReturnDao.getWorkshopReturnMatInfoList(params);
			String bussiness_name = mat_infos.get(0).get("BUSINESS_NAME").toString();
			logger.info("-->getWorkshopReturnConfirmData bussiness_name : " + bussiness_name);
			for(int i=0;i<mat_infos.size();i++) {
				logger.info("-->" + mat_infos.get(i).get("MATNR") + "|" + mat_infos.get(i).get("MO_NO"));
				float TOTAL_RETURN_QTY =  Float.valueOf(mat_infos.get(i).get("TOTAL_RETURN_QTY").toString());
				Map<String, Object> par = new HashMap <String, Object>();
				par.put("RETURN_NO", params.get("RETURN_NO"));
				par.put("RETURN_ITEM_NO", mat_infos.get(i).get("RETURN_ITEM_NO").toString());
				par.put("TOTAL_RETURN_QTY", mat_infos.get(i).get("TOTAL_RETURN_QTY").toString());
				par.put("WERKS", (mat_infos.get(i).get("WERKS")!=null)?mat_infos.get(i).get("WERKS").toString():"");
				par.put("F_WERKS", (mat_infos.get(i).get("F_WERKS")!=null)?mat_infos.get(i).get("F_WERKS").toString():"");
				par.put("WH_NUMBER", (mat_infos.get(i).get("WH_NUMBER")!=null)?mat_infos.get(i).get("WH_NUMBER").toString():"");
				par.put("MATNR", mat_infos.get(i).get("MATNR").toString());
				par.put("SOBKZ", (mat_infos.get(i).get("SOBKZ")!=null)?mat_infos.get(i).get("SOBKZ").toString():"");
				par.put("IO_NO", (mat_infos.get(i).get("IO_NO")!=null)?mat_infos.get(i).get("IO_NO").toString():"");
				par.put("LGORT", (mat_infos.get(i).get("LGORT")!=null)?mat_infos.get(i).get("LGORT").toString():"");
				par.put("LIFNR", (mat_infos.get(i).get("LIFNR")!=null)?mat_infos.get(i).get("LIFNR").toString():"");
				par.put("WBS", (mat_infos.get(i).get("WBS")!=null)?mat_infos.get(i).get("WBS").toString():"");
				par.put("SAP_OUT_NO", (mat_infos.get(i).get("SAP_OUT_NO")!=null)?mat_infos.get(i).get("SAP_OUT_NO").toString():"");
				par.put("SAP_OUT_ITEM_NO", (mat_infos.get(i).get("SAP_OUT_ITEM_NO")!=null)?mat_infos.get(i).get("SAP_OUT_ITEM_NO").toString():"");
				par.put("MO_NO", (mat_infos.get(i).get("MO_NO")!=null)?mat_infos.get(i).get("MO_NO").toString():"");
				par.put("MO_ITEM_NO", (mat_infos.get(i).get("MO_ITEM_NO")!=null)?mat_infos.get(i).get("MO_ITEM_NO").toString():"");
				par.put("PO_NO", (mat_infos.get(i).get("PO_NO")!=null)?mat_infos.get(i).get("PO_NO").toString():"");
				par.put("PO_ITEM_NO", (mat_infos.get(i).get("PO_ITEM_NO")!=null)?mat_infos.get(i).get("PO_ITEM_NO").toString():"");
				par.put("RSNUM", (mat_infos.get(i).get("RSNUM")!=null)?mat_infos.get(i).get("RSNUM").toString():"");
				par.put("RSPOS", (mat_infos.get(i).get("RSPOS")!=null)?mat_infos.get(i).get("RSPOS").toString():"");
				par.put("BUSINESS_NAME", mat_infos.get(i).get("BUSINESS_NAME").toString());
				par.put("BUSINESS_TYPE", mat_infos.get(i).get("BUSINESS_TYPE").toString());
				par.put("COST_CENTER", (mat_infos.get(i).get("COST_CENTER")!=null)?mat_infos.get(i).get("COST_CENTER").toString():"");
				par.put("REQUIREMENT_NO", (mat_infos.get(i).get("REQUIREMENT_NO")!=null)?mat_infos.get(i).get("REQUIREMENT_NO").toString():"");
				par.put("REQUIREMENT_ITEM_NO", (mat_infos.get(i).get("REQUIREMENT_ITEM_NO")!=null)?mat_infos.get(i).get("REQUIREMENT_ITEM_NO").toString():"");
				List<Map<String, Object>> pick_list = new ArrayList<Map<String, Object>>();

				pick_list = WorkshopReturnDao.getWorkshopReturnPickingData(par);

				float PICK_TOTAL_NUM = 0;
				for(int j=0;j<pick_list.size();j++) {
					if(PICK_TOTAL_NUM < TOTAL_RETURN_QTY) {
						logger.info("-->" + pick_list.get(j).get("BATCH") + "|" + pick_list.get(j).get("RETURN_QTY"));
						if(Double.valueOf(pick_list.get(j).get("RETURN_QTY").toString()) >= TOTAL_RETURN_QTY-PICK_TOTAL_NUM) {
							pick_list.get(j).put("RETURN_QTY", TOTAL_RETURN_QTY-PICK_TOTAL_NUM);
							pick_list.get(j).put("QTY1", TOTAL_RETURN_QTY-PICK_TOTAL_NUM);
							result.add(pick_list.get(j));
						}else {
							pick_list.get(j).put("RETURN_QTY", Integer.valueOf(pick_list.get(j).get("RETURN_QTY").toString()));
							pick_list.get(j).put("QTY1", Integer.valueOf(pick_list.get(j).get("RETURN_QTY").toString()));
							result.add(pick_list.get(j));
						}
					}
					PICK_TOTAL_NUM += Float.valueOf((pick_list.get(j).get("RETURN_QTY")==null)?"0":pick_list.get(j).get("RETURN_QTY").toString());
				}
			}
			if(result.size() == 0) {
				throw new RuntimeException("没有查到任何数据！");
			}

			//行文本：根据行文本配置规则自动生成，允许修改。
			Map<String,String>txtMap=new HashMap<String,String>();
	    	txtMap.put("WERKS", params.get("WERKS").toString());
	    	txtMap.put("BUSINESS_NAME",result.get(0).get("BUSINESS_NAME").toString());
	    	txtMap.put("FULL_NAME", currentUser.get("USERNAME").toString());
	    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
	    	for(int i=0;i<result.size();i++) {
	    		if(!"success".equals(txt.get("msg").toString())) {
					throw new RuntimeException(txt.get("msg").toString());
				}else {
					result.get(i).put("ITEM_TEXT", txt.get("txtruleitem"));
				}
			}
		}else {
			result = WorkshopReturnDao.getWorkshopReturnConfirmData(params);
			/*批次：根据行项目的日期按照批次生成规则自动生成退料批次。[需讨论取退料日期是取系统操作的当前日期还是取抬头位置用户输入的凭证日期或过账日期。
			方案一：取过账日期是否更合适些？用户如果需要修改的话可以改，如果取当前日期的话就没办法改了，但是如果过账日期不需要改但是用户又需要生成特定日期的批次就满足不了了。
			方案二：是否可以考虑在列表中增加一个日期的字段，默认取当前日期，允许修改。
			---2018-12-11讨论结论：列表行项目增加日期字段。]*/
			List<Map<String, Object>> condMapList = new ArrayList<Map<String, Object>>();

			for(int i=0;i<result.size();i++) {
				Map<String, Object> condMap=new HashMap<String,Object>();
				condMap.put("WERKS", result.get(i).get("WERKS").toString());
				condMap.put("ASNNO", result.get(i).get("ASNNO")==null?"":result.get(i).get("ASNNO").toString());
				condMap.put("PO_NO", result.get(i).get("PO_NO")==null?"":result.get(i).get("PO_NO").toString());
				condMap.put("RECEIPT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
				String DELIVERY_DATE=result.get(i).get("DELIVERY_DATE")==null?"":result.get(i).get("DELIVERY_DATE").toString();
				String PRODUCT_DATE=result.get(i).get("PRODUCT_DATE")==null?"":result.get(i).get("PRODUCT_DATE").toString();
				String EFFECT_DATE=result.get(i).get("EFFECT_DATE")==null?"":result.get(i).get("EFFECT_DATE").toString();
				String MATNR =result.get(i).get("MATNR")==null?"":result.get(i).get("MATNR").toString();
				String LIFNR=result.get(i).get("LIFNR")==null?"":result.get(i).get("LIFNR").toString();
				String DANGER_FLAG=result.get(i).get("DANGER_FLAG")==null?"0":result.get(i).get("DANGER_FLAG").toString();
				String LGORT=result.get(i).get("LGORT")==null?"":result.get(i).get("LGORT").toString();
				condMap.put("DELIVERY_DATE", DELIVERY_DATE);
				condMap.put("PRODUCT_DATE", PRODUCT_DATE);
				condMap.put("EFFECT_DATE", EFFECT_DATE);
				condMap.put("MATNR", MATNR);
				condMap.put("LIFNR", LIFNR);
				condMap.put("DANGER_FLAG", DANGER_FLAG);
				condMap.put("LGORT", LGORT);
				condMap.put("F_BATCH", "");
				condMap.put("BUSINESS_NAME", result.get(i).get("BUSINESS_NAME"));

				condMapList.add(condMap);
			}

			//2019-06-19 调整为退料交接后产生批次
			/*List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(condMapList);

			for(int i=0;i<result.size();i++) {
				if(!"success".equals(retmapList.get(0).get("MSG").toString())) {
					throw new IllegalArgumentException(retmapList.get(0).get("MSG").toString());
				}
				result.get(i).put("BATCH", retmapList.get(i).get("BATCH"));
			}*/


			//储位:调用推荐储位方法
			List<Map<String, Object>> condMapList2 = new ArrayList<Map<String, Object>>();
			for(int i=0;i<result.size();i++) {
				Map<String, Object> condMap=new HashMap<String,Object>();
				condMap.put("QTY_WMS", result.get(i).get("QTY_WMS"));
				condMap.put("WERKS", result.get(i).get("WERKS"));
				condMap.put("WH_NUMBER", result.get(i).get("WH_NUMBER"));
				condMap.put("MATNR", result.get(i).get("MATNR"));
				condMap.put("BATCH", result.get(i).get("BATCH"));
				condMap.put("LIFNR", result.get(i).get("LIFNR"));
				condMap.put("LGORT", result.get(i).get("LGORT"));
				condMap.put("SOBKZ", result.get(i).get("SOBKZ"));
				condMap.put("BIN_CODE", "AAAA");
				condMapList2.add(condMap);
			}
			List<Map<String,Object>> retmapList = warehouseTasksService.searchBinForPutaway(condMapList2);
			for(int i=0;i<result.size();i++) {
				result.get(i).put("BIN_CODE", retmapList.get(i).get("BIN_CODE"));
			}

			//行文本：根据行文本配置规则自动生成，允许修改。
			Map<String,String>txtMap=new HashMap<String,String>();
	    	txtMap.put("WERKS", params.get("WERKS").toString());
	    	txtMap.put("BUSINESS_NAME",result.get(0).get("BUSINESS_NAME").toString());
	    	txtMap.put("FULL_NAME", currentUser.get("USERNAME").toString());
	    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
	    	for(int i=0;i<result.size();i++) {
	    		if(!"success".equals(txt.get("msg").toString())) {
					throw new RuntimeException(txt.get("msg").toString());
				}else {
					result.get(i).put("ITEM_TEXT", txt.get("txtruleitem"));
				}
			}

		}

		return result;
	}

	@Override
	public List<Map<String,Object>> getMatBatch(Map<String, Object> params){
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());

		List<Map<String, Object>> condMapList = new ArrayList<Map<String, Object>>();
		for(int i=0;i<jarr.size();i++){
			Map<String, Object> condMap=new HashMap<String,Object>();
			condMap.put("WERKS", jarr.getJSONObject(i).getString("WERKS"));
			condMap.put("ASNNO", jarr.getJSONObject(i).getString("ASNNO")==null?"":jarr.getJSONObject(i).getString("ASNNO"));
			condMap.put("PO_NO", jarr.getJSONObject(i).getString("PO_NO")==null?"":jarr.getJSONObject(i).getString("PO_NO"));
			String RECEIPT_DATE=jarr.getJSONObject(i).getString("RECEIPT_DATE")==null?"":jarr.getJSONObject(i).getString("RECEIPT_DATE");
			condMap.put("RECEIPT_DATE", RECEIPT_DATE);
			String DELIVERY_DATE=jarr.getJSONObject(i).getString("DELIVERY_DATE")==null?"":jarr.getJSONObject(i).getString("DELIVERY_DATE");

			String PRODUCT_DATE = "";
			if(jarr.getJSONObject(i).getString("PRODUCT_DATE")==null){
				PRODUCT_DATE = jarr.getJSONObject(i).getString("RECEIPT_DATE")==null?"":jarr.getJSONObject(i).getString("RECEIPT_DATE");
			}else{
				PRODUCT_DATE = jarr.getJSONObject(i).getString("PRODUCT_DATE");
			}

			//String PRODUCT_DATE=jarr.getJSONObject(i).getString("PRODUCT_DATE")==null?"":jarr.getJSONObject(i).getString("PRODUCT_DATE");
			String EFFECT_DATE=jarr.getJSONObject(i).getString("EFFECT_DATE")==null?"":jarr.getJSONObject(i).getString("EFFECT_DATE");
			String MATNR =jarr.getJSONObject(i).getString("MATNR")==null?"":jarr.getJSONObject(i).getString("MATNR");
			String LIFNR=jarr.getJSONObject(i).getString("LIFNR")==null?"":jarr.getJSONObject(i).getString("LIFNR");
			String DANGER_FLAG=jarr.getJSONObject(i).getString("DANGER_FLAG")==null?"0":jarr.getJSONObject(i).getString("DANGER_FLAG");
			String LGORT=jarr.getJSONObject(i).getString("LGORT")==null?"":jarr.getJSONObject(i).getString("LGORT");
			condMap.put("DELIVERY_DATE", DELIVERY_DATE);
			condMap.put("PRODUCT_DATE", PRODUCT_DATE);
			condMap.put("EFFECT_DATE", EFFECT_DATE);
			condMap.put("MATNR", MATNR);
			condMap.put("LIFNR", LIFNR);
			condMap.put("DANGER_FLAG", DANGER_FLAG);
			condMap.put("LGORT", LGORT);
			condMap.put("F_BATCH", "");
			condMap.put("BUSINESS_NAME", jarr.getJSONObject(i).getString("BUSINESS_NAME"));
			condMapList.add(condMap);
		}
		List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(condMapList);
		if(!"success".equals(retmapList.get(0).get("MSG").toString())) {
			throw new RuntimeException(retmapList.get(0).get("MSG").toString());
		}
		//matList.get(i).put("BATCH", retmapList.get(i).get("BATCH"));
		return retmapList;
	}

	@Override
	public List<Map<String, Object>> getSapMoComponentDate(Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		if("35".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getSapMoComponentDate35(params);
			for(int i=0;i<result.size();i++) {
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString()));
			}
		}else if("36".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getSapMoComponentDate36(params);
			for(int i=0;i<result.size();i++) {
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString())
						- Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
				result.get(i).put("QTY1",Float.valueOf(result.get(i).get("TL_QTY").toString())
						- Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("37".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getSapMoComponentDate37(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("38".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getSapMoComponentDate38(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("39".equals(BUSINESS_NAME)) {
			result = WorkshopReturnDao.getSapMoComponentDate39(params);
			for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}
		}else if("40".equals(BUSINESS_NAME)) {	//线边仓退料
			result = WorkshopReturnDao.getSapMoComponentDate40(params);
			/*for(int i=0;i<result.size();i++) {	//可退数量：领料数量-已创单数量-已完成退货的数量+取消/冲销数量
				result.get(i).put("KT_QTY",Float.valueOf(result.get(i).get("TL_QTY").toString()) - Float.valueOf(result.get(i).get("RETURN_QTY").toString())
						- Float.valueOf(result.get(i).get("FINISH_QTY").toString())
						+ Float.valueOf(result.get(i).get("QX_QTY").toString()));
			}*/
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> getPlantInfoByWerks(Map<String, Object> params){
		return WorkshopReturnDao.getPlantInfoByWerks(params);
	}
	@Override
	public int getMoHeadHxQty(String AUFNR) {
		return WorkshopReturnDao.getMoHeadHxQty(AUFNR);
	}
	@Override
	public int getSapMoCount(Map<String, Object> params) {
		return WorkshopReturnDao.getSapMoCount(params);
	}
	@Override
	public int getSapPoCount(Map<String, Object> params) {
		return WorkshopReturnDao.getSapPoCount(params);
	}
	@Override
	public List<Map<String, Object>> getSapPoStatus(Map<String, Object> params){
		return WorkshopReturnDao.getSapPoStatus(params);
	}
	@Override
	public String getSapMoStatus(Map<String, Object> params) {
		return WorkshopReturnDao.getSapMoStatus(params);
	}

	@Override
	public String createWorkshopReturn(Map<String, Object> params) {
		String RETURN_NO = "";
		Map<String,Object> currentUser = userUtils.getUser();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//【退货单抬头表】
		Map<String, Object> params2 = new HashMap <String, Object>();
		params2.put("WERKS", params.get("WERKS").toString());
		params2.put("WMS_DOC_TYPE", "06");
		RETURN_NO = wmscDocNoService.getDocNo(params2).get("docno").toString();
		if(null == RETURN_NO) {
			return "-1";
		}
		String HEADER_TXT = "";
		Map<String, Object> headMap = new HashMap <String, Object>();
		headMap.put("RETURN_NO", RETURN_NO);
		headMap.put("RETURN_TYPE", "06");		//ModBy:YK190621 改为存BUSINESS_CLASS
		headMap.put("RETURN_STATUS", "00");
		headMap.put("F_WERKS", params.get("F_WERKS").toString());
		headMap.put("WERKS", params.get("WERKS").toString());
		headMap.put("F_WH_NUMBER", "");
		headMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
		headMap.put("LIFNR", "");
		headMap.put("LIKTX", "");
		headMap.put("HEADER_TXT", HEADER_TXT);
		headMap.put("IS_AUTO", "");
		headMap.put("DEL", "0");
		headMap.put("CREATOR", currentUser.get("USERNAME"));
		headMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		headMap.put("EDITOR", "");
		headMap.put("EDIT_DATE", "");
		WorkshopReturnDao.insertWmsOutReturnHead(headMap);
		//【退货单行项目表】
		int RETURN_ITEM_NO = 1;
		for(int j=0;j<jarr.size();j++){
			JSONObject outData=  jarr.getJSONObject(j);
			Map<String, Object> itemMap = new HashMap <String, Object>();
			Map<String, Object> itemDetailMap = new HashMap <String, Object>();
			itemMap.put("RETURN_NO",RETURN_NO);
			itemMap.put("RETURN_ITEM_NO",RETURN_ITEM_NO);
			itemMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME").toString());
			itemMap.put("BUSINESS_TYPE",params.get("BUSINESS_TYPE").toString());
			itemMap.put("WERKS",params.get("WERKS").toString());
			itemMap.put("WH_NUMBER",params.get("WH_NUMBER").toString());
			itemMap.put("F_LGORT",params.get("LGORT").toString());
			itemMap.put("MATNR",outData.getString("MATNR"));
			itemMap.put("MAKTX",outData.getString("MAKTX"));
			itemMap.put("UNIT",outData.getString("MEINS"));
			itemMap.put("TOTAL_RETURN_QTY",Float.valueOf(outData.getString("QTY1").trim()));
			itemMap.put("RETURN_PEOPLE",currentUser.get("USERNAME"));
			itemMap.put("RETURN_REASON_DESC",outData.getString("REASON"));
			itemMap.put("ITEM_STATUS","00");
			itemMap.put("MEMO",(outData.getString("MEMO") == null)?"":outData.getString("MEMO"));
			itemMap.put("RECEIPT_NO",(outData.getString("RECEIPT_NO") == null)?"":outData.getString("RECEIPT_NO"));
			itemMap.put("RECEIPT_ITEM_NO",(outData.getString("RECEIPT_ITEM_NO") == null)?"":outData.getString("RECEIPT_ITEM_NO"));
			itemMap.put("LIFNR",(outData.getString("LIFNR") == null)?"":outData.getString("LIFNR"));
			itemMap.put("LIKTX",(outData.getString("LIKTX") == null)?"":outData.getString("LIKTX"));
			String PO_NO = "";
			String PO_ITEM_NO = "";
			if("69".equals(params.get("BUSINESS_NAME").toString())) {
				PO_NO = (outData.getString("RSNUM") == null)?"":outData.getString("RSNUM");
				PO_ITEM_NO = (outData.getString("RSPOS") == null)?"":outData.getString("RSPOS");
			}else {
				PO_NO = (outData.getString("PO_NO") == null)?"":outData.getString("PO_NO");
				PO_ITEM_NO = (outData.getString("PO_ITEM_NO") == null)?"":outData.getString("PO_ITEM_NO");
			}
			itemMap.put("PO_NO",PO_NO);
			itemMap.put("PO_ITEM_NO",PO_ITEM_NO);
			itemMap.put("SAP_OUT_NO",(outData.getString("SAP_OUT_NO") == null)?"":outData.getString("SAP_OUT_NO"));
			itemMap.put("SAP_OUT_ITEM_NO",(outData.getString("SAP_OUT_ITEM_NO") == null)?"":outData.getString("SAP_OUT_ITEM_NO"));
			itemMap.put("MO_NO",outData.getString("AUFNR"));
			itemMap.put("MO_ITEM_NO",outData.getString("POSNR"));
			itemMap.put("RSNUM",(outData.getString("RSNUM") == null)?"":outData.getString("RSNUM"));
			itemMap.put("RSPOS",(outData.getString("RSPOS") == null)?"":outData.getString("RSPOS"));
			itemMap.put("COST_CENTER",params.get("COST_CENTER").toString());
			itemMap.put("IO_NO",(outData.getString("IO_NO") == null)?"":outData.getString("IO_NO"));
			itemMap.put("WBS",params.get("WBS").toString());
			itemMap.put("SAP_MATDOC_NO",(outData.getString("SAP_MATDOC_NO") == null)?"":outData.getString("SAP_MATDOC_NO"));
			itemMap.put("SAP_MATDOC_ITEM_NO",(outData.getString("SAP_MATDOC_ITEM_NO") == null)?"":outData.getString("SAP_MATDOC_ITEM_NO"));
			itemMap.put("INBOUND_NO",(outData.getString("INBOUND_NO") == null)?"":outData.getString("INBOUND_NO"));
			itemMap.put("INBOUND_ITEM_NO",(outData.getString("INBOUND_ITEM_NO") == null)?"":outData.getString("INBOUND_ITEM_NO"));
			itemMap.put("SQE",(outData.getString("SQE") == null)?"":outData.getString("SQE"));
			itemMap.put("PICK_FLAG","0");
			itemMap.put("DEL","0");
			itemMap.put("CREATOR",currentUser.get("USERNAME"));
			itemMap.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
			itemMap.put("EDITOR","");
			itemMap.put("EDIT_DATE","");
			itemMap.put("SQE",outData.getString("SQE"));
			itemMap.put("REQUIREMENT_NO",outData.getString("REQUIREMENT_NO"));
			itemMap.put("REQUIREMENT_ITEM_NO",outData.getString("REQUIREMENT_ITEM_NO"));

			itemDetailMap.put("RETURN_NO",RETURN_NO);
			itemDetailMap.put("RETURN_ITEM_NO",RETURN_ITEM_NO);
			itemDetailMap.put("MATNR",outData.getString("MATNR"));
			itemDetailMap.put("LGORT",outData.getString("LGORT"));
			itemDetailMap.put("F_BATCH",(outData.getString("F_BATCH") == null)?"":outData.getString("F_BATCH"));
			itemDetailMap.put("BATCH",(outData.getString("BATCH") == null)?"":outData.getString("BATCH"));
			itemDetailMap.put("RETURN_QTY",Float.valueOf(outData.getString("QTY1").trim()));
			itemDetailMap.put("LIFNR",(outData.getString("LIFNR") == null)?"":outData.getString("LIFNR"));
			itemDetailMap.put("SOBKZ",outData.getString("SOBKZ"));
			itemDetailMap.put("BIN_CODE",(outData.getString("BIN_CODE") == null)?"":outData.getString("BIN_CODE"));
			itemDetailMap.put("BIN_CODE_XJ",(outData.getString("BIN_CODE_XJ") == null)?"":outData.getString("BIN_CODE_XJ"));
			itemDetailMap.put("XJ_QTY",(outData.getString("XJ_QTY") == null)?"":outData.getString("XJ_QTY"));
			itemDetailMap.put("REAL_QTY",(outData.getString("REAL_QTY") == null)?"":outData.getString("REAL_QTY"));
			itemDetailMap.put("ITEM_TEXT",(outData.getString("ITEM_TEXT") == null)?"":outData.getString("ITEM_TEXT"));
			itemDetailMap.put("RETURN_PEOPLE",currentUser.get("USERNAME"));
			itemDetailMap.put("MEMO",(outData.getString("MEMO") == null)?"":outData.getString("MEMO"));
			itemDetailMap.put("DEL","0");
			itemDetailMap.put("CREATOR",currentUser.get("USERNAME"));
			itemDetailMap.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
			itemDetailMap.put("EDITOR","");
			itemDetailMap.put("EDIT_DATE","");
			itemDetailMap.put("LABEL_NO","");

			WorkshopReturnDao.insertWmsOutReturnItem(itemMap);
			WorkshopReturnDao.insertWmsOutReturnItemDetail(itemDetailMap);
			RETURN_ITEM_NO++;
		}

		return RETURN_NO;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public String confirmWorkshopReturn(Map<String, Object> params) {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//关联查询【退货单行项目表】、【工厂业务类型配置表】和【WMS业务类型表】确认该【退货单行项目】所对应的WMS业务类型是否需要过账SAP
		String SAP_MOVE_TYPE = (jarr.getJSONObject(0).getString("SAP_MOVE_TYPE")!=null)?jarr.getJSONObject(0).getString("SAP_MOVE_TYPE"):"";
		String WMS_MOVE_TYPE = (jarr.getJSONObject(0).getString("WMS_MOVE_TYPE")!=null)?jarr.getJSONObject(0).getString("WMS_MOVE_TYPE"):"";
		logger.info("-->SAP_MOVE_TYPE = " + SAP_MOVE_TYPE + ";WMS_MOVE_TYPE = " + WMS_MOVE_TYPE);
		Map<String,Object> currentUser = userUtils.getUser();
		Map<String,String>txtMap=new HashMap<String,String>();
    	txtMap.put("WERKS", params.get("WERKS").toString());
    	txtMap.put("BUSINESS_NAME",jarr.getJSONObject(0).get("BUSINESS_NAME").toString());
    	txtMap.put("JZ_DATE", params.get("JZ_DATE").toString());
    	txtMap.put("RETURN_NO", params.get("RETURN_NO").toString());
    	txtMap.put("VENDOR_MANAGER", (jarr.getJSONObject(0).get("VENDOR_MANAGER") == null)?"":jarr.getJSONObject(0).get("VENDOR_MANAGER").toString());
    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);

		Map<String,Object> head = new HashMap<String,Object>();
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", txt.get("txtrule"));
		head.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		head.put("TYPE", "00");
		head.put("CREATOR",currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

		List<Map<String,Object>> batchList = (List<Map<String, Object>>) params.get("batchList");

		List<Map<String, Object>> labelList = new ArrayList<Map<String, Object>>();
		for(int i=0;i<jarr.size();i++){
			Map<String,Object> item = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			String REVERSAL_FLAG = "0";
			String CANCEL_FLAG = "0";
			item.put("BUSINESS_CODE", "");
			item.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			item.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			item.put("BUSINESS_CLASS", itemData.getString("BUSINESS_CLASS"));
			item.put("SOBKZ", itemData.getString("SOBKZ"));
			item.put("BIN_CODE", itemData.getString("BIN_CODE"));

			List<Map<String, Object>> moveTypeList = WorkshopReturnDao.getSapWmsMoveType(item);
			if(moveTypeList.size()==0) {
				throw new RuntimeException("没有配置正确的移动类型！");
			}else {
				item.put("WMS_MOVE_TYPE", moveTypeList.get(0).get("WMS_MOVE_TYPE"));
				item.put("SAP_MOVE_TYPE", moveTypeList.get(0).get("SAP_MOVE_TYPE"));
			}
			WMS_MOVE_TYPE = moveTypeList.get(0).get("WMS_MOVE_TYPE").toString();
			SAP_MOVE_TYPE = moveTypeList.get(0).get("SAP_MOVE_TYPE").toString();
			//item.put("WMS_MOVE_TYPE", itemData.getString("WMS_MOVE_TYPE"));
			//item.put("SAP_MOVE_TYPE", itemData.getString("SAP_MOVE_TYPE"));
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
			//item.put("ITEM_TEXT", txt.get("txtruleitem"));
			item.put("ITEM_TEXT", itemData.getString("ITEM_TEXT"));
			item.put("F_WERKS", itemData.getString("F_WERKS"));
			item.put("MATNR", itemData.getString("MATNR").trim());
			item.put("MAKTX", itemData.getString("MAKTX"));
			item.put("F_BATCH", "");
			item.put("WERKS", itemData.getString("WERKS"));
			item.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			//BUG#1262 190812
			if("40".equals(itemData.getString("BUSINESS_NAME"))) {
//				item.put("LGORT", itemData.getString("F_LGORT"));
//				item.put("MOVE_STLOC", itemData.getString("LGORT"));
				item.put("LGORT", itemData.getString("LGORT"));
				item.put("MOVE_STLOC", itemData.getString("F_LGORT"));
			}else {
				item.put("LGORT", itemData.getString("LGORT"));
			}
			//item.put("F_LGORT", itemData.getString("F_LGORT"));
			item.put("UNIT", itemData.getString("UNIT"));
			item.put("QTY_WMS", itemData.getString("QTY1"));
			item.put("QTY_SAP", itemData.getString("QTY1"));
			String batch = "";
			if("2".equals(params.get("ORIGINALBATCH").toString())) {
				batch = batchList.get(i).get("BATCH").toString();
			}else {
				batch = itemData.getString("BATCH");
			}
			item.put("BATCH", batch);
			item.put("RECEIPT_NO", "");
			item.put("RECEIPT_ITEM_NO", "");
			item.put("PO_NO", (itemData.getString("PO_NO")!=null)?itemData.getString("PO_NO"):"");
			item.put("PO_ITEM_NO", (itemData.getString("PO_ITEM_NO")!=null)?itemData.getString("PO_ITEM_NO"):"");
			item.put("IO_NO", (itemData.getString("IO_NO")!=null)?itemData.getString("IO_NO"):"");
			item.put("WBS", (itemData.getString("WBS")!=null)?itemData.getString("WBS"):"");
			item.put("IO_NO", (itemData.getString("IO_NO")!=null)?itemData.getString("IO_NO"):"");
			item.put("COST_CENTER", (itemData.getString("COST_CENTER")!=null)?itemData.getString("COST_CENTER"):"");
			item.put("MO_NO", (itemData.getString("MO_NO")!=null)?itemData.getString("MO_NO"):"");
			item.put("MO_ITEM_NO", (itemData.getString("MO_ITEM_NO")!=null)?itemData.getString("MO_ITEM_NO"):"");
			if("35".equals(itemData.getString("BUSINESS_NAME"))) {
				item.put("RSNUM", (itemData.getString("RSNUM")!=null)?itemData.getString("RSNUM"):"");
				item.put("RSPOS", (itemData.getString("RSPOS")!=null)?itemData.getString("RSPOS"):"");
			}else {
				item.put("RSNUM", "");
				item.put("RSPOS", "");
			}

			item.put("HANDOVER", params.get("HANDOVER"));
			item.put("SAP_OUT_NO", (itemData.getString("SAP_OUT_NO")!=null)?itemData.getString("SAP_OUT_NO"):"");
			item.put("LIFNR", (itemData.getString("LIFNR")!=null)?itemData.getString("LIFNR").trim():"");
			item.put("LIKTX", (itemData.getString("LIKTX")!=null)?itemData.getString("LIKTX"):"");
			item.put("SAP_OUT_ITEM_NO", (itemData.getString("SAP_OUT_ITEM_NO")!=null)?itemData.getString("SAP_OUT_ITEM_NO"):"");
			item.put("RETURN_NO", itemData.getString("RETURN_NO"));
			item.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
			item.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

			//创建标签数据[退回的物料产生新标签]
			/**退料数量/装箱数量计算箱数（即标签数），如果有小数，取向上的整数，每箱数量取装箱数量，如果有尾数，尾数箱数量为尾数，
			比如退料数量50，装箱数量20，产生3箱，1-2箱，装箱数量20,3箱数量10 ，并产生箱序号_ BOX_SN 如：1/3，尾箱需标记END_FLAG=X,;
			标签状态（需要上架的标签，状态为 07已进仓不需要上架的标签，状态为08已上架）；**/

			/***BUG1052 190715
			 * 14.3.9.1创建标签[退回的物料产生新标签]数据【标签表-WMS_CORE_LABEL】：退料数量/装箱数量计算箱数（即标签数），如果有小数，取向上的整数，
			 * 每箱数量取装箱数量，如果有尾数，尾数箱数量为尾数，比如退料数量50，装箱数量20，产生3箱，1-2箱，装箱数量20,3箱数量10 ，并产生箱序号_ BOX_SN 如：1/3，
			 * 尾箱需标记END_FLAG=X,;标签状态（需要上架的标签，状态为 07已进仓不需要上架的标签，状态为08已上架）；
			***/
			String AUTO_PUTAWAY_FLAG = "0"; //= itemData.getString("BIN_CODE");
			Map<String,Object> paramsPutaway=new HashMap<String,Object>();
			paramsPutaway.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			paramsPutaway.put("STORAGE_AREA_CODE", itemData.getString("BIN_CODE"));
			List<Map<String, Object>> listPutaway = WorkshopReturnDao.getPutAwayFlag(paramsPutaway);
			if(listPutaway == null) {
				throw new RuntimeException(itemData.getString("WH_NUMBER") + " " + itemData.getString("BIN_CODE") + "没有配置是否自动上架！");
			}else {
				if(listPutaway.size()>0) {
					AUTO_PUTAWAY_FLAG = listPutaway.get(0).get("AUTO_PUTAWAY_FLAG").toString();
				}
			}
			if(itemData.getString("BOXQTY").equals("0")) {
				throw new RuntimeException("装箱数量不能为0！");
			}
			Double RECEIPT_QTY=Double.valueOf(itemData.getString("QTY1"));
			Double FULL_BOX_QTY=Double.valueOf(itemData.getString("BOXQTY"));
			//String TEST_FLAG = itemData.getString("TEST_FLAG");
			String LABEL_STATUS = "";
			String QC_RESULT_CODE = "";
			if("X".equals(AUTO_PUTAWAY_FLAG)) {
				LABEL_STATUS = "07";
			}else {
				LABEL_STATUS = "08";
			}
			int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);
			Map<String,Object> params2=new HashMap<String,Object>();
			params2.put("WMS_DOC_TYPE", "08");//标签号
			params2.put("WERKS", itemData.getString("WERKS"));
			String labels = "";
			for(int j=1;j<=box_num;j++) {
				Map<String,Object> label=new HashMap<String,Object>();
				String LABEL_NO="";
				Map<String,Object> doc=null;
				doc=wmsCDocNoService.getDocNo(params2);
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				LABEL_NO=doc.get("docno").toString();
				labels += LABEL_NO + ",";
				String BOX_SN=j+"/"+box_num;
				Double BOX_QTY=FULL_BOX_QTY;//装箱数量，计算得出
				String END_FLAG="0";
				if(j==box_num) {
					BOX_QTY = RECEIPT_QTY-(box_num-1)*FULL_BOX_QTY;
					END_FLAG="X";
				}
				label.put("LABEL_NO", LABEL_NO);
				label.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
				label.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
				label.put("LABEL_STATUS", LABEL_STATUS);
				label.put("QC_RESULT_CODE", QC_RESULT_CODE);
				label.put("BOX_SN", BOX_SN);
				label.put("FULL_BOX_QTY", FULL_BOX_QTY);
				label.put("BOX_QTY", BOX_QTY);
				label.put("END_FLAG", END_FLAG);
				label.put("DEL", "0");
				label.put("WERKS", itemData.getString("WERKS"));
				label.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
				label.put("LGORT", itemData.getString("LGORT"));
				label.put("MATNR", itemData.getString("MATNR").trim());
				label.put("MAKTX", itemData.getString("MAKTX"));
				label.put("BIN_CODE", itemData.getString("BIN_CODE"));
				label.put("UNIT", itemData.getString("UNIT"));
				label.put("SOBKZ", itemData.getString("SOBKZ"));
				label.put("BATCH", batch);
				label.put("LIFNR", itemData.getString("LIFNR"));
				label.put("LIKTX", itemData.getString("LIKTX"));
				label.put("PRODUCT_DATE", itemData.getString("CREATE_DATE"));
				label.put("CREATOR", currentUser.get("USERNAME"));
				label.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
				labelList.add(label);
			}
			logger.info("-->LABELS : " + labels);
			item.put("LABEL", labels);
			itemList.add(item);
		}
		//INSERT CORELABEL
		if(labelList.size()>0) {
			WorkshopReturnDao.insertCoreLabel(labelList);
		}



		/**14.3.9.1更新【退货单抬头表】、【退货单行项目表】和【退货单行项目明细表】，抬头表状态更新为“03：完成”，行项目表状态更新为“03：完成”，
		 *并在“实际退货数量（REAL_RETURN_QTY）”字段写入实际退货确认数量，
		 *【退货单抬头表】和【退货单行项目表】中涉及BUSINESS_NAME、BUSINESS_TYPE的存储均取“BUSINESS_CLASS=06 车间退料”对应的值，注意不是存原发料需求对应的值。**/
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			Map<String,Object> par = new HashMap<String,Object>();
			if(i==0) {	//更新【退货单抬头表】
				par.put("HID", itemData.getString("HID"));
				par.put("RETURN_STATUS","03");
				WorkshopReturnDao.updateOutReturnHead(par);
			}
			if("1".equals(params.get("ORIGINALBATCH").toString())) {
				//原批次退回的情况 :更新或插入【退货单行项目明细表】
				par.put("RETURN_NO", itemData.getString("RETURN_NO"));
				par.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
				par.put("BATCH", itemData.getString("BATCH"));
				List<Map<String, Object>> detail_result = WorkshopReturnDao.getOutReturnItemDetail(par);
				if(detail_result.size()==0) {
					Map<String,Object> detail = new HashMap<String,Object>();
					detail.put("RETURN_NO", itemData.getString("RETURN_NO"));
					detail.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
					detail.put("MATNR", itemData.getString("MATNR").trim());
					detail.put("LGORT", itemData.getString("LGORT"));
					detail.put("BATCH", itemData.getString("BATCH"));
					detail.put("RETURN_QTY", itemData.getString("QTY1"));
					detail.put("LIFNR", itemData.getString("LIFNR"));
					detail.put("LIKTX", itemData.getString("LIKTX"));
					detail.put("SOBKZ", itemData.getString("SOBKZ"));
					detail.put("BIN_CODE", itemData.getString("BIN_CODE"));
					detail.put("REAL_QTY", itemData.getString("QTY1"));
					detail.put("ITEM_TEXT", itemData.getString("ITEM_TEXT"));
					detail.put("RETURN_PEOPLE", params.get("FULL_NAME").toString());
					detail.put("MEMO", "");
					detail.put("CREATOR", params.get("FULL_NAME").toString());
					detail.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
					WorkshopReturnDao.insertOutReturnItemDetail(detail);
				}else {
					par.put("DID", detail_result.get(0).get("ID"));
					par.put("REAL_QTY", itemData.getString("RETURN_QTY"));
					WorkshopReturnDao.updateOutReturnItemDetail(par);
				}
				par.put("ITEM_STATUS","03");
				par.put("RETURN_NO", itemData.getString("RETURN_NO"));
				WorkshopReturnDao.updateOutReturnItem(par);
			}else {
				//非原批次退回的情况：更新【退货单行项目表】,更新【退货单行项目明细表】
				par.put("ITEM_STATUS","03");
				par.put("RETURN_NO", itemData.getString("RETURN_NO"));
				WorkshopReturnDao.updateOutReturnItem(par);

				par.put("DID", itemData.getString("DID"));
				par.put("REAL_QTY", itemData.getString("QTY1"));
				WorkshopReturnDao.updateOutReturnItemDetail(par);
			}

		}

		/**14.3.9.2更新【WMS库存表】：先关联查询【工厂库存地点表---WMS_SAP_PLANT_LGORT】判断该接收库位对应的“BAD_FLAG”值，
		 * 如“BAD_FLAG=0”,则根据“工厂+仓库号+料号+WMS批次+库位+储位+供应商代码+特殊库存标识”更新对应行数据的“数量（非限制）（STOCK_QTY）”字段值，数量增加；
		 * 如“BAD_FLAG=X”,则根据“工厂+仓库号+料号+WMS批次+库位+储位+供应商代码+特殊库存标识”更新对应行数据的“冻结数量(FREEZE_QTY)”字段值（数量增加），
		 * 如以上条件的数据在WMS库存表中无记录，则新插入库存数据。*/
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			String BAD_FLAG = "0";
			String batch = "";
			if("2".equals(params.get("ORIGINALBATCH").toString())) {
				batch = batchList.get(i).get("BATCH").toString();
			}else {
				batch = itemData.getString("BATCH");
			}
			Map<String,Object> parMap = new HashMap<String,Object>();
			parMap.put("WERKS", itemData.getString("WERKS"));
			parMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			parMap.put("MATNR", itemData.getString("MATNR").trim());
			parMap.put("MAKTX", itemData.getString("MAKTX"));
			parMap.put("LIFNR", itemData.getString("LIFNR"));
			parMap.put("LIKTX", itemData.getString("LIKTX"));
			parMap.put("LGORT", itemData.getString("LGORT"));
			parMap.put("BIN_CODE", itemData.getString("BIN_CODE"));
			parMap.put("SOBKZ", itemData.getString("SOBKZ"));
			parMap.put("UNIT", itemData.getString("UNIT"));
			parMap.put("BATCH", batch);
			parMap.put("ORIGINALBATCH", params.get("ORIGINALBATCH").toString());
			List<Map<String, Object>> lgort_result = WorkshopReturnDao.getPlantLgortInfo(parMap);
			BAD_FLAG = (lgort_result.size() > 0)?lgort_result.get(0).get("BAD_FLAG").toString():"0";
			if("0".equals(BAD_FLAG)) {
				parMap.put("STOCK_QTY", itemData.getString("QTY1"));
				parMap.put("FREEZE_QTY", "");
				int result = WorkshopReturnDao.updateCoreStockQty(parMap);
				if(result == 0) {
					parMap.put("EDITOR", currentUser.get("USERNAME"));
					parMap.put("EDIT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
					WorkshopReturnDao.insertCoreStockQty(parMap);
				}

			}else if("X".equals(BAD_FLAG)) {
				parMap.put("STOCK_QTY", "");
				parMap.put("FREEZE_QTY", itemData.getString("QTY1"));
				int result = WorkshopReturnDao.updateCoreStockQty(parMap);
				if(result == 0) {
					parMap.put("EDITOR", currentUser.get("USERNAME"));
					parMap.put("EDIT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
					WorkshopReturnDao.insertCoreStockQty(parMap);
				}
			}else {
				//新插入库存数据
				/*parMap.put("STOCK_QTY", itemData.getString("QTY1"));
				parMap.put("EDITOR", params.get("FULL_NAME").toString());
				parMap.put("EDIT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
				WorkshopReturnDao.insertCoreStockQty(parMap);*/

			}
		}



		/**14.3.9.4针对“生产订单退料（冲预留）”类退料，需更新【生产订单组件表--- WMS_SAP_MO_COMPONENT】的“投料数量”：根据“工厂+生产订单+生产订单行项目+料号”
		找到对应的行项目数据，用“投料数量-实际退料数量”，所得结果存入“投料数量字段”。（即投料数量减少）**/
		if("35".equals(jarr.getJSONObject(0).getString("BUSINESS_NAME"))) {
			for(int i=0;i<jarr.size();i++){
				JSONObject itemData=  jarr.getJSONObject(i);
				Map<String,Object> parMap = new HashMap<String,Object>();
				parMap.put("WERKS", itemData.getString("WERKS"));
				parMap.put("MO_NO", itemData.getString("MO_NO"));
				parMap.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO"));
				parMap.put("MATNR", itemData.getString("MATNR").trim());
				parMap.put("RETURN_QTY", itemData.getString("RETURN_QTY"));
				WorkshopReturnDao.updateMOComponentQty(parMap);
			}
		}

		String WMS_NO="";
		if(!WMS_MOVE_TYPE.equals("")) {
			WMS_NO = commonService.saveWMSDoc(head,itemList);
		}
		String SAP_NO = "";

		if(!SAP_MOVE_TYPE.equals("")) {	//需过帐SAP
			params.put("BUSINESS_NAME", jarr.getJSONObject(0).getString("BUSINESS_NAME"));
			params.put("BUSINESS_TYPE", jarr.getJSONObject(0).getString("BUSINESS_TYPE"));
			params.put("BUSINESS_CLASS", jarr.getJSONObject(0).getString("BUSINESS_CLASS"));
			params.put("WMS_NO", WMS_NO);
			params.put("matList", itemList);
			String msg = "";
			try {
				msg = commonService.doSapPost(params);
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage());
			}

			SAP_NO= ";SAP_NO=" + msg;
		}

		return "WMS_NO:" + WMS_NO + SAP_NO;
	}

	public static String removeZeroStr(String str) {
		if(str == null) return "";
		for(int i=0;i<str.length();i++) {
			if(!"0".equals(str.substring(i,i+1))) {
				str = str.substring(i);
				break;
			}
		}
		return str;
	}

}
