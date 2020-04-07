package com.byd.web.bjmes.product.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

/**
 * 产品交接
 * @author cscc tangj
 * @email 
 * @date 2019-10-23 16:16:08
 */
@FeignClient(name = "BJMES-SERVICE")
public interface ProductHandoverRemote {
	
	@RequestMapping(value = "/bjmes-service/productHandover/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
    
    /**
     * 交接保存
     * @param params
     * @return
     */
    @RequestMapping(value = "/bjmes-service/productHandover/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String,Object> params);
    /**
     * 获取生产编号
     * @param params
     * @return
     */
    @RequestMapping(value = "/bjmes-service/productHandover/getProductInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getProductInfo(@RequestParam Map<String, Object> params);
}