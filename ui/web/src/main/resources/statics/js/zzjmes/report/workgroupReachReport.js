var mydata = [];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		order_no: '',
		order_desc: '',
		workgrouplist: [],
		workgroup:''
    },
	created: function(){	
		this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks : {
		  handler:function(newVal,oldVal){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_WORKGROUP_REACH_REPORT',
				},
				async: false,
				success: function (response) { 
					vm.workshop_list=response.data;
					if(response.data && response.data.length &&  response.data.length>0){
						vm.workshop = response.data[0]['code'];
						$.ajax({
							url: baseURL+"masterdata/getUserLine",
							dataType : "json",
							type : "post",
							async: false,
							data : {
								"WERKS": vm.werks,
								"WORKSHOP": response.data[0]['code'],
								"MENU_KEY":'ZZJMES_WORKGROUP_REACH_REPORT',
							},
							success: function (response) { 
								vm.line_list=response.data;
								if(response.data && response.data.length &&  response.data.length>0){
									vm.line = response.data[0]['code'];
								}else{
									vm.line = '';
								}
							}
						});	

					}else{
						vm.worshop = '';
					}
				}
			});	
			
			//根据工厂获取所有车间
			$.ajax({
				url:baseURL+"masterdata/getWerksWorkshopList",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":newVal,
				},
				async: false,
				success: function (response) { 
					vm.use_workshop_list=response.data;
				}
			});	
			
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"masterdata/getUserLine",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":vm.werks,
						"WORKSHOP":newVal,
						"MENU_KEY":'ZZJMES_WORKGROUP_REACH_REPORT',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(response.data && response.data.length &&  response.data.length>0){
							vm.line=response.data[0]['code'];
						}else{
							vm.line = '';
						}
					}
				});	
				
				//班组
				 $.ajax({
		        	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
		        	  data:{
		        		  "WERKS":vm.werks,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"ZZJMES_WORKGROUP_REACH_REPORT",
		        	  },
		        	  success:function(resp){
		        		 vm.workgrouplist = resp.data;
		        	  }
		          })
			}
		}
	},
	methods: {
		getOrderNoFuzzy:function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#order_no",null,fn_cb_getOrderInfo_search,"#werks");
		},
		query:function(){
           	if(vm.werks==''){
         		js.showErrorMessage('请选择工厂！');
        		return false;
        	}
           	if(vm.workshop==''){
           		js.showErrorMessage('请选择车间！');
        		return false;
        	}
           	if(vm.line==''){
           		js.showErrorMessage('请选择线别！');
        		return false;
        	}
         	if(vm.order_no=='' && ($("#start_date").val() == '' || $("#end_date").val() == '' )){
           		js.showErrorMessage('请选择订单或选择计划起始日期！');
        		return false;
        	}
          	if($("#start_date").val() != '' && $("#end_date").val() != '' && ($("#end_date").val()-$("#start_date").val() >7 )){
           		js.showErrorMessage('计划起始日期范围不能超过7天！');
        		return false;
        	}
          	
			js.loading();
			$("#btnQuery").val("查询...");	
			$("#btnQuery").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/report/workgroupReachReport",
				data:{
					'werks': vm.werks,
					'workshop': vm.workshop,
					'line': vm.line,
					'order_no': vm.order_no,
					'workgroup': vm.workgroup,
					'order_desc': vm.order_desc,
					'start_date': $("#start_date").val(),
					'end_date': $("#end_date").val(),
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					if(response.code == 0){
						mydata = [];
						$("#dataGrid").jqGrid('clearGridData');
						mydata= response.data;
						$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid'); 
					}else{
						//$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid');
						js.showErrorMessage("查询失败," + response.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnQuery").val("查询");	
					$("#btnQuery").removeAttr("disabled");
					js.closeLoading();
				}
			});
		},
		enter:function(e){
			vm.query();
		},
		//业务初始化表格
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
					{label: '订单编号', name: 'order_no',index:"order_no", width: "120",align:"center",sortable:false,editable:false},
					{label: '订单描述', name: 'order_desc',index:"order_desc", width: "300",align:"center",sortable:false,editable:false},
		            {label: '大班组', name: 'workgroup_name',index:"workgroup_name", width: "200",align:"center",sortable:false,editable:false,},
		            {label: '计划种类', name: 'plan_sum',index:"plan_sum", width: "120",align:"center",sortable:false,editable:false,formatter:function(val, obj, row, act){
						if(row.workgroup_name=='-'){
							return '-'
						}else{
							return val;
						}

					}},
		            {label: '完成种类', name: 'prod_sum',index:"prod_sum", width: "120",align:"center",sortable:false,editable:false,formatter:function(val, obj, row, act){
		            	if(row.workgroup_name=='-'){
							return '-'
						}else{
							return val;
						}

					}},
		            {label: '欠产种类', name: 'un_prod_sum',index:"un_prod_sum", width: "120",align:"center",sortable:false,editable:false,formatter:function(val, obj, row, act){
		            	if(row.workgroup_name=='-'){
							return '-'
						}else{
							return val;
						}

					}},
		            {label: '达成率', name: 'reach',index:"reach", width: "120",align:"center",sortable:false,editable:false,formatter:function(val, obj, row, act){
		            	if(row.workgroup_name=='-'){
							return '-'
						}else{
							return val;
						}

					}},
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				viewrecords: true,
		        shrinkToFit:false,
		        showCheckbox:false,
		        multiselect: false,
				rowNum: 15,  
				cellsubmit:'clientArray',
		    });	
		},

	}
});

$(function () {
	//设置默认日期，当前日期往前一个月
	var end_date = new Date().toLocaleDateString().replace(/\//g,"-");
	var start_date = GetDateStr(-7);//获取退后一个月的日期
	$("#start_date").val(start_date);
	$("#end_date").val(end_date);
	vm.showTable();
});

function fn_cb_getOrderInfo_search(order){
	vm.order_no = order.order_no;
	vm.order_desc = order.order_desc;
}

function GetDateStr(AddDayCount) { 
	   var dd = new Date();
	   dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
	   var y = dd.getFullYear(); 
	   var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
	   var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0
	   return y+"-"+m+"-"+d; 
}
