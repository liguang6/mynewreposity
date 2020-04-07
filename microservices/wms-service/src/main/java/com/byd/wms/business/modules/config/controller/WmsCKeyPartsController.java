package com.byd.wms.business.modules.config.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.service.WmsCKeyPartsService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 工厂关键零部件配置表
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@RestController
@RequestMapping("config/keyparts")
public class WmsCKeyPartsController {
    @Autowired
    private WmsCKeyPartsService wmsCKeyPartsService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCKeyPartsService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCKeyPartsEntity wmsCKeyParts = wmsCKeyPartsService.selectById(id);

        return R.ok().put("wmsCKeyParts", wmsCKeyParts);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCKeyPartsEntity wmsCKeyParts){
        
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
				new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",wmsCKeyParts.getWerks()).
				eq("MATNR",wmsCKeyParts.getMatnr()));
	    if(materialList.size()==0) {
	    	return R.error("SAP物料信息未维护！");
	    }
        List<WmsCKeyPartsEntity> list=wmsCKeyPartsService.selectList(
    			new EntityWrapper<WmsCKeyPartsEntity>().eq("WERKS", wmsCKeyParts.getWerks())
    			.eq("MATNR", wmsCKeyParts.getMatnr()).eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("关键零部件记录已维护！");
    	}else {
    		wmsCKeyPartsService.insert(wmsCKeyParts);
        	return R.ok();
    	}
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCKeyPartsEntity wmsCKeyParts){
        
        wmsCKeyPartsService.updateAllColumnById(wmsCKeyParts);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCKeyPartsService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCKeyPartsEntity wmsCKeyParts){
    	
		wmsCKeyPartsService.updateById(wmsCKeyParts);
		return R.ok();
    }
    
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestBody()Map<String,Object> param) throws IllegalAccessException{
		Gson gson=new Gson();
		List<WmsCKeyPartsEntity> detailList =gson.fromJson((String) param.get("detailList"),new TypeToken<List<WmsCKeyPartsEntity>>() {}.getType()); 
		
		for(WmsCKeyPartsEntity detail:detailList){
			detail.setCreator(param.get("USERNAME").toString());
			detail.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			detail.setEditor(param.get("USERNAME").toString());
			detail.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		}
		wmsCKeyPartsService.batchSave(detailList);
		return new R();
	}
	/**
	 * 粘贴数据校验
	 * @return 
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestBody Map<String,Object> param){
		Gson gson=new Gson();
		List<WmsCKeyPartsEntity> list =gson.fromJson((String) param.get("saveData"),new TypeToken<List<WmsCKeyPartsEntity>>() {}.getType());
		
		for(WmsCKeyPartsEntity entity:list){
			String msg="";
			if(isEmpty(entity.getMatnr())){
				msg+="SAP料号不能为空;";
			}else {
				List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
						new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",entity.getWerks()).
						eq("MATNR",entity.getMatnr()));
			    if(materialList.size()==0) {
			    	msg+="SAP料号信息未维护;";
			    }else {
			    	entity.setMaktx(materialList.get(0).getMaktx());
			    }
			}
			entity.setMsg(msg);
		}
		return new R().put("list", list);
	}
	public boolean isEmpty(Object obj) {
		boolean result=false;
		if(obj==null) {
			return true;
		}
		if(StringUtils.isBlank(obj.toString())) {
			return true;
		}
		return result;
	}
}
