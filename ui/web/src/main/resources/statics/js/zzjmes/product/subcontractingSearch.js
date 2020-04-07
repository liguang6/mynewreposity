var vm = new Vue({
	el:'#rrapp',
	data:{
		orderQty:100,
		mydata:[],
		workshoplist:[],
		linelist:[],
		lastrow:'',
		lastcell:'',
		del_ids:'',
		check:true,
		showMore:true
	},
	created:function(){
		getWorkShopList();
	},
	methods: {
		query: function () {
			$("#searchPage_werks").val($("#search_werks").val())
			$("#searchPage_order").val($("#search_order").val())
			$("#searchPage_workshop").val($("#search_workshop").val())
			$("#searchPage_line").val($("#search_line").val())
			$("#searchPage_zzj_plan_batch").val($("#search_zzj_plan_batch").val())
			$("#searchPage_zzj_no").val($("#search_zzj_no").val())
			$("#searchPage_product_order").val($("#search_product_order").val())
			$("#searchPage_sender").val($("#search_sender").val())
			$("#searchPage_business_date_start").val($("#search_business_date_start").val())
			$("#searchPage_business_date_end").val($("#search_business_date_end").val())
			
			if($("#search_order").val() === ''){
				alert("请输入订单号查询！")
				return false;
			}

			$("#searchPageForm").submit();
		},
		editSubcontracting: function () {
			console.log("-->: editSubcontracting")
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
			
			var ids = [];
			
			
			
			var trs=$("#dataGrid").children("tbody").children("tr");
			for(var i=0;i<trs.length;i++){
				var tr = trs[i]
				var td = $(tr).find("td").eq(0);
				var td_check = $(tr).find("td").eq(1).find("input");
				//console.log(td.text())
				var ischecked=$(td_check).is(":checked");
				if(ischecked){
					//if(td1.text() != '')vm.del_ids += td1.text() + ','
					//tr.remove();
					ids.push(i)
				}
			}
			
			
			console.log('---->ids : ' + ids)
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var trs=$("#dataGrid").children("tbody").children("tr");
			var editStr = "";
			for (var i = 0; i < ids.length; i++) {
				var tr = trs[ids[i]]
				console.log('-->memo : ' + $(tr).find("td").eq(13).html().trim())
				editStr += $(tr).find("td").eq(20).html().trim() + ',' + $(tr).find("td").eq(8).html().trim() + ',' + 
				$(tr).find("td").eq(10).html().trim() + ',' + $(tr).find("td").eq(11).html().trim() + ',' + 
				$(tr).find("td").eq(12).html().trim() + ',' + 
				(('&nbsp;'===$(tr).find("td").eq(13).html().trim())?'':$(tr).find("td").eq(13).html().trim()) +
				',' + $(tr).find("td").eq(20).html().trim() + ';';
			}
			console.log('-->editStr : ' + editStr)
			$.ajax({
				url:baseURL+"zzjmes/pmdManager/editSubcontractingList",
				dataType : "json",
				type : "post",
				data : {
					"editStr":editStr,
				},
				async: true,
				success: function (response) { 
					
				}
			});
		},
		onWerksChange:function(event) {
			getWorkShopList()
		},
		onWorkshopChange:function(event) {
			getLineList()
		},
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,null)	
		},
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		}
	},
	watch: {
		
	}
});
$(function () {
	fun_ShowTable();
	
	$("#btn_delete").click(function () {
		var trs=$("#dataGrid").children("tbody").children("tr");
		for(var i=trs.length-1;i>0;i--){
			var tr = trs[i]
			var td = $(tr).find("td").eq(0);
			var td1 = $(tr).find("td").eq(20);
			var td_check = $(tr).find("td").eq(1).find("input");
			
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				if(td1.text() != '')vm.del_ids += td1.text() + ','
				tr.remove();
			}
		}
	})
});

function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		searchForm: $("#searchPageForm"),
		columnModel: [
    		{ label: '订单', name: 'order_no',align:'center', index: 'order_no', width: 120 ,sortable:false,frozen: true}, 	
    		{ label: 'SAP工单号', name: 'product_order',align:'center', index: 'product_order', width: 120 ,sortable:false,frozen: true}, 	
    		{ label: 'SAP料号', name: 'sap_mat',align:'center', index: 'sap_mat', width: 120 ,sortable:false}, 	
    		{ label: '零部件号', name: 'zzj_no',align:'center', index: 'zzj_no', width: 120 ,sortable:false}, 	
    		{ label: '零部件名称', name: 'zzj_name',align:'center', index: 'zzj_name', width: 120 ,sortable:false}, 	
    		{ label: '委外工序', name: 'process',align:'center', index: 'process', width: 120 ,sortable:false}, 	
    		{ label: '委外数量', name: 'outsourcing_quantity',align:'center', index: 'outsourcing_quantity', width: 120 ,sortable:false,editable:false}, 	
    		{ label: '单位', name: 'unit',align:'center', index: 'unit', width: 80 ,sortable:false}, 	
    		{ label: '单重', name: 'weight',align:'center', index: 'weight', width: 80 ,sortable:false,editable:false}, 	
    		{ label: '重量', name: 'total_weight',align:'center', index: 'total_weight', width: 80 ,sortable:false,editable:false}, 	
    		/* { label: '单次总重', name: 'quantity',align:'center', index: 'quantity', width: 120 ,sortable:false,editable:true},  */	
    		{ label: '备注', name: 'memo',align:'center', index: 'memo', width: 120 ,sortable:false,editable:false}, 	
    		{ label: '装配位置', name: 'assembly_position',align:'center', index: 'assembly_position', width: 120 ,sortable:false}, 
    		{ label: '使用车间', name: 'use_workshop',align:'center', index: 'use_workshop', width: 120 ,sortable:false}, 
    		/*{ label: '使用工序', name: 'order_no',align:'center', index: 'order_no', width: 120 ,sortable:false}, 	*/
    		{ label: '工艺流程', name: 'process_flow',align:'center', index: 'process_flow', width: 120 ,sortable:false}, 
    		{ label: '发料人', name: 'sender',align:'center', index: 'sender', width: 120 ,sortable:false}, 
    		{ label: '操作人', name: 'editor',align:'center', index: 'editor', width: 120 ,sortable:false}, 
    		{ label: '操作时间', name: 'edit_date',align:'center', index: 'edit_date', width: 120 ,sortable:false}, 
    		
    		{ label: 'id', name: 'IID', align:'center', index: 'IID', width: 100 ,hidden:true}, 
    	],
    	onCellSelect:function(rowid,iCol,cellcontent,e){
			//var row =  $("#dataGrid").jqGrid('getRowData', rowid);
    		console.log('-->rowid : ' + rowid )
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			vm.lastrow=iRow;
			vm.lastcell=iCol;
			vm.check = false;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			
		},
		pager : '#dataGridPage',
		shrinkToFit: false,
		width:3000,
		rowNum:25,
		rownumWidth: 25, 
		rowList : [ 15,50,500 ],
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
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
  	  //async:false,
  	  success:function(resp){
  		  vm.workshoplist = resp.data;
  		  getLineList(vm.workshoplist[0].code)
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
