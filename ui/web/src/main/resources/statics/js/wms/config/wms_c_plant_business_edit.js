var vm = new Vue({
    el:'#rrapp',
    data:{
    	plantbusiness:{},
    	lgortList:[],
    	saveFlag:false
    },
   
    created:function(){	
		this.getWmsBusinessCodeList();
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/plantbusiness/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.plantbusiness.id=r.wmsCPlantBusiness.ID;
	            	vm.plantbusiness.werks=r.wmsCPlantBusiness.WERKS;
	            	vm.plantbusiness.synFlag=r.wmsCPlantBusiness.SYNFLAG;
	            	vm.plantbusiness.approvalFlag=r.wmsCPlantBusiness.APPROVALFLAG;
	            	vm.plantbusiness.overstepReqFlag=r.wmsCPlantBusiness.OVERSTEPREQFLAG;
	            	vm.plantbusiness.overstepHxFlag=r.wmsCPlantBusiness.OVERSTEPHXFLAG;
	            	vm.plantbusiness.lgort=r.wmsCPlantBusiness.LGORT;
                    vm.plantbusiness.publicwerks=r.wmsCPlantBusiness.PUBLICWERKS;
	            	$("#businessClass option[value='"+r.wmsCPlantBusiness.BUSINESSCLASS+"']").attr("selected",true);
	            	$("#businessType option[value='"+r.wmsCPlantBusiness.BUSINESSTYPE+"']").attr("selected",true);
	            	$("#businessName option[value='"+r.wmsCPlantBusiness.BUSINESSNAME+"']").attr("selected",true);
	            	$("#sobkz option[value='"+r.wmsCPlantBusiness.SOBKZ+"']").attr("selected",true);
	            	vm.getWmsBusinessCodeList(r.wmsCPlantBusiness.BUSINESSCODE);
	            	vm.plantbusiness.businessCode=r.wmsCPlantBusiness.BUSINESSCODE;
	            	$("#businessClass").attr("disabled",true);
	            	$("#businessType").attr("disabled",true);
	            	$("#businessName").attr("disabled",true);
	            	$("#sobkz").attr("disabled",true);
	            	$("#businessCode").attr("disabled",true);
	            	vm.lgortList=getLgortList();
	            }
			});
    	},
    	getWmsBusinessCodeList:function(selectedVal){
            if($("#businessClass").val()==''){
    			return ;
    		}
    		if($("#businessType").val()==''){
    			return ;
    		}
    		if($("#businessName").val()==''){
    			return ;
    		}
    		if($("#sobkz").val()==''){
    			return ;
    		}
    		$.ajax({
    	        type: "POST",
    	        url: baseURL + "config/plantbusiness/getWmsBusinessCodeList",
    	        dataType: "json",
    	        data: {
    	        	"businessClass":$("#businessClass").val(),
    	        	"businessType":$("#businessType").val(),
    	        	"businessName":$("#businessName").val(),
    	        	"sobkz":$("#sobkz").val(),	
    	        },
    	        success: function(data){
    	        	if(data.code == 0){
                        var result=data.businessCodeList;
                        var option="<option value=''>请选择</option>";
                        $.each(result,function(i,val){
                        	if(selectedVal==val.BUSINESS_CODE){
                            	option+="<option value='"+val.BUSINESS_CODE+"' wmsMoveType='"+val.WMS_MOVE_TYPE+"' selected>"+val.BUSINESS_CODE+"</option>";
                        	}else{
                            	option+="<option value='"+val.BUSINESS_CODE+"' wmsMoveType='"+val.WMS_MOVE_TYPE+"'>"+val.BUSINESS_CODE+"</option>";
                        	}
                        });
                        $("#businessCode").html(option);
                    }
    	        }
    	    });
    	},
    	plantChange:function(){
    		vm.lgortList=getLgortList();
		}
    }
});
$(document).ready(function(){
	$("#businessClass").change(function(){
		vm.getWmsBusinessCodeList();
	});
	$("#businessType").change(function(){
		vm.getWmsBusinessCodeList();
	});
	$("#businessName").change(function(){
		vm.getWmsBusinessCodeList();
	});
	$("#sobkz").change(function(){
		vm.getWmsBusinessCodeList();
	});
});
$("#saveForm").validate({
	submitHandler: function(form){
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.plantbusiness.werks=$('#werks').val();
		vm.plantbusiness.businessCode=$('#businessCode').val();
		vm.plantbusiness.lgort=$('#lgort').val();
        vm.plantbusiness.publicwerks = $('#publicwerks').val();
		if($("#businessCode").attr("wmsMoveType")=='411_K' || 
			$("#businessCode").attr("wmsMoveType")=='412_K'){
			if(vm.plantbusiness.lgort==''){
				js.showErrorMessage("SAP移动类型【411_K、412_K】需录入中转库");
				return ;
			}
		}
		var url = vm.plantbusiness.id == null ? "config/plantbusiness/save" : "config/plantbusiness/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.plantbusiness),
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
function getLgortList(){
	var lgortList=[];
	//查询库位
  	$.ajax({
      	url:baseUrl + "common/getLoList",
      	async:false,
      	data:{
      		"DEL":'0',
      		"WERKS":vm.plantbusiness.werks,
      		},
      	success:function(resp){
      		lgortList = resp.data;   		
      	}
     });

  	return lgortList;
}