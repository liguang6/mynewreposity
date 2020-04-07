package com.byd.wms.webservice.cloud.service;

import java.util.HashMap;

/**
 * @ClassName
 * @Author rain
 * @Date 2019年11月30日11:35:48
 * @Description QMS质检接口
 **/
public interface QmsWebService {

    /**
     * 发送质检信息给QMS系统
     */
    public HashMap sendQmsData(HashMap hashMap);
    
}
