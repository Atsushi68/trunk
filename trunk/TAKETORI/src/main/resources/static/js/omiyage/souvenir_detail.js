$(function() {

	// カートに追加押下時
	$("#add_cart_btn").on("click", function() {
		// submit
		$("#add_cart_form").submit();
		return false;
	});

});