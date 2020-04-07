package com.byd.web.wms.kn.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.ConfigConstant;
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.wms.kn.service.WmsKnInventoryRemote;


/**
 * 盘点记录
 * @author cscc tangj
 * @email 
 * @date 2018-10-19 10:12:08
 */
@RestController
@RequestMapping("kn/inventory")
public class WmsKnInventoryController {
    @Autowired
    private WmsKnInventoryRemote wmsKnInventoryRemote;
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
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsKnInventoryRemote.list(params);
    }
    /**
     * 打印、导出、明细查询BY inventoryNo
     */
    @RequestMapping("/getItemByInventoryNo")
    public R getItemByInventoryNo(@RequestParam String INVENTORY_NO){
    	return wmsKnInventoryRemote.getItemByInventoryNo(INVENTORY_NO);
    }
    /**
     * 创建盘点表
     */
    @RequestMapping("/create")
    public R create(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));  
		params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return wmsKnInventoryRemote.create(params);
    }
    
    /**
     * 盘点结果录入页面查询
     */
    @RequestMapping("/getInventoryResult")
    public R getInventoryResult(@RequestParam Map<String, Object> params){
    	return wmsKnInventoryRemote.getInventoryResult(params);
    }
    /**
     * 盘点结果录入保存
     */
    @RequestMapping("/saveResult")
    public R saveResult(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME")); 
    	return wmsKnInventoryRemote.saveResult(params);
    }
    // 盘点结果导入保存
    @RequestMapping("/import")
	public R upload(@RequestParam Map<String, Object> params) throws IOException{
    	Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME")); 
    	return wmsKnInventoryRemote.upload(params);
	}
	// Excel导入盘点结果
	@RequestMapping("/previewExcel")
	public R previewExcel(MultipartFile excel,@RequestParam Map<String, Object> params){
		Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		List<String[]> sheet=null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			//e.printStackTrace();
			return R.ok().put("data", entityList);	
		}	
	
		String type=params.get("TYPE").toString();
	
		if(!CollectionUtils.isEmpty(sheet)){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("INVENTORY_NO", row[0]);
				map.put("INVENTORY_ITEM_NO", row[1]);
				map.put("WERKS", row[2]);
				map.put("WH_NUMBER", row[3]);
				map.put("MATNR", row[4]);
				map.put("MAKTX", row[5]);
				map.put("LGORT", row[6]);
				map.put("WH_MANAGER", row[7]);
				map.put("LIFNR", row[8]);
				map.put("LIKTX", row[9]);
				map.put("MEINS", row[10]);
				map.put("STOCK_QTY", row[11]);
				map.put("INVENTORY_QTY", row[12]);
				map.put("INVENTORY_QTY_REPEAT", row[13]);
				entityList.add(map);
			}
		}
		params.put("entityList", entityList);
		return wmsKnInventoryRemote.previewExcel(params);
	}
	/**
     * 盘点结果确认页面查询
     */
    @RequestMapping("/getInventoryConfirm")
    public R getInventoryConfirm(@RequestParam Map<String, Object> params){
    	return wmsKnInventoryRemote.getInventoryConfirm(params);
    }
    /**
     * 盘点结果确认保存
     */
    @RequestMapping("/saveConfirm")
    public R saveConfirm(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return wmsKnInventoryRemote.saveConfirm(params);
    }
    // 打印预览
    @RequestMapping(value = "/printPreview")
    public void printPreview(@RequestParam Map<String, Object> params) {
    	Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	Map<String,Object> variables = new HashMap<>();
    	String dataListStr = params.get("dataList").toString();
    	String inventoryNo = params.get("inventoryNo").toString();
    	String werks = params.get("werks").toString();
    	String whNumber = params.get("whNumber").toString();
    	String baseDir = configConstant.getLocation()+"/barcode/";
    	List<Map<String,Object>> itemList=new ArrayList<Map<String,Object>>();
    	if(dataListStr!=null && dataListStr.length()>0) {
			JSONObject.parseArray(dataListStr, Map.class).forEach(m->{
        		m=(Map<String,Object>)m;
        		itemList.add(m);
        	});
    	}
    	// 构造freemarker模板引擎参数,listVars.size()个数对应pdf文件数
        List<Map<String,Object>> listVars = new ArrayList<>();
        
		variables.put("inventoryNo",inventoryNo); 
		variables.put("werks",werks);   
		variables.put("whNumber",whNumber); 
    	//生成二维码
        try {
        	String picturePath = ""; //图片路径
        	StringBuffer sb = new StringBuffer();
        	sb.append("{inventoryNo:"+inventoryNo);
        	sb.append("}");
			picturePath = QRCodeUtil.encode(sb.toString(),inventoryNo,"",baseDir,true);
			picturePath = picturePath.replaceAll("\\\\", "//");
			variables.put("barCode","file:"+picturePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
        variables.put("itemList",itemList);    
        String basePath="http://"+request.getServerName();
        int port=request.getServerPort();
        String contextPath=request.getContextPath();
        variables.put("contextPath",basePath+":"+port+ contextPath);	        
        listVars.add(variables);
    	
    	PdfUtils.preview(configurer,"wms/print/inventoryDetail.html",listVars,response);
    	wmsKnInventoryRemote.print(params);
    }
    /**
     * 获取库位
     */
    @RequestMapping("/lgortlist")
    public R lgortlist(@RequestParam Map<String, Object> params){
    	
    	Map<String, Object> tempParam=new HashMap<String, Object>();
    	if(params.get("DEL")!=null){
    		tempParam.put("DEL", params.get("DEL"));
    	}
    	if(params.get("WERKS")!=null){
    		tempParam.put("WERKS", params.get("WERKS"));
    	}
    	if(params.get("SOBKZ")!=null){
    		tempParam.put("SOBKZ", params.get("SOBKZ"));
    	}
    	if(params.get("BAD_FLAG")!=null){
    		tempParam.put("BAD_FLAG", params.get("BAD_FLAG"));
    	}
    	return wmsKnInventoryRemote.lgortlist(params);
    }
    /**
     * 加载区域管理员
     */
    @RequestMapping("/getWhManagerList")
    public R getWhManagerList(@RequestParam Map<String, Object> params){
    	return wmsKnInventoryRemote.getWhManagerList(params);
    }
}