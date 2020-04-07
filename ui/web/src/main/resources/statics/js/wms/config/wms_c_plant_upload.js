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
				url:baseUrl + "config/CPlant/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['工厂代码','工厂名称','仓库号','启用供应商管理','启用智能储位','是否已上WMS','是否启用核销业务','是否启用最小包装','是否启用条码','是否启用预留','是否启用费用性订单库存管理','校验消息',''],
							colModel:[
						          {name:"werks",index:'werks',align:'center',width:80},
						          {name:"werksName",index:'werksName',align:'center',width:80},
						          {name:"whNumber",index:'whNumber',align:'center',width:80},
						          {name:"vendorFlag",index:'vendorFlag',align:'center',width:90},
						          {name:"igFlag",index:'igFlag',align:'center',width:90},
						          {name:"wmsFlag",index:'wmsFlag',align:'center',width:90},
						          {name:"hxFlag",index:'hxFlag',align:'center',width:90},
						          {name:"packageFlag",index:'packageFlag',align:'center',width:90},
						          {name:"barcodeFlag",index:'barcodeFlag',align:'center',width:90},
						          {name:"resbdFlag",index:'resbdFlag',align:'center',width:90},
						          {name:"cmmsFlag",index:'cmmsFlag',align:'center',width:90},
						          {name:"msg",index:'msg',align:'center',width:100},
						          {name:"rowNo",index:'rowNo',align:'center',hidden:true}
					        ],
					        width:1000
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
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/CPlant/import",
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