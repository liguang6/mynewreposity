var excelData = null; 
var vm = new Vue({
    el:'#rrapp',
    data:{
    	saveFlag:true
    },   
});
$("#uploadForm").validate({
	  submitHandler:function(e){
		  //提交表单 & 加载数据
		  var form = new FormData(document.getElementById("uploadForm"));
			$.ajax({
				url:baseUrl + "config/CVendor/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['工厂代码','工厂名称','供应商代码','供应商名称','供应商简称','供应商管理者','是否已上SCM','校验消息',''],
							colModel:[
						          {name:"werks",index:'werks',align:'center',width:80},
						          {name:"werksName",index:'werksName',align:'center',width:80},
						          {name:"lifnr",index:'lifnr',align:'center',width:90},
						          {name:"name1",index:'name1',align:'center',width:90},
						          {name:"shortName",index:'shortName',align:'center',width:90},
						          {name:"vendorManager",index:'vendorManager',align:'center',width:90},
						          {name:"isScm",index:'isScm',align:'center',width:90},
						          {name:"msg",index:'msg',align:'center',width:100},
						          {name:"rowNo",index:'rowNo',align:'center',hidden:true}
					        ],
					        width:890
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
         if(excelData[dIndex].isScm=='是'){
             excelData[dIndex].isScm='X';
         }
         if(excelData[dIndex].isScm=='否'){
             excelData[dIndex].isScm='0';
         }
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/CVendor/import",
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