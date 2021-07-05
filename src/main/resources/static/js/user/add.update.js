layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    // 引入 formSelects 模块
    formSelects = layui.formSelects;


    /**
     * 触发表单 submit
     */

    form.on("submit(addOrUpdateUser)",function(data){
        //弹出层
       var index= layer.msg("数据加载中",{time:500,shade:0.8,icon:16});
        //构造title
        var url=ctx+"/user/save";
        //判断是否更新，还是添加
        if($("input[name='id']").val()){
            url=ctx+"/user/update"
        }
        //发送ajax
        $.post(url,data.field,function(obj){
            if(obj.code== 200){
                window.setTimeout(function(){
                    //关闭弹出层
                    top.layer.close(index);
                    //关闭ifream
                    layer.closeAll("iframe");
                },500)
                //加载一下数据
                parent.location.reload();
            }else{
                //添加失败了
                layer.msg(obj.msg,{icon:5 });
            }
        },"json");
        //阻止
        return false;
    });

    /**
     * 取消
     */
    $("#closeBtn").click(function(){
        //假设这是iframe页
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    /**
     * 加载角色，
     */
    var userId=$("input[name=id]").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + "/role/queryRoles?userId="+userId,
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);

});