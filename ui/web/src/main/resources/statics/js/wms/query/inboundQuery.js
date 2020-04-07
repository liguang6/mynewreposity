var last_scan_ele="";
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		businessList:[],
	},
	created:function(){
		$(".poNo").hide();
		$(".productNo").hide();
		this.businessList = getBusinessList("03");
		$.ajax({
			url:baseURL+"common/getWhDataByWerks",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val()
			},
			async: true,
			success: function (response) { 
				$("#wh").empty();
				var strs = "";
			    $.each(response.data, function(index, value) {
			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
			    });
			    $("#wh").append(strs);
			}
		});
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		detail: function (inboundNo,werks,whNumber,inBoundType) {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 进仓单明细</i>',
					baseURL+'wms/query/inboundDetailQuery.html?inboundNo='+inboundNo+'&werks='+werks+'&whNumber='+whNumber+'&inBoundType='+inBoundType, 
					true, true);
		},
		del:function(inboundNo,status){
			$.ajax({
				url:baseURL+"query/inboundQuery/delete",
				dataType : "json",
				type : "post",
				data : {
					INBOUND_NO:inboundNo,
					INBOUN_STATUS:status
				},
				success: function (response) { 
				    if(response.code==0){
				    	js.showMessage('操作成功');
				    	$("#searchForm").submit();
				    }else{
				    	js.showErrorMessage('删除失败');
				    }
				}
			});
		},
		onPlantChange:function(event){
		  //工厂变化的时候，更新库存下拉框
          var werks = event.target.value;
          $.ajax({
  			url:baseURL+"common/getWhDataByWerks",
  			dataType : "json",
  			type : "post",
  			data : {
  				"WERKS":werks
  			},
  			async: true,
  			success: function (response) { 
  				$("#wh").empty();
  				var strs = "";
  			    $.each(response.data, function(index, value) {
  			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
  			    });
  			    $("#wh").append(strs);
  			}
  		});
       },
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	var endDate=new Date(now.getTime()+1*24*3600*1000);
	$("#createDateStart").val(formatDate(startDate));
	$("#createDateEnd").val(formatDate(endDate));
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'单据编号', name:'INBOUND_NO', width:120, align:"center",formatter:function(val, obj, row, act){
				var actions = [];
				var link = '<a onClick=vm.detail("'+val+'","'+row.WERKS+'","'+row.WH_NUMBER+'","'+row.INBOUND_TYPE+'") title="明细" style="cursor: pointer;">'+val+'</a>';
				actions.push(link);
			    return actions.join('');
			}},
			{header:'单据类型', name:'INBOUND_TYPE', width:100, align:"center"},
			{header:'进仓单类型', name:'BUSINESS_NAME_DESC', width:185, align:"center"},
			{header:'工厂', name:'WERKS', width:90, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:90, align:"center"},
			{header:'创建人', name:'CREATOR', width:90, align:"center"},
			{header:'创建时间', name:'CREATE_DATE', width:150, align:"center"},
			{header:'状态', name:'INBOUN_STATUS_DESC', width:110, align:"center"},
			{header: '操作', name: 'actions', width: 138 ,align:'center',formatter:function(val, obj, row, act){
				var actions = [];
				// ”已创建“和” 部分进仓“ 才允许删除
				if(row.INBOUN_STATUS=='00' || row.INBOUN_STATUS=='01'){				
					var link = "<a href='#' onClick=vm.del('"+row.INBOUND_NO+"','"+row.INBOUN_STATUS+"') title='删除'><i class='fa fa-trash'></i>&nbsp;</a>";
					actions.push(link);    
				}
				return actions.join('');
			}}
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		rowList : [15,30 ,50],
        rownumWidth: 65, 
	});
});
function export2Excel(){
	var column=[   
		{"title":'批次', "data":'BATCH', "class":"center"},
		{"title":'标签号', "data":'LABEL_NO', "class":"center"},
		{"title":'工厂', "data":'WERKS',  "class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER',  "class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX',  "class":"center"},	
		{"title":'数量', "data":'BOX_QTY', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'库位', "data":'LGORT', "class":"center"},
		{"title":'储位代码', "data":'BIN_CODE', "class":"center"},
		{"title":'状态', "data":'LABEL_STATUS', "class":"center"},
		{"title":'关键零部件打印数量', "data":'QUANTITY', "class":"center"},
		{"title":'采购订单号', "data":'PO_NO', "class":"center"},
		{"title":'行项目', "data":'PO_ITEM_NO', "class":"center"},
		{"title":'收货单号', "data":'RECEIPT_NO', "class":"center"},
		{"title":'行项目', "data":'RECEIPT_ITEM_NO', "class":"center"},
		{"title":'进仓单号', "data":'INBOUND_NO', "class":"center"},
		{"title":'行项目', "data":'INBOUND_ITEM_NO',  "class":"center"},
		{"title":'备注', "data":'SAP_MATDOC_NO', "class":"center"},
      ];
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/inboundQuery/list",
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
	getMergeTable(results,column,rowGroups,"标签记录");	
}


function funFromjs(result) {
	var str_arr = result.split(";");
	if(str_arr.length>0){
		var matnr = "";
		for(var i=0;i<str_arr.length;i++){
			var str = str_arr[i];
			if(str.split(":")[0]=='M'){
				matnr=str.split(":")[1].split("/")[0];
			}
		}
		
		var e = $.Event('keydown');
		e.keyCode = 13;
		$(last_scan_ele).val(matnr).trigger(e);
		$("#searchForm").submit();
	}
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = $("#" + ele);
	if(IsPC() ){
		$(last_scan_ele).focus();
		return false;
	}
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

