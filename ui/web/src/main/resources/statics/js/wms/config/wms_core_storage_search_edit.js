var vm = new Vue({
    el:'#rrapp',
    data:{
    	storage:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(ID){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/wmsCoreStorageSearch/info?id="+ID,
	            contentType: "application/json",
	            success: function(r) {
	            	console.info('asdasdsa '+r.storageType);
					$("#Id").val(r.storageType.id);
					$("#warehouseCode").val(r.storageType.warehouseCode);
					$("#factoryCode").val(r.storageType.factoryCode);
					$("#storageAreaSearch").val(r.storageType.storageAreaSearch);
					$("#storageAreaCode").val(r.storageType.storageAreaCode);
					$("#priority").val(r.storageType.priority);
					$("#status").val(r.storageType.status);
					//vm.storage = r.storageType;


					$.ajax({
						type: "POST",
						url: baseURL + "config/wmsCoreStorageSearch/getStorageAreaSearch",
						dataType: "json",
						async: false,
						data: {
							"warehouseCode": r.storageType.warehouseCode,
						},
						success: function (resp) {
							var data = resp.resuslt;
							console.info(resp.resuslt);
							var element = $("#storageAreaSearch");
							//element.append("<option value='' >请选择</option>");
							var num = r.storageType.storageAreaSearch;
							element.append("<option value='" + num + "'" + ">" + num + "</option>");
							$.each(data, function (index, val) {
								element.append("<option value='" + val.SEARCH_SEQ + "'" + ">" + val.SEARCH_SEQ + "</option>");

							});
						}
					});


					$.ajax({
						type: "POST",
						url: baseURL + "config/wmsCoreStorageSearch/getStorageAreaCode",
						dataType : "json",
						async:false,
						data: {
							"warehouseCode":r.storageType.warehouseCode,
						},
						success: function(resp){
							var data=resp.resuslt;
							console.info(resp.resuslt);
							var element = $("#storageAreaCode");
							//element.append("<option value='' >请选择</option>");
							var num = r.storageType.storageAreaCode;
							getInfoName2(r.storageType.warehouseCode,r.storageType.storageAreaCode);
							element.append("<option value='" + num + "'" + ">" + num + "</option>");
							$.each(data,function(index,val){
								//element.append("<option value='"+val.STORAGE_AREA_CODE+"'" + ">"+val.AREA_NAME+"</option>");
								element.append("<option value='"+val.STORAGE_AREA_CODE+"'" + ">"+val.STORAGE_AREA_CODE+"</option>");

							});


						}
					});


				}
			});
    	},
		getInfoName:function(){
			getInfoName2($("#warehouseCode").val(),$("#storageAreaCode").val());
		},getWhNoFuzzy:function(){
			getWhNoSelect("#warehouseCode",null,null);
//			getWhNoSelectForRedis("#warehouseCode",null,null);
		},getFactoryNoFuzzy:function(){
			getFactorySelectForRedis("#factoryCode",null,null);
		},



    }
});



function change() {
	getInfoName2($("#warehouseCode").val(),$("#storageAreaCode").val());
}


function getStorageAreaSearch() {
		var warehouseCode = $("#warehouseCode").val();
		$("#storageAreaSearch").html("");

		$.ajax({
		type: "POST",
		url: baseURL + "config/wmsCoreStorageSearch/getStorageAreaSearch",
		dataType : "json",
		async:false,
		data: {
			"warehouseCode":warehouseCode,
		},
		success: function(resp){
			var data=resp.resuslt;
			console.info(resp.resuslt);
			var element=$("#storageAreaSearch");
			element.append("<option value='' >请选择</option>");
			$.each(data,function(index,val){
				element.append("<option value='"+val.SEARCH_SEQ+"'" + ">"+val.SEARCH_SEQ+"</option>");

			});
		}
	});

	var warehouseCode = $("#warehouseCode").val();
	$("#storageAreaCode").html("");

	$.ajax({
		type: "POST",
		url: baseURL + "config/wmsCoreStorageSearch/getStorageAreaCode",
		dataType : "json",
		async:false,
		data: {
			"warehouseCode":warehouseCode,
		},
		success: function(resp){
			var data=resp.resuslt;
			console.info(resp.resuslt);
			var element=$("#storageAreaCode");
			element.append("<option value='' >请选择</option>");
			$.each(data,function(index,val){
				element.append("<option value='"+val.STORAGE_AREA_CODE+"'" + ">"+val.STORAGE_AREA_CODE+"</option>");

			});
		}
	});

}

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




function getInfoName2(WAREHOUSECODE,STORAGEAREACODE){
	$.ajax({
		type: "GET",
		url: baseURL + "config/wmsCoreStorageSearch/infoName?warehouseCode="+WAREHOUSECODE+"&storageAreaCode="+STORAGEAREACODE,
		contentType: "application/json",
		success: function(r){
			if(r.resuslt==null||r.resuslt==""){
				$("#AREANAME").val("");
			}else{
				$("#AREANAME").val(r.resuslt);
			}
		},error:function (r) {
			$("#AREANAME").val("");
		}
	});
}

$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		//vm.storage.storageTypeCode=vm.storage.storageTypeCode.toUpperCase();
		var url;
		if($("#Id").val()==null || $("#Id").val()==""){
			url = "config/wmsCoreStorageSearch/save";
		}else {
			url = "config/wmsCoreStorageSearch/update";
		}
		//var url = vm.storage.id == null ? "config/wmsCoreStorageSearch/save" : "config/wmsCoreStorageSearch/update";
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.storage.warehouseCode=$('#warehouseCode').val();
		vm.storage.factoryCode=$('#factoryCode').val();
		vm.storage.storageAreaSearch=$('#storageAreaSearch').val();
		vm.storage.storageAreaCode=$('#storageAreaCode').val();
		vm.storage.priority=$('#priority').val();
		vm.storage.status=$('#status').val();
		vm.storage.id=$('#Id').val();
		//
		//var falg = regular2("storageAreaSearch");
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
		if(falg){

		}

    }
});