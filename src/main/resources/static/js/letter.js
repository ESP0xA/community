$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	// 点击发送按钮后先隐藏对话框
	$("#sendModal").modal("hide");

	// 提交数据
	// 使用id选择器获取目标用户名
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步post请求
	$.post(
		CONTEXT_PATH + "/letter/send",			// 页面提交的访问路径
		{"toName":toName, "content":content},	// 页面提交的Json
		function(data) {						// 回调函数处理服务端返回的结果
			data = $.parseJSON(data);			// jQuery处理数据转为jSON格式
			if (data.code == 0)	{				// 后端反馈成功，提示框显示成功
				// id选择器获取提示框
				// 将内容设置为 发送成功
				$("#hintBody").text("发送成功!");
			} else {
				$("#hintBody").text(data.msg);
			}

			// 显示发送结果
			$("#hintModal").modal("show");			// 提示框显示
			setTimeout(function () {
				$("#hintModal").modal("hide");		// 2秒后隐藏
				location.reload();					// 不管成功或失败，刷新页面
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}