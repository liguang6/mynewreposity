var excelData = null; 
var vm = new Vue({
    el:'#rrapp',
    data:{
    	saveFlag:true,
    	list:''
    },   
});
$("#uploadForm").validate({
	  submitHandler:function(e){
//		if(!$("#WH_NUMBER").val()){
//			alert("请输入仓库号");
//			return false
//		}
		//提交表单 & 加载数据
		var form = new FormData(document.getElementById("uploadForm"));
		//js.openLoading();
		$.ajax({
			url:baseUrl + "kn/barcodeStorage/preview",
			data:form,
			type:"post",
			contentType:false,
			processData:false,
			success:function(data){
				if(data.code == 0){
					//加载jqgrid
					$("#dataGrid").jqGrid({
					 	datatype: 'local', 
						colNames:['','仓库号','储位代码','储位名称'],
						colModel:[
					    	  {name:"rowNo",index:'rowNo',align:'center',width:25},
					          {name:"WH_NUMBER",index:'WH_NUMBER',align:'center',width:80},
					          {name:"BIN_CODE",index:'BIN_CODE',align:'center',width:100},
					          {name:"BIN_NAME",index:'BIN_NAME',align:'center',width:140},
				        ],
				        width:960
					});
					//清空数据
					$("#dataGrid").dataGrid("clearGridData");
					//加入数据
					for(dIndex in data.data){
						$("#dataGrid").dataGrid("addRowData",dIndex+1,data.data[dIndex]);
					}
					excelData = data.data;
					//console.log("excelData",excelData);
				}
				else
				  alert("上传失败," + data.msg);
			}.bind(this)
		});
		//js.closeLoading();
	  }
});


function saveUpload(){
	 if(!excelData){
		 alert("需要上传的数据为空");
		 wm.saveFlag=false;
		 return false;	
	 }
//	 for(var dIndex in excelData){
//		 if(excelData[dIndex].msg!=''){
//			 alert("第"+excelData[dIndex].rowNo+"行数据校验未通过!");
//			 wm.saveFlag=false;
//			 return false;
//		 }
//	 }
	 //提交数据 & 关闭窗口
	 excelData.forEach(ele=>{
		 delete ele.rowNo
	 })
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "kn/barcodeStorage/import",
		 type:"post",
		 async:false,
		 dataType:"json",
		 contentType: 'application/json',
		 data:jsondata,
		 success:function(resp){
			 if(resp.flag === true){
				 vm.saveFlag= true;
				 vm.list = jsondata;console.log(vm.list)
				 js.showMessage('保存成功');
			 }else{
				 alert("导入失败")
			 }
		 }
	 })
}