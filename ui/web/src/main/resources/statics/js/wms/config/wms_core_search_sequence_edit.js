var vm = new Vue({
    el:'#rrapp',
    data:{
    	storage:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/wmsCoreSearchSequence/info?id="+id,
	            contentType: "application/json",
	            success: function(r){
	            	//vm.storage = r.storageType;
					$("#Id").val(r.storageType.id);
					$("#warehouseCode").val(r.storageType.warehouseCode);
					$("#factoryCode").val(r.storageType.factoryCode);
					$("#storageAreaSearch").val(r.storageType.storageAreaSearch);
					$("#searchSequenceType").val(r.storageType.searchSequenceType);
					$("#descs").val(r.storageType.descs);
					$("#status").val(r.storageType.status);

	            }
			});
    	},getWhNoFuzzy:function(){
    		getWhNoSelect("#warehouseCode",null,null);
//			getWhNoSelectForRedis("#warehouseCode",null,null);
		},getFactoryNoFuzzy:function(){
			getFactorySelectForRedis("#factoryCode",null,null);
		},
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		//vm.storage.storageTypeCode=vm.storage.storageTypeCode.toUpperCase();
		var url;
		if($("#Id").val()==null || $("#Id").val()==""){
			url = "config/wmsCoreSearchSequence/save";
		}else {
			url = "config/wmsCoreSearchSequence/update";
		}
		//var url = vm.storage.id == null ? "config/wmsCoreSearchSequence/save" : "config/wmsCoreSearchSequence/update";
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.storage.warehouseCode=$('#warehouseCode').val();
		vm.storage.factoryCode=$('#factoryCode').val();
		vm.storage.storageAreaSearch=$('#storageAreaSearch').val();
		vm.storage.searchSequenceType=$('#searchSequenceType').val();
		vm.storage.descs=$('#descs').val();
		vm.storage.status=$('#status').val();
		vm.storage.id=$('#Id').val();

		$.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.storage),
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