//$(function () {
//	var asn = getQueryVariable("asnno");
//	vm.getDetail(asn);
//	
//});

var vm = new Vue({
	el:'#rrapp',
	data:{
		asnno:''
	},
	methods: {
		getDetail : function(asnno) {
			vm.asnno=asnno;
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
					
					{ label: '采购订单', name: 'PONO', index: 'PONO', width: 120 }, 
					{ label: '行项目号', name: 'POITM', index: 'POITM', width: 70 },
					{ label: '工厂', name: 'PLCD', index: 'PLCD', width: 50 },
					{ label: '送达仓库', name: 'WHCODE', index: 'WHCODE', width: 70 },
					{ label: '料号', name: 'MACD', index: 'MACD', width: 90 },
					{ label: '物料描述', name: 'MADS', index: 'MADS', width: 170 },
					{ label: '供应商', name: 'VDCD', index: 'VDCD', width: 90 },
					{ label: '供应商名称', name: 'VDNM', index: 'VDNM', width: 190 },
					{ label: '数量', name: 'QTY', index: 'QTY', width: 70 },
					{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60 },
					{ label: '箱数', name: 'BXCNT', index: 'BXCNT', width: 60 },
					{ label: '已收货数量', name: 'HAS_RECEIPT_QTY', index: 'HAS_RECEIPT_QTY', width: 100 },
					{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 100 }
		        
		          ],
		rowNum: 15,
	    rownumWidth: 25,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true,  
	    multiselect:false
	});
}
