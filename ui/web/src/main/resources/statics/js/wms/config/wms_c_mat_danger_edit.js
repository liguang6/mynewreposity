var vm = new Vue({
    el:'#rrapp',
    data:{
    	matdanger:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/matdanger/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.matdanger = r.matdanger;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$('#werks').val());
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
$(function () {
	$("#input-group-addon").css('height',$('#goodDates').css('height'));
});
$("#saveForm").validate({
	submitHandler: function(form){
		// 物料号字段存在小写的转化成大写
		vm.matdanger.matnr=vm.matdanger.matnr.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.matdanger.werks=$('#werks').val();
		vm.matdanger.lifnr = $('#lifnr').val();
		vm.matdanger.matnr=$('#matnr').val();
		vm.matdanger.maktx=$('#maktx').val();
		var url = vm.matdanger.id == null ? "config/matdanger/save" : "config/matdanger/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.matdanger),
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