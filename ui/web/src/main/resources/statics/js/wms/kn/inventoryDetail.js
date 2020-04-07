
var vm = new Vue({
	el:'#rrapp',
	data:{
		inventoryNo:'',
		werks:'',
		whNumber:''
	},
	methods: {
		getDetail : function(inventoryNo,werks,whNumber,status) {
			vm.inventoryNo=inventoryNo;
			vm.werks=werks;
			vm.whNumber=whNumber;
			vm.status=status;
			$.ajax({
				url:baseURL+"kn/inventory/getItemByInventoryNo",
				dataType : "json",
				type : "post",
				data : {
					INVENTORY_NO:inventoryNo,
				},
				//async: false,
				success: function (response) { 
				    var results=response.list; 
				    showTable(results);
				}
			})
		},
	}
});

function showTable(data){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: data,
        colModel: [		
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "150",align:"center",sortable:false,frozen: true},
            {label: '库位号', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:false},
            {label: '仓管员', name: 'WH_MANAGER',index:"WH_MANAGER", width: "80",align:"center",sortable:false},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "80",align:"center",sortable:false},
            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "120",align:"center",sortable:false},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false},            
            {label: '库存数量', name: 'STOCK_QTY',index:"STOCK_QTY", width: "70",align:"center",sortable:false,},
            {label: '初盘数量', name: 'INVENTORY_QTY',index:"INVENTORY_QTY", width: "70",align:"center",sortable:false,},
            {label: '复盘数量', name: 'INVENTORY_QTY_REPEAT',index:"INVENTORY_QTY_REPEAT", width: "70",align:"center",sortable:false,},
            {label: '差异原因', name: 'DIFFERENCE_REASON',index:"DIFFERENCE_REASON", width: "120",align:"center",sortable:false,},
            {label: '初盘人', name: 'INVENTORY_PEOPLE',index:"INVENTORY_PEOPLE", width: "60",align:"center",sortable:false,},
            {label: '初盘时间', name: 'INVENTORY_DATE',index:"INVENTORY_DATE", width: "60",align:"center",sortable:false,},
            {label: '复盘人', name: 'INVENTORY_PEOPLE_REPEAT',index:"INVENTORY_PEOPLE_REPEAT", width: "60",align:"center",sortable:false,},
            {label: '复盘时间', name: 'INVENTORY_DATE_REPEAT',index:"INVENTORY_DATE_REPEAT", width: "60",align:"center",sortable:false,},
            {label: '确认人', name: 'CONFIRMOR',index:"CONFIRMOR", width: "60",align:"center",sortable:false,},
            {label: '确认时间', name: 'CONFIRM_DATE',index:"CONFIRM_DATE", width: "60",align:"center",sortable:false,},
        ],
		shrinkToFit: false,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}
