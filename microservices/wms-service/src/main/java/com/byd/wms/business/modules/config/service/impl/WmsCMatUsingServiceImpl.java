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
import com.byd.wms.business.modules.config.dao.WmsCMatUsingDao;
import com.byd.wms.business.modules.config.entity.WmsCMatUsingEntity;
import com.byd.wms.business.modules.config.service.WmsCMatUsingService;

@Service("wmsCMatUsingService")
public class WmsCMatUsingServiceImpl extends ServiceImpl<WmsCMatUsingDao, WmsCMatUsingEntity> implements WmsCMatUsingService {
	@Autowired
	private WmsCMatUsingDao wmsCMatUsingDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
    	String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
    	String usingFlag = params.get("usingFlag") == null?null:String.valueOf(params.get("usingFlag"));
        
    	if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
        if(StringUtils.isBlank(usingFlag)){
        	usingFlag = params.get("USING_FLAG") == null?null:String.valueOf(params.get("USING_FLAG"));
        }
        
    	Page<WmsCMatUsingEntity> page = this.selectPage(
                new Query<WmsCMatUsingEntity>(params).getPage(),
                new EntityWrapper<WmsCMatUsingEntity>().like("WERKS", werks).
                like("MATNR",matnr).like("USING_FLAG",usingFlag).eq("DEL","0")
        );
        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCMatUsingDao.validate(list);
	}

}
