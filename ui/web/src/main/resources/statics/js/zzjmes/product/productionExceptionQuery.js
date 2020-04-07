
var vm = new Vue({
	el:'#rrapp',
	data:{
		user:{},
		mydata:[],
		workshoplist:[],
		linelist:[],
		processlist:[],
		lastrow:'',
		lastcell:'',
		check:true
	},
	created:function(){
		this.getUser();
		getWorkShopList();
	},
	methods: {
		getUser: function(){
			$.getJSON("../../sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
		query: function () {
			$("#searchForm").submit();
		},
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,null)	
		},
		onWerksChange:function(event) {
			getWorkShopList()
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onWorkshopChange:function(event) {
			getLineList()
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onLineChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onOrderChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		exceptionConfirm: function() {
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
			var arrList = new Array();
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			
			var trs=$("#dataGrid").children("tbody").children("tr");
			var isConfirm = false;
			$.each(trs,function(index,tr){
				var td_check = $(tr).find("td").eq(1).find("input");
				if(index > 0){
					var ischecked=$(td_check).is(":checked");
					if(ischecked){
						if('' != $(tr).find("td").eq(9).text().trim()){
							isConfirm = true;
						}
					}
				}
			})
			if(isConfirm){
				alert("已经处理的异常不能重复处理！");
				return false;
			}
			
			$("#exception_ids").val(ids)
			layer.open({
				type : 1,
				offset : '150px',
				skin : 'layui-layer-molv',
				title : "生产异常处理",
				area : [ '500px', '200px' ],
				shade : 0.6,
				shadeClose : true,
				content : jQuery("#addLayer"),
				btn : [ '确定' ,	'取消'],
				btn1 : function(index) {
					return vm.exceConfirm()
				},
				btn2 : function(index) {
					console.log('btn2')
					layer.close(index);
				}
			});
		},
		exceConfirm : function() {
			if(""===$("#solution").val()){
				alert("处理方案不能为空！")
				return false
			}
			$.ajax({
				url:baseURL+"zzjmes/productionException/exceptionConfirm",
				dataType : "json",
				type : "post",
				data : {
					"exc_ids":$("#exception_ids").val(),
					"solution":$("#solution").val(),
				},
				async: true,
				success: function (response) {
					alert("处理成功！")
					vm.query()
				}
			})
			
		},
		exportExcel : function() {
			var trs=$("#dataGrid").children("tbody").children("tr");
			console.log('-->trs.length = ',trs.length);
			if(trs.length === 1){
				alert("请先查询到数据再进行导出，导出数据不分页")
				return false;
			}
			$("#export_werks").val($("#search_werks").val())
			$("#export_order").val($("#search_order").val())
			$("#export_workshop").val($("#search_workshop").val())
			$("#export_line").val($("#search_line").val())
			$("#export_product_no").val($("#search_product_no").val())
			$("#export_exception_type_code").val($("#search_exception_type_code").val())
			$("#export_reason_type_code").val($("#search_reason_type_code").val())
			$("#export_solution").val($("#search_solution").val())
			$("#exportForm").submit();
		},
		viewDrawing: function(event,material_no){
			getPdf($("#search_werks").val(),material_no);
		}
	},
	watch: {
		
	}
});
$(function () {
	fun_ShowTable();
});
function fun_ShowTable(){
	
	$("#dataGrid").dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
    		{ label: '生产工厂', name: 'werks_name', align:'center', index: 'werks_name', width: 100 },
    		{ label: '订单编号', name: 'order_no',align:'center',  index: 'order_no', width: 80 }, 		
    		{ label: '零部件号', name: 'product_no', align:'center', index: 'product_no', width: 150 }, 			
    		{ label: '名称', name: 'zzj_name', align:'center', index: 'zzj_name', width: 150 }, 
    		{ label: '图号', name: 'material_no', align:'center',  index: 'material_no', width: 150 ,
    			formatter:function(val, obj, row, act){
    				return '<a href="#" title="查看图纸" onClick="vm.viewDrawing(event,\''+val+'\')">'+val+'</a>'
    			},
    		}, 	
    		{ label: '异常类型', name: 'exception_type_name', align:'center', index: 'exception_type_name', width: 100 }, 			
    		{ label: '异常原因', name: 'reason_type_name', align:'center',  index: 'reason_type_name', width: 150 }, 		
    		{ label: '处理方案', name: 'solution', align:'center',  index: 'solution', width: 150 }, 				
    		/*{ label: '处理措施', name: 'solution', align:'center',  index: 'solution', width: 90,
    			formatter:function(val){	//处理措施 0忽略、1异常、2停线
            		if("0"==val){return "忽略";}
            		else if("1"==val){return "异常";}
            		else if("2"==val){return "停线";}
            		else{return "暂未处理";}
            	}
    		}, */			
    		{ label: '录入人', name: 'editor', align:'center', index: 'editor', width: 150 },
    		{ label: 'editor', name: 'editor', align:'center', index: 'editor', width: 50 ,hidden:true},
    		{ label: 'PMD_COUNT', name: 'PMD_COUNT', align:'center', index: 'PMD_COUNT', width: 50 ,hidden:true},
    	],
		shrinkToFit: false,
        width:1900,
    	viewrecords: false,
        showRownum: true, 
        rowNum:15,
        rownumWidth: 15, 
        rowList : [ 15,50,200 ],
        orderBy:"creat_date desc",
        multiselect: false,
        showCheckbox:true
    });
}
function getWorkShopList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
  	  data:{
  		  "WERKS":$("#search_werks").val(),
  		  "MENU_KEY":"ZZJMES_PMD_MANAGE",
  	  },
  	  success:function(resp){
  		  vm.workshoplist = resp.data;
  		  getLineList(vm.workshoplist[0].code)
  		  getProcessList(vm.workshoplist[0].code)
  	  }
    })
}

function getLineList(workshop){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserLine",
  	  data:{
  		  "WERKS": $("#search_werks").val(),
  		  "WORKSHOP": $("#search_workshop").val()?$("#search_workshop").val():workshop,
  		  "MENU_KEY": "ZZJMES_PMD_MANAGE",
  	  },
  	  success:function(resp){
  		 vm.linelist = resp.data;
  	  }
    })
}
function getProcessList(workshop){
	$.ajax({
		url : baseUrl + "masterdata/getWorkshopProcessList",
		data : {
			"WERKS" : $("#search_werks").val(),
			"WORKSHOP" : $("#search_workshop").val() ? $("#search_workshop").val() : workshop,
			"MENU_KEY" : "ZZJMES_PMD_MANAGE",
		},
		success : function(resp) {
			vm.processlist = resp.data;
		}
	})
}
