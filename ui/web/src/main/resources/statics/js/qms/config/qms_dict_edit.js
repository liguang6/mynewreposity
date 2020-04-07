var vm = new Vue({
    el:'#rrapp',
    data:{
    	dict:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "qms/config/qmsDict/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.dict = r.entity;
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){

		var url = vm.dict.id == null ? "qms/config/qmsDict/save" : "qms/config/qmsDict/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.dict),
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
