package com.byd.wms.business.modules.knpda.service;

import java.util.List;
import java.util.Map;

/*PDA 条码拆分
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-11
 */

public interface WmsPdaKnLableRecordService {

	public List<Map<String, Object>> queryLableList(Map<String, Object> params);
}
