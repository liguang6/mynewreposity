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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerTypeEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatManagerService;
import com.byd.wms.business.modules.config.service.WmsCMatManagerTypeService;
import com.byd.wms.business.modules.config.service.WmsCoreWhAreaService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 仓库人料关系模式配置
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@RestController
@RequestMapping("config/matmanagertype")
public class WmsCMatManagerTypeController {
    @Autowired
    private WmsCMatManagerTypeService wmsCMatManagerTypeService;
    @Autowired
    private WmsCMatManagerService wmsCMatManagerService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private WmsCoreWhAreaService wmsCoreWhAreaService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatManagerTypeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCMatManagerTypeEntity wmsCMatManagerType = wmsCMatManagerTypeService.selectById(id);

        return R.ok().put("wmsCMatManagerType", wmsCMatManagerType);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatManagerTypeEntity wmsCMatManagerType){
    	
	    List<WmsCMatManagerTypeEntity> list=wmsCMatManagerTypeService.selectList(
				new EntityWrapper<WmsCMatManagerTypeEntity>().eq("DEL","0")
				.eq("WERKS",wmsCMatManagerType.getWerks())
				.eq("WH_NUMBER",wmsCMatManagerType.getWhNumber())
				.eq("MAT_MANAGER_TYPE",wmsCMatManagerType.getMatManagerType())
				.eq("MANAGER_TYPE",wmsCMatManagerType.getManagerType())
				.eq("MANAGER_STAFF",wmsCMatManagerType.getManagerStaff())
				.eq("AUTHORIZE_CODE",wmsCMatManagerType.getAuthorizeCode())
	    		);
	    if(list.size()>0) {
	    	return R.error("仓库人料关系模式已维护！");
	    }
	    
        wmsCMatManagerTypeService.insert(wmsCMatManagerType);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatManagerTypeEntity wmsCMatManagerType){
        
        wmsCMatManagerTypeService.updateAllColumnById(wmsCMatManagerType);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCMatManagerTypeService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatManagerTypeEntity wmsCMatManagerType){
    	
    	WmsCMatManagerTypeEntity type=wmsCMatManagerTypeService.selectById(wmsCMatManagerType.getId());
    	List<WmsCMatManagerEntity> matManagerList=wmsCMatManagerService.selectList(new EntityWrapper<WmsCMatManagerEntity>()
    			.eq("DEL","0").eq("WH_NUMBER",type.getWhNumber())
				.eq("WERKS",type.getWerks())
				.eq("AUTHORIZE_CODE",type.getAuthorizeCode())
				);
    	if(matManagerList.size()>0) {
    		return R.error("仓库人料关系已配置"+type.getAuthorizeCode()+"授权码，不能删除");
    	}
		wmsCMatManagerTypeService.updateById(wmsCMatManagerType);
		return R.ok();
    }
    /**
	 * 粘贴数据校验
	 * @param excel
	 * @return
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestBody Map<String,Object> params){
		Gson gson=new Gson();
		List<WmsCMatManagerTypeEntity> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<WmsCMatManagerTypeEntity>>() {}.getType());
		String werks="";
		String whNumber="";
		List<Map<String,Object>> matManagerTypeList=commonService.getDictList("MAT_MANAGER_TYPE");
		List<Map<String,Object>> managerTypeList=commonService.getDictList("MANAGER_TYPE");
		for(WmsCMatManagerTypeEntity entity:list){
			String msg="";
			//检查必填信息
			if(isEmpty(entity.getWerks())){
				msg="工厂不能为空;";
			}else {
				if(!werks.equals(entity.getWerks())) {
					List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
							new EntityWrapper<WmsSapPlant>().eq("DEL","0").eq("WERKS",entity.getWerks()));
				    if(plantList.size()==0) {
				    	msg+="工厂信息未维护;";
				    }
				}else {
					werks=entity.getWerks();
				}
			}
			//检查必填信息
			if(isEmpty(entity.getWhNumber())){
				msg="仓库号不能为空;";
			}else {
				if(!whNumber.equals(entity.getWhNumber())) {
					List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
							new EntityWrapper<WmsCoreWhEntity>().eq("DEL","0").eq("WERKS",entity.getWerks()).eq("WH_NUMBER",entity.getWhNumber()));
				    if(whList.size()==0) {
				    	msg+="仓库号信息未维护;";
				    }
				}else {
					whNumber=entity.getWhNumber();
				}
			}
			for(Map<String,Object> map : matManagerTypeList) {
				if(entity.getMatManagerType().equals(map.get("VALUE").toString())) {
					entity.setMatManagerTypeDesc(entity.getMatManagerType());
					entity.setMatManagerType(map.get("CODE").toString());
					break;
				}
			}
			if(entity.getMatManagerTypeDesc()==null) {
				msg+="请录入正确的物料管理方式";
			}
			for(Map<String,Object> map : managerTypeList) {
				if(entity.getManagerType().equals(map.get("VALUE").toString())) {
					entity.setManagerTypeDesc(entity.getManagerType());
					entity.setManagerType(map.get("CODE").toString());
					break;
				}
			}
			if(entity.getManagerTypeDesc()==null) {
				msg+="请录入正确的类型";
			}
			
			entity.setMsg(msg);
		}
		return new R().put("list", list);
	}
	/**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestBody Map<String,Object> params) throws IllegalAccessException{
		Gson gson=new Gson();
		List<WmsCMatManagerTypeEntity> detailList =gson.fromJson((String) params.get("detailList"),new TypeToken<List<WmsCMatManagerTypeEntity>>() {}.getType());
		
		for(WmsCMatManagerTypeEntity detail:detailList){
			detail.setCreator(params.get("user").toString());
			detail.setEditor(params.get("user").toString());
			detail.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			detail.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		}
		wmsCMatManagerTypeService.batchSave(detailList);
		return new R();
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
    /**
     * 加载库位select
     */
    @RequestMapping("/getLgortSelect")
    public R getLgortSelect(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = wmsCMatManagerTypeService.getLgortSelect(params);
        return R.ok().put("list", list);
    }
    /**
     * 加载存储区select
     */
    @RequestMapping("/getWhAreaSelect")
    public R getWhAreaSelect(@RequestBody Map<String, Object> params){
        List<WmsCoreWhAreaEntity> list = wmsCoreWhAreaService.selectList(new EntityWrapper<WmsCoreWhAreaEntity>().eq("DEL","0").eq("WH_NUMBER",params.get("whNumber")));
        return R.ok().put("list", list);
    }
    /**
     * 加载授权码select
     */
    @RequestMapping("/getAuthorityCodeSelect")
    public R getAuthorityCodeSelect(@RequestBody Map<String, Object> params){
        List<WmsCMatManagerTypeEntity> list = wmsCMatManagerTypeService.selectList(new EntityWrapper<WmsCMatManagerTypeEntity>().eq("DEL","0")
        		.eq("WERKS",params.get("werks"))
        		.eq("WH_NUMBER",params.get("whNumber")).setSqlSelect("distinct AUTHORIZE_CODE"));
        return R.ok().put("list", list);
    }
}
