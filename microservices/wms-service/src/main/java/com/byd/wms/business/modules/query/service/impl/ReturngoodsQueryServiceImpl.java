package com.byd.wms.business.modules.query.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.query.dao.InboundQueryDao;
import com.byd.wms.business.modules.query.dao.LabelQueryDao;
import com.byd.wms.business.modules.query.dao.ReturngoodsQueryDao;
import com.byd.wms.business.modules.query.service.InboundQueryService;
import com.byd.wms.business.modules.query.service.LabelQueryService;
import com.byd.wms.business.modules.query.service.ReturngoodsQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 退货单
 * @author cscc tangj
 * @email 
 * @date 2018-11-30 09:23:18
 */
@Service("returngoodsQueryService")
public class ReturngoodsQueryServiceImpl implements ReturngoodsQueryService {
	@Autowired
    private ReturngoodsQueryDao returngoodsQueryDao;
    @Autowired
    private UserUtils userUtils;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=returngoodsQueryDao.getReceiveRoomOutCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=returngoodsQueryDao.getReceiveRoomOutList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
	@Override
	public List<Map<String, Object>> queryItemPage(Map<String,Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
        return returngoodsQueryDao.getReturngoodsItemList(params);
	}
	@Override
	public List<Map<String, Object>> queryReturnTypeList(String type) {
		return returngoodsQueryDao.getReturnTypeList(type);
	}
	@Override
	public List<Map<String, Object>> queryReturnDocTypeList() {
		return returngoodsQueryDao.getReturnDocTypeList();
	}
	@Override
	@Transactional
	public String close(Map<String, Object> params) {
		//6.2.4.2	仅退货单行项目状态=“00：创建和01：下架”状态的允许关闭，针对“00：创建”状态的退货单，点击“关闭”按钮，将退货单抬头表和退货单行项目表
		//		“DEL（删除标示）”字段更新为”X”;针对“01：下架”状态的退货单，将退货单抬头表和退货单行项目表“DEL（删除标示）”字段更新为”X”，同时按照以下逻辑将下架数据返回：
		//1）更新【WMS拣配下架表--- WMS_OUT_PICKING】，将取消下架的行项目数据打上删除标记（即：DEL字段写入X）。
		//2）更新【WMS库存表---WMS_CORE_STOCK】的“冻结数量（FREEZE_QTY）”（冻结数量增加）、“下架数量（XJ_QTY）”（下架数量减少），
		//		清空下架储位（下架储位由BBBB更新为空）。
		System.out.println("-->returngoodsQueryService close RETURN_NO : " + params.get("RETURN_NO"));
		int done_count = 0;
		Map<String,Object> currentUser = userUtils.getUser();
    	params.put("EDITOR",currentUser.get("USERNAME"));
    	params.put("EDIT_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		done_count = returngoodsQueryDao.getReturnItemDoneCount(params);
		System.out.println("-->done_count = " + done_count);
		if(done_count > 0) {
			throw new IllegalArgumentException("不能取消状态为已完成的退货单！");
		}
		// 更新抬头
		returngoodsQueryDao.updateHead(params);
		// 更新行项目
		returngoodsQueryDao.updateItem(params);
		returngoodsQueryDao.updateItemDetail(params);
		List<Map<String,Object>> returnDetailList = returngoodsQueryDao.getReturnDetailList(params);
		for(int i = 0 ;i<returnDetailList.size();i++) {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("MATNR", returnDetailList.get(i).get("MATNR"));
			param.put("BATCH", returnDetailList.get(i).get("BATCH"));
			param.put("LGORT", returnDetailList.get(i).get("LGORT"));
			param.put("LIFNR", returnDetailList.get(i).get("LIFNR"));
			param.put("SOBKZ", returnDetailList.get(i).get("SOBKZ"));
			param.put("XJ_QTY", returnDetailList.get(i).get("XJ_QTY"));

			List<Map<String,Object>> stockList = returngoodsQueryDao.getStockInfo(param);
			if(stockList.isEmpty()) {
				throw new IllegalArgumentException("没有找到对应的库存数据！");
			}
			if(stockList.size()>1) {
				throw new IllegalArgumentException("找到多条对应的库存数据！");
			}
			System.out.println(i + "xj_qty = " + stockList.get(0).get("XJ_QTY"));
			if(Float.valueOf(stockList.get(0).get("XJ_QTY").toString()) < Float.valueOf(returnDetailList.get(i).get("XJ_QTY").toString())) {
				throw new IllegalArgumentException("库存下架数量小于退货单下架数量！");
			}
			returngoodsQueryDao.updateStockInfo(param);
		}		
		return "success";
	}
	@Override
	@Transactional
	public boolean del(Map<String, Object> params) {
		boolean rersult=false;
    	params.put("EDITOR",params.get("USERNAME").toString());
    	params.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		
		// 更新抬头
		int headCount=returngoodsQueryDao.updateHead(params);
		// 更新行项目
		int itemCount=returngoodsQueryDao.updateItem(params);
		// updatePickingFlag:是否需要更新拣配表和库存表标示
		// ITEM_STATUS=01 && PICKING_FLAG=1 才需要更新
		boolean updatePickingFlag=false;
		for(Map<String,Object> object : list) {
			String itemStatus=object.get("ITEM_STATUS").toString();
			String pickingFlag=object.get("PICKING_FLAG").toString();
			if(itemStatus.equals("01") && pickingFlag.equals("1")) {
				updatePickingFlag=true;
				break;
			}
		}
		if(updatePickingFlag) {
			// 更新拣配表
			int pickingCount=returngoodsQueryDao.updateOutPicking(params);
			// 更新下架库存
			returngoodsQueryDao.batchUpdateStock(list);
		}
		return rersult;
		
	}

}
