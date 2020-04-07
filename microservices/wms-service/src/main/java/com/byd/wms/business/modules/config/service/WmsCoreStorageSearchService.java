package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;

import java.util.List;
import java.util.Map;

/**
 * @ClassName WmsCoreSearchSequenceService
 * @Description  分配存储类型至搜索顺序配置
 * @Author qiu.jiaming1
 * @Date 2019/2/28
 */
public interface WmsCoreStorageSearchService extends IService<WmsCoreStorageSearchEntity>{
    // 分页查询
	PageUtils queryPage(Map<String, Object> params);

	//查询存储区域名称
    String queryAreaName(String arg1,String arg2);
    /**
     * 根据仓库号查询搜索顺序列表
     */
    List getStorageAreaSearch(String warehouseCode);
    /**
     * 根据仓库号查询存储类型列表
     */
    List getStorageAreaCode(String warehouseCode);
}
