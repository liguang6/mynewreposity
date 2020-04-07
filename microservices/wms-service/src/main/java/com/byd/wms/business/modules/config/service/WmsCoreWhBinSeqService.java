package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinSeqEntity;

import java.util.List;
import java.util.Map;

/**
 * 仓库储位排序
 * @author cscc tangj
 * @email 
 * @date 2019-03-21 15:36:51
 */
public interface WmsCoreWhBinSeqService extends IService<WmsCoreWhBinSeqEntity> {

	public void updateSeq(WmsCoreWhBinEntity bin);
	
	public void updateSeq(List<WmsCoreWhBinEntity> bin);
   
}

