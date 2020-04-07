package com.byd.wms.business.modules.job;

import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.in.service.WmsInAutoPutawayService;
import com.byd.wms.business.modules.kn.service.WmsKnFreezeRecordService;
import com.byd.wms.business.modules.report.service.EngineMaterialService;
import com.byd.wms.business.modules.report.service.InAndOutStockQtyByDayService;
import com.byd.wms.business.modules.timejob.service.EmailSendService;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("wmsJobs")
public class WmsJobs {
	//private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	WmsSapRemote wmsSapRemote;
	@Autowired
	CommonService commonService;
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private WmsKnFreezeRecordService wmsKnFreezeRecordService;
	@Autowired
	private WmsInAutoPutawayService wmsInAutoPutawayService;
	
	@Autowired
	private EngineMaterialService engineMaterialService;

	@Autowired
	private EmailSendService emailSendService;
	
	@RequestMapping("/sapMaterialSync.task")
	public void sapMaterialSync(@RequestParam(value = "params") String params) {
		wmsSapRemote.getSapMaterialSync(params);
	}
	
	@RequestMapping("/sapVendorSync.task")
	public void sapVendorSync(@RequestParam(value = "params") String params) {
		wmsSapRemote.getSapVendorSync(params);
	}
	
	@RequestMapping("/sapCustomerSync.task")
	public void sapCustomerSync(@RequestParam(value = "params") String params) {
		wmsSapRemote.getSapCustomerSync(params);
	}
	
	@RequestMapping("/sapMoSync.task")
	public void sapMoSync(@RequestParam(value = "params") String params) {
		wmsSapRemote.getSapMoSync(params);
	}
	
	@RequestMapping("/sapPOSync.task")
	public void sapPOSync(@RequestParam(value = "params") String params) {
		wmsSapRemote.getSapPOSync(params);
	}
	
	@RequestMapping("/sapPostJobSync.task")
	public void sapPostJobSync() {
		commonService.sapPostJobSync();
	}
	
	/**
	 * 加载系统所有仓库信息
	 */
	@RequestMapping("/getAllWhSync.task")
	public void getAllWhJobSync() {
		//获取所有仓库号
		List<Map<String, Object>> allWh = commonService.getWhList("", "");
		//更新redis值
		redisUtils.set(RedisKeys.getAllWhKey(), allWh,RedisUtils.NOT_EXPIRE);
	}
	
	/**
	 * WMS过账成功，未及时返回SAP凭证的，定时任务再次获取
	 */
	@RequestMapping("/saveSAPMatDoc.task")
	public void saveSAPMatDoc() {
		wmsInAutoPutawayService.saveSAPMatDoc("1");
	}
	
	/**
	 * 供应商比对定时任务
	 * added by xjw
	 */
	@RequestMapping("/vendorCompareSync.task")
	public void vendorCompareSync(@RequestParam(value = "params") String params) {
		int days = 0;
		int recordNum = 0;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
			if(plist[i].indexOf("recordNum")>=0) {
				recordNum = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dd = new Date();
		String DATE_TO = df.format(dd);
		
		Calendar rightNow = Calendar.getInstance();
		int fdays=0-days;//往前的天数
		rightNow.add(Calendar.DAY_OF_YEAR, fdays);
		String DATE_FROM = df.format(rightNow.getTime());
		
		System.out.println("vendorCompareSync task days:"+days);
		
		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("date_from", DATE_FROM);
		condMap.put("create_time", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));		
		
		commonService.saveUnSyncVendor(condMap);
		
	}
	/**
	 * 物料主数据比对定时任务
	 * @param params
	 */
	@RequestMapping("/matCompareSync.task")
	public void matCompareSync(@RequestParam(value = "params") String params) {
		int days = 0;
		int recordNum = 0;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
			if(plist[i].indexOf("recordNum")>=0) {
				recordNum = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dd = new Date();
		String DATE_TO = df.format(dd);
		
		Calendar rightNow = Calendar.getInstance();
		int fdays=0-days;//往前的天数
		rightNow.add(Calendar.DAY_OF_YEAR, fdays);
		String DATE_FROM = df.format(rightNow.getTime());
		
		System.out.println("matCompareSync task days:"+days);
		
		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("date_from", DATE_FROM);
		condMap.put("create_time", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));		
		
		commonService.saveUnSyncMat(condMap);
	}

	@Autowired
	private InAndOutStockQtyByDayService inAndOutStockQtyByDayService;
	/**
	 *
	 *出入库库存日报表
	 */
	@RequestMapping("/inOutStockQtyReport.task")
	public void inOutStockQtyReport() {
		System.err.println("进入出入库库存日报表service");
		inAndOutStockQtyByDayService.insertWmsReportInoutStock();
	}
	
    //发动机物料报表定时任务
	@RequestMapping("/saveProject.task")
	public void saveProject() {
		engineMaterialService.saveProject();
	}

	@RequestMapping("/saveStock.task")
	public void saveStock() {
		engineMaterialService.saveStock();
	}
	
	@RequestMapping("/stockClear.task")
	public void stockClear(@RequestParam(value = "params") String params) {
		int days = 0;
		String[] plist = params.split(",");
		for (int i=0;i<plist.length;i++) {
			if(plist[i].indexOf("days")>=0) {
				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar rightNow = Calendar.getInstance();
		int fdays=0-days;//往前的天数
		rightNow.add(Calendar.DAY_OF_YEAR, fdays);
		String DATE_FROM = df.format(rightNow.getTime());
		
		Map<String,Object> condMap = new HashMap<String,Object>();
		condMap.put("EDIT_DATE", DATE_FROM);
		commonService.stockClear(condMap);
	}

	@RequestMapping("/sendEmail.task")
	public void sendEmail() {
		emailSendService.sendEmail01();
		emailSendService.sendEmail02();
		emailSendService.sendEmail03();
		emailSendService.sendEmail04();
	}
	//过期的库存物料冻结
		@RequestMapping("/freeze.task")
		public void freeze() {		
			System.err.println("冻结过期物料");
			wmsKnFreezeRecordService.freezeMatList();		
		}
}
