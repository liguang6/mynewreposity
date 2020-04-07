package com.byd.wms.business.modules.config.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAddressEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWh_AddressEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCoreWhAddressService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库信息维护
 *
 * @author tangj 
 * @since 2018-07-31
 */
@RestController
@RequestMapping("config/wh")
public class WmsCoreWhController {
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsCoreWhAddressService wmsCoreWhAddressService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = wmsCoreWhService.queryPage(params);
        PageUtils page = wmsCoreWhService.queryPagenew(params);
        return R.ok().put("page", page);
    }
    /**
     * 根据ID查找记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Map<String,Object> params){
    	String id = params.get("id") == null?null:params.get("id").toString();
    	String addrID = params.get("addrID") == null?null:params.get("addrID").toString();
        WmsCoreWhEntity wh = wmsCoreWhService.selectById(id);
        
        WmsCoreWh_AddressEntity wh_address=new WmsCoreWh_AddressEntity();//返回对象
        wh_address.setId(wh.getId());
        wh_address.setWerks(wh.getWerks());
        wh_address.setWhNumber(wh.getWhNumber());
        
        Map<String,Object> condMap=new HashMap<String,Object>();
        condMap.put("id", addrID);
        condMap.put("werks", wh.getWerks());
        condMap.put("wh_number", wh.getWhNumber());
        condMap.put("del", "0");

		List<WmsCoreWhAddressEntity> retList=wmsCoreWhAddressService.selectByMap(condMap);

        if(retList!=null&&retList.size()>0){
        	wh_address.setId(retList.get(0).getId());
        	wh_address.setWhName(retList.get(0).getWhName());
        	wh_address.setLanguage(retList.get(0).getLanguage());
        	wh_address.setCountry(retList.get(0).getCountry());
        	wh_address.setProvince(retList.get(0).getProvince());
        	wh_address.setCity(retList.get(0).getCity());
        	wh_address.setRegion(retList.get(0).getRegion());
        	wh_address.setStreet(retList.get(0).getStreet());
        	wh_address.setContacts(retList.get(0).getContacts());
        	wh_address.setTel(retList.get(0).getTel());
        	wh_address.setMemo(retList.get(0).getMemo());
        	wh_address.setLgortNo(retList.get(0).getLgortNo());
        }
        return R.ok().put("wh", wh_address);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreWh_AddressEntity wh){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",wh.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
    	List<WmsCoreWhEntity> list=wmsCoreWhService.selectList(
    			new EntityWrapper<WmsCoreWhEntity>().eq("WERKS", wh.getWerks())
    			.eq("WH_NUMBER", wh.getWhNumber())
    			.eq("DEL","0"));
		List<WmsCoreWhAddressEntity> whAdresslist =wmsCoreWhAddressService.selectList(
				new EntityWrapper<WmsCoreWhAddressEntity>().eq("WERKS", wh.getWerks())
						.eq("WH_NUMBER", wh.getWhNumber())
						.eq("LGORT_NO", wh.getLgortNo())
						.eq("DEL","0"));
//    	if(list.size()>0) {
//    		return R.error("该仓库记录已维护！");
//    	}else {
			//System.err.println("============================================"+wh.toString());
    	if(list.size() < 1) {
    		WmsCoreWhEntity wh_entity=new WmsCoreWhEntity();
			wh_entity.setDel(wh.getDel());
			wh_entity.setWerks(wh.getWerks());
			wh_entity.setWhNumber(wh.getWhNumber());
			wh_entity.setEditor(wh.getEditor());
			wh_entity.setEditDate(wh.getEditDate());
			wmsCoreWhService.insert(wh_entity);
    	}

    	if(whAdresslist.size()>0){
			return R.error("该仓库/库位记录已存在！");
		}else if(whAdresslist.size()<1){
			WmsCoreWhAddressEntity whAdress=new WmsCoreWhAddressEntity();
			whAdress.setWerks(wh.getWerks());
			whAdress.setWhNumber(wh.getWhNumber());
			whAdress.setLanguage(wh.getLanguage());
			whAdress.setWhName(wh.getWhName());
			whAdress.setCountry(wh.getCountry());
			whAdress.setProvince(wh.getProvince());
			whAdress.setCity(wh.getCity());
			whAdress.setRegion(wh.getRegion());
			whAdress.setStreet(wh.getStreet());
			whAdress.setContacts(wh.getContacts());
			whAdress.setTel(wh.getTel());
			whAdress.setMemo(wh.getMemo());
			whAdress.setDel(wh.getDel());
			whAdress.setLgortNo(wh.getLgortNo());
			//System.err.println("whAdress "+whAdress.toString());
			wmsCoreWhAddressService.insert(whAdress);
		}

			return R.ok();
//		}
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreWh_AddressEntity wh_address){
		System.err.println(wh_address.toString());
    	WmsCoreWhEntity wh=new WmsCoreWhEntity();
//    	wh.setId(wh_address.getId());
    	wh.setEditor(wh_address.getEditor());
    	wh.setEditDate(wh_address.getEditDate());
    	wh.setWerks(wh_address.getWerks());
    	wh.setWhNumber(wh_address.getWhNumber());
        wmsCoreWhService.updateById(wh);
        
        WmsCoreWhAddressEntity whAdressTemp=new WmsCoreWhAddressEntity();
        
        Map<String,Object> condMap=new HashMap<String,Object>();
        condMap.put("id", wh_address.getId());
        condMap.put("werks", wh.getWerks());
        condMap.put("wh_number", wh.getWhNumber());
        condMap.put("del", "0");
        List<WmsCoreWhAddressEntity> retList=wmsCoreWhAddressService.selectByMap(condMap);//根据工厂和仓库号查询
        
        if(retList!=null&&retList.size()>0){
        	whAdressTemp.setId(retList.get(0).getId());
        	whAdressTemp.setContacts(wh_address.getContacts());
        	whAdressTemp.setCity(wh_address.getCity());
        	whAdressTemp.setCountry(wh_address.getCountry());
        	whAdressTemp.setLanguage(wh_address.getLanguage());
        	whAdressTemp.setMemo(wh_address.getMemo());
        	whAdressTemp.setProvince(wh_address.getProvince());
        	whAdressTemp.setRegion(wh_address.getRegion());
        	whAdressTemp.setStreet(wh_address.getStreet());
        	whAdressTemp.setTel(wh_address.getTel());
        	whAdressTemp.setWerks(wh_address.getWerks());
        	whAdressTemp.setWhName(wh_address.getWhName());
        	whAdressTemp.setWhNumber(wh_address.getWhNumber());
			whAdressTemp.setLgortNo(wh_address.getLgortNo());
        	wmsCoreWhAddressService.updateById(whAdressTemp);
        }else{
        	whAdressTemp.setContacts(wh_address.getContacts());
        	whAdressTemp.setCity(wh_address.getCity());
        	whAdressTemp.setCountry(wh_address.getCountry());
        	whAdressTemp.setLanguage(wh_address.getLanguage());
        	whAdressTemp.setMemo(wh_address.getMemo());
        	whAdressTemp.setProvince(wh_address.getProvince());
        	whAdressTemp.setRegion(wh_address.getRegion());
        	whAdressTemp.setStreet(wh_address.getStreet());
        	whAdressTemp.setTel(wh_address.getTel());
        	whAdressTemp.setWerks(wh_address.getWerks());
        	whAdressTemp.setWhName(wh_address.getWhName());
        	whAdressTemp.setWhNumber(wh_address.getWhNumber());
        	whAdressTemp.setDel("0");
        	whAdressTemp.setLgortNo(wh_address.getLgortNo());
        	wmsCoreWhAddressService.insert(whAdressTemp);
        }
        	
        return R.ok();
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Map<String,Object> params){
    	String id = params.get("id") == null?null:params.get("id").toString();
    	String addrID = params.get("addrID") == null?null:params.get("addrID").toString();
		
		WmsCoreWhEntity whret=wmsCoreWhService.selectById(id);
		if(whret!=null){
			WmsCoreWhAddressEntity whAddress_update=new WmsCoreWhAddressEntity();
        	whAddress_update.setId(Long.parseLong(addrID));
        	whAddress_update.setDel("X");
        	wmsCoreWhAddressService.updateById(whAddress_update);
        	
			Map<String,Object> condMap=new HashMap<String,Object>();
	        condMap.put("werks", whret.getWerks());
	        condMap.put("wh_number", whret.getWhNumber());
	        condMap.put("del", "0");
	        List<WmsCoreWhAddressEntity> retList=wmsCoreWhAddressService.selectByMap(condMap);//根据工厂和仓库号查询
	        //行表都删除了，才删除主表
	        if(retList.size() < 1){
	        	WmsCoreWhEntity entity = new WmsCoreWhEntity();
	    		entity.setId(Long.parseLong(id));
	    		entity.setDel("X");
	    		wmsCoreWhService.updateById(entity);
	        }
		}
		return R.ok();
    }
    
    /**
     * 批量删除(软删)
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("sys:dict:delete")
    public R delete(@RequestBody Long[] ids){
        wmsCoreWhService.deleteBatch(Arrays.asList(ids));
        return R.ok();
    }
    
}
