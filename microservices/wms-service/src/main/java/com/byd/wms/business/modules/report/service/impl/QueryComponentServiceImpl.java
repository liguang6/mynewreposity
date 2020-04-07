package com.byd.wms.business.modules.report.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.report.dao.QueryComponentDao;
import com.byd.wms.business.modules.report.service.QueryComponentService;

/**
 * @author 作者 : ren.wei3@byd.com
 * @version 创建时间：2019年6月4日 下午5:14:41
 */

@Service("queryComponentService")
public class QueryComponentServiceImpl implements QueryComponentService {

	@Autowired
	private QueryComponentDao queryComponentDao;

	@Autowired
	private WmsSapRemote wmsSapRemote;

	/**
	 * 库存查询(与ERP库存比较)
	 */
	@Override
	public PageUtils queryStockPage(Map<String, Object> params) {
		String pageNo = (params.get("pageNo") != null && !params.get("pageNo").equals("")) ? params.get("pageNo").toString() : "1";
		String pageSize = (params.get("pageSize") != null && !params.get("pageSize").equals("")) ? params.get("pageSize").toString() : "15";
		int start = 0;
		int end = 0;
		int count = queryComponentDao.getStockInfoCount(params);
		if (params.get("pageSize") != null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo) - 1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo) * Integer.valueOf(pageSize);
		} else {
			end = count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String, Object>> list = queryComponentDao.getStockInfoList(params);
		List<String> matnrRange = new ArrayList<String>();
		Map<String, Object> returnMap = new HashMap<String, Object>();

		//构造数据
		List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				matnrRange.add(map.get("MATNR") == null ? "" : map.get("MATNR").toString());
			}

			params.put("MATNRANGE", matnrRange);
			//调用SAP接口，获取库存（按物料号）
			returnMap = wmsSapRemote.queryMaterialStock(params);
		}

		String erpdata = returnMap.get("DATA") == null ? "" : returnMap.get("DATA").toString();
		List<Map<String, Object>> saveDataList = (List<Map<String, Object>>) JSONArray.parse(erpdata);
		for (Map<String, Object> rmap1 : list) {
			boolean flag = false;
			String wmsmatnr = rmap1.get("MATNR").toString();
			String wmslifnr = rmap1.get("LIFNR") == null ? "" : rmap1.get("LIFNR").toString();
			String wmssobkz = rmap1.get("SOBKZ") == null ? "" : rmap1.get("SOBKZ").toString();

			for (Map<String, Object> map : saveDataList) {
				String sapmatnr = map.get("MATNR").toString();
				String saplifnr = map.get("LIFNR").toString();
				saplifnr = saplifnr.replaceAll("^(0+)", "");
				if (sapmatnr.equals(wmsmatnr) && wmslifnr.equals("")) {
					if (wmssobkz.equals("E"))
						rmap1.put("ESAL_QTY", map.get("LABST"));//取自有的销售订单库存
					else
						rmap1.put("ERP_QTY", map.get("LABST")); //非限制自有库存
					flag = true;
					break;
				} else if (sapmatnr.equals(wmsmatnr) && wmslifnr.equals(saplifnr)) {
					if (wmssobkz.equals("E"))
						rmap1.put("ESAL_QTY", map.get("KLABS")); //取使用的销售订单库存
					else
						rmap1.put("ERP_QTY", map.get("KLABS")); //取非限制使用的寄售库存
					flag = true;
					break;
				}
			}
			if (!flag) {
				if (wmssobkz.equals("E"))
					rmap1.put("ESAL_QTY", 0);
				else
					rmap1.put("ERP_QTY", 0);
			}
		}

		Page page = new Query<Map<String, Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}
}
