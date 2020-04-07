package com.byd.web.wms.qc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.web.wms.qc.entity.WmsQcInspectionItemEntity;

/**
 * 质检模块的页面跳转控制器
 * @author develop07
 *
 */
@Controller
@RequestMapping("qc/redirect")
public class WmsQcRedirectController {
	
	
	/**
	 * 跳转到单批质检页面
	 * @param batchId
	 * @param model
	 * @param type 质检类型  1.未质检  2.质检中& others
	 * @return
	 */
	@RequestMapping("/singlebatchinspection")
	public String toSingleBatchInspection(String inspectionNo,String inspectionItemNo,String type,String batch,String werks,Model model){
		model.addAttribute("inspectionNo", inspectionNo);
		model.addAttribute("inspectionItemNo", inspectionItemNo);
		model.addAttribute("type", type);
		model.addAttribute("batch",batch);
		model.addAttribute("werks",werks);
		return "wms/qc/wms_qc_quality_inspection_single_batch";
	}
	
	/**
	 * 来料质检之已质检改判
	 * @param id
	 * @return
	 */
	@RequestMapping("/inspectionrejudge")
	public String toInspectionReJudge(Long id,Model model){
		model.addAttribute("id", id);
		return "wms/qc/wms_qc_has_inspected_re_judge";
	}
	
	/**
	 * 定向到 来料质检-（质检中，未质检页面）
	 * @param params
	 * @return
	 */
	@RequestMapping("/redirecttoinspect")
	public String toInspection(@RequestParam Map<String,Object> params,Model model){
		model.addAttribute("params", params);
		return "wms/qc/wms_qc_quality_inspection";
	}
	
	/**
	 * 跳转到 已质检列表页面
	 * @return
	 */
	@RequestMapping("/hasinspectedlist")
	public String toHasInspectedList(@RequestParam Map<String,Object> params,Model model){
		model.addAttribute("params", params);
		return "wms/qc/wms_qc_has_inspected_list";
	}
	
	/**
	 * 跳转到 库存复检-未质检-单批质检页面
	 * @param inspectionNo
	 * @param inspectionItemNo
	 * @param type
	 * @param model
	 * @return
	 */
	@RequestMapping("/to_stock_rejudge_sbatch")
	public String toStockReJudgeSBatch(String inspectionNo,String inspectionItemNo,String type,String batch,Model model){
		model.addAttribute("inspectionNo", inspectionNo);
		model.addAttribute("inspectionItemNo", inspectionItemNo);
		model.addAttribute("type", type);
		model.addAttribute("batch", batch);
		return "wms/qc/wms_qc_stock_re_judge_inited_batch";
	
	}
	/**
	 * 跳到库存复检-质检中页面
	 * @param params
	 * @param model
	 * @return
	 */
	@RequestMapping("/to_stock_rejudge_on_inspect")
	public String toStockRejudgeOnInspect(@RequestParam Map<String,Object> params,Model model){
		if(params == null){
			params = new HashMap<String,Object>();
		}
		model.addAttribute("params", params);
		return "wms/qc/wms_qc_stock_re_judge_processing";
	}
	
	/**
	 * 跳转： 库存复检 未质检
	 * @param params
	 * @param model
	 * @return
	 */
	@RequestMapping("/to_stock_rejudge_not_inspect")
	public String toStockRejudgeNotInspect(@RequestParam Map<String,Object> params,Model model){
		model.addAttribute("params", params);
		return "wms/qc/wms_qc_stock_re_judge_inited";
	}
	
	@RequestMapping("/to_stoke_rejudge_on_inspect_batch")
	public String toStockRejudgeOnInspectBatch(String inspectionNo,String inspectionItemNo,String type,Model model){
		model.addAttribute("inspectionNo", inspectionNo);
		model.addAttribute("inspectionItemNo", inspectionItemNo);
		model.addAttribute("type", type);
		return "wms/qc/wms_qc_stock_re_judge_processing_batch";
	}
	
	@RequestMapping("/to_qc_query_inspection_item")
	public String toQcQueryInspectionItem(String inspectionNo,Model model ){
		model.addAttribute("inspectionNo", inspectionNo);
		return "wms/qc/wms_qc_query_inspection_item";
	}
	
	
	/**
	 * 打印质检单页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/printInspectionItem")
	public String	printInspectionItem(String inspectionNo,Model model){
		List<WmsQcInspectionItemEntity> items = null;//itemService.selectList(new EntityWrapper<WmsQcInspectionItemEntity>().eq("INSPECTION_NO", inspectionNo));
		model.addAttribute("items", items);
		model.addAttribute("head", items.get(0));
		return "wms/qc/wms_qc_query_inspection_item_pdf";
	}
}
