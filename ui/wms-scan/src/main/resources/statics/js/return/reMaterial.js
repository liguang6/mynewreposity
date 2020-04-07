$(function () {
    var lastrow;
    var lastcell;
    var now = new Date(); //当前日期
    var pzDate=new Date(now.getTime());
    $("#docDate").val(pzDate.Format("yyyy-MM-dd HH:mm:ss"));
    $("#debitDate").val(pzDate.Format("yyyy-MM-dd HH:mm:ss"));
    $("#dataGrid1").jqGrid({
        url : baseUrl + "returngoods/reMaterial/initialize",
        datatype: "json",
        postData:{"LABEL_NO":$("#LABEL_NO").val()},
        jsonReader: jsonreader,
        colNames:['物料号','<span style="color:red">*</span><span style="color:blue"><b>退出库位</b></span>',
            '<span style="color:red">*</span><span style="color:blue"><b>接收库位</b></span>','总数量','已扫箱数',
            '库存类型','<span style="color:blue">供应商代码</span>','批次','','','',''],
        colModel:[
            {name:'MATNR',index:'MATNR', width:80, align:'center'},
            {name:'MOVE_STLOC',index:'MOVE_STLOC', width:53, align:'center',editable:true},
            {name:'LGORT',index:'LGORT', width:53, align:'center',editable:true},
            {name:'TOTALL_QTY',index:'TOTALL_QTY', width:80, align:'center'},
            {name:'QTY',index:'QTY', width:50, align:'center',formatter:function(cellValue, options, rowdata, action){
                    var rowId = options.rowId;
                    return "<a href='#' style='color:red;'  onClick=datatable('"+rowId+"')>"+cellValue+"</a>";
                }
            },
            {name:'SOBKZ',index:'SOBKZ', width:50, align:'center',edittype:'select',editable:true},
            {name:'LIFNR',index:'LIFNR', width:65, align:'center',editable:true},
            {name:'BATCH',index:'BATCH', width:45, align:'center'},
            {name:'HEADER_TXT',index:'HEADER_TXT',hidden:'true'},
            {name:'UNIT',index:'UNIT',hidden:'true'},
            {name:'MAKTX',index:'MAKTX',hidden:'true'},
            {name:'CREAT_DATE',index:'CREAT_DATE',hidden:'true'},


        ],
        formatCell:function(rowid, cellname, value, iRow, iCol){
             window.lastrow=iRow;
             window.lastcell=iCol;
        },
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
          lastrow =iRow;
          lastcell=iCol;
        },
        width: 216,
        multiselect: true,
        shrinkToFit:false,
        pager:"#pager1",
        cellEdit:true,
        cellsubmit: "clientArray",
        cellurl : '#',
    });
});

document.onkeydown = function(){
    if(window.event.keyCode == 13 && document.activeElement.id == 'barcode'){
        scanPre();
    }
}
function datatable(rowId){
    debugger
    document.getElementById("div1").style.display = "none";
    document.getElementById("div2").style.display = "none";
    document.getElementById("div3").style.display = "";
    document.getElementById("div4").style.display = "none";

    document.getElementById("btn2").style.display = "none";
    document.getElementById("btn3").style.display = "none";
    document.getElementById("btn7").style.display = "";
    document.getElementById("btn4").style.display = "";
    document.getElementById("btn5").style.display = "none";
    document.getElementById("btn6").style.display = "none";
     var rowdata = $("#dataGrid1").jqGrid('getRowData',rowId);
    $("#dataGrid").jqGrid({
        url : baseUrl + "returngoods/reMaterial/scanInfo",
        datatype: "json",
        jsonReader: jsonreader,
        postData:{params:JSON.stringify(rowdata)},
        colNames:['行项','条形码','数量','BYD批次'],
        colModel:[
            {name:'ROW',index:'ROW', width:50, align:'center'},
            {name:'LABEL_NO',index:'LABEL_NO', width:100, align:'center'},
            {name:'QTY',index:'QTY', width:63, align:'center'},
            {name:'BATCH',index:'BATCH', width:65, align:'center'},
        ],
        width: 216,
        multiselect:true,
        shrinkToFit:false,
        rowNum: 10,
        shrinkToFit:false,
        pager:"#pager"
    });
    //删除操作
    $("#btn7").click(function () {
        var ids=$("#dataGrid").jqGrid('getGridParam','selarrrow');
        var rowdatas = [];
        var validate = "";
        $.each(ids,function(i,rowid){
            validate = validate + rowid+",";
            rowdata = $("#dataGrid").jqGrid('getRowData',rowid);
            rowdatas.push(rowdata);
        });
        if(validate=="") {
            $("#divInfo3").html("请勾选数据后删除");
            document.getElementById("divInfo3").style.visibility="visible";
            setTimeout(function () {
                document.getElementById("divInfo3").style.visibility="hidden";

            },2000);
            return false;
        }else {
            $.ajax({
                url: baseUrl + "returngoods/reMaterial/remove",
                dataType: "json",
                type: "post",
                data: {params: JSON.stringify(rowdatas)},
                async: false,
                success: function (res) {

                    if (res.code == 0){
                        $("#divInfo3").html(res.msg);
                        document.getElementById("divInfo3").style.visibility="visible";
                        setTimeout(function () {
                            document.getElementById("divInfo3").style.visibility="hidden";

                        },2000);
                        $.each(ids,function(i,rowid){
                            $("#dataGrid").delRowData(rowid);
                        });
                        return false;
                    }
                }


            })
        }
    });
   }
function rback(){
    document.getElementById("div1").style.display = "";
    document.getElementById("div2").style.display = "none";
    document.getElementById("div3").style.display = "none";
    document.getElementById("div4").style.display = "none";

    document.getElementById("btn2").style.display = "";
    document.getElementById("btn3").style.display = "";
    document.getElementById("btn4").style.display = "none";
    document.getElementById("btn5").style.display = "none";
    document.getElementById("btn6").style.display = "none";
    document.getElementById("btn7").style.display = "none";
   window.location.reload();
}
function rback2(){
    document.getElementById("div1").style.display = "none";
    document.getElementById("div2").style.display = "";
    document.getElementById("div3").style.display = "none";
    document.getElementById("div4").style.display = "none";

    document.getElementById("btn2").style.display = "none";
    document.getElementById("btn3").style.display = "none";
    document.getElementById("btn4").style.display = "";
    document.getElementById("btn5").style.display = "";
    document.getElementById("btn6").style.display = "none";
    document.getElementById("btn7").style.display = "none";

}
function saveData() {
    $('#dataGrid1').jqGrid("saveCell", window.lastrow, window.lastcell);
    var allRowIds = $('#dataGrid1').getDataIDs();
    if (allRowIds.length == 0) {
        alert("收货数据为空!");
        return;
    }
    var rows = [];
    for (var i = 0; i < allRowIds.length; i++) {
        var rowData = $("#dataGrid1").jqGrid('getRowData', allRowIds[i]);
        rows.push(rowData);
    }
    $.ajax({
        url: baseUrl + "returngoods/reMaterial/saveData",
        dataType: "json",
        type: "post",
        data: {params: JSON.stringify(rows)},
        async: false,
        success:function (res) {
             if(res.code = "0"){
                 $("#divInfo").html(res.msg);
                 document.getElementById("divInfo").style.visibility="visible";
                 setTimeout(function () {
                     document.getElementById("divInfo").style.visibility="hidden";

                 },2000);
                 return false;
             }else{
                 $("#divInfo").html(res.msg);
                 document.getElementById("divInfo").style.visibility="visible";
                 setTimeout(function () {
                     document.getElementById("divInfo").style.visibility="hidden";

                 },2000);
                 return false;
             }
        }
    })
}
function confirm(){
    var now = new Date(); //当前日期
    var pzDate=new Date(now.getTime());
    $('#dataGrid1').jqGrid("saveCell", window.lastrow, window.lastcell);
    var ids = $("#dataGrid1").jqGrid('getGridParam','selarrrow');
    var rowdatas=[];
    var  validate = "";
     var flag = "0";
    $.each(ids,function (i,rowid) {
        validate = validate+rowid+",";
        $("#dataGrid1").saveRow(rowid);
        var rowdata = $("#dataGrid1").jqGrid('getRowData', rowid);
        var rowda = rowdata.QTY;
        var reg=(/>\d*</gi).exec(rowda);
        var qty = (reg == null || reg== "") ? 0 : reg[0].replace("<","").replace(">","");
        rowdata.QTY = qty;
        rowdatas.push(rowdata);

    });
    if(validate == "") {
        $("#divInfo").html("请勾选数据后确认");
        document.getElementById("divInfo").style.visibility="visible";
        setTimeout(function () {
            document.getElementById("divInfo").style.visibility="hidden";

        },2000);
        return false;

    }else if($("#BIN_CODE").val() =="") {
        $("#divInfo").html("储位不能为空");
        document.getElementById("divInfo").style.visibility = "visible";
        setTimeout(function () {
            document.getElementById("divInfo").style.visibility = "hidden";

        }, 2000);
    }else {
        var flag =0;
        $.each(ids, function (i, rowid) {
            var rowdata = $("#dataGrid1").jqGrid('getRowData', rowid);
            var f = rowdata.LGORT;
            var l = rowdata.MOVE_STLOC;
            var b = rowdata.SOBKZ;
            var LI = rowdata.LIFNR;
             flag = 0;
            if (f == "") {
                $("#divInfo").html("接收库位不能为空");
                document.getElementById("divInfo").style.visibility="visible";
                setTimeout(function () {
                    document.getElementById("divInfo").style.visibility="hidden";

                },2000);
                flag = 1;
            } else if (l == "") {
                $("#divInfo").html("退出库位不能为空");
                document.getElementById("divInfo").style.visibility="visible";
                setTimeout(function () {
                    document.getElementById("divInfo").style.visibility="hidden";

                },2000);
                flag = 1;
            } else if (b == "K") {
                if (LI == "") {
                    $("#divInfo").html("供应商代码不能为空");
                    document.getElementById("divInfo").style.visibility="visible";
                    setTimeout(function () {
                        document.getElementById("divInfo").style.visibility="hidden";

                    },2000);
                    flag = 1;
                }
            }
        })
        if (flag == 0) {
            document.getElementById("div1").style.display = "none";
            document.getElementById("div2").style.display = "";

            document.getElementById("btn2").style.display = "none";
            document.getElementById("btn3").style.display = "none";
            document.getElementById("btn4").style.display = "";
            document.getElementById("btn5").style.display = "";

            document.getElementById("docDate").value = pzDate.Format("yyyy-MM-dd");
            document.getElementById("debitDate").value = pzDate.Format("yyyy-MM-dd");
            document.getElementById("HEADER_TXT").value = rowdatas[0].HEADER_TXT;
        }
        //确认过账
        $("#btn5").click(function () {
               document.getElementById("div1").style.display = "none";
               document.getElementById("div2").style.display = "none";
               document.getElementById("div3").style.display = "none";
               document.getElementById("div4").style.display = "";

               document.getElementById("btn2").style.display = "none";
               document.getElementById("btn3").style.display = "none";
               document.getElementById("btn4").style.display = "none";
               document.getElementById("btn5").style.display = "none";
               document.getElementById("btn6").style.display = "";
               document.getElementById("btn7").style.display = "none";

               $.ajax({
                   url: baseUrl + "returngoods/reMaterial/confirmWorkshopReturn",
                   dataType: "json",
                   type: "post",
                   data: {
                       params: JSON.stringify(rowdatas), "BIN_CODE": $("#BIN_CODE").val(),
                       "docDate": $("#docDate").val(), "debitDate": $("#debitDate").val()
                   },
                   async: false,
                   success: function (res) {
                       if (res.code == 0) {
                           $("#divInfo4").html(res.msg);
                           document.getElementById("SAP_NO").value = res.data.SAP_NO;
                           document.getElementById("WMS_NO").value == res.data.WMS_NO;
                           document.getElementById("TIME_SLOT").value == res.data.TIME_SLOT;
                           document.getElementById("divInfo4").style.visibility = "visible";
                           setTimeout(function () {
                               document.getElementById("divInfo4").style.visibility = "hidden";

                           }, 6000);
                           return false;
                       } else {
                           $("#divInfo4").html(res.msg);
                           document.getElementById("divInfo4").style.visibility = "visible";
                           setTimeout(function () {
                               document.getElementById("divInfo4").style.visibility = "hidden";

                           }, 10000);
                           return false;
                       }
                   }


               })
        })

    }
}



document.onkeydown = function(){

    if(window.event.keyCode == 13 && document.activeElement.id == 'barcode'){
        scanPre();
    }
    if(window.event.keyCode == 118){
        history.back(-1);
    }
}//扫码
function scanPre(){
    $.ajax({
        dataType:'json',
        type:'post',
        url:baseUrl + "returngoods/reMaterial/valiateLabel",
        data:{"LABEL_NO":$("#LABEL_NO").val(),
               "BIN_CODE":$("#BIN_CODE").val()
        },
        success:function (res) {
            if(res.code != '0'){
                $("#divInfo").html(res.msg);
                document.getElementById("divInfo").style.visibility="visible";
                setTimeout(function () {
                    document.getElementById("divInfo").style.visibility="hidden";

                },2000);
                return false;
            }else {
                $("#dataGrid1").jqGrid("clearGridData");
                $("#dataGrid1").jqGrid("setGridParam",{
                    url : baseUrl + "returngoods/reMaterial/scan",
                    datatype: "json",
                    postData:{"LABEL_NO":$("#LABEL_NO").val()},
                }).trigger("reloadGrid");
            }

        }
    })

}


//获取grid中的选中行（单行）
//         var obj = $("#jqGridId");
//         var rowid = obj.jqGrid('getGridParam', 'selrow');
// //获取grid中的选中行（多行）
//         var ids=obj.jqGrid('getGridParam','selarrrow');
// //获取行数据
//         var rowData=obj.jqGrid('getRowData',rowid);
// //获取grid表格中的所有rowid
//         var IDs=obj.getDataIDs();
// // 选中行实际表示的位置
// //var ind = $("#jiGouGridList").getInd(rowid);
// //清除grid最后一行数据
//         obj.clearGridData(true);
// //选中行
//         obj.setSelection(rowid);
