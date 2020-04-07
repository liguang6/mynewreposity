var listdata = [];
var trindex = 1;
var lastrow,lastcell; 
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinbound/sapComponentlist',
    	datatype: "local",
		data: listdata,
		colModel: [
					
					{ label: '料号', name: 'MATNR', index: 'MATNR', width: 110 },
					{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 300 },
					{ label: '批次', name: 'BATCH', index: 'BATCH', width: 120,editable:true },
					{ label: '消耗数量', name: 'IN_QTY', index: 'IN_QTY', width: 70,editable:true,editrules:{number:true}},
					{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60 },
					{ label: '', name: 'WERKS', index: 'WERKS', width: 60,hidden:true },
					{ label: '', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 60,hidden:true }
		        
		          ],
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
		  			lastrow=iRow;
		  			lastcell=iCol;
		  		},
		viewrecords: true,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true
    });

});

var vm = new Vue({
	el:'#rrapp',
	data:{
		receiptNo:'',
		receiptItemNo:'',
		inQty:'',
		werks:'',
		wh_number:''
	},
	methods: {
		getDetail : function(receipt_no,receipt_item_no,in_qty,werks,wh_number) {
			
			queryReceiptInfo(receipt_no,receipt_item_no);
			
			vm.receiptNo=receipt_no;
			vm.receiptItemNo=receipt_item_no;
			vm.inQty=in_qty;
			vm.werks=werks;
			vm.wh_number=wh_number;
			
			this.$nextTick(function(){//渲染后再执行此函数
				initGridTable();
			});
				
		},
		close:function(){
			close();
		},
		newOperation:function () {
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATNR" id="'+trindex+'_MATNR" type="text" onblur="queryMads(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MAKTX"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BATCH"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BATCH" id="'+trindex+'_BATCH" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WERKS">'+vm.werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_NUMBER">'+vm.wh_number+'</td>'+
					
			'</tr>'
			$("#dataGrid tbody").append(addtr);
			trindex++ ;
		},
		btn_delete_n:function(){
			var trs=$("#dataGrid").children("tbody").children("tr");
			var reIndex = 1;
			$.each(trs,function(index,tr){
				var td = $(tr).find("td").eq(0);
				var td_check = $(tr).find("td").eq(1).find("input");
				if(index > 0){
					var ischecked=$(td_check).is(":checked");
					if(ischecked){
						tr.remove();
					}else{
						td.text(reIndex);
						$(tr).attr('id',reIndex);
						$(td_check).attr('name','jqg_dataGrid_' + reIndex);
						$(td_check).attr('id','jqg_dataGrid_' + reIndex);
						reIndex++;
					}
				}
			});
			trindex = reIndex;

		}
	}
});



function initGridTable(){
	
	$.ajax({
		url:baseURL+"in/wmsinbound/sapComponentlist",
		dataType : "json",
		type : "post",
		data : {
			"RECEIPT_NO":$("#RECEIPT_NO").val(),
			"RECEIPT_ITEM_NO":$("#RECEIPT_ITEM_NO").val(),
			"IN_QTY":$("#IN_QTY").val(),
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			//alert(response.result);
			if(response.code == "0"){
				listdata = response.result;
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				trindex=listdata.length;
				trindex++;
			}else{
				alert(response.msg)
			}
		}
	});

}

function queryReceiptInfo(receipt_no,receipt_item_no){
	
	$.ajax({
			url : baseUrl + "in/wmsinbound/Receiptlist",
			dataType : "json",
			type : "post",
			data : {
				"RECEIPT_NO":receipt_no,
		  		"RECEIPT_ITEM_NO":receipt_item_no
			},
			success:function(resp){
			   if(resp.code==0){
				  
					 $("#poNo").text(resp.result.PO_NO);
					 $("#poItemNO").text(resp.result.PO_ITEM_NO);
					 $("#lifnr").text(resp.result.LIFNR);
					 $("#liktx").text(resp.result.LIKTX);
					 $("#matnr").text(resp.result.MATNR);
					 $("#maktx").text(resp.result.MAKTX);
					 $("#inQty").text(resp.result.IN_QTY);
					 
			   }
			}
		});
}

function queryMads(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querymaterial",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		$(e).parent("td").next().html(data.mads);
        		$(e).parent("td").next().next().next().next().html(data.unit);
        		
        	}
        }
	});
	
}
