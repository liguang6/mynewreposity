
var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS:"",
		whNumber:"",
		warehourse:[]
	},
	created:function(){
		this.WERKS = $("#werks").find("option").first().val();
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新库存下拉框
		          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })			
			}
		}
	},
	methods: {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		reload: function (event) {
			$("#searchForm").submit();
		},
		
		add:function(){
        	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px;color:blue">创建移储</i>', 'wms/kn/storageMoveCreate.html', true, true);
		}
	}
});
$(function () {
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', width:60, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:60, align:"center"},
			{header:'库位', name:'LGORT', width:70, align:"center"},
			{header:'料号', name:'MATNR', width:100, align:"center"},
			{header:'物料描述', name:'MAKTX', width:150, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'单位', name:'MEINS', width:60, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:90, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:140, align:"center"},
			{header:'特殊库存类型', name:'SOBKZ', width:90, align:"center"},
			{header:'数量', name:'MOVE_QTY', width:60, align:"center"},
			{header:'源储位', name:'F_BIN_CODE', width:70, align:"center"},
			{header:'目标储位', name:'BIN_CODE', width:70, align:"center"},
			{header:'创建人', name:'EDITOR', width:70, align:"center"},
			{header:'创建时间', name:'EDIT_DATE', width:100, align:"center"},
		],	
		rowNum: 15,  
	});
});