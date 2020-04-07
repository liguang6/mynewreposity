var vm = new Vue({
    el:'#rrapp',
    data:{
    	assemblylogistics:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/assemblylogistics/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.assemblylogistics = r.wmsCAssemblyLogistics;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		}
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){

		var url = vm.assemblylogistics.id == null ? "config/assemblylogistics/save" : "config/assemblylogistics/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.assemblylogistics),
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