package com.byd.wms.webservice.ws.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.webservice.ws.entity.WmsCWhEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年3月4日 下午2:17:51 
 * 类说明 
 */
public interface WmsCWhDao extends BaseMapper<WmsCWhEntity> {

	
	Map<String,Object> getWmsCWh(Map<String,Object> param);
}
