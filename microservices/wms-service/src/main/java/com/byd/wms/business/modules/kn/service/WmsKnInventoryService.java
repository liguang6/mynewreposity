package com.byd.wms.business.modules.kn.service;

import com.byd.utils.PageUtils;
import java.util.List;
import java.util.Map;

/**
 * 盘点记录
 * @author cscc
 * @email 
 * @date 2018-10-11 10:12:08
 */
public interface WmsKnInventoryService {
    // 查询盘点记录
    PageUtils queryPage(Map<String, Object> params);
    // 根据盘点任务号查询盘点记录行项目明细
    public List<Map<String,Object>> queryInventoryItem(String inventoryNo);
    // 创建盘点任务；生成盘点抬头、盘点行项目记录 
    public Map<String,Object> createInventoryTask(Map<String, Object> params);
    // 盘点表打印
    public List<Map<String,Object>> print(Map<String, Object> param);
    // 根据盘点任务号查询盘点记录表抬头
    Map<String,Object> getInventoryHead(Map<String,Object> param);
    // 录入盘点结果（初盘、复盘）
 	void batchUpdateResult(Map<String,Object> map);
    // 导入盘点结果（初盘、复盘）
 	public void batchUpdateImp(Map<String, Object> map);
    // 盘点结果确认（差异原因）
  	public void batchUpdateConfirm(Map<String, Object> map);
    // 盘点确认数据查询  
 	List<Map<String,Object>> getInventoryConfirmList(Map<String,Object> param);
    // 根据盘点任务号查询需进行盘点确认的数据行项目明细
    public List<Map<String,Object>> queryInventoryConfirmItem(Map<String, Object> param);
    // 获取区域管理员
    List<Map<String,Object>> getWhManagerList(Map<String,Object> param);
}

