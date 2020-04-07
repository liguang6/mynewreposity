package com.byd.wms.business.modules.pda.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.dao.CommonDao;

@RestController
@RequestMapping("whTask")
public class PdaWarehouseTasksController {
    @Autowired
    private WarehouseTasksService warehouseTasksService;
    @Autowired
    private CommonDao commonDao;
    
    /**
     * 查询仓库任务
     */
    @CrossOrigin
    @RequestMapping("/query")
    public R selectCoreWHTask(@RequestBody Map<String, Object> params) {
        return R.ok().put("data", warehouseTasksService.selectCoreWHTask(params));
    }
    
    /**
     * 创建仓库任务
     */
    @CrossOrigin
    @RequestMapping("/save")
    public R saveWHTask(@RequestBody List<Map<String,Object>> params) {
    	Map map = new HashMap();
		String reqNo = params.get(0).get("REQUIREMENT_NO").toString();
		map.put("REFERENCE_DELIVERY_NO",reqNo );
		map.put("WT_STATUS", "00");
		List<Map<String,Object>> list = warehouseTasksService.selectCoreWHTask(map);
		List<Map<String,Object>> list1 = new ArrayList<>();
		if(list.size()>0) {
			for(Map<String,Object> o : list) {
				for(Map<String,Object> n : params) {
					if(o.get("MATNR").toString().equals(n.get("MATNR"))&&o.get("REFERENCE_DELIVERY_NO").toString().equals(n.get("REFERENCE_DELIVERY_NO"))&&o.get("REFERENCE_DELIVERY_ITEM").toString().equals(n.get("REQUIREMENT_ITEM_NO"))) {
						list1.add(n);
					}
				}
				
			}
		}
		params.removeAll(list1);
		if(params.size()==0)
			return R.ok();
        return R.ok().put("wtNo", warehouseTasksService.saveWHTask(params));
    }

    /**
     * 更新仓库任务
     */
    @CrossOrigin
    @RequestMapping("/update")
    public R updateCoreWHTask(@RequestBody List<Map<String,Object>> params) {
        return R.ok().put("count", warehouseTasksService.updateCoreWHTask(params));
    }

    /**
     * 查询标签信息
     */
    @CrossOrigin
    @RequestMapping("/scanLabel")
    public R scanLabel(@RequestBody Map<String, Object> params) {
        return R.ok().put("data", commonDao.scanLabel(params));
    }
    /**
     * 查询seqno
     */
    @CrossOrigin
    @RequestMapping("/querySeqNo")
    public R querySeqNo(@RequestBody Map<String, Object> params) {
        return R.ok().put("data", commonDao.querySeqNo(params));
    }


}