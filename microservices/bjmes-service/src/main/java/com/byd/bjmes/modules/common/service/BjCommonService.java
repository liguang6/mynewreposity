package com.byd.bjmes.modules.common.service;

import java.util.Map;

import com.byd.utils.PageUtils;

public interface BjCommonService {

	PageUtils getOrderList(Map<String, Object> paramMap);
    
}