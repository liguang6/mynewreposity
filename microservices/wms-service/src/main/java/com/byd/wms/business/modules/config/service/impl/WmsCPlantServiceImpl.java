package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCPlantDao;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.service.WmsCPlantService;

@Service
public class WmsCPlantServiceImpl extends ServiceImpl<WmsCPlantDao, WmsCPlant> implements WmsCPlantService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String WH_NUMBER = params.get("WH_NUMBER") == null?null:String.valueOf(params.get("WH_NUMBER"));
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null ? null : String.valueOf(params.get("WERKS"));
		}
		Page<WmsCPlant> page = this.selectPage(new Query<WmsCPlant>(params).getPage(),
				new EntityWrapper<WmsCPlant>().like("WH_NUMBER", WH_NUMBER).like("werks", werks).eq("del_flag", "0")//已经标识为删除的数据不显示
				);
		return new PageUtils(page);
	}

	@Override
	public List<WmsCPlant> queryWmsCPlant(Map<String, Object> params) {
		return this.selectByMap(params);
	}

	

}
