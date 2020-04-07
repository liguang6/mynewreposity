var listdata = [];
var lastrow,lastcell; 
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'cswlms/dispatchingBillPicking/listFeiJIS',
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '到货口', name: 'WAITING_LOCATION', index: 'WAITING_LOCATION', width: 80 }, 
			{ label: '包装号', name: 'BARCODE', index: 'BARCODE', width: 160 },
			{ label: '拣配单号', name: 'DISPATCHING_NO', index: 'DISPATCHING_NO', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUSNAME', width: 70,
				formatter:function(val){	// 
            		if("01"==val)return "已发布";
            		if("02"==val)return "待打印";
            	}	
			},
			{ label: '物料号', name: 'MATERIAL_CODE', index: 'MATERIAL_CODE', width: 100 },
			{ label: '物料描述', name: 'MATERIAL_DESC', index: 'MATERIAL_DESC', width: 200 },
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 100 },
			{ label: '供应商简称', name: 'VENDOR_NAME', index: 'VENDOR_NAME', width: 150 },
			{ label: '数量', name: 'QUANTITY', index: 'QUANTITY', width: 90 },
			{ label: '拣配区', name: 'PICKING_ADDRESS', index: 'PICKING_ADDRESS', width: 120 },
			{ label: '仓管员', name: 'MANAGER', index: 'MANAGER', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUS', width: 70,hidden:true},
			{ label: '打印剩余时间', name: 'LEFT_PRINT_TIMES', index: 'LEFT_PRINT_TIMES', width: 100 },
			{ label: '最迟交接时间', name: 'HANDOVER_DATE', index: 'HANDOVER_DATE', width: 120 },
			{ label: '线别', name: 'LINE_CATEGORY', index: 'LINE_CATEGORY', width: 80 },
			{ label: '是否试装', name: 'TRIAL_FLAG', index: 'TRIAL_FLAG_NAME', width: 120,
				formatter:function(val){	// 
            		if("0"==val)return "否";
            		if("1"==val)return "是";
            	}	
			},
			{ label: '是否试装', name: 'TRIAL_FLAG', index: 'TRIAL_FLAG', width: 120,hidden:true },
			{ label: '', name: 'ITEM_NO', index: 'ITEM_NO', width: 120 ,hidden:true},
			{ label: '', name: 'COMPONENT_NO', index: 'COMPONENT_NO', width: 120 ,hidden:true},
			{ label: '', name: 'FROM_PLANT_CODE', index: 'FROM_PLANT_CODE', width: 120 ,hidden:true},
			{ label: '', name: 'VENDOR_CODE', index: 'VENDOR_CODE', width: 120 ,hidden:true},
			{ label: '', name: 'ID', index: 'ID', width: 120 ,hidden:true}

        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            /*var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }*/
        },
        beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			   
			}
        
		
    });

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		sortTypeList:[],
		sortTypeName:"",
		relatedareaname:[],//仓管员
		sortType:""
	},
	
	created:function(){
		var werks = $("#werks").val();
		
      	$.ajax({
        	  url:baseUrl + "cswlms/dispatchingBillPicking/listAssembly",
        	  data:{F_WERKS:werks},
        	  success:function(resp){
        		  vm.sortTypeList =resp.data;
        		  vm.sortType=vm.sortTypeList[0].SORT_NUM;
        	  }
          });
		
	},
	methods:{
		query:function(){
			$("#searchForm").submit();
		},
		
		
		onStatusChange:function(event,id){},
		onPlantChange:function(event){
			
	          var werks = event.target.value;
	          $.ajax({
	        	  url:baseUrl + "cswlms/dispatchingBillPicking/listAssembly",
	        	  data:{F_WERKS:werks},
	        	  success:function(resp){
	        		  vm.sortTypeList =resp.data;
	        		  
	        		  vm.sortType=vm.sortTypeList[0].SORT_NUM;
	        		 
	        		
	        	  }
	          });
	          
	          $.ajax({
	        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
	        	  data:{"WERKS":werks},
	        	  success:function(resp){
	        		 vm.relatedareaname = resp.result;
	        	  }
	          })
	          
		},
		feijisdispatching:function(){
			FeiJISdispatching();
		},
		feijisprint:function(){
			printAjax();
		},
		initData:function(){}
	}
});

function queryList(STATUS){
	$.ajax({
		url:baseURL+"cswlms/dispatchingBillPicking/listFeiJIS",
		dataType : "json",
		type : "post",
		data : {
			"FROM_PLANT_CODE":$("#werks").val(),
			"MATERIAL_CODE":$("#mateirlNo").val(),
			"LINE_CATEGORY":$("#lineCategory").val(),
			"PLANT_CODE":$("#plantCode").val(),
			"STATUS":STATUS,
			"WAITING_LOCATION":$("#waitLocation").val(),
			"AUTHORIZE_CODE":$("#whManager").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				listdata = response.result;
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
			}else{
				alert(response.msg)
			}
		}
	});
	

}

$("#btnquerydispatchingFeiJIS").click(function () {
	queryList("");
});

/**
 * 显示单据明细
 * @param asnno
 */
function show(DISPATCHING_NO){
	console.info(">>>"+DISPATCHING_NO);
	var options = {
    		type: 2,
    		maxmin: true,
    		shadeClose: true,
    		title: '<i aria-hidden="true" class="glyphicon glyphicon-search">&nbsp;</i>拣配单明细',
    		area: ["1060px", "500px"],
    		content: "wms/cswlms/dispatchingJISDetail.html",  
    		btn: [],
    		success:function(layero, index){
    			var win = top[layero.find('iframe')[0]['name']];
    			win.vm.getDetail(DISPATCHING_NO);
    		},
    	};
    	options.btn.push('<i class="fa fa-close"></i> 关闭');
    	options['btn'+options.btn.length] = function(index, layero){
    		js.layer.close(index);
    	};
     js.layer.open(options);
}

function FeiJISdispatching(){
	
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要拣配下架的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		rows[id] = row;
	}
	
	js.loading();
	
	$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/feijispicking",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("拣配成功!  "+resp.msg,null,null,1000*600000);
				//列表 单号缓存都置位空
				//$("#dataGrid").jqGrid("clearGridData", true);
				queryList("02");
				//
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				js.alert(resp.msg);
				js.showErrorMessage("拣配失败!  "+resp.msg,null,null,1000*600000);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});
	js.closeLoading();

}

function printAjax(){
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要打印的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		rows[id] = row;
		
	}
	
	$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/printfeijis",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("打印成功!  "+resp.msg,null,null,1000*600000);
				//列表 单号缓存都置位空
				//$("#dataGrid").jqGrid("clearGridData", true);
				feiJISPrint(rows);//
				queryList("02");
				//
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				js.alert(resp.msg);
				js.showErrorMessage("打印成功!  "+resp.msg,null,null,1000*600000);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});
}

function feiJISPrint(rows){
	js.loading();
	
	var barcode_str="";
	$.each(rows,function(i,row){
		if(""==barcode_str){
			barcode_str=row.BARCODE;
		}else{
			barcode_str=barcode_str+","+row.BARCODE;
		}
		
	});
	
	$("#BARCODE").val(barcode_str);
	$("#printButton").click();
	
	$(".btn").attr("disabled", false);
	js.closeLoading();
}






