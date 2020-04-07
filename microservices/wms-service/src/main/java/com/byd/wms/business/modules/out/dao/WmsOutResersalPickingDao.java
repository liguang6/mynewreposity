package com.byd.wms.business.modules.out.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WMS出库取消需求拣配
 * @author qiu.jiaming
 * @date 2019-05-08
 */
public interface WmsOutResersalPickingDao {

	/**
	 * 获取仓库任务清单
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectWhTask(Map<String, Object> params);
	List<Map<String, Object>> selectQtyByStock(Map<String, Object> params);
	List<Map<String, Object>> selectQtyRealByReqItem(Map<String, Object> params);


	boolean updateBfgzByStock(List<HashMap> listBfgz);
	boolean updateWqByStock(List<HashMap> listWq);

	boolean updateBfqrByStock(List<HashMap> listBfQr);

	boolean updateQrByStock(List<HashMap> listQr);

	boolean updateBin(List<HashMap> hm);

	boolean updateStatusByLabel(List<HashMap> params);

	boolean updateStatusByTask(List<HashMap> params);

	void updateBfqrByReqItem(List<HashMap> listBfQr);

	void updateBfqrByReqItem2(List<HashMap> listBfgz);
}
