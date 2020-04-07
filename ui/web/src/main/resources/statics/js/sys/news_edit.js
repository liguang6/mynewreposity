var vm = new Vue({
	el:'#rrapp',
	data:{
		maturgent:{},
		saveFlag:false
	},
	methods:{
		getInfo:function(id){
			$.ajax({
				type: "GET",
				url: baseURL + "sys/news/info/"+id,
				contentType: "application/json",
				success: function(r){
					vm.maturgent = r.objectMap;
					$("#startDate").val(r.objectMap.START_DATE);
					$("#endDate").val(r.objectMap.END_DATE);
				}
			});
		},
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);

		},
	}
});
$("#saveForm").validate({
	submitHandler: function(form){
		// 物料号字段存在小写的转化成大写
		// vm.maturgent.matnr=vm.maturgent.matnr.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.maturgent.WERKS=$('#werks').val();
		vm.maturgent.WH_NUMBER=$('#whNumber').val();
		vm.maturgent.KANBAN_MSG=$('#kanbanMsg').val();
		vm.maturgent.START_DATE=$('#startDate').val();
		vm.maturgent.END_DATE=$('#endDate').val();
		vm.maturgent.KANBAN_TYPE=$('#kanbanType').val();
		var url = vm.maturgent.ID == null ? "sys/news/save" : "sys/news/update";
		$.ajax({
			type: "POST",
			url: baseURL + url,
			contentType: "application/json",
			data: JSON.stringify(vm.maturgent),
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