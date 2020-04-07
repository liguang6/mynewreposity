//国际化取值
var smallUtil=new smallTools();
var array = new Array("SUCCESS");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array, "M");

var vm = new Vue({
	el : '#rrapp',
	data : {
		controlsearch : {},
		saveFlag : false,
		ctlList:[],
		searchList:[],
		ruleList:[]
	},
	methods : {
		getInfo : function(id) {
			$.ajax({
				type : "GET",
				async: false,
				url : baseURL + "config/controlsearch/info/" + id,
				contentType : "application/json",
				success : function(r) {
					let flag = r.wmsCControlSearch.controlFlag;
					let storageAreaSearch = r.wmsCControlSearch.storageAreaSearch;
					let outRule = r.wmsCControlSearch.outRule;
					vm.controlsearch = r.wmsCControlSearch;
				    vm.getControlFlag();	
				    vm.getAreaSearch();
				    vm.getOutRule();
				   // vm.ctlList = vm.ctlList.filter(ele=>{
				   //return ele.controlFlag!=flag
				   //});				    
				   // $("#controlFlag").append("<option value='"+flag+"'" + "selected='true'>"+flag+"</option>");
				    $("#controlFlag").val(flag)
				    $("#storageAreaSearch").val(storageAreaSearch)
				    $("#outRule").val(outRule)
				}
			});
		},
		getPlantNoFuzzy : function() {
			getPlantNoSelect("#factoryCode", null, null);
		},
		getWhNoFuzzy : function() {
			getWhNoSelect("#warehouseCode", null, null);
		},
		getControlFlag(){
			let whNo = ''
			if($("#warehouseCode").val()){
				 whNo =  $("#warehouseCode").val()
				vm.controlsearch.warehouseCode = $("#warehouseCode").val()
			}else{
				 whNo =  vm.controlsearch.warehouseCode
			}	
			$.ajax({
				type : "POST",
				async: false,
				url : baseURL + "config/controlsearch/getControlFlag",
				data : JSON.stringify(vm.controlsearch),
				contentType : "application/json",
				success : function(r) {
					vm.ctlList = r.ctlList;
				}
			});
			
		},
		getAreaSearch(){
			let whNo = ''
			if($("#warehouseCode").val()){
				 whNo =  $("#warehouseCode").val()
				vm.controlsearch.warehouseCode = $("#warehouseCode").val()
			}else{
				 whNo =  vm.controlsearch.warehouseCode
			}	
			$.ajax({
				type : "POST",
				async: false,
				url : baseURL + "config/controlsearch/getAreaSearch",
				data : JSON.stringify(vm.controlsearch),
				contentType : "application/json",
				success : function(r) {
					vm.searchList = r.searchList;
				}
			});
			
		},
		getOutRule(){
			let whNo = ''
			if($("#warehouseCode").val()){
				 whNo =  $("#warehouseCode").val()
				vm.controlsearch.warehouseCode = $("#warehouseCode").val()
			}else{
				 whNo =  vm.controlsearch.warehouseCode
			}	
			$.ajax({
				type : "POST",
				async: false,
				url : baseURL + "config/controlsearch/getOutRule",
				data : JSON.stringify(vm.controlsearch),
				contentType : "application/json",
				success : function(r) {
					vm.ruleList = r.ruleList;
				}
			});
			
		},
	}
});
$("#saveForm")
		.validate(
				{
					submitHandler : function(form) {
						vm.controlsearch.warehouseCode = $('#warehouseCode')
								.val();
						
						vm.controlsearch.controlFlag = $('#controlFlag').val();
						vm.controlsearch.whBusinessType = $('#whBusinessType')
								.val();
						vm.controlsearch.lgort = $('#lgort').val();
						vm.controlsearch.stockType = $('#stockType').val();
						vm.controlsearch.storageAreaSearch = $(
								'#storageAreaSearch').val();
						vm.controlsearch.outRule = $('#outRule').val();
						

						var url = vm.controlsearch.id == null ? "config/controlsearch/save"
								: "config/controlsearch/update";
						$.ajax({
							type : "POST",
							url : baseURL + url,
							contentType : "application/json",
							data : JSON.stringify(vm.controlsearch),
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