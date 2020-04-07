package com.byd.wms.business.modules.report.service;

import java.util.Map;

import com.byd.utils.PageUtils;

/** 
* @author 作者 : ren.wei3@byd.com 
* @version 创建时间：2019年6月4日 下午5:12:12 
* 
*/

public interface QueryComponentService {

	//库存查询(与ERP库存比较)
  	PageUtils queryStockPage(Map<String, Object> params);
}
