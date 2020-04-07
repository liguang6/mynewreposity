var product_type_option = "";
var product_option = "";
var process_flow_option = "";
var process_option="";
var mydata =[];
var lastrow,lastcell,lastcellname,last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks: '',
		werks_name: '',
		workshopList: [],
		workgroupList:[],
		workshop: '',
		workshop_name: '',
		order_no: '',
		product_code: '',
		product_type_code:'',
		
		show:false,
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
		
	},
	methods: {
		getInfo:function(werks,workshop,order_no,product_type_code,werks_name,workshop_name){
			vm.werks=werks;
			vm.workshop=workshop;
			vm.order_no=order_no;
			vm.product_type_code=product_type_code;
			vm.werks_name=werks_name;
			vm.workshop_name=workshop_name;
		},
		//清空表格数据
		clearTable: function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			vm.product_code='';
		},
		
		query: function(){
			$("#dataGrid").jqGrid('clearGridData');
         	vm.queryInfo();
		},
		queryInfo: function(){
			$.ajax({
				url:baseUrl + "config/bjMesOrderProducts/getList",
				data:{
					"order_no":vm.order_no,
					"werks":vm.werks,
					"workshop":vm.workshop,
					"product_type_code":vm.product_type_code,
					"product_code":vm.product_code
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							js.showMessage("未找到符合条件的供货数据！");
							return false;
						}
						addMat(response.data);
					}
				},
			});

		},
		
		save: function(index){
           	//初始化工厂、车间、线别名称值
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#dataGrid").jqGrid('getRowData');
			if(rows.length <= 0){
				js.closeLoading();
				$("#btnSave").attr("disabled", false);
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			for(var i in rows){
				
			}
			
			js.loading("正在保存数据，请稍等...");
			$("#btnSave").html("保存中...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				type : "post",
				dataType: "json",
				async : true,
				url :baseUrl+ "config/bjMesOrderProducts/saveOrderMap",
				data : {
					"ARRLIST": JSON.stringify(rows),
				},
				success:function(response){
					$("#btnSave").html("保存");	
					$("#btnSave").removeAttr("disabled");
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.code==0){
						vm.clearTable();
						var index =layer.index;// parent.layer.getFrameIndex(window.name); 
						layer.close(index);  
						
						js.alert("操作成功！");
			        	 	
					}	
				}	
			});				
		}
	},
	watch: {}
});
$(function () {
    
	fun_ShowTable();

});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{ label: ' ', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				return actions.join(",");
   			}},
   			
			{ label: '订单编号',name:'order_no', align:'center', index: 'order_no', width: 100,hidden:true }, 
			{ label: '工厂', name: 'werks', align:'center', index: 'werks', width: 80,hidden:true }, 
			{ label: '工厂名称', name: 'werks_name', align:'center', index: 'werks_name', width: 80,hidden:true }, 	
			{ label: '车间', name: 'workshop', index: 'workshop', width: 80,align:'center',hidden:true  },	
			{ label: '车间名称', name: 'workshop_name', align:'center', index: 'workshop_name', width: 120,hidden:true }, 
   			{ label: '产品类别', name: 'product_type_code', align:'center', index: 'product_type_code', width: 120,hidden:true }, 
			
			{ label: '图号', name: 'map_no', align:'center', index: 'map_no', width: 100,editable:true }, 
			{ label: '图纸名称', name: 'map_name', align:'center', index: 'map_name', width: 100,editable:true },
			{ label: '备注', name: 'memo', align:'center', index: 'memo', width: 100,editable:true },

			{ label: 'id', name: 'id', index: 'id', width: 80,align:'center',hidden:true }
    		
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,	
        gridComplete:function(){
        	
        },
 	   	beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		formatCell:function(rowid, cellname, value, iRow, iCol){}
    });
}

function addMat(matList){
	$("#dataGrid").jqGrid('clearGridData');
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	var addedMatStr = "";
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){	
		addedMatStr += ";" + row.order_no +";"
	});	
    var rowid = Math.max.apply(null,ids);
	$.each(matList,function(i,row){	
		//if(addedMatStr.indexOf(";" + row.order_no+";") == -1){
			var data = {};
			data.id = ++rowid;
			data.order_no = row.order_no;		
			
			data.werks = row.werks;
			data.workshop_name = row.workshop_name;
			data.workshop = row.workshop;
			data.product_type_code = row.product_type_code;
			data.product_code = row.product_code;
			data.product_name = row.product_name;
			
			data.process_flow_code = row.process_flow_code;
			data.process_flow_name = row.process_flow_name;
			data.process_code = row.process_code;
			data.process_name = row.process_name;
			data.sap_no = row.sap_no;
			data.single_qty = row.single_qty;
			data.standard_hours = row.standard_hours;
			data.editor = row.editor;
			data.edit_date = row.edit_date;

			data.product_id=row.product_id;
			data.product_code_a=row.product_code_a;
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
		/*}else{
			js.showMessage(row.product_code+"数据重复！");
		}   */ 	
	});	
}
//生成
function addsc(matList){
	$("#dataGrid").jqGrid('clearGridData');
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	vm.workshop_name=$("#workshop").find("option:selected").text();
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}

    var rowid = Math.max.apply(null,ids);
	$.each(matList,function(i,row){
			var data = {};
			data.id = ++rowid;
			data.order_no = vm.order_no;
			data.werks = vm.werks;
			data.workshop = vm.workshop;
			data.workshop_name = vm.workshop_name;

			data.product_type_code = row.product_type_code;
			data.product_code = row.product_code;
			data.product_name = row.product_name;
			
			data.process_flow_code = row.process_flow_code;
			data.process_flow_name = row.process_flow_name;
			data.process_code = row.process_code;
			data.process_name = row.process_name;
			data.sap_no = row.sap_no;
			data.single_qty = row.single_qty;
			data.standard_hours = row.standard_hours;
			data.editor = row.editor;
			data.edit_date = row.edit_date;

			data.product_id=row.id;
			data.product_code_a=row.product_code;
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
		/*}else{
			js.showMessage(row.product_code+"数据重复！");
		}   */ 	
	});	
}

function addNewRow(){
	
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}

    var rowid = Math.max.apply(null,ids);
	
			var data = {};
			data.id = ++rowid;
			data.order_no = vm.order_no;
			data.werks = vm.werks;
			data.werks_name = vm.werks_name;
			data.workshop = vm.workshop;
			data.workshop_name = vm.workshop_name;
			data.product_type_code=vm.product_type_code;
			
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
		
	
}

function delMat(id){	
	var data=$("#dataGrid").jqGrid('getRowData',id);
	$("#dataGrid").delRowData(id);
}
function openEditWindow(id){
	
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: false,
		title: "",
		area: ["80%", "80%"],
		content: baseUrl + "config/bjMesOrderProducts/getOrderMapList",
		end:function(){
			$("#searchForm").submit();
		}
	};
	js.layer.open(options);
}




