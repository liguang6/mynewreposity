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
				url:baseUrl + "config/wharea/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['','仓库代码','存储类型代码','存储区名称','存储模式','入库规则','是否混储','是否库容检查','是否自动上架','是否自动补货','是否重量检查','楼层','坐标','状态','空储位搜索顺序','校验提示信息'],
							colModel:[
								  {name:"rowNo",index:'rowNo',align:'center',width:30},
						          {name:"whNumber",index:'whNumber',align:'center',width:80},
						          {name:"storageAreaCode",index:'storageAreaCode',align:'center',width:80},
						          {name:"areaName",index:'areaName',align:'center',width:90},
						          {name:"storageModel",index:'storageModel',align:'center',width:90},
						          {name:"putRule",index:'putRule',align:'center',width:90},
						          {name:"mixFlag",index:'mixFlag',align:'center',width:90},
						          {name:"storageCapacityFlag",index:'storageCapacityFlag',align:'center',width:90},
						          {name:"autoPutawayFlag",index:'autoPutawayFlag',align:'center',width:90},
						          {name:"autoReplFlag",index:'autoReplFlag',align:'center',width:90},
						          {name:"checkWeightFlag",index:'checkWeightFlag',align:'center',width:90},
						          {name:"floor",index:'floor',align:'center',width:90},
						          {name:"coordinate",index:'coordinate',align:'center',width:90},
						          {name:"status",index:'status',align:'center',width:90},
						          {name:"binSearchSequence",index:'binSearchSequence',align:'center',width:90},
						          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:180},
					        ],
					        width:890
						});
						//清空数据
						$("#dataGrid").dataGrid("clearGridData");
						var whNumber=$("#whNumber").val();
						//加入数据
						for(dIndex in data.data){
							if(data.data[dIndex].whNumber!=whNumber){
								data.data[dIndex].msg='请导入'+whNumber+'的数据';
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
		 url:baseUrl + "config/wharea/import",
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