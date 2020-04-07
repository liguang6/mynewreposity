//国际化取值
var smallUtil=new smallTools();
var array = new Array("SUCCESS");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array, "M");

var vm = new Vue({
	el : '#rrapp',
	data : {
		outrule : {},
		saveFlag : false
	},
	methods : {
		getInfo : function(id) {
			$.ajax({
				type : "GET",
				url : baseURL + "config/outruleDetail/info/" + id,
				contentType : "application/json",
				success : function(r) {
					vm.outrule = r.wmsCOutRule;
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
						vm.outrule.warehouseCode = $('#warehouseCode')
								.val();
						vm.outrule.outRule = $('#outRule').val();
						vm.outrule.seqNo = $('#seqNo').val();
						vm.outrule.sortField = $('#sortField').val();
						vm.outrule.sortStrategy = $('#sortStrategy').val();
						var url = vm.outrule.id == null ? "config/outruleDetail/save"
								: "config/outruleDetail/update";
						$.ajax({
							type : "POST",
							url : baseURL + url,
							contentType : "application/json",
							data : JSON.stringify(vm.outrule),
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