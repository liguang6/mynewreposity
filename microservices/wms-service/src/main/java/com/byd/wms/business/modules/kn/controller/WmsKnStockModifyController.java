package com.byd.wms.business.modules.kn.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;
import com.byd.wms.business.modules.kn.service.WmsKnStockModifyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 库存调整记录（库存新增、库存修改）都以EXCEL导入方式操作
 * @author cscc tangj
 * @email
 * @date 2018-11-01 14:12:08
 */
@RestController
@RequestMapping("kn/stockModify")
public class WmsKnStockModifyController {
	@Autowired
	private WmsKnStockModifyService wmsKnStockModifyService;

	/*****************
	 导入模式调整（校验无误的记录直接保存，有错误信息的数据显示到前端页面，可导出供用户修改调整再次导入）
	 tangj 2019-08-26
	 ********************/
	// 库存新增   Excel导入
	@RequestMapping("/previewExcel")
	public R previewExcel(@RequestBody Map<String,Object> params){
		String werks=params.get("werks").toString();
		String whNumber=params.get("whNumber").toString();
		List<Map<String,Object>> entityList = (List<Map<String,Object>>)params.get("entityList");
		// 通过验证 可直接保存记录
		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
		// 验证有错误 显示到页面的记录
		List<Map<String,Object>> errorList=new ArrayList<Map<String,Object>>();
		// 重复行校验（相同 公司，工厂，仓库号，库位，物料号，供应商代码，生产日期 ，储位，库存类型 不能有相同的数据）
		List<String> uniqueList=new ArrayList<String>();
		// 批量查出物料校验所需的数据
		List<Map<String,Object>> materialList=wmsKnStockModifyService.checkMaterialList(params);
		// 批量查出供应商校验所需的数据
		List<String> vnedorParamList=new ArrayList<String>();
		for(Map<String,Object> entity : entityList) {
			if(isNotNull(entity.get("LIFNR"))) {
				vnedorParamList.add(entity.get("LIFNR").toString());
			}
		}
		List<Map<String,Object>> vendorList=new ArrayList<Map<String,Object>>();
		if(vnedorParamList.size()>0) {
			params.put("vnedorParamList", vnedorParamList);
			vendorList=wmsKnStockModifyService.checkVendorList(params);
		}

		// 批量查出库位校验所需的数据
		List<Map<String,Object>> lgortList=wmsKnStockModifyService.checkLgortList(params);
		// 批量查出储位校验所需的数据
		List<Map<String,Object>> binList=wmsKnStockModifyService.checkBinList(params);
		String msg="";
		int rowNo=0;
		for(Map<String,Object> map:entityList){
			map.put("ROW_NO", ++rowNo);
			if(isNotNull(map.get("WERKS"))) {
				// 同工厂代码值校验一次
				if(!werks.equals(map.get("WERKS").toString())) {
					msg="请导入工厂代码"+werks+"数据";
				}
			} else {
				msg="工厂代码不能为空;";
			}
			if(isNotNull(map.get("WH_NUMBER"))) {
				if(!whNumber.equals(map.get("WH_NUMBER").toString())) {
					msg="请导入仓库号"+whNumber+"数据；";
				}
			} else {
				msg+="仓库号不能为空;";
			}
			if(isNotNull(map.get("LGORT"))) {
				boolean isExist=false;
				for(Map<String,Object> lgort : lgortList) {
					if(lgort.get("LGORT").toString().equals(map.get("LGORT").toString())) {
						isExist=true;
						break;
					}
				}
				if(!isExist) {
					msg+="库位【"+map.get("LGORT").toString()+"】未维护;";
				}
			}
			if(isNotNull(map.get("MATNR"))) {
				boolean isExist=false;
				for(Map<String,Object> material : materialList) {
					if(material.get("MATNR").toString().equals(map.get("MATNR").toString())) {
						isExist=true;
						map.put("MAKTX", material.get("MAKTX"));
						map.put("MEINS", material.get("MEINS"));
						map.put("UNIT", material.get("MEINS"));
						break;
					}
				}
				if(!isExist) {
					msg+="SAP物料信息不存在;";
				}
			} else {
				msg+="物料号不能为空;";
			}
			if(isNotNull(map.get("LIFNR"))) {
				boolean isExist=false;
				for(Map<String,Object> vendor : vendorList) {
					if(vendor.get("LIFNR").toString().equals(map.get("LIFNR").toString())) {
						isExist=true;
						map.put("LIKTX", vendor.get("NAME1"));
						break;
					}
				}
				if(!isExist) {
					msg+="供应商代码:"+map.get("LIFNR").toString()+"未维护！";
				}
			}
			if(!isNotNull(map.get("QTY"))) {
				msg+="库存数量不能为空;";
			}
			if(!isNotNull(map.get("BOX_QTY"))) {
				msg+="每箱数量不能为空;";
			}
			if(!isNotNull(map.get("BATCH")) && !isNotNull(map.get("PRODUCTION_DATE"))) {
				msg+="批次和生产日期不能同时为空;";
			}
			if(isNotNull(map.get("BATCH")) && isNotNull(map.get("PRODUCTION_DATE"))) {
				msg+="批次和生产日期只能录入一个;";
			}
			if(isNotNull(map.get("BIN_CODE"))){
				boolean isExist=false;
				for(Map<String,Object> bin :binList) {
					if(bin.get("BIN_CODE").toString().equals(map.get("BIN_CODE").toString())) {
						isExist=true;
						map.put("BIN_NAME", bin.get("BIN_NAME"));
						break;
					}
				}
				if(!isExist) {
					msg+="储位代码未维护;";
				}
			}else {
				msg+="储位代码不能为空;";
			}
			if(isNotNull(map.get("SOBKZ"))) {
				String value="K/Z/E";
				if(value.indexOf(map.get("SOBKZ").toString())<0) {
					msg+="寄售/非寄售字段请输入'K'、'Z'、'E'";
				}
				// 库存类型:E 销售订单，销售订单行号必填
				if("E".equals(map.get("SOBKZ").toString())) {
					if(!isNotNull(map.get("SO_NO"))&& !isNotNull(map.get("SO_ITEM_NO"))) {
						msg+="销售订单号、行项目号不能为空;";
					}
				}
			}
			if(!isNotNull(map.get("QTY_TYPE"))) {
				msg+="非限制/冻结不能为空;";
			}else {
				String value="非限制/冻结";
				if(value.indexOf(map.get("QTY_TYPE").toString())<0) {
					msg+="非限制/冻结字段请输入'非限制'或'冻结'";
				}
			}
			if(msg.equals("")) {
				// 唯一性校验：相同 工厂，仓库号，库位，物料号，供应商代码，生产日期 ，储位，库存类型 不能有相同的数据
				String uniqueStr=map.get("WERKS").toString()+"#"+map.get("WH_NUMBER").toString()+"#"
						+map.get("LGORT").toString()+"#"+map.get("MATNR").toString()+"#"
						+(map.get("LIFNR")!=null ? map.get("LIFNR").toString() : "")
						+"#"+map.get("BIN_CODE").toString()+"#"
						+(map.get("PRODUCTION_DATE")!=null ? map.get("PRODUCTION_DATE").toString() : "")
						+"#"+(map.get("BATCH")!=null ? map.get("BATCH").toString() : "")+"#"
						+map.get("SOBKZ").toString();
				if(!uniqueList.contains(uniqueStr)) {
					uniqueList.add(uniqueStr);
					// 新增 如没有录入批次产生批次
					if (map.get("BATCH")==null || (map.get("BATCH")!=null && map.get("BATCH").toString().equals(""))) {
						String batch=wmsKnStockModifyService.createBatch(map);
						map.put("BATCH", batch);
					}
					saveList.add(map);
				}else {
					msg+="数据已重复";
					map.put("MSG", msg);
					errorList.add(map);
				}
			}else {
				map.put("MSG", msg);
				errorList.add(map);
			}
			msg="";
		}
		// 没有可保存的数据  将校验出错的记录返回
		if(saveList.size()==0) {
			return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
		}else {
			// 产生批次后  再次校验导入数据在系统是否已存在（type=00：如存在报错；type=01：不存在报错）
			params.put("entityList",saveList);
			List<Map<String,Object>> checkStockList=wmsKnStockModifyService.checkStockList(params);
			Iterator<Map<String,Object>> iterator = saveList.iterator();
			while(iterator.hasNext()){
				Map<String,Object> map=iterator.next();
				String matnr=map.get("MATNR").toString();
				String lgort=map.get("LGORT")!=null ? map.get("LGORT").toString() : "";
				String binCode=map.get("BIN_CODE").toString();
				String lifnr=map.get("LIFNR")!=null ? map.get("LIFNR").toString() : "";
				String sobkz=map.get("SOBKZ")!=null ? map.get("SOBKZ").toString() : "";
				String batch=map.get("BATCH").toString();
				for(Map<String,Object> stock : checkStockList) {
					String smatnr=stock.get("MATNR").toString();
					String slgort=stock.get("LGORT")!=null ? stock.get("LGORT").toString() : "";
					String sbinCode=stock.get("BIN_CODE").toString();
					String slifnr=stock.get("LIFNR")!=null ? stock.get("LIFNR").toString() : "";
					String ssobkz=stock.get("SOBKZ")!=null ? stock.get("SOBKZ").toString() : "";
					String sbatch=stock.get("BATCH").toString();
					if(matnr.equals(smatnr) && lgort.equals(slgort)
							&& lifnr.equals(slifnr) && sobkz.equals(ssobkz)
							&& batch.equals(sbatch) && binCode.equals(sbinCode)) {
						msg="系统对应的库存记录已存在,请选择‘库存修改’导入类型导入";
						map.put("MSG", msg);
						iterator.remove();
						errorList.add(map);
						break;
					}
				}
			}
		}
		// 没有可保存的数据  将校验出错的记录返回
		if(saveList.size()==0) {
			return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
		}
		try {
			List<Map<String,Object>> labelList=null;
			Map<String, Object> result=new HashMap<String, Object>();

			params.put("list", saveList);
			result=wmsKnStockModifyService.save(params);

			String wmsNo=result.get("wmsNo").toString();
			if(result.get("labelList")!=null) {
				labelList=(List<Map<String, Object>>) result.get("labelList");
			}
			return R.ok().put("wmsNo", wmsNo).put("labelList", labelList)
					.put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
		}catch(Exception e) {
			// 捕获service抛出的异常信息
			return R.error(e.getMessage()).put("data", errorList);
		}
	}
	// 库存修改   Excel导入预览
	@RequestMapping("/previewModifyExcel")
	public R previewModifyExcel(@RequestBody Map<String,Object> params){
		String werks=params.get("werks").toString();
		String whNumber=params.get("whNumber").toString();
		String barcodeFlag=params.get("BARCODE_FLAG").toString();
		List<Map<String,Object>> entityList = (List<Map<String,Object>>)params.get("entityList");
		// 通过验证 可直接保存记录
		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
		// 验证有错误 显示到页面的记录
		List<Map<String,Object>> errorList=new ArrayList<Map<String,Object>>();
		// 重复行校验（相同 公司，工厂，仓库号，库位，物料号，供应商代码，生产日期 ，储位，库存类型 不能有相同的数据）
		List<String> uniqueList=new ArrayList<String>();
		// 批量查出物料校验所需的数据
		List<Map<String,Object>> materialList=wmsKnStockModifyService.checkMaterialList(params);
		// 批量查出供应商校验所需的数据
		List<String> vnedorParamList=new ArrayList<String>();
		for(Map<String,Object> entity : entityList) {
			if(isNotNull(entity.get("LIFNR"))) {
				vnedorParamList.add(entity.get("LIFNR").toString());
			}
		}
		List<Map<String,Object>> vendorList=new ArrayList<Map<String,Object>>();
		if(vnedorParamList.size()>0) {
			params.put("vnedorParamList", vnedorParamList);
			vendorList=wmsKnStockModifyService.checkVendorList(params);
		}
		// 批量查出库位校验所需的数据
		List<Map<String,Object>> lgortList=wmsKnStockModifyService.checkLgortList(params);
		// 批量查出储位校验所需的数据
		List<Map<String,Object>> binList=wmsKnStockModifyService.checkBinList(params);
		// 校验库存信息
		List<Map<String,Object>> checkStockList=wmsKnStockModifyService.checkStockList(params);
		String msg="";
		int rowNo=0;
		Iterator<Map<String,Object>> iterator = entityList.iterator();
		while(iterator.hasNext()){
			Map<String,Object> map=iterator.next();
			map.put("ROW_NO", ++rowNo);
			if(isNotNull(map.get("WERKS"))) {
				// 同工厂代码值校验一次
				if(!werks.equals(map.get("WERKS").toString())) {
					msg="请导入工厂代码"+werks+"数据";
				}
			} else {
				msg="工厂代码不能为空;";
			}
			if(isNotNull(map.get("WH_NUMBER"))) {
				if(!whNumber.equals(map.get("WH_NUMBER").toString())) {
					msg="请导入仓库号"+whNumber+"数据；";
				}
			} else {
				msg+="仓库号不能为空;";
			}
			if(isNotNull(map.get("LGORT"))) {
				boolean isExist=false;
				for(Map<String,Object> lgort : lgortList) {
					if(lgort.get("LGORT").toString().equals(map.get("LGORT").toString())) {
						isExist=true;
						break;
					}
				}
				if(!isExist) {
					msg+="库位【"+map.get("LGORT").toString()+"】未维护;";
				}
			}
			if(isNotNull(map.get("MATNR"))) {
				boolean isExist=false;
				for(Map<String,Object> material : materialList) {
					if(material.get("MATNR").toString().equals(map.get("MATNR").toString())) {
						isExist=true;
						map.put("MAKTX", material.get("MAKTX"));
						map.put("MEINS", material.get("MEINS"));
						map.put("UNIT", material.get("MEINS"));
						break;
					}
				}
				if(!isExist) {
					msg+="SAP物料信息不存在;";
				}
			} else {
				msg+="物料号不能为空;";
			}
			if(isNotNull(map.get("LIFNR"))) {
				boolean isExist=false;
				for(Map<String,Object> vendor : vendorList) {
					if(vendor.get("LIFNR").toString().equals(map.get("LIFNR").toString())) {
						isExist=true;
						map.put("LIKTX", vendor.get("NAME1"));
						break;
					}
				}
				if(!isExist) {
					msg+="供应商代码:"+map.get("LIFNR").toString()+"未维护！";
				}
			}
			if(!isNotNull(map.get("QTY"))) {
				msg+="库存数量不能为空;";
			}
			if(!isNotNull(map.get("BATCH"))) {
				msg+="批次不能为空;";
			}
			if(isNotNull(map.get("BIN_CODE"))){
				boolean isExist=false;
				for(Map<String,Object> bin :binList) {
					if(bin.get("BIN_CODE").toString().equals(map.get("BIN_CODE").toString())) {
						isExist=true;
						map.put("BIN_NAME", bin.get("BIN_NAME"));
						break;
					}
				}
				if(!isExist) {
					msg+="储位代码未维护;";
				}
			}else {
				msg+="储位代码不能为空;";
			}
			if(isNotNull(map.get("SOBKZ"))) {
				String value="K/Z/E";
				if(value.indexOf(map.get("SOBKZ").toString())<0) {
					msg+="寄售/非寄售字段请输入'K'、'Z'、'E'";
				}
				// 库存类型:E 销售订单，销售订单行号必填
				if("E".equals(map.get("SOBKZ").toString())) {
					if(!isNotNull(map.get("SO_NO"))&& !isNotNull(map.get("SO_ITEM_NO"))) {
						msg+="销售订单号、行项目号不能为空;";
					}
				}
			}
			if(!isNotNull(map.get("QTY_TYPE"))) {
				msg+="非限制/冻结不能为空;";
			}else {
				String value="非限制/冻结";
				if(value.indexOf(map.get("QTY_TYPE").toString())<0) {
					msg+="非限制/冻结字段请输入'非限制'或'冻结'";
				}
			}
			if(msg.equals("")) {
				// 唯一性校验：相同 工厂，仓库号，库位，物料号，供应商代码，生产日期 ，储位，库存类型 不能有相同的数据
				String uniqueStr=map.get("WERKS").toString()+"#"+map.get("WH_NUMBER").toString()+"#"
						+map.get("LGORT").toString()+"#"+map.get("MATNR").toString()+"#"
						+(map.get("LIFNR")!=null ? map.get("LIFNR").toString() : "")
						+"#"+map.get("BIN_CODE").toString()+"#"
						+(map.get("PRODUCTION_DATE")!=null ? map.get("PRODUCTION_DATE").toString() : "")
						+"#"+(map.get("BATCH")!=null ? map.get("BATCH").toString() : "")+"#"
						+map.get("SOBKZ").toString();
				if(!uniqueList.contains(uniqueStr)) {
					uniqueList.add(uniqueStr);
					saveList.add(map);
					String matnr=map.get("MATNR").toString();
					String lgort=map.get("LGORT")!=null ? map.get("LGORT").toString() : "";
					String binCode=map.get("BIN_CODE").toString();
					String lifnr=map.get("LIFNR")!=null ? map.get("LIFNR").toString() : "";
					String sobkz=map.get("SOBKZ")!=null ? map.get("SOBKZ").toString() : "";
					String batch=map.get("BATCH").toString();
					boolean isExsist=false;
					Map<String,Object> stockMap=null;
					for(Map<String,Object> stock : checkStockList) {
						String smatnr=stock.get("MATNR").toString();
						String slgort=stock.get("LGORT")!=null ? stock.get("LGORT").toString() : "";
						String sbinCode=stock.get("BIN_CODE").toString();
						String slifnr=stock.get("LIFNR")!=null ? stock.get("LIFNR").toString() : "";
						String ssobkz=stock.get("SOBKZ")!=null ? stock.get("SOBKZ").toString() : "";
						String sbatch=stock.get("BATCH").toString();
						if(matnr.equals(smatnr) && lgort.equals(slgort)
								&& lifnr.equals(slifnr) && sobkz.equals(ssobkz)
								&& batch.equals(sbatch) && binCode.equals(sbinCode)) {
							isExsist=true;
							stockMap=stock;
							break;
						}
					}
					if(!isExsist) {
						msg="系统库存表未找到对应的修改数据";
						map.put("MSG", msg);
						saveList.remove(map);
						errorList.add(map);
					}
					if(isExsist) {
						BigDecimal addQuantity = new BigDecimal(Double.parseDouble(map.get("QTY").toString()));
						if(map.get("QTY_TYPE").toString().equals("非限制")) {
							BigDecimal oldQuantity = new BigDecimal(Double.parseDouble(stockMap.get("STOCK_QTY").toString()));
							BigDecimal newQuantity = oldQuantity.add(addQuantity);
							if (newQuantity.compareTo(BigDecimal.ZERO) == -1) {
								msg="该条记录将导致库存数量为负,系统中库存数量" + oldQuantity;
							}
							map.put("STOCK_QTY", String.valueOf(newQuantity));
						}
						if(map.get("QTY_TYPE").toString().equals("冻结")) {
							BigDecimal oldQuantity = new BigDecimal(Double.parseDouble(stockMap.get("FREEZE_QTY").toString()));
							BigDecimal newQuantity = oldQuantity.add(addQuantity);
							if (newQuantity.compareTo(BigDecimal.ZERO) == -1) {
								msg= "该条记录将导致冻结数量为负,系统中冻结数量" + oldQuantity;
							}
							map.put("FREEZE_QTY", newQuantity);
						}
						map.put("ID", stockMap.get("ID"));
						if(!msg.equals("")) {
							map.put("MSG", msg);
							saveList.remove(map);
							errorList.add(map);
						}
					}
				}else {
					msg+="数据已重复";
					map.put("MSG", msg);
					errorList.add(map);
				}
			}else {
				map.put("MSG", msg);
				errorList.add(map);
			}
			msg="";
		}
		// 库存修改 启动条码管理仓库  需要到页面段修改库存对应的条码标签数据，才能保存
		if(barcodeFlag.equals("X")) {
			List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
			dataList.addAll(saveList);
			dataList.addAll(errorList);
			return R.ok().put("data", dataList);
		}
		if(saveList.size()==0) {
			return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
		}

		try {
			List<Map<String,Object>> labelList=null;
			Map<String, Object> result=new HashMap<String, Object>();

			params.put("list", saveList);
			result=wmsKnStockModifyService.save(params);

			String wmsNo=result.get("wmsNo").toString();
			if(result.get("labelList")!=null) {
				labelList=(List<Map<String, Object>>) result.get("labelList");
			}
			return R.ok().put("wmsNo", wmsNo).put("labelList", labelList)
					.put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
		}catch(Exception e) {
			// 捕获service抛出的异常信息
			return R.error(e.getMessage()).put("data", errorList);
		}
	}
	// 条码导入库存   Excel导入预览
	@RequestMapping("/previewLabelExcel")
	public R previewLabelExcel(@RequestBody Map<String,Object> params){
		List<Map<String,Object>> entityList = (List<Map<String,Object>>)params.get("entityList");
		String werks=params.get("werks").toString();
		String whNumber=params.get("whNumber").toString();
		// 通过验证 可直接保存记录
		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
		// 验证有错误 显示到页面的记录
		List<Map<String,Object>> errorList=new ArrayList<Map<String,Object>>();
		// 批量查出物料校验所需的数据
		List<Map<String,Object>> materialList=wmsKnStockModifyService.checkMaterialList(params);
		// 批量查出供应商校验所需的数据
		List<String> vnedorParamList=new ArrayList<String>();
		for(Map<String,Object> entity : entityList) {
			if(isNotNull(entity.get("LIFNR"))) {
				vnedorParamList.add(entity.get("LIFNR").toString());
			}
		}
		List<Map<String,Object>> vendorList=new ArrayList<Map<String,Object>>();
		if(vnedorParamList.size()>0) {
			params.put("vnedorParamList", vnedorParamList);
			vendorList=wmsKnStockModifyService.checkVendorList(params);
		}
		// 批量查出库位校验所需的数据
		List<Map<String,Object>> lgortList=wmsKnStockModifyService.checkLgortList(params);
		// 批量查出储位校验所需的数据
		List<Map<String,Object>> binList=wmsKnStockModifyService.checkBinList(params);
		String msg="";
		int rowNo=0;
		List<String> labelNoList=new ArrayList<String>();
		for(Map<String,Object> map:entityList){
			map.put("ROW_NO", ++rowNo);

			if(isNotNull(map.get("LABEL_NO"))) {
				String labelNo=map.get("LABEL_NO").toString();
				if(!labelNoList.contains(labelNo)) {
					labelNoList.add(labelNo);
				}else {
					msg="标签号已重复;";
				}
			} else {
				msg="标签号不能为空;";
			}
			if(isNotNull(map.get("LABEL_STATUS"))) {

			} else {
				msg+="标签状态不能为空;";
			}
			if(isNotNull(map.get("WERKS"))) {
				// 同工厂代码值校验一次
				if(!werks.equals(map.get("WERKS").toString())) {
					msg="请导入工厂代码"+werks+"数据；";
				}
			} else {
				msg="工厂代码不能为空;";
			}
			if(isNotNull(map.get("WH_NUMBER"))) {
				if(!whNumber.equals(map.get("WH_NUMBER").toString())) {
					msg="请导入仓库号"+whNumber+"数据；";
				}
			} else {
				msg+="仓库号不能为空;";
			}
			if(isNotNull(map.get("LGORT"))){
				boolean isExist=false;
				for(Map<String,Object> lgort : lgortList) {
					if(lgort.get("LGORT").toString().equals(map.get("LGORT").toString())) {
						isExist=true;
						break;
					}
				}
				if(!isExist) {
					msg+="库位【"+map.get("LGORT").toString()+"】未维护;";
				}
			}else {
				msg+="库位不能为空;";
			}
			if(isNotNull(map.get("MATNR"))) {
				boolean isExist=false;
				for(Map<String,Object> material : materialList) {
					if(material.get("MATNR").toString().equals(map.get("MATNR").toString())) {
						isExist=true;
						map.put("MAKTX", material.get("MAKTX"));
						map.put("MEINS", material.get("MEINS"));
						map.put("UNIT", material.get("MEINS"));
						break;
					}
				}
				if(!isExist) {
					msg+="SAP物料信息不存在;";
				}
			} else {
				msg+="物料号不能为空;";
			}
			if(isNotNull(map.get("LIFNR"))) {
				boolean isExist=false;
				for(Map<String,Object> vendor : vendorList) {
					if(vendor.get("LIFNR").toString().equals(map.get("LIFNR").toString())) {
						isExist=true;
						map.put("LIKTX", vendor.get("NAME1"));
						break;
					}
				}
				if(!isExist) {
					msg+="供应商代码:"+map.get("LIFNR").toString()+"未维护！";
				}
			}
			if(!isNotNull(map.get("FULL_BOX_QTY"))) {
				msg+="装箱数量不能为空;";
			}
			if(!isNotNull(map.get("BOX_QTY"))) {
				msg+="每箱数量不能为空;";
			}
			if(!isNotNull(map.get("BATCH"))) {
				msg+="批次不能为空;";
			}
			if(!isNotNull(map.get("PRODUCTION_DATE"))) {
				msg+="生产日期不能为空;";
			}
			if(isNotNull(map.get("BIN_CODE"))) {
				boolean isExist=false;
				for(Map<String,Object> bin :binList) {
					if(bin.get("BIN_CODE").toString().equals(map.get("BIN_CODE").toString())) {
						isExist=true;
						map.put("BIN_NAME", bin.get("BIN_NAME"));
						break;
					}
				}
				if(!isExist) {
					msg+="储位代码未维护;";
				}
			}else{
				msg+="储位代码不能为空;";
			}
			if(!isNotNull(map.get("SOBKZ"))) {
				msg+="寄售/非寄售不能为空;";
			}else {
				String value="K, Z, O, E, Q";
				if(value.indexOf(map.get("SOBKZ").toString())<0) {
					msg+="寄售/非寄售字段请输入'K'、'Z'、'E'";
				}
			}
			if(!isNotNull(map.get("EFFECT_DATE"))) {
				msg+="有效期不能为空;";
			}

			if(msg.equals("")) {
				saveList.add(map);
			}else {
				map.put("MSG", msg);
				errorList.add(map);
			}
			msg="";
		}
		List<Map<String,Object>>  labelNoMap=wmsKnStockModifyService.checkLabelList(labelNoList);


		if(labelNoMap.size()>0) {
			Iterator<Map<String,Object>> iterator = saveList.iterator();
			List<String> list = new ArrayList<String>();
			for(Map<String,Object>  labelmap:labelNoMap){
				list.add(labelmap.get("LABEL_NOS").toString());
			}
			Map<String,Object> labelmap =new HashMap<String,Object>(16);
			labelmap.put("LABEL_NOS",list.toString());
			while(iterator.hasNext()){
				Map<String,Object> map=iterator.next();
				String labelNo=map.get("LABEL_NO").toString();
				if(!"".equals(labelNo) && labelmap.get("LABEL_NOS").toString().indexOf(labelNo)>=0) {
					msg= labelNo+":系统已存在！";
					map.put("MSG", msg);
					iterator.remove();
					errorList.add(map);
				}
				msg="";
			}
		}

		if(saveList.size() < entityList.size()) {
			return R.ok().put("data", errorList).put("saveCount", 0)
					.put("errorCount", errorList.size());
		}
		try {
			Map<String, Object> result=new HashMap<String, Object>();
			List<Map<String,Object>> labelList=null;
			result=wmsKnStockModifyService.saveStockLabel(params);
			String wmsNo=result.get("wmsNo").toString();
			if(result.get("labelList")!=null) {
				labelList=(List<Map<String, Object>>) result.get("labelList");
			}
			return R.ok().put("wmsNo", wmsNo).put("labelList", labelList)
					.put("data", errorList).put("saveCount", saveList.size())
					.put("errorCount", errorList.size());
		}catch(Exception e) {
			// 捕获service抛出的异常信息
			return R.error(e.getMessage());
		}
	}

	/**
	 * 保存导入数据
	 */
	@RequestMapping("/save")
	public R save(@RequestBody Map<String, Object> params){
		try {
			List<Map<String,Object>> labelList=null;
			Map<String, Object> result=new HashMap<String, Object>();
			String type=params.get("type").toString();
			//type=02 导入条码新增库存
			if(type.equals("02")) {
				result=wmsKnStockModifyService.saveStockLabel(params);
			}else { // 库存新增、库存修改
				Gson gson=new Gson();
				List<Map<String,Object>> list =gson.fromJson((String) params.get("SAVE_DATA"),new TypeToken<List<Map<String,Object>>>() {}.getType());
				for (Map<String,Object> item : list) {
					if (item.get("BATCH")==null || (item.get("BATCH")!=null && item.get("BATCH").toString().equals(""))) {
						String batch=wmsKnStockModifyService.createBatch(item);
						item.put("BATCH", batch);
					}
				}
				params.put("list", list);
				result=wmsKnStockModifyService.save(params);
			}
			String wmsNo=result.get("wmsNo").toString();
			if(result.get("labelList")!=null) {
				labelList=(List<Map<String, Object>>) result.get("labelList");
			}
			return R.ok().put("wmsNo", wmsNo).put("labelList", labelList);
		}catch(Exception e) {
			// 捕获service抛出的异常信息
			return R.error(e.getMessage());
		}
	}
	// 库存修改  查出对应库存的标签数据  供用户修改，保持库存数据标签记录一致
	@RequestMapping("/getLabelList")
	public R getLabelList(@RequestBody Map<String, Object> params){
		List<Map<String,Object>> labelList=wmsKnStockModifyService.getCoreLabelList(params);
		return R.ok().put("labelList", labelList);
	}
	// 处理空值
	public boolean isNotNull(Object str) {
		boolean result=false;
		if(str==null) {
			return result;
		}
		if(str.toString().trim().equals("")) {
			return result;
		}
		return true;
	}
}