package com.byd.web.wms.kn.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.byd.utils.ExcelReader;
import com.byd.utils.ExcelWriter;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.kn.service.WmsKnStockModifyRemote;
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
    private WmsKnStockModifyRemote wmsKnStockModifyRemote;
    @Autowired
    private UserUtils userUtils;
    
	// 库存新增Excel导入预览
	@RequestMapping("/previewExcel")
	public R previewExcel(MultipartFile excel,@RequestParam Map<String,Object> param){
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		Map<String,Object> user = userUtils.getUser();
		param.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		List<String[]> sheet =  null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			//返回空List （初始化时，没选择导入文件，也显示表头标题）
			return R.ok().put("data", entityList);	
		}	
		
		int index=0;
		int rowNo=1;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", rowNo);
				map.put("COMPANY", row[index]);
				map.put("WERKS", row[++index]);
				map.put("WH_NUMBER", row[++index]);
				map.put("LGORT", row[++index]);
				map.put("MATNR", row[++index]);
				map.put("MAKTX", row[++index]);
				map.put("LIFNR", row[++index]);
				map.put("MEINS", row[++index]);
				map.put("UNIT", map.get("MEINS"));
				map.put("BATCH", row[++index]);
				// type：00 库存新增导入【生产日期】
				map.put("PRODUCTION_DATE", row[++index]);
				map.put("QTY", row[++index]);
				// type：00库存新增导入【每箱数量】
				map.put("BOX_QTY", row[++index]);
				map.put("BIN_CODE", row[++index]);
				map.put("BIN_NAME", row[++index]);
				map.put("SOBKZ", row[++index]);
				map.put("QTY_TYPE", row[++index]);
				// 库存新增导入【备注】
				map.put("MEMO", row[++index]);
				// 【销售订单号】
				map.put("SO_NO", row[++index]);
				// 【销售订单行项目】
				map.put("SO_ITEM_NO", row[++index]);
				
				entityList.add(map);
				rowNo++;
				index=0;
			}
		}
		param.put("entityList", entityList);
		return wmsKnStockModifyRemote.previewExcel(param);
	}
	// 库存修改Excel导入预览
	@RequestMapping("/previewModifyExcel")
	public R previewModifyExcel(MultipartFile excel,@RequestParam Map<String,Object> param){
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		Map<String,Object> user = userUtils.getUser();
		param.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		List<String[]> sheet =  null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			//返回空List （初始化时，没选择导入文件，也显示表头标题）
			return R.ok().put("data", entityList);	
		}	
		
		int index=0;
		int rowNo=1;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", rowNo);
				map.put("COMPANY", row[index]);
				map.put("WERKS", row[++index]);
				map.put("WH_NUMBER", row[++index]);
				map.put("LGORT", row[++index]);
				map.put("MATNR", row[++index]);
				map.put("MAKTX", row[++index]);
				map.put("LIFNR", row[++index]);
				map.put("MEINS", row[++index]);
				map.put("UNIT", map.get("MEINS"));
				map.put("BATCH", row[++index]);			
				map.put("QTY", row[++index]);
				map.put("BIN_CODE", row[++index]);
				map.put("BIN_NAME", row[++index]);
				map.put("SOBKZ", row[++index]);
				map.put("QTY_TYPE", row[++index]);
				// 【销售订单号】
				map.put("SO_NO", row[++index]);
				// 【销售订单行项目】
				map.put("SO_ITEM_NO", row[++index]);
				
				entityList.add(map);
				rowNo++;
				index=0;
			}
		}
		param.put("entityList", entityList);
		return wmsKnStockModifyRemote.previewModifyExcel(param);
	}
	// 标签数据导入库存数据
	@RequestMapping("/previewLabelExcel")
	public R previewLabelExcel(MultipartFile excel,@RequestParam Map<String,Object> param){
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		Map<String,Object> user = userUtils.getUser();
		param.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		List<String[]> sheet =  null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			//返回空List （初始化时，没选择导入文件，也显示表头标题）
			return R.ok().put("data", entityList);	
		}	
		
		int index=0;
		int rowNo=1;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("ROW_NO", rowNo);
				map.put("LABEL_NO", row[index]);
				map.put("LABEL_STATUS", row[++index]);
				map.put("BOX_SN", row[++index]);
				map.put("FULL_BOX_QTY", row[++index]);
				map.put("BOX_QTY", row[++index]);
				map.put("END_FLAG", row[++index]);
				map.put("WERKS", row[++index]);
				map.put("WH_NUMBER", row[++index]);
				map.put("LGORT", row[++index]);
				map.put("MATNR", row[++index]);
				map.put("BIN_CODE", row[++index]);
				map.put("SOBKZ", row[++index]);		
				map.put("F_BATCH", row[++index]);
				map.put("BATCH", row[++index]);
				map.put("LIFNR", row[++index]);
				map.put("PRODUCTION_DATE", row[++index]);
				map.put("REMARK", row[++index]);
				map.put("EFFECT_DATE", row[++index]);
				entityList.add(map);
				rowNo++;
				index=0;
			}
		}
		param.put("entityList", entityList);
		return wmsKnStockModifyRemote.previewLabelExcel(param);
	}
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	try {
    		Map<String,Object> user = userUtils.getUser();
    		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			R r=wmsKnStockModifyRemote.save(params);
	    	return r;
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 导出
     * @throws Exception 
     */
    @RequestMapping("/exportExcel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam Map<String, Object> params) throws Exception{
    	Gson gson=new Gson();
		List<Map<String,Object>> entityList =gson.fromJson((String) params.get("entityList"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		HttpHeaders header = new HttpHeaders();
		String filename = "stock.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("COMPANY-公司");
		fieldTitleList.add("WERKS-工厂");
		fieldTitleList.add("WH_NUMBER-仓库号");
		fieldTitleList.add("LGORT-库位");
		fieldTitleList.add("MATNR-料号");
		fieldTitleList.add("MAKTX-物料描述");
		fieldTitleList.add("LIFNR-供应商");
		fieldTitleList.add("UNIT-单位");
		fieldTitleList.add("BATCH-批次");
		fieldTitleList.add("PRODUCTION_DATE-生产日期");
		fieldTitleList.add("QTY-库存数量");
		fieldTitleList.add("BOX_QTY-每箱数量");
		fieldTitleList.add("BIN_CODE-储位");
		fieldTitleList.add("BIN_NAME-储位名称");
		fieldTitleList.add("SOBKX-寄售/非寄售");
		fieldTitleList.add("QTY_TYPE-非限制/冻结");
		fieldTitleList.add("MEMO-备注");
		fieldTitleList.add("SO_NO-销售订单号");
		fieldTitleList.add("SO_ITEM_NO-销售订行项目");
		fieldTitleList.add("MSG-校验信息");
		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		//HTTP contentType
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			// write records to file --
			OutputStream outputstream = new FileOutputStream(tmpExcel);
			ExcelWriter.writeRecordToFile(outputstream,entityList,fieldTitleList);		
			outputstream.close();//写完后,关闭输出流
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(tmpExcel), header,
					HttpStatus.CREATED);
			
			return responseEntity;
		} catch (IOException e) {
			FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
			throw e;// 重新抛出异
		} finally {
			FileUtils.deleteQuietly(tmpExcel);// 删除临时文件
		}
    }
    /**
     * 查询标签数据
     */
    @RequestMapping("/getLabelList")
    public R getLabelList(@RequestParam Map<String, Object> params){
    	
		return wmsKnStockModifyRemote.getLabelList(params);
	    	
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