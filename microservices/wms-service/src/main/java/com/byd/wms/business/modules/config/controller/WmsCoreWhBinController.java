package com.byd.wms.business.modules.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinSeqService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsCoreWhService;
/**
 * 仓库储位
 * @author tangj
 * @email 
 * @date 2018-08-07 15:36:51
 */
@RestController
@RequestMapping("config/corewhbin")
public class WmsCoreWhBinController {
    @Autowired
    private WmsCoreWhBinService wmsCoreWhBinService;
    @Autowired
    private WmsCoreWhBinSeqService wmsCoreWhBinSeqService;
    @Autowired
    private WmsCoreWhService wmsCoreWhService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCoreWhBinService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        WmsCoreWhBinEntity bin = wmsCoreWhBinService.selectById(id);
        return R.ok().put("bin", bin);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreWhBinEntity bin){
	    List<WmsCoreWhBinEntity> binList=wmsCoreWhBinService.selectList(
	    		 new EntityWrapper<WmsCoreWhBinEntity>()
	    		 .eq("WH_NUMBER", bin.getWhNumber())
	             .eq("BIN_CODE", bin.getBinCode()).eq("DEL","0"));
	    if(binList.size()>0) {
	    	return R.error("仓库储位已维护！");
	    }
	    
        boolean isSaved=wmsCoreWhBinService.insert(bin);
        if(isSaved) {
        	wmsCoreWhBinSeqService.updateSeq(bin);
        	return R.ok();
        }else {
        	return R.error("保存失败");
        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreWhBinEntity wmsCoreWhBin){
        wmsCoreWhBinService.updateAllColumnById(wmsCoreWhBin);//全部更新 
        
        wmsCoreWhBinSeqService.updateSeq(wmsCoreWhBin);
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCoreWhBinEntity wmsCoreWhBin){
    	
        wmsCoreWhBinService.updateById(wmsCoreWhBin);

        return R.ok();
    }
    @RequestMapping("/upload")
	public R upload(@RequestBody List<WmsCoreWhBinEntity> entityList) throws IOException{
    	wmsCoreWhBinService.merge(entityList);
    	// 更新仓库储位排序【WMS_CORE_WH_BIN_SEQ】
    	String storageAreaCode="";
    	for(int i = 0 ; i < entityList.size() ; i++) {
    		WmsCoreWhBinEntity entity=entityList.get(i);
    		if(!storageAreaCode.equals(entity.getStorageAreaCode())) {
    			storageAreaCode=entity.getStorageAreaCode();
    			wmsCoreWhBinSeqService.updateSeq(entity);
    		}
		}
    	
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R preview(@RequestBody List<Map<String,Object>> entityList) throws IOException{
		String wh="";
		StringBuffer binSb=new StringBuffer();
		String msg="";
		if(!CollectionUtils.isEmpty(entityList)){
			for(Map<String,Object> map:entityList){
				
				if(map.get("whNumber")!=null && !map.get("whNumber").toString().equals("") && !"null".equals(map.get("whNumber").toString())) {
					if(!wh.equals(map.get("whNumber").toString())) {
						List<WmsCoreWhEntity> whList=wmsCoreWhService.selectList(
								new EntityWrapper<WmsCoreWhEntity>().eq("WH_NUMBER", map.get("whNumber").toString()).eq("DEL","0"));
					    if(whList.size()==0) {
					    	msg="仓库代码未维护;";
					    }else {
					    	wh=map.get("whNumber").toString();
					    }
					}
				} else {
					msg="仓库代码不能为空;";
				}
				//System.out.println("-->storageAreaCode : " + map.get("storageAreaCode").toString());
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("WH_NUMBER", map.get("whNumber").toString());
				params.put("STORAGE_AREA_CODE", map.get("storageAreaCode").toString());
				Map<String, Object> storageAreaCodeInfo =  wmsCoreWhBinService.queryWhAreaInfo(params);
				if(storageAreaCodeInfo == null) {
					msg+="存储区代码未维护;";
				}
				if(map.get("binCode")!=null && !map.get("binCode").toString().equals("")) {
					if(binSb.indexOf(map.get("binCode").toString().concat(";"))>=0) {
						msg+="储位代码存在重复数据;";
					}else {
						binSb.append(map.get("binCode").toString().concat(";"));
					}
				} else {
					msg+="储位代码不能为空;";
				}
				if(map.get("binName")==null || (map.get("binName")!=null && map.get("binName").toString().equals(""))) {
					msg+="储位名称不能为空;";
				}
				if(map.get("binType")==null || (map.get("binType")!=null && map.get("binType").toString().equals(""))) {
				    msg+="储位类型不能为空;";
				}
				if(map.get("binStatus")==null || (map.get("binStatus")!=null && map.get("binStatus").toString().equals(""))) {
				    msg+="储位状态不能为空;";
				}
				map.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", entityList);
	}
	
	@RequestMapping("/queryBinCode")
	public R queryBinCode(@RequestParam Map<String, Object> params){
		List<WmsCoreWhBinEntity> retWhBinList=wmsCoreWhBinService.queryBinCode(params);
		return R.ok().put("result", retWhBinList);
	}
	
}
