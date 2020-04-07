package com.byd.qms.modules.common.controller;


import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.qms.modules.common.service.QmsCommonService;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;

/**
 * QMS 通用Controller
 *
 * @author tangj 
 * @since 2019-07-24
 */
@RestController
@RequestMapping("common")
public class QmsCommonController {
    @Autowired
    private QmsCommonService commonService;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private RedisUtils redisUtils;
    
    @RequestMapping("/getBusTypeCodeList")
    public R getBusTypeCodeList(@RequestParam(value = "busTypeCode") String busTypeCode){
        List<Map<String,Object>> list = commonService.getBusTypeCodeList(busTypeCode);
        return R.ok().put("list", list);
    }
    @RequestMapping("/getOrderNoList")
    public R getOrderNoList(@RequestParam(value = "orderNo") String orderNo){
        List<Map<String,Object>> list = commonService.getOrderNoList(orderNo);
        return R.ok().put("list", list);
    }
    @RequestMapping("/getTestNodes")
    public R getTestNodes(@RequestParam(value = "testType") String TEST_TYPE,@RequestParam(value = "TEST_CLASS") String TEST_CLASS){
        List<Map<String,Object>> list = commonService.getTestNodes(TEST_TYPE,TEST_CLASS);
        return R.ok().put("list", list);
    }
    @RequestMapping("/getBusList")
    public R getBusList(@RequestBody Map<String,Object> condMap){
    	List<Map<String,Object>> list = commonService.getBusList(condMap);
        return R.ok().put("list", list);
    }
    
    @RequestMapping("/getTestTools")
    public R getTestTools(@RequestBody Map<String,Object> condMap){
    	List<Map<String,Object>> list = commonService.getTestTools(condMap);
        return R.ok().put("list", list);
    }
    
    @RequestMapping("/getQmsTestRecords")
    public R getQmsTestRecords(@RequestBody Map<String,Object> condMap){
    	List<Map<String,Object>> list = commonService.getQmsTestRecords(condMap);
        return R.ok().put("dataList", list);
    }
	
}