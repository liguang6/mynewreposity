package com.byd.wms.business.modules.kn.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.kn.entity.WmsKnLabelRecordEntity;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 条码管理
 *
 * @author qjm
 * @email
 * @date 2019-04-01
 */
public interface WmsKnLabelRecordService {


	// 查询
	PageUtils queryPage(Map<String, Object> params);

	public Map queryById(Long id);

	public void updateLabel(Map map);

	public void deleteLabel(Map map);


	public void save(Map map);

	PageUtils queryByCf(Map map);

	List<Map> saveByCf(Map map);

	/**
	 * 新增标签页面采购订单查询
	 */

	PageUtils poQueryPage(Map<String,Object> params);

	/**
	 * 保存生成标签
	 */
	R saveCoreLabel(Map<String, Object> map);

    List<Map<String, Object>> getLabelList(Map<String, Object> params);
    
    /*
	 * 条码重复打印 更新有效期
	 */
    List<Map<String, Object>> updateEffectDate(Map<String, Object> params);
}

