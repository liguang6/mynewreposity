package com.byd.wms.business.modules.kn.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.kn.entity.WmsKnLabelRecordEntity;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;

import java.util.List;
import java.util.Map;

/**
 * 移储
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-11-07 10:12:08
 */
public interface WmsKnLabelRecordDao extends BaseMapper<WmsKnLabelRecordEntity>{


	//查询记录
	List<Map<String,Object>> getLabelRecordList(Map<String, Object> param);
	//查询条数
	int getLabelRecordCount(Map<String, Object> param);
	public Map queryById(Long id);
	public List<Map<String, Object>> getLabelBywmsNo(String wmsNo);
	public void updateLabel(Map map);

	public void deleteLabel(Map map);

	public void save(Map map);

    void mergeLabelByCf(List<Map> matListMap);
	void insertByCf(Map map);
	void updateBfCf(Map map);

	/**
	 * 二部 查询采购订单
	 */
	List<Map<String,Object>> getPoList(Map<String, Object> param);

	int getPOCount(Map<String, Object> param);

	/**
	 * 二部 根据采购订单打印条码
	 */
	void insertCoreLabel(List<Map<String,Object>> list);

    List<Map<String, Object>> getLabelList(Map<String, Object> params);
    //获取物料有效期
    List<Map<String, Object>> getMatEffectList(Map<String, Object> params);
    //更新有效期
    int updateMatEffect(Map<String, Object> params);
    int updateLabelMatEffect(Map<String, Object> params);
}
