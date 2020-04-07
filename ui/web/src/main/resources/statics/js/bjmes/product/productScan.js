var lastrow,lastcell,lastcellname;
var last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		product_no: '',
		product_code :'',
		node_name: '',
		node_code :'',
		node_type :'上线',
		saveFlag:false,
		product_werks :{}, //产品所属工厂，校验权限
		product_workshop :{}, //产品所属车间，校验权限
		process_code:"",//工位
		scan_node :{},
		user:{},
		order_no:"",
		order_desc:"",
		product_info:"",
		
    },
    watch:{
    	/**werks : {
			handler:function(newVal,oldVal){	
				vm.$nextTick(function(){
					vm.workshop_list=vm.getUserWorkshop(newVal);
					vm.workshop=vm.workshop||"";
				})
				
		  }
		},**/
		workshop:{
			handler:function(newVal,oldVal){
				vm.$nextTick(function(){
					vm.getWorkshopLine();
			  })
				
			}
		},
		
    },
    methods:{
		getUser: function(){
			$.getJSON("/web/sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
		getUserWorkshop : function(werks){
			var list =[]
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks,
					"MENU_KEY":"BJMES_PRODUCT_PLAN_QUERY",
				},
				async: false,
				success: function (response) { 
					list =response.data;
				}
			});	
			return list;
		},
		getWorkshopLine : function(){
			$.ajax({
				url:baseURL+"masterdata/getWorkshopLineList",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":vm.werks,
					"WORKSHOP":vm.workshop
					//"MENU_KEY":"BJMES_PRODUCT_PLAN_QUERY",
				},
				async: false,
				success: function (response) { 
					vm.line_list=response.data;
					vm.line=vm.line==null?(response.data[0]==null?"":response.data[0]['CODE']):vm.line;
				}
			});
		},
		getScanNode : function(){

			$.ajax({
				url:baseURL+"bjmes/product/pe/getScanProductInfo",
				dataType : "json",
				type : "post",
				data : {
					"product_no":vm.product_no,	
					"product_code":vm.product_code				
				},
				async: false,
				success: function (response) { 
					if (response.code=='0') {
						vm.scan_node = response.scan_node;
						vm.node_code=response.scan_node.node_code;
						vm.node_name=response.scan_node.node_name;
						vm.node_type=response.scan_node.node_type=="online"?"上线":"下线";
						vm.process_code = response.scan_node.process_code;
						vm.line =  response.scan_node.line;
						
						showSubParts(response.subParts);
						var last_scan_info = response.last_scan_info==null?"":response.last_scan_info;
						vm.saveFlag=true;
						vm.product_info = vm.product_no+" "+ response.product_info.order_desc +" "+
						response.scan_node.product_name+" "+(last_scan_info==""?"":last_scan_info.node_name)+" "+
						(last_scan_info==""?"":(last_scan_info.scan_type=="online"?"上线":"下线"));
					}else{
						js.showErrorMessage(response.msg,10*1000);
						vm.product_no="";

					}

					
				}
			});
		},
		getProductNoFuzzy : function () {
			vm.order_no="";
			getBjPDProductSelect("#product_no",function(product){
				var flag = true;
				vm.product_werks.werks = product.werks;
				vm.product_werks.werks_name = product.werks_name;
				vm.werks =  product.werks;
				vm.product_workshop.workshop_name = product.workshop_name;
				vm.product_workshop.workshop = product.workshop;
				vm.product_code = product.product_code;
				vm.product_no = product.product_no;
				vm.order_no=product.order_no;
				
				vm.$nextTick(function() {
					var e = $.Event('keydown');
					e.keyCode = 13; 
					$("#product_no").trigger(e);
				})
				
				
			});
		},
    	scan : function(){
			$(vm.table_id).jqGrid("saveCell", lastrow, lastcell);
			var scan_info ={};
			scan_info.scanner=vm.user.FULL_NAME;
			scan_info.node_code=vm.node_code;
			scan_info.node_name=vm.node_name;
			scan_info.werks=vm.product_werks.werks;
			scan_info.werks_name=vm.product_werks.werks_name;
			scan_info.workshop_name=vm.product_workshop.workshop_name;
			scan_info.workshop = vm.product_workshop.workshop;
			scan_info.line=vm.line||"";
			scan_info.line_name = $("#line :selected").text()||"";
			scan_info.online_plan_node_flag = vm.scan_node.online_plan_node_flag;
			scan_info.offline_plan_node_flag = vm.scan_node.offline_plan_node_flag
			scan_info.scan_type = vm.scan_node.scan_type;
			var subParts =$("#dataGrid").jqGrid("getRowData");
			$.each(subParts,function (i,p) {
				p.scanner=vm.user.FULL_NAME;
				p.node_code=vm.node_code;
				p.node_name=vm.node_name;
				p.werks=vm.product_werks.werks;
				p.werks_name=vm.product_werks.werks_name;
				p.workshop = vm.product_workshop.workshop;
				p.workshop_name=vm.product_workshop.workshop_name;
				p.line=vm.line||"";
				p.line_name = $("#line :selected").text();
			})

    		$.ajax({
				url:baseURL+"bjmes/product/pe/saveProductScan",
				dataType : "json",
				type : "post",
				data : {
					"product_no":vm.product_no,	
					"product_code":vm.product_code,
					"order_no" :vm.order_no,
					"subParts":JSON.stringify(subParts),
					"scan_info":JSON.stringify(scan_info),				
				},
				async: false,
				success: function (response) { 
					if (response.code=='0') {
						js.showMessage(response.msg)
						vm.reset();
					}else{
						js.showErrorMessage(response.msg,10*1000);
						//vm.product_no="";

					}

					
				}
			});
    	},
    	reset : function(){
			$("#dataGrid").jqGrid('clearGridData');
			vm.product_code=""
			vm.product_no=""
			vm.saveFlag=false
			vm.product_info =""
    	},
	},
	created :function() {
		this.werks = $("#werks").find("option").first().val();
		this.getUser();
	}
})   

$(function(){	

	$(document).on("keydown","#product_no",function(e){
		if(e.keyCode == 13){
			vm.prouct_no = $(e.target).val()
			var flag =true;
			if(vm.product_code ==null || vm.product_code.trim()==""){
				flag =false;
			}
			if (!flag) {
				return false;
			}
			flag =validatePDWerks(vm.product_werks);
			if (!flag) {
				return false;
			}
			flag = validatePDWorkshop(vm.product_workshop);
			if (!flag) {
				return false;
			}
			//console.info("产品编号查询")
			//扫描产品编码获取待扫描节点;子组件列表等信息
			
			vm.getScanNode();
			$(e.target).select();
		}
		
	})
})


function doScan(ele){
	var url = "../../doScan";
	last_scan_ele = ele;
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
	//alert(last_scan_ele)
}

function funFromjs(result){ 
	var e = $.Event('keydown');
    e.keyCode = 13; 	
	var mat = JSON.parse(result);	
	vm.product_werks = mat.werks_name;
	vm.order_no= mat.order_no;
	vm.product_workshop = mat.workshop_name;	
	$("#"+last_scan_ele).val(mat.product_no);
	$("#"+last_scan_ele).trigger(e);

}

var showSubParts= (gridData)=>{
	var data = $("#dataGrid").data("dataGrid");
	if(data){
		$("#dataGrid").jqGrid('GridUnload');
	}
	//gridData= $("#dataGrid").data("dataGrid");
	/*if (gridData) {
		$(vm.table_id).jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		return false;
	}*/
	var columns = [ {
		label:'id',name:'id', index: 'id',key: true ,hidden:true,formatter:function(val, obj, row, act){
		//console.info(row)
		return row.id
	}}, {
		label : '组件代码',
		name : 'product_code',
		align : "center",
		index : 'product_code',
		sortable:false,
		width : 200
	}, {
		label : '组件名称 ',
		name : 'product_name',
		align : "center",
		index : 'product_name',
		sortable:false,
		width : 300
	}, {
		label : '<span style="color:blue">组件编号 </span> ',
		formatter:function(val, obj, row, act){
			var input_id = "key_parst_no_"+row.id;
			html = val||"";
			html+="<i class=\"ace-icon fa fa-barcode black fa-2x btn_scan\" "+
			"style=\"cursor: pointer;float:right;\" onclick=\"doScan('"+input_id+"')\"> </i>";
			
			return html
		},
		unformat:function(cellvalue, options, rowObject){
			return cellvalue||"";
		},
		name : 'key_parts_no',
		align : "center",
		index : 'key_parts_no',
		sortable:false,
		width : 300,
		editable:true,
	},
	{
		label:'key_parts_id',
		name:'key_parts_id',
		index:'key_parts_id',
		hidden:true,
	},
	{
		label:'key_parts_no_old',
		name:'key_parts_no_old',
		index:'key_parts_no_old',
		hidden:true,
	},
	];
	
	dataGrid = $("#dataGrid").dataGrid({
		datatype:'local',
		//searchForm : $("#searchForm"),
		data:gridData,
		colModel : columns,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
	    viewrecords: false,
	    showRownum:true,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
		//dataid:'id',
		multiselect: false,
		loadComplete : function(data) {
			js.closeLoading();
			$(".btn").attr("disabled", false);
			if (data.code == 500) {
				js.showErrorMessage(data.msg, 1000 * 10);
				return false;
			}
			var rows =  $("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){
				$("#dataGrid").jqGrid('setCell', row.id, "key_parts_no_old", row.key_parts_no);
			})
		},	
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		afterSaveCell: function(rowid, cellname, value, iRow, iCol){
			var row =  $(vm.table_id).jqGrid('getRowData', rowid);	
			if (value == vm.product_no) {
				js.showErrorMessage("抱歉，组件编号不能与产品编号相同！")
				$("#dataGrid").jqGrid('setCell', row.id, "key_parts_no", row.key_parts_no_old,"editable-cell");
			}
			//检查输入的组件编号是否已经绑定
			var flag = checkKeyParts(value);
			if(!flag){
				$("#dataGrid").jqGrid('setCell', row.id, "key_parts_no", row.key_parts_no_old,"editable-cell");
			}
		},
		
	});
}

var validatePDWerks =(product_werks)=>{
	let flag = false;
	$.each($("#werks").find("option"),function (i,w) {
		if ($(w).val() == product_werks.werks) {
			flag = true;
			//vm.werks = werks;
			return false;
		}
	})
	if (!flag) {
		js.showErrorMessage("抱歉，您无权限操作工厂（"+product_werks.werks_name+"）的产品！");
	}
	return flag
}

var validatePDWorkshop =(product_workshop)=>{
	let flag = false;
	vm.workshop_list = vm.getUserWorkshop(vm.werks);
	//console.info(JSON.stringify(list))
	$.each(vm.workshop_list,function (i,w) {
		console.info("workshop:"+w.NAME)
		if (w.NAME == product_workshop.workshop_name) {
			vm.workshop = w.CODE;
			flag = true;
			return false;
		}
	})
	if (!flag) {
		js.showErrorMessage("抱歉，您无权限操作车间（"+product_workshop.workshop_name+"）的产品！");
	}
	return flag	
}

var checkKeyParts =(key_parst_no)=>{
	let flag =false;
	$.ajax({
		url:baseURL+"bjmes/product/pe/getProductByKeyparts",
		dataType : "json",
		type : "post",
		data : {
			"key_parts_no":key_parst_no,	
			"product_no":vm.product_no,
			"werks":vm.werks,
			"workshop":vm.workshop,
			"order_no":vm.order_no
		},
		async: false,
		success: function (response) { 
			if (response.code=='0') {
				flag=true;
			}else{
				js.showErrorMessage(response.msg,10*1000);	
				flag = false;			
			}

			
		}
	});
	return flag;
}