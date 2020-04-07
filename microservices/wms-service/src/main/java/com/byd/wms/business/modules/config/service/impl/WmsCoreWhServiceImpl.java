package com.byd.wms.business.modules.config.service.impl;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCoreWhDao;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;

@Service("wmsCoreWhService")
public class WmsCoreWhServiceImpl extends ServiceImpl<WmsCoreWhDao, WmsCoreWhEntity> implements WmsCoreWhService {

	@Autowired
	private WmsCoreWhDao wmsCoreWhDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
        String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
        String whNumber = params.get("whNumber") == null?null:String.valueOf(params.get("whNumber"));
		
        if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        
        if(StringUtils.isBlank(whNumber)){
        	whNumber = params.get("WH_NUMBER") == null?null:String.valueOf(params.get("WH_NUMBER"));
		}
        
        Page<WmsCoreWhEntity> page = this.selectPage(new Query<WmsCoreWhEntity>(params).getPage(),
				new EntityWrapper<WmsCoreWhEntity>().like("WERKS", werks).like("WH_NUMBER", whNumber).eq("DEL","0")
		);
		return new PageUtils(page);
	}

	@Override
	public int deleteBatch(List ids) {
		int result=this.deleteBatch(ids);
		return result;
	}

	@Override
	public int delById(Long id) {
		int result=this.delById(id);
		return result;
	}

	@Override
	public PageUtils queryPagenew(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsCoreWhDao.getWmsCoreWhAddressList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsCoreWhDao.getWmsCoreWhAddressCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

}
