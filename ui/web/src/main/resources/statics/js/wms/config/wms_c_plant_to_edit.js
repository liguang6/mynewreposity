var vm = new Vue({
	el:'#rrapp',
	data:{
		lgortlist:[],
		WERKS_F:"",
		LGORT_F:"",
		WERKS_T:"",
		LGORT_T:"",
		WMS_MOVE_TYPE:"",
		SAP_MOVE_TYPE:"",
		SOBKZ:"",
		ID:"",
		saveFlag:false
	},
	methods:{
		
	},
	watch:{
		WERKS_F:function(newVal,oldVal){
			console.info(new Date())
			getLgortList(newVal)	
		}
	},
	created:function(){
		this.WERKS=$("#WERKS_F").val()
	}
})

function getLgortList(WERKS){
    //查询库位
  	$.ajax({
      	url:baseUrl + "common/getLoList",
      	async:false,
      	data:{
      		"WERKS":WERKS
      		},
      	success:function(resp){
      		vm.lgortlist = resp.data; 
      	}
      });
  	
}

$("#saveForm").validate({
	submitHandler: function(form){
		var url ="config/cPlantTo/save";
		console.info($("form").serialize())
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	       // contentType: "application/form-data",
	        data: $("form").serialize(),
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

