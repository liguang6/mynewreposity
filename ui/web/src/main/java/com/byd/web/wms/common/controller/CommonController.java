package com.byd.web.wms.common.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.byd.utils.Constant;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.common.staticFactory.LocaleLanguageFactory;
import com.byd.web.sys.masterdata.service.DeptRemote;
import com.byd.web.sys.service.SysUserRemote;
import com.byd.web.wms.common.service.CommonRemote;
import com.byd.web.wms.common.service.WmsSapRemote;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * 通用Controller
 *
 * @author tangj 
 * @since 2018-08-02
 */
@RestController
@RequestMapping("common")
public class CommonController {
    @Autowired
    private CommonRemote commonRemote;
    @Autowired
	WmsSapRemote sapRemote;
    @Autowired
	DeptRemote sysDeptRemote;
    @Autowired
    SysUserRemote sysUserRemote;
    @Autowired
    private FreeMarkerConfigurer configurer;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * pdf预览
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/preview")
    public void preview() {
        // 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        Map<String,Object> variables = new HashMap<>();
        variables.put("title","参数设置测试：我是后台传入的参数！");
        listVars.add(variables);

        PdfUtils.preview(configurer,"wms/print/pdfTest.html",listVars,response);
    }

    /**
     * pdf下载
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/download")
    public void download() {
        List<Map<String,Object>> listVars = new ArrayList<>();
        Map<String,Object> variables = new HashMap<>();
        variables.put("title","参数设置测试：我是后台传入的参数！");
        listVars.add(variables);
        PdfUtils.download(configurer,"wms/print/pdfTest.html",listVars,response,"测试中文.pdf");
    }
    @RequestMapping(value = "/pdf")
    public void pdf() {
    	PdfStamper stamper  = null;
    	Document document = null;
    	PdfReader pdfreader = null;
    	try {
    		String filePath = "D:\\PDF\\K9FE-4002800AF-顶棚线束-B0-A0+-180803-受控版.pdf";
    		String outputPath = "D:\\PDF\\K9FE-4002800AF-顶棚线束-B0-A0+-180803-受控版-new.pdf";
    		pdfreader = new PdfReader(filePath);
			int pdfPage = pdfreader.getNumberOfPages();
			document = new Document(pdfreader.getPageSize(1));
			// 获取页面宽度
			float width = document.getPageSize().getWidth();
			// 获取页面高度
			float height = document.getPageSize().getHeight();
			String picturePath; //图片路径
			stamper  =  new PdfStamper(pdfreader, new FileOutputStream(outputPath));//生成的PDF 路径 outPath
		    for (int i = 1 ;i <= pdfPage; i++){
		        PdfContentByte overContent = stamper.getOverContent(i);
		        picturePath = "D:\\PDF\\11.png";
		        Image image = Image.getInstance(picturePath);//图片名称
		        image.setAbsolutePosition((int)width-400,(int)height-250);//左边距、底边距
		        overContent.addImage(image);
		        overContent.stroke();
		    }
		} catch (Exception e){
		    e.printStackTrace();
		} finally {
		    try {
		        if (null != stamper ){
		            stamper.close();
		        }
		        if (null != document ){
		        	document.close();
		        }
		        if (pdfreader != null){
		        	pdfreader.close();
		        }
		    } catch (Exception e){
		        e.printStackTrace();
		    }

		}
    }
    
    @RequestMapping(value = "/pdfBatch")
    public void pdfBatch() {
    	SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
    	System.out.println(ft.format(new Date()));
    	String baseDir = "D:\\PDF";
    	File file = new File(baseDir);
        if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(baseDir + "\\" + filelist[i]);
                if (!readfile.isDirectory()) {
                    System.out.println("path=" + readfile.getPath());
                    System.out.println("absolutepath=" + readfile.getAbsolutePath());
                    System.out.println("name=" + readfile.getName());
                    String fileName = readfile.getName();
                    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                    String partsInfo = fileName.substring(0, fileName.lastIndexOf("."));
                    String picturePath = ""; //图片路径
                    if("pdf".equals(suffix)) {
                    	//pdf文件
                    	
                    	PdfStamper stamper  = null;
                    	Document document = null;
                    	PdfReader pdfreader = null;
                    	try {
                    		String outputPath = baseDir+"\\new_"+readfile.getName();
                    		pdfreader = new PdfReader(readfile.getPath());
                			int pdfPage = pdfreader.getNumberOfPages();
                			document = new Document(pdfreader.getPageSize(1));
                			// 获取页面宽度
                			float width = document.getPageSize().getWidth();
                			// 获取页面高度
                			float height = document.getPageSize().getHeight();
                			stamper  =  new PdfStamper(pdfreader, new FileOutputStream(outputPath));//生成的PDF 路径 outPath
                		    for (int j = 1 ;j <= pdfPage; j++){
                		        PdfContentByte overContent = stamper.getOverContent(j);
                		        //picturePath = "D:\\PDF\\11.png";
                		        
                		        //生成二维码
                		        picturePath = QRCodeUtil.encode(partsInfo,partsInfo,"",baseDir,true);
                		        Image image = Image.getInstance(picturePath);//图片名称
                		        image.setAbsolutePosition((int)width-500,(int)height-350);//左边距、底边距
                		        overContent.addImage(image);
                		        overContent.stroke();
                		    }
                		} catch (Exception e){
                		    e.printStackTrace();
                		} finally {
                		    try {
                		        if (null != stamper ){
                		            stamper.close();
                		        }
                		        if (null != document ){
                		        	document.close();
                		        }
                		        if (pdfreader != null){
                		        	pdfreader.close();
                		        }
                		        File pictureFile = new File(picturePath);
                		        if(pictureFile.exists()) {
                		        	pictureFile.delete();
                		        }
                		    } catch (Exception e){
                		        e.printStackTrace();
                		    }

                		}
                    	
                    }
                } 
            }
        	

        } 
        System.out.println(ft.format(new Date()));
    }

    /**
     * 列表
     */
    @RequestMapping("/getPlantList")
    public R getPlantList(@RequestParam String werks){
    	return commonRemote.getPlantList(werks);
    }
    
    @RequestMapping("/getWhList")
    public R getWhList(@RequestParam String whNumber){
    	String language = (String)request.getSession().getAttribute(Constant.SESSION_LANGUAGE_KEY);
    	return commonRemote.getWhList(whNumber,language);
    }
    
    @RequestMapping("/getVendorList")
    public R getVendorList(@RequestParam(value = "lifnr") String lifnr,@RequestParam(value = "werks") String werks){
    	return commonRemote.getVendorList(lifnr, werks);
    }
    
    @RequestMapping("/getVendor")
    public R getVendor(@RequestParam(value = "lifnr") String lifnr){
    	return commonRemote.getVendor(lifnr);
    }
    
	@RequestMapping("/getMaterialList")
    public R getMaterialList(@RequestParam String werks,@RequestParam String matnr){
		return commonRemote.getMaterialList(werks, matnr);
    }
    
    /**
     * 根据权限获取业务类型下拉列表
     * @param params
     * @return
     */
    @RequestMapping("/getBusinessList")
    public R getBusinessList(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	params.put("user_id", currentUser.get("USER_ID"));
    	String language = (String)request.getSession().getAttribute(Constant.SESSION_LANGUAGE_KEY);
    	params.put("language", language);
    	R rtn = commonRemote.getBusinessList(params);
    	List<Map<String,Object>> list = (List<Map<String,Object>>)rtn.get("data");
    	if(list !=null && list.size()>0) {
/*	        for (Map<String, Object> map : list) {
				if(StringUtils.isNotBlank(map.get("LANG_KEY")==null?null:map.get("LANG_KEY").toString())){
					map.put("BUSINESS_NAME",LocaleLanguageFactory.getValue(map.get("LANG_KEY").toString()));
				}
			}*/
	        
	        if(params.get("MENU_KEY")!=null && StringUtils.isNotEmpty(params.get("MENU_KEY").toString())) {
	        	List<Map<String, Object>> userBusinessNameList = userUtils.getUserBusinessName(params.get("MENU_KEY").toString());
	        	List<Map<String,Object>> baseList = new ArrayList<Map<String,Object>>();
	        	for (Map<String, Object> map : list) {
	        		baseList.add(map);
				}
	        	boolean hasAll = false;
	        	Iterator<Map<String,Object>> it = list.iterator();
	        	while(it.hasNext()) {
	        		Map<String,Object> map = it.next();
	        		boolean b = false;
	        		for (Map<String, Object> userMap : userBusinessNameList) {
						if(StringUtils.isNotBlank(userMap.get("BUSINESS_NAME")==null?null:userMap.get("BUSINESS_NAME").toString()) 
							&& StringUtils.isNotBlank(map.get("LANG_KEY")==null?null:map.get("LANG_KEY").toString())
							&& userMap.get("BUSINESS_NAME").toString().equals(map.get("LANG_KEY").toString())){
							b = true;
							break;
						}
						if("*".equals(userMap.get("BUSINESS_NAME").toString())) {
							hasAll = true;
							break;
						}
						
					}
	        		if(!b) {
	        			it.remove();
	        		}
	        		if(hasAll) {
	        			break;
	        		}
	        	}
        		if(hasAll) {
        			rtn.put("data", baseList);
        		}
	        	
	        }
    	}
	    
        return rtn;
    }
    
    /**
     * 根据工厂代码、库存类型查询库位列表
     * @param params
     * @return
     */
    @RequestMapping("/getLoList")
    public R getLoList(@RequestParam Map<String,Object> params) {
    	return commonRemote.getLoList(params);
    }
    /**
     * 根据工厂代码查询收料房存放区列表
     * @return
     */
    @RequestMapping("/getGrAreaList")
    public R getGrAreaList(@RequestParam String WERKS) {
    	return commonRemote.getGrAreaList(WERKS);
    }
    
    /**
	 * 根据工厂号获取全部仓库信息@YK20180829
	 * @param WERKS
	 */
    @RequestMapping("/getWhDataByWerks")
    public R getWhDataByWerks(@RequestParam Map<String,Object> params) {
    	String WERKS = params.get("WERKS") == null?null:params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS) && StringUtils.isNotBlank(params.get("MENU_KEY")==null?null:params.get("MENU_KEY").toString())) {
    		String MENU_KEY = params.get("MENU_KEY").toString();
    		return R.ok().put("data", userUtils.getUserWh(MENU_KEY));
    	}
    	if(StringUtils.isEmpty(WERKS) && StringUtils.isEmpty(params.get("MENU_KEY")==null?null:params.get("MENU_KEY").toString())) {
    		return R.ok().put("data", new ArrayList<Map<String,Object>>());
    	}
    	if(StringUtils.isNotBlank(WERKS) && StringUtils.isNotBlank(params.get("MENU_KEY")==null?null:params.get("MENU_KEY").toString())) {
    		String MENU_KEY = params.get("MENU_KEY").toString();
    		return R.ok().put("data", userUtils.getUserWhByWerks(MENU_KEY,WERKS));
    	}
    	
    	R r = commonRemote.getWhDataByWerks(WERKS);
    	List<Map<String,Object>> allWh = r.get("data")==null?null:(List<Map<String,Object>>)r.get("data");
    	if(allWh==null || allWh.size()<=0) {
    		return r;
    	}
    	if(StringUtils.isNotBlank(params.get("MENU_KEY")==null?null:params.get("MENU_KEY").toString())) {
    		String MENU_KEY = params.get("MENU_KEY").toString();
    		Set<Map<String,Object>> userWhList = userUtils.getUserWh(MENU_KEY);
    		List<Map<String,Object>> list = new ArrayList<>();
    		for (Map<String, Object> map : userWhList) {
    			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
    				//找到用户仓库号权限
    				String userWhStr = map.get(MENU_KEY).toString();
    				for (String wh : userWhStr.split(",")) {
    						if(wh.contains("*")) {
    							if("*".equals(wh)) {
    								wh = "";
    							}else {
    								wh = wh.toUpperCase().split("\\*")[0];
    							}
    							
    							for (Map<String, Object> whMap : allWh) {
    								if(WERKS.equals(whMap.get("WERKS").toString()) && 
    										whMap.get("WH_NUMBER").toString().startsWith(wh)) {
    									whMap.put("code", whMap.get("WH_NUMBER").toString());
    									list.add(whMap);
    									
    									break;
    								}
    							}
    						}else {
    							for (Map<String, Object> whMap : allWh) {
    								if(WERKS.equals(whMap.get("WERKS").toString()) && 
    										whMap.get("WH_NUMBER").toString().equals(wh)) {
    									whMap.put("code", whMap.get("WH_NUMBER").toString());
    									list.add(whMap);
    									break;
    								}
    							}
    						}
    					}
    					break;
    				}
    			}
    		r.put("data", list);
	        return r;
        }else {
        	return r;
        }
    	
    	
    }
    
    @RequestMapping("/getReasonData")
	public R getReasonData(@RequestParam String REASON_DESC) {
    	return commonRemote.getReasonData(REASON_DESC);
	}
	
    /**
     * 获取成本中心
     * @param Costcenter
     * @return
     */
	@RequestMapping("/sapCostcenter/{Costcenter}")
	public Map<String,Object> getSapCostcenterDetail(@PathVariable("Costcenter")String Costcenter){
		return sapRemote.getSapBapiCostcenterDetail(Costcenter);
	}
    
    @RequestMapping("/getPlantSetting")
    public R getPlantSetting(@RequestParam String WH_NUMBER){
    	return commonRemote.getPlantSetting(WH_NUMBER);
    }
    @RequestMapping("/getDictList")
    public R getDictList(@RequestParam String type) {
    	R rtn =  commonRemote.getDictList(type);
    	List<Map<String,Object>> list = (List<Map<String,Object>>)rtn.get("data");
        for (Map<String, Object> map : list) {
			if(StringUtils.isNotBlank(map.get("LANG_KEY")==null?null:map.get("LANG_KEY").toString())){
				map.put("VALUE",LocaleLanguageFactory.getValue(map.get("LANG_KEY").toString()));
			}
		}
        return rtn;
    }
    
    @RequestMapping("/getControlFlagList")
    public R getControlFlagList(@RequestParam Map<String, Object> params){
		
    	R rtn = commonRemote.getControlFlagList(params);
    	
        return rtn;
    }

    @RequestMapping("/getMaterialInfo")
    public R getMaterialInfo(@RequestParam Map<String,Object> map) {
    	return commonRemote.getMaterialInfo(map);
    }
    
    @RequestMapping("/getMatStockInfo")
    public R getMatStockInfo(@RequestParam Map<String,Object> map) {
    	return commonRemote.getMatStockInfo(map);
    }

    /**
     * 获取物料管理员
     * @param params
     * @return
     */
    @RequestMapping("/getMatManager")
    public  R getMatManager(@RequestParam Map<String, Object> params) {
    	return commonRemote.getMatManager(params);
    }
    
    /**
     * 根据工厂代码、查询所有库位（包括不良品）
     * @param params
     * @return
     */
    @RequestMapping("/getAllLoList")
    public R getAllLoList(@RequestParam Map<String,Object> params) {
    	return commonRemote.getAllLoList(params);
    }
    
    @RequestMapping("/checkPassword")
    public R checkPassword(@RequestParam Map<String,Object> params) {
    	return sysUserRemote.checkPassword(params);
    }

}