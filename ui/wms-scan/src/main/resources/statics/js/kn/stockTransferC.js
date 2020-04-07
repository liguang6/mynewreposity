var f_lgortList=[];//库位列表

var lastrow;
var lastcell;
$(function () {		
	showGrid();
	showGridA();
	
	
	//------删除操作---------
	  $("#btn5").click(function(){
		  var gr = $("#dataGridA").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids="";
		  var selectedRows=[];
		  $.each(gr,function(i,rowid){
			 ids=ids+rowid+",";
			 var row = $("#dataGridA").jqGrid("getRowData",rowid);
			 selectedRows.push(row);
		  })
		  
		  if (ids != "") {
			  document.getElementById("divInfo").style.visibility="hidden";
				$.ajax({
					type: "POST",
					url : baseUrl + "kn/stockTransferC/deleteLabel",
				    async:false,
					dataType:"json",
					data:{
						deleteList: JSON.stringify(selectedRows)
					},
				    success: function(r){
						if(r.code == 0){
							rback();
							$("#dataGrid").jqGrid("clearGridData");
							$("#dataGrid").jqGrid('setGridParam',{
								datatype : 'json',
								postData : { "barcode":$("#barcode").val()}
							},true).trigger("reloadGrid");
						}else{
							$("#divInfo").html(r.msg);	
							document.getElementById("divInfo").style.visibility="visible"
						}
					}
			 });
		  } else {
			  $("#divInfo").html("请选择条码");
		      document.getElementById("divInfo").style.visibility="visible";
		      $(".btn").attr("disabled", false);
		      return false;
		  }
	  });
	
});
function showGrid(){	
	$("#dataGrid").jqGrid({
    	url : baseUrl + "kn/stockTransferC/list",
    	datatype: "json",
    	jsonReader: jsonreader,
    	colNames:['物料号','供应商代码','<span style="color:red">*</span><span style="color:blue">库位</span>','数量','箱数','源库位','批次','条码','单位'],
	    colModel:[
	    {name:'MATNR',index:'MATNR', width:80, align:'center',sortable:false},
	    {name:'LIFNR',index:'LIFNR', width:80, align:'center',sortable:false},
	    {name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 70 ,sortable:false,
			editable:true,edittype:'select',editrules:{required:true}},
	    {name:'QTY',index:'QTY', width:80, align:'center',sortable:false},
	    {name:'BOX_QTY',index:'BOX_QTY', width:80, align:'center',sortable:false,
	    	formatter:function(cellvalue, options, rowObject){
	    		var rowID=options.rowId;
	    		return "<a href='javascript:void(0)' style='color:red;' onclick=editFunction('"+rowID+"')>"+cellvalue+"</a>";
	    	}
	    },
	    {name:'LGORT',index:'LGORT', width:80, align:'center',sortable:false},
	    {name:'BATCH',index:'BATCH', width:80, align:'center',sortable:false},
	    {name:'LABEL',index:'LABEL', width:80, align:'center',sortable:false,hidden:true},
	    {name:'UNIT',index:'UNIT', width:80, align:'center',sortable:false,hidden:true}
	    ],
	    loadonce: true,
	    cellEdit: true,
		cellsubmit: "clientArray",
	    multiselect: true,
	    rownumWidth:25,
	    width:225,
	    shrinkToFit:false,
	    autoScroll: true, 
	    rowNum: 5,
	    pager:"#pager",
	    formatCell:function(rowid, cellname, value, iRow, iCol){
			var row = $('#dataGrid').jqGrid('getRowData', rowid);
			if(cellname=='F_LGORT'){					
	            $('#dataGrid').jqGrid('setColProp', 'F_LGORT', { editoptions: {value:getCellOption(f_lgortList,'LGORT')} });
			}
	    },
	    beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		}
    });
}
function showGridA(){
	$("#dataGridA").jqGrid({
    	url : "",
    	datatype: "",
    	jsonReader: jsonreader,
    	colNames:['箱序','条形码','数量','BYD批次'],
	    colModel:[
	    {name:'BOX_SN',index:'BOX_SN', width:80, align:'center',sortable:false},
	    {name:'LABEL_NO',index:'LABEL_NO', width:120, align:'center',sortable:false},
	    {name:'BOX_QTY',index:'BOX_QTY', width:50, align:'center',sortable:false},
	    {name:'BATCH',index:'BATCH', width:80, align:'center',sortable:false}
	    ],
	    multiselect: true,
	    rownumWidth:25,
	    rownumbers:true,
	    width:225,
	    shrinkToFit:false,
	    autoScroll: true, 
	    rownumWidth:25,
	    rowNum: 5,
	    pager:"#pagerA"
    });
	
}
function scanPre(){
	
	$(".btn").attr("disabled", true);
	if($("#barcode").val()==""){
        $("#divInfo").html("请扫描条码");
        document.getElementById("divInfo").style.visibility="visible";
        $(".btn").attr("disabled", false);
        return false;
    }
	 $.ajax({
   	  url: baseUrl + "kn/stockTransferC/list",
   	  dataType : "json",
   	  type : "post",
   	  data : {
   		  "barcode":$("#barcode").val()
   	  },
   	  success:function(resp){
   		  if(resp.code == 0){
   			  var result=resp.page.list;
       		  $("#boxlabel").html(resp.boxs);
       		  $("#barcodelabel").html($("#barcode").val());
       		  $("#matnrlabel").html(result[0].MATNR);
       		  $("#maktxlabel").html(result[0].MAKTX);
       		  $("#qtylabel").html(result[0].BOX_QTY);
       		  $("#batchlabel").html(result[0].BATCH);
       		  document.getElementById("divInfo").style.visibility="hidden";
       		  $(".btn").attr("disabled", false);
   		  } else {
   			  $("#divInfo").html(resp.msg);
   			  document.getElementById("divInfo").style.visibility="visible";
   			  $(".btn").attr("disabled", false);
   		  }
   	  }
     });

}
function editFunction(rowID){	
	
	document.getElementById("div1").style.display = "none";
	document.getElementById("div2").style.display = "none";
	document.getElementById("div3").style.display = "none";
	document.getElementById("div4").style.display = "";
	document.getElementById("div5").style.display = "none";

	document.getElementById("btn1").style.display = "none";
	document.getElementById("btn2").style.display = "none";
	document.getElementById("btn3").style.display = "";
	document.getElementById("btn4").style.display = "none";
	document.getElementById("btn5").style.display = "";
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var rowData = $("#dataGrid").jqGrid('getRowData', rowID);
	$("#dataGridA").jqGrid("clearGridData");
	$("#dataGridA").jqGrid('setGridParam',{
		url:baseUrl + "kn/stockTransferC/labelList",
		datatype : 'json',
		postData : rowData,
		page : 1
	},true).trigger("reloadGrid");

}


function showhead(){
	document.getElementById("btn1").style.display = "";
	document.getElementById("btn2").style.display = "none";
	document.getElementById("btn3").style.display = "";
	document.getElementById("btn4").style.display = "none";
	document.getElementById("btn5").style.display = "none";
	
	document.getElementById("div5").style.display = "none";
	document.getElementById("div4").style.display = "none";
	document.getElementById("div3").style.display = "none";	
	document.getElementById("div2").style.display = "";
	document.getElementById("div1").style.display = "none";
	var now = new Date(); //当前日期
	var pzDate=new Date(now.getTime());
	$("#PZ_DATE").val(pzDate.Format("yyyy-MM-dd"));
	$("#JZ_DATE").val(pzDate.Format("yyyy-MM-dd"));
}

function showData(){
	
	//获取仓库 库位
	f_lgortList=getLgortList();
	//获取汇总数据明细
	$("#dataGrid").jqGrid("clearGridData");
	$("#dataGrid").jqGrid('setGridParam',{
		datatype: "json",
		mtype:"POST",
		postData: { "barcode":$("#barcode").val()},
		jsonReader: jsonreader,
		rowNum: 5
	},true).trigger("reloadGrid");
	//显示
	document.getElementById("div1").style.display = "none";
	document.getElementById("div2").style.display = "none";
	document.getElementById("div3").style.display = "";
	document.getElementById("div4").style.display = "none";
	document.getElementById("div5").style.display = "none";

	document.getElementById("btn1").style.display = "none";
	document.getElementById("btn2").style.display = "none";
	document.getElementById("btn3").style.display = "";
	document.getElementById("btn4").style.display = "none";
	document.getElementById("btn5").style.display = "none";
}




function saveA(){
	$("#barcode").val("");
	if($("#SO_NO").val()==""){
        $("#divInfo").html("请输入销售单号");
        document.getElementById("divInfo").style.visibility="visible";
        $(".btn").attr("disabled", false);
        return false;
    }
	if($("#SO_ITEM_NO").val()==""){
        $("#divInfo").html("请输入行号");
        document.getElementById("divInfo").style.visibility="visible";
        $(".btn").attr("disabled", false);
        return false;
    }
	
	var ids = $("#dataGrid").jqGrid('getDataIDs');//获取 tabel的所有ID
	if(ids==undefined||ids.length==0){
		alert("请确认数据至少有一条");
		$("#btn1").html("确认");	
		$("#btn1").removeAttr("disabled");
		return false;
	}
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	for(var i =0; i<ids.length; i++) {
		var row = $("#dataGrid").jqGrid('getRowData', ids[i]);
		if(row.F_LGORT==null||row.F_LGORT==""){
			$("#divInfo").html(ids[i]+"行库位不能为空");
			document.getElementById("divInfo").style.visibility="visible";
			$(".btn").attr("disabled", false);
			return false;
		}
	}

	document.getElementById("btn1").style.display = "";
	document.getElementById("btn2").style.display = "none";
	document.getElementById("btn3").style.display = "";
	document.getElementById("btn4").style.display = "none";
	document.getElementById("btn5").style.display = "none";
	
	document.getElementById("div4").style.display = "none";
	document.getElementById("div3").style.display = "none";	
	document.getElementById("div2").style.display = "";
	document.getElementById("div1").style.display = "none";
	document.getElementById("divInfo").style.visibility="hidden";
	var now = new Date(); //当前日期
	var pzDate=new Date(now.getTime());
	$("#PZ_DATE").val(pzDate.Format("yyyy-MM-dd"));
	$("#JZ_DATE").val(pzDate.Format("yyyy-MM-dd"));	
	
}

function save(){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids = $("#dataGrid").jqGrid('getDataIDs');//获取 tabel的所有ID
	if(ids==undefined||ids.length==0){
		alert("请确认数据至少有一条");
		$("#btn1").html("确认");	
		$("#btn1").removeAttr("disabled");
		return false;
	}
	
	var selectedRows=[];
	var confirm_flag=true;
	var labelNo= "";
	for(var i =0; i<ids.length; i++) {
		var row = $("#dataGrid").jqGrid('getRowData', ids[i]);
		var str=row.LABEL;
		labelNo +=str+",";
		selectedRows.push(row);
	}
	
	$.ajax({
		url:baseUrl+'kn/stockTransferC/save',
		type:"post",
		async:true,
		dataType:"json",
		data:{
			labelNo:labelNo,
			SO_NO:$("#SO_NO").val(),
			SO_ITEM_NO:$("#SO_ITEM_NO").val(),
			matList:JSON.stringify(selectedRows),			
			HEADER_TXT:$("#HEADER_TXT").val(),
			PZ_DATE:$("#PZ_DATE").val(),
			JZ_DATE:$("#JZ_DATE").val()
		},
		success:function(response){
			if(response.code==500){
				$("#divInfo").html(response.msg);
				document.getElementById("divInfo").style.visibility="visible";
				return false;
			}else if(response.code==0){
				$("#barcode").val("");
				$("#dataGrid").jqGrid("clearGridData");
				
				$("#WMS_NO").html(response.WMS_NO);
				$("#SAP_DOC_NO").html(response.SAP_DOC_NO);
			}
			document.getElementById("div1").style.display = "none";
			document.getElementById("div2").style.display = "none";
			document.getElementById("div3").style.display = "none";
			document.getElementById("div4").style.display = "none";
			document.getElementById("div5").style.display = "";

			document.getElementById("btn1").style.display = "none";
			document.getElementById("btn2").style.display = "none";
			document.getElementById("btn3").style.display = "";
			document.getElementById("btn4").style.display = "none";
			document.getElementById("btn5").style.display = "none";
		}
	});
	
}


function rback(){
	//清除页面
	$("#barcode").val("");
	$("#boxlabel").html("");
	$("#barcodelabel").html("");
	$("#matnrlabel").html("");
	$("#maktxlabel").html("");
	$("#qtylabel").html("");
  	$("#batchlabel").html("");
	//返回录入页面
	document.getElementById("div1").style.display = "";
	document.getElementById("div2").style.display = "none";
	document.getElementById("div3").style.display = "none";
	document.getElementById("div4").style.display = "none";
	document.getElementById("div5").style.display = "none";

	document.getElementById("btn1").style.display = "none";
	document.getElementById("btn2").style.display = "";
	document.getElementById("btn3").style.display = "none";
	document.getElementById("btn4").style.display = "";
	document.getElementById("btn5").style.display = "none";
	document.getElementById("divInfo").style.visibility="hidden";
}

function getCellOption(list,cellname){
	var options="";
	for(i=0;i<list.length;i++){
			if(i != list.length - 1) {
				options += list[i][cellname] + ":" + list[i][cellname]  + ";";
			} else {
				options += list[i][cellname]  + ":" +  list[i][cellname] ;
			}
	}
	return options;
}
function getLgortList(){
	var list=[];
	$.ajax({
		url:baseUrl+'kn/stockTransferC/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
}

document.onkeydown = function(){
	
    if(window.event.keyCode == 13 && document.activeElement.id == 'barcode'){
    	scanPre();
    }
    if(window.event.keyCode == 118){
    	history.back(-1);
    }
}