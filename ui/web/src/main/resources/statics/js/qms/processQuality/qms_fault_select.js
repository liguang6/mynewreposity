var vm = new Vue({
	el:'#page_div',
	data:{
		FAULT_LIST:[],
		FAULT_TYPE_LIST:[],
		FAULT_SELECT_LIST:[],
		FAULT_LIST_ALL:[],
		SERIOUS_LEVEL_LIST:[],
		TYPE:'',
	},
	watch:{
		
	},
	methods:{
		getFaultList:function(faultName){
			var _FAULT_TYPE_LIST=[];
			var _FAULT_LIST=[];
			var obj=ajaxGetFaultList();
			_FAULT_TYPE_LIST=obj.FAULT_TYPE_LIST;
			_FAULT_LIST=obj.FAULT_LIST;
			
			if(faultName==null||faultName==""){
				this.FAULT_TYPE_LIST=_FAULT_TYPE_LIST
				this.FAULT_LIST=_FAULT_LIST
			}			
			this.FAULT_LIST_ALL=_FAULT_LIST;
			//console.info(this.FAULT_TYPE_LIST)
		},
		setSeriousLevel:function(fault){//str:F111#S,F112#A
			vm.SERIOUS_LEVEL_LIST=[]
			var str = fault.SERIOUS_LEVEL;
			var fault_type = fault.FAULT_TYPE;
			if(str){
				$.each(str.split(","),function(i,s){
					let f={};
					f.FAULT_CODE=s.split("#")[0]
					f.SERIOUS_LEVEL=s.split("#")[1]				
					vm.SERIOUS_LEVEL_LIST.push(f)
				})
			}
			$("#C_FAULT_TYPE").html(fault_type);
		},
		setFaultType:function(type){
			if(type=='other'){
				vm.showSearch=false
				vm.TYPE='other'
			}else{
				vm.showSearch=true
				vm.TYPE=''
			}
			
		}
		
		
	},
	created:function(){
		//this.getFaultList();
	},

})

$(document).on("keydown","#faultNameSearch",function(e){
	if(e.keyCode=="13"){
		var obj=ajaxGetFaultList($(e.target).val())
		var _FAULT_LIST=obj.FAULT_LIST
		console.info(JSON.stringify(_FAULT_LIST))
		vm.FAULT_LIST_ALL=_FAULT_LIST;
	}
	
})


function ajaxGetFaultList(faultName){
	var _fault={};
	$.ajax({
		url:baseUrl+'/processQuality/test/getFaultList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			faultName:faultName,
		},
		success:function(resp){
			if(resp.msg=='success'){	
				_FAULT_TYPE_LIST=resp.faultTypeList;
				_FAULT_LIST=resp.data;
				_fault.FAULT_TYPE_LIST=_FAULT_TYPE_LIST;
				_fault.FAULT_LIST=_FAULT_LIST;
				//console.info(this.FAULT_TYPE_LIST)
			}
		}			
	})
	return _fault;
}


