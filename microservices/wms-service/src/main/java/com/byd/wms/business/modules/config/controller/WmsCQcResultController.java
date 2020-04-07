package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCQcResultService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 质检结果配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@RestController
@RequestMapping("config/wmscqcresult")
public class WmsCQcResultController {
    @Autowired
    private WmsCQcResultService wmsCQcResultService;
    
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    
//    @Autowired
//    private SysDictService sysDictService;

    /**
     * 列表
     */
    @CrossOrigin
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCQcResultService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    
    /**
     * 查询检验结果信息
     * @param params
     * @return
     */
    @CrossOrigin
    @RequestMapping("/list2")
    public R list2(@RequestParam Map<String, Object> params){
    	String werks = String.valueOf(params.get("werks"));
    	String qcResultCode = String
    			.valueOf(params.get("qcResultCode") == null ? "" : params
    					.get("qcResultCode"));

    	//查询工厂对应的配置
    	Page<WmsCQcResultEntity> page_werks = wmsCQcResultService.selectPage(new Query<WmsCQcResultEntity>(params).getPage(),
    			new EntityWrapper<WmsCQcResultEntity>().eq(StringUtils.isNotBlank(qcResultCode), "qc_result_code",
    					qcResultCode).like(StringUtils.isNotBlank(werks),
    					"werks", werks).eq("del", "0"));
    		//没有配置指定工厂的质检配置，取通用的配置
    	Page<WmsCQcResultEntity> page_all = wmsCQcResultService.selectPage(
    				new Query<WmsCQcResultEntity>(params).getPage(),
    				new EntityWrapper<WmsCQcResultEntity>()
    						.eq(StringUtils.isNotBlank(qcResultCode),
    								"qc_result_code", qcResultCode).eq("del", "0")
    						.isNull("WERKS").or().eq("WERKS", ""));
    	List<WmsCQcResultEntity> werksList = new ArrayList<WmsCQcResultEntity>();
    	if(page_all !=null && page_all.getTotal()>0) {
    		werksList = page_werks.getRecords();
    		List<WmsCQcResultEntity> allList = page_all.getRecords();
    		for (WmsCQcResultEntity qcResult : allList) {
    			boolean b = false;
    			for (WmsCQcResultEntity werksQcResult : werksList) {
    				if(qcResult.getQcResultCode().equals(werksQcResult.getQcResultCode())) {
    					//工厂找到相同配置
    					b = true;
    					break;
    				}
    			}
    			if(!b) {
    				werksList.add(qcResult);
    			}
			}
    	}
    	
        //质检结果按照 qcResultCode值从小到大排序
        Collections.sort(werksList, new Comparator<WmsCQcResultEntity>() {  
        	@Override
            public int compare(WmsCQcResultEntity u1, WmsCQcResultEntity u2) {  
                if (Integer.valueOf(u1.getQcResultCode()) > Integer.valueOf(u2.getQcResultCode())) {  
                    return 1;  
                }  
                if (Integer.valueOf(u1.getQcResultCode()) < Integer.valueOf(u2.getQcResultCode())) {  
                    return -1;  
                }  
                return 0;  
            }  
        }); 
        page_werks.setSize(werksList.size());
        page_werks.setTotal(werksList.size());
    	PageUtils pageUtils =  new PageUtils(page_werks);
    	return R.ok().put("page", pageUtils);
    }


    /**
     * 信息
     */
    @CrossOrigin
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCQcResultEntity wmsCQcResult = wmsCQcResultService.selectById(id);

        return R.ok().put("data", wmsCQcResult);
    }

    /**
     * 保存
     */
    @CrossOrigin
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcResultEntity wmsCQcResult){
    	if(wmsCQcResult.getWerks()!=null && !wmsCQcResult.getWerks().equals("")) {
    		
    	    List<WmsCQcResultEntity> qcResultList=wmsCQcResultService.selectList(
    				new EntityWrapper<WmsCQcResultEntity>().eq("WERKS",wmsCQcResult.getWerks())
    				.eq("QC_RESULT_CODE",wmsCQcResult.getQcResultCode()).eq("DEL","0"));
    	    if(qcResultList.size()>0) {
    	    	return R.error(wmsCQcResult.getWerks()+wmsCQcResult.getQcResultName()+"质检结果信息已维护！");
    	    }
    	}else {
    		 List<WmsCQcResultEntity> qcResultList=wmsCQcResultService.selectList(
     				new EntityWrapper<WmsCQcResultEntity>().isNull("WERKS") //eq("WERKS",wmsCQcResult.getWerks())
     				.eq("QC_RESULT_CODE",wmsCQcResult.getQcResultCode()).eq("DEL","0"));
     	    if(qcResultList.size()>0) {
     	    	return R.error(wmsCQcResult.getQcResultName()+"质检结果信息已维护！");
     	    }
    	}
	    wmsCQcResult.setDel("0");
    	wmsCQcResultService.insert(wmsCQcResult);
        
        return R.ok();
    }

    /**
     * 修改
     */
    @CrossOrigin
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcResultEntity wmsCQcResult){
        
        wmsCQcResultService.updateAllColumnById(wmsCQcResult);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @CrossOrigin
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCQcResultEntity wmsCQcResultEntity){
        wmsCQcResultService.updateAllColumnById(wmsCQcResultEntity);

        return R.ok();
    }
    
    /**
     * 导入预览
     * @param excel
     * @return
     * @throws IOException 
     */
    @CrossOrigin
    @RequestMapping("/preview")
    public R preview(@RequestBody List<WmsCQcResultEntity> entitys) throws IOException{
    	
    	for(WmsCQcResultEntity entity:entitys){
    		//validate
    		entity.setMsgs(new ArrayList<String>());
    	    //必填校验
    		if(StringUtils.isBlank(entity.getWerks()) || StringUtils.isBlank(entity.getQcResultCode())){
    			entity.getMsgs().add("必填信息不能为空");
    		}
    		//校验工厂代码是否存在
    		int plantcount = wmsSapPlantService.selectCount(new EntityWrapper<WmsSapPlant>()
    				.eq("werks", entity.getWerks()));
    		if(plantcount != 1){
    			entity.getMsgs().add("工厂代码不存在");
    		}
    	}
    	return R.ok().put("data", entitys);
    }
    
    /**
     * 导入
     * @param entitys
     * @return
     */
    @CrossOrigin
    @RequestMapping("/import")
    public R importExcel(List<WmsCQcResultEntity> entitys){
    	//
    	wmsCQcResultService.insertBatch(entitys);
    	return R.ok();
    }
    /**
     * 复制保存
     * @param entitys
     * @return
     */
    @CrossOrigin
    @RequestMapping("/batchSave")
    public R batchSave(@RequestBody Map<String, Object> params){
    
    	Gson gson=new Gson();
    	List<Map<String, Object>> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String, Object>>>() {}.getType());
		list.forEach(k->{
			k=(Map<String, Object>)k;	
			k.put("CREATOR",params.get("USERNAME").toString());
	    	k.put("CREATE_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    	k.put("EDITOR",params.get("USERNAME").toString());
	    	k.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		});
    	wmsCQcResultService.saveCopyData(list);
    	return R.ok();
    }
}
