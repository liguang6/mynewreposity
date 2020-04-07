var lastrow,lastcell,lastcellname;

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
		savePlan:function(){
			$("#dataGrid").jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#dataGrid").jqGrid('getRowData');
			var record_list =[];
			var save_flag=true;
			var product_plan_date_list =[];
			
			$.each(rows,function(i,row){
				var rownum = $("#dataGrid").jqGrid("getCell",row.id,0);
				var check_key = row.product_code+"_"+row.plan_date;
				if(row.plan_qty>0 && row.plan_workgroup==""){
					js.showErrorMessage("第"+rownum+"行未输入计划班组！",10*1000);
					save_flag=false;
					return false;
				}
				if(row.plan_qty>0 && row.plan_date==""){
					js.showErrorMessage("第"+rownum+"行未输入计划日期！",10*1000);
					save_flag=false;
					return false;
				}			
				/**
				 * 判断同一个产品是否存在计划日期相同的数据
				 */
				if(product_plan_date_list.indexOf(check_key)>=0){
					js.showErrorMessage("产品("+row.product_name+") 存在相同计划日期数据！" ,10*1000);
					save_flag=false;
					return false;
				}
				product_plan_date_list.push(check_key) ;
				
				if(row.plan_id || row.plan_qty>0){
					record_list.push(row)
				}
			})
			if(!save_flag){
				return false;
			}
			//console.info(JSON.stringify(record_list))
			if(record_list.length==0){
				js.showErrorMessage("无需要保存的数据！",10*1000);
				save_flag=false;
				return false;
			}
			if(!save_flag){
				return false;
			}
			
			$.ajax({
				url:baseURL+"bjmes/product/pe/saveProductPlan",
				dataType : "json",
				type : "post",
				data : {
					"dataList":JSON.stringify(record_list),
				},
				async: true,
				success: function (response) { 
					if(response.code=='500'){
						js.showErrorMessage(response.msg,10*1000);
					}else{
						js.showMessage("保存成功!");
						vm.query();
					}
				}
			});	
			
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
			{label: '操作', name: 'EMPTY_COL', index: '', align:"center",width: 50, sortable:false,formatter:function(val, obj, row, act){
					var actions = [];
					actions.push('<a  href="#" onclick="copyItem('+row.id+')" title="复制" ><i class="fa fa-copy fa-lg" style=\"color:red;\"></i></a>');
					/*console.info("user: "+vm.user.STAFF_NUMBER)
					if(row.editor.indexOf(vm.user.STAFF_NUMBER)>=0){
						actions.push('<a  href="#" onClick="copyItem('+row.id+')" title="复制" ><i class="fa fa-copy fa-lg" style=\"color:red;\"></i></a>');
					}		*/			
					return actions.join("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				},unformat:function(cellvalue, options, rowObject){
				return "";
			}}, 
			{label: '工厂', name: 'werks_name',index:"werks_name", width: "110",align:"center",sortable:false},
            {label: '车间', name: 'workshop_name',index:"workshop_name", width: "110",align:"center",sortable:false,}, 
            {label: '订单', name: 'order_desc',index:"order_desc", width: "150",align:"center",},
            {label: '产品名称', name: 'product_name',index:"product_name", width: "170",align:"center",sortable:false,},
            {label: '产品类别', name: 'product_type_name',index:"product_type_name", width: "100",align:"center",sortable:false,},
            {label: '<span style="color:blue">计划数量</span>', name: 'plan_qty',index:"plan_qty", width: "80",align:"center",sortable:false,editable:true,editrules:{ required:true,number:true},  formatter:'integer',},
            {label: '<span style="color:blue">计划班组</span>', name: 'plan_workgroup',index:"plan_workgroup", width: "120",align:"center",sortable:false,editable:true,edittype:'select',},
            {label: '<span style="color:blue">计划日期</span>', name: 'plan_date',index:"plan_date", width: "90",align:"center",sortable:false,editable:true,editrules:{ required:true,date:true}, editoptions:{
    	        dataInit:function(e){
    	            $(e).click(function(e){
    	            	WdatePicker({dateFmt:'yyyy-MM-dd'})
    	            });
    	        }
    	    }},       
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
			
			var rows=$("#dataGrid").jqGrid('getRowData');
			var cur_date = getCurDate();
			rows.forEach(function(row,index,arr){
				//console.info(JSON.stringify(row))
				$("#dataGrid").jqGrid('setCell',row.id, "ready_plan_qty", row.plan_qty);	
				$("#dataGrid").jqGrid('setCell',row.id, "plan_date", row.plan_date||cur_date,'editable-cell');								
			})
		
		},
		 beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
				lastcellname=cellname;
			},
		 formatCell : function(rowid, cellname, value, iRow, iCol) {
			 if(cellname == "plan_workgroup"){
				 var list = [];
				 vm.workgroup_list.forEach(w=>{
					 list.push(w.CODE+":"+w.NAME);
				 })
				 var option_str = list.join(";");
				 var row = $('#dataGrid').jqGrid('getRowData',rowid);
					$('#dataGrid').jqGrid('setColProp', 'plan_workgroup', {
						editoptions : {
							value : option_str
						}
					});
			 }
		 },
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			/**
			 * 需校验计划数量≤订单需求数量（订单数量×单车用量）-已计划数量，‘工厂+车间+线别+订单+产品+计划班组+计划日期’唯一，
			 * 根据计划信息（订单+工厂+车间+产品+计划数量）生成产品自编号且产品自编号生成总数≤订单产品需求数（订单数量×单车用量），并保存计划ID，
			 * 当计划数量改小保存时系统不再生成自编号，当计划数量改大保存时需生成改大部分数量的产品自编号
			 */
			if(cellname=="plan_qty"){
				var total_plan_qty =isNaN(Number(row.total_plan_qty))?0:Number(row.total_plan_qty);
				var require_qty =isNaN(Number(row.require_qty))?0:Number(row.require_qty); 
				console.info("require_qty:"+require_qty)
				console.info("total_plan_qty:"+total_plan_qty)
				if(require_qty-total_plan_qty+Number(row.ready_plan_qty)-Number(value)<0){
					js.showErrorMessage("计划数量不能超出（订单需求数量-累计计划数量）"+(require_qty-total_plan_qty+Number(row.ready_plan_qty)));
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.ready_plan_qty,'editable-cell');
				}
			}
			
		},

	});
}

function copyItem(id){
	$("#dataGrid").jqGrid("saveCell", lastrow, lastcell);
	var c_row = $("#dataGrid").jqGrid('getRowData',id);
	var detail =c_row;

	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    detail.id = rowid;
    detail.plan_date ="&nbsp;";
    detail.plan_qty="0";
    detail.plan_id="&nbsp;";
    detail.ready_plan_qty="0";
    
    $("#dataGrid").jqGrid("addRowData", rowid, detail, "first"); 
}
