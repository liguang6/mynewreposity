package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.BusTypeDao;
import com.byd.admin.modules.masterdata.entity.BusTypeEntity;
import com.byd.admin.modules.masterdata.service.BusTypeService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;


@Service("settingBusTypeService")
public class BusTypeServiceImpl extends ServiceImpl<BusTypeDao, BusTypeEntity> implements BusTypeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String busTypeCode = (String)params.get("busTypeCode");
    	String internalName = (String)params.get("internalName");
    	String vehicleType = (String)params.get("vehicleType");
        Page<BusTypeEntity> page = this.selectPage(
                new Query<BusTypeEntity>(params).getPage(),
                new EntityWrapper<BusTypeEntity>().like(StringUtils.isNotBlank(busTypeCode),"bus_type_code", busTypeCode)
                .like(StringUtils.isNotBlank(internalName),"internal_name", internalName)
                .like(StringUtils.isNotBlank(vehicleType),"vehicle_type", vehicleType)
        		
        );

        return new PageUtils(page);
    }
    
    @Override
    public List<BusTypeEntity> queryAll(Map<String, Object> params) {
    	Wrapper<BusTypeEntity> wrapper = new EntityWrapper<BusTypeEntity>();
    	List<BusTypeEntity> all = this.selectList(wrapper);
    	return all;
    }
    
    @Override
    public List<Map<String,Object>> getBusTypeList(Map<String, Object> params){
    	return baseMapper.getBusTypeList(params);
    }
}
