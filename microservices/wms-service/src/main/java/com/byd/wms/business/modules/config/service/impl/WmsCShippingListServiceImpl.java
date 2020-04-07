package com.byd.wms.business.modules.config.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.dao.WmsCShippingListDao;
import com.byd.wms.business.modules.config.service.WmsCShippingListService;



/** 
 * 类说明 
 */
@Service("wmsCShippingListService")
public class WmsCShippingListServiceImpl implements WmsCShippingListService {
	@Autowired
	WmsCShippingListDao wmsCShippingListDao;
	@Autowired
	private WmsCDocNoService docNoService;
	
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCShippingListDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String, Object>> list=wmsCShippingListDao.queryNoticeMailList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}
	@Override
	public void saveNoticeMail(Map<String, Object> params) {
		if(params.get("o")==null||params.get("itemList")==null)
			return ;
		Map<String, Object> map = JSONObject.parseObject(params.get("o").toString(), Map.class);
		List<Map<String,Object>> itemList = (List<Map<String,Object>>)JSONArray.parse(params.get("itemList").toString());
		String werks = itemList.get(0).get("FACT_NO").toString();
		String DNNo = docNoService.getDocNo(werks, WmsDocTypeEnum.OUT_DN.getCode());
		for(Map<String,Object> m:itemList) {
			m.putAll(map);
			m.put("DNNo", DNNo);
		}		
		wmsCShippingListDao.insertNoticeMail(itemList);
	}
	@Override
	public Map<String, Object> selectById(String id,String itemNo) {
		return wmsCShippingListDao.selectById(id,itemNo);
	}

	@Override
	public void updateById(Map<String, Object> params) {
		if(params.get("o")==null||params.get("itemList")==null)
			return ;
		Map<String, Object> map = JSONObject.parseObject(params.get("o").toString(), Map.class);
		List<Map<String,Object>> itemList = (List<Map<String,Object>>)JSONArray.parse(params.get("itemList").toString());
		for(Map<String,Object> m:itemList) {
			m.putAll(map);
		}	
		wmsCShippingListDao.updateById(itemList);
	}

	
}
