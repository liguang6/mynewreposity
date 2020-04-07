
var vm = new Vue({
	el:'#app-vue',
	data:{
		saveFlag:false,
		showList:false
	},
	methods: {
		
		showEditTable: function (batchSelectArr) {
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				cellsubmit:'clientArray',
				columnModel: [
					{header: '零部件编码', name: 'parts_no', width: "110",align:"center",sortable:false,}, 	
		            {header: '零部件名称', name: 'parts_name',index:"testItem", width: "150",align:"center",sortable:false,}, 
		          //  {header: '材料/规格', name: 'size',width: "120",align:"center",sortable:false,}, 	
		            {header: '供应商',name:'vendor', width: "160",align:"center",sortable:false,},
		          //  {header: '工序代码', name: 'process_code', width: "70",align:"center",sortable:false,}, 	
		            {header: '工序名称', name: 'process_name', width: "120",align:"center",sortable:false,}, 
		            {header: '批次', name: 'batch', width: "150",align:"center",sortable:false,}, 	
		            {header: '物料流水编号',name:'material_no', width: "180",align:"center",sortable:false,},
		        ],
				gridComplete :function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				viewrecords: true,
		        rownumbers: false, 
		        rownumWidth: 25, 
		        autowidth:true,
			});
		},
		showTable:function (){
			$('#dataGrid').dataGrid({
			searchForm: $("#searchForm"),
			cellurl:'#',
			cellsubmit:'clientArray',
			columnModel: [
				{header: '零部件编码', name: 'parts_no', width: "120",align:"center",sortable:false,}, 	
	            {header: '零部件名称', name: 'parts_name',index:"testItem", width: "180",align:"center",sortable:false,}, 
	            {header: '材料/规格', name: 'size',width: "120",align:"center",sortable:false,}, 	
	            {header: '供应商',name:'vendor', width: "80",align:"center",sortable:false,},
//	            {header: '工序代码', name: 'process_code', width: "90",align:"center",sortable:false,}, 	
	            {header: '工序名称', name: 'process', width: "120",align:"center",sortable:false,}, 
				{header:"备注",name:"3C_components",width: "80",align:"center",sortable:false,},					
	            {header: '追溯信息', name: 'batch', width: "220",align:"center",sortable:false,}, 	
//	            {header: '物料流水编号',name:'material_no', width: "180",align:"center",sortable:false,},
			],
			gridComplete :function(){
				js.closeLoading();
				$(".btn").attr("disabled", false);
			},		
			viewrecords: true,
	        rownumbers: true, 
	        rownumWidth: 25, 
	        autowidth:true, 
		})
	  },
	}
});
$(function(){
	$("#tplDetailId").val(getUrlKey("tplDetailId"));
	$("#lineName").val(getUrlKey("lineName"));
	
	$("#testType").val(getUrlKey("testType"));
	var testType=getUrlKey("testType");
	if(testType=='01'){
		vm.showList=false;
		$("#bmsorder").val(getUrlKey("order"));
		$("#bmsFactoryName").val(getUrlKey("factoryName"));
		$("#busNumber").val(getUrlKey("bus_number"));
		$("#workshop").val(getUrlKey("workshop"));
		$("#bmsOrderConfigName").val(getUrlKey("orderConfigName"));
		$("#key_components_template_id").val(getUrlKey("key_components_template_id"));
		vm.showTable();
	}
	if(testType=='02'){
		$("#vmesorder").val(getUrlKey("order"));
		$("#vmesFactoryName").val(getUrlKey("factoryName"));
		$("#vmesOrderConfigName").val(getUrlKey("orderConfigName"));
		$("#vin").val(getUrlKey("vin"));
		vm.showList=true;
		vm.showEditTable();
	}
	
});	

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}