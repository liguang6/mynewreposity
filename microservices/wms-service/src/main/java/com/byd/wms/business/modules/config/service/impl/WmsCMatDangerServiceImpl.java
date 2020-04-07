package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatDangerDao;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
import com.byd.wms.business.modules.config.service.WmsCMatDangerService;

@Service("wmsCMatDangerService")
public class WmsCMatDangerServiceImpl extends ServiceImpl<WmsCMatDangerDao, WmsCMatDangerEntity> implements WmsCMatDangerService {
	@Autowired
	private WmsCMatDangerDao wmsCMatDangerDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
        String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
        String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
        String dangerFlag = params.get("dangerFlag") == null?null:String.valueOf(params.get("dangerFlag"));
		String lifnr = params.get("lifnr") == null?null:String.valueOf(params.get("lifnr"));
		//兼容大小写格式参数
        if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
        if(StringUtils.isBlank(dangerFlag)){
        	dangerFlag =  params.get("DANGER_FLAG") == null?null:String.valueOf(params.get("DANGER_FLAG"));
        }
		if(StringUtils.isBlank(lifnr)){
			lifnr =  params.get("lifnr") == null?null:String.valueOf(params.get("lifnr"));
		}
        Page<WmsCMatDangerEntity> page = this.selectPage(new Query<WmsCMatDangerEntity>(params).getPage(),
				new EntityWrapper<WmsCMatDangerEntity>().eq("DEL","0")
				.eq(StringUtils.isNotEmpty(werks),"WERKS", werks)
				.eq(StringUtils.isNotEmpty(matnr),"MATNR", matnr)
				.eq(StringUtils.isNotEmpty(dangerFlag),"DANGER_FLAG", dangerFlag)
				.eq(StringUtils.isNotEmpty(lifnr),"LIFNR", dangerFlag)
		);
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> validateMatDager(List<String> list) {
		return wmsCMatDangerDao.validateMatDager(list);
	}

}
