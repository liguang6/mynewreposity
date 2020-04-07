var vm = new Vue({
	el : '#page_div',
	data : {
		TEST_TYPE : '', // 检验类别：01大巴 02 专业车
		formUrl : baseUrl + "processQuality/test/getProcessTestRecordList",
		TEST_NODE_LIST : [],
		BUS_NO : '',
		VIN : '',
	},
	watch : {
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				vm.TEST_NODE_LIST = vm.getTestNodes();
			}
		},
	},
	methods:{
		query : function(){
			if($("#VIN").val().trim()==""&&$("#ORDER_NO").val().trim()==""){
				js.showErrorMessage("请输入订单号或者车号/VIN!")
				return false;
			}
			ajaxQuery();
		},
		getTestNodes : function() {
			let list = [];
			$.ajax({
				url : baseUrl + '/qms/common/getTestNodes',
				type : 'post',
				async : false,
				dataType : 'json',
				data : {
					testType : vm.TEST_TYPE,
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
		confirm : function(){
			var rows=getSelectedRows();
			if(rows.length==0){
				js.alert("请选择至少一条记录");
				return false;
			}
			var selectedRows=[];
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				selectedRows.push(row)
			})
			console.info(JSON.stringify(selectedRows))
			
			$.ajax({
				url : baseUrl + "processQuality/test/confirmProcessTest",
				type : 'post',
				async : false,
				dataType : 'json',
				data : {
					recordList : JSON.stringify(selectedRows),
					TEST_CLASS:'整车'
				},
				success : function(resp) {
					if (resp.msg == 'success') {
						js.showMessage("确认成功！")
						vm.query();
					}
				}
			})
		},
	},
	created : function(){

	}
});

getOrderNoSelect("#ORDER_NO", null, function(order){
	vm.TEST_TYPE=order.TEST_TYPE;
})

getBusSelect("#VIN", "#WERKS", "#TEST_TYPE", function(bus) {
	vm.BUS_NO = bus.BUS_NO;
	vm.VIN =  bus.VIN;
	if(vm.TEST_TYPE==""){
		vm.TEST_TYPE=bus.TEST_TYPE;
	}	
	$("#ORDER_NO").val(bus.ORDER_NO)
});

function funFromjs(result) {
	//alert(result)
	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(result).trigger(e);
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = $("#" + ele);
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}


function ajaxQuery(){
	var data = $('#dataGrid').data("dataGrid");
	if (data) {
		//$("#dataGrid").jqGrid('GridUnload');
		$("#dataGrid").setGridParam({rowNum:$("#pageSize").val()||15}).trigger("reloadGrid"); 
	}
	var columns = [ {
		label : '工厂',
		name : 'WERKS',
		align : "center",
		index : 'WERKS',
		width : '60'
	}, {
		label : '检验节点',
		name : 'TEST_NODE',
		align : "center",
		index : 'TEST_NODE',
		width : '90'
	}, {
		label : '车号 ',
		name : 'BUS_NO',
		align : "center",
		index : 'BUS_NO',
		width : '197'
	},  {
		label : 'VIN ',
		name : 'VIN',
		align : "center",
		index : 'VIN',
		width : '160'
	},{
		label : '订单 ',
		name : 'ORDER_TEXT',
		align : "center",
		index : 'ORDER_TEXT',
		width : '210'
	},{
		label : '检验员 ',
		name : 'TESTOR',
		align : "center",
		index : 'TESTOR',
		width : '90'
	},{
		label : '检验日期 ',
		name : 'TEST_DATE',
		align : "center",
		index : 'TEST_DATE',
		width : '160'
	},{
		label : '检验结果',
		name : 'TEST_RESULT',
		align : "center",
		index : 'TEST_RESULT',
		width : '90',
	},{
		label : '确认人 ',
		name : 'CONFIRMOR',
		align : "center",
		index : 'CONFIRMOR',
		width : '90'
	},{
		label : '确认日期 ',
		name : 'CONFIRM_DATE',
		align : "center",
		index : 'CONFIRM_DATE',
		width : '160'
	},{
		label : '查看',
		name : '',
		align : "center",
		index : '',
		width:'60',
		formatter : function(val, obj, row, act) {
			var url=baseUrl + "qms/processQuality/qms_process_test_detail.html?ORDER_NO="+row.ORDER_NO+"&&ORDER_TEXT="+row.ORDER_TEXT
			+"&TEST_TYPE="+row.TEST_TYPE+"&VIN="+row.VIN+"&BUS_NO="+row.BUS_NO+"&TEST_NODE="+row.TEST_NODE
			+"&WERKS="+row.WERKS;
			var html= '<a href="'+url+'"  class="addTabPage" title="记录查看"><i class="fa fa-search fa-lg" style="cursor:pointer"></i>';
			if(row.TEST_RESULT !='未录入'){
				return html;
			}
			
			return "";
		},
		unformat:function(){
			return "";
		}
		
	},
	{
		name : 'TEST_TYPE',
		index : 'TEST_TYPE',
		hidden: true
	},
	];

	dataGrid = $("#dataGrid")
			.dataGrid(
					{
						searchForm : $("#searchForm"),
						colModel : columns,
						cellEdit:true,
						cellurl:'#',
						cellsubmit:'clientArray',
						viewrecords: false,
						rownumbers:false,
					    shrinkToFit:true,
					    autowidth:true,
					    showRownum:false,
						rowNum: 15,  
						rowList : [15,30 ,50],	
						//dataid:'id',
						multiselect: true,
						beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
							   var $myGrid = $(this),  
							   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
							   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
							   //alert(JSON.stringify(cm))
							   if(cm[i].name == 'cb'){
								   console.info(rowid)
								   $('#dataGrid').jqGrid('setSelection',rowid);
							   }
							   		
							   return (cm[i].name == 'cb');  
							  /* $('#dataGrid').jqGrid('setSelection',rowid);
							   return false;*/
							}, 
					});
}