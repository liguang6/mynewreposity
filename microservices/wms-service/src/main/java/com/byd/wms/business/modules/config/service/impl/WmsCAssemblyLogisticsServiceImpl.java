package com.byd.wms.business.modules.config.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCAssemblyLogisticsDao;
import com.byd.wms.business.modules.config.entity.WmsCAssemblyLogisticsEntity;
import com.byd.wms.business.modules.config.service.WmsCAssemblyLogisticsService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年7月26日 下午2:50:07 
 * 类说明 
 */
@Service("wmsCAssemblyLogisticsService")
public class WmsCAssemblyLogisticsServiceImpl extends ServiceImpl<WmsCAssemblyLogisticsDao,WmsCAssemblyLogisticsEntity> implements WmsCAssemblyLogisticsService {
	@Autowired
	private WmsCAssemblyLogisticsDao wmsCAssemblyLogisticsDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werksF = params.get("werks_f") == null?null:String.valueOf(params.get("werks_f"));
		Page<WmsCAssemblyLogisticsEntity> page = this.selectPage(new Query<WmsCAssemblyLogisticsEntity>(params).getPage(),
				new EntityWrapper<WmsCAssemblyLogisticsEntity>().eq("DEL","0")
				.eq(StringUtils.isNotEmpty(werksF),"WERKS_F", werksF)
		);
		return new PageUtils(page);
	}
	@Override
	public int batchSave(List<WmsCAssemblyLogisticsEntity> list) {
		for(WmsCAssemblyLogisticsEntity item:list){
			if("是".equals(item.getWmsFlagF())){
				item.setWmsFlagF("X");
			}else{
				item.setWmsFlagF("0");
			}
			
			if("无需过账".equals(item.getSapFlagF())){
				item.setSapFlagF("00");
			}else if("实时过账".equals(item.getSapFlagF())){
				item.setSapFlagF("01");
			}else{
				item.setSapFlagF("02");
			}
		}
		
		return wmsCAssemblyLogisticsDao.merge(list);
	}

}
