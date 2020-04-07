package com.byd.admin.modules.masterdata.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.WorkgroupDao;
import com.byd.admin.modules.masterdata.dao.WorkgroupNoDao;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.WorkgroupNoService;
import com.byd.admin.modules.masterdata.service.WorkgroupService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service("workgroupNoService")
public class WorkgroupNoServiceImpl  implements WorkgroupNoService {
	@Autowired
	WorkgroupNoDao workgroupNoDao;

	
	@Override
	public List<Map<String, Object>> getStandardWorkgroupList(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getStandardWorkgroupList(map);
	}


	@Override
	public boolean delete(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean deleteBatchIds(Collection<? extends Serializable> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean deleteById(Serializable arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean deleteByMap(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insert(DeptEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertAllColumn(DeptEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertBatch(List<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertBatch(List<DeptEntity> arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertOrUpdate(DeptEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertOrUpdateAllColumn(DeptEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertOrUpdateAllColumnBatch(List<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertOrUpdateAllColumnBatch(List<DeptEntity> arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertOrUpdateBatch(List<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean insertOrUpdateBatch(List<DeptEntity> arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<DeptEntity> selectBatchIds(
			Collection<? extends Serializable> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DeptEntity selectById(Serializable arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<DeptEntity> selectByMap(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int selectCount(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<DeptEntity> selectList(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, Object> selectMap(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map<String, Object>> selectMaps(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Page<Map<String, Object>> selectMapsPage(Page arg0,
			Wrapper<DeptEntity> arg1) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object selectObj(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Object> selectObjs(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DeptEntity selectOne(Wrapper<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Page<DeptEntity> selectPage(Page<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Page<DeptEntity> selectPage(Page<DeptEntity> arg0,
			Wrapper<DeptEntity> arg1) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean update(DeptEntity arg0, Wrapper<DeptEntity> arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean updateAllColumnBatchById(List<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean updateAllColumnBatchById(List<DeptEntity> arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean updateAllColumnById(DeptEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean updateBatchById(List<DeptEntity> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean updateBatchById(List<DeptEntity> arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean updateById(DeptEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<Map<String, Object>> getWorkShopByCode(Map<String, String> map) {
		return workgroupNoDao.getWorkShopByCode(map);
	}


	@Override
	public Map<String, Object> getWorkGroupById(Map<String, String> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getWorkGroupById(map);
	}


	@Override
	public List<Map<String, Object>> getWorkGroupByCode(Map<String, String> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getWorkGroupByCode(map);
	}


	@Override
	public Map<String, Object> getWorkTeamById(Map<String, String> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getWorkTeamById(map);
	}


	@Override
	public Map<String, Object> getDeptWorkTeamById(Map<String, String> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getDeptWorkTeamById(map);
	}


	@Override
	public List<Map<String, Object>> getWorkshopWorkgroupByCode(
			Map<String,Object> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getWorkshopWorkgroupByCode(map);
	}


	@Override
	public List<Map<String, Object>> getWorkshopWorkgroupWorkTeamByCode(
			Map<String,Object> map) {
		// TODO Auto-generated method stub
		return workgroupNoDao.getWorkshopWorkgroupWorkTeamByCode(map);
	}


}
