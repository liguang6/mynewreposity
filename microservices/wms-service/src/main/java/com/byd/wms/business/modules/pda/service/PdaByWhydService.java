package com.byd.wms.business.modules.pda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date $time$ $date$
 * @Description //TODO $end$
 **/

public interface PdaByWhydService {

    /**
     * 查询条码信息
     * @param map
     * @return
     */
    public List<Map<String,Object>> queryByBarcode(Map<String, Object> map);

    /**
     * 校验储位
     * @param map
     * @return
     */
    public List<Map<String,Object>> validateBinCode(Map<String, Object> map);

    /**
     * 提交
     * @param params
     * @return
     */
    boolean commitByHwyd(HashMap params);

    public List<Map<String,Object>> queryBin(HashMap hm);

    List<Map<String, Object>> queryStockQty(HashMap hm);

    boolean commitByHwyd2(HashMap hm);
}
