package com.byd.wms.webservice.ws.dao;

import java.util.List;
import java.util.Map;

public interface MesServiceDao {
	public void savePDMMap(Map<String, Object> map);
	public void updatePDMMap(Map<String, Object> map);
    public List<String> getExistPDMMap(Map<String, Object> map);
}
