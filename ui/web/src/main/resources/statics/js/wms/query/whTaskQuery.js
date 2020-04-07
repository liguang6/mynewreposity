var vm = new Vue({
	el:'#rrapp',
	data:{
		wh_list:[],
		werks:'',
		relatedareaname:[],//仓管员
		whNumber:''
	},
	watch:{
		whNumber:{
			handler:function(newVal,oldVal){
				$.ajax({
		        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
		        	  data:{"WERKS":vm.werks,"WH_NUMBER":newVal},
		        	  success:function(resp){
		        		 vm.relatedareaname = resp.result;
		        	  }
		          })
			}
		},
		werks : {
			handler:function(newVal,oldVal){
				
				$.ajax({
					url:baseURL+"common/getWhDataByWerks",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":newVal
					},
					async: true,
					success: function (response) { 
						vm.wh_list=response.data
						vm.whNumber=response.data[0]['WH_NUMBER']
					}
				});
				}
			}
		},
	created:function(){
		this.werks=$("#werks").val();
		$.ajax({
			url:baseURL+"common/getWhDataByWerks",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":this.werks
			},
			async: true,
			success: function (response) {
				var strs = "";
				this.whNumber=response.data[0].WH_NUMBER
				this.wh_list=response.data;
			}
		});
	},
	methods: {
		query: function () {
			//$("#searchForm").submit();
		},
		exp: function () {
			export2Excel();
		}
	}
});
$(function () {	
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	$("#createDateStart").val(formatDate(startDate));
	$("#createtDateEnd").val(formatDate(now));
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'单据号', name:'REFERENCE_DELIVERY_NO', width:110, align:"center"},
			{header:'行项目', name:'REFERENCE_DELIVERY_ITEM', width:60, align:"center"},
			{header:'仓库任务号', name:'TASK_NUM', width:110, align:"center"},
			{header:'状态', name:'WT_STATUS', width:75, align:"center",
				formatter: function(item, index){
	    		    return item=='00' ? "未清" : (item=='01' ? "部分确认" : (item=='02' ? "已确认" : (item=='03' ? "已取消" : (item=='04' ? "部分过账" : (item=='05' ? "已过账" : "")
	    		    		))))}},
			{header:'料号', name:'MATNR', width:90, align:"center"},
			{header:'物料描述', name:'MAKTX', width:160, align:"center"},	
			{header:'单位', name:'UNIT', width:50, align:"center"},
			{header:'需求数量', name:'QUANTITY', width:65, align:"center"},
			{header:'推荐数量', name:'CONFIRM_QUANTITY', width:75, align:"center"},
			{header:'实拣数量', name:'CONFIRM_QUANTITY', width:75, align:"center"},
			{header:'已过账数量', name:'REAL_QUANTITY', width:85, align:"center"},
			{header:'批次', name:'BATCH', width:90, align:"center"},
			{header:'源存储区', name:'FROM_STORAGE_AREA', width:90, align:"center"},
			{header:'源储位', name:'FROM_BIN_CODE', width:90, align:"center"},
			{header:'供应商', name:'LIFNR', width:85, align:"center"},
			{header:'库位', name:'LGORT', width:70, align:"center"},
			{header:'库存类型', name:'SOBKZ', width:65, align:"center"},
			{header:'仓管员', name:'CONFIRMOR', width:70, align:"center"},
			{header:'条码号', name:'LABEL_NO', width:110, align:"center"},
			{header:'物流器具号', name:'MOULD_NO', width:80, align:"center"},
			{header:'核销标记', name:'HX_FLAG', width:65, align:"center"},
			{header:'目的存储区', name:'TO_STORAGE_AREA', width:80, align:"center"},
			{header:'目的储位', name:'TO_BIN_CODE', width:100, align:"center"},
			{header:'操作人', name:'EDITOR', width:80, align:"center"},
			{header:'操作时间', name:'EDIT_DATE', width:80, align:"center"},
			{header:'创建时间', name:'CREATE_DATE', width:80, align:"center"},
			
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		width:2500,
		showCheckbox:true,
		rowList : [  15,30 ,50],
        rownumWidth: 35, 
        multiselect: true,
	});
	var hideCol=[];
	DynamicColume(hideCol,"显示列..","#dataGrid","#example-getting-started");
});
function export2Excel(){
	var column= [
		{title: '单据号', data:'REFERENCE_DELIVERY_NO' },
		{title: '仓库任务行项目', data:'REFERENCE_DELIVERY_ITEM'},
		{title: '仓库任务号', data:'TASK_NUM'},
		{title: '状态', data:'WT_STATUS'},
		{title: '料号', data:'MATNR'},
		{title: '物料描述', data:'MAKTX'},			
		{title: '单位', data:'UNIT'},
		{title: '需求数量', data:'QUANTITY'},
		{title: '推荐数量', data:'CONFIRM_QUANTITY'},
		{title: '实拣数量', data:'CONFIRM_QUANTITY'},
		{title: '已过账数量',data:'REAL_QUANTITY'},
		{title: '批次',  data:'BATCH'},
		{title: '源储位', data:'FROM_STORAGE_AREA'},
		{title: '源储位名称',data:'FROM_BIN_CODE'},
		{title: '供应商', data:'LIFNR'},
		{title: '库位', data:'LGORT'},
		{title: '库存类型', data:'SOBKZ'},
		{title: '仓管员',  data:'SO_NO'},
		{title: '条码号', data:'LABEL_NO'},
		{title: '物流器具号', data:'MOULD_NO'},
		{title: '核销标记', data:'HX_FLAG'},
		{title: '目的储位', data:'TO_STORAGE_AREA'},
		{title: '目的储位名称', data:'TO_BIN_CODE'},
		{title: '操作人', data:'EDITOR'},
		{title: '操作时间', data:'EDIT_DATE'},
		{title: '创建时间', data:'CREATE_DATE'},	
	] 
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/taskQuery/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    console.log("results",results);
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"仓库任务明细");	
}

function query(){
	$("#AUTHORIZE_NAME").val($("#whManager").find("option:selected").text());
	var optionCol=['TASK_NUM','REFERENCE_DELIVERY_ITEM','LABEL_NO','MOULD_NO','HX_FLAG','TO_STORAGE_AREA','TO_BIN_CODE','EDITOR','EDIT_DATE','CREATE_DATE']
	$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).showCol(optionCol).trigger('reloadGrid'); 
	var hideCol=[];
	DynamicColume(hideCol,"显示列..","#dataGrid","#example-getting-started");
}
