package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatUrgentDao;
import com.byd.wms.business.modules.config.entity.WmsCMatUrgentEntity;
import com.byd.wms.business.modules.config.service.WmsCMatUrgentService;

@Service("wmsCMatUrgentService")
public class WmsCMatUrgentServiceImpl extends ServiceImpl<WmsCMatUrgentDao, WmsCMatUrgentEntity> implements WmsCMatUrgentService {
	@Autowired
	private WmsCMatUrgentDao wmsCMatUrgentDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
    	String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
    	String urgentFlag = params.get("urgentFlag") == null?null:String.valueOf(params.get("urgentFlag"));
        
    	if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
        if(StringUtils.isBlank(urgentFlag)){
        	urgentFlag = params.get("URGENT_FLAG") == null?null:String.valueOf(params.get("URGENT_FLAG"));
        }
    	
    	Page<WmsCMatUrgentEntity> page = this.selectPage(
                new Query<WmsCMatUrgentEntity>(params).getPage(),
                new EntityWrapper<WmsCMatUrgentEntity>().like("WERKS", werks).
                like("MATNR",matnr).like("URGENT_FLAG",urgentFlag).eq("DEL","0")
        );
        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCMatUrgentDao.validate(list);
	}

}
