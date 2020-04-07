var excelData = null; 
var vm = new Vue({
    el:'#rrapp',
    data:{
    	saveFlag:true
    },   
});
$("#uploadForm").validate({
	  submitHandler:function(e){
		  js.loading();
		  //提交表单 & 加载数据
		  var form = new FormData(document.getElementById("uploadForm"));
			$.ajax({
				url:baseUrl + "config/plantbusiness/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['','工厂代码','业务类型代码','WMS业务类型分类','WMS业务类型','WMS业务类型名称','特殊库存标示','SAP同步标示','校验提示信息'],
							colModel:[
					    	  {name:"rowNo",index:'rowNo',align:'center',width:40},
					          {name:"werks",index:'werks',align:'center',width:90},
					          {name:"businessCode",index:'businessCode',align:'center',width:110},
					          {name:"businessClass",index:'businessClass',align:'center',width:110},
					          {name:"businessType",index:'businessType',align:'center',width:110},
					          {name:"businessName",index:'businessName',align:'center',width:110},
					          {name:"sobkz",index:'sobkz',align:'center',width:110},
					          {name:"synFlag",index:'synFlag',align:'center',width:100},
					          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:140},	  
					        ],
					        width:1190
						});
						//清空数据
						$("#dataGrid").dataGrid("clearGridData");
						//加入数据
						for(dIndex in data.data){
							$("#dataGrid").dataGrid("addRowData",dIndex+1,data.data[dIndex]);
						}
						excelData = data.data;
						console.log("excelData",excelData);
					}
					else
					  alert("上传失败," + data.msg);
				}.bind(this)
			});
			js.closeLoading();
	  }
});


function saveUpload(){
	 if(excelData === null){
		 alert("需要上传的数据为空");
		 wm.saveFlag=false;
		 return false;	
	 }
	 for(var dIndex in excelData){
		 if(excelData[dIndex].msg!=''){
			 alert("第"+excelData[dIndex].rowNo+"行数据校验未通过!");
			 wm.saveFlag=false;
			 return false;
		 }
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/plantbusiness/import",
		 type:"post",
		 dataType:"json",
		 contentType: 'application/json',
		 data:jsondata,
		 success:function(resp){
			 if(resp.code === 0){
				 close();
				 js.showMessage('保存成功');
			 }else{
				 alert("上传失败," + resp.msg)
			 }
		 }
	 })
}