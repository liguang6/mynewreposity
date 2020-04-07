$(function () {
	var now = new Date(); //当前日期
	var pzDate=new Date(now.getTime());
	$("#docDate").val(pzDate.Format("yyyy-MM-dd"));
	$("#debitDate").val(pzDate.Format("yyyy-MM-dd"));
	
	$("#dataGrid").jqGrid({
    	url : baseUrl + "in/autoPutaway/docItem",
    	datatype: "json",
    	mtype:"POST",
    	jsonReader: jsonreader,
    	colNames:['行项','物料号','总数','箱数','调出工厂',''],
	    colModel:[
	    {name:'DOC_ITEM',index:'DOC_ITEM', width:35, align:'center',formatter:function(val, obj, row, act){
	    	return '<a href="#" onClick="labelItem('+row.DOC_NO+',\''+val+'\');">'+val+'</a>';
		}
	    },
	    {name:'MATNR',index:'MATNR', width:80, align:'center'},
	    {name:'SUM_QTY',index:'SUM_QTY', width:52, align:'center'},
	    {name:'BOXS',index:'BOXS', width:45, align:'center'},
	    {name:'WERKS',index:'WERKS', width:63, align:'center'},
	    {name:'DOC_NO',index:'DOC_NO', width:63, hidden:true}
	    ],
	    width: 225,
	    loadonce: true,
	    multiselect: false,
	    shrinkToFit:true,
	    rowNum: 10,
	    shrinkToFit:false,
	    pager:"#pager"
    });
	
	$("#dataGrid1").jqGrid({
		datatype: "local",
    	colNames:['条码','数量','',''],
	    colModel:[
		    {name:'LABEL_NO',index:'LABEL_NO', width:134, align:'center'},
		    {name:'QTY',index:'QTY', width:56, align:'center'},
		    {name:'REF_DOC_NO',index:'REF_DOC_NO', width:63, hidden:true},
		    {name:'REF_DOC_ITEM',index:'REF_DOC_ITEM', width:63, hidden:true}
	    ],
	    width: 225,
	    loadonce: true,
	    multiselect: true,
	    shrinkToFit:true,
	    rowNum: 10,
	    shrinkToFit:false,
	    pager:"#pager1"
    });
	
	
	//------删除操作---------
	  $("#btn5").click(function(){
		  var gr = $("#dataGrid1").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids="";
		  var selectedRows=[];
		  var docNo;
		  var docItem;
		  $.each(gr,function(i,rowid){
			 ids=ids+rowid+",";
			 var row = $("#dataGrid1").jqGrid("getRowData",rowid);
			 docNo = row.REF_DOC_NO;
			 docItem = row.REF_DOC_ITEM;
			 selectedRows.push(row);
		  })
		  
		  if (ids != "") {
			  document.getElementById("divInfo").style.visibility="hidden";
				$.ajax({
					type: "POST",
					url : baseUrl + "in/autoPutaway/deleteLabel",
				    async:false,
					dataType:"json",
					data:{
						deleteList: JSON.stringify(selectedRows)
					},
				    success: function(r){
						if(r.code == 0){
							$("#divInfo").html("删除成功");
							$("#dataGrid1").jqGrid("clearGridData");
							$("#dataGrid1").setGridParam({
								url : baseUrl + "in/autoPutaway/labelItem",
								datatype: "json",
								mtype:"POST",
								postData:{
									"docNo":docNo,"docItem":docItem
								},
								jsonReader: jsonreader,
								rowNum: 3
							}).trigger("reloadGrid");
						}else{
							$("#divInfo").html("删除成功");
						    
						}
						document.getElementById("divInfo").style.visibility="visible";
					}
			 });
		  } else {
			  $("#divInfo").html("请选择条码");
		      document.getElementById("divInfo").style.visibility="visible";
		      return false;
		  }
	  });
});

function docItem(){
	document.getElementById("div1").style.display = "none";
	document.getElementById("div2").style.display = "none";
	document.getElementById("div3").style.display = "";
	document.getElementById("div4").style.display = "none";
	document.getElementById("div5").style.display = "none";
	document.getElementById("div6").style.display = "none";
	
	document.getElementById("btn1").style.display = "none";
	document.getElementById("btn3").style.display = "none";
	document.getElementById("btn4").style.display = "";
	document.getElementById("btn5").style.display = "none";
	document.getElementById("btn6").style.display = "none";
	document.getElementById("btn7").style.display = "none";

	$("#dataGrid").jqGrid("clearGridData");
	$("#dataGrid").setGridParam({
		url : baseUrl + "in/autoPutaway/docItem",
		datatype: "json",
		mtype:"POST",
		jsonReader: jsonreader
	}).trigger("reloadGrid");
	
	document.getElementById("divInfo").style.visibility="hidden";
}

function labelItem(docNo,docItem){
	document.getElementById("div1").style.display = "none";
	document.getElementById("div2").style.display = "none";
	document.getElementById("div3").style.display = "none";
	document.getElementById("div4").style.display = "";
	document.getElementById("div5").style.display = "none";
	document.getElementById("div6").style.display = "none";
	
	document.getElementById("btn1").style.display = "none";
	document.getElementById("btn3").style.display = "none";
	document.getElementById("btn4").style.display = "none";
	document.getElementById("btn5").style.display = "";
	document.getElementById("btn6").style.display = "";
	document.getElementById("btn7").style.display = "none";
	
	$("#dataGrid1").jqGrid("clearGridData");
	$("#dataGrid1").setGridParam({
		url : baseUrl + "in/autoPutaway/labelItem",
		datatype: "json",
		mtype:"POST",
		postData:{
			"docNo":docNo,"docItem":docItem
		},
		jsonReader: jsonreader,
		rowNum: 10
	}).trigger("reloadGrid");
	
}


function scanPre(){
	$(".btn").attr("disabled", true);
	if($("#barcode").val()==""){
        $("#divInfo").html("请扫描条码");
        document.getElementById("divInfo").style.visibility="visible";
        $(".btn").attr("disabled", false);
        return false;
    }
	
	if($("#refDocNo").val()==""){
        $("#divInfo").html("请输入参考单据");
        document.getElementById("divInfo").style.visibility="visible";
        $(".btn").attr("disabled", false);
        return false;
    }
	
    if($("#fromWerks").val()==""){
        $("#divInfo").html("请输入调出工厂");
        document.getElementById("divInfo").style.visibility="visible";
        $(".btn").attr("disabled", false);
        return false;
    }
    
    $.ajax({
    	  url: baseUrl + "in/autoPutaway/scan",
    	  dataType : "json",
    	  type : "post",
    	  data : {
    		  "LABEL_NO":$("#barcode").val(),
    		  "REF_DOC_NO":$("#refDocNo").val(),
    		  "fromWerks":$("#fromWerks").val(),
    		  "binCode":$("#binCode").val()
    	  },
    	  success:function(resp){
    		  if(resp.code == 0){
        		  $("#barcode").val("");
        		  $("#boxlabel").html(resp.boxs);
        		  $("#barcodelabel").html(resp.result[0].LABEL_NO);
        		  $("#matnrlabel").html(resp.result[0].MATNR);
        		  $("#maktxlabel").html(resp.result[0].MAKTX);
        		  $("#qtylabel").html(resp.result[0].BOX_QTY);
        		  $("#batchlabel").html(resp.result[0].BATCH);
        		  document.getElementById("div2").style.display="";
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

function confirm(){
	$(".btn").attr("disabled", true);
	$("#divInfo").html("过账中，请稍等。。。");
	document.getElementById("divInfo").style.visibility="visible";
	
	if($("#docDate").val()==""){
        $("#divInfo").html("请输入凭证日期");
        document.getElementById("divInfo").style.visibility="visible";
        return false;
    }
	
	if($("#debitDate").val()==""){
        $("#divInfo").html("请输入记账日期");
        document.getElementById("divInfo").style.visibility="visible";
        return false;
    }
	
	$.ajax({
  	  url: baseUrl + "in/autoPutaway/confirm",
  	  dataType : "json",
  	  type : "post",
  	  data : {
  		  "docDate":$("#docDate").val(),
  		  "debitDate":$("#debitDate").val(),
  		  "headertxt":$("#headertxt").val()
  	  },
  	  success:function(resp){
  		  if(resp.code == 0){
  			  $("#wmsno").html(resp.wmsno);
	  		  $("#sapdoc").html(resp.sapdoc);
	  		  $("#receiptno").html(resp.receiptno);
	  		  $("#iqcno").html(resp.iqcno);
	  		  $("#operationTime").html(resp.operationTime);
	  		  
	  		  $(".btn").attr("disabled", false);
	  		  document.getElementById("div5").style.display = "none";
  			  document.getElementById("div6").style.display = "";
  			  document.getElementById("btn4").style.display = "none";
  			  document.getElementById("btn7").style.display = "none";
  			  document.getElementById("btn8").style.display = "";
  			  document.getElementById("divInfo").style.visibility="hidden";
  		  } else {
  			  $(".btn").attr("disabled", false);
  			  $("#divInfo").html(resp.msg);
  			  document.getElementById("divInfo").style.visibility="visible";
  		  }
  	  }
    });
}

function postDiv(){
	document.getElementById("div1").style.display = "none";
	document.getElementById("div2").style.display = "none";
	document.getElementById("div3").style.display = "none";
	document.getElementById("div4").style.display = "none";
	document.getElementById("div5").style.display = "";
	document.getElementById("div6").style.display = "none";
	
	document.getElementById("btn1").style.display = "none";
	document.getElementById("btn3").style.display = "none";
	document.getElementById("btn4").style.display = "";
	document.getElementById("btn5").style.display = "none";
	document.getElementById("btn6").style.display = "none";
	document.getElementById("btn7").style.display = "";
	document.getElementById("divInfo").style.visibility="hidden";
}

function rback(){
	document.getElementById("div1").style.display = "";
	document.getElementById("div2").style.display = "";
	document.getElementById("div3").style.display = "none";
	document.getElementById("div4").style.display = "none";
	document.getElementById("div5").style.display = "none";
	document.getElementById("div6").style.display = "none";
	
	document.getElementById("btn1").style.display = "";
	document.getElementById("btn3").style.display = "";
	document.getElementById("btn4").style.display = "none";
	document.getElementById("btn5").style.display = "none";
	document.getElementById("btn6").style.display = "none";
	document.getElementById("btn7").style.display = "none";
	document.getElementById("divInfo").style.visibility="hidden";
}

document.onkeydown = function(){
	
    if(window.event.keyCode == 13 && document.activeElement.id == 'barcode'){
    	scanPre();
    }
    if(window.event.keyCode == 118){
    	history.back(-1);
    }
}