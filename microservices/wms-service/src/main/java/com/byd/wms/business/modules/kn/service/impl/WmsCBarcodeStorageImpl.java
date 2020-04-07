package com.byd.wms.business.modules.kn.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.kn.dao.WmsCBarcodeSterilisationDao;
import com.byd.wms.business.modules.kn.dao.WmsCBarcodeStorageDao;
import com.byd.wms.business.modules.kn.service.WmsCBarcodeStorageService;

@Service("wmsCBarcodeStorageService")
public class WmsCBarcodeStorageImpl implements WmsCBarcodeStorageService {
	@Autowired
	private WmsCBarcodeSterilisationDao WmsCBarcodeSterilisationDao;
	@Autowired
	private WmsCBarcodeStorageDao WmsCBarcodeStorageDao;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo = (params.get("pageNo") != null && !params.get("pageNo").equals(""))
				? params.get("pageNo").toString()
				: "1";
		String pageSize = (params.get("pageSize") != null && !params.get("pageSize").equals(""))
				? params.get("pageSize").toString()
				: "15";
		int start = 0;
		int end = 6000;
		int total = WmsCBarcodeStorageDao.getBarcodeStorageCount(params);
		if (params.get("pageSize") != null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo) - 1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo) * Integer.valueOf(pageSize) > total ? total
					: Integer.valueOf(pageNo) * Integer.valueOf(pageSize);
		}
		params.put("start", start);
		params.put("end", end);
		List<Map<String, Object>> list = WmsCBarcodeStorageDao.queryBarcodeStorage(params);
		Page page = new Query<Map<String, Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(total);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	@Override
	public boolean importExel(List<Map<String, Object>> params) {
		List<Map<String, Object>> list = WmsCBarcodeStorageDao.queryAll();
		try {
			if (list.containsAll(params)) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

}
