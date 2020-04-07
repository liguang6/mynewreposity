/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.qms.modules.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * QMS通用Dao
 * 
 * @author tangj
 * @email 
 * @date 2019年07月24日 下午15:46:16
 */
public interface QmsCommonDao {
    // 通过代码模糊查找车型数据
	public List<Map<String,Object>> getBusTypeCodeList(@Param("busTypeCode")String busTypeCode);

	public List<Map<String, Object>> getTestNodes(@Param("testType") String testType, @Param("TEST_CLASS") String TEST_CLASS);
	
	// 通过代码模糊查找订单数据
	public List<Map<String,Object>> getOrderNoList(@Param("orderNo")String orderNo);

	public List<Map<String, Object>> getBusList(Map<String, Object> condMap);

	public List<Map<String, Object>> getTestTools(Map<String, Object> condMap);

	public List<Map<String, Object>> getQmsTestRecords(Map<String, Object> condMap);
	
}
