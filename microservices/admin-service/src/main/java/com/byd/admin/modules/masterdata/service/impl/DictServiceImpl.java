package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.DictDao;
import com.byd.admin.modules.masterdata.entity.DictEntity;
import com.byd.admin.modules.masterdata.service.DictService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;


@Service("sysDictService")
public class DictServiceImpl extends ServiceImpl<DictDao, DictEntity> implements DictService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	Page<DictEntity> page = null;
        String name = (String)params.get("name");
        String type = (String)params.get("type");
        page = new Query<DictEntity>(params).getPage();
        if(page.getLimit()==-1) {
        	List<DictEntity> records = this.selectList(new EntityWrapper<DictEntity>()
                    .like(StringUtils.isNotBlank(name),"name", name)
                    .like(StringUtils.isNotBlank(type),"type", type));
			page.setRecords(records);
			page.setTotal(records.size());
			page.setSize(records.size());
        }else {
            page = this.selectPage(
            		page,
                    new EntityWrapper<DictEntity>()
                        .like(StringUtils.isNotBlank(name),"name", name)
                        .like(StringUtils.isNotBlank(type),"type", type)
            );
        }

        return new PageUtils(page);
    }
    @Override
    public List<Map<String,Object>> getDictlistByType(Map<String, Object> params){
    	return baseMapper.getDictlistByType(params);
    }

    @Override
    public int queryMasterDictWerksOrderNum(Map<String, Object> params){
        return baseMapper.queryMasterDictWerksOrderNum(params);
    }

}
