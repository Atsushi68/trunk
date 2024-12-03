$(function() {

	// autocomplete
	$("#omiyageName").autocomplete({
		source: function (req, resp) {
			$.ajax({
				url: '/order/admin/omiyage/autocomplete',
				type: 'get',
				data: {"search": req.term},
				dataType: 'json',
				success: function (response) {

					// 商品が取得できたか
					if (response.length == 0) {
						resp(['']);
					} else {
						// autocomplete用の配列に格納しなおし
						var list = {};
						$.each(response, function(index, value) {
							var detail = {};
							detail["label"] = value.id + ": [" + value.category + "] " + value.name + " ￥" + value.price;
							detail["value"] = value.id + ": [" + value.category + "] " + value.name + " ￥" + value.price;
							detail["id"] = value.id;
							list[index] = detail;
						});
						resp(list);
					}
				},
				error: function (err) {
					resp(['']);
				}
			});
		},
		select: function(e, ui) {
			if (ui.item) {
				// $('#omiyageName_id').val(ui.item.id); // 商品IDはhiddenに設定
			}
		}
	});

	// お土産の注文キャンセル
	$(document).on("click", ".cancel", function() {

		// クリックした注文番号
		var roomNo = $('#roomNo').val();
		var orderId = $(this).data("order");
		var txt = 'お土産の注文を取り消します。よろしいですか？';
		Swal.fire({
			title: '注文取り消し',
			text: txt,
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '取り消し',
			cancelButtonText: 'キャンセル'
		}).then((result) => {
			if (result.value) {
				$("#order_id").val(orderId);
				$("#room_no").val(roomNo);
				$("#cancelForm").submit();
			}
		});
		return false;
	});

	// 迷子注文のキャンセル
	$(document).on("click", ".processed", function() {

		// クリックした注文番号
		var roomNo = $('#roomNo').val();
		var orderId = $(this).data("order");
		var txt = 'お土産の注文を処理済にします。よろしいですか？';
		Swal.fire({
			title: '処理済',
			text: txt,
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '処理済',
			cancelButtonText: 'キャンセル'
		}).then((result) => {
			if (result.value) {
				$("#lost_order_id").val(orderId);
				$("#lost_room_no").val(roomNo);
				$("#processedForm").submit();
			}
		});
		return false;
	});


	// お土産注文表示を更新
	function reloadOmiyage() {
		var html = '';
		var roomNo = $('#roomNo').val();

		$.ajax({
			url: '/order/admin/omiyage/order/' + roomNo + '/',
			type: 'GET',
			dataType: 'json'
		})
		.done(function(data) {
			html += '<table class="omiyage">';
			html += '<thead><tr>';
			html += '<th class="omiyage_name">商品名</th><th class="price">金額</th><th class="num">個数</th><th class="action"></th>';
			html += '</tr></thead>';
			html += '<tbody>';
			$.each(data,function(i, item) {
				html += '<tr>';
				html += '<td>' + item['name'] + '</td>';
				html += '<td class="price">' + item['price'].toLocaleString() + '円</td>';
				html += '<td class="num">' + item['num'] + '個</td>';
				html += '<td class="action"><button class="btn waves-effect waves-light red cancel" data-order="' + item['order_id'] + '"><i class="material-icons left">cancel</i>取り消し</button></td>';
				html += '</tr>';
			});
			html += '</tbody>';
			html += '<table>';
		})
		.fail(function() {
			html += '<p>ERROR - 取得できませんでした</p>';
		})
		.always(function() {
			var date = new Date();
			var y = date.getFullYear();
			var m = ("00" + (date.getMonth()+1)).slice(-2);
			var d = ("00" + date.getDate()).slice(-2);
			var h = ("00" + date.getHours()).slice(-2);
			var mi = ("00" + date.getMinutes()).slice(-2);
			html += '<p class="update_date">' + y + "/" + m + "/" + d + " " + h + ':' +  mi + '  更新<p>';
			$('#omiyageTable').html(html);
		});
	}

	// 迷子注文一覧
	function getLostOrderList() {

		html = "";
		var roomNo = $('#roomNo').val();

		$.ajax({
			url: '/order/admin/omiyage/lost/order/' + roomNo + '/',
			type: 'GET',
			dataType: 'json'
		})
		.done(function(data) {
			if (data.length != 0) {
				html += '<div class="card-panel red lighten-4">';
				html += '<b class="red-text">※この客室で、現在のユーザーと紐付いていない注文が存在します。下記注文を処理して、処理済にしてください。</b>';
				html += '<table class="omiyage">';
				html += '<thead><tr>';
				html += '<th class="omiyage_name">商品名</th><th class="price">金額</th><th class="num">個数</th><th class="action"></th>';
				html += '</tr></thead>';
				html += '<tbody>';
				$.each(data,function(i, item) {
					html += '<tr>';
					html += '<td>' + item['name'] + '</td>';
					html += '<td>' + item['price'].toLocaleString() + '円</td>';
					html += '<td>' + item['num'] + '個</td>';
					html += '<td><button class="btn waves-effect waves-light processed" data-order="' + item['order_id'] + '"><i class="material-icons left">done</i>処理済</button></td>';
					html += '</tr>';
				});
				html += '</tbody>';
				html += '<table>';

				var date = new Date();
				var y = date.getFullYear();
				var m = ("00" + (date.getMonth()+1)).slice(-2);
				var d = ("00" + date.getDate()).slice(-2);
				var h = ("00" + date.getHours()).slice(-2);
				var mi = ("00" + date.getMinutes()).slice(-2);
				html += '<p class="update_date">' + y + "/" + m + "/" + d + " " + h + ':' +  mi + '  更新<p>';
				html += '</div>';
				$('#lostOrderTable').html(html);
			}
		})
		.fail(function() {
			html += '<p>ERROR - 取得できませんでした</p>';
		})
	}

	$(window).on('load',function(){
		// 定期実行
	    setInterval(function(){
	    	reloadOmiyage();
	    	getLostOrderList();
	    }, 300000);

	    // 初回実行
	    reloadOmiyage();
	    getLostOrderList();
	});
});