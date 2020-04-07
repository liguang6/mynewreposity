
$(function () {
    $("#dataGrid").dataGrid({
        url: baseUrl + 'masterdata/pmdMapQuery',
        searchForm: $("#searchForm"),
        datatype: "json",
        mtype: 'POST', 
        colModel: [			
			{ label: '图纸编号', name: 'MAP_NO', index: 'MAP_NO', align:"center",width: 120 }, 	
			{ label: '图纸名称', name: 'MAP_NAME', index: 'MAP_NAME', align:"center",width: 120 }, 
			{ label: '图幅', name: 'MAP_SHEET', index: 'MAP_SHEET',align:"center",sortable:false, width: 48 }, 
			{ label: '版本', name: 'VERSION', index: 'VERSION', align:"center",width: 48 }, 			
			{ label: '图纸存储地址', name: 'MAP_URL', index: 'MAP_URL',align:"center",sortable:false, width: 300 }, 			
			{ label: '图纸接收时间', name: 'EDIT_DATE', index: 'EDIT_DATE', width: 110 }, 			
			{ label: '状态', name: 'STATUS', index: 'STATUS',align:"center",sortable:false, width: 48 }, 		
			{ label: '发布人', name: 'PUBLISHER', index: 'PUBLISHER',align:"center",sortable:false, width: 90 }, 	
			{ label: '发布时间', name: 'PUBLISH_DATE', index: 'PUBLISH_DATE',align:"center",sortable:false, width: 110 }, 	
        ],
		viewrecords: true,
        rowNum: 15,
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,        
       
    });
    
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
	},
	watch:{
	},
	methods: {
		query: function () {
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "masterdata/pmdMapQuery");
			vm.reload();
		},
		reload: function (event) {
			js.loading();
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "masterdata/pmdMapQuery");
			vm.showList = true;
			var page = $("#dataGrid").jqGrid('getGridParam','page');
			var data = {
						MAP_NO:$("#MAP_NO").val(),
						MAP_NAME:$("#MAP_NAME").val(),
						start_date:$("#start_date").val(),
						end_date:$("#end_date").val(),
					};
			var strdata = JSON.stringify(data);
			$.ajax({
				url:baseUrl+"masterdata/pmdMapQuery",
				type:"post",
				data:data,
				success:function(result){
					//update dataGrid
					console.log(result);
					$("#dataGrid").jqGrid('setGridParam',{
						datatype:'json',
						data:result.page.list
					}).trigger("reloadGrid");
				}
			});
			js.closeLoading();
		},
		exp:function(){
			$("#searchForm").attr("id","exportForm");
			$("#exportForm").attr("action",baseUrl + "masterdata/pmdMapExport");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
		},
	},
	created:function(){
		
	}
});