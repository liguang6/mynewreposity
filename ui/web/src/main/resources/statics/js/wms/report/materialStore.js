var mydata = [
    {id: "1", more_value: ""}, {id: "2", more_value: ""}, {id: "3", more_value: ""}, {id: "4", more_value: ""}, {id: "5", more_value: ""}];
var listdata = [];
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList:false,
        wh_list: [],
        werks: '',
        whNumber: '',
        lgort:'',
        receipt_no:'',
        lifnr:'',
        matnr:'',
        batch:'',
        gr_area:'',
        po_no:'',
        receipt_date:'',
        receiver:'',
        manager:''
    },
    watch: {
        werks: {
            handler: function (newVal, oldVal) {
                $.ajax({
                    url: baseURL + "common/getWhDataByWerks",
                    dataType: "json",
                    type: "post",
                    data: {
                        "WERKS": newVal,
                        "MENU_KEY": "STOCK_COMPARE"
                    },
                    async: true,
                    success: function (response) {
                        vm.wh_list = response.data;
                        if (response.data.length > 0) {
                            vm.whNumber = response.data[0].WH_NUMBER//方便 v-mode取值
                        }
                    }
                });
            }
        },
        whNumber:{
            handler:function(newVal,oldVal){
                $.ajax({
                    url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
                    data:{"WERKS":vm.werks,"WH_NUMBER":newVal},
                    success:function(resp){
                        vm.relatedareaname = resp.result;
                    }
                })
            }

        }
    },
    created: function () {
        this.werks = $("#werks").val();
        $.ajax({
            url: baseURL + "common/getWhDataByWerks",
            dataType: "json",
            type: "post",
            data: {
                "WERKS": this.werks,
                "MENU_KEY": "STOCK_COMPARE"
            },
            async: true,
            success: function (response) {
                var strs = "";
                this.wh_list = response.data;
                if (response.data.length > 0) {
                    vm.whNumber = response.data[0].WH_NUMBER//方便 v-mode取值
                }
            }
        });
    },
    methods: {
        query: function () {
            // var matnr=$("#matnr").val();
            // var batch=$("#batch").val();
            // if(matnr==''){
            //     layer.msg("请输入物料号！",{time:1000});
            //     return false;
            // }
            // if(batch==''){
            //     layer.msg("请输入批次号！",{time:1000});
            //     return false;
            // }

            $(".btn").attr("disabled", true);

            js.loading();
            vm.$nextTick(function(){//数据渲染后调用
                $("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                js.closeLoading();
            })

            $(".btn").attr("disabled", false);
        },
        exp: function () {
            export2Excel();
        },
        more: function (e) {
            $("#moreGrid").html("")
            $.each(mydata, function (i, d) {
                var tr = $("<tr index=" + i + "/>");
                var td_1 = $("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
                var td_2 = $("<td style='height:30px;padding: 0 0' />").html("<input type='text'name='val_more' style='width:100%;height:30px;border:0'> ")
                $(tr).append(td_1)
                $(tr).append(td_2)
                $("#moreGrid").append(tr)
            })

            layer.open({
                type: 1,
                title: '选择更多',
                closeBtn: 1,
                btn: ['确认'],
                offset: 't',
                resize: true,
                maxHeight: '300',
                area: ['400px', '400px'],
                skin: 'layui-bg-green', //没有背景色
                shadeClose: false,
                content: "<div id='more_div'>" + $('#layer_more').html() + "</div>",
                btn1: function (index) {
                    var inputs = $("#more_div tbody").find("tr").find("input[type='text']");
                    var values = []
                    $.each(inputs, function (i, input) {
                        if ($(input).val().trim().length > 0) {
                            values.push($(input).val())
                        }
                    })
                    $(e).val(values.join(","))
                }
            });
        },

    }
});
$(function () {
    $('#dataGrid').dataGrid({
        datatype: "local",
        searchForm: $("#searchForm"),
        data: listdata,
        columnModel: [
            {header: '工厂', name: 'WERKS', width: 45, sortable: false, align: "center"},
            {header: '仓库号', name: 'WH_NUMBER', width: 60, sortable: false, align: "center"},
            {header: '收货单号', name: 'RECEIPT_NO', width: 80, sortable: false, align: "center"},//2
            {header: '收货单行项目', name: 'RECEIPT_ITEM_NO', width: 100, sortable: false, align: "center"},//2
            {header: '送检单号', name: 'INSPECTION_NO', width: 80, sortable: false, align: "center"},//3
            {header: '送检单行项目', name: 'INSPECTION_ITEM_NO', width: 100, sortable: false, align: "center"},//3
            {header: '供应商代码', name: 'LIFNR', width: 80, sortable: false, align: "center"},
            {header: '供应商名称', name: 'LIKTX', width: 80, sortable: false, align: "center"},
            {header: '业务类型', name: 'VALUE', width: 70, sortable: false, align: "center"},//complex //2
            {header: '物料号', name: 'MATNR', width: 60, sortable: false, align: "center"},
            {header: '物料描述', name: 'MAKTX', width: 150, sortable: false, align: "center"},
            {header: '计量单位', name: 'UNIT', width: 80, sortable: false, align: "center"},
            {header: '数量', name: 'RH_QTY', width: 45, sortable: false, align: "center"},
            {header: '箱数', name: 'BOX_COUNT', width: 45, sortable: false, align: "center"},//2
            {header: '库存类型', name: 'SOBKZ', width: 70, sortable: false, align: "center"},//2
            {header: '库位', name: 'LGORT', width: 45, sortable: false, align: "center"},//2
            {header: '储位', name: 'GR_AREA', width: 45, sortable: false, align: "center"},
            {header: '收货日期', name: 'RECEIPT_DATE', width: 70, sortable: false, align: "center"},//2
            {header: '收货人', name: 'RECEIVER', width: 60, sortable: false, align: "center"},//2
            {header: '采购订单', name: 'PO_NO', width: 70, sortable: false, align: "center"},//2
            {header: '采购订单行项目', name: 'PO_ITEM_NO', width: 100, sortable: false, align: "center"},//2
            {header: 'BYD批次', name: 'BATCH', width: 80, sortable: false, align: "center"},
            {header: '需求跟踪号', name: 'BEDNR', width: 80, sortable: false, align: "center"},//2
            {header: '申请者', name: 'AFNAM', width: 60, sortable: false, align: "center"},//2
            {header: '上架责任人', name: 'MANAGER', width: 80, sortable: false, align: "center"},//complex //5 4
            {header: '在库天数', name: 'STORE_DATE', width: 70, sortable: false, align: "center"},//未完成 //2
            {header: '检验日期', name: 'QC_DATE', width: 70, sortable: false, align: "center"},//6
            {header: '合格数量', name: 'INABLE_QTY', width: 80, sortable: false, align: "center"},//6
            {header: '不合格数量', name: 'RETURNABLE_QTY　', width: 80, sortable: false, align: "center"},//2
            {header: '质检状态', name: 'INSPECTION_ITEM_STATUS', width: 80, sortable: false, align: "center"},//3
            {header: '质检结果', name: 'QC_RESULT_CODE', width: 80, sortable: false, align: "center"},//6
            {header: '质检原因', name: 'QC_RESULT', width: 80, sortable: false, align: "center"},//6
            {header: '原供应商代码', name: '', width: 100, sortable: false, align: "center"},//未完成
            {header: '原供应商名称', name: '', width: 100, sortable: false, align: "center"},//未完成
            {header: '未上架数量', name: 'RH_QTY', width: 80, sortable: false, align: "center"},
            {header: '供应商批次', name: '', width: 80, sortable: false, align: "center"},//未完成
            {header: '送货单', name: 'ASNNO', width: 60, sortable: false, align: "center"}//2
        ],
        viewrecords: true,
        shrinkToFit:false,
        showCheckbox:false,
        rowNum: 15,
        rowList : [15,50,100,200],
        width:3500,
        height:'auto',
        gridComplete: function () {
            // var ids = $("#dataGrid").getDataIDs();
            // for(var i=0;i<ids.length;i++){
            //     var rowData = $("#dataGrid").getRowData(ids[i]);
            //     if(Number(rowData.ERP_QTY) != Number(rowData.STOCK_QTY)){
            //         $('#'+ids[i]).find("td").addClass("SelectBG");
            //     }
            // }
        }
    });

});

function export2Excel() {
    var column = [
        {title: '工厂', data: 'WERKS', class: "center"},
        {title: '仓库号', data: 'WH_NUMBER', class: "center"},
        {title: '收货单号', data: 'RECEIPT_NO', class: "center"},
        {title: '收货单行项目', data: 'RECEIPT_ITEM_NO', class: "center"},
        {title: '送检单号', data: 'INSPECTION_NO', class: "center"},
        {title: '送检单行项目', data: 'INSPECTION_ITEM_NO', class: "center"},
        {title: '供应商代码', data: 'LIFNR', class: "center"},
        {title: '供应商名称', data: 'LIKTX', class: "center"},
        {title: '业务类型', data: 'VALUE', class: "center"},
        {title: '物料号', data: 'MATNR', class: "center"},
        {title: '物料描述', data: 'MAKTX', class: "center"},
        {title: '计量单位', data: 'UNIT', class: "center"},
        {title: '数量', data: 'RH_QTY', class: "center"},
        {title: '箱数', data: 'BOX_COUNT', class: "center"},
        {title: '库存类型', data: 'SOBKZ', class: "center"},
        {title: '库位', data: 'LGORT', class: "center"},
        {title: '储位', data: 'BIN_CODE', class: "center"},
        {title: '收货日期', data: 'RECEIPT_DATE', class: "center"},
        {title: '收货人', data: 'RECEIVER', class: "center"},
        {title: '采购订单', data: 'PO_NO', class: "center"},
        {title: '采购订单行项目', data: 'PO_ITEM_NO', class: "center"},
        {title: 'BYD批次', data: 'BATCH',  class: "center"},
        {title: '需求跟踪号', data: 'BEDNR', class: "center"},
        {title: '申请者', data: 'AFNAM', class: "center"},
        {title: '上架责任人', data: 'MANAGER', class: "center"},
        {title: '在库天数', data: 'RECEIPT_DATE',class: "center"},//未完成
        {title: '检验日期', data: 'QC_DATE', class: "center"},
        {title: '合格数量', data: 'INABLE_QTY', class: "center"},
        {title: '不合格数量', data: 'RETURNABLE_QTY　', class: "center"},
        {title: '质检状态', data: 'INSPECTION_ITEM_STATUS',  class: "center"},
        {title: '质检结果', data: 'QC_RESULT_CODE', class: "center"},
        {title: '质检原因', data: 'QC_RESULT',  class: "center"},
        {title: '原供应商代码', data: '', class: "center"},//未完成
        {title: '原供应商名称', data: '',  class: "center"},//未完成
        {title: '未上架数量', data: 'RH_QTY',  class: "center"},
        {title: '供应商批次', data: '', class: "center"},//未完成
        {title: '送货单', data: 'ASNNO', class: "center"},
    ]

    var results = [];
    var params = $("#searchForm").serialize();
    var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i");
    var l = params.replace(reg, '$1');
    params = l;
    $.ajax({
        url: baseURL + "report/materialStore/list",
        dataType: "json",
        type: "post",
        data: params,
        async: false,
        success: function (response) {
            results = response.page.list;
            console.log("results", results);
        }
    });
    var rowGroups=[];
//	getMergeTable1(results,column,rowGroups,"WMS与SAP库存对比报表");
    toExcel("收料房报表", results, column);
}

function getLgortList() {
    var lgortList = [];
    //查询
    $.ajax({
        url: baseUrl + "common/getLoList",
        async: false,
        data: {
            "DEL": '0',
            "WERKS": $("#werks").val(),
        },
        success: function (resp) {
            lgortList = resp.data;
        }
    });
 
    return lgortList;
}
//

