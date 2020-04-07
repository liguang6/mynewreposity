var lastrow,lastcell;
$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [	
			
			{ label: 'product_type_code', name: 'product_type_code', index: 'product_type_code', width: 80,align:'center',hidden:true  },
			{ label: '产品类别', name: 'product_type_name', index: 'product_type_name', width: 80,align:'center'  },
			{ label: '产品代码', name: 'product_code', index: 'product_code', width: 80,align:'center'},
			{ label: '产品名称', name: 'product_name', index: 'product_name', width: 120,align:'center'},
			{ label: '工位代码', name: 'process_code', index: 'process_code', width: 70,align:'center'},
			{ label: '工位名称', name: 'process_name', index: 'process_name', width: 140,align:'center'},
			{ label: '加工流程代码', name: 'process_flow_code', index: 'process_flow_code', width: 100,align:'center'},
			{ label: '加工流程名称', name: 'process_flow_name', index: 'process_flow_name', width: 120,align:'center'},
			{ label: '状态', name: 'status', index: 'status', width: 80,align:'center',formatter:function(cellval, obj, row){
				var html="";
				if(row.status=='0'){
					html= "<span>正常</span>"
				}
				if(row.status=='1'){
					html= "<span>停用</span>"
				}
				return html;
			}},
			{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>检测节点', name: 'test_node',index: 'test_node',width: "100",align:"center",sortable:false,editable:true,editrules:{required: true},editoptions:{
				dataInit:function(el){
				　  $(el).keydown(function(){
					　  getTestNodeInfoSelect(el,function(node){
					});
				});
			},}},
			{ label: '编辑人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'edit_date', index: 'edit_date', width: 120,align:'center'  },	
			{ label: 'parent_product_id', name: 'parent_product_id', index: 'parent_product_id', width: 80,align:'center',hidden:true  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true }
		],
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			if(cellname=='test_node' && value!=''){
				$.ajax({
					url:baseUrl + "qms/config/checkNode/getList",
					dataType : "json",
					type : "post",
					data : {"testNode":value,"customNoFlag":"X"},
					async: false,
					success: function (response) { 
						if(response.code==0){
							list = response.data;
							if(list.length==0){
								js.showErrorMessage("检测节点【"+value+"】未维护！");
								$("#dataGrid").jqGrid("setCell",rowid,"test_node","&nbsp;");
								return false;
							}
						}else{
							js.showErrorMessage("查找检测节点出错，请联系管理员！");
							$("#dataGrid").jqGrid("setCell",rowid,"test_node","&nbsp;");
							return false;
						}
					}
				});
			}
		},
		viewrecords: true,
		showRownum : true,
        shrinkToFit:false,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
        rowNum: 15,  
		rowList : [15,50 ,100],	
		showCheckbox:true,
		multiselect: true,
    });
});

var vm = new Vue({
	el:'#rrapp',
    data:{
    	WERKS:"",
    	DEVICE_CODE:"",
    	MACHINE_CODE:"",
    	STATUS:"",
    	warehourse:[],
    	workshoplist:[],
    	linelist:[],
    	machinelist:[],
    	WORKSHOP:"",
    	LINE:""
    },
    created: function(){
    	this.WERKS = $("#WERKS").find("option").first().val();
	},
	watch:{
		
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getProductNoFuzzy:function(){
			//模糊查找
			getBjProductNoSelect("#product",fn_cb_getProductCode);
			
		},
		save:function(){
			$("#dataGrid").jqGrid("saveCell", lastrow, lastcell);	
			var savedata=[];
			var ids = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');
            for(var i in ids){
				var rowData = $("#dataGrid").jqGrid('getRowData', ids[i]); 
				savedata.push(rowData);
			}
			if(savedata.length==0){
				js.showErrorMessage("请勾选数据！");
				return;
			}
			$.ajax({
				url : baseURL+"config/bjMesProducts/updateTestNode",
				method:'post',
				dataType:'json',
				async:false,
				data: {"savedata":JSON.stringify(savedata)},
				success:function(response){
					if(response.code==0){
						js.showMessage("保存成功！");
					}
				}
			});
		}
	}
});

function fn_cb_getProductCode(product){
	$("#product").val(product.product_code);
}
function getTestNodeInfoSelect(elementId, fn_backcall) {
	var node={};
	var list;
	$(elementId).typeahead({	
		source : function(input, process) {
			var data={
				"test_node":input,
				"customNoFlag":"X"
			};		
			return $.ajax({
				url:baseUrl + "qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					list = response.data;
					var results = new Array();
					$.each(list, function(index, value) {
						results.push(value.testNode);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var testNode = "";
			$.each(list, function(index, value) {
				if (value.testNode == item) {
					testNode = value.testNode;
				}
			})
			return item;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(list, function(index, value) {
				if (value.testNode == item) {
					node=value;
					$(elementId).val(item);	
				}
			});
			if (typeof (fn_backcall) == "function") {
				fn_backcall(node);
			}
		}
	})
}