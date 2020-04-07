package com.byd.wms.business.modules.common.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.wms.business.modules.common.entity.WmsCTxtEntity;

/** 
 * @author 作者 E-mail: peng.tao1!byd.com
 * @version 创建时间：2018年8月23日 上午9:39:54 
 * 类说明 
 */
public interface WmsCTxtService extends IService<WmsCTxtEntity> {
	public Map<String, Object> getRuleTxt(Map<String, String> params);
}
