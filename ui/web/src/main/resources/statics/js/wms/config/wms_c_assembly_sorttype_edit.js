var vm = new Vue({
    el:'#rrapp',
    data:{
    	assemblysorttype:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/assemblySortType/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.assemblysorttype = r.wmsCAssemblySortType;
	            }
			});
    	}
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){

		var url = vm.assemblysorttype.id == null ? "config/assemblySortType/save" : "config/assemblySortType/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.assemblysorttype),
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