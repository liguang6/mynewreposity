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
		//js.openLoading();
		$.ajax({
			url:baseUrl + "config/matdanger/preview",
			data:form,
			type:"post",
			contentType:false,
			processData:false,
			success:function(data){
				if(data.code == 0){
					//加载jqgrid
					$("#dataGrid").jqGrid({
					 	datatype: 'local', 
						colNames:['','工厂代码','供应商代码','物料号','物料描述','是否危化品','保质期时长','是否允许延长有效期','备注','校验提示信息'],
						colModel:[
					    	  {name:"rowNo",index:'rowNo',align:'center',width:25},
					          {name:"werks",index:'werks',align:'center',width:80},
					          {name:"lifnr",index:'lifnr',align:'center',width:100},
					          {name:"matnr",index:'matnr',align:'center',width:140},
					          {name:"maktx",index:'maktx',align:'center',width:180},
					          {name:"dangerFlag",index:'dangerFlag',align:'center',width:90},
					          {name:"goodDates",index:'goodDates',align:'center',width:90},
					          {name:"extendedEffectDate",index:'extendedEffectDate',align:'center',width:120},
					          {name:"memo",index:'memo',align:'center',width:120},
					          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:180},
							  
				        ],
				        width:960
					});
					//清空数据
					$("#dataGrid").dataGrid("clearGridData");
					//加入数据
					var werks=$('#werks').val();
					for(dIndex in data.data){
						if(data.data[dIndex].werks=werks){
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
		//js.closeLoading();
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
		 url:baseUrl + "config/matdanger/import",
		 type:"post",
		 dataType:"json",
		 contentType: 'application/json',
		 data:jsondata,
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