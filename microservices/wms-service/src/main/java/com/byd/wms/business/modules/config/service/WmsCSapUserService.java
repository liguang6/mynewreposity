package com.byd.wms.business.modules.config.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCSapUserEntity;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月29日 上午11:13:07 
 * 类说明 
 */
public interface WmsCSapUserService extends IService<WmsCSapUserEntity> {
	PageUtils queryPage(Map<String,Object> params);
}
