package com.byd.admin.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysTextCookieEntity;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SysTextCookieService extends IService<SysTextCookieEntity> {
	String queryPage(Map<String, Object> params);

	void saveInputVal(Map<String, Object> params);
    
}
