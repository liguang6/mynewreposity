package com.byd.wms.business.modules.inpda.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.ListUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.in.service.WmsInAutoPutawayService;
import com.byd.wms.business.modules.inpda.service.WmsAutoPutawayPdaService;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;

/**
 * PDA一步联动收货
 * @author ren.wei3
 *
 */

@Service("wmsAutoPutawayPdaService")
public class WmsAutoPutawayPdaServiceImpl implements WmsAutoPutawayPdaService{

	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsSapRemote wmsSapRemote;
	@Autowired
    private UserUtils userUtils;
	@Autowired
	private WmsInAutoPutawayService wmsInAutoPutawayService;
	@Autowired
	private WmsCTxtService wmsCtxService;
	@Autowired
	WmsOutRequirementItemDao itemDAO;
	
	/**
	 * 扫描条码
	 */
	@Override
	public Map<String, Object> scan(Map<String, Object> params) {
		Map<String, Object> resMap=new HashMap<String, Object>();
		
		if (params.get("LABEL_NO").equals("")) {
			throw new IllegalArgumentException("请扫描条码！");
		}
		
		if (params.get("REF_DOC_NO").equals("")) {
			throw new IllegalArgumentException("参考单据为空！");
		}
		
		if (params.get("fromWerks").equals("")) {
			throw new IllegalArgumentException("调出工厂为空！");
		}
		
		String refdoc = params.get("REF_DOC_NO").toString();
		String fromWerks = params.get("fromWerks").toString();
		
		//获取条码缓存表数据
		Map<String, Object> cparams = new HashMap<String, Object>();
		cparams.putAll(params);
		cparams.put("LABEL_NO", "");
		cparams.put("CREATOR", params.get("USER_NAME"));
    	List<Map<String,Object>> labelCachelist = commonService.getLabelCacheInfoNoPage(cparams);
    	for (Map<String,Object> labelCache:labelCachelist) {
    		if (!labelCache.get("REF_DOC_NO").equals(refdoc)) {
    			throw new IllegalArgumentException("单据："+labelCache.get("REF_DOC_NO")+"未完成");
    		}
    		if (labelCache.get("LABEL_NO").equals(params.get("LABEL_NO"))) {
        		throw new IllegalArgumentException("已存在，勿重复扫描！");
        	}
    	}
    	
    	Map<String, Object> lparams = new HashMap<String, Object>();
    	lparams.putAll(params);
    	lparams.put("WH_NUMBER", "");
		List<Map<String,Object>> labellist = commonService.getLabelInfo(lparams);
    	if (labellist.size() < 1) {
    		throw new IllegalArgumentException("未找到条码！");
    	}
    	
    	//01已收料（待质检），02已收料（无需质检）03待进仓(已质检)，04待退货(已质检)  这几个状态的条码，不能再扫描。
    	if(labellist.get(0).get("LABEL_STATUS").equals("01") || labellist.get(0).get("LABEL_STATUS").equals("02")
    			|| labellist.get(0).get("LABEL_STATUS").equals("03") || labellist.get(0).get("LABEL_STATUS").equals("04")) {
    		throw new IllegalArgumentException("条码已收货！");
    	}
    	
    	Map<String, Object> bparams = new HashMap<String, Object>();
    	bparams.put("BUSINESS_NO", refdoc);
    	List<Map<String,Object>> barcodelist = commonService.getBarcodeLog(bparams);
    	if (barcodelist.size() > 0) {
    		throw new IllegalArgumentException("参考单据已收过货！");
    	}
    	
    	//验证条码是否属于参考单据中物料
//    	List<Map<String,Object>> list = commonService.getWmsDocList(refdoc);
    	Map<String,Object> keyMap = new HashMap<String,Object>();
		keyMap.put("REQUIREMENT_NO",refdoc);
		List<Map<String,Object>> list = itemDAO.queryRequirementItem(keyMap);
		
    	if (list.size() > 0) {
    		boolean err = false;
    		for (Map<String,Object> item : list) {
    			if (!item.get("WERKS").equals(fromWerks.toUpperCase())) {
    				throw new IllegalArgumentException("发货工厂与需求中工厂不一致，请确认！");
				}
    			
    			if (item.get("MATNR").equals(labellist.get(0).get("MATNR"))) {
    				labellist.get(0).put("REF_DOC_ITEM", item.get("REQUIREMENT_ITEM_NO"));
    				err = true;
    				break;
				}
    		}
    		
    		if (!err) {
        		throw new IllegalArgumentException("条码不属于该需求号");
        	}
    		
    	} else {
    		boolean err = false;
    		String docYear = "";
    		if (docYear.equals("")) {
				docYear = DateUtils.format(new Date(),DateUtils.DATE_PATTERN).substring(0, 4);
			}
			Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(refdoc,docYear);
			if (null != rm.get("CODE") && !rm.get("CODE").equals("")) {
        		throw new IllegalArgumentException("SAP凭证不存在！");
        	}
			
			List<Map<String,Object>> itemList =(List<Map<String, Object>>) rm.get("GOODSMVT_ITEMS");
			
			for (Map<String,Object> item : itemList) {
				if (!item.get("X_AUTO_CRE").equals("X")) {
					if (!item.get("WERKS").equals(fromWerks.toUpperCase())) {	
						throw new IllegalArgumentException("发货工厂与凭证中工厂不一致！");
					}
					
					if (item.get("MATNR").equals(labellist.get(0).get("MATNR"))) {
						labellist.get(0).put("REF_DOC_ITEM", item.get("MATDOC_ITM"));
	    				err = true;
	    				break;
					}
				}
			}
			
			if (!err) {
        		throw new IllegalArgumentException("条码不属于该凭证");
        	}
    	}
    	
    	for (Map<String, Object> label:labellist) {
    		label.put("WH_NUMBER", params.get("WH_NUMBER"));
    		label.put("MENU_KEY", "PDA_GR_01");
    		label.put("QTY", label.get("BOX_QTY"));
    		label.put("CREATOR", params.get("USER_NAME"));
    		label.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		label.put("REF_DOC_NO", refdoc);
    	}
    	
    	commonService.saveLabelCacheInfo(labellist);
    	
    	int boxs = commonService.getLabelCacheInfoCount(cparams);
    	
    	resMap.put("result", labellist);
    	resMap.put("boxs", boxs);
    	return resMap;
	}
	
	/**
	 * 获取参考凭证明细
	 */
	@Override
	public PageUtils docItem(Map<String, Object> params) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		params.put("CREATOR", params.get("USER_NAME"));
		List<Map<String,Object>> labelCachelist = commonService.getLabelCacheInfoNoPage(params);
		
		if(labelCachelist.size() > 0) {
			String refdoc = labelCachelist.get(0).get("REF_DOC_NO").toString();

			Map<String,Object> keyMap = new HashMap<String,Object>();
			keyMap.put("REQUIREMENT_NO",refdoc);
			list = itemDAO.queryRequirementItem(keyMap);
			
			if (list.size() > 0) {//参考单号为WMS需求
	    		for (Map<String,Object> item : list) {
	    			int ibox = 1;
	    			item.put("DOC_NO", refdoc);
	    			item.put("DOC_ITEM", item.get("REQUIREMENT_ITEM_NO"));
	    			for (Map<String,Object> clable : labelCachelist) {
	    				if (clable.get("REF_DOC_ITEM").equals(item.get("REQUIREMENT_ITEM_NO"))) {
	    					BigDecimal lqty = clable.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(clable.get("QTY").toString()); //单个条码数量
	    					BigDecimal sqty = item.get("SUM_QTY") == null ? BigDecimal.ZERO:new BigDecimal(item.get("SUM_QTY").toString()); //总数
	    					item.put("SUM_QTY", sqty.add(lqty));
	    					item.put("BOXS", ibox++);
		    			}
	    			}
	    		}
			} else { //参考单号为SAP凭证
				String docYear = "";
	    		if (docYear.equals("")) {
					docYear = DateUtils.format(new Date(),DateUtils.DATE_PATTERN).substring(0, 4);
				}
				Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(refdoc,docYear);
				if (null != rm.get("CODE") && !rm.get("CODE").equals("")) {
	        		throw new IllegalArgumentException("凭证不存在！");
	        	}
				
				List<Map<String,Object>> itemList =(List<Map<String, Object>>) rm.get("GOODSMVT_ITEMS");
				
				for (Map<String,Object> item : itemList) {
					
					if (!item.get("X_AUTO_CRE").equals("X")) {
						
						int ibox = 1;
						item.put("DOC_NO", refdoc);
						item.put("DOC_ITEM", item.get("MATDOC_ITM"));
						for (Map<String,Object> clable : labelCachelist) {
		    				if (clable.get("REF_DOC_ITEM").equals(item.get("MATDOC_ITM"))) {
		    					BigDecimal lqty = clable.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(clable.get("QTY").toString()); //单个条码数量
		    					BigDecimal sqty = item.get("SUM_QTY") == null ? BigDecimal.ZERO:new BigDecimal(item.get("SUM_QTY").toString()); //总数
		    					
		    					item.put("SUM_QTY", sqty.add(lqty));
		    					item.put("BOXS", ibox++);
			    			}
		    			}
						
						list.add(item);
					}
				}
			}
		}
		
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
        return new PageUtils(page);
	}
	
	@Override
	public PageUtils labelItem(Map<String, Object> params) {
		params.put("CREATOR", params.get("USER_NAME"));
		List<Map<String,Object>> labelCachelist = commonService.getLabelCacheInfoNoPage(params);
		
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(labelCachelist);
		page.setTotal(labelCachelist.size());
        return new PageUtils(page);
	}
	
	@Override
	public void deleteLabel(Map<String, Object> params) {
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>) JSONArray.parse(params.get("deleteList").toString());
    	for (Map<String, Object> param : deleteList) {
    		param.put("WH_NUMBER", params.get("WH_NUMBER"));
    		param.put("CREATOR", params.get("USER_NAME"));
    		param.put("MENU_KEY", params.get("MENU_KEY"));
    	}
		
    	commonService.deleteLabelCacheInfo(deleteList);
	}
	
	/**
	 * 过账
	 */
	public Map<String, Object> confirm(Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		Map<String, Object> resultmap = new HashMap<String, Object>();
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(); //原始参考凭证行信息
		List<Map<String,Object>> newlist = new ArrayList<Map<String,Object>>(); //为过账准备的
		List<Map<String, Object>> labList = new ArrayList<Map<String, Object>>();//更新条码准备的
		List<Map<String, Object>> lablogList = new ArrayList<Map<String, Object>>();//更新条码日志准备的
		
		params.put("CREATOR", params.get("USER_NAME"));
		List<Map<String,Object>> labelCachelist = commonService.getLabelCacheInfoNoPage(params);
		
		//检查凭证行数量和已扫总数是否相等
		if(labelCachelist.size() > 0) {
			String refdoc = labelCachelist.get(0).get("REF_DOC_NO").toString();
			params.put("MAT_DOC", refdoc);
//			list = commonService.getWmsDocList(refdoc);
			
			Map<String,Object> keyMap = new HashMap<String,Object>();
			keyMap.put("REQUIREMENT_NO",refdoc);
			list = itemDAO.queryRequirementItem(keyMap);
			
			if (list.size() > 0) {//参考单号为WMS凭证
	    		for (Map<String,Object> item : list) {
	    			item.put("QTY_WMS", item.get("QTY_REAL"));
	    			
	    			for (Map<String,Object> clable : labelCachelist) {
	    				
	    				if (clable.get("REF_DOC_ITEM").equals(item.get("REQUIREMENT_ITEM_NO"))) {
	    					BigDecimal lqty = clable.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(clable.get("QTY").toString()); //单个条码数量
	    					BigDecimal sqty = item.get("SUM_QTY") == null ? BigDecimal.ZERO:new BigDecimal(item.get("SUM_QTY").toString()); //总数
	    					item.put("SUM_QTY", sqty.add(lqty));
	    					
	    					clable.put("FROM_WERKS", item.get("WERKS"));
	    					clable.put("FROM_LGORT", item.get("LGORT"));
		    			}
	    				
	    			}
	    		}
			} else { //参考单号为SAP凭证
				String docYear = "";
	    		if (docYear.equals("")) {
					docYear = DateUtils.format(new Date(),DateUtils.DATE_PATTERN).substring(0, 4);
				}
				Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(refdoc,docYear);
				if (null != rm.get("CODE") && !rm.get("CODE").equals("")) {
	        		throw new IllegalArgumentException("凭证不存在！");
	        	}
				
				List<Map<String,Object>> itemList =(List<Map<String, Object>>) rm.get("GOODSMVT_ITEMS");
				
				for (Map<String,Object> item : itemList) {
					
					if (!item.get("X_AUTO_CRE").equals("X")) {
						
						for (Map<String,Object> clable : labelCachelist) {
		    				if (clable.get("REF_DOC_ITEM").equals(item.get("MATDOC_ITM"))) {
		    					BigDecimal lqty = clable.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(clable.get("QTY").toString()); //单个条码数量
		    					BigDecimal sqty = item.get("SUM_QTY") == null ? BigDecimal.ZERO:new BigDecimal(item.get("SUM_QTY").toString()); //总数
		    					item.put("SUM_QTY", sqty.add(lqty));
		    					clable.put("FROM_WERKS", item.get("WERKS"));
		    					clable.put("FROM_LGORT", item.get("STGE_LOC"));
		    					clable.put("WMS_ITEM_NO", item.get("MATDOC_ITM"));
			    			}
		    			}
						item.put("WMS_ITEM_NO", item.get("MATDOC_ITM"));
						item.put("QTY_WMS", item.get("ENTRY_QNT"));
						list.add(item);
					}
				}
			}
			
			for (Map<String,Object> item : list) {
				BigDecimal docqty = item.get("QTY_WMS") == null ? BigDecimal.ZERO:new BigDecimal(item.get("QTY_WMS").toString()); //凭证数量
				BigDecimal scanqty = item.get("SUM_QTY") == null ? BigDecimal.ZERO:new BigDecimal(item.get("SUM_QTY").toString()); //扫描总数
				if (docqty.compareTo(scanqty) != 0) {
					throw new IllegalArgumentException(item.get("WMS_ITEM_NO")+":已扫数量与凭证数量不一致");
				}
			}
			
			//按批次分组合计过账数量
			int itemNo = 10;
			List<List<Map<String, Object>>> grouplists = ListUtils.getListByGroup(labelCachelist,"BATCH");
			for (List<Map<String, Object>> glist : grouplists) {
				Map<String,Object> postmap = new HashMap<String,Object>();
				StringBuffer LABEL_NO_BF = new StringBuffer();
				
				for (Map<String, Object> blist : glist) {
					if (postmap.isEmpty()) {
						postmap.putAll(blist);
					} else {
						BigDecimal lqty = blist.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(blist.get("QTY").toString()); //单个条码数量
						BigDecimal sqty = postmap.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(postmap.get("QTY").toString()); //已合计数量
						postmap.put("QTY", sqty.add(lqty));
					}
					LABEL_NO_BF.append(blist.get("LABEL_NO")).append(",");
					
					Map<String, Object> labMap = new HashMap<String, Object>();
					labMap.put("LABEL_NO", blist.get("LABEL_NO")); 
					labMap.put("WH_NUMBER", params.get("WH_NUMBER")); 
					labMap.put("LABEL_STATUS", "01");
					labList.add(labMap);
				}
				
				postmap.put("RECEIVE_QTY", postmap.get("QTY").toString());
				postmap.put("ITEM_NO", String.format("%5d", itemNo).replace(" ", "0"));
				params.put("FROMWERKS", postmap.get("FROM_WERKS"));
				itemNo = itemNo + 10;
				
				//读取配置设置 头文本和行文本
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
						postmap.put("ITEM_TEXT", txtruleitem);
						postmap.put("TXTRULE", txtrule);
					}
				}
				
				postmap.put("QTY_WMS", postmap.get("QTY"));
				postmap.put("LABEL", LABEL_NO_BF);
				postmap.put("HANDLE_FTU", "02");
				postmap.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
				postmap.put("DELIVERY_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN_POINT));
				newlist.add(postmap);
			}
			
			//获取作业时长
			ListUtils.sort(labelCachelist, "CREATE_DATE", true);
			String operationTime = labelCachelist.get(0).get("CREATE_DATE").toString();
			Date opd = DateUtils.stringToDate(operationTime, DateUtils.DATE_TIME_PATTERN);
			Date currentdate = new Date();
			long diff = currentdate.getTime() - opd.getTime();
		    long days = diff / (1000 * 60 * 60 * 24);  
		    long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);  
		    long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60); 
		    long seces = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60)-minutes*(1000*60))/1000;
		    
		    StringBuffer operationTimeStr = new StringBuffer();
		    if(days>0)
		    	operationTimeStr.append(days+"天");
		    if(hours>0)
		    	operationTimeStr.append(hours+"小时");
		    if(minutes>0)
		    	operationTimeStr.append(minutes+"分");
		    if(seces>0)
		    	operationTimeStr.append(seces+"秒");
		    
			//开始一步联动收货逻辑
			try{
				String wmsno = "";
				String sapdoc = "";
				String receiptno = "";
				String iqcno = "";
				Map<String, Object> retMap=new HashMap<String, Object>();
				params.put("USERNAME", currentUser.get("USERNAME"));
			   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
			   	params.put("CREATOR", currentUser.get("USERNAME"));
			   	params.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			   	params.put("OPERATION_TIME", operationTimeStr);
			   	params.put("ITEMLIST", JSONArray.toJSONString(newlist));
			   	
			   	//创建PO
				retMap = wmsInAutoPutawayService.createPO(params);
				
				String poNumber = retMap.get("PO_NUMBER") == null ? "":retMap.get("PO_NUMBER").toString();
				String message = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
				//保存日志和状态
				params.put("PO_NUMBER", poNumber);
				params.put("MESSAGE", message);
				String timestampstr = wmsInAutoPutawayService.saveStepLog(params);
				
				//删除条码缓存表
				List<Map<String, Object>> delparams = new ArrayList<Map<String, Object>>();
				delparams.add(params);
				commonService.deleteLabelCacheInfo(delparams);
				//更新条码
				commonService.updateLabel(labList);
				
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
					receiptno = retMap.get("RECEIPT_NO") == null ? "":retMap.get("RECEIPT_NO").toString();
					iqcno = retMap.get("INSPECTION_NO") == null ? "":retMap.get("INSPECTION_NO").toString();
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
					throw new IllegalArgumentException("收货失败！原因：" + message);
				}
				
				//保存出入库条码日志
				List<Map<String,Object>> doclist = commonService.getWmsDocList(wmsno);
				for (Map<String,Object> docitem : doclist) {
					
					List<Map<String,Object>> laberparams = new ArrayList<Map<String,Object>>();
					
					String [] labels = docitem.get("LABEL_NO").toString().split(",");
					for(int i=0;i<labels.length; i++) {
						Map<String,Object> lparam = new HashMap<String,Object>();
						lparam.put("LABEL_NO", labels[i]);
						laberparams.add(lparam);
					}
					List<Map<String,Object>> laberinfolist = commonService.getLabelInfoBatch(laberparams);
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
				commonService.insertBarcodeLog(lablogList);
				
				resultmap.put("receiptno", receiptno);
				resultmap.put("iqcno", iqcno);
				resultmap.put("wmsnostr", wmsno);
				resultmap.put("sapdoc", sapdoc);
				resultmap.put("operationTime", operationTimeStr);
				
			} catch (Exception e) {
				throw new IllegalArgumentException("系统异常："+e.getMessage());
			}
			
		} else {
			throw new IllegalArgumentException("无过账数据");
		}
		
		return resultmap;
	}
}
