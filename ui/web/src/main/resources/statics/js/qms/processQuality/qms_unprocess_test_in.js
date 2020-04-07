var dataGrid;
var lastrow,lastcell,lastcellname,lastrowid;
var deleteList=[];
var gridData=[];
var vm = new Vue({
	el : '#page_div',
	data : {
		TEST_NODE_LIST :[],
		TEST_TYPE : '',
		BUS_NO:'',
		VIN : '',
		formUrl: baseUrl + "processQuality/test/getUnProcessTestList",
	},
	watch : {
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				vm.TEST_NODE_LIST = vm.getTestNodes(newVal);
				//vm.query();
			}
		},
	},
	methods : {
		query : function() {
			ajaxQuery();
			showGrid();		
		},
		getTestNodes : function(TEST_TYPE) {
			let list = [];
			$.ajax({
				url : baseUrl + '/qms/common/getTestNodes',
				type : 'post',
				async : false,
				dataType : 'json',
				data : {
					testType : TEST_TYPE,
					TEST_CLASS:'整车'
				},
				success : function(resp) {
					if (resp.msg == 'success') {
						list = resp.list;
					}
				}
			})
			return list;
		},
		saveRecord : function(){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows = $("#dataGrid").jqGrid('getRowData');
			if(rows.length<=0){
				js.showErrorMessage("无需要保存的数据！");
				return false;
			}
			var saveFlag=true;
			$.each(rows,function(i,row){
				row.WERKS=$("#WERKS").val();
				if(row.ORDER_NO == undefined || row.ORDER_NO ==""){
					js.showErrorMessage("第"+(i+1)+"行不是有效车号或者VIN号！");
					saveFlag=false;
					return false;
				}
				if(row.TEST_NODE == undefined || row.TEST_NODE ==""){
					js.showErrorMessage("第"+(i+1)+"未输入检验节点！");
					saveFlag=false;
					return false;
				}
				if(row.TEST_ITEM == undefined || row.TEST_ITEM ==""){
					js.showErrorMessage("第"+(i+1)+"未输入检验项目！");
					saveFlag=false;
					return false;
				}
				if(row.FAULT_TYPE == undefined || row.FAULT_TYPE ==""){
					js.showErrorMessage("第"+(i+1)+"未输入问题分类！");
					saveFlag=false;
					return false;
				}
				if(row.RECTIFY_UNIT == undefined || row.RECTIFY_UNIT ==""){
					js.showErrorMessage("第"+(i+1)+"未输入整改单位！");
					saveFlag=false;
					return false;
				}			
			})
			
			/**
				 * AJAX 保存检验记录
				 */
			if (saveFlag)
				$.ajax({
					url : baseUrl + "processQuality/test/saveUnTestRecord",
					type : 'post',
					async : false,
					dataType : 'json',
					data : {
						recordList : JSON.stringify(rows),
						deleteIds : deleteList.join(",")
					},
					success : function(resp) {
						if (resp.msg == 'success') {
							js.showMessage("保存成功！");
							$('#dataGrid').trigger('reloadGrid');
							
							//$("#VIN").val("")
						}else{
							js.showErrorMessage("系统异常，保存失败!");
							return false;
						}
					}
				})
		},
		
	},
	created : function(){
		this.TEST_TYPE='01';
		this.$nextTick(function(){
			showGrid();
		})
	}
});

$(function() {
	getBusSelect("#VIN", "#WERKS", "#TEST_TYPE", function(bus) {
		vm.VIN = bus.VIN;
		vm.BUS_NO = bus.BUS_NO;
	});
	
	$(document).on("input", "#VIN", function() {
		vm.VIN=""
		vm.BUS_NO=""
	})
	
	getOrderNoSelect("#ORDER_NO", null, function(order) {
		console.info(order)
	})
	
	$(document).on("input", "input[name='BUS_NO' ]", function(e) {
		getBusSelect(e.target, "#WERKS", null, function(bus) {
			console.info("--->bus info :"+JSON.stringify(bus))
			console.info("--->lastrowid :"+lastrowid)
			$("#dataGrid").jqGrid('setCell', lastrowid,"VIN", bus.VIN||'','editable-cell',{editable:true});
			$("#dataGrid").jqGrid('setCell', lastrowid,"ORDER_DESC", bus.ORDER_TEXT||'','editable-cell',{editable:true});
			$("#dataGrid").jqGrid('setCell', lastrowid,"TEST_TYPE", bus.TEST_TYPE||'');
			$("#dataGrid").jqGrid('setCell', lastrowid,"ORDER_NO", bus.ORDER_NO||'');
		});
	})
	
	$(document).on("input", "input[name='VIN' ]", function(e) {
		var vin = "";
		getBusSelect(e.target, "#WERKS", null, function(bus) {
			console.info("--->lastrowid :"+lastrowid)
			console.info("--->VIN :"+bus.VIN)
			vin=bus.VIN;
			$("#dataGrid").jqGrid('setCell', lastrowid,"BUS_NO", bus.BUS_NO||'','editable-cell',{editable:true});
			//$("#dataGrid").jqGrid('setCell', lastrowid,"VIN", $(e.target).val(),'editable-cell',{editable:true});
			$(e.target).val(bus.VIN)
			$("#dataGrid").jqGrid('setCell', lastrowid,"ORDER_DESC", bus.ORDER_TEXT||'','editable-cell',{editable:true});
			$("#dataGrid").jqGrid('setCell', lastrowid,"TEST_TYPE", bus.TEST_TYPE||'');
			$("#dataGrid").jqGrid('setCell', lastrowid,"ORDER_NO", bus.ORDER_NO||'');
		});
	})
	
});	

function showGrid(){
	var data = $('#dataGrid').data("dataGrid");
	
	if (data) {
		$("#dataGrid").jqGrid('GridUnload');
	}
	var columns =[
		{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
			return obj.ID
		}},
		{ label: '<i class=\"fa fa-plus fa-lg\" style=\"color:blue\" onclick=\"addItem()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 70, sortable:false,formatter:function(val, obj, row, act){
				var actions = [];
				actions.push('<a href="#" onClick="delItem('+row.ID+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times fa-lg" style=\"color:red\"></i></a>');
				actions.push('<a href="#" onClick="copyItem('+row.ID+')" title="复制" ><i class="fa fa-clone " style=\"color:green\"></i></a>');
				return actions.join("&nbsp;&nbsp;&nbsp;&nbsp;");
			},unformat:function(cellvalue, options, rowObject){
			return "";
		}}, 
		{ label: '车号', name: 'BUS_NO',align:"left", index: 'BUS_NO', width: 150,sortable:false, editable : true,}, 	
		{ label: 'VIN号', name: 'VIN',align:"left", index: 'VIN', width: 150,sortable:false, editable : true,}, 	
		{ label: '订单', name: 'ORDER_DESC',align:"center", index: 'ORDER_DESC', width: 150,sortable:false, editable : false,}, 
		{ label: '检验节点', name: 'TEST_NODE',align:"center", index: 'TEST_NODE', width: 80,sortable:false, editable : true,edittype:'select',}, 
		{ label: '检验项目', name: 'TEST_ITEM',align:"center", index: 'TEST_ITEM', width: 180,sortable:false, editable : true,}, 
		{ label: '判定', name: 'JUDGE',align:"center", index: 'JUDGE', width: 50,sortable:false, editable : true,edittype:'select',}, 
		{ label: '问题分类', name: 'FAULT_TYPE',align:"center", index: 'FAULT_TYPE', width: 100,sortable:false, editable : true,edittype:'select',}, 
		{ label: '整改单位', name: 'RECTIFY_UNIT',align:"center", index: 'RECTIFY_UNIT', width: 100,sortable:false, editable : true,edittype:'select',}, 
		{ label: '检验员', name: 'TESTOR',align:"center", index: 'TESTOR', width: 90,sortable:false, }, 	
		{ label: '维护时间', name: 'TEST_DATE',align:"center", index: 'TEST_DATE', width: 120,sortable:false, }, 	
		{label: '', name: 'TEST_TYPE',align:"center", index: 'TEST_TYPE', hidden:true,},
		{label: '', name: 'RECORD_ID',align:"center", index: 'RECORD_ID', hidden:true,},
		{label: '', name: 'ORDER_NO',align:"center", index: 'ORDER_NO', hidden:true,},
		{label: '', name: 'UPDATE_FLAG',align:"center", index: 'UPDATE_FLAG', hidden:true,},
	]
	
	dataGrid = $("#dataGrid") .dataGrid({
				datatype: "local",
				data:gridData,
				searchForm : $("#searchForm"),
				cellEdit : true,
				cellurl : '#',
				cellsubmit : 'clientArray',
				colModel : columns,
				viewrecords : true,
				showRownum : false,
				width : "100%",
				autowidth : true,
				height : "100%",
				shrinkToFit : true,
				autoScroll : true,
				rownumWidth : 25,
				rowNum : -1,
				//loadonce : true,
				emptyrecords:"",
				formatter : {
					number : {
						decimalSeparator : ".",
						thousandsSeparator : " ",
						decimalPlaces : 3,
						defaultValue : ''
					}
				},
				/* dataId:'ID', */
				multiselect : false,
				loadComplete : function(data) {
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if (data.code == 500) {
						js.showErrorMessage(data.msg, 1000 * 10);
						return false;
					}
				},	
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrowid=rowid;
					lastrow=iRow;
					lastcell=iCol;
					lastcellname=cellname;
					console.info("lastrow:"+lastrow+ "  lastcell:"+lastcell)
				},
				formatCell : function(rowid, cellname, value, iRow, iCol) {					 
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
				
					if (cellname == 'TEST_NODE') {	
						$('#dataGrid').jqGrid('setColProp', 'TEST_NODE', {
							editoptions : {
								value : "OK:OK;NG:NG;NA:NA"
							}
						});
					}
					
					if (cellname == 'JUDGE') {					
						$('#dataGrid').jqGrid('setColProp', 'JUDGE', {
							editoptions : {
								value : "OK:OK;NG:NG;NA:NA"
							}
						});
					}
					
					if(cellname == 'FAULT_TYPE' ){
						$('#dataGrid').jqGrid('setColProp', 'FAULT_TYPE', { editoptions: {value:getSelectOptions("#FAULT_TYPE") } })					
					}
					
					if(cellname == 'RECTIFY_UNIT' ){
						$('#dataGrid').jqGrid('setColProp', 'RECTIFY_UNIT', { editoptions: {value:getSelectOptions("#RECTIFY_UNIT") } })					
					}
					
					if(cellname == 'TEST_NODE' ){
						var list = vm.getTestNodes(rec.TEST_TYPE);			
						var options="";
						for(i=0;i<list.length;i++){
							if(i != list.length - 1) {
								options += list[i].TEST_NODE + ":" + list[i].TEST_NODE   + ";";
							} else {
								options += list[i].TEST_NODE  + ":" + list[i].TEST_NODE 
							}
						}
						$('#dataGrid').jqGrid('setColProp', 'TEST_NODE', { editoptions: {value:options} })					
					}
				},
				afterSaveCell : function(rowid, cellname, value, iRow, iCol) {
					$("#dataGrid").jqGrid('setCell', rowid,"UPDATE_FLAG", 'X');
				}
				
				
			});	
	
}


function addItem(){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);	
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    
	 var data={};
	 data.ID=rowid;
	 data.BUS_NO="";
	 data.VIN="";
	 data.ORDER_DESC="";
	 data.TEST_NODE="";
	 data.TEST_ITEM="";
	 data.JUDGE="";
	 data.FAULT_TYPE="";
	 data.RECTIFY_UNIT="";
	 data.TESTOR="";
	 data.TEST_DATE="";
	 data.UPDATE_FLAG="X";
	 
	 console.info(rowid)
	 $("#dataGrid").jqGrid("addRowData", rowid, data, "first"); 
}

function copyItem(rowid){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);	
	var row = $('#dataGrid').jqGrid('getRowData', rowid);
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
    var max_rowid = Math.max.apply(null,ids) ;
    
	var data={};
	 data.ID=max_rowid+1;
	 data.BUS_NO="";
	 data.VIN="";
	 data.ORDER_DESC="";
	 data.TEST_NODE=row.TEST_NODE;
	 data.TEST_ITEM=row.TEST_ITEM;
	 data.JUDGE=row.JUDGE;
	 data.FAULT_TYPE=row.FAULT_TYPE;
	 data.RECTIFY_UNIT=row.RECTIFY_UNIT;
	 data.TESTOR="";
	 data.TEST_DATE="";
	 data.UPDATE_FLAG="X";
	 console.info(max_rowid+1)
	 $("#dataGrid").jqGrid("addRowData", max_rowid+1, data, "before",rowid); 
}

function delItem(rowid){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);	
	console.info(rowid)
	var row = $('#dataGrid').jqGrid('getRowData', rowid);
	if(row.RECORD_ID!=undefined && row.RECORD_ID !=""){
		deleteList.push(row.RECORD_ID)
	}
	
	$("#dataGrid").delRowData(rowid)
}

function getSelectOptions(elementId){
	var options="";
	var select_options = $(elementId).find("option");
	for(i=0;i<select_options.length;i++){
			if(i != select_options.length - 1) {
				options += $(select_options[i]).text() + ":" + $(select_options[i]).val()  + ";";
			} else {
				options += $(select_options[i]).text() + ":" + $(select_options[i]).val()
			}
	}
	//alert(options)
	console.info(options)
	
	return options;
}

function ajaxQuery(){
	var params=$("#searchForm").serialize();
	$.ajax({
		url : vm.formUrl,
		type : 'post',
		async : false,
		dataType : 'json',
		data : params,
		success : function(resp) {
			if (resp.msg == 'success') {
				gridData=resp.page.list;
			}
		}
	})
}