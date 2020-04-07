package com.byd.wms.business.modules.kn.service;

import java.util.List;
import java.util.Map;

/**
 * 库存调整
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-01 10:12:08
 */
public interface WmsKnStockModifyService {
    // 库存调整保存；返回事务凭证号 、标签List
    public Map<String,Object> save(Map<String, Object> params);
    // 标签新增库存保存
    public Map<String,Object> saveStockLabel(Map<String, Object> params);
    
    List<Map<String,Object>> getCoreLabelList(Map<String,Object> params);
    
    // 查询标签号是否已存在
    public List<Map<String,Object>>  checkLabelList(List<String> list);
 	
 	String createBatch(Map<String,Object> params);
    // 导入操作 批量查出校验所需的数据 （物料、供应商、库位、储位等）2019-08-26 tangj
 	List<Map<String,Object>> checkMaterialList(Map<String,Object> params);
 	
 	List<Map<String,Object>> checkVendorList(Map<String,Object> params);
 	
 	List<Map<String,Object>> checkLgortList(Map<String,Object> params);
 	
 	List<Map<String,Object>> checkBinList(Map<String,Object> params);
 	
 	List<Map<String,Object>> checkStockList(Map<String,Object> params);

}

