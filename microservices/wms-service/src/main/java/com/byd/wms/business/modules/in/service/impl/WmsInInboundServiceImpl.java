package com.byd.wms.business.modules.in.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.service.WmsInInboundService;

@Service("wmsInInboundService")
public class WmsInInboundServiceImpl implements WmsInInboundService {

	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	
	@Autowired
    WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WarehouseTasksService warehouseTasksService;
	@Autowired
    WmsCWhService wmsCWhService;
	@Autowired
	CommonService commonService;
	
	@Override
	public List<Map<String, Object>> getDeptNameByWerk(Map<String, Object> map) {
		return wmsInInboundDao.getDeptNameByWerk(map);
	}
	
	//根据工厂、仓库号、仓管员获取待进仓的来料任务清单
	@Override
	public List<Map<String,Object>> getInboundTasks(Map<String, Object> params){
		List<Map<String, Object>> userInboundTasks = new ArrayList<Map<String, Object>>();
    	//1、根据质检员查询授权码、物料管理方式 清单
		List<Map<String, Object>> matManagerAuthCodeList = commonService.getMatManagerAuthCodeList(params);
		StringBuffer userTypeAuthCode = new StringBuffer();
		StringBuffer lgortTypeAuthCode = new StringBuffer();
		StringBuffer WERKS = new StringBuffer();
		matManagerAuthCodeList.forEach(map -> {
			map = (Map<String, Object>)map;
			String MAT_MANAGER_TYPE = map.get("MAT_MANAGER_TYPE")==null?"":map.get("MAT_MANAGER_TYPE").toString();
			if("00".equals(MAT_MANAGER_TYPE)) {
				params.put("CONDMG", "3");
				WERKS.append( map.get("WERKS")==null?"":map.get("WERKS").toString() );
				userTypeAuthCode.append(map.get("AUTHORIZE_CODE")==null?"":map.get("AUTHORIZE_CODE").toString()+",");
			}
			if("20".equals(MAT_MANAGER_TYPE)) {
				params.put("CONDMG", "4");
				WERKS.append( map.get("WERKS")==null?"":map.get("WERKS").toString() );
				lgortTypeAuthCode.append(map.get("AUTHORIZE_CODE")==null?"":map.get("AUTHORIZE_CODE").toString()+",");
			}
		});
		
		params.put("WERKS", WERKS.toString());
    	
		if(userTypeAuthCode.toString().length()>0) {
			//2、物料管理方式-00（人） 根据授权码查询物料管理模式可进仓收货单信息
			params.put("AUTHORIZE_CODE", userTypeAuthCode.toString());
			List<Map<String, Object>> list = wmsInInboundDao.queryReceiptByAuthCode(params);
			userInboundTasks.addAll(list);
		}
    	
		if(lgortTypeAuthCode.toString().length()>0) {
			//3、物料管理方式-20（库存地点） 根据库存地点查询可进仓收货单信息
			params.put("LGORT", lgortTypeAuthCode.toString());
			List<Map<String, Object>> list = wmsInInboundDao.queryReceiptByLgort(params);
			userInboundTasks.addAll(list);
		}
		
		return userInboundTasks;
	}
	
	/**
	 * 查询可进仓的外购收货单信息
	 */
	@Override
	public List<Map<String, Object>> getReceiptList(Map<String, Object> map) {
		//根据仓管员条件，通过后台的sql拼接，选择带出仓管员的结果集
		/*仓管员为空且 WMS_C_MAT_MANAGER_TYPE表的MAT_MANAGER_TYPE为00，则参数  CONDMG=1
		 * 仓管员为空且 WMS_C_MAT_MANAGER_TYPE表的MAT_MANAGER_TYPE为20，则参数  CONDMG=2
		 * 仓管员不为空且 WMS_C_MAT_MANAGER_TYPE表的MAT_MANAGER_TYPE为00，则参数  CONDMG=3
		 * 仓管员不为空且 WMS_C_MAT_MANAGER_TYPE表的MAT_MANAGER_TYPE为20，则参数  CONDMG=4
		 * WMS_C_MAT_MANAGER_TYPE表的MAT_MANAGER_TYPE没配置 ，则参数  CONDMG=0
		 */
		
		List<Map<String,Object>> relatedAreaName=wmsInInboundDao.getRelatedAreaName(map);//根据工厂仓库号查询
		if(relatedAreaName!=null&&relatedAreaName.size()>0){
			if("".equals(map.get("RELATED_AREA_NAME"))){//仓管员查询条件为空，即全部
				if("00".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					map.put("CONDMG", "1");
				}else if("20".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					map.put("CONDMG", "2");
				}else{
					map.put("CONDMG", "0");
				}
			}else{//仓管员有值
				if("00".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					map.put("CONDMG", "3");
				}else if("20".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					map.put("CONDMG", "4");
				}else{
					map.put("CONDMG", "0");
				}
			}
		}else{
			map.put("CONDMG", "0");
		}
		
		List<Map<String,Object>> receiptList=wmsInInboundDao.getReceiptList(map);
		if(receiptList!=null&&receiptList.size()>0){
			//查询  105过账需要参考103凭证 需要的 REF_SAP_MATDOC_NO  REF_SAP_MATDOC_ITEM_NO  REF_SAP_MATDOC_YEAR ,
			//20190611 增加列表是否 是委外物料，查询出wms_sap_po_item表的PSTYP字段
			//20190620 thw 优化为批量查询相关信息
			List<Map<String,Object>> retwmsdocList = wmsInInboundDao.getWmsDocItemByReceiptNo(receiptList);
			for(Map<String,Object> retMap:receiptList){
				retMap.put("PSTYP", "");
				if(retMap.get("RECEIPT_NO")!=null&&retMap.get("RECEIPT_ITEM_NO")!=null){
					String RECEIPT_NO = retMap.get("RECEIPT_NO").toString();
					String RECEIPT_ITEM_NO = retMap.get("RECEIPT_ITEM_NO").toString();
					for (Map<String, Object> retMap2 : retwmsdocList) {
						String RECEIPT_NO2 = retMap2.get("RECEIPT_NO").toString();
						String RECEIPT_ITEM_NO2 = retMap2.get("RECEIPT_ITEM_NO").toString();
						if(RECEIPT_NO.equals(RECEIPT_NO2) && RECEIPT_ITEM_NO.equals(RECEIPT_ITEM_NO2)) {
							String MAT_DOC = retMap2.get("MAT_DOC")==null?"":retMap2.get("MAT_DOC").toString();
							String MAT_DOC_ITEM_STR = retMap2.get("MAT_DOC_ITEM")==null?"":retMap2.get("MAT_DOC_ITEM").toString();
							String[] MAT_DOC_ITEM_ARR = MAT_DOC_ITEM_STR.split(":");
							String MAT_DOC_ITEM = MAT_DOC_ITEM_ARR.length == 2?MAT_DOC_ITEM_ARR[1]:"";
							String DOC_YEAR = retMap2.get("DOC_YEAR")==null?"":retMap2.get("DOC_YEAR").toString();
							
							String PSTYP = retMap2.get("PSTYP")==null?"":retMap2.get("PSTYP").toString();
							
							retMap.put("REF_SAP_MATDOC_NO", MAT_DOC);
							retMap.put("REF_SAP_MATDOC_ITEM_NO", MAT_DOC_ITEM);
							retMap.put("REF_SAP_MATDOC_YEAR", DOC_YEAR);
							retMap.put("PSTYP", PSTYP);
							
							break;
						}
					}
				}
				
				//SAP交货单收货业务（收货单字段BUSINESS_NAME=03，BUSINESS_TYPE=06）创建进仓单
				//2019072 改为都保存
				//if("03".equals(retMap.get("BUSINESS_NAME"))&&"06".equals(retMap.get("BUSINESS_TYPE"))){
					retMap.put("F_WERKS", retMap.get("WERKS"));
					retMap.put("F_WH_NUMBER", retMap.get("WH_NUMBER"));
					retMap.put("F_LGORT", retMap.get("LGORT"));
				//}
			}
			
			
		}
		return receiptList;
	}

	//根据工厂和仓库号获取物料管理模式-授权码
	@Override
	public List<Map<String, Object>> getRelatedAreaName(Map<String, Object> map) {
		List<Map<String,Object>> relatedAreaName=wmsInInboundDao.getRelatedAreaName(map);
		return relatedAreaName;
	}

	/**
	 * 创建外购进仓单
	 */
	@SuppressWarnings("finally")
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String saveInbound(Map<String, Object> params) throws Exception {
		//ModBy:YK190528 (BUG 540)不启用条码的仓库号 创建进仓单 不需要保存条码数据。
		String barCodeFlag = params.get("BARCODE_FLAG")==null?"0":params.get("BARCODE_FLAG").toString();
		List<Map<String, Object>> Labellist=new ArrayList<Map<String, Object>>();
		//1、生成外购进仓单编号
		Map<String, Object> docParma=new HashMap<String, Object>();//WMS_DOC_TYPE
    	docParma.put("WMS_DOC_TYPE", "07");//进仓单
    	Map<String, Object> retNo=wmsCDocNoService.getDocNo(docParma);
    	String docNo="";
    	if("success".equals(retNo.get("MSG"))){
    		docNo=retNo.get("docno").toString();
    	}
    	if(docNo.equals("")) {
    		return docNo;
    	}
    	
		// ARRLIST
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		
		//2、封装进仓单抬头数据-WMS_IN_INBOUND_HEAD
		
		Map<String, Object> tempHeadmap=new HashMap<String, Object>();
		tempHeadmap.put("INBOUND_NO", docNo);
		tempHeadmap.put("INBOUND_TYPE", "00");//进仓单类型 00 外购进仓单 01 自制进仓单
		tempHeadmap.put("INBOUN_STATUS", "00");//创建状态
		tempHeadmap.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
		tempHeadmap.put("WH_NUMBER", jarr.getJSONObject(0).getString("WH_NUMBER"));
		tempHeadmap.put("HEADER_TXT", jarr.getJSONObject(0).getString("HEADER_TXT"));
		tempHeadmap.put("IS_AUTO", "0");
		tempHeadmap.put("DEL", "0");
		tempHeadmap.put("CREATOR", params.get("USERNAME").toString());
		tempHeadmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		tempHeadmap.put("QC_RESULT", jarr.getJSONObject(0).getString("QC_RESULT"));
		
		wmsInInboundDao.insertWmsInboundHead(tempHeadmap);
		
		//3、封装进仓行项目数据- WMS_IN_INBOUND_ITEM
		List<Map<String, Object>> itemlist=new ArrayList<Map<String, Object>>();
		int m=0;//用于试装数量行项目
		for(int i=0;i<jarr.size();i++){
			
			Map<String, Object> tempmap=new HashMap();
			
			String IN_QTY=jarr.getJSONObject(i).getString("IN_QTY");//进仓数量
			BigDecimal IN_QTY_D=BigDecimal.ZERO;//进仓数量
			if(!"".equals(IN_QTY)&&IN_QTY!=null){
				IN_QTY_D=new BigDecimal(IN_QTY);
			}
			
			String TRY_QTY=jarr.getJSONObject(i).getString("TRY_QTY");//试装数量
			BigDecimal TRY_QTY_D=BigDecimal.ZERO;//试装数量
			if(!"".equals(TRY_QTY)&&TRY_QTY!=null){
				TRY_QTY_D=new BigDecimal(TRY_QTY);
			}
			
			String BIN_CODE_TEMP=jarr.getJSONObject(i).getString("BIN_CODE");
			if(TRY_QTY_D.compareTo(BigDecimal.ZERO)>0){//试装数量大于0  
				IN_QTY_D=IN_QTY_D.subtract(TRY_QTY_D);//进仓数量=进仓数量-试装数量
				//试装数量再插入一行
				
				jarr.getJSONObject(i).put("INBOUND_NO", docNo);
				jarr.getJSONObject(i).put("INBOUND_ITEM_NO", i+1+m);
				jarr.getJSONObject(i).put("IN_QTY", TRY_QTY_D);//进仓数量=试装数量  插入试装行
				jarr.getJSONObject(i).put("BIN_CODE", jarr.getJSONObject(i).getString("TRY_BIN_CODE"));
				jarr.getJSONObject(i).put("CREATOR", params.get("USERNAME").toString());
				jarr.getJSONObject(i).put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				Map<String, Object> tempmap_try=insertMapByJsonObject(jarr.getJSONObject(i));  
				tempmap_try.put("TRY_ROW_FLAG", "X");
				tempmap_try.put("TRY_BIN_CODE", jarr.getJSONObject(i).getString("TRY_BIN_CODE"));
				
				itemlist.add(tempmap_try);
				if(IN_QTY_D.compareTo(BigDecimal.ZERO)>0){//20190719 添加此判断
					m++;
				}
				
			}
			if(IN_QTY_D.compareTo(BigDecimal.ZERO)>0){//插入试装 行完成后插入正常行
				jarr.getJSONObject(i).put("INBOUND_NO", docNo);
				jarr.getJSONObject(i).put("INBOUND_ITEM_NO", i+1+m);
				jarr.getJSONObject(i).put("IN_QTY", IN_QTY_D);
				jarr.getJSONObject(i).put("TRY_QTY", BigDecimal.ZERO);//试装数量赋值0
				jarr.getJSONObject(i).put("BIN_CODE", BIN_CODE_TEMP);
				jarr.getJSONObject(i).put("CREATOR", params.get("USERNAME").toString());
				jarr.getJSONObject(i).put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				tempmap=insertMapByJsonObject(jarr.getJSONObject(i));
				
				itemlist.add(tempmap);
			}
			
		}
		List<Map<String,Object>>  inboundItemLabelList = null;
		if("X".equals(barCodeFlag)) {
			//校验标签是否已创进仓单
			inboundItemLabelList = wmsInInboundDao.getInboundItemLabelList(itemlist);
		}
		
		//4、获取推荐上架的储位 start
		warehouseTasksService.searchBinForPutaway(itemlist);
		//报错字符串
		StringBuffer err_bf = new StringBuffer();
		
		for (int i=0;i<itemlist.size();i++) {
			Map<String,Object> item = itemlist.get(i);
			String BIN_CODE_SHELF="";
			if(item.get("BIN_CODE_SHELF")!=null){
				BIN_CODE_SHELF = item.get("BIN_CODE_SHELF").toString();
			}
			String BIN_CODE="";
			if(item.get("BIN_CODE")!=null){
				BIN_CODE=item.get("BIN_CODE").toString();
			}
			if("".equals(BIN_CODE_SHELF)||"".equals(BIN_CODE)){//BIN_CODE_SHELF或者BIN_CODE为空报错
				err_bf.append("第 "+item.get("RN").toString()+"行，批次 "+item.get("BATCH")+" 物料号 "+item.get("MATNR")+"获取推荐的储位为空，不能创建进仓单！");
				
			}
					
			//判断是否是试装行
			if(item.get("TRY_ROW_FLAG")!=null && "X".equals(item.get("TRY_ROW_FLAG"))){
				//试装行，储位不要改变,上架缓存储位等于试装储位
				item.put("AUTO_PUTAWAY_FLAG", "X");
				item.put("BIN_CODE_SHELF", item.get("TRY_BIN_CODE").toString());
				item.put("BIN_CODE", item.get("TRY_BIN_CODE").toString());
			}else{
				if(!"".equals(BIN_CODE_SHELF)&&!"".equals(BIN_CODE)&&(BIN_CODE_SHELF.equals(BIN_CODE))){
					//BIN_CODE和BIN_CODE_SHELF相等，则AUTO_PUTAWAY_FLAG表示为自动上架
					itemlist.get(i).put("AUTO_PUTAWAY_FLAG", "X");
				}else{
					itemlist.get(i).put("AUTO_PUTAWAY_FLAG", "0");
				}
			}
			
			//启用了条码
			if("X".equals(barCodeFlag)) {
				if(inboundItemLabelList!=null && inboundItemLabelList.size()>0) {
					List<String> labelLst = new ArrayList<String>();
					for (Map<String, Object> inboundItemLabelMap : inboundItemLabelList) {
						if(null != inboundItemLabelMap.get("CREATED_FLAG") && 
								"X".equals(inboundItemLabelMap.get("CREATED_FLAG").toString())){
							err_bf.append("标签号"+inboundItemLabelMap.get("LABEL_NO").toString()+"在进仓单表中已经存在，不能再次进仓！");
						}
						if(item.get("INBOUND_NO").toString().equals(inboundItemLabelMap.get("INBOUND_NO_I").toString())
								&& item.get("INBOUND_ITEM_NO").toString().equals(inboundItemLabelMap.get("INBOUND_ITEM_NO_I").toString())
								) {
							labelLst.add(inboundItemLabelMap.get("LABEL_NO").toString());
						}
					}
					//非试装行维护进仓单行项目关联的标签字符串
					if(item.get("TRY_ROW_FLAG")!=null && "X".equals(item.get("TRY_ROW_FLAG"))){//试装行
						
					}else{//非试装行
						if(labelLst.size()>0){
							String labelstr_0=labelLst.toString().substring(1,labelLst.toString().length());//截取前后[]
							labelstr_0=labelstr_0.substring(0, labelstr_0.length()-1);
							item.put("LABEL_NO", labelstr_0);
						}
					}
					
				}
				
			}
			
			//
			//用于更新标签状态数据准备
			Map<String, Object> labelmap=new HashMap();
			labelmap.put("RECEIPT_NO", itemlist.get(i).get("RECEIPT_NO"));
			labelmap.put("RECEIPT_ITEM_NO", itemlist.get(i).get("RECEIPT_ITEM_NO"));
			if("0".equals(itemlist.get(i).get("AUTO_PUTAWAY_FLAG"))){
				labelmap.put("LABEL_STATUS", "07");
			}else{//AUTO_PUTAWAY_FLAG对应的状态X
				labelmap.put("LABEL_STATUS", "08");
			}
			
			Labellist.add(labelmap);
			
		}
		//20190708 注释 是否启用条码的判断
		//if("X".equals(barCodeFlag)) {
			//更新标签表状态
			wmsInInboundDao.updateCoreLabel(Labellist);
			
		//}
		
		if(err_bf.toString().length()>0) {
			throw new RuntimeException(err_bf.toString());
		}
		//创建进仓单行项目
		wmsInInboundDao.insertWmsInboundItem(itemlist);
		
		//保存委外清单消耗
		List<Map<String, Object>> cptist=new ArrayList<Map<String, Object>>();
		String arrlist=params.get("ARRLIST_WW").toString();
		if(!"[]".equals(arrlist)){//arrlist不为空
			arrlist=arrlist.substring(2, arrlist.length());
			arrlist=arrlist.substring(0,arrlist.length()-2);
			arrlist="[".concat(arrlist).concat("]");
			JSONArray jarr_ww = JSON.parseArray(arrlist);
			for(int k=0;k<jarr_ww.size();k++){
				Map<String, Object> tempmap=new HashMap();
				System.out.println(">>"+jarr_ww.get(k));
				
				if(!",".equals(jarr_ww.get(k))){//当手动增加行的时候 ，不知道为甚么会有，号出现在里面
				tempmap.put("WERKS", jarr_ww.getJSONObject(k).getString("WERKS"));
				tempmap.put("EBELN", jarr_ww.getJSONObject(k).getString("EBELN"));
				tempmap.put("EBELP", jarr_ww.getJSONObject(k).getString("EBELP"));
				tempmap.put("MATN2", jarr_ww.getJSONObject(k).getString("MATN2"));
				tempmap.put("MAKTX2",jarr_ww.getJSONObject(k).getString("MAKTX2"));
				tempmap.put("MENG2", jarr_ww.getJSONObject(k).getString("MENG2"));
				tempmap.put("MEIN2", jarr_ww.getJSONObject(k).getString("MEIN2"));
				tempmap.put("BATCH", jarr_ww.getJSONObject(k).getString("BATCH"));
				tempmap.put("RECEIPT_NO", jarr_ww.getJSONObject(k).getString("RECEIPT_NO"));
				tempmap.put("RECEIPT_ITEM_NO", jarr_ww.getJSONObject(k).getString("RECEIPT_ITEM_NO"));
				cptist.add(tempmap);
				}
			}
			if(cptist.size()>0){
				saveWmsInPoCptConsume(itemlist,cptist);
			}
		}
		
		return docNo;
	}
	
	private Map insertMapByJsonObject(JSONObject jarr) throws Exception{
		Map<String, Object> tempmap1=new HashMap();
		tempmap1.put("INBOUND_NO", jarr.getString("INBOUND_NO"));
		tempmap1.put("INBOUND_ITEM_NO", jarr.getString("INBOUND_ITEM_NO"));
		
		tempmap1.put("ITEM_TEXT", jarr.getString("ITEM_TEXT"));
		
		tempmap1.put("ITEM_STATUS", "00");//创建状态
		
		//查询业务类型
		Map<String, Object> cond=new HashMap();
		cond.put("BUSINESS_TYPE", jarr.getString("BUSINESS_TYPE"));
		cond.put("BUSINESS_NAME", "10");//固定为 交接进仓-收货
		cond.put("BUSINESS_CLASS", "02");
		cond.put("SOBKZ", jarr.getString("SOBKZ"));
		List<Map<String, Object>> retBusinessInfo=wmsInInboundDao.getBusinessInfo_cond(cond);
		
		if(retBusinessInfo.size()>0){
		
		tempmap1.put("BUSINESS_NAME", retBusinessInfo.get(0).get("BUSINESS_NAME"));
		
		tempmap1.put("BUSINESS_TYPE", retBusinessInfo.get(0).get("BUSINESS_TYPE"));
		}
		
		tempmap1.put("DEL", "0");
		
		tempmap1.put("F_WERKS", jarr.getString("F_WERKS"));
			
		tempmap1.put("F_WH_NUMBER", jarr.getString("F_WH_NUMBER"));
			
		tempmap1.put("F_LGORT", jarr.getString("F_LGORT"));
		
		
		tempmap1.put("F_BATCH", jarr.getString("F_BATCH"));
		//werks
		
		tempmap1.put("WERKS", jarr.getString("WERKS"));
		//WH_NUMBER
		tempmap1.put("WH_NUMBER", jarr.getString("WH_NUMBER"));
		
		tempmap1.put("LGORT", jarr.getString("LGORT"));
		
		tempmap1.put("MATNR", jarr.getString("MATNR"));
		
		tempmap1.put("MAKTX", jarr.getString("MAKTX"));
		
		tempmap1.put("BIN_CODE", jarr.getString("BIN_CODE"));
		
		tempmap1.put("F_UNIT", jarr.getString("F_UNIT"));
		
		tempmap1.put("UNIT", jarr.getString("UNIT"));
		
		tempmap1.put("F_QTY", jarr.getString("F_QTY"));
		
		tempmap1.put("IN_QTY", jarr.getString("IN_QTY"));//进仓数量
		
		tempmap1.put("TRY_QTY", jarr.getString("TRY_QTY"));//试装数量
		
		tempmap1.put("BATCH", jarr.getString("BATCH"));
		
		tempmap1.put("REAL_QTY", jarr.getString("REAL_QTY"));
		
		tempmap1.put("QTY_CANCEL", jarr.getString("QTY_CANCEL"));
		
		tempmap1.put("SOBKZ", jarr.getString("SOBKZ"));
		
		tempmap1.put("FULL_BOX_QTY", jarr.getString("FULL_BOX_QTY"));
		
		tempmap1.put("BOX_COUNT", jarr.getString("BOX_COUNT"));
		
		tempmap1.put("WH_MANAGER", jarr.getString("WH_MANAGER"));
		
		tempmap1.put("RECEIPT_NO", jarr.getString("RECEIPT_NO"));
		
		tempmap1.put("RECEIPT_ITEM_NO", jarr.getString("RECEIPT_ITEM_NO"));
		
		tempmap1.put("LIFNR", jarr.getString("LIFNR"));
		
		tempmap1.put("LIKTX", jarr.getString("LIKTX"));
		
		tempmap1.put("COST_CENTER", jarr.getString("COST_CENTER"));
		
		tempmap1.put("IO_NO", jarr.getString("IO_NO"));
		
		tempmap1.put("WBS", jarr.getString("WBS"));
		
		tempmap1.put("SAKTO", jarr.getString("SAKTO"));
		
		tempmap1.put("ANLN1", jarr.getString("ANLN1"));
		
		tempmap1.put("MO_NO", jarr.getString("MO_NO"));
		
		tempmap1.put("MO_ITEM_NO", jarr.getString("MO_ITEM_NO"));
		
		tempmap1.put("BEDNR", jarr.getString("BEDNR"));
		
		tempmap1.put("PRODUCT_DATE", jarr.getString("PRODUCT_DATE"));
		
		
		tempmap1.put("CREATOR", jarr.getString("CREATOR"));
		tempmap1.put("CREATE_DATE", jarr.getString("CREATE_DATE"));
		
		tempmap1.put("BIN_CODE_SHELF", jarr.getString("BIN_CODE_SHELF"));
		tempmap1.put("AUTO_PUTAWAY_FLAG", jarr.getString("AUTO_PUTAWAY_FLAG"));
		
		tempmap1.put("RN", jarr.getString("RN")==null?"":jarr.getString("RN").toString());
		
		tempmap1.put("REF_SAP_MATDOC_NO", jarr.getString("REF_SAP_MATDOC_NO"));
		tempmap1.put("REF_SAP_MATDOC_ITEM_NO", jarr.getString("REF_SAP_MATDOC_ITEM_NO"));
		tempmap1.put("REF_SAP_MATDOC_YEAR", jarr.getString("REF_SAP_MATDOC_YEAR"));
		
		return tempmap1;
	}

	@Override
	public String newInbound(List<String> labels,String werks,String whNumber,String binCode,String ltWare) {
		Map<String,Object> params= new HashMap<String,Object>();
		params.put("LABEL_NO_LIST", labels);
		List<Map<String,Object>> labelList =  wmsInInboundDao.queryLabel(params);
		//根据批次分类
		Map<String,List<Map<String,Object>>> batchLabelMap = new HashMap<String,List<Map<String,Object>>>();
		for(Map<String,Object> label:labelList) {
			String BATCH = label.get("BATCH").toString();
			if(batchLabelMap.get(BATCH) == null) {
				List<Map<String,Object>> val = new ArrayList<Map<String,Object>>();
				batchLabelMap.put(BATCH, val);
			}
			batchLabelMap.get(BATCH).add(label);
		}
		//创建进仓单头
		Map<String,Object> inInboundHeadMap = new HashMap<String,Object>();
		UserUtils us = new UserUtils();
		String username =  us.getTokenUsername();
		String qcResultCode = labelList.get(0).get("QC_RESULT_CODE").toString();//??是否所有标签的质检结果唯一
		String docType = "07";
		String inInboundNo =  wmsCDocNoService.getDocNo(werks, docType);
		inInboundHeadMap.put("INBOUND_NO", inInboundNo);
		inInboundHeadMap.put("INBOUND_TYPE", "00");//进仓单类型 00 外购进仓单 01 自制进仓单
		inInboundHeadMap.put("INBOUN_STATUS", "00");//创建状态
		inInboundHeadMap.put("WERKS", werks);
		inInboundHeadMap.put("WH_NUMBER",whNumber);
		inInboundHeadMap.put("HEADER_TXT", "");
		inInboundHeadMap.put("IS_AUTO", "0");
		inInboundHeadMap.put("DEL", "0");
		inInboundHeadMap.put("CREATOR", username);
		inInboundHeadMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		inInboundHeadMap.put("QC_RESULT", qcResultCode);
		wmsInInboundDao.insertWmsInboundHead(inInboundHeadMap);
		
		//创建进仓单行项目
		//每个批次，对应一个进仓单行项目
		Integer inInboundItemNo = 1;
		List<Map<String,Object>> inInboundItemList = new ArrayList<Map<String,Object>>();
		batchLabelMap.forEach((k,v)->{
			String labelWerks = v.get(0).get("WERKS").toString();
			String labelWhNumber = v.get(0).get("WH_NUMBER").toString();
			String labelLgort = v.get(0).get("LGORT").toString();
			String labelBatch = v.get(0).get("BATCH").toString();
			String labelMatnr = v.get(0).get("MATNR").toString();
			String labelMaktx = v.get(0).get("MAKTX").toString();
			String labelUnit = v.get(0).get("UNIT").toString();
			
			//标签集合用逗号分隔
			String labelNoArr = "";
			for(Map<String,Object> lab:v) {
				labelNoArr += lab.get("LABEL_NO").toString() + ",";
			}
			labelNoArr= labelNoArr.substring(0, labelNoArr.length()-1);
			
			Map<String,Object> inInboundItem = new HashMap<String,Object>();
			inInboundItem.put("INBOUND_NO", inInboundNo);
			inInboundItem.put("INBOUND_ITEM_NO", inInboundItemNo);
			inInboundItem.put("ITEM_TEXT", null);
			inInboundItem.put("ITEM_STATUS", "00");
			inInboundItem.put("BUSINESS_CODE", "");
			inInboundItem.put("BUSINESS_NAME", "");
			inInboundItem.put("BUSINESS_TYPE", "");
			inInboundItem.put("DEL", "0");
			inInboundItem.put("F_WERKS",labelWerks);
			inInboundItem.put("F_WH_NUMBER",labelWhNumber);
			inInboundItem.put("F_LGORT",labelLgort);
			inInboundItem.put("F_BATCH",labelBatch);
			inInboundItem.put("F_BATCH",labelBatch);
			inInboundItem.put("WERKS",werks);
			inInboundItem.put("WH_NUMBER",whNumber);
			inInboundItem.put("LGORT","");
			inInboundItem.put("MATNR",labelMatnr);
			inInboundItem.put("MAKTX",labelMaktx);
			inInboundItem.put("BIN_CODE_SHELF",binCode);
			inInboundItem.put("BIN_CODE",binCode);
			inInboundItem.put("AUTO_PUTAWAY_FLAG","0");
			inInboundItem.put("UNIT",labelUnit);
			//TODO: 字段填充	
			inInboundItem.put("F_QTY",null);
			inInboundItem.put("IN_QTY",null);
			inInboundItem.put("TRY_QTY",null);
			inInboundItem.put("BATCH",null);
			inInboundItem.put("REAL_QTY",null);
			inInboundItem.put("QTY_CANCEL",null);
			inInboundItem.put("SOBKZ",null);
			inInboundItem.put("FULL_BOX_QTY",null);
			inInboundItem.put("BOX_COUNT",null);
			inInboundItem.put("WH_MANAGER",null);
			inInboundItem.put("CUSTOMER_MATNR",null);
			inInboundItem.put("BEDNR",null);
			inInboundItem.put("RECEIPT_NO",null);
			inInboundItem.put("RECEIPT_ITEM_NO",null);
			inInboundItem.put("PO_NO",null);
			inInboundItem.put("PO_ITEM_NO",null);
			inInboundItem.put("LIFNR",null);
			inInboundItem.put("LIKTX",null);
			inInboundItem.put("COST_CENTER",null);
			inInboundItem.put("IO_NO",null);
			inInboundItem.put("WBS",null);
			inInboundItem.put("SAKTO",null);
			inInboundItem.put("ANLN1",null);
			inInboundItem.put("MO_NO",null);
			inInboundItem.put("MO_ITEM_NO",null);
			inInboundItem.put("PRODUCT_DATE",null);
			inInboundItem.put("SAP_MATDOC_NO",null);
			inInboundItem.put("SAP_MATDOC_ITEM_NO",null);
			inInboundItem.put("WORKSHOP",null);
			inInboundItem.put("WORKGROUP_NO",null);
			inInboundItem.put("CAR_TYPE",null);
			inInboundItem.put("MOULD_NO",null);
			inInboundItem.put("LT_WARE",null);
			inInboundItem.put("DRAWING_NO",null);
			inInboundItem.put("OPERATOR",null);
			inInboundItem.put("PRO_STATION",null);
			inInboundItem.put("MEMO",null);
			inInboundItem.put("CREATOR",null);
			inInboundItem.put("CREATE_DATE",null);
			inInboundItem.put("SO_NO",null);
			inInboundItem.put("SO_ITEM_NO",null);
			
			inInboundItemList.add(inInboundItem);
		});
		wmsInInboundDao.insertWmsInboundItem(inInboundItemList);
		return inInboundNo;
	}
	
	/**
	 * 查询仓管员关联的 上架任务
	 * @param werks 工厂
	 * @param whNumer 仓管
	 * @param admin 仓管员
	 * @return
	 */
	public List<Map<String,Object>> getInboundTask(String werks,String whNumer,String admin){
		/*
		 * 1.通过仓管员、工厂、库位获取人料关系
		 * 2.通过物料仓库，查询仓库上架任务
		 */
		if(StringUtils.isBlank(werks) || StringUtils.isBlank(whNumer) || StringUtils.isBlank(admin)) {
			throw new IllegalArgumentException();
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("WERKS", werks);//仓库
		params.put("WH_NUMBER", whNumer);//工厂
		params.put("MAT_MANAGER_TYPE", "00");//人与料关联
		params.put("MANAGER", admin);//仓管员
		
		List<Map<String,Object>> matManagerList =  wmsInInboundDao.queryMatManager(params);
		if(matManagerList == null || matManagerList.size() ==0) {
			throw new IllegalArgumentException("没有配置人料关系，无法查上架任务");
		}
		Map<String,Object> map = new HashMap<String,Object>();
		List<String> matnrList = matManagerList.stream().map(item->item.get("MATNR").toString()).collect(Collectors.toList());
		List<String> whStatusList = Arrays.asList("00","01");
		map.put("MATNR_LIST", matnrList);
		map.put("WH_NUMBER", whNumer);
		map.put("WH_STATUS_LIST", whStatusList);
		List<Map<String,Object>> whTaskList =  wmsInInboundDao.queryWhTask(map);
		return whTaskList;
	}

	@Override
	public List<Map<String, Object>> getReceiptInfoByReceiveNo(
			Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsInInboundDao.getReceiptInfoByReceiveNo(map);
	}
	
	@Override
	public List<Map<String, Object>> getComponentInfoByPoNo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsInInboundDao.getComponentInfoByPoNo(map);
	}

	@Override
	public void insertWmsInPoCptConsume(List<Map<String, Object>> cptList) {
		wmsInInboundDao.insertWmsInPoCptConsume(cptList);
		
	}
	
	void saveWmsInPoCptConsume(List<Map<String, Object>> itemlist,List<Map<String, Object>> cptlist){
		List<Map<String, Object>> addcptlist=new ArrayList<Map<String, Object>>();
		//RECEIPT_NO  RECEIPT_ITEM_NO
		for(Map<String, Object> item:itemlist){
			for(Map<String, Object> cpt:cptlist){
				if(item.get("RECEIPT_NO").equals(cpt.get("RECEIPT_NO"))
						&&item.get("RECEIPT_ITEM_NO").equals(cpt.get("RECEIPT_ITEM_NO"))){
					//委外清单里面收货单号和行项目号与页面的相同的，就保存到委外清单表
					cpt.put("INBOUND_NO", item.get("INBOUND_NO"));
					cpt.put("INBOUND_ITEM_NO",item.get("INBOUND_ITEM_NO"));
					cpt.put("EDITOR", item.get("CREATOR"));
					cpt.put("EDIT_DATE", item.get("CREATE_DATE"));
					
					addcptlist.add(cpt);
				}
			}
		}
		if(addcptlist.size()>0){
			wmsInInboundDao.insertWmsInPoCptConsume(addcptlist);
		}
		
	}
	@Override
	public List<Map<String, Object>> getBusinessInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsInInboundDao.getBusinessInfo(map);
	}
	
	private Map insertMapByList(Map<String, Object> itemMap) throws Exception{
		Map<String, Object> tempmap1=new HashMap();
		tempmap1.put("INBOUND_NO", itemMap.get("INBOUND_NO"));
		tempmap1.put("INBOUND_ITEM_NO", itemMap.get("INBOUND_ITEM_NO"));
		
		tempmap1.put("ITEM_TEXT", itemMap.get("ITEM_TEXT"));
		
		tempmap1.put("ITEM_STATUS", "00");//创建状态
		
		//查询业务类型
		Map<String, Object> cond=new HashMap();
		cond.put("BUSINESS_TYPE", "08");
		cond.put("BUSINESS_NAME", "10");//
		cond.put("BUSINESS_CLASS", "02");
		cond.put("SOBKZ", itemMap.get("SOBKZ"));
		List<Map<String, Object>> retBusinessInfo=wmsInInboundDao.getBusinessInfo_cond(cond);
		
		if(retBusinessInfo.size()>0){
		
		tempmap1.put("BUSINESS_NAME", retBusinessInfo.get(0).get("BUSINESS_NAME"));
		
		tempmap1.put("BUSINESS_TYPE", retBusinessInfo.get(0).get("BUSINESS_TYPE"));
		}
		
		tempmap1.put("DEL", "0");
		
		tempmap1.put("F_WERKS", itemMap.get("F_WERKS")==null?"":itemMap.get("F_WERKS").toString());
			
		tempmap1.put("F_WH_NUMBER", itemMap.get("F_WH_NUMBER")==null?"":itemMap.get("F_WH_NUMBER").toString());
			
		tempmap1.put("F_LGORT", itemMap.get("F_LGORT")==null?"":itemMap.get("F_LGORT").toString());
		
		
		tempmap1.put("F_BATCH", itemMap.get("F_BATCH")==null?"":itemMap.get("F_BATCH").toString());
		//werks
		
		tempmap1.put("WERKS", itemMap.get("WERKS")==null?"":itemMap.get("WERKS").toString());
		//WH_NUMBER
		tempmap1.put("WH_NUMBER", itemMap.get("WH_NUMBER")==null?"":itemMap.get("WH_NUMBER").toString());
		
		tempmap1.put("LGORT", itemMap.get("LGORT")==null?"":itemMap.get("LGORT").toString());
		
		tempmap1.put("MATNR", itemMap.get("MATNR")==null?"":itemMap.get("MATNR").toString());
		
		tempmap1.put("MAKTX", itemMap.get("MAKTX")==null?"":itemMap.get("MAKTX").toString());
		
		tempmap1.put("BIN_CODE", itemMap.get("BIN_CODE")==null?"":itemMap.get("BIN_CODE").toString());
		
		tempmap1.put("F_UNIT", itemMap.get("F_UNIT")==null?"":itemMap.get("F_UNIT").toString());
		
		tempmap1.put("UNIT", itemMap.get("UNIT")==null?"":itemMap.get("UNIT").toString());
		
		tempmap1.put("F_QTY", itemMap.get("F_QTY")==null?"":itemMap.get("F_QTY").toString());
		
		tempmap1.put("IN_QTY", itemMap.get("IN_QTY")==null?"":itemMap.get("IN_QTY").toString());//进仓数量
		
		tempmap1.put("TRY_QTY", itemMap.get("TRY_QTY")==null?"":itemMap.get("TRY_QTY").toString());//试装数量
		
		tempmap1.put("BATCH", itemMap.get("BATCH")==null?"":itemMap.get("BATCH").toString());
		
		tempmap1.put("REAL_QTY", itemMap.get("REAL_QTY")==null?"":itemMap.get("REAL_QTY").toString());
		
		tempmap1.put("QTY_CANCEL", itemMap.get("QTY_CANCEL")==null?"":itemMap.get("QTY_CANCEL").toString());
		
		tempmap1.put("SOBKZ", itemMap.get("SOBKZ")==null?"":itemMap.get("SOBKZ").toString());
		
		tempmap1.put("FULL_BOX_QTY", itemMap.get("FULL_BOX_QTY")==null?"":itemMap.get("FULL_BOX_QTY").toString());
		
		tempmap1.put("BOX_COUNT", itemMap.get("BOX_COUNT")==null?"":itemMap.get("BOX_COUNT").toString());
		
		tempmap1.put("WH_MANAGER", itemMap.get("WH_MANAGER")==null?"":itemMap.get("WH_MANAGER").toString());
		
		tempmap1.put("RECEIPT_NO", itemMap.get("RECEIPT_NO")==null?"":itemMap.get("RECEIPT_NO").toString());
		
		tempmap1.put("RECEIPT_ITEM_NO", itemMap.get("RECEIPT_ITEM_NO")==null?"":itemMap.get("RECEIPT_ITEM_NO").toString());
		
		tempmap1.put("LIFNR", itemMap.get("LIFNR")==null?"":itemMap.get("LIFNR").toString());
		
		tempmap1.put("LIKTX", itemMap.get("LIKTX")==null?"":itemMap.get("LIKTX").toString());
		
		tempmap1.put("COST_CENTER", itemMap.get("COST_CENTER"));
		
		tempmap1.put("IO_NO", itemMap.get("IO_NO"));
		
		tempmap1.put("WBS", itemMap.get("WBS"));
		
		tempmap1.put("SAKTO", itemMap.get("SAKTO"));
		
		tempmap1.put("ANLN1", itemMap.get("ANLN1"));
		
		tempmap1.put("MO_NO", itemMap.get("MO_NO"));
		
		tempmap1.put("MO_ITEM_NO", itemMap.get("MO_ITEM_NO"));
		
		tempmap1.put("BEDNR", itemMap.get("BEDNR"));
		
		tempmap1.put("PRODUCT_DATE", itemMap.get("PRODUCT_DATE"));
		
		
		tempmap1.put("CREATOR", itemMap.get("CREATOR"));
		tempmap1.put("CREATE_DATE", itemMap.get("CREATE_DATE"));
		
		tempmap1.put("BIN_CODE_SHELF", itemMap.get("BIN_CODE_SHELF"));
		tempmap1.put("AUTO_PUTAWAY_FLAG", itemMap.get("AUTO_PUTAWAY_FLAG"));
		
		tempmap1.put("RN", itemMap.get("RN")==null?"":itemMap.get("RN").toString());
		
		tempmap1.put("REF_SAP_MATDOC_NO", itemMap.get("REF_SAP_MATDOC_NO"));
		tempmap1.put("REF_SAP_MATDOC_ITEM_NO", itemMap.get("REF_SAP_MATDOC_ITEM_NO"));
		tempmap1.put("REF_SAP_MATDOC_YEAR", itemMap.get("REF_SAP_MATDOC_YEAR"));
		
		return tempmap1;
	}
}
