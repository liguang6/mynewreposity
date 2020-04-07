package com.byd.wms.business.modules.qc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.StringUtils;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.dao.WmsQcRecordDao;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;
import com.byd.wms.business.modules.qc.service.WmsQcRecordService;

@Service("wmsQcRecordService")
public class RecordServiceImpl extends ServiceImpl<WmsQcRecordDao, WmsQcRecordEntity>
		implements WmsQcRecordService {

	@Autowired
	WmsQcRecordDao dao;
	@Autowired
	private WmsQcInspectionItemDao itemDAO;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<WmsQcRecordEntity> page = this.selectPage(new Query<WmsQcRecordEntity>(params).getPage(),
				new EntityWrapper<WmsQcRecordEntity>());

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryRecordList(Map<String, Object> params) {
		Page<Map<String, Object>> page = new Query<Map<String, Object>>(params).getPage();
		List<Map<String, Object>> records = dao.selectRecordList(page, params);


		// 根据标签状态过滤
		String labelStatus = (String) params.get("labelStatus");
		List<String> labelStatusList = new ArrayList<String>();
		labelStatusList.add(labelStatus);

		List<String> labelNoList = new ArrayList<String>();
		records = records.stream().filter(l -> {
			if (StringUtils.isNotBlank(labelStatus)) {
				String labelList = (String) l.get("LABEL_NO");
				if (StringUtils.isNotBlank(labelList)) {
					String[] labels = labelList.split(",");
					Map<String, Object> map = new HashMap<String, Object>();
					labelNoList.clear();
					for (String label : labels)
						labelNoList.add(label);
					map.put("LABEL_STATUS_LIST", labelStatusList);
					map.put("LABEL_NO_LIST", labelNoList);
					List<Map<String, Object>> labelResult = itemDAO.queryLabelInfo(map);
					if (labelResult == null || labelResult.size() == 0) {
						return false;
					}
				}
			}
			return true;
		}).collect(Collectors.toList());
		page.setRecords(records);
		return new PageUtils(page);
	}

}
