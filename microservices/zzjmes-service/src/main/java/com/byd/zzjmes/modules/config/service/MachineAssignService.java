package com.byd.zzjmes.modules.config.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月11日 下午2:22:39 
 * 类说明 
 */
public interface MachineAssignService {
	public PageUtils getMachineAssignList(Map<String, Object> params);
	public int insertMachineAssign(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateMachineAssign(Map<String, Object> params);
	public int delMachineAssign(Map<String, Object> params);
	public List<Map<String, Object>> selectMachineByNo(Map<String, Object> params);
}
