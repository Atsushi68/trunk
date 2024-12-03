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