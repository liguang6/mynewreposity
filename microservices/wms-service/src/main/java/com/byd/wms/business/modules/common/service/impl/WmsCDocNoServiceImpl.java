package com.byd.wms.business.modules.common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.common.AppConfigConstant;
import com.byd.wms.business.modules.common.dao.WmsCDocNoDao;
import com.byd.wms.business.modules.common.entity.WmsCDocNo;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
@Service
public class WmsCDocNoServiceImpl extends ServiceImpl<WmsCDocNoDao, WmsCDocNo> implements WmsCDocNoService{
	@Autowired
	private AppConfigConstant appconfigconstant;
	@Autowired
	WmsCDocNoDao wmsCDocNoDao;
	@Autowired
	RedisUtils redisUtils;
	
	@Override
	public List<Map<String, Object>> getDictByMap(Map<String, Object> params){
		return wmsCDocNoDao.getDictByMap(params);
	}
	
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		Page<WmsCDocNo> page = this.selectPage(new Query<WmsCDocNo>(params).getPage(),
				new EntityWrapper<WmsCDocNo>().like("werks", werks).eq("del", "0")//已经标识为删除的数据不显示
				);
		return new PageUtils(page);
	}

	/**
	 * params 传入参数 WERKS 工厂代码   WMS_DOC_TYPE WMS单据类型
	 */
//	@Override
//	public Map<String, Object> getDocNo(Map<String, Object> params) {
//		// 根据 werks doctype查询出配置信息
//		// if 当前编号==0 那么 当前编号=起始号
//		// 编号=系统代码+编号前缀 +当前编号+编号后缀
//		// else 
//		// 编号=系统代码+编号前缀 +当前编号+编号后缀
//		// 当前编号=当前编号+递增数量
//		Map<String, Object> param=new HashMap<String, Object> ();
//		String sys_str=appconfigconstant.getSys();//系统代码
//		
//		param.put("werks", params.get("WERKS"));
//		param.put("doc_type", params.get("WMS_DOC_TYPE"));
//		param.put("del", "0");
//		
//		Map<String, Object> retmap=new HashMap<String, Object>();
//		String docno="";
//		String msg="";
//		List<WmsCDocNo> docNolist=this.selectByMap(param);
//		if(docNolist==null || docNolist.size()<=0) {
//			param.remove("werks");
//			docNolist=this.selectByMap(param);
//		}
//		if(docNolist!=null&&docNolist.size()>0){
//			int nolength=docNolist.get(0).getNoLength();//编码长度
//			int docno_current=0;//当前编码
//			
//			WmsCDocNo docNoEntity = docNolist.get(0);
//			//获取编号
//			wmsCDocNoDao.getDocNextNo(docNoEntity);
//			docno_current = docNoEntity.getCurrentNo();
//			
//			String docno_current_str=String.valueOf(docno_current);
//			
//			String add0="";
//			if(docno_current_str.length()<nolength){//当前长度小于设置的长度
//				for(int i=0;i<nolength-docno_current_str.length();i++){
//					add0="0".concat(add0);
//				}
//			}else if(docno_current_str.length()>nolength){//当前长度大于设置的长度
//				msg="当前编码的长度大于设置的长度!请重新设置长度!";
//			}
//			docno_current_str=add0.concat(docno_current_str);
//			if("".equals(msg)){//没有报错信息
//				docno=sys_str+(docNolist.get(0).getPreNo()==null?"":docNolist.get(0).getPreNo())+docno_current_str+(docNolist.get(0).getBackNo()==null?"":docNolist.get(0).getBackNo());
//			}
//			
//			retmap.put("docno", docno);
//			retmap.put("MSG", "success");
//		}else{
//			retmap.put("MSG", "单据编号规则未配置，无法产生单据编号!");
//		}
//		return retmap;
//	}
	
	@Override
	public synchronized Map<String, Object> getDocNo(Map<String, Object> params) {
		// 根据 werks doctype查询出配置信息
		// if 当前编号==0 那么 当前编号=起始号
		// 编号=系统代码+编号前缀 +当前编号+编号后缀
		// else 
		// 编号=系统代码+编号前缀 +当前编号+编号后缀
		// 当前编号=当前编号+递增数量
		/**
		 * 编号从Redis中取，当Redis中docNo数量少于10，就查询出配置信息，补充Redis中docNo列表，
		 * 数量默认100，docType为08和14类型的每次补充数量为500
		 * 并更新WMS_C_DOC_NO表中CURRENT_NO的值
		 */		
		Map<String, Object> retmap=new HashMap<String, Object>();
		String docno="";
		String msg="";

		String docType = params.get("WMS_DOC_TYPE").toString();
		Map<String, Object> param=new HashMap<String, Object> ();
		String sys_str=appconfigconstant.getSys();//系统代码
		
		Map<String,Object> rs_map = redisUtils.get("wms:docType_"+docType,Map.class);
		int doc_no_count = 0; //redis中doc_no剩余数量
		int add_count = 0;	//补全数量
		int incrementNum = 1; //步长
		int docno_current=0;//当前编码
		int no_length =0;//配置的doc_no长度
		String preNo = ""; //前缀
		String backNo = ""; //后缀
		
		if(rs_map!=null){
			doc_no_count =  (int) rs_map.get("doc_no_count");
			docno_current = (int) rs_map.get("docno_current");
			incrementNum = (int) rs_map.get("incrementNum");
			no_length = (int) rs_map.get("no_length");
			preNo = rs_map.get("preNo")==null?"":rs_map.get("preNo").toString();
			backNo = rs_map.get("backNo")==null?"":rs_map.get("backNo").toString();

		}else{//redis 中未找到docNo信息，查询数据库表中获取当前docNo信息，并保存到redis中
			if("08".equals(docType) || "14".equals(docType)){
				add_count = 500;
			}else
				add_count = 100;
			
			param.put("werks", params.get("WERKS"));
			param.put("doc_type", docType);
			param.put("del", "0");
			List<WmsCDocNo> docNolist=this.selectByMap(param);
			if(docNolist==null || docNolist.size()<=0) {
				param.remove("werks");
				docNolist=this.selectByMap(param);
			}	

			if(docNolist!=null&&docNolist.size()>0){
				incrementNum = docNolist.get(0).getIncrementNum();
				docno_current = docNolist.get(0).getCurrentNo()+incrementNum;
				no_length = docNolist.get(0).getNoLength();
				rs_map = new HashMap<String,Object>();
				preNo = docNolist.get(0).getPreNo() == null?"":docNolist.get(0).getPreNo();
				backNo = docNolist.get(0).getBackNo() == null?"":docNolist.get(0).getBackNo();
				rs_map.put("doc_no_count", add_count);
				rs_map.put("docno_current", docno_current);
				rs_map.put("incrementNum", incrementNum);
				rs_map.put("no_length", no_length);
				rs_map.put("preNo", preNo);
				rs_map.put("backNo", backNo);
				
				doc_no_count = add_count;

				param.put("add_count", add_count);
				wmsCDocNoDao.updateDocNoById(param);
			}else{
				retmap.put("MSG", "单据编号规则未配置，无法产生单据编号!");
				return retmap;
			}
			

		}		
		if(doc_no_count<=10){//不足时，补全redis中doc_no_count数量，更新数据库中当前docNo
			if("08".equals(docType) || "14".equals(docType)){
				add_count = 500-doc_no_count;
			}else
				add_count = 100-doc_no_count;

			docno_current = (int)rs_map.get("docno_current");

			param.put("add_count", add_count);
			wmsCDocNoDao.updateDocNoById(param);

			rs_map.put("doc_no_count", add_count+doc_no_count);

		}else{
			docno_current = (int)rs_map.get("docno_current");
		}
		
		String docno_current_str=String.valueOf(docno_current);
			
		String add0="";
		if(docno_current_str.length()<no_length){//当前长度小于设置的长度
			for(int i=0;i<no_length-docno_current_str.length();i++){
				add0="0".concat(add0);
			}
		}else if(docno_current_str.length()>no_length){//当前长度大于设置的长度
			msg="当前编码的长度大于设置的长度!请重新设置长度!";
			retmap.put("MSG", msg);
			return retmap;
		}
		docno_current_str=add0.concat(docno_current_str);
		if("".equals(msg)){//没有报错信息
			docno=sys_str+(preNo+docno_current_str+backNo);
		}
		
		retmap.put("docno", docno);
		retmap.put("MSG", "success");

		rs_map.put("doc_no_count", doc_no_count-1);
		rs_map.put("docno_current", docno_current+incrementNum);
		redisUtils.set("wms:docType_"+docType, rs_map);
		return retmap;
	}

	/**
	 *@param  工厂代码   WMS单据类型
	 *@return
	 */
	@Override
	public String getDocNo(String werks, String docType) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("WERKS", werks);
		params.put("WMS_DOC_TYPE", docType);
		Map<String,Object> mapResult = getDocNo(params);
		return (String)mapResult.get("docno");
	}
	
	/**
	 * 批量获取
	 */
	@Override
	public Map<String, Object> getDocNoBatch(Map<String, Object> params) {
		// 根据 werks doctype查询出配置信息
		// if 当前编号==0 那么 当前编号=起始号
		// 编号=系统代码+编号前缀 +当前编号+编号后缀
		// else 
		// 编号=系统代码+编号前缀 +当前编号+编号后缀
		// 当前编号=当前编号+递增数量
		int incrementNum = Integer.parseInt(params.get("INCREMENT_NUM") == null ? "0" : params.get("INCREMENT_NUM").toString());
		Map<String, Object> param=new HashMap<String, Object> ();
		String sys_str=appconfigconstant.getSys();//系统代码
		
		param.put("werks", params.get("WERKS"));
		param.put("doc_type", params.get("WMS_DOC_TYPE"));
		param.put("del", "0");
		
		Map<String, Object> retmap=new HashMap<String, Object>();
		String docno="";
		String msg="";
		List<WmsCDocNo> docNolist=this.selectByMap(param);
		if(docNolist==null || docNolist.size()<=0) {
			param.remove("werks");
			docNolist=this.selectByMap(param);
		}
		if(docNolist!=null&&docNolist.size()>0){
			int nolength=docNolist.get(0).getNoLength();//编码长度
			int docno_current=0;//当前编码
			
			WmsCDocNo docNoEntity = docNolist.get(0);
			
			if (incrementNum != 0) 
				docNoEntity.setIncrementNum(incrementNum);
			//获取编号
			wmsCDocNoDao.getDocNextNo(docNoEntity);
			docno_current = docNoEntity.getCurrentNo();
			
			List<String> docnolist = new ArrayList<String>();
			for (int n=0; n<incrementNum; n++) {
				String docno_current_str=String.valueOf(docno_current);
				
				String add0="";
				if(docno_current_str.length()<nolength){//当前长度小于设置的长度
					for(int i=0;i<nolength-docno_current_str.length();i++){
						add0="0".concat(add0);
					}
				}else if(docno_current_str.length()>nolength){//当前长度大于设置的长度
					msg="当前编码的长度大于设置的长度!请重新设置长度!";
				}
				docno_current_str=add0.concat(docno_current_str);
				if("".equals(msg)){//没有报错信息
					docno=sys_str+(docNolist.get(0).getPreNo()==null?"":docNolist.get(0).getPreNo())+docno_current_str+(docNolist.get(0).getBackNo()==null?"":docNolist.get(0).getBackNo());
				}
				docnolist.add(docno);
				docno_current--;
			}
			
			retmap.put("docno", docnolist);
			retmap.put("MSG", "success");
		}else{
			retmap.put("MSG", "单据编号规则未配置，无法产生单据编号!");
		}
		return retmap;
	}

}
