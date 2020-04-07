var trindex = 11;
var mydata = [
    { id: "1",orderNo: ""},{ id: "2",orderNo: ""},{ id: "3",orderNo: ""},{ id: "4",orderNo: ""},{ id: "5",orderNo: ""},
    { id: "6",orderNo: ""},{ id: "7",orderNo: ""},{ id: "8",orderNo: ""},{ id: "9",orderNo: ""},{ id: "10",orderNo: ""},];
var vm = new Vue({
    el:'#rrapp',
    data:{},
    methods: {
        refresh:function(){
            $('#dataGrid').trigger( 'reloadGrid' );
        }
    }
});

$(function(){
    $("#inOrderNo").focus();
    $("#dataGrid").dataGrid({
        datatype: "local",
        data: mydata,
        colModel: [
            {label: '供应商数据批量同步', name: 'V',index:"orderNo", width: "100",align:"center",sortable:false,
                formatter:function(val){
                    return "<input type='text' style='width:98%'/>";
                }
            },
            {label: '', name: '',index:"empty", width: "500",align:"center",sortable:false,
                formatter:function(val){
                    return "";
                }
            }
        ],
        showCheckbox:true
    });

    $("#newOperation").click(function () {
        var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
            '<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trindex +'</td>' +
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
            '<td role="gridcell" style="text-align:center;" title="" aria-describedby="dataGrid_sys"><input type="text" style="width:98%">'+'</td>' +
			//'<td role="gridcell" style="text-align:center;" title="" aria-describedby="dataGrid_werks">'+'</td>' +
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td></tr>'
        $("#dataGrid tbody").append(addtr);
        trindex++ ;
    });

    $("#btnSync").click(function () {

        var orderNo = "";
        if($('#inOrderNo').val() != ""){
            orderNo = $('#inOrderNo').val() + ",";
        }
        var trs=$("#dataGrid").children("tbody").children("tr");
        $.each(trs,function(index,tr){
            var cbx=$(tr).find("td").find("input").attr("type");
            if(cbx!=undefined){
                var c_checkbox=$(tr).find('input[type=checkbox]');
                var ischecked=$(c_checkbox).is(":checked");
                if(ischecked){
                    var order = $(tr).find("td").eq(2).find("input").val();
                    if(order != "")orderNo += order + ",";
                }
            }
        });
        orderNo = orderNo.substring(0,orderNo.length-1);

        if("" == orderNo){
            alert("请输入或勾选需同步的供应商！");
            return false;
        }
        $("#btnSync").val("同步数据中,请稍候...");
        $("#btnSync").attr("disabled","disabled");
        $.ajax({
            url:baseURL+"config/CVendor/syncSAPVendor",
            dataType : "json",
            type : "post",
            data : {
                "VENDOR":orderNo
            },
            async: true,
            success: function (response) {
                js.showMessage(response.msg);
                $("#btnSync").removeAttr("disabled");
                $("#btnSync").val("同步");
            }
        });
    });

    $("#btn_refresh").click(function () {
        trindex = 11;
        $('#inOrderNo').val("");
        $('#dataGrid').trigger( 'reloadGrid' );
    });

    $("#btn_delete").click(function () {
        var trs=$("#dataGrid").children("tbody").children("tr");
        var reIndex = 1;
        $.each(trs,function(index,tr){
            var td = $(tr).find("td").eq(0);
            var td_check = $(tr).find("td").eq(1).find("input");
            if(index > 0){
                //if(tr.getAttribute("aria-selected")){
                var ischecked=$(td_check).is(":checked");
                if(ischecked){
                    //$(td_check).attr('checked', false);
                    //$('#dataGrid').jqGrid('delRowData',index);
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
    });

    $('#dataGrid').bind('paste', function(e) {
        e.preventDefault(); // 消除默认粘贴
        // 获取粘贴板数据
        var data = null;
        var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
        data = clipboardData.getData('Text');
        // console.log(data.replace(/\t/g, '\\t').replace(/\n/g,'\\n')); //data转码

        // 解析数据
        var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
            return (item !== "")
        }).map(function(item) {
            return item.split("\t");
        });
        // 输出至网页表格
        var tab = this; // 表格DOM
        var td = $(e.target).parents('td');
        var startRow = td.parents('tr')[0].rowIndex;
        var startCell = td[0].cellIndex;
        var rows = tab.rows.length; // 总行数
        //for (var i = 0; i < arr.length && startRow + i < rows; i++) {
        for (var i = 0; i < arr.length; i++) {
            if(startRow + i >= rows){
                var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
                    '<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trindex + '</td>' +
					'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
                    '<td role="gridcell" style="text-align:center;" title="" aria-describedby="dataGrid_sys"><input type="text" style="width:98%">'+'</td>' +
					//'<td role="gridcell" style="text-align:center;" title="" aria-describedby="dataGrid_werks">'+'</td>' +
					'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td></tr>'
                $("#dataGrid tbody").append(addtr);
                trindex++ ;
            }
            var cells = tab.rows[startRow + i].cells.length; // 该行总列数
            for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
                var cell = tab.rows[startRow + i].cells[startCell + j];
                $(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
            }
        }
    });

});