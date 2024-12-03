// spmenu
(function($) {
    $(function() {
        var $header = $('#top_head');
        // Nav Fixed
        $(window).scroll(function() {
            if ($(window).scrollTop() > 350) {
                $header.addClass('fixed');
            } else {
                $header.removeClass('fixed');
            }
            $header.removeClass('open');
        });
        // Nav Toggle Button
        $('#nav_toggle').click(function(){
            $header.toggleClass('open');
        });        // Toggle Button_2
        $('#close').click(function(){
		$('#nav_toggle').click();
        });
});
})(jQuery);

var mediaQuery = matchMedia('(max-width: 599px)');
handle(mediaQuery);
mediaQuery.addListener(handle);
function handle(mq) {
  if (mq.matches) {
$(function() {
    var showFlag = false;
    var topBtn = $('#scroll');    
    var showFlag = false;
    $(window).scroll(function () {
        if ($(this).scrollTop() > 100) {
            if (showFlag == false) {
                showFlag = true;
                topBtn.stop().animate({'bottom' : '0px'}, 900); 
            }
        } else {
            if (showFlag) {
                showFlag = false;
                topBtn.stop().animate({'bottom' : '-500px'}, 900); 
            }
        }
    });
});
  } else {
$(function() {
    var showFlag = false;
    var topBtn = $('#scroll');    
    var showFlag = false;
    $(window).scroll(function () {
        if ($(this).scrollTop() > 100) {
            if (showFlag == false) {
                showFlag = true;
                topBtn.stop().animate({'bottom' : '0px'}, 900); 
            }
        } else {
            if (showFlag) {
                showFlag = false;
                topBtn.stop().animate({'bottom' : '-500px'}, 900); 
            }
        }
    });
});
  }
}
// アンカー処理
$(function(){
   $('a[href^="#"]').click(function() {
      var speed = 700; // ミリ秒
      var href= $(this).attr("href");
      var target = $(href == "#" || href == "" ? 'html' : href);
      var position = target.offset().top;
      $('body,html').animate({scrollTop:position}, speed, 'swing');
      return false;
   });
});

// TOPで言語選択時にお風呂予約に遷移
$(function(){
   $('.bath-link').click(function() {
      var id = $(this).attr('id');
      var url = "";
      if (id == 1) {
        url = "/order/ja/bath/";
      }
      if (id == 2) {
        url = "/order/en/bath/";
      }
      if (id == 3) {
        url = "/order/zh-cn/bath/";
      }
      if (id == 4) {
        url = "/order/zh-tw/bath/";
      }
      if (id == 5) {
        url = "/order/ko/bath/";
      }
      window.location.href = url;
   });
});