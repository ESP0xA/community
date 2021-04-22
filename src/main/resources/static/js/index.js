$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 点击发布后隐藏弹出框
	$("#publishModal").modal("hide");

	// 向服务器发送异步请求

	// 使用id选择器获取id为 recipient-name 和 message-text 的文本框的值
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/discuss/add",			// 访问路径
		{"title":title,"content":content},		// 提交的数据
		// 回调函数处理返回结果
		function(data) {
			data = $.parseJSON(data);

			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后，自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果成功发帖，刷新页面
				if (data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);
}