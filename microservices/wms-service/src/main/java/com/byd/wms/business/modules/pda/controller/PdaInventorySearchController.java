package com.byd.wms.business.modules.pda.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.wms.business.modules.pda.service.PdaInventorySearchService;

@RestController
@RequestMapping("inventorySearch")
public class PdaInventorySearchController {
    @Autowired
    private PdaInventorySearchService pdaInventorySearchService;
    
    
    /**
      *查询物料号
     */
    @CrossOrigin
    @RequestMapping("/queryMatnr")
    public R selectCoreWHTask(@RequestBody Map<String, Object> params) {
        return R.ok().put("data", pdaInventorySearchService.queryMatnr(params));
    }
    
    @CrossOrigin
    @RequestMapping("/list")
    public R inventoryList(@RequestBody Map<String, Object> params) {
        return R.ok().put("data", pdaInventorySearchService.inventoryList(params));
    }
    
   

   
}