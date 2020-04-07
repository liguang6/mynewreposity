package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCSapUserDao;
import com.byd.wms.business.modules.config.entity.WmsCSapUserEntity;
import com.byd.wms.business.modules.config.service.WmsCSapUserService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年8月29日 上午11:19:03 
 * 类说明 
 */
@Service
public class WmsCSapUserServiceImpl extends ServiceImpl<WmsCSapUserDao, WmsCSapUserEntity> implements WmsCSapUserService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		
		if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
		Page<WmsCSapUserEntity> page = this.selectPage(new Query<WmsCSapUserEntity>(params).getPage(),
				new EntityWrapper<WmsCSapUserEntity>().like("werks", werks).eq("del", "0")//已经标识为删除的数据不显示
				);
		return new PageUtils(page);
	}

	

}
