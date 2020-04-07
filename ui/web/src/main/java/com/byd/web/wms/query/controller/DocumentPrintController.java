package com.byd.web.wms.query.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.byd.web.wms.config.service.PrintTemplateRemote;
import com.byd.web.wms.kn.service.WmsKnLabelRecordRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.ConfigConstant;
import com.byd.utils.PdfUtils;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.wms.in.service.WmsInboundRemote;
import com.byd.web.wms.query.service.DocumentPrintRemote;
import com.google.common.collect.Lists;

/**
 * 单据打印通用控制器
 *
 * @author develop01
 * @since 2018-12-13
 */

@RestController
@RequestMapping("docPrint")
public class DocumentPrintController {
	@Autowired
	private DocumentPrintRemote documentPrintRemote;
	@Autowired
	private WmsInboundRemote wmsInboundRemote;
	@Autowired
	private FreeMarkerConfigurer configurer;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;
	@Autowired
	private PrintTemplateRemote printTemplateRemote;
    @Autowired
	WmsKnLabelRecordRemote wmsKnLabelRecordRemote;

	/**
	 * 需求查询条码打印
	 *
	 * @param params
	 */
	@RequestMapping(value = "/outRequirementQueryPrint")
	public void outRequirementQueryPrint(@RequestParam Map<String, Object> params) {
		/**
		 * 成本中心- 对应字段COST_CENTER、 WBS元素号 -WBS、 SAP交货单-SAP_OUT_NO、 订单号-PO_NO
		 * /MO_NO/SO_NO/IO_NO ， 凭证号- SAP_MATDOC_NO
		 *
		 */
		System.err.println("params " + params);
		String LabelList = params.get("LabelList") == null ? null : params.get("LabelList").toString();
		System.err.println("LabelList " + LabelList);
		String LIKTX = params.get("LIKTX") == null ? "" : params.get("LIKTX").toString();
		List<Map<String, Object>> listVars = new ArrayList<>();
		List<Map> matListMap = JSONObject.parseArray(LabelList, Map.class);
		// String PageSize =
		// params.get("PageSize")==null?"0":params.get("PageSize").toString();
		String PageWidth = "";
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String logoPicturePath = basePath + ":" + port + request.getContextPath();
		int step = 10;
		if (LIKTX.equals("小标签打印")) {
			PageWidth = "148.5mm";
			step = 10;
		} else if (LIKTX.equals("大标签打印")) {
			PageWidth = "297mm";
			step = 30;
		}

		for (Map mat : matListMap) {
			if (mat.get("MAKTX") != null && !mat.get("MAKTX").equals("")) {
				String maktx = mat.get("MAKTX").toString();
				mat.put("MAKTX", maktx.length() <= 27 ? maktx : maktx.substring(0, 27));
			}
		}

		String BUSINESS_NAME = matListMap.get(0).get("BUSINESS_NAME") == null ? ""
				: matListMap.get(0).get("BUSINESS_NAME").toString();
		System.err.println("BUSINESS_NAME " + BUSINESS_NAME);
		// 生产订单领料，补料
		if ("41".equals(BUSINESS_NAME) || "42".equals(BUSINESS_NAME)) {
			System.err.println("生产订单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
//					if ("41".equals(BUSINESS_NAME)) {
//						variables.put("HEADER_TXT", "生产订单领料单");
//					} else if ("42".equals(BUSINESS_NAME)) {
//						variables.put("HEADER_TXT", "生产订单补料单");
//					}
					variables.put("HEADER_TXT", matListMap.get(0).get("PURPOSE"));
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));

					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryScdd.html", listVars, response);
		}
		// 内部订单领料单
		if ("43".equals(BUSINESS_NAME)) {
			System.err.println("内部订单领料单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
//					variables.put("HEADER_TXT", "内部订单领料单");
					variables.put("HEADER_TXT", matListMap.get(0).get("PURPOSE"));
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));

					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryNbdd.html", listVars, response);
		}
		// 成本中心领料单
		if ("44".equals(BUSINESS_NAME)) {
			System.err.println("成本中心领料单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
//					variables.put("HEADER_TXT", "成本中心领料单");
					variables.put("HEADER_TXT", matListMap.get(0).get("PURPOSE"));
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));

					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryCbzx.html", listVars, response);
		}
		// WBS领料单
		if ("45".equals(BUSINESS_NAME)) {
			System.err.println("WBS领料单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();
					String WBS = (matListMap.get(0).get("WBS") == null) ? " " : matListMap.get(0).get("WBS").toString();
					String RECEIVE_WERKS = (matListMap.get(0).get("RECEIVE_WERKS") == null) ? " "
							: matListMap.get(0).get("RECEIVE_WERKS").toString();
					String RECEIVE_LGORT = (matListMap.get(0).get("RECEIVE_LGORT") == null) ? " "
							: matListMap.get(0).get("RECEIVE_LGORT").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);
					variables.put("WBS", WBS);

					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("RECEIVE_WERKS", RECEIVE_WERKS);
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("RECEIVE_LGORT", RECEIVE_LGORT);
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
//					variables.put("HEADER_TXT", "WBS领料单");
					variables.put("HEADER_TXT", matListMap.get(0).get("PURPOSE"));

					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryWbs.html", listVars, response);
		}
		//委托订单投料
		if("49".equals(BUSINESS_NAME)){
			System.err.println("委托订单投料打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0){
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
//					variables.put("HEADER_TXT", "委托订单投料单");
					variables.put("HEADER_TXT", matListMap.get(0).get("PURPOSE"));
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));

					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryWtdd.html", listVars, response);
		}
		// 调拨领料单
		if ("50".equals(BUSINESS_NAME) || "53".equals(BUSINESS_NAME)
				|| "48".equals(BUSINESS_NAME) || "47".equals(BUSINESS_NAME) || "46".equals(BUSINESS_NAME)
				|| "77".equals(BUSINESS_NAME) || "76".equals(BUSINESS_NAME)) {
			System.err.println("调拨单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					for (Map mat : matList) {
						if (mat.get("LIKTX") != null && !mat.get("LIKTX").equals("")) {
							String liktx = mat.get("LIKTX").toString();
							mat.put("LIKTX", liktx.length() <= 8 ? liktx : liktx.substring(0, 8));
						}
					}
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();
					String WBS = (matListMap.get(0).get("WBS") == null) ? " " : matListMap.get(0).get("WBS").toString();
					String RECEIVE_WERKS = (matListMap.get(0).get("RECEIVE_WERKS") == null) ? " "
							: matListMap.get(0).get("RECEIVE_WERKS").toString();
					String RECEIVE_LGORT = (matListMap.get(0).get("RECEIVE_LGORT") == null) ? " "
							: matListMap.get(0).get("RECEIVE_LGORT").toString();

					String PURPOSE = (matListMap.get(0).get("PURPOSE") == null) ? " "
							: matListMap.get(0).get("PURPOSE").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);
					variables.put("WBS", WBS);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("RECEIVE_WERKS", RECEIVE_WERKS);
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("RECEIVE_LGORT", RECEIVE_LGORT);
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());

					variables.put("PURPOSE", PURPOSE);

					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
					variables.put("HEADER_TXT", "调拨单");
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));
					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryDbd.html", listVars, response);
		}
		// 销售订单
		if ("52".equals(BUSINESS_NAME)) {
			System.err.println("销售订单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();
					String WBS = (matListMap.get(0).get("WBS") == null) ? " " : matListMap.get(0).get("WBS").toString();
					String RECEIVE_WERKS = (matListMap.get(0).get("RECEIVE_WERKS") == null) ? " "
							: matListMap.get(0).get("RECEIVE_WERKS").toString();
					String RECEIVE_LGORT = (matListMap.get(0).get("RECEIVE_LGORT") == null) ? " "
							: matListMap.get(0).get("RECEIVE_LGORT").toString();
					String CUSTOMER = (matListMap.get(0).get("CUSTOMER") == null) ? " "
							: matListMap.get(0).get("CUSTOMER").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);
					variables.put("WBS", WBS);
					variables.put("CUSTOMER", CUSTOMER);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("RECEIVE_WERKS", RECEIVE_WERKS);
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("RECEIVE_LGORT", RECEIVE_LGORT);
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
					variables.put("HEADER_TXT", "销售发货单");
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));

					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryXsdd2.html", listVars, response);
		}
		// UB转储单
		if ("64".equals(BUSINESS_NAME)) {
			System.err.println("UB转储单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();
					String WBS = (matListMap.get(0).get("WBS") == null) ? " " : matListMap.get(0).get("WBS").toString();
					String RECEIVE_WERKS = (matListMap.get(0).get("RECEIVE_WERKS") == null) ? " "
							: matListMap.get(0).get("RECEIVE_WERKS").toString();
					String RECEIVE_LGORT = (matListMap.get(0).get("RECEIVE_LGORT") == null) ? " "
							: matListMap.get(0).get("RECEIVE_LGORT").toString();
					String CUSTOMER = (matListMap.get(0).get("CUSTOMER") == null) ? " "
							: matListMap.get(0).get("CUSTOMER").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);
					variables.put("WBS", WBS);
					variables.put("CUSTOMER", CUSTOMER);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("RECEIVE_WERKS", RECEIVE_WERKS);
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("RECEIVE_LGORT", RECEIVE_LGORT);
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
					variables.put("HEADER_TXT", "UB转储单");
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));
					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryUb.html", listVars, response);
		}
		// 销售订单发货/SAP交货单销售发货（311F）
		if ("51".equals(BUSINESS_NAME)) {
			System.err.println("销售订单打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String SAP_OUT_NO = (matListMap.get(0).get("SAP_OUT_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_NO").toString();
					String SAP_MATDOC_NO = (matListMap.get(0).get("SAP_MATDOC_NO") == null) ? " "
							: matListMap.get(0).get("SAP_MATDOC_NO").toString();
					String IO_NO = (matListMap.get(0).get("IO_NO") == null) ? " "
							: matListMap.get(0).get("IO_NO").toString();
					String COST_CENTER = (matListMap.get(0).get("COST_CENTER") == null) ? " "
							: matListMap.get(0).get("COST_CENTER").toString();
					String WBS = (matListMap.get(0).get("WBS") == null) ? " " : matListMap.get(0).get("WBS").toString();
					String RECEIVE_WERKS = (matListMap.get(0).get("RECEIVE_WERKS") == null) ? " "
							: matListMap.get(0).get("RECEIVE_WERKS").toString();
					String RECEIVE_LGORT = (matListMap.get(0).get("RECEIVE_LGORT") == null) ? " "
							: matListMap.get(0).get("RECEIVE_LGORT").toString();
					String CUSTOMER = (matListMap.get(0).get("CUSTOMER") == null) ? " "
							: matListMap.get(0).get("CUSTOMER").toString();
					String SAP_OUT_ITEM_NO = (matListMap.get(0).get("SAP_OUT_ITEM_NO") == null) ? " "
							: matListMap.get(0).get("SAP_OUT_ITEM_NO").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);
					variables.put("SAP_OUT_NO", SAP_OUT_NO);
					variables.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
					variables.put("IO_NO", IO_NO);
					variables.put("COST_CENTER", COST_CENTER);
					variables.put("WBS", WBS);
					variables.put("CUSTOMER", CUSTOMER);

					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("RECEIVE_WERKS", RECEIVE_WERKS);
					variables.put("LGORT", matListMap.get(0).get("LGORT").toString());
					variables.put("RECEIVE_LGORT", RECEIVE_LGORT);
					variables.put("BUKRS_NAME", matListMap.get(0).get("BUKRS_NAME").toString());
					if (matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") > 0) {
						variables.put("DIV",
								matListMap.get(0).get("BUKRS_NAME").toString().substring(
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf("(") + 1,
										matListMap.get(0).get("BUKRS_NAME").toString().indexOf(")")));
					} else {
						variables.put("DIV", matListMap.get(0).get("BUKRS_NAME").toString());
					}
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					variables.put("LIFNR", matListMap.get(0).get("LIFNR").toString() + " "
							+ matListMap.get(0).get("LIKTX").toString());
					variables.put("HEADER_TXT", "销售发货领料单");
					variables.put("RECEIVER", matListMap.get(0).get("RECEIVER")==null?"":matListMap.get(0).get("RECEIVER"));
					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryXsdd.html", listVars, response);

		}

		// STO一步联动bug2019
		if ("75".equals(BUSINESS_NAME)) {
			System.err.println("STO一步联动打印=================");
			// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
			String baseDir = configConstant.getLocation() + "/barcode/";
			if (LabelList != null && LabelList.length() > 0) {
				// 生成二维码
				step = 18;
				List<List<Map>> l = Lists.partition(matListMap, step);
				for (List<Map> matList : l) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("itemList", matList);
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();

						sb.append("{PKID:" + matListMap.get(0).get("REQUIREMENT_NO") + "}");

						// sb.append(",WERKS:"+m.get("WERKS").toString()+"}");

						picturePath = QRCodeUtil.encodeBarCode(sb.toString(),
								matListMap.get(0).get("REQUIREMENT_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String business_name = m.get("BUSINESS_NAME").toString();
					String RECEIVE_WERKS = (matListMap.get(0).get("RECEIVE_WERKS") == null) ? " "
							: matListMap.get(0).get("RECEIVE_WERKS").toString();

					String PURPOSE = (matListMap.get(0).get("PURPOSE") == null) ? " "
							: matListMap.get(0).get("PURPOSE").toString();

					variables.put("contextPath", logoPicturePath);
					// 宽度
					variables.put("PAGEWIDTH", PageWidth);


					variables.put("RETURN_NO", matListMap.get(0).get("REQUIREMENT_NO").toString());

					variables.put("WERKS", matListMap.get(0).get("WERKS").toString());
					variables.put("RECEIVE_WERKS", RECEIVE_WERKS);
					variables.put("CREATOR", matListMap.get(0).get("CREATOR").toString());
					variables.put("CREATE_DATE", matListMap.get(0).get("CREATE_DATE").toString());
					//调入工厂、调出工厂信息可配置表
					variables.put("COMPANY_CODE", matListMap.get(0).get("COMPANY_CODE").toString());
					variables.put("COMPANY_NAME", matListMap.get(0).get("COMPANY_NAME").toString());
					variables.put("COMPANY_ENGLISH", matListMap.get(0).get("COMPANY_ENGLISH").toString());
					variables.put("TEL", matListMap.get(0).get("TEL").toString());
					variables.put("FAX", matListMap.get(0).get("FAX").toString());
					variables.put("ZIP_CODE", matListMap.get(0).get("ZIP_CODE").toString());
					variables.put("ADRESS", matListMap.get(0).get("ADRESS").toString());
					variables.put("ADRESS_ENGLISH", matListMap.get(0).get("ADRESS_ENGLISH").toString());
                      //调入工厂、调出工厂信息可配置表
					variables.put("CONSIGNOR_TEL", matListMap.get(0).get("CONSIGNOR_TEL").toString());
					variables.put("CONSIGNOR", matListMap.get(0).get("CONSIGNOR").toString());
					variables.put("CONSIGNOR_ADRESS", matListMap.get(0).get("CONSIGNOR_ADRESS").toString());
					variables.put("CONSIGNEE_TEL", matListMap.get(0).get("CONSIGNEE_TEL").toString());
					variables.put("CONSIGNEE", matListMap.get(0).get("CONSIGNEE").toString());
					variables.put("CONSIGNEE_ADRESS", matListMap.get(0).get("CONSIGNEE_ADRESS").toString());
					listVars.add(variables);
				}
			}
			PdfUtils.preview(configurer, "wms/print/requirementQueryYBLD.html", listVars, response);

		}

		// PdfUtils.preview(configurer,"wms/print/labelTmp_Label.html",listVars,response);
	}

	/**
	 * 标签打印预览
	 *ct
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/labelLabelPreview")
	/* @RequiresPermissions("docPrint:labelLabelPreview") */
	public void labelPreview(@RequestParam Map<String, Object> params) {
		String labelListStr = params.get("labelList") == null ? null : params.get("labelList").toString();
		String LIKTX = params.get("LIKTX") == null ? "" : params.get("LIKTX").toString();
		String TEMP_SIZE = params.get("labelSize") == null ? "" : params.get("labelSize").toString();
		String TEMP_TYPE = params.get("labelType") == null ? "" : params.get("labelType").toString();

		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();
		String baseDir = configConstant.getLocation() + "/barcode/";

		String templateName= "wms/print/labelTmp_Label.html";
		//模板隐藏字段
		String hiddenFields = "";
		if (labelListStr != null && labelListStr.length() > 0) {
			List<Map> mapList = JSONObject.parseArray(labelListStr, Map.class);
			//查询模板配置信息
			Map paramMap = new HashMap();
			paramMap.put("WERKS",mapList.get(0).get("WERKS")== null ? "" : mapList.get(0).get("WERKS").toString());
			paramMap.put("KUNNR",mapList.get(0).get("KUNNR")== null ? "" : mapList.get(0).get("KUNNR").toString());
			paramMap.put("TEMP_SIZE",TEMP_SIZE);
			paramMap.put("TEMP_TYPE",TEMP_TYPE);
			List<Map<String,Object>> template = printTemplateRemote.getPrintTemplate(paramMap);
			//有且仅有一个模板符合条件时，取配置模板；否则用默认模板
			if(template != null && template.size() == 1 ){
				templateName = template.get(0).get("TEMP_NAME").toString();
				//隐藏字段
				hiddenFields = template.get(0).get("HIDDEN_FIELD") == null ? "" : template.get(0).get("HIDDEN_FIELD").toString();
			}
			for (Map m : mapList) {
				String PO_NO = m.get("PO_NO") == null ? "" : m.get("PO_NO").toString();
				String PO_ITEM_NO = m.get("PO_ITEM_NO") == null ? "" : m.get("PO_ITEM_NO").toString();
				String WERKS = m.get("WERKS") == null ? "" : m.get("WERKS").toString();
				String LIFNR = m.get("LIFNR") == null ? "" : m.get("LIFNR").toString();
				String MATNR = m.get("MATNR") == null ? "" : m.get("MATNR").toString();
				String UNIT = m.get("UNIT") == null ? "" : m.get("UNIT").toString();
				String BATCH = m.get("BATCH") == null ? "" : m.get("BATCH").toString();
				String LABEL_NO = m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO").toString();
				String PRODUCT_DATE = m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE").toString();
				String EFFECT_DATE = m.get("EFFECT_DATE") == null ? "" : m.get("EFFECT_DATE").toString();
				String BOX_QTY = m.get("BOX_QTY") == null ? "" : m.get("BOX_QTY").toString();
				String BOX_SN = m.get("BOX_SN") == null ? "" : m.get("BOX_SN").toString();

				Map<String, Object> variables = new HashMap<>();
				variables.put("LIKTX", m.get("LIKTX") == null ? LIKTX : m.get("LIKTX"));
				variables.put("LABEL_NO", LABEL_NO);
				variables.put("MATNR", MATNR);
				variables.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
				variables.put("LIFNR", LIFNR);
				variables.put("WERKS",WERKS);
				variables.put("PO_NO", PO_NO);
				variables.put("PO_ITEM_NO", PO_ITEM_NO);
				variables.put("BEDNR", m.get("BEDNR") == null ? "" : m.get("BEDNR"));
				variables.put("PRODUCT_DATE",PRODUCT_DATE);
				variables.put("EFFECT_DATE", EFFECT_DATE);
				variables.put("BATCH", BATCH);
				variables.put("BOX_QTY", BOX_QTY);
				variables.put("BOX_SN", BOX_SN);
				variables.put("UNIT", UNIT);
				//隐藏字段控制
				if(!hiddenFields.isEmpty()){
					String[] fields = hiddenFields.split(",");
					for (String field : fields) {
						if(!field.isEmpty()) variables.put(field, "");
					}
				}
				try {

					// 生成二维码
					if(template == null || template.size()== 0 || template.get(0).get("QRCODE").toString().equals("Y")){
						// 图片路径
						String picturePath = "";
						StringBuffer sb = new StringBuffer();

						String MAT = MATNR + "/" + UNIT;
						String PO = PO_NO + "/" + PO_ITEM_NO;
						String BOX = BOX_QTY + "/" + BOX_SN;

						sb.append("P:" + WERKS);
						sb.append(";V:" + LIFNR);
						sb.append(";M:" + MAT);
						sb.append(";B:" + BATCH);
						sb.append(";S:" + LABEL_NO);
						sb.append(";PO:" + PO);
						sb.append(";Q:" + BOX);
						sb.append(";D:" + PRODUCT_DATE);
						sb.append(";YX:" + EFFECT_DATE + ";");

						picturePath = QRCodeUtil.encode(sb.toString(), LABEL_NO, "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					}
					// 生成条形码
					if(template != null && template.size() != 0 && template.get(0).get("BARCODE").toString().equals("Y")) {
						if(LABEL_NO.isEmpty()) {
							variables.put("tCode", "");
						}else{
							String picturePath2 = QRCodeUtil.encodeBarCode(LABEL_NO, LABEL_NO + "-tCode" , "", baseDir, true);
							picturePath2 = picturePath2.replaceAll("\\\\", "//");
							variables.put("tCode", "file:" + picturePath2);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				listVars.add(variables);
			}
		}
		PdfUtils.preview(configurer,templateName,listVars,response);
	}

	/**
	 * 标签打印预览-A4
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/labelPreview_A4")
	/* @RequiresPermissions("docPrint:labelPreview_A4") */
	public void labelPreview_A4(@RequestParam Map<String, Object> params) {
		String labelListStr = params.get("labelListA4") == null ? null : params.get("labelListA4").toString();
		String LIKTX = params.get("LIKTXA4") == null ? "" : params.get("LIKTXA4").toString();
		String TEMP_SIZE = params.get("labelSizeA4") == null ? "" : params.get("labelSizeA4").toString();
		String TEMP_TYPE = params.get("labelTypeA4") == null ? "" : params.get("labelTypeA4").toString();
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();

		String baseDir = configConstant.getLocation() + "/barcode/";

        String templateName= "wms/print/labelTmp_Label_A4.html";
        //模板隐藏字段
        String hiddenFields = "";
		List<Map<String, Object>> labelList = new ArrayList<>();
		if (labelListStr != null && labelListStr.length() > 0) {
			List<Map> mapList = JSONObject.parseArray(labelListStr, Map.class);
            //查询模板配置信息
            Map paramMap = new HashMap();
            paramMap.put("TEMP_TYPE",TEMP_TYPE);
            paramMap.put("WERKS",mapList.get(0).get("WERKS")== null ? "" : mapList.get(0).get("WERKS").toString());
			paramMap.put("KUNNR",mapList.get(0).get("KUNNR")== null ? "" : mapList.get(0).get("KUNNR").toString());
			paramMap.put("TEMP_SIZE",TEMP_SIZE);
			paramMap.put("TEMP_TYPE",TEMP_TYPE);
			List<Map<String,Object>> template = printTemplateRemote.getPrintTemplate(paramMap);
            //有且仅有一个模板符合条件时，取配置模板；否则用默认模板
            if(template != null && template.size() == 1 ){
                templateName = template.get(0).get("TEMP_NAME").toString();
                //隐藏字段
                hiddenFields = template.get(0).get("HIDDEN_FIELD") == null ? "" : template.get(0).get("HIDDEN_FIELD").toString();
            }
			for (Map m : mapList) {
				Map<String, Object> label = new HashMap<>();

                String PO_NO = m.get("PO_NO") == null ? "" : m.get("PO_NO").toString();
                String PO_ITEM_NO = m.get("PO_ITEM_NO") == null ? "" : m.get("PO_ITEM_NO").toString();
                String WERKS = m.get("WERKS") == null ? "" : m.get("WERKS").toString();
                String LIFNR = m.get("LIFNR") == null ? "" : m.get("LIFNR").toString();
                String MATNR = m.get("MATNR") == null ? "" : m.get("MATNR").toString();
                String UNIT = m.get("UNIT") == null ? "" : m.get("UNIT").toString();
                String BATCH = m.get("BATCH") == null ? "" : m.get("BATCH").toString();
                String LABEL_NO = m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO").toString();
                String PRODUCT_DATE = m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE").toString();
                String EFFECT_DATE = m.get("EFFECT_DATE") == null ? "" : m.get("EFFECT_DATE").toString();
                String BOX_QTY = m.get("BOX_QTY") == null ? "" : m.get("BOX_QTY").toString();
                String BOX_SN = m.get("BOX_SN") == null ? "" : m.get("BOX_SN").toString();

                label.put("LIKTX", m.get("LIKTX") == null ? LIKTX : m.get("LIKTX"));
				label.put("LABEL_NO", LABEL_NO);
				label.put("MATNR", MATNR);
				label.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
				label.put("LIFNR", LIFNR);
				label.put("WERKS", WERKS);
				label.put("PO_NO", PO_NO);
				label.put("PO_ITEM_NO", PO_ITEM_NO);
				label.put("BEDNR", m.get("BEDNR") == null ? "" : m.get("BEDNR"));
				label.put("PRODUCT_DATE", PRODUCT_DATE);
				label.put("EFFECT_DATE", EFFECT_DATE);
				label.put("BATCH", BATCH);
				label.put("BOX_QTY", BOX_QTY);
                label.put("BOX_SN", BOX_SN);
				label.put("UNIT", UNIT);
                //隐藏字段控制
                if(!hiddenFields.isEmpty()){
                    String[] fields = hiddenFields.split(",");
                    for (String field : fields) {
                        if(!field.isEmpty()) label.put(field, "");
                    }
                }


				try {
                    // 生成二维码
                    if(template == null || template.size()== 0 || template.get(0).get("QRCODE").toString().equals("Y")){
                        String picturePath = ""; // 图片路径
                        StringBuffer sb = new StringBuffer();

                        String MAT = MATNR + "/" + UNIT;
                        String PO = PO_NO + "/" + PO_ITEM_NO;
                        String BOX = BOX_QTY + "/" + BOX_SN;

                        sb.append("P:" + WERKS);
                        sb.append(";V:" + LIFNR);
                        sb.append(";M:" + MAT);
                        sb.append(";B:" + BATCH);
                        sb.append(";S:" + LABEL_NO);
                        sb.append(";PO:" + PO);
                        sb.append(";Q:" + BOX);
                        sb.append(";D:" + PRODUCT_DATE);
                        sb.append(";YX:" + EFFECT_DATE + ";");

                        picturePath = QRCodeUtil.encode(sb.toString(), m.get("LABEL_NO").toString(), "", baseDir, true);
                        picturePath = picturePath.replaceAll("\\\\", "//");
                        label.put("barCode", "file:" + picturePath);
                    }
					// 生成条形码
					if(template != null && template.size() != 0 && template.get(0).get("BARCODE").toString().equals("Y")) {
						if(LABEL_NO.isEmpty()) {
							label.put("tCode", "");
						}else{
							String picturePath2 = QRCodeUtil.encodeBarCode(LABEL_NO, LABEL_NO+"-tCode", "", baseDir, true);
							picturePath2 = picturePath2.replaceAll("\\\\", "//");
							label.put("tCode", "file:" + picturePath2);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				labelList.add(label);
			}
			Map<String, Object> variables = new HashMap<>();
			variables.put("labelList", labelList);
			listVars.add(variables);
		}

		PdfUtils.preview(configurer, templateName, listVars, response);
	}

	/**
	 * 收货进仓单打印预览
	 *
	 */
	@RequestMapping(value = "/inBoundPreview")
	/* @RequiresPermissions("docPrint:inBoundPreview") */
	public void inBoundPreview(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.inBoundPreview(params);
		List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		if (params.get("subInBoundType") != null && "01".equals(params.get("subInBoundType"))) {// 外购
			PdfUtils.preview(configurer, "wms/print/inBoundTmp.html", listVars, response);
		} else if (params.get("subInBoundType") != null && "02".equals(params.get("subInBoundType"))) {// 自制
			PdfUtils.preview(configurer, "wms/print/inInternalBoundTmp.html", listVars, response);
		}else if (params.get("subInBoundType3") != null && "03".equals(params.get("subInBoundType3"))) {// 外购小letter
			PdfUtils.preview(configurer, "wms/print/inBoundSmallTmp.html", listVars, response);
		}
	}

	/**
	 * 自制进仓单标签打印预览
	 *
	 */
	@RequestMapping(value = "/inInternalBoundPreview")
	/* @RequiresPermissions("docPrint:inBoundPreview") */
	public void inInternalBoundPreview(@RequestParam Map<String, Object> params) {
		System.err.println("自制进仓单标签打印=================");
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();

		String labelListStr = params.get("labelList") == null
				? (params.get("labelListA4") == null ? null : params.get("labelListA4").toString())
				: params.get("labelList").toString();
		if (!StringUtils.isEmpty(labelListStr)) {
			// 查询-标签打印
			String baseDir = configConstant.getLocation() + "/barcode/";

			Map<String, Object> deptMap = new HashMap<String, Object>();
			deptMap.put("WERKS", params.get("WERKS"));
			List<Map<String, Object>> divlist = wmsInboundRemote.getDeptNameByWerk(deptMap);
			String divName = "";
			if (divlist != null && divlist.size() > 0) {
				divName = divlist.get(0).get("NAME").toString();
			}
			List<Map> mapList = JSONObject.parseArray(labelListStr, Map.class);
			for (Map m : mapList) {
				Map<String, Object> variables = new HashMap<>();
				variables.put("DIV", divName);
				variables.put("WERKS", m.get("WERKS"));
				variables.put("LABEL_NO", m.get("LABEL_NO"));
				variables.put("BATCH", m.get("BATCH"));
				variables.put("MATNR", m.get("MATNR"));
				variables.put("MAKTX", m.get("MAKTX"));
				variables.put("IN_QTY", m.get("BOX_QTY"));
				variables.put("UNIT", m.get("UNIT"));
				variables.put("BEDNR", m.get("BEDNR") == null ? "" : m.get("BEDNR"));
				variables.put("DRAWING_NO", m.get("DRAWING_NO") == null ? "" : m.get("DRAWING_NO"));
				variables.put("PRO_STATION", m.get("PRO_STATION") == null ? "" : m.get("PRO_STATION"));
				variables.put("CAR_TYPE", m.get("CAR_TYPE") == null ? "" : m.get("CAR_TYPE"));
				variables.put("WORKGROUP_NO", m.get("WORKGROUP_NO") == null ? "" : m.get("WORKGROUP_NO"));
				variables.put("MOULD_NO", m.get("MOULD_NO") == null ? "" : m.get("MOULD_NO"));
				variables.put("OPERATOR", m.get("OPERATOR") == null ? "" : m.get("OPERATOR"));
				variables.put("PRODUCT_DATE",
						(m.get("PRODUCT_DATE") == null || StringUtils.isEmpty(m.get("PRODUCT_DATE").toString())) ? ""
								: m.get("PRODUCT_DATE").toString().substring(0, 10));
				variables.put("SO_NO", m.get("SO_NO") == null ? "" : m.get("SO_NO"));
				variables.put("SO_ITEM_NO", m.get("SO_ITEM_NO") == null ? "" : m.get("SO_ITEM_NO"));
				String DDH = m.get("MO_NO") == null ? ""
						: m.get("MO_NO").toString().concat("/")
								.concat(m.get("MO_ITEM_NO") == null ? "" : m.get("MO_ITEM_NO").toString());
				if (StringUtils.isEmpty(DDH) || "/".equals(DDH)) {
					DDH = m.get("IO_NO") == null ? "" : m.get("IO_NO").toString();
				}
				if (StringUtils.isEmpty(DDH) || "/".equals(DDH)) {
					DDH = m.get("COST_CENTER") == null ? "" : m.get("COST_CENTER").toString();
				}
				if (StringUtils.isEmpty(DDH) || "/".equals(DDH)) {
					DDH = m.get("WBS") == null ? "" : m.get("WBS").toString();
				}
				if (StringUtils.isEmpty(DDH) || "/".equals(DDH)) {
					DDH = m.get("PO_NO_E") == null ? ""
							: m.get("PO_NO_E").toString().concat("/")
									.concat(m.get("PO_ITEM_NO_E") == null ? "" : m.get("PO_ITEM_NO_E").toString());
				}
				variables.put("DDH", DDH);

				// 生成二维码
				try {
					String picturePath = ""; // 图片路径
					StringBuffer sb = new StringBuffer();
					String WERKS = m.get("WERKS") == null ? "" : m.get("WERKS").toString();
					sb.append("P:" + WERKS);
					String MATNR = m.get("MATNR") == null ? "" : m.get("MATNR").toString();
					MATNR += "/";
					MATNR += m.get("UNIT") == null ? "" : m.get("UNIT").toString();
					sb.append(";M:" + MATNR);
					String BATCH = m.get("BATCH") == null ? "" : m.get("BATCH").toString();
					sb.append(";B:" + BATCH);
					sb.append(";PO:" + DDH);
					String IN_QTY = m.get("IN_QTY") == null ? "" : m.get("IN_QTY").toString();
					IN_QTY += "/";
					IN_QTY += m.get("BOX_SN") == null ? "" : m.get("BOX_SN").toString();
					sb.append(";Q:" + IN_QTY);
					String MOULD_NO = m.get("MOULD_NO") == null ? "" : m.get("MOULD_NO").toString();
					sb.append(";MO:" + MOULD_NO);
					String OPERATOR = m.get("OPERATOR") == null ? "" : m.get("OPERATOR").toString();
					sb.append(";W:" + OPERATOR);
					String DRAWING_NO = m.get("DRAWING_NO") == null ? "" : m.get("DRAWING_NO").toString();
					sb.append(";PR:" + DRAWING_NO);
					String PRO_STATION = m.get("PRO_STATION") == null ? "" : m.get("PRO_STATION").toString();
					sb.append(";WP:" + PRO_STATION);
					String CAR_TYPE = m.get("CAR_TYPE") == null ? "" : m.get("CAR_TYPE").toString();
					sb.append(";C:" + CAR_TYPE);
					String WORKGROUP_NO = m.get("WORKGROUP_NO") == null ? "" : m.get("WORKGROUP_NO").toString();
					sb.append(";T:" + WORKGROUP_NO);
					String LABEL_NO = m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO").toString();
					sb.append(";PN:" + LABEL_NO);
					String PRODUCT_DATE = m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE").toString();
					sb.append(";D:" + PRODUCT_DATE + ";");

					picturePath = QRCodeUtil.encode(sb.toString(), m.get("LABEL_NO").toString(), "", baseDir, true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					variables.put("barCode", "file:" + picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				variables.put("contextPath", params.get("contextPath").toString());
				listVars.add(variables);
			}
			;
		} else {
			// 进仓打印
			Map<String, Object> rtnMap = documentPrintRemote.inInternalBoundPreview(params);
			listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		}

		PdfUtils.preview(configurer, "wms/print/internalInboundTmp_Label.html", listVars, response);
	}

	/**
	 * 自制进仓单列表打印预览
	 *
	 */
	@RequestMapping(value = "/inInternalBoundListPreview")
	public void inInternalBoundListPreview(@RequestParam Map<String, Object> params) {
		System.err.println("进仓单打印=================");
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.inInternalBoundListPreview(params);
		List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		if (params.get("inboundNo1") != null) {// 自制大letter打印
			System.err.println("自制进仓单大letter打印=================");
			PdfUtils.preview(configurer, "wms/print/inInternalBoundTmp.html", listVars, response);
		} else if (params.get("inboundNo2") != null && "02".equals(params.get("subInBoundType1"))) {// 自制小letter打印
			System.err.println("自制进仓单小letter打印=================");
			PdfUtils.preview(configurer, "wms/print/inInternalSmallBoundTmp.html", listVars, response);
		} else if (params.get("inboundNo2") != null && "01".equals(params.get("subInBoundType1"))) {// 外购小letter打印
			System.err.println("外购小letter打印=================");
			PdfUtils.preview(configurer, "wms/print/inBoundSmallTmp.html", listVars, response);
		}
	}

	/**
	 * 送检单打印预览
	 *
	 * @param params
	 */
	@RequestMapping(value = "/inspectionLabelPreview")
	public void inspectionLabelPreview(@RequestParam Map<String, Object> params) {
		String arr_str = params.get("inspectionList") == null ? "" : params.get("inspectionList").toString();
		String INSPECTION_NO = "";
		String LIFNR = "";
		String LIKTX = "";
		String INSPECTION_TYPE = "来料质检";
		String WERKS = "";
		String CREATE_DATE = "";
		String CREATOR = "";
		String baseDir = configConstant.getLocation() + "/barcode/";

		Map<String, Object> variables = new HashMap<>();// 模板数据封装

		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf文件数
		List<Map<String, Object>> listVars = new ArrayList<>();
		List<Map> dataList = null;
		if (arr_str.length() > 0) {
			List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();

			try {
				dataList = JSONObject.parseArray(arr_str, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (dataList.size() > 0) {
				INSPECTION_NO = dataList.get(0).get("INSPECTION_NO").toString();
				LIFNR = dataList.get(0).get("LIFNR") == null ? "" : dataList.get(0).get("LIFNR").toString();
				LIKTX = dataList.get(0).get("LIKTX") == null ? "" : dataList.get(0).get("LIKTX").toString();
				INSPECTION_TYPE = dataList.get(0).get("INSPECTION_TYPE") == null ? "来料质检"
						: ("01".equals(dataList.get(0).get("INSPECTION_TYPE")) ? "来料质检" : "库存复检");
				WERKS = dataList.get(0).get("WERKS") == null ? "" : dataList.get(0).get("WERKS").toString();
				CREATE_DATE = dataList.get(0).get("CREATE_DATE") == null ? ""
						: dataList.get(0).get("CREATE_DATE").toString();
				CREATOR = dataList.get(0).get("CREATOR") == null ? "" : dataList.get(0).get("CREATOR").toString();

				variables.put("INSPECTION_NO", INSPECTION_NO);
				variables.put("LIFNR", LIFNR);
				variables.put("LIKTX", LIKTX);
				variables.put("INSPECTION_TYPE", INSPECTION_TYPE);
				variables.put("WERKS", WERKS);
				variables.put("CREATE_DATE", CREATE_DATE);
				variables.put("CREATOR", CREATOR);
			}

			for (int i = 0; i < dataList.size(); i++) {
				Map<String, Object> m = (Map<String, Object>) dataList.get(i);
				m.put("INDEX", i + 1);
				itemList.add(m);
			}
			// 生成二维码
			try {
				String picturePath = ""; // 图片路径
				StringBuffer sb = new StringBuffer();
				sb.append("{INSPECTION_NO:" + INSPECTION_NO);
				sb.append(",WERKS:" + WERKS + "}");

				picturePath = QRCodeUtil.encode(sb.toString(), INSPECTION_NO, "", baseDir, true);
				picturePath = picturePath.replaceAll("\\\\", "//");
				variables.put("barCode", "file:" + picturePath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			variables.put("itemList", itemList);
			String basePath = "http://" + request.getServerName();
			int port = request.getServerPort();
			String contextPath = request.getContextPath();
			variables.put("contextPath", basePath + ":" + port + contextPath);

			listVars.add(variables);
		}

		PdfUtils.preview(configurer, "wms/print/qcInspectionTmp.html", listVars, response);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/wareHouseOutPickupPrint")
	public void wareHouseOutPickupPrint(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.wareHouseOutPickupPrint(params);
		List<Map<String, Object>> listVars = new ArrayList<Map<String, Object>>();
		listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		PdfUtils.preview(configurer, "wms/print/wareHouseOutPickupPrintTmp.html", listVars, response);
	}

	/**
	 * 收料房退货打印标签
	 *
	 * @param params PageSize:0(默认)：A4； 1：A4高度减半
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/receiveRoomOutPrint")
	public void receiveRoomOutPrint(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.receiveRoomOutPrint(params);
		List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		String business_name = rtnMap.get("business_name").toString();
		if ("25".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/receiveRoomOutTmp.html", listVars, response);
		} else if ("27".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/receiveRoomOutTmp2.html", listVars, response);
		} else if ("26".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/receiveRoomOutTmp3.html", listVars, response);
		} else {
			PdfUtils.preview(configurer, "wms/print/receiveRoomOutTmp.html", listVars, response);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/wareHouseOutPrint")
	public void wareHouseOutPrint(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.wareHouseOutPrint(params);
		List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		String business_name = rtnMap.get("business_name").toString();
		if ("28".equals(business_name) || "29".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp.html", listVars, response);
		} else if ("26".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp2.html", listVars, response);
		} else if ("27".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp2.html", listVars, response);
		} else if ("30".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp3.html", listVars, response);
		} else if ("34".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp4.html", listVars, response);
		} else if ("33".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp5.html", listVars, response);
		} else if ("31".equals(business_name)) {
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp6.html", listVars, response);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/workshopReturnOutPrint")
	public void workshopReturnOutPrint(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.receiveRoomOutPrint(params);
		List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		String business_name = rtnMap.get("business_name").toString();
		if ("38".equals(business_name)) { // 38 成本中心退料(202)
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_38.html", listVars, response);
		} else if ("39".equals(business_name)) { // 39 WBS元素退料(222)
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_39.html", listVars, response);
		} else if ("37".equals(business_name)) { // 37 研发/内部/CO订单退料(262)
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_37.html", listVars, response);
		} else if ("69".equals(business_name)) { // 69 委外加工退料(542)
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_69.html", listVars, response);
		} else if ("35".equals(business_name) || "36".equals(business_name)) { // 生产订单
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_35.html", listVars, response);
		} else if ("40".equals(business_name)) { // 40 线边仓退料(312)
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_40.html", listVars, response);
		} else if ("71".equals(business_name)) { // 71 销售订单退货(657)
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp_71.html", listVars, response);
		} else if ("70".equals(business_name)) { // 70 STO销售退货
			PdfUtils.preview(configurer, "wms/print/wareHouseOutTmp2.html", listVars, response);
		} else {
			PdfUtils.preview(configurer, "wms/print/workshopReturnOutTmp.html", listVars, response);
		}
	}

	@RequestMapping(value = "/wmsDocPrint")
	public void wmsDocPrint(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		List<Map<String, Object>> listVars = new ArrayList<Map<String, Object>>();
		Map<String, Object> testMap = new HashMap<String, Object>();
		testMap.put("username", "admin");
		testMap.put("contextPath", contextPath);
		listVars.add(testMap);
		PdfUtils.preview(configurer, "wms/print/wmsDocPrintTmp.html", listVars, response);
	}

	/**
	 * jis需求拣配单据打印
	 *
	 */
	@RequestMapping(value = "/dispatchingJISPrintPreview")
	public void dispatchingJISPrintPreview(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();

		String dispatchingNo_str = params.get("DISPATCHING_NO").toString();
		String buprint_str = params.get("BUPRINT").toString();
		String[] dispatching_Array = dispatchingNo_str.split(",");
		for (int i = 0; i < dispatching_Array.length; i++) {
			Map<String, Object> params_tmp = new HashMap<String, Object>();
			params_tmp.put("DISPATCHING_NO", dispatching_Array[i]);
			params_tmp.put("contextPath", contextPath);
			params_tmp.put("BUPRINT", buprint_str);
			Map<String, Object> rtnMap = documentPrintRemote.dispatchingJISPrintPreview(params_tmp);

			List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
			PdfUtils.preview(configurer, "wms/print/dispatchingJISTmp.html", listVars, response);
		}

	}

	/**
	 * 非jis需求拣配标签打印
	 *
	 */
	@RequestMapping(value = "/dispatchingFeiJISPrintPreview")
	public void dispatchingFeiJISPrintPreview(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		Map<String, Object> rtnMap = documentPrintRemote.dispatchingFeiJISPrintPreview(params);
		List<Map<String, Object>> listVars = (List<Map<String, Object>>) rtnMap.get("listVars");
		PdfUtils.preview(configurer, "wms/print/dispatchingFeiJISTmp_Label.html", listVars, response);
	}

	/**
	 * sto送货单单打印预览
	 *
	 * @param params
	 */
	@RequestMapping(value = "/stoLabelPreview")
	public void stoLabelPreview(@RequestParam Map<String, Object> params) {
		String param = params.get("stoList").toString();
		Map<String, Object> map = JSONObject.parseObject(param, Map.class);
		String itemList = (String) map.get("itemList");
		String stoNo = "";
		String sapNo = "";
		String wmsNo = "";
		String lifnr = "";
		String toWerks = "";
		String address = "";
		String tel = "";
		String contacts = "";
		String baseDir = configConstant.getLocation() + "/barcode/";

		Map<String, Object> variables = new HashMap<>();// 模板数据封装

		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf文件数
		List<Map<String, Object>> listVars = new ArrayList<>();
		List<Map> dataList = null;
		if (itemList.length() > 0) {

			try {
				dataList = JSONObject.parseArray(itemList, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (dataList.size() > 0) {
				stoNo = map.get("stoNo").toString();
				sapNo = dataList.get(0).get("SAP_OUT_NO") == null ? "" : dataList.get(0).get("SAP_OUT_NO").toString();
				wmsNo = dataList.get(0).get("WMS_NO") == null ? "" : dataList.get(0).get("WMS_NO").toString();
				lifnr = dataList.get(0).get("PROVIDER_CODE") == null ? ""
						: dataList.get(0).get("PROVIDER_CODE").toString();
				String bookingDate = dataList.get(0).get("BOOKING_DATE") == null ? ""
						: dataList.get(0).get("BOOKING_DATE").toString();
				toWerks = dataList.get(0).get("FACT_NO") == null ? "" : dataList.get(0).get("FACT_NO").toString();
				address = dataList.get(0).get("ADDRESS") == null ? "" : dataList.get(0).get("ADDRESS").toString();
				tel = dataList.get(0).get("TEL") == null ? "" : dataList.get(0).get("TEL").toString();
				contacts = dataList.get(0).get("CONTACTS") == null ? "" : dataList.get(0).get("CONTACTS").toString();

				variables.put("stoNo", stoNo);
				variables.put("sapNo", sapNo);
				variables.put("wmsNo", wmsNo);
				variables.put("lifnr", lifnr);
				variables.put("bookingDate", bookingDate);
				variables.put("toWerks", toWerks);
				variables.put("address", address);
				variables.put("tel", tel);
				variables.put("contacts", contacts);
			}

			// 生成二维码
			try {
				String picturePath = ""; // 图片路径
				StringBuffer sb = new StringBuffer();
				sb.append("{stoNo:" + stoNo);
				sb.append("}");

				picturePath = QRCodeUtil.encode(sb.toString(), stoNo, "", baseDir, true);
				picturePath = picturePath.replaceAll("\\\\", "//");
				variables.put("barCode", "file:" + picturePath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			variables.put("itemList", dataList);
			String basePath = "http://" + request.getServerName();
			int port = request.getServerPort();
			String contextPath = request.getContextPath();
			variables.put("contextPath", basePath + ":" + port + contextPath);
			listVars.add(variables);
		}

		PdfUtils.preview(configurer, "wms/print/stoList.html", listVars, response);
	}

	/**
	 * sz总装配送标签打印预览-A4
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/shipLabelPreview")
	public void shipLabelPreview(@RequestParam Map<String, Object> params) {
		String tmptype = "A4";
		String labelListStr = params.get("labelListA4") == null ? null : params.get("labelListA4").toString();
		if (labelListStr == null) {
			tmptype = "barcode";
			labelListStr = params.get("labelList") == null ? null : params.get("labelList").toString();
		}

		String LIKTX = params.get("LIKTXA4") == null ? "" : params.get("LIKTXA4").toString();
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();

		String baseDir = configConstant.getLocation() + "/barcode/";
		List<Map<String, Object>> labelList = new ArrayList<>();
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String logoPicturePath = basePath + ":" + port + request.getContextPath();

		if (labelListStr != null && labelListStr.length() > 0) {
			JSONObject.parseArray(labelListStr, Map.class).forEach(m -> {
				m = (Map<String, Object>) m;

				Map<String, Object> label = new HashMap<>();
				label.put("LIKTX", m.get("LIKTX") == null ? LIKTX : m.get("LIKTX"));
				label.put("LABEL_NO", m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO"));
				label.put("MATNR", m.get("MATNR") == null ? "" : m.get("MATNR"));
				label.put("MAKTX", m.get("ZMAKTX") == null ? "" : m.get("ZMAKTX"));
				label.put("LIFNR", m.get("WERKS") == null ? "" : m.get("WERKS"));
				label.put("WERKS", m.get("WERKS") == null ? "" : m.get("WERKS"));
				label.put("REFERENCE_NO", m.get("REFERENCE_NO") == null ? "" : m.get("REFERENCE_NO")); // 工单号
				label.put("DOSAGE", m.get("DOSAGE") == null ? "" : m.get("DOSAGE"));
				label.put("STATION", m.get("STATION") == null ? "" : m.get("STATION"));
				label.put("BATCH", m.get("BATCH") == null ? "" : m.get("BATCH"));
				label.put("BOX_QTY", m.get("BOX_QTY") == null ? "" : m.get("BOX_QTY"));
				label.put("UNIT", m.get("UNIT") == null ? "" : m.get("UNIT"));
				label.put("ON_LINE_TYPE", m.get("ON_LINE_TYPE") == null ? "" : m.get("ON_LINE_TYPE"));
				label.put("ON_LINE_MOUTH", m.get("ON_LINE_MOUTH") == null ? "" : m.get("ON_LINE_MOUTH"));
				label.put("CAR_TYPE", m.get("CAR_TYPE") == null ? "" : m.get("CAR_TYPE"));
				label.put("CONFIGURATION", m.get("CONFIGURATION") == null ? "" : m.get("CONFIGURATION"));
				label.put("BIN_CODE", m.get("BIN_CODE") == null ? "" : m.get("BIN_CODE"));
				label.put("PRODUCT_CODE", m.get("PRODUCT_CODE") == null ? "" : m.get("PRODUCT_CODE"));

				// 生成二维码
				try {
					String picturePath = ""; // 图片路径
					StringBuffer sb = new StringBuffer();
					sb.append("{PN:" + m.get("LABEL_NO").toString());
					sb.append(";M:" + m.get("MATNR").toString());
					sb.append(";B:" + m.get("BATCH").toString() + ";Q:");
					sb.append(m.get("BOX_QTY") == null ? "0" : m.get("BOX_QTY").toString());
					sb.append(";PO:" + m.get("REFERENCE_NO").toString());
					sb.append(";P:" + m.get("WERKS").toString() + "}");

					picturePath = QRCodeUtil.encode(sb.toString(), m.get("LABEL_NO").toString(), "", baseDir, true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					label.put("barCode", "file:" + picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				label.put("contextPath", logoPicturePath);
				labelList.add(label);
			});
			Map<String, Object> variables = new HashMap<>();
			variables.put("labelList", labelList);
			listVars.add(variables);
		}

		if (tmptype.equals("A4")) {
			PdfUtils.preview(configurer, "wms/print/shipping_Label_A4.html", listVars, response);
		} else {
			PdfUtils.preview(configurer, "wms/print/shipping_Label.html", labelList, response);
		}

	}

	@RequestMapping(value = "/transferlabel")
	public void transferlabel(@RequestParam Map<String, Object> params) {
		params.put("labelListA4", params.get("transferlabel"));
		this.shipLabelPreview(params);
	}

	@RequestMapping(value = "/storageLabelPreview")
	public void storageLabelPreview(@RequestParam Map<String, Object> params) {
		String labelListStr = params.get("labelList") == null ? null : params.get("labelList").toString();
		List<Map<String, Object>> listVars = new ArrayList<>();
		Map map = (Map<String, Object>) JSONObject.parse(labelListStr);
		String baseDir = configConstant.getLocation() + "/barcode/";
		if (map.get("list").toString() != null && map.get("list").toString().length() > 0) {
			JSONObject.parseArray(map.get("list").toString(), Map.class).forEach(m -> {
				m = (Map<String, Object>) m;
				Map<String, Object> variables = new HashMap<>();
				variables.put("WH_NUMBER", m.get("WH_NUMBER") == null ? "" : m.get("WH_NUMBER"));
				variables.put("BIN_CODE", m.get("BIN_CODE") == null ? "" : m.get("BIN_CODE"));
				variables.put("BIN_NAME", m.get("BIN_NAME") == null ? "" : m.get("BIN_NAME"));

				// 生成二维码
				try {
					String picturePath = ""; // 图片路径
					StringBuffer sb = new StringBuffer();
					// sb.append("{WH_NUMBER:"+m.get("WH_NUMBER").toString());
					sb.append(m.get("BIN_CODE").toString());
					// sb.append(",BIN_NAME:"+m.get("BIN_NAME").toString()+"}");
					// System.err.println(sb.toString());
					picturePath = QRCodeUtil.encodeBarCode(sb.toString(),
							m.get("WH_NUMBER").toString() + m.get("BIN_CODE").toString(), "", baseDir, true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					variables.put("barCode", "file:" + picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				listVars.add(variables);
			});
		}
		if ("true".equals(map.get("flag").toString()))
			PdfUtils.preview(configurer, "wms/print/wms_c_barcode_storage.html", listVars, response);
		if ("false".equals(map.get("flag").toString()))
			PdfUtils.preview(configurer, "wms/print/wms_c_barcode_storages.html", listVars, response);

	}

	/**
	 * 关键零部件标签
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/keyPartsLabel")
	/* @RequiresPermissions("docPrint:labelPreview_A4") */
	public void keyPartsLabel(@RequestParam Map<String, Object> params) {
		String keyPartsListStr = params.get("keyPartsList") == null ? null : params.get("keyPartsList").toString();
		String LIKTX = params.get("LIKTX") == null ? "" : params.get("LIKTX").toString();
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();

		String baseDir = configConstant.getLocation() + "/barcode/";
		if (keyPartsListStr != null && keyPartsListStr.length() > 0) {
			JSONObject.parseArray(keyPartsListStr, Map.class).forEach(m -> {
				m = (Map<String, Object>) m;
				int QUANTITY = Integer.parseInt(m.get("QUANTITY").toString());
				for (int i = 0; i < QUANTITY; i++) {
					Map<String, Object> variables = new HashMap<>();
					variables.put("LIKTX", m.get("LIKTX") == null ? LIKTX : m.get("LIKTX"));
					variables.put("MATNR", m.get("MATNR") == null ? "" : m.get("MATNR"));
					variables.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
					variables.put("LIFNR", m.get("LIFNR") == null ? "" : m.get("LIFNR"));
					variables.put("WERKS", m.get("WERKS") == null ? "" : m.get("WERKS"));
					variables.put("BATCH", m.get("BATCH") == null ? "" : m.get("BATCH"));
					// variables.put("QUANTITY",m.get("QUANTITY")==null?"":m.get("QUANTITY"));

					// 生成二维码
					try {
						String picturePath = ""; // 图片路径
						StringBuffer sb = new StringBuffer();
						/*
						 * sb.append("{LABEL_NO:"+m.get("LABEL_NO").toString());
						 * sb.append(",MATNR:"+m.get("MATNR").toString());
						 * sb.append(",BATCH:"+m.get("BATCH").toString()+",BOX_QTY:"); //
						 * sb.append(m.get("QUANTITY")==null?"0":m.get("QUANTITY").toString());
						 * sb.append(",LIFNR:"+m.get("LIFNR").toString()+"}");
						 */

						sb.append("P:" + m.get("WERKS") == null ? "" : m.get("WERKS"));
						sb.append(";V:" + m.get("LIFNR") == null ? "" : m.get("LIFNR"));
						sb.append(";M:" + m.get("MATNR") == null ? ""
								: m.get("MATNR") + "/" + m.get("UNIT") == null ? "" : m.get("UNIT").toString());
						sb.append(";B:" + m.get("BATCH") == null ? "" : m.get("BATCH"));
						sb.append(";S:" + m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO"));
						sb.append(";PO:" + m.get("PO_NO") == null ? ""
								: m.get("PO_NO") + "/" + m.get("PO_ITEM_NO") == null ? "" : m.get("PO_ITEM_NO").toString());
						sb.append(";Q:" + m.get("BOX_QTY") == null ? ""
								: m.get("BOX_QTY") + "/" + m.get("BOX_SN") == null ? "" : m.get("BOX_SN").toString());
						sb.append(";D:" + m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE"));
						sb.append(";YX:" + m.get("EFFECT_DATE") == null ? "" : m.get("EFFECT_DATE"));

						picturePath = QRCodeUtil.encode(sb.toString(), m.get("LABEL_NO").toString(), "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						variables.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}

					listVars.add(variables);
				}
			});
		}

		PdfUtils.preview(configurer, "wms/print/keyPartsLabel.html", listVars, response);
	}

	/**
	 * 配送标签打印预览
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/shipLabelPreviewcs")
	public void shipLabelPreviewcs(@RequestParam Map<String, Object> params) {
		String tmptype = "A4";
		String labelListStr = params.get("labelListA4") == null ? null : params.get("labelListA4").toString();
		if (labelListStr == null) {
			tmptype = "barcode";
			labelListStr = params.get("labelList") == null ? null : params.get("labelList").toString();
		}

		String LIKTX = params.get("LIKTXA4") == null ? "" : params.get("LIKTXA4").toString();
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();

		String baseDir = configConstant.getLocation() + "/barcode/";
		List<Map<String, Object>> labelList = new ArrayList<>();
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String logoPicturePath = basePath + ":" + port + request.getContextPath();

		if (labelListStr != null && labelListStr.length() > 0) {
			JSONObject.parseArray(labelListStr, Map.class).forEach(m -> {
				m = (Map<String, Object>) m;

				Map<String, Object> label = new HashMap<>();
				label.put("LIKTX", m.get("LIKTX") == null ? LIKTX : m.get("LIKTX"));
				label.put("REQUIREMENT_NO", m.get("REQUIREMENT_NO") == null ? "" : m.get("REQUIREMENT_NO"));
				label.put("MATNR", m.get("MATNR") == null ? "" : m.get("MATNR"));
				label.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
				label.put("LIFNR", m.get("LIFNR") == null ? "" : m.get("LIFNR"));
				label.put("WH_NUMBER", m.get("WH_NUMBER") == null ? "" : m.get("WH_NUMBER"));
				label.put("WERKS", m.get("WERKS") == null ? "" : m.get("WERKS"));
				label.put("QTY", m.get("QTY") == null ? "" : m.get("QTY"));
				label.put("QTY_XJ", m.get("QTY_XJ") == null ? "" : m.get("QTY_XJ"));
				label.put("STATION", m.get("STATION") == null ? "" : m.get("STATION"));
				label.put("BATCH", m.get("BATCH") == null ? "" : m.get("BATCH"));
				label.put("UNIT", m.get("UNIT") == null ? "" : m.get("UNIT"));
				label.put("VALUE", m.get("VALUE") == null ? "" : m.get("VALUE"));
				label.put("REQUIRED_DATE", m.get("REQUIRED_DATE") == null ? "" : m.get("REQUIRED_DATE"));
				label.put("FULL_NAME", m.get("FULL_NAME") == null ? "" : m.get("FULL_NAME"));
				label.put("BIN_CODE", m.get("BIN_CODE") == null ? "" : m.get("BIN_CODE"));
				label.put("BIN_NAME", m.get("BIN_NAME") == null ? "" : m.get("BIN_NAME"));

				// 生成二维码
				try {
					String picturePath = ""; // 图片路径
					String barcode = m.get("REQUIREMENT_NO").toString();
					picturePath = QRCodeUtil.encode(barcode, barcode, "", baseDir, true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					label.put("barCode", "file:" + picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				label.put("contextPath", logoPicturePath);
				labelList.add(label);
			});
			Map<String, Object> variables = new HashMap<>();
			variables.put("labelList", labelList);
			listVars.add(variables);
		}

		if (tmptype.equals("A4")) {
			PdfUtils.preview(configurer, "wms/print/shipping_Label_CS_A4.html", listVars, response);
		} else {
			PdfUtils.preview(configurer, "wms/print/shipping_Label_CS.html", labelList, response);
		}

	}

	/**
	 * 关键零部件标签(需求拣配用)
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/keyPartsLabelPick")
	public void keyPartsLabelPick(@RequestParam Map<String, Object> params) {
		String tmptype = "A4";
		String labelListStr = params.get("labelListA4") == null ? null : params.get("labelListA4").toString();
		if (labelListStr == null) {
			tmptype = "barcode";
			labelListStr = params.get("labelList") == null ? null : params.get("labelList").toString();
		}

		String LIKTX = params.get("LIKTXA4") == null ? "" : params.get("LIKTXA4").toString();
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();

		String baseDir = configConstant.getLocation() + "/barcode/";
		List<Map<String, Object>> labelList = new ArrayList<>();
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String logoPicturePath = basePath + ":" + port + request.getContextPath();

		if (labelListStr != null && labelListStr.length() > 0) {
			JSONObject.parseArray(labelListStr, Map.class).forEach(m -> {
				m = (Map<String, Object>) m;
				int qty_xj = Integer.parseInt(m.get("XJ").toString());
				for (int i = 0; i < qty_xj; i++) {
					Map<String, Object> label = new HashMap<>();
					label.put("LIKTX", m.get("LIKTX") == null ? LIKTX : m.get("LIKTX"));
					label.put("MATNR", m.get("MATNR") == null ? "" : m.get("MATNR"));
					label.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
					label.put("LIFNR", m.get("LIFNR") == null ? "" : m.get("LIFNR"));
					label.put("WERKS", m.get("WERKS") == null ? "" : m.get("WERKS"));
					label.put("BATCH", m.get("BATCH") == null ? "" : m.get("BATCH"));

					// 生成二维码
					try {
						String picturePath = ""; // 图片路径
						String barcode = m.get("MATNR").toString();
						StringBuffer sb = new StringBuffer();
						sb.append("{P:" + m.get("WERKS"));
						sb.append(";V:" + m.get("LIFNR"));
						sb.append(";M:" + m.get("MATNR") + "/" + m.get("UNIT"));
						sb.append(";B:" + m.get("BATCH") + "}");
						picturePath = QRCodeUtil.encode(sb.toString(), barcode, "", baseDir, true);
						picturePath = picturePath.replaceAll("\\\\", "//");
						label.put("barCode", "file:" + picturePath);
					} catch (Exception e) {
						e.printStackTrace();
					}

					label.put("contextPath", logoPicturePath);
					labelList.add(label);
				}
			});
			Map<String, Object> variables = new HashMap<>();
			variables.put("labelList", labelList);
			listVars.add(variables);
		}

		if (tmptype.equals("A4")) {
			PdfUtils.preview(configurer, "wms/print/keyPartsLabel_A4.html", listVars, response);
		} else {
			PdfUtils.preview(configurer, "wms/print/keyPartsLabel.html", labelList, response);
		}

	}

    /**
     * 通过LABEL_NO标签打印预览
     *
     */
    @RequestMapping(value = "/labelLabelPreviewByLabel")
    /* @RequiresPermissions("docPrint:labelLabelPreview") */
    public void labelPreviewByLabel(@RequestParam Map<String, Object> params) {
        String labelNOList = params.get("labelNo") == null ? null : params.get("labelNo").toString();
		String TEMP_SIZE = params.get("labelSize") == null ? "" : params.get("labelSize").toString();
		String TEMP_TYPE = params.get("labelType") == null ? "" : params.get("labelType").toString();
        // 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String, Object>> listVars = new ArrayList<>();
        String baseDir = configConstant.getLocation() + "/barcode/";

        String templateName= "wms/print/labelTmp_Label.html";
        //模板隐藏字段
        String hiddenFields = "";
        if (labelNOList != null && labelNOList.length() > 0) {
            Map<String, Object> labelParamMap = new HashMap<>();
            labelParamMap.put("LABEL_NOS",labelNOList);
            List<Map<String, Object>> mapList = wmsKnLabelRecordRemote.getLabelList(labelParamMap);
            //查询模板配置信息
            Map paramMap = new HashMap();
			paramMap.put("TEMP_TYPE",TEMP_TYPE);
			paramMap.put("WERKS",mapList.get(0).get("WERKS")== null ? "" : mapList.get(0).get("WERKS").toString());
			paramMap.put("KUNNR",mapList.get(0).get("KUNNR")== null ? "" : mapList.get(0).get("KUNNR").toString());
			paramMap.put("TEMP_SIZE",TEMP_SIZE);
			paramMap.put("TEMP_TYPE",TEMP_TYPE);
			List<Map<String,Object>> template = printTemplateRemote.getPrintTemplate(paramMap);
            //有且仅有一个模板符合条件时，取配置模板；否则用默认模板
            if(template != null && template.size() == 1 ){
                templateName = template.get(0).get("TEMP_NAME").toString();
                //隐藏字段
                hiddenFields = template.get(0).get("HIDDEN_FIELD") == null ? "" : template.get(0).get("HIDDEN_FIELD").toString();
            }
            for (Map m : mapList) {
                String PO_NO = m.get("PO_NO") == null ? "" : m.get("PO_NO").toString();
                String PO_ITEM_NO = m.get("PO_ITEM_NO") == null ? "" : m.get("PO_ITEM_NO").toString();
                String WERKS = m.get("WERKS") == null ? "" : m.get("WERKS").toString();
                String LIFNR = m.get("LIFNR") == null ? "" : m.get("LIFNR").toString();
                String MATNR = m.get("MATNR") == null ? "" : m.get("MATNR").toString();
                String UNIT = m.get("UNIT") == null ? "" : m.get("UNIT").toString();
                String BATCH = m.get("BATCH") == null ? "" : m.get("BATCH").toString();
                String LABEL_NO = m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO").toString();
                String PRODUCT_DATE = m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE").toString();
                String EFFECT_DATE = m.get("EFFECT_DATE") == null ? "" : m.get("EFFECT_DATE").toString();
                String BOX_QTY = m.get("BOX_QTY") == null ? "" : m.get("BOX_QTY").toString();
                String BOX_SN = m.get("BOX_SN") == null ? "" : m.get("BOX_SN").toString();

                Map<String, Object> variables = new HashMap<>();
                variables.put("LIKTX", m.get("LIKTX") == null ? "" : m.get("LIKTX"));
                variables.put("LABEL_NO", LABEL_NO);
                variables.put("MATNR", MATNR);
                variables.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
                variables.put("LIFNR", LIFNR);
                variables.put("WERKS",WERKS);
                variables.put("PO_NO", PO_NO);
                variables.put("PO_ITEM_NO", PO_ITEM_NO);
                variables.put("BEDNR", m.get("BEDNR") == null ? "" : m.get("BEDNR"));
                variables.put("PRODUCT_DATE",PRODUCT_DATE);
                variables.put("EFFECT_DATE", EFFECT_DATE);
                variables.put("BATCH", BATCH);
                variables.put("BOX_QTY", BOX_QTY);
                variables.put("BOX_SN", BOX_SN);
                variables.put("UNIT", UNIT);
                //隐藏字段控制
                if(!hiddenFields.isEmpty()){
                    String[] fields = hiddenFields.split(",");
                    for (String field : fields) {
                        if(!field.isEmpty()) variables.put(field, "");
                    }
                }
                try {

                    // 生成二维码
                    if(template == null || template.size()== 0 || template.get(0).get("QRCODE").toString().equals("Y")){
                        // 图片路径
                        String picturePath = "";
                        StringBuffer sb = new StringBuffer();

                        String MAT = MATNR + "/" + UNIT;
                        String PO = PO_NO + "/" + PO_ITEM_NO;
                        String BOX = BOX_QTY + "/" + BOX_SN;

                        sb.append("P:" + WERKS);
                        sb.append(";V:" + LIFNR);
                        sb.append(";M:" + MAT);
                        sb.append(";B:" + BATCH);
                        sb.append(";S:" + LABEL_NO);
                        sb.append(";PO:" + PO);
                        sb.append(";Q:" + BOX);
                        sb.append(";D:" + PRODUCT_DATE);
                        sb.append(";YX:" + EFFECT_DATE + ";");

                        picturePath = QRCodeUtil.encode(sb.toString(), LABEL_NO, "", baseDir, true);
                        picturePath = picturePath.replaceAll("\\\\", "//");
                        variables.put("barCode", "file:" + picturePath);
                    }
					// 生成条形码
					if(template != null && template.size() != 0 && template.get(0).get("BARCODE").toString().equals("Y")) {
						if(LABEL_NO.isEmpty()) {
							variables.put("tCode", "");
						}else{
							String picturePath2 = QRCodeUtil.encodeBarCode(LABEL_NO, LABEL_NO + "-tCode", "", baseDir, true);
							picturePath2 = picturePath2.replaceAll("\\\\", "//");
							variables.put("tCode", "file:" + picturePath2);
						}

					}

                } catch (Exception e) {
                    e.printStackTrace();
                }

                listVars.add(variables);
            }
        }
        PdfUtils.preview(configurer,templateName,listVars,response);
    }

    /**
     * 标签打印预览-A4
     */
    @RequestMapping(value = "/labelPreviewByLabel_A4")
    /* @RequiresPermissions("docPrint:labelPreview_A4") */
    public void labelPreviewByLabel_A4(@RequestParam Map<String, Object> params) {
        String labelNOList = params.get("labelNoA4") == null ? null : params.get("labelNoA4").toString();

		String TEMP_SIZE = params.get("labelSizeA4") == null ? "" : params.get("labelSizeA4").toString();
		String TEMP_TYPE = params.get("labelTypeA4") == null ? "" : params.get("labelTypeA4").toString();
        // 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String, Object>> listVars = new ArrayList<>();

        String baseDir = configConstant.getLocation() + "/barcode/";

        String templateName= "wms/print/labelTmp_Label_A4.html";
        //模板隐藏字段
        String hiddenFields = "";
        List<Map<String, Object>> labelList = new ArrayList<>();
        if (labelNOList != null && labelNOList.length() > 0) {
            Map<String, Object> labelParamMap = new HashMap<>();
            labelParamMap.put("LABEL_NOS",labelNOList);
            List<Map<String, Object>> mapList = wmsKnLabelRecordRemote.getLabelList(labelParamMap);
            //查询模板配置信息
            Map paramMap = new HashMap();
			paramMap.put("TEMP_TYPE",TEMP_TYPE);
			paramMap.put("WERKS",mapList.get(0).get("WERKS")== null ? "" : mapList.get(0).get("WERKS").toString());
			paramMap.put("KUNNR",mapList.get(0).get("KUNNR")== null ? "" : mapList.get(0).get("KUNNR").toString());
			paramMap.put("TEMP_SIZE",TEMP_SIZE);
			paramMap.put("TEMP_TYPE",TEMP_TYPE);List<Map<String,Object>> template = printTemplateRemote.getPrintTemplate(paramMap);
            //有且仅有一个模板符合条件时，取配置模板；否则用默认模板
            if(template != null && template.size() == 1 ){
                templateName = template.get(0).get("TEMP_NAME").toString();
                //隐藏字段
                hiddenFields = template.get(0).get("HIDDEN_FIELD") == null ? "" : template.get(0).get("HIDDEN_FIELD").toString();
            }
            for (Map m : mapList) {
                Map<String, Object> label = new HashMap<>();

                String PO_NO = m.get("PO_NO") == null ? "" : m.get("PO_NO").toString();
                String PO_ITEM_NO = m.get("PO_ITEM_NO") == null ? "" : m.get("PO_ITEM_NO").toString();
                String WERKS = m.get("WERKS") == null ? "" : m.get("WERKS").toString();
                String LIFNR = m.get("LIFNR") == null ? "" : m.get("LIFNR").toString();
                String MATNR = m.get("MATNR") == null ? "" : m.get("MATNR").toString();
                String UNIT = m.get("UNIT") == null ? "" : m.get("UNIT").toString();
                String BATCH = m.get("BATCH") == null ? "" : m.get("BATCH").toString();
                String LABEL_NO = m.get("LABEL_NO") == null ? "" : m.get("LABEL_NO").toString();
                String PRODUCT_DATE = m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE").toString();
                String EFFECT_DATE = m.get("EFFECT_DATE") == null ? "" : m.get("EFFECT_DATE").toString();
                String BOX_QTY = m.get("BOX_QTY") == null ? "" : m.get("BOX_QTY").toString();
                String BOX_SN = m.get("BOX_SN") == null ? "" : m.get("BOX_SN").toString();

                label.put("LIKTX", m.get("LIKTX") == null ? "" : m.get("LIKTX"));
                label.put("LABEL_NO", LABEL_NO);
                label.put("MATNR", MATNR);
                label.put("MAKTX", m.get("MAKTX") == null ? "" : m.get("MAKTX"));
                label.put("LIFNR", LIFNR);
                label.put("WERKS", WERKS);
                label.put("PO_NO", PO_NO);
                label.put("PO_ITEM_NO", PO_ITEM_NO);
                label.put("BEDNR", m.get("BEDNR") == null ? "" : m.get("BEDNR"));
                label.put("PRODUCT_DATE", PRODUCT_DATE);
                label.put("EFFECT_DATE", EFFECT_DATE);
                label.put("BATCH", BATCH);
                label.put("BOX_QTY", BOX_QTY);
                label.put("BOX_SN", BOX_SN);
                label.put("UNIT", UNIT);
                //隐藏字段控制
                if(!hiddenFields.isEmpty()){
                    String[] fields = hiddenFields.split(",");
                    for (String field : fields) {
                        if(!field.isEmpty()) label.put(field, "");
                    }
                }

                try {
                    // 生成二维码
                    if(template == null || template.size()== 0 || template.get(0).get("QRCODE").toString().equals("Y")){
                        String picturePath = ""; // 图片路径
                        StringBuffer sb = new StringBuffer();

                        String MAT = MATNR + "/" + UNIT;
                        String PO = PO_NO + "/" + PO_ITEM_NO;
                        String BOX = BOX_QTY + "/" + BOX_SN;

                        sb.append("P:" + WERKS);
                        sb.append(";V:" + LIFNR);
                        sb.append(";M:" + MAT);
                        sb.append(";B:" + BATCH);
                        sb.append(";S:" + LABEL_NO);
                        sb.append(";PO:" + PO);
                        sb.append(";Q:" + BOX);
                        sb.append(";D:" + PRODUCT_DATE);
                        sb.append(";YX:" + EFFECT_DATE + ";");

                        picturePath = QRCodeUtil.encode(sb.toString(), m.get("LABEL_NO").toString(), "", baseDir, true);
                        picturePath = picturePath.replaceAll("\\\\", "//");
                        label.put("barCode", "file:" + picturePath);
                    }
					// 生成条形码
					if(template != null && template.size() != 0 && template.get(0).get("BARCODE").toString().equals("Y")) {
						if(LABEL_NO.isEmpty()) {
							label.put("tCode", "");
						}else{
							String picturePath2 = QRCodeUtil.encodeBarCode(LABEL_NO, LABEL_NO + "-tCode", "", baseDir, true);
							picturePath2 = picturePath2.replaceAll("\\\\", "//");
							label.put("tCode", "file:" + picturePath2);
						}

					}
                } catch (Exception e) {
                    e.printStackTrace();
                }

                labelList.add(label);
            }
            Map<String, Object> variables = new HashMap<>();
            variables.put("labelList", labelList);
            listVars.add(variables);
        }

        PdfUtils.preview(configurer, templateName, listVars, response);
    }
    /**
	 * 标签重复打印预览
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/labelPrintAgain")
	public void labelPrintAgain(@RequestParam Map<String, Object> params){

		String TEMP_SIZE = params.get("labelSizeBD") == null ? "" : params.get("labelSizeBD").toString();
		String TEMP_TYPE = params.get("labelTypeBD") == null ? "" : params.get("labelTypeBD").toString();
		String LIKTX = params.get("LIKTX") == null ? "" : params.get("LIKTX").toString();
		List<Map<String,Object>> mapList=wmsKnLabelRecordRemote.updateEffectDate(params);

		JSONArray labelList = JSONArray.parseArray(JSON.toJSONString(mapList));
		if(TEMP_SIZE.indexOf("A4")<0) {
			params.put("labelList", labelList);
			params.put("LIKTX", LIKTX);
			params.put("labelSize", TEMP_SIZE);
			params.put("labelType", TEMP_TYPE);
			this.labelPreview(params);
		}else {
			params.put("labelListA4", labelList);
			params.put("LIKTXA4", LIKTX);
			params.put("labelSizeA4", TEMP_SIZE);
			params.put("labelTypeA4", TEMP_TYPE);
			this.labelPreview_A4(params);
		}
	}
}
