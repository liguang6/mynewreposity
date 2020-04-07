var vm = new Vue({
    el:'#rrapp',
    data:{
    	wh:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id,addrID){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/wh/info",
	            data:{
	            	id:id,
	            	addrID:addrID
	            },
	            contentType: "application/json",
	            success: function(r){
	            	console.info(r.wh)
	            	vm.wh = r.wh;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){
		// 仓库代码字段存在小写的转化成大写
		vm.wh.whNumber=vm.wh.whNumber.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.wh.werks=$('#werks').val();
		vm.wh.lgortNo=$('#lgortNo').val();
		var url = vm.wh.id == null ? "config/wh/save" : "config/wh/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.wh),
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