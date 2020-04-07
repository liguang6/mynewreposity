package com.byd.web.wms.qc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.ExcelWriter;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.qc.entity.WmsQcInspectionItemEntity;
import com.byd.web.wms.qc.service.WmsQcServiceRemote;

/**
 * 送检单明细
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcinspectionitem")
public class WmsQcInspectionItemController {
   

	/**
	 * wms-business 服务
	 */
	@Autowired
	WmsQcServiceRemote qcServiceRemote;
	
	@Autowired
	UserUtils userUtils;
	
    /**
     * 来料质检 - 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	Map<String,Object> user =  userUtils.getUser();
    	String FULL_NAME = (String) user.get("FULL_NAME");
    	params.put("FULL_NAME", FULL_NAME);
        return qcServiceRemote.listInspectionItem(params);
    }
    
    /**
     * 来料质检之已质检查询
     * @param params
     * @return
     */
    @RequestMapping("/listHasInspected")
    public R listHasInspected(@RequestParam Map<String, Object> params){
    	return qcServiceRemote.listHasInspected(params);
    }
    
    @RequestMapping("/getInspectionItemTask")
    public R getInspectionItemTask(@RequestParam Map<String, Object> params){
    	return qcServiceRemote.getInspectionItemTask(params);
    }
    

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return qcServiceRemote.infoInspectionItem(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsQcInspectionItemEntity wmsQcInspectionItem){
    	return qcServiceRemote.save(wmsQcInspectionItem);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsQcInspectionItemEntity wmsQcInspectionItem){
    	return qcServiceRemote.update(wmsQcInspectionItem);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        return qcServiceRemote.deleteInspectionItem(ids);
    }
    
    /**
     * 创建复检单 - 查询
     * @param params
     * @return
     */
    @RequestMapping("/stokerejudgeitems")
    public R queryStockReJudgeItems(@RequestParam Map<String,Object> params){
    	 return qcServiceRemote.queryStockReJudgeItems(params);
    }

    /**
     * 库存复检-未质检
     * @param params
     * @return
     */
    @RequestMapping("/stock_rejudge_not_inspected")
    public R selectStockReJudgeNotInspected(@RequestParam Map<String,Object> params){
    	 return qcServiceRemote.selectStockReJudgeNotInspected(params);
    }
    
    /**
     * 库存复检-质检中
     * @param params
     * @return
     */
    @RequestMapping("/stoke_rejuge_on_inspect")
    public R selectStockRejudgeOnInspect(@RequestParam Map<String,Object> params){
    	 return qcServiceRemote.selectStockRejudgeOnInspect(params);
    }
    
    /**
     * 导出库存质检未质检数据
     * @param params
     * @return 字节流
     * @throws Exception 
     */
    @RequestMapping("/export_excel_stock_rejudge_not_inspect")
    public ResponseEntity<byte[]> exportExcelStockRejudgeNotInspect(@RequestParam Map<String,Object> params) throws Exception{
    	Map<String,String> filedTitleMap = new HashMap<String,String>();
    	List<WmsQcInspectionItemEntity> entitys = new ArrayList<WmsQcInspectionItemEntity>();
    	PageUtils page =  (PageUtils) qcServiceRemote.selectStockReJudgeNotInspected(params).get("page");//wmsQcInspectionItemService.selectStockReJudgeNotInspected(params);
    	List<Map<String,Object>> records = (List<Map<String, Object>>) page.getList();
    	return ExcelWriter.generateBytesResponse(entitys, filedTitleMap);
    }
}
