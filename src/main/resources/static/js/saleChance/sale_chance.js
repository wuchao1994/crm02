layui.use(['form', 'jquery', 'jquery_cookie', 'table'], function () {
    var form = layui.form,
        layer = layui.layer,
        table = layui.table,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url: ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth: 95,
        page: true, // 开启分页
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "saleChanceListTable",
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'chanceSource', title: '机会来源', align: "center"},
            {field: 'customerName', title: '客户名称', align: 'center'},
            {field: 'cgjl', title: '成功几率', align: 'center'},
            {field: 'overview', title: '概要', align: 'center'},
            {field: 'linkMan', title: '联系人', align: 'center'},
            {field: 'linkPhone', title: '联系电话', align: 'center'},
            {field: 'description', title: '描述', align: 'center'},
            {field: 'createMan', title: '创建人', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center'},
            {field: 'uname', title: '指派人', align: 'center'},
            {field: 'assignTime', title: '分配时间', align: 'center'},
            {
                field: 'state', title: '分配状态', align: 'center', templet: function (d) {
                    return formatterState(d.state);
                }
            },
            {
                field: 'devResult', title: '开发状态', align: 'center', templet: function (d) {
                    return formatterDevResult(d.devResult);
                }
            },
            {title: '操作', templet: '#saleChanceListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    /**
     * 格式化分配状态
     *  0 - 未分配
     *  1 - 已分配
     *  其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state) {
        if (state == 0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if (state == 1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     *  0 - 未开发
     *  1 - 开发中
     *  2 - 开发成功
     *  3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value) {
        if (value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if (value == 1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if (value == 2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if (value == 3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }


    /**
     * 开发搜索功能
     */

    $(".search_btn").click(function () {
        //上述方法等价于
        table.reload('saleChanceListTable', {
            where: { //设定异步数据接口的额外参数，任意设
                customerName: $("input[name='customerName']").val(),
                createMan: $("input[name='createMan']").val(),
                state: $("#state").val()
            }
            , page: {
                curr: 1 //重新从第 1 页开始
            }
        }); //只重载数据

    });

    /**
     * 触发头部工具栏
     */
    //触发事件
    table.on('toolbar(saleChances)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        console.log(checkStatus);
        switch (obj.event) {
            case 'add':
                openAddOrupdateDialog();
                break;
            case 'del':
                deleteSaleChanceDialog(checkStatus.data);
                break;
        }
        ;
    });

    /**
     *
     * @param ids
     */

    function deleteSaleChanceDialog(data) {
        //判断
        if (data.length == 0) {
            layer.msg("请选择删除的数据");
            return;
        }

        layer.confirm("你确定要删除此数据吗?", {
            btn: ["确定", "取消"]
        }, function (index) {
            //关闭确认框
            layer.close(index);
            //收集数据数据ids=1&ids=2&ids=3;
            var ids = [];
            //遍历
            for (var x in data) {
                ids.push(data[x].id);
            }
            console.log(ids.toString() + "<<")
            //删除
            $.ajax({
                type: "post",
                url: ctx + "/sale_chance/dels",
                data: {"ids": ids.toString()},
                dataType: "json",
                success: function (obj) {
                    if (obj.code == 200) {
                        //重新加载页面
                        tableIns.reload()
                    } else {
                        //删除失败了
                        layer.msg(obj.msg, {icon: 5});
                    }
                }
            });
        });

    }


    function openAddOrupdateDialog(sid) {
        var title = "<h2>营销机会--添加操作</h2>";
        var url = ctx + "/sale_chance/addOrUpdateSaleChancePage";
        //非空即为真
        if (sid) {
            title = "<h2>营销机会--更新操作</h2>";
            url = url + "?id=" + sid;
        }
        console.log(url + "<<")
        //弹出层
        layer.open({
            title: title,
            type: 2,
            content: url,
            area: ["500px", "620px"],
            maxmin: true
        });
    }

    /**
     * 触发行内工具栏
     */
    //工具条事件
    table.on('tool(saleChances)', function (obj) { //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        console.log(data);
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if (layEvent === 'edit') { //查看
            openAddOrupdateDialog(obj.data.id);
        } else if (layEvent === 'del') { //删除
            layer.confirm('真的删除行么', function (index) {
                //关闭弹出层
                layer.close(index);
                //向服务端发送删除指令
                $.ajax({
                    type: "post",
                    url: ctx + "/sale_chance/dels",
                    data: {"ids":data.id},
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 200) {
                            //删除成功了
                            tableIns.reload();
                        } else {
                            //删除失败
                            layer.msg(data.msg, {icon: 5})
                        }
                    }
                });
            });
        }
    });

});