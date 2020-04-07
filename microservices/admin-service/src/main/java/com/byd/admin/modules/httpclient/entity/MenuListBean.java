package com.byd.admin.modules.httpclient.entity;


/**
 * @author lu.yupei
 * @Description 菜单同步类详细信息
 * @version 1.0
 * @date 2019/10/30
 */
public class MenuListBean {

		/**
		 "menuCode": "string",     （菜单编号）
		 "menuName": "string",     （菜单名称）
		 "module1Code": "string",  （1级模块编号）
		 "module1Name": "string",  （1级模块名称）
		 "module2Code": "string",  （2级模块编号）
		 "module2Name": "string" , （2级模块编号）
		 "systemCode": "string",   （系统编号，默认："SZWMS001"）
		 "systemName": "string",   （系统名称，默认："深圳WMS001"）
		 "urlPath": "string"        (菜单URL)
		 */

		private String menuCode;
		private String menuName;
		private String menuUrl;
		private String systemCode;
		private String systemName;
		private String module1Code;
		private String module1Name;
		private String module2Code;
		private String module2Name;




		public String getMenuCode() {
			return menuCode;
		}

		public void setMenuCode(String menuCode) {
			this.menuCode = menuCode;
		}

		public String getModule2Name() {
			return module2Name;
		}

		public void setModule2Name(String module2Name) {
			this.module2Name = module2Name;
		}

		public String getMenuName() {
			return menuName;
		}

		public void setMenuName(String menuName) {
			this.menuName = menuName;
		}

		public String getModule2Code() {
			return module2Code;
		}

		public void setModule2Code(String module2Code) {
			this.module2Code = module2Code;
		}

		public String getModule1Name() {
			return module1Name;
		}

		public void setModule1Name(String module1Name) {
			this.module1Name = module1Name;
		}

		public String getSystemCode() {
			return systemCode;
		}

		public void setSystemCode(String systemCode) {
			this.systemCode = systemCode;
		}

		public String getSystemName() {
			return systemName;
		}

		public void setSystemName(String systemName) {
			this.systemName = systemName;
		}

		public String getMenuUrl() {
			return menuUrl;
		}

		public void setMenuUrl(String menuUrl) {
			this.menuUrl = menuUrl;
		}

	public String getModule1Code() {
			return module1Code;
		}

		public void setModule1Code(String module1Code) {
			this.module1Code = module1Code;
		}
}
