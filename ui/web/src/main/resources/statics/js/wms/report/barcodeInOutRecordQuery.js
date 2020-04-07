
var vm = new Vue({
	el:'#rrapp',
	data:{
		wms_no:'',
		wms_item_no:'',
		werks:'',
		wh_number:''
	},
	methods: {
		getBarDetail : function(wms_no,wms_item_no,werks,wh_number) {
			console.log(wms_no+" "+wms_item_no+" "+werks+" "+wh_number);
			vm.wms_no=wms_no;
			vm.wms_item_no=wms_item_no;
			vm.werks=werks;
			vm.wh_number=wh_number;
			this.$nextTick(function(){//渲染后再执行此函数
				initGridTable();
			});

		},
	}
});

function initGridTable(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel:[
			{label:'条码标签号', name:'LABEL_NO', width:120, align:"center"},
			{label:'数量', name:'QTY', width:60, align:"center",sortable:false},
			{label:'操作人', name:'CREATOR', width:80, align:"center",sortable:false},
			{label:'操作时间', name:'CREATE_DATE', width:90, align:"center"},
			{label:'料号', name:'MATNR', width:90, align:"center"},
			{label:'物料描述', name:'MAKTX', width:160, align:"center",sortable:false},
			{label:'WMS移动类型', name:'WMS_MOVE_TYPE', width:120, align:"center"},
			{label:'业务单据号', name:'BUSINESS_NO', width:120, align:"center"},
			{label:'业务单据号行项目', name:"BUSINESS_ITEM_NO", width:200, align:"center"}
		],
		rowNum: 15,
		rownumWidth: 25,
		autowidth:true,
		shrinkToFit:false,
		autoScroll: true,
		multiselect:true,
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			var $myGrid = $(this),
				i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
				cm = $myGrid.jqGrid('getGridParam', 'colModel');
			/*if(cm[i].name == 'cb'){
                console.info(">>>");
                $('#dataGrid').jqGrid('setSelection',rowid);
            }*/

			return (cm[i].name == 'cb');
		}
	});
}
