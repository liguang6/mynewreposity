var workshop_edit='';
var workgroup_no_edit='';
var workteam_no_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    	workshoplist:[],
    	workgrouplist:[],
    	workteamlist:[],
    	workshopname:'',
    	workshopcode:'',
    	workgroupcode:'',
    	workteamcode:'',
    	werks:'',
    	dept_id:'',
    	tree_names:'',
    	tree_level:'',
    	saveFlag:false,
    	id:''
    },
    created: function(){
    	this.werks = $("#WERKS").find("option").first().val();
	},
    watch:{
    	werks:{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MASTERDATA_WORKGROUP",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.workshopcode=resp.data[0].CODE;
		        		 }
		        		 if(workshop_edit!=''){
		        			 vm.workshopcode=workshop_edit;
		        		 }
		        		 
		        	  }
		          })
			}
		},
		workshopcode:{//车间改变的时候，获取班组
			handler:function(newVal,oldVal){
				$.ajax({
			      	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
			    	  data:{
			    		  "WORKSHOP":vm.workshopcode,
			    		  "WERKS":$("#WERKS").val()
			    	  },
			    	  success:function(resp){
			    		 vm.workgrouplist = resp.data;
			    		 if(resp.data.length>0){
			    			 vm.workgroupcode=resp.data[0].CODE;
			    			 //获取小班组
			    			 if(workgroup_no_edit==''){//新增的
			    				 getworkteamList(vm.workgrouplist,vm.workgroupcode);
			    			 }
			    			 
			    		 }
			    		 if(workgroup_no_edit!=''){//修改的
			    			 vm.workgroupcode=workgroup_no_edit;
			    		 }
			    	  }
			      });
			}
		}
    },
    methods:{
    	onWorkGroupChange:function(event){
    		var workgroup = event.target.value;
	        if(workgroup === null || workgroup === '' || workgroup === undefined){
	        	return;
	        }
	       
	        //班组改变，查询出小班组
	        $.ajax({
	        	url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
	        	  data:{
	        		  "PARENT_WORKGROUP_NO":workgroup,
		    		  "WORKSHOP":$("#WORKSHOP").val(),
		    		  "TYPE":'1'
	        	  },
	        	  success:function(resp){
	        		  vm.workteamlist = resp.result;
	        		 if(resp.result.length>0){
	        			 vm.workteam=resp.result[0].WORKGROUP_NO;
	        		 }
	        		 
	        	  }
	          })
	          for(var i=0;i<vm.workgrouplist.length;i++){
	        	  if(vm.workgrouplist[i].CODE==workgroup){
	        		  vm.dept_id=vm.workgrouplist[i].DEPT_ID;
	      			  vm.tree_names=vm.workgrouplist[i].TREE_NAMES;
	      			  vm.tree_level=vm.workgrouplist[i].TREE_LEVEL;
	        	  }
	          }
	          
	          //前提是在sys_dept中存在对应的班组
    		/*$.ajax({
		      	  url:baseUrl + "masterdata/workgroup/getWorkGroupByCode",
		    	  data:{
		    		  "WORKGROUP_NO":workgroup,
		    		  "WERKS":$("#WERKS").val()
		    		  },
		    	  success:function(resp){
		    		  if(resp.result.length>0){
		    			  vm.dept_id=resp.result[0].DEPT_ID;
		    			  vm.tree_names=resp.result[0].TREE_NAMES;
		    		  }else{
		    			  js.alert("组织结构中还没有添加该班组");
		    			  return;
		    		  }
		    		 
		    	  }
		      })*/
    	},
    	getWorkGroupInfo:function(){
    		$.ajax({
		      	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
		    	  data:{
		    		  "WORKSHOP":$("#WORKSHOP").val(),
		    		  "TYPE":'0'
		    	  },
		    	  success:function(resp){
		    		 vm.workgrouplist = resp.result;
		    		 if(resp.result.length>0){
	        			 vm.WORKGROUP=resp.result[0].WORKGROUP_NO;
	        		 }
		    	  }
		      });
    		
      },
      getInfo:function(id){
    	  $.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/workgroup/getDeptWorkTeamById",
	            data:{"ID":id},
	            success: function(r){
		            vm.id = r.result.DEPT_ID;
		            vm.werks = r.result.WERKS;
	            	vm.workshopname = r.result.SHOPNAME;
	            	vm.workshopcode = r.result.SHOPCODE;
	            	vm.workgroupcode = r.result.WORKGROUPCODE;
	            	vm.workteamcode = r.result.CODE;
	            	vm.tree_names = r.result.TREE_NAMES;
	            	vm.tree_level = r.result.TREE_LEVEL;
	            	
	            	vm.dept_id = r.result.PARENT_ID;
	            	workgroup_no_edit=r.result.WORKGROUPCODE;
	            	workteam_no_edit=r.result.CODE;
	            	workshop_edit=r.result.SHOPCODE;
          	}
	            
			});
    	  //LIST赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/workgroup/getWorkTeamById",
	            data:{"ID":id},
	            success: function(r){
	            	//查询出班组
	            	/*$.ajax({
	  		      	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
	  		    	  data:{
	  		    		  "WORKSHOP":r.result.WORKSHOPCODE,
	  		    		  "TYPE":"0"
	  		    		  },
	  		    	  success:function(resp){
	  		    		 vm.workgrouplist = resp.result;
	  		    		 vm.workgroupcode=workgroup_no_edit;//重新赋值
	  		    	  }
	  		      	});*/
	            	$.ajax({
				      	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
				    	  data:{
				    		  "WORKSHOP":r.result.WORKSHOPCODE,
				    		  "WERKS":r.result.WERKS
				    	  },
				    	  success:function(resp){
				    		 vm.workgrouplist = resp.data;
				    		 vm.workgroupcode=workgroup_no_edit;
				    		
				    	  }
				      });
	            	//查询小班组
	            	$.ajax({
		  		      	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
		  		    	  data:{
		  		    		  "WORKSHOP":r.result.WORKSHOPCODE,
		  		    		  "PARENT_WORKGROUP_NO":r.result.WORKGROUPCODE,
		  		    		  "TYPE":"1"
		  		    	  },
		  		    	  success:function(resp){
		  		    		 vm.workteamlist = resp.result;
		  		    		 vm.workteamcode=workteam_no_edit;//重新赋值
		  		    	  }
		  		      	});
	            	
	            }
	            
			});
			//查询组织结构表 页面赋值
			//this.$nextTick(function(){
				
			//});
			
  	}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		js.loading("正在保存,请稍候...");
		var url = vm.id =="" ? "masterdata/workgroup/saveDept" : "masterdata/workgroup/updateDept";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        //contentType: "application/json",
	        data: {
	        	"DEPT_ID":vm.id,
	        	"PARENT_ID":$("#PARENT_ID").val(),
	        	"WORK_GROUP_NO":$("#WORKTEAM_NO").val(),//小班组
	        	"WORK_GROUP_NAME":$("#WORKTEAM_NO").find("option:selected").text(),//小班组
	        	"TREE_NAMES":$("#TREE_NAMES").val(),
	        	"DEPT_TYPE":$("#DEPT_TYPE").val(),
	        	"TREE_LEVEL":vm.tree_level,
	        	
	        	"WORKGROUP":$("#WORKGROUP_NO").val(),//班组
	        	"WORKSHOP":$("#WORKSHOP").val(),
	    		"WERKS":$("#WERKS").val()
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
//	  }
//  }); 
 }
});

function getworkteamList(workgrouplist,workgroup){
	//班组改变，查询出小班组
    $.ajax({
    	url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
    	  data:{
    		  "PARENT_WORKGROUP_NO":workgroup,
    		  "WORKSHOP":$("#WORKSHOP").val(),
    		  "TYPE":'1'
    	  },
    	  success:function(resp){
    		  vm.workteamlist = resp.result;
    		 if(resp.result.length>0){
    			 vm.workteamcode=resp.result[0].WORKGROUP_NO;
    		 }
    		 
    	  }
      })
      for(var i=0;i<workgrouplist.length;i++){
    	  if(workgrouplist[i].CODE==workgroup){
    		  vm.dept_id=workgrouplist[i].DEPT_ID;
  			  vm.tree_names=workgrouplist[i].TREE_NAMES;
  			  vm.tree_level=workgrouplist[i].TREE_LEVEL;
    	  }
      }
}