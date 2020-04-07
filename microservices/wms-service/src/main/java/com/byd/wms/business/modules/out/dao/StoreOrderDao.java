package com.byd.wms.business.modules.out.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementLabelEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/19
 * @description：
 */
@Service("storeOrderDao")
public interface StoreOrderDao{
    void insertHX(List<Map<String, Object>> params);//插入行项目

    void insertTM(List<Map<String, Object>> params);//插入条码

    Map<String, Object> selectHead(String requirement_no);//头表查找

    List<Map<String, Object>> selectItem(String requirement_no);//项目表查找

    List<Map<String, Object>> selectLabel(String requirement_item_no, String requirement_no);//条码表查找

    List<Map<String, Object>> selectReturnMsg(String dispatching_no);//返回结果查询

    List<Map<String, Object>> selectOtherSystemREQ(Map<String, Object> params);

    void saveOtherSystemREQ(List<Map<String,Object>> params);

    void updateOtherSystemREQ(List<Map<String,Object>> params);

}
