package com.byd.web.sys.masterdata.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.HttpContextUtils;
import com.byd.utils.R;
import com.byd.utils.StringUtils;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.entity.DictEntity;
import com.byd.web.sys.masterdata.service.DictRemote;
import com.byd.web.sys.masterdata.service.MasterDataRemote;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


/**
 * 主数据对外提供服务类
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@RestController
@RequestMapping("/masterdata")
public class MasterDataController{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MasterDataRemote masterDataRemote;
	@Autowired
	private DictRemote masterdataDictRemote;//主数据服务字典
	@Autowired
    private UserUtils userUtils;
	
	@Value("${spring.servlet.multipart.location}")
	private String fileLocation; //文件存放根据路径
	@Value("${web.file-root.ROMOTE_DESIGNPAPER_PLM}")
	private String ROMOTE_DESIGNPAPER_PLM; //PLM系统图纸linux挂载路径
	@Value("${web.file-root.ROMOTE_DESIGNPAPER_WERKS}")
	private String ROMOTE_DESIGNPAPER_WERKS; //工厂手工维护图纸挂载路径
	
	/**
	 * 获取所有工厂清单
	 * @param params CODE 工厂代码
	 * @return
	 */
    @RequestMapping("/getWerksList")
    public R getWerksList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getWerksList(params);
    }
	
	/**
	 * 根据工厂代码获取工厂下所有车间
	 * @param params CODE 工厂代码
	 * @return
	 */
    @RequestMapping("/getWerksWorkshopList")
    public R getWerksWorkshopList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getWerksWorkshopList(params);
    }
	
	/**
	 * 根据工厂代码、车间获取车间下所有班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getWorkshopWorkgroupList")
    public R getWorkshopWorkgroupList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getWorkshopWorkgroupList(params);
    }
    
	/**
	 * 根据工厂代码、车间、班组获取所有小班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getTeamList")
    public R getTeamList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getTeamList(params);
    }
    
    /**
	 * 根据工厂代码获取用户权限车间
	 * @param params CODE/WERKS 工厂代码 
	 * @return
	 */
    @RequestMapping("/getUserWorkshopByWerks")
    public R getUserWorkshopByWerks(@RequestParam Map<String, Object> params) {
    	return masterDataRemote.getUserWorkshopByWerks(params);
    }
    
    /**
	 * 根据工厂代码、车间获取用户权限线别
	 * @param params WERKS 工厂代码  WORKSHOP 车间
	 * @return
	 */
    @RequestMapping("/getUserLine")
    public R getUserLine(@RequestParam Map<String, Object> params) {
    	return masterDataRemote.getUserLine(params);
    }
    
	/**
	 * 根据工厂代码、车间获取车间下所有线别信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getWorkshopLineList")
    public R getWorkshopLineList(@RequestParam Map<String, Object> params) {
    	return masterDataRemote.getWorkshopLineList(params);
    }
    
	/**
	 * 获取所有车型清单
	 * @param params VEHICLE_TYPE 车辆类型
	 * @return
	 */
    @RequestMapping("/getBusTypeList")
    public R getBusTypeList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getBusTypeList(params);
    }
    
	/**
	 * 获取品质检验标准清单
	 * @param STANDARD_TYPE：检验标准类型 如外观标准，STANDARD_NAME：标准描述  pageSize 限制匹配记录条数
	 * @return
	 */
    @RequestMapping("/getQmsTestStandard")
    public R getQmsTestStandard(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getQmsTestStandard(params);
    }
    
	/**
	 * 获取品质抽样规则
	 * @param WERKS：工厂代码 WORKSHOP：车间代码/名称  ORDER_AREA_CODE：订单区域代码
	 * @return
	 */
    @RequestMapping("/getQmsTestRules")
    public R getQmsTestRules(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getQmsTestRules(params);
    }
    
	/**
	 * 获取品质检验工具
	 * @param params WERKS：工厂代码  TEST_TOOL_NO： 检具编码  TEST_TOOL_NAME：检具名称 pageSize:模糊匹配最大显示条数
	 * @return
	 */
    @RequestMapping("/getQmsTestToolList")
    public R getQmsTestToolList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getQmsTestToolList(params);
    }
    
    
	/**
	 * 根据图号获取图纸临时文件地址
	 * @param params material_no/MATERIAL_NO：图号
	 * @return
	 */
    @RequestMapping("/getMaterialNoMapFile")
    public R getMaterialNoMapFile(@RequestParam Map<String, Object> params){
    	String filePath = "";
    	
     	String material_no = params.get("material_no")==null?(params.get("MATERIAL_NO")==null?"":params.get("MATERIAL_NO").toString()):params.get("material_no").toString();
    	if(StringUtils.isEmpty(material_no)) {
    		return R.error("获取失败！请检查图纸编号是否正确！");
    	}
    	if(material_no.indexOf("-") <=0) {
    		return R.error("获取失败！请检查图纸编号是否正确！");
    	}
    	params.put("material_no", material_no);
    	R r = masterDataRemote.getMaterialNoMapFile(params);
		PdfStamper stamper = null;
		PdfReader pdfreader = null;
    	try {
    		String map_url = null;
			map_url = r.get("data")==null?"":r.get("data").toString();
			if(!StringUtils.isEmpty(map_url)){
				logger.info("-->map_url ： " + map_url);	//D8UR\D8UR-2813200P-电池包支架总成_002_A0_2019-6-10.pdf
				pdfreader = new PdfReader(ROMOTE_DESIGNPAPER_PLM + "/" + map_url);
				SimpleDateFormat df_v = new SimpleDateFormat("yyyyMMddHHmmss");
				String localFile = "DesignPdf" + df_v.format(new Date()) + ".pdf";
				File file2 = new File(fileLocation+"/pdmFiles");
				File[] fs2 = file2.listFiles();
				if (fs2 != null) {
					for (File f : fs2) { // 临时文件保留1分钟
						if (!f.isDirectory()) {
							if (f.getName().indexOf(df_v.format(new Date()).substring(0, 12)) < 0) {
								File file3 = new File(fileLocation+"/pdmFiles/" + f.getName());
								file3.delete();
							}
						}
					}
				}
				stamper = new PdfStamper(pdfreader, new FileOutputStream(fileLocation + "/pdmFiles/" + localFile));
				filePath = HttpContextUtils.getHttpServletRequest().getContextPath()+ "/upload/pdmFiles/" +localFile;;
			}else{
				// K9UB-5716712K K9UB-B-5716712K
				String bus_type = material_no.substring(0, material_no.lastIndexOf("-"));

				File file = new File(ROMOTE_DESIGNPAPER_WERKS + "/" + bus_type + "/"
						+ material_no.substring(material_no.lastIndexOf("-") + 1, material_no.indexOf("-") + 5));
				File[] fs = file.listFiles(); // 遍历path下的文件和目录，放在File数组中
				if (fs == null) {
					return R.error("获取失败！没找到车型" + material_no.substring(0, material_no.indexOf("-") + 5) + "的图纸！");
				}
				String fileName = "";
				int ver = 0;
				for (File f : fs) { // 遍历File[]数组 
					//logger.info("-->fileName ： " + f.getName());
					if (f.getName().indexOf(material_no+ "-") >= 0) {
						if (!f.isDirectory()) { // 若非目录(即文件)，则打印
							int cur_ver = Integer.valueOf(f.getName().replace(material_no + "-", "")
									.substring(f.getName().replace(material_no + "-", "").indexOf("-") + 1)
									.substring(0,
											f.getName().replace(material_no + "-", "")
													.substring(f.getName().replace(material_no + "-", "").indexOf("-") + 1)
													.indexOf("-"))
									.substring(1));
							if (cur_ver >= ver) {
								fileName = f.getName();
								ver = cur_ver;
							}
						}
					}
				}

				logger.info("-->PdfReader ： " + ROMOTE_DESIGNPAPER_WERKS + "/" + material_no.substring(0, material_no.indexOf("-")) + "/"
						+ material_no.substring(material_no.indexOf("-") + 1, material_no.indexOf("-") + 5) + "/" + fileName);
				pdfreader = new PdfReader(ROMOTE_DESIGNPAPER_WERKS + "/" + material_no.substring(0, material_no.indexOf("-")) + "/"
						+ material_no.substring(material_no.indexOf("-") + 1, material_no.indexOf("-") + 5) + "/" + fileName);
				SimpleDateFormat df_v = new SimpleDateFormat("yyyyMMddHHmmss");
				String localFile = "DesignPdf" + df_v.format(new Date()) + ".pdf";
				File file2 = new File(fileLocation+ "/pdmFiles/");
				File[] fs2 = file2.listFiles();
				if (fs2 != null) {
					for (File f : fs2) { // 临时文件保留1分钟
						if (!f.isDirectory()) {
							if (f.getName().indexOf(df_v.format(new Date()).substring(0, 12)) < 0) {
								File file3 = new File(fileLocation+ "/pdmFiles/" + f.getName());
								file3.delete();
							}
						}
					}
				}
				stamper = new PdfStamper(pdfreader, new FileOutputStream(fileLocation + "/pdmFiles/" + localFile));
				filePath = HttpContextUtils.getHttpServletRequest().getContextPath()+ "/upload/pdmFiles/" +localFile;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("获取失败！没找到的图纸！");
		}finally {
			
			try {
				if (null != stamper) {
					stamper.close();
				}
				if (pdfreader != null) {
					pdfreader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return R.error("获取失败！没找到的图纸！");
			}
		}
    	Map<String,Object> currentUser = userUtils.getUser();
    	Map<String,Object> rtnMap = new java.util.HashMap<String, Object>();
    	rtnMap.put("filePath", filePath);
    	rtnMap.put("username", currentUser.get("USERNAME"));
    	return R.ok().put("data", rtnMap);
    }
    
    /**
     * 图纸库检索
     */
    @RequestMapping("/pmdMapQuery")
    @ResponseBody
    public R pmdMapQuery(@RequestParam Map<String, Object> params){
    	return masterDataRemote.pmdMapQuery(params);
    }
    
	/**
	 *  图纸库导出
	 */
	@RequestMapping("/pmdMapExport")
    public ResponseEntity<byte[]> pmdMapExport(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) masterDataRemote.getPmdMapList(params).get("data");
		HttpHeaders header = new HttpHeaders();
		String filename = "pmdMapInfo.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		
		fieldTitleList.add("MAP_NO-图纸编号");
		fieldTitleList.add("MAP_NAME-图纸名称");
		fieldTitleList.add("MAP_SHEET-零部件名称");
		fieldTitleList.add("VERSION-版本");
		fieldTitleList.add("MAP_URL-图纸存储地址");
		fieldTitleList.add("EDIT_DATE-传输时间");
		fieldTitleList.add("STATUS-状态");
		fieldTitleList.add("PUBLISHER-图纸发布人");
		fieldTitleList.add("PUBLISH_DATE-发布时间");

		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
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
	 *  获取工厂、车间下所有标准工序信息
	 * @param params 
	 * @return
	 */
    @RequestMapping("/getWorkshopProcessList")
    public R getWorkshopProcessList(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getWorkshopProcessList(params);
    }
    /**
	 * 根据工厂代码、车间组获取所有小班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getTeamListByWorkshop")
    public R getTeamListByWorkshop(@RequestParam Map<String, Object> params){
    	return masterDataRemote.getTeamListByWorkshop(params);
    }
    
    @RequestMapping("/getMasterdataDictList")
	public  R getMasterdataDictList(@RequestParam Map<String, Object> params) {
		List<DictEntity> list = masterdataDictRemote.selectByMap(params);
		return R.ok().put("data", list);
	}

    @RequestMapping("/getProcessList")
	public  R getProcessList(@RequestParam Map<String, Object> params) {
		return masterDataRemote.getProcessList(params);
	}
}
