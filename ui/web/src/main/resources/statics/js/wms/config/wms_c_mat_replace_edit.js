//国际化取值
var smallUtil=new smallTools();
var array = new Array("SUCCESS");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

var vm = new Vue({
    el:'#rrapp',
    data:{
    	matreplace:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/matreplace/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.matreplace = r.wmsCMatReplace;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getMaterialNoFuzzy:function(){
			getMaktxByMatnr("#werks","#matnr","#maktx");
		},
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		// 物料号字段存在小写的转化成大写
		vm.matreplace.matnr=vm.matreplace.matnr.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.matreplace.werks=$('#werks').val();
		vm.matreplace.matnr=$('#matnr').val();
		vm.matreplace.maktx=$('#maktx').val();
		vm.matreplace.cMaterialCode=$('#cMaterialCode').val();
		vm.matreplace.cMaterielDesc=$('#cMaterielDesc').val();
		vm.matreplace.innerlMaterialCode=$('#innerlMaterialCode').val();
		vm.matreplace.innerlMaterialDesc=$('#innerlMaterialDesc').val();
		
		var url = vm.matreplace.id == null ? "config/matreplace/save" : "config/matreplace/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.matreplace),
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                    js.showMessage(languageObj.SUCCESS);
                    vm.saveFlag= true;
                }else{
                    alert(data.msg);
                }
	        }
	    });
    }
});