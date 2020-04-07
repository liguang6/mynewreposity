
var num;
var vm = new Vue({
    el:'#rrapp',
    data:{
    	storage:{},
    	saveFlag:false,
    },
    methods:{
    	getUserId:function(userId){
			$("#userId").val(userId);
		},
    	getInfo:function(ID){
			$.ajax({
	            type: "GET",
	            url: baseURL + "sys/dataPermission/userAuthInfo?id="+ID,
	            contentType: "application/json",
	            success: function(r){
					//vm.storage = r.dataPermissionResult;
					$("#Id").val(r.dataPermissionResult.ID);
					$("#menuId").val(r.dataPermissionResult.MENUID);
					$("#menuName").val(r.dataPermissionResult.MENUNAME);
					$("#authFields").val(r.dataPermissionResult.AUTHFIELDS);
					$("#authValue").val(r.dataPermissionResult.AUTHVALUE);
					$("#userId").val(r.dataPermissionResult.USERID);
					$.ajax({
						type: "POST",
						url: baseURL + "sys/dataPermission/getAuthFields",
						//contentType: "application/json",
						dataType : "json",
						async:false,
						data: {
							"menuId":r.dataPermissionResult.MENUID,
						},
						success: function(resp){
							var data=resp.dataPermissionResult;
							var element=$("#authFields");
							var num = r.dataPermissionResult.AUTHFIELDS;
							element.append("<option value='"+num+"'" + ">"+num+"</option>");
							$.each(data,function(index,val){
								element.append("<option value='"+val.AUTHFIELDS+"'" + ">"+val.AUTHNAME+"</option>");

							});
//				        	$.each(data,function(index,val){
//				        		if(num==val.AUTHFIELDS){
//				        			element.append("<option value='"+val.AUTHFIELDS+"'" + ">"+val.AUTHNAME+"</option>");
//				        		}else{
//				        			element.append("<option value='"+val.AUTHFIELDS+"'" + ">"+val.AUTHNAME+"</option>");
//				        		}
//				        	});
						}
					});





	            }
			});
    	},
		getUserNoFuzzy:function(){
			getUserSelect("#userName",null,null);
		},
		getRoleNoFuzzy: function (event) {
			getRoleSelect("#roleName",null,null);
		},
		getMenuNoFuzzy: function (event) {
			getMenuSelect("#menuName",null,null);
		},getAuthFields: function (event) {
			getAuthFieldsByMenuId();
		},
    }
});


function getAuthFieldsByMenuId(){
	var menuId = $("#menuId").val();
	$("#authFields").html("");

	$.ajax({
		type: "POST",
		url: baseURL + "sys/dataPermission/getAuthFields",
		//contentType: "application/json",
		dataType : "json",
		async:false,
		data: {
			"menuId":menuId,
		},
		success: function(resp){
			var data=resp.dataPermissionResult;
			var element=$("#authFields");
			element.append("<option value='' >请选择</option>");
			$.each(data,function(index,val){
				element.append("<option value='"+val.AUTHFIELDS+"'" + ">"+val.AUTHNAME+"</option>");

			});
		}
	});


}



$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		var url;
		if($("#Id").val()==null || $("#Id").val()==""){
			url = "sys/dataPermission/userAuthSave";
		}else {
			url = "sys/dataPermission/userAuthUpdate";
		}
		//var url = $("#Id").val() == null ? "sys/dataPermission/save" : "sys/dataPermission/update";
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.storage = {};
		vm.storage.userId=$('#userId').val();
		vm.storage.menuId=$('#menuId').val();
		vm.storage.authFields=$('#authFields').val();
		vm.storage.authValue=$('#authValue').val();
		vm.storage.id=$('#Id').val();
//		alert(JSON.stringify(vm.storage));
//		alert($('#authFields').val());
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.storage),
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                    js.showMessage('操作成功');
                    vm.saveFlag= true;
                }else{
                    alert(data.msg);
                }
	        }
	    });
    }
});



function getUserSelect(elementId, submitId, fn_backcall) {
	$(submitId).val("")
	var wh = {};
	var whlist;
	$(elementId).typeahead({
		source: function (input, process) {
			var data = {
				"userName": input
			};
			$.ajax({
				url: baseURL + "sys/user/getUserList",
				dataType: "json",
				type: "post",
				data: data,
				success: function (response) {
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function (index, value) {
						results.push(value.USERNAME);
					})
					return process(results);
				}, error: function () {
					alert("error");
				}
			});
		},
		items: 20,
		highlighter: function (item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function (index, value) {
				if (value.USERNAME == item) {
					whNumber = value.USERID;
					whName = value.USERNAME;
				}
			})
			return whNumber + "  " + whName;
		},
		matcher: function (item) {
			return true;
		},
		updater: function (item) {
			return item;
		},
		afterSelect: function (item) {
			$.each(whlist, function (index, value) {
				if (value.USERNAME == item) {
					whNumber = value;
					//selectId = value.id;
					$("#userId").val(value.USERID);
					$(submitId).val(value.USERID)
				}


			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}


function getRoleSelect(elementId, submitId, fn_backcall) {
	$(submitId).val("")
	var wh = {};
	var whlist;
	$(elementId).typeahead({
		source: function (input, process) {
			var data = {
				"roleName": input
			};
			$.ajax({
				url: baseURL + "sys/user/getRoleList",
				dataType: "json",
				type: "post",
				data: data,
				success: function (response) {
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function (index, value) {
						results.push(value.ROLENAME);
					})
					return process(results);
				}, error: function () {
					alert("error");
				}
			});
		},
		items: 20,
		highlighter: function (item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function (index, value) {
				if (value.ROLENAME == item) {
					whNumber = value.ROLEID;
					whName = value.ROLENAME;
				}
			})
			return whNumber + "  " + whName;
		},
		matcher: function (item) {
			return true;
		},
		updater: function (item) {
			return item;
		},
		afterSelect: function (item) {
			$.each(whlist, function (index, value) {
				if (value.ROLENAME == item) {
					whNumber = value;
					//selectId = value.id;
					$("#roleId").val(value.ROLEID);
					$("#roleName").val(value.ROLENAME);
					$(submitId).val(value.ROLEID)
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}


function getMenuSelect(elementId, submitId, fn_backcall) {
	$(submitId).val("")
	var wh = {};
	var whlist;
	$(elementId).typeahead({
		source: function (input, process) {
			var data = {
				"menuName": input
			};
			$.ajax({
				url: baseURL + "sys/user/getMenuList",
				dataType: "json",
				type: "post",
				data: data,
				success: function (response) {
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function (index, value) {
						results.push(value.MENUNAME);
					})
					return process(results);
				}, error: function () {
					alert("error");
				}
			});
		},
		items: 20,
		highlighter: function (item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function (index, value) {
				if (value.MENUNAME == item) {
					whNumber = value.MENUID;
					whName = value.MENUNAME;
				}
			})
			return whNumber + "  " + whName;
		},
		matcher: function (item) {
			return true;
		},
		updater: function (item) {
			return item;
		},
		afterSelect: function (item) {
			$.each(whlist, function (index, value) {
				if (value.MENUNAME == item) {
					whNumber = value;
					$("#menuId").val(value.MENUID);
					$(submitId).val(value.MENUID);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}



