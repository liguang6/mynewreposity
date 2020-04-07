var vm = new Vue({
	el : '#page_div',
	data : {
		TEST_TYPE : '', // 检验类别：01大巴 02 专业车
		formUrl : baseUrl + "processQuality/test/getPartsTestRecordList",
		TEST_NODE_LIST : [],
	},
	watch : {
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				vm.TEST_NODE_LIST = vm.getTestNodes();
			}
		},
	},
	methods : {
		query : function() {
			if($("#CAB_NO").val().trim()==""&&$("#ORDER_NO").val().trim()==""){
				if(vm.TEST_TYPE=='01'){
					js.showErrorMessage("请输入订单号或自编号!")
				}
				if(vm.TEST_TYPE=='02'){
					js.showErrorMessage("请输入订单号或驾驶室编号!!")
				}
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
					TEST_CLASS:'零部件'
				},
				success : function(resp) {
					if (resp.msg == 'success') {
						list = resp.list;
					}
				}
			})
			return list;
		},
	},
	created:function(){
		this.TEST_TYPE='01'
	}
		
});	

$(function(){
	var cd=new Date();
	l_y = cd.getFullYear();
	l_m = cd.getMonth();
	l_d=1;
	$("#start_date").val(formatDate(new Date(l_y,l_m,l_d)))
	$("#end_date").val(getCurDate());
	
	getCABSelect("#CAB_NO", "#WERKS", "#TEST_TYPE", function(bus) {
		
	});
	
	getOrderNoSelect("#ORDER_NO", null, null)
	
})

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
		$("#dataGrid").jqGrid('GridUnload');
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
		width : '120'
	}, {
		label :vm.TEST_TYPE=='01' ? '自编号' : '驾驶室',
		name : vm.TEST_TYPE=='01' ? 'CUSTOM_NO' : 'CAB_NO',
		align : "center",
		index : vm.TEST_TYPE=='01' ? 'CUSTOM_NO' : 'CAB_NO',
		width : '170'
	}, {
		label : '订单 ',
		name : 'ORDER_TEXT',
		align : "center",
		index : 'ORDER_TEXT',
		width : '230'
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
		label : '一次交检结果',
		name : 'ONE_PASS_STATUS',
		align : "center",
		index : 'ONE_PASS_STATUS',
		width : '95',
	}, {
		label : '操作',
		name : '',
		align : "center",
		index : '',
		width:'60',
		formatter : function(val, obj, row, act) {
			var CUSTOM_NO= Base64.encode(row.CUSTOM_NO||"");
				//encodeURIComponent(row.CUSTOM_NO||"");
			var url=baseUrl + "qms/processQuality/qms_parts_test_detail.html?ORDER_NO="+row.ORDER_NO+"&&ORDER_TEXT="+row.ORDER_TEXT
			+"&TEST_TYPE="+row.TEST_TYPE+(vm.TEST_TYPE=='01'?("&CUSTOM_NO="+CUSTOM_NO):("&CAB_NO="+row.CAB_NO))
			+"&TEST_NODE="+row.TEST_NODE +"&WERKS="+row.WERKS;
			var html= '<a href="'+url+'"  class="addTabPage" title="记录查看"><i class="fa fa-search fa-lg" style="cursor:pointer"></i>';
			if(row.TEST_RESULT !='未录入'){
				return html;
			}
			
			return "";
		},
		
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
						viewrecords: true,
						rownumbers:false,
					    shrinkToFit:true,
					    autowidth:true,
						rowNum: 15,  
						rowList : [15,30 ,50],	
						dataid:'id',
					});
}

