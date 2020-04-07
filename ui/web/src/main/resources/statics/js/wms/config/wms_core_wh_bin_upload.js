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
				url:baseUrl + "config/corewhbin/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						console.log("data.data",data.data);
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['','仓库代码','存储区代码','储位代码','储位名称','储位类型','排','列','层','容积','容积单位','承重','承重单位','容积使用',
								'重量使用','周转率','可容存储单元','占用存储单元','X轴','Y轴','Z轴','储位状态','校验提示信息'],
							colModel:[
								  {name:"rowNo",index:'rowNo',align:'center',width:30},
						          {name:"whNumber",index:'whNumber',align:'center',width:80},
						          {name:"storageAreaCode",index:'storageAreaCode',align:'center',width:120},
						          {name: "binCode", index: 'binCode', width: 80,align:'center'  },
							      {name: 'binName', index: 'binName', width: 80,align:'center'  },
							      {name: 'binType', index: 'binType', width: 80,align:'center'  },
							      {name: 'binRow', index: 'binRow', width: 60,align:'center'  },
							      {name: 'binColumn', index: 'binColumn', width: 60,align:'center'  },
							      {name: 'binFloor', index: 'binFloor', width: 60,align:'center'  },
							      {name: 'vl', index: 'vl', width: 80,align:'center'  },
							      {name: 'vlUnit', index: 'vlUnit', width: 80,align:'center'  },
							      {name: 'wt', index: 'wt', width: 80,align:'center'  },
							      {name: 'wtUnit', index: 'wtUnit', width: 80,align:'center'  },
							      {name: 'vlUse', index: 'vlUse', width: 80,align:'center'  },
							      {name: 'wtUse', index: 'wtUse', width: 80,align:'center'  },
							      {name: 'turnoverRate', index: 'turnoverRate', width: 80,align:'center'  },
							      {name: 'aStorageUnit', index: 'aStorageUnit', width: 80,align:'center'  },
							      {name: 'uStorageUnit', index: 'uStorageUnit', width: 80,align:'center'  },
							      {name: 'x', index: 'x', width: 80,align:'center'  },
							      {name: 'y', index: 'y', width: 80,align:'center'  },
							      {name: 'z', index: 'z', width: 80,align:'center'  },
							      {name: 'binStatus', index: 'binStatus', width: 80,align:'center'  },
						          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:180},
					        ],
					        width:890
						});
						//清空数据
						$("#dataGrid").dataGrid("clearGridData");
						//加入数据
						for(dIndex in data.data){
							var whNumber=$("#whNumber").val();
							if(whNumber!=data.data[dIndex].whNumber){
                               data.data[dIndex].msg=data.data[dIndex].whNumber+"导入仓库号与所选仓库号不符";
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
		 var binType=excelData[dIndex].binType.trim();
		 if(binType!=''){
			 if(binType=='虚拟储位'){excelData[dIndex].binType='00';}
			 if(binType=='进仓位'){excelData[dIndex].binType='01';}
		     if(binType=='存储位'){excelData[dIndex].binType='02';}
		     if(binType=='拣配位'){excelData[dIndex].binType='03';}
		     if(binType=='存储位'){excelData[dIndex].binType='04';}
		     if(binType=='立库位'){excelData[dIndex].binType='05';}
		 }
		 var binStatus=excelData[dIndex].binStatus.trim();
		 if(binStatus!=''){
			 if(binStatus=='未启用'){excelData[dIndex].binStatus='00';}
		     if(binStatus=='可用'){excelData[dIndex].binStatus='01';}
		     if(binStatus=='不可用'){excelData[dIndex].binStatus='02';}
		     if(binStatus=='锁定'){excelData[dIndex].binStatus='03';}
		 }
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/corewhbin/import",
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