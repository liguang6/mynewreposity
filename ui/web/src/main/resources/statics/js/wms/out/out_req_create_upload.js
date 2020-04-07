var excelData = null;
var vm = new Vue({
    el:'#rrapp',
    data:{
		innerOrder:'',
		innerOrder:'', // 内部、CO订单类型
        werks:'',
        whNumber:'',
        requireTypes:'',
    	saveFlag:true,
		aceptLgortList:'',
		acceptPlant:''
    } 
});
$("#uploadForm").validate({
	  submitHandler:function(e){
	      //console.info('werks===================='+vm.werks);
		  //提交表单 & 加载数据
		  var form = new FormData(document.getElementById("uploadForm"));
          form.append("WEARKS", vm.werks);
          form.append("requireTypes", vm.requireTypes);
          form.append("innerOrder", vm.innerOrder);
          form.append("autyp", vm.innerOrder_AUTYP);
          var url = '';
          if(vm.requireTypes==='41'|| vm.requireTypes==='42'){
			  url = baseUrl + "out/createRequirement/preview";
		  }else if(vm.requireTypes==='43'){
			  url = baseUrl + "out/innerorder/preview";
		  }else if(vm.requireTypes==='47'|| vm.requireTypes==='48'){
			  url = baseUrl + "out/producelinewarehouse/preview";
		  }else if(vm.requireTypes==='46'){
			  url = baseUrl + "out/outreq/preview";
		  }

          js.loading();
          console.info(form);
			$.ajax({
				url:url,
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){

						if(vm.requireTypes==='41'|| vm.requireTypes==='42'){
							//加载jqgrid
							$("#dataGrid").jqGrid({
								datatype: 'local',
								colNames:['','生产订单号','订单行项目号','料号','物料描述','单位','需求数量','供应商代码','库位','产线','工位',''
								],
								colModel:[
									{name:"rowNo",index:'rowNo',align:'center',width:30},
									{name:"AUFNR",index:'AUFNR',align:'center',width:80},
									{name:"POSNR",index:'POSNR',align:'center',width:80},
									{name:"MATNR",index:'MATNR',align:'center',width:80},
									{name:"MAKTX",index:'MAKTX',align:'center',width:120},
									{name: "MEINS", index: 'MEINS', width: 80,align:'center'  },
									{name: 'REQ_QTY', index: 'REQ_QTY', width: 80,align:'center'  },
									{name: 'VENDOR', index: 'VENDOR', width: 80,align:'center' },
									{name: 'LGORT', index: 'LGORT', width: 60,align:'center' },
									{name: 'LINE', index: 'LINE', width: 60,align:'center' },
									{name: 'STATION', index: 'STATION', width: 60,align:'center' },
									{name: 'WERKS', index: 'WERKS', width: 60,align:'center',hidden:true},
								],
								width:890
							});
						}else if(vm.requireTypes==='43'){
							//加载jqgrid
							$("#dataGrid").jqGrid({
								datatype: 'local',
								colNames:['','料号','物料描述','单位','需求数量','库位','供应商代码','工位','备注','',''
								],
								colModel:[
									{name:"rowNo",index:'rowNo',align:'center',width:30},
									{name:"MATNR",index:'MATNR',align:'center',width:80},
									{name:"MAKTX",index:'MAKTX',align:'center',width:120},
									{name: "MEINS", index: 'MEINS', width: 80,align:'center'  },
									{name: 'REQ_QTY', index: 'REQ_QTY', width: 80,align:'center'  },
									{name: 'LGORT', index: 'LGORT', width: 60,align:'center' },
									{name: 'VENDOR', index: 'VENDOR', width: 80,align:'center' },
									{name: 'STATION', index: 'STATION', width: 60,align:'center' },
									{name: 'MENO', index: 'MENO', width: 60,align:'center' },
									{name: 'WERKS', index: 'WERKS', width: 60,align:'center',hidden:true},
									{name: 'innerOrder', index: 'innerOrder', width: 60,align:'center',hidden:true},
									{name: 'innerOrder_AUTYP', index: 'innerOrder_AUTYP', width: 60,align:'center',hidden:true},
								],
								width:890
							});
						}else if(vm.requireTypes==='47' || vm.requireTypes==='48'){
							//加载jqgrid
							$("#dataGrid").jqGrid({
								datatype: 'local',
								colNames:['','生产订单号','订单行项目号','料号','物料描述','单位','需求数量','供应商代码','库位','产线','工位','接收库位','',
								],
								colModel:[
									{name:"rowNo",index:'rowNo',align:'center',width:30},
									{name:"AUFNR",index:'AUFNR',align:'center',width:80},
									{name:"POSNR",index:'POSNR',align:'center',width:80},
									{name:"MATNR",index:'MATNR',align:'center',width:80},
									{name:"MAKTX",index:'MAKTX',align:'center',width:120},
									{name: "MEINS", index: 'MEINS', width: 80,align:'center'  },
									{name: 'REQ_QTY', index: 'REQ_QTY', width: 80,align:'center'  },
									{name: 'VENDOR', index: 'VENDOR', width: 80,align:'center' },
									{name: 'LGORT', index: 'LGORT', width: 60,align:'center' },
									{name: 'LINE', index: 'LINE', width: 60,align:'center' },
									{name: 'STATION', index: 'STATION', width: 60,align:'center' },
									{name: 'aceptLgortList', index: 'aceptLgortList', width: 60,align:'center'},//fixbug1512
									{name: 'WERKS', index: 'WERKS', width: 60,align:'center',hidden:true},
								],
								width:890
							});
						}else if(vm.requireTypes==='46'){
							//加载jqgrid
							$("#dataGrid").jqGrid({
								datatype: 'local',
								colNames:['','料号','物料描述','单位','需求数量','库位','供应商代码','产线','预计箱数','接收工厂','接收库位',''
								],
								colModel:[
									{name:"rowNo",index:'rowNo',align:'center',width:30},
									{name:"MATNR",index:'MATNR',align:'center',width:80},
									{name:"MAKTX",index:'MAKTX',align:'center',width:120},
									{name: "MEINS", index: 'MEINS', width: 80,align:'center'  },
									{name: 'REQ_QTY', index: 'REQ_QTY', width: 80,align:'center'  },
									{name: 'LGORT', index: 'LGORT', width: 80,align:'center'  },
									{name: 'VENDOR', index: 'VENDOR', width: 80,align:'center' },
									{name: 'LINE', index: 'LINE', width: 60,align:'center' },
									// {name: 'MENO', index: 'MENO', width: 60,align:'center' },
									{name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,align:'center' },
									{name: 'acceptPlant', index: 'acceptPlant', width: 60,align:'center' },
									{name: 'aceptLgortList', index: 'aceptLgortList', width: 60,align:'center' },
									{name: 'WERKS', index: 'WERKS', width: 60,align:'center',hidden:true},
								],
								width:890
							});
						}


						//清空数据
						$("#dataGrid").dataGrid("clearGridData");
						//加入数据
						for(dIndex in data.data){
							$("#dataGrid").dataGrid("addRowData",dIndex+1,data.data[dIndex]);
						}
						excelData = data.data;
						js.closeLoading();
					}
					else
					  alert("上传失败," + data.msg);
					  js.closeLoading();
				}.bind(this)
			});
	  }
});


function saveUpload(){
	 if(excelData === null){
		 alert("需要上传的数据为空");
		 vm.saveFlag=false;
		 return false;
	 }
	 for(var dIndex in excelData){
		 if(excelData[dIndex].msg!=''){
		 	console.info("==============="+excelData[dIndex].msg);
			 alert("第"+excelData[dIndex].rowNo+"行数据校验未通过! "+excelData[dIndex].msg);
			 vm.saveFlag=false;
			 return false;
		 }

	 }
    for(var dIndex in excelData){
        excelData[dIndex].requireTypes = vm.requireTypes;
        excelData[dIndex].werks = vm.werks;
        excelData[dIndex].whNumber = vm.whNumber;
        excelData[dIndex].internalOrder = vm.innerOrder;
        excelData[dIndex].innerOrder_AUTYP = vm.innerOrder_AUTYP;
		excelData[dIndex].aceptLgortList = vm.aceptLgortList;
		excelData[dIndex].acceptPlant = vm.acceptPlant;
    }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	var url = '';
	if(vm.requireTypes==='41'|| vm.requireTypes==='42'){
		url = baseUrl + "out/createRequirement/import";
	}else if(vm.requireTypes==='43'){
		url = baseUrl + "out/innerorder/import";
	}else if(vm.requireTypes==='48' ){
		url = baseUrl + "out/producelinewarehouse/create";
	}else if(vm.requireTypes==='47' ){ //类型不转移
		console.log("hahah")
		url = baseUrl + "out/outreq/createOutReq13";
	}else if(vm.requireTypes==='46' ){ //303,301
		url = baseUrl + "out/outreq/createOutReq11";
	}

	console.info('jsondata '+jsondata);
	$.ajax({
		 url:url,
		 type:"post",
		 dataType:"json",
		 contentType: 'application/json',
		 data:jsondata,
		 success:function(resp){
		 	console.info('resp================>>> '+resp);
		 	console.info('resp.code================>>> '+resp.code);
			 if(resp.code == '0'){
				 //js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
				 $("#outreqNoLayer").html(resp.data);
				 layer.open({
					 type : 1,
					 offset : '50px',
					 skin : 'layui-layer-molv',
					 title : "需求创建成功",
					 area : [ '600px', '200px' ],
					 shade : 0,
					 shadeClose : false,
					 content : jQuery("#resultLayer"),
					 btn : [ '确定'],
					 btn1 : function(index) {
						 layer.close(index);
					 }
				 });
				 //$("#dataGrid").jqGrid("clearGridData", true);
				 //$(".btn").attr("disabled", false);
				 js.closeLoading();
			 }else{
				 js.alert("上传失败,"+resp.msg);
				 js.showErrorMessage("上传失败,"+resp.msg,1000*100);
				 //$(".btn").attr("disabled", false);
				 js.closeLoading();
			 }

			 /*if(resp.code === 0){
				 close();
				 js.showMessage('保存成功,需求号为：'+resp.data);
			 }else{
				 alert("上传失败," + resp.msg)
			 }*/
		 }
	 })
}
