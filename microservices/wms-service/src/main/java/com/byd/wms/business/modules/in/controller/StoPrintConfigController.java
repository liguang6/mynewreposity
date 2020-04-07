package com.byd.wms.business.modules.in.controller;


import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.in.service.StoPrintConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("in/sto")
public class StoPrintConfigController {
    @Autowired
    private StoPrintConfigService stoPrintConfigService;

    @RequestMapping("/query")
    public R query(@RequestParam Map<String, Object> params) {
        PageUtils page = stoPrintConfigService.queryList(params);
        return R.ok().put("page", page);
    }

    /**
     * 修改
     */
    @RequestMapping("/edit")
    public R update(@RequestParam Map<String, Object> params) {
        stoPrintConfigService.edit(params);
        return R.ok();
    }
    /**
     * 增加
     *
     */
      @RequestMapping("/add")
    public R add(@RequestParam Map<String,Object> params){
          stoPrintConfigService.add(params);
          return R.ok();
      }
      @RequestMapping("/del")
    public R del(@RequestParam Map<String,Object> params){
          stoPrintConfigService.del(params);
          return R.ok();
      }

}
