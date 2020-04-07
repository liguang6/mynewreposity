package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.masterdata.entity.ProcessEntity;

/**
 * 标准工序表-基础数据
 * 
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
public interface ProcessDao extends BaseMapper<ProcessEntity> {
	// 模糊查找工序数据
	public List<Map<String,Object>> getProcessList(Map<String,String> map);
	
	public int checkRepeat(ProcessEntity entity);
	
	List<Map<String,Object>> getWorkshopProcessList(Map<String,Object> map);
	
}
