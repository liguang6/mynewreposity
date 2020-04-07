package com.byd.wms.business.modules.query.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.query.dao.SapDocQueryDao;
import com.byd.wms.business.modules.query.service.SapDocQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

/**
 * 查询SAP凭证记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 10:12:08
 */
@Service("sapDocQueryService")
public class SapDocQueryServiceImpl implements SapDocQueryService {
	@Autowired
    private SapDocQueryDao sapDocQueryDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=sapDocQueryDao.getDocCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		params.put("MATERIAL", params.get("MATERIAL").toString().replaceAll(" ",""));
		params.put("MATERIAL", params.get("MATERIAL").toString().replaceAll("\t",""));
		List<Map<String,Object>> list=sapDocQueryDao.getDocList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

}
