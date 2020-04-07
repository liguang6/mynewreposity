package com.byd.wms.business.modules.kn.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.kn.entity.WmsKnFreezeRecordEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 库存冻结记录
 *
 * @author cscc
 * @email 
 * @date 2018-10-11 10:12:08
 */
public interface WmsKnFreezeRecordService extends IService<WmsKnFreezeRecordEntity> {
    // 查询冻结、解冻库存
    PageUtils queryPage(Map<String, Object> params);
    // 冻结、解冻
    public boolean freeze(Map<String, Object> params);
    // 冻结、解冻记录查询
    PageUtils queryFreezeRecordPage(Map<String, Object> params);
    // 创建冻结定时任务 
    public void createFreezeJobs(String param);
    // app扫描标签号获取数据
    List<Map<String,Object>> getDataByLabelNo(String labelNo);
    // app冻结、解冻保存
    boolean appFreeze(Map<String, Object> params);
    //物料批次过期冻结
    public void freezeMatList();
}

