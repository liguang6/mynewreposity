package com.byd.web.bjmes.config.controller;

import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.bjmes.config.service.ProcessFlowRemote;

/** 
 * @author 作者 tangjin
 * @version 创建时间：2019年10月30日 下午5:13:13 
 * 类说明 加工流程
 */
@RestController
@RequestMapping("/config/bjMesProcessFlow")
public class ProcessFlowController {

	@Autowired
    private ProcessFlowRemote processFlowRemote;
	@Autowired
    private UserUtils userUtils;
    
    /**
     * 分页查询列表
     */
    @RequestMapping("/list")
    public R queryPage(@RequestParam Map<String, Object> params){
        return processFlowRemote.queryPage(params);
    }

	@RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return processFlowRemote.info(id);
    }

    /**
     * 保存加工流程
     */
    @RequestMapping("/saveProcessFlow")
    public R saveProcessFlow(@RequestParam Map<String, Object> params){
        params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	params.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return processFlowRemote.saveProcessFlow(params);
    }

    /**
     * 更新加工流程
     */
    @RequestMapping("/updateProcessFlow")
    public R updateProcessFlow(@RequestParam Map<String, Object> params){
        params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	params.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return processFlowRemote.updateProcessFlow(params);
    }
    
    /**
     * 删除加工流程
     */
    @RequestMapping("/deleteProcessFlow/{process_flow_code}")
    public R deleteProcessFlow(@PathVariable("process_flow_code") String process_flow_code){
    	return processFlowRemote.deleteProcessFlow(process_flow_code);
    }

    /**
     * 保存加工节点
     */
    @RequestMapping("/saveNode")
    public R saveNode(@RequestParam Map<String, Object> params){
        params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	params.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return processFlowRemote.saveNode(params);
    }

    /**
     * 删除加工节点
     */
    @RequestMapping("/deleteNode")
    public R deleteNode(@RequestParam Map<String, Object> params){
        params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	params.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return processFlowRemote.deleteNode(params);
    }
    
	@RequestMapping("/getNodeList")
    public R getNodeList(@RequestParam Map<String,String> map){
    	return processFlowRemote.getNodeList(map);
    }
}
