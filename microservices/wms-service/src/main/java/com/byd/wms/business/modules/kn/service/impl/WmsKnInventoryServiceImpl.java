package com.byd.wms.business.modules.kn.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.dao.WmsCMatDangerDao;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
import com.byd.wms.business.modules.kn.dao.WmsKnFreezeRecordDao;
import com.byd.wms.business.modules.kn.dao.WmsKnInventoryDao;
import com.byd.wms.business.modules.kn.entity.WmsKnFreezeRecordEntity;
import com.byd.wms.business.modules.kn.service.WmsKnFreezeRecordService;
import com.byd.wms.business.modules.kn.service.WmsKnInventoryService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service("wmsKnInventoryService")
public class WmsKnInventoryServiceImpl implements WmsKnInventoryService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WmsKnInventoryDao wmsKnInventoryDao;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	
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
		List<Map<String,Object>> list=wmsKnInventoryDao.getInventoryList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsKnInventoryDao.getInventoryCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
	@Override
	public List<Map<String, Object>> queryInventoryItem(String inventoryNo) {
		return wmsKnInventoryDao.getInventoryItemList(inventoryNo);
	}
	@Override
	@Transactional
	public Map<String,Object> createInventoryTask(Map<String, Object> params) {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String LGORT_STR = params.get("LGORT") == null?"":params.get("LGORT").toString();
		if(params.get("WH_MANAGER")!=null && !params.get("WH_MANAGER").toString().equals("")) {
			String [] whManagerArray=params.get("WH_MANAGER").toString().split(",");
			params.put("WH_MANAGER", whManagerArray);
		}
		if(params.get("LGORT")!=null && !params.get("LGORT").toString().equals("")) {
			String [] lgortArray=params.get("LGORT").toString().split(",");
			params.put("LGORT", lgortArray);
		}
		// 盘点比例
		double proportion=Double.parseDouble(params.get("PROPORTION").toString());
		// 根据条件，从库存表【WMS_CORE_STOCK】获取用于盘点的库存数据
		List<Map<String,Object>> stockList=wmsKnInventoryDao.getInventoryStockList(params);
		if(stockList.size()==0) {
			resultMap.put("code", "1");
			resultMap.put("msg", "没有符合条件的可盘点库存数据");
			return resultMap;
		}
		// 根据盘点比例，从可盘点的库存表随机抽取数据保存到盘点表行项目明细【WMS_KN_INVENTORY_ITEM】
		List<Map<String,Object>> saveStockList=new ArrayList<Map<String,Object>>();
		Map<Integer,Integer> indexMap=new HashMap<Integer,Integer>();
		int inventoryItemCount=(int) Math.ceil(proportion*(stockList.size())/100);
		while(saveStockList.size()<inventoryItemCount) {
			Random rand = new Random();
			int index=rand.nextInt(stockList.size());
			if(!indexMap.containsKey(index)) {
				saveStockList.add(stockList.get(index));
				indexMap.put(index, index);
			}
		}
		// 获取盘点任务号
		String inventoryNo=wmsCDocNoService.getDocNo("","12");
		params.put("INVENTORY_NO", inventoryNo);
		params.put("CREATOR",params.get("USERNAME").toString());
		params.put("CREATE_DATE",DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
		// 保存盘点表抬头【WMS_KN_INVENTORY_HEAD】
		params.put("LGORT", LGORT_STR);
		int headCount=wmsKnInventoryDao.saveInventoryHead(params);
		// 封装盘点任务号、行项目号
		for(int i=0;i<saveStockList.size();i++) {
			Map<String,Object> map=saveStockList.get(i);
			map.put("INVENTORY_NO", inventoryNo);
			map.put("INVENTORY_ITEM_NO",(i+1)+"");
		}
		// 保存盘点表行项目明细【WMS_KN_INVENTORY_ITEM】
		int detailCount=wmsKnInventoryDao.saveInventoryItem(saveStockList);
		if(headCount>0 && detailCount>0) {
			resultMap.put("code", "0");
			resultMap.put("msg", "保存成功");
		}else {
			resultMap.put("code", "0");
			resultMap.put("msg", "保存盘点表出错，请联系管理员");
		}
		return resultMap;
	}
	@Override
	public List<Map<String, Object>> print(Map<String,Object> params) {
		String inventoryNo=params.get("inventoryNo").toString();
		String status=params.get("status").toString();

		// 当前盘点表状态为“已创建”状态，才需要更新为“已打印:01”
		if(status.equals("00")) {
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("STATUS", "01");
			param.put("INVENTORY_NO", inventoryNo);
			param.put("EDITOR",params.get("USERNAME").toString());
			param.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			wmsKnInventoryDao.updateStatus(param);
		}
        
		return null;
	}
	@Override
	public List<Map<String, Object>> getWhManagerList(Map<String, Object> param) {
		return wmsKnInventoryDao.getWhManagerList(param);
	}
	@Override
	public Map<String, Object> getInventoryHead(Map<String, Object> param) {
		return wmsKnInventoryDao.getInventoryHead(param);
	}
	@Override
	@Transactional
	// 录入盘点结果保存
	public void batchUpdateResult(Map<String, Object> paramMap) {
		// type【初盘(00);复盘(01);】
		String type=paramMap.get("TYPE").toString();
		String inventoryNo=paramMap.get("INVENTORY_NO").toString();
		Map<String,Object> param=new HashMap<String,Object>();
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) paramMap.get("SAVE_DATA"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		list.forEach(k->{
			k=(Map<String,Object>)k;	
			k.put("TYPE",type);
			k.put("PEOPLE",paramMap.get("USERNAME").toString());
			k.put("DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		});
		List<Long> ids=new ArrayList<Long>();
		for(Map<String, Object> object : list) {
			Long id=Long.parseLong(object.get("ID").toString().replace(".0", ""));
			ids.add(id);
		}
		param.put("IDS", ids);
		param.put("INVENTORY_NO",inventoryNo);
		// 查询盘点行项目的"初盘数量"or"复盘数量"字段是否已全部更新
		List<Map<String, Object>> otherItemList=wmsKnInventoryDao.getExistsItemList(param);
		// "初盘数量"、"复盘数量"都已录入完整;才更新状态值
		if(otherItemList.size()==0) {
			param.put("INVENTORY_NO", inventoryNo);
			param.put("EDITOR",paramMap.get("USERNAME").toString());
			param.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			// type=初盘(00)：更新盘点抬头表status为"已初盘:02"
			if(type.equals("00")) {
				param.put("STATUS", "02");
			}
			// type=复盘(01)：更新盘点抬头表status为"已复盘:03"
			if(type.equals("01")) {
				param.put("STATUS", "03");	
			}
			wmsKnInventoryDao.updateStatus(param);
		}
		// 更新盘点行项目表的"初盘数量"or"复盘数量"等字段
	    wmsKnInventoryDao.batchUpdateInventory(list);
		
	}
	@Override
	@Transactional
	// 导入盘点结果保存
	public void batchUpdateImp(Map<String, Object> paramMap) {
		// type【初盘(00);复盘(01)】
		String type=paramMap.get("TYPE").toString();
    	List<Long> ids=new ArrayList<Long>();
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) paramMap.get("SAVE_DATA"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		String inventoryNo=list.get(0).get("INVENTORY_NO").toString();
		List<Map<String,Object>> itemList=wmsKnInventoryDao.getInventoryItemList(inventoryNo);
		list.forEach(k->{
			k=(Map<String,Object>)k;
			k.put("TYPE", type);
			k.put("PEOPLE",paramMap.get("USERNAME").toString());
			k.put("DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		    for(Map<String,Object> item : itemList) {
		    	if(item.get("INVENTORY_ITEM_NO").toString()
		    			.equals(k.get("INVENTORY_ITEM_NO").toString())) {
		    		k.put("ID", item.get("ID"));
		    		ids.add(Long.parseLong(item.get("ID").toString()));
		    	}
		    }
		});
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("IDS", ids);
		param.put("INVENTORY_NO",inventoryNo);
		// 查询盘点行项目的"初盘数量"or"复盘数量"字段是否已全部更新
		List<Map<String, Object>> otherItemList=wmsKnInventoryDao.getExistsItemList(param);
		// 行项目表的"初盘数量"or"复盘数量"都已录入;才更新HEAD的STATUS状态值
		if(otherItemList.size()==0) {
			param.put("INVENTORY_NO", inventoryNo);
			param.put("EDITOR",paramMap.get("USERNAME").toString());
			param.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			// type=初盘(00)：更新盘点抬头表status为"已初盘:02"
			if(type.equals("00")) {
				param.put("STATUS", "02");
			}
			// type=复盘(01)：更新盘点抬头表status为"已复盘:03"
			if(type.equals("01")) {
				param.put("STATUS", "03");	
			}
			wmsKnInventoryDao.updateStatus(param);
		}
		// 更新盘点行项目表的"初盘数量"or"复盘数量"等字段
		wmsKnInventoryDao.batchUpdateInventory(list);
	}
	@Override
	@Transactional
	// 盘点结果确认保存
	public void batchUpdateConfirm(Map<String, Object> params) {
		Map<String,Object> param=new HashMap<String,Object>();
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) params.get("SAVE_DATA"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		for(Map<String,Object> k : list) {
			k=(Map<String,Object>)k;
			k.put("TYPE",params.get("TYPE"));
			k.put("PEOPLE",params.get("USERNAME").toString());
			k.put("DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		};
		List<Long> ids=new ArrayList<Long>();
		for(Map<String, Object> object : list) {
			Long id=Long.parseLong(object.get("ID").toString());
			ids.add(id);
		}
		param.put("IDS", ids);
		param.put("INVENTORY_NO", params.get("INVENTORY_NO"));
		// 查询盘点行项目的”差异原因“字段是否已全部更新
		List<Map<String, Object>> otherItemList=wmsKnInventoryDao.getExistsItemList(param);
		// "差异原因"都已更新;更新头表STATUS字段状态值
		if(otherItemList.size()==0) {
			param.put("INVENTORY_NO", params.get("INVENTORY_NO"));
			param.put("EDITOR",params.get("USERNAME").toString());
			param.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			// 更新盘点抬头表status为"已确认:04"
            param.put("STATUS", "04");	
			wmsKnInventoryDao.updateStatus(param);
		}
		//批量更新盘点行项目表【WMS_KN_INVENTORY_ITEM】的差异原因、确认人、确认时间"等字段
		wmsKnInventoryDao.batchUpdateInventory(list);
	}
	// 根据盘点任务号查询需进行盘点确认的数据行项目明细
	@Override
	public List<Map<String, Object>> queryInventoryConfirmItem(Map<String, Object> params) {
		String confirmor = params.get("USERNAME").toString();
		String inventoryNo = params.get("INVENTORY_NO").toString();
		List<Map<String, Object>> resultList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list=wmsKnInventoryDao.getInventoryItemList(inventoryNo);
		// 筛选出复盘数量不等于账面数量的记录
		for(Map<String, Object> map : list) {
			BigDecimal stockQty=(BigDecimal) map.get("STOCK_QTY");
			BigDecimal inventoryQtyRepeat= (BigDecimal) map.get("INVENTORY_QTY_REPEAT");
		    if(stockQty.compareTo(inventoryQtyRepeat)!=0) {
		    	map.put("CONFIRMOR", confirmor);
		    	map.put("DIF_QTY", inventoryQtyRepeat.subtract(stockQty));
		    	resultList.add(map);
		    }
		}
		return resultList;
	}
	@Override
	public List<Map<String, Object>> getInventoryConfirmList(Map<String, Object> param) {
		return wmsKnInventoryDao.getInventoryConfirmList(param);
	}
	
}
