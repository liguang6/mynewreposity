package com.byd.zzjmes.modules.produce.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
/**
 * 自制件品质检验
 * @author tangj
 * @email 
 * @date 2019-09-16 10:12:08
 */
public interface QmTestRecordService{
	
	public PageUtils queryPage(Map<String, Object> params);	
	// 导入保存
	void save(Map<String,Object> param);
	// 删除
	void del(Map<String,Object> param);
	// 查询订单区域信息（用于获取检测规则，生产检测数据）
	List<Map<String,Object>> getOrderList(Map<String,Object> param);
	// 获取需求数量
	List<Map<String,Object>> getPmdInfo(Map<String,Object> params);
	// 查询
	List<Map<String,Object>> getTestRecordList(Map<String,Object> param);
	// 查询抬头数据
	List<Map<String,Object>> getHeadList(Map<String,Object> params);
	// 导入时校验机台信息
	List<String> checkMachine(Map<String,Object> params);
	
}
