package com.byd.wms.business.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.wms.business.modules.config.dao.WmsCoreWhAreaDao;
import com.byd.wms.business.modules.config.dao.WmsCoreWhBinDao;
import com.byd.wms.business.modules.config.dao.WmsCoreWhBinSeqDao;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinSeqEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinSeqService;

@Service("wmsCoreWhBinSeqService")
public class WmsCoreWhBinSeqServiceImpl extends ServiceImpl<WmsCoreWhBinSeqDao, WmsCoreWhBinSeqEntity> implements WmsCoreWhBinSeqService {
	@Autowired
	private WmsCoreWhBinSeqDao wmsCoreWhBinSeqDao;
	@Autowired
	private WmsCoreWhAreaDao wmsCoreWhBinAreaDao;
	@Autowired
	private WmsCoreWhBinDao wmsCoreWhBinDao;
	@Override
	@Transactional
	public void updateSeq(WmsCoreWhBinEntity bin) {
        List<WmsCoreWhBinSeqEntity> list=new ArrayList<WmsCoreWhBinSeqEntity>();
        // 仓库存储区：空储位搜索顺序
        List<WmsCoreWhAreaEntity> arealist=wmsCoreWhBinAreaDao.selectList(new EntityWrapper<WmsCoreWhAreaEntity>()
	    		 .eq("DEL","0").eq("WH_NUMBER", bin.getWhNumber())
	    		 .eq("STORAGE_AREA_CODE", bin.getStorageAreaCode()));
        String seqType="00"; // 默认按储位编码排序
        if(arealist.size()>0) {
        	WmsCoreWhAreaEntity area=arealist.get(0);
        	String binSearchSequence=area.getBinSearchSequence();
        	if(binSearchSequence!=null && !binSearchSequence.equals("")) {
        		seqType=area.getBinSearchSequence();
        	}
        }
        List<WmsCoreWhBinEntity> seqbinList=null;
        if(seqType.equals("00")) { // 默认按储位编码排序
        	seqbinList=wmsCoreWhBinDao.selectList(
      	    		 new EntityWrapper<WmsCoreWhBinEntity>()
      	    		 .eq("DEL","0").eq("WH_NUMBER", bin.getWhNumber())
      	    		 .eq("STORAGE_AREA_CODE", bin.getStorageAreaCode())
      	    		 .orderBy("BIN_CODE"));
        }else { // 按坐标排序
        	seqbinList=wmsCoreWhBinDao.selectList(
      	    		 new EntityWrapper<WmsCoreWhBinEntity>()
      	    		 .eq("DEL","0").eq("WH_NUMBER", bin.getWhNumber())
      	    		 .eq("STORAGE_AREA_CODE", bin.getStorageAreaCode())
      	    		 .orderBy("BIN_ROW").orderBy("BIN_FLOOR").orderBy("BIN_COLUMN"));
        }
        
	    for(int i=0;i<seqbinList.size();i++) {
	    	WmsCoreWhBinEntity entity=seqbinList.get(i);
	    	WmsCoreWhBinSeqEntity bean=new WmsCoreWhBinSeqEntity();
	    	bean.setWhNumber(entity.getWhNumber());
	    	bean.setSeqType(seqType);
	    	bean.setSeqno(i);
	    	bean.setBinCode(entity.getBinCode());
	    	bean.setEditor(entity.getEditor());
	    	bean.setEditDate(entity.getEditDate());
	    	list.add(bean);
	    }
	    if(seqbinList.size()>0) {
	    	Map<String,Object> param=new HashMap<String,Object>();
		    param.put("whNumber", bin.getWhNumber());
		    param.put("binCodeList", seqbinList);
		    wmsCoreWhBinSeqDao.delBinSeq(param);
	    }
	    this.insertBatch(list);
	}
	@Override
	public void updateSeq(List<WmsCoreWhBinEntity> bin) {
		
	}
    
}
