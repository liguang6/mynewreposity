package com.byd.wms.business.modules.out.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.wms.business.modules.out.dao.WmsOutPickingDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.service.RequirementManageService;

@Service
public class RequirementManageServiceImpl implements RequirementManageService {

	@Autowired
	WmsOutRequirementHeadDao headDAO;
	
	@Autowired
	WmsOutRequirementItemDao itemDAO;
	
	@Autowired
	WmsOutPickingDao pickingDAO;

	@Autowired
	private UserUtils userUtils;
	
	
	@Override
	public PageUtils selectRequirementHeadList(Map<String, Object> params, Integer currentPage,Integer pageSize) {
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(currentPage,pageSize);
		System.err.println(params.toString());
		page.setRecords(headDAO.selectRequirementHeadList(page,params));
		return new PageUtils(page);
	}

	public PageUtils selectRequirementItemList(Map<String, Object> params, Integer currentPage,Integer pageSize) {
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(currentPage,pageSize);
		System.err.println("params "+params.toString());
		page.setRecords(headDAO.selectRequirementItemList(page, params));
		return new PageUtils(page);
	}


	@Override
    @Transactional(rollbackFor = Exception.class)
	public R closeRrequirement(Map<String, Object> params) {
		System.err.println("closeRrequirement " + params.toString());
		Map<String,Object> user = userUtils.getUser();
		JSONArray jsonArray = JSON.parseArray(params.get("params").toString());
		List<Map> LIST = JSONObject.parseArray(jsonArray.toJSONString(), Map.class);
		//更新WMS_OUT_REQUIREMENT_HEAD、WMS_OUT_REQUIREMENT_ITEM  中REQUIREMENT_STATUS=X 已删除；
		if (LIST == null || LIST.size() == 0) {
			return R.error("请勾选行项目！");
		}
		//获取修改人，修改时间
		//fixbug1589:需求关闭/删除 没有保存更新人和时间数据
		for(int i = 0;i < LIST.size();i++){
			LIST.get(i).put("EDITOR", user.get("USERNAME").toString());
			LIST.get(i).put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		}
		//删除
		if (LIST.get(0).get("REQUIREMENT_HEAD_STATUS").toString().equals("00")) {
			itemDAO.updateQxStatusByRequirementItem(LIST);
			//查询行表状态是否都是已删除状态
			Map keyMap = new HashMap();
			keyMap.put("REQUIREMENT_NO",LIST.get(0).get("REQUIREMENT_NO").toString());
			List<Map> lmap = itemDAO.queryStatusByRequirementItem(keyMap);
			boolean flag = true;
			for(int j = 0;j<lmap.size();j++){
				System.err.println(lmap.get(j).get("REQ_ITEM_STATUS").toString());
				if(!lmap.get(j).get("REQ_ITEM_STATUS").toString().equals("X")){
					flag = false;
				};
			}
			if(flag){
				itemDAO.updateQxStatusByRequirementHead(LIST);
			}
		} else { //关闭
			for(Map<String,Object> ITEM:LIST){
				//查询仓库任务表的状态
				Map map = new HashMap();
				map.put("REQUIREMENT_NO",ITEM.get("REQUIREMENT_NO").toString());
				map.put("REQUIREMENT_ITEM_NO",ITEM.get("REQUIREMENT_ITEM_NO").toString());
				Map resultMap = itemDAO.queryStatusByWhTask(map);
				/*if(resultMap==null || resultMap.size()==0){
					return R.error("行项目没有对应的仓库任务");
				}*/
				if(resultMap==null || resultMap.size()==0){

				}else {
					return R.error("请先取消仓库任务!");
				}
				//System.err.println("resultMap   "+resultMap.toString());
				//String WT_STATUS = (String) resultMap.get("WT_STATUS");
				/**
				 * 状态 状态00已创建 01 已审批 02 备料中 03 部分下架 04 已下架 05 部分交接 06 已交接 07 关闭
				 */
				/*if(ITEM.get("REQUIREMENT_HEAD_STATUS")!=null &&
						(ITEM.get("REQUIREMENT_HEAD_STATUS").equals("02")
								||ITEM.get("REQUIREMENT_HEAD_STATUS").equals("03")
								||ITEM.get("REQUIREMENT_HEAD_STATUS").equals("05"))){
					*//**
					 * 仓库任务状态 00：未清 01：部分确认 02：已确认 03：已取消 04：部分过账 05：已过账
					 *//*
					if(WT_STATUS!=null && (WT_STATUS.equals("00")
							|| WT_STATUS.equals("01") || WT_STATUS.equals("04"))){
						return R.error("请先取消仓库任务!");
					}
				}
				if(ITEM.get("REQUIREMENT_HEAD_STATUS")!=null &&
						(ITEM.get("REQUIREMENT_HEAD_STATUS").equals("04"))){
					*//**
					 * 仓库任务状态 00：未清 01：部分确认 02：已确认 03：已取消 04：部分过账 05：已过账
					 *//*
					if(WT_STATUS!=null && (WT_STATUS.equals("02")
					)){
						return R.error("请先取消仓库任务!");
					}
				}*/
			}
			//更新WMS_OUT_REQUIREMENT_HEAD、WMS_OUT_REQUIREMENT_ITEM  中REQUIREMENT_STATUS=07 已关闭；
			itemDAO.updateGbStatusByRequirementItem(LIST);
			//查询行表状态是否都是已关闭状态
			Map keyMap = new HashMap();
			keyMap.put("REQUIREMENT_NO",LIST.get(0).get("REQUIREMENT_NO").toString());
			List<Map> lmap = itemDAO.queryStatusByRequirementItem(keyMap);
			boolean flag = true;
			for(int j = 0;j<lmap.size();j++){
				System.err.println(lmap.get(j).get("REQ_ITEM_STATUS").toString());
				if(!lmap.get(j).get("REQ_ITEM_STATUS").toString().equals("06")){
					flag = false;
				};
			}
			//
			if(flag){
				itemDAO.updateGbStatusByRequirementHead(LIST);
			}
		}

		return R.ok();
	}
}
