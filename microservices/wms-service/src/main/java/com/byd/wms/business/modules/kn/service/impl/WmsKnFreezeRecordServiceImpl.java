package com.byd.wms.business.modules.kn.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.config.dao.WmsCPlantDao;
import com.byd.wms.business.modules.config.dao.WmsCWhDao;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.kn.dao.WmsKnFreezeRecordDao;
import com.byd.wms.business.modules.kn.dao.WmsKnStorageMoveDao;
import com.byd.wms.business.modules.kn.entity.WmsKnFreezeRecordEntity;
import com.byd.wms.business.modules.kn.service.WmsKnFreezeRecordService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service("wmsKnFreezeRecordService")
public class WmsKnFreezeRecordServiceImpl extends ServiceImpl<WmsKnFreezeRecordDao, WmsKnFreezeRecordEntity> implements WmsKnFreezeRecordService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WmsKnFreezeRecordDao wmsKnFreezeRecordDao;
	@Autowired
	private WmsKnStorageMoveDao wmsKnStorageMoveDao;
	@Autowired
	private WarehouseTasksService warehouseTasksService;
	
	@Autowired
	private WmsCWhDao wmsCWhDao;
	
	@Autowired
	private CommonService commonService;
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsKnFreezeRecordDao.getStockInfoList(params);
		int count=wmsKnFreezeRecordDao.getStockInfoCount(params);
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
        	params.put("start", start);params.put("end", end);
        	list=wmsKnFreezeRecordDao.getStockInfoList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
    }
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean freeze(Map<String, Object> params) {
		boolean rersult=false;
		List<Map<String,Object>> labelStatusList=new ArrayList<Map<String,Object>>();
		// freezeRecord操作类型【00：冻结；01：解冻】
    	String freezeType=params.get("freezeType").toString();
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		//判断AUTO_PUTAWAY_FLAG =0，则产生上架任务  ，否则不产生上架任务，
		List<Map<String,Object>> whtakslist=new ArrayList<Map<String,Object>>();
		// 1. 通过储位关联存储区域  查找AUTO_PUTAWAY_FLAG 是否自动上架标示 【0：否；X：是】
		String whNumber=params.get("whNumber").toString();
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("WH_NUMBER", whNumber);
		param.put("BINCODES", list);
		List<Map<String,Object>> autoPutAwayList=wmsKnStorageMoveDao.getAutoPutawayFlagList(param);
		
		for(Map<String,Object> k : list) {
			String binCode=k.get("BIN_CODE").toString();
			String targetBinCode=k.get("TARGET_BIN_CODE")!=null ? k.get("TARGET_BIN_CODE").toString() : "";
			k.put("FREEZE_TYPE",freezeType);
			k.put("EDITOR",params.get("USERNAME"));
	    	k.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		    if(k.get("BARCODE_FLAG")!=null && "X".equals(k.get("BARCODE_FLAG").toString())) {
		    	labelStatusList.add(k);
		    }
		    
		    for(Map<String,Object> autoPutAway : autoPutAwayList) {
				String baseBinCode=autoPutAway.get("BIN_CODE").toString();
				String autoPutAwayFlag=autoPutAway.get("AUTO_PUTAWAY_FLAG").toString();
				String toStorageArea=autoPutAway.get("STORAGE_AREA_CODE").toString();
				// 1、目标储位与源储位不同 （相同就不产生上架任务）；2、手动上架 AUTO_PUTAWAY_FLAG =0   需产生上架任务
				if(!binCode.equals(targetBinCode)&& baseBinCode.equals(targetBinCode) && autoPutAwayFlag.equals("0")) {
			    	k.put("FROM_BIN_CODE", k.get("BIN_CODE"));
			    	k.put("TO_BIN_CODE", k.get("TARGET_BIN_CODE"));
			    	k.put("TO_STORAGE_AREA", toStorageArea);
			    	k.put("UNIT", k.get("MEINS"));
			    	k.put("PROCESS_TYPE", "00");//上架
			    	k.put("DEL", "0");
			    	whtakslist.add(k);
			    }
			}	   
		}	
		// 启用标签管理，更新标签表WMS_CORE_LABEL的LABEL_STATUS【冻结：11已冻结；解冻：08已上架】
		if(labelStatusList.size()>0) {
			wmsKnFreezeRecordDao.batchUpdateCoreLabelStatus(labelStatusList);
		}
		// 更新库存记录(冻结：非限制库存stock_qty减少，冻结数量freeze_qty增加；解冻反之)
		int upateCount=wmsKnFreezeRecordDao.batchUpdateStock(list);
		// 插入冻结记录表
		int insertCount=wmsKnFreezeRecordDao.saveFreezeRecord(list);
		if(insertCount>0) {
			rersult=true;
		}
	    //  手动上架  生上架任务
		if(whtakslist.size()>0) {
			warehouseTasksService.saveWHTask(whtakslist);
		}
		return rersult;
	}
	@Override
	public PageUtils queryFreezeRecordPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
        String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
        String whNumber = params.get("whNumber") == null?null:String.valueOf(params.get("whNumber"));
        String lifnr = params.get("lifnr") == null?null:String.valueOf(params.get("lifnr"));
        String freezeType = params.get("freezeType") == null?null:String.valueOf(params.get("freezeType"));
        String batch = params.get("batch") == null?null:String.valueOf(params.get("batch"));

        Page<WmsKnFreezeRecordEntity> page = this.selectPage(new Query<WmsKnFreezeRecordEntity>(params).getPage(),
				new EntityWrapper<WmsKnFreezeRecordEntity>()
				.eq(StringUtils.isNotEmpty(werks),"WERKS", werks)
				.eq(StringUtils.isNotEmpty(matnr),"MATNR", matnr)
				.eq(StringUtils.isNotEmpty(whNumber),"WH_NUMBER", whNumber)
				.eq(StringUtils.isNotEmpty(lifnr),"LIFNR", lifnr)
				.eq(StringUtils.isNotEmpty(freezeType),"FREEZE_TYPE", freezeType)
				.eq(StringUtils.isNotEmpty(batch),"BATCH", batch)
		);
        
		return new PageUtils(page);
	}
	@Override
	public void createFreezeJobs(String param) {
		
	}
	@Override
	public List<Map<String,Object>> getDataByLabelNo(String labelNo) {
		return wmsKnFreezeRecordDao.getDataByLabelNo(labelNo);
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean appFreeze(Map<String, Object> params) {
		boolean rersult=false;
		List<Map<String,Object>> stockList=new ArrayList<Map<String,Object>>();
		// freezeRecord操作类型【00：冻结；01：解冻】
    	String freezeType=params.get("type").toString();
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		list.forEach(k->{
			k=(Map<String,Object>)k;	
			k.put("FREEZE_TYPE",freezeType);
			//k.put("EDITOR",params.get("USERNAME"));
	    	k.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		});	
		// 不同标签号 同工厂、仓库号、物料号、批次、库位、储位、供应商、特殊库存类型数量求和，用于更新库存表、插入冻结记录表
		Map<String, List<Map<String,Object>>> groupByMap =
		 list.stream().collect(Collectors.groupingBy(o ->o.get("WERKS")+ "_" +o.get("WH_NUMBER")
		 + "_" +o.get("MATNR")+ "_" +o.get("BATCH")+ "_" +o.get("LGORT")+ "_" +o.get("BIN_CODE")
		 + "_" +o.get("LIFNR")+ "_" +o.get("SOBKZ")));
		Iterator entries = groupByMap.entrySet().iterator(); 
		while (entries.hasNext()) { 
		  Map.Entry entry = (Map.Entry) entries.next(); 
		  List<Map<String,Object>> value = (List<Map<String,Object>>)entry.getValue(); 
		  Double totalFreezeQty = value.stream().mapToDouble(m->Double.parseDouble(m.get("BOX_QTY").toString())).sum();
		  Map<String,Object> stockMap=value.get(0);
		  stockMap.put("QUANTITY", totalFreezeQty);
		  stockMap.put("FREEZE_TYPE",freezeType);
		 // stockMap.put("EDITOR",params.get("USERNAME"));
		  stockMap.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		  stockList.add(stockMap);
		}
		
		// 启用标签管理，更新标签表WMS_CORE_LABEL的LABEL_STATUS【冻结：11已冻结；解冻：08已上架】
	    wmsKnFreezeRecordDao.batchUpdateCoreLabelStatus(list);
		// 更新库存记录(冻结：非限制库存stock_qty减少，冻结数量freeze_qty增加；解冻反之)
		int upateCount=wmsKnFreezeRecordDao.batchUpdateStock(stockList);
		// 插入冻结记录表
		int insertCount=wmsKnFreezeRecordDao.saveFreezeRecord(stockList);
		if(insertCount>0) {
			rersult=true;
		}
		return rersult;
	}
	//过期冻结
	@Override
	public void freezeMatList() {
		System.out.println("方法执行");
		//查询过期的库存物料库存信息  
		String date=DateUtils.format(new Date(), DateUtils.DATE_PATTERN);
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("EFFECT_DATE", date);
		List<Map<String,Object>> matInfolist =wmsKnFreezeRecordDao.getMatInfoList(param);
		List<Map<String,Object>> freezeRecordList=new ArrayList<Map<String,Object>>();//冻结使用的物料信息
		 
		String WMS_NO="";
		String SAP_NO="";
		String BUSINESS_NAME="81";
		String BUSINESS_TYPE="17";
		String BUSINESS_CLASS="10";
		List<String> werksList=new ArrayList<String>();
		Map<String,Object> wmsDocMatMapList= new HashMap<String,Object>();
		//List<Map<String,Object>> wmsDocMatList = new ArrayList<Map<String,Object>>();
	    
		if(matInfolist.size()>0) {
			for(Map<String,Object> k : matInfolist) {				
				k.put("FREEZE_TYPE","00");
				k.put("EDITOR","");
		    	k.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));				    
			    k.put("EFFECT_DATE", "");
			    k.put("REASON_CODE", "");
			    k.put("REASON", "批次过期");
			    freezeRecordList.add(k);
			    
			    //是否启用SAP过账
			    String werks=k.get("WERKS").toString();
			    String wh_number=k.get("WH_NUMBER").toString();
			    Map<String,Object> paramInfo = new HashMap<String,Object>();
				paramInfo.put("WERKS",werks);
				paramInfo.put("WH_NUMBER", wh_number);
				Map<String,Object> result=wmsCWhDao.getWmsCWh(paramInfo);
				String freeze =result.get("FREEZEPOSTSAPFLAG").toString();
				if(freeze.equals("X")) {
					List<Map<String,Object>> wmsDocMatList = new ArrayList<Map<String,Object>>();
				    if(werksList.contains(werks)) {
				    	
				    	wmsDocMatList=(List<Map<String, Object>>) wmsDocMatMapList.get(werks);
				    }else {
				    	werksList.add(werks);
				    }
				    //SAP物料清单
				    Map<String,Object> wmsDocMatMap = new HashMap<String,Object>();
				    wmsDocMatMap.put("BUSINESS_NAME", BUSINESS_NAME);
				    wmsDocMatMap.put("BUSINESS_TYPE", BUSINESS_TYPE);
				    wmsDocMatMap.put("BUSINESS_CLASS", BUSINESS_CLASS);			    
				    wmsDocMatMap.put("WERKS", k.get("WERKS"));
				    wmsDocMatMap.put("MATNR", k.get("MATNR"));
				    wmsDocMatMap.put("LGORT", k.get("LGORT"));
				    String sobkz=k.get("SOBKZ").toString();
				    if(sobkz.equals("K")) {
				    	wmsDocMatMap.put("LIFNR", k.get("LIFNR"));
				    	wmsDocMatMap.put("WMS_MOVE_TYPE","344_K");
					    wmsDocMatMap.put("SAP_MOVE_TYPE","344_K");
				    }else if(sobkz.equals("E")) {
				    	wmsDocMatMap.put("SO_NO", k.get("SO_NO"));
				    	wmsDocMatMap.put("SO_ITEM_NO", k.get("SO_ITEM_NO"));
				    	wmsDocMatMap.put("WMS_MOVE_TYPE","344_E");
					    wmsDocMatMap.put("SAP_MOVE_TYPE","344_E");
				    }
				    wmsDocMatMap.put("SOBKZ", k.get("SOBKZ"));
				    wmsDocMatMap.put("QTY_WMS", k.get("QUANTITY"));
					wmsDocMatMap.put("QTY_SAP", k.get("QUANTITY"));
				    wmsDocMatMap.put("UNIT", k.get("MEINS"));
				    wmsDocMatList.add(wmsDocMatMap);
				   
				    wmsDocMatMapList.put(werks, wmsDocMatList);
				}
			  
			}
			// 启用标签管理，更新标签表WMS_CORE_LABEL的LABEL_STATUS【冻结：11已冻结；解冻：07已进仓】
			wmsKnFreezeRecordDao.batchUpdateCoreLabelStatus(freezeRecordList);
			
			// 更新库存记录(冻结：非限制库存stock_qty减少，冻结数量freeze_qty增加；解冻反之)
			int updateCountB=wmsKnFreezeRecordDao.batchUpdateStock(freezeRecordList);
			
			// 插入冻结记录表
			int insertCount=wmsKnFreezeRecordDao.saveFreezeRecord(freezeRecordList);
			
			// 启用 SAP过账 生成WMS凭证号 和SAP凭证号
			if(werksList!=null&&werksList.size()>0){				
				Map<String,Object> head=new HashMap<String,Object>();
				head.put("PZ_DATE", date);
				head.put("JZ_DATE", date);
				head.put("PZ_YEAR", date.substring(0,4));
				head.put("HEADER_TXT", "");
				head.put("TYPE",  "00");//标准凭证
				head.put("CREATOR", "");
				head.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));	
				for(String werks:werksList) {
					List<Map<String,Object>> MatList =(List<Map<String, Object>>) wmsDocMatMapList.get(werks);
					String WMS_NO_temp=commonService.saveWMSDoc(head, MatList);
					WMS_NO=WMS_NO+" "+WMS_NO_temp;
					head.put("matList", MatList);
					head.put("WERKS", werks);
					head.put("BUSINESS_NAME", BUSINESS_NAME);
					head.put("BUSINESS_TYPE", BUSINESS_TYPE);
					head.put("BUSINESS_CLASS", BUSINESS_CLASS);
					String SAP_NO_temp=commonService.doSapPost(head);
					SAP_NO=SAP_NO+" "+SAP_NO_temp;
				}								
			}
			
		}
		System.out.println("冻结数据:"+matInfolist.size()+";WMS_NO:"+WMS_NO+";SAP_NO:"+SAP_NO);
	}

}
