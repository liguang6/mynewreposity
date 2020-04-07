package com.byd.wms.business.modules.qc.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;
import java.util.Map;

/**
 * 检验记录
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcRecordService extends IService<WmsQcRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    PageUtils queryRecordList(Map<String, Object> params);
}

