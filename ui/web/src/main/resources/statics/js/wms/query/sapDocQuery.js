
var vm = new Vue({
	el:'#rrapp',
	data:{
		showDiv:false,
		lgort:[]
	},
	created:function(){
	  var werks = $("#werks").val();
      //查询库位
      $.ajax({
        url:baseUrl + "common/getAllLoList",
        data:{
        	"DEL":'0',
        	"WERKS":$("#werks").val(),
        },
        success:function(resp){
        	vm.lgort = resp.data;
        }
      });
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		onPlantChange:function(event){
		  //工厂变化的时候，更新库存下拉框
          var plantCode = event.target.value;
          if(plantCode === null || plantCode === '' || plantCode === undefined){
        	  return;
          }
	      //查询库位
	      $.ajax({
	        url:baseUrl + "common/getAllLoList",
	        data:{
	        	"WERKS":plantCode,
	        },
	        success:function(resp){
	        	vm.lgort = resp.data;
	        }
	       });
		},
		exp:function(){
			export2Excel();
		},
		detail:function(wmsNo,werks){
		  js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> WMS事务记录</i>',
				  baseUrl + 'wms/query/docQuery.html?wmsNo='+wmsNo+'&werks='+werks, 
			true, true);
		}
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#pstngDateStart").val(formatDate(startDate));
	$("#pstngDateEnd").val(formatDate(now));
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'PLANT', width:55, align:"center",frozen: true},
			{header:'库位', name:'STGE_LOC', width:55, align:"center",frozen: true},
			{header:'移动类型', name:'MOVE_TYPE', width:65, align:"center",frozen: true},
			{header:'记账日期', name:'DOC_DATE', width:90, align:"center"},
			{header:'WMS凭证', name:'REF_WMS_NO', width:130, align:"center",formatter:function(val, obj, row, act){
				var actions = [];
				var link = '<a onClick=vm.detail("'+val+'","'+row.PLANT+'") title="明细" style="cursor: pointer;">'+val+'</a>';
				actions.push(link);
			    return actions.join('');
			}},
			{header:'行项目', name:'REF_WMS_ITEM_NO', width:55, align:"center"},
			{header:'SAP凭证', name:'MAT_DOC', width:90, align:"center"},
			{header:'行项目', name:'MATDOC_ITM', width:55, align:"center"},
			{header:'过账日期', name:'PSTNG_DATE', width:90, align:"center"},
			{header:'料号', name:'MATERIAL', width:100, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'数量', name:'ENTRY_QNT', width:50, align:"center"},
			{header:'单位', name:'ENTRY_UOM', width:50, align:"center"},
			{header:'抬头文本', name:'HEADER_TXT', width:125, align:"center"},
		],	
		viewrecords: true,
        shrinkToFit:false,
		rowNum: 15,  
	});
});
function export2Excel(){
	var column=[      
		{"title":'工厂', "data":'PLANT', "class":"center"},
		{"title":'库位', "data":'STGE_LOC', "class":"center"},
		{"title":'移动类型', "data":'MOVE_TYPE', "class":"center"},
		{"title":'记账日期', "data":'DOC_DATE',"class":"center"},
		{"title":'WMS凭证', "data":'REF_WMS_NO', "class":"center"},
		{"title":'WMS凭证行项目', "data":'REF_WMS_ITEM_NO',"class":"center"},
		{"title":'SAP凭证', "data":'MAT_DOC', "class":"center"},
		{"title":'SAP凭证行项目', "data":'MATDOC_ITM', "class":"center"},
		{"title":'过账日期', "data":'PSTNG_DATE', "class":"center"},
		{"title":'料号', "data":'MATERIAL',"class":"center"},
		{"title":'批次', "data":'BATCH', "class":"center"},
		{"title":'数量', "data":'ENTRY_QNT', "class":"center"},
		{"title":'单位', "data":'ENTRY_UOM', "class":"center"},
		{"title":'抬头文本', "data":'HEADER_TXT',"class":"center"},
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/sapDocQuery/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
//	getMergeTable(results,column,rowGroups,"SAP凭证记录");	
	toExcel("SAP凭证记录",results,column);
}
