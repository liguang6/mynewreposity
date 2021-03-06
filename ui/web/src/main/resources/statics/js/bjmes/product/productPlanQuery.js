$(function(){
	var now = new Date(); 
	var nowTime = now.getTime() ; 
	var day = now.getDay();
	var oneDayTime = 24*60*60*1000 ; 

	//显示周一
	var startTime = nowTime-(day-1)*oneDayTime;
	//显示周日
	var endTime =  nowTime +(7-day)*oneDayTime ; 
	//初始化日期时间
	var startday = new Date(startTime);
	var endday = new Date(endTime);
	$("#start_date").val(formatDate(startday));
	$("#end_date").val(formatDate(endday));
	
})

var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		workgroup_list :[],
		/*line:"",
		line_list :[],*/
		order_no: '',
		product_type_list:[],
		product_type_code:'',
		product_list:[],
		product_code:'',
		order_product_list :[],
    },
    watch : {
    	werks : {
  			handler:function(newVal,oldVal){
			
  				vm.getUserWorkshop(newVal);
  		  }
  		},
  		workshop:{
  			handler:function(newVal,oldVal){
  				vm.$nextTick(function(){
  					vm.getWorkgroup(vm.werks,newVal);
				})
  				
  			}
  		},
  		product_type_code : {
  			handler:function(newVal,oldVal){
  				vm.product_list=[];
  				var product_str="";
  				$.each(vm.product_type_list,function(index,item){
  					if(item.product_type_code == newVal){
  						product_str=item.product_str;
  					}
  				})
  				//alert(product_str)
  				if(product_str=="" || product_str ==undefined){
  					return false;
  				}
  				$.each(product_str.trim().split(","),function(index,item){
  					var p = {};
  					p.product_code=item.split(":")[0];
  					p.product_name=item.split(":")[1];
  					vm.product_list.push(p);
  				})
    		  }
  		},
    },
    methods : {
    	getOrderNoFuzzy:function(){
			getBjOrderNoSelect("#order_no",null,function(order){
				//console.info(JSON.stringify(order))
				vm.order_no = order.order_no;
				//获取订单产品列表
				vm.getOrderProducts();
				
			});
		},
		getUserWorkshop : function(werks){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks,
					"MENU_KEY":"BJMES_PRODUCT_PLAN_QUERY",
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					//vm.workshop="BJ";
					vm.workshop=vm.workshop||response.data[0]['code'];
				}
			});	
		},
		getWorkgroup :function(werks,workshop){
			$.ajax({
				url:baseURL+"masterdata/getWorkshopWorkgroupList",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks,
					"WORKSHOP":workshop,
				},
				async: true,
				success: function (response) { 
					vm.workgroup_list=response.data;
				}
			});	
		},
		getOrderProducts : function(){
			$.ajax({
				url:baseURL+"bjmes/product/pe/getOrderProducts",
				dataType : "json",
				type : "post",
				data : {
					"order_no":vm.order_no,
				},
				async: true,
				success: function (response) { 
					vm.product_type_list=[];
					vm.order_product_list=response.data;		
					let product_type_code_list = [];
					$.each(vm.order_product_list,function(index,item){
						if(product_type_code_list.indexOf(item.product_type_code)<0){
							product_type_code_list.push(item.product_type_code);
							vm.product_type_list.push(item);
						}
					})

				}
			});	
		},		
		query:function(){
			if(vm.workshop ==""){
				js.showErrorMessage("请选择车间！",10*1000);
				return false;
			}
			if(vm.order_no ==""){
				js.showErrorMessage("请输入订单！",10*1000);
				return false;
			}
			
			ajaxQuery();
		},
		
    },
    created : function(){
    	this.werks = $("#werks").find("option").first().val();
    },
})    


var ajaxQuery = function(){
	var data = $("#dataGrid").data("dataGrid");
	if (data) {
		$("#dataGrid").jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		/*$(vm.table_id).jqGrid("clearGridData");
		$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); */
	}
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		viewrecords: false,
		showRownum : true,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		shrinkToFit : true,
		autoScroll : true,
		columnModel: [
			{label:'id',name:'id', index: 'id',key: true ,hidden:true,},
			{label: '工厂', name: 'werks_name',index:"werks_name", width: "110",align:"center",sortable:false},
            {label: '车间', name: 'workshop_name',index:"workshop_name", width: "110",align:"center",sortable:false,}, 
            {label: '订单', name: 'order_desc',index:"order_desc", width: "150",align:"center",},
            {label: '产品名称', name: 'product_name',index:"product_name", width: "170",align:"center",sortable:false,},
            {label: '产品类别', name: 'product_type_name',index:"product_type_name", width: "100",align:"center",sortable:false,},
            {label: '计划数量', name: 'plan_qty',index:"plan_qty", width: "80",align:"center",sortable:false,},
            {label: '计划班组', name: 'plan_workgroup',index:"plan_workgroup", width: "120",align:"center",sortable:false,},
            {label: '计划日期', name: 'plan_date',index:"plan_date", width: "90",align:"center",sortable:false,},       
            {label: '维护者', name: 'editor',index:"editor", width: "75",align:"center",sortable:false,formatter:function(val, obj, row, act){          	
            	if(row.editor !=undefined && row.editor !='null'){
            		return row.editor;
            	}else if(row.creator !=undefined && row.creator !='null'){
            		return row.creator;
            	}else{
            		return "";
            	}
            }},
            {label: '维护时间', name: 'edit_date',index:"edit_date", width: "120",align:"center",sortable:false,formatter:function(val, obj, row, act){
            	if(row.edit_date !=undefined && row.edit_date !="null"){
            		return row.edit_date;
            	}else if(row.creat_date !=undefined && row.creat_date !="null"){
            		return row.creat_date;
            	}else{
            		return "";
            	}
            }},
            {label:'product_code',name:'product_code',index:'product_code',hidden:true},
            {label:'order_no',name:'order_no',index:'order_no',hidden:true},
            {label:'werks',name:'werks',index:'werks',hidden:true},
            {label:'workshop',name:'workshop',index:'workshop',hidden:true},
            {label:'plan_id',name:'plan_id',index:'plan_id',hidden:true},
            {label:'ready_plan_qty',name:'ready_plan_qty',index:'ready_plan_qty',hidden:true},
            {label:'require_qty',name:'require_qty',index:'require_qty',hidden:true},
            {label:'total_plan_qty',name:'total_plan_qty',index:'total_plan_qty',hidden:true},
		],	
		
        /*rowNum: 15,  
		rowList : [15,50 ,100],	*/
		loadComplete : function() {
			js.closeLoading();
			$(".btn").attr("disabled", false);
		
		},
		

	});
}