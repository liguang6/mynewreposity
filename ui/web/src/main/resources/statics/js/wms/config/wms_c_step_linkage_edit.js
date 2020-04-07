//国际化取值
var smallUtil=new smallTools();
var array = new Array("SUCCESS");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array, "M");

var vm = new Vue({
	el : '#rrapp',
	data : {
		steplinkage : {},
		saveFlag : false
	},
	methods : {
		getInfo : function(id) {
			$.ajax({
				type : "GET",
				url : baseURL + "config/steplinkage/info/" + id,
				contentType : "application/json",
				success : function(r) {
					vm.steplinkage = r.wmsCStepLinkage;
				}
			});
		},
		
		getWhNoFuzzy : function() {
			getWhNoSelect("#warehouseCode", null, null);
		},
	}
});
$("#saveForm").validate({
					submitHandler : function(form) {
						vm.steplinkage.warehouseCode = $('#warehouseCode').val();
						vm.steplinkage.werksFrom = $('#werksFrom').val();
						vm.steplinkage.werksTo = $('#werksTo').val();
						vm.steplinkage.docType = $('#docType').val();
						vm.steplinkage.ekgrp = $('#ekgrp').val();
						vm.steplinkage.taxCode = $('#taxCode').val();
						vm.steplinkage.buName = $('#buName').val();
						vm.steplinkage.ekorg = $('#ekorg').val();
						vm.steplinkage.bukrs = $('#bukrs').val();
						vm.steplinkage.lifnr = $('#lifnr').val();
						vm.steplinkage.kunnr = $('#kunnr').val();
						vm.steplinkage.soDocType = $('#soDocType').val();
						vm.steplinkage.soGroup = $('#soGroup').val();
						vm.steplinkage.soChannel = $('#soChannel').val();
						vm.steplinkage.productGroup = $('#productGroup').val();
						vm.steplinkage.shippingPoint = $('#shippingPoint').val();
						
						var url = vm.steplinkage.id == null ? "config/steplinkage/save"
								: "config/steplinkage/update";
						$.ajax({
							type : "POST",
							url : baseURL + url,
							contentType : "application/json",
							data : JSON.stringify(vm.steplinkage),
							async : false,
							success : function(data) {
								if (data.code == 0) {
									js.showMessage(languageObj.SUCCESS);
									vm.saveFlag = true;
								} else {
									alert(data.msg);
								}
							}
						});
					}
				});