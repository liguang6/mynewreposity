var excelData = []; 
var vm = new Vue({
    el:'#rrapp',
    data:{
		saveFlag:true,
		order_type:'',
		vehicle_type_code:'',
		order_no:'',
		testNodeList:[],
		testNode:''
	},  
	watch:{
//		busTypeCode : {
//		  handler:function(newVal,oldVal){
//			$.ajax({
//				url:baseURL+"qms/config/checkNode/getTestNodeList",
//				dataType : "json",
//				type : "post",
//				data : {
//					"testType":$("#busTypeCode").attr("testType")
//				},
//				async: true,
//				success: function (response) { 
//					vm.wh_list=response.data
//					vm.whNumber=response.data[0]['WH_NUMBER']
//				}
//			});	
//		  }
//		},
//		testType : {
//		  handler:function(newVal,oldVal){
//			$.ajax({
//				url:baseURL+"qms/config/",
//				dataType : "json",
//				type : "post",
//				data : {
//					"WERKS":newVal
//				},
//				async: true,
//				success: function (response) { 
//					vm.wh_list=response.data
//					vm.whNumber=response.data[0]['WH_NUMBER']
//				}
//			});	
//		  }
//		}
	},
	created: function(){
		
	},
	methods: {
		save:function(){
		   saveUpload();
		},
		getBusTypeCodeNoSelect:function(){
			getBusTypeCodeNoSelect("#busTypeCode","#busTypeCode",vm.getTestNodeList);
		},
		checkTestType:function(){
			if($("#busTypeCode").val()==''){
				if(vm.testNodeList.length==0){
					js.showErrorMessage("请输入正确的车型！");
				}
			}
		},
		getOrderNoSelect:function(){
			getOrderNoSelect("#orderNo",null,function(orderObj){
				if($("#busTypeCode").attr("testtype")!=''){
					if($("#busTypeCode").attr("testtype")=='01'){
						if($("#orderNo").val().indexOf("D")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,大巴订单以D为首字符");
							$("#orderNo").val("");
						}
					}
					if($("#busTypeCode").attr("testtype")=='02'){
						if($("#orderNo").val().indexOf("Z")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,专用车订单以Z为首字符");
							$("#orderNo").val("");
						}
					}
				}
			});
		},
		getTestNodeList:function(){
			if($("#busTypeCode").attr("testtype")=='02'){
				$("#orderNo").attr("readonly",true);
			}else{
				$("#orderNo").removeAttr("readonly");
			}
			$.ajax({
				url:baseURL+"qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#busTypeCode").attr("testtype")
				},
				async: true,
				success: function (response) { 
					vm.testNodeList=response.data;
					//vm.testNode=response.data[0]['testNode'];
				}
			});	
		},
	}
});
$(function () {
	//初始化table
	showAddTable();
	
});
$("#uploadForm").validate({
	submitHandler:function(e){
		showAddTable();
	}
});
function showAddTable(){
	jQuery('#dataGrid').GridUnload();  
	var form = new FormData(document.getElementById("uploadForm"));
	console.log("form",form);
	js.loading("正在导入,请稍候...");
	$("#btnImport").val("正在导入...");	
	$("#btnImport").attr("disabled","disabled");
	$.ajax({
		url:baseUrl + "qms/checkTemplate/previewExcel",
		data:form,
		type:"post",
		contentType:false,
		processData:false,
		success:function(response){
			js.closeLoading();
			$("#btnImport").val("导入预览");	
			$("#btnImport").removeAttr("disabled");
			if(response.code == 0){
				//加载jqgrid
				$("#dataGrid").dataGrid({
				 	datatype: 'local', 
					colNames:['工序名称','检验项目','标准要求','检验分组','数值必填项','一次交检合格项','拍照必录项','是否巡检项','校验提示信息','行项目'],
					colModel:[
					  {name:"processName",index:'processName',align:'center',width:"100"},
			          {name:"testItem",index:'testItem',align:'center',width:"160"},
			          {name:"testStandard",index:'testStandard',align:'center',width:"260"},
			          {name:"testGroup",index:'testGroup',align:'center',width:"80"},
			          {name:"numberFlag",index:'numberFlag',align:'center',width:"80"},
			          {name:"onePassedFlag",index:'onePassedFlag',align:'center',width:"120"},
			          {name:"photoFlag",index:'photoFlag',align:'center',width:"100"},
			          {name:"patrolFlag",index:'patrolFlag',align:'center',width:"90"},
			          {name:"msg",index:'msg',align:'center',classes:"msg-font",width:"120"}, 
					  {name:"tempItemNo",index:'tempItemNo',align:'center',hidden:true},

			        ],
			        viewrecords: true,
			        shrinkToFit:false,
			        width:1100,
			        rownumbers:false,
				});
				//清空数据
				$("#dataGrid").dataGrid("clearGridData");
				//加入数据
				for(dIndex in response.data){
					$("#dataGrid").dataGrid("addRowData",dIndex+1,response.data[dIndex]);
				}
				excelData = response.data;
				console.log("excelData",excelData);
			}
			else
			  alert("上传失败," + response.msg);
		}.bind(this)
	});
}

function saveUpload(){
	console.log("excelData",excelData);
	 if(excelData.length == 0){
		 js.showErrorMessage("需要上传的数据为空");
		 return false;	
	 }
	 for(var dIndex in excelData){
		 if(excelData[dIndex].msg!=''){
			 js.showErrorMessage("第"+excelData[dIndex].tempItemNo+"行数据校验未通过!");
			 wm.saveFlag=false;
			 return false;
		 }
	 }
	 if($('#busTypeCode').val()==''){
		 js.showErrorMessage("车型不能为空!");
         return ;
	 }
     if($('#testNode').val()==''){
    	 js.showErrorMessage("检验节点不能为空!");
         return ;
	 }
	 if($('#busTypeCode').attr("testType")=='01'){
		 if($("#orderNo").val()==''){
			 js.showErrorMessage("订单号不能为空!");
	         return ;
		 }
	 }
	 
	 js.loading("保存中...");
	 $("#btnSave").html("保存中...");
	 $("#btnSave").attr("disabled","disabled");
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "qms/checkTemplate/save",
		 type:"post",
		 dataType:"json",
		 data:{
			 saveData:jsondata,
			 orderNo:$('#orderNo').val(),
			 busTypeCode:$('#busTypeCode').val(),
			 testType:$('#busTypeCode').attr("testType"),
			 testNode:$('#testNode').val(),
		 },
		 success:function(resp){
			 $("#btnSave").html("保存");	
			 $("#btnSave").removeAttr("disabled");
			 js.closeLoading();
			 if(resp.code === 0){
				js.showMessage('保存成功！'+resp.tempNo);
				$("#dataGrid").dataGrid("clearGridData");
				excelData=[];
			 }else{
				 alert("导入失败：" + resp.msg);
			 }
		 }
	 });
}
