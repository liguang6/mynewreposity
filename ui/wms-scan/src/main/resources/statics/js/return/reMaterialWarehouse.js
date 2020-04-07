var jsonreader = {
    root: "page.list",
    page: "page.currPage",
    total: "page.totalPage",
    records: "page.totalCount",
    repeatitems: false
};
$(function () {
    //默认选中录入页签\返回按钮显示
    document.getElementById("div_input").style.display = "";//录入页签
    document.getElementById("btnCancel").style.display = "";//返回按钮
    document.getElementById("btnConfirmInfo").style.display = "";//确认信息按钮

    //初始化抬头信息的时间信息,默认当天
    var now = new Date(); //当前日期
    var pzDate = new Date(now.getTime());
    $("#docDate").val(pzDate.Format("yyyy-MM-dd"));
    $("#debitDate").val(pzDate.Format("yyyy-MM-dd"));

    //获取缓存数据
    initPoCacheInfo();


    // 初始化注册返回事件
    $('#btnCancel').on('click', function () {
        history.go(-1);
        // location.href = '${request.contextPath}/in/poreceiptpda.html';
    });

    // 初始化条码信息删除事件
    $('#btnPoReBarDeleteBar').on('click', function () {
        var rowids = $('#poreBarDataGrid').jqGrid('getGridParam', 'selarrrow');
        if (rowids.length == 0) {
            alert("请选择要删除的条码数据!");
            return;
        }
        var rows = [];
        for (var i = 0; i < rowids.length; i++) {
            var rowData = $("#poreBarDataGrid").jqGrid('getRowData', rowids[i]);
            rows.push(rowData.LABEL_NO);
        }
        console.log(rows);
        $.ajax({
            url: baseUrl + "returngoods/wmsReMaterialWarehouse/deleteLabel",
            dataType: "json",
            type: "post",
            data: {
                "LABEL_ARR": JSON.stringify(rows)
            },
            async: true,
            success: function (resp) {
                if (resp.code == '0') {
                    // $("#poreBarDataGrid").jqGrid('clearGridData');
                    // $("#btnsaveinteralbound").html("执行");
                    for (var i = 0; i < rowids.length; i++) {
                        // $("#poreBarDataGrid").delGridRow(rowids[i]);
                        $("#poreBarDataGrid").jqGrid("delRowData", rowids[i]);
                        var hiboxqty = parseInt($("#hi_boxqty").val()) - 1;
                        $("#hi_boxqty").val(hiboxqty);
                        $("#pore_boxqty").val(hiboxqty);
                    }
                    alert("删除条码信息成功! ");
                    // js.closeLoading();
                } else {
                    $("#poreBarDataGrid").jqGrid('clearGridData');
                    // $("#btnsaveinteralbound").html("执行");
                    alert(resp.msg);
                    // js.closeLoading();
                }
            }
        });
    });

    // 初始化抬头信息跳转确认按钮事件
    $('#btnConfirmInfo').on('click', function () {
        //生成抬头文本
        $.ajax({
            url: baseUrl + "returngoods/wmsReMaterialWarehouse/createheadText",
            dataType: "json",
            type: "post",
            data: {
                "PZ_DATE": $("#docDate").val(),
                "JZ_DATE": $("#debitDate").val(),
            },
            success: function (resp2) {
                if (resp2.code == '0') {
                    changePoReTab("head");//跳转到抬头信息页签
                    $("#headText").val(resp2.HEADER_TXT);
                } else {
                    alert(resp2.msg);
                }
            }
        })
    });

    $('#btnConfirm').on('click', function () {
        $('#dataGrid1').jqGrid("saveCell", window.lastrow, window.lastcell);
        var allRowIds = $('#poreDataGrid').getDataIDs();
        if (allRowIds.length == 0) {
            alert("收货数据为空!");
            return;
        }
        var rows = [];
        for (var i = 0; i < allRowIds.length; i++) {
            var rowData = $("#poreDataGrid").jqGrid('getRowData', allRowIds[i]);
            rows.push(rowData);
        }

        $.ajax({
            url: baseUrl + "returngoods/wmsReMaterialWarehouse/poReceiptPdaIn",
            dataType: "json",
            type: "post",
            data: {
                "PZ_DATE": $("#docDate").val(),
                "JZ_DATE": $("#debitDate").val(),
                "HEADER_TXT": $("#headText").val(),
                "matList":JSON.stringify(rows)
            },
            success: function (resp2) {
                if (resp2.code == '0') {
                    $("#RETURN_NO").val(resp2.result.RETURN_NO);
                    $("#RETURN_ITEM_NO").val(resp2.result.RETURN_ITEM_NO);
                    $("#WMS_NO").val(resp2.result.WMS_NO);
                    $("#pore_time_slot").val(resp2.result.TIME_SLOT);
                    $("#hi_boxqty").val(0);//用于计算已扫箱数,过账成功需要清除已扫箱数.
                    changePoReTab("result");//显示返回页签,其他页签\确认按钮等隐藏
                } else {
                    alert(resp2.msg);
                }
            }
        })
    });

    //初始化明细数据grid
    $("#poreDataGrid").jqGrid({
        url : baseUrl + "returngoods/wmsReMaterialWarehouse/getGridPoreData",
        datatype: "json",
        jsonReader: jsonreader,
        colNames: ['物料号', '<a style="color:red">*</a>退出库位', '<a style="color:red">*</a>接收库位','<a style="color:red">*</a>托盘号', '总数量', '箱数', '库存类型', '供应商代码','批次',
            '','','',''],
        colModel: [
            {name: 'MATNR', index: 'MATNR', width: 80, align: 'center'},
            {name: 'MOVE_STLOC', index: 'MOVE_STLOC', width: 60, align: 'center',editable:true},
            {name: 'LGORT', index: 'LGORT', width: 60, align: 'center',editable:true},
            {name: 'TPNUM', index: 'TPNUM', width: 50, align: 'center',editable:true},
            {name: 'RECEIPT_QTY', index: 'RECEIPT_QTY', width: 40, align: 'center'},
            {name: 'ALLBOX_QTY', index: 'ALLBOX_QTY', width: 30, align: 'center',
                formatter: function viewLink(cellValue, options, rowdata, action) {
                    var rowId=options.rowId;
                    return "<a href='javascript:void(0)' style='color:red;' onclick=viewPoBarInfo('"+rowId+"')>"+cellValue+"</a>";
                    // return "<a  onclick=\"viewPoBarInfo('" + rowId + "')\">" + "</a> ";
                }
                },
            {name: 'SOBKZ', index: 'SOBKZ', width: 60, align: 'center'},
            {name: 'LIFNR', index: 'LIFNR', width: 70, align: 'center', editable:true},
            {name: 'BATCH', index: 'BATCH', width: 70, align: 'center'},

            {name: 'CREATE_DATE', index: 'CREATE_DATE', width: 80, align: 'center',hidden:'true'},
            {name:'HEADER_TXT',index:'HEADER_TXT',hidden:'true'},
            {name:'UNIT',index:'UNIT',hidden:'true'},
            {name:'LABEL_NO',index:'LABEL_NO',hidden:'true'},

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
        rowNum: 10,
        cellEdit:true,
        shrinkToFit:false,
        cellsubmit: "clientArray",

        // rownumbers:true,
        // cellEdit: true,
        // cellsubmit: "clientArray",
        // rownumWidth:25,
        // autowidth:true,
        autoScroll: true,
        pager: "#porePager"
    });


});

/**
 * 初始化查询缓存信息
 */
function initPoCacheInfo() {
    $.ajax({
        url: baseUrl + "returngoods/wmsReMaterialWarehouse/getPorecCache",
        dataType: "json",
        type: "post",
        data: {"LABEL_NO": $("#porebarcode").val()},
        success: function (resp) {
            if (resp.code == '0') {
                if (resp.result.length > 0) {
                    // document.getElementById("itemarrays").value=resp.result;//js原生写法
                    $("#itemarrays").val(resp.result.toString());
                    // $("input[name='itemarrays']").val(resp.result);
                    // $('#itemarrays').attr("value",resp.result);

                    $("#pore_labelid").val(resp.result[0].LABEL_ID);
                    $("#pore_boxqty").val(resp.result.length);
                    $("#hi_boxqty").val(resp.result.length);//用于计算已扫箱数
                    $("#pore_matnr").val(resp.result[0].MATNR);
                    $("#pore_maktx").val(resp.result[0].MAKTX);
                    $("#pore_qty").val(resp.result[0].BOX_QTY);
                    $("#pore_batch").val(resp.result[0].BATCH);

                    $("#poreDataGrid").jqGrid('clearGridData');
                    for(var i=0;i<=resp.page.list.length;i++)
                        $("#poreDataGrid").jqGrid('addRowData',i+1,resp.page.list[i]);
                }
            } else {
                alert(resp.msg);
            }
        }
    });
}

/**
 * 点击链接查看条码信息
 * @param rowId
 */
function viewPoBarInfo(rowId) {
    debugger;
    var rowData = $("#poreDataGrid").jqGrid('getRowData',rowId);
    showBarDataGrid();
    $("#poreBarDataGrid").jqGrid('setGridParam',{
        url:baseUrl + "returngoods/wmsReMaterialWarehouse/getBarGridPoreData",
        datatype : 'json',
        postData : rowData,
        page : 1
    },true).trigger("reloadGrid");
    document.getElementById("div_data_barinfo").style.display = "";
    document.getElementById("div_data_info").style.display = "none";

}

/**
 *  初始化条码grid
 */
function showBarDataGrid(){
    $("#poreBarDataGrid").jqGrid({
        url : "",
        datatype: "",
        jsonReader: jsonreader,
        colNames:['行项','条形码','数量','BYD批次'],
        colModel:[
            {name:'ROWID',index:'ROWID', width:50, align:'center'},
            {name:'LABEL_NO',index:'LABEL_NO', width:100, align:'center'},
            {name:'BOX_QTY',index:'BOX_QTY', width:63, align:'center'},
            {name:'BATCH',index:'BATCH', width:65, align:'center'},
        ],
        width: 216,
        multiselect: true,
        rowNum: 10,
        shrinkToFit:false,
        rownumbers:true,
        // autowidth:true,
        autoScroll: true,
        pager: "#poreBarPager"
    });
}

/**
 * 切换页签事件
 * @param tabFlag
 */
function changePoReTab(tabFlag) {
    if (tabFlag == "input") {
        document.getElementById("div_input").style.display = "";
        document.getElementById("div_datainfo").style.display = "none";//隐藏
        document.getElementById("div_headinfo").style.display = "none";//隐藏
        document.getElementById("div_headconfirm_result").style.display = "none";//隐藏
        document.getElementById("btnPoReDeleteBar").style.display = "none";//隐藏
        document.getElementById("btnConfirmInfo").style.display = "";
        document.getElementById("btnConfirm").style.display = "none";
    } else if (tabFlag == "data") {
        document.getElementById("div_input").style.display = "none";//隐藏
        document.getElementById("div_datainfo").style.display = "";
        document.getElementById("div_headinfo").style.display = "none";//隐藏
        document.getElementById("div_headconfirm_result").style.display = "none";//隐藏
        document.getElementById("btnConfirmInfo").style.display = "none";
        document.getElementById("btnConfirm").style.display = "none";//隐藏
        document.getElementById("div_data_info").style.display = "";
        document.getElementById("div_data_barinfo").style.display = "none";

        //获取采购订单收货明细数据
        getGridPoreData();
    } else if (tabFlag == "head") {
        document.getElementById("div_input").style.display = "none";//隐藏
        document.getElementById("div_datainfo").style.display = "none";//隐藏
        document.getElementById("div_headinfo").style.display = "";
        document.getElementById("div_headconfirm_result").style.display = "none";//隐藏
        document.getElementById("btnPoReDeleteBar").style.display = "none";//隐藏
        document.getElementById("btnConfirmInfo").style.display = "none";
        document.getElementById("btnConfirm").style.display = "";
    } else if (tabFlag == "result") {
        document.getElementById("div_input").style.display = "none";
        document.getElementById("div_datainfo").style.display = "none";//隐藏
        document.getElementById("div_headinfo").style.display = "none";//隐藏
        document.getElementById("div_headconfirm_result").style.display = "";//显示返回信息
        document.getElementById("btnPoReDeleteBar").style.display = "none";//隐藏
        document.getElementById("btnConfirmInfo").style.display = "none";
        document.getElementById("btnConfirm").style.display = "none";
    }
    //切换页面时刷新数据
    // console.log('-->changeTab'+tabFlag )
    ////this.$refs.ontabbar.$refs.otherPage.page.methods.reloadPage()
}

/**
 * 扫描条形码触发
 */
function poreScanPre() {
    $.ajax({
        url: baseUrl + "returngoods/wmsReMaterialWarehouse/validatePoReceiptLable",
        dataType: "json",
        type: "post",
        data: {"LABEL_NO": $("#barcode").val(),
        "BIN_CODE":$("#BIN_CODE").val(),
        },
        success: function (resp1) {
            if (resp1.code == '0') {
                $.ajax({
                    url: baseUrl + "returngoods/wmsReMaterialWarehouse/getPoDetailByBarcode",
                    dataType: "json",
                    type: "post",
                    data: {"LABEL_NO": $("#barcode").val(),
                        "TPNUM":$("#TPNUM").val()},
                    success: function (resp2) {
                        if (resp2.code == '0') {
                            var hiboxqty = parseInt($("#hi_boxqty").val()) + 1;
                            $("#hi_boxqty").val(hiboxqty);
                            $("#porescandata").val(resp2.result);

                            $("#wa_labelid").val(resp2.result.LABEL_ID);
                            $("#wa_boxqty").val(hiboxqty);
                            $("#wa_matnr").val(resp2.result.MATNR);
                            $("#wa_maktx").val(resp2.result.MAKTX);
                            $("#wa_qty").val(resp2.result.BOX_QTY);
                            $("#wa_batch").val(resp2.result.BATCH);
                        } else {
                            alert(resp2.msg);
                        }
                    }
                })
            } else {
                alert(resp1.msg);
            }
        }
    })
    // alert($("#porebarcode").val());

}

/**
 * 查询明细信息
 */
function getGridPoreData(){
    $.ajax({
        url: baseUrl + "returngoods/wmsReMaterialWarehouse/getGridPoreData",
        dataType : "json",
        type : "post",
        data: {"LABEL_NO": $("#barcode").val()},
        success:function(resp){
            $("#poreDataGrid").jqGrid('clearGridData');
            var jsonreader = {
                root:"list",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false
            };
            for(var i=0;i<=resp.page.list.length;i++)
                $("#poreDataGrid").jqGrid('addRowData',i+1,resp.page.list[i]);

            //切换展示明细数据div
            document.getElementById("div_datainfo").style.display="";
            document.getElementById("div_data_info").style.display="";

        }
    })
}



//键盘按键监听事件
document.onkeydown = function () {
    if (window.event.keyCode == 13 && document.activeElement.id == 'porebarcode') {//enter键以及当前激活元素为条形码的id
        poreScanPre();
    }
}

// 初始化返回事件
// function returnClick(){
//     history.go(-1);
// }

