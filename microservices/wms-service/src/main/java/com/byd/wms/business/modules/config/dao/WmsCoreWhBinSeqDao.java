package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCoreWhBinSeqEntity;

import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 仓库储位排序
 * @author cscc tangj
 * @email 
 * @date 2019-03-21 15:36:51
 */
public interface WmsCoreWhBinSeqDao extends BaseMapper<WmsCoreWhBinSeqEntity> {
	
	public int delBinSeq(Map<String,Object> param);
}
