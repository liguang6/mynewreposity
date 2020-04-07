var mydata = []; 
var lastrow,lastcell;
var vm = new Vue({
    el:'#rrapp',
    data:{
		saveFlag:true,
		testType:'',
		orderNo:'',
		testNodeList:[],
		testNode:''
	},  
	watch:{
		testType : {
		  handler:function(newVal,oldVal){
			  if(newVal==''){
				  vm.testNodeList=[];
				  return;
			  }
			  this.getTestNodeList();
		  }
		},
		testNode : {
		  handler:function(newVal,oldVal){
			  console.log(vm.testType+"-"+vm.testNode+"-"+vm.orderNo);
			  if(vm.testType!='' && vm.testNode!=''){
				  // 大巴 必须录入订单号才能查找品质模板
				  if(vm.testType=='02' || (vm.testType=='01' && vm.orderNo!='')){
					 this.getTemplate();
				  }
			  }	
		  }
		},
		orderNo:function(newVal,oldVal){
			if(vm.testType!='' && vm.testNode!=''){
				  // 大巴 必须录入订单号才能查找品质模板
				  if(vm.orderNo!=''){
					 this.getTemplate();
				  }
		    }	
		}
	},
	created: function(){
		
	},
	methods: {
		save:function(){
//			 if($('#werks').val()==''){
//				 js.showErrorMessage("工厂代码不能为空!");
//		         return ;
//			 }
			 if($("#testType").val()==''){
				 js.showErrorMessage("订单类型不能为空!");
		         return ;
			 }
		     if($("#testNode").val()==''){
		    	 js.showErrorMessage("检验节点不能为空!");
		         return ;
			 }
			 if($("#testType").val()=='01'){
				 if($("#orderNo").val()==''){
					 js.showErrorMessage("订单号不能为空!");
			         return ;
				 }
			 }
			 var save_flag=false;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var saveData=[];
			//获取选中表格数据
			var rows = $("#dataGrid").jqGrid("getRowData");	
			if(rows.length==0){
				js.showErrorMessage("未录入任何信息，不允许保存!");
				return;
			}
			for (var i = 0; i < rows.length; i++) {
			    var data=rows[i];
			    console.log(data["testResult"]+"-"+data["judge"]);
	            if(data["testResult"]!='' || data["judge"]!=''){
					save_flag=true;
				}
				saveData.push(data);
			}
			if(!save_flag){
				js.showErrorMessage("检测结果、判定不能为空!");
				return;
			}
			console.log("saveData",saveData);
			 js.loading("保存中...");
			 $("#btnSave").html("保存中...");
			 $("#btnSave").attr("disabled","disabled");
			 //提交数据 & 关闭窗口
			 var jsondata = JSON.stringify(saveData);
			 $.ajax({
				 url:baseUrl + "qms/patrolRecord/save",
				 type:"post",
				 dataType:"json",
				 async:false,
				 data:{
					 saveData:jsondata,
					 orderNo:$('#orderNo').val(),
					 werks:$('#werks').val(),
					 testType:$('#testType').val(),
					 testNode:$('#testNode').val(),
				 },
				 success:function(resp){
					 $("#btnSave").html("保存");	
					 $("#btnSave").removeAttr("disabled");
					 js.closeLoading();
					 if(resp.code === 0){
						js.showMessage('保存成功！巡检编号：'+resp.patrolRecordNo);
						$("#dataGrid").dataGrid("clearGridData");
					 }else{
						 alert("保存失败：" + resp.msg);
					 }
				 }
			 });
		},
		getTemplate:function(){
			$.ajax({
				url:baseURL+"qms/patrolRecord/getTemplateList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#testType").val(),
					"testNode":$("#testNode").val(),
					"orderNo":$("#orderNo").val()
				},
				async: false,
				success: function (response) { 
					if(response.code!=0){
						js.showErrorMessage(response.msg);
					}
					mydata=response.data;
					console.log("mydata",mydata);
					showAddTable(mydata);
				}
			});	
		},
		getOrderNoSelect:function(){
			getOrderNoSelect("#orderNo",null,function(orderObj){
				vm.orderNo=$("#orderNo").val()
				if($("#orderNo").val()!=''){
					if(vm.testType=='01'){
						if($("#orderNo").val().indexOf("D")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,大巴订单以D为首字符");
							vm.orderNo="";
						}
					}
					if(vm.testType=='02'){
						if($("#orderNo").val().indexOf("Z")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,专用车订单以Z为首字符");
							vm.orderNo="";
						}
					}
				}
			});
		},
		checkTestType:function(){
			if($("#testType").val()==''){
				if(vm.testNodeList.length==0){
					js.showErrorMessage("请选择订单类型！");
				}
			}
		},
		getTestNodeList:function(){
			console.log("getTestNodeList ");
			$.ajax({
				url:baseURL+"qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#testType").val()
				},
				async: false,
				success: function (response) { 
					vm.testNodeList=response.data;
				}
			});	
		},
	}
});
$(function () {
	showAddTable(mydata);
});

function showAddTable(mydata){
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
		cellsubmit:'clientArray',
		datatype: "local",
		data: mydata, 
		cellEdit:true,
		cellurl:'#',
		colNames:['工序名称','检验项目','标准要求','检验结果','判定','复检结果','复判','',''],
		colModel:[
		  {name:"processName",index:'processName',align:'center',width:"120"},
          {name:"testItem",index:'testItem',align:'center',width:"120"},
          {name:"testStandard",index:'testStandard',align:'center',width:"480"},
          {name:"testResult",index:'testResult',align:'center',width:"160",sortable:false,editable:true},
          {name:"judge",index:'judge',align:'center',width:"60",editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:'OK:OK;NG:NG;NA:NA',dataEvents:[]},
		  },
          {name:"reTestResult",index:'reTestResult',align:'center',width:"120",editable:true},
          {name:"reJudge",index:'reJudge',align:'center',width:"60",editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:'OK:OK;NG:NG',dataEvents:[]},
		  },
          {name:"tempNo",index:'tempNo',align:'center',hidden:true},
		  {name:"tempItemNo",index:'tempItemNo',align:'center',hidden:true},
        ],
        beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
	   		if(value!='' && value!=null){
	   			if(cellname=='judge'){
                   if(value=='NG'){
                	   $("#dataGrid").jqGrid('setCell',rowid,"reTestResult",'',{background :'blue'});
                	   $("#dataGrid").jqGrid('setCell',rowid,"reJudge",'',{background :'blue'});
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reTestResult', '','editable-cell');
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reJudge', '','editable-cell');
                	  
                       $("#"+rowid).children().eq(iCol+1).removeClass('not-editable-cell');
                       $("#"+rowid).children().eq(iCol+2).removeClass('not-editable-cell');
                   }
                   if(value=='OK' || value=='NA'){
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reTestResult', '','not-editable-cell');
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reJudge', '','not-editable-cell');
                	   $("#dataGrid").jqGrid('setCell',rowid,"reTestResult",'',{background :''});
                	   $("#dataGrid").jqGrid('setCell',rowid,"reJudge",'',{background :''});
                   }
				}
	   		}
		},
        viewrecords: true,
        shrinkToFit:false,
        width:1100,
        rownumbers:false,
        //showCheckbox:true,
	});
}

