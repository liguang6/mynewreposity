var vm = new Vue({
    el:'#rrapp',
    data:{
    	matusing:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/matusing/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.matusing = r.wmsCMatUsing;
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
		vm.matusing.matnr=vm.matusing.matnr.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.matusing.werks=$('#werks').val();
		vm.matusing.matnr=$('#matnr').val();
		vm.matusing.maktx=$('#maktx').val();
		vm.matusing.startDate=$('#startDate').val();
		vm.matusing.endDate=$('#endDate').val();
		var url = vm.matusing.id == null ? "config/matusing/save" : "config/matusing/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.matusing),
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