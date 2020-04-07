package com.byd.wms.business.modules.query.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.datasources.DataSourceNames;
import com.byd.utils.datasources.annotation.DataSource;
import com.byd.wms.business.modules.in.dao.SCMDeliveryDao;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.query.service.SCMDeliveryQueryService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年11月20日 下午4:14:19 
 * 类说明 
 */
@Service
public class SCMDeliveryQueryServiceImpl implements SCMDeliveryQueryService{
	@Autowired
	private SCMDeliveryDao scmDeliveryDao;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> queryHeadBySCM(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.queryHeadBySCM(params);
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> queryItemBySCM(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.queryItemBySCM(params);
	}
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public PageUtils queryPageDetail(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=scmDeliveryDao.queryItemBySCMCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=scmDeliveryDao.queryItemBySCM(params);
		
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
	
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=scmDeliveryDao.queryHeadBySCMCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=scmDeliveryDao.queryHeadBySCM(params);
		
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
	@Override
	public List<Map<String, Object>> getHasReceiveQty(Map<String,Object> param){
		List<Map<String, Object>> retReceipt_qty=wmsInReceiptDao.getHasReceiveQty(param);//HAS_RECEIPT_QTY
		return retReceipt_qty;
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public void updateSCMState(List<Map<String, Object>> params) {
		scmDeliveryDao.updateHEAD(params);
		scmDeliveryDao.updateITEM(params);
		scmDeliveryDao.updateDETAIL(params);
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public int updateHEAD(List<Map<String, Object>> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.updateHEAD(params);
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public int updateITEM(List<Map<String, Object>> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.updateITEM(params);
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public int updateDETAIL(List<Map<String, Object>> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.updateDETAIL(params);
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public int updateTPO_onWayAmount(List<Map<String, Object>> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.updateTPO_onWayAmount(params);
	}

	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> queryAllItemBySCM(
			List<Map<String, Object>> params) {
		// TODO Auto-generated method stub
		return scmDeliveryDao.queryAllItemBySCM(params);
	}
}
