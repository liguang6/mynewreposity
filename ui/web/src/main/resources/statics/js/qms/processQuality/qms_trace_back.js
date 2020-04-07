var mydata={};
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		orderNo:'',
		testType:'',
		
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
	},
	methods: {
		query: function () {
			
			if($("#testType").val()=='01'){
				if($("#vin").val()=='' && $("#busNumber").val()=='' && $("#orderNo").val()==''
					&& $("#batch").val()=='' && $("#parts").val()==''){
					js.showErrorMessage("VIN、车号、零部件、批次、订单请至少输入一个查询条件！");
					return;
				}
				$("#dataGrid").jqGrid('GridUnload');
				// 初始化DataGrid对象
				$('#dataGrid').dataGrid({
					searchForm: $("#searchForm"),
					columnModel: [
						{label:"车号",align:"center",name:"bus_number",width: 160 ,},
						{label:"VIN",align:"center",name:"vin",width: 160 ,},
						{label:"订单",align:"center",name:"order_desc",width: 120 ,},
			            {label:"工厂",align:"center",name:"factory_name",width: 80 ,},
			            {label:"车间",align:"center",name:"workshop",width: 70 ,},
			            {label:"零部件编号",align:"center",name:"parts_no",width: 90 ,},
			            {label:"零部件名称",align:"center",name:"parts_name",sortable:false,},
			            {label:"批次",align:"center",name:"batch",sortable:false,},
			            {label:"安装日期",align:"center",name:"edit_date"},
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
				if($("#vin").val()=='' && $("#orderNo").val()==''
					&& $("#materialNo").val()=='' && $("#parts").val()==''){
					js.showErrorMessage("VIN、零部件、物料流水号、订单请至少输入一个查询条件！");
					return;
				}
				$("#dataGrid").jqGrid('GridUnload');
				
				// 初始化DataGrid对象
				$('#dataGrid').dataGrid({
					searchForm: $("#searchForm"),
					columnModel: [
						{ label: 'VIN号', name: 'vin', index: 'vin', width: 140,frozen:true,align:"center" }, 
				        { label: '驾驶室/电池包编号', name: 'material_barcode', index: 'material_barcode', width: 150,align:"center" }, 
				        { label: '订单', name: 'order_name', index: 'order_name',frozen:true, width: 120,align:"center" },
						{ label: '订单配置', name: 'order_config_name', index: 'order_config_name', width: 50,align:"center" }, 
						{ label: '工厂', name: 'factory_name', index: 'factory_name', width: 80,align:"center" }, 
						{ label: '车间', name: 'workshop_name', index: 'workshop_name', width: 50,align:"center" }, 
						{ label: '线别', name: 'line_name', index: 'line_name', width: 80,align:"center" }, 
						//{ label: '工序编码', name: 'process_code', index: 'process_code', width: 70,align:"center" }, 
						{ label: '工序名称', name: 'process_name', index: 'process_name', width: 100,align:"center" }, 
						{ label: '零部件号', name: 'parts_no', index: 'parts_no', width: 90,align:"center" }, 
						{ label: '零部件名称', name: 'parts_name', index: 'parts_name', width: 120,align:"center" }, 
						//{ label: '批次', name: 'batch', index: 'batch', width: 160 ,align:"center"},
						{ label: '物料流水编号', name: 'material_no', index: 'material_no', width: 160 ,align:"center"}, 
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
		getOrderNoSelect:function(){
			getOrderNoSelect("#orderNo",null,function(orderObj){
				if($("#orderNo").val()!=''){
					if(vm.testType=='01'){
						if($("#orderNo").val().indexOf("D")<0){
							js.showErrorMessage("输入订单与所选择的订单类型不符,大巴订单以D为首字符");
							$("#orderNo").val("");
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
	    	{ label: 'VIN号', name: 'vin', index: 'vin', width: 120,frozen:true,align:"center",frozen:true, }, 
	        { label: '驾驶室/电池包编号', name: 'factory_name', index: 'factory_name',frozen:true, width: 150,align:"center" }, 
	        { label: '订单', name: 'order_no', index: 'order_no',frozen:true, width: 70,align:"center" },
			{ label: '订单配置', name: 'order_config_name', index: 'order_config_name', width: 90,align:"center" }, 
			{ label: '工厂', name: 'factory_name', index: 'factory_name', width: 50,align:"center" }, 
			{ label: '车间', name: 'workshop_name', index: 'workshop_name', width: 50,align:"center" }, 
			{ label: '线别', name: 'line_name', index: 'line_name', width: 50,align:"center" }, 
			{ label: '工序编码', name: 'process_code', index: 'process_code', width: 80,align:"center" }, 
			{ label: '工序名称', name: 'process_name', index: 'process_name', width: 90,align:"center" }, 
			{ label: '零部件号', name: 'parts_no', index: 'parts_no', width: 90,align:"center" }, 
			{ label: '零部件名称', name: 'parts_name', index: 'parts_name', width: 120,align:"center" }, 
			{ label: '批次', name: 'batch', index: 'batch', width: 160 ,align:"center"},
			{ label: '物料流水编号', name: 'material_no', index: 'material_no', width: 160 ,align:"center"},
	    ],
	});
});
