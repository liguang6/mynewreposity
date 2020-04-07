package com.byd.qms.modules.config.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.qms.modules.config.service.QmsTestToolService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
/**
 * 品质 检具库
 * @author cscc tangj
 * @email 
 * @date 2019-07-22 14:12:08
 */
@RestController
@RequestMapping("config/testTool")
public class QmsTestToolController {
    @Autowired
    private QmsTestToolService qmsTestToolService;

    /**
     * 列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestBody Map<String, Object> params){
        PageUtils page = qmsTestToolService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表(不分页)
     */
    @RequestMapping("/getList")
    public R getList(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = qmsTestToolService.getList(params);

        return R.ok().put("data", list);
    }

    /**
     * 明细信息
     */
    @RequestMapping("/info/{id}")
    public R queryDetail(@PathVariable("id") Long id){
        Map<String,Object> entity = qmsTestToolService.getById(id);
        return R.ok().put("entity", entity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> param){
    	param.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	Map<String,Object> map=new HashMap<String,Object>();
    	map.put("werks", param.get("werks"));
    	map.put("testToolNo", param.get("testToolNo"));
    	List<Map<String,Object>> list=qmsTestToolService.getList(map);
    	if(list.size()>0) {
    		return R.error(param.get("testToolNo").toString()+"信息已维护!");
    	}
    	int result=qmsTestToolService.save(param);

    	return result>0 ? R.ok(): R.error("新增失败，请联系管理员");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> param){
    	param.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	int result=qmsTestToolService.update(param);
        
    	return result>0 ? R.ok(): R.error("更新失败，请联系管理员");
    }

    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	
    	int result=qmsTestToolService.delete(id);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }

}