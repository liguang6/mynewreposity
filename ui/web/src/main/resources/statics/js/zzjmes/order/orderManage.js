
var vm = new Vue({
	el:'#rrapp',
	data:{
		user:{},
		orOrder:{
		id:0,werks:'',werks_name:'',order_no:'',order_name:'',order_type_code:'',order_type_name:'',bus_type_code:'',internal_name:'',
		order_qty:'',order_area_code:'',order_area_name:'',productive_year:'',delivery_date:'',order_desc:'',relate_order:'',status:'00',
		sale_dept_code:'',sale_dept_name:'',memo:'',editor:'',edit_date:'',creator:'',creat_date:'',PMD_COUNT:0
		},
		pageData:[]
	},
	created:function(){
		this.getUser();
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
		delOrder : function(event,id) {
			console.log('-->update id = ' + id);
			$.each(vm.pageData, function(index, order) {
				if(order.id === id){
					vm.orOrder = vm.pageData[index]
				}
			})
			console.log('-->editOrder PMD_COUNT : ' + this.orOrder.PMD_COUNT + "|USER : " + this.user.STAFF_NUMBER)
			if(this.orOrder.PMD_COUNT > 0){
				alert("不能删除存在下料明细的订单")
				return false
			}
			if(this.orOrder.creator.substring(0,this.orOrder.creator.indexOf(':')) != this.user.STAFF_NUMBER){
				if('管理员' != this.user.FULL_NAME){
					alert("只有订单创建者/维护人/管理员才有权限删除订单")
					return false
				}
			}
			var r=confirm("确认要删除此订单吗!");
			if (r==true){
				$.ajax({
					url:baseURL+"zzjmes/order/delOrder",
					dataType : "json",
					type : "post",
					data : {
						"id":id
					},
					async: true,
					success: function (response) {
						alert("删除成功")
						$("#btnSearchData").click();
					}
				})
			}
		},
		addOrder : function() {
			this.orOrder.productive_year = $("#productive_year").val()
			this.orOrder.delivery_date = $("#delivery_date").val()	
			if(this.orOrder.werks === ''){
				alert('请选择工厂！')
				return false
			}
			if(this.orOrder.sale_dept_code === ''){
				alert('请选择销售部！')
				return false
			}
			if(this.orOrder.order_name === ''){
				alert('订单名称不能为空')
				return false
			}
			if(this.orOrder.order_type_code === ''){
				alert('订单类型不能为空')
				return false
			}
			if(this.orOrder.bus_type_code === ''){
				alert('请选择车型！')
				return false
			}
			if(isNaN(this.orOrder.order_qty)){
				alert('订单数量不能为空且应为数字！')
				return false
			}	
			if(this.orOrder.order_area_code === ''){
				alert('请选择订单区域！')
				return false
			}	
			if(this.orOrder.productive_year === ''){
				alert('请选择生产年份！')
				return false
			}
			if(this.orOrder.delivery_date === ''){
				alert('请选择订单交期！')
				return false
			}
			$.ajax({
				url:baseURL+"zzjmes/order/addOrder",
				dataType : "json",
				type : "post",
				data : {
					"werks":$("#werks").find("option:selected").text(),
					"werks_name":this.orOrder.werks,
					"sale_dept_code":this.orOrder.sale_dept_code,
					"sale_dept_name":$("#sale_dept").find("option:selected").text(),
					"order_name":this.orOrder.order_name,
					"order_type_code":this.orOrder.order_type_code,
					"order_type_name":$("#order_type").find("option:selected").text(),
					"bus_type_code":this.orOrder.bus_type_code,
					"internal_name":$("#bus_type").find("option:selected").text(),
					"order_qty":this.orOrder.order_qty,
					"order_area_code":this.orOrder.order_area_code,
					"order_area_name":$("#order_area").find("option:selected").text(),
					"productive_year":this.orOrder.productive_year,
					"delivery_date":this.orOrder.delivery_date,
					"order_desc":this.orOrder.order_desc,
					"relate_order":this.orOrder.relate_order,
					"status":"00",
					"memo":this.orOrder.memo,
				},
				async: true,
				success: function (response) {
					if(response.code == "0"){
						alert("新增订单成功！");
						$("#btnSearchData").click();
					}else{
						alert("新增订单失败！");
					}
				}
			})
		},
		editOrder: function() {
			//只有订单创建者/维护人才有权限修改订单
			console.log('-->editOrder id : ' + this.orOrder.creator.substring(0,this.orOrder.creator.indexOf(':')) + "|USER : " + this.user.STAFF_NUMBER)
			if(this.orOrder.creator.substring(0,this.orOrder.creator.indexOf(':')) != this.user.STAFF_NUMBER){
				if('管理员' != this.user.FULL_NAME){
					alert("只有订单创建者/维护人/管理员才有权限修改订单")
					return false
				}
			}
			this.orOrder.productive_year = $("#productive_year").val()
			this.orOrder.delivery_date = $("#delivery_date").val()	
			if(this.orOrder.werks === ''){
				alert('请选择工厂！')
				return false
			}
			if(this.orOrder.sale_dept_code === ''){
				alert('请选择销售部！')
				return false
			}
			if(this.orOrder.order_name === ''){
				alert('订单名称不能为空')
				return false
			}
			if(this.orOrder.order_type_code === ''){
				alert('订单类型不能为空')
				return false
			}
			if(this.orOrder.bus_type_code === ''){
				alert('请选择车型！')
				return false
			}
			if(isNaN(this.orOrder.order_qty)){
				alert('订单数量不能为空且应为数字！')
				return false
			}	
			if(this.orOrder.order_area_code === ''){
				alert('请选择订单区域！')
				return false
			}	
			if(this.orOrder.productive_year === ''){
				alert('请选择生产年份！')
				return false
			}
			if(this.orOrder.delivery_date === ''){
				alert('请选择订单交期！')
				return false
			}
			$.ajax({
				url:baseURL+"zzjmes/order/editOrder",
				dataType : "json",
				type : "post",
				data : {
					"id":this.orOrder.id,
					"order_name": this.orOrder.order_name,
					"order_qty": this.orOrder.order_qty,
					"productive_year": this.orOrder.productive_year,
					"delivery_date": this.orOrder.delivery_date,
					"order_desc": this.orOrder.order_desc,
					"relate_order": this.orOrder.relate_order,
					"order_area_code": this.orOrder.order_area_code,
					"order_area_name":$("#order_area").find("option:selected").text(),
					"status": this.orOrder.status,
					"sale_dept_code": this.orOrder.sale_dept_code,
					"sale_dept_name": $("#sale_dept").find("option:selected").text(),
					"memo": this.orOrder.memo,
					"editor": this.orOrder.editor,
					"edit_date": this.orOrder.edit_date
				},
				async: true,
				success: function (response) {
					if(response.code == "0"){
						alert("修改订单成功！");
						$("#btnSearchData").click();
					}else{
						alert("修改订单失败！");
					}
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
			$("#export_order_type_code").val($("#search_order_type_code").val())
			$("#export_bus_type_code").val($("#search_bus_type_code").val())
			$("#export_sale_dept_code").val($("#search_sale_dept_code").val())
			$("#export_status").val($("#search_status").val())
			$("#export_year").val($("#search_year").val())
			$("#exportForm").submit();
		},
		update : function(event,id) {
			console.log('-->update id = ' + id);
			$.each(vm.pageData, function(index, order) {
				if(order.id === id){
					vm.orOrder = vm.pageData[index]
					vm.orOrder.werks = vm.pageData[index].werks_name
				}
			})
			$("#productive_year").val(this.orOrder.productive_year)
			$("#delivery_date").val(this.orOrder.delivery_date)
			$("#werks").attr("disabled","disabled").css("background-color","#E5E5E5")
			$("#order_type").attr("disabled","disabled").css("background-color","#E5E5E5")
			$("#bus_type").attr("disabled","disabled").css("background-color","#E5E5E5")
			layer.open({
				type : 1,
				offset : '50px',
				skin : 'layui-layer-molv',
				title : "编辑订单",
				area : [ '600px', '410px' ],
				shade : 0,
				shadeClose : true,
				content : jQuery("#addLayer"),
				btn : [ '确定' ,	'取消'],
				btn1 : function(index) {
					return vm.editOrder()
				},
				btn2 : function(index) {
					console.log('btn2')
					layer.close(index);
					$("#btnConfirm").removeAttr("disabled");
				}
			});
		}
	},
	watch: {
	    'orOrder.order_name': function(newVal){
	    	if(this.orOrder.order_name != '' && this.orOrder.bus_type_code != '' && this.orOrder.order_qty != ''){
				this.orOrder.order_desc = this.orOrder.order_name + $("#bus_type").find("option:selected").text() + " " + this.orOrder.order_qty + "台"
			}else{
				this.orOrder.order_desc = ''
			}
	    },
	    'orOrder.bus_type_code': function(newVal){
	    	if(this.orOrder.order_name != '' && this.orOrder.bus_type_code != '' && this.orOrder.order_qty != ''){
				this.orOrder.order_desc = this.orOrder.order_name + $("#bus_type").find("option:selected").text() + " " +  this.orOrder.order_qty + "台"
			}else{
				this.orOrder.order_desc = ''
			}
	    },
	    'orOrder.order_qty': function(newVal){
	    	if(this.orOrder.order_name != '' && this.orOrder.bus_type_code != '' && this.orOrder.order_qty != ''){
				this.orOrder.order_desc = this.orOrder.order_name + $("#bus_type").find("option:selected").text() + " " +  this.orOrder.order_qty + "台"
			}else{
				this.orOrder.order_desc = ''
			}
	    },
	}
});
$(function () {
	$("#search_year").val(new Date().getFullYear())
	fun_ShowTable();
	
	$("#btnAdd").click(function () {
		$("#werks").removeAttr("disabled").css("background-color","#FFFFFF")
		$("#order_type").removeAttr("disabled").css("background-color","#FFFFFF")
		$("#bus_type").removeAttr("disabled").css("background-color","#FFFFFF")
		//vm.orOrder = {werks:'',sale_dept_code:'',order_type_code:'',bus_type_code:'',order_area_code:''};
		vm.orOrder = {id:0,werks:'',werks_name:'',order_no:'',order_name:'',order_type_code:'',order_type_name:'',bus_type_code:'',internal_name:'',
		order_qty:'',order_area_code:'',order_area_name:'',productive_year:'',delivery_date:'',order_desc:'',relate_order:'',status:'00',
		sale_dept_code:'',sale_dept_name:'',memo:'',editor:'',edit_date:'',creator:'',creat_date:''}
		layer.open({
			type : 1,
			offset : '50px',
			skin : 'layui-layer-molv',
			title : "新增订单",
			area : [ '600px', '410px' ],
			shade : 0,
			shadeClose : true,
			content : jQuery("#addLayer"),
			btn : [ '确定' ,	'取消'],
			btn1 : function(index) {
				return vm.addOrder()
			},
			btn2 : function(index) {
				console.log('btn2')
				layer.close(index);
				$("#btnConfirm").removeAttr("disabled");
			}
		});
	})
});
function fun_ShowTable(){
	
	$("#dataGrid").dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
    		{ label: '订单编号', name: 'order_no',align:'center',  index: 'order_no', width: 80 }, 		
    		{ label: '订单描述', name: 'order_desc', align:'center', index: 'order_desc', width: 160 }, 			
    		{ label: '订单区域', name: 'order_area_name', align:'center', index: 'order_area_name', width: 70 }, 
    		{ label: '销售部', name: 'sale_dept_name', align:'center',  index: 'sale_dept_name', width: 80 }, 	
    		{ label: '生产年份', name: 'productive_year', align:'center', index: 'productive_year', width: 70 }, 			
    		{ label: '订单交期', name: 'delivery_date', align:'center',  index: 'delivery_date', width: 90 }, 			
    		{ label: '订单状态', name: 'status', align:'center', index: 'status', width: 70 ,
    			formatter:function(val, obj, row, act){
    				vm.pageData.push(row)
    				switch(val) {
	    				case '00':
	    			        return '未生产'
	    			        break;
	    				case '01':
	    			        return '生产中'
	    			        break;
	    				case '02':
	    			        return '已完成'
	    			        break;
	    			}
    			}, 			
    		},
    		{ label: '订单类型', name: 'order_type_name', align:'center', index: 'order_type_name', width: 70 }, 	
    		{ label: '生产工厂', name: 'werks_name', align:'center', index: 'werks_name', width: 150 },
    		{ label: '订单数量', name: 'order_qty', align:'center', index: 'order_qty', width: 100 }, 
    		{ label: '车型', name: 'bus_type_code', align:'center', index: 'bus_type_code', width: 100 }, 
    		{ label: '关联订单', name: 'relate_order', align:'center', index: 'relate_order', width: 100 }, 
    		{ label: '备注', name: 'memo', align:'center', index: 'memo', width: 100 }, 
    		{ label: '维护人', name: 'creator', align:'center', index: 'creator', width: 150 }, 			
    		{ label: '维护时间', name: 'creat_date', index: 'creat_date', width: 160 },
    		{ label: '操作', name: 'actions', width: 60 ,align:'center',formatter:function(val, obj, row, act){
    			var actions = [];
    			var link = "<a href='#' onClick='vm.update(event,"+row.id+")' title='修改'><i class='fa fa-pencil'></i>&nbsp;</a> <a href='#' onClick='vm.delOrder(event,"+row.id+")' title='删除'><i class='fa fa-remove'></i>&nbsp;</a>";
				actions.push(link);
    		    return actions.join('');
    		}},
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
    });
	//$("#dataGrid").jqGrid("setFrozenColumns");
}

