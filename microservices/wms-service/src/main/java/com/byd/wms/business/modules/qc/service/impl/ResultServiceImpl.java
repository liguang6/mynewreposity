package com.byd.wms.business.modules.qc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.StringUtils;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.dao.WmsQcResultDao;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;
import com.byd.wms.business.modules.qc.service.WmsQcResultService;

@Service("wmsQcResultService")
public class ResultServiceImpl extends ServiceImpl<WmsQcResultDao, WmsQcResultEntity> implements WmsQcResultService {
	@Autowired
	private WmsQcResultDao resultDAO;
	@Autowired
	private ServiceUtils baseService;
	@Autowired
	private WmsQcInspectionItemDao itemDAO;
	@Override
	public List<WmsQcResultEntity> queryRejudgeItems(Map<String, Object> params) {
		String FULL_NAME = (String) params.get("FULL_NAME");
		List<WmsQcResultEntity>  list = resultDAO.selectRejuedgeResultItems(params);
		//update: 2019-05-28
		list.forEach( r -> {
			//设置质检员
			r.setQcPeople(FULL_NAME);
			//设置质检结果是否创建了退货单
			List<Map<String,Object>> a = resultDAO.queryOutReturnByQcResult(r.getQcResultNo(), r.getQcResultItemNo());
			if(a != null && a.size() > 0) {
				if(a.get(0).get("RETURN_NO") != null && a.get(0).get("RETURN_ITEM_NO") != null) {
					r.setHasOutReturn(true);
				}else {
					r.setHasOutReturn(false);
				}
			}
			//设置质检结果是否创建了进仓单
			List<Map<String,Object>> b = resultDAO.queryInInboundByQcResult(r.getQcResultNo(), r.getQcResultItemNo());
			if(b != null && b.size() > 0) {
				if(b.get(0).get("INBOUND_NO") != null && b.get(0).get("INBOUND_ITEM_NO") != null) {
					r.setHasInInbound(true);
				}else {
					r.setHasInInbound(false);
				}
			}
		});
		return list;
	}

	@Override
	public PageUtils queryResultList(Map<String, Object> params) {
		Page<Map<String,Object>> page = new Query<Map<String,Object>>(params).getPage();
		List<Map<String,Object>> records =  resultDAO.selectResultList(page,params);
		
		
		//检验状态，根据质检结果配置查出
		String inspectionType = params.get("inspectionType") == null ? null : (String) params.get("inspectionType");
		if(StringUtils.isNotBlank(inspectionType)){
			
			records = records.stream().filter(m-> {
				String werks = (String) m.get("WERKS");
				String qcResultCode = (String)m.get("QC_RESULT_CODE");
				WmsCQcResultEntity config = baseService.getCQcResult(werks, qcResultCode);
				if(config.getQcStatus().equals(inspectionType)){
					return true;
				}
				return false;
			}).collect(Collectors.toList());
			
		}
		
		//根据标签状态过滤
		String labelStatus =(String) params.get("labelStatus");
		List<String> labelStatusList = new ArrayList<String>();
		labelStatusList.add(labelStatus);
	
		List<String> labelNoList = new ArrayList<String>();
		records = records.stream().filter(l-> {
			if(StringUtils.isNotBlank(labelStatus)) {
				String labelList =(String) l.get("LABEL_NO");
				if(StringUtils.isNotBlank(labelList)) {
					String[] labels = labelList.split(",");
					Map<String,Object> map = new HashMap<String,Object>();
					labelNoList.clear();
					for(String label:labels)
						labelNoList.add(label);
					map.put("LABEL_STATUS_LIST", labelStatusList);
					map.put("LABEL_NO_LIST", labelNoList);
					List<Map<String,Object>> labelResult =  itemDAO.queryLabelInfo(map);
					if(labelResult == null || labelResult.size() ==0) {
						return false;
					}
				}
			}
			return true;
		}).collect(Collectors.toList());
		page.setRecords(records);
		return new PageUtils(page);
	}

	@Override
	public PageUtils queryDestroyQtyList(Map<String, Object> params) {
		Page<Map<String,Object>> page = new Query<Map<String,Object>>(params).getPage();
		List<Map<String,Object>> records =  resultDAO.selectDestroyQtyList(page, params);
		page.setRecords(records);
		return new PageUtils(page);
	}}
