var mydata = [];
var moredata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		receive_workshop:"",
		workshop_list :[],
		order_no:'',
    },
	created:function(){	
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
					"MENU_KEY":'BJMES_PRODUCT_HANDOVER',
				},
				async: true,
				success: function (response) { 
					if(response.data!=null && response.data.length>0){
						vm.workshop_list=response.data;
						vm.workshop=response.data[0]['code'];
						vm.receive_workshop=response.data[0]['code'];
					}	
				}
			});	
		  }
		},
	},
	methods: {
		getOrderNoFuzzy:function(){
			getBjOrderNoSelect("#order_no",null,function(order){
				vm.order_no = order.order_no;
			});
		},
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
					{ label: '产品编号', name: 'product_no', align:'center', index: 'product_no', width: 100 }, 
		    		{ label: '产品名称', name: 'product_name', align:'center',  index: 'product_name', width: 120 }, 	
		    		{ label: '订单', name: 'order_no', align:'center',  index: 'order_no', width: 65 },
					{ label: '产品类别', name: 'demand_quantity', align:'center',  index: 'demand_quantity', width: 80 },
					{ label: '交接类型', name: 'handover_type_desc', align:'center',  index: 'handover_type_desc', width: 80 },
		    		{ label: '供应工厂', name: 'werks_name',align:'center',  index: 'werks_name', width: 80 }, 		
					{ label: '供应车间', name: 'workshop_name',align:'center',  index: 'workshop_name', width: 80 }, 		
					{ label: '接收车间', name: 'receive_workshop_name',align:'center',  index: 'receive_workshop_name', width: 80 },
					{ label: '供应班组', name: 'deliver_workgroup_name',align:'center',  index: 'deliver_workgroup_name', width: 80 }, 		
					{ label: '接收班组', name: 'receive_workgroup_name',align:'center',  index: 'receive_workgroup_name', width: 80 }, 		
					{ label: '交付人', name: 'deliver_user',align:'center',  index: 'deliver_user', width: 60 }, 
		    		{ label: '接收人', name: 'receive_user',align:'center',  index: 'receive_user', width: 70 }, 		
					{ label: '交付日期', name: 'deliver_date',align:'center',  index: 'deliver_date', width: 70}, 
					{ label: '维护人', name: 'creator',align:'center',  index: 'creator', width: 60 }, 
		    		{ label: '维护日期', name: 'create_date',align:'center',  index: 'create_date', width: 70 }, 
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				rowNum : 15,
		    });	
		},
		query:function(){
			if(vm.order_no==''){
				js.showErrorMessage("请输入订单号！");
				return;
			}
			if(vm.werks==''){
				js.showErrorMessage("请选择工厂！");
				return;
			}
			jQuery('#dataGrid').GridUnload();
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "bjmes/productHandover/queryPage");
			$("#dataGrid").dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
					{ label: '产品编号', name: 'product_no', align:'center', index: 'product_no', width: 110 }, 
		    		{ label: '产品名称', name: 'product_name', align:'center',  index: 'product_name', width: 120 }, 	
		    		{ label: '订单', name: 'order_no', align:'center',  index: 'order_no', width: 80 },
		    		{ label: '产品类别', name: 'product_type_name', align:'center',  index: 'product_type_name', width: 70 },
		    		{ label: '供应工厂', name: 'werks_name',align:'center',  index: 'werks_name', width: 70 }, 		
					{ label: '供应车间', name: 'workshop_name',align:'center',  index: 'workshop_name', width: 70 }, 		
					{ label: '接收车间', name: 'receive_workshop_name',align:'center',  index: 'receive_workshop_name', width: 70 }, 
					{ label: '供应班组', name: 'deliver_workgroup_name',align:'center',  index: 'deliver_workgroup_name', width: 80 }, 		
					{ label: '接收班组', name: 'receive_workgroup_name',align:'center',  index: 'receive_workgroup_name', width: 80 }, 				
					{ label: '交付人', name: 'deliver_user',align:'center',  index: 'deliver_user', width: 80 }, 
		    		{ label: '接收人', name: 'receive_user',align:'center',  index: 'receive_user', width: 80 }, 		
					{ label: '交付日期', name: 'deliver_date',align:'center',  index: 'deliver_date', width: 90 }, 
					{ label: '维护人', name: 'creator',align:'center',  index: 'creator', width: 70 }, 
		    		{ label: '维护日期', name: 'create_date',align:'center',  index: 'create_date', width: 90 }, 
		    	],
				shrinkToFit: false,
		        width:1900,
		    	viewrecords: false,
		        showRownum: true, 
		        rowNum:15,
		        rownumWidth: 30, 
		        rowList : [ 15,50,100 ],
		    });
	    },
	    addWorkgroupHandover:function(){
	    	js.addTabPage(null,'<i class="fa fa-add" style="font-size:12px">新增班组交接</i>',
	    			'bjmes/product/productHandoverAdd.html?handover_type=1',true, true);
	    },
        addWorkshopHandover:function(){
        	js.addTabPage(null,'<i class="fa fa-add" style="font-size:12px">新增车间交接</i>',
	    			'bjmes/product/productHandoverAdd.html?handover_type=2',true, true);
	    },
		exp:function(){
		    $("#searchForm").attr("id","exportForm");
			$("#exportForm").attr("action",baseUrl + "bjmes/productHandover/expData");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
	    }
	}
});

$(function () {
	var now = new Date(); 
	var nowTime = now.getTime() ; 
	var day = now.getDay();
	var oneDayTime = 24*60*60*1000 ; 
	//显示周一
	var MondayTime = nowTime - (day-1)*oneDayTime ; 
	//显示周日
	var SundayTime =  nowTime + (7-day)*oneDayTime ; 
	//初始化日期时间
	var monday = new Date(MondayTime);
	var sunday = new Date(SundayTime);
	$("#start_date").val(formatDate(monday));
	$("#end_date").val(formatDate(sunday));
	vm.showTable();
	
});
//调用摄像头扫二维码
function doScan(ele){
	var url = "../../doScan";
	last_scan_ele = ele;
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

function funFromjs(result){ 
	var e = $.Event('keydown');
    e.keyCode = 13; 	
	$("#"+last_scan_ele).val(result);
    $("#"+last_scan_ele).trigger(e);
}