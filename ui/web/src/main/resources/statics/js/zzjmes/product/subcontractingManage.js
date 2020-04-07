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
		editSubcontracting: function (event,head_id) {
      console.log("-->editSubcontracting head_id : " + head_id)
      $("#subTable tbody").html("")
			$.ajax({
        url:baseUrl + "zzjmes/pmdManager/getSubcontractingItemList",
        data:{
          "head_id":head_id
        },
        //async:false,
        success:function(resp){
          for(var i=0;i<resp.result.length;i++){
            var addtr = '<tr><td>'+resp.result[i].item_no+'</td><td>'+resp.result[i].order_no+'</td><td>'+resp.result[i].zzj_plan_batch+'</td>'+
            '<td>'+resp.result[i].process+'</td><td>'+resp.result[i].zzj_no+'</td><td>'+resp.result[i].product_order+
            '</td><td><input type="text" style="width:40px" value="'+resp.result[i].outsourcing_quantity+
            '"/></td><td><input type="text" style="width:40px" value="'+resp.result[i].weight+
            '"/></td><td><input type="text" style="width:40px" value="'+resp.result[i].total_weight+
            '"/></td><td>'+resp.result[i].sender+'</td><td>'+resp.result[i].memo+'</td></tr>'
				  	$("#subTable tbody").append(addtr);
          }
          
        }
      });
      layer.open({
        type : 1,
        offset : '50px',
        skin : 'layui-layer-molv',
        title : "编辑委外加工纪录",
        area : [ '900px', '360px' ],
        shade : 0.5,
        shadeClose : true,
        content : jQuery("#subLayer"),
        btn : [ '确定','取消'],
        btn1 : function(index) {
          saveSubItemInfo(head_id);
          layer.close(index);
        },
        btn2 : function(index) {
          console.log('btn3')
          layer.close(index);
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

function saveSubItemInfo(head_id){
  console.log('-->saveSubItemInfo head_id : ' + head_id)
  var subtbody=$("#subtbody").find("tr");
  $.each(subtbody,function(index,param){
    var tds=$(param).children("td");
    console.log($(tds[0]).html() + "|" + $(tds[6]).find("input").val())
    $.ajax({
      url:baseUrl + "zzjmes/pmdManager/editSubcontractingItem",
      data:{
        "head_id":head_id,
        "item_no":$(tds[0]).html(),
        "outsourcing_quantity":$(tds[6]).find("input").val(),
        "weight":$(tds[7]).find("input").val(),
        "total_weight":$(tds[8]).find("input").val(),
      },
      //async:false,
      success:function(resp){
        alert("修改成功！")
      }
    })
  })

}

function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		searchForm: $("#searchPageForm"),
		columnModel: [
    		{ label: '委外发货单号', name: 'outsourcing_no',align:'center', index: 'outsourcing_no', width: 120 ,sortable:false,frozen: true}, 	
    		{ label: '生产工厂', name: 'werks',align:'center', index: 'werks', width: 80 ,sortable:false}, 
    		{ label: '生产车间', name: 'workshop',align:'center', index: 'workshop', width: 80 ,sortable:false}, 
    		{ label: '生产线别', name: 'line',align:'center', index: 'line', width: 80 ,sortable:false}, 
    		{ label: '外包供应商', name: 'vendor',align:'center', index: 'vendor', width: 80 ,sortable:false}, 
    		{ label: '总重', name: 'total_weight',align:'center', index: 'total_weight', width: 60 ,sortable:false},
    		{ label: '发货日期', name: 'business_date',align:'center', index: 'business_date', width: 100 ,sortable:false}, 
    		{ label: '备注', name: 'memo',align:'center', index: 'memo', width: 120 ,sortable:false}, 
    		{ label: '操作人', name: 'editor',align:'center', index: 'editor', width: 120 ,sortable:false}, 
    		{ label: '操作时间', name: 'edit_date',align:'center', index: 'edit_date', width: 150 ,sortable:false}, 	
    		{ label: '编辑作', name: 'actions', width: 60 ,align:'center',formatter:function(val, obj, row, act){
    			var actions = [];
    			var link = "<a href='#' onClick='vm.editSubcontracting(event,\""+row.id+"\")' title='修改'><i class='fa fa-pencil'></i>&nbsp;</a>";
				  actions.push(link);
    		  return actions.join('');
    		}}
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
