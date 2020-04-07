var listdata = [];
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'cswlms/dispatchingBillPicking/listJISDetail',
    	datatype: "local",
		data: listdata,
		colModel: [
					
					{ label: '排序值', name: 'JIS_SEQUENCE', index: 'JIS_SEQUENCE', width: 120 }, 
					{ label: '料号', name: 'MATERIAL_CODE', index: 'MATERIAL_CODE', width: 90 },
					{ label: '物料描述', name: 'MATERIAL_DESC', index: 'MATERIAL_DESC', width: 250 },
					{ label: '批次', name: 'BATCH', index: 'BATCH', width: 150 },
					{ label: '供应商名称', name: 'VENDOR_NAME', index: 'VENDOR_NAME', width: 190 },
					{ label: '数量', name: 'QUANTITY', index: 'QUANTITY', width: 60 }
		        
		          ],
       
        viewrecords: true,
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: false
    });

});

var vm = new Vue({
	el:'#rrapp',
	data:{
		dispatching_no:""
	},
	methods: {
		getDetail : function(DISPATCHING_NO) {
			vm.dispatching_no=DISPATCHING_NO;
			this.$nextTick(function(){//渲染后再执行此函数
				initGridTable();
			});
				
		},
	}
});

function initGridTable(){
	$.ajax({
		url:baseURL+"/cswlms/dispatchingBillPicking/listJISDetail",
		dataType : "json",
		type : "post",
		data : {
			"DISPATCHING_NO":$("#DISPATCHING_NO").val(),
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				listdata = response.result;
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
			}else{
				alert(response.msg)
			}
		}
	});
}
