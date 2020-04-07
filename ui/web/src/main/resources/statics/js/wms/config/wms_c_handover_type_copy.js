var lastrow,lastcell;
var data={};
var vm = new Vue({
	el:'#rrapp',
	data:{
		saveFlag:false,
    	werks:"",
    	targetWerks:"",
    	whNumber: "",
    	targetWhNumber:"",
    	s_warehourse:[],
    	t_warehourse:[],
    },
    created: function(){
    	this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks:{
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新库存下拉框
		          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal},
		        	  success:function(resp){
		        		 vm.s_warehourse = resp.data;
		        		 vm.whNumber = resp.data[0].WH_NUMBER;
		        		 var mydata=getSourceData();
		        		 showData(mydata);
		        	  }
		          });			
			}
		},
		whNumber:{
			handler:function(newVal,oldVal){	
				var mydata=getSourceData();
       		    showData(mydata);
			}
		},
		targetWerks:{
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新库存下拉框
		          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal},
		        	  success:function(resp){
		        		 vm.t_warehourse = resp.data;
		        		 //vm.targetWhNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })			
			}
		}
	},
	methods: {
		 save: function () {
			if($("#werks").val()==''){
			   alert("请选择源工厂");
			   return false;
			}
			if($("#targetWerks").val()==''){
			   alert("请选择目标工厂");
			   return false;
			}
			if($("#werks").val()!='' && $("#targetWerks").val()!=''){
				if($("#werks").val()==$("#targetWerks").val()){
					alert("目标工厂不能与源工厂相同");
					return false;
				}
			}
			var save_flag=true;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var saveData=[];
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	

			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);

				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
				if(saveData[i]["WH_NUMBER"]!=$("#targetWhNumber").val()){
					alert("请选择目标仓库！");
					return false;
				}
			}
			var url = baseURL+"config/handoverType/copy";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType: "json",
			    async:false,
				data : {
					saveData:JSON.stringify(saveData)
				},
			    success: function(r){
			    	if(r.code === 0){
			        	vm.saveFlag= true;
			        	js.showMessage(r.msg+"：保存成功！");
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
//		whChange:function(){
//			//showData();
//		},
//		query: function () {
//			$("#dataGrid").jqGrid('GridUnload');
//			jQuery("#dataGrid").trigger("reloadGrid"); 
//		   showData();
//		},
	}
});
$(function(){
	showData([]);
});
function showData (mydata) {
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
        	{ label: '工厂代码', name: 'WERKS', index: 'werks', width: 100,align:'center' },
			{ label: '仓库号', name: 'WH_NUMBER', index: 'whNumber', width: 100,align:'center' },
			{ label: '业务类型名称', name: 'BUSINESS_NAME_DESC', index: 'businessName', width: 220,align:'center'  }, 			
			{ label: '交接模式', name: 'HANDOVER_TYPE_DESC', index: 'handoverTypeDesc', width: 180,align:'center'  },
			{ label: 'BUSINESS_NAME', name: 'BUSINESS_NAME', index: 'businessName', hidden:true },
			{ label: 'HANDOVER_TYPE', name: 'HANDOVER_TYPE', index: 'handoverType', hidden:true },
        ],
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){

		},
		shrinkToFit: false,
		showCheckbox:true,
		viewrecords: true,
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
    });

}
function getSourceData() {
	var mydata=[];
	$.ajax({
		type: "POST",
	    url: baseURL+"config/handoverType/list",
	    dataType : "json",
		data : {
			werks:vm.werks,
			whNumber:vm.whNumber
		},
		async:false,
	    success: function(r){
	    	if(r.code == 0){
	    		mydata=r.page.list;
			}
		}
	});
	return mydata;
}
function onPlantChange() {
	var target=$("#targetWerks").val();
	var ids = $("#dataGrid").jqGrid("getDataIDs");
	for (var i = 0; i < ids.length; i++) {
		$("#dataGrid").jqGrid('setCell', ids[i], "WERKS", target,'not-editable-cell');
	}
}
function onWhNumberChange() {
	var target=$("#targetWhNumber").val();
	var ids = $("#dataGrid").jqGrid("getDataIDs");
	for (var i = 0; i < ids.length; i++) {
		$("#dataGrid").jqGrid('setCell', ids[i], "WH_NUMBER", target,'not-editable-cell');
	}
}