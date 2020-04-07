package com.byd.admin.modules.httpclient.entity;


import java.util.List;

/**
 * @author lu.yupei
 * @Description 用户菜单权限关联头信息
 * @version 1.0
 * @date 2019/10/30
 */
public class HttpUserMenuRefVO {


	/**
	 "synType": "string",         （操作类型：ADD-增加;UPDATE-更新;DELETE-删除）
	 "userMenuRefList": "List"    （用户菜单详细信息集合）
	 */

	private String synType;
	private List<UserMenuRefListBean> userMenuRefList;

	public String getSynType() {
		return synType;
	}

	public void setSynType(String synType) {
		this.synType = synType;
	}

	public List<UserMenuRefListBean> getUserMenuRefList() {
		return userMenuRefList;
	}

	public void setUserMenuRefList(List<UserMenuRefListBean> userMenuRefList) {
		this.userMenuRefList = userMenuRefList;
	}

}
