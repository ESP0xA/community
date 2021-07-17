function like(btn, entityType, entityId, entityUserId) {
    //window.alert("debug");
    // 向服务器发送一个异步请求对点赞进行处理
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, entityId, entityUserId},
        function(data) {    // 回调函数，对返回的数据进行处理
            data = $.parseJSON(data);
            //$(btn).children("b").text("已赞");
            if (data.code == 0) {   // 成功，改变页面赞的状态，和数量
                // 通过传递进来的按钮改变下级标签b和i的值
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            } else {    // 失败，弹出提示
                alert(data.msg);
            }
        }
    );
}