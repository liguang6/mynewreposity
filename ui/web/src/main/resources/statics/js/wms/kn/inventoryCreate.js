var mydata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		saveFlag:false,
		inventory:{},
		whNumber:"",
		warehourse:[],
		relatedareaname:[]
	},
	created:function(){
		loadWhNumber();
		loadLgort();
	},
	methods: {
		
		onPlantChange:function(){
			loadWhNumber();
			loadLgort();
		},
		more: function(e){
			$("#moreGrid").html("")
			$.each(mydata,function(i,d){
				var tr=$("<tr index="+i+"/>");
				var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
				var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text'name='val_more' style='width:100%;height:30px;border:0'> ")
				$(tr).append(td_1)
				$(tr).append(td_2)
				$("#moreGrid").append(tr)
			})
			
			layer.open({
			  type: 1,
			  title:'选择更多',
			  closeBtn: 1,
			  btn:['确认'],
			  offset:'t',
			  resize:true,
			  maxHeight:'300',
			  area: ['300px','300px'],
			  skin: 'layui-bg-green', //没有背景色
			  shadeClose: false,
			  content: "<div id='more_div'>"+$('#layer_more').html()+"</div>",
			  btn1:function(index){
				  var ckboxs=$("#more_div tbody").find("tr").find("input[type='checkbox']:checked");
				  var values=[]
				  $.each(ckboxs,function(i,ck){
					  var input=$(ck).parent("td").next("td").find("input")
					  if($(input).val().trim().length>0){
						  values.push($(input).val())
					  }					  
				  })
				  $(e).val(values.join(","))
			  }
			});
	  },
		onWhChange:function(){
			$.ajax({
					  type:'post',
					  datatype:'json',
		        	  url:baseUrl + "kn/inventory/getWhManagerList",
		        	  data:{"WERKS":$("#werks").val(),"WH_NUMBER":$("#whNumber").val()},
		        	  success:function(resp){
		        		 vm.relatedareaname = resp.list;
		        		$("#whManager").empty();
		        		for (var i = 0; i < resp.list.length; i++) {  
		                    $("#whManager").append("<option value='" + resp.list[i].MANAGER_STAFF + "'>" + resp.list[i].MANAGER + "</option>");  
		                }  
		                $("#whManager").multiselect("destroy").multiselect({  
		                    // 自定义参数，按自己需求定义  
		                    nonSelectedText : '--请选择--', 
		                    allSelectedText:'全部',
		                   // selectAllText:"全部",
		                    maxHeight : 350,
		                    buttonWidth: '100%',
		                  //  includeSelectAllOption : true,   
		                    numberDisplayed : 30  
		                });  
		        	  }
		          })
		}
	}
});
$(function () {
	$("#input-group-addon").css('height',$('#proportion').css('height'));
	$('#whManager').multiselect({
		//includeSelectAllOption: true,//是否现实全选
		allSelectedText:'全部',
		nonSelectedText:'--请选择--',
		//selectAllText:"全部",
		buttonWidth: '100%',
	});
	$('#lgort').multiselect({
		//includeSelectAllOption: true,//是否现实全选
		allSelectedText:'全部',
		nonSelectedText:'请选择',
		//selectAllText:"全部",
		buttonWidth: '100%',
	});
	//vm.addtaginput($("#lifnr"));
	$(document).on("click","#checkall",function(e){

		if ($(e.target).is(":checked")) {
			check_All_unAll("#moreGrid",true);	
		}
		if($(e.target).is(":checked")==false){
			//alert("反选")
			check_All_unAll("#moreGrid",false);
		}
	});
	//粘贴
	$(document).on("paste","#more_div input[type='text']",function(e){
		setTimeout(function(){
			//alert($(e.target).val())
			var tr=$(e.target).parent("td").parent("tr")
			
			var index = parseInt($("#more_div tbody").find("tr").index(tr));
			console.info(index)
			 var copy_text=$(e.target).val();		 
			 var dist_list=copy_text.split(" ");
			
			 if(dist_list.length <=0){
				 return false;
			 }
			 $.each(dist_list,function(i,value){
				 console.info(value)
				 $("#more_div tbody").find("tr").eq(index+i).find("input").val(value)
			 })
			 
		},100)
		
	})
});
$("#saveForm").validate({

	submitHandler: function(form){
		var url = baseURL+"kn/inventory/create";
		var lgort=$("#lgort").val()!=null ? $("#lgort").val().toString() : "";
		var whManager=$("#whManager").val()!=null ? $("#whManager").val().toString() : "";
		$.ajax({
			type: "POST",
		    url: url,
		    dataType : "json",
		    async:false,
			data : {
				WERKS:$("#werks").val(),
				WH_NUMBER:$("#whNumber").val(),
				INVENTORY_TYPE:$("#inventoryType").val(),
				LIFNR:$("#lifnr").val(),
				LGORT:lgort,
				WH_MANAGER:whManager,
				PROPORTION:$("#proportion").val()
			},
		    success: function(r){
		    	if(r.code === 0){
		        	vm.saveFlag= true;
		        	js.showMessage(r.msg);
				}else{
					js.showMessage(r.msg);
				}
			}
		});
	}
});
function loadWhNumber(){
	var plantCode = $("#werks").val();
   //初始化仓库
    $.ajax({
        url:baseUrl + "common/getWhDataByWerks",
        data:{"WERKS":plantCode},
        success:function(resp){
            vm.warehourse = resp.data;
            $("#whNumber").empty();
    		for (var i = 0; i < resp.data.length; i++) {  
    			var whNumber=resp.data[i].WH_NUMBER;
                $("#whNumber").append("<option value='" +whNumber+ "'>" + whNumber + "</option>");  
            }  
        }
    });
}
function loadLgort(){
	//查询库位
  	$.ajax({
		  type:'post',
		  url:baseUrl + "kn/inventory/lgortlist",
		  dataType:"json",
      	data:{
      		"DEL":'0',
      		"WERKS":$("#werks").val(),
//      		"SOBKZ":"Z",
//      		"BAD_FLAG":'0'
      		},
      	success:function(resp){
      		var lgortlist = resp.list;
      	// 加载库位
			$("#lgort").empty();
    		for (var i = 0; i < lgortlist.length; i++) {  
    			var lgort=lgortlist[i].lgort;
                $("#lgort").append("<option value='" +lgort+ "'>" + lgort + "</option>");  
            }  
            $("#lgort").multiselect("destroy").multiselect({  
                // 自定义参数，按自己需求定义  
                nonSelectedText : '--请选择--', 
                allSelectedText:'全部',
                selectAllText:"全部",
                maxHeight : 350,
                buttonWidth: '100%',
                includeSelectAllOption : true,   
                numberDisplayed : 30  
            });  
      	}
     }); 
}
function refreshMore(){
	  $.each($("#more_div").find("input[type='text']"),function(i,input){
		  $(input).val("")
	  })
}

function addMore(){
	var tr=$("<tr />");
	  var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' /> ")
	  var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text' style='width:100%;height:30px;border:0'> ")
	  $(tr).append(td_1)
	  $(tr).append(td_2)
	  $("#moreGrid tbody").append(tr)
}

function delMore(){	
	$.each($("#more_div").find("input[type='checkbox']"),function(i,input){
		  if($(input).is(':checked') ){
			  $(input).parent("td").parent("tr").remove()
		  }
	  })
}
//表格下复选框全选、反选;checkall:true全选、false反选
function check_All_unAll(tableId, checkall) {
	if (checkall) {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked", true);
	} else {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked",false);
	}
}