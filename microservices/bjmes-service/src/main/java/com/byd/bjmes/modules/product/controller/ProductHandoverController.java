package com.byd.bjmes.modules.product.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.bjmes.modules.product.service.ProductHandoverService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * 产品交接
 * @author cscc tangj
 * @email 
 * @date 2019-10-23 16:16:08
 */
@RestController
@RequestMapping("productHandover")
public class ProductHandoverController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ProductHandoverService matHandoverService;
    
    /**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
    	PageUtils page = matHandoverService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    @RequestMapping("/getProductInfo")
 	public R getMatInfo(@RequestParam Map<String,Object> params){
    	
 		return R.ok().put("data", matHandoverService.getProductInfo(params));
 	}
    
    /**
     * 保存
     * @param params
     * @return
     */
    @RequestMapping("/save")
  	public R save(@RequestBody Map<String,Object> params){
    	try {
    		matHandoverService.save(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return R.error("保存失败！" + e.getMessage());
		}
		return R.ok();
  	}
    // 校验生产编号是否已交接
    @RequestMapping("/checkProductInfo")
    public R checkProductInfo(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=matHandoverService.checkProductInfo(params.get("product_no").toString());
    	return R.ok().put("data", list);
    }
}