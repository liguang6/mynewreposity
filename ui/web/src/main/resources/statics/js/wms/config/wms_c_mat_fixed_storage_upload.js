//国际化取值
var smallUtil=new smallTools();
var array = new Array("SUCCESS","WH_NUMBER","PLANT","MATNR","MATNR_DESC","STORAGE_TYPE","BIN_CODE","MAX_QTY",
		"MIN_QTY","LAST_EDIT_DATE","LAST_EDIT","CHECK_MESSAGE","FAIL_UPLOAD","DATA_UPLOAD_EMPTY","DATA_CHECK_FAILED",
	"LIFNR","STOCK_TYPE","LGORT","PRIORITY");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

var excelData = null;
var vm = new Vue({
    el:'#rrapp',
    data:{
    	saveFlag:false
    },
});
$("#uploadForm").validate({
	  submitHandler:function(e){
		  js.loading();
		  //提交表单 & 加载数据
		  var form = new FormData(document.getElementById("uploadForm"));
			$.ajax({
				url:baseUrl + "config/fixedStorage/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local',
							colNames:['',languageObj.WH_NUMBER,languageObj.PLANT,languageObj.MATNR,languageObj.MATNR_DESC,languageObj.STORAGE_TYPE,languageObj.BIN_CODE,
								languageObj.PRIORITY,languageObj.MIN_QTY,languageObj.MAX_QTY,languageObj.LGORT,languageObj.STOCK_TYPE,
								languageObj.LIFNR,languageObj.CHECK_MESSAGE],
							colModel:[
						    	  {name:"rowNo",index:'rowNo',align:'center',width:40},
						    	  {name:"whNumber",index:'whNumber',align:'center',width:80},
						          {name:"werks",index:'werks',align:'center',width:80},
						          {name:"matnr",index:'matnr',align:'center',width:110},
						          {name:"maktx",index:'maktx',align:'center',width:180},
						          {name:"storageAreaCode",index:'storageAreaCode',align:'center',width:100},
						          {name:"binCode",index:'binCode',align:'center',width:100},
						          {name:"seqno",index:'seqno',align:'center',width:100},
						          //{name:"qty",index:'qty',align:'center',width:100},
						          {name:"stockM",index:'stockM',align:'center',width:100},
						          {name:"stockL",index:'stockL',align:'center',width:100},
						          {name:"lgort",index:'lgort',align:'center',width:100},
						          {name:"sobkz",index:'sobkz',align:'center',width:100},
						          {name:"lifnr",index:'lifnr',align:'center',width:100},
						          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:150},

					        ],
					        width:1190
						});
						debugger;
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
					  alert(languageObj.FAIL_UPLOAD+"," + data.msg);
				}.bind(this)
			});
			js.closeLoading();
	  },
});


function saveUpload(){
	 if(excelData === null){
		 alert(languageObj.DATA_UPLOAD_EMPTY);
		 vm.saveFlag=false;
		 return false;
	 }
	 for(var dIndex in excelData){
		 if(excelData[dIndex].msg!=''){
			 alert(excelData[dIndex].rowNo+languageObj.DATA_CHECK_FAILED);
			 vm.saveFlag=false;
			 return false;
		 }
	 }
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/fixedStorage/import",
		 type:"post",
		 dataType:"json",
		 contentType: 'application/json',
		 data:jsondata,
		 success:function(resp){
			 if(resp.code === 0){
				 vm.saveFlag=true;
				 close();
				 js.showMessage(languageObj.SUCCESS);
			 }else{
				 alert(languageObj.FAIL_UPLOAD+"," + resp.msg)
			 }
		 }
	 })
}
