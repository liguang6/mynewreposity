package com.byd.web.wms.kn.controller;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.ConfigConstant;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.wms.kn.service.WmsCBarcodeSterilisationRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
@RestController
@RequestMapping("kn/barcodeSterilisation")
public class WmsCBarcodeSterilisationController {
	@Autowired
	private WmsCBarcodeSterilisationRemote WmsCBarcodeSterilisationRemote;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private FreeMarkerConfigurer configurer;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;


	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return WmsCBarcodeSterilisationRemote.list(params);
	}
		
	@RequestMapping("/one")
	public R one(@RequestParam Map<String, Object> params) {
		return WmsCBarcodeSterilisationRemote.one(params);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/saveCoreLabel")
	public R save(@RequestParam Map<String, Object> params) {
		return WmsCBarcodeSterilisationRemote.saveCoreLabel(params);
	}

	/**
	 *
	 * 打印标签预览
	 */
	@RequestMapping("/print")
	public R print(@RequestParam Map<String, Object> params) {
		return WmsCBarcodeSterilisationRemote.printCoreLabel(params);
	}

	@RequestMapping("/labelLabelPreview")
	public void labelLabelPreview(@RequestParam Map<String, Object> params) {
		String labelListStr = params.get("labelList")==null?null:params.get("labelList").toString();

		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String,Object>> listVars = new ArrayList<>();

		String baseDir = configConstant.getLocation()+"/barcode/";

		if(labelListStr!=null && labelListStr.length()>0) {
			List<Map> list=JSONObject.parseArray(labelListStr, Map.class);
			for (Map<String,Object> m:list ){
				m=(Map<String,Object>)m;

				Map<String,Object> variables = new HashMap<>();
				variables.put("LIKTX",m.get("LIKTX")==null?"":m.get("LIKTX"));
				variables.put("LABEL_NO",m.get("LABELNO")==null?"":m.get("LABELNO"));
				variables.put("MATNR",m.get("MATNR")==null?"":m.get("MATNR"));
				variables.put("MAKTX",m.get("MAKTX")==null?"":m.get("MAKTX"));
				variables.put("LIFNR",m.get("LIFNR")==null?"":m.get("LIFNR"));
				variables.put("WERKS",m.get("WERKS")==null?"":m.get("WERKS"));
				variables.put("PO_NO",m.get("PONO")==null?"":m.get("PONO"));
				variables.put("PO_ITEM_NO",m.get("POITEMNO")==null?"":m.get("POITEMNO"));
				variables.put("BEDNR",m.get("BEDNR")==null?"":m.get("BEDNR"));
				variables.put("PRODUCT_DATE",m.get("PRODUCTDATE")==null?"":m.get("PRODUCTDATE"));
				variables.put("BATCH",m.get("BATCH")==null?"":m.get("BATCH"));
				variables.put("BOX_QTY",m.get("BOXQTY")==null?"":m.get("BOXQTY"));
				variables.put("UNIT",m.get("UNIT")==null?"":m.get("UNIT"));

				//生成二维码
				try {
					String picturePath = ""; //图片路径
					StringBuffer sb = new StringBuffer();
					sb.append("{LABEL_NO:"+m.get("LABELNO").toString());
					sb.append(",MATNR:"+m.get("MATNR").toString());
					sb.append(",BATCH:"+m.get("BATCH").toString());
					sb.append(",BOX_QTY:"+m.get("BOXQTY")==null?"0":m.get("BOXQTY").toString());
					sb.append(",UNIT:"+m.get("UNIT").toString()+"}");

					picturePath = QRCodeUtil.encode(sb.toString(),m.get("LABELNO").toString(),"",baseDir,true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					variables.put("barCode","file:"+picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				listVars.add(variables);
			};
		}

		PdfUtils.preview(configurer,"wms/print/labelTmp_Label.html",listVars,response);
	}
}
