var vm = new Vue({
    el:'#rrapp',
    data:{
    	wharea:{storageModel:"00",putRule:"02",mixFlag:"0",storageCapacityFlag:"0",autoPutawayFlag:"0",autoReplFlag:"0",checkWeightFlag:"0",status:"00",binSearchSequence:'00'},
    	saveFlag:false,
    	whNumber
    },
    created: function(){

	},
	watch:{
		'wharea.whNumber':{
			deep: true,
			handler:function(newVal,oldVal){
		    	// 加载存储类型代码select
			}
		}
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/wharea/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.wharea = r.wharea;
	            	$("#storageTypeCode").attr("disabled",true);
	            }
			});
    	},
    	getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
		},
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		// 小写转化成大写
		vm.wharea.whNumber=$("#whNumber").val();
		var url = vm.wharea.id == null ? "config/wharea/save" : "config/wharea/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.wharea),
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
