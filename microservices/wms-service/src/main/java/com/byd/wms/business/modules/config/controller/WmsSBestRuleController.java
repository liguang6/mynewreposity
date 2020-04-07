package com.byd.wms.business.modules.config.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsSBestRuleEntity;
import com.byd.wms.business.modules.config.service.WmsSBestRuleService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;

/**
 * @author ren.wei3
 * 最优出入库规则配置
 */
@RestController
@RequestMapping("config/ruleConfig")
public class WmsSBestRuleController {

	@Autowired
    private WmsSBestRuleService wmsSBestRuleService;
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsSBestRuleService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	WmsSBestRuleEntity fixedStorage = wmsSBestRuleService.selectById(id);
        return R.ok().put("ruleConfig", fixedStorage);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsSBestRuleEntity params){
    	
//	    List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
//				new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER",params.getWhNumber()).eq("DEL","0"));
//	    if(whList.size()==0) {
//	    	return R.error("WH_NOT_FOUND");
//	    }
	    List<WmsSBestRuleEntity> list=wmsSBestRuleService.selectList(
				new EntityWrapper<WmsSBestRuleEntity>()
				.eq("WH_NUMBER",params.getWhNumber())
				.eq("RULE_TYPE",params.getRuleType())
				.eq("SEQNO",params.getSeqno())
	    		);
	    if(list.size()>0) {
	    	return R.error("DATA_EXISTS");
	    }
	    if (params.getDel() == null ||params.getDel().equals("")) {
	    	params.setDel("0");
	    }
	    
	    wmsSBestRuleService.insert(params);

        return R.ok();
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsSBestRuleEntity params){
    	try {
    		wmsSBestRuleService.updateById(params);
    	} catch(Exception e) {
    		return R.error(e.getCause().getMessage());
    	}
    	
    	return R.ok();
    }
    
    /**
     * 删除BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam String ids){
    	String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			wmsSBestRuleService.deleteById(Long.parseLong(id[i]));
		}
		return R.ok();
    }
}
