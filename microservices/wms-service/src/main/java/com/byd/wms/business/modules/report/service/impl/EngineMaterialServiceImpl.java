package com.byd.wms.business.modules.report.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byd.wms.business.modules.config.dao.WmsCEngineDao;
import com.byd.wms.business.modules.report.dao.EngineMaterialDao;
import com.byd.wms.business.modules.report.service.EngineMaterialService;

/** 
* 
*/

@Service("engineMaterialService")
public class EngineMaterialServiceImpl implements EngineMaterialService {

	@Autowired
	private EngineMaterialDao engineMaterialDao;

	@Autowired
	private WmsCEngineDao wmsCEngineDao;

	@Override
	public List<Map<String, Object>> queryProject(Map<String, Object> params) {

		return engineMaterialDao.queryProject(params);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveProject() {
		Calendar calendar = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> matList = wmsCEngineDao.queryAll(new HashMap<>());
		for (Map<String, Object> m : matList) {
			m.put("date", df.format(calendar.getTime()));
			m.put("day", calendar.getTime().getDate());
		}
		List<Map<String, Object>> batchpickList = engineMaterialDao.queryBatchpick(matList);
		List<Map<String, Object>> bitpickList = engineMaterialDao.queryBitpick(matList);
		List<Map<String, Object>> afterList = engineMaterialDao.queryAftersale(matList);
		List<Map<String, Object>> inList = engineMaterialDao.queryIn(matList);
		
		for (Map<String, Object> m : batchpickList) {
			for(Map<String, Object> e : bitpickList) {
				if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
				   m.get("LIFNR").toString().equals(e.get("LIFNR").toString())&&
				   m.get("PROJECT_CODE").toString().equals(e.get("PROJECT_CODE").toString())&&
				   m.get("DAYS_CODE").toString().equals(e.get("DAYS_CODE").toString())) {
						m.put("SCATTER_QTY", e.get("SCATTER_QTY"));
				}
			}
		
			for(Map<String, Object> e : afterList) {
				if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
				   m.get("LIFNR").toString().equals(e.get("LIFNR").toString())&&
				   m.get("PROJECT_CODE").toString().equals(e.get("PROJECT_CODE").toString())&&
				   m.get("DAYS_CODE").toString().equals(e.get("DAYS_CODE").toString())) {
						m.put("AFTER_QTY", e.get("AFTER_QTY"));
				}
			}
			for(Map<String, Object> e : inList) {
				if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
				   m.get("LIFNR").toString().equals(e.get("LIFNR").toString())&&
				   m.get("PROJECT_CODE").toString().equals(e.get("PROJECT_CODE").toString())&&
				   m.get("DAYS_CODE").toString().equals(e.get("DAYS_CODE").toString())) {
						m.put("IN_QTY", e.get("IN_QTY"));
				}
			}
			//TODO
			//m.put("RETURN_QTY", 1);//退货数量
		}
		engineMaterialDao.mergeHistory(batchpickList);
		List<Map<String, Object>> stockList = engineMaterialDao.getMaterialStock(matList);
		engineMaterialDao.updateStock(stockList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveStock() {
		List<Map<String, Object>> matList = wmsCEngineDao.queryAll(new HashMap<>());
		List<Map<String, Object>> stockList = engineMaterialDao.getMaterialStock(matList);
		engineMaterialDao.mergeStock(stockList);
	}

	@Override
	public List<Map<String, Object>> list(Map<String, Object> params) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.getTime().getDate();
		params.put("DAYS_CODE", day);
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		queryList = engineMaterialDao.queryByDay(params);
		if(queryList==null||queryList.size()==0) 
			queryList = engineMaterialDao.queryMat(params);
		if (day == 1) {
			rtnList.addAll(queryList);
			rtnList.forEach(m->m.put("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())));	
			List<Map<String, Object>> stockList = engineMaterialDao.getMaterialStock(rtnList);			
			for(Map<String, Object> m : rtnList) {
				m.put("SINGLE", m.get("SINGLE_QTY"));
				for(Map<String, Object> e : stockList) {
					if( m.get("WH_NUMBER").toString().equals(e.get("WH_NUMBER").toString())&&
							m.get("WERKS").toString().equals(e.get("WERKS").toString())	&&
							m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
							m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
							m.get("PROJECT_CODE").toString().equals(e.get("PROJECT_CODE").toString())&&
						    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("STOCKQTY", e.get("STOCK_QTY"));
						m.put("VIRTUALQTY", Integer.valueOf(e.get("STOCK_QTY").toString())/Integer.valueOf(m.get("SINGLE_QTY").toString()));
						m.put("FREEZEQTY", e.get("FREEZE_QTY"));
						m.put("SURPLUS", m.get("LAST_MONTH_QTY"));
					}
				}
			}
			List<Map<String, Object>> batchpickList = engineMaterialDao.queryBatchpick(rtnList);
			List<Map<String, Object>> bitpickList = engineMaterialDao.queryBitpick(rtnList);
			List<Map<String, Object>> afterList = engineMaterialDao.queryAftersale(rtnList);
			List<Map<String, Object>> inList = engineMaterialDao.queryIn(rtnList);
			List<Map<String, Object>> stockList1 = engineMaterialDao.getMaterialStock(rtnList);
			for(Map<String, Object> m : rtnList) {
				for(Map<String, Object> e : batchpickList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("BATCHPICK" + day, e.get("BATCH_QTY"));
					}
				}
				for(Map<String, Object> e : bitpickList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("BITPICK" + day, e.get("SCATTER_QTY"));
					}
				}
				for(Map<String, Object> e : afterList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("AFTERSALE" + day, e.get("AFTER_QTY"));
					}
				}
				for(Map<String, Object> e : inList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("IN" + day, e.get("IN_QTY"));
					}
				}
				for(Map<String, Object> e : stockList1) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("QTY" + day, e.get("STOCK_QTY"));
						m.put("FREEZE" + day, e.get("FREEZE_QTY"));
					}
				}
				//TODO
				m.put("RETURN" + day, 1);
			}
		} else {
			Object o = null;
			for(Map<String, Object> m: queryList) {
				o = m.get("DAYS_CODE");
			}
			if(o!=null) {
				for (Integer i = 1; i < day; i++) {
					for (Map<String, Object> m : queryList) {
						if (i.toString().equals(m.get("DAYS_CODE").toString())) {
							Map<String, Object> temp = new HashMap<>();
							temp.put("WH_NUMBER", m.get("WH_NUMBER"));
							temp.put("WERKS", m.get("WERKS"));
							temp.put("MATNR", m.get("MATNR"));
							temp.put("MAKTX", m.get("MAKTX"));
							temp.put("LGORT", m.get("LGORT"));
							temp.put("PROJECT_CODE", m.get("PROJECT_CODE"));
							temp.put("LIFNR", m.get("LIFNR"));
							temp.put("SINGLE", m.get("SINGLE_QTY"));
							temp.put("SURPLUS", m.get("LAST_MONTH_QTY"));
							temp.put("PERSON", m.get("PERSON"));
							temp.put("PERSON", m.get("PERSON"));
							temp.put("date", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
							
							temp.put("BATCHPICK" + i, m.get("BATCH_QTY"));
							temp.put("BITPICK" + i, m.get("SCATTER_QTY"));
							temp.put("RETURN" + i, m.get("RETURN_QTY"));
							temp.put("AFTERSALE" + i, m.get("AFTER_QTY"));
							temp.put("FREEZE" + i, m.get("FREEZE_QTY"));
							temp.put("IN" + i, m.get("IN_QTY"));
							temp.put("QTY" + i, m.get("DAYS_QTY"));
							tempList.add(temp);
						}
					}
				}   
				//合计
				for (Map<String, Object> m : tempList) {
					boolean flag = true;
					for(Map<String, Object> ele : rtnList) {
						if( m.get("WH_NUMBER").toString().equals(ele.get("WH_NUMBER").toString())&&
							m.get("WERKS").toString().equals(ele.get("WERKS").toString())	&&
							m.get("MATNR").toString().equals(ele.get("MATNR").toString())&&
							m.get("LGORT").toString().equals(ele.get("LGORT").toString())&&
							m.get("PROJECT_CODE").toString().equals(ele.get("PROJECT_CODE").toString())&&
						    m.get("LIFNR").toString().equals(ele.get("LIFNR").toString())) {
							ele.putAll(m);
							flag = false;
							break;
						}else 
							flag = true;
					}
					if(flag)
						rtnList.add(m);
				}
			}
			if(rtnList==null||rtnList.size()==0) {
				rtnList = queryList;
				rtnList.forEach(m->{
					m.put("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
					m.put("SINGLE", m.get("SINGLE_QTY"));
				});	
			}
			List<Map<String, Object>> stockList = new ArrayList<>();
			for(Map<String, Object> m : rtnList) {
				if(m.get("MATNR")!=null) {
					stockList = engineMaterialDao.getMaterialStock(rtnList);
					break;
				}
			}
			for(Map<String, Object> m : rtnList) {
				for(Map<String, Object> e : stockList) {
					if( m.get("WH_NUMBER").toString().equals(e.get("WH_NUMBER").toString())&&
							m.get("WERKS").toString().equals(e.get("WERKS").toString())	&&
							m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
							m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
						    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("STOCKQTY", e.get("STOCK_QTY"));
						m.put("VIRTUALQTY", Integer.valueOf(e.get("STOCK_QTY").toString())/Integer.valueOf(m.get("SINGLE").toString()));
						m.put("FREEZEQTY", e.get("FREEZE_QTY"));
					}
				}
			}
			List<Map<String, Object>> batchpickList = new ArrayList<>();
			List<Map<String, Object>> bitpickList = new ArrayList<>();
			List<Map<String, Object>> afterList = new ArrayList<>();
			List<Map<String, Object>> inList = new ArrayList<>();
			List<Map<String, Object>> stockList1 = new ArrayList<>();
			if(rtnList!=null&&rtnList.size()>0) {
				 batchpickList = engineMaterialDao.queryBatchpick(rtnList);
				 bitpickList = engineMaterialDao.queryBitpick(rtnList);
				 afterList = engineMaterialDao.queryAftersale(rtnList);
				 inList = engineMaterialDao.queryIn(rtnList);
				 stockList1 = engineMaterialDao.getMaterialStock(rtnList);
			}
			for(Map<String, Object> m : rtnList) {
				for(Map<String, Object> e : batchpickList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("BATCHPICK" + day, e.get("BATCH_QTY"));
					}
				}
				for(Map<String, Object> e : bitpickList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("BITPICK" + day, e.get("SCATTER_QTY"));
					}
				}
				for(Map<String, Object> e : afterList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("AFTERSALE" + day, e.get("AFTER_QTY"));
					}
				}
				for(Map<String, Object> e : inList) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("IN" + day, e.get("IN_QTY"));
					}
				}
				for(Map<String, Object> e : stockList1) {
					if(m.get("MATNR").toString().equals(e.get("MATNR").toString())&&
					m.get("LGORT").toString().equals(e.get("LGORT").toString())&&
				    m.get("LIFNR").toString().equals(e.get("LIFNR").toString())) {
						m.put("QTY" + day, e.get("STOCK_QTY"));
						m.put("FREEZE" + day, e.get("FREEZE_QTY"));
					}
				}
				//TODO
				//m.put("RETURN" + day, 1);//退货数量
			}
		}
		return rtnList;
	}
}
