package com.byd.wms.business.modules.in.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsCStepLinkageEntity;
import com.byd.wms.business.modules.config.service.WmsCStepLinkageService;
import com.byd.wms.business.modules.in.dao.WmsInPutawayStatusAndLogDao;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.in.service.WmsInAutoPutawayService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

/**
 * STO一步联动服务类
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:34:36 
 *
 */
@Service("wmsInAutoPutawayService")
public class WmsInAutoPutawayServiceImpl implements WmsInAutoPutawayService{

	@Autowired
	private WmsCStepLinkageService wmsCStepLinkageService;
	
	@Autowired
	private WmsSapRemote wmsSapRemote;
	
	@Autowired
	private WmsInPutawayStatusAndLogDao wmsInPutawayStatusAndLogDao;
	
	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	
	@Autowired
    WmsCTxtService wmsCTxtService;
	
	@Autowired
    private WmsInReceiptService wmsInReceiptService;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> createPO(Map<String, Object> params){
		
		List<WmsCStepLinkageEntity> list = wmsCStepLinkageService.selectList(new EntityWrapper<WmsCStepLinkageEntity>()
				.eq("WH_NUMBER", params.get("WH_NUMBER"))
				.eq("WERKS_FROM", params.get("FROMWERKS").toString().toUpperCase())
				.eq("WERKS_TO", params.get("WERKS")));
		
		if (list == null || list.size() <=0) {
			throw new RuntimeException("一步联动主数据未配置");
		}
		params.put("docType", list.get(0).getDocType());
		params.put("docDate", DateUtils.format(new Date(),DateUtils.DATE_PATTERN_POINT));
		params.put("purchOrg", list.get(0).getEkorg());
		params.put("purGroup", list.get(0).getEkgrp());
		params.put("vendor", list.get(0).getLifnr());
		params.put("compCode", list.get(0).getBukrs());
		Map<String,Object> returnMap = wmsSapRemote.createStoPO(params);
		
		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> createDN(Map<String, Object> params){
		params.put("DUE_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN_POINT));
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("WH_NUMBER", params.get("WH_NUMBER"));
		param.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR"));
		param.put("EBELN", params.get("EBELN"));
		
		List<Map<String, Object>> loglist = wmsInPutawayStatusAndLogDao.getLog(param);
		if (loglist == null && loglist.size() <= 0) {
			throw new RuntimeException("未找到合适数据");
		}
		params.put("ITEMLIST", JSONArray.toJSONString(loglist));
		Map<String,Object> returnMap = wmsSapRemote.createStoDN(params);
		
		String delivery = returnMap.get("DELIVERY") == null ? "":returnMap.get("DELIVERY").toString();
		String message = returnMap.get("MESSAGE") == null ? "":returnMap.get("MESSAGE").toString();
		//保存日志和状态
		List<Map<String,String>> statusList = new ArrayList<Map<String,String>>();
		Map<String, String> statusparam = new HashMap<String, String>();
		if (delivery.equals("")) {
			statusparam.put("STEP2_STATUS", "E");
			statusparam.put("MSG2", message.substring(0, message.length()>300?300:message.length()));
		} else {
			statusparam.put("STEP2_STATUS", "S");
			statusparam.put("VBELN", delivery);
			statusparam.put("MSG2", " ");
		}
		statusparam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		statusparam.put("WH_NUMBER", params.get("WH_NUMBER").toString());
		statusparam.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR").toString());
		statusList.add(statusparam);
		wmsInPutawayStatusAndLogDao.updatePutawayStatus(statusList);
		
		return returnMap;
	}
	
	//ModBy:YK190722 把生成批次的方法移到事务外面 避免重复生成批次
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> postDNService(Map<String, Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		List<Map<String, Object>> putawayInfolist = (List<Map<String, Object>>) params.get("putawayInfolist");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String vbeln = putawayInfolist.get(0).get("VBELN").toString();
		/**
		 * 保存收货单数据
		 */
		params.put("WMS_DOC_TYPE", "02");//收货单
		String RECEIPT_NO="";//收货单号
		Map<String,Object> doc=null;
		doc=wmsCDocNoService.getDocNo(params);
		if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
			throw new RuntimeException(doc.get("MSG").toString());
		}
		RECEIPT_NO=doc.get("docno").toString();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		int i=1;
		for(Map<String,Object> m:putawayInfolist) {
			m.put("RECEIPT_NO", RECEIPT_NO);
			m.put("RECEIPT_DATE", curDate);
			m.put("RECEIPT_ITEM_NO", i);
			m.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
			m.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE"));
			m.put("INABLE_QTY", 0);
			m.put("PO_NO", m.get("EBELN"));
			m.put("PO_ITEM_NO", m.get("EBELP"));
			i++;
		}
		
		wmsInReceiptDao.insertReceiptInfo(putawayInfolist);
		
		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(putawayInfolist);
		
		/**
		 * 保存标签数据
		 */				
		if (null == putawayInfolist.get(0).get("LABEL_NO") || putawayInfolist.get(0).get("LABEL_NO").equals(""))
			this.saveCoreLabel(skList,putawayInfolist);
		
		/**
		 * 产生WMS凭证记录 
		 * 
		 */
		String WMS_NO=commonService.saveWMSDoc(params,putawayInfolist);
		params.put("WMS_NO", WMS_NO);
		
		
		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params,putawayInfolist);	//送检单号
		
		
		returnMap = wmsSapRemote.postDN(WERKS, vbeln);
		
		List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
		if("0".equals(returnMap.get("CODE"))) {
			//交货单过账，通过交货单获取物料凭证
			/**
			 * 通过调用SAP服务接口，读取SAP交货单数据
			 */
			try{
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(vbeln);
			//交货单关联的凭证信息 docList
			List<Map<String,Object>> dnDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");
			if(null != dnDocList && dnDocList.size()>0) {
				boolean flag = false;
				for (Map<String, Object> map : dnDocList) {
					/**
					 * 判断获取的凭证是否为物料凭证（凭证号不为空且移动类型不为空）
					 */
					if(map.get("VBELN")!=null && map.get("BWART")!=null) {
						//过账成功，获取到了物料凭证
						flag = true;
						returnMap.put("MAT_DOC", map.get("VBELN"));
						returnMap.put("MATDOC_ITM", map.get("POSNN"));
						returnMap.put("MAT_DOC_YEAR", map.get("MJAHR"));
						
						Map<String, String> sapDocMap = new HashMap<String,String>();
						sapDocMap.put("MAT_DOC", map.get("VBELN").toString());
						sapDocMap.put("MATDOC_ITM", map.get("POSNN").toString());
						sapDocMap.put("DOC_YEAR", map.get("MJAHR").toString());
						sapDocMap.put("MOVE_TYPE", map.get("BWART").toString());
						sapDocMap.put("MATERIAL", map.get("MATNR").toString());
						sapDocMap.put("PLANT", params.get("WERKS").toString());
						sapDocMap.put("STGE_LOC", "00ZJ");
						sapDocMap.put("ENTRY_QNT", map.get("RFMNG").toString());
						sapDocMap.put("ENTRY_UOM", map.get("MEINS").toString());
						sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
						sap_detail_list.add(sapDocMap);
						
					}
				}
				if(!flag) {
					//过账失败
					throw new RuntimeException("交货单"+vbeln+"过账失败，未产生过账凭证！");
				} else {
					List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
					Map<String,String> head= new HashMap<String,String>();
					head.put("REF_WMS_NO", WMS_NO);
					head.put("MAT_DOC", returnMap.get("MAT_DOC").toString());
					head.put("DOC_YEAR", returnMap.get("MAT_DOC_YEAR").toString());
					head.put("DOC_DATE", params.get("PZ_DATE").toString());
					head.put("PSTNG_DATE", params.get("JZ_DATE").toString());
					head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
					sap_head_list.add(head);
					commonDao.insertSapDocHead(sap_head_list);
					commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
					
					String MAT_DOC_STR = "645+101"+":"+head.get("MAT_DOC")+";";

					/**
					 * 更新WMS凭证抬头SAP物料凭证信息
					 */
					Map<String,Object> wmsDocMap = new HashMap<String,Object>();
					wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
					wmsDocMap.put("WMS_NO", WMS_NO);
					commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);
				}

			}else {
				returnMap.put("CODE", "-2");
				returnMap.put("MESSAGE", "交货单"+vbeln+"过账，SAP延迟未及时产生凭证！");
			}
		} else {
			throw new RuntimeException("收货失败");
		}
				
		returnMap.put("INSPECTION_NO", INSPECTION_NO);
		returnMap.put("WMS_NO", WMS_NO);
		returnMap.put("RECEIPT_NO", RECEIPT_NO);
		returnMap.put("lableList", skList);
		returnMap.put("inspectionList", putawayInfolist);
		
		return returnMap;
	}
	
	/**
	 * 一步联动交货单收货过账
	 */
	@Override
	public Map<String, Object> postDN(Map<String, Object> params) {
		boolean batchflag = false;
		String WERKS = params.get("WERKS").toString();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("WH_NUMBER", params.get("WH_NUMBER"));
		param.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR"));
		
		List<Map<String, Object>> putawayInfolist = wmsInPutawayStatusAndLogDao.getPutawayInfo(param);
		if (putawayInfolist == null && putawayInfolist.size() <= 0) {
			throw new RuntimeException("未找到合适数据");
		}
		
		params.put("OPERATION_TIME", putawayInfolist.get(0).get("OPERATION_TIME"));
		params.put("matList", putawayInfolist);
		params.put("BUSINESS_NAME", "03");//SAP交货单收料
		params.put("BUSINESS_TYPE", "06");//SAP交货单
		params.put("BUSINESS_CLASS", "01");
		params.put("JZ_DATE", putawayInfolist.get(0).get("JZ_DATE"));
		params.put("PZ_DATE", putawayInfolist.get(0).get("PZ_DATE"));
		
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		params.put("SEARCH_DATE", curDate);
		String TEST_FLAG = wmsInReceiptService.getTestFlag(params);//工厂质检配置
		
		if(TEST_FLAG ==null || TEST_FLAG.equals("")) {
			throw new RuntimeException("收货工厂"+WERKS+"未配置SAP交货单收料的工厂质检标识主数据！");
		}
		
		params.put("TEST_FLAG", TEST_FLAG);
		
		//读取和匹配物料质检配置表的免检物料数据
    	List<String> matnr_list=new ArrayList<String>();
    	if("00".equals(TEST_FLAG)||"01".equals(TEST_FLAG)) {
    		List<Map<String,Object>> qc_mat_list=null;
    		Map<String,Object> qc_params=new HashMap<String,Object>();
    		qc_params.putAll(params);
    		if("00".equals(TEST_FLAG)) {
    			qc_params.put("TEST_FLAG", "01");// 免检
    		}else if("01".equals(TEST_FLAG))  {
    			qc_params.put("TEST_FLAG", "00");//质检
    		}
    		qc_mat_list = wmsInReceiptService.getQCMatList(qc_params);
    		if(qc_mat_list!=null && qc_mat_list.size()>0) {
        		qc_mat_list.forEach(q->{
        			q=(Map)q;
        			matnr_list.add((String) q.get("MATNR"));
        		});
    		}
    	}
		
		String vbeln = putawayInfolist.get(0).get("VBELN").toString();
		
		for(Map<String, Object> putawayInfo : putawayInfolist) {
			putawayInfo.put("SAP_OUT_NO", putawayInfo.get("VBELN"));
			putawayInfo.put("SAP_OUT_ITEM_NO", putawayInfo.get("EBELP"));
			putawayInfo.put("LFIMG", putawayInfo.get("QTY"));
			putawayInfo.put("RECEIPT_QTY", putawayInfo.get("QTY"));
			putawayInfo.put("QTY_SAP", putawayInfo.get("QTY"));
			putawayInfo.put("BIN_CODE", putawayInfo.get("GR_AREA"));
			putawayInfo.put("VGBEL", putawayInfo.get("EBELN"));
			putawayInfo.put("VGPOS", putawayInfo.get("EBELP"));
			putawayInfo.put("CREATOR", params.get("USERNAME"));
			putawayInfo.put("CREATE_DATE", params.get("CREATE_DATE"));
			putawayInfo.put("SOBKZ", "Z");
			putawayInfo.put("REVERSAL_FLAG", "X");
			putawayInfo.put("CANCEL_FLAG", "X");
			
			if(putawayInfo.get("BATCH") == null ||putawayInfo.get("BATCH").equals("")) {
				batchflag = true;
			}
			
			//质检
    		if("00".equals(TEST_FLAG)) {
    			if(matnr_list.contains(putawayInfo.get("MATNR"))) {
    				//免检
    				putawayInfo.put("TEST_FLAG", "01");
    			}else {
    				//质检
    				putawayInfo.put("TEST_FLAG", "00");
    			}
    		}
    		//免检
    		if("01".equals(TEST_FLAG)) {
    			if(matnr_list.contains(putawayInfo.get("MATNR"))) {
    				//质检
    				putawayInfo.put("TEST_FLAG", "00");
    			}else {
    				putawayInfo.put("TEST_FLAG", "01");
    			}
    		}
    		if("02".equals(TEST_FLAG)) {
    			putawayInfo.put("TEST_FLAG", "02");
    		}
    		
		}
		
		if(params.get("HEADER_TXT") != null) {
			//获取抬头文本
			Map<String,String>txtMap=new HashMap<String,String>();
	    	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
	    	txtMap.put("WERKS", params.get("WERKS").toString());
	    	txtMap.put("BUSINESS_NAME","23" );
	    	txtMap.put("JZ_DATE", JZ_DATE);
	    	txtMap.put("currentUser", params.get("FULL_NAME").toString());
	    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
	    	txtMap.put("SAP_OUT_NO", vbeln);
	    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
	    	
	    	params.put("HEADER_TXT", txt.get("txtrule"));
		}
    	
		
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE="";

		
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", "03");//SAP交货单收料
		cdmap.put("BUSINESS_TYPE", "06");//SAP交货单
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		/**
		 * 生成WMS批次信息
		 */	
		if(batchflag) {
			this.setMatBatch(cdmap ,putawayInfolist);	
		}
		
		params.put("putawayInfolist", putawayInfolist);
		Map<String,Object> returnMap = this.postDNService(params);
		
		return returnMap;
	}
	
	/**
	 * WMS过账成功的，SAP空凭证的情况下，再次过账SAP。
	 */
	@Override
	public Map<String, Object> postDNAgain(Map<String, Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
		String werks = params.get("WERKS") == null?"":params.get("WERKS").toString();
		String vbeln = params.get("VBELN").toString();
		
		//过账前再获取一遍凭证，如为空再过账
		Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(vbeln);
		//交货单关联的凭证信息 docList
		List<Map<String,Object>> deliveryDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");
		if(null == deliveryDocList || deliveryDocList.size() < 1) {
			//交货单过账
			returnMap = wmsSapRemote.postDN(werks, vbeln);
			
			if("0".equals(returnMap.get("CODE"))) {
				//交货单过账，通过交货单获取物料凭证
				try{
					Thread.sleep(3000);
				} catch (Exception e) {
				}
				dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(vbeln);
				
			} else {
				throw new RuntimeException("收货失败");
			}
		}
		
		//交货单关联的凭证信息 docList
		List<Map<String,Object>> dnDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");
		if(null != dnDocList && dnDocList.size()>0) {
			boolean flag = false;
			for (Map<String, Object> map : dnDocList) {
				/**
				 * 判断获取的凭证是否为物料凭证（凭证号不为空且移动类型不为空）
				 */
				if(map.get("VBELN")!=null && map.get("BWART")!=null) {
					//过账成功，获取到了物料凭证
					flag = true;
					returnMap.put("MAT_DOC", map.get("VBELN"));
					returnMap.put("MATDOC_ITM", map.get("POSNN"));
					returnMap.put("MAT_DOC_YEAR", map.get("MJAHR"));
					
					Map<String, String> sapDocMap = new HashMap<String,String>();
					sapDocMap.put("MAT_DOC", map.get("VBELN").toString());
					sapDocMap.put("MATDOC_ITM", map.get("POSNN").toString());
					sapDocMap.put("DOC_YEAR", map.get("MJAHR").toString());
					sapDocMap.put("MOVE_TYPE", map.get("BWART").toString());
					sapDocMap.put("MATERIAL", map.get("MATNR").toString());
					sapDocMap.put("PLANT", werks);
					sapDocMap.put("STGE_LOC", "00ZJ");
					sapDocMap.put("ENTRY_QNT", map.get("RFMNG").toString());
					sapDocMap.put("ENTRY_UOM", map.get("MEINS").toString());
					sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
					sap_detail_list.add(sapDocMap);
					
				}
			}
			if(!flag) {
				//过账失败
				throw new RuntimeException("交货单"+vbeln+"过账失败，未产生过账凭证！");
			} else {
				List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
				Map<String,String> head= new HashMap<String,String>();
				head.put("REF_WMS_NO", params.get("WMS_NO").toString());
				head.put("MAT_DOC", returnMap.get("MAT_DOC").toString());
				head.put("DOC_YEAR", returnMap.get("MAT_DOC_YEAR").toString());
				head.put("DOC_DATE", params.get("PZ_DATE").toString());
				head.put("PSTNG_DATE", params.get("JZ_DATE").toString());
				head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
				head.put("WH_NUMBER", params.get("WH_NUMBER").toString());
				head.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR").toString());
				head.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				sap_head_list.add(head);
				commonDao.insertSapDocHead(sap_head_list);
				commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
				wmsInPutawayStatusAndLogDao.updatePutawayStatus(sap_head_list);//更新日志状态
				
				String MAT_DOC_STR = "645+101"+":"+head.get("MAT_DOC")+";";

				/**
				 * 更新WMS凭证抬头SAP物料凭证信息
				 */
				Map<String,Object> wmsDocMap = new HashMap<String,Object>();
				wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
				wmsDocMap.put("WMS_NO", params.get("WMS_NO").toString());
				commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);
			}

		}else {
			returnMap.put("CODE", "-2");
			returnMap.put("MESSAGE", "交货单"+vbeln+"过账，SAP延迟未及时产生凭证！");
		}
		
		
		return returnMap;
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String saveStepLog(Map<String, Object> params){
		String werks = params.get("WERKS") == null ? "":params.get("WERKS").toString();
		String matdoc = params.get("MAT_DOC") == null ? "":params.get("MAT_DOC").toString();
		String timestampstr = DateUtils.format(new Date(),DateUtils.DATE_TIME_STAMP);
		String poNumber = params.get("PO_NUMBER") == null ? "":params.get("PO_NUMBER").toString();
		String message = params.get("MESSAGE") == null ? "":params.get("MESSAGE").toString();
		
		//保存日志表
		List<Map<String,String>> itemList = (List<Map<String,String>>) JSONArray.parse(params.get("ITEMLIST").toString());
		List<Map<String,Object>> logs = new ArrayList<Map<String,Object>>();
		for (Map<String,String> item : itemList) {
			Map<String, Object> step1 = new HashMap<String, Object>();
			step1.put("WH_NUMBER", params.get("WH_NUMBER"));
			step1.put("TIME_STAMP_STR", timestampstr);
			step1.put("WERKS", werks);
			step1.put("MATNR", item.get("MATNR"));
			BigDecimal Qty = item.get("RECEIVE_QTY") == null ? BigDecimal.ZERO:new BigDecimal(item.get("RECEIVE_QTY")); 
			step1.put("QTY", Qty);
			step1.put("UNIT", item.get("UNIT"));
			if (!poNumber.equals("")) {
				step1.put("EBELN", poNumber);
				step1.put("EBELP", item.get("ITEM_NO"));
			}
			step1.put("BATCH", item.get("BATCH"));
			step1.put("FULL_BOX_QTY", item.get("FULL_BOX_QTY"));
			step1.put("GR_AREA", item.get("BIN_CODE"));
			step1.put("PRODUCT_DATE", item.get("PRODUCT_DATE"));
			step1.put("ITEM_TEXT", item.get("ITEM_TEXT"));
			step1.put("LABEL_NO", item.get("LABEL"));
			step1.put("DOC_ITEM_NO", item.get("WMS_ITEM_NO"));
			step1.put("HANDLE_FTU", item.get("HANDLE_FTU"));
			logs.add(step1);
		}
		wmsInPutawayStatusAndLogDao.insertLog(logs);
		
		//保存状态表
		Map<String, Object> statusParam = new HashMap<String, Object>();
		statusParam.put("WH_NUMBER", params.get("WH_NUMBER"));
		statusParam.put("TIME_STAMP_STR", timestampstr);
		statusParam.put("DOC_NO", matdoc);
		statusParam.put("WERKS_FROM", params.get("FROMWERKS").toString().toUpperCase());
		statusParam.put("WERKS_TO", werks);
		statusParam.put("LGORT", "00ZJ");
		statusParam.put("PZ_DATE", params.get("PZDDT"));
		statusParam.put("JZ_DATE", params.get("JZDDT"));
		statusParam.put("EDITOR", params.get("USERNAME"));
		statusParam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		if (!poNumber.equals("")) {
			statusParam.put("STEP1_STATUS", "S");
			statusParam.put("EBELN", poNumber);
		} else {
			statusParam.put("STEP1_STATUS", "E");
			statusParam.put("MSG1", message.substring(0, message.length()>300?300:message.length()));
		}	
		statusParam.put("OPERATION_TIME", params.get("OPERATION_TIME")!=null ?params.get("OPERATION_TIME").toString():"");
		wmsInPutawayStatusAndLogDao.insertStatus(statusParam);
		
		return timestampstr;
	}
	
	@Override
	public void updatePutawayStatus(List<Map<String,String>> params){
		wmsInPutawayStatusAndLogDao.updatePutawayStatus(params);
	}
	
	
	/**
	 * 生成WMS批次信息
	 */
	public void setMatBatch(Map<String,Object> cdmap,List<Map<String,Object>> matList) {
		List<Map<String, Object>> condMapList = new ArrayList<Map<String, Object>>();
		
		matList.forEach(k->{
			k=(Map)k;
			
			Map<String, Object> condMap=new HashMap<String,Object>();
			condMap.put("WERKS", k.get("WERKS").toString());
			condMap.put("ASNNO", k.get("ASNNO")==null?"":k.get("ASNNO").toString());
			condMap.put("PO_NO", k.get("PO_NO")==null?"":k.get("PO_NO").toString());
			condMap.put("RECEIPT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
			
			String DELIVERY_DATE=k.get("DELIVERY_DATE")==null?"":k.get("DELIVERY_DATE").toString();
			String PRODUCT_DATE=k.get("PRODUCT_DATE")==null?"":k.get("PRODUCT_DATE").toString();
			String EFFECT_DATE=k.get("EFFECT_DATE")==null?"":k.get("EFFECT_DATE").toString();
			String MATNR =k.get("MATNR").toString();
			String LIFNR=k.get("LIFNR")==null?"":k.get("LIFNR").toString();
			String DANGER_FLAG=k.get("DANGER_FLAG")==null?"0":k.get("DANGER_FLAG").toString();
			String LGORT=k.get("LGORT")==null?"":k.get("LGORT").toString();
			String F_BATCH=k.get("F_BATCH")==null?"":k.get("F_BATCH").toString();
			condMap.put("DELIVERY_DATE", DELIVERY_DATE);
			condMap.put("PRODUCT_DATE", PRODUCT_DATE);
			condMap.put("EFFECT_DATE", EFFECT_DATE);
			condMap.put("MATNR", MATNR);
			condMap.put("LIFNR", LIFNR);
			condMap.put("DANGER_FLAG", DANGER_FLAG);
			condMap.put("LGORT", LGORT);
			condMap.put("F_BATCH", F_BATCH);
			condMap.put("BUSINESS_NAME",cdmap.get("BUSINESS_NAME"));
			
			condMapList.add(condMap);
		});
		
		List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(condMapList);
		
		for (int i=0;i<retmapList.size();i++) {
			Map<String,Object> retmap = retmapList.get(i); 
			/**
			 * 获取批次出错，抛出异常
			 */
			if(retmap.get("MSG")!=null&&!"success".equals(retmap.get("MSG"))) {
				throw new RuntimeException((String) retmap.get("MSG"));
			}
			matList.get(i).put("BATCH", retmap.get("BATCH"));
		}

	}
	
	/**
	 *保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
	 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
	 * @param params 
	 */
	public void saveRhStock(List<Map<String,Object>> matList) {
		for(Map<String,Object> m:matList) {
			m.put("WERKS", m.get("WERKS")==null?"":m.get("WERKS"));
			m.put("WH_NUMBER", m.get("WH_NUMBER")==null?"":m.get("WH_NUMBER"));
			m.put("LGORT", m.get("LGORT")==null?"":m.get("LGORT"));
			m.put("MATNR", m.get("MATNR")==null?"":m.get("MATNR"));
			m.put("MAKTX", m.get("MAKTX")==null?"":m.get("MAKTX"));
			m.put("F_BATCH", m.get("F_BATCH")==null?"":m.get("F_BATCH"));
			m.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH"));
			m.put("UNIT", m.get("UNIT")==null?"":m.get("UNIT"));
			m.put("RH_QTY", m.get("RH_QTY")==null?m.get("RECEIPT_QTY"):m.get("RH_QTY"));
			m.put("SOBKZ", m.get("SOBKZ")==null?"Z":m.get("SOBKZ"));
			m.put("LIFNR", m.get("LIFNR")==null?"":m.get("LIFNR"));
			m.put("LIKTX", m.get("LIKTX")==null?"":m.get("LIKTX"));
		}
		
		if(matList.size()>0) {
			wmsInReceiptDao.updateRhStock(matList);
		}
	}
	
	/**
	 * 保存标签数据
	 */
	public void saveCoreLabel(List<Map<String, Object>> skList, List<Map<String, Object>> matList) {
		String LABEL_STATUS = "01";//00创建，01已收料（待质检），02已收料（无需质检）03待进仓(已质检)，04待退货(已质检)，05收料房退货，06库房退货，07已进仓，08已上架，09已下架，10已出库，11已冻结，12已锁定，20关闭）
		String QC_RESULT_CODE = null;
		//从行项目中获取收货单号和收货单行项目号
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("WMS_DOC_TYPE", "08");//标签号
		params.put("WERKS", matList.get(0).get("WERKS"));

		List<Map<String,Object>> updateList=new ArrayList<Map<String,Object>>();
		
		for(Map<String, Object> m:matList) {
			String TEST_FLAG = m.get("TEST_FLAG").toString();
			if(TEST_FLAG.endsWith("01")) {
				//免检
				LABEL_STATUS = "03";
				QC_RESULT_CODE = "11";
			}
			if(TEST_FLAG.endsWith("02")) {
				//无需质检
				LABEL_STATUS = "02";
			}
			
			List<String> shotLst1 = new ArrayList<String>();
			if (m.get("LABEL_NO") != null) {
				shotLst1 = JSON.parseArray(m.get("LABEL_NO").toString(), String.class);
			}
			
			if(shotLst1 != null && shotLst1.size() > 0 ) {
				for (String laberNo : shotLst1) {
					Map<String,Object> labMap=new HashMap<String,Object>();
		    		labMap.put("LABEL_NO", laberNo);  //LABEL_NO
		    		labMap.put("LABEL_STATUS", LABEL_STATUS); 
		    		labMap.put("QC_RESULT_CODE", QC_RESULT_CODE);
		    		labMap.put("RECEIPT_NO", m.get("RECEIPT_NO"));
		    		labMap.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO")+"");
		    		labMap.put("WH_NUMBER", m.get("WH_NUMBER"));
		    		labMap.put("LIFNR", m.get("LIFNR"));
		    		labMap.put("LIKTX", m.get("LIKTX"));
		    		updateList.add(labMap);
				}
			} else {
				
				Double RECEIPT_QTY=Double.valueOf(m.get("RECEIPT_QTY").toString());
				Double FULL_BOX_QTY=Double.valueOf(m.get("FULL_BOX_QTY").toString());
				int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);
				
				StringBuffer LABEL_NO_BF = new StringBuffer();
				
				for(int i=1;i<=box_num;i++) {
					Map<String,Object> sk=new HashMap<String,Object>();
					sk.putAll(m);

					String LABEL_NO="";
					Map<String,Object> doc=null;
					doc=wmsCDocNoService.getDocNo(params);
					if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
						throw new RuntimeException(doc.get("MSG").toString());
					}
					LABEL_NO=doc.get("docno").toString();
					String BOX_SN=i+"/"+box_num;
					Double BOX_QTY=FULL_BOX_QTY;//装箱数量，计算得出
					String END_FLAG="0";
					if(i==box_num && i!=1) {
						BOX_QTY = RECEIPT_QTY-(box_num-1)*FULL_BOX_QTY;
						if(BOX_QTY.compareTo(FULL_BOX_QTY) != 0) {
							END_FLAG="X";
						}
					}

					sk.put("LABEL_NO", LABEL_NO);
					sk.put("LABEL_STATUS", LABEL_STATUS);
					sk.put("QC_RESULT_CODE", QC_RESULT_CODE);
					sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
					sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO")+"");
					sk.put("TRY_QTY", m.get("TRY_QTY"));
					sk.put("GR_QTY", m.get("RECEIPT_QTY"));
					sk.put("SOBKZ", m.get("SOBKZ"));
					sk.put("CREATOR", m.get("CREATOR"));
					sk.put("CREATE_DATE", m.get("CREATE_DATE"));
					sk.put("WH_NUMBER", m.get("WH_NUMBER"));
					sk.put("LGORT", m.get("LGORT"));
					sk.put("BOX_SN", BOX_SN);
					sk.put("FULL_BOX_QTY", m.get("FULL_BOX_QTY"));
					sk.put("BOX_QTY", BOX_QTY);
					sk.put("END_FLAG", END_FLAG);

					sk.put("PRODUCT_DATE", m.get("PRODUCT_DATE")==null?null:m.get("PRODUCT_DATE"));
					sk.put("EFFECT_DATE", m.get("EFFECT_DATE")==null?"":m.get("EFFECT_DATE").toString());
					sk.put("LIFNR", m.get("LIFNR"));
					sk.put("LIKTX", m.get("LIKTX"));
					sk.put("BEDNR", m.get("BEDNR")==null?"":m.get("BEDNR").toString());
					sk.put("PO_NO", m.get("PO_NO")==null?"":m.get("PO_NO").toString());
					sk.put("PO_ITEM_NO", m.get("PO_ITEM_NO")==null?"":m.get("PO_ITEM_NO").toString());

					skList.add(sk);
					
					LABEL_NO_BF.append(LABEL_NO).append(",");
				}
				
				if(LABEL_NO_BF.length()>0) {
					m.put("LABEL_NO", LABEL_NO_BF.toString().substring(0, LABEL_NO_BF.length()-1));
				}else {
					m.put("LABEL_NO", "");
				}
				
			}
		}
		if(updateList.size()>0) {
	    	commonService.updateLabel(updateList);
		} else {
			wmsInReceiptDao.insertCoreLabel(skList);
		}
	}
	
	
	/**
	 * 质检:读取工厂质检配置表：
		 *  00质检：产生送检单，读取和匹配物料质检配置表的免检物料数据，产生质检结果为免检的质检结果数据和质检记录数据，对应送检单抬头状态为全部完成，
		 *  行项目状态为完成，收货单可进仓数量等于对应送检单数量。
		 *  01免检 产生送检单，读取和匹配物料质检配置表的免检物料数据，产生质检结果为免检的质检结果数据和质检记录数据，对应送检单抬头状态为全部完成，
		 *  行项目状态为完成，收货单可进仓数量等于对应送检单数量。
		 *  02 无需质检 不产生送检单，收货单可进仓数量等于收货数量
	 * @return 收货单可进仓数量
	 */
	public String doQualityCheck(Map<String, Object> params,List<Map<String, Object>> matList) {
		String TEST_FLAG=params.get("TEST_FLAG").toString();//工厂维护质检配置
		List<Map<String,Object>> match_list=new ArrayList<Map<String,Object>>();//检验结果明细数据列表
		
		String INSPECTION_NO="";//送检单号
		String INSPECTION_STATUS="00";//送检单抬头状态默认为创建（00） 送检单状态  字典定义：（00创建，01部分完成，02全部完成，04关闭）
		String INSPECTION_TYPE="01";//送检单类型为来料质检（01）
		String WH_NUMBER=(String)matList.get(0).get("WH_NUMBER");//仓库号
		String INSPECTION_ITEM_STATUS="00";//送检单行项目状态默认为未质检  （00未质检，01已质检，02关闭）	
		String CREATOR=(String)matList.get(0).get("CREATOR");
		String CREATE_DATE=(String)matList.get(0).get("CREATE_DATE");
		//送检单抬头数据
		Map<String,Object> INSPECTION_MAP=new HashMap<String,Object>();
		
		//工厂维度配置的质检状态为“质检”、免检情况
		if("00".equals(TEST_FLAG)||"01".equals(TEST_FLAG)) {
			params.put("WMS_DOC_TYPE", "03");//送检单
			Map<String,Object> doc=wmsCDocNoService.getDocNo(params);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			INSPECTION_NO=doc.get("docno").toString();//送检单号
			
			int i=1;
			for(Map m:matList) {
				m.put("INSPECTION_NO", INSPECTION_NO);
				m.put("INSPECTION_ITEM_NO", i++);//送检单行项目号
				m.put("INSPECTION_ITEM_STATUS", INSPECTION_ITEM_STATUS);
				m.put("STOCK_SOURCE", "01");//送检单明细库存来源为收料房
				m.put("INSPECTION_QTY", m.get("RECEIPT_QTY"));//送检数量
				
				//判断物料维护质检配置是否为“免检”
				if(m.get("TEST_FLAG")!=null && "01".equals(m.get("TEST_FLAG").toString())) {
					//免检
					m.put("INSPECTION_ITEM_STATUS", "01"); //送检单状态修改为，已质检
					match_list.add(m);
				}
				
			}
			String QC_RECORD_NO="";//检验记录号
			String QC_RESULT_NO="";//检验结果号
			if(match_list !=null && match_list.size()>0) { 
				//免检物料，产生质检结果和质检记录
				doc=new HashMap<String,Object>();
				params.put("WMS_DOC_TYPE", "04");//检验记录
				doc=wmsCDocNoService.getDocNo(params);//检验记录号
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				QC_RECORD_NO=doc.get("docno").toString();//检验记录号
				
				params.put("WMS_DOC_TYPE", "05");//检验结果
				doc=wmsCDocNoService.getDocNo(params);//检验结果
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				QC_RESULT_NO=doc.get("docno").toString();//检验结果

				//根据匹配行项目生成质检记录和质检结果
				int j=1;
				for(Map<String,Object> m:match_list) {
					m.put("QC_RECORD_NO", QC_RECORD_NO);
					m.put("QC_RESULT_NO", QC_RESULT_NO);
					
					m.put("QC_RESULT_CODE", "11");//检验结果：免检
					m.put("QC_RESULT_TEXT", "免检");//检验结果：免检
					m.put("QC_RESULT", "免检");
					m.put("QC_STATUS", "02");//质检状态 字典定义：00未质检 01质检中 02已质检				
					m.put("QC_RESULT_ITEM_NO", j);
					m.put("QC_RECORD_ITEM_NO", j);
					m.put("QC_RECORD_TYPE", "01");//质检记录类型 字典定义： 01初判 02重判
					m.put("RESULT_QTY", m.get("RECEIPT_QTY"));//质检结果数量
					m.put("RECORD_QTY", m.get("RECEIPT_QTY"));//质检结果数量
					m.put("QC_DATE", m.get("CREATE_DATE"));
					j++;
				}
				/**
				 * 保存检验记录和检验结果
				 */
				wmsInReceiptDao.insertQCResult(match_list);
				wmsInReceiptDao.insertQCRecord(match_list);	
				
				//根据质检结果行项目更新收货单行项目的可进仓数量
				wmsInReceiptDao.updateReceiptInableQty(match_list);	
				
				if(match_list.size()<matList.size()) {
					INSPECTION_STATUS="01";//送检单抬头状态默认为创建（00） 送检单状态  字典定义：（00创建，01部分完成，02全部完成，04关闭）
				}else {
					INSPECTION_STATUS="02";
				}
			}
			
			INSPECTION_MAP.put("INSPECTION_NO", INSPECTION_NO);
			INSPECTION_MAP.put("INSPECTION_STATUS", INSPECTION_STATUS);
			INSPECTION_MAP.put("INSPECTION_TYPE", INSPECTION_TYPE);
			INSPECTION_MAP.put("WMS_NO", params.get("WMS_NO"));
			INSPECTION_MAP.put("WERKS", params.get("WERKS"));
			INSPECTION_MAP.put("WH_NUMBER", WH_NUMBER);
			INSPECTION_MAP.put("CREATOR", CREATOR);
			INSPECTION_MAP.put("CREATE_DATE", CREATE_DATE);
			INSPECTION_MAP.put("IS_AUTO", "X");
			INSPECTION_MAP.put("DEL", "0");
			//保存送检单抬头
			wmsInReceiptDao.insertInspectionHead(INSPECTION_MAP);
			//保存送检单明细
			wmsInReceiptDao.insertInspectionItem(matList);
			
			//更新WMS凭证行项目的送检单号和行项目号
			wmsInReceiptDao.updateWMSDocItemInspection(matList);
			
		}else {//无需质检，更新收货单行项目的可进仓数量为收货数量
			for(Map m:matList) {
				m.put("RESULT_QTY", m.get("RECEIPT_QTY"));//质检结果数量
			}
			wmsInReceiptDao.updateReceiptInableQty(matList);	
		}
		return INSPECTION_NO;
		
	}
	
	@Override
	public PageUtils loglist(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		params.put("endDate", params.get("endDate") + " 23:59:59");
		
		int start = 0;int end = 0;
		int count = wmsInPutawayStatusAndLogDao.getloglistCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String, Object>> list = wmsInPutawayStatusAndLogDao.getloglist(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
        
	}
	
	@Override
	public List<Map<String,Object>> getPutawayInfo(Map<String, Object> params){
		return wmsInPutawayStatusAndLogDao.getPutawayInfo(params);
	}
	
	@Override
	public int updatePutawayLog(List<Map<String,Object>> params) {
		return wmsInPutawayStatusAndLogDao.updatePutawayLog(params);
	}
	
	/**
	 * 保存SAP凭证信息
	 * doctype:1、交货单凭证、2、普通账务凭证
	 */
	@Override
	public void saveSAPMatDoc(String doctype){
		List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
		List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
		System.out.println("Start >>>>>>>>>>>>>>>>>>>saveSAPMatDoc()");
		if("1".equals(doctype)) {
			//交货单过账，通过交货单获取物料凭证
			/**
			 * 通过调用SAP服务接口，读取SAP交货单数据
			 */
			List<Map<String,Object>> matDocEmptList = wmsInPutawayStatusAndLogDao.getSapMatDocEmpty();
			for(Map<String,Object> matdocempt : matDocEmptList) {
				String werks = matdocempt.get("WERKS_TO") ==null?"": matdocempt.get("WERKS_TO").toString();
				
				Map<String,String> head= new HashMap<String,String>();
				head.put("REF_WMS_NO", matdocempt.get("WMS_NO").toString());
				head.put("DOC_DATE", matdocempt.get("PZ_DATE").toString());
				head.put("PSTNG_DATE", matdocempt.get("JZ_DATE").toString());
				head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
				head.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				head.put("WH_NUMBER", matdocempt.get("WH_NUMBER").toString());
				head.put("TIME_STAMP_STR", matdocempt.get("TIME_STAMP_STR").toString());
				
				Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(matdocempt.get("VBELN").toString());
				//交货单关联的凭证信息 docList
				List<Map<String,Object>> dnDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");
				if(null != dnDocList && dnDocList.size()>0) {
					boolean flag = false;
					for (Map<String, Object> map : dnDocList) {
						/**
						 * 判断获取的凭证是否为物料凭证（凭证号不为空且移动类型不为空）
						 */
						if(map.get("VBELN")!=null && map.get("BWART")!=null) {
							//过账成功，获取到了物料凭证
							flag = true;
							
							head.put("MAT_DOC", map.get("VBELN").toString());
							head.put("DOC_YEAR", map.get("MJAHR").toString());
							
							Map<String, String> sapDocMap = new HashMap<String,String>();
							sapDocMap.put("MAT_DOC", map.get("VBELN").toString());
							sapDocMap.put("MATDOC_ITM", map.get("POSNN").toString());
							sapDocMap.put("DOC_YEAR", map.get("MJAHR").toString());
							sapDocMap.put("MOVE_TYPE", map.get("BWART").toString());
							sapDocMap.put("MATERIAL", map.get("MATNR").toString());
							sapDocMap.put("PLANT", werks);
							sapDocMap.put("STGE_LOC", "00ZJ");
							sapDocMap.put("ENTRY_QNT", map.get("RFMNG").toString());
							sapDocMap.put("ENTRY_UOM", map.get("MEINS").toString());
							sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
							sap_detail_list.add(sapDocMap);
							
						}
					}
					if(flag) {
						sap_head_list.add(head);
					}
				}
			}
			if (sap_head_list.size() > 0) {
				wmsInPutawayStatusAndLogDao.updatePutawayStatus(sap_head_list);
			}
			
		} else {
			//DOTO 普通凭证类型
		}
		
		if (sap_head_list.size() > 0) {
			commonDao.insertSapDocHead(sap_head_list);
			commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
			
			String MAT_DOC_STR = "645+101"+":"+sap_head_list.get(0).get("MAT_DOC")+";";

			/**
			 * 更新WMS凭证抬头SAP物料凭证信息
			 */
			Map<String,Object> wmsDocMap = new HashMap<String,Object>();
			wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
			wmsDocMap.put("WMS_NO", sap_head_list.get(0).get("REF_WMS_NO"));
			commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);
		}
		
	}
	
	/**
	 * 获取凭证已收数量
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getReceivedQty(List<Map<String, Object>> paramList){
		return wmsInPutawayStatusAndLogDao.getReceivedQty(paramList);
	}
}
