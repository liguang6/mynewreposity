/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.utils;

/**
 * Redis所有Keys
 *
 * @author develop01 
 * @since 3.0.0 2017-07-18
 */
public class RedisKeys {
	
	public static String getAllWerksKey() {
		return "wms:staticData:allWerks";
	}
	public static String getAllWhKey() {
		return "wms:staticData:allWh";
	}
	
	public static String getUserKey(String key) {
		return "wms:user:" + key;
	}
	
	public static String getUserMenuKey(String key) {
		return "wms:user:menu:" + key;
	}
	
	public static String getUserAuthKey(String key) {
		return "wms:user:auth:" + key;
	}
	
	public static String getUserWerksKey(String key) {
		return "wms:user:werks:" + key;
	}
	public static String getUserWhKey(String key) {
		return "wms:user:wh:" + key;
	}
	public static String getUserBusinessNameKey(String key) {
		return "wms:user:businessName:" + key;
	}
	public static String getUserQmsTestGroupKey(String key) {
		return "qms:user:testGroup:" + key;
	}
	
	public static String getUserMesWorkshopKey(String key) {
		return "mes:user:workshop:" + key;
	}
	
	
	public static String getUserMesLineKey(String key) {
		return "mes:user:line:" + key;
	}
	
	public static String getUserTokenKey(String key) {
		return "mes:user:token:" + key;
	}

    public static String getSysConfigKey(String key){
        return "sys:config:" + key;
    }

    public static String getAllMenuKey(){
        return "wms:sys:allMenu:";
    }
    public static String getAllAuthKey(){
        return "wms:sys:allAuth:";
    }
}
