package com.byd.wms.business.modules.pda.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.wms.business.modules.pda.service.StoSendCreateService;

@RestController
@RequestMapping("/sto")
public class StoSendCreateController {
	@Autowired
	private StoSendCreateService stoSendCreateService;
	/**
     * 列表
     */
	@CrossOrigin
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.list(params));
    }
	
	@CrossOrigin
    @RequestMapping("/create")
    public R create(@RequestParam Map<String, Object> params){
		return R.ok().put("data", stoSendCreateService.create(params));
    }
	
	@CrossOrigin
    @RequestMapping("/querySto")
    public R querySto(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.querySto(params));
    }
	
	@CrossOrigin
    @RequestMapping("/queryContact")
    public R queryContact(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.queryContact(params));
    }
	
	@CrossOrigin
    @RequestMapping("/queryWMSNo")
    public R queryWMSNo(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.queryWMSNo(params));
    }
	
	@CrossOrigin
    @RequestMapping("/queryCustomer")
    public R queryCustomer(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.queryCustomer(params));
    }
	
	@CrossOrigin
    @RequestMapping("/queryLiktx")
    public R queryLiktx(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.queryLiktx(params));
    }
	
	@CrossOrigin
    @RequestMapping("/checkAddr")
    public R checkAddr(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.checkAddr(params));
    }
	
	@CrossOrigin
    @RequestMapping("/checkExist")
    public R checkExist(@RequestParam Map<String, Object> params){
        return R.ok().put("data", stoSendCreateService.checkExist(params));
    }
	
	@RequestMapping("/queryBydeliveryNo")
    public R queryBydeliveryNo(@RequestBody List<Map<String, Object>> params){
        return  R.ok().put("data", stoSendCreateService.queryBydeliveryNo(params));
    }
    
}
