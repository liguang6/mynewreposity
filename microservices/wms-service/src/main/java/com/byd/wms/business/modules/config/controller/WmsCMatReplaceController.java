package com.byd.wms.business.modules.config.controller;


import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCMatReplaceEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatReplaceService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 物料替代数据管理
 *
 */
@RestController
@RequestMapping("config/matreplace")
public class WmsCMatReplaceController {
    @Autowired
    private WmsCMatReplaceService wmsCMatReplaceService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatReplaceService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        WmsCMatReplaceEntity wmsCMatReplace = wmsCMatReplaceService.selectById(id);

        return R.ok().put("wmsCMatReplace", wmsCMatReplace);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatReplaceEntity wmsCMatReplace){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",wmsCMatReplace.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS", wmsCMatReplace.getWerks()).
                eq("MATNR", wmsCMatReplace.getMatnr()));
    	if(materialList.size()==0) {
    		return R.error(wmsCMatReplace.getWerks()+"未维护该SAP物料!");
    	}
    	
    	wmsCMatReplaceService.insert(wmsCMatReplace);

        return R.ok();
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatReplaceEntity wmsCMatReplace){
    	
    	wmsCMatReplaceService.updateById(wmsCMatReplace);
		return R.ok();
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatReplaceEntity wmsCMatReplace){
    	
    	wmsCMatReplaceService.updateById(wmsCMatReplace);
        
        return R.ok();
        
    }
}

	

