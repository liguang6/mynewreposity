package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCHandoverDao;
import com.byd.wms.business.modules.config.dao.WmsCHandoverTypeDao;
import com.byd.wms.business.modules.config.entity.WmsCHandoverEntity;
import com.byd.wms.business.modules.config.service.WmsCHandoverService;

@Service("wmsCHandoverService")
public class WmsCHandoverServiceImpl extends ServiceImpl<WmsCHandoverDao, WmsCHandoverEntity> implements WmsCHandoverService {
	@Autowired
	private WmsCHandoverDao wmsCHandoverDao;
//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//    	String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
//    	String whNumber = params.get("whNumber") == null?null:String.valueOf(params.get("whNumber"));
//		Page<WmsCHandoverEntity> page = this.selectPage(new Query<WmsCHandoverEntity>(params).getPage(),
//				new EntityWrapper<WmsCHandoverEntity>().eq(StringUtils.isNotBlank(werks),"WERKS", werks)
//				.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
//				.eq("DEL","0")
//		);
//		return new PageUtils(page);
//    }
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCHandoverDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsCHandoverDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
	@Override
	public List<Map<String, Object>> getCHandoverList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsCHandoverDao.getCHandoverList(map);
	}
}
