package com.byd.admin.modules.httpclient.entity;


import java.util.List;

/**
 * @author lu.yupei
 * @Description 菜单同步类头信息
 * @version 1.0
 * @date 2019/10/30
 */
public class HttpMenuDataVO {


	/**
	 "dataType": "string",    （数据类型：1-菜单;2-模块）
	 "synType": "string",     （操作类型：ADD-增加;UPDATE-更新;DELETE-删除）
	 "menuList":"List"        （菜单模块详细信息）
	 */

	private String dataType; //数据类型：1-菜单;2-模块
	private String synType; //操作类型：ADD-增加;UPDATE-更新;DELETE-删除
	private List<MenuListBean> menuList;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSynType() {
		return synType;
	}

	public void setSynType(String synType) {
		this.synType = synType;
	}

	public List<MenuListBean> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<MenuListBean> menuList) {
		this.menuList = menuList;
	}

}
