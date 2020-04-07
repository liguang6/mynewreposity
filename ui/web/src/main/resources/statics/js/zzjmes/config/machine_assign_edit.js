var workshop_edit='';
var devicetype_edit='';
var workgroup_edit='';
var line_edit='';
var process_edit='';
var section_edit='';
var machine_edit='';
var specificationModel_edit='';
var workteam_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    	machineAssign: 
    	{werks:'',
    	werks_name:'',
    	workshop:'',
    	workshop_name:'',
    	workgroup:'',
    	workgroup_name:'',
    	workteam:'',
    	workteam_name:'',
    	line:'',
    	line_name:'',
    	process:'',
    	section:'',
    	devicetype:'',
    	device_name:'',
    	machine:'',
    	machine_name:'',
    	machinecode:'',
    	specificationModel:'',
    	status:'',
    	username:'',
    	assets:'',
    	memo:'',
    	id:''},
    	
    	workshoplist:[],
    	workgrouplist:[],
    	workteamlist:[],
    	linelist:[],
    	processlist:[],
    	sectionlist:[],
    	devicetypelist:[],
    	machinelist:[],
    	saveFlag:false
    },
    created: function(){
    	this.machineAssign.werks = $("#werks").find("option").first().val();
    	this.machineAssign.status = $("#status").find("option").first().val();
	},
    watch:{
    	"machineAssign.werks":{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.machineAssign.workshop=resp.data[0].CODE;
		        			 if(workshop_edit!=''){
		        				 vm.machineAssign.workshop=workshop_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
		          //设备类型
		          $.ajax({
		        	  url:baseUrl + "masterdata/device/getDeviceTypeByCode",
		        	  data:{
		        		  "DEVICE_CODE":''
		        	  },
		        	  
		        	  success:function(resp){
		        		 vm.devicetypelist = resp.data;
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.devicetype=resp.data[0].DEVICECODE;
		        			 if(devicetype_edit!=''){
		        				 vm.machineAssign.devicetype=devicetype_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
		          //带出机台名称
		          $.ajax({
		        	  url:baseUrl + "masterdata/device/getDeviceList",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "STATUS":"00",
		        		  //"DEVICE_CODE":vm.machineAssign.devicetype,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.machinelist = resp.data;
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.machine=resp.data[0].MACHINE_CODE;
		        			 if(machine_edit!=''){
		        				 vm.machineAssign.machine=machine_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
			}
		
		},
		"machineAssign.workshop":{
			handler:function(newVal,oldVal){
				//班组
				 $.ajax({
		        	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
		        	  data:{
		        		  "WERKS":vm.machineAssign.werks,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workgrouplist = resp.data;
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.workgroup=resp.data[0].CODE;
		        			 //新增的时候
		        			 if(workgroup_edit==''){//获取小班组
		        				 getworkteamList(vm.machineAssign.workgroup,newVal,vm.machineAssign.werks);
		        			 }
		        			 if(workgroup_edit!=''){
		        				 vm.machineAssign.workgroup=workgroup_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
		        //线别
		          $.ajax({
		        	  url:baseUrl + "masterdata/getUserLine",
		        	  data:{
		        		  "WERKS":vm.machineAssign.werks,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.linelist = resp.data;
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.line=resp.data[0].CODE;
		        			 if(line_edit!=''){
		        				 vm.machineAssign.line=line_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
		          //工序
		        $.ajax({
		        	  url:baseUrl + "masterdata/getWorkshopProcessList",
		        	  data:{
		        		  "WERKS":vm.machineAssign.werks,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.processlist = resp.data;
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.process=resp.data[0].PROCESS_CODE;
		        			 if(process_edit!=''){
		        				 vm.machineAssign.process=process_edit;
		        			 }
		        			 
		        		 }
		        		 
		        	  }
		          })
			}
		
		
		},
		"machineAssign.devicetype":{
			handler:function(newVal,oldVal){
				
				$.ajax({
		        	  url:baseUrl + "masterdata/device/getDeviceList",
		        	  data:{
		        		  "WERKS":vm.machineAssign.werks,
		        		  "STATUS":"00",
		        		  "DEVICE_CODE":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.machinelist = resp.data;
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.machine=resp.data[0].MACHINE_CODE;
		        			 if(machine_edit!=''){
		        				 vm.machineAssign.machine=machine_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
			}
		},
		"machineAssign.machine":{
			handler:function(newVal,oldVal){
				vm.machineAssign.machinecode=newVal;
				
				$.ajax({
		        	  url:baseUrl + "masterdata/device/getDeviceList",
		        	  data:{
		        		  "WERKS":$("#werks").val(),
		        		  "STATUS":"00",
		        		  "WERKS_MACHINE":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 if(resp.data.length>0){
		        			 //vm.machineAssign.specificationModel=resp.data[0].SPECIFICATION_MODEL;
		        			 if(specificationModel_edit!=''){
		        				 vm.machineAssign.specificationModel=specificationModel_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
			}
			
		}
		
    },
    methods:{
    	getInfo:function(id){
      	  
        	//页面赋值
    			$.ajax({
    	            type: "GET",
    	            url: baseURL + "zzjmes/machineAssign/info/"+id,
    	            contentType: "application/json",
    	            success: function(r){
    	            	vm.machineAssign = r.machineAssign;
    	            	workshop_edit=vm.machineAssign.workshop;
    	            	devicetype_edit=vm.machineAssign.devicetype;
    	            	workgroup_edit=vm.machineAssign.workgroup;
    	            	line_edit=vm.machineAssign.line;
    	            	process_edit=vm.machineAssign.process;
    	            	section_edit=vm.machineAssign.section;
    	            	machine_edit=vm.machineAssign.machinecode;
    	            	specificationModel_edit=vm.machineAssign.specificationModel;
    	            	workteam_edit=vm.machineAssign.workteam;
    	            	vm.machineAssign.id=id;
    	            	
    	            }
    			});
    	
    			
    		
      		},
    	onWorkGroupChange:function(event){
    			$.ajax({
  	        	  url:baseUrl + "masterdata/getTeamList",
  	        	  data:{
  	        		  "WORKGROUP":workgroup_edit,
  		    		  "WORKSHOP":workshop_edit,
  		    		  "WERKS":$("#werks").val()
  	        	  },
  	        	  success:function(resp){
  	        		  vm.workteamlist = resp.data;
  	        		 if(workteam_edit!=''){//修改状态 小班组 查询，并赋值 
  	        			 vm.machineAssign.workteam=workteam_edit;
  	        		 }
  	        		
  	        	  }
  	          })
  	       
    		
    		var workgroup = event.target.value;
	        if(workgroup === null || workgroup === '' || workgroup === undefined){
	        	return;
	        }
			$.ajax({
	        	  url:baseUrl + "masterdata/getTeamList",
	        	  data:{
	        		  "WORKGROUP":workgroup,
		    		  "WORKSHOP":$("#workshop").val(),
		    		  "WERKS":$("#werks").val()
	        	  },
	        	  success:function(resp){
	        		  vm.workteamlist = resp.data;
	        		 if(resp.data.length>0){
	        			 vm.machineAssign.workteam=resp.data[0].CODE;
	        			 
	        		 }
	        		
	        		
	        	  }
	          })
		
    	},
    	checkUserInfo:function(){
    		$.ajax({
				url:baseURL+"sys/user/checkUserMap",
				dataType : "json",
				type : "post",
				data : {"user":$("#username1").val()},
				async: false,
				success: function (resp) { 
					if(resp.data.length>0){
						return true;
					}else{
						js.alert("该人员不存在!");
						$("#username1").val("");
						return false;
						
						
					}
				}
			});
    	},
		getUserInfoNoFuzzy:function(){
			getUserInfoNoSelect("#username1",fn_getuserCode);
		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		$.ajax({
			url:baseURL+"sys/user/checkUserMap",
			dataType : "json",
			type : "post",
			data : {"user":$("#username1").val()},
			async: false,
			success: function (resp) { 
				if(resp.data.length>0){
					js.loading("正在保存,请稍候...");
					var url = vm.machineAssign.id =='' ? "zzjmes/machineAssign/save" : "zzjmes/machineAssign/update";
					vm.machineAssign.line_name=$("#line").find("option:selected").text();
					vm.machineAssign.workshop_name=$("#workshop").find("option:selected").text();
					vm.machineAssign.werks_name=$("#werks").find("option:selected").text();
					vm.machineAssign.workteam_name=$("#workteam").find("option:selected").text();
					vm.machineAssign.workgroup_name=$("#workgroup").find("option:selected").text();
					vm.machineAssign.process_name=$("#process").find("option:selected").text();
					vm.machineAssign.device_name=$("#devicetype").find("option:selected").text();
					vm.machineAssign.machine_name=$("#machine").find("option:selected").text();//机台名称
					
					$.ajax({
				        type: "POST",
				        url: baseURL + url,
				        dataType : "json",
				        data: {
				        	"machinecode":vm.machineAssign.machinecode,
				        	"machine":vm.machineAssign.machine_name,
				        	"process":vm.machineAssign.process,
				        	"process_name":vm.machineAssign.process_name,
				        	"section":vm.machineAssign.section,
				        	"workgroup_name":vm.machineAssign.workgroup_name,
				        	"workgroup":vm.machineAssign.workgroup,
				        	"workteam_name":vm.machineAssign.workteam_name,
				        	"workteam":vm.machineAssign.workteam,
				        	"werks":vm.machineAssign.werks,
				        	"werks_name":vm.machineAssign.werks_name,
				        	"workshop":vm.machineAssign.workshop,
				        	"workshop_name":vm.machineAssign.workshop_name,
				        	"line":vm.machineAssign.line,
				        	"line_name":vm.machineAssign.line_name,
				        	"devicetype":vm.machineAssign.devicetype,
				        	"device_name":vm.machineAssign.device_name,
				        	"specificationModel":vm.machineAssign.specificationModel,
				        	"status":vm.machineAssign.status,
				        	"username1":vm.machineAssign.username,
				        	"memo":vm.machineAssign.memo,
				        	"assets":vm.machineAssign.assets,
				        	"id":vm.machineAssign.id
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
				}else{
					js.alert("该人员不存在!");
					$("#username1").val("");
					return false;
					
				}
			}
		});
		
	}
});

function getworkteamList(workgroup,workshop,werks){
	$.ajax({
  	  url:baseUrl + "masterdata/getTeamList",
  	  data:{
  		  "WORKGROUP":workgroup,
  		  "WORKSHOP":workshop,
  		  "WERKS":werks
  	  },
  	  success:function(resp){
  		  vm.workteamlist = resp.data;
  		 if(resp.data.length>0){
  			vm.machineAssign.workteam=resp.data[0].CODE;
  			 
  		 }
  		
  	  }
    })
}

function fn_getuserCode(userInfo){
	console.info("kkk"+userInfo.USERNAME);
	$("#username1").val(userInfo.USERNAME);
	vm.machineAssign.username=userInfo.USERNAME;
}