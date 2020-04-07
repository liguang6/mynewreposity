package com.byd.wms.business.modules.pda.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.pda.dao.JITDao;
import com.byd.wms.business.modules.pda.service.JITService;

@Service("jITService")
public class JITServiceImpl implements JITService {
	@Autowired
	private JITDao jITDao;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private JITService jITService;
	
	@Autowired
	private WmsCDocNoService wmsCDocNoService;

	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;

	@Autowired
	private WmsOutRequirementHeadDao headDao;

	@Autowired
	private WmsOutRequirementItemDao itemDao;

	@Autowired
	private WarehouseTasksService warehouseTasksService;

	@Autowired
	private CommonService commonService;

	@Override
	public List JITScanLabel(Map<String, Object> params) {
		return jITDao.JITScanLabel(params);
	}

	public List JITScanDeliveryNo(Map<String, Object> params) {
		return jITDao.JITScanDeliveryNo(params);
	}

	@Transactional(rollbackFor = Exception.class)
	public void JITPick(Map<String, Object> params) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("SAVE_DATA");

		Map<String, Object> currentUser = userUtils.getUser();
		Map<String, Object> labparams = new HashMap<String, Object>();
		labparams.put("WMS_DOC_TYPE", "08");
		labparams.put("WERKS", list.get(0).get("WERKS"));

		// 条码
		List<Map<String, Object>> oldLabList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> labList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> o : list) {
			List<String> label = (List<String>) (o.get("label"));
			Double qty = Double.valueOf(o.get("QTY")==null?"0":o.get("QTY").toString()); // 需求数量
			Double reqty = Double.valueOf(o.get("reQTY")==null?"0":o.get("reQTY").toString()); // 已扫数量
			Double dosage = Double.valueOf(o.get("DOSAGE")==null?"0":o.get("DOSAGE").toString());// 最小包装数量
			if(dosage==0)
				throw new RuntimeException("最小包装数量不能为空");
			int box_num =qty>=reqty?(int) Math.ceil(reqty / dosage):(int) Math.ceil(qty / dosage);
			if(reqty<=qty) {
				for (String s : label) {
					//更新扫描条码状态
					Map<String, Object> oldLabMap = new HashMap<String, Object>();
					oldLabMap.put("LABEL_NO", s);
					oldLabMap.put("LABEL_STATUS", "09");
					oldLabList.add(oldLabMap);
				}
			}else {
				int q = 0;
				for (int j = 0; j < label.size() - 1; j++) {
					Map<String, Object> oldLabMap = new HashMap<String, Object>();
					oldLabMap.put("LABEL_NO", label.get(j));
					oldLabMap.put("LABEL_STATUS", "09");
					oldLabMap.put("LABEL_STATUS", params.get("WERKS"));
					oldLabList.add(oldLabMap);
					Map<String, Object> res = (Map<String, Object>) jITService.JITScanLabel(oldLabMap).get(0);
					q+=Integer.valueOf(res.get("BOX_QTY")==null?"":res.get("BOX_QTY").toString());
				}
				Map<String, Object> oldLabMap = new HashMap<String, Object>();
				oldLabMap.put("LABEL_NO", label.get(label.size() - 1));
				oldLabMap.put("LABEL_STATUS", "09");						
				oldLabMap.put("BOX_QTY", qty-q);
				oldLabList.add(oldLabMap);
				
				String labelNo = "";
				Map<String, Object> docNo = null;
				docNo = wmsCDocNoService.getDocNo(labparams);
				if (docNo.get("MSG") != null && !"success".equals(docNo.get("MSG"))) {
					throw new RuntimeException(docNo.get("MSG").toString());
				}
				labelNo = docNo.get("docno").toString();
				Map<String, Object> labMap = new HashMap<String, Object>();
				labMap.put("LABEL_NO", labelNo);
				labMap.put("LABEL_STATUS", "00");
				labMap.put("BOX_QTY", reqty - qty);
				labMap.put("MATNR", o.get("MATNR"));
				labMap.put("MAKTX", o.get("MAKTX"));
				labList.add(labMap);
			}
			
			//条码转换  生成新条码
			for (int i = 1; i <= box_num; i++) {
				String LABEL_NO = "";
				Map<String, Object> doc = null;
				doc = wmsCDocNoService.getDocNo(labparams);
				if (doc.get("MSG") != null && !"success".equals(doc.get("MSG"))) 
					throw new RuntimeException(doc.get("MSG").toString());
				LABEL_NO = doc.get("docno").toString();
				String BOX_SN = i + "/" + box_num;
				Double BOX_QTY = dosage;
				String END_FLAG = "0";				
				if (i == box_num) {
					END_FLAG = "X";
					if(reqty<=qty) 
						BOX_QTY = reqty - (box_num - 1) * dosage;
					else 
						BOX_QTY = qty - (box_num - 1) * dosage;
				}
				Map<String, Object> labMap = new HashMap<String, Object>();
				labMap.put("LABEL_NO", LABEL_NO);
				labMap.put("LABEL_STATUS", "09");
				labMap.put("BOX_SN", BOX_SN);
				labMap.put("FULL_BOX_QTY", dosage);
				labMap.put("BOX_QTY", BOX_QTY);
				labMap.put("END_FLAG", END_FLAG);
				labMap.put("MATNR", o.get("MATNR"));
				labMap.put("MAKTX", o.get("MAKTX"));
				labMap.put("STATION", o.get("POINT_OF_USE"));
				labMap.put("ON_LINE_TYPE", "");
				labMap.put("ON_LINE_MOUTH", "");
				labMap.put("CAR_TYPE", "");
				labMap.put("DOSAGE", dosage);
				labMap.put("CONFIGURATION", "");
				labList.add(labMap);
			}
		}
		if (oldLabList.size() > 0)
			commonService.updateLabel(oldLabList);//扫描条码
		if (labList.size() > 0)
			wmsInReceiptDao.insertCoreLabel(labList);//条码转换

		// 需求
		WmsOutRequirementHeadEntity outReuirementHead = new WmsOutRequirementHeadEntity();
        Map<String, Object> obj = list.get(0);
		outReuirementHead.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
		outReuirementHead.setWerks(obj.get("WERKS")==null?"":obj.get("WERKS").toString());
		outReuirementHead.setWhNumber(obj.get("WH_NUMBER")==null?"":obj.get("WH_NUMBER").toString());
		outReuirementHead.setRequiredDate(obj.get("DEMAND_TIME")==null?"":obj.get("DEMAND_TIME").toString());		
		outReuirementHead.setRequirementStatus("04");
		String requirementNo = wmsCDocNoService.getDocNo(obj.get("WERKS").toString(),
				WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		outReuirementHead.setRequirementNo(requirementNo);
		outReuirementHead.setDel("0");

		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		for (Map<String, Object> o : list) {
			WmsOutRequirementItemEntity outRequirementItemEntity = new WmsOutRequirementItemEntity();
			// outRequirementItemEntity.setBusinessName("44");
			// outRequirementItemEntity.setBusinessType("14");
			outRequirementItemEntity.setCreator(currentUser.get("USERNAME").toString());
			outRequirementItemEntity.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
			outRequirementItemEntity.setLgort(o.get("LGORT")==null?"":o.get("LGORT").toString());
			outRequirementItemEntity.setLifnr(o.get("LIFNR")==null?"":o.get("LIFNR").toString());
			outRequirementItemEntity.setMaktx(o.get("MAKTX")==null?"":o.get("MAKTX").toString());
			outRequirementItemEntity.setMatnr(o.get("MATNR")==null?"":o.get("MATNR").toString());
			outRequirementItemEntity.setQty(Double.valueOf(o.get("reQTY").toString()));
			outRequirementItemEntity.setReqItemStatus("03");
			outRequirementItemEntity.setUnit(o.get("UNIT")==null?"":o.get("UNIT").toString());
			outRequirementItemEntity.setRequirementNo(requirementNo);
			outRequirementItemEntity.setRequirementItemNo(o.get("DLV_ITEM").toString());
			outRequirementItemEntity.setDel("0");
			itemList.add(outRequirementItemEntity);
		}
		headDao.insert(outReuirementHead);
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}

		// 仓库任务
		List<Map<String, Object>> wtList = new ArrayList<Map<String, Object>>();
		List ll = new ArrayList<>();
		for (Map<String, Object> param : list) {
			 param.put("WH_NUMBER", param.get("WH_NUMBER"));
			// param.put("FROM_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
			// param.put("TO_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
			param.put("FROM_BIN_CODE", param.get("POU")==null?"":param.get("POU"));
			param.put("TO_BIN_CODE", "BBBB");
			int recommendQty = Integer.valueOf(param.get("reQTY")==null?"0":param.get("reQTY").toString());
			int QTY = Integer.valueOf(param.get("QTY")==null?"0":param.get("QTY").toString());
			recommendQty = recommendQty <= QTY ? recommendQty:QTY;
			param.put("QUANTITY", recommendQty);
			param.put("CONFIRM_QUANTITY", recommendQty);
			param.put("PROCESS_TYPE", "01");
			param.put("WT_STATUS", "02"); // 已下架
			param.put("MATNR", param.get("MATNR"));
			param.put("MAKTX", param.get("MAKTX"));
			param.put("UNIT", param.get("UNIT"));
			param.put("LIFNR", param.get("LIFNR")==null?"":param.get("LIFNR"));
			param.put("BATCH", param.get("BATCH")==null?"":param.get("BATCH"));			
			param.put("CREATOR", currentUser.get("USERNAME"));
			param.put("CREATE_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			param.put("REFERENCE_DELIVERY_NO", requirementNo);
			param.put("REFERENCE_DELIVERY_ITEM", param.get("DLV_ITEM"));
			List<Map<String, Object>> l = labList.stream().filter(ele->ele.get("MATNR").equals(param.get("MATNR"))&&ele.get("LABEL_STATUS").equals("09")).collect(Collectors.toList());
			for(Map<String, Object> m:l) {
				ll.add(m.get("LABEL_NO"));
			}
			//param.put("LABEL_NO", JSONArray.toJSONString(ll));
			param.put("LABEL_NO", StringUtils.join(ll.toArray(), ','));
			wtList.add(param);
		}
		if (wtList.size() > 0)
			warehouseTasksService.saveWHTask(wtList);

		// 库存
		// START 更新库存,扣减仓位非限制库存,增加下架数量。
		List<Map<String, Object>> stockMatList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> StockMatList1 = new ArrayList<Map<String, Object>>();
		/*
		for (Map<String, Object> param : list) {
			Map<String, Object> stockMat = new HashMap<String, Object>();
			stockMat.put("WERKS", param.get("WERKS"));
			stockMat.put("WH_NUMBER", param.get("WH_NUMBER"));
			stockMat.put("MATNR", param.get("MATNR"));
			stockMat.put("LGORT", param.get("LGORT"));
			stockMat.put("BIN_CODE", param.get("POU"));
			stockMat.put("BATCH", param.get("BATCH"));
			// stockMat.put("SOBKZ", param.get("SOBKZ"));
			stockMat.put("LIFNR", param.get("LIFNR"));
			BigDecimal confirmQty = new BigDecimal(param.get("reQTY")==null?"0":param.get("reQTY").toString());
			stockMat.put("STOCK_QTY", confirmQty.negate());
			stockMat.put("XJ_QTY", confirmQty);
			stockMat.put("EDITOR", currentUser.get("USERNAME"));
			stockMat.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			stockMatList.add(stockMat);

		}
		*/
		
		List<Map<String, Object>> labelInfo = (List<Map<String, Object>>) list.get(0).get("labelInfo");
		for (Map<String, Object> param : labelInfo) {
			Map<String, Object> stockMat = new HashMap<String, Object>();
			stockMat.put("WERKS", param.get("WERKS"));
			stockMat.put("WH_NUMBER", param.get("WH_NUMBER"));
			stockMat.put("MATNR", param.get("MATNR"));
			stockMat.put("LGORT", param.get("LGORT"));
			stockMat.put("BIN_CODE", param.get("BIN_CODE"));
			stockMat.put("BATCH", param.get("BATCH"));
			stockMat.put("SOBKZ", param.get("SOBKZ"));
			stockMat.put("LIFNR", param.get("LIFNR"));
			BigDecimal confirmQty = new BigDecimal(param.get("BOX_QTY")==null?"0":param.get("BOX_QTY").toString());
			stockMat.put("STOCK_QTY", confirmQty.negate());
			stockMat.put("XJ_QTY", confirmQty);
			stockMat.put("EDITOR", currentUser.get("USERNAME"));
			stockMat.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			stockMatList.add(stockMat);

		}
		commonService.modifyWmsStock(stockMatList);

		Map<String, Object> saveparams = new HashMap<String, Object>();
		saveparams.put("STOCK_TYPE", "STOCK_QTY");
		saveparams.put("matList", StockMatList1);
		commonService.saveWmsStock(saveparams);
		// END 更新库存,扣减仓位非限制库存,增加下架数量,同时新增(修改)一笔BBBB库存。
	}
}