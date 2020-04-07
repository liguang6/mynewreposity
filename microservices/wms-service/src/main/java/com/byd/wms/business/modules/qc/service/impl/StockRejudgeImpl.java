package com.byd.wms.business.modules.qc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.kn.entity.WmsKnFreezeRecordEntity;
import com.byd.wms.business.modules.kn.service.WmsKnFreezeRecordService;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionHeadDao;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionHeadEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;
import com.byd.wms.business.modules.qc.enums.InspectionItemStatus;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionHeadService;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionItemService;
import com.byd.wms.business.modules.qc.service.WmsQcRecordService;
import com.byd.wms.business.modules.qc.service.WmsQcResultService;
import com.byd.wms.business.modules.qc.service.WmsQcStockRejudge;

@Service
public class StockRejudgeImpl implements WmsQcStockRejudge {

	@Autowired
	private WmsQcInspectionHeadService insHeadService;
	@Autowired
	private WmsQcInspectionItemService insItemService;
	@Autowired
	private WmsQcRecordService recordService;
	@Autowired
	private WmsQcResultService resultService;
	@Autowired
	private ServiceUtils baseQcService;
	@Autowired
	private WmsQcInspectionHeadDao dao;
	@Autowired
	WmsQcInspectionItemDao itemDAO;
	
	@Autowired
	WmsKnFreezeRecordService freezeService;
	/**
	 * 库存复检-未质检-保存
	 * 
	 * @throws IllegalAccessException
	 */
	@Override
	@Transactional
	public void saveStockRejudgeNotInspect(List<Map<String, Object>> items)
			throws IllegalAccessException {
		int itemNo = 1;
		for (Map<String, Object> item : items) {
			String INSPECTION_NO = (String) item.get("INSPECTION_NO");
			String INSPECTION_ITEM_NO = (String) item.get("INSPECTION_ITEM_NO");
			String WERKS = (String) item.get("WERKS");
			String WH_NUMBER = (String) item.get("WH_NUMBER");
			String QC_RESULT_CODE = (String) item.get("QC_RESULT_CODE");
			String LGORT = (String) item.get("LGORT");
			String BIN_CODE = (String) item.get("BIN_CODE");
			String BARCODE_FLAG = (String) item.get("BARCODE_FLAG");
			String EFFECT_DATE = (String) item.get("EFFECT_DATE");
			String EXTENDED_EFFECT_DATE = (String) item.get("EXTENDED_EFFECT_DATE");
			String MATNR = (String) item.get("MATNR");
			String BATCH = (String) item.get("BATCH");
			String LABEL_NO = (String) item.get("LABEL_NO");
			String MEMO = (String) item.get("MEMO");
			String USERNAME = (String) item.get("USERNAME");
			String LIFNR = (String) item.get("LIFNR");
			String SOBKZ = item.get("SOBKZ").toString();
			//质检数量,用户输入
			//默认为送检数量
			//单批质检的情况下，用户输入(QTY)
			Double QTY =item.get("QTY") == null ? Double.parseDouble(item.get("INSPECTION_QTY").toString()) : Double.parseDouble(String.valueOf(item.get("QTY")));
			Double STOCK_QTY = Double.valueOf(item.get("STOCK_QTY")==null?"0":item.get("STOCK_QTY").toString());
			Double FREEZE_QTY = Double.valueOf(item.get("FREEZE_QTY")==null?"0":item.get("FREEZE_QTY").toString());
			// 更新送检单明细
			WmsQcInspectionHeadEntity inspectionHead = new WmsQcInspectionHeadEntity();
			inspectionHead.setInspectionStatus("02");// 已完成
			if(StringUtils.isNotBlank(MEMO)) inspectionHead.setMemo(MEMO);//更新备注信息
			insHeadService.update(inspectionHead,new EntityWrapper<WmsQcInspectionHeadEntity>().eq("INSPECTION_NO", INSPECTION_NO));
			
			// 更新送检单状态
			WmsQcInspectionItemEntity inspectionItem = new WmsQcInspectionItemEntity();
			inspectionItem.setInspectionItemStatus(InspectionItemStatus.FINISHED.getCode());
			insItemService.update(inspectionItem,new EntityWrapper<WmsQcInspectionItemEntity>().eq("INSPECTION_NO", INSPECTION_NO).eq("INSPECTION_ITEM_NO", INSPECTION_ITEM_NO));

			// 新增质检结果
			item.put("RESULT_QTY", QTY);
			item.put("RECORD_QTY", QTY);
			if(item.get("DESTROY_QTY")==null){
				item.put("DESTROY_QTY",0);
			}
			

			//--- 查询关联的标签 ---
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("BATCH", item.get("BATCH"));
			map.put("WERKS", item.get("WERKS"));
			map.put("WH_NUMBER", item.get("WH_NUMBER"));
			List<String> labelStatusList = new ArrayList<String>();
			labelStatusList.add("08");
			labelStatusList.add("11");
			map.put("LABEL_STATUS_LIST", labelStatusList);
			List<Map<String,Object>> labels =  itemDAO.queryLabelInfo(map);
			List<String> labelNoList = labels.stream().map(m->m.get("LABEL_NO").toString()).collect(Collectors.toList());
			String labelNoListString = "";
			for(String s:labelNoList) {
				labelNoListString += s + ",";
			}
			if(labelNoListString.length() != 0)
				labelNoListString = labelNoListString.substring(0, labelNoListString.length() - 1);
			//--- END ---
			item.put("QC_RECORD_TYPE", "01");//库存复检未质检，质检类型为初判
			item.put("STOCK_SOURCE", "02");//库存复检，来源为收料房
			WmsQcResultEntity result = baseQcService.generateQcResult(item);
			result.setQcResultItemNo(String.valueOf(itemNo));
			result.setLabelNo(labelNoListString);
			resultService.insert(result);
			// 新增质检记录
			WmsQcRecordEntity record = baseQcService.genereateQcRecord(item);
			record.setQcRecordItemNo(String.valueOf(itemNo));
			record.setLabelNo(labelNoListString);
			recordService.insert(record);

			itemNo++;

			WmsCQcResultEntity configQcResult = baseQcService.getCQcResult(WERKS, QC_RESULT_CODE);
			if (configQcResult == null) {
				throw new IllegalAccessException("质检结果配置不存在");
			}
			// 根据【质检结果配置表】配置判定为不合格或退货类型的质检数量，自动触发库存冻结. 库存数量 - 质检数量 | 冻结数量 + 质检数量
			if ( (configQcResult.getReturnFlag().equals("X") || configQcResult.getWhFlag().equals("0") ) && 
					(QTY-FREEZE_QTY>0)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("WERKS", WERKS);
				params.put("WH_NUMBER", WH_NUMBER);
				params.put("LGORT", LGORT);
				params.put("BIN_CODE", BIN_CODE);
				params.put("MATNR", MATNR);//料号
				params.put("QTY", QTY-FREEZE_QTY);// 质检数量
				params.put("LIFNR", LIFNR);//供应商
				params.put("BATCH", BATCH);
				params.put("SOBKZ", SOBKZ);
				params.put("EDITOR", USERNAME);
				params.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				dao.updateStockQty(params);
				Double Q = QTY-FREEZE_QTY;
				//追加库存冻结记录表
				addKnFreezeRecord(item,Q.longValue(),"00");
			}
			
			//2019-07-05 add by thw 根据【质检结果配置表】配置判定为合格，自动触发库存解冻
			if ((configQcResult.getWhFlag().equals("X") || configQcResult.getReturnFlag().equals("0")) && 
					QTY-STOCK_QTY>0) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("WERKS", WERKS);
				params.put("WH_NUMBER", WH_NUMBER);
				params.put("LGORT", LGORT);
				params.put("BIN_CODE", BIN_CODE);
				params.put("MATNR", MATNR);//料号
				params.put("QTY", STOCK_QTY-QTY);// 质检数量
				params.put("LIFNR", LIFNR);//供应商
				params.put("BATCH", BATCH);
				params.put("SOBKZ", SOBKZ);
				params.put("EDITOR", USERNAME);
				params.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				dao.updateStockQty(params);
				
				Double Q = QTY-STOCK_QTY;
				//追加库存解冻记录表
				addKnFreezeRecord(item,Q.longValue(),"01");
			}
			

			//更新标签状态
			if(StringUtils.isNotBlank(BARCODE_FLAG) && "X".equals(BARCODE_FLAG)) {
				//不是质检中状态
				if(!"01".equals(configQcResult.getQcStatus())) {					
					String labelStatus = "";
					if("X".equals(configQcResult.getWhFlag()) && "0".equals(configQcResult.getReturnFlag())) {
						//合格
						labelStatus = "08";
					}else {
						//不合格
						labelStatus = "04";
					}
					/*
					 * 如果有标签号，则只更新参数传过来的标签
					 * 否则，更新批次关联的所有标签
					 */
					if(StringUtils.isNotBlank(LABEL_NO)) {
						dao.updateWmsCoreLabelStatus(labelStatus,LABEL_NO);
					}else {
						for(Map<String,Object> label:labels) {
							dao.updateWmsCoreLabelStatus(labelStatus,(String) label.get("LABEL_NO"));
						}
					}
				}
			}
			
			//允许延长有效期
			if(StringUtils.isNotBlank(EXTENDED_EFFECT_DATE) && "X".equals(EXTENDED_EFFECT_DATE)) {
				if(StringUtils.isNotBlank(EFFECT_DATE)) {
					//更新物料批次表的，有效日期字段
					Map<String,Object> map1 = new HashMap<String,Object>();
					map1.put("WERKS", WERKS);
					map1.put("MATNR", MATNR);
					map1.put("BATCH", BATCH);
					map1.put("EFFECT_DATE", EFFECT_DATE);
					itemDAO.updateWmsCoreMatBatch(map1);
				}
			}
		}

	}

	/**
	 * 库存复检-质检中-保存 （批量 & 单批） 更新【检验记录】表和【检验结果】表数量（QTY）和检验结果（QC_RESULT_CODE）字段值，
	 * 质检记录类型（QC_RECORD_TYPE）为“02重判”。拆分的质检结果生成新的检验记录行项目。
	 */
	@Override
	public void saveStockRejudgeOnInspect(List<Map<String, Object>> items) {
		items.get(0).get("MATNR");
		items.get(0).get("LIFNR");
		// 1.同一个送检单归类
		Map<String, List<Map<String, Object>>> orderedItems = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> item : items) {
			String INSPECTION_NO = (String) item.get("INSPECTION_NO");
			if (orderedItems.get(INSPECTION_NO) == null) {
				orderedItems.put(INSPECTION_NO,
						new ArrayList<Map<String, Object>>());
			}
			orderedItems.get(INSPECTION_NO).add(item);
		}

		for (String key : orderedItems.keySet()) {
			// 计算数据库中的合格总数和不合格总数

			List<Map<String, Object>> its = orderedItems.get(key);
			List<WmsQcRecordEntity> records = new ArrayList<WmsQcRecordEntity>();
			List<WmsQcResultEntity> results = new ArrayList<WmsQcResultEntity>();

			int itemNo = 1;
			for (Map<String, Object> ispectItem : its) {
				ispectItem.put("RECORD_QTY", ispectItem.get("RESULT_QTY"));
				WmsQcRecordEntity record = baseQcService
						.genereateQcRecord(ispectItem);
				record.setQcRecordItemNo(String.valueOf(itemNo));
				WmsQcResultEntity result = baseQcService
						.generateQcResult(ispectItem);
				result.setQcResultItemNo(String.valueOf(itemNo));
				records.add(record);
				results.add(result);
				itemNo ++;
			}

			// 计算变化的合格数量 & 变化的不合格数量
			Map<String, Object> firstItem = its.get(0);
			String INSPECTION_NO = (String) firstItem.get("INSPECTION_NO");
			String INSPECTION_ITEM_NO = (String) firstItem
					.get("INSPECTION_ITEM_NO");
			Double qualifiedQty = 0.0, unQualifiedQty = 0.0;
			List<WmsQcResultEntity> existResults = resultService
					.selectList(new EntityWrapper<WmsQcResultEntity>().eq(
							"INSPECTION_NO", INSPECTION_NO).eq(
							"INSPECTION_ITEM_NO", INSPECTION_ITEM_NO));
			qualifiedQty = getQualifeidQtyMap(its) - getQualifeidQty(existResults);
			unQualifiedQty = getUnQualifeidQtyMap(its) - getUnQualifeidQty(existResults);

			// 2.新增质检记录
			recordService.insertBatch(records);
			// 3.删除原来的质检结果
			resultService.delete(new EntityWrapper<WmsQcResultEntity>().eq("INSPECTION_NO", INSPECTION_NO).eq("INSPECTION_ITEM_NO", INSPECTION_ITEM_NO));
			// 4.新增质检结果
			resultService.insertBatch(results);

			/*
			 * 5.需要更新库存数量 
			 * 逻辑：
			 * 更新库存表，库存数量和冻结数量 库存数量 = 库存数量 + 变化的合格数量 
			 * 冻结数量 = 冻结数量  + 变化的不合格数量
			 */
			firstItem.put("qualifiedQty", qualifiedQty);
			firstItem.put("unQualifiedQty", unQualifiedQty);
			dao.updateOnInspectStockQty(firstItem);
			//6.2019-06-05
			if(unQualifiedQty>0) {
				//新增库存冻结记录
				addKnFreezeRecord(firstItem,unQualifiedQty.longValue(),"00");
			}

		}

	}
	
	void addKnFreezeRecord(Map<String,Object> params,Long QTY,String freezeType) {
		WmsKnFreezeRecordEntity freezeRecord = new WmsKnFreezeRecordEntity();
		freezeRecord.setBatch((String)params.get("BATCH"));
		freezeRecord.setBinCode((String)params.get("BIN_CODE"));
		freezeRecord.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		freezeRecord.setEditor((String)params.get("USERNAME"));
		freezeRecord.setFreezeQty(QTY.longValue());
		freezeRecord.setFreezeType(freezeType);
		freezeRecord.setLgort((String)params.get("LGORT"));
		freezeRecord.setLifnr((String)params.get("LIFNR"));
		freezeRecord.setLiktx((String)params.get("LIKTX"));
		freezeRecord.setMaktx((String)params.get("MAKTX"));
		freezeRecord.setMatnr((String)params.get("MATNR"));
		freezeRecord.setMeins((String)params.get("UNIT"));
		String reason = params.get("QC_RESULT_TEXT")==null?"":params.get("QC_RESULT_TEXT").toString();
		String reasonCode = params.get("QC_RESULT_CODE")==null?"":params.get("QC_RESULT_CODE").toString();
		freezeRecord.setReason(reason);
		freezeRecord.setReasonCode(reasonCode);
		freezeRecord.setSobkz(params.get("SOBKZ")==null?"Z":params.get("SOBKZ").toString());
		freezeRecord.setWerks((String)params.get("WERKS"));
		freezeRecord.setWhNumber((String)params.get("WH_NUMBER"));
		freezeService.insert(freezeRecord);
	}

	/**
	 * 获取合格总数
	 * 
	 * @param results
	 * @return
	 */
	private Double getQualifeidQty(List<WmsQcResultEntity> results) {
		Double totalQualifeidQty = 0.0;
		for (WmsQcResultEntity result : results) {
			WmsCQcResultEntity configQcResult = baseQcService.getCQcResult(
					result.getWerks(), result.getQcResultCode());
			if (configQcResult.getReturnFlag().equals("0")
					&& configQcResult.getGoodFlag().equals("0")
					&& configQcResult.getReviewFlag().equals("0")) {//不是退货 不是不良品 不是需要评审的  = 合格品
				totalQualifeidQty += result.getResultQty();
			}
		}
		return totalQualifeidQty;
	}

	/**
	 * 获取不合格的数量总数
	 * 
	 * @param results
	 * @return
	 */
	private Double getUnQualifeidQty(List<WmsQcResultEntity> results) {
		Double totalUnQualifeidQty = 0.0;
		for (WmsQcResultEntity result : results) {
			WmsCQcResultEntity configQcResult = baseQcService.getCQcResult(
					result.getWerks(), result.getQcResultCode());
			if (configQcResult.getReturnFlag().equals("X")
					|| configQcResult.getGoodFlag().equals("X")) {
				totalUnQualifeidQty += result.getResultQty();
			}
		}
		return totalUnQualifeidQty;
	}

	private Double getQualifeidQtyMap(List<Map<String, Object>> items) {
		Double totalQualifeidQty = 0.0;
		for (Map<String, Object> map : items) {
			String werks = (String) map.get("WERKS");
			String qcResultCode = (String) map.get("QC_RESULT_CODE");
			Double qty = Double.parseDouble(String.valueOf(map.get("RESULT_QTY")));
			WmsCQcResultEntity configQcResult = baseQcService.getCQcResult(
					werks, qcResultCode);
			if (configQcResult.getReturnFlag().equals("0")
					&& configQcResult.getGoodFlag().equals("0")
					&& configQcResult.getReviewFlag().equals("0")) {
				totalQualifeidQty += qty;
			}
		}
		return totalQualifeidQty;
	}

	private Double getUnQualifeidQtyMap(List<Map<String, Object>> items) {
		Double totalUnQualifeidQty = 0.0;
		for (Map<String, Object> map : items) {
			String werks = (String) map.get("WERKS");
			String qcResultCode = (String) map.get("QC_RESULT_CODE");
			Double qty = Double.parseDouble(String.valueOf(map.get("RESULT_QTY")));
			WmsCQcResultEntity configQcResult = baseQcService.getCQcResult(werks, qcResultCode);
			if (configQcResult.getReturnFlag().equals("X") || configQcResult.getGoodFlag().equals("X")) {
				totalUnQualifeidQty += qty;
			}
		}
		return totalUnQualifeidQty;
	}

}
