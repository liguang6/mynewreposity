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
import com.byd.wms.business.modules.config.dao.WmsCKeyPartsDao;
import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;
import com.byd.wms.business.modules.config.service.WmsCKeyPartsService;


@Service("wmsCKeyPartsService")
public class WmsCKeyPartsServiceImpl extends ServiceImpl<WmsCKeyPartsDao, WmsCKeyPartsEntity> implements WmsCKeyPartsService {
	@Autowired
    private WmsCKeyPartsDao wmsCKeyPartsDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        
        String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
        String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
        String keyPartsNo = params.get("keyPartsNo") == null?null:String.valueOf(params.get("keyPartsNo"));
		//兼容大小写格式参数
        if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
        if(StringUtils.isBlank(keyPartsNo)){
        	keyPartsNo =  params.get("KEY_PARTS_NO") == null?null:String.valueOf(params.get("KEY_PARTS_NO"));
        }
        Page<WmsCKeyPartsEntity> page = this.selectPage(new Query<WmsCKeyPartsEntity>(params).getPage(),
				new EntityWrapper<WmsCKeyPartsEntity>().eq("DEL","0")
				.eq(StringUtils.isNotEmpty(werks),"WERKS", werks)
				.eq(StringUtils.isNotEmpty(matnr),"MATNR", matnr)
				.eq(StringUtils.isNotEmpty(keyPartsNo),"KEY_PARTS_NO", keyPartsNo)
		);
		return new PageUtils(page);
    }

	@Override
	public int batchSave(List<WmsCKeyPartsEntity> list) {
		// TODO Auto-generated method stub
		return wmsCKeyPartsDao.merge(list);
	}

}
