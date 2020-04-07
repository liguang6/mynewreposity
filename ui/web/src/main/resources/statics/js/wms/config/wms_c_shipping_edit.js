var vm = new Vue({
	el:'#rrapp',
	data:{
		maturgent:{},
		saveFlag:false
	},
	methods:{
		getInfo:function(id,itemNo){
			$.ajax({
				type: "GET",
				url: baseURL + "config/shipping/info/"+id+"/"+itemNo,
				contentType: "application/json",
				success: function(r){
					vm.maturgent = r.objectMap;
				}
			});
		}
	}
});
$("#saveForm").validate({
	submitHandler: function(form){
		vm.maturgent.FREIGHT_COMPANY=$('#freight_company').val();
		vm.maturgent.FREIGHT_LICENSE_PLATE=$('#freight_license_plate').val();
		vm.maturgent.FREIGHT_DRIVER=$('#freight_driver').val();
		vm.maturgent.RECEIVING_CARGO=$('#receiving_cargo').val();
		vm.maturgent.RECEIVING_ADDRESS=$('#receiving_address').val();
		vm.maturgent.RECEIVING_TELEPHONE=$('#receiving_telephone').val();
		vm.maturgent.CONSIGNMENT_NUMBER=$('#consignment_number').val();
		vm.maturgent.DELIVER_DATE=$('#deliver_date').val();
		vm.maturgent.SHIPMENT_DATE=$('#shipment_date').val();
		vm.maturgent.EXPECTED_SERVICE=$('#expected_service').val();
		vm.maturgent.VOLUME_UNIT_PRICE=$('#volume_unit_price').val();
		vm.maturgent.WEIGHT_UNIT_PRICE=$('#weight_unit_price').val();		
		vm.saveFlag = true

//		var url = vm.maturgent.DNNO == null ? "config/shipping/save" : "config/shipping/update";
//		$.ajax({
//			type: "POST",
//			url: baseURL + url,
//			contentType: "application/json",
//			data: JSON.stringify(vm.maturgent),
//			async:false,
//			success: function(data){
//				if(data.code == 0){
//					js.showMessage('操作成功');
//					vm.saveFlag= true;
//				}else{
//					alert(data.msg);
//				}
//			}
//		});
	}
});

