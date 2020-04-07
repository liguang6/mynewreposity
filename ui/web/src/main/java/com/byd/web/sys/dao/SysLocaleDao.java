package com.byd.web.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.web.sys.entity.SysLocaleEntity;

/**
 * @author ren.wei3
 *
 */
public interface SysLocaleDao extends BaseMapper<SysLocaleEntity> {
	
	List<SysLocaleEntity> getAllLocale();
	
}
