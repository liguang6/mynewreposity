package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 仓库储位
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 15:36:51
 */
public interface WmsCoreWhBinDao extends BaseMapper<WmsCoreWhBinEntity> {
	/**
	 * 导入校验，区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public Map<String,Object> queryWhAreaInfo(Map<String, Object> params);
	/**
	 * 随机存储查找现有库存仓位
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findAlreadyBinForRandom(Map<String, Object> params);
	/**
	 * 查找空仓位
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findEmptyBin(Map<String, Object> params);
	
	/**
	 * 查找现有库存附近的空仓位， 现有库存仓位都放满时使用
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findEmptyBinForNeighbor(Map<String, Object> params);
	
	public int updateBinStorageUnit(List<Map<String,Object>> params);
	
	public List<Map<String,Object>> findWhBinByBinCode(String whNumber,String binCode);
	
	int merge(List<WmsCoreWhBinEntity> list);
}
