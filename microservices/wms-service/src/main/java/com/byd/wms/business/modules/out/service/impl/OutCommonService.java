package com.byd.wms.business.modules.out.service.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;

/**
 * 出库模块的通用功能
 * @author develop07
 *
 */
@Service("outCommonService")
public class OutCommonService {
	@Autowired
	SendCreateRequirementDao dao;
	
	/**
	 * 
	 * @param werks
	 * @param businessCode
	 * @return 需要审批: true 不需审批: false
	 */
	boolean checkApproval(String werks,List businessCode) throws Exception {
		List list = new ArrayList();
		for (int i = 0;i<businessCode.size();i++){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("werks", werks);
			params.put("businessCode",businessCode.get(i));
			List<Map<String,Object>> approvalList = dao.selectApprovalFlag(params);
			if(CollectionUtils.isNotEmpty(approvalList)){
				String appFlag = (String) approvalList.get(0).get("APPROVAL_FLAG");
				list.add(appFlag);
			}
		}
		boolean flag = false;
		for (int j = 0;j<list.size()-1;j++){
			for (int k = 0;k<list.size()-1-j;k++){
				if(!list.get(k).equals(list.get(k+1))){
					flag = true;
				}
			}
		}
		System.err.println("flag======================"+flag);
		System.err.println("StringUtils.lowerCase((list.get(0).toString())))   "+StringUtils.lowerCase((list.get(0).toString())));
		if(!flag){
			if("x".equals(StringUtils.lowerCase((list.get(0).toString())))){
				return true;
			}
			return false;
		}else {
			System.err.println("工厂"+werks+"下的"+"审批状态不一致！");
			throw new Exception("工厂"+werks+"下的"+"审批状态不一致！");
		}

	}
	
	boolean checkApproval(String werks,String businessType,String businessName) throws Exception {
		List  bCode = dao.selectBusinessCode(businessType, businessName);
		System.err.println("bCode   "+bCode);
		return checkApproval(werks, bCode);
	}

}
