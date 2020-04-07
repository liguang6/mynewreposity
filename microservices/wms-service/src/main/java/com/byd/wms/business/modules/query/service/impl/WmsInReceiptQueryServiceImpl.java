package com.byd.wms.business.modules.query.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.query.dao.WmsInReceiptQueryDao;
import com.byd.wms.business.modules.query.service.WmsInReceiptQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

/**
 * 查询收料房的物料操作记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-15 10:12:08
 */
@Service("wmsInReceiptQueryService")
public class WmsInReceiptQueryServiceImpl implements WmsInReceiptQueryService {
	@Autowired
    private WmsInReceiptQueryDao wmsInReceiptQueryDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsInReceiptQueryDao.getReceiptInfoCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		params.put("MATNR", params.get("MATNR").toString().replaceAll(" ",""));
		params.put("MATNR", params.get("MATNR").toString().replaceAll("\t",""));
		List<Map<String,Object>> list=wmsInReceiptQueryDao.getReceiptInfoList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	/*从采购订单表获取需求跟踪号
	 * (non-Javadoc)
	 * @see com.byd.wms.business.modules.query.service.WmsInReceiptQueryService#getPOITEMBednr(java.util.Map)
	 */
	@Override
	public List<Map<String,Object>> getPOITEMBednr(Map<String,Object> param){
			return wmsInReceiptQueryDao.getPOITEMBednr(param);
	}

	@Override
	public PageUtils queryStockPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsInReceiptQueryDao.getStockInfoCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		params.put("MATNR", params.get("MATNR").toString().replaceAll(" ",""));
		params.put("MATNR", params.get("MATNR").toString().replaceAll("\t",""));
		List<Map<String,Object>> list=wmsInReceiptQueryDao.getStockInfoList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public PageUtils queryBasicDataUnsyncPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;
		int end = 0;
		int count=wmsInReceiptQueryDao.queryBasicDataUnsyncCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=wmsInReceiptQueryDao.queryBasicDataUnsync(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getLabelData(Map<String, Object> params) {
		return wmsInReceiptQueryDao.getLabelData(params);
	}
	
	
}
