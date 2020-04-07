
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		busTypeCode:'',
		testNodeList:[],
		testNode:''
	},
	created:function(){
		
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		getBusTypeCodeNoSelect:function(){
			getBusTypeCodeNoSelect("#busTypeCode","#busTypeCode",vm.getTestNodeList);
		},
		getOrderNoSelect:function(){
			getOrderNoSelect("#orderNo","#orderNo",function(){});
		},
		checkTestType:function(){
			if($("#busTypeCode").val()==''){
				if(vm.testNodeList.length==0){
					js.showErrorMessage("请输入正确的车型！");
				}
			}
		},
		getTestNodeList:function(){
			$.ajax({
				url:baseURL+"qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#busTypeCode").attr("testType")
				},
				async: true,
				success: function (response) { 
					vm.testNodeList=response.data;
				}
			});	
		},
		del:function(tempNo){
			$.ajax({
				url:baseURL+"qms/checkTemplate/delete/"+tempNo,
				contentType: "application/json",
				type: "GET",
				success: function (response) { 
				    if(response.code==0){
				    	js.showMessage('操作成功');
				    	$("#searchForm").submit();
				    }else{
				    	js.showErrorMessage(response.msg);
				    }
				}
			});
		},
		imp: function () {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 模板导入</i>',
			'qms/processQuality/qms_check_template_import.html',
					true, true);
		},
		detail: function (testType,busTypeCode,orderNo,testNode,tempNo) {
			js.addTabPage(null, '<i class="fa fa-search" style="font-size:12px"> 模板查看</i>',
			'qms/processQuality/qms_check_template_edit.html?tempNo='+tempNo+'&testNode='+testNode+'&busTypeCode='+busTypeCode+'&testType='+testType+'&orderNo='+orderNo+'&operateType=info',
					true, true);
		},
		edit: function (testType,busTypeCode,orderNo,testNode,tempNo) {
			js.addTabPage(null, '<i class="fa fa-pencil" style="font-size:12px"> 模板编辑</i>',
			'qms/processQuality/qms_check_template_edit.html?tempNo='+tempNo+'&testNode='+testNode+'&busTypeCode='+busTypeCode+'&testType='+testType+'&orderNo='+orderNo+'&operateType=edit',
					true, true);
		},
	}
});
$(function () {
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'模板编号', name:'TEMP_NO', width:150, align:"center"},
			{header:'车型', name:'BUS_TYPE_CODE', width:100, align:"center"},
			{header:'订单', name:'ORDER_NO', width:185, align:"center"},
			{header:'检验节点', name:'TEST_NODE', width:90, align:"center"},
			{header:'检验类别', name:'TEST_TYPE', width:90, align:"center"},
			{header:'创建人', name:'CREATOR', width:120, align:"center"},
			{header:'创建时间', name:'CREATE_DATE', width:160, align:"center"},
			{header: '操作', name: 'actions', width: 180 ,align:'center',formatter:function(val, obj, row, act){
				var actions = [];
				actions.push("<a title='查看模板' onClick='vm.detail(\""+row.TEST_TYPE+"\",\""+row.BUS_TYPE_CODE+"\",\""+row.ORDER_NO+"\",\""+row.TEST_NODE+"\",\""+row.TEMP_NO+"\")'><i class='fa fa-search'></i></a>&nbsp;&nbsp;&nbsp;");
				if(row.USE_COUNT==0 && row.LOGIN_USER==row.CREATOR){
				    actions.push("<a title='编辑模板' onClick='vm.edit(\""+row.TEST_TYPE+"\",\""+row.BUS_TYPE_CODE+"\",\""+row.ORDER_NO+"\",\""+row.TEST_NODE+"\",\""+row.TEMP_NO+"\")'><i class='fa fa-pencil'></i></a>&nbsp;&nbsp;&nbsp;");
				}
				// 只有导入用户才允许删除操作  已录入品质数据，不允许删除模板  
				if(row.USE_COUNT==0 && row.LOGIN_USER==row.CREATOR){
					actions.push("<a title='删除模板' onClick='vm.del(\""+row.TEMP_NO+"\")'><i class='fa fa-trash'></i></a>&nbsp;");
				}
				return actions.join('');
			}}
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		rowList : [15,30 ,50],
        rownumWidth: 65, 
	});
});
