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
	            url: baseURL + "bjmes/config/bjMesDict/info/"+id,
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

		var url = vm.dict.id == null ? "bjmes/config/bjMesDict/save" : "bjmes/config/bjMesDict/update";
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
