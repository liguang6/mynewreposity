package com.byd.web.out.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.R;
import com.byd.web.out.service.OutPdaRemote;

/**
 * PDA一步联动收货
 * 
 * @author ren.wei3
 *
 */

@RestController
@RequestMapping("/out/pda")
public class OutPdaController {

	@Autowired
	protected HttpSession session;
	@Autowired
	private OutPdaRemote xiaJiaJianPeiPdaRemote;

	/**
	 * 推荐
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/xiaJiaJianPei/tuiJian")
	public R tuiJian(@RequestParam Map<String, Object> params) {
		String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
    	params.put("REQUIREMENT_NO", reqno);
    	params.put("REFERENCE_DELIVERY_NO", reqno);
    	String dingWeiLable = params.get("DINGWEI_LABLE") == null?"":params.get("DINGWEI_LABLE").toString().trim();
    	params.put("DINGWEI_LABLE", dingWeiLable);
    	params.put("WH_NUMBER", session.getAttribute("warehouse"));
		params.put("WERKS", session.getAttribute("werks"));
		return xiaJiaJianPeiPdaRemote.tuiJian(params);
	}
	
	/**
	 * 下架拣配-根据页面扫描的条码进行校验，校验通过返回条码和数量，否则返回count=0
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/xiaJiaJianPei/scanLabel")
	public R scanLabel(@RequestParam Map<String, Object> params) {
		return xiaJiaJianPeiPdaRemote.scanLabel(params);
	}

	/**
	 * 保存下架信息
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/xiaJiaJianPei/saveXiaJiaXinXi")
	public R saveXiaJiaXinXi(@RequestParam Map<String, Object> params) {
		params.put("EDITOR", session.getAttribute("username"));
		params.put("label_ls", (List<Map>)JSONObject.parseArray(params.get("barcodes").toString(),Map.class));
		return xiaJiaJianPeiPdaRemote.saveXiaJiaXinXi(params);
	}
}
