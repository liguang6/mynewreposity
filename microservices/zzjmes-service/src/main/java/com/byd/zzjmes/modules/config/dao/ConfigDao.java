package com.byd.zzjmes.modules.config.dao;

import java.util.List;
import java.util.Map;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月11日 下午2:24:24 
 * 类说明 
 */
public interface ConfigDao {
	public List<Map<String, Object>> getMachineAssignList(Map<String, Object> params);
	public int getMachineAssignCount(Map<String, Object> params);
	public int insertMachineAssign(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateMachineAssign(Map<String, Object> params);
	public int delMachineAssign(Map<String, Object> params);
	public List<Map<String, Object>> selectMachineByNo(Map<String, Object> params);
	
}
