package com.byd.web.wms.out.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.byd.utils.R;
import com.byd.web.wms.common.service.WmsSapRemote;
import com.byd.web.wms.out.entity.WbsElementAO;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;

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
	WmsOutReqServiceRemote outReqServiceRemote;
	
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
	public R saveWBSElement(@RequestBody List<WbsElementAO> wbsList){return outReqServiceRemote.saveWBSElement(wbsList);}
	@Autowired
	FreeMarkerConfigurer conf;
	
	@RequestMapping("/pdf")
	public void pdfPreview(String requirementNo,String printSize,HttpServletResponse response,HttpServletRequest request){
		//TODO:
	}
}
