package com.byd.wms.business.modules.out.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.config.dao.WmsCWhDao;
import com.byd.wms.business.modules.out.dao.ScannerOutDAO;
import com.byd.wms.business.modules.out.service.CreateRequirementService;
import com.byd.wms.business.modules.out.service.ScannerOutService;



/**
 * 扫描枪出库 && PDA扫描出库
 * @author
 *
 */
@RestController
@RequestMapping("/out/scanner")
public class ScannerOutController {
	
	@Autowired
	CreateRequirementService service;
	
	@Autowired
	ScannerOutDAO scannerOutDAO;
	
	@Autowired
	ScannerOutService scannerOutService;

	@Autowired
	UserUtils userUtils;
	
	@Autowired
	private WmsCTxtService wmsCtxService;
	
	@Autowired
	private WmsCWhDao wmsCWhdao;
	
	@Autowired
	private CommonService commonService;
	


	/**
	 * 根据标签号，查询标签信息
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryScanner")
	public R queryScannerOut(@RequestBody Map<String,Object> data){
		boolean errflag = false;
		String errmsg = "";
		List<Map<String,Object>> list =  scannerOutDAO.selectCoreLabel(data);
		if (list.size() > 0) {
			
			if (list.get(0).get("LABEL_STATUS").equals("09")) {
				errflag = true;
				errmsg = "标签已下架，不允许出库！";
			}
			if (list.get(0).get("LABEL_STATUS").equals("10")) {
				errflag = true;
				errmsg = "标签已出库，不允许出库！";
			}
			if (list.get(0).get("LABEL_STATUS").equals("11")) {
				errflag = true;
				errmsg = "标签已冻结，不允许出库！";
			}
			if (list.get(0).get("LABEL_STATUS").equals("12")) {
				errflag = true;
				errmsg = "标签已锁定，不允许出库！";
			}
			if (list.get(0).get("LABEL_STATUS").equals("20")) {
				errflag = true;
				errmsg = "标签已关闭，不允许出库！";
			}
			
			//如果仓库启用条码管理, 则检查标签状态是否可出库, 反之不检查。 update by renwei
			if (errflag) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("werks", data.get("WERKS"));
				params.put("whNumber", data.get("WH_NUMBER"));
				params.put("start", 0);params.put("end", 10);
				List<Map<String,Object>> whlist=wmsCWhdao.getWmsCWhList(params);
				if (whlist.size() > 0) {
					if (whlist.get(0).get("BARCODE_FLAG").equals("X")) {
						return R.error(errmsg);
					}
				}
			}
			
			//增加库存检查逻辑。 update by renwei
			Map<String, Object> stockParams = new HashMap<String, Object>();
			stockParams.put("WH_NUMBER", data.get("WH_NUMBER"));
			stockParams.put("WERKS", data.get("WERKS"));
			stockParams.put("MATNR", list.get(0).get("MATNR"));
			stockParams.put("BATCH", list.get(0).get("BATCH"));
			stockParams.put("LIFNR", list.get(0).get("LIFNR"));
			stockParams.put("SOBKZ", list.get(0).get("SOBKZ"));
			stockParams.put("LGORT", list.get(0).get("LGORT"));
			List<Map<String, Object>> stockList = commonService.getWmsStockforMap(stockParams);
			if (stockList.size() < 1 ) {
				return R.error(list.get(0).get("BATCH")+" 批次无库存!");
			} else {
				BigDecimal boxqty = list.get(0).get("BOX_QTY") == null ? BigDecimal.ZERO : new BigDecimal(list.get(0).get("BOX_QTY").toString());
				for (Map<String, Object> stock :stockList) {
					if (!stock.get("BIN_CODE").equals("9010")) { //排除上架缓存储位
						BigDecimal stockqty = new BigDecimal(stock.get("STOCK_QTY").toString());
						if (boxqty.compareTo(stockqty) <= 0) {
							boxqty = BigDecimal.ZERO;
							break;
						} else {
							boxqty = boxqty.subtract(stockqty);
						}
					}
				}
				
				if (boxqty.compareTo(BigDecimal.ZERO) > 0) 
					return R.error(list.get(0).get("MATNR") +"物料，库存短缺"+boxqty+",请先拆分标签!");
			}
			
			
			
			Map<String, String> txtparams = new HashMap<String, String>(); 
			txtparams.put("BUSINESS_NAME", data.get("businessName")==null?"":data.get("businessName").toString());
			txtparams.put("JZ_DATE", data.get("JZ_DATE")==null?"":data.get("JZ_DATE").toString());
			txtparams.put("WERKS", data.get("WERKS") ==null?"":data.get("WERKS").toString());
			txtparams.put("FULL_NAME", userUtils.getUser().get("FULL_NAME").toString());
			txtparams.put("PURPOSE", list.get(0).get("PURPOSE") ==null?"":list.get(0).get("PURPOSE").toString());
//			txtparams.put("requirement_no", params.get("REQUIREMENT_NO") ==null?"":params.get("REQUIREMENT_NO").toString());
			Map<String, Object> retrule=wmsCtxService.getRuleTxt(txtparams);
			for(Map<String, Object> map : list) {
				if(!retrule.isEmpty()){
					if("success".equals(retrule.get("msg"))){
						String txtrule=retrule.get("txtrule").toString();//头文本
						String txtruleitem=retrule.get("txtruleitem").toString();//行文本
						map.put("ITEM_TEXT", txtruleitem);
						map.put("TXTRULE", txtrule);
					}
				}
			}
		}
		if(CollectionUtils.isEmpty(list)){
			return R.error("没有符合条件的标签信息，请确认参数是否正确");
		}		
		return R.ok().put("data", list);
	}


	/**
	 * 根据标签号，查询标签信息
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryScannerByPda")
	@CrossOrigin
	public List queryScannerByPda(@RequestBody Map<String,Object> data){
		System.err.println("queryScannerByPda===>>>"+data.toString());
		List<Map<String,Object>> list =  scannerOutService.queryCoreLabelByPda(data);
		return list;
	}


	/**
	 * 保存标签信息
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/listSaveByPdaBarcode")
	@CrossOrigin
	public R listSaveByPdaBarcode(@RequestBody Map<String,Object> params){
		System.err.println("listSaveByPdaBarcode===>>>"+params.toString());
		//考虑更新wms_core_label表的状态 代表扫描过的条码
		boolean isOk=scannerOutService.saveLabelByPda((ArrayList) params.get("params"));
		if(!isOk) {
			return R.error("操作失败");
		}
		System.err.println(R.ok());
		return R.ok();
	}


	/**
	 * WEB页面查询条码缓存表数据
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryBarcodeCache")
	@CrossOrigin
	public List<Map<String,Object>> queryBarcodeCache(@RequestBody Map<String,Object> data){
		data.put("CREATOR",userUtils.getUser().get("USERNAME"));
		List<Map<String,Object>> list =  scannerOutService.queryBarcodeCache(data);
		return list;
	}

	/**
	 * 查询条码是否被扫描
	 * @param data
	 * @return
	 */
	@RequestMapping("/queryBarcodeOnly")
	@CrossOrigin
	public List<Map<String,Object>> queryBarcodeOnly(@RequestBody Map<String,Object> data){
		List<Map<String,Object>>  count =  scannerOutService.queryBarcodeOnly(data);
		return count;
	}

	/**
	 * 交接过账后删除缓存记录
	 * @param data
	 * @return
	 */
	@RequestMapping("/deteleCacheBarcodeByCreator")
	@CrossOrigin
	public R deteleCacheBarcodeByCreator(@RequestBody List<String> data){
		scannerOutService.deteleCacheBarcodeByCreator(data);
		return R.ok();
	}



	//校验需求号
	@RequestMapping("/validReqNo")
	public R validReqNo(@RequestBody Map<String,Object> params){
		return R.ok().put("data", scannerOutDAO.selectOutRequirementHead(params));
	}
	
	//业务类型
	@RequestMapping("/queryBusinessName")
	@CrossOrigin
	public R queryBusinessName(@RequestBody Map<String,Object> params){
		return R.ok().put("data", scannerOutDAO.selectBusinessNames((String)params.get("werks"),(String) params.get("businessClass")));
	}
	
	/**
	 * 下架
	 * @return
	 */
	@RequestMapping("/obtained")
	public R obtained(@RequestBody List<Map<String,Object>> params){
		scannerOutService.obtained(params);
		return R.ok();
	}
	
	/**
	 * 取消下架
	 * @return 成功 / 失败
	 */
	@RequestMapping("/cancelObtained")
	public R cancelObtained(@RequestBody List<Map<String,Object>> params){
		try {
			scannerOutService.cancelOntained(params);
		} catch (IllegalArgumentException e) {
			return R.error(e.getMessage());
		} catch (Exception e) {
			return R.error("取消下架失败");
		}
		return R.ok();
	}
	
	/**
	 * 交接确认
	 * @return
	 */
	@RequestMapping("/handoverComfirm")
	public R handoverComfirm(@RequestBody List<Map<String,Object>> params){
		String msg;
		try {
			msg = scannerOutService.handoverComfirm(params);
			return R.ok(msg);
		} catch (Exception e) {
			return R.error(e.getMessage());
		}
		
	}
	
}
