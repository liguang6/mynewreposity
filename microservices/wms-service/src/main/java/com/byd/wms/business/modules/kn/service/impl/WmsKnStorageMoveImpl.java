package com.byd.wms.business.modules.kn.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.config.dao.WmsCWhDao;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.kn.dao.WmsKnStorageMoveDao;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;
import com.byd.wms.business.modules.kn.service.WmsKnStorageMoveService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
@Service("wmsKnStorageMoveService")
public class WmsKnStorageMoveImpl extends ServiceImpl<WmsKnStorageMoveDao, WmsKnStorageMoveEntity> implements WmsKnStorageMoveService {
	@Autowired
	private WmsKnStorageMoveDao wmsKnStorageMoveDao;
	@Autowired
	private WarehouseTasksService warehouseTasksService;
	@Override
	public List<Map<String,Object>> queryStock(Map<String, Object> params) {
		return wmsKnStorageMoveDao.getStockInfoList(params);
	}

	@Override
	@Transactional
	public void save(List<Map<String, Object>> list) {
		List<Map<String, Object>> stockList=new ArrayList<Map<String, Object>>();
		
		//判断AUTO_PUTAWAY_FLAG =0，则产生上架任务  ，否则不产生上架任务，
		List<Map<String,Object>> whtakslist=new ArrayList<Map<String,Object>>();
		// 1. 通过储位关联存储区域  查找AUTO_PUTAWAY_FLAG
		String whNumber=list.get(0).get("WH_NUMBER").toString();
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("WH_NUMBER", whNumber);
		param.put("BINCODES", list);
		List<Map<String,Object>> autoPutAwayList=wmsKnStorageMoveDao.getAutoPutawayFlagList(param);
		
		for(Map<String, Object> map : list) {
			String targetBinCode=map.get("TARGET_BIN_CODE").toString();
			
			for(Map<String,Object> autoPutAway : autoPutAwayList) {
				String binCode=autoPutAway.get("BIN_CODE").toString();
				String autoPutAwayFlag=autoPutAway.get("AUTO_PUTAWAY_FLAG").toString();
				String toStorageArea=autoPutAway.get("STORAGE_AREA_CODE").toString();
				// 手动上架 AUTO_PUTAWAY_FLAG =0
				if(binCode.equals(targetBinCode) && autoPutAwayFlag.equals("0")) {
					BigDecimal qty= new BigDecimal(map.get("QTY").toString());
					BigDecimal moveQty= new BigDecimal(map.get("STOCK_QTY").toString());
					int taskQty=wmsKnStorageMoveDao.getWhTaskList(map);
					if(moveQty.add(new BigDecimal(taskQty)).compareTo(qty)==1) {
						throw new RuntimeException(map.get("MATNR")+":移储数量+任务表状态为00或者01（数量-确认数量）不能大于库存数量");
					}
					map.put("QUANTITY", map.get("STOCK_QTY"));
			    	map.put("FROM_BIN_CODE", map.get("BIN_CODE"));
			    	map.put("TO_BIN_CODE", map.get("TARGET_BIN_CODE"));
			    	map.put("TO_STORAGE_AREA", toStorageArea);
			    	map.put("UNIT", map.get("MEINS"));
			    	map.put("PROCESS_TYPE", "00");//上架
			    	map.put("DEL", "0");
			    	whtakslist.add(map);
			    }
				// 自动上架 AUTO_PUTAWAY_FLAG =X
				if(binCode.equals(targetBinCode) && autoPutAwayFlag.equals("X")) {
					stockList.add(map);
				}
			}
		}
		//  自动上架 
		//1、新增移储记录；
		//2、更新源储位库存；
		//3、插入或更新目标储位库存；
		//4、启用条码管理【BARCODE_FLAG=‘X’】的仓库 更新WMS_CORE_LABEL、 WMS_CORE_STOCK_LABEL的储位字段
		if(stockList.size()>0) {
			// 保存移储记录
			wmsKnStorageMoveDao.saveStorageMove(list);
			// 更新源储位库存
			wmsKnStorageMoveDao.updateWmsStock(list);
			// 插入或更新目标储位库存
			wmsKnStorageMoveDao.mergeWmsStock(list);
			//启用条码管理【BARCODE_FLAG=‘X’】的仓库 更新WMS_CORE_LABEL、 WMS_CORE_STOCK_LABEL的储位字段
			String barcodeFlag=list.get(0).get("BARCODE_FLAG")!=null ?
					list.get(0).get("BARCODE_FLAG").toString() : "";
			if(barcodeFlag.equals("X")) {
				List<Map<String,Object>> coreStockLabelList=new ArrayList<Map<String,Object>>();
				for(Map<String, Object> map : stockList) {
				   List<Map<String,Object>> coreLabelList=wmsKnStorageMoveDao.getCoreLabelList(map);
				   for(Map<String,Object> coreLabelMap : coreLabelList) {
					   Map<String,Object> coreStockLabelMap=new HashMap<String,Object>();
					   coreStockLabelMap.put("LABEL_NO", coreLabelMap.get("LABEL_NO"));
					   coreStockLabelMap.put("BIN_CODE", map.get("TARGET_BIN_CODE"));
					   coreStockLabelMap.put("BIN_NAME", map.get("TARGET_BIN_NAME"));
					   coreStockLabelMap.put("EDITOR", map.get("EDITOR"));
					   coreStockLabelMap.put("EDIT_DATE", map.get("EDIT_DATE"));
					   coreStockLabelList.add(coreStockLabelMap);
				   }
				}
				if(coreStockLabelList.size()>0) {
					wmsKnStorageMoveDao.updateWmsCoreLabel(coreStockLabelList);
					wmsKnStorageMoveDao.updateWmsCoreStockLabel(coreStockLabelList);
				}
			}
		}
	   //  手动上架  只产生上架任务
		if(whtakslist.size()>0) {
			warehouseTasksService.saveWHTask(whtakslist);
		}
	}

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
		List<Map<String,Object>> list=wmsKnStorageMoveDao.getStorageMoveList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsKnStorageMoveDao.getStorageMoveCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
}
