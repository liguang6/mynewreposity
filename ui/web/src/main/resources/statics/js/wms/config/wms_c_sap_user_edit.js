var vm = new Vue({
	el:'#rrapp',
    data:{
    	csapuser:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/CSapUser/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.csapuser = r.csapuser;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		}

    }
});

$("#saveForm").validate({
	
	submitHandler: function(form){
		vm.csapuser.werks=$("#werks").val();
	var url = vm.csapuser.id == null ? "config/CSapUser/save" : "config/CSapUser/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.csapuser),
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
