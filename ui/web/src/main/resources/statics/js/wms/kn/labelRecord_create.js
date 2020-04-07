var mydata = [];
var lastrow,lastcell;
var trindex = 1;

$(function(){
    mydata.length=0;
    $("#dataGrid").jqGrid('GridUnload');
    $("#dataGrid").jqGrid("clearGridData", true);
    $("#dataGrid").dataGrid({
        url: baseUrl + 'kn/labelRecord/poList',
        datatype: "local",
        data: mydata,
        colModel: [
            {label: '工厂', name: 'WERKS',index:"WERKS", width: 50,align:"center"},
            {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: 60,align:"center"},
            {label: '采购订单', name: 'EBELN',index:"EBELN", width: 100,align:"center"},
            {label: '行项目', name: 'EBELP',index:"EBELP", width: 70,align:"center"},
            {label: '物料号', name: 'MATNR',index:"MATNR", width: 100,align:"center",editable:true},
            {label: '物料描述',name: 'TXZ01',index:"TXZ01",width: 150,align:"center"},
            {label: '单位',name: 'MEINS',index:"MEINS",width: 50,align:"center"},
            {label: '库存地点',name: 'LGORT',index:"LGORT",width: 80,align:"center",editable:true},
            {label: '订单数量',name: 'MAX_MENGE',index:"MAX_MENGE",width: 100,align:"center"},
            {label: '收货数量',name: 'MENGE',index:"MENGE",width: 70,align:"center",editable:true,},
            {label: '装箱数量',name: 'FULL_BOX_QTY',index:"FULL_BOX_QTY",width: 70,align:"center",editable:true,},
            {label: '生产日期',name: 'PRODUCT_DATE',index:"PRODUCT_DATE",width: 100,align:"center",editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                        WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,onpicked:function(pd){
                                var current = this.value;
                                var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');

                                var grData = $("#dataGrid").jqGrid("getRowData",rowId)//获取选中的行数据
                                var prfrq =grData["PRFRQ"];
                                var mantr =grData["MATNR"];
                                if(vm.prfrqflag == "X"  ){
                                    if(prfrq == null || prfrq == ""){
                                        $(e).val("");
                                        js.showMessage("工厂："+vm.WERKS+"启用了保质期管理，物料："+mantr+"的保质期未同步！");
                                    }else{
                                        var currentTime = new Date(current).getTime();
                                        var effectTime = new Date(currentTime + Number(prfrq)*60*60*24*1000);
                                        var effect = effectTime.getFullYear()+"-"+(effectTime.getMonth()+1)+"-"+effectTime.getDate();
                                        $("#dataGrid").jqGrid('setCell',rowId,"EFFECT_DATE",effect);
                                    }
                                }
                            }
                        })
                    });
                }
            }},
            {label: '保质日期',name: 'EFFECT_DATE',index:"EFFECT_DATE",width: 100,align:"center"},
            {label: '保质时长(天)',name: 'PRFRQ',index:"PRFRQ",width: 100,align:"center"},
            {label:'LIFNR',width:0,name:'LIFNR',index:"LIFNR",align:"center",hidden:true},
            {label:'LIKTX',width:0,name:'LIKTX',index:"LIKTX",align:"center",hidden:true},
            {label:'PRFRQFLAG',width:0,name:'PRFRQFLAG',index:"PRFRQFLAG",align:"center",hidden:true},
            {label:'库存类型',width:0,name:'SOBKZ',index:"SOBKZ",align:"center"}

        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
            lastrow=iRow;
            lastcell=iCol;
            lastcellname=cellname;
        },
        onSelectAll:function(rowids,status){},
        onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,
        autoScroll: true,
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },

        gridComplete:function(){

        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
        beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
            var $myGrid = $(this),
                i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
                cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
            if(cm[i].name == 'cb'){
                $('#dataGrid').jqGrid('setSelection',rowid);
            }
            return (cm[i].name == 'cb');

        }
    });
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        WERKS:"",
        EBELN:"",
        WH_NUMBER:"",
        warehourse:[],
        prfrqflag:""
    },
    created:function(){
        loadWhNumber();
    },
    methods: {
        onPlantChange:function(){
            loadWhNumber();

        },
        query: function () {
            js.loading();
            vm.$nextTick(function(){//数据渲染后调用
                $("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                js.closeLoading();
            })

        },
        saveBtn:function(){

            $(".btn").attr("disabled", true);
            $('#dataGrid').jqGrid("saveCell", lastrow, lastcell);

            var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
            var rows=[];

            if(ids.length == 0){
                alert("请至少选中一条数据！");
                $(".btn").attr("disabled", false);
                return false;
            }else {

                var trs=$("#dataGrid").children("tbody").children("tr");
                for(var m=1;m<trs.length;m++){
                    var cbx=$(trs[m]).find("td").find("input").attr("type");
                    if(cbx!=undefined){
                        var c_checkbox=$(trs[m]).find('input[type=checkbox]');
                        var ischecked=$(c_checkbox).is(":checked");
                        if(ischecked){

                            var WERKS  = $(trs[m]).find("td").eq(2).html();

                            var WH_NUMBER = $(trs[m]).find("td").eq(3).html();

                            var EBELN = $(trs[m]).find("td").eq(4).html();
                            var EBELP =$(trs[m]).find("td").eq(5).html();

                            var MATNR ="";
                            if($(trs[m]).find("td").eq(6).html() != "" || $(trs[m]).find("td").eq(6).find("input").val()!=undefined){
                                MATNR = $(trs[m]).find("td").eq(6).html() != "" ?
                                    $(trs[m]).find("td").eq(6).html() : $(trs[m]).find("td").eq(6).find("input").val();
                            }else{
                                js.showMessage("请输入物料");
                                $(".btn").attr("disabled", false);
                                return false;
                            }
                            var TXZ01 = $(trs[m]).find("td").eq(7).html();

                            var MEINS = $(trs[m]).find("td").eq(8).html();

                            var LGORT = "";
                            if($(trs[m]).find("td").eq(9).html().replace(/&nbsp;/g,'') != "" || $(trs[m]).find("td").eq(9).find("input").val()!=undefined){
                                LGORT = $(trs[m]).find("td").eq(9).html().replace(/&nbsp;/g,'') != "" ?
                                    $(trs[m]).find("td").eq(9).html().replace(/&nbsp;/g,'') : $(trs[m]).find("td").eq(9).find("input").val();
                            }

                            var MAX_MENGE = $(trs[m]).find("td").eq(10).html();
                            var MENGE ="";
                            if($(trs[m]).find("td").eq(11).html().replace(/&nbsp;/g,'') != "" || $(trs[m]).find("td").eq(11).find("input").val()!=undefined){
                                MENGE = $(trs[m]).find("td").eq(11).html().replace(/&nbsp;/g,'') != "" ?
                                    $(trs[m]).find("td").eq(11).html().replace(/&nbsp;/g,'') : $(trs[m]).find("td").eq(11).find("input").val();
                            }else{
                                js.showMessage("未维护收货数量");
                                $(".btn").attr("disabled", false);
                                return false;
                            }
                            var FULL_BOX_QTY ="";
                            if($(trs[m]).find("td").eq(12).html().replace(/&nbsp;/g,'') != "" || $(trs[m]).find("td").eq(12).find("input").val()!=undefined){
                                FULL_BOX_QTY = $(trs[m]).find("td").eq(12).html().replace(/&nbsp;/g,'') != "" ?
                                    $(trs[m]).find("td").eq(12).html().replace(/&nbsp;/g,'') : $(trs[m]).find("td").eq(12).find("input").val();
                            }else{
                                js.showMessage("未维护装箱数量");
                                $(".btn").attr("disabled", false);
                                return false;
                            }
                            var PRODUCT_DATE ="";
                            if($(trs[m]).find("td").eq(13).html() != "" || $(trs[m]).find("td").eq(13).find("input").val()!=undefined){
                                PRODUCT_DATE = $(trs[m]).find("td").eq(13).html() != "" ?
                                    $(trs[m]).find("td").eq(13).html() : $(trs[m]).find("td").eq(13).find("input").val();
                            }
                            var EFFECT_DATE = $(trs[m]).find("td").eq(14).html();
                            var PRFRQ = $(trs[m]).find("td").eq(15).html().replace(/&nbsp;/g,'');
                            var PRFRQFLAG = $(trs[m]).find("td").eq(18).html().replace(/&nbsp;/g,'');
                            if(PRFRQFLAG =="X" && (PRFRQ == "" || PRFRQ == undefined )){
                                js.showMessage("仓库："+WH_NUMBER+"启用了质检周期管理，物料:"+
                                    MATNR+"在工厂:"+WERKS+"下尚未同步质检周期！");
                                $(".btn").attr("disabled", false);
                                return false;
                            }
                            var LIFNR = $(trs[m]).find("td").eq(16).html().replace(/&nbsp;/g,'');

                            var LIKTX = $(trs[m]).find("td").eq(17).html().replace(/&nbsp;/g,'');
                            var SOBKZ = $(trs[m]).find("td").eq(19).html().replace(/&nbsp;/g,'');

                            rows.push("{"+"'WERKS':'"+WERKS+"',"+"'WH_NUMBER':'"+WH_NUMBER+"',"+"'EBELN':'"+EBELN+
                                "',"+"'EBELP':'"+EBELP+"',"+"'MATNR':'"+MATNR+"',"+"'TXZ01':'"+TXZ01+
                                "',"+"'MEINS':'"+MEINS+"',"+"'MAX_MENGE':'"+MAX_MENGE+"',"+"'MENGE':'"+MENGE+
                                "',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+"',"+"'PRODUCT_DATE':'"+PRODUCT_DATE+"',"+"'EFFECT_DATE':'"+EFFECT_DATE+
                                "',"+"'PRFRQ':'"+PRFRQ+"',"+"'LIFNR':'"+LIFNR+"',"+"'LIKTX':'"+LIKTX+"',"+"'LGORT':'"+LGORT+"','SOBKZ':'"+SOBKZ+
                                "'}");
                        }
                    }
                }

                $.ajax({
                    url:baseURL+"kn/labelRecord/saveLabel",
                    dataType : "json",
                    type : "post",
                    data : {
                        "ARRLIST":JSON.stringify(rows),
                    },
                    async: true,
                    success: function (resp) {
                        if(resp.code == '0'){
                            $("#labelNo").val(resp.labels);
                            $("#labelNoA4").val(resp.labels);
                            layer.open({
                                type : 1,
                                offset : '50px',
                                skin : 'layui-layer-molv',
                                title : "根据采购订单打印条码",
                                area : [ '600px', '200px' ],
                                shade : 0,
                                shadeClose : false,
                                content : jQuery("#prntLayer"),
                                btn : [ '确定'],
                                btn1 : function(index) {
                                    layer.close(index);
                                }
                            });
                            $(".btn").attr("disabled", false);
                        }else{
                            js.alert("保存失败");
                            js.showErrorMessage(resp.msg,1000*100);
                            $(".btn").attr("disabled", false);
                        }
                    }
                });
            }
        },
        print: function () {

            $('#labelSize').val("100mm*70mm");
            $('#labelType').val("raw-material");
            $("#printButton").click();
        },
        printA4: function () {
            $('#labelSizeA4').val("A4(210mm*297mm)");
            $('#labelTypeA4').val("raw-material");
            $("#printButtonA4").click();
        }
    }
});

function newOperation(){

    var currentDate = formatDate(new Date());

    var werks=$("#WERKS").val();
    var wh_number=$("#WH_NUMBER").val();
    var prfrqflag = vm.prfrqflag;

    trindex =$("#dataGrid").children("tbody").children("tr").length ;

    var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
        '<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
        '<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+


        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WERKS">'+werks+'</td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WH_NUMBER">'+wh_number+'</td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_EBELN"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_EBELP"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATNR" id="'+trindex+'_MATNR" type="text" onblur="queryMatInfo(this)"></td>'+
        '<td role="gridcell" style="" title="" aria-describedby="dataGrid_TXZ01"></td>'+
        '<td role="gridcell" style="" title="" aria-describedby="dataGrid_MEINS"></td>'+
        '<td role="gridcell" style="" title="" aria-describedby="dataGrid_LGORT"></td>'+
        '<td role="gridcell" style="" title="" aria-describedby="dataGrid_MAX_MENGE"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MENGE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MENGE" id="'+trindex+'_MENGE" type="text"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BOX_QTY" id="'+trindex+'_BOX_QTY" type="text"></td>'+

        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRODUCT_DATE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRODUCT_DATE" id="'+trindex+'PRODUCT_DATE" type="text" value="'+currentDate+'" onclick="calculaDate(this)"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_EFFECT_DATE"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRFRQ"></td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRFRQFLAG">'+prfrqflag+'</td>'+
        '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_SOBKZ"></td>'+
        '</tr>';
    $("#dataGrid tbody").append(addtr);
}

function queryMatInfo(e){
    var werks = $("#WERKS").val();
    var mat = $(e).val().trim();
    if(mat == null) return;
    $.ajax({
        url:baseUrl +  "out/createRequirement/mat?mat=" + mat + "&werks="+werks,
        success:function(resp){
            if(resp.code === 0){
                var matinfo = resp.data;
                //设置物料描述，物料单位
                $(e).parent("td").next().html(matinfo.MAKTX);
                $(e).parent("td").next().next().html(matinfo.MEINH);
                $(e).parent("td").next().next().next().next().next().next().next().next().html(matinfo.PRFRQ);
                if(vm.prfrqflag == "X"  ){
                    if(matinfo.PRFRQ == null || matinfo.PRFRQ == ""){
                        $(e).val("");
                        js.showMessage("工厂："+werks+"启用了保质期管理，物料："+mat+"的保质期未同步！");
                    }else{
                        var prfrq = matinfo.PRFRQ;
                        var current = $(e).parent("td").next().next().next().next().next().next().find("input").val();
                        var currentTime = new Date(current).getTime();
                        var effectTime = new Date(currentTime + Number(prfrq)*60*60*24*1000);
                        var effect = effectTime.getFullYear()+"-"+effectTime.getMonth() + 1+"-"+effectTime.getDate();
                        $(e).parent("td").next().next().next().next().next().next().next().html(effect);
                    }

                }
            }else{
                $(e).val("");
                js.showMessage(resp.msg);
            }
        }

    })
}

function deleteOperation(){

    var trs=$("#dataGrid").children("tbody").children("tr");
    var reIndex = 1;
    $.each(trs,function(index,tr){
        var td = $(tr).find("td").eq(0);
        var td_check = $(tr).find("td").eq(1).find("input");
        if(index > 0){
            var ischecked=$(td_check).is(":checked");
            if(ischecked){
                tr.remove();
            }else{
                td.text(reIndex);
                $(tr).attr('id',reIndex);
                $(td_check).attr('name','jqg_dataGrid_' + reIndex);
                $(td_check).attr('id','jqg_dataGrid_' + reIndex);
                reIndex++;
            }
        }
    });
    trindex = reIndex;
}

function loadWhNumber(){
    ////初始化查询仓库
    var plantCode = $("#WERKS").val();

    //初始化仓库
    $.ajax({
        url:baseUrl + "common/getWhDataByWerks",
        data:{"WERKS":plantCode},
        success:function(resp){
            vm.warehourse = resp.data;
            if(resp.data.length>0){
                vm.WH_NUMBER=resp.data[0].WH_NUMBER//方便 v-model取值
                loadCPlantInfo();
            }

        }
    });
}

function loadCPlantInfo(){

    //初始化仓库
    $.ajax({
        url:baseUrl + "/config/CPlant/list",
        data:{"werks":vm.WERKS,"whNumber":vm.WH_NUMBER},
        success:function(resp){
            if(resp.page.count >0){
                vm.prfrqflag=resp.page.list[0].PRFRQFLAG
            }

        }
    });
}

function calculaDate(e) {
    WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,onpicked:function(pd){
            var current = $(e).val().trim();
            var prfrq =$(e).parent("td").next().next().html();
            if(vm.prfrqflag == "X"  ){
                if(prfrq == null || prfrq == ""){
                    $(e).val("");
                    js.showMessage("工厂："+werks+"启用了保质期管理，物料："+mat+"的保质期未同步！");
                }else{
                    var currentTime = new Date(current).getTime();
                    var effectTime = new Date(currentTime + Number(prfrq)*60*60*24*1000);
                    var effect = effectTime.getFullYear()+"-"+effectTime.getMonth()+"-"+effectTime.getDay();
                    $(e).parent("td").next().html(effect);
                }

            }



        }});
}