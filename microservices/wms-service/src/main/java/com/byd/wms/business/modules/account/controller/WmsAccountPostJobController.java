package com.byd.wms.business.modules.account.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;

/**
 * 过账失败任务处理，包含失败任务再次过账，失败任务删除等功能
 * @author (changsha) thw
 * @date 2019-5-16
 */
@RestController
@RequestMapping("account/wmsAccountPostJob")
public class WmsAccountPostJobController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private CommonService commonService;
    @Autowired
    private UserUtils userUtils;

	@RequestMapping("/listPostJob")
    public R listPostJob(@RequestBody Map<String, Object> params) {
	 	PageUtils page  = commonService.getSapPostJobList(params);
		return R.ok().put("page", page);
	}
	
    /**
     * 失败任务过账
     * @param params
     * @return
     */
    @RequestMapping("/post")
    public R post(@RequestBody Map<String, Object> params) {
    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	try {
           		Map<String,Object> currentUser = userUtils.getUser();
           		params.put("USERNAME", currentUser.get("USERNAME"));
           		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
           		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    		
    	    	String matListStr = params.get("matList").toString();
    			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    	    	
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("CREATOR").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("UPDATRE_JOB_FLAG", "00");
    	    		
    	    		matList.add(m);
    	    	});
    	    	params.put("matList", matList);
    	    	
    	    	//2019-08-05优化，查询待过账数据状态是否为处理中，如存在处理中状态数据，说明有其他请求在处理这些数据，提示不能重复处理错误
    	    	List<Map<String,Object>> rtnMatList = commonService.getSapPostJobListByJobFlag(matList);
    	    	if(rtnMatList.size() > 0) {
    	    		StringBuffer sb = new StringBuffer();
    	    		for (Map<String, Object> map : rtnMatList) {
						sb.append(map.get("SAP_JOB_INFO")==null?"":map.get("SAP_JOB_INFO").toString());
					}
    	    		throw new RuntimeException("异步过账任务："+sb.toString()+"正在处理中，不能重复处理！");
    	    	}
    	    	
    	    	//2019-08-05优化，将待过账的数据状态修改为处理中...
    	    	int updateRows = commonService.updateSapJobItemJobFlag(matList);
    	    	if(updateRows <0 || updateRows != matList.size()) {
    	    		throw new RuntimeException("将过账失败任务状态更新为处理中失败！");
    	    	}
    			
    	    	return commonService.sapPostJobPost(params, matList);
		} catch(Exception e) {
			//处理异常，将过账失败任务状态恢复为过账失败状态
			for (Map<String,Object> matItem : matList) {
				matItem.put("UPDATRE_JOB_FLAG", matItem.get("JOB_FLAG"));
			}
			commonService.updateSapJobItemJobFlag(matList);
			e.printStackTrace();
			return R.error("处理失败，请联系管理员！"+e.getMessage());
		}
    }
    
    /**
     * 失败任务关闭（删除过账任务）
     * @param params
     * @return
     */
    @RequestMapping("/closePostJob")
    public R closePostJob(@RequestBody Map<String, Object> params) {
    	try {
       		Map<String,Object> currentUser = userUtils.getUser();
       		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		
	    	String matListStr = params.get("matList").toString();
			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
			
	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
	    		m=(Map<String,Object>)m;
				m.put("JOB_FLAG", "04");
				m.put("POST_QTY", null);
	    		
	    		m.put("CREATOR", params.get("CREATOR").toString());
	    		m.put("CREATE_DATE", curDate);
	    		matList.add(m);
	    	});
			
	    	return commonService.sapPostJobCancel(matList);
    		
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("操作失败，请联系管理员！"+e.getMessage());
		}
    }
}
