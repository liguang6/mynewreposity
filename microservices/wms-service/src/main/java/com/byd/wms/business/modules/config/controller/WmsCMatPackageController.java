package com.byd.wms.business.modules.config.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatPackageService;
import com.byd.wms.business.modules.config.service.WmsCVendorService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 物料包装规格配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
@RestController
@RequestMapping("config/matPackage")
public class WmsCMatPackageController {
    @Autowired
    private WmsCMatPackageService wmsCMatPackageService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Autowired
    private WmsCVendorService wmsCVendorService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatPackageService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
    	Map<String, Object> result=new HashMap<String,Object>();
    	result.put("head", wmsCMatPackageService.getHeadById(id));
        List<Map<String,Object>> list = wmsCMatPackageService.getItemByHeadId(id);
        PageUtils page = new PageUtils(list, list.size(), list.size(), 1);
        return R.ok().put("matPackage", result).put("page", page);
    }

    /**
     * 保存
     */
    @RequestMapping("/merge")
    public R merge(@RequestParam Map<String, Object> param){
   
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
				new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",param.get("werks")).
				eq("MATNR",param.get("matnr")));
	    if(materialList.size()==0) {
	    	return R.error("SAP物料信息未维护！");
	    }
//    	List<WmsCVendor> vendorList=wmsCVendorService.selectList(new EntityWrapper<WmsCVendor>()
//	    		 .eq("LIFNR", param.get("lifnr")).eq("WERKS", param.get("werks"))
//	             .eq("DEL_FLAG","0"));
//    	if(vendorList.size()==0) {
//    		return R.error(param.get("werks")+"供应商代码【"+param.get("lifnr")+"】未配置!");
//    	}
	    wmsCMatPackageService.merge(param);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> param){
//    	List<WmsCVendor> vendorList=wmsCVendorService.selectList(new EntityWrapper<WmsCVendor>()
//	    		 .eq("LIFNR", param.get("lifnr")).eq("WERKS", param.get("werks"))
//	             .eq("DEL_FLAG","0"));
//	   	if(vendorList.size()==0) {
//	   		return R.error(param.get("werks")+"供应商代码【"+param.get("lifnr")+"】未配置!");
//	   	}
    	wmsCMatPackageService.merge(param);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Map<String, Object> param){
    	wmsCMatPackageService.delete(param);
		return R.ok();
    }

}
