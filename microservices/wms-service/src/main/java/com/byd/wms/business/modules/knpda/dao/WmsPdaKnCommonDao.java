package com.byd.wms.business.modules.knpda.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface WmsPdaKnCommonDao extends BaseMapper {
	
	public List<Map<String, Object>> getLabelInfoList(Map<String, Object> params);
	public int getLabelInfoListCount(Map<String, Object> params);
	public List<Map<String, Object>> getPdaLabelInfo(Map<String, Object> params);
	public int getPdaLabelInfoCount(Map<String, Object> params);
	public int updateLabelInfo(List<Map<String, Object>> params);
	public int insertBarCodeLog(List<Map<String, Object>> params);
}
