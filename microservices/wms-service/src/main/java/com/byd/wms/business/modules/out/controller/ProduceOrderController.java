package com.byd.wms.business.modules.out.controller;

import java.io.IOException;
import java.util.*;
import com.byd.utils.DateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;
import com.byd.wms.business.modules.out.entity.CostCenterAO;
import com.byd.wms.business.modules.out.entity.CreateProduceOrderAO;
import com.byd.wms.business.modules.out.entity.ProduceOrderVO;
import com.byd.wms.business.modules.out.service.CreateRequirementService;



/**
 * 生产订单领料需求创建
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/createRequirement")
public class ProduceOrderController {

	@Autowired
	CreateRequirementService createRequirementService;

	@RequestMapping("/getPlantBusinessTypes")
	public R getPlantBusinessTypes(@RequestParam Map<String, Object> params) {
		return R.ok().put("list",
				createRequirementService.getPlantBusinessTypes(params));
	}

	@RequestMapping("/processProduceOrder")
	public R processProduceOrder(@RequestBody List<ProduceOrderVO> produceOrders) {
		Map<String, Integer> checkResultMap = createRequirementService.validProduceOrders(produceOrders);
		String msg = "";
		for (String orderNo : checkResultMap.keySet()) {
			switch (checkResultMap.get(orderNo)) {
			case 1:
				msg += (orderNo + "订单信息不存在/未同步，请确认输入是否正确或手动同步生产订单\n");
				break;
			case 2:
				msg += (orderNo + "生产订单不存在\n");
				break;
			case 3:
				msg += (orderNo + "您没有此订单所属工厂的权限\n");
				break;
			case 4:
				msg += (orderNo + "生产订单未发布或已关闭，不允许发料\n");
				break;
			case 5:
				msg += (orderNo + "订单未发布，不允许发料\n");
				break;
			default:
				msg += (orderNo + "校验失败\n");
			}
		}
		if (msg.length() > 0) {
			return R.error(msg);
		}
		return R.ok();
	}

	/**
	 * 查询生产订单信息 - 生产订单领料
	 * @param params
	 * @return
	 */
	@RequestMapping("/producerOrders")
	public R queryProducerOrderInfo(@RequestBody Map<String,Object> params){
//		System.err.println(params.toString());
		String referOrderLgort = (String)params.get("referOrderLgort");
		String filterZeroRequireLine = (String)params.get("filterZeroRequireLine");
		String werks = (String)params.get("werks");
		String whNumber = (String)params.get("whNumber");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queryItems = (List<Map<String, Object>>) params.get("items");
		List<ProduceOrderVO> orderList = new ArrayList<ProduceOrderVO>();
		for(Map<String,Object> item:queryItems){
			if(item.get("orderNo") == null)
				return R.error("订单号不能为空");

			String orderNo = ((String)item.get("orderNo")).trim();

			String mat = (String)item.get("mat");
			String location = (String)item.get("location");
			String station = (String)item.get("station");

			Double qty = null;
			Double requireSuitQty = null;
			try {
				if(StringUtils.isNoneBlank((String)item.get("qty"))){
					qty = Double.parseDouble((String)item.get("qty"));
				}
				if(StringUtils.isNoneBlank((String)item.get("requireSuitQty"))){
					requireSuitQty = Double.parseDouble((String)item.get("requireSuitQty"));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return R.error("数字转换错误");
			}

			ProduceOrderVO order = new ProduceOrderVO();
			ProduceOrderVO order1 = new ProduceOrderVO();
			order.setLocation(location);
			order.setMat(mat);
			order.setOrderNo(orderNo);
			order.setQty(qty);
			order.setRequireSuitQty(requireSuitQty);
			order.setStation(station);
			orderList.add(order);
		}
			List<Map<String, Object>> sapMoComponentList = createRequirementService.queryProducerOrders(referOrderLgort, filterZeroRequireLine, orderList, werks, whNumber, false, false);

			String msg = "";
			for (Map<String, Object> sapMoComponent : sapMoComponentList) {
				if (null == sapMoComponent.get("MAKTX") || sapMoComponent.get("MAKTX").equals("")) {
					msg += (sapMoComponent.get("MATNR") + "物料未同步；\n");
				}
				//判断需求数量是否为空，不为空则批量导入需求数量。
				for(int i=0;i<orderList.size();i++){
					if(orderList.get(i).getQty()!=null){
						sapMoComponentList.get(i).put("REQ_QTY",orderList.get(i).getQty());
					}else{
						sapMoComponentList.get(i).put("REQ_QTY",sapMoComponentList.get(i).get("REQ_QTY"));
					}
				}
			}
			if (!msg.equals("")) {
				return R.error(msg);
			}
			return R.ok().put("data", sapMoComponentList);
	}

	@Autowired
	WmsSapRemote sapRemote;

	@RequestMapping("/sapCostcenter/{Costcenter}")
	public Map<String,Object> getSapCostcenterDetail(@PathVariable("Costcenter")String Costcenter){
		return sapRemote.getSapBapiCostcenterDetail(Costcenter);
	}

	@Autowired
	SendCreateRequirementDao dao;
	@RequestMapping("/mat")
	public R getMat(String werks,String mat){
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("werks", werks);
		queryMap.put("matnr", mat);
		List<Map<String,Object>> mats=  dao.selectMaterial(queryMap);
		if(mats == null || mats.size() < 1){
			return R.error(mat + "物料不存在/未同步，请确认输入是否正确或手动执行同步功能");
		}
		return R.ok().put("data", mats.get(0));
	}

	@RequestMapping("/costcenterReq")
	public R createCostcenterRequirement(@RequestBody List<CostCenterAO> items){
		System.err.println("============ "+items.toString());
		String requestNO = createRequirementService.createRequirementFromICQCenterSplit(items);
		return R.ok().put("data", requestNO);
	}

	/**
	 * 出库-创建需求:库位点击事件，查询库位信息方法
	 * @param werks
	 * @param whNumber
	 * @param matnr
	 * @return
	 */
	@RequestMapping("/lgortAndStock")
	public R getLgortStockByMaterial(String werks,String whNumber,String matnr){
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("werks", werks);
		queryMap.put("matnr", matnr);
		queryMap.put("whNumber", whNumber);
		List<Map<String, Object>> data =  dao.selectLgortStockByMaterial(queryMap);
		return R.ok().put("data", data);
	}

	@RequestMapping("/costcenterBatchLoadPreview")
	public R batchLoadPreview(MultipartFile excel) throws IOException{
		List<String[]> sheet = ExcelReader.readExcel(excel);
		List<CostCenterAO> data = new ArrayList<CostCenterAO>();
		for(String[] row:sheet){
			CostCenterAO item = new CostCenterAO();
			item.setMATNR(row[0]);
			item.setREQ_QTY(Double.parseDouble(row[1]));
			item.setMEMO(row[2]);
			data.add(item);
		}
		//校验
		for(CostCenterAO costCenterAO:data){
			if(costCenterAO.getMessages() == null){
				costCenterAO.setMessages(new ArrayList<String>());
			}
			Map<String,Object> queryMap = new HashMap<String,Object>();
			queryMap.put("matnr", costCenterAO.getMATNR());
			List<Map<String,Object>> mats=  dao.selectMaterial(queryMap);
			if(mats == null || mats.size() == 0){
				costCenterAO.getMessages().add( costCenterAO.getMATNR()+ "物料不存在/未同步，请确认输入是否正确或手动执行同步功能");
			}else{
				costCenterAO.setMAKTX((String)mats.get(0).get("MAKTX"));
				costCenterAO.setMEINS((String)mats.get(0).get("MEINS"));
			}
		}

		return R.ok().put("data", data);
	}

	@RequestMapping("/create")
	public R createProduceOrderReq(@RequestBody List<CreateProduceOrderAO> cList){
		String reqNo;
		try {
			reqNo = createRequirementService.createProduceOrderOutReqSplit(cList);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}

		return R.ok().put("data", reqNo);
	}


	/**
	 * 查询非上线物料
	 * @param werks
	 * @return
	 */
	@RequestMapping("matuseing/{werks}")
	public R matUsing(@PathVariable("werks")String werks){

		return R.ok().put("data", createRequirementService.queryMatUseing(werks));
	}


	@RequestMapping("sapPoItemHead/{werks}/{poNo}/")
	public R sapPoItemHead(@PathVariable("werks")String werks,@PathVariable("poNo")String poNo){
		//查询SapPoItemHead
		return R.ok().put("data",dao.selectSapPoHeadInfo(werks, poNo));
	}


	@RequestMapping("/upload")
	public R upload(@RequestBody List<CreateProduceOrderAO> entityList) throws IOException{
		//System.err.println("uploaduploaduploaduploadupload");
		String reqNo;
		try {
			reqNo = createRequirementService.creteScddByUpload(entityList);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("data", reqNo);
	}

	@RequestMapping("/preview")
	public R preview(@RequestBody List<Map<String,Object>> entityList) throws IOException {
        //System.err.println("========================previewpreviewpreview==================");
		System.err.println(entityList.toString());
        String msg = "";
		if (!CollectionUtils.isEmpty(entityList)) {
            for (Map<String, Object> map : entityList) {

				if (map.get("AUFNR") == null || (map.get("AUFNR") != null && map.get("AUFNR").toString().equals(""))) {
					msg += "生产订单号不能为空！";
				}
				if(map.get("requireTypes").equals("41")){
					if (map.get("POSNR") == null || (map.get("POSNR") != null && map.get("POSNR").toString().equals(""))) {
						msg += "生产订单行项目号不能为空！";
					}
				}

                if (map.get("MATNR") == null || (map.get("MATNR") != null && map.get("MATNR").toString().equals(""))) {
                    msg += "料号不能为空！";
                }
                if (map.get("REQ_QTY") == null || (map.get("REQ_QTY") != null && map.get("REQ_QTY").toString().equals(""))) {
                    msg += "需求数量不能为空！";
                }
                map.put("editDate", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
                map.put("msg", msg);
            }

        }
        return R.ok().put("data", entityList);
    }

}
