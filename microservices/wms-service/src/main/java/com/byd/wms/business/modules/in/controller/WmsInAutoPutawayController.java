package com.byd.wms.business.modules.in.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.in.service.WmsInAutoPutawayService;

/**
 * 
 * @author ren.wei3
 * STO一步联动收货
 */
@RestController
@RequestMapping("in/autoPutaway")
public class WmsInAutoPutawayController {

	@Autowired
	private WmsSapRemote wmsSapRemote;
	
	@Autowired
	private CommonService commonDao;
	
	@Autowired
	private WmsInAutoPutawayService wmsInAutoPutawayService;
	
	@Autowired
    private UserUtils userUtils;
	
	@Autowired
	private WmsCTxtService wmsCtxService;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String matdoc = params.get("MAT_DOC") == null ? "":params.get("MAT_DOC").toString();
		String docYear = params.get("DOC_YEAR") == null ? "":params.get("DOC_YEAR").toString();
		String fromWerks = params.get("FROMWERKS") == null ? "":params.get("FROMWERKS").toString();
		
		list = commonDao.getWmsDocList(matdoc);
		if (list.size() > 0) {
			
			for (Map<String,Object> item : list) {
				item.put("WH_NUMBER", params.get("WH_NUMBER"));
			}
			//获取已收货数量
			List<Map<String,Object>> receivedlist = wmsInAutoPutawayService.getReceivedQty(list);
			
			loop1 : for (Map<String,Object> item : list) {
				if (!item.get("WERKS").equals(fromWerks.toUpperCase())) {
					return R.error("发货工厂与凭证中工厂不一致，请确认！");	
				}
				item.put("FROM_WERKS", item.get("WERKS"));
				item.put("FROM_LGORT", item.get("LGORT"));
				//计算可收货数量
				boolean recflag = true;
				for (Map<String,Object> rec : receivedlist) {
					if (rec.get("DOC_NO").equals(item.get("WMS_NO")) && rec.get("DOC_ITEM_NO").equals(item.get("WMS_ITEM_NO"))) {
						BigDecimal reqty = rec.get("RECEIVED_QTY") == null ? BigDecimal.ZERO:new BigDecimal(rec.get("RECEIVED_QTY").toString());
						BigDecimal wmsqty = item.get("QTY_WMS") == null ? BigDecimal.ZERO:new BigDecimal(item.get("QTY_WMS").toString());
						if(wmsqty.subtract(reqty).compareTo(BigDecimal.ZERO) <= 0) {
							continue loop1;
						}
						item.put("RECEIVED_QTY", reqty);
						item.put("RECEIVE_QTY", wmsqty.subtract(reqty));
						recflag = false;
						break;
					}
				}
				if(recflag) {
					item.put("RECEIVED_QTY", "");
					item.put("RECEIVE_QTY", item.get("QTY_WMS"));
				}
				
				Map<String, String> txtparams = new HashMap<String, String>(); 
				txtparams.put("BUSINESS_NAME", "00");
				txtparams.put("JZ_DATE", params.get("JZDDT")==null?"":params.get("JZDDT").toString());
				txtparams.put("WERKS", params.get("WERKS") ==null?"":params.get("WERKS").toString());
				txtparams.put("FULL_NAME", params.get("FULL_NAME") ==null?"":params.get("FULL_NAME").toString());
				Map<String, Object> retrule=wmsCtxService.getRuleTxt(txtparams);
				if(!retrule.isEmpty()){
					if("success".equals(retrule.get("msg"))){
						String txtrule=retrule.get("txtrule").toString();//头文本
						String txtruleitem=retrule.get("txtruleitem").toString();//行文本
						
						item.put("ITEM_TEXT", txtruleitem);
						
						item.put("TXTRULE", txtrule);
					}
				}
			}
		} else {
			if (docYear.equals("")) {
				docYear = DateUtils.format(new Date(),DateUtils.DATE_PATTERN).substring(0, 4);
			}
			Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(matdoc,docYear);
			List<Map<String,Object>> itemList =(List<Map<String, Object>>) rm.get("GOODSMVT_ITEMS");
			
			if (itemList != null) {
				List<Map<String,Object>> paramlist = new ArrayList<Map<String,Object>>();
				for (Map<String,Object> item : itemList) {
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("WERKS", params.get("WERKS"));
					param.put("MATNR", item.get("MATNR"));
					paramlist.add(param);
					item.put("WH_NUMBER", params.get("WH_NUMBER"));
				}
				//获取物料描述信息
				List<Map<String,Object>> materiallist = commonDao.getMaterialInfoBatch(paramlist);
				//获取已收货数量
				List<Map<String,Object>> receivedlist = wmsInAutoPutawayService.getReceivedQty(itemList);
				
				int i = 1;
				loop1 : for (Map<String,Object> item : itemList) {
					if (!item.get("WERKS").equals(fromWerks.toUpperCase())) {
						return R.error("发货工厂与凭证中工厂不一致，请确认！");	
					}
					
					if (!item.get("X_AUTO_CRE").equals("X")) {
						item.put("INDX", i++);
						item.put("WMS_ITEM_NO", item.get("MATDOC_ITM"));
						item.put("QTY_WMS", item.get("ENTRY_QNT"));
						//计算可收货数量
						boolean recflag = true;
						for (Map<String,Object> rec : receivedlist) {
							if (rec.get("DOC_NO").equals(item.get("MAT_DOC")) && rec.get("DOC_ITEM_NO").equals(item.get("MATDOC_ITM"))) {
								BigDecimal reqty = rec.get("RECEIVED_QTY") == null ? BigDecimal.ZERO:new BigDecimal(rec.get("RECEIVED_QTY").toString());
								BigDecimal sapqty = item.get("ENTRY_QNT") == null ? BigDecimal.ZERO:new BigDecimal(item.get("ENTRY_QNT").toString());
								if(sapqty.subtract(reqty).compareTo(BigDecimal.ZERO) <= 0) {
									continue loop1;
								}
								item.put("RECEIVED_QTY", reqty);
								item.put("RECEIVE_QTY", sapqty.subtract(reqty));
								recflag = false;
								break;
							}
						}
						if(recflag) {
							item.put("RECEIVED_QTY", "");
							item.put("RECEIVE_QTY", item.get("ENTRY_QNT"));
						}
						
						for (Map<String,Object> material :materiallist) {
							if(material.get("MATNR").equals(item.get("MATNR"))) {
								item.put("MAKTX", material.get("MAKTX"));
								break;
							}
						}
						
						Map<String, String> txtparams = new HashMap<String, String>(); 
						txtparams.put("BUSINESS_NAME", "00");
						txtparams.put("JZ_DATE", params.get("JZDDT")==null?"":params.get("JZDDT").toString());
						txtparams.put("WERKS", params.get("WERKS") ==null?"":params.get("WERKS").toString());
						txtparams.put("FULL_NAME", params.get("FULL_NAME") ==null?"":params.get("FULL_NAME").toString());
						Map<String, Object> retrule=wmsCtxService.getRuleTxt(txtparams);
						if(!retrule.isEmpty()){
							if("success".equals(retrule.get("msg"))){
								String txtrule=retrule.get("txtrule").toString();//头文本
								String txtruleitem=retrule.get("txtruleitem").toString();//行文本
								
								item.put("ITEM_TEXT", txtruleitem);
								
								item.put("TXTRULE", txtrule);
							}
						}
						item.put("FROM_WERKS", item.get("WERKS"));
						item.put("FROM_LGORT", item.get("STGE_LOC"));
						item.put("UNIT", item.get("ENTRY_UOM"));
						list.add(item);
					}
				}
			}
		}
		
		if (list.size() <= 0) {
			return R.error("凭证无可收货信息！");	
		}
		return R.ok().put("list", list);
	}
	
	@RequestMapping("/createPO")
	public R createPO(@RequestParam Map<String, Object> params)  {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME"));
	   	params.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			String wmsno = "";
			String sapdoc = "";
			List<Map<String,String>> itemList = (List<Map<String,String>>) JSONArray.parse(params.get("ITEMLIST").toString());
			int itemNo = 10;
			for (Map<String,String> item : itemList) {
				item.put("ITEM_NO", String.format("%5d", itemNo).replace(" ", "0"));
				itemNo = itemNo + 10;
				item.put("DELIVERY_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN_POINT));
			}
			params.put("ITEMLIST", JSONArray.toJSONString(itemList));
			//创建PO
			retMap = wmsInAutoPutawayService.createPO(params);
			
			String poNumber = retMap.get("PO_NUMBER") == null ? "":retMap.get("PO_NUMBER").toString();
			String message = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
			//保存日志和状态
			params.put("PO_NUMBER", poNumber);
			params.put("MESSAGE", message);
			String timestampstr = wmsInAutoPutawayService.saveStepLog(params);
			
			//创建DN
			if(!poNumber.equals("")) {
				Thread.sleep(1000);
				params.put("EBELN", poNumber);
				params.put("TIME_STAMP_STR", timestampstr);
				retMap = wmsInAutoPutawayService.createDN(params);
			}
			String delivery = retMap.get("DELIVERY") == null ? "":retMap.get("DELIVERY").toString();
			
			//交货单收货
			if(!delivery.equals("")) {
				retMap = wmsInAutoPutawayService.postDN(params);
				
				wmsno = retMap.get("WMS_NO") == null ? "":retMap.get("WMS_NO").toString();
				String receiptno = retMap.get("RECEIPT_NO") == null ? "":retMap.get("RECEIPT_NO").toString();
				String iqcno = retMap.get("INSPECTION_NO") == null ? "":retMap.get("INSPECTION_NO").toString();
				sapdoc = retMap.get("MAT_DOC") == null ? "":retMap.get("MAT_DOC").toString();
				String message1 = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
				
				//更新日志状态
				List<Map<String,String>> statusList = new ArrayList<Map<String,String>>();
				Map<String, String> statusparam = new HashMap<String, String>();
				if (wmsno.equals("")) {
					statusparam.put("STEP3_STATUS", "E");
					statusparam.put("MSG3", message1.substring(0, message1.length()>300?300:message1.length()));
				} else {
					statusparam.put("STEP3_STATUS", "S");
					statusparam.put("RECEIPT_NO", receiptno);
					statusparam.put("INSPECTION_NO", iqcno);
					statusparam.put("WMS_NO", wmsno);
					statusparam.put("MAT_DOC", sapdoc);
					statusparam.put("MSG3", " ");
				}
				statusparam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				statusparam.put("WH_NUMBER", params.get("WH_NUMBER").toString());
				statusparam.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR").toString());
				statusList.add(statusparam);
				wmsInAutoPutawayService.updatePutawayStatus(statusList);
			}
			
			
			if (!message.equals("")) {
				return R.error("收货失败！原因：" + message);
			}
			return R.ok().put("postr", poNumber)
					.put("dnstr", delivery)
					.put("wmsnostr", wmsno)
					.put("sapdoc", sapdoc)
					.put("lableList", retMap.get("lableList"))
					.put("inspectionList", retMap.get("inspectionList"));
		} catch (Exception e) {
			return R.error("系统异常："+e.getMessage());	
		}	
	}
	
	@RequestMapping("/createDN")
	public R createDN(@RequestParam Map<String, Object> params)  {
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			Thread.sleep(3000);
			retMap = wmsInAutoPutawayService.createDN(params);
			String delivery = retMap.get("DELIVERY") == null ? "":retMap.get("DELIVERY").toString();
			String message = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
			
			if (!message.equals("")) {
				return R.error("PO创建失败！原因：" + message);
			}
			return R.ok().put("msg", delivery);
		} catch (Exception e) {
			return R.error("系统异常："+e.getMessage());	
		}	
	}
	
	@RequestMapping("/postDN")
	public R postDN(@RequestParam Map<String, Object> params)  {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME"));
	   	params.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
	   	
		Map<String, Object> retMap=new HashMap<String, Object>();
		String wmsno = "";
		String receiptno ="";
		String iqcno = "";
		String sapdoc = "";
		String message = "";
    	
		try{
			Thread.sleep(1000);
			retMap = wmsInAutoPutawayService.postDN(params);
			wmsno = retMap.get("WMS_NO") == null ? "":retMap.get("WMS_NO").toString();
			receiptno = retMap.get("RECEIPT_NO") == null ? "":retMap.get("RECEIPT_NO").toString();
			iqcno = retMap.get("INSPECTION_NO") == null ? "":retMap.get("INSPECTION_NO").toString();
			sapdoc = retMap.get("MAT_DOC") == null ? "":retMap.get("MAT_DOC").toString();
			message = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
			
			return R.ok().put("msg", wmsno);
		} catch (Exception e) {
			message = e.getMessage();
			return R.error("系统异常："+e.getMessage());	
		} finally {
			//保存日志和状态
			List<Map<String,String>> statusList = new ArrayList<Map<String,String>>();
			Map<String, String> statusparam = new HashMap<String, String>();
			if (wmsno.equals("")) {
				statusparam.put("STEP3_STATUS", "E");
				statusparam.put("MSG3", message.substring(0, message.length()>300?300:message.length()));
			} else {
				statusparam.put("STEP3_STATUS", "S");
				statusparam.put("RECEIPT_NO", receiptno);
				statusparam.put("INSPECTION_NO", iqcno);
				statusparam.put("WMS_NO", wmsno);
				statusparam.put("MAT_DOC", sapdoc);
			}
			statusparam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			statusparam.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			statusparam.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR").toString());
			statusList.add(statusparam);
			wmsInAutoPutawayService.updatePutawayStatus(statusList);
		}
	}
	
	/**
     * yi一步联动日志
     */
    @RequestMapping("/loglist")
    public R loglist(@RequestParam Map<String, Object> params){
        PageUtils page = wmsInAutoPutawayService.loglist(params);
        return R.ok().put("page", page);
    }
    
    
    @RequestMapping("/autoPutawayProcess")
	public R autoPutawayProcess(@RequestParam Map<String, Object> params)  {
		Map<String,Object> currentUser = userUtils.getUser();
	   	Map<String,Object> returnMap = new HashMap<String,Object>();
		boolean successflag = true;
		try{
			List<Map<String,Object>> timeList = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> itemList = (List<Map<String,Object>>) JSONArray.parse(params.get("ITEMLIST").toString());
			//根据时间戳执行，过滤时间戳重复的数据
			for (Map<String,Object> item : itemList) {
				int flag = 0;// 0为新增数据， 
				for (Map<String,Object> times : timeList) {  
					if(times.get("TIME_STAMP_STR").equals(item.get("TIME_STAMP_STR"))) {
						flag = 1;  
						continue;  
					}
				}
				if (flag == 0) { 
					Map<String,Object> dataMapTemp = new HashMap<String,Object>();
					dataMapTemp.putAll(item);
					timeList.add(dataMapTemp); 
				}
			}
			
			if (timeList.size() < 1) {
				return R.error("未找到合适数据");
			}
			
			
			for (Map<String,Object> item : timeList) {
				String whnumber = item.get("WH_NUMBER").toString();
				String werks = item.get("WERKS").toString();
				String timeStampStr = item.get("TIME_STAMP_STR").toString();
				String poNumber = "";
				
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("WH_NUMBER", whnumber);
				param.put("TIME_STAMP_STR", timeStampStr);
				
				List<Map<String, Object>> putawayInfolist = wmsInAutoPutawayService.getPutawayInfo(param);
				if (putawayInfolist == null && putawayInfolist.size() <= 0) {
					continue;
				}
				
				String sapdoc = putawayInfolist.get(0).get("MAT_DOC")==null?"":putawayInfolist.get(0).get("MAT_DOC").toString();
				String wmsno = putawayInfolist.get(0).get("WMS_NO")==null?"":putawayInfolist.get(0).get("WMS_NO").toString();
				
				//执行第一步前先检查是否已成功
				if (putawayInfolist.get(0).get("STEP1_STATUS") == null ||!putawayInfolist.get(0).get("STEP1_STATUS").equals("S")) {
					Map<String, Object> step1map = new HashMap<String, Object>();
					step1map.put("WH_NUMBER", item.get("WH_NUMBER"));
					step1map.put("FROMWERKS", item.get("WERKS_FROM"));
					step1map.put("WERKS", item.get("WERKS"));
					
					int itemNo = 10;
					for (Map<String,Object> info : putawayInfolist) {
						info.put("ITEM_NO", String.format("%5d", itemNo).replace(" ", "0"));
						info.put("RECEIVE_QTY", info.get("QTY"));
						itemNo = itemNo + 10;
					}
					step1map.put("ITEMLIST", JSONArray.toJSONString(putawayInfolist));
					
					returnMap = wmsInAutoPutawayService.createPO(step1map);
					
					//保存日志和状态
					poNumber = returnMap.get("PO_NUMBER") == null ? "":returnMap.get("PO_NUMBER").toString();
					String message = returnMap.get("MESSAGE") == null ? "":returnMap.get("MESSAGE").toString();
					
					List<Map<String,String>> statusList = new ArrayList<Map<String,String>>();
					Map<String, String> statusparam = new HashMap<String, String>();
					if (!poNumber.equals("")) {
						statusparam.put("STEP1_STATUS", "S");
						statusparam.put("EBELN", poNumber);
						statusparam.put("MSG1", "  ");
						
					} else {
						statusparam.put("STEP1_STATUS", "E");
						statusparam.put("MSG1", message.substring(0, message.length()>300?300:message.length()));
					}	
					statusparam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
					statusparam.put("WH_NUMBER", whnumber);
					statusparam.put("TIME_STAMP_STR", timeStampStr);
					statusList.add(statusparam);
					wmsInAutoPutawayService.updatePutawayStatus(statusList);
					
					if (!poNumber.equals("")) {
						for (Map<String,Object> info : putawayInfolist) {
							info.put("EBELN", poNumber);
							info.put("EBELP", info.get("ITEM_NO"));
						}
						
						wmsInAutoPutawayService.updatePutawayLog(putawayInfolist);
					}
					
					if (poNumber.equals("")) {
						successflag = false;
						continue;
					}
					
				}
				
				//执行第二步前先检查是否已成功
				if (putawayInfolist.get(0).get("STEP2_STATUS") == null ||!putawayInfolist.get(0).get("STEP2_STATUS").equals("S")) {
					Thread.sleep(1000);
					Map<String, Object> step2map = new HashMap<String, Object>();
					step2map.put("WH_NUMBER", whnumber);
					step2map.put("TIME_STAMP_STR", timeStampStr);
					step2map.put("EBELN", item.get("EBELN"));
					step2map.put("WERKS", werks);
					
					returnMap = wmsInAutoPutawayService.createDN(step2map);
					
					String delivery = returnMap.get("DELIVERY") == null ? "":returnMap.get("DELIVERY").toString();
					if (delivery.equals("")) {
						successflag = false;
						continue;
					}
				}
				
				//执行第三步前先检查是否已成功
				if (putawayInfolist.get(0).get("STEP3_STATUS") == null ||!putawayInfolist.get(0).get("STEP3_STATUS").equals("S")) {
					Map<String, Object> step3map = new HashMap<String, Object>();
					step3map.put("WH_NUMBER", whnumber);
					step3map.put("TIME_STAMP_STR", timeStampStr);
					step3map.put("WERKS", werks);
					step3map.put("USERNAME", currentUser.get("USERNAME"));
					step3map.put("FULL_NAME", currentUser.get("FULL_NAME"));
					step3map.put("CREATOR", currentUser.get("USERNAME"));
					step3map.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
					
					returnMap = wmsInAutoPutawayService.postDN(step3map);
					
					wmsno = returnMap.get("WMS_NO") == null ? "":returnMap.get("WMS_NO").toString();
					String receiptno = returnMap.get("RECEIPT_NO") == null ? "":returnMap.get("RECEIPT_NO").toString();
					String iqcno = returnMap.get("INSPECTION_NO") == null ? "":returnMap.get("INSPECTION_NO").toString();
					sapdoc = returnMap.get("MAT_DOC") == null ? "":returnMap.get("MAT_DOC").toString();
					String message = returnMap.get("MESSAGE") == null ? "":returnMap.get("MESSAGE").toString();
					
					//保存日志和状态
					List<Map<String,String>> statusList = new ArrayList<Map<String,String>>();
					Map<String, String> statusparam = new HashMap<String, String>();
					if (wmsno.equals("")) {
						statusparam.put("STEP3_STATUS", "E");
						statusparam.put("MSG3", message.substring(0, message.length()>300?300:message.length()));
						successflag = false;
					} else {
						statusparam.put("STEP3_STATUS", "S");
						statusparam.put("RECEIPT_NO", receiptno);
						statusparam.put("INSPECTION_NO", iqcno);
						statusparam.put("WMS_NO", wmsno);
						statusparam.put("MAT_DOC", sapdoc);
						statusparam.put("MSG3", " ");
					}
					statusparam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
					statusparam.put("WH_NUMBER", whnumber);
					statusparam.put("TIME_STAMP_STR", timeStampStr);
					statusList.add(statusparam);
					wmsInAutoPutawayService.updatePutawayStatus(statusList);
				}
				
				//WMS过账成功的，SAP空凭证的情况下，再次过账SAP。
				if(sapdoc.equals("")) {
					Map<String, Object> step4map = new HashMap<String, Object>();
					step4map.put("WH_NUMBER", whnumber);
					step4map.put("TIME_STAMP_STR", timeStampStr);
					step4map.put("WERKS", werks);
					step4map.put("VBELN", putawayInfolist.get(0).get("VBELN"));
					step4map.put("WMS_NO", wmsno);
					step4map.put("PZ_DATE", putawayInfolist.get(0).get("PZ_DATE"));
					step4map.put("JZ_DATE", putawayInfolist.get(0).get("JZ_DATE"));
					wmsInAutoPutawayService.postDNAgain(step4map);
				}
				
				//根据操作时长判断是否在PDA操作的，则保存条码日志
				if (putawayInfolist.get(0).get("OPERATION_TIME")!= null && !putawayInfolist.get(0).get("OPERATION_TIME").equals("")) {
					
					String refdoc = putawayInfolist.get(0).get("DOC_NO").toString();
					List<Map<String, Object>> lablogList = new ArrayList<Map<String, Object>>();//更新条码日志准备的
					
					List<Map<String,Object>> doclist = commonDao.getWmsDocList(refdoc);
					for (Map<String,Object> docitem : doclist) {
						
						List<Map<String,Object>> laberparams = new ArrayList<Map<String,Object>>();
						
						String [] labels = docitem.get("LABEL_NO").toString().split(",");
						for(int i=0;i<labels.length; i++) {
							Map<String,Object> lparam = new HashMap<String,Object>();
							lparam.put("LABEL_NO", labels[i]);
							laberparams.add(lparam);
						}
						List<Map<String,Object>> laberinfolist = commonDao.getLabelInfoBatch(laberparams);
						for (Map<String,Object> laberinfo : laberinfolist) {
							Map<String,Object> lablog = new HashMap<String,Object>();
							lablog.putAll(docitem);
							lablog.put("LABEL_NO", laberinfo.get("LABEL_NO"));
							lablog.put("QTY", laberinfo.get("BOX_QTY"));
							lablog.put("CLIENT", docitem.get("HANDLE_FTU"));
							lablog.put("BUSINESS_NO", refdoc);
							lablogList.add(lablog);
						}
					}
					commonDao.insertBarcodeLog(lablogList);
				}
			}
			
			if (successflag) {
				return R.ok();
			} else {
				return R.error("有失败数据!  请稍后查询日志记录.");
			}
		} catch (Exception e) {
			return R.error("系统异常："+e.getMessage());	
		}	
	}
	
}
