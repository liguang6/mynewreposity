package com.byd.zzjmes.modules.product.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.product.dao.ZzjJTOperationDao;
import com.byd.zzjmes.modules.product.service.ZzjJTOperationService;

@Service("operationService")
public class ZzjJTOperationServiceImpl implements ZzjJTOperationService {
	@Autowired
	ZzjJTOperationDao zzjJTOperationDao;
	@Autowired
	private UserUtils userUtils;

	@Override
	public List<Map<String, Object>> getJTPlan(Map<String, Object> params) {

		return zzjJTOperationDao.getJTPlanList(params);
	}

	@Override
	public R getPmdItems(Map<String, Object> params) {
		R r = new R();
		List<Map<String, Object>> data = zzjJTOperationDao.getPmdItems(params);
		r.put("data", data);
		data = data.stream().filter(m -> {
			return m.get("zzj_pmd_items_id") != null && !"".equals(m.get("zzj_pmd_items_id"));
		}).collect(Collectors.toList());

		if (data == null || data.size() == 0) {
			r.put("msg", "未找到有效零部件信息,请确认是否导入下料明细！");
		} else {
			// 根据下料明细id和机台查询是否存在机台计划
			List<String> plan_list = zzjJTOperationDao.getJTPlanListByZzj(data);

			data = data.stream().filter(m -> {
				return !plan_list.contains(m.get("zzj_pmd_items_id").toString());
			}).collect(Collectors.toList());

			if (data == null || data.size() == 0) {
				r.put("msg", "零部件" + params.get("zzj_no") + "已在机台" + params.get("machine") + "绑定计划!");

			}
		}

		return r;

	}

	@Override
	public R getMachineAchieve(Map<String, Object> params) {
		Map<String, Object> data = zzjJTOperationDao.getMachineAchieve(params);
		return R.ok().put("data", data);
	}

	@Override
	@Transactional
	public R saveJTBindData(Map<String, Object> params) {
		R r = new R();
		String recordListStr = params.get("dataList").toString();
		try {
			List<Map> item_list = JSONObject.parseArray(recordListStr, Map.class);
			String create_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
			Map<String, Object> u = userUtils.getUser();
			item_list.forEach(m -> {
				m.put("creator", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
				m.put("create_date", create_date);
				m.put("operate_type", "02"); // 操作类型 :01 开始加工 02 机台绑定 03 产量录入 04车间内交接 05生产自检 06 品质检验 07 委外加工
			});

			List<Map> head_list = item_list.stream().collect(Collectors.collectingAndThen(
					Collectors.toCollection(
							() -> new TreeSet<>(Comparator.comparing(m -> m.get("order_no") + ";" + m.get("werks") + ";"
									+ m.get("workshop") + ";" + m.get("line") + ";" + m.get("zzj_plan_batch")))),
					ArrayList::new));

			System.out.println(head_list.size());
			/**
			 * 用head_list查询 机台计划头表中是否存在记录,存在移除
			 */
			List<String> plan_head_list = zzjJTOperationDao.getJTPlanHeadList(head_list);
			head_list = head_list.stream()
					.filter(m -> !plan_head_list.contains((m.get("order_no") + ";" + m.get("werks") + ";"
							+ m.get("workshop") + ";" + m.get("line") + ";" + m.get("zzj_plan_batch"))))
					.collect(Collectors.toList());

			if (head_list.size() > 0) {
				zzjJTOperationDao.saveJTPlanHead(head_list);
			}

			zzjJTOperationDao.saveJTPlanItems(item_list);

			/**
			 * 保存零部件履历信息 ZZJ_PMD_OPERATE_RECORD
			 */
			zzjJTOperationDao.saveZzjOperRecord(item_list);

			r = R.ok("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			r = R.ok("保存失败！" + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

		return r;
	}

	@Override
	public R getPmdOutputItems(Map<String, Object> params) {
		List<Map<String, Object>> data = zzjJTOperationDao.getPmdItems(params);
		data = data.stream().filter(m -> {
			return m.get("zzj_pmd_items_id") != null && !"".equals(m.get("zzj_pmd_items_id"));
		}).collect(Collectors.toList());

		if (data == null || data.size() == 0) {
			return R.error().put("msg", "未绑定机台" + params.get("machine") + "或未找到有效零部件信息！");
		} else {
			// 计划外工序无需校验计划
			if (!"02".equals(params.get("process_type"))) {
				// 根据下料明细id和机台查询是否存在机台计划
				List<String> plan_list = zzjJTOperationDao.getJTPlanListByZzj(data);
				data = data.stream().filter(m -> {
					return plan_list.contains(m.get("zzj_pmd_items_id").toString());
				}).collect(Collectors.toList());

				if (data == null || data.size() == 0) {
					return R.error().put("msg", "零部件" + params.get("zzj_no") + "未在机台：" + params.get("machine") + " 工序："
							+ params.get("process_name") + "绑定计划!");
				}
			}

			// 查询上一工序是否有录入产量，未录入：本工序不允许录入产量
			// List<String> last_out_list = zzjJTOperationDao.getJTOutputListByZzj(data);
			/**
			 * 筛选上工序已经录入产量的数据,没有上工序的不校验
			 * 
			 */
			data = data.stream().filter(m -> {
				String last_process = m.get("last_process") == null ? "" : m.get("last_process").toString(); // 上工序
																												// ：为空表示没有上工序
				// return last_out_list.contains(m.get("zzj_pmd_items_id").toString()) ||
				// "".equals(last_process) ;
				double last_prod_quantity = m.get("last_prod_quantity") == null ? 0
						: Double.parseDouble(m.get("last_prod_quantity").toString());
				return last_prod_quantity > 0 || "".equals(last_process);
			}).collect(Collectors.toList());

			if (data == null || data.size() == 0) {
				return R.error().put("msg", "零部件" + params.get("zzj_no") + "在上一工序未录入产量!");
			}
		}

		return R.ok().put("data", data);
	}

	@Override
	@Transactional
	public R saveJTOutputData(Map<String, Object> params) {
		String recordListStr = params.get("dataList").toString();
		String process_type = params.get("process_type") == null ? "" : params.get("process_type").toString();
		R r = new R();
		try {
			List<Map> item_list = JSONObject.parseArray(recordListStr, Map.class);
			String create_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
			Map<String, Object> u = userUtils.getUser();
			item_list.forEach(m -> {
				m.put("creator", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
				m.put("create_date", create_date);
				m.put("output_type", "1"); // 产量录入类型 1 新增 2 报废
				m.put("operate_type", "03"); // 操作类型 :01 开始加工 02 机台绑定 03 产量录入 04车间内交接 05生产自检 06 品质检验 07 委外加工
			});

			/**
			 * 计划外工序插入虚拟机台计划 ZZJ_MACHINE_PLAN_ITEMS
			 */
			if ("02".equals(process_type)) {
				zzjJTOperationDao.insertVisualMachinePlan(item_list);
				item_list.forEach(m -> {
					m.put("plan_item_id", m.get("machine_plan_items_id"));
					m.put("status", "3");
				});
			}

			// 保存ZZJ_OUTPUT表数据
			zzjJTOperationDao.saveZzjOutputData(item_list);

			/**
			 * 更新ZZJ_MACHINE_PLAN_ITEMS表累计产量：prod_quantity ，计件产量：piece_quantity，状态;
			 * 计划外工序不需要更新计划
			 */
			if (!"02".equals(process_type)) {
				zzjJTOperationDao.updateMachinePlanOutput(item_list);
			}

			/**
			 * 比对KEY列表，判断是否存在当前工序记录数据,order_no;werks;workshop;line;zzj_plan_batch;zzj_pmd_items_id
			 * 存在记录：更新该记录的当前工序 不存在记录：插入一行记录
			 */
			List<Map> cp_update_list = new ArrayList<Map>();
			List<Map> cp_new_list = new ArrayList<Map>();

			List<String> key_list = zzjJTOperationDao.getCurrentProcessList(item_list);
			cp_update_list = item_list.stream().filter(m -> {
				String _key = m.get("order_no") + ";" + m.get("werks") + ";" + m.get("workshop") + ";" + m.get("line")
						+ ";" + m.get("zzj_plan_batch") + ";" + m.get("zzj_pmd_items_id");
				System.out.println("comapre key :" + _key);
				return key_list.contains(_key);
			}).collect(Collectors.toList());
			cp_new_list = item_list.stream()
					.filter(m -> !key_list.contains((m.get("order_no") + ";" + m.get("werks") + ";" + m.get("workshop")
							+ ";" + m.get("line") + ";" + m.get("zzj_plan_batch") + ";" + m.get("zzj_pmd_items_id"))))
					.collect(Collectors.toList());

			// 更新ZZJ_PMD_CURRENT_PROCESS表中当前工序current_process
			if (cp_new_list.size() > 0) {
				zzjJTOperationDao.saveZzjCurrentProcess(cp_new_list);
			}
			if (cp_update_list.size() > 0) {
				zzjJTOperationDao.updateZzjCurrentProcess(cp_update_list);
			}

			/**
			 * 保存零部件履历信息 ZZJ_PMD_OPERATE_RECORD
			 */
			zzjJTOperationDao.saveZzjOperRecord(item_list);

			r = R.ok("保存成功！");
		} catch (Exception e) {
			r = R.error("保存失败! " + e.getMessage());
			throw new RuntimeException("保存失败:" + e.getMessage());
		}

		return r;
	}

	@Override
	public R startOpera(Map<String, Object> params) {
		String recordListStr = params.get("dataList").toString();
		List<Map> item_list = JSONObject.parseArray(recordListStr, Map.class);
		String create_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		Map<String, Object> u = userUtils.getUser();
		item_list.forEach(m -> {
			m.put("creator", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
			m.put("create_date", create_date);
			m.put("operate_type", "01"); // 操作类型 :01 开始加工 02 机台绑定 03 产量录入 04车间内交接 05生产自检 06 品质检验 07 委外加工
		});
		/**
		 * 保存零部件履历信息 ZZJ_PMD_OPERATE_RECORD
		 */
		zzjJTOperationDao.saveZzjOperRecord(item_list);

		return R.ok("操作成功！");
	}

	@Override
	public PageUtils queryOutputRecords(Map<String, Object> params) {
		int totalCount = zzjJTOperationDao.queryOutputRecordsCount(params);
		Page page = new Query<Map>(params).getPage();
		int pageSize = params.get("pageSize") == null ? 15 : Integer.parseInt((String) params.get("pageSize"));
		int pageNo = params.get("pageNo") == null ? 1 : Integer.parseInt((String) params.get("pageNo"));
		int start = (pageNo - 1) * pageSize;
		int length = pageSize;
		params.put("start", start);
		params.put("length", length);

		List<Map<String, Object>> records = zzjJTOperationDao.queryOutputRecords(params);
		page.setRecords(records);
		page.setTotal(totalCount);
		page.setSize(pageSize);

		return new PageUtils(page);
	}

	/**
	 * 查询零部件某工序下可录入数量 数量<=计划数量-累计产量 且 <=上一道工序（可以通过下料明细ID关联取工艺流程判断）的累计产量
	 * 如果上一道工序有多道工序时，不判断；
	 */
	@Override
	public R getPmdAbleQty(Map<String, Object> params) {
		int last_prod_qty = 0;
		int prod_qty = 0;
		int plan_qty = 0;
		int able_qty = 0;
		Map<String, Object> out_info = zzjJTOperationDao.getPmdPlanOutInfo(params);
		plan_qty = Integer.parseInt(out_info.get("plan_quantity").toString());
		prod_qty = Integer.parseInt(out_info.get("prod_quantity").toString());
		last_prod_qty = Integer.parseInt(out_info.get("last_prod_qty").toString());
		int next_prod_qty = Integer.parseInt(out_info.get("next_prod_qty").toString());
		int old_prod_qty = Integer.parseInt(params.get("prod_quantity").toString());
		int quantity = Integer.parseInt(params.get("quantity").toString());
		/**
		 * 存在上工序，产量不能超过上工序产量
		 */

		if (params.get("last_process") != null && !"".equals(params.get("last_process").toString())) {
			able_qty = last_prod_qty;
			if (able_qty < quantity) {
				return R.error("不能超出上工序（" + params.get("last_process") + ")已录入产量：" + able_qty);
			}
		}

		/**
		 * 存在下工序，产量不能小于下工序产量
		 */
		if (params.get("next_process") != null && !"".equals(params.get("next_process").toString())) {
			able_qty = next_prod_qty;
			if (able_qty > quantity) {
				return R.error("不能小于下工序（" + params.get("next_process") + ")已录入产量：" + able_qty);
			}
		}

		able_qty = plan_qty - prod_qty + old_prod_qty;
		if (able_qty < quantity) {
			return R.error("不能超出剩余可录入产量：" + able_qty);
		} else
			return R.ok().put("data", able_qty);

		/**
		 * if(params.get("last_process")==null||"".equals(params.get("last_process").toString()))
		 * {//无上工序，不校验上工序产量 able_qty=plan_qty-prod_qty+old_prod_qty; }else {
		 * able_qty=Math.min(last_prod_qty, plan_qty-prod_qty+old_prod_qty); }
		 * 
		 * return R.ok().put("data", able_qty);
		 **/
	}

	/**
	 * 保存修改后的产量数据，ZZJ_OUTPUT、ZZJ_MACHINE_PLAN_ITEMS 更新机台计划状态：小于计划数量时
	 * 更新为生产中，=计划数量时更新为已完成，=0 时更新为‘已锁定’
	 * 更新批次零部件当前工序(ZZJ_PMD_CURRENT_PROCESS)：累计产量=0时需要更新为上一道工序
	 * 
	 */
	@Override
	@Transactional
	public R savePmdOutQty(Map<String, Object> params) {
		R r = new R();
		String edit_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		Map<String, Object> u = userUtils.getUser();
		params.put("editor", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
		params.put("edit_date", edit_date);

		try {
			zzjJTOperationDao.updatePmdOutput(params);

			zzjJTOperationDao.updateMachinePlanOutputByItem(params);

			zzjJTOperationDao.updateZzjCurrentProcessByItem(params);

			r = R.ok("修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			r = R.error("修改失败！" + e.getMessage());
			throw new RuntimeException(e);
		}

		return r;
	}

	/**
	 * 删除产量记录，更新机台计划状态：小于计划数量时 更新为生产中，=计划数量时更新为已完成，=0 时更新为‘已锁定’
	 * 更新批次零部件当前工序(ZZJ_PMD_CURRENT_PROCESS)：累计产量=0时需要更新为上一道工序 后工序存在产量录入数据时，不允许删除
	 */
	@Override
	public R deletePmdOutInfo(Map<String, Object> params) {
		R r = new R();
		String edit_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		Map<String, Object> u = userUtils.getUser();
		params.put("editor", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
		params.put("edit_date", edit_date);
		Map<String, Object> out_info = zzjJTOperationDao.getPmdPlanOutInfo(params);
		int next_prod_qty = Integer.parseInt(out_info.get("next_prod_qty").toString());
		if (next_prod_qty > 0) {
			return R.error("下工序：" + params.get("next_process") + "存在产量记录，不允许删除！");
		}

		try {
			zzjJTOperationDao.deletePmdOutput(params);

			if ("产量新增".equals(params.get("output_type"))) {
				params.put("quantity", 0);// 删除产量录入记录，产量记为0
			}
			if ("产量报废".equals(params.get("output_type"))) {
				params.put("prod_quantity", 0);// 删除产量报废记录，报废数量记为0
			}

			zzjJTOperationDao.updateMachinePlanOutputByItem(params);

			zzjJTOperationDao.updateZzjCurrentProcessByItem(params);

			r = R.ok("删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			r = R.error("删除失败！" + e.getMessage());
			throw new RuntimeException(e);
		}

		return r;
	}

	/**
	 * 新增报废记录 更新机台计划状态：小于计划数量时 更新为生产中，=计划数量时更新为已完成，=0 时更新为‘已锁定’
	 * 更新批次零部件当前工序(ZZJ_PMD_CURRENT_PROCESS)：累计产量=0时需要更新为上一道工序
	 */
	@Override
	@Transactional
	public R scrapePmdOutInfo(Map<String, Object> params) {
		R r = new R();
		String edit_date = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
		Map<String, Object> u = userUtils.getUser();
		params.put("editor", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
		params.put("edit_date", edit_date);
		List<Map> scrape_list = new ArrayList<Map>();
		Map<String, Object> s_map = new HashMap<String, Object>();
		s_map.putAll(params);
		s_map.put("output_type", "2"); // // 产量录入类型 1 新增 2 报废
		s_map.put("plan_item_id", params.get("machine_plan_items_id"));
		// s_map.put("workgroup_name", params.get("workgroup"));
		// s_map.put("team_name", params.get("team"));
		s_map.put("machine", params.get("machine_code"));
		s_map.put("quantity", params.get("scrape_quantity"));
		// s_map.put("product_date", DateUtils.format(new
		// Date(),DateUtils.DATE_PATTERN));
		s_map.put("creator", u.get("FULL_NAME") + "(" + u.get("STAFF_NUMBER") + ")");
		s_map.put("create_date", edit_date);
		scrape_list.add(s_map);

		try {
			zzjJTOperationDao.saveZzjOutputData(scrape_list);

			int _prod_qty = Integer.parseInt(params.get("prod_quantity").toString());
			int _plan_qty = Integer.parseInt(params.get("plan_quantity").toString());
			int _scrape_qty = Integer.parseInt(params.get("scrape_quantity").toString());
			if (_prod_qty - _scrape_qty == 0) {
				params.put("plan_status", "1"); // 产量为0 ，状态更新为已锁定
			}
			if (_prod_qty - _scrape_qty < _plan_qty) {
				params.put("plan_status", "2"); // 产量<计划数量 ，状态更新为生产中
			}
			if (_prod_qty - _scrape_qty == _plan_qty) {
				params.put("plan_status", "3"); // 产量等于计划数量 ，状态更新为已完成
			}
			zzjJTOperationDao.updateMachinePlanByScrape(params);

			zzjJTOperationDao.updateZzjCurrentProcessByItem(params);

			r = R.ok("报废成功！");
		} catch (Exception e) {
			e.printStackTrace();
			r = R.error("报废失败！" + e.getMessage());
			throw new RuntimeException(e);
		}

		return r;
	}

	@Override
	public PageUtils queryCombRecords(Map<String, Object> params) {
		int totalCount = zzjJTOperationDao.queryCombRecordsCount(params);
		Page page = new Query<Map>(params).getPage();
		int pageSize = params.get("pageSize") == null ? 15 : Integer.parseInt((String) params.get("pageSize"));
		int pageNo = params.get("pageNo") == null ? 1 : Integer.parseInt((String) params.get("pageNo"));
		int start = (pageNo - 1) * pageSize;
		int length = pageSize;
		params.put("start", start);
		params.put("length", length);

		List<Map<String, Object>> records = zzjJTOperationDao.queryCombRecords(params);
		page.setRecords(records);
		page.setTotal(totalCount);
		page.setSize(pageSize);

		return new PageUtils(page);
	}

	@Override
	public R getPmdBaseInfo(Map<String, Object> params) {
		R r = new R();
		Map<String, Object> info = null;
		info = zzjJTOperationDao.getPmdBaseInfo(params);
		r.put("data", info);
		return r;
	}

	@Override
	public R checkBindPlan(Map<String, Object> params) {
		Map<String, Object> info = null;
		info = zzjJTOperationDao.getPmdBindPlan(params);
		return R.ok().put("data", info);
	}

	
	
}
