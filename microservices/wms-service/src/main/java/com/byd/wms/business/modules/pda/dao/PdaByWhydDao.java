package com.byd.wms.business.modules.pda.dao;

import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PdaByWhydDao {

    List<Map<String, Object>> queryByBarcode(Map<String, Object> map);

    List<Map<String,Object>> validateBinCode(Map<String, Object> map);

    boolean commitByHwyd(ArrayList params);

    List<Map<String, Object>> queryBin(HashMap hm);

    List<Map<String, Object>> queryStockQty(HashMap hm);

    boolean insertByStock(HashMap hm);

    boolean updateByStock(HashMap hm);

    boolean updateBin(HashMap hm);

    boolean updateBinByStock(HashMap params);
}
