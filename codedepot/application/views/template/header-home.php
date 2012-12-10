<script>
$(document).ready(function() {
	
	$(".bottom-image a").click(function(e){
		e.preventDefault();
		$(".content-box-main").html("<div class='loading'></div>")
		$.ajax({
			url:$(this).attr("href"),
			success: function(data) {
				$(".content-box-main").html(data);
			}
		});
	});
	$(function() {
 	    $(".site_news")
 	        .click(function() { 
 	        	$(".image-back").css("background-image","url('<?php echo base_url()?>images/news_img.png')");
 	           
 	        });
 	});
	$(function() {
 	    $(".site_top")
 	        .click(function() { 
 	        	$(".image-back").css("background-image","url('<?php echo base_url()?>images/cpp.png')");
 	           
 	        });
 	});
	$(function() {
 	    $(".site_recent")
 	        .click(function() { 
 	        	$(".image-back").css("background-image","url('<?php echo base_url()?>images/news_img.png')");
 	           
 	        });
 	});
	
});

</script>

<div id="header_top">
	<div class="wrapper">
		<?php $this->load->view('template/nav_top.php'); ?>
	</div>
</div>
<div class="header_bottom_home">
	<div class="wrapper">
		<div class="site_logo">
			<a href="<?php echo base_url(); ?>"><img
				src="<?php echo base_url(); ?>/images/logo_white.png" width="429"
				height="71" alt="CodeDepot Logo" /> </a>
		</div>	
		<!-- Search bar -->
		<?php $this->load->view('template/search_bar.php');?>
		<div class="clear"></div>
	</div>
	<div class="image-back">
		<div class="top-image">
		</div>
		<div class="bottom-image">
			<div class="container">
				<a href="<?php echo base_url(); ?>home/get_news/" class="site_news">
				<div class="lang-box">
					<div class="logo-lang">
					Site News
					</div>
				</div>
				</a>
				<a href="<?php echo base_url(); ?>home/get_top/" class="site_top">
				<div class="lang-box">
					<div class="logo-lang">
					Top-Submissions
					</div>
				</div>
				</a>
				<a href="<?php echo base_url(); ?>home/get_recent/" class="site_recent">
				<div class="lang-box" style="margin-right:0px;">
					<div class="logo-lang">
					Recent Submissions
					</div>
				</div>
				</a>
			</div>
		</div>
	</div>
</div>

