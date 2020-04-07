package com.byd.admin.modules.httpclient.entity;


/**
 * @author lu.yupei
 * @Description 用户菜单权限关联详细信息
 * @version 1.0
 * @date 2019/10/30
 */
public class UserMenuRefListBean {

		/**
		 "menuCode": "string",  （采购编号或菜单ID）
		 "staffAccount": "string", （员工号）
		 "systemCode": "string" （系统编号，默认："SZWMS001"）
		 */

		private String menuCode;
		private String staffAccount;
		private String systemCode;

		public String getMenuCode() {
			return menuCode;
		}

		public void setMenuCode(String menuCode) {
			this.menuCode = menuCode;
		}

		public String getStaffAccount() {
			return staffAccount;
		}

		public void setStaffAccount(String staffAccount) {
			this.staffAccount = staffAccount;
		}

	public String getSystemCode() {
			return systemCode;
		}

		public void setSystemCode(String systemCode) {
			this.systemCode = systemCode;
		}


}
