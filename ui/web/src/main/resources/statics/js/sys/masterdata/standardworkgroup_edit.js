var workshop_edit='';
var line_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
		workgrouplist:[],
		WORKSHOP_CODE:'',
		WORKSHOP_NAME:'',
    	TYPE:'',
		WORKGROUP_NO:'',
		WORKGROUP_NAME:'',
		RESPONSIBILITY:'',
		MEMO:'',
		PARENT_WORKGROUP_NO:'',
    		saveFlag:false,
    		id:''
    },
    created: function(){
    	this.PARENT_WORKGROUP_NO = $("#PARENT_WORKGROUP_NO").find("option").first().val();
	},
    watch:{
		TYPE:{
			handler:function(newVal,oldVal){
				if(newVal=='1'){//小班组才查询
				$.ajax({
					url:baseUrl + "masterdata/StandardWorkgroup/getStandardWorkgroupList",
					data:{
						"WORKSHOP":vm.WORKSHOP_CODE,
						"TYPE":"0"
					  },
					success:function(resp){
					   vm.workgrouplist = resp.data;
					   
					}
					});

					$("#PARENT_WORKGROUP_NO_DIV").css('display','block');
				}else{
					$("#PARENT_WORKGROUP_NO_DIV").css('display','none');
				}
			}
		}
	},
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/StandardWorkgroup/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.WORKSHOP_CODE = r.result.WORKSHOP;
	            	vm.TYPE = r.result.TYPE;
					vm.WORKGROUP_NO = r.result.WORKGROUP_NO;
					vm.WORKGROUP_NAME = r.result.WORKGROUP_NAME;
					vm.RESPONSIBILITY = r.result.RESPONSIBILITY;
					vm.MEMO = r.result.MEMO;
					vm.id = r.result.ID;
					//查询标准班组的父班组
					if(r.result.TYPE=='1'){
						$.ajax({
							url:baseUrl + "masterdata/StandardWorkgroup/getStandardWorkgroupList",
							data:{
								"WORKSHOP":r.result.WORKSHOP,
								"TYPE":"0"
							  },
							success:function(resp){
							   vm.workgrouplist = resp.data;
							   vm.PARENT_WORKGROUP_NO= r.result.PARENT_WORKGROUP_NO;
							}
							});
							$("#PARENT_WORKGROUP_NO_DIV").css('display','block');
					}else{
						$("#PARENT_WORKGROUP_NO_DIV").css('display','none');
					}
					
	            }
			});
	
		
  		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		
		vm.WORKSHOP_NAME=$("#WORKSHOP").find("option:selected").text();
		js.loading("正在保存,请稍候...");
		if(vm.TYPE=='0'){
			vm.PARENT_WORKGROUP_NO='';//是选择的班组就没有父节点
		}
		
		var url = vm.id =='' ? "masterdata/StandardWorkgroup/save" : "masterdata/StandardWorkgroup/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        //contentType: "application/json",
	        data: {
				WORKSHOP:vm.WORKSHOP_CODE,
				WORKSHOP_NAME:vm.WORKSHOP_NAME,
	        	TYPE:vm.TYPE,
				WORKGROUP_NO:vm.WORKGROUP_NO,
				WORKGROUP_NAME:vm.WORKGROUP_NAME,
				RESPONSIBILITY:vm.RESPONSIBILITY,
				MEMO:vm.MEMO,
				PARENT_WORKGROUP_NO:vm.PARENT_WORKGROUP_NO,
	        	ID:vm.id
	        },
	        async:false,
	        success: function(data){
	        	js.closeLoading();
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