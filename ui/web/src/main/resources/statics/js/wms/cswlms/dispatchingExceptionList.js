var listdata = [];
var lastrow,lastcell; 
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'cswlms/dispatchingBillPicking/listwmlsException',
    	datatype: "local",
		data: listdata,
        colModel: [		
			{ label: '包装号', name: 'BARCODE', index: 'BARCODE', width: 160 },
			{ label: '拣配单号', name: 'DISPATCHING_NO', index: 'DISPATCHING_NO', width: 120 },
			{ label: '配送行项目号', name: 'ITEM_NO', index: 'ITEM_NO', width: 100 },
			{ label: '组件行项目号', name: 'COMPONENT_NO', index: 'COMPONENT_NO', width: 100 },
			{ label: '创建时间', name: 'CREATE_DATE', index: 'CREATE_DATE', width: 150 },
			{ label: '创建人', name: 'CREATE_USER_ID', index: 'CREATE_USER_ID', width: 100 },
			{ label: '成功重新发送', name: 'ISSUBMIT', index: 'ISSUBMIT', width: 100,
				formatter:function(val){	// 
            		if("0"==val)return "是";
            		if("1"==val)return "否";
            	}	
			},
			{ label: '类型', name: 'TYPE', index: 'TYPE', width: 120,
				formatter:function(val){	// 
            		if("1"==val)return "需求交接";
            		if("2"==val)return "需求重新发布";
            		if("3"==val)return "需求拆分";
            	}	
			},
			{ label: 'BARCODE1', name: 'BARCODE1', index: 'BARCODE1', width: 100 },
			{ label: 'DISPATCHINGNO1', name: 'DISPATCHINGNO1', index: 'DISPATCHINGNO1', width: 100 },
			{ label: 'BARCODE2', name: 'BARCODE2', index: 'BARCODE2', width: 100 },
			{ label: 'DISPATCHINGNO2', name: 'DISPATCHINGNO2', index: 'DISPATCHINGNO2', width: 100 }

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
        multiselect: true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            /*var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }*/
        },
        beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			   
			}
        
		
    });

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		sortTypeList:[],
		sortTypeName:"",
		sortType:""
	},
	
	created:function(){
		
	},
	methods:{
		query:function(){
			$("#searchForm").submit();
		},
		
		
		onStatusChange:function(event,id){},
		onPlantChange:function(event){
			
		},
		save:function(){},
		initData:function(){}
	}
});

function queryList(){
	$.ajax({
		url:baseURL+"cswlms/dispatchingBillPicking/listwmlsException",
		dataType : "json",
		type : "post",
		data : {
			"BARCODE":$("#barcode").val(),
			"CREATE_DATE_START":$("#createDateStart").val(),
			"CREATE_DATE_END":$("#createDateEnd").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				listdata = response.result;
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
			}
			
		}
	});
	

}

$("#btnqueryException").click(function () {
	queryList();
});









