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
				url:baseUrl + "config/matstorage/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['','工厂代码','仓库代码','物料号','物料描述','入库控制标识','出库控制标识','长','宽','高','长宽高单位','体积','体积单位','重量','重量单位','最小包装单位','单存储单元数量','最大库存','最小库存','是否启用最小包装','是否启用外部批次','相关联物料','排斥物料','校验提示信息'],
							colModel:[
								  {name:"rowNo",index:'rowNo',align:'center',width:30},
								  {name:"werks",index:'werks',align:'center',width:80},
						          {name:"whNumber",index:'whNumber',align:'center',width:80},
						          {name:"matnr",index:'matnr',align:'center',width:80},
						          {name:"maktx",index:'maktx',align:'center',width:150},
							      {name: 'inControlFlag', index: 'inControlFlag', width: 80,align:'center'  },
							      {name: 'outControlFlag', index: 'outControlFlag', width: 60,align:'center'  },
							      {name: 'length', index: 'length', width: 80,align:'center'  },
							      {name: 'width', index: 'width', width: 80,align:'center'  },
							      {name:"height",index:'height',align:'center',width:80},
							      {name:"sizeUnit",index:'sizeUnit',align:'center',width:80},
							      {name: 'volum', index: 'volum', width: 50,align:'center'  },
							      {name: 'volumUnit', index: 'volumUnit', width: 50,align:'center'  },
							      {name: 'weight', index: 'weight', width: 50,align:'center'  },
							      {name:"weightUnit",index:'weightUnit',align:'center',width:80},
							      {name: 'storageUnit', index: 'storageUnit', width: 80,align:'center'  },
							      {name: 'qty', index: 'qty', width: 80,align:'center'  },
							      {name: 'stockL', index: 'stockL', width: 80,align:'center'  },
							      {name: 'stockM', index: 'stockM', width: 80,align:'center'  },
							      {name: 'mpqFlag', index: 'mpqFlag', width: 80,align:'center'  },
							      {name: 'externalBatchFlag', index: 'externalBatchFlag', width: 80,align:'center'  },
							      {name: 'correlationMaterial', index: 'correlationMaterial', width: 80,align:'center'  },
							      {name: 'repulsiveMaterial', index: 'repulsiveMaterial', width: 80,align:'center'  },
						          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:180}
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
		 
		 var mpqFlag=excelData[dIndex].mpqFlag;
		 if(mpqFlag!=''){
			 if(mpqFlag=='否'){excelData[dIndex].mpqFlag='0';}
		     if(mpqFlag=='是'){excelData[dIndex].mpqFlag='X';}
		 }
		 
		 var externalBatchFlag=excelData[dIndex].externalBatchFlag;
		 if(externalBatchFlag!=''){
			 if(externalBatchFlag=='否'){excelData[dIndex].externalBatchFlag='0';}
		     if(externalBatchFlag=='是'){excelData[dIndex].externalBatchFlag='X';}
		 }
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/matstorage/upload",
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