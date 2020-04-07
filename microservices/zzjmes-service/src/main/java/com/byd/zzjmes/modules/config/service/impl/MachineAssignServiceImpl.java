package com.byd.zzjmes.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.zzjmes.modules.config.dao.ConfigDao;
import com.byd.zzjmes.modules.config.service.MachineAssignService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月11日 下午2:23:47 
 * 类说明 
 */
@Service("machineAssignService")
public class MachineAssignServiceImpl implements MachineAssignService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ConfigDao configDao;
	@Override
	public PageUtils getMachineAssignList(Map<String, Object> params) {
		Page<Map<String, Object>> roderPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		//workshoplist
		if(params.get("workshop")!=null ){
			String[] workshoplist=params.get("workshop").toString().split(",");
				params.put("workshoplist", workshoplist);
			
		}
		//workgrouplist
		if(params.get("workgroup")!=null ){
			String[] workgrouplist=params.get("workgroup").toString().split(",");
			if(workgrouplist.length==1&&"".equals(workgrouplist[0])){
				
			}else{
				params.put("workgrouplist", workgrouplist);
			}
		}
		//linelist
		if(params.get("line")!=null ){
			String[] linelist=params.get("line").toString().split(",");
			if(linelist.length==1&&"".equals(linelist[0])){
				
			}else{
				params.put("linelist", linelist);
			}
		}
		roderPage.setRecords(configDao.getMachineAssignList(params));
		roderPage.setSize(pageSize);
		roderPage.setTotal(configDao.getMachineAssignCount(params));
		roderPage.setCurrent(pageNo);//要传入当前页
		PageUtils page = new PageUtils(roderPage);		
		return page;
	}
	@Override
	public int insertMachineAssign(Map<String, Object> params) {
		
		return configDao.insertMachineAssign(params);
	}
	@Override
	public Map<String, Object> selectById(Map<String, Object> params) {
		
		return configDao.selectById(params);
	}
	@Override
	public int updateMachineAssign(Map<String, Object> params) {
		
		return configDao.updateMachineAssign(params);
	}
	@Override
	public int delMachineAssign(Map<String, Object> params) {
		
		return configDao.delMachineAssign(params);
	}
	@Override
	public List<Map<String, Object>> selectMachineByNo(Map<String, Object> params) {
		return configDao.selectMachineByNo(params);
	}

}
