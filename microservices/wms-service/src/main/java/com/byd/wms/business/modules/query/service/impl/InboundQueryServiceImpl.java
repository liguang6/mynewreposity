package com.byd.wms.business.modules.query.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.query.dao.InboundQueryDao;
import com.byd.wms.business.modules.query.service.InboundQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

/**
 * 进仓单标签
 * @author cscc tangj
 * @email 
 * @date 2018-11-29 09:56:18
 */
@Service("inboundQueryService")
public class InboundQueryServiceImpl implements InboundQueryService {
	@Autowired
    private InboundQueryDao inboundQueryDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=inboundQueryDao.getInboundCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		params.put("MATNR", params.get("MATNR").toString().replaceAll(" ",""));
		params.put("MATNR", params.get("MATNR").toString().replaceAll("\t",""));
		List<Map<String,Object>> list=inboundQueryDao.getInboundList(params);
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
    		params.put("START", start);params.put("END", end);
    		list=inboundQueryDao.getInboundList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	@Override
	public PageUtils queryInboundItemPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=inboundQueryDao.getInboundItemCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		params.put("MATNR", params.get("MATNR").toString().replaceAll(" ",""));
		params.put("MATNR", params.get("MATNR").toString().replaceAll("\t",""));
		List<Map<String,Object>> list=inboundQueryDao.getInboundItemList(params);
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
    		params.put("START", start);params.put("END", end);
    		list=inboundQueryDao.getInboundItemList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	@Override
	@Transactional
	public boolean delete(Map<String, Object> params) {
		
		 int a=inboundQueryDao.updateHead(params);
		 
		 int b=inboundQueryDao.updateItem(params);
		 
		 if(a>0 && b>0) {
			 return true;
		 }else {
			 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			 return false;
		 }
	}
	

}
