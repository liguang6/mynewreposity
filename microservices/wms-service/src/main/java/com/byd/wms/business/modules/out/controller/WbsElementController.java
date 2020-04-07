package com.byd.wms.business.modules.out.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WbsElementAO;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.service.CreateRequirementService;

/**
 * WBS 元素发货
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/wbsElement")
public class WbsElementController {
	
	@Autowired
	private WmsSapRemote wmsSapRemote;
	
	@Autowired
	private CreateRequirementService service;
	
	/**
	 * 从SAP查询WBS元素信息
	 * @param wbsElementNo
	 * @return
	 */
	@RequestMapping("/wbs_element/{wbsElementNo}")
	/*@RequiresPermissions("out:wbselement:query")*/
	public R queryWBSElement(@PathVariable("wbsElementNo")String wbsElementNo){
		Map<String,Object> wbs = wmsSapRemote.getSapBapiSapBapiWbs(wbsElementNo);
		return R.ok().put("data", wbs);
	}
	
	/**
	 * 创建WBS元素发货需求
	 * @param wbsList wbs列表,json
	 * @return
	 */
	@RequestMapping("/save")
	public R saveWBSElement(@RequestBody List<WbsElementAO> wbsList){
		//System.err.println(wbsList.toString());
		String requireNo = "";
		try {
			 requireNo =  service.createWBSElementOutRequirementSplit(wbsList);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}
		return R.ok().put("data", requireNo);
	}
	@Autowired
	FreeMarkerConfigurer conf;
	
	@Autowired
	WmsOutRequirementItemDao itemDAO;
	
	@Autowired
	WmsOutRequirementHeadDao headDAO;
	
	@RequestMapping("/pdf")
	public void pdfPreview(String requirementNo,String printSize,HttpServletResponse response,HttpServletRequest request){
		//入参为空，返回
		if(StringUtils.isBlank(requirementNo)){
			return ;
		}
		
		List<Map<String,Object>> listVar = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("title", "WBS元素发货单");
		map.put("printSize", printSize);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath(); 
		map.put("basePath", basePath);
		//项目全路径，定位图片等静态资源
		
		WmsOutRequirementHeadEntity queryEntity = new WmsOutRequirementHeadEntity();
		queryEntity.setRequirementNo(requirementNo);
		WmsOutRequirementHeadEntity headEntity = headDAO.selectOne(queryEntity);
		List<WmsOutRequirementItemEntity> itemList = itemDAO.selectList(new EntityWrapper<WmsOutRequirementItemEntity>().eq("REQUIREMENT_NO", requirementNo));
		//没查到数据，返回
		if(headEntity == null || itemList == null || itemList.size() == 0){
			return ;
		}
		map.put("head", headEntity);
		map.put("itemList", itemList);
		listVar.add(map);
		PdfUtils.preview(conf, "modules/out/requirement_wbs_element_print_tpl.html", listVar, response);
	}
}
