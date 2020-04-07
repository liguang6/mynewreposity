package com.byd.admin.modules.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byd.utils.UserUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.sys.dao.SysColumnConfigurationDao;
import com.byd.admin.modules.sys.entity.SysColumnConfigurationEntity;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.service.SysColumnConfigurationService;
import com.byd.utils.Constant;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service
public class SysColumnConfigurationImpl extends ServiceImpl<SysColumnConfigurationDao, SysColumnConfigurationEntity>
implements SysColumnConfigurationService {

    @Autowired
    SysColumnConfigurationDao SysColumnConfigurationDao;
	@Autowired
	private UserUtils userUtils;


    public List queryDataPermission(Map<String, String> params) {
        List lists = baseMapper.queryColumnConfiguration(params);
        return  lists;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String gridId = (String)params.get("menuId");
		Page<SysColumnConfigurationEntity> page = this.selectPage(
			new Query<SysColumnConfigurationEntity>(params).getPage(),
			new EntityWrapper<SysColumnConfigurationEntity>()
				.eq(StringUtils.isNotBlank(gridId),"grid_Id", gridId)
				 .orderBy("idx_Seq")
				//.addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		return new PageUtils(page);
    }

	@Override
	public List<SysColumnConfigurationEntity> ColumnConfiguration(Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveColumnConfiguration(List<Map> columnList) {
		String descDef = "";
		String columnCode = "";
		for (Map<String,Object>columnmap: columnList) {
			SysColumnConfigurationEntity syscolnmu = new SysColumnConfigurationEntity();
			
			String hideRmk = columnmap.get("hideRmk").toString();
			if (hideRmk.equals("是") || hideRmk.equals("YES")) {
				hideRmk = "1";
			} else {
				hideRmk = "0";
			}
			String defHide = columnmap.get("defHide").toString();
			if (defHide.equals("是") || defHide.equals("YES")) {
				defHide = "1";
			} else {
				defHide = "0";
			}
			
			Long columnId=(long) -1;
			String columnNo= columnmap.get("columnNo").toString();
			Long gridId=Long.valueOf(columnmap.get("gridId").toString()) ;
			String columnName= columnmap.get("columnName").toString();
			Long idxSeq=Long.valueOf(columnmap.get("idxSeq").toString());
			if (null != columnmap.get("columnWidth") && !columnmap.get("columnWidth").equals("")) {
				Long columnWidth=Long.valueOf(columnmap.get("columnWidth").toString());
				syscolnmu.setColumnWidth(columnWidth);
			}else {
				syscolnmu.setColumnWidth((long)60);
			}
			
			if (null != columnmap.get("columnId") && !columnmap.get("columnId").equals("")) {
				columnId=Long.valueOf(columnmap.get("columnId").toString());
				syscolnmu.setcolumnId(columnId);
			}
			syscolnmu.setHideRmk(hideRmk);
			syscolnmu.setDefHide(defHide);
			syscolnmu.setcolumnName(columnName);
			syscolnmu.setColumnNo(columnNo);
			syscolnmu.setGridId(gridId);
			syscolnmu.setIdxSeq(idxSeq);
			
			
			Map<String,Object> queryparam = new HashMap<String,Object>();
			queryparam.put("grid_Id", gridId);
			queryparam.put("column_No", columnNo);
			queryparam.put("column_Id", columnId);
			List<SysColumnConfigurationEntity> locallist = super.selectByMap(queryparam);
			if (locallist.isEmpty()) {
				super.insert(syscolnmu);
			} else {
				super.update(syscolnmu, new EntityWrapper<SysColumnConfigurationEntity>()
						.eq("column_Id", columnId)
						.eq(StringUtils.isNotBlank(columnNo),"column_No", columnNo)

						);
			}
    	}
		
	}

	@Override
	public PageUtils queryUserGridPage(Map<String, Object> params) {
		String gridNo = (String)params.get("gridNo");
//		 List<String> gridIds = SysColumnConfigurationDao.queryGridIdByGridNo(gridNo);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
        int start = 0;int end = 6000;
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }
        params.put("start", start);
        params.put("end", end);
        params.put("gridNo", gridNo);
        List<Map<String,Object>> records = SysColumnConfigurationDao.queryUserGrid(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));
        if(records.size()>0) {      	
            page.setRecords(records);
            page.setTotal(SysColumnConfigurationDao.getUserGridPageCount(params));
         
        }else {
        	records = SysColumnConfigurationDao.queryUserGridDefault(params);
        	page.setRecords(records);
            page.setTotal(SysColumnConfigurationDao.getUserGridPageDefaultCount(params));
        }
        

		return new PageUtils(page);
	}

	@Override
	public void saveUserConfiguration(Map<String, Object> params) {
		String columnListStr=params.get("columnList").toString();
    	SysUserEntity user = null;
    	List<Map> columnList = JSONObject.parseArray(columnListStr, Map.class);
		String descDef = "";
		String columnCode = "";
		for (Map<String,Object>columnmap: columnList) {
			Map<String,Object> usermap=new HashMap<String,Object>(); 
			String hideRmk = columnmap.get("HIDERMK").toString();
			if (hideRmk.equals("是") || hideRmk.equals("YES")) {
				usermap.put("hideRmk", 1);
			} else {
				usermap.put("hideRmk", 0);
			}
			
			Long columnId=Long.valueOf(columnmap.get("COLUMNID").toString());
			Long idxSeq=Long.valueOf(columnmap.get("IDXSEQ").toString());
			Long columnWidth=Long.valueOf(columnmap.get("COLUMNWIDTH").toString());
			usermap.put("idxSeq", idxSeq);
			usermap.put("columnWidth", columnWidth);
			usermap.put("columnId", columnId);
			usermap.put("userId", userUtils.getUserId());
			int count = SysColumnConfigurationDao.queryUserConfiguration(usermap);
			if (count==0) {
				SysColumnConfigurationDao.insertUserConfiguration(usermap);
			} else {
				SysColumnConfigurationDao.updateUserConfiguration(usermap);
			}
    	}
		
		
	}

	@Override
	public void deleteUserConfiguration(List<Map> columnList) {
		String descDef = "";
		String columnCode = "";
		for (Map<String,Object>columnmap: columnList) {
			SysColumnConfigurationEntity syscolnmu = new SysColumnConfigurationEntity();						
			Long columnId=(long) -1;
			String columnNo= columnmap.get("columnNo").toString();
			Long gridId=Long.valueOf (columnmap.get("gridId").toString());		
			if (null != columnmap.get("columnId") && !columnmap.get("columnId").equals("")) {
				columnId=Long.valueOf(columnmap.get("columnId").toString());
				syscolnmu.setcolumnId(columnId);
			}
			Map<String,Object> queryparam = new HashMap<String,Object>();
			queryparam.put("grid_Id", gridId);
			queryparam.put("column_No", columnNo);
			queryparam.put("column_Id", columnId);
			List<SysColumnConfigurationEntity> locallist = super.selectByMap(queryparam);
			super.deleteById(columnId);
    	}
		
	}





}
