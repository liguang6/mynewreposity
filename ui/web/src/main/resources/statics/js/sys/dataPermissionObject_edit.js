var vm = new Vue({
    el:'#rrapp',
    data:{
    	storage:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(ID){
			$.ajax({
	            type: "GET",
				url: baseURL + "sys/dataPermissionObject/info?id="+ID,
	            contentType: "application/json",
	            success: function(r){
					$("#ID").val(r.dataPermissionResultObject.ID);
					$("#menuId").val(r.dataPermissionResultObject.MENUID);
					$("#menuName").val(r.dataPermissionResultObject.MENUNAME);
					$("#AUTHFIELDS").val(r.dataPermissionResultObject.AUTHFIELDS);
					$("#AUTHNAME").val(r.dataPermissionResultObject.AUTHNAME);
	            }
			});
    	},getMenuNoFuzzy: function (event) {
			getMenuSelect("#menuName",null,null);
		},
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		var url;
		if($("#ID").val()==null || $("#ID").val()==""){
			url = "sys/dataPermissionObject/save";
		}else {
			url = "sys/dataPermissionObject/update";
		}
		//var url = $("#Id").val() == null ? "sys/dataPermission/save" : "sys/dataPermission/update";
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.storage.menuId=$('#menuId').val();
		vm.storage.menuName=$('#MENUNAME').val();
		vm.storage.authFields=$('#AUTHFIELDS').val();
		vm.storage.authName=$('#AUTHNAME').val();
		vm.storage.id=$('#ID').val();
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

