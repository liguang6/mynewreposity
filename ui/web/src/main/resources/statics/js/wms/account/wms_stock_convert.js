var lastrow,lastcell,lastcellname;
var lgortList=[];//接收库位列表
var f_lgortList=[];//源库位列表
var mat_stock_list=[];
var vm = new Vue({
		el:'#page_div',
		data:{
			WMS_MOVE_TYPE:"",
			//F_WERKS:"",
			WERKS:"",
			//F_WH_NUMBER:"",
			WH_NUMBER:"",
			whList:[],
			SOBKZ:"K",
			//F_LGORT:"",
			LIFNR:"",
			LGORT:"",
			HEADER_TXT:"",
			PZ_DATE:getCurDate(),
			JZ_DATE:getCurDate(),
			lgortList:[],
			RECEIVER:"",
			MATNR_T:"",//转换物料（收货/发货）
			BIN_CODE:"",//储位
			BATCH:"",
			SO_NO:"",
			SO_ITEM_NO:"",
			STOCK_QTY:"",
			showStock:false,
		},
		watch:{
			WMS_MOVE_TYPE:{
				handler:function(newVal,oldVal){
					if(newVal=='411_K'){
						vm.SOBKZ='K';
					}
					if(newVal=='412_K'){
						vm.SOBKZ='Z';
					}
					if(newVal=='309'){
						vm.SOBKZ='Z';
					}
					if(newVal=='310'){
						vm.SOBKZ='Z';
					}
					if(newVal=='413'){
						vm.SOBKZ='Z';
					}
					if(newVal=='413_E'){
						vm.SOBKZ='E';
					}
					if(newVal=='411_E'){
						vm.SOBKZ='E';
					}
					this.$nextTick(function(){
						 if(vm.WMS_MOVE_TYPE=='413'){
							 vm.lgortList=getLgortList(vm.WERKS,null,null,'Z,E');
						 }else{
							 vm.lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);
						 }
						 if(vm.lgortList.length>0){
							 vm.LGORT = vm.lgortList[0].LGORT;
						 }
						
						showGrid();	
					})
			
				}
			},
			WERKS:{
				handler:function(newVal,oldVal){
					 $.ajax({
			        	  url:baseUrl + "common/getWhDataByWerks",
			        	  data:{"WERKS":newVal/*,"MENU_KEY":"IN_RECEIPT"*/},
			        	  success:function(resp){
			        		 vm.whList = resp.data;
			        		 if(resp.data.length>0){
			        			 vm.WH_NUMBER=resp.data[0].WH_NUMBER
			        		 }		        		 
			        	  }
			          })
			         
					 if(vm.WMS_MOVE_TYPE=='413'){
						 vm.lgortList=getLgortList(newVal,null,null,'Z,E');
					 }else{
						 vm.lgortList=getLgortList(newVal,null,null,vm.SOBKZ);
					 }
					 this.$nextTick(function(){
						 vm.LGORT = $("#LGORT").find("option").first().val();
						 setWerks();
					 })
					 
				}  
			},
			WH_NUMBER:{
				handler:function(newVal,oldVal){
					setWHNumber();
				}
			},
			MATNR_T: {
				handler:function(newVal,oldVal){
					if(vm.WMS_MOVE_TYPE=='310'&&newVal.length>0){
						var list= getMatStockInfo(newVal,vm.WERKS,vm.WH_NUMBER,vm.LGORT,null,null,null);
						setOption(list,"LIFNR");
					}
				}
			},
			LIFNR:{
				handler:function(newVal,oldVal){
					if(vm.WMS_MOVE_TYPE=='310'&&newVal.length>0){
						var list= getMatStockInfo(vm.MATNR_T,vm.WERKS,vm.WH_NUMBER,vm.LGORT,newVal,null,null);
						setOption(list,"BATCH");
					}
				}
			},
			BATCH:{
				handler:function(newVal,oldVal){
					if(vm.WMS_MOVE_TYPE=='310'&&newVal.length>0){
						var list= getMatStockInfo(vm.MATNR_T,vm.WERKS,vm.WH_NUMBER,vm.LGORT,vm.LIFNR,null,newVal);
						setOption(list,"BIN_CODE");
					}
				}
			},
			BIN_CODE:{
				handler:function(newVal,oldVal){
					if(vm.WMS_MOVE_TYPE=='310'&&newVal.length>0){
						var list= getMatStockInfo(vm.MATNR_T,vm.WERKS,vm.WH_NUMBER,vm.LGORT,vm.LIFNR,newVal,vm.BATCH);						
						this.$nextTick(function(){
							vm.showStock=true;
							vm.STOCK_QTY=list[0].STOCK_QTY
							clearQty();
						})
						
					}
				}
			},
			SO_NO:{
				handler:function(newVal,oldVal){
					$("#dataGrid").jqGrid('clearGridData');
				}
			},
			SO_ITEM_NO:{
				handler:function(newVal,oldVal){
					$("#dataGrid").jqGrid('clearGridData');
				}
			}
			
		},
		methods:{
			getStockInfo:function(){
				if(vm.WMS_MOVE_TYPE=='310'){
					var list=getMatStockInfo(vm.MATNR_T,vm.WERKS,vm.WH_NUMBER,vm.LGORT,null,null,null)
					if(list.length==0){
						js.showErrorMessage(vm.WERKS+" "+vm.MATNR_T+" 未找到库存信息！");	
						vm.MATNR_T="";
						return false;
					}
					setOption(list,"LIFNR");
				}
			},
			confirm:function(){
				$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
				var rows = getSelectedRows();
				js.loading("处理中...");
				$("#btnConfirm").html("处理中...");
				$("#btnConfirm").attr("disabled","disabled");
				if(rows==undefined||rows.length==0){
					js.alert("请选择至少一条记录");
					$("#btnConfirm").html("确认");	
					$("#btnConfirm").removeAttr("disabled");
					js.closeLoading();
					return false;
				}
				var selectedRows=[];
				$.each(rows,function(i,rowid){
					var row=$("#dataGrid").jqGrid('getRowData',rowid);
					selectedRows.push(row)
				})
				
				var confirm_flag=true;
				var MATNR_L= [];
				$.each(selectedRows,function(i,row){
					var MATNR = ""
					if(vm.WMS_MOVE_TYPE =='411_K' ){
						MATNR=row.F_LGORT+","+row.MATNR+","+row.LIFNR+","+row.BATCH+","+row.F_BIN_CODE
					}
					if(vm.WMS_MOVE_TYPE =='412_K' ){
						MATNR=row.LGORT+","+row.MATNR+","+row.LIFNR+","+row.BATCH+","+row.BIN_CODE
					}
					if(vm.WMS_MOVE_TYPE =='309' ){
						MATNR=row.F_LGORT+","+row.MATNR+","+row.LIFNR+","+row.BATCH+","+row.F_BIN_CODE
					}
					if(vm.WMS_MOVE_TYPE =='310' ){
						MATNR=row.LGORT+","+row.MATNR+","+row.LIFNR+","+row.BATCH+","+row.BIN_CODE
					}
					if(vm.WMS_MOVE_TYPE =='413' ){
						MATNR=row.LGORT+","+row.MATNR+","+row.LIFNR+","+row.BATCH+","+row.BIN_CODE
					}
					if(vm.WMS_MOVE_TYPE =='413_E' ){
						MATNR=row.F_LGORT+","+row.F_MATNR+","+row.LIFNR+","+row.BATCH+","+row.F_BIN_CODE
					}
					if(vm.WMS_MOVE_TYPE =='411_E' ){
						MATNR=row.F_LGORT+","+row.F_MATNR+","+row.LIFNR+","+row.BATCH+","+row.F_BIN_CODE
					}
					
					if(MATNR_L.indexOf(MATNR)>=0&&MATNR!=""){
						confirm_flag=false;
						js.showErrorMessage(MATNR+" 数据重复！");
						$("#btnConfirm").html("确认");	
						$("#btnConfirm").removeAttr("disabled");
						js.closeLoading();
						return false;
					}
					MATNR_L.push(MATNR);
				})
				
				if(confirm_flag){
					/**
					 * 确认后台逻辑
					 */
					$.ajax({
						url:baseUrl+"account/stockConvert/save",
						type:"post",
						async:true,
						dataType:"json",
						data:{
							matList:JSON.stringify(selectedRows),
							WERKS:vm.WERKS,
							WH_NUMBER:vm.WH_NUMBER,
							WMS_MOVE_TYPE:vm.WMS_MOVE_TYPE,
							HEADER_TXT:vm.HEADER_TXT,
							PZ_DATE:vm.PZ_DATE,
							JZ_DATE:vm.JZ_DATE,
							RECEIVER:vm.RECEIVER,
							LGORT:vm.LGORT,
							BIN_CODE:vm.BIN_CODE,
							MATNR_T:vm.MATNR_T,
							BATCH:vm.BATCH,
							LIFNR:vm.LIFNR,
							SO_NO:vm.SO_NO,
							SO_ITEM_NO:vm.SO_ITEM_NO,
						},
						success:function(response){
							$("#btnConfirm").html("确认");	
							$("#btnConfirm").removeAttr("disabled");
							js.closeLoading();
							if(response.code==500){
								js.showErrorMessage(response.msg);
								return false;
							}
							$('#dataGrid').jqGrid("clearGridData");
							
							$("#resultMsg").html(response.msg);
				        	layer.open({
				        		type : 1,
				        		offset : '50px',
				        		skin : 'layui-layer-molv',
				        		title : "操作成功",
				        		area : [ '500px', '250px' ],
				        		shade : 0,
				        		shadeClose : false,
				        		content : jQuery("#resultLayer"),
				        		btn : [ '确定'],
				        		btn1 : function(index) {
				        			layer.close(index);
				        		}
				        	});
							//js.showMessage(response.msg+"&nbsp;&nbsp;<a href='#' onclick='showLablePrint("+response.lableList+")'>来料标签打印"+"</a>",null,null,1000*20);
						}
					})
				}
			},
			reset:function(){
				$("#WERKS").attr("disabled",false)
				$("#WH_NUMBER").attr("disabled",false)
				vm.showStock=false;
				vm.LIFNR="";
				vm.MATNR_T="";
				vm.BIN_CODE="";
				vm.BATCH="";
				vm.HEADER_TXT="";
				vm.STOCK_QTY="";
				vm.RECEIVER="";
				vm.SO_NO="";
				vm.SO_ITEM_NO=""
				$("#dataGrid").jqGrid('clearGridData');
			}
		},
		created:function(){
			this.WH_NUMBER=$("#WH_NUMBER").find("option").first().val();
			this.WERKS=$("#WERKS").find("option").first().val();
			this.WMS_MOVE_TYPE="411_K"
			this.showStock=false;
		}
		
		
})	

function getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			PO_NO:PO_NO,
			PO_ITEM_NO:PO_ITEM_NO,
			SOBKZ:SOBKZ
		},
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
}
//表格渲染
function showGrid(){
	var multiselect=true;
	var columns=[];
	var afterSaveCell=null;
	var formatCell=null;
	var data = $('#dataGrid').data("dataGrid");

	if(data){
		$("#dataGrid").jqGrid('GridUnload');
	}
	
	if(vm.WMS_MOVE_TYPE =='411_K' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>',
					name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 220,sortable:false, }, 
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商代码</span>', name: 'LIFNR', align:"center",index: 'LIFNR', width: 85,
   				sortable:false,editable:true,edittype:'select',editrules:{required:true}},
   			{display:'批次', label: '<span style="color:red">*</span><span style="color:blue">批次</span>&nbsp;&nbsp;',
				name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},}, 	
			{display:'储位', label: '<span style="color:red">*</span><span style="color:blue">储位</span>&nbsp;&nbsp;',
					name: 'F_BIN_CODE',align:"center", index: 'F_BIN_CODE', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{ label: '非限制库存', name: 'STOCK_QTY', align:"center",index: 'STOCK_QTY', width: 80,sortable:false,}	,								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>', name: 'LGORT', align:"center",index: 'LGORT', sortable:false,width: 80,editable:true,edittype:'select',editrules:{required:true}}	, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">接收储位</span>', name: 'BIN_CODE', align:"center",index: 'BIN_CODE', sortable:false,width: 80,editable:true,editrules:{required:true}}, 	
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];
		
		formatCell=function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			console.info($("#dataGrid").jqGrid('getColProp',cellname))
			if(cellname=='F_LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'F_LGORT', { editoptions: {value:getCellOption(f_lgortList,'LGORT')} });
			}
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}		
			if(cellname=='BATCH'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,row.BIN_CODE,null);
				var options=getCellOption(list,'BATCH')
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:options} });			
			}
			if(cellname=='F_BIN_CODE'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				if(row.BATCH==undefined||row.BATCH=="&nbsp;"||row.BATCH==''){
					js.showErrorMessage("请输入批次!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,null,row.BATCH);
				var options=getCellOption(list,'BIN_CODE')
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'F_BIN_CODE', { editoptions: {value:options} });			
			}

		};
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='F_LGORT'){
				$("#dataGrid").jqGrid('setCell',rowid, "MATNR",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			if(cellname=='MATNR'){ //根据物料号列表查询物料信息	
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,null,null,null);
				if(list.length==0){
					js.showErrorMessage("未找到库存信息！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", list[0]['MAKTX'],'not-editable-cell');
				var options=getCellOption(list,'LIFNR')
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", list[0].LIFNR,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:options} });
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			
			if(cellname=='F_BIN_CODE'){
				console.info("BATCH:"+row.BATCH)
				console.info("LIFNR:"+row.LIFNR)
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",list[0].STOCK_QTY,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",list[0].STOCK_QTY,'editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",list[0].UNIT,'not-editable-cell');
			}
			
			if(cellname=='LIFNR'){
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH",'&nbsp;','editable-cell');
			}
			
			if(cellname=='QTY'){
				if(Number(row.QTY)>Number(row.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",row.STOCK_QTY,'editable-cell');
					return false;
				}
			}
			
		};
	}
	//412K(自有转寄售)
	if(vm.WMS_MOVE_TYPE =='412_K' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>',
					name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 220,sortable:false, }, 
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商代码</span>', name: 'LIFNR', align:"center",index: 'LIFNR', width: 85,
   				sortable:false,editable:true,edittype:'select',editrules:{required:true}},
   			{display:'批次', label: '<span style="color:red">*</span><span style="color:blue">批次</span>&nbsp;&nbsp;',
				name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},}, 	
			{display:'储位', label: '<span style="color:red">*</span><span style="color:blue">储位</span>&nbsp;&nbsp;',
					name: 'F_BIN_CODE',align:"center", index: 'F_BIN_CODE', width: 100 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{ label: '非限制库存', name: 'STOCK_QTY', align:"center",index: 'STOCK_QTY', width: 80,sortable:false,}	,								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>', name: 'LGORT', align:"center",index: 'LGORT', sortable:false,width: 80,editable:true,edittype:'select',editrules:{required:true}}	, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">接收储位</span>', name: 'BIN_CODE', align:"center",index: 'BIN_CODE', sortable:false,width: 80,editable:true,editrules:{required:true}}, 	
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];
		
		formatCell=function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			console.info($("#dataGrid").jqGrid('getColProp',cellname))
			if(cellname=='F_LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'F_LGORT', { editoptions: {value:getCellOption(f_lgortList,'LGORT')} });
			}
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}		
			if(cellname=='BATCH'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,row.BIN_CODE,null);
				var options=getCellOption(list,'BATCH')
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:options} });			
			}
			if(cellname=='F_BIN_CODE'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				if(row.BATCH==undefined||row.BATCH=="&nbsp;"||row.BATCH==''){
					js.showErrorMessage("请输入批次!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,null,row.BATCH);
				var options=getCellOption(list,'BIN_CODE')
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'F_BIN_CODE', { editoptions: {value:options} });			
			}

		};
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);

			if(cellname=='MATNR'){//根据物料号列表查询物料信息	
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,null,null,null);
				if(list.length==0){
					js.showErrorMessage("未找到库存信息！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", list[0]['MAKTX'],'not-editable-cell');
				var options=getCellOption(list,'LIFNR')
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", list[0].LIFNR,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:options} });
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			
			if(cellname=='F_BIN_CODE'){
				console.info("BATCH:"+row.BATCH)
				console.info("LIFNR:"+row.LIFNR)
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",list[0].STOCK_QTY,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",list[0].STOCK_QTY,'editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",list[0].UNIT,'not-editable-cell');
			}
			
			if(cellname=='LIFNR'){
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH",'&nbsp;','editable-cell');
			}
			if(cellname=='QTY'){
				if(Number(row.QTY)>Number(row.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",row.STOCK_QTY,'editable-cell');
					return false;
				}
			}
		};
	}
	
	//309(物料转移)
	if(vm.WMS_MOVE_TYPE =='309' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>',
					name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 220,sortable:false, }, 
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商代码</span>', name: 'LIFNR', align:"center",index: 'LIFNR', width: 85,
   				sortable:false,editable:true,edittype:'select',editrules:{required:true}},
   			{display:'批次', label: '<span style="color:red">*</span><span style="color:blue">批次</span>&nbsp;&nbsp;',
				name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},}, 	
			{display:'储位', label: '<span style="color:red">*</span><span style="color:blue">储位</span>&nbsp;&nbsp;',
					name: 'BIN_CODE',align:"center", index: 'BIN_CODE', width: 100 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{ label: '非限制库存', name: 'STOCK_QTY', align:"center",index: 'STOCK_QTY', width: 80,sortable:false,}	,								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];
		
		formatCell=function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			console.info($("#dataGrid").jqGrid('getColProp',cellname))
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}		
			if(cellname=='BATCH'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,row.BIN_CODE,null);
				var options=getCellOption(list,'BATCH')
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:options} });			
			}
			if(cellname=='BIN_CODE'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				if(row.BATCH==undefined||row.BATCH=="&nbsp;"||row.BATCH==''){
					js.showErrorMessage("请输入批次!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,null,row.BATCH);
				var options=getCellOption(list,'BIN_CODE')
				$("#dataGrid").jqGrid('setCell',rowid, "BIN_CODE", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:options} });			
			}

		};
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);

			if(cellname=='MATNR'){//根据物料号列表查询物料信息	
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,null,null,null);
				if(list.length==0){
					js.showErrorMessage("未找到库存信息！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", list[0]['MAKTX'],'not-editable-cell');
				var options=getCellOption(list,'LIFNR')
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", list[0].LIFNR,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:options} });
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			
			if(cellname=='BIN_CODE'){
				console.info("BATCH:"+row.BATCH)
				console.info("LIFNR:"+row.LIFNR)
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",list[0].STOCK_QTY,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",list[0].STOCK_QTY,'editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",list[0].UNIT,'not-editable-cell');
			}
			
			if(cellname=='LIFNR'){
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH",'&nbsp;','editable-cell');
			}
			if(cellname=='QTY'){
				if(Number(row.QTY)>Number(row.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",row.STOCK_QTY,'editable-cell');
					return false;
				}
			}
		};
		
	}
	
	//310(物料转移)
	if(vm.WMS_MOVE_TYPE =='310' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'接收库位', label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>',
					name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{label: '<span style="color:blue">接收方</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
						name: 'RECEIVER',align:"center", index: 'RECEIVER', width: 70 ,sortable:false,editable:true,}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">接收料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 257,sortable:false, },  			
   			{display:'批次', label: '批次', name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false, editable:false,}, 		
			{display:'接收储位', label: '<span style="color:red">*</span><span style="color:blue">接收储位</span>&nbsp;&nbsp;',
					name: 'BIN_CODE',align:"center", index: 'BIN_CODE', width: 80 ,sortable:false,
					editable:true,editrules:{required:true},}, 								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];
		formatCell=function(rowid, cellname, value, iRow, iCol){
			var rec = $('#dataGrid').jqGrid('getRowData', rowid);
			//console.info(JSON.stringify(rec))
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}
		}
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);

			if(cellname=='MATNR'){//根据物料号列表查询物料信息	
				var info=getMaterialInfo(row.MATNR,row.WERKS);
				if(info==null){
					js.showErrorMessage(row.WERKS+" "+row.MATNR+" 物料信息不存在，请检查是否输入有误，如果输入无误，请使用物料数据同步 功能同步！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", info.MAKTX,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",info.MEINS,'not-editable-cell');
			}
			
			if(cellname=='QTY'){
				var rows=$("#dataGrid").jqGrid('getRowData');
				var total_qty=0;
				$.each(rows,function(i,row){
					total_qty+=Number(row.QTY)					
				})
				console.info(vm.STOCK_QTY)
				if(total_qty>Number(vm.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
					return false;
				}
				
			}
		};
		
	}
	
	//413(自有转销售)
	if(vm.WMS_MOVE_TYPE =='413' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>',
					name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 220,sortable:false, }, 
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商代码</span>', name: 'LIFNR', align:"center",index: 'LIFNR', width: 85,
   				sortable:false,editable:true,edittype:'select',editrules:{required:true}},
   			{display:'批次', label: '<span style="color:red">*</span><span style="color:blue">批次</span>&nbsp;&nbsp;',
				name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},}, 	
			{display:'储位', label: '<span style="color:red">*</span><span style="color:blue">储位</span>&nbsp;&nbsp;',
					name: 'BIN_CODE',align:"center", index: 'BIN_CODE', width: 100 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{ label: '非限制库存', name: 'STOCK_QTY', align:"center",index: 'STOCK_QTY', width: 80,sortable:false,}	,								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];
		
		formatCell=function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			console.info($("#dataGrid").jqGrid('getColProp',cellname))
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}		
			if(cellname=='BATCH'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,row.BIN_CODE,null);
				var options=getCellOption(list,'BATCH')
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:options} });			
			}
			if(cellname=='BIN_CODE'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				if(row.BATCH==undefined||row.BATCH=="&nbsp;"||row.BATCH==''){
					js.showErrorMessage("请输入批次!")
					return false;
				}
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,null,row.BATCH);
				var options=getCellOption(list,'BIN_CODE')
				$("#dataGrid").jqGrid('setCell',rowid, "BIN_CODE", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:options} });			
			}

		};
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);

			if(cellname=='MATNR'){//根据物料号列表查询物料信息	
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,null,null,null);
				if(list.length==0){
					js.showErrorMessage("未找到库存信息！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", list[0]['MAKTX'],'not-editable-cell');
				var options=getCellOption(list,'LIFNR')
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", list[0].LIFNR,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:options} });
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			
			if(cellname=='BIN_CODE'){
				console.info("BATCH:"+row.BATCH)
				console.info("LIFNR:"+row.LIFNR)
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",list[0].STOCK_QTY,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",list[0].STOCK_QTY,'editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",list[0].UNIT,'not-editable-cell');
			}
			
			if(cellname=='LIFNR'){
				var list= getMatStockInfo(row.MATNR,row.WERKS,row.WH_NUMBER,row.LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH",'&nbsp;','editable-cell');
			}
			if(cellname=='QTY'){
				if(Number(row.QTY)>Number(row.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",row.STOCK_QTY,'editable-cell');
					return false;
				}
			}
		};
	}
	
	//413E(销售转销售)
	if(vm.WMS_MOVE_TYPE =='413_E' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>',
					name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">料号</span>', name: 'F_MATNR', align:"center",index: 'F_MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'F_MAKTX',align:"center", index: 'F_MAKTX', width: 220,sortable:false, }, 
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商代码</span>', name: 'LIFNR', align:"center",index: 'LIFNR', width: 85,
   				sortable:false,editable:true,edittype:'select',editrules:{required:true}},
   			{display:'批次', label: '<span style="color:red">*</span><span style="color:blue">批次</span>&nbsp;&nbsp;',
				name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},}, 	
			{display:'储位', label: '<span style="color:red">*</span><span style="color:blue">储位</span>&nbsp;&nbsp;',
					name: 'F_BIN_CODE',align:"center", index: 'F_BIN_CODE', width: 100 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{ label: '非限制库存', name: 'STOCK_QTY', align:"center",index: 'STOCK_QTY', width: 80,sortable:false,}	,								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			{display:'接收库位', label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},},
			{ label: '<span style="color:red">*</span><span style="color:blue">收货料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
	   		{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 220,sortable:false, }, 	
	   		{ label: '<span style="color:red">*</span><span style="color:blue">接收销售订单</span>', name: 'SO_NO', align:"center",index: 'SO_NO', width: 100,editable:true,sortable:false,}, 
	   		{ label: '<span style="color:red">*</span><span style="color:blue">订单行号</span>', name: 'SO_ITEM_NO', align:"center",index: 'SO_ITEM_NO', width: 100,editable:true,sortable:false,}, 
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];

		formatCell=function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			console.info($("#dataGrid").jqGrid('getColProp',cellname))
			if(cellname=='F_LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'F_LGORT', { editoptions: {value:getCellOption(f_lgortList,'LGORT')} });
			}
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}		
			if(cellname=='BATCH'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,row.BIN_CODE,null);
				var options=getCellOption(list,'BATCH')
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:options} });			
			}
			if(cellname=='F_BIN_CODE'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				if(row.BATCH==undefined||row.BATCH=="&nbsp;"||row.BATCH==''){
					js.showErrorMessage("请输入批次!")
					return false;
				}
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,null,row.BATCH);
				var options=getCellOption(list,'BIN_CODE')
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'F_BIN_CODE', { editoptions: {value:options} });			
			}

		};
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='F_LGORT'){
				$("#dataGrid").jqGrid('setCell',rowid, "MATNR",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			if(cellname=='F_MATNR'){ //根据物料号列表查询物料库存信息	
				var list= getMatStockInfo(value,row.WERKS,row.WH_NUMBER,row.F_LGORT,null,null,null);
				if(list.length==0){
					js.showErrorMessage("未找到库存信息！");	
					$("#dataGrid").jqGrid('setCell',rowid, "F_MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "F_MAKTX", list[0]['MAKTX'],'not-editable-cell');
				var options=getCellOption(list,'LIFNR')
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", list[0].LIFNR,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:options} });
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			
			if(cellname=='F_BIN_CODE'){
				console.info("BATCH:"+row.BATCH)
				console.info("LIFNR:"+row.LIFNR)
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",list[0].STOCK_QTY,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",list[0].STOCK_QTY,'editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",list[0].UNIT,'not-editable-cell');
			}
			
			if(cellname=='LIFNR'){
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH",'&nbsp;','editable-cell');
			}
			
			if(cellname=='MATNR'){//根据物料号列表查询物料信息	
				var info=getMaterialInfo(row.MATNR,row.WERKS);
				if(info==null){
					js.showErrorMessage(row.WERKS+" "+row.MATNR+" 物料信息不存在，请检查是否输入有误，如果输入无误，请使用物料数据同步 功能同步！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", info.MAKTX,'not-editable-cell');
			}
			
			if(cellname=='QTY'){
				if(Number(row.QTY)>Number(row.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",row.STOCK_QTY,'editable-cell');
					return false;
				}
			}
			
		};
	}
	
	//411E(销售转自有)
	if(vm.WMS_MOVE_TYPE =='411_E' ){
		columns=[		
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}},    			
   			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, }, 		
   			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>',
					name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 70 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
   			{ label: '<span style="color:red">*</span><span style="color:blue">料号</span>', name: 'F_MATNR', align:"center",index: 'F_MATNR', width: 100,editable:true,sortable:false,}, 			
   			{ label: '物料描述', name: 'F_MAKTX',align:"center", index: 'F_MAKTX', width: 220,sortable:false, }, 
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商代码</span>', name: 'LIFNR', align:"center",index: 'LIFNR', width: 85,
   				sortable:false,editable:true,edittype:'select',editrules:{required:true}},
   			{display:'批次', label: '<span style="color:red">*</span><span style="color:blue">批次</span>&nbsp;&nbsp;',
				name: 'BATCH',align:"center", index: 'BATCH', width: 100 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},}, 	
			{display:'储位', label: '<span style="color:red">*</span><span style="color:blue">储位</span>&nbsp;&nbsp;',
					name: 'F_BIN_CODE',align:"center", index: 'F_BIN_CODE', width: 100 ,sortable:false,
					editable:true,edittype:'select',editrules:{required:true},}, 	
			{ label: '非限制库存', name: 'STOCK_QTY', align:"center",index: 'STOCK_QTY', width: 80,sortable:false,}	,								
   			{display:'数量', label: '<span style="color:red">*</span><span style="color:blue">数量</span>', name: 'QTY', align:"center",index: 'QTY', width: 70,editable:true,editrules:{
   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	, 	
   			{display:'接收库位', label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},},
			{ label: '<span style="color:red">*</span><span style="color:blue">收货料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 100,editable:true,sortable:false,}, 			
	   		{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 220,sortable:false, }, 	
	   		{display:'接收储位', label: '<span style="color:red">*</span><span style="color:blue">接收储位</span>&nbsp;&nbsp;',
				name: 'BIN_CODE',align:"center", index: 'BIN_CODE', width: 85 ,sortable:false,
				editable:true,editrules:{required:true},}, 
	   		{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   			];
		
		formatCell=function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			console.info($("#dataGrid").jqGrid('getColProp',cellname))
			if(cellname=='F_LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'F_LGORT', { editoptions: {value:getCellOption(f_lgortList,'LGORT')} });
			}
			if(cellname=='LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getCellOption(lgortList,'LGORT')} });
			}		
			if(cellname=='BATCH'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,row.BIN_CODE,null);
				var options=getCellOption(list,'BATCH')
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:options} });			
			}
			if(cellname=='F_BIN_CODE'){
				if(row.LIFNR==undefined||row.LIFNR=="&nbsp;"||row.LIFNR==''){
					js.showErrorMessage("请输入供应商代码!")
					return false;
				}
				if(row.BATCH==undefined||row.BATCH=="&nbsp;"||row.BATCH==''){
					js.showErrorMessage("请输入批次!")
					return false;
				}
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,null,row.BATCH);
				var options=getCellOption(list,'BIN_CODE')
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE", list[0].BATCH,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'F_BIN_CODE', { editoptions: {value:options} });			
			}

		};
		
		afterSaveCell=function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='F_LGORT'){
				$("#dataGrid").jqGrid('setCell',rowid, "MATNR",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			if(cellname=='F_MATNR'){ //根据物料号列表查询物料库存信息	
				var list= getMatStockInfo(value,row.WERKS,row.WH_NUMBER,row.F_LGORT,null,null,null);
				if(list.length==0){
					js.showErrorMessage("未找到库存信息！");	
					$("#dataGrid").jqGrid('setCell',rowid, "F_MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "F_MAKTX", list[0]['MAKTX'],'not-editable-cell');
				var options=getCellOption(list,'LIFNR')
				$("#dataGrid").jqGrid('setCell',rowid, "LIFNR", list[0].LIFNR,'editable-cell');
				$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:options} });
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH", '&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
			}
			
			if(cellname=='F_BIN_CODE'){
				console.info("BATCH:"+row.BATCH)
				console.info("LIFNR:"+row.LIFNR)
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",list[0].STOCK_QTY,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",list[0].STOCK_QTY,'editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",list[0].UNIT,'not-editable-cell');
			}
			
			if(cellname=='LIFNR'){
				var list= getMatStockInfo(row.F_MATNR,row.WERKS,row.WH_NUMBER,row.F_LGORT,row.LIFNR,value,row.BATCH);
				$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "QTY",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "UNIT",'&nbsp;','not-editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "F_BIN_CODE",'&nbsp;','editable-cell');
				$("#dataGrid").jqGrid('setCell',rowid, "BATCH",'&nbsp;','editable-cell');
			}
			
			if(cellname=='MATNR'){//根据物料号列表查询物料信息	
				var info=getMaterialInfo(row.MATNR,row.WERKS);
				if(info==null){
					js.showErrorMessage(row.WERKS+" "+row.MATNR+" 物料信息不存在，请检查是否输入有误，如果输入无误，请使用物料数据同步 功能同步！");	
					$("#dataGrid").jqGrid('setCell',rowid, "MATNR", "&nbsp;",'editable-cell');
					return false;
				}
				$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", info.MAKTX,'not-editable-cell');
			}
			
			if(cellname=='QTY'){
				if(Number(row.QTY)>Number(row.STOCK_QTY)){
					js.showErrorMessage("不能超出非限制库存数量！")
					$("#dataGrid").jqGrid('setCell',rowid, "QTY",row.STOCK_QTY,'editable-cell');
					return false;
				}
			}
			
		};
	}
	/**
	 * 初始化表格
	 * @returns
	 */
	dataGrid=$("#dataGrid").dataGrid({
	    datatype: "local",
	    searchForm:$("#searchForm"),
	    cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
	    colModel: columns,
	    viewrecords: false,
	    showRownum:false,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
	    rownumWidth: 25, 
	    /*loadonce:true,*/
	    sortable:false,
	    rowNum:-1,
	    formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
	    dataId:'id',
	   multiselect: multiselect,
	   beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
	   beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
		   var $myGrid = $(this),  
		   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
		   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
		   if(cm[i].name == 'cb'){
			   $('#dataGrid').jqGrid('setSelection',rowid);
		   }   		
		   return (cm[i].name == 'cb');  

		}, 
		formatCell:formatCell,
		afterSaveCell:afterSaveCell,
	});
	
}	

function addMat(){
	if(vm.WERKS == "" ||  vm.WH_NUMBER == ""){
		js.alert("请先选中工厂和仓库号！");
		return false;
	}
	
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');

	//console.info(ids)
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    
    //alert(BIN_CODE)
    var data={}; 
	data.id=rowid;
	
    if(vm.WMS_MOVE_TYPE=='411_K'){
    	 //根据工厂，库存类型查询库位列表
        f_lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);        
        if(f_lgortList == undefined || f_lgortList == null || f_lgortList.length <=0){
        	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
        	return false;
        }
        lgortList=getLgortList(vm.WERKS,null,null,'Z');  
        data.F_WERKS=vm.WERKS;
        data.F_WH_NUMBER=vm.WH_NUMBER;
        data.WERKS=vm.WERKS;
        data.WH_NUMBER=vm.WH_NUMBER;
        data.F_LGORT=f_lgortList[0].LGORT
        data.LGORT=lgortList[0].LGORT
        
    }
    
    if(vm.WMS_MOVE_TYPE=='412_K'){
   	 //根据工厂，库存类型查询库位列表
       f_lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);       
       if(f_lgortList == undefined || f_lgortList == null || f_lgortList.length <=0){
       	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
       	return false;
       }
       lgortList=getLgortList(vm.WERKS,null,null,'K');  
       data.F_WERKS=vm.WERKS;
       data.F_WH_NUMBER=vm.WH_NUMBER;
       data.WERKS=vm.WERKS;
       data.WH_NUMBER=vm.WH_NUMBER;
       data.F_LGORT=f_lgortList[0].LGORT
       data.LGORT=lgortList[0].LGORT
   }
    
    if(vm.WMS_MOVE_TYPE=='309'){
      	 //根据工厂，库存类型查询库位列表
          lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);        
          if(lgortList == undefined || lgortList == null || lgortList.length <=0){
          	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
          	return false;
          }
          data.WERKS=vm.WERKS;
          data.WH_NUMBER=vm.WH_NUMBER;
          data.LGORT=lgortList[0].LGORT
      } 
    
    if(vm.WMS_MOVE_TYPE=='310'){
     	 //根据工厂，库存类型查询库位列表
         lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);        
         if(lgortList == undefined || lgortList == null || lgortList.length <=0){
         	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
         	return false;
         }
         data.WERKS=vm.WERKS;
         data.WH_NUMBER=vm.WH_NUMBER;
         data.LGORT=lgortList[0].LGORT
     } 
    
    if(vm.WMS_MOVE_TYPE=='413'){
    	 //根据工厂，库存类型查询库位列表
        lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);        
        if(lgortList == undefined || lgortList == null || lgortList.length <=0){
        	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
        	return false;
        }
        data.WERKS=vm.WERKS;
        data.WH_NUMBER=vm.WH_NUMBER;
        data.LGORT=lgortList[0].LGORT
    } 
    
    if(vm.WMS_MOVE_TYPE=='413_E'){
    	//判断是否输入了销售订单和行项目号
		if(vm.SO_NO == undefined || vm.SO_NO ==null || vm.SO_NO ==""){
			js.showErrorMessage("请输入销售订单号！");
			$("#SO_NO").focus();
			return false;
		}
		if(vm.SO_ITEM_NO == undefined || vm.SO_ITEM_NO ==null || vm.SO_ITEM_NO ==""){
			js.showErrorMessage("请输入销售订单行号！");
			$("#SO_ITEM_NO").focus();
			return false;
		}
   	 //根据工厂，库存类型查询库位列表
       f_lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);        
       lgortList=f_lgortList;
       if(f_lgortList == undefined || f_lgortList == null || f_lgortList.length <=0){
       	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
       	return false;
       }
       data.WERKS=vm.WERKS;
       data.WH_NUMBER=vm.WH_NUMBER;
       data.LGORT=lgortList[0].LGORT
       data.F_LGORT=lgortList[0].LGORT
   } 
    
    if(vm.WMS_MOVE_TYPE=='411_E'){
	    	//判断是否输入了销售订单和行项目号
			if(vm.SO_NO == undefined || vm.SO_NO ==null || vm.SO_NO ==""){
				js.showErrorMessage("请输入销售订单号！");
				$("#SO_NO").focus();
				return false;
			}
			if(vm.SO_ITEM_NO == undefined || vm.SO_ITEM_NO ==null || vm.SO_ITEM_NO ==""){
				js.showErrorMessage("请输入销售订单行号！");
				$("#SO_ITEM_NO").focus();
				return false;
			}
      	 //根据工厂，库存类型查询库位列表
          f_lgortList=getLgortList(vm.WERKS,null,null,vm.SOBKZ);        
          lgortList=f_lgortList;
          if(f_lgortList == undefined || f_lgortList == null || f_lgortList.length <=0){
          	js.showErrorMessage("工厂："+vm.WERKS+"未配置库存地点！");
          	return false;
          }
          data.WERKS=vm.WERKS;
          data.WH_NUMBER=vm.WH_NUMBER;
          data.LGORT=lgortList[0].LGORT
          data.F_LGORT=lgortList[0].LGORT
      } 
    
    $("#dataGrid").jqGrid("addRowData", rowid, data, "last"); 
    
    $("#WERKS").attr("disabled",true)
	$("#WH_NUMBER").attr("disabled",true)
	
	//$("#dataGrid").jqGrid('setCell', rowid, 'LIFNR', '', 'not-editable-cell');  
   // $("#dataGrid").jqGrid('setCell', rowid, 'BATCH', '', 'not-editable-cell');  
    //$("#dataGrid").jqGrid('setCell', rowid, 'F_BIN_CODE', '', 'not-editable-cell');  
	$('#dataGrid').jqGrid('setColProp', 'LIFNR', { editoptions: {value:""} });
    $('#dataGrid').jqGrid('setColProp', 'BATCH', { editoptions: {value:""} });
    $('#dataGrid').jqGrid('setColProp', 'F_BIN_CODE', { editoptions: {value:""} });
    //alert(rowid)
}

function delMat(id){
	//console.info(id)
	$("#dataGrid").delRowData(id)
}

function getCellOption(list,cellname){
	var options="";
	//var lgortList=getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ);
	for(i=0;i<list.length;i++){
			if(i != list.length - 1) {
				options += list[i][cellname] + ":" + list[i][cellname]  + ";";
			} else {
				options += list[i][cellname]  + ":" +  list[i][cellname] ;
			}
	}
	//alert(options)
	//console.info(options)
	
	return options;
}

function setOption(list,element){
	var str="<option value=''>请选择</option>";
	for(i=0;i<list.length;i++){
		str+="<option value='"+list[i][element]+"' >"+list[i][element]+"</option>";
	}
	$("#"+element).html(str);
}
	
function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
/*	if(ids!=null){
		rowid=Math.min.apply(null,ids);
	}*/
	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	
	if(lastcellname==e){
		$.each(rows,function(i,row){
			console.info(Number(row.ID)+"/"+rowid+"/"+cellvalue)
			if(Number(row.ID)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.ID), e, cellvalue,'editable-cell');
			}
		})
	}
}
/**
 * 输入物料编码查询物料及库存信息
 * @param MANTR,WERKS,WH_NUMBER,LGORT,LIFNR,BIN_CODE,BATCH
 * @returns
 */

function getMatStockInfo(MATNR,WERKS,WH_NUMBER,LGORT,LIFNR,BIN_CODE,BATCH){
	var data = {
			WERKS:WERKS,
			WH_NUMBER:WH_NUMBER,
			LGORT:LGORT,
			MATNR:MATNR,
			LIFNR:LIFNR==null?null:LIFNR.trim(),
			BIN_CODE:BIN_CODE,
			BATCH:BATCH,
			SOBKZ:vm.SOBKZ
	}
	if(vm.WMS_MOVE_TYPE=='413_E'||vm.WMS_MOVE_TYPE=='411_E'){
		data.SO_NO=vm.SO_NO
		data.SO_ITEM_NO=vm.SO_ITEM_NO
	}
	var list=[];
	$.ajax({
		url:baseURL+"common/getMatStockInfo",
		dataType : "json",
		type : "post",
		data : data,
		async: false,
		success: function (response) { 
			list=response.data	
		}
	});
	return list;
}

function getMaterialInfo(MATNR,WERKS){
	var data = {
			WERKS:WERKS,
			MATNR:MATNR,
	}
	var info={};
	$.ajax({
		url:baseURL+"common/getMaterialInfo",
		dataType : "json",
		type : "post",
		data : data,
		async: false,
		success: function (response) { 
			info=response.data	
		}
	});
	return info;
}

function setWerks(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', Number(row.ID), "WERKS", vm.WERKS,'editable-cell');//收货工厂默认为页面工厂下拉列表的值
	})
}

function setWHNumber(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', Number(row.ID), "WH_NUMBER", vm.WH_NUMBER,'editable-cell');//收货工厂默认为页面工厂下拉列表的值
	})
}

function clearQty(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', Number(row.ID), "QTY", '&nbsp;','editable-cell');
	})
}