package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCoreWhBinDao;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;

@Service("wmsCoreWhBinService")
public class WmsCoreWhBinServiceImpl extends ServiceImpl<WmsCoreWhBinDao, WmsCoreWhBinEntity> implements WmsCoreWhBinService {
	@Autowired
	private WmsCoreWhBinDao wmsCoreWhBinDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String whNumber = params.get("whNumber") == null?null:String.valueOf(params.get("whNumber"));
        String storageAreaCode = params.get("storageAreaCode") == null?null:String.valueOf(params.get("storageAreaCode"));
        String binType = params.get("binType") == null?null:String.valueOf(params.get("binType"));
        String binCode = params.get("binCode") == null?null:String.valueOf(params.get("binCode"));
        
        if(StringUtils.isBlank(whNumber)){
        	whNumber = params.get("WH_NUMBER") == null?null:String.valueOf(params.get("WH_NUMBER"));
		}
        if(StringUtils.isBlank(binType)){
        	binType = params.get("BIN_TYPE") == null?null:String.valueOf(params.get("BIN_TYPE"));
		}
        if(StringUtils.isBlank(storageAreaCode)){
        	storageAreaCode = params.get("STORAGE_AREA_CODE") == null?null:String.valueOf(params.get("STORAGE_AREA_CODE"));
		}
        if(StringUtils.isBlank(binCode)){
        	binCode = params.get("BIN_CODE") == null?null:String.valueOf(params.get("BIN_CODE"));
		}
        
        Page<WmsCoreWhBinEntity> page = this.selectPage(
                new Query<WmsCoreWhBinEntity>(params).getPage(),
                new EntityWrapper<WmsCoreWhBinEntity>().like("WH_NUMBER", whNumber).like("BIN_TYPE", binType)
                .like("STORAGE_AREA_CODE", storageAreaCode).like("BIN_CODE", binCode).eq("DEL","0")
        );

        return new PageUtils(page);
    }
    /**
	 * 导入校验，区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list){
		return wmsCoreWhBinDao.validate(list);
	}
	
	public Map<String,Object> queryWhAreaInfo(Map<String, Object> params){
		return wmsCoreWhBinDao.queryWhAreaInfo(params);
	}
	
	/**
	 * 随机存储查找现有库存仓位
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findAlreadyBinForRandom(Map<String, Object> params) {
		return wmsCoreWhBinDao.findAlreadyBinForRandom(params);
	}
	
	/**
	 * 查找空仓位
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findEmptyBin(Map<String, Object> params){
		return wmsCoreWhBinDao.findEmptyBin(params);
	}
	
	/**
	 * 查找现有库存附近的空仓位， 现有库存仓位都放满时使用
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findEmptyBinForNeighbor(Map<String, Object> params) {
		return wmsCoreWhBinDao.findEmptyBinForNeighbor(params);
	}
	
	/**
	 * 下架更新储位占用存储单元
	 * @param params
	 * @return
	 */
	@Override
	public int updateBinStorageUnit(List<Map<String,Object>> params) {
		return wmsCoreWhBinDao.updateBinStorageUnit(params);
	}
	public int merge(List<WmsCoreWhBinEntity> list) {
		// TODO Auto-generated method stub
		return wmsCoreWhBinDao.merge(list);
	}
	@Override
	public List<WmsCoreWhBinEntity> queryBinCode(Map<String, Object> params) {
		Map<String, Object> condMap=new HashMap<String, Object>();
		condMap.put("WH_NUMBER", params.get("WH_NUMBER"));
		condMap.put("BIN_CODE", params.get("BIN_CODE"));
		condMap.put("DEL", params.get("DEL"));
		List<WmsCoreWhBinEntity> retList=this.selectByMap(condMap);
		return retList;
	}
}
