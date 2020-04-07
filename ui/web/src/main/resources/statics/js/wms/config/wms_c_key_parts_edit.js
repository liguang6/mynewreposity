var vm = new Vue({
    el:'#rrapp',
    data:{
    	keyparts:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/keyparts/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.keyparts = r.wmsCKeyParts;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);
		},
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){

		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.keyparts.werks=$('#werks').val();
		vm.keyparts.matnr=$('#matnr').val();
		vm.keyparts.maktx=$('#maktx').val();
		var url = vm.keyparts.id == null ? "config/keyparts/save" : "config/keyparts/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.keyparts),
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