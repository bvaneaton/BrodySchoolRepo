<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>CodeDepot</title>
<?php $this->load->view('template/service_head.php'); ?>
	<script>
	$(function() { 
		$('#navigation div.category_menu_dropdown .container_menu div').hover(

		function(){
	    	$('div .top_hits', this).fadeIn(); 

	  	}, function(){
	    	$('div .top_hits', this).fadeOut('fast'); 

	   	});
	}); 
	$(function() { 
		$('#navigation li.browse_menu').hover(

		function(){
		    $('div.category_menu_dropdown', this).fadeIn(250); 

		}, function(){
		    $('div.category_menu_dropdown', this).fadeOut(320); 

		});
	});
	 
    </script>
</head>

<body>
<div id="page_container">
	<div id="container">
		<!--header start -->
		<?php $this->load->view('template/header-home.php'); ?>
		<!-- header end -->


		<!-- content area start -->
		<div class="wrapper-content-home">
		<div class="wrapper-content">
			<!-- left col start -->
			<?php echo $content; ?>
			<!-- left col end -->

			<!-- right col start -->
			<?php if (isset($right_bar)) {?>
			<div class="float_right page_right">
				<?php echo $right_bar ?>
			</div>
			<?php } ?>
			<!-- right col end -->
			<div class="clear"></div>
		</div>
		</div>
		<!-- content area end -->

		<!-- Footer start -->
		<div id="footer">
			<?php $this->load->view('template/footer.php'); ?>
		</div>
		<!-- footer end -->
	</div>
</div>
</body>
</html>
