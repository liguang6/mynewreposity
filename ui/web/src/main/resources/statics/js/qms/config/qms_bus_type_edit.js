var vm = new Vue({
    el:'#rrapp',
    data:{
    	busType:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "qms/config/busType/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	console.info(r.entity)
	            	vm.busType = r.entity;
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){
		var url = vm.busType.id == null ? "qms/config/busType/save" : "qms/config/busType/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.busType),
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
