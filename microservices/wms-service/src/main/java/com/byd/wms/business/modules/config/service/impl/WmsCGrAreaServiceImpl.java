package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCGrAreaDao;
import com.byd.wms.business.modules.config.entity.WmsCGrAreaEntity;
import com.byd.wms.business.modules.config.service.WmsCGrAreaService;

@Service("wmsCGrAreaService")
public class WmsCGrAreaServiceImpl extends ServiceImpl<WmsCGrAreaDao, WmsCGrAreaEntity> implements WmsCGrAreaService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
        String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
        if(StringUtils.isBlank(werks)){
        	werks =  params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
        }
        
        String areaCode = params.get("areaCode") == null?null:String.valueOf(params.get("areaCode"));
		if(StringUtils.isBlank(areaCode)){
			areaCode = params.get("AREA_CODE") == null?null:String.valueOf(params.get("AREA_CODE"));
		}
        Page<WmsCGrAreaEntity> page = this.selectPage(new Query<WmsCGrAreaEntity>(params).getPage(),
				new EntityWrapper<WmsCGrAreaEntity>().like("WERKS", werks).like("AREA_CODE", areaCode).eq("DEL","0")
		);
		return new PageUtils(page);
	}

}
