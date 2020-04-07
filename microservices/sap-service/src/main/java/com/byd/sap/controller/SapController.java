package com.byd.sap.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.byd.sap.modules.job.entity.SapMaterialEntity;
import com.byd.sap.modules.job.entity.SapMaterialUnitEntity;
import com.byd.sap.modules.job.entity.SapPoAccountEntity;
import com.byd.sap.modules.job.entity.SapPoHeadEntity;
import com.byd.sap.modules.job.entity.SapPoItemEntity;
import com.byd.sap.modules.job.entity.ScheduleJobLogEntity;
import com.byd.sap.service.IwmsSapRealtimeService;
import com.byd.sap.service.IwmsSapService;
import com.byd.sap.util.R;
import com.sap.conn.jco.JCoTable;

@RestController
public class SapController {
	private static final Logger log = LoggerFactory.getLogger("SapController");
	@Autowired
	private IwmsSapService wmsSapService;
	@Autowired
	private IwmsSapRealtimeService wmsSapRealtimeService;
	
	//从SAP系统获取物料凭证信息  FOR FeignClient
	@PostMapping("/SapBapiGoodsmvtDetail")
	@ResponseBody
	public Map<String,Object> SapBapiGoodsmvtDetail(@RequestParam String materialdocument,@RequestParam String matdocumentyear) {
		Map<String,Object> returnMap = new HashMap<String,Object>();	
		returnMap = wmsSapService.getSapBapiGoodsmvtDetail(materialdocument ,matdocumentyear);
		log.info("---->SapBapiGoodsmvtDetail returnMap = " + returnMap);
//		System.out.println("---->SapBapiGoodsmvtDetail returnMap = " + returnMap);
		return returnMap;
	}
	
	//从SAP系统获取成本中心信息   FOR FeignClient getCostcenterDetail
	@PostMapping("/SapBapiCostcenterDetail")
	@ResponseBody
	public Map<String,String> SapBapiCostcenterDetail(@RequestParam String Costcenter){
		Map<String,String> returnMap = new HashMap<String,String>();
		returnMap = wmsSapService.getCostcenterDetail(Costcenter);
		System.out.println("---->SapBapiCostcenterDetail returnMap = " + returnMap);
		return returnMap;
	}
	
	//从SAP系统获取交货单信息（SAP交货单、STO交货单） FOR FeignClient getDeliveryGetlist
	@PostMapping("/SapBapiDeliveryGetlist")
	@ResponseBody
	public Map<String,Object> SapBapiDeliveryGetlist(@RequestParam String deliveryNO){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = wmsSapService.getDeliveryGetlist(addZeroForNum(deliveryNO,10));
		System.out.println("---->SapBapiDeliveryGetlist returnMap = " + returnMap);
		return returnMap;
	}
	
	//从SAP系统获取WBS元素信息 FOR FeignClient getSapBapiWbs
	@PostMapping("/SapBapiWbs")
	@ResponseBody
	public Map<String,Object> SapBapiWbs(@RequestParam String wbsno){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = wmsSapService.getSapBapiWbs(wbsno);
		System.out.println("---->SapBapiWbs returnMap = " + returnMap);
		return returnMap;
	}
	
	//从SAP系统获取CO订单信息  FOR FeignClient getSapBapiKaufOrder
	@PostMapping("/SapBapiKaufOrder")
	@ResponseBody
	public Map<String,Object> SapBapiKaufOrder(@RequestParam String orderId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = wmsSapService.getSapBapiKaufOrder(orderId);
		System.out.println("---->SapBapiKaufOrder returnMap = " + returnMap);
		return returnMap;
	}
	
	//从SAP系统获取内部订单信息  FOR FeignClient getSapBapiInternalorderDetail
	@PostMapping("/SapBapiInternalorderDetail")
	@ResponseBody
	public Map<String,String> SapBapiInternalorderDetail(@RequestParam String orderId){
		Map<String,String> returnMap = new HashMap<String,String>();
		returnMap = wmsSapService.getSapBapiInternalorderDetail(orderId);
		System.out.println("---->SapBapiInternalorderDetail returnMap = " + returnMap);
		return returnMap;
	}
	
	//从SAP系统获取生产订单信息  FOR FeignClient getSapBapiProdordDetail
	@PostMapping("/SapBapiProdordDetail")
	@ResponseBody
	public Map<String,Object> SapBapiProdordDetail(@RequestParam String orderId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = wmsSapService.getSapBapiProdordDetail(orderId);
		System.out.println("---->SapBapiProdordDetail returnMap = " + returnMap);
		return returnMap;
	}
	
	//手动同步 物料信息  FOR FeignClient
	@PostMapping("/SapBapiMaterialInfoSync")
	@ResponseBody
	public Map<String,Object> SapBapiMaterialInfoSync(@RequestParam Map<String,Object> paramMap){	
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String msg = "";
		String plant = paramMap.get("PLANT").toString();
		String[] materialList = paramMap.get("MATERIAL").toString().split(",");
		
		for(int i=0;i<materialList.length;i++) {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			SapMaterialEntity sapMaterial = new SapMaterialEntity();
			
			resultMap = wmsSapService.getSapBapiMaterialInfo(plant, materialList[i]);
			if(resultMap.get("CODE")=="0") {
				if(resultMap.get("MATERIAL") != null)sapMaterial.setMATNR(resultMap.get("MATERIAL").toString());
				if(resultMap.get("MATL_DESC_ZH") != null)sapMaterial.setMAKTX(resultMap.get("MATL_DESC_ZH").toString());
				if(resultMap.get("MATL_DESC_EN") != null)sapMaterial.setMAKTXEN(resultMap.get("MATL_DESC_EN").toString());
				if(resultMap.get("BASE_UOM") != null)sapMaterial.setMEINS(resultMap.get("BASE_UOM").toString());
				if(resultMap.get("PLANT") != null)sapMaterial.setWERKS(resultMap.get("PLANT").toString());
				if(resultMap.get("PO_UNIT") != null)sapMaterial.setBSTME(resultMap.get("PO_UNIT").toString());
				if(resultMap.get("ISSUE_UNIT") != null)sapMaterial.setAUSME(resultMap.get("ISSUE_UNIT").toString());
				if(resultMap.get("PUR_STATUS") != null)sapMaterial.setMMSTA(resultMap.get("PUR_STATUS").toString());
				if(resultMap.get("DEL_FLAG") != null)sapMaterial.setLVORM(resultMap.get("DEL_FLAG").toString());
				if(resultMap.get("PROC_TYPE") != null)sapMaterial.setBESKZ(resultMap.get("PROC_TYPE").toString());
				if(resultMap.get("SPPROCTYPE") != null)sapMaterial.setSOBSL(resultMap.get("SPPROCTYPE").toString());
				if(resultMap.get("PRICE_CTRL") != null)sapMaterial.setVPRSV(resultMap.get("PRICE_CTRL").toString());
				if(resultMap.get("MOVING_PR") != null)sapMaterial.setVERPR(resultMap.get("MOVING_PR").toString());
				if(resultMap.get("STD_PRICE") != null)sapMaterial.setSTPRS(resultMap.get("STD_PRICE").toString());
				if(resultMap.get("PRICE_UNIT") != null)sapMaterial.setPEINH(resultMap.get("PRICE_UNIT").toString());
				if(resultMap.get("BATCH_MGMT") != null)sapMaterial.setXCHPF(resultMap.get("BATCH_MGMT").toString());
				//同步过来的质检周期需要乘以2
				if(resultMap.get("PRFRQ") != null ){
					if(!"".equals(resultMap.get("PRFRQ").toString())){
						String prfrq = resultMap.get("PRFRQ").toString();
						BigDecimal prfrq2 = BigDecimal.valueOf(Long.parseLong(prfrq)).multiply(BigDecimal.valueOf(2));
                        sapMaterial.setPRFRQ(prfrq2.toString());
					}

				}
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dd = new Date();
				String IMPORTDATE = df.format(dd);
				sapMaterial.setIMPORTDATE(IMPORTDATE);
				sapMaterial.setIMPORTOR("WMS");
				
				List<Map<String,Object>> unitList = (List<Map<String,Object>>)resultMap.get("unitList");
				List<SapMaterialUnitEntity> unitEntityList = new ArrayList<SapMaterialUnitEntity>();
				for (Map<String, Object> map : unitList) {
					SapMaterialUnitEntity sapMaterialUnitEntity = new SapMaterialUnitEntity();
					sapMaterialUnitEntity.setMATNR(sapMaterial.getMATNR());
					if(map.get("ALT_UNIT") != null)sapMaterialUnitEntity.setMEINH(map.get("ALT_UNIT").toString());
					if(map.get("NUMERATOR") != null)sapMaterialUnitEntity.setUMREZ(map.get("NUMERATOR").toString());
					if(map.get("DENOMINATR") != null)sapMaterialUnitEntity.setUMREN(map.get("DENOMINATR").toString());
					sapMaterialUnitEntity.setIMPORTDATE(IMPORTDATE);
					unitEntityList.add(sapMaterialUnitEntity);
				}
	
				//同步SapMaterialEntity到本地数据库
				wmsSapService.syncSapMaterialData(sapMaterial);
				wmsSapService.syncSapMaterialUnitData(unitEntityList);
				msg += materialList[i] + "同步成功；<br/>";
			}else {
				msg += materialList[i] + "同步失败；"+resultMap.get("MESSAGE")+"<br/>";
			}			
		}
		
		returnMap.put("CODE", "0");
        returnMap.put("MESSAGE", msg);
		return returnMap;
	}

	//手动同步 供应商信息
	@PostMapping("/SapBapiVendorInfoSync")
	@ResponseBody
	public Map<String,Object> SapBapiVendorInfoSync(@RequestParam Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String msg = "";
		String[] vendorList = paramMap.get("VENDOR").toString().split(",");

		for(int i=0;i<vendorList.length;i++) {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			String vendor = vendorList[i];
			resultMap = wmsSapService.getSapBapiVendorInfo(vendor);
			if(resultMap.get("CODE")=="0") {

				msg += vendorList[i] + "同步成功；<br/>";

			}else {
				msg += vendorList[i] + "同步失败；"+resultMap.get("MESSAGE")+"<br/>";
			}
		}

		returnMap.put("CODE", "0");
		returnMap.put("MESSAGE", msg);
		return returnMap;
	}

	//手动同步 生产订单  FOR FeignClient
	@PostMapping("/SapBapiProdordDetailSync")
	@ResponseBody
	public Map<String,Object> SapBapiProdordDetailSync(@RequestParam Map<String,Object> paramMap){		
		String[] orderNoList = paramMap.get("orderNo").toString().split(",");
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String msg = "";
		for(int i=0;i<orderNoList.length;i++) {
			System.out.println("---->" + i + " : " + addZeroForNum(orderNoList[i],12));
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap = wmsSapService.getSapBapiProdordDetail(addZeroForNum(orderNoList[i],12));
			resultMap.put("WMS_USER", paramMap.get("WMS_USER"));
			if(resultMap.get("CODE")=="0") {
				wmsSapService.syncSapMoData(resultMap);
				msg += orderNoList[i] + "同步成功；<br/>";
			}else {
				msg += orderNoList[i] + "同步失败；<br/>";
			}
		}
		returnMap.put("CODE", "0");
        returnMap.put("MESSAGE", msg);
		return returnMap;
	}
	
	//手动同步 采购订单  FOR FeignClient
	@PostMapping("/SapBapiPoDetailSync")
	@ResponseBody
	public Map<String,Object> SapBapiPoDetailSync(@RequestParam Map<String,Object> paramMap){
		String[] orderNoList = paramMap.get("orderNo").toString().split(",");
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String msg = "";
		for(int i=0;i<orderNoList.length;i++) {
			boolean result = wmsSapService.getSapBapiPo(orderNoList[i]);
			if(result) {
				msg += orderNoList[i] + "同步成功；<br/>";
			}else {
				msg += orderNoList[i] + "同步失败；<br/>";
			}
		}
		returnMap.put("CODE", "0");
        returnMap.put("MESSAGE", msg);
		return returnMap;		
	}
	
	//货物移动过账   FOR FeignClient sapGoodsmvtCreate
	@PostMapping("/SapBapiGoodsmvtCreate")
	@ResponseBody
	public Map<String,Object> sapGoodsmvtCreate(@RequestBody Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			returnMap = wmsSapRealtimeService.sapGoodsmvtCommonCreate(paramMap);
			log.info("---->SapBapiGoodsmvtCreate returnMap = " + returnMap);
//			System.out.println("---->SapBapiGoodsmvtCreate returnMap = " + returnMap);
		} catch (Exception e) {
			returnMap.put("CODE", "-1");
	        returnMap.put("MESSAGE", "系统异常："+e.getMessage());
		}
		return returnMap;
	}
	
	
	//冲销货物移动凭证  FOR FeignClient sapGoodsmvtCancel MBST冲销整个凭证
	@PostMapping("/SapBapiGoodsmvtCancel")
	@ResponseBody
	public Map<String,Object> sapGoodsmvtCancel(@RequestBody Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			returnMap = wmsSapRealtimeService.sapGoodsmvtCancel(paramMap);
			log.info("---->SapBapiGoodsmvtCancel returnMap = " + returnMap);
//			System.out.println("---->SapBapiGoodsmvtCancel returnMap = " + returnMap);
		} catch (Exception e) {
			returnMap.put("CODE", "-1");
	        returnMap.put("MESSAGE", "系统异常："+e.getMessage());
		}
		return returnMap;
	}
	
	//SAP交货单修改、简配  FOR FeignClient sapDeliveryChange
	@PostMapping("/SapDeliveryChange")
	@ResponseBody
	public Map<String,Object> sapDeliveryChange(@RequestBody Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = wmsSapRealtimeService.sapDeliveryChange(paramMap);
		System.out.println("---->SapDeliveryChange returnMap = " + returnMap);
		return returnMap;
	}
	
	
	//SAP交货单过账  FOR FeignClient sapDeliveryUpdate
	@PostMapping("/SapDeliveryUpdate")
	@ResponseBody
	public Map<String,Object> sapDeliveryUpdate(@RequestBody Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = wmsSapRealtimeService.sapDeliveryUpdate(paramMap);
		System.out.println("---->SapDeliveryUpdate returnMap = " + returnMap);
		return returnMap;
	}
	
	
	//同步任务 
	@PostMapping("/SapMaterialSync")
	@ResponseBody
	public Map<String,Object> SapMaterialSync(@RequestParam String params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		System.out.println("---->SapController SapMaterialSync Start 参数为：" + params);
		int days = 1;
		int recordNum = 100000;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
			if(plist[i].indexOf("recordNum")>=0) {
				recordNum = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date dd = new Date();
		String DATE_TO = df.format(dd);
		String DATE_FROM = df.format(dd.getTime() - days * 24 * 60 * 60 * 1000);
		
		String prepareFunName = "ZBTS_ASYN_MATERIAL_PREPARE";
		String readFunName = "ZBTS_MATERIAL_READ";
		String deleteFunName = "ZBTS_MATERIAL_DELETE";
		String paraTableName = "T_BUKRS";
		//Map<String,String> prepareDataMap = new HashMap<String,String>();
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		Map<String,String> companyMapParam = new HashMap<String,String>(); 
		companyMapParam.put("SYNC_MAT", "X");
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList(companyMapParam);
		for(int i=0;i<bukrs.size();i++) {
			if(bukrs.get(i).get("BUKRS") !=null && !"".equals(bukrs.get(i).get("BUKRS"))) {
				Map<String,String> readParam = new HashMap<String,String>();
				readParam.put("BUKRS", bukrs.get(i).get("BUKRS"));
				prepareDataList.add(readParam);	
			}
		}
		
		if (prepareDataList.size() < 1) {
		    throw new RuntimeException("没有要同步的公司代码");
		}
		//prepareDataMap.put("BUKRS_0", "C160");
		prepareInputMap.put("DATE_FROM", DATE_FROM);
		prepareInputMap.put("DATE_TO", DATE_TO);
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		prepareInputMap.put("REQUEST", "YYYNYNYNNN");
		readParamMap.put("RECORD_NUM", String.valueOf(recordNum));
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_ZMMARC");
		outParamList.add("T_ZMMARA");
		outParamList.add("T_ZMMAKT");
		outParamList.add("T_ZMMBEW");
		outParamList.add("T_ZMMARM");
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);checkList.add(2);checkList.add(3);checkList.add(5);checkList.add(7);	
		
		List<JCoTable> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
		
		if(outputList.size()==5) {
			//循环主表 ZMMARC 创建SapMaterialEntity
			int v_count = outputList.get(0).getNumRows();
			for(int i=0;i<v_count;i++) {
				SapMaterialEntity sapMaterial = new SapMaterialEntity();
				
				outputList.get(0).setRow(i);
				String matnr = outputList.get(0).getString("MATNR");
				String weaks = outputList.get(0).getString("WERKS");
				sapMaterial.setMATNR(outputList.get(0).getString("MATNR"));
				sapMaterial.setWERKS(outputList.get(0).getString("WERKS"));
				sapMaterial.setAUSME(outputList.get(0).getString("AUSME"));
				sapMaterial.setMMSTA(outputList.get(0).getString("MMSTA"));
				sapMaterial.setLVORM(outputList.get(0).getString("LVORM"));
				sapMaterial.setBESKZ(outputList.get(0).getString("BESKZ"));
				sapMaterial.setSOBSL(outputList.get(0).getString("SOBSL"));
				sapMaterial.setXCHPF(outputList.get(0).getString("XCHPF"));
				sapMaterial.setPRFRQ(outputList.get(0).getString("PRFRQ"));
				//循环另外4张表获取SapMaterialEntity数据
				//01 [ZMMARA]
				int m = outputList.get(1).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(1).setRow(n);
					if(matnr.equals(outputList.get(1).getString("MATNR"))) {
						sapMaterial.setMATLVORM(outputList.get(1).getString("LVORM"));
						sapMaterial.setBSTME(outputList.get(1).getString("BSTME"));
						sapMaterial.setMEINS(outputList.get(1).getString("MEINS"));
					}
				}
				
				//02 [ZMMAKT]
				m = outputList.get(2).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(2).setRow(n);
					if(matnr.equals(outputList.get(2).getString("MATNR"))) {
						if("1".equals(outputList.get(2).getString(2))){
							sapMaterial.setMAKTX(outputList.get(2).getString("MAKTX"));
						}else if("E".equals(outputList.get(2).getString(2))){
							sapMaterial.setMAKTXEN(outputList.get(2).getString("MAKTX"));
						}
					}
				}
				
				//03 [ZMMBEW]
				m = outputList.get(3).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(3).setRow(n);
					if(matnr.equals(outputList.get(3).getString("MATNR")) && weaks.equals(outputList.get(3).getString(2))) {
						sapMaterial.setVPRSV(outputList.get(3).getString("VPRSV"));
						sapMaterial.setVERPR(outputList.get(3).getString("VERPR"));
						sapMaterial.setSTPRS(outputList.get(3).getString("STPRS"));
						sapMaterial.setPEINH(outputList.get(3).getString("PEINH"));
					}
				}
				
				Date dNow = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
				String IMPORT_DATE = sdf.format(dNow);
				
				//04 [ZMMARM]
				m = outputList.get(4).getNumRows();
				List<SapMaterialUnitEntity> unitList = new ArrayList<SapMaterialUnitEntity>();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(4).setRow(n);
					if(matnr.equals(outputList.get(4).getString("MATNR")) && !outputList.get(4).getString("MEINH").equals(outputList.get(1).getString("MEINS"))) {
						SapMaterialUnitEntity sapMaterialUnitEntity = new SapMaterialUnitEntity();
						sapMaterialUnitEntity.setMATNR(outputList.get(4).getString("MATNR"));
						sapMaterialUnitEntity.setMEINH(outputList.get(4).getString("MEINH"));
						sapMaterialUnitEntity.setUMREZ(outputList.get(4).getString("UMREZ"));
						sapMaterialUnitEntity.setUMREN(outputList.get(4).getString("UMREN"));
						sapMaterialUnitEntity.setIMPORTDATE(IMPORT_DATE);
						
						unitList.add(sapMaterialUnitEntity);
					}
				}
				
				sapMaterial.setIMPORTDATE(IMPORT_DATE);
				//同步SapMaterialEntity到本地数据库
				wmsSapService.syncSapMaterialData(sapMaterial);
				wmsSapService.syncSapMaterialUnitData(unitList);
			}
			
			System.out.println("---->sapMaterialSync execute END 同步数据数量：" + v_count);
			returnMap.put("CODE", "0");
	        returnMap.put("MESSAGE", "SUCCESS 同步数据数量:" + v_count);
		}else {
			System.out.println("---->sapMaterialSync execute 失败，开始纪录错误日志：");
			// 记录失败日志 
			SimpleDateFormat logdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String createTime = logdf.format(new Date());
			ScheduleJobLogEntity scheduleJobLog = new ScheduleJobLogEntity();
			scheduleJobLog.setBEANNAME("SapJobs");
			scheduleJobLog.setMETHODNAME("sapMaterialSync");
			scheduleJobLog.setPARAMS(params);
			scheduleJobLog.setSTATUS("1");
			scheduleJobLog.setERROR("sapMaterialSync error");
			scheduleJobLog.setCREATETIME(createTime);
			wmsSapService.insert_ScheduleJobLog(scheduleJobLog);
			returnMap.put("CODE", "-1");
	        returnMap.put("MESSAGE", "ERROR");
	        
		}
		return returnMap;
	}
	//同步任务
	@PostMapping("/SapCustomerSync")
	@ResponseBody
	public Map<String,Object> SapCustomerSync(@RequestParam String params) {
		return wmsSapService.syncSapCustomerData(params);
	}
	
	//同步任务 
	@PostMapping("/SapVendorSync")
	@ResponseBody
	public Map<String,Object> SapVendorSync(@RequestParam String params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		System.out.println("---->SapController SapVendorSync Start ");
		
		String prepareFunName = "ZVMI_PREPARE_U";	//ZVMI_PREPARE
		String readFunName = "ZVMI_008_READ";
		String deleteFunName = "ZVMI_008_DELETE";
		String paraTableName = "ZVMI008_EKORG";
		//Map<String,String> prepareDataMap = new HashMap<String,String>();
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		Map<String,String> companyMapParam = new HashMap<String,String>(); 
		companyMapParam.put("SYNC_VENDOR", "X");
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList(companyMapParam);
		for(int i=0;i<bukrs.size();i++) {
			String ekorg_str = bukrs.get(i).get("BUKRS")==null?"":bukrs.get(i).get("BUKRS").toString().trim();
			if(StringUtils.isEmpty(ekorg_str)) {
				Map<String,String> readParam = new HashMap<String,String>();
				readParam.put("EKORG", bukrs.get(i).get("BUKRS"));//此处设置，只能同步所属采购组织与公司代码一致的供应商信息
				prepareDataList.add(readParam);
			}else {
				//解析采购组织字符串 C160,A160
				String[] ekog_arr = ekorg_str.split(",");
				for (String string : ekog_arr) {
					if(!StringUtils.isEmpty(string)) {
						Map<String,String> readParam = new HashMap<String,String>();
						readParam.put("EKORG", string);
						prepareDataList.add(readParam);
					}
				}
			}
		}
		
		int days = 5;
		//int recordNum = 1000;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date dd = new Date();
		String DATE_TO = df.format(dd);
		String DATE_FROM = df.format(dd.getTime() - days * 24 * 60 * 60 * 1000);
		
		//prepareDataMap.put("EKORG_0", "C160");
		prepareInputMap.put("INSTANCE_ID", "00000");
		prepareInputMap.put("SIG08", "X");
		readParamMap.put("RECORD_NUM", "1000000");
		prepareInputMap.put("UDATEFR", DATE_FROM);
		prepareInputMap.put("UDATETO", DATE_TO);
		prepareInputMap.put("UTIMEFR", "000000");
		prepareInputMap.put("UTIMETO", "235959");		
		
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_VMI008");
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);
		
		
		List<JCoTable> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
		
		if(outputList.size()>0) {
			wmsSapService.syncSapVendorData(outputList.get(0));			
			int v_count = outputList.get(0).getNumRows();
			outputList.get(0).deleteAllRows();
			System.out.println("---->SapJobs sapVendorSync END 同步数据数量：" + v_count);
			returnMap.put("CODE", "0");
	        returnMap.put("MESSAGE", "SUCCESS 同步数据数量:" + v_count);
		}else {
			System.out.println("---->sapVendorSync execute 失败，开始纪录错误日志：");
			// 记录失败日志 
			SimpleDateFormat logdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String createTime = logdf.format(new Date());
			ScheduleJobLogEntity scheduleJobLog = new ScheduleJobLogEntity();
			scheduleJobLog.setBEANNAME("SapJobs");
			scheduleJobLog.setMETHODNAME("sapVendorSync");
			scheduleJobLog.setPARAMS("");
			scheduleJobLog.setSTATUS("1");
			scheduleJobLog.setERROR("sapVendorSync error");
			scheduleJobLog.setCREATETIME(createTime);
			wmsSapService.insert_ScheduleJobLog(scheduleJobLog);
			returnMap.put("CODE", "-1");
	        returnMap.put("MESSAGE", "ERROR");
		}
		
		return returnMap;
	}
	
	//定时同步任务  生产订单
	@PostMapping("/SapMoSync")
	@ResponseBody
	public Map<String,Object> SapMoSync(@RequestParam String params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		System.out.println("---->SapController sapMoSync Start ");
		
		StringBuffer WERKS_BF = new StringBuffer();
		
		String prepareFunName = "ZBTS_PPPO_VERIFY_PREPARE";
		String readFunName = "ZBTS_PPPO_VERIFY";
		String deleteFunName = "ZBTS_PPPO_VERIFY_DELETE";
		String paraTableName = "T_BUKRS";
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		Map<String,String> companyMapParam = new HashMap<String,String>(); 
		companyMapParam.put("SYNC_MO", "X");
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList(companyMapParam);
		for(int i=0;i<bukrs.size();i++) {
			Map<String,String> readParam = new HashMap<String,String>();
			readParam.put("BUKRS", bukrs.get(i).get("BUKRS"));
			prepareDataList.add(readParam);	
			
			WERKS_BF.append(bukrs.get(i).get("WERKS").toString());
		}
		/*prepareInputMap.put("DATE_FROM", "20180701");
		prepareInputMap.put("DATE_TO", "20180711");
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");*/
		
		int days = 1;
		int recordNum = 10000;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
			if(plist[i].indexOf("recordNum")>=0) {
				recordNum = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date dd = new Date();
		String DATE_TO = df.format(dd);
		
		Calendar rightNow = Calendar.getInstance();
		int fdays=0-days;//往前的天数
		rightNow.add(Calendar.DAY_OF_YEAR, fdays);
		String DATE_FROM = df.format(rightNow.getTime());
				
		prepareInputMap.put("DATE_FROM", DATE_FROM);
		prepareInputMap.put("DATE_TO", DATE_TO);
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");
		
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		readParamMap.put("RECORD_NUM", "2000");
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_ZMAFKO");
		outParamList.add("T_ZMAFPO");
		outParamList.add("T_ZMRESB");
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);		
		List<JCoTable> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);

		if(outputList.size()>0) {
			int v_count = outputList.get(0).getNumRows();			
			wmsSapService.syncSapMoData(outputList.get(0), outputList.get(1), outputList.get(2));
			System.out.println("---->ZBTS_PPPO_VERIFY execute ENDDD 同步数据数量：" + v_count);
			returnMap.put("CODE", "0");
	        returnMap.put("MESSAGE", "SUCCESS 同步数据数量:" + v_count);
		}else {
			System.out.println("---->ZBTS_PPPO_VERIFY execute 失败，开始纪录错误日志：");
			returnMap.put("CODE", "-1");
	        returnMap.put("MESSAGE", "ERROR");
		}
		
		return returnMap;
	}
	
	//同步任务  采购订单
	@PostMapping("/SapPOSync")
	@ResponseBody
	public Map<String,Object> SapPOSync(@RequestParam String params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		System.out.println("---->SapController SapPOSync Start ");
		
		String prepareFunName = "ZBTS_MMPO_VERIFY_PREPARE";
		String readFunName = "ZBTS_MMPO_VERIFY_READ";
		String deleteFunName = "ZBTS_MMPO_VERIFY_DELETE";
		String paraTableName = "T_BUKRS";
		
		//Map<String,String> prepareDataMap = new HashMap<String,String>();
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		//设置同步的工厂
		Map<String,String> companyMapParam = new HashMap<String,String>(); 
		companyMapParam.put("SYNC_PO", "X");
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList(companyMapParam);
		for(int i=0;i<bukrs.size();i++) {
			Map<String,String> readParam = new HashMap<String,String>();
			readParam.put("BUKRS",bukrs.get(i).get("BUKRS"));
			prepareDataList.add(readParam);			
		}
		
		int days = 1;
		int recordNum = 10000;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
			if(plist[i].indexOf("recordNum")>=0) {
				recordNum = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date dd = new Date();
		String DATE_TO = df.format(dd);
		
		Calendar rightNow = Calendar.getInstance();
		int fdays=0-days;//往前的天数
		rightNow.add(Calendar.DAY_OF_YEAR, fdays);
		String DATE_FROM = df.format(rightNow.getTime());
				
		prepareInputMap.put("DATE_FROM", DATE_FROM);
		prepareInputMap.put("DATE_TO", DATE_TO);
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		
		readParamMap.put("RECORD_NUM", String.valueOf(recordNum));
		
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_ZMEKKN");//T_PO_ITEMS_ACCOUNT
		outParamList.add("T_ZMEKKO");//T_PO
		outParamList.add("T_ZMEKPO");//T_PO_ITEMS
		outParamList.add("T_ZMRESB_MMPO");//T_PO_ITEMS_COMPONENT
		outParamList.add("T_ZMMSEG");//SCM系统读取订单交货已完成标示
		outParamList.add("T_ZMCDHDR");//采购订单行项目更改记录表
		
		List<JCoTable> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,null);
		//boolean result = true;
		if(outputList.size()==6) {
			int v_count = outputList.get(0).getNumRows();
			Date dNow = new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
			for(int i=0;i<v_count;i++) {
				//同步PoAccount 开始
				SapPoAccountEntity sapPoAccountEntity=new SapPoAccountEntity();
				outputList.get(0).setRow(i);
				sapPoAccountEntity.setAUFNR(outputList.get(0).getString("AUFNR"));//订单号 AUFNR 内部订单 生产订单号
				sapPoAccountEntity.setEBELP(outputList.get(0).getString("EBELP"));//行项目编号 EBELP
				sapPoAccountEntity.setEBELN(outputList.get(0).getString("EBELN"));//采购订单号 EBELN
				
				String IMPORT_DATE = sdf.format(dNow);
				sapPoAccountEntity.setIMPORT_DATE(IMPORT_DATE);
				
				sapPoAccountEntity.setKOSTL(outputList.get(0).getString("KOSTL"));//成本中心 KOSTL
				sapPoAccountEntity.setMENGE(outputList.get(0).getString("MENGE"));//数量 MENGE
				sapPoAccountEntity.setSAKTO(outputList.get(0).getString("SAKTO"));//总账科目编号 SAKTO
				sapPoAccountEntity.setZEKKN(outputList.get(0).getString("ZEKKN"));
				
				wmsSapService.syncPoAccount(sapPoAccountEntity);
				//同步PoAccount 结束
			}
			
			for(int i=0;i<outputList.get(1).getNumRows();i++) {
				//同步Pohead 开始
				outputList.get(1).setRow(i);
				SapPoHeadEntity sapPoHeadEntity=new SapPoHeadEntity();
				sapPoHeadEntity.setEBELN(outputList.get(1).getString("EBELN"));
				sapPoHeadEntity.setBSART(outputList.get(1).getString("BSART"));
				sapPoHeadEntity.setBUKRS(outputList.get(1).getString("BUKRS"));
				sapPoHeadEntity.setEKORG(outputList.get(1).getString("EKORG"));
				sapPoHeadEntity.setEKGRP(outputList.get(1).getString("EKGRP"));
				sapPoHeadEntity.setLIFNR(removeZeroStr(outputList.get(1).getString("LIFNR")));
				sapPoHeadEntity.setFRGRL(outputList.get(1).getString("FRGRL"));
				sapPoHeadEntity.setBEDAT(outputList.get(1).getString("BEDAT"));
				
				Date dNow1 = new Date();
				String IMPORT_DATE1 = sdf.format(dNow1);
				sapPoHeadEntity.setIMPORT_DATE(IMPORT_DATE1);
				sapPoHeadEntity.setFRGGR(outputList.get(1).getString("FRGGR"));
				//sapPoHeadEntity.setFRGSX(outputList.get(1).getString("FRGSX"));
				wmsSapService.syncPoHead(sapPoHeadEntity);
				//同步Pohead 结束
			}
			
			for(int i=0;i<outputList.get(2).getNumRows();i++) {
				//同步Poitem 开始
				outputList.get(2).setRow(i);
				SapPoItemEntity sapPoItemEntity=new SapPoItemEntity();
				sapPoItemEntity.setEBELN(outputList.get(2).getString("EBELN"));
				sapPoItemEntity.setEBELP(outputList.get(2).getString("EBELP"));
				sapPoItemEntity.setLOEKZ(outputList.get(2).getString("LOEKZ"));
				sapPoItemEntity.setMATNR(outputList.get(2).getString("MATNR"));
				sapPoItemEntity.setTXZ01(outputList.get(2).getString("TXZ01"));
				sapPoItemEntity.setWERKS(outputList.get(2).getString("WERKS"));
				sapPoItemEntity.setLGORT(outputList.get(2).getString("LGORT"));
				sapPoItemEntity.setBEDNR(outputList.get(2).getString("BEDNR"));
				
				String menge_str=outputList.get(2).getString("MENGE");//数量 MENGE
				if(!"".equals(menge_str)&&menge_str!=null){
				sapPoItemEntity.setMENGE(new BigDecimal(menge_str));
				}
				
				sapPoItemEntity.setMEINS(outputList.get(2).getString("MEINS"));
				sapPoItemEntity.setPSTYP(outputList.get(2).getString("PSTYP"));
				sapPoItemEntity.setKNTTP(outputList.get(2).getString("KNTTP"));
				sapPoItemEntity.setELIKZ(outputList.get(2).getString("ELIKZ"));
				sapPoItemEntity.setUNTTO(outputList.get(2).getString("UNTTO"));
				sapPoItemEntity.setUEBTO(outputList.get(2).getString("UEBTO"));
				sapPoItemEntity.setRETPO(outputList.get(2).getString("RETPO"));
				sapPoItemEntity.setAFNAM(outputList.get(2).getString("AFNAM"));
				sapPoItemEntity.setMFRPN(outputList.get(2).getString("MFRPN"));
				
				Date dNow2 = new Date();
				String IMPORT_DATE2 = sdf.format(dNow2);
				sapPoItemEntity.setIMPORT_DATE(IMPORT_DATE2);
				
				if("K".equals(outputList.get(2).getString("PSTYP")) || "2".equals(outputList.get(2).getString("PSTYP"))){
					sapPoItemEntity.setSOBKZ("K");//寄售，特殊库存类型设置为K
				}else {
					sapPoItemEntity.setSOBKZ("Z");
				}
				//过量交货限制 UEBTO % 不为空，且 数量 MENGE 不为空
				if(!("".equals(outputList.get(2).getString("UEBTO"))||outputList.get(2).getString("UEBTO")==null)){
					if(!"".equals(menge_str)&&menge_str!=null){
						BigDecimal uebto_big=new BigDecimal(outputList.get(2).getString("UEBTO").replaceAll("%", ""));
						BigDecimal menge_big=new BigDecimal(menge_str);
						sapPoItemEntity.setMAX_MENGE((uebto_big.add(new BigDecimal(100))).divide(new BigDecimal(100)).multiply(menge_big).toString());
					}
				}else{//否则就取 数量 MENGE
					sapPoItemEntity.setMAX_MENGE(menge_str);
				}
				
				wmsSapService.syncPoItem(sapPoItemEntity);
				//同步Poitem 结束
			}
			
			for(int i=0;i<outputList.get(3).getNumRows();i++) {
				outputList.get(3).setRow(i);
				Map<String,Object> queryMap = new HashMap<String,Object>();
				Date dNow1 = new Date();
				String IMPORT_DATE = sdf.format(dNow1);
				queryMap.put("EBELN", outputList.get(3).getString("EBELN"));
				queryMap.put("EBELP", outputList.get(3).getString("EBELP"));
				queryMap.put("ETENR", outputList.get(3).getString("ETENR"));
				queryMap.put("RSPOS", outputList.get(3).getString("RSPOS"));
				queryMap.put("MATN1", outputList.get(3).getString("MATN1"));
				queryMap.put("MENG1", outputList.get(3).getString("MENG1"));
				queryMap.put("MEIN1", outputList.get(3).getString("MEIN1"));
				queryMap.put("MATN2", outputList.get(3).getString("MATN2"));
				queryMap.put("MENG2", outputList.get(3).getString("MENG2"));
				queryMap.put("MEIN2", outputList.get(3).getString("MEIN2"));
				queryMap.put("WERKS", outputList.get(3).getString("WERKS"));
				queryMap.put("IMPORT_DATE", IMPORT_DATE);
				queryMap.put("UPDATE_DATE", IMPORT_DATE);
				
				wmsSapService.syncPoComponent(queryMap);
			}	
			System.out.println("---->SapJobs sapVendorSync END 同步数据数量：" + v_count);
			returnMap.put("CODE", "0");
	        returnMap.put("MESSAGE", "SUCCESS 同步数据数量");
		}else {
			System.out.println("---->sapPOSync execute 失败，开始纪录错误日志：");
			// 记录失败日志 
			SimpleDateFormat logdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String createTime = logdf.format(new Date());
			ScheduleJobLogEntity scheduleJobLog = new ScheduleJobLogEntity();
			scheduleJobLog.setBEANNAME("SapJobs");
			scheduleJobLog.setMETHODNAME("sapPOSync");
			scheduleJobLog.setPARAMS("");
			scheduleJobLog.setSTATUS("1");
			scheduleJobLog.setERROR("sapPOSync error");
			scheduleJobLog.setCREATETIME(createTime);
			wmsSapService.insert_ScheduleJobLog(scheduleJobLog);
			returnMap.put("CODE", "-1");
	        returnMap.put("MESSAGE", "ERROR");
		}		
		return returnMap;
	}
	
	
	@GetMapping("/SyncVenDorJob")
	public R SyncVenDorJob() {
		/*************START 货物移动过账**************************/
		/*Map<String,Object> returnMap = new HashMap<String,Object>();		
		Map<String, Object> paramMap = new HashMap<String,Object>();	
		
		paramMap.put("WERKS", "C113");
		
		paramMap.put("PSTNG_DATE", "20180723");				//凭证中的过帐日期
		paramMap.put("DOC_DATE", "20180723");				//凭证中的凭证日期
		paramMap.put("HEADER_TXT", "外部销售发货(251)测试抬头文本");	//凭证抬头文本
		
		List<Map<String,String>> itemList =  new ArrayList<Map<String,String>>();
		Map<String,String> item = new HashMap<String,String>();		
		item.put("MATERIAL", "10028653-00");				//GOODSMVT_ITEM
		item.put("PLANT", "C190");
		item.put("STGE_LOC", "0030");
		item.put("GR_RCPT", "TEST");
		//item.put("BATCH", "");
		//item.put("MOVE_TYPE", "101");
		item.put("SPEC_STOCK", "");
		item.put("ENTRY_QNT", "1");
		item.put("ENTRY_UOM", "PCS");
		//item.put("ORDERID", "120000068676");
		item.put("CUSTOMER", "24481");
		//item.put("PO_NUMBER", "6900016223");
		//item.put("PO_ITEM", "10");
		item.put("ITEM_TEXT", "行项目文本");
		item.put("MVT_IND", "");
		itemList.add(item);
		
		paramMap.put("ITEMLIST", itemList);
		
		returnMap = wmsSapRealtimeService.sapGoodsmvtCreateMB03_251(paramMap);
		System.out.println("---->returnMap = " + returnMap);*/
		/*************END   货物移动过账**************************/
		
		//return (true)?R.ok():R.error();
		
		/*************START 从SAP系统获取物料凭证信息********************/
		Map<String,Object> returnMap = new HashMap<String,Object>();	
		returnMap = wmsSapService.getSapBapiGoodsmvtDetail("5027821597","2018");
		System.out.println("---->returnMap = " + returnMap);
		
		/*************END   从SAP系统获取物料凭证信息********************/
		
		/*************START 从SAP系统获取客户数据信息********************/
		/*Map<String,String> paramMap = new HashMap<String,String>();
		Map<String,Object> returnMap = new HashMap<String,Object>();	
		//ZGSD_CRM_001_01
		returnMap = wmsSapService.getSapBapiCrmInfo(50);
		System.out.println("---->returnMap = " + returnMap);*/
		/*************END   从SAP系统获取客户数据信息********************/
		
		/*************START 从SAP系统获取库存信息********************/
		/*String prepareFunName = "ZVMI_PREPARE";
		String readFunName = "ZVMI_004_READ";
		String deleteFunName = "ZVMI_004_DELETE";
		String paraTableName = "WERKS";
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList();
		for(int i=0;i<bukrs.size();i++) {
			Map<String,String> readParam = new HashMap<String,String>();
			readParam.put("WERKS", bukrs.get(i).get("BUKRS"));
			prepareDataList.add(readParam);			
		}

		prepareInputMap.put("SIG04", "X");
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		//readParamMap.put("RECORD_NUM", "2000");
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_VMI004");
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);		
		List<Table> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
		boolean result = true;
		if(outputList.size()>0) {
			int v_count = outputList.get(0).getNumRows();
			
			//wmsSapService.syncSapMoData(outputList.get(0), outputList.get(1), outputList.get(2));
			
			outputList.get(0).deleteAllRows();
			System.out.println("---->ZBTS_PPPO_VERIFY execute ENDDD 同步数据数量：" + v_count);
		}else {
			result = false;
		}*/
		/*************END   从SAP系统获取库存信息 ********************/
		
		
		/*Map<String,String> paramMap = new HashMap<String,String>();
		Map<String,Object> returnMap = new HashMap<String,Object>();		
		returnMap = wmsSapService.getSapBapiKaufOrder("001500001300");
		System.out.println("---->returnMap = " + returnMap);*/
		
		
		/*String prepareFunName = "ZBTS_PPPO_VERIFY_PREPARE";
		String readFunName = "ZBTS_PPPO_VERIFY";
		String deleteFunName = "ZBTS_PPPO_VERIFY_DELETE";
		String paraTableName = "T_BUKRS";
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList();
		for(int i=0;i<bukrs.size();i++) {
			Map<String,String> readParam = new HashMap<String,String>();
			readParam.put(bukrs.get(i).get("BUKRS"),"BUKRS");
			prepareDataList.add(readParam);			
		}
		prepareInputMap.put("DATE_FROM", "20180701");
		prepareInputMap.put("DATE_TO", "20180711");
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		readParamMap.put("RECORD_NUM", "2000");
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_ZMAFKO");
		outParamList.add("T_ZMAFPO");
		outParamList.add("T_ZMRESB");
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);		
		List<JCoTable> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
		boolean result = true;
		if(outputList.size()>0) {
			int v_count = outputList.get(0).getNumRows();
			
			wmsSapService.syncSapMoData(outputList.get(0), outputList.get(1), outputList.get(2));
			
			outputList.get(0).deleteAllRows();
			System.out.println("---->ZBTS_PPPO_VERIFY execute ENDDD 同步数据数量：" + v_count);
		}else {
			result = false;
		}*/
		return null;
		
		/*Map<String,String> paramMap = new HashMap<String,String>();
		Map<String,Object> returnMap = new HashMap<String,Object>();		
		returnMap = wmsSapService.getSapBapiProdordDetail("003100007316");
		System.out.println("---->returnMap = " + returnMap);
		paramMap = (Map<String, String>) returnMap.get("headerMap");
		System.out.println("-->CO_AUFLD = " + paramMap.get("CO_AUFLD"));
		Map<String, String> moHeadMap = new HashMap<String,String>();
		moHeadMap.put("AUART", paramMap.get("AUART"));*/
		
		/*Map<String,String> paramMap = new HashMap<String,String>();
		Map<String,String> returnMap = new HashMap<String,String>();
		paramMap.put("ORDERID", "120000017762");//paramMap.put("POSEX_E", "000010");
		
		returnMap = wmsSapService.getSapBapiInternalorderDetail("120000017762");
		System.out.println("---->returnMap = " + returnMap);*/
		
		
		//JCoParameterList returnDetail = wmsSapService.getSapBapiExportParameter("BAPI_INTERNALORDER_GETDETAIL", paramMap,"ALLOWED_BUS_TRACT");
		//ALLOWED_BUS_TRACT|SYSTEM_STATUS|USER_STATUS

		//System.out.println("---->RETURN MESSAGE  = " + returnDetail.getStructure("RETURN").getString("MESSAGE"));
		//System.out.println("---->RETURN CODE  = " + returnDetail.getStructure("RETURN").getString("CODE"));
		//System.out.println("---->RETURN TYPE  = " + returnDetail.getStructure("RETURN").getString("TYPE"));
		
		
		//System.out.println("---->outputParam = " + returnDetail);	
		//System.out.println("---->outputParam WBS_ELEM = " + returnDetail.getStructure("MASTER_DATA").getString("ORDER_NAME"));	
		//boolean result = wmsSapService.sapVendorSync();
		
		
		//----TEST----
		/*Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("IHREZ_E", "D2017095");paramMap.put("POSEX_E", "000010");
		
		JCoTable table = wmsSapService.getSapBapiData("ZMMWK_D19_001", paramMap,"IT_ZSDD143");
		System.out.println("---->outputParam = " + table.toString());*/
		
		//boolean result = wmsSapService.sapVendorSync();
		
				
		/*
		String prepareFunName = "ZBTS_ASYN_MATERIAL_PREPARE";
		String readFunName = "ZBTS_MATERIAL_READ";
		String deleteFunName = "ZBTS_MATERIAL_DELETE";
		String paraTableName = "T_BUKRS";
		Map<String,String> prepareDataMap = new HashMap<String,String>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList();
		for(int i=0;i<bukrs.size();i++) {
			prepareDataMap.put("BUKRS_" + i, bukrs.get(i).get("BUKRS"));
		}
		//prepareDataMap.put("BUKRS_0", "C160");
		prepareInputMap.put("DATE_FROM", "20180701");
		prepareInputMap.put("DATE_TO", "20180711");
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		prepareInputMap.put("REQUEST", "YYYNYNYNNN");
		readParamMap.put("RECORD_NUM", "200");
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_ZMMARC");
		outParamList.add("T_ZMMARA");
		outParamList.add("T_ZMMAKT");
		outParamList.add("T_ZMMBEW");
		outParamList.add("T_ZMMARM");
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);checkList.add(2);checkList.add(3);checkList.add(5);checkList.add(7);	
		
		List<Table> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataMap, prepareInputMap, readParamMap, outParamList,checkList);
		boolean result = true;
		if(outputList.size()==5) {
			//循环主表 ZMMARC 创建SapMaterialEntity
			int v_count = outputList.get(0).getNumRows();
			for(int i=0;i<v_count;i++) {
				SapMaterialEntity sapMaterial = new SapMaterialEntity();
				SapMaterialUnitEntity sapMaterialUnitEntity = new SapMaterialUnitEntity();
				outputList.get(0).setRow(i);
				String matnr = outputList.get(0).getString("MATNR");
				String weaks = outputList.get(0).getString("WERKS");
				sapMaterial.setMATNR(outputList.get(0).getString("MATNR"));
				sapMaterial.setWERKS(outputList.get(0).getString("WERKS"));
				sapMaterial.setAUSME(outputList.get(0).getString("AUSME"));
				sapMaterial.setMMSTA(outputList.get(0).getString("MMSTA"));
				sapMaterial.setLVORM(outputList.get(0).getString("LVORM"));
				sapMaterial.setBESKZ(outputList.get(0).getString("BESKZ"));
				sapMaterial.setSOBSL(outputList.get(0).getString("SOBSL"));
				
				sapMaterialUnitEntity.setMATNR(outputList.get(0).getString("MATNR"));
				
				//循环另外4张表获取SapMaterialEntity数据
				//01 [ZMMARA]
				int m = outputList.get(1).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(1).setRow(n);
					if(matnr.equals(outputList.get(1).getString("MATNR"))) {
						sapMaterial.setMATLVORM(outputList.get(1).getString("LVORM"));
						sapMaterial.setBSTME(outputList.get(1).getString("BSTME"));
						sapMaterial.setMEINS(outputList.get(1).getString("MEINS"));
					}
				}
				
				//02 [ZMMAKT]
				m = outputList.get(2).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(2).setRow(n);
					if(matnr.equals(outputList.get(2).getString("MATNR"))) {
						if("1".equals(outputList.get(2).getString(2))){
							sapMaterial.setMAKTX(outputList.get(2).getString("MAKTX"));
						}else if("E".equals(outputList.get(2).getString(2))){
							sapMaterial.setMAKTXEN(outputList.get(2).getString("MAKTX"));
						}
					}
				}
				
				//03 [ZMMBEW]
				m = outputList.get(3).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(3).setRow(n);
					if(matnr.equals(outputList.get(3).getString("MATNR")) && weaks.equals(outputList.get(3).getString(2))) {
						sapMaterial.setVPRSV(outputList.get(3).getString("VPRSV"));
						sapMaterial.setVERPR(outputList.get(3).getString("VERPR"));
						sapMaterial.setSTPRS(outputList.get(3).getString("STPRS"));
						sapMaterial.setPEINH(outputList.get(3).getString("PEINH"));
					}
				}
				
				//04 [ZMMARM]
				m = outputList.get(4).getNumRows();
				for(int n=0;n<m;n++) {		//循环 另外4张表 的每行数据
					outputList.get(4).setRow(n);
					if(matnr.equals(outputList.get(4).getString("MATNR"))) {
						sapMaterialUnitEntity.setMEINH(outputList.get(4).getString("MEINH"));
						sapMaterialUnitEntity.setUMREZ(outputList.get(4).getString("UMREZ"));
						sapMaterialUnitEntity.setUMREN(outputList.get(4).getString("UMREN"));
					}
				}
				
				Date dNow = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
				String IMPORT_DATE = sdf.format(dNow);
				sapMaterialUnitEntity.setIMPORTDATE(IMPORT_DATE);
				
				//同步SapMaterialEntity到本地数据库
				wmsSapService.syncSapMaterialData(sapMaterial);
				wmsSapService.syncSapMaterialUnitData(sapMaterialUnitEntity);
			}
			
			System.out.println("---->SapSync_VENDOR_Job execute END 同步数据数量：" + v_count);
		}else {
			result = false;
			// TODO 记录失败日志
			
			
		}*/
		
	}
	
	@GetMapping("/SyncPoJobIdoc")
	public R SyncPoJobIdoc() {
		String prepareFunName = "ZBTS_MMPO_VERIFY_PREPARE";
		String readFunName = "ZBTS_MMPO_VERIFY_READ";
		String deleteFunName = "ZBTS_MMPO_VERIFY_DELETE";
		String paraTableName = "T_BUKRS";
		
		//Map<String,String> prepareDataMap = new HashMap<String,String>();
		Map<String,String> prepareInputMap = new HashMap<String,String>();
		Map<String,String> readParamMap = new HashMap<String,String>();
		List<Map<String,String>> prepareDataList = new ArrayList<Map<String,String>>();
		//设置同步的工厂
		Map<String,String> companyMapParam = new HashMap<String,String>(); 
		companyMapParam.put("SYNC_PO", "X");
		List<Map<String,String>> bukrs = wmsSapService.getWmsSapCompanyList(companyMapParam);
		for(int i=0;i<bukrs.size();i++) {
			Map<String,String> readParam = new HashMap<String,String>();
			readParam.put(bukrs.get(i).get("BUKRS"),"BUKRS");
			prepareDataList.add(readParam);			
			//prepareDataMap.put("BUKRS_" + i, bukrs.get(i).get("BUKRS"));
		}
		
		prepareInputMap.put("DATE_FROM", "20180701");
		prepareInputMap.put("DATE_TO", "20180716");
		prepareInputMap.put("TIME_FROM", "00:00:00");
		prepareInputMap.put("TIME_TO", "23:59:59");
		SimpleDateFormat ins_sdf=new SimpleDateFormat("yyyyMMddHHmmss"); 	//设置时间格式
		String INSTANCE_ID = "WMS" + ins_sdf.format(new Date());
		prepareInputMap.put("INSTANCE_ID", INSTANCE_ID);
		
		readParamMap.put("RECORD_NUM", "200");
		
		List<String> outParamList = new ArrayList<String>();
		outParamList.add("T_ZMEKKN");//T_PO_ITEMS_ACCOUNT
		outParamList.add("T_ZMEKKO");//T_PO
		outParamList.add("T_ZMEKPO");//T_PO_ITEMS
		outParamList.add("T_ZMRESB_MMPO");//T_PO_ITEMS_COMPONENT
		outParamList.add("T_ZMMSEG");//SCM系统读取订单交货已完成标示
		outParamList.add("T_ZMCDHDR");//采购订单行项目更改记录表
		
		List<Integer> checkList = new ArrayList<Integer>();
		checkList.add(1);
		
		List<JCoTable> outputList = wmsSapService.getSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
				prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
		boolean result = true;
		if(outputList.size()==6) {
			int v_count = outputList.get(0).getNumRows();
			
			for(int i=0;i<v_count;i++) {
				//同步PoAccount 开始
				SapPoAccountEntity sapPoAccountEntity=new SapPoAccountEntity();
				outputList.get(0).setRow(i);
				//sapPoAccountEntity.setANLN1(outputList.get(0).getString("ANLN1"));
				//sapPoAccountEntity.setANLN2(outputList.get(0).getString("ANLN2"));
				sapPoAccountEntity.setAUFNR(outputList.get(0).getString("AUFNR"));//订单号 AUFNR 内部订单 生产订单号
				sapPoAccountEntity.setEBELP(outputList.get(0).getString("EBELP"));//行项目编号 EBELP
				sapPoAccountEntity.setEBELN(outputList.get(0).getString("EBELN"));//采购订单号 EBELN
				
				Date dNow = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
				String IMPORT_DATE = sdf.format(dNow);
				sapPoAccountEntity.setIMPORT_DATE(IMPORT_DATE);
				
				sapPoAccountEntity.setKOSTL(outputList.get(0).getString("KOSTL"));//成本中心 KOSTL
				//sapPoAccountEntity.setLOEKZ(outputList.get(0).getString("LOEKZ"));
				sapPoAccountEntity.setMENGE(outputList.get(0).getString("MENGE"));//数量 MENGE
				//sapPoAccountEntity.setPRCTR(outputList.get(0).getString("PRCTR"));
				//sapPoAccountEntity.setPS_PSP_PNR(outputList.get(0).getString("PS_PSP_PNR"));
				sapPoAccountEntity.setSAKTO(outputList.get(0).getString("SAKTO"));//总账科目编号 SAKTO
				sapPoAccountEntity.setZEKKN(outputList.get(0).getString("ZEKKN"));
				
				wmsSapService.syncPoAccount(sapPoAccountEntity);
				//同步PoAccount 结束
				
				//同步Pohead 开始
				outputList.get(1).setRow(i);
				SapPoHeadEntity sapPoHeadEntity=new SapPoHeadEntity();
				sapPoHeadEntity.setEBELN(outputList.get(1).getString("EBELN"));
				//sapPoHeadEntity.setBSTYP(outputList.get(1).getString("BSTYP"));
				sapPoHeadEntity.setBSART(outputList.get(1).getString("BSART"));
				sapPoHeadEntity.setBUKRS(outputList.get(1).getString("BUKRS"));
				sapPoHeadEntity.setEKORG(outputList.get(1).getString("EKORG"));
				sapPoHeadEntity.setEKGRP(outputList.get(1).getString("EKGRP"));
				sapPoHeadEntity.setLIFNR(outputList.get(1).getString("LIFNR"));
				//sapPoHeadEntity.setAEDAT(outputList.get(1).getString("AEDAT"));
				sapPoHeadEntity.setFRGRL(outputList.get(1).getString("FRGRL"));
				sapPoHeadEntity.setBEDAT(outputList.get(1).getString("BEDAT"));
				
				Date dNow1 = new Date();
				String IMPORT_DATE1 = sdf.format(dNow1);
				sapPoHeadEntity.setIMPORT_DATE(IMPORT_DATE1);
				sapPoHeadEntity.setFRGGR(outputList.get(1).getString("FRGGR"));
				//sapPoHeadEntity.setFRGSX(outputList.get(1).getString("FRGSX"));
				wmsSapService.syncPoHead(sapPoHeadEntity);
				//同步Pohead 结束
				
				//同步Poitem 开始
				outputList.get(2).setRow(i);
				SapPoItemEntity sapPoItemEntity=new SapPoItemEntity();
				sapPoItemEntity.setEBELN(outputList.get(2).getString("EBELN"));
				sapPoItemEntity.setEBELP(outputList.get(2).getString("EBELP"));
				sapPoItemEntity.setLOEKZ(outputList.get(2).getString("LOEKZ"));
				sapPoItemEntity.setMATNR(outputList.get(2).getString("MATNR"));
				sapPoItemEntity.setTXZ01(outputList.get(2).getString("TXZ01"));
				//sapPoItemEntity.setBUKRS(outputList.get(2).getString("BUKRS"));
				sapPoItemEntity.setWERKS(outputList.get(2).getString("WERKS"));
				sapPoItemEntity.setLGORT(outputList.get(2).getString("LGORT"));
				sapPoItemEntity.setBEDNR(outputList.get(2).getString("BEDNR"));
				//sapPoItemEntity.setMATKL(outputList.get(2).getString("MATKL"));
				
				String menge_str=outputList.get(2).getString("MENGE");//数量 MENGE
				if(!"".equals(menge_str)&&menge_str!=null){
				sapPoItemEntity.setMENGE(new BigDecimal(menge_str));
				}
				
				sapPoItemEntity.setMEINS(outputList.get(2).getString("MEINS"));
				//sapPoItemEntity.setLMEIN(outputList.get(2).getString("LMEIN"));
				sapPoItemEntity.setPSTYP(outputList.get(2).getString("PSTYP"));
				sapPoItemEntity.setKNTTP(outputList.get(2).getString("KNTTP"));
				//sapPoItemEntity.setLEWED(outputList.get(2).getString("LEWED"));
				sapPoItemEntity.setELIKZ(outputList.get(2).getString("ELIKZ"));
				sapPoItemEntity.setUNTTO(outputList.get(2).getString("UNTTO"));
				sapPoItemEntity.setUEBTO(outputList.get(2).getString("UEBTO"));
				sapPoItemEntity.setRETPO(outputList.get(2).getString("RETPO"));
				sapPoItemEntity.setAFNAM(outputList.get(2).getString("AFNAM"));
				sapPoItemEntity.setMFRPN(outputList.get(2).getString("MFRPN"));
				//sapPoItemEntity.setMFRNR(outputList.get(2).getString("MFRNR"));
				
				Date dNow2 = new Date();
				String IMPORT_DATE2 = sdf.format(dNow2);
				sapPoItemEntity.setIMPORT_DATE(IMPORT_DATE2);
				
				//sapPoItemEntity.setSOBKZ(outputList.get(2).getString("SOBKZ"));
				if("K".equals(outputList.get(2).getString("PSTYP")) || "2".equals(outputList.get(2).getString("PSTYP"))){
					sapPoItemEntity.setSOBKZ("K");//寄售，特殊库存类型设置为K
				}else {
					sapPoItemEntity.setSOBKZ("Z");
				}
				//过量交货限制 UEBTO % 不为空，且 数量 MENGE 不为空
				
				if(!"".equals(outputList.get(2).getString("UEBTO"))&&outputList.get(2).getString("UEBTO")!=null){
					if(!"".equals(menge_str)&&menge_str!=null){
						BigDecimal uebto_big=new BigDecimal(outputList.get(2).getString("UEBTO"));
						BigDecimal menge_big=new BigDecimal(menge_str);
						//((100+UEBTO)/100)*MENGE
						sapPoItemEntity.setMAX_MENGE((uebto_big.add(new BigDecimal(100))).divide(new BigDecimal(100)).multiply(menge_big).toString());
					}
				
				}else{//否则就取 数量 MENGE
					sapPoItemEntity.setMAX_MENGE(menge_str);
				}
				
				wmsSapService.syncPoItem(sapPoItemEntity);
				//同步Poitem 结束
			}
			
		}else {
			result = false;
		}
		
		return (result)?R.ok():R.error();
	}
	
	@GetMapping("/SyncPoJob")
	public R SyncPoJob() {
		
		wmsSapService.getSapBapiPo("7000007922");
		boolean result=true;
		return (result)?R.ok():R.error();
	}
	
	@GetMapping("/SyncDelivery")
	public R SyncDelivery() {
		
		wmsSapService.getSapBapiWbs("AU019CS13007-01-0001");
		boolean result=true;
		return (result)?R.ok():R.error();
	}
	
	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		StringBuffer sb = null;
		while (strLen < strLength) {
			sb = new StringBuffer();
			sb.append("0").append(str);
			str = sb.toString();
			strLen = str.length();
		}
		return str;
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
	
	@PostMapping("/queryMaterialStock")
	@ResponseBody
	public Map<String,Object> queryMaterialStock(@RequestBody Map<String,Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
			returnMap = wmsSapService.queryMaterialStock(params);
		} catch (Exception e) {
			returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "系统异常："+e.getMessage());
		}
		return returnMap;
	}
}
