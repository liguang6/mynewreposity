function toItem(event){
	window.location.href= baseUrl + "qc/redirect/to_qc_query_inspection_item?inspectionNo="+event.target.text;
}

$(function() {
	$("#dataGrid").dataGrid({
		url : baseUrl + 'qcQuery/inspectionList',
		datatype : "json",
		searchForm : $("#searchForm"),
		colModel : [ {
			label : '送检单号',
			name : 'INSPECTION_NO',
			index : 'INSPECTION_NO',
			formatter : function(val) {
				return "<a href='#' id='' onclick='toItem(event)' >" + val + "</a>";
			},
			align : "center"
		}, {
			label : "送检单类型",
			name : "INSPECTION_TYPE",
			index : "INSPECTION_TYPE",
			formatter : function(val) {
				return val === '01' ? "来料质检" : "库存复检";
			},
			align : "center"
		}, {
			label : "工厂",
			name : "WERKS",
			index : "WERKS",
			align : "center"
		}, {
			label : "供应商代码",
			name : "LIFNR",
			index : "LIFNR",
			align : "center"
		}, {
			label : '供应商名称',
			name : "LIKTX",
			index : "LIKTX",
			align : "center"
		}, {
			label : "送检单状态",
			name : "INSPECTION_STATUS",
			index : "INSPECTION_STATUS",
			align : "center",
			formatter : function(val) {
				var name = '';
				switch (val) {
				case "00":
					name = "创建";
					break;
				case "01":
					name = "部分完成";
					break;
				case "02":
					name = "全部完成";
					break;
				case "03":
					name = "关闭";
					break;
				default:
					name = "未知"
				}
				return name;
			}
		},{
			label:"创建人",
			name:"CREATOR",
			index:"CREATOR",
			align : "center",
		},
		 {
				label : '创建时间',
				name : 'CREATE_DATE',
				index : 'CREATE_DATE',
				align : "center"
			}
		],
		rowNum: 15,
        rownumWidth: 25, 
	})

	$("#query").click(function() {
		$("#searchForm").submit();
	});

})