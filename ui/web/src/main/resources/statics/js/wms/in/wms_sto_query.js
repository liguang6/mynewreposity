var listdata = [];
var listdata1 = [];
var lastrow,lastcell,lastcellname,lastrow1,lastcell1,lastcellname1;;
$(function(){
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [						
			{ label: '采购订单', name: 'PO_NO', index: 'PO_NO', width: 80, sortable:false, align: 'center'},
			{ label: '订单行项目', name: 'PO_ITEM', index: 'PO_ITEM', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '物料号', name: 'MAT_NO', index: 'MAT_NO', width: 80, sortable:false, align: 'center'},
			{ label: '物料描述', name: 'MAT_DESC', index: 'MAT_DESC', width: 80, sortable:false, align: 'center'},			
			{ label: '过账数量', name: 'ITEM_QTY', index: 'ITEM_QTY', width: 80, sortable:false, align: 'center'},			
			{ label: '单位', name: 'UNIT_NO', index: 'UNIT_NO', width: 80, sortable:false, align: 'center'},
			{ label: '接收工厂', name: 'FACT_NO', index: 'FACT_NO', width: 80, sortable:false, align: 'center'},	
			{ label: '仓库', name: 'WHCD_NO', index: 'WHCD_NO', width: 80, sortable:false, align: 'center'},	
			{ label: '接收库位', name: 'LGORT_NO', index: 'LGORT_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '供应商', name: 'PROVIDER_CODE', index: 'PROVIDER_CODE', width: 80, sortable:false, align: 'center'},
			{ label: '供应商名称', name: 'PROVIDER_NAME', index: 'PROVIDER_NAME', width: 80, sortable:false, align: 'center'},
			{ label: '箱数', name: 'BOXNO', index: 'BOXNO', width: 80, sortable:false, align: 'center'},		
			{ label: 'SAP交货单', name: 'SAP_OUT_NO', index: 'SAP_OUT_NO', width: 80, align: 'center', sortable:false }, 
			{ label: 'SAP交货单行项目', name: 'SAP_OUT_ITEM_NO', index: 'SAP_OUT_ITEM_NO', width: 160, align: 'center', sortable:false }, 			
			{ label: '批次', name: 'BYD_BATCH', index: 'BYD_BATCH', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '行项目', name: 'ITEM_NO', index: 'ITEM_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '条码', name: 'BARCODE_NO', index: 'BARCODE_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '预约送货时间', name: 'BOOKING_DATE', index: 'BOOKING_DATE', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '收货地址', name: 'WHCD_NO', index: 'WHCD_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: 'wms凭证号', name: 'WMS_NO', index: 'WMS_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '联系人', name: 'CONTACTS', index: 'CONTACTS', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '联系地址', name: 'ADDRESS', index: 'ADDRESS', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '联系电话', name: 'TEL', index: 'TEL', width: 80, sortable:false, align: 'center',hidden:true},
        ],
		viewrecords: true,
		showRownum:false,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        multiselect: true,
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow: function (rowid, e) {  // 点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			},
    });
	
	$("#dataGrid1").dataGrid({
    	datatype: "local",
		data: listdata1,
        colModel: [		
        	{ label: '包装箱号', name: 'BARCODE_NO', index: 'BARCODE_NO', width: 80, sortable:false, align: 'center'},
        	{ label: '物料号', name: 'MAT_NO', index: 'MAT_NO', width: 80, sortable:false, align: 'center'},
			{ label: '物料描述', name: 'MAT_DESC', index: 'MAT_DESC', width: 80, sortable:false, align: 'center'},	
			{ label: '单位', name: 'UNIT_NO', index: 'UNIT_NO', width: 80, sortable:false, align: 'center'},
			{ label: '供应商', name: 'PROVIDER_CODE', index: 'PROVIDER_CODE', width: 80, sortable:false, align: 'center'},
			{ label: '供应商名称', name: 'PROVIDER_NAME', index: 'PROVIDER_NAME', width: 80, sortable:false, align: 'center'},
			{ label: '批次', name: 'BYD_BATCH', index: 'BYD_BATCH', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '条码数量', name: 'BOX_QTY', index: 'BOX_QTY', width: 80, sortable:false, align: 'center'},
			{ label: '采购订单', name: 'PO_NO', index: 'PO_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '订单行项目', name: 'PO_ITEM', index: 'PO_ITEM', width: 80, sortable:false, align: 'center',hidden:true},
			// { label: '每箱数量', name: 'QTY', index: 'QTY', width: 80,
			// sortable:false, align: 'center',editable : true,},
			{ label: '工厂', name: 'FACT_NO', index: 'FACT_NO', width: 80, sortable:false, align: 'center',hidden:true},	
			{ label: '库位', name: 'LGORT_NO', index: 'LGORT_NO', width: 80, sortable:false, align: 'center',hidden:true},
			// { label: '箱数', name: 'QTY', index: 'QTY', width: 80,
			// sortable:false, align: 'center',editable : true,},
			{ label: 'SAP交货单', name: 'SAP_OUT_NO', index: 'SAP_OUT_NO', width: 80, align: 'center', sortable:false,hidden:true},
			{ label: 'SAP交货单行项目', name: 'SAP_OUT_ITEM_NO', index: 'SAP_OUT_ITEM_NO', width: 160, align: 'center', sortable:false,hidden:true},	
			{ label: '行项目', name: 'ITEM_NO', index: 'ITEM_NO', width: 80, sortable:false, align: 'center',hidden:true},
		
        ],
		viewrecords: true,
		showRownum:false,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        multiselect: true,
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow1=iRow;
			lastcell1=iCol;
			lastcellname1=cellname;
		},
		beforeSelectRow: function (rowid1, e1) {  // 点击复选框才选中行
			   var $myGrid1 = $(this),  
			   i1 = $.jgrid.getCellIndex($(e1.target).closest('td')[0]),  
			   cm1 = $myGrid1.jqGrid('getGridParam', 'colModel'); 
			   if(cm1[i1].name == 'cb'){
				   $('#dataGrid1').jqGrid('setSelection',rowid1);
			   }			   		
			   return (cm1[i1].name == 'cb'); 
			},
    });
	$("#tab2").css('display','none'); 
	if($("#stoNo").val())
	  queryList()
});
			
var vm = new Vue({
	el:"#vue",
	data:{

	},
	watch:{
		whNumber:{
			handler:function(newVal,oldVal){
				$.ajax({
		        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
		        	  data:{"WERKS":$("#werks").val(),"WH_NUMBER":newVal},
		        	  success:function(resp){
		        		 vm.relatedareaname = resp.result;
		        	  }
		          })
			}
		}
	},
	created:function(){
		// 初始化查询仓库
		var plantCode = $("#werks").val();
		
        // 初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{"WERKS":plantCode},
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER// 方便 v-mode取值
    		 }
      	  }
        });
	},

			methods:{
				query:function(){
					$("#searchForm").submit();
				},
				printSto:function(){
					// 获取选中表格数据
					var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
					if(ids.length == 0){
						alert("当前还没有勾选任何行项目数据！");
						return false;
					}
					var saveData = [];
					for (var i = 0; i < ids.length; i++) {
						var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
						saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
					}
					if(saveData.length>0){
						let params = {"stoNo":$("#stoNo").val(),"itemList":JSON.stringify(saveData)}
						$("#stoList").val(JSON.stringify(params));
						$("#printButton").click();
					}
					
				},	
				printLabel:function(){
					// 获取选中表格数据
					var ids = $("#dataGrid1").jqGrid("getGridParam", "selarrrow");
					if(ids.length == 0){
						alert("当前还没有勾选任何行项目数据！");
						return false;
					}
					var saveData = [];
					for (var i = 0; i < ids.length; i++) {
						var data=$("#dataGrid1").jqGrid('getRowData',ids[i]);
						saveData[i] = $("#dataGrid1").jqGrid('getRowData',ids[i]);
					}
					if(saveData.length>0){
						saveData.forEach(ele=>{
							  ele.LIKTX = ele.PROVIDER_NAME
							  ele.LIFNR = ele.PROVIDER_CODE
							  ele.LABEL_NO = ele.BARCODE_NO
							  ele.MATNR = ele.MAT_NO
							  ele.MAKTX = ele.MAT_DESC
							  ele.PO_ITEM_NO = ele.PO_ITEM
							  ele.BATCH = ele.BYD_BATCH
							  ele.UNIT = ele.UNIT_NO
							  ele.WERKS = ele.FACT_NO
						})   
						$("#labelList").val(JSON.stringify(saveData));
						$("#LIKTX").val(saveData[0].LIKTX);
						$("#printLabelButton").click();
					}
					
				},	
				
				onStatusChange:function(event,id){},
				onPlantChange:function(event){

					  // 工厂变化的时候，更新仓库号下拉框
			          var plantCode = event.target.value;
			          if(plantCode === null || plantCode === '' || plantCode === undefined){
			        	  return;
			          }
			          
			          // 查询工厂仓库
			          $.ajax({
			        	  url:baseUrl + "common/getWhDataByWerks",
			        	  data:{"WERKS":plantCode},
			        	  success:function(resp){
			        		 vm.warehourse = resp.data;
			        		 if(resp.data.length>0){
			         			 vm.whNumber=resp.data[0].WH_NUMBER// 方便
																	// v-mode取值
			         		 }
			        		 
			        	  }
			          })
				},
				
				getVendorNoFuzzy:function(){
					getVendorNoSelect("#lifnr",null,null,$("werks").val());
				},				
				initDataGrid:function(){}
			}
});
function queryList(){
	$("#tab1").css('display','block'); 
	$("#tab2").css('display','none'); 
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/sto/querySto",
		dataType : "json",
		type : "post",
		data : {
			// "WERKS":$("#werks").val(),
			// "WH_NUMBER":$("#whNumber").val(),
			"stoNo":$("#stoNo").val()
		},
		async: true,
		success: function (response) { 
			if(response.code == "0"){
                let list = response.data;
                let params = []
                if(list.length>0){
                	$.ajax({
            			url:baseURL+"in/sto/queryWMSNo",
            			dataType : "json",
            			type : "post",
            			data :{
            				"DELIVERY_NO":list[0].DELIVERY_NO
            			},
            			async: false,
            			success: function (response) { 
            				if(response.code == "0"){
            	                let result = response.data;
            	               list.forEach(function(ele,index,array){
            	            	   if(result[0]&&result[0].WMS_NO)
            	            		   ele.WMS_NO = result[0].WMS_NO
            	            	　　})
            				}
            			}
            		});	
                	$.ajax({
            			url:baseURL+"in/sto/queryContact",
            			dataType : "json",
            			type : "post",
            			data :{
            				"WERKS":list[0].FACT_NO,
            				"WH_NUMBER":list[0].WHCD_NO
            			},
            			async: false,
            			success: function (response) { 
            				if(response.code == "0"){
            					if(response.data[0]){
            						let result = response.data[0];
                 	               list.forEach(function(ele,index,array){
                 	            	   ele.CONTACTS = result.CONTACTS
                 	            	   ele.TEL = result.TEL
                 	            	   COUNTRY = Boolean(result.COUNTRY)?result.COUNTRY:""
                 	            	   PROVINCE = Boolean(result.PROVINCE)?result.PROVINCE:""
                 	            	   CITY = Boolean(result.CITY)?result.CITY:""
                 	            	   REGION = Boolean(result.REGION)?result.REGION:""
                 	            	   STREET = Boolean(result.STREET)?result.STREET:""
                 	            	   ele.ADDRESS = COUNTRY+PROVINCE+CITY+REGION+STREET
                 	            	　　})
            					}
            				}
            			}
            		});	
                }
				else
					js.alert('送货单号不存在!');
				$("#dataGrid").jqGrid('clearGridData');
				$("#dataGrid").jqGrid('setGridParam',{ 
					datatype:'local', data : list, 
			    }).trigger("reloadGrid");
			}
			else{
				listdata = [];
				$("#dataGrid").jqGrid('clearGridData');
				js.alert(response.msg);
				$(".btn").attr("disabled", false);
			}
		}
	});
}

function queryLabel(event){
	$("#tab1").css('display','none'); 
	$("#tab2").css('display','block'); 
$.ajax({
	url:baseURL+"in/sto/querySto",
	dataType : "json",
	type : "post",
	data : {
		// "WERKS":$("#werks").val(),
		// "WH_NUMBER":$("#whNumber").val(),
		"stoNo":$("#stoNo").val()
	},
	async: true,
	success: function (response) { 
		if(response.code == "0"){
			listdata1 = []
			let list = response.data;
			list.forEach(ele=>{
				if(ele.BARCODE_NO.indexOf(',')!=-1){
					var temp= new Array();
				    temp=ele.BARCODE_NO.split(',');
				    let total = (Number)(ele.ITEM_QTY)
				    let perqty = (Number)(ele.FULL_BOX_QTY)
				    let per = Math.ceil(total/perqty)-1
				    let endQTY = total-per*perqty
				    for(let i=0;i<temp.length;i++){
				    	let obj={}
				    	if(i==temp.length-1){
				    		obj = {
						    		"BARCODE_NO":temp[i],"MAT_NO":ele.MAT_NO,"MAT_DESC":ele.MAT_DESC,"UNIT_NO":ele.UNIT_NO,"PROVIDER_CODE":ele.PROVIDER_CODE,"PROVIDER_NAME":ele.PROVIDER_NAME,	
						    		"BYD_BATCH":ele.BYD_BATCH,"BOX_QTY":endQTY,"PO_NO":ele.PO_NO,"PO_ITEM":ele.PO_ITEM,"FACT_NO":ele.FACT_NO,"LGORT_NO":ele.LGORT_NO,
						    		"SAP_OUT_NO":ele.SAP_OUT_NO,"SAP_OUT_ITEM_NO":ele.SAP_OUT_ITEM_NO,"ITEM_NO":ele.ITEM_NO
						    	}						    	
				    	}else{
				    		obj = {
						    		"BARCODE_NO":temp[i],"MAT_NO":ele.MAT_NO,"MAT_DESC":ele.MAT_DESC,"UNIT_NO":ele.UNIT_NO,"PROVIDER_CODE":ele.PROVIDER_CODE,"PROVIDER_NAME":ele.PROVIDER_NAME,	
						    		"BYD_BATCH":ele.BYD_BATCH,"BOX_QTY":ele.FULL_BOX_QTY,"PO_NO":ele.PO_NO,"PO_ITEM":ele.PO_ITEM,"FACT_NO":ele.FACT_NO,"LGORT_NO":ele.LGORT_NO,
						    		"SAP_OUT_NO":ele.SAP_OUT_NO,"SAP_OUT_ITEM_NO":ele.SAP_OUT_ITEM_NO,"ITEM_NO":ele.ITEM_NO
						    	}
				    	}
				    	listdata1.push(obj)	
				    }				   
				}else{
						ele.BOX_QTY = ele.ITEM_QTY
					    listdata1.push(ele)
				}
			})
			$("#dataGrid1").jqGrid('clearGridData');
			$("#dataGrid1").jqGrid('setGridParam',{ // 重新加载数据
				datatype:'local', data : listdata1, // newdata 是符合格式要求的需要重新加载的数据
		    }).trigger("reloadGrid");
			$(".btn").attr("disabled", false);
		}else{
			listdata1 = [];
			$("#dataGrid").jqGrid('clearGridData');
			js.alert(response.msg);
			$(".btn").attr("disabled", false);
		}
	}
});
}

$("#btnquery").click(function () {

	if(!$("#stoNo").val()){
		js.alert("送货单号不能为空！");
		return false;
	}
	queryList();
});