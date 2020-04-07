package com.byd.wms.business.modules.out.enums;

/**
 * 出库模块的字典类型 枚举类型
 * 
 * @author develop07
 *
 */
public enum OutModuleEnum {
	// 外部销售发货(251)业务类型
	OUT_SALES_BUSINESS_TYPE("00"),
	// 外部销售发货(251)业务名称
	OUT_SALES_BUSINESS_NAME("52"),
	// WBS元素领料(221)业务名称
	WBS_BUSINESS_NAME("45"),
	// WBS元素领料(221)业务类型
	WBS_BUSINESS_TYPE("15"),
	// UB转储单发货(351)业务类型
	UB_BUSINESS_TYPE("02"),
	// UB转储单发货(351)业务名称
	UB_BUSINESS_NAME("64"),

	INNER_ORDER_BUSINESS_NAME("43"), INNER_ORDER_BUSINESS_TYPE("13"),

	// 工厂间凋波业务
	PLANT_MOVE_BUSINESS_NAME("57"), PLANT_MOVE_BUSINESS_TYPE("00"), PLANT_MOVE_BUSINESS_NAME_301("77"),
	PLANT_MOVE_BUSINESS_TYPE_301("19"),
	WPLANT_MOVE_BUSINESS_NAME("46"),
	// 成本中心
	ICQ_BUSINESS_NAME("44"),
	//// 成本中心
	ICQ_BUSINESS_TYPR("14"),

	// 库存地点调拨311—类型不转移
	STORE_PLACE_MOVE_BUSINESS_NAME("47"),
	// 库存地点调拨311—类型不转移
	STORE_PLACE_MOVE_BUSINESS_TYPE("00"),

	A303_BUSINESS_NAME("53"), A303_BUSINESS_TYPE("17"),

	SAP_BUSINESS_NAME("50"), SAP_BUSINESS_TYPE("06"),
	SAP_BUSINESS_NAME2("51"), SAP_BUSINESS_TYPE2("16"),

	SAP_A311T_BUSINESS_NAME("54"), SAP_A311T_BUSINESS_TYPE("07"),
	// 业务分类出库全部都相同
	BUSINESS_CLASS("07"),

	// 报废551
	BF_MOVE_BUSINESS_NAME("73"),
	// 报废551
	BF_MOVE_BUSINESS_TYPR("14"),

	// pda311
	PDA311_BUSINESS_NAME("76"),

	PDA311_BUSINESS_TYPR("00"),

	// STO一步联动
	STO_YBLD_MOVE_BUSINESS_NAME("75"),
	// STO一步联动
	STO_YBLD_BUSINESS_TYPR("18"),

	WY_BUSINESS_NAME("49"),
	WY_BUSINESS_TYPE("02"),
	
	//生产订单领料（261）
	OUT_PRO_BUSINESS_NAME("41"),
	OUT_PRO_BUSINESS_TYPE("10"),
	OUT_PRO_HX_BUSINESS_TYPE("11"),
	//生产订单补料（261）
	OUT_PRO_BL_BUSINESS_NAME("42"),
	;

	// --------------------------------//
	private OutModuleEnum(String code) {
		this.code = code;
	}

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
