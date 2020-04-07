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
		prod_process: '',
		tab_id: '',
		formUrl: '#',
		columnModel: [],
		report_type: ''
    },
	created:function(){	
		this.werks = $("#werks").find("option").first().val();
		this.tab_id="#div_1";
		this.report_type = 'order';
	},
	watch:{
		tab_id : {
			handler : function(newVal,oldVal){
				var initFlag = false;
				vm.table_id = "#dataGrid"+newVal.replace("#","_");
				$("#gbox_dataGrid"+oldVal.replace("#","_")).css("display","none");
				$("#gbox_dataGrid"+newVal.replace("#","_")).css("display","");
				if(newVal == "#div_1"){// 订单
					if(vm.formUrl ==='#'){
						initFlag = true;
					}
					vm.formUrl = baseUrl + "zzjmes/report/pmdReqReachReport";
					vm.report_type = "order";
					vm.columnModel = [	   
						{label: '订单编号', name: 'order_no',index:"order_no", width: "120",align:"center",sortable:false,editable:false},
						{label: '订单描述', name: 'order_desc',index:"order_desc", width: "300",align:"center",sortable:false,editable:false},
			            {label: '需求种类', name: 'req_sum',index:"req_sum", width: "120",align:"center",sortable:false,editable:false,
							formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdSearch("'+row.order_no+'") title="下料明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
						}},
			            /*{label: '计划种类', name: 'plan_sum',index:"plan_sum", width: "120",align:"center",sortable:false,editable:false,},*/
			            {label: '完成种类', name: 'prod_sum',index:"prod_sum", width: "120",align:"center",sortable:false,editable:false,						
							formatter:function(val, obj, row, act){
							var actions = [];
							var link = '<a onClick=vm.pmdOutputReachReport("'+row.order_no+'","ok") title="产量达成明细" style="cursor: pointer;">'+val+'</a>';
							actions.push(link);
						    return actions.join('');
			            }},
			            {label: '欠产种类', name: 'un_prod_sum',index:"un_prod_sum", width: "120",align:"center",sortable:false,editable:false,	
			            	formatter:function(val, obj, row, act){
							var actions = [];
							var link = '<a onClick=vm.pmdOutputReachReport("'+row.order_no+'","ng") title="产量达成明细" style="cursor: pointer;">'+val+'</a>';
							actions.push(link);
						    return actions.join('');
			            }},
			            {label: '达成率', name: 'reach',index:"reach", width: "150",align:"center",sortable:false,editable:false,},
			        ];
				}
				if(newVal == "#div_2"){// 使用车间
					vm.formUrl = baseUrl + "zzjmes/report/pmdReqReachReport";
					vm.report_type = 'use_workshop';
					vm.columnModel = [	   
						{label: '订单编号', name: 'order_no',index:"order_no", width: "120",align:"center",sortable:false,editable:false},
						{label: '订单描述', name: 'order_desc',index:"order_desc", width: "300",align:"center",sortable:false,editable:false},
						{label: '使用车间', name: 'use_workshop',index:"use_workshop", width: "100",align:"center",sortable:false,editable:false},
						{label: '需求种类', name: 'req_sum',index:"req_sum", width: "100",align:"center",sortable:false,editable:false,
							formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdSearch("'+row.order_no+'") title="下料明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
						}},
			            /*{label: '计划种类', name: 'plan_sum',index:"plan_sum", width: "100",align:"center",sortable:false,editable:false,},*/
			            {label: '完成种类', name: 'prod_sum',index:"prod_sum", width: "100",align:"center",sortable:false,editable:false,
							formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdOutputReachReport("'+row.order_no+'","ok") title="产量达成明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
				        }},
			            {label: '欠产种类', name: 'un_prod_sum',index:"un_prod_sum", width: "100",align:"center",sortable:false,editable:false,
			            	formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdOutputReachReport("'+row.order_no+'","ng") title="产量达成明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
				        }},
			            {label: '达成率', name: 'reach',index:"reach", width: "100",align:"center",sortable:false,editable:false,},
			        ];
				}
				if(newVal == "#div_3"){// 工段
					vm.formUrl = baseUrl + "zzjmes/report/pmdReqReachReport";
					vm.report_type = 'section';
					vm.columnModel = [	   
						{label: '订单编号', name: 'order_no',index:"order_no", width: "120",align:"center",sortable:false,editable:false},
						{label: '订单描述', name: 'order_desc',index:"order_desc", width: "300",align:"center",sortable:false,editable:false},
						{label: '工段', name: 'section',index:"section", width: "150",align:"center",sortable:false,editable:false},
						{label: '需求种类', name: 'req_sum',index:"req_sum", width: "100",align:"center",sortable:false,editable:false,
							formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdSearch("'+row.order_no+'") title="下料明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
						}},
			            /*{label: '计划种类', name: 'plan_sum',index:"plan_sum", width: "100",align:"center",sortable:false,editable:false,},*/
			            {label: '完成种类', name: 'prod_sum',index:"prod_sum", width: "100",align:"center",sortable:false,editable:false,
							formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdOutputReachReport("'+row.order_no+'","ok") title="产量达成明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
				        }},
			            {label: '欠产种类', name: 'un_prod_sum',index:"un_prod_sum", width: "100",align:"center",sortable:false,editable:false,
			            	formatter:function(val, obj, row, act){
								var actions = [];
								var link = '<a onClick=vm.pmdOutputReachReport("'+row.order_no+'","ng") title="产量达成明细" style="cursor: pointer;">'+val+'</a>';
								actions.push(link);
							    return actions.join('');
				        }},
			            {label: '达成率', name: 'reach',index:"reach", width: "100",align:"center",sortable:false,editable:false,},
			        ];
				}
				vm.$nextTick(function(){
					if(initFlag){
						vm.showTable();
					}else{
						vm.query();
					}
					
				})
				
			}
		},
		werks : {
		  handler:function(newVal,oldVal){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_PMD_REQ_REACH_REPORT',
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					if(response.data && response.data.length &&  response.data.length>0){
						vm.workshop=response.data[0]['code'];
						$.ajax({
							url:baseURL+"masterdata/getUserLine",
							dataType : "json",
							type : "post",
							data : {
								"WERKS":vm.werks,
								"WORKSHOP": vm.workshop,
								"MENU_KEY":'ZZJMES_PMD_REQ_REACH_REPORT',
							},
							async: true,
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
						vm.workshop = '';
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
				async: true,
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
						"MENU_KEY":'ZZJMES_PMD_REQ_REACH_REPORT',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(response.data && response.data.length &&  response.data.length>0){
							vm.line = response.data[0]['code'];
						}else{
							vm.line = '';
						}
					}
				});	
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
          	if($("#start_date").val() != '' && $("#end_date").val() != '' && ($("#end_date").val()-$("#start_date").val() >30 )){
           		js.showErrorMessage('计划起始日期范围不能超过30天！');
        		return false;
        	}
			jQuery(vm.table_id).GridUnload();
			$("#searchForm").attr("action",vm.formUrl);
			$(vm.table_id).dataGrid({
				searchForm: $("#searchForm"),
				columnModel: vm.columnModel,	
				viewrecords: true,
		        shrinkToFit:false,
		        showCheckbox:false,
		        multiselect: false,
				rowNum: 15,  
				rowList : [15,50,100,200],
			});
		},
		enter:function(e){
			vm.query();
		},
		//业务初始化表格
		showTable:function(mydata){
			$("#dataGrid_div_1").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
					{label: '订单编号', name: 'order_no',index:"order_no", width: "120",align:"center",sortable:false,editable:false},
					{label: '订单描述', name: 'order_desc',index:"order_desc", width: "300",align:"center",sortable:false,editable:false},
		            {label: '需求种类', name: 'req_sum',index:"req_sum", width: "120",align:"center",sortable:false,editable:false,},
		            /*{label: '计划种类', name: 'plan_sum',index:"plan_sum", width: "120",align:"center",sortable:false,editable:false,},*/
		            {label: '完成种类', name: 'prod_sum',index:"prod_sum", width: "120",align:"center",sortable:false,editable:false,},
		            {label: '欠产种类', name: 'un_prod_sum',index:"un_prod_sum", width: "120",align:"center",sortable:false,editable:false,},
		            {label: '达成率', name: 'reach',index:"reach", width: "150",align:"center",sortable:false,editable:false,},
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
				rowList : [15,30,100,200],
				cellsubmit:'clientArray',
		    });	
		},
		pmdSearch: function (order_no) {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">下料明细查询</i>',
			'zzjmes/product/pmdSearch.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+'&line='+vm.line, 
					true, true);
		},
		pmdOutputReachReport: function (order_no,status) {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">订单达成明细</i>',
			'zzjmes/report/pmdOutPutReachReport.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+
			'&line='+vm.line+'&status='+status+'&prod_process='+vm.prod_process, 
					true, true);
		},
	}
});

$(function () {
	//设置默认日期，当前日期往前一个月
	var end_date = new Date().toLocaleDateString().replace(/\//g,"-");
	var start_date = GetDateStr(-30);//获取退后一个月的日期
	$("#start_date").val(start_date);
	$("#end_date").val(end_date);
	/**
	 * 切换Tab
	 */
	$(document).on("click","#myTab>li",function(e){
		var tag_name=$(e.target).prop("tagName");
		console.info("点击的元素:"+tag_name)
		if(tag_name=="I"){
			return false;
		}
		if(tag_name=="A"){
			vm.tab_id=$(e.target).attr("href")
		}else{
			vm.tab_id=$(e.target).children("a").attr("href")
		}
		
	})
});

function fn_cb_getOrderInfo_search(order){
	vm.order_no = order.order_no;
}

function GetDateStr(AddDayCount) { 
	   var dd = new Date();
	   dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
	   var y = dd.getFullYear(); 
	   var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
	   var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0
	   return y+"-"+m+"-"+d; 
}
