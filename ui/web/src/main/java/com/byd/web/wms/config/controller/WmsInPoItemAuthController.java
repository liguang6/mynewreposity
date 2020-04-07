package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsInPoItemAuthEntity;
import com.byd.web.wms.config.service.WmsInPoItemAuthRemote;
/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月31日 下午1:17:47 
 * 类说明 
 */
@RestController
@RequestMapping("/config/poAuth")
public class WmsInPoItemAuthController {
	@Autowired
	WmsInPoItemAuthRemote wmsinpoitemauthRemote;
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmsinpoitemauthRemote.list(params);	
	}
	/*
	 * 根据采购单号获取采购列表
	 */
	@RequestMapping("/Polist")
	public R getPolist(@RequestParam Map<String, Object> params){
		
		//查询登陆用户对应的工厂
    	
    	//params.put("werksList", werksList);
		
		return wmsinpoitemauthRemote.getPolist(params);
	}
	
	@RequestMapping("/PoAuthSave")
	public R savePoData(@RequestParam Map<String, Object> params){
		return wmsinpoitemauthRemote.savePoData(params);
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsinpoitemauthRemote.info(id);
    }
    
    @RequestMapping("/update")
	public R update(@RequestBody WmsInPoItemAuthEntity entity){
		//validate
		entity.setEditor("");
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		wmsinpoitemauthRemote.update(entity);
		return R.ok();
	}
    
    @RequestMapping("/del")
	public R delete(Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsInPoItemAuthEntity entity = new WmsInPoItemAuthEntity();
		entity.setId(id);
		entity.setDel("1");
		wmsinpoitemauthRemote.update(entity);
		return R.ok();
	}
    
    @RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsInPoItemAuthEntity entity = new WmsInPoItemAuthEntity();
			entity.setId(Long.parseLong(id[i]));
			entity.setDel("1");
			wmsinpoitemauthRemote.update(entity);
		}
		return R.ok();
	}
    
    @RequestMapping("/queryByEbeln")
	public R queryByEbeln(@RequestParam Map<String, Object> params){
    	return wmsinpoitemauthRemote.queryByEbeln(params);
    }
    /**
     * 小写的参数
     * @param params
     * @return
     */
    @RequestMapping("/queryByEbelnMin")
	public R queryByEbelnMin(@RequestParam Map<String, Object> params){
    	
    	return wmsinpoitemauthRemote.queryByEbelnMin(params);
    }
    
    @RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
    	List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<WmsInPoItemAuthEntity> entityList = new ArrayList<WmsInPoItemAuthEntity>();
		int index=0;
		if(sheet != null && sheet.size() > 0){
			
			for(String[] row:sheet){
				WmsInPoItemAuthEntity entity = new WmsInPoItemAuthEntity();
				
				entity.setWerks(row[0]);
				
				entity.setEbeln(row[1]);
				
				entity.setEbelp(row[2]);
				
				entity.setMatnr(row[3]);
				
				entity.setTxz01(row[4]);
				
				entity.setLifnr(row[5]);
				
				BigDecimal maxmenge_d=BigDecimal.ZERO;
				if(!"".equals(row[6])||row[6]!=null){
					maxmenge_d=new BigDecimal(row[6]);
				}
				entity.setMaxMenge(maxmenge_d);
				
				
				entity.setAuthWerks(row[7]);
				
				
				entity.setRowNo(String.valueOf(++index));
				
				entityList.add(entity);
				
			}
		}
		return wmsinpoitemauthRemote.previewExcel(entityList);
	}
    @RequestMapping("/import")
	public R upload(@RequestParam Map<String, Object> params) throws IOException{
    	return wmsinpoitemauthRemote.upload(params);
    }
}
