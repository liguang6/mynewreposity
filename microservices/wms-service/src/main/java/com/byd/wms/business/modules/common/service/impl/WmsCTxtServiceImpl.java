package com.byd.wms.business.modules.common.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.wms.business.modules.common.dao.WmsCTxtDao;
import com.byd.wms.business.modules.common.entity.WmsCTxtEntity;
import com.byd.wms.business.modules.common.service.WmsCTxtService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月23日 上午9:41:47 
 * 类说明 
 */
@Service
public class WmsCTxtServiceImpl extends ServiceImpl<WmsCTxtDao,WmsCTxtEntity> implements WmsCTxtService {
	@Autowired
	WmsCTxtDao wmscTxtDao;
	@Override
	/**
	 * params必须传入  BUSINESS_NAME 业务类型名称 ，可选参数 WERKS 工厂代码
	 * 
	 */
	public Map<String, Object> getRuleTxt(Map<String, String> params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		String txtrule_ret="";
		String txtruleitem_ret="";
		if(params.get("BUSINESS_NAME")==null){
			retMap.put("msg", "业务类型名称必须传入!");
			return retMap;
		}else{
			String sql="";
			if(params.get("WERKS")!=null&&!"".equals(params.get("WERKS"))){
				sql="WERKS='"+params.get("WERKS")+"'";
			}else{
				sql="1=1";
			}
			List<WmsCTxtEntity> wmsctxtlist=this.selectList(new EntityWrapper<WmsCTxtEntity>().eq("BUSINESS_NAME", params.get("BUSINESS_NAME"))
					.addFilter(sql)
					.eq("DEL", "0"));
			if(wmsctxtlist.size()==0){//查询无工厂维度配置
				wmsctxtlist=this.selectList(new EntityWrapper<WmsCTxtEntity>().eq("BUSINESS_NAME", params.get("BUSINESS_NAME"))
						.eq("DEL", "0"));			
			}
			
			if(wmsctxtlist.size()==0) {
				retMap.put("msg", "success");
				retMap.put("txtrule", txtrule_ret);
				retMap.put("txtruleitem", txtruleitem_ret);
				return retMap;
			}
			
			String txtrule=wmsctxtlist.get(0).getTxtRule();
			String txtruleitem=wmsctxtlist.get(0).getTxtRuleItem();
			String[] txtruleArray=StringUtils.substringsBetween(txtrule, "{", "}");//头文本规则
			String[] txtruleitemArray=StringUtils.substringsBetween(txtruleitem, "{", "}");//行文本规则
			
	/*		for(int i=0;i<txtruleArray.length;i++){//检查头文本规则的参数是否都齐全
				if(params.get(txtruleArray[i])==null){
					retMap.put("msg", "该规则必须传入参数"+txtruleArray[i]);
					return retMap;
				}
			}
			
			for(int j=0;j<txtruleitemArray.length;j++){//检查行文本规则的参数是否都齐全
				if(params.get(txtruleitemArray[j])==null){
					retMap.put("msg", "该规则必须传入参数"+txtruleitemArray[j]);
					return retMap;
				}
			}*/
			if(txtruleArray!=null) {
				for(int m=0;m<txtruleArray.length;m++){//头文本替换
						for (String key : params.keySet()) {
							if(key.equals(txtruleArray[m])){
								txtrule_ret=txtrule.replace("{"+txtruleArray[m]+"}", params.get(key)==null?"":params.get(key));
								txtrule=txtrule_ret;
							}
					}
				}
			

				for(int m=0;m<txtruleArray.length;m++){//头文本替换 没有被替换掉的变量 否则会出现包含{WERKS}的数据
					txtrule_ret=txtrule.replace("{"+txtruleArray[m]+"}", "");
					txtrule=txtrule_ret;
				}
			}
			retMap.put("txtrule", txtrule_ret);
			
			if(txtruleitemArray!=null) {
				for(int n=0;n<txtruleitemArray.length;n++){//行文本替换
					for(String key : params.keySet()){
						if(key.equals(txtruleitemArray[n])){
							txtruleitem_ret=txtruleitem.replace("{"+txtruleitemArray[n]+"}", params.get(key)==null?"":params.get(key));
							txtruleitem=txtruleitem_ret;
						}
					}
				}
				
				for(int n=0;n<txtruleitemArray.length;n++){//行文本替换 没有被替换掉的变量 否则会出现包含{WERKS}的数据
					txtruleitem_ret=txtruleitem.replace("{"+txtruleitemArray[n]+"}", "");
					txtruleitem=txtruleitem_ret;
				}
			}
			
			retMap.put("txtruleitem", txtruleitem_ret);
			retMap.put("msg", "success");
		}
		return retMap;
	}
	

}
