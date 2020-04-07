package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsInPoItemAuthEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年8月31日 上午10:38:03 
 * 类说明 
 */
public interface WmsInPoItemAuthService extends IService<WmsInPoItemAuthEntity>{
	PageUtils queryPage(Map<String,Object> params);
	
	List<Map<String,Object>> getPolist(Map<String, Object> map);
	
	void savePoAuthData(Map<String, Object> map);
	
    void savePoAuthDataImport(Map<String, Object> params);
}
