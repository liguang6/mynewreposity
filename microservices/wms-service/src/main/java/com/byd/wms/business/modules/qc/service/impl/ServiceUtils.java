package com.byd.wms.business.modules.qc.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.config.service.WmsCQcResultService;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;

@Service
public class ServiceUtils {
	@Autowired
	WmsCDocNoService wmsCDocNoService;
	@Autowired
	WmsQcInspectionItemDao itemDAO;
	
	private static final String WMS_DOC_TYPE_RECORD = "04";
	private static final String WMS_DOC_TYPE_RESULT = "05";
	
	/**
	 * Map to entity
	 * @param params
	 * @return
	 */
	WmsQcRecordEntity genereateQcRecord(Map<String,Object> item){
		WmsQcRecordEntity record = new WmsQcRecordEntity();
		String INSPECTION_NO = (String)item.get("INSPECTION_NO");
		String INSPECTION_ITEM_NO = (String)item.get("INSPECTION_ITEM_NO");
		String WERKS = (String) item.get("WERKS");
		String WH_NUMBER = (String) item.get("WH_NUMBER");
		String BATCH = (String) item.get("BATCH");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("WERKS", WERKS);
		params.put("WMS_DOC_TYPE", WMS_DOC_TYPE_RECORD);
		String QC_RECORD_NO = (String)wmsCDocNoService.getDocNo(params).get("docno");
		String QC_RECORD_TYPE = item.get("QC_RECORD_TYPE") == null ? "02" : item.get("QC_RECORD_TYPE").toString();
		String STOCK_SOURCE = item.get("STOCK_SOURCE") == null ? "02" : item.get("STOCK_SOURCE").toString();
		String LGORT = (String) item.get("LGORT");
		String SOBKZ = item.get("SOBKZ")==null?"Z":item.get("SOBKZ").toString();
		String LIFNR = item.get("LIFNR")==null?"":item.get("LIFNR").toString();
		String MATNR = (String) item.get("MATNR");
		String MAKTX = (String) item.get("MAKTX");
		String UNIT = (String) item.get("UNIT");
		Double RECORD_QTY = Double.valueOf(String.valueOf(item.get("RECORD_QTY")));
		String QC_DATE = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		String QC_RESULT_CODE = (String) item.get("QC_RESULT_CODE");
		String QC_RESULT_TEXT = (String) item.get("QC_RESULT_TEXT");
		String QC_RESULT = (String) item.get("QC_RESULT");
		Double DESTROY_QTY = Double.valueOf(String.valueOf(item.get("DESTROY_QTY")));
		String IQC_COST_CENTER = (String) item.get("IQC_COST_CENTER");
		//质检员没有填，则取当前登录的用户
		String QC_PEOPLE = item.get("QC_PEOPLE") == null ? (String) item.get("USERNAME") : (String)item.get("QC_PEOPLE");
		String CREATOR = item.get("USERNAME").toString();
		String CREATE_DATE = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		String EDITOR = item.get("USERNAME").toString();
		String EDIT_DATE = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		
		record.setInspectionNo(INSPECTION_NO);
		record.setInspectionItemNo(INSPECTION_ITEM_NO);
		record.setWerks(WERKS);
		record.setWhNumber(WH_NUMBER);
		record.setBatch(BATCH);
		record.setQcRecordNo(QC_RECORD_NO);
		record.setQcRecordType(QC_RECORD_TYPE);
		record.setStockSource(STOCK_SOURCE);
		record.setLgort(LGORT);
		record.setSobkz(SOBKZ);
		record.setLifnr(LIFNR);
		record.setMatnr(MATNR);
		record.setMaktx(MAKTX);
		record.setUnit(UNIT);
		record.setRecordQty(RECORD_QTY);
		record.setQcDate(QC_DATE);
		record.setQcResultCode(QC_RESULT_CODE);
		record.setQcResultText(QC_RESULT_TEXT);
		record.setQcResult(QC_RESULT);
		record.setDestroyQty(DESTROY_QTY);
		record.setIqcCostCenter(IQC_COST_CENTER);
		record.setQcPeople(QC_PEOPLE);
		record.setCreator(CREATOR);
		record.setCreateDate(CREATE_DATE);
		record.setEditor(EDITOR);
		record.setEditDate(EDIT_DATE);
		return record;
	}
	
	
	WmsQcResultEntity generateQcResult(Map<String,Object> item){
		WmsQcResultEntity result = new WmsQcResultEntity();
		String INSPECTION_NO = (String)item.get("INSPECTION_NO");
		String INSPECTION_ITEM_NO = (String)item.get("INSPECTION_ITEM_NO");
		String WERKS = (String) item.get("WERKS");
		String WH_NUMBER = (String) item.get("WH_NUMBER");
		String BATCH = (String) item.get("BATCH");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("WERKS", WERKS);
		params.put("WMS_DOC_TYPE", WMS_DOC_TYPE_RESULT);
		String QC_RESULT_NO = (String)wmsCDocNoService.getDocNo(params).get("docno");
		String QC_RECORD_TYPE = item.get("QC_RECORD_TYPE") == null ? "02" : item.get("QC_RECORD_TYPE").toString();
		String STOCK_SOURCE = item.get("STOCK_SOURCE") == null ? "02" : item.get("STOCK_SOURCE").toString();
		String LGORT = (String) item.get("LGORT");
		String SOBKZ = item.get("SOBKZ")==null?"Z":item.get("SOBKZ").toString();
		String LIFNR = item.get("LIFNR")==null?"":item.get("LIFNR").toString();
		String MATNR = (String) item.get("MATNR");
		String MAKTX = (String) item.get("MAKTX");
		String UNIT = (String) item.get("UNIT");
		Double RESULT_QTY = Double.valueOf(String.valueOf(item.get("RESULT_QTY")));
		String QC_DATE = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		String QC_RESULT_CODE = (String) item.get("QC_RESULT_CODE");
		String QC_RESULT_TEXT = (String) item.get("QC_RESULT_TEXT");
		String QC_RESULT = (String) item.get("QC_RESULT");
		Double DESTROY_QTY = Double.valueOf(String.valueOf(item.get("DESTROY_QTY")));
		String IQC_COST_CENTER = (String) item.get("IQC_COST_CENTER");
		String QC_PEOPLE = item.get("QC_PEOPLE") == null ? (String) item.get("USERNAME") : (String)item.get("QC_PEOPLE");
		String CREATOR = item.get("USERNAME").toString();
		String CREATE_DATE = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		String EDITOR = item.get("USERNAME").toString();
		String EDIT_DATE = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		
		result.setInspectionNo(INSPECTION_NO);
		result.setInspectionItemNo(INSPECTION_ITEM_NO);
		result.setWerks(WERKS);
		result.setWhNumber(WH_NUMBER);
		result.setBatch(BATCH);
		result.setQcResultNo(QC_RESULT_NO);
		result.setQcRecordType(QC_RECORD_TYPE);
		result.setStockSource(STOCK_SOURCE);
		result.setLgort(LGORT);
		result.setSobkz(SOBKZ);
		result.setLifnr(LIFNR);
		result.setMatnr(MATNR);
		result.setMaktx(MAKTX);
		result.setUnit(UNIT);
		result.setResultQty(RESULT_QTY);
		result.setQcDate(QC_DATE);
		result.setQcResultCode(QC_RESULT_CODE);
		result.setQcResultText(QC_RESULT_TEXT);
		result.setQcResult(QC_RESULT);
		result.setDestroyQty(DESTROY_QTY);
		result.setIqcCostCenter(IQC_COST_CENTER);
		result.setQcPeople(QC_PEOPLE);
		result.setCreator(CREATOR);
		result.setCreateDate(CREATE_DATE);
		result.setEditor(EDITOR);
		result.setEditDate(EDIT_DATE);
		return result;
	}
	
	private static List<WmsCQcResultEntity> COMMOM_C_QC_RESULTS;
	@Autowired
	private WmsCQcResultService cQcResultService ;
	/**
	 * 获取质检结果配置
	 * 1.如果存在特定于工厂的配置
	 * 2.没有取通用的配置
	 * @param qcResultCode
	 * @return
	 */
	@Cacheable
	WmsCQcResultEntity getCQcResult(String werks,String qcResultCode){
		if(COMMOM_C_QC_RESULTS == null){
			COMMOM_C_QC_RESULTS = cQcResultService.selectList(new EntityWrapper<WmsCQcResultEntity>()
					.isNull("WERKS").or().eq("WERKS", ""));
			//查询工厂代码为空或者为""的质检结果配置
		}
		WmsCQcResultEntity specialResult =  cQcResultService.selectOne(
				new EntityWrapper<WmsCQcResultEntity>()
				.eq("WERKS", werks)
				.eq("QC_RESULT_CODE", qcResultCode));
		if(specialResult != null){
			return specialResult;
		}
		for(WmsCQcResultEntity result:COMMOM_C_QC_RESULTS){
			if(result.getQcResultCode().equals(qcResultCode)){
				return result;
			}
		}
		return null;
	}

	/**
	 * 查询工厂是否开启了条码管理
	 * @param werks
	 * @param whNumber
	 * @return
	 */
	@Cacheable
	boolean isBarcodeFlag(String werks,String whNumber) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("WERKS", werks);
		map.put("WH_NUMBER", whNumber);
		if(StringUtils.isBlank(werks) || StringUtils.isBlank(whNumber)) {
			throw new IllegalArgumentException();
		}
	 	List<Map<String,Object>> ll =  itemDAO.queryWhConfig(map);
		if(ll != null && ll.size() > 0) {
			String f = (String)ll.get(0).get("BARCODE_FLAG");
			if(f != null && "X".equals(f)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取送检单行+送检单行项目 关联的标签
	 * 标签以逗号分隔
	 * @param inspectionNo
	 * @param batch
	 * @return
	 */
	String getInspectionItemLabels(String receiptNo,String receiptItemNo) {
		 List<Map<String,Object>> labels =  itemDAO.queryInspectionItemLabelsByReceipt(receiptNo, receiptItemNo);
    	 String label = "";
    	 if(labels != null && labels.size() > 0) {
    		 label = labels.get(0).get("LABELS").toString();
    	 }
    	 return label;
	}
}
