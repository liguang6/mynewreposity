package com.byd.bjmes.modules.config.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * @author 作者 tangjin
 * @version 创建时间：2019年10月30日 下午5:00:00 
 * 类说明 加工流程
 */
public interface BjMesProcessFlowDao {

	public List<Map<String, Object>> getListByPage(Map<String, Object> params);

	public int getListCount(Map<String, Object> params);

	public List<Map<String, Object>> getNodeList(Map<String, Object> params);

	public void delById(@Param("id") Long id);

	public void delByProcessFlowCode(@Param("process_flow_code") String process_flow_code);
	
	public void saveProcessFlow(Map<String,Object> params);

	public void updateProcessFlow(Map<String,Object> params);

	public void saveNodeInfo(Map<String,Object> params);

	public void updateNodeInfo(Map<String,Object> params);

	public void updateNodeSeq(Map<String,Object> params);

	public Map<String, Object> checkProcessFlow(Map<String,Object> params);
}
