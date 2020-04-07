package com.byd.wms.webservice.ws.dao;

import java.util.Map;

public interface AuthUserDao {

	Map<String,Object> getUserByUserName(String username);
}
