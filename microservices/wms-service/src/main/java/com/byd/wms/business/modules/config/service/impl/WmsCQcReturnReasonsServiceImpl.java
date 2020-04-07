package com.byd.wms.business.modules.config.service.impl;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCQcReturnReasonsDao;
import com.byd.wms.business.modules.config.entity.WmsCQcReturnReasonsEntity;
import com.byd.wms.business.modules.config.service.WmsCQcReturnReasonsService;

@Service("wmsCQcReturnReasonsService")
public class WmsCQcReturnReasonsServiceImpl extends ServiceImpl<WmsCQcReturnReasonsDao, WmsCQcReturnReasonsEntity> implements WmsCQcReturnReasonsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String REASONS_TYPE = params.get("REASON_TYPE") == null?null:String.valueOf(params.get("REASON_TYPE"));
        
        Page<WmsCQcReturnReasonsEntity> page = this.selectPage(
                new Query<WmsCQcReturnReasonsEntity>(params).getPage(),
                new EntityWrapper<WmsCQcReturnReasonsEntity>().eq(!StringUtils.isEmpty(REASONS_TYPE), "REASON_TYPE", REASONS_TYPE)
        );

        return new PageUtils(page);
    }

}
