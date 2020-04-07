package com.byd.web.wms.out.controller;

import com.byd.utils.R;
import com.byd.web.wms.out.service.WmsOutPickingServiceRemote;
import com.byd.web.wms.out.service.WmsOutResersalPickingServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 取消需求拣配控制器
 * @author qiu.jiaming1
 *
 */
@RestController
@RequestMapping("/out/resersalPicking")
public class WmsOutResersalPickingController {

	@Autowired 
	private WmsOutResersalPickingServiceRemote wmsOutResersalPickingServiceRemote ;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsOutResersalPickingServiceRemote.list(params);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
        return wmsOutResersalPickingServiceRemote.update(params);
    }


}
