package com.byd.wms.business.modules.pda.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byd.wms.business.modules.pda.dao.PdaInventorySearchDao;
import com.byd.wms.business.modules.pda.service.PdaInventorySearchService;

@Service("pdaInventorySearchService")
public class PdaInventorySearchServiceImpl implements PdaInventorySearchService {
	@Autowired
	private PdaInventorySearchDao pdaInventorySearchDao;

	@Override
	public List queryMatnr(Map<String, Object> params) {

		return pdaInventorySearchDao.queryMatnr(params);
	}

	public List inventoryList(Map<String, Object> params) {

		return pdaInventorySearchDao.inventoryList(params);
	}

}
