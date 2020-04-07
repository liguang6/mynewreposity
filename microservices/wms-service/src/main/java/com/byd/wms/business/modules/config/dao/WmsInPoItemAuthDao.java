package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsInPoItemAuthEntity;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月31日 上午10:34:15 
 * 类说明 
 */
public interface WmsInPoItemAuthDao extends BaseMapper<WmsInPoItemAuthEntity> {
	List<Map<String,Object>> getPolist(Map<String, Object> map);
}
