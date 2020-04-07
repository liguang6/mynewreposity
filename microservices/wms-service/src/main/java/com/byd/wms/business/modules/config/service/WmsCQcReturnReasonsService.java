package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCQcReturnReasonsEntity;

import java.util.Map;

/**
 * 退货原因配置表
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-15 17:10:53
 */
public interface WmsCQcReturnReasonsService extends IService<WmsCQcReturnReasonsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

