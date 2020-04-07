package com.byd.bjmes.modules.product.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.bjmes.modules.product.dao.ProductHandoverDao;
import com.byd.bjmes.modules.product.service.ProductHandoverService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/***
 * @Desc 车间内交接、车间供货
 * @author tangj
 * @date 2019-10-23 16:12:06
 */
@Service("matHandoverService")
public class ProductHandoverServiceImpl implements ProductHandoverService{
	@Autowired
	private ProductHandoverDao matHandoverDao;

	@Override
	public List<Map<String, Object>> getProductInfo(Map<String, Object> params) {
		return matHandoverDao.getProductInfo(params);
	}

	@Override
	@Transactional
	public void save(Map<String, Object> params) {
		Gson gson=new Gson();

		List<Map<String,Object>> detail_list=new ArrayList<Map<String,Object>>();
		
		if(params.get("matInfoList")!=null && !params.get("matInfoList").toString().equals("")) {
			detail_list =
				gson.fromJson((String) params.get("matInfoList"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		}
		params.put("detail_list", detail_list);
		matHandoverDao.save(params);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=matHandoverDao.getHandoverCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list=matHandoverDao.getHandoverList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		if(page.getRecords().size()==0&&page.getCurrent()!=1) {
        	pageNo="1";
        	if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
    			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
    			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
    		}else {
    			end=count;
    		}
    		params.put("start", start);params.put("limit", end);
        	list=matHandoverDao.getHandoverList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> checkProductInfo(String product_no) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("product_no", product_no);
		return matHandoverDao.getHandoverList(map);
	}
}
