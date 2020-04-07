package com.byd.wms.business.modules.config.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCoreSearchSequenceDao;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.service.WmsCoreSearchSequenceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("wmsCSearchSequenceService")
public class WmsCoreSearchSequenceServiceImpl extends ServiceImpl<WmsCoreSearchSequenceDao, WmsCoreSearchSequenceEntity> implements WmsCoreSearchSequenceService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<WmsCoreSearchSequenceEntity> page = new Query<WmsCoreSearchSequenceEntity>(params).getPage();
        String warehouseCode = params.get("warehouseCode") == null?"":String.valueOf(params.get("warehouseCode"));
        String storageAreaSearch = params.get("storageAreaSearch") == null?"":String.valueOf(params.get("storageAreaSearch"));
		String storageSequenceType = params.get("storageSequenceType") == null?"":String.valueOf(params.get("storageSequenceType"));
		if(page.getLimit()==-1) {
			List<WmsCoreSearchSequenceEntity> records = this.selectList(new EntityWrapper<WmsCoreSearchSequenceEntity>()
					.eq(StringUtils.isNotBlank(warehouseCode),"WH_NUMBER", warehouseCode)
					.eq(StringUtils.isNotBlank(storageAreaSearch),"SEARCH_SEQ", storageAreaSearch)
					.eq(StringUtils.isNotBlank(storageSequenceType),"SEARCH_SEQ_TYPE",storageSequenceType )
					.eq("DEL","0")
					.orderBy("ID"));
			page.setRecords(records);
			page.setTotal(records.size());
			page.setSize(records.size());
		}else {
			page = this.selectPage(
					page,
					new EntityWrapper<WmsCoreSearchSequenceEntity>()
							.eq(StringUtils.isNotBlank(warehouseCode),"WH_NUMBER", warehouseCode)
							.eq(StringUtils.isNotBlank(storageAreaSearch),"SEARCH_SEQ", storageAreaSearch)
							.eq(StringUtils.isNotBlank(storageSequenceType),"SEARCH_SEQ_TYPE",storageSequenceType )
							.eq("DEL","0")
							.orderBy("ID")
			);
			if(page.getRecords().size()==0){
				//处理多页下条件查找跳转到第一页
				page.setCurrent(1);
				page = this.selectPage(
						page,
						new EntityWrapper<WmsCoreSearchSequenceEntity>()
								.eq(StringUtils.isNotBlank(warehouseCode),"WH_NUMBER", warehouseCode)
								.eq(StringUtils.isNotBlank(storageAreaSearch),"SEARCH_SEQ", storageAreaSearch)
								.eq(StringUtils.isNotBlank(storageSequenceType),"SEARCH_SEQ_TYPE",storageSequenceType )
								.eq("DEL","0")
								.orderBy("ID")
				);
			}

		}

		return new PageUtils(page);
	}

}
