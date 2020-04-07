package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;

import java.util.List;
import java.util.Map;

/**
 * 仓库储位
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 15:36:51
 */
public interface WmsCoreWhBinService extends IService<WmsCoreWhBinEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
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
	
	/**
	 * 下架更新储位占用存储单元
	 * @param params
	 * @return
	 */
	public int updateBinStorageUnit(List<Map<String,Object>> params);
	
	/**
	 * 批量导入插入or更新
	 * @param params
	 * @return
	 */
	int merge(List<WmsCoreWhBinEntity> list);
	
	public List<WmsCoreWhBinEntity> queryBinCode(Map<String, Object> params);
}

