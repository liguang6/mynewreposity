package com.byd.wms.business.modules.qc.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionItemService;



/**
 * 送检单明细
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcinspectionitem")
public class InspectionItemController {
    @Autowired
    private WmsQcInspectionItemService wmsQcInspectionItemService;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 来料质检 - 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){     
    	Map<String,Object> user = userUtils.getUser();
    	params.put("FULL_NAME", user.get("FULL_NAME"));
    	params.put("USERNAME", user.get("USERNAME"));
        PageUtils page = wmsQcInspectionItemService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 查询来料质检任务清单
     */
    @RequestMapping("/getInspectionItemTask")
    public R getInspectionItemTask(@RequestParam Map<String, Object> params){     
    	Map<String,Object> user = userUtils.getUser();
    	params.put("FULL_NAME", user.get("FULL_NAME"));
    	params.put("USERNAME", user.get("USERNAME"));
    	
    	List<Map<String,Object>> userTask = wmsQcInspectionItemService.getInspectionItemTask(params);
    	
        return R.ok().put("data", userTask);
    }
    
    /**
     * 来料质检之已质检查询
     * @param params
     * @return
     */
    @RequestMapping("/listHasInspected")
    public R listHasInspected(@RequestParam Map<String, Object> params){
    	PageUtils page =  wmsQcInspectionItemService.queryHasInspectedListWithPage(params);
    	return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsQcInspectionItemEntity wmsQcInspectionItem = wmsQcInspectionItemService.selectById(id);

        return R.ok().put("wmsQcInspectionItem", wmsQcInspectionItem);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsQcInspectionItemEntity wmsQcInspectionItem){
        wmsQcInspectionItemService.insert(wmsQcInspectionItem);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsQcInspectionItemEntity wmsQcInspectionItem){
        ValidatorUtils.validateEntity(wmsQcInspectionItem);
        wmsQcInspectionItemService.updateAllColumnById(wmsQcInspectionItem);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsQcInspectionItemService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    
    /**
     * 创建复检单 - 查询
     * @param params
     * @return
     */
    @RequestMapping("/stokerejudgeitems")
    public R queryStockReJudgeItems(@RequestParam Map<String,Object> params){
    	PageUtils page =  wmsQcInspectionItemService.queryStockReJudgeItemsWithPage(params);
    	return R.ok().put("page", page);
    }

    /**
     * 库存复检-未质检
     * @param params
     * @return
     */
    @RequestMapping("/stock_rejudge_not_inspected")
    public R selectStockReJudgeNotInspected(@RequestParam Map<String,Object> params){
    	PageUtils page = wmsQcInspectionItemService.selectStockReJudgeNotInspected(params);
    	return R.ok().put("page", page);
    }
    
    /**
     * 库存复检-质检中
     * @param params
     * @return
     */
    @RequestMapping("/stoke_rejuge_on_inspect")
    public R selectStockRejudgeOnInspect(@RequestParam Map<String,Object> params){
    	List<Map<String,Object>> list = wmsQcInspectionItemService.selectStockRejudgeOnInspect(params);
    	return R.ok().put("list", list);
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
    	PageUtils page =  wmsQcInspectionItemService.selectStockReJudgeNotInspected(params);
    	page.getList();
    	//..
    	return ExcelWriter.generateBytesResponse(entitys, filedTitleMap);
    }
}
