package com.byd.admin.modules.masterdata.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.masterdata.service.TestToolService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月19日 上午10:52:08 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/testTool")
public class TestToolController {
	@Autowired
    private TestToolService testToolService;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = testToolService.getQmsTestToolListByPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("ID", id);
    	Map<String, Object> retMap=testToolService.selectById(param);

        return R.ok().put("result", retMap);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	try{
    	Map<String,Object> condMap=new HashMap<String,Object>();
    	condMap.put("WERKS", params.get("werks"));
    	condMap.put("TEST_TOOL_NO", params.get("test_tool_no"));
    	List<Map<String,Object>> retList=testToolService.getQmsTestToolList(condMap);
    	if(retList!=null&&retList.size()>0){
    		throw new RuntimeException("检具编码 "+params.get("test_tool_no")+" 在工厂 "+params.get("werks")+" 中已经存在，不能再次添加！");
    	}
    	
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("status", "0");
    	params.put("editor", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	testToolService.insertQmsTestTool(params);
    	} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
    	return R.ok();
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("status", "0");
    	params.put("editor", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	testToolService.updateQmsTestTool(params);
    	return R.ok();
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("id", id);
    	testToolService.delQmsTestTool(param);

        return R.ok();
    }
}
