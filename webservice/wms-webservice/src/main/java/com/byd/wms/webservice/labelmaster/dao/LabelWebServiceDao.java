package com.byd.wms.webservice.labelmaster.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LabelWebServiceDao {
    public List<Map<String,Object>> getLabelMaster(Map<String, Object> map);

    public String getLogDate(@Param("SYS_FLAG")String SYS_FLAG,@Param("WERKS")String WERKS);

    public int getSysFlag(@Param("SYS_FLAG")String SYS_FLAG,@Param("WERKS")String WERKS);

    public void saveLog(Map<String, Object> map);

    public void saveLog2(Map<String, Object> map);

}
