package com.byd.wms.business.modules.qc.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;

/**
 * 送检单明细
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcInspectionItemDao extends BaseMapper<WmsQcInspectionItemEntity> {
	/**
	 * 查询物料质检项，分页
	 * 根据wms_c_mat_urgent表的内容，紧急物料排序靠前
	 * @param params
	 * @return
	 */
	List<WmsQcInspectionItemEntity> selectInspectionItemList(@Param("page")Pagination page,@Param("params")Map<String,Object> params);

	/**
	 * 质检中
	 * @param page
	 * @param params
	 * @return
	 */
    List<WmsQcInspectionItemEntity>  selectInspectionList2(@Param("page")Pagination page,@Param("params")Map<String,Object> params);

    /**
     * 来料质检之已质检查询
     * @param page
     * @param params
     * @return
     */
    List<Map<String,Object>> selectHasInspectedItem(@Param("page")Pagination page,@Param("params")Map<String,Object> params);
 
    /**
     * 查询库存复检数据
     * @param params
     * @return
     */
    List<Map<String,Object>> selectStockReJudgeItems(@Param("params")Map<String,Object> params);
    
    /**
     * 库存复检-未质检查询
     * @param params
     * @return
     */
    List<Map<String,Object>> selectStockReJudgeNotInspected(@Param("params")Map<String,Object> params);
    
    /**
     * 查询 库存复检 质检中
     * @param params
     * @return
     */
    List<Map<String,Object>> selectStockRejudgeOnInspect(@Param("params")Map<String,Object> params);
    
    /**
     * 
     * @param map
     * @return
     */
    List<Map<String,Object>> queryLabelInfo(@Param("params")Map<String,Object> map);
    
    void updateWmsCoreMatBatch(Map<String,Object> map);
    
    List<Map<String,Object>> queryDict(String type);
    
    List<WmsQcInspectionItemEntity> queryInspectionByLabelNo(Map<String,Object> map);
    
    List<Map<String,Object>> queryInspectionInfos(String INSPECTION_NO);
    
    List<WmsQcInspectionItemEntity> listInspectionItems(Map<String,Object> map);
    
    
    /**
     * 仓库配置查询
     * @param map
     * @return
     */
    List<Map<String,Object>> queryWhConfig(Map<String,Object> map);
         
    List<Map<String,Object>> queryInspectionInfoForRejudge(Map<String,Object> map);
    /**
     * 
     * 查询送检单
     * 根据批次和工厂 关联的标签信息
     * @param batch
     * @return
     */
    List<Map<String,Object>> queryInspectionItemLabelsByReceipt(String RECEIPT_NO,String RECEIPT_ITEM_NO);
    
    /**
     * 根据物料清单查询未质检送检单行项目信息
     * @param matList
     * @return
     */
    List<Map<String,Object>> queryInspectionItemByMatList(List<Map<String,Object>> matList);
    
    /**
     * 根据工厂、仓库号、库位查询未质检送检单行项目信息
     * @param map
     * @return
     */
    List<Map<String,Object>> queryInspectionItemByLgort(Map<String,Object> map);
}
