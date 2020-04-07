var testRulesdata = [{ID:'1',STARTNUM:'0',ENDNUM:'',TESTRULE:'首末件',PRONUM:'',QCNUM:''}];		//初始化
var lastrow,lastcell;

$(function () {
	var rulesObj={};
	
	$("#dataGrid_testrules").dataGrid({
		datatype: "local",
		data: testRulesdata,
        colModel: [
			
            {label: '<i class="fa fa-plus" aria-hidden="true" onclick="newOperation_testrules()"></i>', name: 'DEL',index:"DEL", width: "80",align:"center",sortable:false,editable:false/*,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
            	}*/
            },
            {label: '开始数量', name: 'STARTNUM',index:"STARTNUM", width: "80",align:"center",sortable:false,editable:false},
            {label: '截至数量', name: 'ENDNUM',index:"ENDNUM", width: "80",align:"center",sortable:false,editable:true},
            {label: '抽样规则', name: 'TESTRULE',index:"TESTRULE", width: "80",align:"center",sortable:false,editable:true},
            {label: '生产抽样数', name: 'PRONUM',index:"PRONUM", width: "80",align:"center",sortable:false,editable:true},
            {label: '品质抽样数', name: 'QCNUM',index:"QCNUM", width: "80",align:"center",sortable:false,editable:true},
            {label: '', name: '',index:"CHECK", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<input class="checkall" type="checkbox"><span>全检 </span>';
            	}	
            },
            {
				label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return row.ID
			}}
            
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		shrinkToFit: false,
        width:600,
        height:50,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
	
	
	//全检复选框 
	$(document).on("click",".checkall",function(e){	
		if ($(this).is(':checked')){
			$(e.target).closest("td").prev().find("input").eq(0).attr('disabled','disabled');
		}else{
			$(e.target).closest("td").prev().find("input").eq(0).removeAttr("disabled");
		}
		
	});
	
});

function newOperation_testrules(){
	console.info("dd");
	var trMoreindex = $("#dataGrid_testrules").children("tbody").children("tr").length-1;
	trMoreindex++;
	testRulesdata.push({ id: trMoreindex,STARTNUM:'',ENDNUM:'',TESTRULE:'',PRONUM:'',QCNUM:''});
	
	var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr selected-row ui-state-hover" aria-selected = "false">'+
	'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="'+trMoreindex+'" aria-describedby="dataGrid_testrules_rn">'+trMoreindex+'</td>'+
	'<td role="gridcell" style="text-align:center;"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i></td>'+
	'<td role="gridcell" style="text-align:center;"><input role="textbox" name="STARTNUM" type="text" value=""></td>'+
	'<td role="gridcell" style="text-align:center;"><input role="textbox" name="ENDNUM" type="text" value=""></td>'+
	'<td role="gridcell" style="text-align:center;"><input role="textbox" name="TESTRULE" type="text" value="首末件"></td>'+
	'<td role="gridcell" style="text-align:center;"><input role="textbox" name="PRONUM" type="text" value=""></td>'+
	'<td role="gridcell" style="text-align:center;"><input role="textbox" name="QCNUM" type="text" value=""></td>'+
	'<td role="gridcell" style="text-align:center;" title="全检 " aria-describedby="dataGrid_testrules_"><input class="checkall" type="checkbox"><span>全检 </span></td>'+
	'</tr>'
	$("#dataGrid_testrules tbody").append(addtr);
	
	/*var ids = $("#dataGrid_testrules").jqGrid('getDataIDs');
	
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    rulesObj.ID = rowid;
    rulesObj.DEL='<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
    rulesObj.STARTNUM ='';
    rulesObj.ENDNUM ='';
    rulesObj.TESTRULE ='';
    rulesObj.PRONUM ='';
    rulesObj.QCNUM ='';
    rulesObj.CHECK ='<input class="checkall" type="checkbox"><span>全检 </span>';
    
    $("#dataGrid_testrules").jqGrid("addRowData", rowid, rulesObj, "last");*/
};

var workshop_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    	WERKS:'',
    	WERKS_NAME:'',
    	WORKSHOP:'',
    	WORKSHOP_NAME:'',
    	ORDER_AREA_CODE:'',
    	ORDER_AREA_NAME:'',
    	TEST_RULES:'',
    	MEMO:'',
    	
    	workshoplist:[],
    	
    	saveFlag:false,
    	id:''
    },
    created: function(){
    	this.WERKS = $("#WERKS").find("option").first().val();
    	
	},
    watch:{
    	WERKS:{

			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MASTERDATA_TEST_RULES",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.WORKSHOP=resp.data[0].CODE;
		        			 if(workshop_edit!=''){
		        				 vm.WORKSHOP=workshop_edit;
		        			 }
		        		 }
		        		 
		        	  }
		          })
		          
			}
		
		
    	}
    },
    methods:{
    
      getInfo:function(id){
    	  var rulesObj_edit={};
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/testRules/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.WERKS = r.result.WERKS;
	            	vm.WERKS_NAME = r.result.WERKS_NAME;
	            	vm.WORKSHOP = r.result.WORKSHOP;
	            	vm.WORKSHOP_NAME = r.result.WORKSHOP_NAME;
	            	vm.ORDER_AREA_CODE = r.result.ORDER_AREA_CODE;
	            	vm.ORDER_AREA_NAME = r.result.ORDER_AREA_NAME;
	            	vm.TEST_RULES = r.result.TEST_RULES;
	            	vm.MEMO = r.result.MEMO;
	            	vm.id = r.result.ID;
	            	
	            	workshop_edit=r.result.WORKSHOP;
	            	
	            	$("#dataGrid_testrules").jqGrid("clearGridData", true);
	            	var ruleJson = jQuery.parseJSON(vm.TEST_RULES);
	            	for(var i in ruleJson){
	            		var trMoreindex = $("#dataGrid_testrules").children("tbody").children("tr").length-1;
	            		trMoreindex++;
	            		var	addtr='<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr selected-row ui-state-hover" aria-selected = "false">' +
	            		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="'+trMoreindex+'" aria-describedby="dataGrid_testrules_rn">'+trMoreindex+'</td>'+
	            		'<td role="gridcell" style="text-align:center;">'+((i>0)?'<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>':'')+'</td>' +
	        			'<td role="gridcell" style="text-align:center;"><input role="textbox" name="STARTNUM" type="text" value="'+ruleJson[i].from+'" /></td>' +
	        			'<td role="gridcell" style="text-align:center;"><input role="textbox" name="ENDNUM" type="text" value="'+ruleJson[i].to+'" /></td>' +
	        			'<td role="gridcell" style="text-align:center;"><input role="textbox" name="TESTRULE" type="text" value="'+ruleJson[i].dec+'" /></td>' +
	        			'<td role="gridcell" style="text-align:center;"><input role="textbox" name="PRONUM" type="text" value="'+ruleJson[i].prducu+'" /></td>' +
	        			'<td role="gridcell" style="text-align:center;"><input role="textbox" name="QCNUM" type="text" class="'+((ruleJson[i].qc=='全检')?'"disabled=\'disabled\'"':'')+'" value="'+((ruleJson[i].qc=='全检')?"0":ruleJson[i].qc)+'" /></td>' +
	        			'<td role="gridcell" style="text-align:center;" title="全检 " aria-describedby="dataGrid_testrules_"><input class="checkall" '+((ruleJson[i].qc=='全检')?'checked="checked"':'')+' type="checkbox"><span>全检 </span></td>' +
	        			'</tr>';
	        			$("#dataGrid_testrules tbody").append(addtr);
	            		/*var rowid=trMoreindex;
	            		rulesObj_edit.ID = trMoreindex;
	            		rulesObj_edit.DEL='<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
	            		rulesObj_edit.STARTNUM =ruleJson[i].from;
	            		rulesObj_edit.ENDNUM =ruleJson[i].to;
	            		rulesObj_edit.TESTRULE =ruleJson[i].dec;
	            		rulesObj_edit.PRONUM =ruleJson[i].prducu;
	            		rulesObj_edit.QCNUM ='<input role="textbox" name="QCNUM" type="text" class="'+((ruleJson[i].qc=='全检')?'"disabled=\'disabled\'"':'')+'" value="'+((ruleJson[i].qc=='全检')?"0":ruleJson[i].qc)+'" />';
	            		rulesObj_edit.CHECK ='<input class="checkall" '+((ruleJson[i].qc=='全检')?'checked="checked"':'')+' type="checkbox"><span>全检 </span></td>';
	            	    
	            	    $("#dataGrid_testrules").jqGrid("addRowData", rowid, rulesObj_edit, "last");*/
	        			
	        			
	        			
	            	}
	            	
	            }
			});
	
		
  		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		//获取抽检规则
		var rule_parameters=$("#dataGrid_testrules tbody").find("tr");
		var rule_str = '[';
		var to_num = 0;
		var check = true;
		$.each(rule_parameters,function(index,param){
			if(index>0){//因为第一个tr是表头
			var tds=$(param).children("td");
			
			
			var num_2="";
			
			if($(tds[2]).find("input").val()!=undefined){
				num_2=parseInt($(tds[2]).find("input").val());
			}else{
				num_2=parseInt($(tds[2]).html());
			}
			
			var num_3="";
			if($(tds[3]).find("input").val()!=undefined){
				
				num_3=parseInt($(tds[3]).find("input").val());
			}else{
				num_3=parseInt($(tds[3]).html());
			}
			
			var str_4="";
			if($(tds[4]).find("input").val()!=undefined){
				
				str_4=$(tds[4]).find("input").val();
			}else{
				str_4=$(tds[4]).html();
			}
			
			var num_5="";
			if($(tds[5]).find("input").val()!=undefined){
				num_5=parseInt($(tds[5]).find("input").val());
			}else{
				num_5=parseInt($(tds[5]).html());
			}
			
			var str_6="";
			if($(tds[6]).find("input").val()!=undefined){
				
				str_6=$(tds[6]).find("input").val();
			}else{
				str_6=$(tds[6]).html();
			}
			
			//校验起始数是否连续
			if(num_3 < num_2){
				alert("数量不连续，请检查数据！");
				check = false;
				return false;
			}
			
			if(!(Number.isInteger(num_2)&&Number.isInteger(num_3)
					&&Number.isInteger(num_5))){
				alert("数量必需是整数！");
				check = false;
				return false;
			}
			
			if(index > 1){
				if(num_2 != to_num){
					alert("数量不连续，请检查数据！");
					check = false;
					return false;
				}
			}
			
			if(str_4 === ''){
				alert("抽样规则不能为空，请检查数据！");
				check = false;
				return false;
			}
			
			to_num = num_3+1;
			console.info("check:"+$(tds[7]).find("input").is(':checked'));
			rule_str += '{"dec":"'+str_4
			+'","from":"'+num_2
			+'","to":"'+num_3
			+'","prducu":"'+num_5
			+'","qc":"'+(($(tds[7]).find("input").is(':checked'))?'全检':str_6)
			+'"},';
			}
		});
		
		rule_str = rule_str.substring(0,rule_str.length-1) + ']';
		if(!check){
			return false;
		}
		//
		
		vm.WERKS_NAME=$("#WERKS").find("option:selected").text();
		vm.WORKSHOP_NAME=$("#WORKSHOP").find("option:selected").text();
		vm.ORDER_AREA_NAME=$("#ORDER_AREA_CODE").find("option:selected").text();
		js.loading("正在保存,请稍候...");
		var url = vm.id =='' ? "masterdata/testRules/save" : "masterdata/testRules/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        //contentType: "application/json",
	        data:{
	        	WERKS:vm.WERKS,
	        	WERKS_NAME:vm.WERKS_NAME,
	        	WORKSHOP:vm.WORKSHOP,
	        	WORKSHOP_NAME:vm.WORKSHOP_NAME,
	        	ORDER_AREA_CODE:vm.ORDER_AREA_CODE,
	        	ORDER_AREA_NAME:vm.ORDER_AREA_NAME,
	        	TEST_RULES:rule_str,
	        	MEMO:vm.MEMO,
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