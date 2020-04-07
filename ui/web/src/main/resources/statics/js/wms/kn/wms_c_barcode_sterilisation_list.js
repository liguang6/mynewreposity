$(function(){

    $("#dataGrid").dataGrid({
        searchForm:$("#searchForm"),
        datatype: "json",
        colModel: [
            {label: '物料号', name: 'MATNR',index:"MATNR", width: 200,align:"center"},
            {label: '移动类型',name: 'WMSMOVETYPE',index:"WMSMOVETYPE",width: 200,align:"center"},
            {label: '数量', name: 'QTYWMS',index:"QTYWMS", width: 150,align:"center" },
            {label: '库位', name: 'BINCODE',index:"BINCODE", width: 150,align:"center" },
            {label: '移动原因', name: 'WMSMOVETYPE',index:"WMSMOVETYPE", width: 150,align:"center" },
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: 150,align:"center" },
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: 150,align:"center" },
            {label: '批次', name: 'BATCH',index:"BATCH", width: 150,align:"center" },
            {label: '工厂', name: 'WERKS',index:"WERKS", width: 150,align:"center" },
            {label: '单位', name: 'UNIT',index:"UNIT", width: 150,align:"center" },
            {label: '订单号', name: 'WMSNO',index:"WMSNO", width: 150,align:"center" },
            {label: '行项目', name: 'WMSITEMNO',index:"WMSITEMNO", width: 150,align:"center" },
            {label:'ID',width:75,name:'ID',index:"ID",align:"center",hidden:true}//隐藏此列
        ],
        viewrecords: true,
        rowNum: 15,
        rownumWidth: 25,
        showCheckbox:true,
        multiselect: true,
        multiboxonly:true,
        beforeSelectRow: function(){
            $("#jqgridId").jqGrid('resetSelection');
            return(true);
        },
    });


    //-------编辑操作--------
    $("#editOperation").click(function(){
        var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
        if(gr !== null && gr!==undefined){
            var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
            var options = {
                type: 2,
                maxmin: true,
                shadeClose: true,
                title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>条码打印',
                area: ["70%", "50%"],
                content: baseURL + 'wms/kn/wms_c_barcode_sterilisation_one.html',
                btn: ['<i class="fa fa-check"></i> 确定'],
                success:function(layero, index){
                    var win = layero.find('iframe')[0].contentWindow;
                    win.getInfo(grData.ID);
                    win.init();
                },
                btn1:function(index,layero){
                    var win = layero.find('iframe')[0].contentWindow;
                    win.printpage();
            		try {
            			parent.layer.close(index);
                    } catch(e){
                    	layer.close(index);
                    }

                }
            };
            options.btn.push('<i class="fa fa-close"></i> 关闭');
            js.layer.open(options);
        }else{
            layer.msg("请选择要编辑的行",{time:1000});
        }
    });



});

 var vm = new Vue({
     el:'#rrapp',
     data:{
    	 warehourse:[],
  		 whNumber:""
     },
  	created:function(){
		var plantCode = $("#werks").val();
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
	      data:{
	  		 "WERKS":plantCode,
	  		 "MENU_KEY":"A43"
	  	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER
    		 }
      	  }
        });
 	},
     methods: {
//         refresh:function(){
//             $("#searchForm").submit();
//         },
//         reload: function (event) {
//             $("#searchForm").submit();
//         },
//         getPlantNoFuzzy:function(){
//             getPlantNoSelect("#werks",null,null);
//         },
//         getVendorNoFuzzy:function(){
//             getVendorNoSelect("#lifnr",null,null,$("werks").val());
//         }
    		onPlantChange : function() {
    			var plantCode = $("#werks").val();
    			$.ajax({
    				url:baseUrl + "common/getWhDataByWerks",
    				data:{"WERKS":plantCode},
    				success:function(resp){
    					vm.warehourse = resp.data;
    					if(resp.data.length>0){
    						vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
    					}

    				}
    			});
    		}
     }
 });


function getSelRow(gridSelector){
    var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
    return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function listselectCallback(index,rtn){
    rtn = rtn ||false;
    if(rtn){
        //操作成功，刷新表格
        vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
    }
}