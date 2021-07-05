layui.use(['form','jquery','jquery_cookie','table'], function () {
    var form = layui.form,
        table=layui.table,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    /**
     * 渲染列表
     */

    var  tableIns = table.render({
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });


    /**
     * 搜索
     */

    $(".search_btn").click(function (){
        tableIns.reload({
            where:{
                userName:$("input[name=userName]").val(),
                email:$("input[name=email]").val(),
                phone:$("input[name=phone]").val()
            },
            page:{
                curr:1
            }
        });

    });


    /**
     * 触发头部工具栏
     */

    table.on("toolbar(users)",function(obj){
        // console.log(obj);
       var checkStatus= table.checkStatus(obj.config.id);
        var dataEvent=obj.event;
        if(dataEvent === 'add'){
            openAddOrUpdateDialog();
            return ;
        }else if(dataEvent === 'del'){
            deleteUserDialog(checkStatus.data);
            return ;
        }
    });

    function  deleteUserDialog(data){
        //验证
        if(data.length==0){
            layer.msg("请选择要删除的数据?");
            return ;
        }
        //声明数组存储数据
        var ids=[];
        //遍历
        for(var x in data){
            ids.push(data[x].id);
        }
        layer.confirm("你确定要删除数据吗?",{
            btn:["确定","取消"],
        },function(index){
            layer.close(index);
            //发送ajax
            $.ajax({
                type:"post",
                data:{"ids":ids.toString()},
                url:ctx+"/user/delete",
                dataType:"json",
                success:function (obj){
                    if(obj.code == 200){
                        //重新加载表格
                        tableIns.reload();
                    }else{
                        //删除失败
                        layer.msg(obj.msg,{icon: 5 });
                    }
                }
            });

        });


    }


    function  openAddOrUpdateDialog(id){
        //默认添加操作
        var title="<h3>用户模块--添加操作</h3>";
        var url=ctx+"/user/addOrUpdatePage";
        if(id){
            title="<h3>用户模块--更新操作</h3>";
            url=url+"?id="+id;
        }
        //弹出层，iframe
        layer.open({
            type:2,
            content:url,
            title:title,
            area:["650px","400px"],
            maxmin:true
        });


    }
    /**
     * 触发行内工具栏
     */

    table.on("tool(users)",function(obj){
        console.log(obj);
        var dataEvent=obj.event;
        if(dataEvent === 'edit'){
            openAddOrUpdateDialog(obj.data.id);
            return ;
        }else if(dataEvent === 'del'){
            layer.confirm("你确定要删除数据吗?",{
                btn:["确定","取消"],
            },function(index){
                layer.close(index);
                //发送ajax
                $.ajax({
                    type:"post",
                    data:{"ids":obj.data.id},
                    url:ctx+"/user/delete",
                    dataType:"json",
                    success:function (obj){
                        if(obj.code == 200){
                            //重新加载表格
                            tableIns.reload();
                        }else{
                            //删除失败
                            layer.msg(obj.msg,{icon: 5 });
                        }
                    }
                });

            });
            return ;
        }
    });

})