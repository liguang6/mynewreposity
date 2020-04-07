package com.byd.wms.business.modules.config.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsInPoItemAuthDao;
import com.byd.wms.business.modules.config.entity.WmsInPoItemAuthEntity;
import com.byd.wms.business.modules.config.service.WmsInPoItemAuthService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月31日 上午10:39:46 
 * 类说明 
 */
@Service
public class WmsInPoItemAuthServiceImpl extends ServiceImpl<WmsInPoItemAuthDao,WmsInPoItemAuthEntity> implements WmsInPoItemAuthService{
	
	
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		String lifnr = params.get("lifnr") == null?null:String.valueOf(params.get("lifnr"));
		String ebeln = params.get("ebeln") == null?null:String.valueOf(params.get("ebeln"));
		String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
		
		if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
		if(StringUtils.isBlank(lifnr)){
			lifnr = params.get("LIFNR") == null?null:String.valueOf(params.get("LIFNR"));
		}
		if(StringUtils.isBlank(ebeln)){
			ebeln = params.get("EBELN") == null?null:String.valueOf(params.get("EBELN"));
		}
		if(StringUtils.isBlank(matnr)){
			matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
		}
		
		Page<WmsInPoItemAuthEntity> page = this.selectPage(new Query<WmsInPoItemAuthEntity>(params).getPage(),
				new EntityWrapper<WmsInPoItemAuthEntity>().in("werks", werks).like("lifnr", lifnr)
				.like("ebeln", ebeln).like("matnr", matnr).eq("del", "0")//已经标识为删除的数据不显示
				);
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getPolist(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return baseMapper.getPolist(map);
	}

	@Override
	public void savePoAuthData(Map<String, Object> params) {
		
		String fg=params.get("FG").toString();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){//EBELN
			String ebeln=jarr.getJSONObject(i).getString("EBELN");
			String werks=jarr.getJSONObject(i).getString("WERKS");
			String lifnr=jarr.getJSONObject(i).getString("LIFNR");
			String ebelp=jarr.getJSONObject(i).getString("EBELP");
			String matnr=jarr.getJSONObject(i).getString("MATNR");
			String txz01=jarr.getJSONObject(i).getString("TXZ01");
			String maxMenge=jarr.getJSONObject(i).getString("MAX_MENGE");
			String authWerks=jarr.getJSONObject(i).getString("AUTHWERKS");
			
			WmsInPoItemAuthEntity entity=new WmsInPoItemAuthEntity();
			entity.setDel("0");
			entity.setCreator(params.get("USERNAME").toString());
			entity.setCreatDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			
			entity.setEbeln(ebeln);
			entity.setWerks(werks);
			entity.setLifnr(lifnr);
			entity.setEbelp(ebelp);
			entity.setMatnr(matnr);
			entity.setTxz01(txz01);
			
			BigDecimal maxMenge_D=BigDecimal.ZERO;
			if(!"".equals(maxMenge)||maxMenge!=null){
				maxMenge_D=new BigDecimal(maxMenge);
			}
			entity.setMaxMenge(maxMenge_D);
			entity.setAuthWerks(authWerks);
			
			Map<String, Object> cond=new HashMap<String, Object>();
			cond.put("EBELN", ebeln);
			cond.put("EBELP", ebelp);
			cond.put("DEL", "0");
			//查询记录是否存在
			List<WmsInPoItemAuthEntity> condret=baseMapper.selectByMap(cond);
			if(condret.size()>0&&"Y".equals(fg)){//存在并且要覆盖
				entity.setId(condret.get(0).getId());
				baseMapper.updateById(entity);
			}else if(condret.size()>0&&"N".equals(fg)){//存在并且不要覆盖
				continue;
			}else{
				baseMapper.insert(entity);
			}
			
			
		}
		
	}
	
	@Override
	public void savePoAuthDataImport(Map<String, Object> params) {
		
		String fg=params.get("FG").toString();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){//EBELN
			String ebeln=jarr.getJSONObject(i).getString("ebeln");
			String werks=jarr.getJSONObject(i).getString("werks");
			String lifnr=jarr.getJSONObject(i).getString("lifnr");
			String ebelp=jarr.getJSONObject(i).getString("ebelp");
			String matnr=jarr.getJSONObject(i).getString("matnr");
			String txz01=jarr.getJSONObject(i).getString("txz01");
			String maxMenge=jarr.getJSONObject(i).getString("maxMenge");
			String authWerks=jarr.getJSONObject(i).getString("authWerks");
			
			WmsInPoItemAuthEntity entity=new WmsInPoItemAuthEntity();
			entity.setDel("0");
			entity.setCreator(params.get("USERNAME").toString());
			entity.setCreatDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			
			entity.setEbeln(ebeln);
			entity.setWerks(werks);
			entity.setLifnr(lifnr);
			entity.setEbelp(ebelp);
			entity.setMatnr(matnr);
			entity.setTxz01(txz01);
			
			BigDecimal maxMenge_D=BigDecimal.ZERO;
			if(!"".equals(maxMenge)||maxMenge!=null){
				maxMenge_D=new BigDecimal(maxMenge);
			}
			entity.setMaxMenge(maxMenge_D);
			entity.setAuthWerks(authWerks);
			
			Map<String, Object> cond=new HashMap<String, Object>();
			cond.put("EBELN", ebeln);
			cond.put("EBELP", ebelp);
			cond.put("DEL", "0");
			//查询记录是否存在
			List<WmsInPoItemAuthEntity> condret=baseMapper.selectByMap(cond);
			if(condret.size()>0&&"Y".equals(fg)){//存在并且要覆盖
				entity.setId(condret.get(0).getId());
				baseMapper.updateById(entity);
			}else if(condret.size()>0&&"N".equals(fg)){//存在并且不要覆盖
				continue;
			}else{
				baseMapper.insert(entity);
			}
			
			
		}
		
	}

}
