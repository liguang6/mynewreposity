var listdata = [];
$(function(){
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '物流配送单号', name: 'DELIVERY_NO', index: 'DELIVERY_NO', width: 100, align: "center"},
			{ label: '配送单行项目', name: 'DELIVERY_ITEM_NO', index: 'DELIVERY_ITEM_NO', width: 50, align: "center"},
			{ label: '物流需求号', name: 'REQUIREMENT_NO', index: 'REQUIREMENT_NO', width: 100 , align: "center"},
			{ label: 'WMS需求号', name: 'WMS_REQUIREMENT_NO', index: 'WMS_REQUIREMENT_NO', width: 160, align: "center"},
			{ label: 'WMS需求行项目号', name: 'WMS_REQUIREMENT_ITEM_NO', index: 'WMS_REQUIREMENT_ITEM_NO', width: 80, align: "center"},
			{ label: '仓库号', name: 'WAREHOUSE_CODE', index: 'WAREHOUSE_CODE', width: 80, align: "center"},
			{ label: '出库工厂', name: 'PLCD', index: 'PLCD', width: 80  , align: "center"},
			{ label: '发货库位', name: 'LOCD', index: 'LOCD', width: 80 , align: "center"},
			{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 100 , align: "center"},
			{ label: '物料描述', name: 'MATEDS', index: 'MATEDS', width: 100 , align: "center"},
			{ label: '数量', name: 'QUANTY', index: 'QUANTY', width: 50 , align: "center"},
			{ label: '工位', name: 'STATION', index: 'STATION', width: 50 , align: "center"},
			{ label: '储位', name: 'POU', index: 'POU', width: 100 , align: "center"},
			{ label: '产线', name: 'PRODUCTION_LINE', index: 'PRODUCTION_LINE', width: 50 , align: "center"},
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 80 , align: "center"},
			{ label: '配送路线', name: 'DELIVERYROUTE', index: 'DeliveryRoute', width: 50 , align: "center"},
			{ label: '上线路线', name: 'LINEFEEDINGROUTE', index: 'LINEFEEDINGROUTE', width: 50 , align: "center"},
			{ label: '配送模式/类型', name: 'DELIVERYTYPE', index: 'DELIVERYTYPE', width: 50 , align: "center"},
			{ label: '配送时间', name: 'DELIVERYTIME', index: 'DELIVERYTIME', width: 100 , align: "center"},
			{ label: '供应商代码', name: 'VENDOR_CODE', index: 'VENDOR_CODE', width: 80 , align: "center"},
			{ label: '供应商', name: 'VENDOR', index: 'VENDOR', width: 160 , align: "center"},
			{ label: '库存类型', name: 'STTPNM', index: 'STTPNM', width: 50 , align: "center"},
			{ label: '接收工厂', name: 'RECEIVEPLCD', index: 'RECEIVEPLCD', width: 80 , align: "center"},
			{ label: '接收方/库位', name: 'RECEIVELOCD', index: 'RECEIVELOCD', width: 50 , align: "center"},
			{ label: '标识单号', name: 'PRODUCT_NO', index: 'PRODUCT_NO', width: 80 , align: "center"},
			{ label: '备注', name: 'RMARK', index: 'RMARK', width: 160 , align: "center"},
			{ label: 'WMS配送单号', name: 'WMS_DELIVERY_NO', index: 'WMS_DELIVERY_NO', width: 60, hidden: true },
			{ label: '接收方类型', name: 'RECEIVELOCDTYPE', index: 'RECEIVELOCDTYPE', width: 160 ,hidden:true},
			{ label: '操作类型/业务类型', name: 'MOTYPE', index: 'MOTYPE', width: 160 ,hidden:true},
			{ label: 'taskNo', name: 'TASK_NUM', index: 'TASK_NUM', width: 160 ,hidden:true},
			{ label: '条码', name: 'LABEL_NO', index: 'LABEL_NO', width: 160,hidden:true},
			{ label: '物流器具', name: 'MOULD_NO', index: 'MOULD_NO', width: 160,hidden:true}
        ],
		viewrecords: true,
		showRownum:false,        
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
    });
});
			
var vm = new Vue({
	el:"#vue",
	data:{		
		warehourse:[],
		whNumber:"",		
	},

	created:function(){
		var plantCode = $("#werks").val();
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{
	  		 "WERKS":plantCode,
	  		 "MENU_KEY":"WLMS_DELIVERY"
	  	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER
    		 }
      	  }
        });
	},
	methods:{
		onPlantChange:function(event){
	        var plantCode = event.target.value;
	        if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	return;
	        }
	        $.ajax({
	        	url:baseUrl + "common/getWhDataByWerks",
	        	data:{"WERKS":plantCode,"MENU_KEY":"WLMS_DELIVERY"},
	        	success:function(resp){
	        		vm.warehourse = resp.data;
	        		if(resp.data.length>0){
	        			vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
	         		}
	        	}
	          })
		}
	}
});

function queryList(){
	$.ajax({
		url:baseURL+"out/delivery/list",
		dataType : "json",
		type : "post",
		data : {
//			"WERKS":$("#werks").val(),
//			"WH_NUMBER":$("#whNumber").val(),			
		},
		async: true,
		success: function (response) { 
			if(response.code == "0"){
				if(response.page.list.length === 0){
					layer.msg("未查到数据！",{time:3000});
				}
				listdata = listdata.concat(response.page.list);
				$("#dataGrid").jqGrid('clearGridData');
				$("#dataGrid").jqGrid('setGridParam',{ // 重新加载数据 
					datatype:'local', data : response.page.list, // newdata 是符合格式要求的需要重新加载的数据
			    }).trigger("reloadGrid");
			}else{
				alert(response.msg)
			}
		}
	});
}

$("#query").click(function () {
	if($("#No").val()!=''){//如果单号不为空
		queryList();
	}else{
		js.alert("单号不能为空！");
		return false;
	}
});
$("#confirm").click(function () {
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow')
	if(ids.length==0){
		js.alert("请选择数据!")
		return;
	}
	var rows = []
	for(var i=0;i<ids.length;i++){
		var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);	
			rowData.WERKS = $("#werks").val()	
			rows.push(rowData)
		}
	$.ajax({
		url:baseUrl + "out/delivery/delivery",			
		type : "post",
		contentType : 'application/json; charset=UTF-8',
		data : 	JSON.stringify(rows),
		async:  false ,
		success:function(resp){
			js.alert(resp.msg)
		}
	})
	
});

