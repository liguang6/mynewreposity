package com.byd.wms.business.modules.query.service;

import java.util.List;
import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 退货单查询
 * @author cscc tangj
 * @email 
 * @date 2018-11-30 08:55:22
 */
public interface ReturngoodsQueryService {

    PageUtils queryPage(Map<String, Object> params);
    // 查询明细
	public List<Map<String,Object>> queryItemPage(Map<String,Object> params);
	// 关闭退货单
	public boolean del(Map<String,Object> params);
	public String close(Map<String,Object> params);
    // 查询退货单类型
    List<Map<String,Object>> queryReturnDocTypeList();
    // 查询退货类型
    List<Map<String,Object>> queryReturnTypeList(String type);
}

