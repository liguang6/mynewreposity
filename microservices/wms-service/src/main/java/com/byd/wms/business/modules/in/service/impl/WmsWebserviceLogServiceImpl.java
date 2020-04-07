package com.byd.wms.business.modules.in.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.in.dao.WmsWebserviceLogDao;
import com.byd.wms.business.modules.in.entity.WmsWebServiceLogEntity;
import com.byd.wms.business.modules.in.service.WmsWebserviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author rain
 * @date 2019年11月27日14:12:20
 * @description webservicelog
 */
@Service
public class WmsWebserviceLogServiceImpl extends ServiceImpl<WmsWebserviceLogDao, WmsWebServiceLogEntity> implements WmsWebserviceLogService {
	@Autowired
	private WmsWebserviceLogDao wmsWebserviceLogDao;


	/**
	 * 接口日志查询
	 * @param params
	 * @return
	 */
	@Override
//	@DataSource(name = DataSourceNames.SECOND)
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsWebserviceLogDao.queryLogCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=wmsWebserviceLogDao.queryLogInfos(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	@Override
	public WmsWebServiceLogEntity queryByPkLog(Long pkLog) {

		return baseMapper.queryByPkLog(pkLog);
	}

}
