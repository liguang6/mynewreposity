package com.byd.qms.modules.processQuality.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.processQuality.dao.QmsProcessTestDao;
import com.byd.qms.modules.processQuality.service.QmsProcessTestService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
@Service("qmsProcessTestService")
public class QmsProcessTestServiceImpl implements QmsProcessTestService {
	@Autowired
	private QmsProcessTestDao qmsProcessTestDao;
	@Override
	public List<Map<String, Object>> getProcessTestList(Map<String, Object> condMap) {
		List<Map<String,Object>> recordlist=qmsProcessTestDao.getTestRecordInList(condMap);
		if(recordlist==null||recordlist.size()==0) {
			recordlist=qmsProcessTestDao.getProcessTestTplList(condMap);
		}
		return recordlist;
	}
	@Override
	public List<Map<String, Object>> getFaultList(Map<String, Object> params) {
		List<Map<String,Object>> datalist=qmsProcessTestDao.getFaultList(params);
		return datalist;
	}
	
	@Override
	@Transactional
	public R saveTestRecord(List<Map<String, Object>> recordList) {
		R r=new R();		
		try {
			String RECORD_ID=recordList.get(0).get("RECORD_ID")==null?"":recordList.get(0).get("RECORD_ID").toString();
			List<Map<String,Object>> update_list = recordList.stream().filter(m->{
				return "X".equals(m.get("UPDATE_FLAG"));
			}).collect(Collectors.toList());
	
			//如果存在RECORD_ID：更新QMS_PROCESS_TEST_RECORD表记录
			if(!"".equals(RECORD_ID) && update_list!=null && update_list.size()>0) {
				qmsProcessTestDao.updateTestRecord(update_list);
				//qmsProcessTestDao.updateAbnormal(recordList);
			}else {  //不存在,查询QMS_PROCESS_TEST_RECORD是否记录存在，有：更新记录，无：插入记录		
				Map<String,Object> condMap = new HashMap<String,Object>();
				condMap=recordList.get(0);		
				List<Map<String, Object>> _list = qmsProcessTestDao.getTestRecordInList(condMap);
				if(_list!=null && _list.size()>0) {//表中存在记录，更新UPLDATE_FLAG 为X的记录
					if(update_list!=null && update_list.size()>0) {
						qmsProcessTestDao.updateTestRecord(update_list);
					}
					
				}else
					qmsProcessTestDao.insertTestRecord(recordList);	
				//qmsProcessTestDao.insertAbnormal(recordList);
			}
			r.ok("保存成功！");
		}catch(Exception e) {
			e.printStackTrace();
			 r.error("系统异常："+e.getMessage());
			throw new RuntimeException(e.getMessage());			
		}
		
		return r;
	
	}
	@Override
	@Transactional
	public R saveAbnormalInfo(Map<String, Object> params) {
		int abnormal_id=params.get("ABNORMAL_ID")==null?0:
			(params.get("ABNORMAL_ID").equals("")?0:Integer.parseInt(params.get("ABNORMAL_ID").toString()));
		int test_record_id=params.get("RECORD_ID")==null?0:
			(params.get("RECORD_ID").equals("")?0:Integer.parseInt(params.get("RECORD_ID").toString()));
		if(abnormal_id>0 ) {//存在记录：更新
			qmsProcessTestDao.updateAbnormal(params);
		}else {
			qmsProcessTestDao.insertAbnormal(params);
			abnormal_id=Integer.parseInt(params.get("id").toString());
		}
		if(test_record_id>0) {
			qmsProcessTestDao.updateTestAbnormal(abnormal_id, test_record_id);
		}
		return R.ok().put("ABNORMAL_ID", abnormal_id).put("ABNORMAL_INFO", params);
	}
	@Override
	@Transactional
	public R delAbnormalInfo(String abnormal_id,String record_id) {
		qmsProcessTestDao.deleteAbnormal(Integer.parseInt(abnormal_id));
		qmsProcessTestDao.updateTestAbnormal(0,Integer.parseInt(record_id));
		return R.ok().put("ABNORMAL_ID", 0);
	}
	@Override
	public PageUtils getProcessTestRecordList(Map<String, Object> params) {
		int totalCount = qmsProcessTestDao.getProcessTestRecordCount(params);		
		Page page=new Query<Map>(params).getPage();
		int pageSize = params.get("pageSize")==null?15: Integer.parseInt((String)params.get("pageSize"));
		int pageNo= params.get("pageNo")==null?1:Integer.parseInt((String)params.get("pageNo"));
		int start = (pageNo-1)*pageSize;
		int length = pageSize;
		params.put("start", start);
		params.put("length", length);
		
		List<Map<String, Object>> records=qmsProcessTestDao.getProcessTestRecordList(params);
		page.setRecords(records);
		page.setTotal(totalCount);
		page.setSize(pageSize);
		
		return new PageUtils(page);
	}
	@Override
	public PageUtils getProcessTestDetail(Map<String, Object> params) {
		List<Map<String,Object>> recordlist=qmsProcessTestDao.getTestRecordInList(params);
		PageUtils page=new PageUtils(new Page().setRecords(recordlist));
		return page;
	}
	@Override
	public PageUtils getPartsTestRecordList(Map<String, Object> params) {
		int totalCount =qmsProcessTestDao.getPartsTestRecordCount(params);
		Page page=new Query<Map>(params).getPage();
		int pageSize = params.get("pageSize")==null?15: Integer.parseInt((String)params.get("pageSize"));
		int pageNo= params.get("pageNo")==null?1:Integer.parseInt((String)params.get("pageNo"));
		int start = (pageNo-1)*pageSize;
		int length = pageSize;
		params.put("start", start);
		params.put("length", length);
		List<Map<String, Object>> records=qmsProcessTestDao.getPartsTestRecordList(params);
		page.setRecords(records);
		page.setTotal(totalCount);
		page.setSize(pageSize);
		
		return new PageUtils(page);
	}
	@Override
	public List<Map<String, Object>> getTestNGRecordList(Map<String, Object> params) {	
		return qmsProcessTestDao.getTestNGRecordList(params);
	}
	@Override
	public List<Map<String, Object>> getDPUData(Map<String, Object> params) {
		return qmsProcessTestDao.getDPUData(params);
	}
	@Override
	public R confirmProcessTest(List<Map<String, Object>> recordList) {
		try {
			qmsProcessTestDao.updateProcessTestConfirm(recordList);
			return R.ok();
		}catch(Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}	
		
	}
	@Override
	public R getProcessFTYData(Map<String, Object> params) {
		String[] test_node_list= params.get("TEST_NODE").toString().split(",");
		String[] werks_list =  params.get("WERKS").toString().split(",");
		params.put("TEST_NODE_LIST", test_node_list);
		params.put("WERKS_LIST", werks_list);
		
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		//getFTYData_Y 获取质检车辆一次交检合格统计数据
		List<Map<String,Object>> fty_list=qmsProcessTestDao.getFTYData(params);
		
		// 获取各检验节点目标值列表
		List<Map<String,Object>> target_val_list = qmsProcessTestDao.getNodesTargetVal(params);
		List<Double> targetValList = new ArrayList<Double>();
		for(String test_node:test_node_list) {
			Double d=0d;
			for(Map m : target_val_list) {
				if(m.get("TEST_NODE").equals(test_node)) {
					d=Double.valueOf(m.get("TARGET_VALUE").toString());
					break;
				}
			}
			targetValList.add(d);
		}
		
		/**
		 * 初始化数据 eg: [{WERKS:'C190',DATA:[1,1,1,1,1]}]
		 */
		for(String w:werks_list) {
			Map<String,Object> o=new HashMap<String,Object>();
			o.put("WERKS", w);
			List<Double> data = new ArrayList<Double>();
			for(String test_node:test_node_list) {
				Double d=null;
				for(Map<String,Object> f: fty_list) {
					if(o.get("WERKS").equals(f.get("WERKS")) && f.get("TEST_NODE").equals(test_node)) {
						d=Double.valueOf(f.get("RATE").toString());
						break;
					}
				}
				data.add(d);
			}
			o.put("DATA", data);
			dataList.add(o);
		}

		return R.ok().put("dataList", dataList).put("targetValList",targetValList);
	}
	
	@Override
	public List<Map<String, Object>> getUnProcessTestList(Map<String, Object> condMap) {
		List<Map<String,Object>> recordlist=qmsProcessTestDao.getUnProcessTestList(condMap);
		return recordlist;
	}
	@Override
	@Transactional
	public R saveUnTestRecord(List<Map<String, Object>> recordList, String deleteIds) {
		R r =new R();
		try {
			if(deleteIds!=null && deleteIds.length()>0) {
				qmsProcessTestDao.deleteUnTestRecord(Arrays.asList(deleteIds.split(",")));
			}
			recordList=recordList.stream().filter(m->"X".equals(m.get("UPDATE_FLAG"))).collect(Collectors.toList());
			if(recordList.size()>0) {
				qmsProcessTestDao.saveUnTestRecord(recordList);
			}
			
			r=R.ok();
		}catch(Exception e) {			
			r=R.error(e.getMessage());
			throw new RuntimeException(e);
		}
		
		return r;
	}
	@Override
	public R getUnProcessReportData(Map<String, Object> params) {
		Set<String> categories = new TreeSet<String>();
		String[] test_node_list= params.get("TEST_NODE").toString().split(",");
		params.put("TEST_NODE_LIST", test_node_list);
		List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
		/**
		 * 查询报表源数据
		 */
		List<Map<String,Object>> unList = qmsProcessTestDao.getUnProcessReportData(params);
		unList.stream().forEach(m->categories.add((String) m.get("ITEM")));
		
		/**
		 * 初始化数据 eg: [{ITEM:'C190',DATA:[1,1,1,1,1]}]
		 */
		for(String w:categories) {
			Map<String,Object> o=new HashMap<String,Object>();
			o.put("ITEM", w);
			List<Integer> data = new ArrayList<Integer>();
			for(String test_node:test_node_list) {
				Integer d=null;
				for(Map<String,Object> f: unList) {
					if(w.equals(f.get("ITEM")) && f.get("TEST_NODE").equals(test_node)) {
						d=Integer.parseInt(f.get("FAULT_NUM").toString());
						break;
					}
				}
				data.add(d);
			}
			o.put("DATA", data);
			dataList.add(o);
		}
		
		return R.ok().put("dataList", dataList).put("categories",categories);
	}
	@Override
	public R getBJFTYData(Map<String, Object> params) {
		String[] test_node_list= params.get("TEST_NODE").toString().split(",");
		String[] werks_list =  params.get("WERKS").toString().split(",");
		params.put("TEST_NODE_LIST", test_node_list);
		params.put("WERKS_LIST", werks_list);
		
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> fty_list = qmsProcessTestDao.getBJFtyData(params);
		
		List<String> X_LIST = null; 			//图表X轴数据列表		
		//工厂维度
		if("werks".equals(params.get("WEIDU"))) {
			X_LIST = Arrays.asList(werks_list);
		}
		//订单维度
		if("order".equals(params.get("WEIDU"))) {
			X_LIST = qmsProcessTestDao.getBJFTYOrderList(params);
		}
		Map<String,Object> series_total = new HashMap<String,Object>();
		List<Integer> data_total = new ArrayList<Integer>();
		series_total.put("ITEM", "交检数");
		series_total.put("DATA", data_total);
		dataList.add(series_total);
		Map<String,Object> series_ok = new HashMap<String,Object>();
		List<Integer> data_ok= new ArrayList<Integer>();
		series_ok.put("ITEM", "合格数");
		series_ok.put("DATA", data_ok);
		dataList.add(series_ok);
		Map<String,Object> series_rate = new HashMap<String,Object>();
		List<Double> data_rate = new ArrayList<Double>();
		series_rate.put("ITEM", "一次交检合格率");
		series_rate.put("DATA", data_rate);
		dataList.add(series_rate);
		
		for(String w:X_LIST) {			
			Integer bus_num = null;
			Integer bus_ok = null;
			Double rate = null;
			for(Map m : fty_list) {
				if(w.equals(m.get("ITEM"))) {
					bus_num = Integer.parseInt(m.get("BUS_NUM").toString());
					bus_ok =  Integer.parseInt(m.get("OK_NUM").toString());
					rate = Double.valueOf(m.get("RATE").toString());				
					break;
				}
			}
			data_total.add(bus_num);
			data_ok.add(bus_ok);
			data_rate.add(rate);
			
		}
		
		return R.ok().put("X_LIST", X_LIST).put("dataList", dataList);
	}
	
	@Override
	public R getFaultScatterData(Map<String, Object> params) {
		List<String> X_LIST = new ArrayList<String>(); 			//图表X轴数据列表		
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		BigDecimal total_count = new BigDecimal(0);
		dataList = qmsProcessTestDao.getFaultScatterData(params);
		for(Map m : dataList) {
			Integer fault_num = Integer.parseInt(m.get("FAULT_NUM").toString());
			total_count=total_count.add(BigDecimal.valueOf(fault_num));
			X_LIST.add(m.get("ITEM").toString());			
		}
		
		for(int i=0;i<dataList.size();i++) {
			Map<String,Object> m = dataList.get(i);
			Integer fault_num = Integer.parseInt(m.get("FAULT_NUM").toString());
			BigDecimal rate = BigDecimal.valueOf(fault_num).divide(total_count, 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
			m.put("RATE", rate);
			if(i>0) {
				m.put("TOTAL_RATE", rate.add( (BigDecimal) (dataList.get(i-1).get("TOTAL_RATE"))));
			} else 
				m.put("TOTAL_RATE", rate);
			
		}
		
		
		return R.ok().put("dataList", dataList).put("X_LIST", X_LIST);
	}
		
	
}
