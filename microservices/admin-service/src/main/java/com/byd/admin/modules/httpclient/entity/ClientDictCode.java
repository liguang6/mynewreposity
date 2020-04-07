package com.byd.admin.modules.httpclient.entity;

/**
 * 接口实现方式：httpClient
 * @author rain
 * @date 2019年10月31日14:28:02
 * @description WMS与云平台接口的常量数据类
 */
public interface ClientDictCode {

	/**
	 * 其它地方引用，当IP和端口号改变只需要更改一个地方
	 */
	public static final String HTTPCLIENT_WMSTOYPT_IPPORT = "http://webcloudtest.byd.com.cn/integrate";//云平台菜单【dev测试】服务器ip+端口号

//	public static final String HTTPCLIENT_WMSTOYPT_IPPORT = "http://10.43.57.116:8181/menu-provider";//云平台菜单【本地测试】服务器ip+端口号

	/**
	 * 同步菜单数据接口:http://IP:port/syn/menudata
	 */
	public static final String HTTPCLIENT_WMSTOYPT_MENUURL = "/syn/menudata";

	/**
	 * 同步用户菜单关系接口:http://IP:port/syn/userMenuRef
	 */
	public static final String HTTPCLIENT_WMSTOYPT_USERMENUURL = "/syn/userMenuRef";

	/**
	 * 与云平台菜单同步，系统编号统一管理
	 */
	public static final String WMSTOYPT_SYSTEM_CODE = "BYD_WMS_SZ";

	/**
	 * 与云平台菜单同步，系统名称统一管理
	 */
	public static final String WMSTOYPT_SYSTEM_NAME = "比亚迪深圳WMS001";


}
