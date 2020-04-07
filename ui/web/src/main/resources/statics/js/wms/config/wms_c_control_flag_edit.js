//国际化取值
var smallUtil=new smallTools();
var array = new Array("SUCCESS");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array, "M");

var vm = new Vue({
	el : '#rrapp',
	data : {
		controlflag : {},
		saveFlag : false
	},
	methods : {
		getInfo : function(id) {
			$.ajax({
				type : "GET",
				url : baseURL + "config/controlflag/info/" + id,
				contentType : "application/json",
				success : function(r) {
					vm.controlflag = r.wmsCControlFlag;
				}
			});
		},
		getPlantNoFuzzy : function() {
			getPlantNoSelect("#factoryCode", null, null);
		},
		getWhNoFuzzy : function() {
			getWhNoSelect("#warehouseCode", null, null);
		},
	}
});
$("#saveForm")
		.validate(
				{
					submitHandler : function(form) {
						vm.controlflag.warehouseCode = $('#warehouseCode')
								.val();
						
						vm.controlflag.controlFlag = $('#controlFlag').val();
						vm.controlflag.controlFlagType = $('#controlFlagType')
								.val();
						vm.controlflag.desc = $('#desc').val();
						
						var url = vm.controlflag.id == null ? "config/controlflag/save"
								: "config/controlflag/update";
						$.ajax({
							type : "POST",
							url : baseURL + url,
							contentType : "application/json",
							data : JSON.stringify(vm.controlflag),
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