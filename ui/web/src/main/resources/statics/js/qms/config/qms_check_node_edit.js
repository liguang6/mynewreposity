var vm = new Vue({
    el:'#rrapp',
    data:{
    	checkNode:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "qms/config/checkNode/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	console.info(r.entity)
	            	vm.checkNode = r.entity;
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){

		if(vm.checkNode.vinFlag==vm.checkNode.customNoFlag){
			js.showErrorMessage("'按车号/VIN号录入'与'是否自编号'值必选一个，不能相同！");
			return ;
		}
		var url = vm.checkNode.id == null ? "qms/config/checkNode/save" : "qms/config/checkNode/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.checkNode),
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
