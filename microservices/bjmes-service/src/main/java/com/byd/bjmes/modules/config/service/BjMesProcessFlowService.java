package com.byd.bjmes.modules.config.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/** 
 * @author 作者 tangjin
 * @version 创建时间：2019年10月30日 下午4:55:12 
 * 类说明 加工流程
 */
public interface BjMesProcessFlowService {

	public PageUtils queryPage(Map<String, Object> params);

	public void updateProcessFlow(Map<String,Object> params);

	public void deleteProcessFlow(String process_flow_code);

	public List<Map<String,Object>> getNodeList(Map<String,Object> params);

	public void saveProcessFlow(Map<String, Object> params);

	public void saveNode(Map<String, Object> params);

	public void deleteNode(Map<String,Object> params);
}
