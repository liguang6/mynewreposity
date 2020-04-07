var vm = new Vue({
	el : '#page_div',
	data : {
		TEST_GROUP : '品质', // 品质、生产
		formUrl : baseUrl + "processQuality/test/getProcessTestDetail",
		ORDER_NO:'',
		ORDER_TEXT:'',
		VIN:'',
		BUS_NO:'',
		TEST_NODE:'',
		TEST_TYPE :'',
		WERKS:'',
		TEST_TOOL_NO:'',
		FAULT_TYPE_LIST:[],
	},
	watch : {
		TEST_GROUP : {
			handler : function(newVal, oldVal) {
				ajaxQuery();
			}
		},
	},
	methods : {
		getFaultList:function(faultName){
			var _FAULT_TYPE_LIST=[];
			var _FAULT_LIST=[];
			var obj=ajaxGetFaultList();
			_FAULT_TYPE_LIST=obj.FAULT_TYPE_LIST;
			_FAULT_LIST=obj.FAULT_LIST;
			
			if(faultName==null||faultName==""){
				this.FAULT_TYPE_LIST=_FAULT_TYPE_LIST
			}			
			//console.info(this.FAULT_TYPE_LIST)
		},
	},
	created:function(){
		this.TEST_TYPE=getQueryString("TEST_TYPE")||""
		this.ORDER_NO=getQueryString("ORDER_NO")||""
		this.ORDER_TEXT=getQueryString("ORDER_TEXT")||""
		this.BUS_NO=getQueryString("BUS_NO")||""
		this.VIN=getQueryString("VIN")||""
		this.TEST_NODE=getQueryString("TEST_NODE")||""
		this.WERKS=getQueryString("WERKS")||"";
		this.getFaultList();
	}
		
});	
$(function(){
	ajaxQuery();
})

function ajaxQuery(){
	var data = $('#dataGrid').data("dataGrid");
	if (data) {
		$("#dataGrid").jqGrid('GridUnload');
	}
	var columns = [ {
		label : '序号',
		name : 'ID',
		index : 'ID',
		align : "left",
		width : 60,
		key : true,
		hidden : true
	},{
		label : '异常信息',
		name : 'ABNORMAL_INFO',
		index : 'ABNORMAL_INFO',
		hidden : true
	}, {
		label : '工序名称',
		name : 'PROCESS_NAME',
		align : "center",
		index : 'PROCESS_NAME',
		width : '140'
	}, {
		label : '检验项目',
		name : 'TEST_ITEM',
		align : "center",
		index : 'TEST_ITEM',
		width : '160'
	}, {
		label : '标准要求 ',
		name : 'TEST_STANDARD',
		align : "center",
		index : 'TEST_STANDARD',
		width : '320'
	}, {
		label : '检验结果',
		name : 'TEST_RESULT',
		align : "center",
		index : 'TEST_RESULT',
		width : '150',
		editable : true,
	},  {
		label : '数值项',
		name : 'NUMBER_FLAG',
		align : "center",
		index : 'NUMBER_FLAG',
		width : '60',
		formatter : function(val, obj, row, act) {
			if (val == 'X') {
				return '是'
			} else
				return '否';
		},
		unformat : function(cellvalue, options, rowObject) {
			return cellvalue;
		}
	},  {
		label : '一次交检项',
		name : 'ONE_PASSED_FLAG',
		align : "center",
		index : 'ONE_PASSED_FLAG',
		width : '75',
		formatter : function(val, obj, row, act) {
			if (val == 'X') {
				return '是'
			} else
				return '否';
		},
		unformat : function(cellvalue, options, rowObject) {
			return cellvalue;
		}
	},{
		label : '判定',
		name : 'JUDGE',
		align : "center",
		index : 'JUDGE',
		width : '80',
	}, {
		label : '复检结果',
		name : 'RE_TEST_RESULT',
		align : "center",
		index : 'RE_TEST_RESULT',
		width : '80',
	}, {
		label : '复判',
		name : 'RE_JUDGE',
		align : "center",
		index : 'RE_JUDGE',
		width : '80',
		sortable : false,
	}, {
		label : '现场图片',
		name : 'PHOTO',
		align : "center",
		index : 'PHOTO',
		formatter : function(val, obj, row, act) {
			if (val !="" && val !=null) {
				return  '<a href="'+ row.PHOTO+ '" class="addTabPage" title="图片查看">'+ '<i class="fa fa-search fa-lg" style="cursor:pointer" ></i>' + '</a>';
			} else
				return '';
		},
	}, {
		label : '异常详情',
		name : 'ABNORMAL',
		align : "center",
		index : 'ABNORMAL',
		width : '100',
		formatter : function(val, obj, row, act) {
			if (row.ABNORMAL_INFO !="" && row.ABNORMAL_INFO !=null) {
				return   '<i class="fa fa-search fa-lg" style="cursor:pointer" onclick="showException(\''
				+ row.ID + '\')"></i>';
			} else
				return '';
		},
		unformat:function(val, options, rowObject){
			console.info(rowObject.ABNORMAL_INFO)
			return rowObject.ABNORMAL_INFO;
		},
	},  {
		label : '检具编号',
		name : 'TEST_TOOL_NO',
		align : "center",
		index : 'TEST_TOOL_NO',
		width : '160'
	},{
		label : '创建人',
		name : 'CREATOR',
		align : "center",
		index : 'CREATOR',
		width : '80'
	},{
		label : '创建时间',
		name : 'CREATE_DATE',
		align : "center",
		index : 'CREATE_DATE',
		width : '135'
	},{
		label : '修改人',
		name : 'EDITOR',
		align : "center",
		index : 'EDITOR',
		width : '80'
	},{
		label : '修改时间',
		name : 'EDIT_DATE',
		align : "center",
		index : 'EDIT_DATE',
		width : '135'
	},{
		label : '图片地址',
		name : 'PHOTO_URL',
		index : 'PHOTO_URL',
		hidden : true
	},{
		label : 'TEST_TOOL_NO',
		name : 'TEST_TOOL_NO',
		index : 'TEST_TOOL_NO',
		hidden : true
	},
	];

	dataGrid = $("#dataGrid")
			.dataGrid(
					{
						searchForm : $("#searchForm"),
						colModel : columns,
						viewrecords: false,
						rownumbers:false,					
					    shrinkToFit:false,
					    autowidth:true,
					    loadComplete: function(data){
					    	if (data.code == 500) {
								js.showErrorMessage(data.msg, 1000 * 10);								
								return false;
							}
					    	var rows = $("#dataGrid").jqGrid('getRowData');
					    	if(rows.length>0){
					    		vm.TEST_TOOL_NO = rows[0].TEST_TOOL_NO;
					    	}					    	
					    	js.closeLoading();
					    },
					});
}

function showException(rowId) {
	var row = $("#dataGrid").jqGrid("getRowData", rowId);
	
	var ABNORMAL_INFO = {};
	if (row.ABNORMAL_INFO != null && row.ABNORMAL_INFO != "") {
		ABNORMAL_INFO = JSON.parse(row.ABNORMAL_INFO.replace('///g', ''));
	}
	console.info(row)
	layer.open({
		type : 2,
		title : [ '异常查看', 'font-size:18px' ],
		closeBtn : 1,
		btn : [ '关闭' ],
		offset : [ '100px', '' ],
		area : [ '580px', '400px' ],
		skin : 'layui-bg-green', // 没有背景色
		maxmin : true,
		shadeClose : true,
		content : baseUrl + "/qms/processQuality/qms_abnormal_info.html",
		success : function(layero, index) {
			var win = window[layero.find('iframe')[0]['name']];
			win.vm.FAULT_TYPE_LIST=vm.FAULT_TYPE_LIST||[];
			win.vm.RESP_WORKGROUP =ABNORMAL_INFO.RESP_WORKGROUP||"";
			win.vm.WERKS =ABNORMAL_INFO.RESP_WERKS||"";
			win.vm.RESP_UNIT=ABNORMAL_INFO.RESP_UNIT || "";
			win.vm.REASON=ABNORMAL_INFO.REASON || "";
			win.vm.SOLUTION=ABNORMAL_INFO.SOLUTION || "";
			win.vm.SOLUTION_PERSON=ABNORMAL_INFO.SOLUTION_PERSON || "";
			win.vm.BAD_CLASS=ABNORMAL_INFO.BAD_CLASS || "";
			win.vm.BAD_SOURCE=ABNORMAL_INFO.BAD_SOURCE || "";
			win.vm.MEMO=ABNORMAL_INFO.MEMO || "";
			//win.vm.CONFIRMOR=ABNORMAL_INFO.CONFIRMOR
		},
		btn1 : function(index, layero) {
			
		},
	});

}

function ajaxGetFaultList(faultName){
	var _fault={};
	$.ajax({
		url:baseUrl+'/processQuality/test/getFaultList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			faultName:faultName,
		},
		success:function(resp){
			if(resp.msg=='success'){	
				_FAULT_TYPE_LIST=resp.faultTypeList;
				_FAULT_LIST=resp.data;
				_fault.FAULT_TYPE_LIST=_FAULT_TYPE_LIST;
				_fault.FAULT_LIST=_FAULT_LIST;
				//console.info(this.FAULT_TYPE_LIST)
			}
		}			
	})
	return _fault;
}
