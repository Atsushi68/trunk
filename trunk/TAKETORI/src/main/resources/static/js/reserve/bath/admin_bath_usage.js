$(function() {

	$(document).on("click", ".cancel_room", function(){

		var reserveDate = $(this).parents('tr').data('reserve_date') + '',
			timeCd = $(this).parents('tr').data('time_cd'),
			timeNm = $(this).parents('tr').data('time_nm'),
			bathCd = $(this).parents('td').data('bath_cd'),
			roomNo = $(this).parents('td').data('room_no');

		var txt = '貸切風呂の予約を取り消します。よろしいですか？';
		txt += reserveDate.substring(0, 4) + '/' + reserveDate.substring(4, 6) + '/' + reserveDate.substring(6, 8) +  ' ' + timeNm + ' ' + roomNo + '号室';
		Swal.fire({
			  title: '予約取消',
			  text: txt,
			  type: 'warning',
			  showCancelButton: true,
			  confirmButtonText: '取消',
			  cancelButtonText: 'キャンセル'
			}).then((result) => {
			  if (result.value) {
					$(this).parents('form').children('#reserveDate').val(reserveDate);
					$(this).parents('form').children('#timeCd').val(timeCd);
					$(this).parents('form').children('#bathCd').val(bathCd);
					$(this).parents('form').children('#roomNo').val(roomNo);
					$(this).parents('form').submit();
			  }
			});

		return false;
	});
	var html = "";
    var date = new Date();
	var y = date.getFullYear();
	var m = ("00" + (date.getMonth()+1)).slice(-2);
	var d = ("00" + date.getDate()).slice(-2);
	var h = ("00" + date.getHours()).slice(-2);
	var mi = ("00" + date.getMinutes()).slice(-2);
	html += '<p class="update_date">' + y + "/" + m + "/" + d + " " + h + ':' +  mi + '  更新<p>';
	$('#update_date').html(html);

	$(window).on('load',function() {
		// 定期実行
	    setInterval(function(){ location.reload(); }, 300000);
	});
});