package com.byd.wms.business.modules.qc.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.dao.WmsQcResultDao;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;
import com.byd.wms.business.modules.qc.service.WmsQcStockRejudge;
import com.byd.wms.business.modules.qc.service.impl.InspectionHeadServiceImpl;
/**
 * 质检模块pda 接口
 * @author yang.lin35
 * @create 2019-04-19
 */
@RestController
@RequestMapping("/qc/pda")
public class PDAController {
	@Autowired
	WmsQcInspectionItemDao itemDAO;
	@Autowired
	InspectionHeadServiceImpl headService;
	@Autowired
	WmsQcResultDao resultDAO;
	@Autowired
	WmsQcStockRejudge stockRejudeService;
	//查询质检选项
	@CrossOrigin
	@RequestMapping("/qc_result_dict")
	public R getQcResultDict() {
		return R.ok().put("data", itemDAO.queryDict("QC_RESULT_CODE"));
	}
	
	//单箱质检保存
	@CrossOrigin
	@RequestMapping("/box_qc_save")
	public R boxQcSave(@RequestBody Map<String,Object> data) {
		String LABEL_NO = (String) data.get("LABEL_NO");
		String QC_RESULT_CODE = (String) data.get("QC_RESULT_CODE");
		String QC_RESULT_TEXT = (String) data.get("QC_RESULT_TEXT");
		String QC_RESULT = (String) data.get("QC_RESULT");
		if(StringUtils.isBlank(LABEL_NO) || StringUtils.isBlank(QC_RESULT_CODE) || StringUtils.isBlank(QC_RESULT_TEXT) || StringUtils.isBlank(QC_RESULT) )
			return R.error("参数错误");
		
		//根据标签号，查询送检单
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("labelNo", LABEL_NO);
		List<WmsQcInspectionItemEntity> labels =  itemDAO.queryInspectionByLabelNo(map);
		if(labels == null || labels.size() == 0)
			return R.error(String.format("未找到%s标签的消息", LABEL_NO));
		
		labels.forEach(o -> {
			o.setQcResultCode(QC_RESULT_CODE);
			o.setQcResultText(QC_RESULT_TEXT);
			o.setReturnreason(QC_RESULT);
			o.setBatchQcFlag("true");
		});
		headService.saveBatchInspectionResult(labels, "");
		return R.ok();
	}
	
	@CrossOrigin
	@RequestMapping("/batch_qc_query")
	public R batchQcQuery(@RequestBody Map<String,Object> data) throws SQLException {
		String INSPECTION_NO = (String) data.get("INSPECTION_NO");
		return R.ok().put("data", itemDAO.queryInspectionInfos(INSPECTION_NO));
	}
	
	//批量质检，提交
	@CrossOrigin
	@RequestMapping("/batch_qc_save")
	public R batchQcSave(@RequestBody Map<String,Object> data) {
		String QC_RESULT_CODE = (String) data.get("QC_RESULT_CODE");
		String QC_RESULT_TEXT = (String) data.get("QC_RESULT_TEXT");
		String QC_RESULT = (String) data.get("QC_RESULT");
		String INSPECTION_NO = (String) data.get("INSPECTION_NO");
		if(StringUtils.isBlank(QC_RESULT_CODE) || StringUtils.isBlank(QC_RESULT_TEXT) || StringUtils.isBlank(QC_RESULT) || StringUtils.isBlank(INSPECTION_NO)) {
			return R.error("参数错误");
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("INSPECTION_NO", INSPECTION_NO);
		List<WmsQcInspectionItemEntity> inspectionList =  itemDAO.listInspectionItems(map);
		if(inspectionList == null || inspectionList.size() ==0)
			return R.error("未找到送检单信息");
		
		inspectionList.forEach(o -> {
			o.setQcResultCode(QC_RESULT_CODE);
			o.setQcResultText(QC_RESULT_TEXT);
			o.setReturnreason(QC_RESULT);
			o.setBatchQcFlag("true");
		});
		
		headService.saveBatchInspectionResult(inspectionList, "");
		return R.ok();
	}
	
	@CrossOrigin
	@RequestMapping("/single_qc_change_query")
	public R sinagleQcChangeQuery(@RequestBody Map<String,Object> data) {
		String LABEL_NO = (String) data.get("LABEL_NO"); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("LABEL_NO", LABEL_NO);
		List<WmsQcResultEntity> list =  resultDAO.queryQcResult(params);
		if(list == null || list.size() != 1) {
			return R.error("数据错误!");
		}
		return R.ok().put("data", list);
	}

	//单箱改判
	@CrossOrigin
	@RequestMapping("/single_qc_change_save")
	public R singleQcChangeSave(@RequestBody WmsQcResultEntity data) {
		String QC_RESULT_CODE = data.getQcResultCode();
		String QC_RESULT_TEXT = data.getQcResultText();
		String QC_RESULT = data.getQcResult();
		String LABEL_NO = data.getPdaLabelNo();
		
		if(StringUtils.isBlank(QC_RESULT_CODE) || StringUtils.isBlank(QC_RESULT_TEXT) || StringUtils.isBlank(LABEL_NO)) {
			return R.error("参数错误");
		}
		List<WmsQcResultEntity> results = new ArrayList<WmsQcResultEntity>();
		results.add(data);
		headService.reJudgeSave(results, "");//TODO: 加上用户名
		return R.ok();
	}

	@CrossOrigin
	@RequestMapping("/batch_qc_change_query")
	public R batchQcChangeQuery(@RequestBody Map<String,Object> data) {
		String INSPECTION_NO = (String) data.get("INSPECTION_NO");
		if(StringUtils.isBlank(INSPECTION_NO))
			return R.error();
		List<Map<String,Object>> inspectionInfoList =  itemDAO.queryInspectionInfos(INSPECTION_NO);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("INSPECTION_NO", INSPECTION_NO);
		List<WmsQcResultEntity> results =  resultDAO.queryQcResult(map);
		if(inspectionInfoList == null || inspectionInfoList.size() ==0 ) {
			return R.error("数据错误");
		}
		//如果送检单只关联了一个质检结果
		if(results.size() == 1) {
			inspectionInfoList.get(0).put("QC_RESULT_CODE", results.get(0).getQcResultCode());
			inspectionInfoList.get(0).put("QC_RESULT_TEXT", results.get(0).getQcResultText());
			inspectionInfoList.get(0).put("QC_RESULT", results.get(0).getQcResult());
		}
		
		return R.ok().put("data", inspectionInfoList);
	}
	
	@CrossOrigin
	@RequestMapping("/batch_qc_change_save")
	public R batchQcChangeSave(@RequestBody Map<String,Object> data) {
		String INSPECTION_NO = (String) data.get("INSPECTION_NO");
		String QC_RESULT_CODE = (String) data.get("QC_RESULT_CODE");
		String QC_RESULT = (String) data.get("QC_RESULT");
		String QC_RESULT_TEXT = (String) data.get("QC_RESULT_TEXT");
		String LABELS = (String) data.get("LABELS");
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("INSPECTION_NO", INSPECTION_NO);
		List<WmsQcResultEntity>  results  = resultDAO.queryQcResult(map);
		boolean first = false;//标签状态只需更新一次，存在多个质检结果时,只第一条记录加上PdaLabelNo
		for(WmsQcResultEntity result:results) {
			result.setQcResultCode(QC_RESULT_CODE);
			result.setQcResultText(QC_RESULT_TEXT);
			result.setQcResult(QC_RESULT);
			if(!first){
				result.setPdaLabelNo(LABELS);
				first = true;
			}
		}
		headService.reJudgeSave(results, "");//TODO: 账号的用户名
		return R.ok();
	}
	
	//单箱复检保存
	@CrossOrigin
	@RequestMapping("/single_qc_rejudge_save")
	public R singleQcReJudgeQuery(@RequestBody Map<String,Object> data) {
		String LABEL_NO = (String) data.get("LABEL_NO");
		String QC_RESULT_CODE = (String) data.get("QC_RESULT_CODE");
		String QC_RESULT_TEXT = (String) data.get("QC_RESULT_TEXT");
		String QC_RESULT = (String) data.get("QC_RESULT");
		
		if(StringUtils.isBlank(LABEL_NO) || StringUtils.isBlank(QC_RESULT_CODE) || StringUtils.isBlank(QC_RESULT_TEXT) || StringUtils.isBlank(QC_RESULT)) {
			return R.error("参数异常");
		}
		
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("LABEL_NO", LABEL_NO);
		List<Map<String,Object>> inspectionList =  itemDAO.queryInspectionInfoForRejudge(map);
		if(inspectionList == null || inspectionList.size() != 1)
			return R.error("数据异常");
		
		inspectionList.forEach(m-> {
			m.put("QC_RESULT_CODE", QC_RESULT_CODE);
			m.put("QC_RESULT_TEXT", QC_RESULT_TEXT);
			m.put("QC_RESULT", QC_RESULT);
			m.put("LABEL_NO", LABEL_NO);
			m.put("USERNAME", "");//TODO:获取用户信息
		});
		
		try {
			stockRejudeService.saveStockRejudgeNotInspect(inspectionList);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return R.error("数据操作异常");
		}
		return R.ok();
	}
	
	@CrossOrigin
	@RequestMapping("/batch_qc_rejudge_query")
	public R batchQcReJudgeQuery(@RequestBody Map<String,Object> data) {
		String INSPECTION_NO = (String) data.get("INSPECTION_NO");
		if(StringUtils.isBlank(INSPECTION_NO)) {
			return R.error("参数异常");
		}
		//查询送检单数据，和条码管理，过期日期信息

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("INSPECTION_NO", INSPECTION_NO);
		List<Map<String,Object>> inspectionInfo =  itemDAO.queryInspectionInfoForRejudge(map);
		if(inspectionInfo == null || inspectionInfo.size() != 1) {
			return R.error("数据异常");
		}
		return R.ok().put("data", inspectionInfo);
	}
	
	//pda - 库存复检批量保存
	@CrossOrigin
	@RequestMapping("/batch_qc_rejudge_save")
	public R batchQcReJudgeSave(@RequestBody Map<String,Object> data) {
		String INSPECTION_NO = (String) data.get("INSPECTION_NO");
		String QC_RESULT_CODE = (String) data.get("QC_RESULT_CODE");
		String QC_RESULT_TEXT = (String) data.get("QC_RESULT_TEXT");
		String QC_RESULT = (String) data.get("QC_RESULT");
		String EFFECT_DATE = (String) data.get("EFFECT_DATE");
		if(StringUtils.isBlank(INSPECTION_NO) || StringUtils.isBlank(QC_RESULT_CODE) || StringUtils.isBlank(QC_RESULT_TEXT) || StringUtils.isBlank(QC_RESULT) ||  StringUtils.isBlank(EFFECT_DATE)) {
			return R.error("参数异常");
		}
		
		//查询送检单信息和其他附加信息（有效日期，有效日期标识，条码标识）
		Map<String,Object> qmap = new HashMap<String,Object>();
		qmap.put("INSPECTION_NO", INSPECTION_NO);
		List<Map<String,Object>> map = itemDAO.queryInspectionInfoForRejudge(qmap);
		//根据入参，修改质检结果，质检有效日期
		map.forEach(m->{
			m.put("QC_RESULT_CODE", QC_RESULT_CODE);
			m.put("QC_RESULT_TEXT", QC_RESULT_TEXT);
			m.put("QC_RESULT", QC_RESULT);
			m.put("EFFECT_DATE", EFFECT_DATE);
			m.put("USERNAME", "");//TODO: 用户名获取
		});
		try {
			stockRejudeService.saveStockRejudgeNotInspect(map);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return R.error("操作异常");
		}
		return R.ok();
	}
}
