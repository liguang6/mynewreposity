var vm = new Vue({
    el:'#rrapp',
    data:{
    	wh:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(HEADID,ITEMID){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/engine/info/"+HEADID+"/"+ITEMID,
	            contentType: "application/json",
	            success: function(r){
	            	r.result.CODE = r.result.PROJECT_CODE
	            	vm.wh = r.result;
	            	
	            	console.log(vm.wh)
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#WERKS",null,null);
		},
    }
});
$("#saveForm").validate({
	submitHandler: function(form){console.log(vm.wh.id)
		var url = vm.wh.HEADID&&vm.wh.ITEMID ? "config/engine/update" : "config/engine/save"
		vm.wh.WERKS=$('#WERKS').val();
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.wh),
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                    js.showMessage('操作成功')
                    vm.saveFlag= true
                }else{
                    alert(data.msg)
                }
	        }
	    });
    }
});