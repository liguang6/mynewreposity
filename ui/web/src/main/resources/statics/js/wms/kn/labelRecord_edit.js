var vm = new Vue({
    el:'#rrapp',
    data:{
		label:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(ID){
			$.ajax({
				type: "GET",
				url: baseURL + "kn/labelRecord/info?id="+ID,
				contentType: "application/json",
	            success: function(r){
	            	vm.label = r.storageType;
	            }
			});
    	},
		getInfoName:function(){
			getInfoName2($("#warehouseCode").val(),$("#storageAreaCode").val());
		},getWhNoFuzzy:function(){
			getWhNoSelectForRedis("#warehouseCode",null,null);
		},getFactoryNoFuzzy:function(){
			getFactorySelectForRedis("#factoryCode",null,null);
		},



    }
});

function regular2(p1) {
	var smallUtil=new smallTools();
	var result = smallUtil.getPKey("NUMBERREGULAR","M");
	var value = $('#'+p1).val();
	if(!/^[0-9]*$/.test(value) ){
		$('#'+p1).addClass('form-error');
		$('input').attr('placeholder',result);
		alert(result);
		//return result;
		return false;
	}
	return true;
}


$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		//vm.storage.storageTypeCode=vm.storage.storageTypeCode.toUpperCase();
		var url;

		if($("#Id").val()==null || $("#Id").val()==""){
			url = "kn/labelRecord/save";
		}else {
			url = "kn/labelRecord/update";
		}
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		$.ajax({
			type: "POST",
			url: baseURL + url,
			contentType: "application/json",
			data: JSON.stringify(vm.label),
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