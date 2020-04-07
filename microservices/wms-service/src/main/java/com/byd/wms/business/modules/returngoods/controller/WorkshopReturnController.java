package com.byd.wms.business.modules.returngoods.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.returngoods.service.WorkshopReturnService;

@RestController
@RequestMapping("workshopReturn")
public class WorkshopReturnController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WorkshopReturnService workshopReturnService;
	@Autowired
	WmsSapRemote wmsSapRemote;
    
	@RequestMapping("/getBusinessNameListByWerks")
    public R getBusinessNameListByWerks(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> result = workshopReturnService.getBusinessNameListByWerks(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getSapInOrderInfo")
	public R getSapInOrderInfo(@RequestParam Map<String, Object> params){
		Map<String,Object> result = wmsSapRemote.getSapBapiKaufOrder(params.get("ORDER").toString());
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getSapVendorInfo")
	public R getSapVendorInfo(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> result = workshopReturnService.getSapVendorInfo(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getCostCenterInfo")
	public R getCostCenterInfo(@RequestParam Map<String, Object> params){
		Map<String,Object> result = wmsSapRemote.getSapBapiCostcenterDetail(params.get("COSTCENTER").toString());
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getWbsInfo")
	public R getWbsInfo(@RequestParam Map<String, Object> params){
		Map<String,Object> result = wmsSapRemote.getSapBapiSapBapiWbs(params.get("WBS").toString());
		return R.ok().put("result", result);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getWorkshopReturnData")
    public R getWorkshopReturnData(@RequestParam Map<String, Object> params){
		logger.info("---->getWorkshopReturnData");
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if("35".equals(BUSINESS_NAME)||"36".equals(BUSINESS_NAME)) {	//35 生产订单退料(冲预留262)//36 生产订单退料(不冲预留262)
			//判断工厂是否启用核销业务,2.3.3.2如启用则继续判断该生产订单是否做过核销账务,如剩余核销数量>0（关联查询【生产订单抬头核销表--- WMS_HX_MO_HEAD】，
			//则不允许做继续做生产订单退料业务，系统返回消息：该生产订单存在V类业务数据。
			List<Map<String, Object>> plantInfo = new ArrayList<Map<String, Object>>();
			plantInfo = workshopReturnService.getPlantInfoByWerks(params);
			logger.info("-->HX_FLAG = " + plantInfo.get(0).get("HX_FLAG"));
			if("X".equals(plantInfo.get(0).get("HX_FLAG"))) {
				String ORDERS = params.get("ORDERS").toString();
				String[] orders = ORDERS.split(",");
				for(int i=0;i<orders.length;i++) {
					if(orders[i].length()>0) {
						int hx_qty = workshopReturnService.getMoHeadHxQty(orders[i]);
						if(hx_qty > 0) {
							return R.error("生产订单："+orders[i]+"存在V类业务数据！");
						}
					}
				}
			}
			//判断生产订单是否是有效订单：判断【生产订单抬头表】的“订单状态（ISTAT_TXT）”状态，包含REL状态，不包含LKD和DLFL状态的即为有效订单。
			Map<String, Object> par = new HashMap <String, Object>();
			String ORDERS = params.get("ORDERS").toString();
			String[] orders = ORDERS.split(",");
			for(int i=0;i<orders.length;i++) {
				par.put("AUFNR", orders[i]);
				par.put("WERKS", params.get("WERKS").toString());
				int mo_qty = workshopReturnService.getSapMoCount(par);
				if(mo_qty == 0) {
					return R.error("生产订单："+orders[i]+"不存在，请检查生产订单或手工同步生产订单！");
				}else {
					String mo_status = workshopReturnService.getSapMoStatus(par);
					logger.info("-->mo_status:" + mo_status);
					//订单状态需 包含REL 不含DLFL 不含LKD  REL MSPT PRC SETC SSAP OPGN NEWQ
					if(!((mo_status.indexOf("REL") >= 0) && (mo_status.indexOf("DLFL") < 0) && (mo_status.indexOf("LKD") < 0))) {
						return R.error("生产订单："+orders[i]+"未释放，已关闭或已删除！");
					}
				}
			}
			result = workshopReturnService.getWorkshopReturnData(params);	
		}else if("37".equals(BUSINESS_NAME)) {	//37 研发/内部/CO订单退料(262)		
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("38".equals(BUSINESS_NAME)) {	//38 成本中心退料(202)
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("39".equals(BUSINESS_NAME)) {	//39 WBS元素退料(222)
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("40".equals(BUSINESS_NAME)) {	//40 线边仓退料(312)
			List<Map<String, Object>> plantInfo = new ArrayList<Map<String, Object>>();
			plantInfo = workshopReturnService.getPlantInfoByWerks(params);
			logger.info("-->HX_FLAG = " + plantInfo.get(0).get("HX_FLAG"));
			if("X".equals(plantInfo.get(0).get("HX_FLAG"))) {
				String ORDERS = params.get("ORDERS").toString();
				if(!"".equals(ORDERS)) {
					String[] orders = ORDERS.split(",");
					for(int i=0;i<orders.length;i++) {
						if(orders[i].length()>0) {
							int hx_qty = workshopReturnService.getMoHeadHxQty(orders[i]);
							if(hx_qty > 0) {
								return R.error("生产订单："+orders[i]+"存在V类业务数据！");
							}
						}
					}
				}
			}
			//判断生产订单是否是有效订单：判断【生产订单抬头表】的“订单状态（ISTAT_TXT）”状态，包含REL状态，不包含LKD和DLFL状态的即为有效订单。
			Map<String, Object> par = new HashMap <String, Object>();
			String ORDERS = params.get("ORDERS").toString();
			if(!"".equals(ORDERS)) {
				String[] orders = ORDERS.split(",");
				for(int i=0;i<orders.length;i++) {
					par.put("AUFNR", orders[i]);
					par.put("WERKS", params.get("WERKS").toString());
					int mo_qty = workshopReturnService.getSapMoCount(par);
					if(mo_qty == 0) {
						return R.error("生产订单："+orders[i]+"不存在，请检查生产订单或手工同步生产订单！");
					}else {
						String mo_status = workshopReturnService.getSapMoStatus(par);
						logger.info("-->mo_status:" + mo_status);
						//订单状态需 包含REL 不含DLFL 不含LKD  REL MSPT PRC SETC SSAP OPGN NEWQ
						if(!((mo_status.indexOf("REL") >= 0) && (mo_status.indexOf("DLFL") < 0) && (mo_status.indexOf("LKD") < 0))) {
							return R.error("生产订单："+orders[i]+"未释放，已关闭或已删除！");
						}
					}
				}
			}
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("68".equals(BUSINESS_NAME)) {	//68UB转储单退货(352)
			Map<String, Object> par = new HashMap <String, Object>();
			String PO_ORDER = params.get("PO_ORDER").toString();
			String[] orders = PO_ORDER.split(",");
			for(int i=0;i<orders.length;i++) {
				par.put("PONO", orders[i]);
				par.put("WERKS", params.get("WERKS").toString());
				int po_qty = workshopReturnService.getSapPoCount(par);
				if(po_qty == 0) {
					return R.error("采购订单："+orders[i]+"不存在，或者不属于"+params.get("WERKS").toString()+"，请检查采购订单或手工同步采购订单！");
				}else {
					List<Map<String, Object>> poStatus = workshopReturnService.getSapPoStatus(par);
					String lifnr = poStatus.get(0).get("LIFNR").toString();
					if(lifnr.indexOf(params.get("WERKS").toString()) <0 ) {
						//采购订单********不是****工厂需发货的订单
						return R.error("采购订单："+orders[i]+"不是"+params.get("WERKS").toString()+"工厂需发货的订单！");
					}
					//10.3.2判断采购订单是否为UB转储单，如不是UB转储单则报错采购订单***********不包含发货项目。
					//（通过采购订单抬头的采购凭证类型BSART判断，凭证类型为UB或ZUB的为允许操作的采购订单）
					String BSART = poStatus.get(0).get("BSART").toString();
					if(!("UB".equals(BSART) || "ZUB".equals(BSART))) {
						return R.error("采购订单："+orders[i]+"不包含发货项目！");
					}
					if("X".equals(poStatus.get(0).get("FRGRL").toString())) {
						return R.error("采购订单："+orders[i]+"未批准！");
					}
					/**10.3.4判断采购订单是否是有效订单：已审批的，且采购订单的行项目未标识为交货已完成、未删除的，如果一个采购订单不同的行项目对应多个工厂，
					 * 只显示符合账号权限的行项目，如同一采购订单部分行项目被标记为交货已完成或者已被删除，则只显示符合条件的行项目。采购订单头表、行项目表状态说明：
					【采购订单抬头---WMS_SAP_PO_HEAD】表的“FRGRL=空”代表采购订单已审批，“FRGRL=X”代表采购订单未审批。
					      【采购订单行项目---WMS_SAP_PO_ITEM】表“ELIKZ =X”代表交货已完成。
						采购订单行项目---WMS_SAP_PO_ITEM】表的“LOEKZ=空”代表未删除的。
						如采购订单未批准，点击“创建退货单”时，系统返回消息：采购凭证 ********** 未批准！
						如采购订单行项目均被删除，系统返回消息：采购订单**********不包含发货项目。
						如采购订单行项目均标记为“交货已完成”，系统返回消息：采购订单**********不包含发货项目。**/
					int num1 = 0;//完成数
					int num2 = 0;//删除数
					for(int j=0;j<poStatus.size();j++) {
						if("X".equals(poStatus.get(j).get("ELIKZ").toString())) {
							num1++;
						}
						if("X".equals(poStatus.get(j).get("LOEKZ").toString())) {
							num2++;
						}
					}
					if(num1 == poStatus.size()) {
						return R.error("采购订单："+orders[i]+"均已完成，不包含发货项目！");
					}
					if(num2 == poStatus.size()) {
						return R.error("采购订单："+orders[i]+"均已删除，不包含发货项目！");
					}
				}
			}
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("69".equals(BUSINESS_NAME)) {	//69 委外加工退料(542)
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("70".equals(BUSINESS_NAME)) {	//70 STO销售退货
			String SAP_ORDERS = params.get("SAP_ORDERS").toString();
			/**BUG 1051 190715
			 * 8.3.1.1退货SAP交货单：调取SAP接口获取SAP交货单行项目信息查询SAP【LIKP】表，VBTYP= T类型的(即SD的凭证类别=订单的退货类型)，
			 * 如查询确认交货单的VBTYP≠T，或SAP交货单行项目中的移动类型≠675则系统报错：SAP交货单**********不是正确的退货交货单；
			 * 判断SAP交货单行项目中的WERKS与页面的工厂不一致时，报错：‘你无权操作***工厂的SAP交货单’；。
			**/
			String[] sapOrderList = SAP_ORDERS.split(",");
			for(int i=0;i<sapOrderList.length;i++) {
				Map<String, Object> sto_result = new HashMap<String, Object>();
				logger.info("-->saporder : " + sapOrderList[i]);
				try {
					sto_result = wmsSapRemote.getSapBapiDeliveryGetlist(sapOrderList[i]);
				}catch(Exception e){
					return R.error("调用SAP接口查询失败！");
				}
				if(!"T".equals(((Map<String,Object>)sto_result.get("header")).get("VBTYP"))) {
					return R.error("SAP交货单"+sapOrderList[i]+" VBTYP:"+((Map<String,Object>)sto_result.get("header")).get("VBTYP")+",不是正确的退货交货单(T)。");
				}
				if(!params.get("WERKS").equals(((List<Map<String, Object>>)sto_result.get("item")).get(0).get("WERKS"))) {
					return R.error("SAP交货单"+sapOrderList[i]+"不是工厂"+params.get("WERKS")+"的交货单。");
				}
				//logger.info("-->BWART : " + ((List<Map<String, Object>>)sto_result.get("item")).get(0).get("BWART"));
				if(!"675".equals(((List<Map<String, Object>>)sto_result.get("item")).get(0).get("BWART"))) {
					return R.error("SAP交货单"+sapOrderList[i]+"不是正确的退货交货单!");
				}
			}
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("71".equals(BUSINESS_NAME)) {	//71 销售订单退货(657)
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("72".equals(BUSINESS_NAME)) {	//72 外部销售退货(252)
			//查询WMS【采购订单同步表】，确认采购订单是否存在
			Map<String, Object> par = new HashMap <String, Object>();
			String PO_ORDER = params.get("PO_ORDER").toString();
			String[] orders = PO_ORDER.split(",");
			for(int i=0;i<orders.length;i++) {
				par.put("PONO", orders[i]);
				par.put("WERKS", params.get("WERKS").toString());
				int po_qty = workshopReturnService.getSapPoCount(par);
				if(po_qty == 0) {
					return R.error("采购订单："+orders[i]+"不存在，或者不属于"+params.get("WERKS").toString()+"，请检查采购订单或手工同步采购订单！");
				}else {
					//判断页面所选的工厂是否和采购订单的供货工厂一致
					//判断采购订单是否是有效订单
					List<Map<String, Object>> poStatus = workshopReturnService.getSapPoStatus(par);
					String lifnr = poStatus.get(0).get("LIFNR").toString();
					if(lifnr.indexOf(params.get("WERKS").toString()) <0 ) {
						//采购订单********不是****工厂需发货的订单
						return R.error("采购订单："+orders[i]+"不是"+params.get("WERKS").toString()+"工厂需发货的订单！");
					}
					//【采购订单抬头---WMS_SAP_PO_HEAD】表的“FRGRL=空”代表采购订单已审批，“FRGRL=X”代表采购订单未审批。
					//如采购订单未批准，点击“创建退货单”时，系统返回消息：采购凭证 ********** 未批准！
					if("X".equals(poStatus.get(0).get("FRGRL").toString())) {
						return R.error("采购订单："+orders[i]+"未批准！");
					}
					//【采购订单行项目---WMS_SAP_PO_ITEM】表“ELIKZ =X”代表交货已完成。
					//如采购订单行项目均标记为“交货已完成”，系统返回消息：采购订单**********不包含发货项目。
					//【采购订单行项目---WMS_SAP_PO_ITEM】表的“LOEKZ=空”代表未删除的。
					//如采购订单行项目均被删除，系统返回消息：采购订单**********不包含发货项目。
					int num1 = 0;//完成数
					int num2 = 0;//删除数
					for(int j=0;j<poStatus.size();j++) {
						if("X".equals(poStatus.get(j).get("ELIKZ").toString())) {
							num1++;
						}
						if("X".equals(poStatus.get(j).get("LOEKZ").toString())) {
							num2++;
						}
					}
					if(num1 == poStatus.size()) {
						return R.error("采购订单："+orders[i]+"均已完成，不包含发货项目！");
					}
					if(num2 == poStatus.size()) {
						return R.error("采购订单："+orders[i]+"均已删除，不包含发货项目！");
					}
					
				}
			}
			result = workshopReturnService.getWorkshopReturnData(params);
		}else if("46".equals(BUSINESS_NAME)) {
			//46 工厂间调拨
			result = workshopReturnService.getWorkshopReturnData(params);
		}
			
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getWorkshopReturnConfirmData")
    public R getWorkshopReturnConfirmData(@RequestParam Map<String, Object> params){
		logger.info("---->getWorkshopReturnConfirmData");
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			result = workshopReturnService.getWorkshopReturnConfirmData(params);
		}catch(Exception e) {
			return R.error(e.getMessage());
		}
		
		for(int i=0;i<result.size();i++) {
			result.get(i).put("QTY1", result.get(i).get("RETURN_QTY"));
			result.get(i).put("BOXQTY", result.get(i).get("RETURN_QTY"));
		}
		
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getSapMoComponentDate")
    public R getSapMoComponentDate(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();	
		result = workshopReturnService.getSapMoComponentDate(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/createWorkshopReturn")
	public R createWorkshopReturn(@RequestParam Map<String, Object> params){
		String outNo = workshopReturnService.createWorkshopReturn(params);
		if("-1".equals(outNo)) {
			return R.error("退料单号生成失败！");
		}else {
			return R.ok().put("outNo",outNo);
		}
	}
	
	@RequestMapping("/confirmWorkshopReturn")
	public R confirmWorkshopReturn(@RequestParam Map<String, Object> params){
		String msg = "";
		try {
			if("2".equals(params.get("ORIGINALBATCH").toString())) {
				//否 原批次退回 根据用户填的日期生成批次
				List<Map<String,Object>> batchList = workshopReturnService.getMatBatch(params);
				params.put("batchList", batchList);
			}
			msg = workshopReturnService.confirmWorkshopReturn(params);
		}catch(Exception e) {
			return R.error(e.getMessage());
		}
		if("-1".equals(msg)) {
			return R.error("交接失败！");
		}else {
			return R.ok().put("MESSAGE",msg);
		}
	}
}
