package com.byd.web.sys.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.web.sys.dao.SysLocaleDao;
import com.byd.web.sys.entity.SysLocaleEntity;
import com.byd.web.sys.service.SysLocaleService;

/**
 * @author ren.wei3
 *
 */
@Service("sysLocaleService")
public class SysLocaleServiceImpl extends ServiceImpl<SysLocaleDao, SysLocaleEntity> implements SysLocaleService{

	private static String dataTime = null;
	
	@Autowired
	private SysLocaleDao sysLocaleDao;
	
	@Override
    public PageUtils queryPage(Map<String, Object> params) {
		Page<SysLocaleEntity> page = null;
        String pkey = (String)params.get("pkey");
        String mdesc = (String)params.get("mdesc");
        page = new Query<SysLocaleEntity>(params).getPage();
        
        if (StringUtils.isBlank(pkey) && StringUtils.isBlank(mdesc)) {
        	return new PageUtils(page);
        }
        
        pkey = pkey.toUpperCase();
        if(page.getLimit()==-1) {
        	List<SysLocaleEntity> records = this.selectList(new EntityWrapper<SysLocaleEntity>()
                    .like(StringUtils.isNotBlank(pkey),"P_KEY", pkey)
                    .like(StringUtils.isNotBlank(mdesc),"MIDDLE_DESC", mdesc)
                    .orderBy("ID"));
			page.setRecords(records);
			page.setTotal(records.size());
			page.setSize(records.size());
        }else {
            page = this.selectPage(
            		page,
                    new EntityWrapper<SysLocaleEntity>()
                        .like(StringUtils.isNotBlank(pkey),"P_KEY", pkey)
                        .like(StringUtils.isNotBlank(mdesc),"MIDDLE_DESC", mdesc)
                        .orderBy("ID")
                        
            );
        }

        return new PageUtils(page);
	}
	
	/**
	 * 新增&修改方法
	 */
	public void saveLanguage(List<Map> languageList) {
		String descDef = "";
		String languageCode = "";
		for (Map<String,Object> languagemap: languageList) {
			SysLocaleEntity syslocale = new SysLocaleEntity();
			
			String pkey = languagemap.get("pKey").toString().toUpperCase();
			String languageType = languagemap.get("lKey").toString();
			if (languageType.equals("中文") || languageType.equals("Chinese")) {
				languageCode = "ZH_CN";
			} else {
				languageCode = "EN_US";
			}
			String desctype = languagemap.get("descDef").toString();
			if (desctype.equals("中") || desctype.equals("Middle")) {
				descDef = "M";
			} else if(desctype.equals("长") || desctype.equals("Long")) {
				descDef = "L";
			} else {
				descDef = "S";
			}
			if (null != languagemap.get("id") && !languagemap.get("id").equals("")) {
				syslocale.setId(Long.valueOf(languagemap.get("id").toString()));
			}
			syslocale.setpKey(pkey);
			syslocale.setlKey(languageCode);
			syslocale.setDescDef(descDef);
			syslocale.setMiddleDesc(languagemap.get("middleDesc").toString());
			syslocale.setLongDesc(languagemap.get("longDesc").toString());
			syslocale.setShortDesc(languagemap.get("shortDesc").toString());
			syslocale.setModifyDate(new Date());
			
			Map<String,Object> queryparam = new HashMap<String,Object>();
			queryparam.put("P_KEY", pkey);
			queryparam.put("L_KEY", languageCode);
			List<SysLocaleEntity> locallist = super.selectByMap(queryparam);
			if (locallist.isEmpty()) {
				super.insert(syslocale);
			} else {
				super.update(syslocale, new EntityWrapper<SysLocaleEntity>()
						.eq(StringUtils.isNotBlank(pkey),"P_KEY", pkey)
						.eq(StringUtils.isNotBlank(languageCode),"L_KEY", languageCode));
			}
//			super.insertOrUpdate(syslocale);
    	}
		
	}
	
	public List<SysLocaleEntity> getAllLocale() {
		dataTime = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		return sysLocaleDao.getAllLocale();
	}
	
	public List<SysLocaleEntity> refreshModifyData() {
		String needRefDate = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		EntityWrapper<SysLocaleEntity> param = new EntityWrapper<SysLocaleEntity>();
		param.where("modify_date>= to_date({0},'yyyy-MM-dd hh24:mi:ss')", dataTime);
		List<SysLocaleEntity> records = this.selectList(param);
		dataTime = needRefDate;
		return records;
	}
}
