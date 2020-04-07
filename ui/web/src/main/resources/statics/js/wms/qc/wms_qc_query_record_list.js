$(function(){
	//--
	$("#dataGrid").dataGrid({
		url : baseUrl + 'qcQuery/resultList',
		datatype : "json",
		searchForm : $("#searchForm"),
		colModel : [ {
			label : '批次',
			name : 'BATCH',
			index : 'BATCH',
			align : "center",
			width : 100,
		},{
			label:"质检记录类型",
			name:"QC_RECORD_TYPE",
			index:"QC_RECORD_TYPE",
			align:"center",
			formatter:function(val){
				if(val === '01'){
					return '初判'
				}
				else if(val === '02'){
					return '重判'
				}
				else{
					return ''
				}
			},
			width:100
		},{
			
			label:"工厂",
			name:"WERKS",
			index:"WERKS",
			align:"center",
			width:50
		},{
			label:'仓库号',
			name:'WH_NUMBER',
			index : 'WH_NUMBER',
			align : 'center',
			width : 70
		},{
			label : '库位',
			name : 'LGORT',
			index : 'LGORT',
			align : 'center',
			width : 50
		},{
			label : "储位",
			name : "BIN_CODE",
			index : "BIN_CODE",
			align : "center",
			width : 50
		},{
			label : '需求跟踪号',
			name : "BEDNR",
			index : 'BEDNR',
			align : 'center',
			width : 80
		},{
			
			label:"供应商代码",
			name:"LIFNR",
			index:"LIFNR",
			align:"center",
			width:100
		},{
			label : '供应商名称',
			name : 'LIKTX',
			index : 'LIKTX',
			align : 'center'
		},{
			label:"料号",
			name:"MATNR",
			index:"MATNR",
			align:"center",
			width:130
		},{
			label:"物料描述",
			name:"MAKTX",
			index:"MAKTX",
			align:"center"
		},{
			label:"收货单号",
			name:"RECEIPT_NO",
			index:"RECEIPT_NO",
			align:"center",
			width:150
		},{
			label:"收货/创建日期",
			name:"RECEIPT_DATE",
			index:"RECEIPT_DATE",
			align:"center",
			width:130
		},{
			label:"送检数量",
			name:"INSPECTION_QTY",
			index:"INSPECTION_QTY",
			align:"center",
			width:100
		},{
			label:"送检单号",
			name:"INSPECTION_NO",
			index:"INSPECTION_NO",
			align:"center",
			width:130
		},
		{
			label : '送检单行号',
			name : 'INSPECTION_ITEM_NO',
			index : 'INSPECTION_ITEM_NO',
			align : 'center',
			width : 100
		},
		{
			label:"质检日期",
			name:"QC_DATE",
			index:"QC_DATE",
			align:"center"
		},
		{
			label : "检验结果",
			name : "QC_RESULT_CODE",
			index : "QC_RESULT_CODE",
			width:70,
			formatter : function(val) {
				if (val === '01') {
					return "让步接收";
				}
				if (val === "02" || val === '11') {
					return "合格"
				}
				if (val === "03") {
					return "不合格"
				}
				if (val === "04") {
					return "挑选使用"
				}
				if (val === "05") {
					return "紧急放行"
				}
				if (val === "06") {
					return "特采"
				}
				if (val === "07") {
					return "退货"
				}
				if (val === "08") {
					return "实验中"
				}
				if (val === "09") {
					return "评审中"
				}
				if (val === "10") {
					return "精测中"
				}
				
				return "未知";
			},
			align : "center"
		},{
			label : '不良原因',
			name : 'QC_RESULT',
			index : 'QC_RESULT',
			align : 'center',
			width : 150
		},{
			label:"质检员",
			name:"QC_PEOPLE",
			index:"QC_PEOPLE",
			align:"center",
			width:70
		},{
			label:"质检数量",
			name:"RECORD_QTY",
			index:"RECORD_QTY",
			align:"center",
			width:70
		},{
			label : "标签号",
			name : "LABEL_NO",
			index : "LABEL_NO",
			align : "center"
		}],
		rowNum: 15,
        rownumWidth: 25, 
		viewrecords: true,
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true,
	});
	
	DynamicColume();
});