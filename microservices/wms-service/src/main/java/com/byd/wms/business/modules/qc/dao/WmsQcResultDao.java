package com.byd.wms.business.modules.qc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;

/**
 * 检验结果
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcResultDao extends BaseMapper<WmsQcResultEntity> {
	public List<WmsQcResultEntity> selectRejuedgeResultItems(@Param("params")Map<String,Object> params);

    public List<Map<String,Object>> selectResultList(@Param("page")Pagination page,@Param("params")Map<String,Object> params);

   
    public List<Map<String,Object>> selectDestroyQtyList(@Param("page")Pagination page,@Param("params")Map<String,Object> params);

    List<WmsQcResultEntity> queryQcResult(Map<String,Object> map);

    /**
     * 查询质检结果关联的退货单
     * @return
     */
    public List<Map<String,Object>> queryOutReturnByQcResult(@Param("qcResultNo")String qcResultNo,@Param("qcResultItemNo")String qcResultItemNo);
    
    /**
     * 查询质检结果关联的进仓单
     * @param qcResultNo
     * @param qcResultItemNo
     * @return
     */
    public List<Map<String,Object>> queryInInboundByQcResult(@Param("qcResultNo")String qcResultNo,@Param("qcResultItemNo")String qcResultItemNo);
}
