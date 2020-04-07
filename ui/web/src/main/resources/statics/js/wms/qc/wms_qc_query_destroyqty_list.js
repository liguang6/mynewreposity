$(function(){
	//--
	$("#dataGrid").dataGrid({
		url : baseUrl + 'qcQuery/resultList',
		datatype : "json",
		searchForm : $("#searchForm"),
		colModel : [{	
			label:"工厂",
			name:"WERKS",
			index:"WERKS",
			align:"center"
		},
		 {
			label : '批次',
			name : 'BATCH',
			index : 'BATCH',
			align : "center"
		},
		{
			label:"料号",
			name:"MATNR",
			index:"MATNR",
			align:"center"
		},
		{
			label:"物料描述",
			name:"MAKTX",
			index:"MAKTX",
			align:"center"
		},
		{
			label:"送检数量",
			name:"INSPECTION_QTY",
			index:"INSPECTION_QTY",
			align:"center"
		},
		{
			label:"破坏数量",
			name:"DESTROY_QTY",
			index:"DESTROY_QTY",
			align:"center"
		},
		{
			label:"破坏过账数量",
			name:"IN_DESTROY_QTY",
			index:"IN_DESTROY_QTY",
			align:"center"
		},
		{
			label:"单位",
			name:"UNIT",
			index:"UNIT",
			align:"center"
		},
		{
			
			label:"供应商代码",
			name:"LIFNR",
			index:"LIFNR",
			align:"center"
		},
		{
			label:"供应商名称",
			name:"LIKTX",
			index:"LIKTX",
			align:"center"
		},
		{
			label:"质检员",
			name:"QC_PEOPLE",
			index:"QC_PEOPLE",
			align:"center"
		},{
			label:"质检日期",
			name:"QC_DATE",
			index:"QC_DATE",
			align:"center"
		},{
			label:"成本中心",
			name:"IQC_COST_CENTER",
			index:"IQC_COST_CENTER",
			align:'center'
		}],
		
		rowNum: 15,
        rownumWidth: 25, 
	});
});