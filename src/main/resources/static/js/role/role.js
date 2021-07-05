layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click",function(){
        tableIns.reload({
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                roleName: $("input[name='roleName']").val()
            }
        });
    });

    /**
     * 触发头部工具栏
     */
    table.on("toolbar(roles)",function(obj){
       var checkStatus= table.checkStatus(obj.config.id);
       console.log(checkStatus.data);
       //判断
        if(obj.event === 'add'){
            openAddOrUpdateDialog();
            return;
        }else if(obj.event === 'grant'){
            openAddGrandDialog(checkStatus.data);
            return ;
        }
    });


    function  openAddGrandDialog(datas){
        if(datas.length==0){
            layer.msg("请选择角色",{icon:5});
            return ;
        }

        if(datas.length>1){
            layer.msg("暂时不支持批量授权",{icon:5});
            return ;
        }
        var title="<h3>角色授权--操作</h3>";
        var url=ctx+"/role/toAddGrandPage?roleId="+datas[0].id;
        //弹出框，
        layer.open({
            type:2,
            title:title,
            content: url,
            maxmin:true,
            area:["600px","280px"]
        })

    }


    function  openAddOrUpdateDialog(rid){
        var title="<h3>角色模块---新增操作</h3>";
        var url=ctx+"/role/toAddOrUpdatePage";
        if(rid){
            title="<h3>角色模块---更新操作</h3>";
            url+="?rid="+rid;
        }
        //弹出层
        layer.open({
            type:2,
            title:title,
            content:url,
            maxmin:true,
            area:["600px","280px"]
        });
    }
    /**
     * 触发行内工具栏
     */
    table.on("tool(roles)",function(obj){
        if(obj.event === 'edit'){
            openAddOrUpdateDialog(obj.data.id);
            return ;
        }else if(obj.event === 'del'){

            layer.msg();
            layer.confirm("你确定要是删除数据吗?",{
                btn:["确认","取消"],
            },function(index){
                //关闭
                layer.close(index);
                //发送
                $.post(ctx+"/role/delete",{"rid":obj.data.id},function(data){
                    if(data.code == 200){
                        //刷新数据
                        tableIns.reload();
                    }else{
                        //失败了
                        layer.msg(data.msg,{icon: 5 });
                    }

                },"json")
            });

        }
    });
});