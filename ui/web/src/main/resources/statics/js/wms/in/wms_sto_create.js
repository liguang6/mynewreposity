var listdata = [];
var lastrow,lastcell,lastcellname;
$(function(){
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: 'SAP交货单', name: 'SAP_OUT_NO', index: 'SAP_OUT_NO', width: 80, align: 'center', sortable:false }, 
			{ label: 'SAP交货单行项目', name: 'SAP_OUT_ITEM_NO', index: 'SAP_OUT_ITEM_NO', width: 160, align: 'center', sortable:false }, 
			{ label: '采购订单', name: 'PO_NO', index: 'PO_NO', width: 80, sortable:false, align: 'center'},
			{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 80, sortable:false, align: 'center'},
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 80, sortable:false, align: 'center'},			
			{ label: '过账数量', name: 'QTY_SAP', index: 'QTY_SAP', width: 80, sortable:false, align: 'center'},
			{ label: '每箱数量', name: 'QTY', index: 'QTY', width: 80, sortable:false, align: 'center',editable : true,},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 80, sortable:false, align: 'center'},			
			{ label: '工厂', name: 'WERKS', index: 'WERKS', width: 80, sortable:false, align: 'center'},
			{ label: '供应商', name: 'LIFNR', index: 'LIFNR', width: 80, sortable:false, align: 'center'},
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 80, sortable:false, align: 'center'},
			{ label: '库位', name: 'LGORT', index: 'LGORT', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '仓库号', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '订单行项目', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 80, sortable:false, align: 'center',hidden:true},
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 80, sortable:false, align: 'center',hidden:true},
		
        ],
		showRownum:false,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        // autowidth:true,
        // shrinkToFit:false,
        // autoScroll: true,
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

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		warehourse:[],
		whNumber:"",
		deliveryTime:getCurDate(),
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
				save:function(){
					let flag = false
					$.ajax({
						url:baseUrl + "in/sto/checkExist",
						dataType : "json",
						type : "post",
						data : {
							"requirementNo":$("#requirementNo").val()						
						},
						async: false,
						success:function(resp){
							let list = resp.data
							if(list[0].COUNT)
								flag = true
						}
					});
					if(flag){
						js.alert("需求单号已创建!");
						return
					}
						
					let whNo = ''
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
					if(ids.length==0){
						js.alert("请选择要交接的数据!");
						return;
					}
					var rows = [];
					for(var i=0;i<ids.length;i++){
						var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);							  		
			  			if (Number(rowData.QTY_SAP) <= 0) {
			  				js.showErrorMessage("过账数量不能为空");
			  				return;
			  			}
			  			if (Number(rowData.QTY) <= 0) {
			  				js.showErrorMessage("每箱数量不能为空");
			  				return;
			  			}
			  			if(!$("#toWerks").val()){
			  				js.alert("接收工厂不能为空！");
			  				return false;
			  			}
			  			if(!$("#toLgort").val()){
			  				js.alert("接收库位不能为空！");
			  				return false;
			  			}
			  			rows.push(rowData);	
			  		}
					js.loading();
					$.ajax({
						url:baseUrl + "in/sto/checkAddr",
						dataType : "json",
						type : "post",
						data : {
							// "userWerks":$("#werks").val(),
							"toWerks":$("#toWerks").val(),
							"toLgort":$("#toLgort").val(),								
						},
						async: false,
						success:function(resp){
							let list = resp.data
							if(list.length)
								whNo = list[0].WH_NUMBER
						}
					});
					if(whNo){
						$.ajax({
							url:baseUrl + "in/sto/create",
							dataType : "json",
							type : "post",
							data : {
								// "userWerks":$("#werks").val(),
								"whNo":whNo,//接收仓库
								"requirementNo":$("#requirementNo").val(),
								"toWerks":$("#toWerks").val(),
								"toLgort":$("#toLgort").val(),
								"deliveryTime":$("#deliveryTime").val(),
								"itemList":JSON.stringify(rows)		
							},
							async: false,
							success:function(resp){
								if(resp.code == '0'){
									$("#dataGrid").jqGrid('clearGridData');
									var params = JSON.stringify(resp.stoNo)
									window.location.href = baseUrl + "in/sto/to_stoList?stoNo="+resp.data;
									js.closeLoading();
								}
							}
						});
					}else{
						js.closeLoading();
						js.alert('库位或工厂输入有误');
		  				return false;
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
	$("#customerCode").val('')
    $("#customerName").val('')
	$.ajax({
		url:baseURL+"in/sto/list",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"SAP_OUT_NO":$("#sap").val(),
			"requirementNo":$("#requirementNo").val()
		},
		async: false,
		success: function (response) { 
			if(response.code == "0"){
                let list = response.data
                let params = []
                if(list.length>0){
                	$("#customerCode").val(list[0].CUSTOMER)
                	list.forEach(ele=>{
                	params.push(ele.LIFNR)
                	$.ajax({
            			url:baseURL+"in/sto/queryLiktx",
            			dataType : "json",
            			type : "post",
            			data :"LIFNR="+ele.LIFNR,
            			async: false,
            			success: function (response) { 
            				if(response.code == "0"){
            	                let result = response.data
            	               list.forEach(function(ele,index,array){
            	            	   ele.LIKTX = result[0].NAME1
            	            	　　})
            				}
            			}
            		});	
                	})
                }
				else
					js.alert('需求单号不存在!');
				$("#dataGrid").jqGrid('clearGridData');
				$("#dataGrid").jqGrid('setGridParam',{ 
					datatype:'local', data : list, 
			    }).trigger("reloadGrid");
			}else{
				listdata = [];
				$("#dataGrid").jqGrid('clearGridData');
				js.alert(response.msg);
			}
		}
	});
	if($("#customerCode").val()){
		$.ajax({
			url:baseURL+"in/sto/queryCustomer",
			dataType : "json",
			type : "post",
			data : {
				"KUNNR":$("#customerCode").val()
			},
			async: false,
			success: function (response) { 
				if(response.code == "0"){
	                let list = response.data
					$("#customerName").val(list[0].NAME1)
				}
			}
		});	
	}

}

$("#btnquery").click(function () {

	if(!$("#requirementNo").val()){
		js.alert("需求单号不能为空！");
		return false;
	}
	queryList();
});