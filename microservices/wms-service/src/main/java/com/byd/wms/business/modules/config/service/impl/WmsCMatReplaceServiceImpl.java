package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatReplaceDao;
import com.byd.wms.business.modules.config.entity.WmsCMatReplaceEntity;
import com.byd.wms.business.modules.config.service.WmsCMatReplaceService;

@Service("wmsCMatReplaceService")
public class WmsCMatReplaceServiceImpl extends ServiceImpl<WmsCMatReplaceDao, WmsCMatReplaceEntity> implements WmsCMatReplaceService {
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
    	String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));

    	if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
    
    	Page<WmsCMatReplaceEntity> page = this.selectPage(
                new Query<WmsCMatReplaceEntity>(params).getPage(),
                new EntityWrapper<WmsCMatReplaceEntity>().like("FACTORY_CODE", werks).
                like("MATERIAL_CODE",matnr).eq("DEL","0")
        );
        return new PageUtils(page);
    }

	

}
