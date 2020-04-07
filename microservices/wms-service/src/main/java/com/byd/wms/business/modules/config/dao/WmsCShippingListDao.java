package com.byd.wms.business.modules.config.dao;


import java.util.List;
import java.util.Map;


public interface WmsCShippingListDao{

	 	int getListCount(Map<String, Object> params);

	    List<Map<String, Object>> queryNoticeMailList(Map<String, Object> params);

	    void insertNoticeMail(List<Map<String,Object>> params);

	    Map<String, Object> selectById(String id,String itemNo);

	    void updateById(List<Map<String,Object>> params);
}
