package com.byd.wms.webservice.ws.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.webservice.common.remote.BusinessRemote;
import com.byd.wms.webservice.common.util.WebServiceAccessLock;
import com.byd.wms.webservice.ws.dao.WmsCWhDao;
import com.byd.wms.webservice.ws.dao.WmsWebServiceKnFreezeRecordDao;
import com.byd.wms.webservice.ws.dao.WmsWebServiceQmsCheckDao;
import com.byd.wms.webservice.ws.service.WmsWebServiceQmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.*;


@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsWebServiceQmsService" // 接口地址
)
public class WmsWebServiceQmsImpl implements WmsWebServiceQmsService {
	
	@Autowired
	BusinessRemote businessRemote;
	@Autowired
	WmsWebServiceKnFreezeRecordDao wmsWebServiceKnFreezeRecordDao;
	@Autowired
	WmsWebServiceQmsCheckDao wmsWebServiceQmsCheckDao;
	@Autowired
	WmsCWhDao wmsCWhDao;

	
	//private WarehouseTasksService warehouseTasksService;

	/**
     * WMS获取QMS把需物料冻结的物料清单
     * @param 
     * WERKS：工厂
     * WH_NUMBER：仓库号
     * MATNR：物料号
     * BATCH: 批次号
     * FREEZE_TYPE: freezeRecord操作类型【00：冻结；01：解冻：02：修改有效期】
     * EFFECT_DATE:有效期
     * REASON_CODE:解冻代码
     * REASON:解冻原因
     * EDITOR:冻结账号
     * @return
     */
	@Override
	public String transFreezeMatInfo(String param) {
		String curTime = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		JSONObject res = new JSONObject();
		WebServiceAccessLock.operatelock("WmsFreezeService", WebServiceAccessLock.COUNT_TYPE_ADD);
		int ac_count=WebServiceAccessLock.currentCount;
		System.out.println("WmsFreezeService当前访问数:" + ac_count);
		if(ac_count >=WebServiceAccessLock.MAX_COUNT) {
			res.put("MSGTY","fail");
			res.put("MSGTX","WmsFreezeService接口访问次数过多，请稍后重试！");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
			return res.toString();
		}	
		
		List<Map<String,Object>> wmsDocMatList = new ArrayList<Map<String,Object>>(); //过账使用的物料清单
		String WMS_NO="";
		String SAP_NO="";
		String BUSINESS_NAME="";
		String BUSINESS_TYPE="";
		String BUSINESS_CLASS="";
		
		// 解析参数
		JSONObject js = (JSONObject) JSON.parse(param);
		String werks=js.get("WERKS").toString();
		String wh_number=js.get("WH_NUMBER").toString();
		String matnr=js.get("MATNR").toString();
		String batch=js.get("BATCH").toString();
    	String freezeType=js.get("FREEZE_TYPE").toString();
    	String effectDate=js.containsKey("EFFECT_DATE")?js.getString("EFFECT_DATE"):null;
    	String reasonCode=js.containsKey("REASON_CODE")?js.getString("REASON_CODE"):null;
    	String reason=js.containsKey("REASON")?js.getString("REASON"):null;
    	String editor=js.get("EDITOR").toString();
    	Map<String,Object> m = new HashMap<String,Object>();
    	
		try {
			List<Map<String,Object>> labelStatusList=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> freezeRecordList=new ArrayList<Map<String,Object>>();
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("WERKS",werks);
			params.put("WH_NUMBER", wh_number);
			params.put("MATNR", matnr);
			params.put("BATCH", batch);
			params.put("FREEZE_TYPE", freezeType);
			params.put("EFFECT_DATE", effectDate);
			params.put("REASON_CODE", reasonCode);
			params.put("REASON", reason);
			params.put("EDITOR", editor);
			
			Map<String,Object> paramInfo = new HashMap<String,Object>();
			paramInfo.put("WERKS",werks);
			paramInfo.put("WH_NUMBER", wh_number);
			Map<String,Object> result=wmsCWhDao.getWmsCWh(paramInfo);
			String freeze =result.get("FREEZEPOSTSAPFLAG").toString();
			
			//获取物料信息
			List<Map<String,Object>> list=wmsWebServiceKnFreezeRecordDao.getStockInfoList(params);
			//WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期 HEADER_TXT：抬头文本 ,
			// * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型,BUSINESS_CLASS:业务分类
			
			if(list.size()>0) {
				for(Map<String,Object> k : list) {				
					k.put("FREEZE_TYPE",freezeType);
					k.put("EDITOR",editor);
			    	k.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));				    
				    k.put("EFFECT_DATE", effectDate);
				    k.put("REASON_CODE", reasonCode);
				    k.put("REASON", reason);
				    freezeRecordList.add(k);
				    if(freeze.equals("X")) {
				    	//SAP 物料清单
			    	    Map<String,Object> wmsDocMatMap = new HashMap<String,Object>();
					    String type="";
					    if(freezeType.equals("00")) {
					    	BUSINESS_NAME="81";
					    	BUSINESS_TYPE="17";
					    	BUSINESS_CLASS="10";
						    type="344";
					    }else if(freezeType.equals("01")){
					    	BUSINESS_NAME="82";
					    	BUSINESS_TYPE="17";
					    	BUSINESS_CLASS="10";
						    type="343";
					    }
					    wmsDocMatMap.put("BUSINESS_NAME", BUSINESS_NAME);
					    wmsDocMatMap.put("BUSINESS_TYPE", BUSINESS_TYPE);
					    wmsDocMatMap.put("BUSINESS_CLASS", BUSINESS_CLASS);
					    wmsDocMatMap.put("WERKS", k.get("WERKS"));
					    wmsDocMatMap.put("MATNR", k.get("MATNR"));
					    wmsDocMatMap.put("LGORT", k.get("LGORT"));
					    String sobkz=k.get("SOBKZ").toString();
					    if(sobkz.equals("K")) {
					    	wmsDocMatMap.put("LIFNR", k.get("LIFNR"));
					    	wmsDocMatMap.put("WMS_MOVE_TYPE",type.concat("_K"));
						    wmsDocMatMap.put("SAP_MOVE_TYPE",type.concat("_K"));
					    }else if(sobkz.equals("E")) {
					    	wmsDocMatMap.put("SO_NO", k.get("SO_NO"));
					    	wmsDocMatMap.put("SO_ITEM_NO", k.get("SO_ITEM_NO"));
					    	wmsDocMatMap.put("WMS_MOVE_TYPE",type.concat("_E"));
						    wmsDocMatMap.put("SAP_MOVE_TYPE",type.concat("_E"));
					    }
					    wmsDocMatMap.put("SOBKZ", k.get("SOBKZ"));
					    wmsDocMatMap.put("QTY_WMS", k.get("QUANTITY"));
						wmsDocMatMap.put("QTY_SAP", k.get("QUANTITY"));
					    wmsDocMatMap.put("UNIT", k.get("MEINS"));
					    wmsDocMatList.add(wmsDocMatMap);
					}
				}
				
				// FREEZE_TYPE 操作类型【00：冻结；01：解冻；02：修改有效期】
				if(freezeType.equals("00")) {
					// 启用标签管理，更新标签表WMS_CORE_LABEL的LABEL_STATUS【冻结：11已冻结；解冻：07已进仓】
					wmsWebServiceKnFreezeRecordDao.batchUpdateCoreLabelStatus(freezeRecordList);
					
					// 更新库存记录(冻结：非限制库存stock_qty减少，冻结数量freeze_qty增加；解冻反之)
					int updateCountB=wmsWebServiceKnFreezeRecordDao.batchUpdateStock(freezeRecordList);
					
					// 插入冻结记录表
					int insertCount=wmsWebServiceKnFreezeRecordDao.saveFreezeRecord(freezeRecordList);
					if(insertCount>0) {
						res.put("MSGTY","success");
						res.put("MSGTX","冻结成功");
						res.put("PROGRAM","WMS");
						res.put("CTIME",curTime);
					}else {
						res.put("MSGTY","fail");
						res.put("MSGTX","冻结失败");
						res.put("PROGRAM","WMS");
						res.put("CTIME",curTime);
					}
					
				}else if(freezeType.equals("01")) {
					// 启用标签管理，更新标签表WMS_CORE_LABEL的LABEL_STATUS【冻结：11已冻结；解冻：07已进仓】
					int updateCountC=wmsWebServiceKnFreezeRecordDao.batchUpdateCoreLabelStatus(freezeRecordList);
					
					// 更新库存记录(冻结：非限制库存stock_qty减少，冻结数量freeze_qty增加；解冻反之)
					int updateCountB=wmsWebServiceKnFreezeRecordDao.batchUpdateStock(freezeRecordList);
					
					//更新有效期 批次流水表WMS_CORE_MAT_BATCH的EFFECT_DATE字段
					int updateCountA=wmsWebServiceKnFreezeRecordDao.updateMatBatch(freezeRecordList);
					
					// 插入解冻记录表
					int insertCount=wmsWebServiceKnFreezeRecordDao.saveFreezeRecord(freezeRecordList);
					
					if(insertCount>0) {		
						res.put("MSGTY","success");
						res.put("MSGTX","解冻成功，有效期更新成功！");
						res.put("PROGRAM","WMS");
						res.put("CTIME",curTime);
					}else {
						res.put("MSGTY","fail");
						res.put("MSGTX","解冻失败！");
						res.put("PROGRAM","WMS");
						res.put("CTIME",curTime);
					}
				}else if(freezeType.equals("02")) {
					//更新有效期 批次流水表WMS_CORE_MAT_BATCH的EFFECT_DATE字段
					int updateCountA=wmsWebServiceKnFreezeRecordDao.updateMatBatch(freezeRecordList);
					res.put("MSGTY","success");
					res.put("MSGTX","有效期更新成功！");
					res.put("PROGRAM","WMS");
					res.put("CTIME",curTime);
					
				}			
			}else {
				res.put("MSGTY","fail");
				res.put("PROGRAM","WMS");
				res.put("CTIME",curTime);
				res.put("MSGTX", "该批次物料库存数量为0或者没有该批次物料的库存信息");
			}
			if(freeze.equals("X")) {
				//SAP过账
				String date=DateUtils.format(new Date(), DateUtils.DATE_PATTERN);
				Map<String,Object> head=new HashMap<String,Object>();
				head.put("PZ_DATE", date);
				head.put("JZ_DATE", date);
				head.put("PZ_YEAR", date.substring(0,4));
				head.put("HEADER_TXT", "");
				head.put("TYPE",  "00");//标准凭证
				head.put("CREATOR", "");
				head.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				Map<String,Object> Param=new HashMap<String, Object>();
				if(wmsDocMatList!=null&&wmsDocMatList.size()>0){
					Param.put("head",head);
					Param.put("wmsDocMatList",wmsDocMatList);
					String WMS_NO_temp=businessRemote.saveWMSDoc(Param);
					WMS_NO=WMS_NO+" "+WMS_NO_temp;
					Map<String,Object> SapParam=head;
					SapParam.put("matList", wmsDocMatList);
					SapParam.put("WERKS", werks);
					SapParam.put("BUSINESS_NAME", BUSINESS_NAME);
					SapParam.put("BUSINESS_TYPE", BUSINESS_TYPE);
					SapParam.put("BUSINESS_CLASS", BUSINESS_CLASS);
					String SAP_NO_temp=businessRemote.doSapPost(SapParam);
					SAP_NO=SAP_NO+" "+SAP_NO_temp;
					System.out.println("WMS_NO:"+WMS_NO);	
					System.out.println("SAP_NO:"+SAP_NO);
				}
			}		
			System.out.println("WmsFreezeService 更新数据:"+list.size());	
			WebServiceAccessLock.operatelock("WmsFreezeService", WebServiceAccessLock.COUNT_TYPE_SUB);
			return res.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("transferMapInfo: "+e.getMessage());
			res.put("MSGTY","fail");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
			res.put("MSGTX", "接口异常，传输失败！");
			if(e.getMessage().contains("Duplicate entry")) {
				res.put("MSGTX", e.getMessage());
			}
			WebServiceAccessLock.operatelock("WmsFreezeService", WebServiceAccessLock.COUNT_TYPE_SUB);
			return res.toString();
		}
		//传入立库 调用立库webService 接口
		
		
	}

	/**
	 * QMS把质检结果返回WMS
	 * @param result_info
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String qualityResultService(String result_info) {
		String curTime = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String curUser = "QMS";
		JSONObject res = new JSONObject();
		String resultVstatus="100";//接口执行状态:[{"100","成功"},{"101","失败"}]
		WebServiceAccessLock.operatelock("qualityResultService", WebServiceAccessLock.COUNT_TYPE_ADD);
		int ac_count=WebServiceAccessLock.currentCount;
		System.out.println("qualityResultService当前访问数:" + ac_count);
		if(ac_count >=WebServiceAccessLock.MAX_COUNT) {
			res.put("MSGTY","fail");
			res.put("MSGTX","qualityResultService接口访问次数过多，请稍后重试！");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
//			res.put("success", false);
//			res.put("msg", "qualityResultService接口访问次数过多，请稍后重试！");
			resultVstatus="101";
			return res.toString();
		}

		/**
		 * 是否
		 */
		Map<String,String> yesOrNotMap= new HashMap<String,String>();
		yesOrNotMap.put("是","03");yesOrNotMap.put("否","04");
		/**
		 * 质检结果（QC_RESULT_CODE）（接收状态）（01让步接收 02合格 03不合格 04挑选使用 05紧急放行 06特采 07退货 08实验中 09评审中 10精测中';）
		 */
		Map<String,String> qcResultCodeMap= new HashMap<String,String>();
		qcResultCodeMap.put("让步接收","01");qcResultCodeMap.put("合格","02");qcResultCodeMap.put("不合格","03");qcResultCodeMap.put("挑选使用","04");qcResultCodeMap.put("紧急放行","05");
		qcResultCodeMap.put("特采","06");qcResultCodeMap.put("退货","07");qcResultCodeMap.put("实验中","08");qcResultCodeMap.put("评审中","09");qcResultCodeMap.put("精测中","10");

		JSONArray jsa=null;
		try {
			System.out.println("qualityResultService_Info:"+result_info);
			jsa=JSONArray.parseArray(result_info);

		}catch(Exception e) {
			res.put("MSGTY","fail");
			res.put("MSGTX","qualityResultService接口：参数不是有效JSONArray数据！");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
//			res.put("success", false);
//			res.put("msg", "qualityResultService接口：参数不是有效JSON数据！");
			resultVstatus="101";
			return res.toString();
		}
		Map<String, Double> checkDuplicatedMap = new HashMap<>();
		String checkKeyNo;
		Set<String> qualifiedQcNoSet=new HashSet<>();
		List<Map<String,Object>> yQualifiedQcNoList= new ArrayList<>();
		Set<String> noQualifiedQcNoSet=new HashSet<>();
		List<Map<String,Object>> nQualifiedQcNoList= new ArrayList<>();
		List<Map<String,Object>> coreNeedUpdateList= new ArrayList<>();
		List<Map<String,Object>> tQualifiedQcNoList= new ArrayList<>();
		List<Map<String,Object>> map_list= new ArrayList<>();
		for (int i = 0; i < jsa.size(); i++) {
			String inspectionNo,inspectionItemNo;
			String[] inspection_number;
			JSONArray labelNoArr;
			JSONObject sa = jsa.getJSONObject(i);
			Map<String,Object> m = new HashMap<>();
			/**
			 * QC_RESULT_NO	VARCHAR2(32)	检验记录号	是	QC_RESULT_NO
			 * QC_RESULT_ITEM_NO	VARCHAR2(10)	检验记录行项目号	否	QC_RESULT_ITEM_NO
			 * INSPECTION_NUMBER	VARCHAR2(50)	送检单号	是	INSPECTION_NO 送检单行项目	是	INSPECTION_ITEM_NO	通过“/”分开
			 * WERKS	VARCHAR2(8)	工厂代码	是	WERKS
			 * WH_NUMBER	VARCHAR2(16)	仓库编号	是	WH_NUMBER
			 * RECEIPT_NO	VARCHAR2(32)	收货单号	是	关联查询收货单【WMS_IN_RECEIPT】-RECEIPT_NO
			 * RECEIPT_ITEM_NO	VARCHAR2(10)	收货单行项目	是	关联查询收货单【WMS_IN_RECEIPT】-RECEIPT_ITEM_NO
			 * BATCH	VARCHAR2(32)	BYD批次	是	BATCH
			 * MATNR	VARCHAR2(18)	物料编码	是	MATNR
			 * INABLE_QTY	NUMBER(32,3)	送检单合格数量	是	需要关联累加写入收货单【WMS_IN_RECEIPT】-INABLE_QTY
			 * RETURNABLE_QTY	NUMBER(32,3)	送检单不合格数量	是	需要关联累加写入收货单【WMS_IN_RECEIPT】-RETURNABLE_QTY
			 * ZDHG	VARCHAR2(1)	整单质检结果（新增）	是	X：整单合格 空 对应多个结果，传输条码	待定(yes,no,t)
			 * LABEL_NO	VARCHAR2(32)	条码ID	否	关联标签表【wms_core_label】-LABEL_NO	数组
			 * PACK_LABEL_NO	VARCHAR2(32)	包条码ID	否	关联标签表【wms_core_label】-待定
			 * PALLET_LABEL_NO	VARCHAR2(32)	栈板条码ID	否	关联标签表【wms_core_label】-待定
			 * BOX_QTY	NUMBER(32,3)	最小包装数量：	否	关联标签表【wms_core_label】-BOX_QTY
			 * LABEL_STATUS	VARCHAR2(20)	条码是否合格	否	关联回写标签表【wms_core_label】-LABEL_STATUS  是对应值03 否对应值04
			 * QC_RESULT_CODE	VARCHAR2(20)	质检结果（接收状态）（01让步接收 02合格 03不合格 04挑选使用 05紧急放行 06特采 07退货 08实验中 09评审中 10精测中';）	　是	关联回写标签表【wms_core_label】-QC_RESULT_CODE
			 * RETURN_REASON_TYPE	VARCHAR2(2)	质检原因（质检备注）	否	关联回写质检结果【wms_qc_result】-RETURN_REASON_TYPE
			 * QC_PEOPLE	VARCHAR2(50)	质检人员	是	关联回写质检结果【wms_qc_result】-QC_PEOPLE
			 * QC_DATE	VARCHAR2(20)	质检日期:质检时间	是	关联回写质检结果【wms_qc_result】-QC_DATE
			 * QC_RECORD_TYPE	VARCHAR2(20)	质检记录类型（01初判 02重判）	否	关联回写质检结果【wms_qc_result】-QC_RECORD_TYPE
			 *
			 *
			 * 	接口返回
			 * MSGTY	VARCHAR2(1)	接口调用返回结果			关联回写送检单抬头【WMS_QC_INSPECTION_HEAD】-INSPECTION_STATUS和送检单明细【WMS_QC_INSPECTION_ITEM】-INSPECTION_ITEM_STATUS-INSPECTION_STATUS  先更新送检单明细表01，再判断对应送检单所有行项目状态是否是01，如果是就更新送检单抬头02，如果不是更新送检单抬头01
			 * MSGTX	VARCHAR2(220)	接口消息
			 * PROGRAM	VARCHAR2(100)	系统标识 （调用接口类型）
			 * CTIME	VARCHAR2(20)	日期时间
			 */
			m.put("QC_RESULT_NO", sa.get("QC_RESULT_NO"));
			m.put("QC_RESULT_ITEM_NO", sa.get("QC_RESULT_ITEM_NO"));
			m.put("INSPECTION_NUMBER", sa.get("INSPECTION_NUMBER"));
//			INSPECTION_NO=m.get("INSPECTION_NUMBER").toString().substring(0,14);//CSSJ0000000105/0010,CSSJ0000000105,0010
//			INSPECTION_ITEM_NO=m.get("INSPECTION_NUMBER").toString().substring(14);
			inspection_number = m.get("INSPECTION_NUMBER").toString().split("/");
			inspectionNo=inspection_number[0];
			inspectionItemNo=inspection_number[1];
			m.put("INSPECTION_NO", inspectionNo);
			m.put("INSPECTION_ITEM_NO",inspectionItemNo);
			m.put("WERKS", sa.get("WERKS"));
			m.put("WH_NUMBER", sa.get("WH_NUMBER"));
			m.put("RECEIPT_NO", sa.get("RECEIPT_NO"));
			m.put("RECEIPT_ITEM_NO", sa.get("RECEIPT_ITEM_NO"));
			m.put("BATCH", sa.get("BATCH"));
			m.put("MATNR", sa.get("MATNR"));
			m.put("INABLE_QTY", sa.get("INABLE_QTY"));
			m.put("RETURNABLE_QTY", sa.get("RETURNABLE_QTY"));
			m.put("ZDHG", sa.get("ZDHG"));//整单质检结果(yes,no,t)
			m.put("LABEL_NO", sa.get("LABEL_NO"));//数组
			m.put("PACK_LABEL_NO", sa.get("PACK_LABEL_NO"));
			m.put("PALLET_LABEL_NO", sa.get("PALLET_LABEL_NO"));
			m.put("BOX_QTY", sa.get("BOX_QTY"));
			if(sa.get("LABEL_STATUS") != null) {
				m.put("LABEL_STATUS", yesOrNotMap.get(sa.get("LABEL_STATUS").toString()));
			}
			if(sa.get("QC_RESULT_CODE") != null) {
//				m.put("QC_RESULT_CODE", qcResultCodeMap.get(sa.get("QC_RESULT_CODE").toString()));
				// 传输的是名称还是code?直接传的Code(确认了),从name转换为code，后面可以直接保存
				m.put("QC_RESULT_CODE", sa.get("QC_RESULT_CODE"));
			}
			m.put("RETURN_REASON_TYPE", sa.get("RETURN_REASON_TYPE"));
			m.put("QC_PEOPLE", sa.get("QC_PEOPLE"));
			m.put("QC_DATE", sa.get("QC_DATE"));
			m.put("QC_RECORD_TYPE", sa.get("QC_RECORD_TYPE"));
			m.put("CREATOR", curUser);
			m.put("CREATE_DATE", curTime);
			if(sa.get("ZDHG") !=null) {//Y：整单合格；N：整单不合格；T：挑选使用（需传条码号）,整单对应送检单
				if ("Y".equals(sa.get("ZDHG").toString())) {//整单合格
					qualifiedQcNoSet.add(m.get("INSPECTION_NO").toString());
					yQualifiedQcNoList.add(m);
				}
				else if ("N".equals(sa.get("ZDHG").toString())) {//整单不合格
					noQualifiedQcNoSet.add(m.get("INSPECTION_NO").toString());
					nQualifiedQcNoList.add(m);
				}else if("T".equals(sa.get("ZDHG").toString())){
					//当没有整单的质检结果，才需要逐个更新标签表数据
					tQualifiedQcNoList.add(m);
					labelNoArr = JSONArray.parseArray(sa.get("LABEL_NO").toString());
					for (int j = 0; j < labelNoArr.size(); j++) {
						String labelNoJObj = labelNoArr.get(j).toString();
						m.put("LABEL_NO", labelNoJObj);
						List<Map<String, Object>> coreLableMap = wmsWebServiceQmsCheckDao.getCoreLableByNo(m);
						if(coreLableMap == null || coreLableMap.isEmpty()){
							res.put("MSGTY","fail");
							res.put("MSGTX","qualityResultService接口：找不到对应的条码信息！");
							res.put("PROGRAM","WMS");
							res.put("CTIME",curTime);
							resultVstatus="101";
							return res.toString();
						}
						coreNeedUpdateList.add(m);
					}
				}else {
					res.put("MSGTY","fail");
					res.put("MSGTX","qualityResultService接口：ZDHG整单质检结果只能为Y、N、T！");
					res.put("PROGRAM","WMS");
					res.put("CTIME",curTime);
					resultVstatus="101";
					return res.toString();
				}
			}
			/**
			 * 必填校验
			 */
			if("".equals(m.get("QC_RESULT_NO")) || "".equals(m.get("INSPECTION_NO")) || "".equals(m.get("INSPECTION_ITEM_NO")) || "".equals(m.get("WERKS"))
					|| "".equals(m.get("WH_NUMBER")) || "".equals(m.get("RECEIPT_NO"))|| "".equals(m.get("RECEIPT_ITEM_NO"))|| "".equals(m.get("BATCH"))
					|| "".equals(m.get("MATNR")) || "".equals(m.get("INABLE_QTY"))|| "".equals(m.get("RETURNABLE_QTY"))
					|| "".equals(m.get("ZDHG"))|| "".equals(m.get("QC_PEOPLE"))|| "".equals(m.get("QC_DATE"))){
				res.put("MSGTY","fail");
				res.put("MSGTX","qualityResultService接口：接口必填字段缺失！");
				res.put("PROGRAM","WMS");
				res.put("CTIME",curTime);
				resultVstatus="101";
				return res.toString();
			}
			Map<String, Object> inReceiptMap = wmsWebServiceQmsCheckDao.getInReceiptByNo(m);
			if(inReceiptMap == null || inReceiptMap.isEmpty()){
				res.put("MSGTY","fail");
				res.put("MSGTX","qualityResultService接口：找不到对应的收货单明细信息！");
				res.put("PROGRAM","WMS");
				res.put("CTIME",curTime);
				resultVstatus="101";
				return res.toString();
			}
			List<Map<String, Object>> inspectionItemMap = wmsWebServiceQmsCheckDao.getInspectionItemByNo(m);
			if(inspectionItemMap == null || inspectionItemMap.isEmpty()){
				res.put("MSGTY","fail");
				res.put("MSGTX","qualityResultService接口：找不到对应的送检单明细信息！");
				res.put("PROGRAM","WMS");
				res.put("CTIME",curTime);
				resultVstatus="101";
				return res.toString();
			}
			//用于后面重复性校验
			checkKeyNo=m.get("INSPECTION_NO").toString()+":"+m.get("INSPECTION_ITEM_NO").toString();
			if(checkDuplicatedMap.containsKey(checkKeyNo)){
				double temQty = checkDuplicatedMap.get(checkKeyNo);
				checkDuplicatedMap.put(checkKeyNo,temQty+Double.parseDouble(m.get("INABLE_QTY").toString())+Double.parseDouble(m.get("RETURNABLE_QTY").toString()));
			}else{
				checkDuplicatedMap.put(checkKeyNo,Double.parseDouble(m.get("INABLE_QTY").toString())+Double.parseDouble(m.get("RETURNABLE_QTY").toString()));
			}
			map_list.add(m);
		}

		Map<String,Object> condMap= new HashMap<>();
		condMap.put("map_list", map_list);
		try {
			//校验重复性,质检结果相同的送检单和行项目的数量之和 不能大于送检单行项目的送检数量
			for(Map.Entry<String, Double> entry : checkDuplicatedMap.entrySet()){
				String mapKey = entry.getKey();
				double mapValue = entry.getValue();
				Map<String, Object> parm = new HashMap<>();
				parm.put("INSPECTION_NO", mapKey.split(":")[0]);
				parm.put("INSPECTION_ITEM_NO", mapKey.split(":")[1]);
				Map<String,Object> dbInspeItem=wmsWebServiceQmsCheckDao.getInspectionItemByNoINo(parm);
				if(mapValue > Double.parseDouble(dbInspeItem.get("INSPECTION_QTY").toString())){//当质检结果数量大于质检单数量
					res.put("MSGTY","fail");
					res.put("MSGTX","qualityResultService接口：质检结果数量大于质检单数量！");
					res.put("PROGRAM","WMS");
					res.put("CTIME",curTime);
					resultVstatus="101";
					return res.toString();
				}
			}
			if(!map_list.isEmpty()) {
				/**
				 * 更新收货单（WMS_IN_RECEIPT）里面的送检单合格数量（INABLE_QTY）和送检单不合格数量（RETURNABLE_QTY）
				 */
				wmsWebServiceQmsCheckDao.batchUpdateInReceipt(map_list);

				/**
				 * 关联回写标签表（wms_core_label）,需要添加质检人,质检日期和质检原因
				 * 这几个字段是否需要回写,不需要回写(确定了),Pack_LABEL_NO, pallet_label_no, BOX_QTY
				 * 条码是否合格（LABEL_STATUS  是对应值03 否对应值04），
				 * 质检结果（QC_RESULT_CODE）（接收状态）（01让步接收 02合格 03不合格 04挑选使用 05紧急放行 06特采 07退货 08实验中 09评审中 10精测中';）
				 */
				if(!coreNeedUpdateList.isEmpty()) {
					wmsWebServiceQmsCheckDao.batchUpdateCoreLabel(coreNeedUpdateList);
					/**
					 * 回写质检结果(是新增还是修改,新增(确定新增)又是怎么样的(因为有整单和部分的区别,整单关联查询出来新增一行,部分的有多少条存多少)?),保存质检结果
					 * 关联回写质检结果【WMS_QC_INSPECTION_ITEM】-质检原因（质检备注）RETURN_REASON_TYPE
					 * 关联回写质检结果【WMS_QC_INSPECTION_ITEM】-质检人员QC_PEOPLE
					 * 关联回写质检结果【WMS_QC_INSPECTION_ITEM】-质检日期:质检时间QC_DATE
					 * 关联回写质检结果【WMS_QC_INSPECTION_ITEM】-质检记录类型（01初判 02重判）
					 */
					Map<String,Object> tqcResultTMap= new HashMap<>();
					tqcResultTMap.put("map_list", tQualifiedQcNoList);
					wmsWebServiceQmsCheckDao.batSaveTQcResult(tqcResultTMap);
				}
				if(!yQualifiedQcNoList.isEmpty()){//整单合格
//					List<String> qualifiedQcList= new ArrayList<>();
//					qualifiedQcNoSet.forEach(set-> qualifiedQcList.add(set));
					wmsWebServiceQmsCheckDao.bthUpQCoreLabelByInspectionNo(yQualifiedQcNoList);
					//整单合格保存质检结果信息
					Map<String,Object> yqcResultTMap= new HashMap<>();
					yqcResultTMap.put("map_list", yQualifiedQcNoList);
					wmsWebServiceQmsCheckDao.batSaveYNQcResult(yqcResultTMap);
				}
				if(!nQualifiedQcNoList.isEmpty()){//整单不合格
//					List<String> noQualifiedQcList= new ArrayList<>();
//					noQualifiedQcNoSet.forEach(set-> noQualifiedQcList.add(set));
					wmsWebServiceQmsCheckDao.bthUpNqCoreLabelByInspectionNo(nQualifiedQcNoList);
					//整单不合格保存质检结果信息
					Map<String,Object> nqcResultTMap= new HashMap<>();
					nqcResultTMap.put("map_list", nQualifiedQcNoList);
					wmsWebServiceQmsCheckDao.batSaveYNQcResult(nqcResultTMap);
				}

				/**
				 * 更新WMS质检单状态
				 * 关联回写送检单抬头【WMS_QC_INSPECTION_HEAD】-INSPECTION_STATUS
				 * 和送检单明细【WMS_QC_INSPECTION_ITEM】-INSPECTION_ITEM_STATUS-INSPECTION_STATUS
				 * 先更新送检单明细表01，再判断对应送检单所有行项目状态是否是01，如果是就更新送检单抬头02，如果不是更新送检单抬头01
				 * 头信息状态,明细存在一个未质检则为部分质检.不存在则为全部质检.
				 */
				wmsWebServiceQmsCheckDao.batchUpInspeItemStatus(map_list);
				wmsWebServiceQmsCheckDao.batchUpInspeHeadStatus(map_list);
				wmsWebServiceQmsCheckDao.batchUpInspeHeadAllStatus(map_list);

			}
			res.put("MSGTY","success");
			res.put("MSGTX","qualityResultService质检结果传输处理成功！");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
//			res.put("success", true);
//			res.put("msg", "qualityResultService质检结果传输成功！");
			System.out.println("qualityResults 更新收货单数据和保存质检结果数据:"+map_list.size());
			WebServiceAccessLock.operatelock("qualityResultService", WebServiceAccessLock.COUNT_TYPE_SUB);
			resultVstatus="100";
			return res.toString();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("qualityResultService: "+e.getMessage());
			res.put("MSGTY","fail");
			res.put("MSGTX", "qualityResultService接口异常，传输失败！");
			if(e.getMessage().contains("Duplicate entry")) {
				res.put("MSGTX", e.getMessage());
			}
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
//			res.put("success", false);
//			res.put("msg", "qualityResultService接口异常，传输失败！");
//			if(e.getMessage().contains("Duplicate entry")) {
//				res.put("msg", e.getMessage());
//			}
			WebServiceAccessLock.operatelock("qualityResultService", WebServiceAccessLock.COUNT_TYPE_SUB);
			resultVstatus="101";
			System.out.println(res.toString());
			throw new RuntimeException("qualityResultService接口异常，传输失败！"+e.getMessage());
		}finally {
			/**
			 * 保存接口日志
			 * #{d.biz_id},#{d.flow_no},#{d.from_no},#{d.to_no},
			 * 			#{d.from_jsondata},#{d.to_jsondata},#{d.vstatus},
			 * 			#{d.cturid},#{d.ctdt},#{d.upurid},#{d.updt}
			 */
			Map<String, Object> logMap=new HashMap<>();
			logMap.put("biz_id","");
			logMap.put("flow_no","qualityResultService");
			logMap.put("from_no","QMS");
			logMap.put("to_no","WMS");
			logMap.put("from_jsondata",result_info);
			logMap.put("to_jsondata",res.toString());
			logMap.put("vstatus",resultVstatus);
			logMap.put("cturid",curUser);
			logMap.put("ctdt",curTime);
			logMap.put("upurid","");
			logMap.put("updt","");
			wmsWebServiceQmsCheckDao.insertLogInfo(logMap);
		}
	}

	/**
	 * WMS获取收料房报表数据
	 * @param params	 * 
	 * WERKS 工厂, 
	 * WH_NUMBER 仓库号, 
	 * LGORT 库位, 
	 * RECEIPT_NO 收货单号, 
	 * LIFNR 供应商, 
	 * MATNR  物料号, 
	 * BATCH 批次号, 
	 * GR_AREA  储位, 
	 * PO_NO 采购订单, 
	 * RECEIPT_DATE_START  收货 开始日期, 
	 * RECEIPT_DATE_END  收货 结束日期, 
	 * RECEIVER 收货人, 
	 * MANAGER 物料责任人, 
	 * 
	 * @return
	 */
	@Override
	public String getMaterialListService(String params) {
		
		JSONObject res = new JSONObject();
		String curTime = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		WebServiceAccessLock.operatelock("getMaterialListService", WebServiceAccessLock.COUNT_TYPE_ADD);
		int ac_count=WebServiceAccessLock.currentCount;
		System.out.println("getMaterialListService当前访问数:" + ac_count);
		if(ac_count >=WebServiceAccessLock.MAX_COUNT) {
			res.put("MSGTY","fail");
			res.put("MSGTX","getMaterialListService接口访问次数过多，请稍后重试！");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);	
			return res.toString();
		}
		// 解析参数
		JSONObject js =new JSONObject();
		try {
			System.out.println("qualityResultService_Info:"+params);
			js = (JSONObject) JSON.parse(params);	
		}catch(Exception e) {
			res.put("MSGTY","fail");
			res.put("MSGTX","getMaterialListService接口：参数不是有效JSON数据！");
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
			return res.toString();
		}
		//获取数据
		try {
			Map<String,Object> it = js.getInnerMap();
			R r=businessRemote.list(it); 
			int code =(int) r.get("code");
			if(code==0) {
				Map<String,Object> page=(HashMap<String, Object>) r.get("page");
				List<Map<String,Object>> list=(List<Map<String, Object>>) page.get("list");
				if(list.size()>0) {
					res.put("DATA",list);
				}else {
					res.put("DATA",null);
				}
				res.put("MSGTY","success");
				res.put("MSGTX","getMaterialListService接口,数据查询成功！");
			}else {
				res.put("MSGTY","fail");
				res.put("MSGTX","getMaterialListService接口,数据查询异常！");
				
			}
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
			
			
			return res.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("transferMapInfo: "+e.getMessage());
			res.put("MSGTY","fail");
			res.put("MSGTX", "getMaterialListService接口异常，传输失败！");
			if(e.getMessage().contains("Duplicate entry")) {
				res.put("MSGTX", e.getMessage());
			}
			res.put("PROGRAM","WMS");
			res.put("CTIME",curTime);
			
			WebServiceAccessLock.operatelock("getMaterialListService", WebServiceAccessLock.COUNT_TYPE_SUB);
			return res.toString();
		}
	}


	
}
