var listdata = [];
var lastrow,lastcell; 
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'cswlms/dispatchingBillPicking/listQueRen',
    	datatype: "local",
		data: listdata,
        colModel: [		
			{ label: '包装号', name: 'BARCODE', index: 'BARCODE', width: 160 },
			{ label: '拣配单号', name: 'DISPATCHING_NO', index: 'DISPATCHING_NO', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUSNAME', width: 70,
				formatter:function(val){	// 
            		if("01"==val)return "已发布";
            		if("02"==val)return "待打印";
            		if("03"==val)return "拣配中";
            	}	
			},
			{ label: '物料号', name: 'MATERIAL_CODE', index: 'MATERIAL_CODE', width: 100 },
			{ label: '物料描述', name: 'MATERIAL_DESC', index: 'MATERIAL_DESC', width: 200 },
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 100 },
			{ label: '供应商代码', name: 'VENDOR_CODE', index: 'VENDOR_CODE', width: 100 },
			{ label: '供应商简称', name: 'VENDOR_NAME', index: 'VENDOR_NAME', width: 150 },
			{ label: '上线模式', name: 'TYPE', index: 'TYPENAME', width: 70,
				formatter:function(val){	// 
            		if("01"==val)return "JIT";
            		if("02"==val)return "JIS";
            		if("03"==val)return "EKANBAN";
            	}	
			},
			{ label: '数量', name: 'QUANTITY', index: 'QUANTITY', width: 90 },
			{ label: '仓管员', name: '', index: '', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUS', width: 70,hidden:true},
			{ label: '线别', name: 'LINE_CATEGORY', index: 'LINE_CATEGORY', width: 80 },
			{ label: '剩余交接时间', name: 'LEFT_HANDOVER_TIMES', index: 'LEFT_HANDOVER_TIMES', width: 120 },
			{ label: '', name: 'XJ_QTY', index: 'XJ_QTY', width: 100 ,hidden:true},
			{ label: '', name: 'ITEM_NO', index: 'ITEM_NO', width: 100,hidden:true},
			{ label: '', name: 'COMPONENT_NO', index: 'COMPONENT_NO', width: 100,hidden:true},
			{ label: '', name: 'FROM_PLANT_CODE', index: 'FROM_PLANT_CODE', width: 100,hidden:true},
			{ label: '', name: 'LGORT', index: 'LGORT', width: 100,hidden:true},
			{ label: '', name: 'TYPE', index: 'TYPE', width: 150 ,hidden:true},
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
	          
		},
		save:function(){
			
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要拣配确认的数据!");
				return;
			}
			
			var werksstr ="";
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				if(werksstr!=""){
					werksstr= werksstr+","+row.FROM_PLANT_CODE;
				}else{
					werksstr=row.FROM_PLANT_CODE;
				}
				
			}
			
			//验证交接模式
			var business_rows=[];
			business_rows[0]="46";
			$.ajax({
	        	  url:baseUrl + "config/handoverType/wmsCHandoverTypelist",
	        	  data:{
	        		  "WERKS":werksstr,
	        		  "WH_NUMBER":"",
	        		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows)
	        		  },
	        	  success:function(resp){
	        		  if(resp.handover_type_flag!=""){//存在交接类型不一致的情况
	        			  js.alert("存在多个业务交接模式不一致！");
	        		  }else{
	        			
	        		  if(resp.business_strName!=""){//存在 找不到的业务类型
	        			  js.alert("交接业务类型"+resp.business_strName+"没有配置交接权限！");
							 return false;
	        		  }else{
	        			  if(resp.result.length>0){
			        		  if(resp.result[0].HANDOVER_TYPE=='00'){//无交接过账
			        			  var user="";
			        			  var username=""
			        				  dispatchingcheck(user,username);
			        		  }else{
			        			  commonHandover(validateUser,validateUser,resp.result[0].HANDOVER_TYPE);
			        		  }
			        		  
			         	  }else{
			         		 js.alert("交接模式没有配置！");
							 return false;
			         	  }
	        		  }
		        	  
	        	  }
        		  }
	          })
			
			
		},
		querylistbydanhao:function(){
			queryList();
		},
		initData:function(){}
	}
});

function queryList(){
	$.ajax({
		url:baseURL+"cswlms/dispatchingBillPicking/listQueRen",
		dataType : "json",
		type : "post",
		data : {
			"FROM_PLANT_CODE":$("#werks").val(),
			"DISPATCHING_NO":$("#dispatchingNo").val(),
			"LINE_CATEGORY":$("#lineCategory").val(),
			"PLANT_CODE":$("#plantCode").val(),
			"STATUS":'03'
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				listdata = response.result;
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
			}else{
				
				js.showMessage(response.msg,null,null,1000*600000);
			}
			
			
			$("#dispatching_count").html(listdata.length);
			
		}
	});
	

}

$("#btnquerydispatchingqueren").click(function () {
	queryList();
});



function dispatchingcheck(user,username){
	var handover="";
	if(user!=""){// 非 无交接过账 要验证
		handover=user+":"+username;
	}
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要拣配确认的数据!");
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
		url:baseUrl + "cswlms/dispatchingBillPicking/checkQueRen",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				dispatchingupdate(handover);
				js.closeLoading();
			}else{
				alert(resp.msg);
				js.closeLoading();
			}
		}
	});
	js.closeLoading();
}

function dispatchingupdate(handover){
	
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要拣配确认的数据!");
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
		url:baseUrl + "cswlms/dispatchingBillPicking/updateQueRen",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows),
			"HANDOVER":handover
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("拣配确认成功!  "+resp.msg,null,null,1000*600000);
				queryList();
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				alert(resp.msg);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});
	js.closeLoading();

}

function validateUser(user,username){
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要提交的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	
	var business_rows=[];
	business_rows[0]="46";
	
	//验证交接人员信息
	if(user==""||username==""){
		js.alert("交接人员不能为空！");
		return ;
	}
	
	$.ajax({
  	  url:baseUrl + "config/handover/wmsCHandoverlist",
  	  data:{
  		  "WERKS":$("#werks").val(),
  		  "WH_NUMBER":"",
  		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows),
  		  "STAFF_NUMBER":user
  		  },
  	  success:function(resp){
      	  if(resp.result.length>0){
      		dispatchingcheck(user,username);
       	  }else{
       		 js.alert("交接人员没有对应权限！");
			 return ;
       	  }
  	  }
    });
}






