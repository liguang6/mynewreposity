var vm = new Vue({
	el:'#rrapp',
	data:{
		receiptno:'',
		itemno:''
	},
	methods: {
		getDetail : function(receiptno,itemno) {
			vm.receiptno=receiptno;
			vm.itemno=itemno;
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
					
					{ label: '条码号', name: 'LABEL_NO', index: 'LABEL_NO', width: 130, align:'center'}, 
					{ label: '料号', name: 'MATNR', index: 'MATNR', width: 100 , align:'center'},
					{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 270 , align:'center'},
					{ label: '装箱数量', name: 'BOX_QTY', index: 'BOX_QTY', width: 90 , align:'center'},
					{ label: '箱序', name: 'BOX_SN', index: 'BOX_SN', width: 80 , align:'center'},
					{ label: '满箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 90 , align:'center'}
		          ],
		rowNum: 15,
	    rownumWidth: 25,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true,  
	    multiselect:false
	});
}
