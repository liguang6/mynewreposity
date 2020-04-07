package com.byd.qms.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.config.dao.QmsCheckNodeDao;
import com.byd.qms.modules.config.service.QmsCheckNodeService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service("qmsCheckNodeService")
public class QmsCheckNodeServiceImpl implements QmsCheckNodeService {
	@Autowired
	private QmsCheckNodeDao qmsCheckNodeDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = qmsCheckNodeDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=qmsCheckNodeDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		if(page.getRecords().size()==0&&page.getCurrent()!=1) {
        	pageNo="1";
        	if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
    			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
    			limit = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
    		}else {
    			limit=count;
    		}
    		params.put("start", start);params.put("limit", limit);
        	list=qmsCheckNodeDao.getListByPage(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
    }
	
	@Override
	public int save(Map<String, Object> params){
		return qmsCheckNodeDao.save(params);
	}
	@Override
	public Map<String, Object> getById(Long id) {
		// TODO Auto-generated method stub
		return qmsCheckNodeDao.getById(id);
	}

	@Override
	public List<Map<String, Object>> getList(Map<String, Object> params) {
		return qmsCheckNodeDao.getList(params);
	}

	@Override
	public int update(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return qmsCheckNodeDao.update(params);
	}

	@Override
	public int delete(Long id) {
		// TODO Auto-generated method stub
		return qmsCheckNodeDao.delete(id);
	}
}
