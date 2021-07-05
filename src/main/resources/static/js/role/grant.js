    var zTreeObj;
    $(function () {
        loadModuleInfo();
    });


    function loadModuleInfo() {
        //发送ajax
        $.ajax({
            type: "post",
            url: ctx + "/role/queryModule",
            dataType: "json",
            data: {"roleId":$("#roleId").val()},
            success: function (data) {
                // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
                var setting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    view: {
                        showLine: false
                        // showIcon: false
                    },
                    check: {
                        enable: true,
                        chkboxType: {"Y": "ps", "N": "ps"}
                    },
                    callback: {
                        onCheck: zTreeOnCheck
                    }
                };
                var zNodes = data;
                zTreeObj = $.fn.zTree.init($("#test1"), setting, zNodes);
            }
        });
    }

    /**
     * 当点击ztree的复选框，触发的函数
     * @param event
     * @param treeId
     * @param treeNode
     */
    function zTreeOnCheck(event, treeId, treeNode) {
        //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
        var nodes = zTreeObj.getCheckedNodes(true);
        //console.log(nodes);
        //nodes--mid,roleId
        var roleId = $("#roleId").val();
        //获取mid
        var mIds = "mIds=";
        //遍历
        for (var x in nodes) {
            if (x < nodes.length - 1) {
                mIds = mIds + nodes[x].id + "&mIds=";
            } else {
                mIds += nodes[x].id;
            }
        }
        console.log(mIds);

        //发送ajax批量添加
        $.ajax({
            type: "post",
            data: mIds + "&roleId=" + roleId,
            url: ctx + "/role/addGrant",
            dataType: "json",
            success: function (data) {
                console.log(data);
                alert(data.msg);
            }

        });
    };
