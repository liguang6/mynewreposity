package com.byd.wms.business.modules.common.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.dao.WmsCBatchPlanRulesDao;
import com.byd.wms.business.modules.common.dao.WmsCoreMatBatchDao;
import com.byd.wms.business.modules.common.entity.WmsCBatchPlanRulesEntity;
import com.byd.wms.business.modules.common.service.WmsCBatchPlanRulesService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年8月21日 下午1:32:11 
 * 类说明 
 */
@Service
public class WmsCBatchPlanRulesServiceImpl extends ServiceImpl<WmsCBatchPlanRulesDao, WmsCBatchPlanRulesEntity> implements
		WmsCBatchPlanRulesService {
	@Autowired
	private WmsCoreMatBatchDao wmscorematbatchdao;
	@Autowired
	private WmsCBatchPlanRulesDao wmsCBatchPlanRulesDao;
	
	public WmsCoreMatBatchDao getWmscorematbatchdao() {
		return wmscorematbatchdao;
	}

	public void setWmscorematbatchdao(WmsCoreMatBatchDao wmscorematbatchdao) {
		this.wmscorematbatchdao = wmscorematbatchdao;
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		Page<WmsCBatchPlanRulesEntity> page = this.selectPage(new Query<WmsCBatchPlanRulesEntity>(params).getPage(),
				new EntityWrapper<WmsCBatchPlanRulesEntity>().like("werks", werks).eq("del", "0")//已经标识为删除的数据不显示
				);
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> selectBatchCodeList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmscorematbatchdao.selectBatchCodeList(map);
	}

	@Override
	public int merge(List<WmsCBatchPlanRulesEntity> list) {
		return wmsCBatchPlanRulesDao.merge(list);
	}
}
