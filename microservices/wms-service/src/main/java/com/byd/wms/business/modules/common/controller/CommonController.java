package com.byd.wms.business.modules.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;

/**
 * 通用Controller
 *
 * @author tangj 
 * @since 2018-08-02
 */
@RestController
@RequestMapping("common")
public class CommonController {
    @Autowired
    private CommonService commonService;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/getPlantList")
    public R getPlantList(@RequestParam(value = "werks") String werks){
    	List<Map<String,Object>> resultWerks = new ArrayList<>();
    	/**
    	 * 从redis获取所有工厂信息
    	 */
    	Pattern pattern = Pattern.compile(werks,Pattern.CASE_INSENSITIVE);
    	List<Map<String,Object>> allWerks = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWerksKey());
    	
    	if (allWerks != null) {
	        for (Map<String,Object> redisWerk:allWerks) {
	            Matcher matcher = pattern.matcher((String) redisWerk.get("WERKS"));
	            if(matcher.find()){
	            	resultWerks.add(redisWerk);
	            }
	        }
      	}
    	
        return R.ok().put("list", resultWerks);
    }


    @RequestMapping("/getWhList")
    public R getWhList(@RequestParam(value = "whNumber") String whNumber,@RequestParam(value = "language") String language){
    	List<Map<String,Object>> list = new ArrayList<>();
    	
        Pattern pattern = Pattern.compile(whNumber,Pattern.CASE_INSENSITIVE);
    	/**
    	 * 从redis获取所有工厂信息
    	 */
        List<Map<String,Object>> allWh = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWhKey());
      
        if (allWh != null) {
	        for (Map<String,Object> a:allWh) {
	            Matcher matcher = pattern.matcher((String) a.get("WH_NUMBER"));
	            if(matcher.find()){
	            	list.add(a);
	            }
	        }
        }
    	
        return R.ok().put("list", list);
    }
    
    
    @RequestMapping("/getVendorList")
    public R getVendorList(@RequestParam(value = "lifnr") String lifnr,@RequestParam(value = "werks") String werks){
        List<Map<String,Object>> list = commonService.getVendorList(lifnr,werks);
        return R.ok().put("list", list);
    }
    
    @RequestMapping("/getVendor")
    public R getVendor(@RequestParam(value = "lifnr") String lifnr){
        List<Map<String,Object>> list = commonService.getVendor(lifnr);
        return R.ok().put("list", list);
    }
    
	
	@RequestMapping("/getMaterialList")
    public R getMaterialList(@RequestParam(value = "werks")  String werks,@RequestParam(value = "matnr")  String matnr){
        List<Map<String,Object>> list = commonService.getMaterialList(werks,matnr);
        return R.ok().put("list", list);
    }
    
    /**
     * 根据权限获取业务类型下拉列表
     * @param params
     * @return
     */
    @RequestMapping("/getBusinessList")
    public R getBusinessList(@RequestParam Map<String, Object> params){
        List<Map<String,Object>> list = commonService.getBusinessList(params);
        return R.ok().put("data", list);
    }
    
    /**
     * 根据工厂代码、库存类型查询库位列表
     * @param params
     * @return
     */
    @RequestMapping("/getLoList")
    public R getLoList(@RequestParam Map<String,Object> params) {
    	List<Map<String,Object>> list = commonService.getLoList(params);
        return R.ok().put("data", list);
    }
    /**
     * 根据工厂代码查询收料房存放区列表
     * @return
     */
    @RequestMapping("/getGrAreaList")
    public R getGrAreaList(@RequestParam(value = "WERKS") String WERKS) {
    	List<Map<String, Object>> list= commonService.getGrAreaList(WERKS);
    	return R.ok().put("data", list);
    }
    
    /**
	 * 根据工厂号获取全部仓库信息@YK20180829
	 * @param WERKS
	 */
    @RequestMapping("/getWhDataByWerks")
    public R getWhDataByWerks(@RequestParam(value = "WERKS") String WERKS) {
    	List<Map<String, Object>> list = commonService.getWhDataByWerks(WERKS);
    	return R.ok().put("data", list);
    }
    
    @RequestMapping("/getReasonData")
	public R getReasonData(@RequestParam(value = "REASON_DESC") String REASON_DESC) {
		List<Map<String,String>> reason = new ArrayList<Map<String,String>>();
		if("*".equals(REASON_DESC))REASON_DESC="";
		reason = commonService.getQcReturnReason(REASON_DESC);		
		return R.ok().put("reason",reason);
	}
    
    @RequestMapping("/getPlantSetting")
    //@RequiresPermissions("")
    public R getPlantSetting(@RequestParam(value = "WH_NUMBER") String WH_NUMBER){
        Map<String,Object> setting = commonService.getPlantSetting(WH_NUMBER);
        return R.ok().put("data", setting);
    }
    @RequestMapping("/getDictList")
    public R getDictList(@RequestParam(value = "type") String type) {
    	List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
    	list=commonService.getDictList(type);
    	return R.ok().put("data", list);
    }

    /**
     * 模糊查询 出入库策略控制标识
     * @param params
     * @return
     */
    @RequestMapping("/getControlFlagList")
    public R getControlFlagList(@RequestParam Map<String, Object> params){
        List<Map<String,Object>> list = commonService.getControlFlagList(params);
        return R.ok().put("list", list);
    }

    /**
     * 根据工厂、物料编码查询物料信息
     * @param params
     * @return
     */
    @RequestMapping("/getMaterialInfo")
    public  R getMaterialInfo(@RequestParam Map<String, Object> params) {
    	return R.ok().put("data", commonService.getMaterialInfo(params));
    }

    /**
     * 根据工厂代码、查询所有库位（包括不良品）
     * @param params
     * @return
     */
    @RequestMapping("/getAllLoList")
    public R getAllLoList(@RequestParam Map<String,Object> params) {
    	List<Map<String,Object>> list = commonService.getAllLoList(params);
        return R.ok().put("data", list);
    }

    /**
     * 
     * @param params
     * @return
     */
    @RequestMapping("/getMatStockInfo")
    public  R getMatStockInfo(@RequestParam Map<String, Object> params) {
    	return R.ok().put("data", commonService.getMatStockInfo(params));
    }
    
    /**
     * 获取物料管理员
     * @param params
     * @return
     */
    @RequestMapping("/getMatManager")
    public  R getMatManager(@RequestParam Map<String, Object> params) {
    	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("STAFF_NUMBER", currentUser.get("STAFF_NUMBER"));
	   	List<Map<String,Object>> result = commonService.getMatManager(params);
	   	if(result.size()<=0) {
	   		Map map = new HashMap<>();
	   		map.put("MANAGER_STAFF", currentUser.get("STAFF_NUMBER"));
	   		map.put("MANAGER", currentUser.get("FULL_NAME"));
	   		result.add(map);
	   	}
	   	String ALL_MANAGER_STAFF = "";
	   	for (Map<String, Object> map : result) {
	   		ALL_MANAGER_STAFF += map.get("MANAGER_STAFF")==null?"":map.get("MANAGER_STAFF").toString();
		}
	   	Map map = new HashMap<>();
   		map.put("MANAGER_STAFF", ALL_MANAGER_STAFF);
   		map.put("MANAGER", "全部");
   		
   		result.add(0, map);
    	return R.ok().put("data", result);
    }

    @CrossOrigin
    @RequestMapping("/getBinCode")
    public R getBinCode(@RequestBody Map<String,Object> params) {
    	return R.ok().put("result", commonService.getBinCode(params));
    }
    
    @CrossOrigin
    @RequestMapping("/getLabelInfo")
    public R getLabelInfo(@RequestBody Map<String, Object> params){
    	return R.ok().put("result", commonService.getLabelInfo(params));
    }
    
    @CrossOrigin
    @RequestMapping("/getLabelInfoPda")
    public List<Map<String,Object>> getLabelInfoPda(@RequestBody Map<String, Object> params){
    	return commonService.getLabelInfo(params);
    }
    
    @RequestMapping("/doSapPost")
    public String doSapPost(@RequestBody Map<String, Object> params){
    	return commonService.doSapPost(params);
    }
    
    @RequestMapping("/saveWMSDoc")
    public String saveWMSDoc(@RequestBody Map<String, Object> params){
    	Map<String,Object> headMap=(Map<String, Object>) params.get("head");
    	List<Map<String,Object>> wmsDocMatList= (List<Map<String, Object>>) params.get("wmsDocMatList");
    	return commonService.saveWMSDoc(headMap,wmsDocMatList);
    }
    
    /**
	 * 根据仓库号，获取仓库表信息
	 * @param param
	 * @return
	 */
    @RequestMapping("/wmsCoreWhList")
	public List<Map<String,Object>> getWmsCoreWhList(@RequestBody Map<String, Object> params){
		return commonService.getWmsCoreWhList(params);
	}
    
  //校验条码缓存表，并返回条码
    @RequestMapping("/getLabelCacheInfo")
   	public List<Map<String,Object>> getLabelCacheInfo(@RequestBody Map<String, Object> params){
    	
    	List<Map<String,Object>> dataList=commonService.getLabelCacheInfo(params);
   		return dataList;
   	}
     //保存条码信息到条码缓存表
    @RequestMapping("/saveLabelCacheInfo")
   	public int saveLabelCacheInfo(@RequestBody List<Map<String,Object>> params){
    	
    	int insertCount=commonService.saveLabelCacheInfo(params);
   		return insertCount;
   	}
    //删除条码信息到条码缓存表
    @RequestMapping("/deleteLabelCacheInfo")
   	public int deleteLabelCacheInfo(@RequestBody Map<String, Object> params){
    	List<Map<String,Object>> param = new ArrayList<Map<String,Object>>(); 
    	int deleteCountt=commonService.deleteLabelCacheInfo(param);
   		return deleteCountt;
   	}
}