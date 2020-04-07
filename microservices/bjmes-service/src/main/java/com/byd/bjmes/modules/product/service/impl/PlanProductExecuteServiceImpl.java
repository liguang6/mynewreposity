package com.byd.bjmes.modules.product.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.alibaba.fastjson.JSONObject;
import com.byd.bjmes.modules.product.dao.PlanProductExecuteDao;
import com.byd.bjmes.modules.product.service.PlanProductExecuteService;
import com.byd.utils.DateUtils;
import com.byd.utils.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ppExecuteService")
public class PlanProductExecuteServiceImpl implements PlanProductExecuteService {
	@Autowired
	PlanProductExecuteDao ppExecuteDao;

	@Override
	public R getOrderProducts(Map<String, Object> params) {
		R r = new R();
		List<Map<String, Object>> datalist = ppExecuteDao.getOrderProducts(params);
		return r.ok().put("data", datalist);
	}

	@Override
	public List<Map<String, Object>> getProductPlanList(Map<String, Object> params) {
		List<Map<String, Object>> datalist = ppExecuteDao.getProductPlanList(params);
		int i = 1;
		for (Map<String, Object> m : datalist) {
			m.put("id", i++);
		}
		return datalist;
	}

	@Override
	public R saveProductPlan(Map<String, Object> params) {
		R r = new R();
		String recordListStr = params.get("dataList").toString();
		List<Map<String, Object>> update_list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> add_list = new ArrayList<Map<String, Object>>();
		try {
			List<Map> item_list = JSONObject.parseArray(recordListStr, Map.class);
			item_list.forEach(m -> {
				m.put("creator", params.get("creator"));
				m.put("create_date", params.get("create_date"));
				if (m.get("plan_id") != null && !"".equals(m.get("plan_id"))) {
					update_list.add(m);
				} else {
					add_list.add(m);
				}
			});

			if (update_list.size() > 0) {
				ppExecuteDao.updateProductPlan(update_list);
			}

			if (add_list.size() > 0) {
				ppExecuteDao.saveProductPlan(add_list);
			}

			r = R.ok().put("msg", "保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			r = R.error().put("msg", "保存失败！" + e.getMessage());
		}

		return r;
	}

	@Override
	public R getScanProductInfo(Map<String, Object> params) {
		R r = new R();
		Map<String, Object> to_scan_node = null;
		Map<String, Object> product_info = ppExecuteDao.getProductInfo(params);
		if (product_info == null || product_info.get("product_code") == null
				|| product_info.get("product_code").equals("")) {
			return R.error("未找到该产品信息，请检查产品编号是否正确！");
		}
		/**
		 * 获取扫描流程信息
		 */
		List<Map<String, Object>> process_flow = ppExecuteDao.getProcessFlow(params);
		if (process_flow == null || process_flow.size() == 0) {
			return R.error("未配置加工流程！");
		}
		/**
		 * 获取产品下一个扫描节点信息
		 */
		Map<String, Object> last_scan_info = ppExecuteDao.getLastScanInfo(params);
		if (last_scan_info == null) {// 无扫描记录
			to_scan_node = process_flow.get(0);
			to_scan_node.put("scan_type", "online");
		} else if ("online".equals(last_scan_info.get("scan_type"))) {// 扫了上线未扫下线，待扫描节点为当前节点下线
			int i = 0;
			for (Map<String, Object> m : process_flow) {
				if (m.get("node_code").equals(last_scan_info.get("node_code"))) {
					break;
				}
				i++;
			}
			to_scan_node = process_flow.get(i);
			to_scan_node.put("scan_type", "offline");
			to_scan_node.put("line", last_scan_info.get("line"));
		} else if ("offline".equals(last_scan_info.get("scan_type"))) {// 扫了下线，待扫描节点为流程中下一节点上线
			int i = 0;
			for (Map<String, Object> m : process_flow) {
				if (m.get("node_code").equals(last_scan_info.get("node_code"))) {
					break;
				}
				i++;
			}
			if (i + 1 == process_flow.size()) {
				return R.error("该产品已生产完毕！");
			}
			to_scan_node = process_flow.get(i + 1);
			to_scan_node.put("scan_type", "online");

		}
		/**
		 * 获取产品组件列表信息
		 */
		params.put("parent_process_code", to_scan_node.get("node_code"));
		List<Map<String, Object>> subParts = ppExecuteDao.getProdSubParts(params);

		r.put("scan_node", to_scan_node);
		r.put("subParts", subParts);
		r.put("last_scan_info", last_scan_info);
		r.put("product_info", product_info);
		return r;
	}

	@Override
	public R getPDProductList(Map<String, Object> params) {

		return R.ok().put("data", ppExecuteDao.getPDProductList(params));
	}

	@Override
	@Transactional
	public R saveProductScan(Map<String, Object> params) {
		R r = new R();
		String edit_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		String scan_info = params.get("scan_info").toString();
		String subParts = params.get("subParts") == null ? "" : params.get("subParts").toString();
		String product_no = params.get("product_no").toString();
		String product_code = params.get("product_code").toString();
		String order_no = params.get("order_no").toString();

		try {
			List<Map> subParts_list = new ArrayList<Map>();
			if (!"".equals(subParts) && subParts.startsWith("[")) {
				subParts_list = JSONObject.parseArray(subParts, Map.class);
			}

			Map _scan_info = JSONObject.parseObject(scan_info);
			_scan_info.put("scan_time", edit_date);
			_scan_info.put("order_no", order_no);
			_scan_info.put("product_no", product_no);

			/**
			 * 保存扫描记录BJMES_PD_SCAN
			 */
			ppExecuteDao.savePDScanInfo(_scan_info);
			/**
			 * 保存keyparts信息BJMES_PD_KEY_PARTS
			 */
			subParts_list.stream().forEach(m -> {
				m.put("scan_time", edit_date);
				m.put("order_no", order_no);
				m.put("product_no", product_no);
			});
			if (subParts_list != null && subParts_list.size() > 0) {
				ppExecuteDao.savePDKey_Parts(subParts_list);
			}

			/**
			 * 更新BJMES_PD_PRODUCTS表online_date、offline_date、latest_node_code
			 */
			ppExecuteDao.savePDProductsInfo(_scan_info);
			r = R.ok("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			r = R.error("保存失败，" + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

		return r;
	}

	/**
	 * 根据组件编号获取产品信息，判断是否已经绑定了产品，校验订单是否和产品一致
	 */
	@Override
	public R getProductByKeyparts(Map<String, Object> params) {

		String order_no = params.get("order_no").toString();
		List<Map<String, Object>> pd_list = ppExecuteDao.getProductByKeyparts(params);
		if (pd_list == null || pd_list.size() == 0) {
			return R.error("抱歉，未查询到对应组件信息，请确认输入的编号是否有效！");
		}
		if (!order_no.equals(pd_list.get(0).get("order_no"))) {// 不属于同一订单
			return R.error("抱歉，该组件不属于订单：" + order_no);
		} else if (pd_list.get(0).get("product_no") == null || "".equals(pd_list.get(0).get("product_no"))) {// 未绑定产品
			return R.ok();
		} else if (!params.get("product_no").equals(pd_list.get(0).get("product_no"))) {
			return R.error("抱歉，该组件已绑定了产品：" + pd_list.get(0).get("product_no"));
		} else {
			return R.ok().put("product_info", pd_list);
		}

	}

	@Override
	public R getProductPDList(Map<String, Object> params) {
		List<Map<String,Object>> datalist = ppExecuteDao.getProductPDList(params);
		/**
		 * 获取
		 */
		return R.ok().put("data", datalist);
	}

	
	
}
