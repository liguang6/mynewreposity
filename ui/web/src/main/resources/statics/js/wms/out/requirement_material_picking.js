var grid = $("#dataGrid");
var mydata=[];
var lastrow,lastcell;
var vm = new Vue({
 	el:"#vue-app",
 	data:{
 		warehourse:[],
 		whNumber:"",
 		relatedareaname:[],//仓管员
 		lgortList:'',
 		business_name:''
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
// 		this.onPlantChange();
 		//初始化查询仓库
		var plantCode = $("#werks").val();
		
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
	      data:{
	  		 "WERKS":plantCode,
	  		 "MENU_KEY":"A70"
	  	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
    		 }
      		
      	  }
        });
 	},
 	methods:{
 		init:function(){
 			console.log(" ----> init");
// 			initGridTable();
 		},
 		// 查询
 		query:function(){
 		   var requirementNo=$("#requirementNo").val();
 		   console.log("requirementNo",requirementNo);
 		   if(requirementNo==''){
 			  layer.msg("请输入需求号！",{time:1000});
 			  return false;
 		   }
 		   $.ajax({
 			  url:baseUrl + "out/picking/list",
 		  	  data:{
 		  		 "WERKS":$('#werks').val(),
 		  		 "WH_NUMBER":$("#whNumber").val(),
 		  		 "REQUIREMENT_NO":requirementNo,
 		  		 "MANAGER":$("#manager").val(),
 		  		 "ORDER_NO":$("#orderNo").val(),
 		  		 "LGORT":$("#lgort").val(),
 		  		 "MATNR":$("#matnr").val(),
 		  		 "STATION":$("#station").val(),
 		  		 "REQ_ITEM_STATUS":$("#reqItemStatus").val()
 		  	  },
 		  	  success:function(resp){
 		  		mydata = resp.page.list;
 		  		$("#dataGrid").jqGrid('clearGridData');
 		  		$("#dataGrid").jqGrid('setGridParam',{ // 重新加载数据 
					 datatype:'local', data : resp.page.list, // newdata 是符合格式要求的需要重新加载的数据
				  }).trigger("reloadGrid");
 		  		
 		  	  }
 		   });
 		},
 		// 推荐
 		recommend:function(){
 			$(".btn").attr("disabled", true);
// 			$("#dataGrid").jqGrid('clearGridData');
 			var requirementNo=$("#requirementNo").val();
  		    if(requirementNo==''){
  			  layer.msg("请输入需求号！",{time:1000});
  			  $(".btn").attr("disabled", false);
  			  return false;
  		    }
  		    
  		    js.loading();
 			$.ajax({
 				url : baseUrl + "out/picking/recommend",
 				dataType : "json",
 				type : "post",
 				data : {
 					"WERKS":$('#werks').val(),
 	 		  		 "WH_NUMBER":$("#whNumber").val(),
 	 		  		 "REQUIREMENT_NO":$("#requirementNo").val(),
 	 		  		 "MANAGER":$("#whManager").val(),
 	 		  		 "ORDER_NO":$("#orderNo").val(),
 	 		  		 "LGORT":$("#lgort").val(),
 	 		  		 "MATNR":$("#matnr").val(),
 	 		  		 "STATION":$("#station").val(),
 	 		  		 "REQ_ITEM_STATUS":$("#reqItemStatus").val()
 				},
 				success : function(resp) {
 				   if(resp.code==0){
 					  if(resp.page.list.length === 0){
 						 $("#headDiv").css("display","none");
 						 layer.msg("未找到数据！",{time:2000});
 					  } else{
 						 $("#headDiv").css("display","block");
 						 var headInfo=resp.page.list[0];
 	 					 $("#type").text(headInfo.VALUE);
 	 					 $("#reqDate").text(headInfo.REQUIRED_DATE+" "+headInfo.REQUIRED_TIME);
 	 					 $("#creator").text(headInfo.CREATOR);
 	 					 $("#model").text(headInfo.REQUIRED_MODEL);
 					  }
 					  
 					  mydata = resp.page.list;
 	 		  		  $("#dataGrid").jqGrid('clearGridData');
 	 		  		  $("#dataGrid").jqGrid('setGridParam',{ // 重新加载数据 
 						 datatype:'local', data : resp.page.list, // newdata 是符合格式要求的需要重新加载的数据
 					  }).trigger("reloadGrid");
 	 		  		  js.closeLoading();
 				   } else{
						if(resp.msg != ""){
							js.showErrorMessage(resp.msg);
							$(".btn").attr("disabled", false);
						}
						js.closeLoading();
				   }
 				}
 			});

 		},
 		// 下架
 		confirm:function(){
 			$(".btn").attr("disabled", true);
 			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
 			
 		   var flag=true;
 		   var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
  		   if(ids.length==0){
 	 		      　layer.msg("请勾选行项目！",{time:1500});
 	 		   $(".btn").attr("disabled", false);
 	 		      　return;
  		   }
  		   var savaData=[];
  		   for(var i=0;i<ids.length;i++){
  			 var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);
  			 if (rowData.BATCH === '') {
  				flag = false;
  				js.showErrorMessage("批次不能为空");
  			 }
  			 if (rowData.BIN_CODE === '') {
  				flag = false;
  				js.showErrorMessage("储位不能为空");
  			 }
  			 if (rowData.RECOMMEND_QTY === '') {
  				flag = false;
  				js.showErrorMessage("推荐数量不能为空");
  			 }
  			if (rowData.RECOMMEND_QTY <= "0") {
  				flag = false;
  				js.showErrorMessage("推荐数量不能为0");
  			 }
  			 var aa = Number(rowData.RECOMMEND_QTY) +Number(rowData.QTY_XJ);
  			 if (aa > rowData.QTY && rowData.OVERSTEP_REQ_FLAG !=='X') {
  				flag = false;
  				js.showErrorMessage("推荐数量不能大于可下架数量");
  			 }
  			 savaData.push(rowData);
  		   }
  		   
  		   if(flag){
  			   js.loading();
  			   $.ajax({
  				    url : baseUrl + "out/picking/picking",
				    type:"post",
					dataType:"json",
					data : {
						"WERKS" : $('#werks').val(),
						"WH_NUMBER" : $('#whNumber').val(),
						"REQUIREMENT_NO" : $('#requirementNo').val(),
						"SAVE_DATA" : JSON.stringify(savaData),
					},
					success : function(resp) {
						if(resp.code==0){
							$("#dataGrid").jqGrid('clearGridData');
							layer.msg("下架成功！",{time:3000});
							js.closeLoading();
						}else{
							if(resp.msg != ""){
								js.showErrorMessage(resp.msg);
							} else {
								js.showErrorMessage("未找到合适的批次信息");
							}
							$(".btn").attr("disabled", false);
							js.closeLoading();
						}
					}
				});
  		   }
 		},
 		// 取消下架
 		cancel:function(){
 			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
 		   
 		   if(ids.length==0){
	 		      　layer.msg("请勾选行项目！",{time:1500});
	 		      　return;
 		   }else{
 			   for(var i=0;i<ids.length;i++){
 				  console.log("id",ids[i]+":"+ids.length);
 				  $("#dataGrid").jqGrid("delRowData", ids[i]);
 			   }  
 		   }
 		},
 		// 插入
 		insert:function(){
 			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
 			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
 			
  		    if(ids.length==0){
 	 		      　layer.msg("请勾选行项目！",{time:1500});
 	 		      　return;
  		    }
 			var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow");
 			
 			var grData = $("#dataGrid").jqGrid("getRowData",selectedId)//获取选中的行数据
 			if(selectedId=='' || selectedId==undefined){
 				layer.msg("请勾选行项目！",{time:1500});
 				return;
 			}
 			if(grData.TASK_NUM ==="") {
 				layer.msg("拆分的行不允许再拆分！",{time:3000});
 				return;
 			}
 			if(grData.RECOMMEND_QTY === '0' || grData.RECOMMEND_QTY <= "0") {
 				layer.msg("请输入推荐数量，以便拆分！",{time:3000});
 				return;
 			}
 			
 			var kcfqty = Number(grData.QUANTITY) - Number(grData.QTY_XJ);
 			if(grData.RECOMMEND_QTY >= kcfqty) {
 				layer.msg("推荐数量小于需求数量，才能拆分！",{time:3000});
 				return;
 			}
 			
 			var newquantity = Number(kcfqty) - Number(grData.RECOMMEND_QTY);
 			var qqty = Number(grData.QUANTITY) - Number(newquantity);
 			$("#dataGrid").jqGrid('setCell',selectedId ,"QUANTITY",qqty);
 			
 			if(grData.HX_FLAG ==="A") {
 				layer.msg("核销项目不允许手动添加拣配行！",{time:3000});
 				return;
 			}
// 			alert(JSON.stringify(grData));
 			var ids = jQuery("#dataGrid").jqGrid('getDataIDs');
// 			console.log("ids",ids);
 			var rowid = parseInt(getMaxId(ids)) + 1;
 	        var dataRow=[ { id: rowid, MO_NO: grData.MO_NO, SO_NO: grData.SO_NO, MATNR: grData.MATNR, MAKTX: grData.MAKTX, QUANTITY: newquantity, QTY: grData.QTY, 
 	        	UNIT: grData.UNIT, REQ_ITEM_STATUS: grData.REQ_ITEM_STATUS, MO_ITEM_NO: grData.MO_ITEM_NO, SO_ITEM_NO: grData.SO_ITEM_NO,REQUIREMENT_ITEM_NO: grData.REQUIREMENT_ITEM_NO, 
 	        	STATION: grData.STATION, BUSINESS_NAME: grData.BUSINESS_NAME, BUSINESS_TYPE: grData.BUSINESS_TYPE, STORAGE_AREA_CODE: grData.STORAGE_AREA_CODE, 
 	        	TASK_NUM_REF: grData.TASK_NUM, ID: grData.ID, RECOMMEND_QTY_H: newquantity,LGORT_OLD: grData.LGORT_OLD,BIN_CODE_OLD: grData.BIN_CODE_OLD,
 	        	BATCH_OLD: grData.BATCH_OLD,SOBKZ_OLD: grData.SOBKZ_OLD,LIFNR_OLD: grData.LIFNR_OLD,QUANTITY_REF: grData.QUANTITY,OVERSTEP_REQ_FLAG: grData.OVERSTEP_REQ_FLAG}];
 	        
 	       
 	       if (selectedId) {    
// 	    	  console.log("rowid",rowid);
 	           $("#dataGrid").dataGrid("addRowData", 11, dataRow, "after", selectedId);
 	       } else { 
 	           $("#dataGrid").jqGrid("addRowData", rowid, dataRow, "last");    
 	       }      
 	       var data = $("#dataGrid").jqGrid("getRowData"); 
 	       $("#dataGrid").find("input:checkbox").attr("enabled", "enabled");
// 	       console.log("data",data);
 		},
 		//显示列表
 		showTable:function(){
 			$("#dataGrid").dataGrid({
 		        datatype: 'local',
 		        data: mydata,
 		        cellEdit:true,
 				cellurl:'#',
 				cellsubmit:'clientArray',
 		        colNames: ['订单号','销售订单','料号', '物料描述', '需求数量', '下架数量', '单位', '批次', '储位', '推荐数量', '状态', '核销', '供应商','库位','库存类型',
 		        	'订单行','订单行','需求行', '工位','仓库任务','消息','','','','','','','','','','','','','','','','','','','',''],
 		        colModel: [
 		        	{ name: 'MO_NO', index: 'MO_NO', width: 100, sortable:false, align: 'center', hidden:true,
		            	 cellattr: function(rowId, tv, rawObject, cm, rdata) {
 		                    //合并单元格
 		                    return 'id=\'MO_NO' + rowId + "\'";
 		                }
                    },
                    { name: 'SO_NO', index: 'SO_NO', width: 100, sortable: false ,align: 'center',hidden:true,
                    	cellattr: function(rowId, tv, rawObject, cm, rdata) {
 		                    //合并单元格
 		                    return 'id=\'SO_NO' + rowId + "\'";
 		                }
                    },
 		            { name: 'MATNR', index: 'MATNR', width: 110, sortable:false, align: 'center',
 		            	 cellattr: function(rowId, tv, rawObject, cm, rdata) {
  		                    //合并单元格
  		                    return 'id=\'MATNR' + rowId + "\'";
  		                }
                    },
 		            { name: 'MAKTX', index: 'MAKTX', width: 260, sortable:false, align: 'center',
                    	 cellattr: function(rowId, tv, rawObject, cm, rdata) {
  		                    //合并单元格
  		                    return 'id=\'MAKTX' + rowId + "\'";
  		                }

 		            },
 		            { name: 'QUANTITY', index: 'QUANTITY', width: 90, sortable:false, align: 'center'},
 		            { name: 'QTY_XJ', index: 'QTY_XJ', width: 90, sortable:false, align: 'center'},
 		            { name: 'UNIT', index: 'UNIT', width: 55, sortable: false,align: 'center' },
 		            { name: 'BATCH', index: 'BATCH', width: 100, sortable: false,editable:true,align: 'center' },
 		            { name: 'BIN_CODE', index: 'BIN_CODE', width: 100, sortable: false,editable:true ,align: 'center'},
 		            { name: 'RECOMMEND_QTY', index: 'RECOMMEND_QTY', width: 70, sortable: false,editable:true, editrules:{number:true}, align: 'center' },
 		            { name: 'REQ_ITEM_STATUS', index: 'REQ_ITEM_STATUS', width: 70, sortable: false,align: 'center',
 		            	formatter:function(val, obj, row, act){
	 		   				if(val == "00"){
	 		   					return "已创建";
	 		   				} else if(val == "01"){
	 		   					return "备料中";
	 		   				} else if(val == "02"){
	 		   					return "部分下架";
	 		   				} else if(val == "03"){
	 		   					return "已下架";
	 		   				} else if(val == "04"){
	 		   					return "部分交接";
	 		   				} else if(val == "05"){
	 		   					return "已交接";
	 		   				} else if(val == "06"){
	 		   					return "关闭";
	 		   				} else if(val == "99"){
	 		   					return "已推荐";
	 		   				} else{
	 		   					return val;
	 		   				}
	 		   				
 		   				}
 		            },
 		            { name: 'HX_FLAG', index: 'HX_FLAG', width: 50, sortable: false ,align: 'center', hidden:true,
 		            	formatter:function(val, obj, row, act){
 		   				if(val == "1"){
 		   					return "A";
 		   				} else{
 		   					return "";
 		   				}} 
 		            },
 		            { name: 'LIFNR', index: 'LIFNR', width: 80, sortable: false,editable:true ,align: 'center'},
 		            { name: 'LGORT', index: 'LGORT', width: 60, sortable: false,editable:false,align: 'center' },
 		            { name: 'SOBKZ', index: 'SOBKZ', width: 65, sortable: false,editable:false,align: 'center' },
// 		            { name: 'STOCK_STATUS', index: 'STOCK_STATUS', width: 65, sortable: false,editable:true,align: 'center' },
 		            { name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 70, sortable: false, align: 'center', hidden:true},
 		            { name: 'SO_ITEM_NO', index: 'SO_ITEM_NO', width: 70, sortable: false ,align: 'center',hidden:true},
 		            { name: 'REQUIREMENT_ITEM_NO', index: 'REQUIREMENT_ITEM_NO', width: 60, sortable: false, align: 'center'},
 		            { name: 'STATION', index: 'STATION', width: 60, sortable: false ,align: 'center'},
 		            { name: 'TASK_NUM', index: 'TASK_NUM', width: 120, sortable: false ,align: 'center'},
 		            { name: 'MSG', index: 'MSG', width: 220, sortable: false ,align: 'center'},
 		            { name: 'OVERSTEP_REQ_FLAG', index: 'OVERSTEP_REQ_FLAG', hidden:true},
 		            { name: 'BUSINESS_NAME', index: 'BUSINESS_NAME',hidden:true},
 		            { name: 'BUSINESS_TYPE', index: 'BUSINESS_TYPE', hidden:true},
 		            { name: 'QTY', index: 'QTY', hidden:true},
 		            { name: 'CONFIRM_QUANTITY', index: 'CONFIRM_QUANTITY', hidden:true},
 		            { name: 'STOCK_ID', index: 'STOCK_ID',hidden:true},
 		            { name: 'STORAGE_AREA_CODE', index: 'STORAGE_AREA_CODE',hidden:true},
 		            { name: 'TASK_NUM_REF', index: 'TASK_NUM_REF',hidden:true},
 		            { name: 'QUANTITY_REF', index: 'QUANTITY_REF',hidden:true},
 		            { name: 'BATCH_OLD', index: 'BATCH_OLD', hidden:true},
 		            { name: 'BIN_CODE_OLD', index: 'BIN_CODE_OLD', hidden:true},
 		            { name: 'LIFNR_OLD', index: 'LIFNR_OLD', hidden:true},
 		            { name: 'LGORT_OLD', index: 'LGORT_OLD', hidden:true},
 		            { name: 'SOBKZ_OLD', index: 'SOBKZ_OLD', hidden:true},
 		            { name: 'RECOMMEND_QTY_H', index: 'RECOMMEND_QTY_H', hidden:true},
 		            { name: 'QTY_REAL', index: 'QTY_REAL',hidden:true},
 		            { name: 'REAL_QUANTITY', index: 'REAL_QUANTITY',hidden:true},
 		            { name: 'KEYPARTSFLAG', index: 'KEYPARTSFLAG',hidden:true},
 		            { name: 'VERIFY', index: 'VERIFY',hidden:true},
		            { name: 'ID', index: 'ID', hidden:true}
 		        ],
 		        showCheckbox: true,
 		        rownumbers: true,
 		        //sortname: 'id',
 		        viewrecords: false,
 		       // sortorder: 'desc',
 		        height: '100%',
 		        multiselect: true,
 			    autowidth:true,
 		        shrinkToFit:false,
 		        autoScroll: true, 
 		        gridComplete: function() {
 		            //②在gridComplete调用合并方法
 		           var gridName = "dataGrid";
 		           Merger(gridName, 'MATNR');
 		           Merger(gridName, 'MAKTX');
// 		           Merger(gridName, 'QTY','MATNR');
// 		           Merger(gridName, 'QTY_XJ','MATNR');
 		           var sourcedata =  $("#dataGrid").jqGrid('getRowData');
 		           
 		           if (!isEmpty(mydata[0]) && !isEmpty(mydata[0].MO_NO)) {
 		        	  $("#dataGrid").setGridParam().showCol("MO_NO").trigger("reloadGrid");
 		        	  $("#dataGrid").setGridParam().showCol("MO_ITEM_NO").trigger("reloadGrid");
// 		        	  Merger(gridName, 'MO_NO');
	               } else{
	            	  $("#dataGrid").setGridParam().hideCol("MO_NO").trigger("reloadGrid");
	 		          $("#dataGrid").setGridParam().hideCol("MO_ITEM_NO").trigger("reloadGrid");
	               }
 		           
 		           if (!isEmpty(mydata[0]) && !isEmpty(mydata[0].SO_NO)) {
 		        	  $("#dataGrid").setGridParam().showCol("SO_NO").trigger("reloadGrid");
 		        	  $("#dataGrid").setGridParam().showCol("SO_ITEM_NO").trigger("reloadGrid");
 		        	  Merger(gridName, 'SO_NO');
	               } else{
	            	  $("#dataGrid").setGridParam().hideCol("SO_NO").trigger("reloadGrid");
	 		          $("#dataGrid").setGridParam().hideCol("SO_ITEM_NO").trigger("reloadGrid");
	               }
 		          
 		           if (!isEmpty(mydata[0]) && !isEmpty(mydata[0].HX_FLAG)) {
		        	  $("#dataGrid").setGridParam().showCol("HX_FLAG").trigger("reloadGrid");
	               } else{
	            	  $("#dataGrid").setGridParam().hideCol("HX_FLAG").trigger("reloadGrid");
	               }
 		           
 		           var ids = $("#dataGrid").getDataIDs();
 		           for(var i=0;i<ids.length;i++){
 		               var rowData = $("#dataGrid").getRowData(ids[i]);
 		               if(rowData.KEYPARTSFLAG != null && rowData.KEYPARTSFLAG !='' && rowData.KEYPARTSFLAG != undefined){
		                	//如果是关键物料设置自定义的颜色
		                    $('#'+ids[i]).find("td").addClass("keyParts-select");
		               }
 		               
 		               if(!isEmpty(rowData.MSG)){
 		                   $('#'+ids[i]).find("td").addClass("SelectBG");
 		               }
 		              
 		               if (!isEmpty(rowData.MO_NO) ) {
 		            	   flag = true;
 		               } 
 		           }
 		            
 		           $(".jqgrow", $("#dataGrid")).each(function (index, row) {
 				        var $row = $(row);
// 				       console.log("STATUS",mydata);
 				       var ds = JSON.stringify(mydata[index]);
 				       if(!isEmpty(ds)) {
 				    	 if(mydata[index].REQ_ITEM_STATUS === "03" || mydata[index].REQ_ITEM_STATUS === "05" || mydata[index].REQ_ITEM_STATUS === "06"){
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'BATCH', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'RECOMMEND_QTY', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'BIN_CODE', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'LIFNR', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'LGORT', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'SOBKZ', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'STOCK_STATUS', '', 'not-editable-cell');
	 				    }
 				    	if(mydata[index].REQ_ITEM_STATUS === "02" || mydata[index].HX_FLAG === "1"){
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'BATCH', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'BIN_CODE', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'LIFNR', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'LGORT', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'SOBKZ', '', 'not-editable-cell');
	 	 			          $("#dataGrid").jqGrid('setCell', index+1, 'STOCK_STATUS', '', 'not-editable-cell');
	 				    }
 				      }
 					});
 		        },
 		       onSelectAll: function(aRowids,status) {
 			        if (status) {
 			           var rowIds = $("#dataGrid").jqGrid('getDataIDs');//获取jqgrid中所有数据行的id
 	 			       for(var k=0; k<rowIds.length; k++) {
 	 			         var curRowData = $("#dataGrid").jqGrid('getRowData', rowIds[k]);//获取指定id所在行的所有数据.
 	 			          if(curRowData.REQ_ITEM_STATUS === "已下架" || curRowData.REQ_ITEM_STATUS === "已交接" || curRowData.REQ_ITEM_STATUS === "关闭" || !isEmpty(curRowData.MSG)) {
 	 			                $("#dataGrid").jqGrid("setSelection", rowIds[k],false);
 	 			          }
 	 			           
 	 			       }
 			        }
 			    },
 		        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
 					lastrow=iRow;
 					lastcell=iCol;
 				},
 				onCellSelect : function(rowid,iCol,cellcontent,e){
 		   	 		var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
 			   	},
 			    onSelectRow:function(rowid,e){
 			    	var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
 			    	if(rec.REQ_ITEM_STATUS === "已下架" || rec.REQ_ITEM_STATUS === "已交接" || rec.REQ_ITEM_STATUS === "关闭" || !isEmpty(rec.MSG)) {
 	 	 			   $("#dataGrid").jqGrid("setSelection", rowid,false);
 	 	 			   layer.msg("该数据无法下架！",{time:1500});
 	 		   	 	}
 			    },
 		        beforeSelectRow:function(rowid,e){
 		        	var $myGrid = $(this),  
 					   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
 					   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
 					   if(cm[i].name == 'cb'){
 						   $('#dataGrid').jqGrid('setSelection',rowid);
 					   }
 					   return (cm[i].name == 'cb'); 
 		        },
 		        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
 		        	
 		        	var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
 		        	if(cellname == 'BATCH'){
 		        		$.ajax({
 		    				url : baseUrl + "out/picking/getStockInfo",
 		    				data : {
 		    					"WERKS" : $('#werks').val(),
 		    					"WH_NUMBER" : $('#whNumber').val(),
 		    					"MATNR" : rec.MATNR.replace(/^\s*|\s*$/g,""),
 		    					"BATCH" : value.replace(/^\s*|\s*$/g,""),
 		    				},
 		    				success : function(resp) {
 		    					if(resp.list.length==0){
 		    						js.showErrorMessage("未找到合适的批次信息");
 		    						$("#dataGrid").jqGrid('setCell', rowid, cellname, null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'BIN_CODE', null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LGORT', null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LIFNR', null,'editable-cell');
 		    					}else{
 		    						var data=resp.list[0];
// 		    						console.log("data",data);
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'BIN_CODE', data.BIN_CODE,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LGORT', data.LGORT,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', data.SOBKZ,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LIFNR', data.LIFNR,'editable-cell');
 		    						if(!isEmpty(rec.TASK_NUM)) {
 		    							$("#dataGrid").jqGrid('setCell', rowid, 'RECOMMEND_QTY', rec.RECOMMEND_QTY_H,'not-editable-cell');
 		    						} else {
 		    							$("#dataGrid").jqGrid('setCell', rowid, 'RECOMMEND_QTY', rec.RECOMMEND_QTY_H,'editable-cell');
 		    						}
 		    						
 		    					}
 		    				}
 		    			});
 		        	}
 		        	
 		        	if(cellname == 'BIN_CODE'){
 		        		$.ajax({
 		    				url : baseUrl + "out/picking/getStockInfo",
 		    				data : {
 		    					"WERKS" : $('#werks').val(),
 		    					"WH_NUMBER" : $('#whNumber').val(),
 		    					"MATNR" : rec.MATNR,
 		    					"BATCH" : rec.BATCH.replace(/^\s*|\s*$/g,""),
 		    					"BIN_CODE" : value.replace(/^\s*|\s*$/g,""),
 		    				},
 		    				success : function(resp) {
 		    					if(resp.list.length==0){
 		    						js.showErrorMessage("该仓位未找到库存");
 		    						$("#dataGrid").jqGrid('setCell', rowid, cellname, null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LGORT', null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', null,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LIFNR', null,'editable-cell');
 		    					}else{
 		    						var data=resp.list[0];
// 		    						console.log("data",data);
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LGORT', data.LGORT,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', data.SOBKZ,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'LIFNR', data.LIFNR,'editable-cell');
 		    						$("#dataGrid").jqGrid('setCell', rowid, 'RECOMMEND_QTY', rec.RECOMMEND_QTY_H,'not-editable-cell');
 		    					}
 		    				}
 		    			});
 		        	}
 		        	
 		        	if(cellname == 'RECOMMEND_QTY'){
	        			var reqQty=rec.QUANTITY;
	        			var xjQty=rec.QTY_XJ!='' ? rec.QTY_XJ : 0;
//	        			console.log(value+"-"+(reqQty-xjQty));
	        			if(rec.OVERSTEP_REQ_FLAG !=='X' && parseFloat(value)>(parseFloat(reqQty)-parseFloat(xjQty))){
 		        			js.showErrorMessage('不能进行超需求下架:'+(parseFloat(reqQty)-parseFloat(xjQty)));
 		        			$("#dataGrid").jqGrid('setCell', rowid, cellname, null,'editable-cell');
 		        			return ;
 		        		}
	        		}
 		        }

 		    });
 		},
 	    // 导出
 		exp:function(){
 			var requirementNo=$("#requirementNo").val();
  		    if(requirementNo==''){
  			  layer.msg("请输入需求号！",{time:1000});
  			  return false;
  		    }
 			export2Excel();
 		},
 		openLgortSelectWindow : function(call_back) {
			// 库位选择框
			js.layer.open({
				type : 2,
				title : "库位",
				area : [ "40%", "60%" ],
				content : baseUrl + "wms/out/stock_mutiselect.html",
				btn : [ "确定", "取消" ],
				success : function(layero, index_) {
					// 加载完成后，初始化
					var werks = $("#werks").val();
					var whNumber = $("#whNumber").val();
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.init(werks, whNumber);
				},
				yes : function(index_, layero) {
					// 提交，调用生产订单页面的提交方法。
					var win = layero.find('iframe')[0].contentWindow;
					var lgortList = win.vm.getSelectedItems();
					var lgortItems = lgortList
							.map(function(val) {
								return val.LGORT;
							});
					var lgortList = lgortItems.join(",");
					if(lgortList.length > 0 && lgortList.charAt(lgortList.length-1) === ','){
						lgortList = lgortList.substring(0,lgortList.length-1);
					}
					if(call_back != null){
						call_back(lgortList);
					}
					else{
						//没有回调
						vm.lgortList = lgortList;
					}
					win.vm.close();
				},
				no : function(index, layero) {
				}
			});
		},
		onPlantChange:function(event){
 			//工厂变化的时候，更新仓库号下拉框
	        var plantCode = event.target.value;
	        if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	return;
	        }
	        //查询工厂仓库
	        $.ajax({
	        	url:baseUrl + "common/getWhDataByWerks",
	        	data:{"WERKS":plantCode,
	        		"MENU_KEY":"A70"
	        	},
	        	success:function(resp){
	        		vm.warehourse = resp.data;
	        		if(resp.data.length>0){
	        			vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
	         		}
	        	}
	          })
 		},
 	    // 打印配送标签
 		printlabel:function(){
 		  var requirementNo=$("#requirementNo").val();
  		  if(requirementNo==''){
  			layer.msg("请输入需求号！",{time:1000});
  			return false;
  		  }
  		  
  		  var labelurl;
  		  $.ajax({
			  url:baseUrl + "out/picking/getReq",
		  	  data:{
		  		 "WERKS":$('#werks').val(),
		  		 "WH_NUMBER":$("#whNumber").val(),
		  		 "REQUIREMENT_NO":requirementNo
		  	  },
		  	  sync:false,
		  	  success:function(resp){
		  		if(resp.code==0){
		  			var bn = resp.business_name;
		  			if (bn == '77') {
		  	  			labelurl = 'wms/out/shippingLabel.html';
		  			} else {
		  				labelurl = 'wms/out/shippingLabel_cs.html';
		  			}
		  			
		  	  		var options = {
							type: 2,
							maxmin: true,
							shadeClose: true,
							title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>配送标签打印',
							area: ["1000px", "500px"],
							content: labelurl,
							btn: ['条码打印','A4纸打印'],
							success:function(layero, index){
								var win = layero.find('iframe')[0].contentWindow;
								win.vm.saveFlag = false;
								win.getInfo(requirementNo,$("#whNumber").val(),$("#matnr").val());
								win.init();
							},
							btn1:function(index,layero){
								var win = layero.find('iframe')[0].contentWindow;
								win.printpage();
								vm.reload();
								//parent.layer.close(index);
							},
							btn2:function(index,layero){
								var win = layero.find('iframe')[0].contentWindow;
								win.printpage("A4");
								vm.reload();
							}
						};
						options.btn.push('<i class="fa fa-close"></i> 关闭');
						options['btn'+options.btn.length] = function(index, layero){
							if(typeof listselectCallback == 'function'){
							}
						};
						js.layer.open(options);
					
		  		} else {
		  			layer.msg(resp.msg,{time:2000});
		  			return false;
		  		}
		  	  }
		  });
 		},
 		//打印关键零部件标签
 		printkeyparts:function(){
 			var requirementNo=$("#requirementNo").val();
 	  		if(requirementNo==''){
 	  			layer.msg("请输入需求号！",{time:1000});
 	  			return false;
 	  		}
 	  		
 	  		$.ajax({
 				url:baseUrl + "out/picking/getReq",
 			  	data:{
 			  		"WERKS":$('#werks').val(),
 			  		"WH_NUMBER":$("#whNumber").val(),
 			  		"REQUIREMENT_NO":requirementNo,
 			  		"LABELTYPE":"parts"
 			  	},
 			  	sync:false,
 			  	success:function(resp){
 			  		if(resp.code==0){
 			  		var bn = resp.business_name;
 			  	  	var options = {
 							type: 2,
 							maxmin: true,
 							shadeClose: true,
 							title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>关键零部件标签打印',
 							area: ["1000px", "500px"],
 							content: 'wms/out/keyPartsLabel_print.html',
 							btn: ['条码打印','A4纸打印'],
 							success:function(layero, index){
 								var win = layero.find('iframe')[0].contentWindow;
 								win.vm.saveFlag = false;
 								win.getInfo(requirementNo,$("#whNumber").val());
 								win.init();
 							},
 							btn1:function(index,layero){
 								var win = layero.find('iframe')[0].contentWindow;
 								win.printpage();
 								vm.reload();
 								//parent.layer.close(index);
 							},
 							btn2:function(index,layero){
 								var win = layero.find('iframe')[0].contentWindow;
 								win.printpage("A4");
 								vm.reload();
 							}
 						};
 						options.btn.push('<i class="fa fa-close"></i> 关闭');
 						options['btn'+options.btn.length] = function(index, layero){
 							if(typeof listselectCallback == 'function'){
 							}
 						};
 						js.layer.open(options);
 						
 			  	} else {
 			  		layer.msg(resp.msg,{time:2000});
 			  		return false;
 			  	}
 			  }
 	  		});
 		}
 	}
 });
$(document).ready(function() {
    vm.showTable();
//    var data = $("#dataGrid").jqGrid("getRowData"); 
//    console.log("data",data);
    /** 设置增行按钮的click事件处理 */
    $("#addButton").bind("click", function() {
        var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow");
        var dataRow = { 
            age: "",
            sex: ""
        };   
        var ids = jQuery("#dataGrid").jqGrid('getDataIDs');
        var rowid = getMaxId(ids) + 1;
        if (selectedId) {        
            $("#dataGrid").jqGrid("addRowData", rowid, dataRow, "after", selectedId);
        } else { 
            $("#dataGrid").jqGrid("addRowData", rowid, dataRow, "last");    
        }      
    });
  

    /** 设置删行按钮的click事件处理 */
    $("#removeButton").bind("click", function() {
     var selectedId = $("#dataGrid").jqGrid("getGridParam","selrow");
     if(!selectedId){
      　　alert("请选择要删除的行");
      　　return;
     }else{          
         $("#gridTable").jqGrid("delRowData", selectedId);     
     }
    });
});
// 校验是否可超需求下架
function checkOverstepReq(rowData){
	var isOverstepReq=false;
	$.ajax({
		url : baseUrl + "out/materialPicking/checkOverstepReq",
		data : {
			"WERKS" : $("#werks").val(),
			"BUSINESS_NAME" : rowData.BUSINESS_NAME,
			"BUSINESS_TYPE" : rowData.BUSINESS_TYPE,
			"SOBKZ" : rowData.SOBKZ,
		},
		sync:false,
		success : function(resp) {
			isOverstepReq=resp.isOverstepReq;
//			console.log("isOverstepReq",isOverstepReq);
		}
	});
	return isOverstepReq;
}
//公共调用方法
function Merger(gridName, CellName,referCellName) {
    //得到显示到界面的id集合
    var mya = $("#" + gridName + "").getDataIDs();
    //当前显示多少条
    var length = mya.length;
    for (var i = 0; i < length; i++) {
        //从上到下获取一条信息
        var before = $("#" + gridName + "").jqGrid('getRowData', mya[i]);
        //定义合并行数
        var rowSpanTaxCount = 1;
        for (j = i + 1; j <= length; j++) {
            //和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
            var end = $("#" + gridName + "").jqGrid('getRowData', mya[j]);
            if(referCellName=='' || referCellName==undefined){
	            if (before[CellName] == end[CellName]) {
	                rowSpanTaxCount++;
	                $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
	            } else {
	                rowSpanTaxCount = 1;
	                break;
	            }
            }else{
            	if (before[referCellName] == end[referCellName]) {
	                rowSpanTaxCount++;
	                $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
	            } else {
	                rowSpanTaxCount = 1;
	                break;
	            }
            }
            $("#" + CellName + "" + mya[i] + "").attr("rowspan", rowSpanTaxCount);
        }
    }
}
function getMaxId(ids){
	var maxId=0;
	for(var i=0;i<ids.length;i++){
		if(maxId<parseInt(ids[i])){
			maxId=ids[i];
		}
	}
//	console.log("maxId",maxId);
	return maxId;
}

function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}

//用于点击其他地方保存正在编辑状态下的行
$('html').bind('click', function(e) { 
    if (!isEmpty(lastcell)) {
        if($(e.target).closest('#dataGrid').length == 0) {  
        	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
        	lastrow="";
        	lastcell="";
        }
    }
});

function export2Excel(){
	var column= [
		{title:'序号', data:'ID' },
		{title:'料号', data:'MATNR'},
		{title:'物料描述', data:'MAKTX'},	
		{title:'需求数', data:'QUANTITY'},	
		{title:'下架数', data:'QTY_XJ'},	
		{title:'订单号', data:'MO_NO' },
		{title:'批次', data:'BATCH'},
		{title:'推荐数量', data:'RECOMMEND_QTY'},
		{title:'储位', data:'BIN_CODE'},
		{title:'储位名称', data:'BIN_NAME'},
		{title:'供应商', data:'LIFNR'},
		{title:'库位', data:'LGORT'},
		{title:'库存类型', data:'SOBKZ'},
		{title:'单位', data:'MEINS'},
		{title:'核销', data:'HX_FLAG'},
		{title:'工位', data:'STATION'},
	] 
	var results=[];
	var params=$("#searchForm").serialize();
	var requirementNo=$("#requirementNo").val();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseUrl + "out/picking/pickList",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
//		    console.log("results",results);
		}
	});
	var rowGroups=[];
	if(!isEmpty(results)){
		getMergeTable1(results,column,rowGroups,"拣配清单",requirementNo);	
	} else {
		layer.msg("未找到拣配数据！",{time:1000});
		return false;
	}
	
}

function getMergeTable1(result,columns,rowsGroup,filename,tablename){
	/**
	 * 创建table
	 */
	var table=document.getElementById("tb_excel");
	
	/**
	 * 创建table head
	 */
	var table_head=$("<tr />");
	$.each(columns,function(i,column){
		var th=$("<th style='border:thin solid #000000;' bgcolor='#00FF00'/>");
		th.attr("class",column.class);
		th.attr("width",column.width);
		th.html(column.title);
		$(table_head).append(th);
	})
	
	if (!isEmpty(tablename)) {
		var table_name=$("<tr />");
		var th=$("<th style='border:thin solid #000000;'/>");
		th.attr("colspan",columns.length);
		th.html("需求捡配单"+tablename);
		$(table_name).append(th);
		$(table).append($(table_name));
	}
	
	$(table).append($(table_head));
	
	var warp = document.createDocumentFragment();// 创建文档碎片节点,最后渲染该碎片节点，减少浏览器渲染消耗的资源
	
	var data_process={};
	data_process.result=result;
	data_process.columns=columns;
	data_process.rowsGroup=rowsGroup;
	

    var curWwwPath=window.document.location.href;  
    // 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName=window.document.location.pathname;  
    var pos=curWwwPath.indexOf(pathName);  
    // 获取主机地址，如： http://localhost:8083
    var localhostPath=curWwwPath.substring(0,pos);  
    // 获取带"/"的项目名，如：/uimcardprj
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);  
    var baseRoot = localhostPath+projectName;  
	//alert(localhostPath);
	
	var worker=new Worker(baseRoot+"/statics/js/mergeTableCell.js")
	worker.postMessage(data_process);
	
	worker.onmessage=function(event){
		
	var trs_data=event.data;
	//alert(JSON.stringify(trs_data))
	$.each(trs_data,function(i,tr_obj){
		var tr=$("<tr />");
		$.each(tr_obj,function(j,td_obj){
			var td=$("<td style='border:thin solid #000000;'/>");
			td.attr("class","center");
			td.attr("id",td_obj.id);
			td.attr("rowspan",td_obj.rowspan);
			td.attr("width",td_obj.width);
			if (j ===11) {
				td.html("&nbsp;"+td_obj.html);
			}else{
				td.html(td_obj.html);
			}

			if(!td_obj.hidden){
				$(td).appendTo(tr)
			}
			
		});
		$(warp).append(tr);
	})
	
	$(table).append($(warp));
	//alert($(table).html())
	
	/**
	 * 导出excel
	 */
	htmlToExcel("tb_excel", "", "",filename,"");
	
	
	//导出后清除表格
	$(table).empty();
	//document.body.removeChild(table);
	worker.terminate();
	}
}
