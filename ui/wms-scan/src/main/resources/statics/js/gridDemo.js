$(function () {
    $("#dataGrid").jqGrid({
//    	url : baseUrl + "in/gridDemo",
    	datatype : "local",
//    	jsonReader: jsonreader,
    	colNames:['仓库号','名称'],
	    colModel:[
	    {name:'CODE',index:'CODE', width:80, align:'center'},
	    {name:'WH_NAME',index:'WH_NAME', width:130, align:'center'}
	    ],
	    width: 216,
	    loadonce: true,
	    multiselect: true,
	    shrinkToFit:true,
	    rowNum: 10,
	    pager:"#pager"
    });
    
    //------删除操作---------
	  $("#delete").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  
		  var ids="";
		  var selectedRows=[];
		  $.each(gr,function(i,rowid){
			 ids=ids+rowid+",";
			 var row = $("#dataGrid").jqGrid("getRowData",rowid);
			 selectedRows.push(row);
		  })
		  
		  if (ids != "") {
			  alert(JSON.stringify(selectedRows));
				  $.ajax({
					type: "POST",
					url : baseUrl + "in/gridDemo",
				    async:false,
					dataType:"json",
					data:{
						deleteList: JSON.stringify(selectedRows)
					},
				    success: function(r){
						if(r.code == 0){

						}else{
//							alert(r.msg);
						}
					}
				});
		  } 
		  else alert("请选择!");
	  });
});

function scanPre(){
    $.ajax({
    	  url: baseUrl + "in/gridDemo",
    	  dataType : "json",
    	  type : "post",
    	  data : {"barcode":$("#barcode").val()},
    	  success:function(resp){
    		  $("#dataGrid").jqGrid('clearGridData');
    		  var jsonreader = {
    					root:"list",
    					page: "currPage",
    					total: "totalPage",
    					records: "totalCount",
    					repeatitems: false
    				};
    		  for(var i=0;i<=resp.page.list.length;i++)
    		    	$("#dataGrid").jqGrid('addRowData',i+1,resp.page.list[i]);
    		  
    		  $("#dataGrid").setGridParam().trigger("reloadGrid");
    		  document.getElementById("div1").style.display="";
    	  }
      })
}
