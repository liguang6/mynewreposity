package com.byd.qms.modules.processQuality.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.processQuality.service.QmsProcessTestService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

@RestController
@RequestMapping("processQuality/test")
public class QmsProcessTestController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private QmsProcessTestService qmsProcessTestService;
	@Autowired
    private UserUtils userUtils;
	
	@CrossOrigin
	@RequestMapping("/getTestList")
	public R getProcessTestList(@RequestBody Map<String, Object> params) {
		List<Map<String,Object>> datalist=qmsProcessTestService.getProcessTestList(params);
		PageUtils page=new PageUtils(new Page().setRecords(datalist));
		return R.ok().put("page", page);		
	}
	
	@RequestMapping("/getFaultList")
	public R getFaultList(@RequestBody Map<String, Object> params) {
		List<Map<String,Object>> datalist=qmsProcessTestService.getFaultList(params);
		Set<String> faultTypeList = new TreeSet<String>();
		datalist.forEach(fault->{
			faultTypeList.add(fault.get("FAULT_TYPE").toString());
		});
		return R.ok().put("data", datalist).put("faultTypeList", faultTypeList);		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveTestRecord")
	public R saveTestRecord(@RequestBody Map<String, Object> params) {
		R r=null;
		Map<String,Object> _user = userUtils.getUser();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
		String CREATE_DATE=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		List<Map<String,Object>> recordList=new ArrayList<Map<String,Object>>();
		
		String recordListStr=params.get("recordList").toString();
		JSONObject.parseArray(recordListStr,Map.class).forEach(m->{
			m=(Map<String,Object>) m;
			m.put("CREATOR", _user.get("FULL_NAME"));
			m.put("CREATE_DATE", CREATE_DATE);		
			if("X".equals(m.get("UPDATE_FLAG"))) {
				m.put("EDITOR", _user.get("FULL_NAME"));
				m.put("EDIT_DATE", CREATE_DATE);		
			}
			
			if(m.get("ABNORMAL_ID")==null||"".equals(m.get("ABNORMAL_ID"))) {
				m.put("ABNORMAL_ID", 0);
			}
			recordList.add(m);
		});
		r=qmsProcessTestService.saveTestRecord(recordList);
		return r;
	}
	

	@RequestMapping("/saveAbnormalInfo")
	public R saveAbnormalInfo(@RequestBody Map<String, Object> params) {
		R r=null;
		Map<String,Object> _user = userUtils.getUser();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
		String CREATE_DATE=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		params.put("EDITOR",  _user.get("FULL_NAME"));
		params.put("EDIT_DATE", CREATE_DATE);
		
		r=qmsProcessTestService.saveAbnormalInfo(params);
		return r;
	}
	
	@RequestMapping("/delAbnormalInfo")
	public R delAbnormalInfo(@RequestParam(value="ABNORMAL_ID") String ABNORMAL_ID,@RequestParam(value="RECORD_ID") String RECORD_ID) {
		R r=qmsProcessTestService.delAbnormalInfo(ABNORMAL_ID,RECORD_ID);
		return r;
	}
	
	@RequestMapping("/getProcessTestRecordList")
	public R getProcessTestRecordList(@RequestBody Map<String, Object> params) {
		PageUtils page= qmsProcessTestService.getProcessTestRecordList(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/getProcessTestDetail")
	public R getProcessTestDetail(@RequestBody Map<String, Object> params) {
		PageUtils page= qmsProcessTestService.getProcessTestDetail(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/getPartsTestRecordList")
	public R getPartsTestRecordList(@RequestBody Map<String, Object> params) {
		PageUtils page= qmsProcessTestService.getPartsTestRecordList(params);
		return R.ok().put("page", page);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getProcessDpuData")
	public R getProcessDpuData(@RequestBody Map<String, Object> params) {
		logger.info("DPU报表数据查询："+JSONObject.toJSONString(params));
		try {
			String WEIDU=(String) params.get("WEIDU");
			String START_DATE= params.get("START_DATE")==null?"":params.get("START_DATE").toString();
			String END_DATE= params.get("END_DATE")==null?"":params.get("END_DATE").toString();
			List itemList=new ArrayList();//X轴数据列表
			if(WEIDU.equals("day")) {
				itemList=getDateList(START_DATE,END_DATE);
			}
			if(WEIDU.equals("week")) {
				itemList=getWeekList(START_DATE,END_DATE);
			}
			if(WEIDU.equals("month")) {
				itemList=getMonthList(START_DATE,END_DATE);
			}
			
			List<Map<String,Object>> test_record_list = new ArrayList<Map<String,Object>>();//表格数据
			List<Map<String,Object>> chart_list = new ArrayList<Map<String,Object>>();//图表数据获取 
			List<Map<String,Object>> chartList = new ArrayList<Map<String,Object>>();//图表数据
			for(Object item:itemList) {
				Map<String,Object> m=new HashMap<String,Object>();
				m.put("item", item+"");
				m.put("bus_num", "0");
				m.put("bug_num", "0");
				chartList.add(m);
			}
			
			test_record_list=qmsProcessTestService.getTestNGRecordList(params);
			
			chart_list=qmsProcessTestService.getDPUData(params);
			for(Map<String,Object> m:chart_list) {
				for(Map<String,Object>_m:chartList) {
					if(_m.get("item").toString().equals(m.get("item").toString())) {
						_m.put("bus_num", m.get("bus_num"));
						_m.put("bug_num", m.get("bug_num"));
					}
				}
			}
			if(WEIDU.equals("order")) {
				chartList=chart_list;
			}
			Page page=new Query<Map>(params).getPage();
			page.setRecords(test_record_list);
			page.setTotal(test_record_list.size());
			page.setSize(params.get("pageSize")==null?15: Integer.parseInt((String)params.get("pageSize")));

			return R.ok().put("itemList", itemList).put("chartList", chartList).put("page", new PageUtils(page));
			
		}catch(Exception e) {
			logger.error(e.getMessage());
			return R.error(e.getMessage());
		}
		

	}
	
	
	
	private List<String> getDateList(String sdate, String edate) throws ParseException {
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = df.parse(sdate);
		startCalendar.setTime(startDate);
		Date endDate = df.parse(edate);
		endCalendar.setTime(endDate);
		List<String> datelist=new ArrayList<String>();
		while (true) {
			if (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
				datelist.add(df.format(startCalendar.getTime()));
			} else {
				break;
			}
			startCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return datelist;
	}
	private List getWeekList(String sdate, String edate) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(df.parse(sdate));
		endCalendar.setTime(df.parse(edate));
		int s_week = startCalendar.get(Calendar.WEEK_OF_YEAR);
		int e_week = endCalendar.get(Calendar.WEEK_OF_YEAR);
		int weekcount=s_week;
		List weeklist=new ArrayList();
		while(weekcount<=e_week){
			weeklist.add(weekcount);
			weekcount+=1;
		}
		return weeklist; 
	}
	private List getMonthList(String sdate, String edate) throws ParseException{
		List monthlist=new ArrayList();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(df.parse(sdate));
		endCalendar.setTime(df.parse(edate));
		int s_month = startCalendar.get(Calendar.MONTH)+1;
		int e_month = endCalendar.get(Calendar.MONTH)+1;
		while(s_month<=e_month){
			monthlist.add(s_month);
			s_month+=1;
		}
		return monthlist;
	}
	
	@RequestMapping("/confirmProcessTest")
	public R confirmProcessTest(@RequestBody Map<String, Object> params) {
		R r=null;
		Map<String,Object> _user = userUtils.getUser();

		String CONFIRM_DATE=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		List<Map<String,Object>> recordList=new ArrayList<Map<String,Object>>();
		
		String recordListStr=params.get("recordList").toString();
		JSONObject.parseArray(recordListStr,Map.class).forEach(m->{
			m=(Map<String,Object>) m;
			m.put("CONFIRMOR", _user.get("FULL_NAME"));
			m.put("CONFIRM_DATE", CONFIRM_DATE);		
			recordList.add(m);
		});
		
		r=qmsProcessTestService.confirmProcessTest(recordList);
		return r;
	}
	
	@RequestMapping("/getProcessFTYData")
	public R getProcessFTYData(@RequestBody Map<String, Object> params) {
		R r =null;
		r=qmsProcessTestService.getProcessFTYData(params);
		return r;
	}
	
	@RequestMapping("/getUnProcessTestList")
	public R getUnProcessTestList(@RequestBody Map<String, Object> params) {
		List<Map<String,Object>> datalist=qmsProcessTestService.getUnProcessTestList(params);
		PageUtils page=new PageUtils(new Page().setRecords(datalist));
		return R.ok().put("page", page);		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveUnTestRecord")
	public R saveUnTestRecord(@RequestBody Map<String, Object> params) {
		R r=null;
		Map<String,Object> _user = userUtils.getUser();

		String TEST_DATE=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		List<Map<String,Object>> recordList=new ArrayList<Map<String,Object>>();
		
		String recordListStr=params.get("recordList").toString();
		String deleteIds = params.get("deleteIds").toString();
		
		JSONObject.parseArray(recordListStr,Map.class).forEach(m->{
			m=(Map<String,Object>) m;
			m.put("TESTOR", _user.get("FULL_NAME"));
			m.put("TEST_DATE", TEST_DATE);		
			recordList.add(m);
		});
		
		r=qmsProcessTestService.saveUnTestRecord(recordList,deleteIds);
		return r;
	}
	
	@RequestMapping("/getUnProcessReportData")
	public R getUnProcessReportData(@RequestBody Map<String, Object> params) {
		R r =null;
		r=qmsProcessTestService.getUnProcessReportData(params);
		return r;
	}
	
	@RequestMapping("/getBJFTYData")
	public R getBJFTYData(@RequestBody Map<String, Object> params) {
		R r =null;
		r=qmsProcessTestService.getBJFTYData(params);
		return r;
	}
	
	@RequestMapping("/getFaultScatterData")
	public R getFaultScatterData(@RequestBody Map<String, Object> params) {
		R r =null;
		r=qmsProcessTestService.getFaultScatterData(params);
		return r;
	}
	
	
}
