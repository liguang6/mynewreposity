$(function(){
	  
	  $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	        datatype: "json",
	        colModel: [			
	            { label: '用户名', name: 'username', width: 75},
	            {label:"工号",name: 'staffNumber',width: 75},
	            {label:'姓名',width:75,name:'fullName'},
	            { label: '所属部门', name: 'deptName', sortable: false, width: 155 },
				{ label: '邮箱', name: 'email', width: 150 },
				{ label: '手机号', name: 'mobile', width: 100 },
				{ label: '一卡通号', name: 'cardHd', width: 100 }
	        ],
	        viewrecords: true,
	        rowNum: 15,
	        rownumbers: false, 
	        rownumWidth: 25, 
	        autowidth:true,
	        multiselect: false,
	        //横向滚动条
	        shrinkToFit:false,  
	        autoScroll: true,
	        showCheckbox:true
	    });
	  
	
	//------同步操作---------
	  $("#syscard").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		if(gr !== null && gr!==undefined){
		  
		  staffnumber_list="";
		  var trs=$("#dataGrid").children("tbody").children("tr");
		  
			$.each(trs,function(index,tr){
				var cbx=$(tr).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(tr).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var staffnumber = $(tr).find("td").eq(3).html();
						if(staffnumber != "")staffnumber_list += staffnumber + ",";
					}
				}
			});
				  //do something
				 //alert(staffnumber_list);
				  $.ajax({
					  url:baseUrl + "sys/user/updateCardid",
					  type:"post",
					  data:{"staffnumber":staffnumber_list},
					  success:function(resp){
						  if(resp.code === 0){
							  alert("操作成功！");
							  vm.reload();
						  }else{
							 alert("操作失败,"+resp.msg);
						  }
					  }
				  }); 
	  }else{
			 layer.msg("请选择要同步的行",{time:2000});
		 }	
		  
		  
	  });
	  
});

var vm = new Vue({
	el:'#rrapp',
	data:{},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		}
	}
});



function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
	}
}