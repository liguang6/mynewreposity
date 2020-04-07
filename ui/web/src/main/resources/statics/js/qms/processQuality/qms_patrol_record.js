var mydata={};
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		busTypeCode:'',
		testNodeList:[],
		testNode:'',
		testType:''
	},
	watch:{
		testType : {
		  handler:function(newVal,oldVal){
			  console.log("newVal",newVal);
			  if(newVal==''){
				  vm.testNodeList=[];
				  return;
			  }
			  this.getTestNodeList();
		  }
		},
	},
	methods: {
		query: function () {
			if($("#orderNo").val()==''){
				js.showErrorMessage("请输入订单！");
				return;
			}
			$("#dataGrid").jqGrid('GridUnload');
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
					{header:'巡检编号', name:'patrolRecordNo', width:180, align:"center"},
					{header:'工厂', name:'werks', width:100, align:"center"},
					{header:'订单', name:'orderNo', width:200, align:"center"},
					{header:'检验节点', name:'testNode', width:150, align:"center"},
					{header:'检验结果', name:'judgeDesc', width:90, align:"center"},
					{header:'检验员', name:'testor', width:120, align:"center"},
					{header:'检验时间', name:'testDate', width:160, align:"center"},
					{header: '操作', name: 'actions', width: 90 ,align:'center',formatter:function(val, obj, row, act){
						var actions = [];
						actions.push("<a title='查看' onClick='vm.detail(\""+row.patrolRecordNo+"\",\""+row.werks+"\",\""+row.orderNo+"\",\""+row.testNode+"\",\""+row.testor+"\",\""+row.testDate+"\")'><i class='fa fa-search'></i></a>&nbsp;");
						actions.push("<a title='编辑' onClick='vm.edit(\""+row.patrolRecordNo+"\",\""+row.werks+"\",\""+row.orderNo+"\",\""+row.testNode+"\",\""+row.testor+"\",\""+row.testDate+"\")'><i class='fa fa-pencil'></i></a>&nbsp;");
						actions.push("<a title='模板' onClick='vm.del(\""+row.patrolRecordNo+"\")'><i class='fa fa-trash'></i></a>&nbsp;");				
						return actions.join('');
					}}
				],	
				viewrecords: true,
			    shrinkToFit:false,
				rowNum: 15,  
				rowList : [15,30 ,50],
		        rownumWidth: 65, 
			});
		},
		getOrderNoSelect:function(){
			getOrderNoSelect("#orderNo",null,function(orderObj){
				if($("#orderNo").val()!=''){
					if(vm.testType=='01'){
						if($("#orderNo").val().indexOf("D")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,大巴订单以D为首字符");
							$("#orderNo").val("");;
						}
					}
					if(vm.testType=='02'){
						if($("#orderNo").val().indexOf("Z")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,专用车订单以Z为首字符");
							$("#orderNo").val("");;
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
			$.ajax({
				url:baseURL+"qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#testType").val()
				},
				async: true,
				success: function (response) { 
					vm.testNodeList=response.data;
				}
			});	
		},
		del:function(patrolRecordNo){
			$.ajax({
				url:baseURL+"qms/patrolRecord/delete/"+patrolRecordNo,
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
		add:function(){
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 巡检记录新增</i>',
				'qms/processQuality/qms_patrol_record_edit.html',
					true, true);
		},
		detail: function (patrolRecordNo,werks,orderNo,testNode,testor,testDate) {
			js.addTabPage(null, '<i class="fa fa-search" style="font-size:12px"> 巡检记录查看</i>',
			'qms/processQuality/qms_patrol_record_detail.html?patrolRecordNo='+patrolRecordNo+'&werks='+werks+'&orderNo='+orderNo+
			 '&testNode='+testNode+'&testor='+testor+'&testDate='+testDate+'&operateType=info',
					true, true);
		},
		edit: function (patrolRecordNo,werks,orderNo,testNode,testor,testDate) {
			js.addTabPage(null, '<i class="fa fa-pencil" style="font-size:12px"> 巡检记录编辑</i>',
					'qms/processQuality/qms_patrol_record_detail.html?patrolRecordNo='+patrolRecordNo+'&werks='+werks+'&orderNo='+orderNo+
					 '&testNode='+testNode+'&testor='+testor+'&testDate='+testDate+'&operateType=edit',
					true, true);
		},
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-30*24*3600*1000);
	$("#startDate").val(formatDate(startDate));
	$("#endDate").val(formatDate(now));
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    colModel: [		
	    	{label:'巡检编号', name:'patrolRecordNo', width:180, align:"center"},
			{label:'工厂', name:'werks', width:100, align:"center"},
			{label:'订单', name:'orderNo', width:200, align:"center"},
			{label:'检验节点', name:'testNode', width:150, align:"center"},
			{label:'检验结果', name:'judgeDesc', width:90, align:"center"},
			{label:'检验员', name:'testor', width:120, align:"center"},
			{label:'检验时间', name:'testDate', width:160, align:"center"},
			{label: '操作', name: 'actions', width: 90 ,align:'center'}
	    ],
	});
});
