var excelData = null; 
var vm = new Vue({
    el:'#rrapp',
    data:{
    	saveFlag:true,
    	whNumber:"",
		warehourse:[],
    },   
    created:function(){
		loadWhNumber();
	},
	methods: {
		save:function(){
		   saveUpload();
		}
	}
});
$(function () {
	showTable();
});
$("#uploadForm").validate({
	submitHandler:function(e){
		showTable();
	}
});
function showTable(){
	//提交表单 & 加载数据
	var form = new FormData(document.getElementById("uploadForm"));
	//js.openLoading();
	$.ajax({
		url:baseUrl + "kn/inventory/previewExcel",
		data:form,
		type:"post",
		contentType:false,
		processData:false,
		success:function(response){
			if(response.code == 0){
				//加载jqgrid
				$("#dataGrid").jqGrid({
				 	datatype: 'local', 
					colNames:['盘点任务号','行项目','工厂','仓库号','物料号','物料描述','库位','仓管员','供应商代码','供应商名称','单位','账面数量','初盘数量','复盘数量','校验提示信息'],
					colModel:[
			    	  {name:"INVENTORY_NO",index:'INVENTORY_NO',align:'center',width:200},
			          {name:"INVENTORY_ITEM_NO",index:'INVENTORY_ITEM_NO',align:'center',width:100},
			          {name:"WERKS",index:'WERKS',align:'center',width:100},
			          {name:"WH_NUMBER",index:'WH_NUMBER',align:'center',width:100},
			          {name:"MATNR",index:'MATNR',align:'center',width:180},
			          {name:"MAKTX",index:'MAKTX',align:'center',width:200},
			          {name:"LGORT",index:'LGORT',align:'center',width:90},
			          {name:"WH_MANANGER",index:'WH_MANANGER',align:'center',width:120},
			          {name:"LIFNR",index:'LIFNR',align:'center',width:120},
			          {name:"LIKTX",index:'LIKTX',align:'center',width:120},
			          {name:"MEINS",index:'MEINS',align:'center',width:60},
			          {name:"STOCK_QTY",index:'STOCK_QTY',align:'center',width:100},
			          {name:"INVENTORY_QTY",index:'INVENTORY_QTY',align:'center',width:120},
			          {name:"INVENTORY_QTY_REPEAT",index:'INVENTORY_QTY_REPEAT',align:'center',width:120},
			          {name:"MSG",index:'MSG',align:'center',classes:"msg-font",width:200},  
			        ],
			        width:1590
				});
				//清空数据
				$("#dataGrid").dataGrid("clearGridData");
				//加入数据
				for(dIndex in response.data){
					$("#dataGrid").dataGrid("addRowData",dIndex+1,response.data[dIndex]);
				}
				excelData = response.data;
				console.log("excelData",excelData);
			}
			else
			  alert("上传失败," + data.msg);
		}.bind(this)
	});
}

function saveUpload(){
	 if(excelData === null){
		 alert("需要上传的数据为空");
		 vm.saveFlag=false;
		 return false;	
	 }
	 for(var dIndex in excelData){
		 if(excelData[dIndex].MSG!=''){
			 alert("第"+excelData[dIndex].rowNo+"行数据校验未通过!");
			 vm.saveFlag=false;
			 return false;
		 }
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "kn/inventory/import",
		 type:"post",
		 dataType:"json",
		 data:{
			 SAVE_DATA:jsondata,
			 TYPE:$('#type').val()
		 },
		 success:function(resp){
			 if(resp.code === 0){
			    vm.saveFlag= true;
				js.showMessage('保存成功');
			 }else{
				 alert("导入失败," + resp.msg)
			 }
		 }
	 })
}
function loadWhNumber(){
	//初始化查询仓库
	var plantCode = $("#werks").val();
	
    //初始化仓库
    $.ajax({
  	  url:baseUrl + "masterdata/dept/userdept",
  	  data:{"deptType":"7","parentDeptCode":plantCode},
  	  success:function(resp){
  		 vm.warehourse = resp;
  		 vm.whNumber=vm.warehourse[0].code;
  		
  	  }
    });
}