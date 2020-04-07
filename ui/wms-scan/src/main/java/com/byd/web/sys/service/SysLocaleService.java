package com.byd.web.sys.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.web.sys.entity.SysLocaleEntity;

/**
 * @author ren.wei3
 *
 */
public interface SysLocaleService extends IService<SysLocaleEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    public void saveLanguage(List<Map> params);
    
    public List<SysLocaleEntity> getAllLocale();
    
    public List<SysLocaleEntity> refreshModifyData();
}
