$(function() {

	// 発行ボタンクリック時
	$("#issue").on("click", function() {
		Swal.fire({
			title: '新規発行',
			html:'チェックした客室のログイン用QRコードを<br>新規発行します',
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '発行',
			cancelButtonText: 'キャンセル'
		})
		.then((result) => {
			if (result.value) {
				$("#room_form").submit();
			}
		});
		return false;
	});

	// ダウンロードボタンクリック時
	$("#download").on("click", function() {
		var kbn = $(this).data("kbn");
		window.location.href = "/order/admin/room/qrcode/download/" + kbn;
		return false;
	});

	// 再発行ボタンクリック時
	$(".btn_reissue").on("click", function() {

		// roomNo取得
		var roomNo = $(this).data("room");

		Swal.fire({
			title: '再発行',
			html:'客室' + roomNo + 'のログイン用QRコードを<br>再発行します',
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '再発行',
			cancelButtonText: 'キャンセル'
		})
		.then((result) => {
			if (result.value) {
				$("#roomNo").val(roomNo);
				$("#kbn").val("再発行");
				$("#room_form").submit();
			}
		});
		return false;
	});

	// 使用不可にする
	$(".btn_del").on("click", function() {

		// roomNo取得
		var roomNo = $(this).data("room");

		Swal.fire({
			title: '使用不可にする',
			html:'客室' + roomNo + 'のログイン用QRコードを<br>使用不可にします',
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '使用不可',
			cancelButtonText: 'キャンセル'
		})
		.then((result) => {
			if (result.value) {
				$("#roomNo").val(roomNo);
				$("#kbn").val("使用不可");
				$("#room_form").submit();
			}
		});
		return false;
	});

	// 使用不可を解除
	$(".btn_restore").on("click", function() {

		// roomNo取得
		var roomNo = $(this).data("room");

		Swal.fire({
			title: '使用不可解除する',
			html:'客室' + roomNo + 'のログイン用QRコードの<br>使用不可を解除します',
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '解除',
			cancelButtonText: 'キャンセル'
		})
		.then((result) => {
			if (result.value) {
				$("#roomNo").val(roomNo);
				$("#kbn").val("解除");
				$("#room_form").submit();
			}
		});
		return false;
	});

	// チェック全選択
	$('#chk_all').on('click', function() {
		$("input[name='chk_rooms']").prop('checked', this.checked);
	});
    // チェックボックスがクリックされたら、
    $("input[name='chk_rooms']").on('click', function() {
    	if ($('#boxes :checked').length == $('#boxes :input').length) {
    		// 全てのチェックボックスにチェックが入っていたら、「全選択」 = checked
    		$('#chk_all').prop('checked', true);
    	} else {
    		// 1つでもチェックが入っていたら、「全選択」 = checked
    		$('#chk_all').prop('checked', false);
    	}
    });

});