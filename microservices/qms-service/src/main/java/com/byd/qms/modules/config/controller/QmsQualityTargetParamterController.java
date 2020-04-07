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
import com.byd.qms.modules.config.service.QmsQualityTargetParamterService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
/**
 * 品质 质量目标参数
 * @author cscc tangj
 * @email 
 * @date 2019-07-29 14:12:08
 */
@RestController
@RequestMapping("config/qmsTargetParam")
public class QmsQualityTargetParamterController {
    @Autowired
    private QmsQualityTargetParamterService qmsTargetParamterService;

    /**
     * 列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestBody Map<String, Object> params){
        PageUtils page = qmsTargetParamterService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表(不分页)
     */
    @RequestMapping("/getList")
    public R getList(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = qmsTargetParamterService.getList(params);

        return R.ok().put("data", list);
    }

    /**
     * 明细信息
     */
    @RequestMapping("/info/{id}")
    public R queryDetail(@PathVariable("id") Long id){
        Map<String,Object> entity = qmsTargetParamterService.getById(id);
        return R.ok().put("entity", entity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> param){
    	param.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	// 通过“订单类型”与“检验节点”两字段，确保数据唯一性
//    	Map<String,Object> map=new HashMap<String,Object>();
//    	map.put("testType", param.get("testType"));
//    	map.put("testNode", param.get("testNode"));
//    	List<Map<String,Object>> list=qmsTargetParamterService.getList(map);
//    	if(list.size()>0) {
//    		return R.error("检验节点【"+param.get("testNode").toString()+"】信息已维护!");
//    	}
    	int result=qmsTargetParamterService.save(param);

    	return result>0 ? R.ok(): R.error("新增失败，请联系管理员");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> param){
    	param.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	int result=qmsTargetParamterService.update(param);
        
    	return result>0 ? R.ok(): R.error("更新失败，请联系管理员");
    }

    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	
    	int result=qmsTargetParamterService.delete(id);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }

}