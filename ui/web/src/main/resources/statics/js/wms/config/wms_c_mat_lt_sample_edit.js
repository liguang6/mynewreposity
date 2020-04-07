var vm = new Vue({
    el:'#rrapp',
    data:{
    	sample:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/sample/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.sample = r.sample;
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
		vm.sample.matnr=vm.sample.matnr.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.sample.werks=$('#werks').val();
		vm.sample.matnr=$('#matnr').val();
		vm.sample.maktx=$('#maktx').val();
		vm.sample.startDate=$('#startDate').val();
		vm.sample.endDate=$('#endDate').val();
		var url = vm.sample.id == null ? "config/sample/save" : "config/sample/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.sample),
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