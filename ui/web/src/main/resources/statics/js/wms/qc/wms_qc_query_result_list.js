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
			width : 100
		},{
			
			label:"工厂",
			name:"WERKS",
			index:"WERKS",
			align:"center",
			width : 50
		},{
			label : '仓库',
			name : 'WH_NUMBER',
			index : 'WH_NUMBER',
			align : 'center',
			width : 50
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
			label:"供应商代码",
			name:"LIFNR",
			index:"LIFNR",
			align:"center",
			width : 100
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
			width : 100
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
			width : 130
		},{
			label:"收货/创建日期",
			name:"RECEIPT_DATE",
			index:"RECEIPT_DATE",
			align:"center",
			width : 100
		},{
			label:"送检数量",
			name:"INSPECTION_QTY",
			index:"INSPECTION_QTY",
			align:"center",
			width : 70
		},{
			label:"送检单号",
			name:"INSPECTION_NO",
			index:"INSPECTION_NO",
			align:"center",
			width : 150
		},{
			label : '送检单行号',
			name : 'INSPECTION_ITEM_NO',
			index : 'INSPECTION_ITEM_NO',
			align : 'center',
			width : 100
		}, {
			label : "检验结果",
			name : "QC_RESULT_CODE",
			index : "QC_RESULT_CODE",
			width : 100,
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
			label : '原因分类',
			name : 'RETURN_REASON_TYPE_NAME',
			index : 'RETURN_REASON_TYPE_NAME',
			align : 'center',
			width : 150
		},{
			label:"质检员",
			name:"QC_PEOPLE",
			index:"QC_PEOPLE",
			align:"center",
			width : 70
		},{
			label : '质检日期',
			name : 'QC_DATE',
			index : 'QC_DATE',
			align : 'center',
			width : 100
		},{
			label:"质检数量",
			name:"RESULT_QTY",
			index:"RESULT_QTY",
			align:"center",
			width : 70
		},{
			label:"需求跟踪号",
			name:"BEDNR",
			index:"BEDNR",
			align:"center",
			width : 100
		},{
			label:"库存来源",
			name:"STOCK_SOURCE",
			index:"STOCK_SOURCE",
			width : 100,
			formatter:function(val){
				if(val=== '01'){
					return "收料房"
				}
				if(val === "02"){
					return "库房"
				}
				return "未知"
			},
			align:"center"
		},{
			label: "标签号",
			name : "LABEL_NO",
			index : "LABEL_NO",
			align : "center",
			width : 80
		},
		{
			label : "备注",
			name : "MEMO",
			index : "MEMO",
			align : "center",
			width : 100
		}],
		rowNum: 15,
        rownumWidth: 25, 
		viewrecords: true,
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
	});
	DynamicColume(title="列显示");
});


var vm = new Vue({
	el:"#vue-app",
	data:{warehourse:[]},
	created:function(){
		this.onPlantChange();
		
	},
	methods:{
		onPlantChange:function(){
	          var plantCode = $("#werks").val();
	          if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	  return;
	          }
	          //查询仓库
	          $.ajax({
			      url:baseUrl + "common/getWhDataByWerks",
			      data:{"WERKS":plantCode},
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        	  }
	          })
		},
		exp: function () {
			export2Excel();
		}
	}
});

function export2Excel(){
	var column=[      
		{"title":'工厂代码', "data":'WERKS', "class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER', "class":"center"},
		{"title":'库位', "data":'LGORT',"class":"center"},
		{"title":'储位', "data":'BIN_CODE',"class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},
		{"title":'批次', "data":'BATCH',"class":"center"},
		{"title":'送检单号', "data":'INSPECTION_QTY', "class":"center"},
		{"title":'行项目号', "data":'INSPECTION_QTY', "class":"center"},
		{"title":'送检数量', "data":'INSPECTION_QTY', "class":"center"},
		{"title":'质检数量', "data":'RESULT_QTY', "class":"center"},
		{"title":'检验结果', "data":'QC_RESULT_CODE', "class":"center"},
		{"title":'不良原因', "data":'QC_RESULT', "class":"center"},
		{"title":'原因分类', "data":'RETURN_REASON_TYPE_NAME', "class":"center"},
		{"title":'质检员', "data":'QC_PEOPLE', "class":"center"},
		{"title":'质检日期', "data":'QC_DATE', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'特殊库存类型', "data":'SOBKZ',"class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'供应商名称', "data":'LIKTX',  "class":"center"},
		{"title":'需求跟踪号', "data":'BEDNR', "class":"center"},
		{"title":'收货/创建日期', "data":'RECEIPT_DATE', "class":"center"},
		{"title":'库存来源', "data":'STOCK_SOURCE',"class":"center"},
		{"title":'标签', "data":'LABEL_NO', "class":"center"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	$.ajax({
		url:baseUrl + 'qcQuery/resultList',
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"检验结果清单");	
}