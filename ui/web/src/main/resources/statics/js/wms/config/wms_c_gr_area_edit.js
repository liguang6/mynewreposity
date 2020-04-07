var vm = new Vue({
    el:'#rrapp',
    data:{
    	area:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/area/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.area = r.area;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		// 存放区代码字段存在小写的转化成大写
		vm.area.areaCode=vm.area.areaCode.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.area.werks=$('#werks').val();
		var url = vm.area.id == null ? "config/area/save" : "config/area/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.area),
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