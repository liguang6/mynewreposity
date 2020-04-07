var mydata={};
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		orderNo:'',
		testType:'',
		orderConfigName:'',
		orderConfigList:[],
		workshopList:[],
		lineList:[],
		showList:false,
	},
	watch:{
		testType:function(newVal,oldVal){
		  if(vm.testType=='01'){
			  this.showList=false;
			  
		  }
		  if(vm.testType=='02'){
			  this.showList=true; 
		  }
		}
	},
	created:function(){
		this.testType='01';
		$.ajax({
			 url:baseUrl + "qms/keyParts/queryWorkshopList",
			 type:"post",
			 dataType:"json",
			 data:{
			 },
			 success:function(resp){
				 vm.workshopList=resp.data;
			 }
		 });
		$.ajax({
			 url:baseUrl + "qms/keyParts/queryLineNameList",
			 type:"post",
			 dataType:"json",
			 data:{
			 },
			 success:function(resp){
				 vm.lineList=resp.data;
			 }
		 });
	},
	methods: {
		query: function () {
			
			
			if($("#testType").val()=='01'){
				if(vm.orderNo=='' && $("#vin").val()==''){
					js.showMessage("订单、VIN请至少输入一个查询条件！");
					return;
				}
				$("#dataGrid").jqGrid('GridUnload');
				// 初始化DataGrid对象
				$('#dataGrid').dataGrid({
					searchForm: $("#searchForm"),
					columnModel: [
						{label:"车号",align:"center",name:"bus_number",width: 160 ,},
			            {label:"工厂",align:"center",name:"factory_name",width: 80 ,},
			            {label:"车间",align:"center",name:"workshop",width: 70 ,},
			            {label:"订单",align:"center",name:"order_no",width: 90 ,},
			            {label:"订单描述",align:"center",name:"order_desc",width: 150 ,sortable:false,},
			            {label:"订单配置",align:"center",name:"order_config_name","defaultContent": "",sortable:false,},
			            {label:"录入人",align:"center",name:"username","defaultContent": "",sortable:false,},
			            {label:"录入时间",align:"center",name:"edit_date","defaultContent": ""},
			            {label:'操作', name:'actions', align:"center", sortable:false, title:false, formatter: function(val, obj, row, act){
							var actions = [];
							var order=row.order_desc ;
							actions.push('<a onClick="vm.bmsdetail(\''+row.key_components_template_id+'\',\''+row.bus_number+'\',\''+row.factory_name+'\',\''+row.workshop+'\',\''+order+'\',\''+row.order_config_name+'\')" title="查看"><i class="fa fa-search"></i></a>&nbsp;');
							return actions.join('');
						}},
				    ],
					// 加载成功后执行事件
					ajaxSuccess: function(data){
						js.closeLoading();
						$(".btn").attr("disabled", false);
					},
					viewrecords: true,
					rowNum: 15,
					shrinkToFit:true,
					autowidth :true,
					rowList : [15,30,50],
					shrinkToFit:false,  
					autoScroll: true, 
				});
			}
			// 专用车
			if($("#testType").val()=='02'){
				
				console.log("orderNo",$("#orderNo").val()+"-"+$("#vin").val());
				var order_no=$("#orderNo").val();
				if(order_no=='' && $("#vin").val()==''){
					js.showMessage("订单、VIN请至少输入一个查询条件！");
					return;
				}
				
				$("#dataGrid").jqGrid('GridUnload');
				// 初始化DataGrid对象
				$('#dataGrid').dataGrid({
					searchForm: $("#searchForm"),
					columnModel: [
						{label:'VIN号/物料条码', name:'vin', index:'vin', width:200, align:"center", formatter: function(val, obj, row, act){
							
							return (row.vin!=null ? row.vin : '')+(row.materialBarcode!=null ? row.materialBarcode : '');
						}},
						{label:'订单', name:'orderNo', index:'orderNo', width:100, align:"center"},
						{label:'订单描述', name:'orderDesc', index:'orderDesc', width:220, align:"center"},
						{label:'配置', name:'orderConfigName', index:'orderConfigName', width:160, align:"center"},
						{label:'工厂', name:'factoryName', index:'factoryName', width:150, align:"center"},
						{label:'线别', name:'lineName', index:'lineName', width:150, align:"center"},
						{label:'操作', name:'actions', align:"center",sortable:false, title:false, formatter: function(val, obj, row, act){
							var actions = [];
							var vin=(row.vin!=null ? row.vin : '')+(row.materialBarcode!=null ? row.materialBarcode : '');
							var order=row.orderNo+' '+row.orderDesc ;
							actions.push('<a onClick="vm.detail(\''+row.tplDetailId+'\',\''+row.vin+'\',\''+row.factoryName+'\',\''+row.lineName+'\',\''+order+'\',\''+row.orderConfigName+'\',\''+row.orderDesc+'\')" title="查看"><i class="fa fa-search"></i></a>&nbsp;');
							return actions.join('');
						}},
						{label:'tplDetailId', name:'tplDetailId', index:'tplDetailId', hidden:true},
				    
				    ],
					// 加载成功后执行事件
					ajaxSuccess: function(data){
						js.closeLoading();
						$(".btn").attr("disabled", false);
					},
					rowNum: 15,
					shrinkToFit:true,
					autowidth :true,
					rowList : [15,30,50],
					shrinkToFit:false,  
					autoScroll: true, 
				});
			}
		},
//		getOrderNoSelect:function(){
//			//getOrderNoSelect("#orderNo","#orderNo",vm.orderNoAfterSelect());
//		},
		onOrderNoChange:function(){
			getOrderNoSelect("#orderNo",null,function(orderObj){
				vm.orderNo=$("#orderNo").val();
				$.ajax({
					 url:baseUrl + "qms/keyParts/queryOrderConfigList",
					 type:"post",
					 dataType:"json",
					 async:false,
					 data:{
						 orderNo:$("#orderNo").val(),
						 testType:$('#testType').val(),
					 },
					 success:function(resp){
						 vm.orderConfigList=resp.data;
					 }
				 });
			}.bind(this));
		},
		
		bmsdetail: function (key_components_template_id,bus_number,factoryName,workshop,order,orderConfigName) {
			js.addTabPage(null, '<i class="fa fa-search" style="font-size:12px">查看</i>',
			'qms/processQuality/qms_key_parts_detail.html?key_components_template_id='+key_components_template_id+'&testType=01'+
			'&bus_number='+bus_number+'&workshop='+workshop+'&factoryName='+factoryName+'&order='+order+
			'&orderConfigName='+orderConfigName,
					true, true);
		},
		detail: function (tplDetailId,vin,factoryName,lineName,order,orderConfigName) {
			js.addTabPage(null, '<i class="fa fa-search" style="font-size:12px">查看</i>',
			'qms/processQuality/qms_key_parts_detail.html?tplDetailId='+tplDetailId+'&testType=02'+
			'&vin='+vin+'&lineName='+lineName+'&factoryName='+factoryName+'&order='+order+
			'&orderConfigName='+orderConfigName,
					true, true);
		},
	}
});
$(function () {
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    colModel: [		
	    	{ label: '车号', name: 'bus_number', index: 'bus_number', width: 150,frozen:true,align:"center" }, 
	        { label: '工厂', name: 'factory_name', index: 'factory_name',frozen:true, width: 70,align:"center" }, 
	        { label: '车间', name: 'workshop', index: 'workshop',frozen:true, width: 150,align:"center" }, 
	        { label: '订单', name: 'order_no', index: 'order_no',frozen:true, width: 70,align:"center" },
			{ label: '订单配置', name: 'order_config_name', index: 'order_config_name', width: 50,align:"center" }, 
			{ label: '录入人', name: 'username', index: 'username', width: 160 ,align:"center"},
			{ label: '录入时间', name: 'edit_date', index: 'edit_date', width: 160 ,align:"center"},
	    ],
	});
});
