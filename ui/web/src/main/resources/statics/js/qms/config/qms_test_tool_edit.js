var vm = new Vue({
    el:'#rrapp',
    data:{
    	testTool:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "qms/config/testTool/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	console.info(r.entity)
	            	vm.testTool = r.entity;
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){
		vm.testTool.werks=$("#werks").val();
		var url = vm.testTool.id == null ? "qms/config/testTool/save" : "qms/config/testTool/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.testTool),
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
