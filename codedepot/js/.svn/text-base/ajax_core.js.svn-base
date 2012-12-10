$.fn.setupFormAjaxRequest = function() {
	$(this).submit(function() {
		var className = $(this).attr("class");
		$(this).replaceWith("<div class='"+ className +" loading'></div>");
		$.post(
			$(this).attr('action'),
			$(this).serialize(),
			function(data){
				$("." + className).replaceWith(data);
				$("." + className).setupFormAjaxRequest();
			}
		);
		return false;
	});
}