package com.byd.wms.business.modules.outpda.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.outpda.dao.OutPdaDao;
import com.byd.wms.business.modules.outpda.service.OutPdaService;

@Service
public class OutPdaServiceImpl implements OutPdaService {
	@Autowired
	private OutPdaDao outPdaDao;

	@Override
	public PageUtils queryTaskPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=outPdaDao.getTaskCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=outPdaDao.getTaskList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
	
	/**
	 * 根据需求号获取超发标识
	 * @param params
	 * @return
	 */
	public String getChaFaBiaoShi(Map<String, Object> params) {
		return outPdaDao.getChaFaBiaoShi(params);
	}
	
	/**
	 * 根据页面扫描的条码进行校验，校验通过返回条码和数量，否则返回count=0
	 */
	@Override
	public Map<String, Object> getLabelInfo(Map<String, Object> params) {
		Map<String, Object> rsMap = outPdaDao.getLabelInfo(params);
		if (rsMap == null) {
			rsMap = new HashMap<String, Object>();
			rsMap.put("count", 0);
		}
		return rsMap;
	}
	
	/**
	 * 保存下架信息
	 * 
	 * @param params
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveXiaJiaXinXi(Map<String, Object> params) {
		if(params.get("QBJP").toString().toLowerCase().equals("true")) {
			params.put("WT_STATUS", "02");
		}else {
			params.put("WT_STATUS", "01");
		}		
		params.put("LABEL_STATUS", "09");
		outPdaDao.updateTask(params);
		outPdaDao.updateLabel(params);
		outPdaDao.insertJianPeiLabel(params);
	}
}
