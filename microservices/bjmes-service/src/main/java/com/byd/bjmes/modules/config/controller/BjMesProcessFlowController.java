package com.byd.bjmes.modules.config.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.bjmes.modules.config.service.BjMesProcessFlowService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * @author 作者 tangjin
 * @version 创建时间：2019年10月30日 下午5:00:00 类说明 加工流程
 */
@RestController
@RequestMapping("config/bjMesProcessFlow")
public class BjMesProcessFlowController {
    @Autowired
    private BjMesProcessFlowService bjMesProcessFlowService;

    /**
     * 分页查询加工流程列表（去重process_flow_code）
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params) {
        PageUtils page=bjMesProcessFlowService.queryPage(params);
        return R.ok().put("page", page);
    }
	
    /**
     * 保存加工流程
     */
    @RequestMapping("/saveProcessFlow")
    public R saveProcessFlow(@RequestParam Map<String, Object> params) {
        try {
            bjMesProcessFlowService.saveProcessFlow(params);
            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        } 
    }
    /**
     * 更新加工流程
     */
    @RequestMapping("/updateProcessFlow")
    public R updateProcessFlow(@RequestParam Map<String, Object> params) {
        try {
            bjMesProcessFlowService.updateProcessFlow(params);
            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }  
    }
    /**
     * 保存加工节点
     */
    @RequestMapping("/saveNode")
    public R saveNode(@RequestParam Map<String, Object> params) {
        try {
            bjMesProcessFlowService.saveNode(params);
            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }  
    }
    /**
     * 删除加工节点
     */
    @RequestMapping("/deleteNode")
    public R deleteNode(@RequestParam Map<String, Object> params) {
        try {
            bjMesProcessFlowService.deleteNode(params);
            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }  
    }
    /**
     * 根据process_flow_code删除加工流程
     */
    @RequestMapping("/deleteProcessFlow/{process_flow_code}")
    public R deleteProcessFlow(@PathVariable("process_flow_code") String process_flow_code){
        try {
            bjMesProcessFlowService.deleteProcessFlow(process_flow_code);
            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }  
    }
    /**
     * 根据process_flow_code查询加工节点
     */
    @RequestMapping("/getNodeList")
    public R getNodeList(@RequestParam Map<String, Object> params){
        List<Map<String,Object>> list = bjMesProcessFlowService.getNodeList(params);
        return R.ok().put("data", list);
    }
}
