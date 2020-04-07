package com.byd.wms.webservice.cloud.controller;

import com.byd.utils.R;
import com.byd.wms.webservice.cloud.service.QmsWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @ClassName
 * @Author rain
 * @Date 2019年11月30日11:28:22
 * @Description QMS质检接口
 **/
@RestController
@RequestMapping("/qmswebservice")
public class QmsWebServiceController {

    @Autowired
    private QmsWebService qmsWebService;


	/**
	 * @description 入库质检信息接口:发送质检信息给QMS系统
	 * @author rain
	 * @date 2019年11月30日11:33:41
	 * @param params
	 * @return
	 */
	@RequestMapping("/sendQmsData")
    public R sendQmsData(@RequestBody HashMap params){

        HashMap hm = qmsWebService.sendQmsData(params);
		if("fail".equals(hm.get("msgty"))){
            return R.error("调用送检单接口失败！" + hm.get("msgtx"));
        }
        return R.ok().put("msgty",hm.get("msgty")).put("msgtx",hm.get("msgtx"));
    }


}
