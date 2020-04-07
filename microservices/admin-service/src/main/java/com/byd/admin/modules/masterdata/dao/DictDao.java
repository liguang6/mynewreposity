package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.masterdata.entity.DictEntity;

/**
 * 数据字典
 *
 * @author Mark 
 * @since 3.1.0 2018-01-27
 */
public interface DictDao extends BaseMapper<DictEntity> {
	List<Map<String,Object>> getDictlistByType(Map<String, Object> params);

	public int queryMasterDictWerksOrderNum(Map<String, Object> params);
}
