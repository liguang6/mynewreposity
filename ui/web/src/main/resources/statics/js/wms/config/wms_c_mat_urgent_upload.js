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
				url:baseUrl + "config/maturgent/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['','工厂代码','物料号','物料描述','紧急物料标示','有效开始日期','有效结束日期','备注','校验提示信息'],
							colModel:[
						    	  {name:"rowNo",index:'rowNo',align:'center',width:40},
						          {name:"werks",index:'werks',align:'center',width:80},
						          {name:"matnr",index:'matnr',align:'center',width:110},
						          {name:"maktx",index:'maktx',align:'center',width:180},
						          {name:"urgentFlag",index:'urgentFlag',align:'center',width:100},
						          {name:"startDate",index:'startDate',align:'center',width:100},
						          {name:"endDate",index:'endDate',align:'center',width:100},
						          {name:"memo",index:'memo',align:'center',width:100},
						          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:150},
								  
					        ],
					        width:1190
						});
						//清空数据
						$("#dataGrid").dataGrid("clearGridData");
						var werks=$("#werks").val();
						//加入数据
						for(dIndex in data.data){
							if(data.data[dIndex].werks!=werks){
								data.data[dIndex].msg='请导入'+werks+'工厂数据';
							}
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
		 url:baseUrl + "config/maturgent/import",
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