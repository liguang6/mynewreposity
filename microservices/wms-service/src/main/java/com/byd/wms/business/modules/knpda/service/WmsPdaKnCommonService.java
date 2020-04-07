package com.byd.wms.business.modules.knpda.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

/*
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-11
 */

public interface WmsPdaKnCommonService {

	public PageUtils queryLableList(Map<String, Object> params);
	
	public PageUtils getLableInfoList(Map<String, Object> params);
	
	public void deleteLabelList(Map<String, Object> params);
	public Map<String, Object> save(Map<String, Object> params);
}
