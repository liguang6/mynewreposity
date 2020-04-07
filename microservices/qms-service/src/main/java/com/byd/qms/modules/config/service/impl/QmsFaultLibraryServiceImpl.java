package com.byd.qms.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.config.dao.QmsFaultLibraryDao;
import com.byd.qms.modules.config.service.QmsFaultLibraryService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service("qmsFaultLibraryService")
public class QmsFaultLibraryServiceImpl implements QmsFaultLibraryService {
	@Autowired
	private QmsFaultLibraryDao qmsFaultLibraryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = qmsFaultLibraryDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=qmsFaultLibraryDao.getListByPage(params);
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
        	list=qmsFaultLibraryDao.getListByPage(params);
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
		// 产生新的故障代码：取缺陷类型中文拼音首字母+四位流水号
		String faultCode="QT";  //默认其他拼音首字母
		if(params.get("faultCode")!=null && !params.get("faultCode").toString().equals("")) {
			faultCode=params.get("faultCode").toString();
		}
		Integer newFaultCode=qmsFaultLibraryDao.getMaxFaultCode(faultCode);
		newFaultCode=newFaultCode!=null ? newFaultCode+1 : 1;
		if(newFaultCode.toString().length()==1) {
			params.put("faultCode",faultCode+"000"+newFaultCode);
		}
		if(newFaultCode.toString().length()==2) {
			params.put("faultCode",faultCode+"00"+newFaultCode);
		}
		if(newFaultCode.toString().length()==3) {
			params.put("faultCode",faultCode+"0"+newFaultCode);
		}
		if(newFaultCode.toString().length()==4) {
			params.put("faultCode",faultCode+newFaultCode);
		}
		return qmsFaultLibraryDao.save(params);
	}
	@Override
	public Map<String, Object> getById(Long id) {
		// TODO Auto-generated method stub
		return qmsFaultLibraryDao.getById(id);
	}

	@Override
	public List<Map<String, Object>> getList(Map<String, Object> params) {
		return qmsFaultLibraryDao.getList(params);
	}

	@Override
	public int update(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return qmsFaultLibraryDao.update(params);
	}

	@Override
	public int delete(Long id) {
		// TODO Auto-generated method stub
		return qmsFaultLibraryDao.delete(id);
	}
}
