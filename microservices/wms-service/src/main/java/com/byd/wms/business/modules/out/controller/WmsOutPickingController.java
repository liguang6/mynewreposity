package com.byd.wms.business.modules.out.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.service.WmsOutPickingService;

/**
 * 需求拣配控制器
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("/out/picking")
public class WmsOutPickingController {

	@Autowired
	private WmsOutPickingService wmsOutPickingService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
    private RedisUtils redisUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsOutPickingService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 推荐
     */
    @RequestMapping("/recommend")
    public R recommend(@RequestParam Map<String, Object> params){
    	try {
    		String requirementNo = params.get("REQUIREMENT_NO").toString();
    		String uid = UUID.randomUUID().toString();
    		if (redisUtils.tryLock(requirementNo, uid))
    		{
    			params.put("UID", uid);
    			PageUtils page = wmsOutPickingService.recommend(params);
    			redisUtils.unlock(requirementNo, uid);
        		return R.ok().put("page", page);
    		} else {
    			return R.error(requirementNo+",正在执行中，请稍后再试！");	
    		}
    		
    	} catch (Exception e) {
			return R.error(e.getMessage());				
		}  
    }
	
    /**
     * 下架
     */
    @RequestMapping("/picking")
    public R picking(@RequestParam Map<String, Object> params){
    	try {
    		String requirementNo = params.get("REQUIREMENT_NO").toString();
    		String uid = UUID.randomUUID().toString();
    		if (redisUtils.tryLock(requirementNo, uid))
    		{
    			params.put("UID", uid);
    			wmsOutPickingService.picking(params);
    			redisUtils.unlock(requirementNo, uid);
    		} else {
    			return R.error(requirementNo+",正在执行中，请稍后再试！");	
    		}
	    } catch (Exception e) {
			return R.error("系统异常："+e.getMessage());				
		}  
    	return R.ok();
    }
    
    /**
     * 获取库存
     * @param params
     * @return
     */
    @RequestMapping("/getStockInfo")
    public R getStockInfo(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> relist = new ArrayList<Map<String, Object>>();
//    	List<Map<String, Object>> paramlist = new ArrayList<Map<String, Object>>();
//    	paramlist.add(params);
    	List<Map<String, Object>> list = commonService.getWmsStockforMap(params);
    	if (list.size() > 0) {
    		for (Map<String, Object> maps : list) {
    			BigDecimal qty = maps.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(maps.get("STOCK_QTY").toString()); 
    			if (qty.compareTo(BigDecimal.ZERO) > 0 && !maps.get("BIN_CODE").equals("BBBB")) {
    				relist.add(maps);
    			}
    		}
    	}
    	return R.ok().put("list", relist);
    }
    
    /**
     * 获取配送标签数据
     * @param params
     * @return
     */
    @RequestMapping("/shippingLabel")
    public R queryShippingLabel(@RequestParam Map<String, Object> params){
    	try {
    		String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
        	params.put("REQUIREMENT_NO", reqno);
	        PageUtils page = wmsOutPickingService.queryShippingLabel(params);
	        return R.ok().put("page", page);
    	} catch (Exception e) {
			return R.error(e.getMessage());				
		} 
    }
    
    /**
     * 获取配送标签数据cs
     * @param params
     * @return
     */
    @RequestMapping("/shippingLabelcs")
    public R queryShippingLabelcs(@RequestParam Map<String, Object> params){
    	try {
    		String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
        	params.put("REQUIREMENT_NO", reqno);
	    	PageUtils page = wmsOutPickingService.queryShippingLabelcs(params);
	        return R.ok().put("page", page);
	    } catch (Exception e) {
			return R.error(e.getMessage());				
		}  
    }
    
    /**
     * 获取关键零部件标签
     * @param params
     * @return
     */
    @RequestMapping("/keyPartsLabel")
    public R querykeyPartsLabel(@RequestParam Map<String, Object> params){
    	try {
    		String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
        	params.put("REQUIREMENT_NO", reqno);
	    	PageUtils page = wmsOutPickingService.queryKeyPartsLabel(params);
	        return R.ok().put("page", page);
	    } catch (Exception e) {
			return R.error(e.getMessage());				
		}  
    }
    
    /**
     * 打印时保存配送标签
     * @param params
     * @return
     */
    @RequestMapping("/saveShippingLabel")
    public R saveShippingLabel(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> list = wmsOutPickingService.saveShippingLabel(params);
        return R.ok().put("list", list);
    }
    
    /**
     * 拣配清单
     */
    @RequestMapping("/pickList")
    public R pickList(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsOutPickingService.selectPickList(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 获取需求头
     */
    @RequestMapping("/getReq")
    public R getReq(@RequestParam Map<String, Object> params){
    	String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
    	String ltype = params.get("LABELTYPE") == null?"":params.get("LABELTYPE").toString().trim();
    	params.put("REQUIREMENT_NO", reqno);
    	List<WmsOutRequirementHeadEntity> head = wmsOutPickingService.getReq(params);
    	if (head.size() < 1) {
    		return R.error("需求号不存在！");
    	}
    	try {
    		if (!ltype.equals("")) {
    			wmsOutPickingService.queryKeyPartsLabel(params);
    		} else {
    			if (head.get(0).getRequirementType().equals("77")) {
    	    		wmsOutPickingService.queryShippingLabel(params);
    	    	} else {
    	    		wmsOutPickingService.queryShippingLabelcs(params);
    	    	}
    		}
	    	
    	} catch (Exception e) {
			return R.error(e.getMessage());				
		}  
        return R.ok().put("business_name", head.get(0).getRequirementType());
    }
}
