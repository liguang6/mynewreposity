package com.byd.wms.business.modules.common.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.annotations.TableField;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.AppConfigConstant;
import com.byd.wms.business.modules.common.entity.WmsCDocNo;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;

@RestController
@RequestMapping("/common/docNo")
public class WmsCDocNoController {
	@Autowired
	WmsCDocNoService wmscDocNoService;
	@Autowired
	private AppConfigConstant appconfigconstant;
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmscDocNoService.queryPage(params);	
		List<WmsCDocNo> cDocNOList=(List<WmsCDocNo>) page.getList();
		if(cDocNOList!=null){
			for(int i=0;i<cDocNOList.size();i++){
				String doctype=cDocNOList.get(i).getDocType();
				if(!"".equals(doctype)||doctype!=null){
					
					//在字典表中查询出该类型的id
					Map<String, Object> paramDict=new HashMap<String, Object> ();
					paramDict.put("TYPE", "WMS_DOC_TYPE");//WMS单据类型
					paramDict.put("CODE", cDocNOList.get(i).getDocType());
					List<Map<String,Object>> retDictlist=wmscDocNoService.getDictByMap(paramDict);
					Map<String,Object> sysDict=null;
					if(retDictlist.size()>0){
						sysDict=retDictlist.get(0);
					}
					if(sysDict!=null){
						cDocNOList.get(i).setDocTypeName(sysDict.get("VALUE").toString());
						
						String sys_str=appconfigconstant.getSys();//系统代码
						cDocNOList.get(i).setSys(sys_str);
					}
				}
				
			}
		}
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/listEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		params.put("del", "0");
		String onlyOne="";
		List<WmsCDocNo> entityList=wmscDocNoService.selectByMap(params);
		if(entityList!=null&&entityList.size()>0){
			onlyOne="true";
		}else{
			onlyOne="false";
		}
		return R.ok().put("onlyOne", onlyOne);
	}
	
	@RequestMapping("/save")
	public R add(@RequestParam Map<String, Object> params){
		WmsCDocNo entity = new WmsCDocNo();
		/**
		 * 工厂代码
		 */
		String werks = params.get("werks")==null?"":params.get("werks").toString();
		/**
		 * 单据类型
		 */
		String docType = params.get("docType")==null?"":params.get("docType").toString();
		/**
		 * 编号前缀
		 */
		String preNo = params.get("preNo")==null?"":params.get("preNo").toString();
		/**
		 * 编号后缀
		 */
		String backNo = params.get("backNo")==null?"":params.get("backNo").toString();
		/**
		 * 起始号
		 */
		int startNo = params.get("backNo")==null?1:Integer.valueOf(params.get("backNo").toString());
		/**
		 * 递增
		 */
		int incrementNum = params.get("incrementNum")==null?1:Integer.valueOf(params.get("incrementNum").toString());
		/**
		 * 编号长度
		 */
		int noLength = params.get("noLength")==null?10:Integer.valueOf(params.get("noLength").toString());
		/**
		 * 当前编号
		 */
		int currentNo = params.get("currentNo")==null?1:Integer.valueOf(params.get("currentNo").toString());
		
		String memo = params.get("memo")==null?"":params.get("memo").toString();
		
		/**
		 * 单据类型名称
		 */
		String docTypeName = params.get("docTypeName")==null?"":params.get("docTypeName").toString();
		
		String sys = params.get("sys")==null?"":params.get("sys").toString();
		
		entity.setWerks(werks);
		entity.setDocType(docType);
		entity.setPreNo(preNo);
		entity.setBackNo(backNo);
		entity.setStartNo(startNo);
		entity.setIncrementNum(incrementNum);
		entity.setNoLength(noLength);
		entity.setCurrentNo(currentNo);
		entity.setMemo(memo);
		entity.setDocTypeName(docTypeName);
		entity.setSys(sys);
		//validate
		entity.setDel("0");
		entity.setCreator(params.get("USERNAME")==null?"":params.get("USERNAME").toString());
		entity.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
		wmscDocNoService.insert(entity);
		return R.ok();
	}
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	WmsCDocNo cdocno = wmscDocNoService.selectById(id);
    	cdocno.setDocTypeName("peizhi");
        return R.ok().put("cdocno", cdocno);
    }
    
    @RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params){
    	
		WmsCDocNo entity = new WmsCDocNo();
		entity.setId(Long.valueOf(params.get("id").toString()));
		/**
		 * 工厂代码
		 */
		String werks = params.get("werks")==null?"":params.get("werks").toString();
		/**
		 * 单据类型
		 */
		String docType = params.get("docType")==null?"":params.get("docType").toString();
		/**
		 * 编号前缀
		 */
		String preNo = params.get("preNo")==null?"":params.get("preNo").toString();
		/**
		 * 编号后缀
		 */
		String backNo = params.get("backNo")==null?"":params.get("backNo").toString();
		/**
		 * 起始号
		 */
		int startNo = params.get("backNo")==null?1:Integer.valueOf(params.get("backNo").toString());
		/**
		 * 递增
		 */
		int incrementNum = params.get("incrementNum")==null?1:Integer.valueOf(params.get("incrementNum").toString());
		/**
		 * 编号长度
		 */
		int noLength = params.get("noLength")==null?10:Integer.valueOf(params.get("noLength").toString());
		/**
		 * 当前编号
		 */
		int currentNo = params.get("currentNo")==null?1:Integer.valueOf(params.get("currentNo").toString());
		
		String memo = params.get("memo")==null?"":params.get("memo").toString();
		
		/**
		 * 单据类型名称
		 */
		String docTypeName = params.get("docTypeName")==null?"":params.get("docTypeName").toString();
		
		String sys = params.get("sys")==null?"":params.get("sys").toString();
		
		entity.setWerks(werks);
		entity.setDocType(docType);
		entity.setPreNo(preNo);
		entity.setBackNo(backNo);
		entity.setStartNo(startNo);
		entity.setIncrementNum(incrementNum);
		entity.setNoLength(noLength);
		entity.setCurrentNo(currentNo);
		entity.setMemo(memo);
		entity.setDocTypeName(docTypeName);
		entity.setSys(sys);
		entity.setDel(params.get("del")==null?"":params.get("del").toString());
		entity.setEditor(params.get("USERNAME")==null?"":params.get("USERNAME").toString());
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
		wmscDocNoService.updateById(entity);
		return R.ok();
	}
    
    @RequestMapping("/del")
	public R delete(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCDocNo entity = new WmsCDocNo();
		entity.setId(id);
		entity.setDel("X");
		wmscDocNoService.updateById(entity);
		return R.ok();
	}
	@RequestMapping("/dels")
	public R deletes(@RequestParam String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsCDocNo entity = new WmsCDocNo();
			entity.setId(Long.parseLong(id[i]));
			entity.setDel("X");
			wmscDocNoService.updateById(entity);
		}
		return R.ok();
	}
	
	@RequestMapping("/getdocno")
	public R getdocno(@RequestParam Map<String, Object> params){
		Map<String, Object> docnomap = wmscDocNoService.getDocNo(params);	
		return R.ok().put("docnomap", docnomap);
	}
}
